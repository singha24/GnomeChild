package com.example.assa.gnomechild;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by Assa on 08/11/2015.
 */
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebActivity extends Activity {

    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://twitter.com/hashtag/heatmap");

    }

}