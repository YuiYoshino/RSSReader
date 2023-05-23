package jp.ac.cm0107.rssreader;

import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


import javax.net.ssl.HttpsURLConnection;

public class AsyncHttpRequest implements Runnable {
    private Handler handler;
    private MainActivity mainActivity;
    private String urlStr;
    private String resStr;

    public AsyncHttpRequest(Handler handler, MainActivity mainActivity, String urlStr) {
        this.handler = handler;
        this.mainActivity = mainActivity;
        this.urlStr = urlStr;
    }

    @Override
    public void run() {
        Log.i("RssReader", "BackgroundTask start...");
        // バックグラウンド(非同期)で実行する処理を記述する
        // HTTP 通信の処理
        resStr = "取得に失敗しました。";
        HttpsURLConnection connection = null;

        try {
            //接続先 Web サイトの URL の文字列を URL クラスのオブジェクトにする
            URL url = new URL(urlStr);
            //接続先 Web サイトへの接続を開始
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            //接続先から取得した InputStream を文字列データにする
            resStr = inputStreamToString(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AsyncHttpRequest", e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        // 非同期処理後の処理
        handler.post(new Runnable() {
            @Override
            public void run() {
                onPostExecute();
            }
        });
    }

    private String inputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine())!= null){
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }


    private void onPostExecute() {
        Log.i("RssReader", "onPostExecute start...");

        // 非同期処理後に実行する処理を記述する
        ArrayList<RssItem> ary = JsonHelper.parseJson(resStr);
        for (RssItem tmp : ary) {
            mainActivity.adapter.add(tmp);
        }
        ListView list = mainActivity.findViewById(R.id.resultList);
        list.setAdapter(mainActivity.adapter);
    }
}