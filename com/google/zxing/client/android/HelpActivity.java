package com.google.zxing.client.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import barcodescanner.xservices.nl.barcodescanner.R;

public final class HelpActivity extends Activity {
    private static final String BASE_URL = ("file:///android_asset/html-" + LocaleManager.getTranslatedAssetLanguage() + '/');
    private WebView webView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.help);
        WebView webView2 = (WebView) findViewById(R.id.help_contents);
        this.webView = webView2;
        if (icicle == null) {
            webView2.loadUrl(BASE_URL + "index.html");
            return;
        }
        webView2.restoreState(icicle);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || !this.webView.canGoBack()) {
            return super.onKeyDown(keyCode, event);
        }
        this.webView.goBack();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        this.webView.saveState(icicle);
    }
}
