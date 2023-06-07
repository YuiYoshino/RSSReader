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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    protected RowModelAdapter adapter;
    private ExecutorService executorService;
    private Handler handler;
    String topics;

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd(EE) HH:mm:ss");
    String [] spnItemStr = {
            "Sport","IT","Main"
    };
    String item  = spnItemStr[0] ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ステータスバー非表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // タイトルバー非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        Spinner spn = findViewById(R.id.spnCategory);

        ArrayAdapter<String> adapterSpn = new ArrayAdapter<>(
                this, androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,spnItemStr
        );
        adapterSpn.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spn.setAdapter(adapterSpn);
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Spinner spinner = (Spinner) adapterView;
                item = (String) spinner.getSelectedItem();
                new selectEvent(item);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



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
        /*
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
        });*/
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
        Button reload = findViewById(R.id.reloadBtn);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new selectEvent(item);
            }
        });
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
                    SimpleDateFormat sdf =new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
                    String str = item.getPubDate();
                    try {
                        Date date = sdf.parse(str);
                        String dateToString = simpleDateFormat.format(date);
                        txtId.setText(dateToString);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                Button btn = convertView.findViewById(R.id.btnLink);
                if (btn != null){
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri uri = Uri.parse((item.getLink()));
                            Intent intent = new Intent(MainActivity.this,WebActivity.class);
                            intent.putExtra("web",uri);
                            startActivity(intent);
                        }
                    });
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

    private class selectEvent  {
        public selectEvent(String item) {
            if (item.equals(spnItemStr[0])){
                topics = "categories/sports";
                new UriBuild();
            } else if (item.equals(spnItemStr[1])) {
                topics = "categories/it";
                new UriBuild();
            } else if (item.equals(spnItemStr[2])){
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