package jp.ac.cm0107.rssreader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra("web");
        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient()); // WebViewを設定する
        webView.getSettings().setJavaScriptEnabled(true); // JavaScriptを有効にする
        webView.loadUrl(uri.toString());

    }
}