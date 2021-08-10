package org.apache.cordova.engine;

import android.webkit.JavascriptInterface;
import org.apache.cordova.CordovaBridge;
import org.apache.cordova.ExposedJsApi;
import org.json.JSONException;

/* access modifiers changed from: package-private */
public class SystemExposedJsApi implements ExposedJsApi {
    private final CordovaBridge bridge;

    SystemExposedJsApi(CordovaBridge bridge2) {
        this.bridge = bridge2;
    }

    @Override // org.apache.cordova.ExposedJsApi
    @JavascriptInterface
    public String exec(int bridgeSecret, String service, String action, String callbackId, String arguments) throws JSONException, IllegalAccessException {
        return this.bridge.jsExec(bridgeSecret, service, action, callbackId, arguments);
    }

    @Override // org.apache.cordova.ExposedJsApi
    @JavascriptInterface
    public void setNativeToJsBridgeMode(int bridgeSecret, int value) throws IllegalAccessException {
        this.bridge.jsSetNativeToJsBridgeMode(bridgeSecret, value);
    }

    @Override // org.apache.cordova.ExposedJsApi
    @JavascriptInterface
    public String retrieveJsMessages(int bridgeSecret, boolean fromOnlineEvent) throws IllegalAccessException {
        return this.bridge.jsRetrieveJsMessages(bridgeSecret, fromOnlineEvent);
    }
}
