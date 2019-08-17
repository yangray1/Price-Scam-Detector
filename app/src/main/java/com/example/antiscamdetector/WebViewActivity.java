package com.example.antiscamdetector;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewActivity extends AppCompatActivity {
    /* Help from https://www.youtube.com/watch?v=TUXui5ItBkM */

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Only works if declared inside here.
        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        webView = findViewById(R.id.browserView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
