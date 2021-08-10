package org.apache.cordova.engine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;

public class SystemWebView extends WebView implements CordovaWebViewEngine.EngineView {
    SystemWebChromeClient chromeClient;
    private CordovaInterface cordova;
    private SystemWebViewEngine parentEngine;
    private SystemWebViewClient viewClient;

    public SystemWebView(Context context) {
        this(context, null);
    }

    public SystemWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* access modifiers changed from: package-private */
    public void init(SystemWebViewEngine parentEngine2, CordovaInterface cordova2) {
        this.cordova = cordova2;
        this.parentEngine = parentEngine2;
        if (this.viewClient == null) {
            setWebViewClient(new SystemWebViewClient(parentEngine2));
        }
        if (this.chromeClient == null) {
            setWebChromeClient(new SystemWebChromeClient(parentEngine2));
        }
    }

    @Override // org.apache.cordova.CordovaWebViewEngine.EngineView
    public CordovaWebView getCordovaWebView() {
        SystemWebViewEngine systemWebViewEngine = this.parentEngine;
        if (systemWebViewEngine != null) {
            return systemWebViewEngine.getCordovaWebView();
        }
        return null;
    }

    public void setWebViewClient(WebViewClient client) {
        this.viewClient = (SystemWebViewClient) client;
        super.setWebViewClient(client);
    }

    public void setWebChromeClient(WebChromeClient client) {
        this.chromeClient = (SystemWebChromeClient) client;
        super.setWebChromeClient(client);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        Boolean ret = this.parentEngine.client.onDispatchKeyEvent(event);
        if (ret != null) {
            return ret.booleanValue();
        }
        return super.dispatchKeyEvent(event);
    }
}
