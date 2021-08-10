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
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.help);
        WebView webView2 = (WebView) findViewById(R.id.help_contents);
        this.webView = webView2;
        if (bundle == null) {
            webView2.loadUrl(BASE_URL + "index.html");
            return;
        }
        webView2.restoreState(bundle);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4 || !this.webView.canGoBack()) {
            return super.onKeyDown(i, keyEvent);
        }
        this.webView.goBack();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.webView.saveState(bundle);
    }
}
