package com.example.pricescamdetector;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {
    /* Help from https://www.youtube.com/watch?v=TUXui5ItBkM */

    private WebView webView;
    private boolean goBackToList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Only works if declared inside here.
        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");
        goBackToList = false;

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
        if (webView.canGoBack() && !goBackToList){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    // Override action bar back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goBackToList = true;
            onBackPressed();
            return true;
        }
        return false;
    }
}
