package jp.ac.cm0107.rssreader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    protected RowModelAdapter adapter;
    private ExecutorService executorService;
    private Handler handler;
    String topics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnTOP = findViewById(R.id.TopPicksbtn);
        Button bntIT = findViewById(R.id.ITbtn);
        Button btnSPORT = findViewById(R.id.SPORTSbtn);

        btnSPORT.setOnClickListener(new BtnEvent());
        bntIT.setOnClickListener(new BtnEvent());
        btnTOP.setOnClickListener(new BtnEvent());
//        TextView txtTime = findViewById(R.id.textTime);
//        Calendar cl = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd(EEEE)HH:mm:ss");
//        String fd = sdf.format(cl.getTime());
//        txtTime.setText(fd + "更新");

        Looper mainLooper = getMainLooper();
        handler = HandlerCompat.createAsync(mainLooper);

        executorService = Executors.newSingleThreadExecutor();

        adapter = new RowModelAdapter(this);
//        ArrayList<RssItem>ary=JsonHelper.parseJson(getData());
//        for(RssItem item:ary){
//            adapter.add(item);
//        }

        ListView list = findViewById(R.id.resultList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RssItem item = (RssItem) adapterView.getAdapter().getItem(i);
                Toast.makeText(MainActivity.this,item.getLink(),Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse((item.getLink()));
                Intent intent = new Intent(MainActivity.this,WebActivity.class);
                intent.putExtra("web",uri);
                startActivity(intent);
            }
        });
/*
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority("jec-cm-linux2020.lolipop.io");
        uriBuilder.path("test.php");
        uriBuilder.appendQueryParameter(
                "url", "https://news.yahoo.co.jp/rss/categories/sports.xml");

        Log.i("MainActivity", uriBuilder.build().toString());

        AsyncHttpRequest asyncHttpRequest =
                new AsyncHttpRequest(handler, MainActivity.this, uriBuilder.toString() );
        executorService.submit(asyncHttpRequest);*/
    }
    class RowModelAdapter extends ArrayAdapter {

        public RowModelAdapter(@NonNull Context context) {
            super(context, R.layout.row_item);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            RssItem item = (RssItem) getItem(position);
            if(convertView == null){
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.row_item,null);
            }
            if (item != null){
                TextView txtName = convertView.findViewById(R.id.txtRowTtle);

                if (txtName != null){
                    txtName.setText(item.getTitle());
                }
                TextView txtId = convertView.findViewById(R.id.txtRowLink);
                if(txtId != null){
                    txtId.setText(String.valueOf(item.getPubDate()));
                }
            }
            return convertView;
        }
    }
    private String getData() {
        String json = "";
        BufferedReader br = null;
        try{
            InputStream in = getAssets().open("rss.json");
            br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine())!= null){
                sb.append(line);
            }
            json = sb.toString();
        }catch (Exception e){
            Log.e("MainActivity", Log.getStackTraceString(e));
        }finally {
            try {
                if(br != null)br.close();
            }catch (IOException e){
                Log.e("MainActivity", Log.getStackTraceString(e));
            }
        }
        return json;
    }

    private class BtnEvent implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId()==R.id.SPORTSbtn){
                topics = "categories/sports";
                new UriBuild();
            } else if (view.getId()==R.id.ITbtn) {
                topics = "categories/it";
                new UriBuild();
            } else if (view.getId()==R.id.TopPicksbtn){
                topics = "topics/top-picks";
                new UriBuild();
            }
        }
    }




    private class UriBuild {
        public  UriBuild() {
            adapter.clear();

            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("https");
            uriBuilder.authority("jec-cm-linux2020.lolipop.io");
            uriBuilder.path("test.php");
            uriBuilder.appendQueryParameter(
                    "url", "https://news.yahoo.co.jp/rss/"+ topics +".xml");

            Log.i("MainActivity", uriBuilder.build().toString());

            AsyncHttpRequest asyncHttpRequest =
                    new AsyncHttpRequest(handler, MainActivity.this, uriBuilder.toString());
            executorService.submit(asyncHttpRequest);
            adapter.notifyDataSetChanged();
        }
    }
}