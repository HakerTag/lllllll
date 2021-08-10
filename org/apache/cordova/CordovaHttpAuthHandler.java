package org.apache.cordova;

import android.webkit.HttpAuthHandler;

public class CordovaHttpAuthHandler implements ICordovaHttpAuthHandler {
    private final HttpAuthHandler handler;

    public CordovaHttpAuthHandler(HttpAuthHandler handler2) {
        this.handler = handler2;
    }

    @Override // org.apache.cordova.ICordovaHttpAuthHandler
    public void cancel() {
        this.handler.cancel();
    }

    @Override // org.apache.cordova.ICordovaHttpAuthHandler
    public void proceed(String username, String password) {
        this.handler.proceed(username, password);
    }
}
