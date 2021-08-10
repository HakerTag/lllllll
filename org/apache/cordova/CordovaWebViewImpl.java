package org.apache.cordova;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.NativeToJsMessageQueue;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.json.JSONException;
import org.json.JSONObject;

public class CordovaWebViewImpl implements CordovaWebView {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final String TAG = "CordovaWebViewImpl";
    private CoreAndroid appPlugin;
    private Set<Integer> boundKeyCodes = new HashSet();
    private CordovaInterface cordova;
    protected final CordovaWebViewEngine engine;
    private EngineClient engineClient = new EngineClient();
    private boolean hasPausedEver;
    private int loadUrlTimeout = 0;
    String loadedUrl;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private NativeToJsMessageQueue nativeToJsMessageQueue;
    private PluginManager pluginManager;
    private CordovaPreferences preferences;
    private CordovaResourceApi resourceApi;

    static /* synthetic */ int access$108(CordovaWebViewImpl x0) {
        int i = x0.loadUrlTimeout;
        x0.loadUrlTimeout = i + 1;
        return i;
    }

    public static CordovaWebViewEngine createEngine(Context context, CordovaPreferences preferences2) {
        try {
            return (CordovaWebViewEngine) Class.forName(preferences2.getString("webview", SystemWebViewEngine.class.getCanonicalName())).getConstructor(Context.class, CordovaPreferences.class).newInstance(context, preferences2);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create webview. ", e);
        }
    }

    public CordovaWebViewImpl(CordovaWebViewEngine cordovaWebViewEngine) {
        this.engine = cordovaWebViewEngine;
    }

    public void init(CordovaInterface cordova2) {
        init(cordova2, new ArrayList(), new CordovaPreferences());
    }

    @Override // org.apache.cordova.CordovaWebView
    @SuppressLint({"Assert"})
    public void init(CordovaInterface cordova2, List<PluginEntry> pluginEntries, CordovaPreferences preferences2) {
        if (this.cordova == null) {
            this.cordova = cordova2;
            this.preferences = preferences2;
            this.pluginManager = new PluginManager(this, this.cordova, pluginEntries);
            this.resourceApi = new CordovaResourceApi(this.engine.getView().getContext(), this.pluginManager);
            NativeToJsMessageQueue nativeToJsMessageQueue2 = new NativeToJsMessageQueue();
            this.nativeToJsMessageQueue = nativeToJsMessageQueue2;
            nativeToJsMessageQueue2.addBridgeMode(new NativeToJsMessageQueue.NoOpBridgeMode());
            this.nativeToJsMessageQueue.addBridgeMode(new NativeToJsMessageQueue.LoadUrlBridgeMode(this.engine, cordova2));
            if (preferences2.getBoolean("DisallowOverscroll", false)) {
                this.engine.getView().setOverScrollMode(2);
            }
            this.engine.init(this, cordova2, this.engineClient, this.resourceApi, this.pluginManager, this.nativeToJsMessageQueue);
            this.pluginManager.addService(CoreAndroid.PLUGIN_NAME, "org.apache.cordova.CoreAndroid");
            this.pluginManager.init();
            return;
        }
        throw new IllegalStateException();
    }

    @Override // org.apache.cordova.CordovaWebView
    public boolean isInitialized() {
        return this.cordova != null;
    }

    @Override // org.apache.cordova.CordovaWebView
    public void loadUrlIntoView(final String url, boolean recreatePlugins) {
        LOG.d(TAG, ">>> loadUrl(" + url + ")");
        final boolean recreatePlugins2 = false;
        if (url.equals("about:blank") || url.startsWith("javascript:")) {
            this.engine.loadUrl(url, false);
            return;
        }
        if (recreatePlugins || this.loadedUrl == null) {
            recreatePlugins2 = true;
        }
        if (recreatePlugins2) {
            if (this.loadedUrl != null) {
                this.appPlugin = null;
                this.pluginManager.init();
            }
            this.loadedUrl = url;
        }
        final int currentLoadUrlTimeout = this.loadUrlTimeout;
        final int loadUrlTimeoutValue = this.preferences.getInteger("LoadUrlTimeoutValue", 20000);
        final Runnable loadError = new Runnable() {
            /* class org.apache.cordova.CordovaWebViewImpl.AnonymousClass1 */

            public void run() {
                CordovaWebViewImpl.this.stopLoading();
                LOG.e(CordovaWebViewImpl.TAG, "CordovaWebView: TIMEOUT ERROR!");
                JSONObject data = new JSONObject();
                try {
                    data.put("errorCode", -6);
                    data.put("description", "The connection to the server was unsuccessful.");
                    data.put("url", url);
                } catch (JSONException e) {
                }
                CordovaWebViewImpl.this.pluginManager.postMessage("onReceivedError", data);
            }
        };
        final Runnable timeoutCheck = new Runnable() {
            /* class org.apache.cordova.CordovaWebViewImpl.AnonymousClass2 */

            public void run() {
                try {
                    synchronized (this) {
                        wait((long) loadUrlTimeoutValue);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (CordovaWebViewImpl.this.loadUrlTimeout == currentLoadUrlTimeout && CordovaWebViewImpl.this.cordova.getActivity() != null) {
                    CordovaWebViewImpl.this.cordova.getActivity().runOnUiThread(loadError);
                } else if (CordovaWebViewImpl.this.cordova.getActivity() == null) {
                    LOG.d(CordovaWebViewImpl.TAG, "Cordova activity does not exist.");
                }
            }
        };
        if (this.cordova.getActivity() != null) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class org.apache.cordova.CordovaWebViewImpl.AnonymousClass3 */

                public void run() {
                    if (loadUrlTimeoutValue > 0) {
                        CordovaWebViewImpl.this.cordova.getThreadPool().execute(timeoutCheck);
                    }
                    CordovaWebViewImpl.this.engine.loadUrl(url, recreatePlugins2);
                }
            });
        } else {
            LOG.d(TAG, "Cordova activity does not exist.");
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void loadUrl(String url) {
        loadUrlIntoView(url, true);
    }

    @Override // org.apache.cordova.CordovaWebView
    public void showWebPage(String url, boolean openExternal, boolean clearHistory, Map<String, Object> params) {
        Intent intent;
        LOG.d(TAG, "showWebPage(%s, %b, %b, HashMap)", url, Boolean.valueOf(openExternal), Boolean.valueOf(clearHistory));
        if (clearHistory) {
            this.engine.clearHistory();
        }
        if (!openExternal) {
            if (this.pluginManager.shouldAllowNavigation(url)) {
                loadUrlIntoView(url, true);
                return;
            }
            LOG.w(TAG, "showWebPage: Refusing to load URL into webview since it is not in the <allow-navigation> whitelist. URL=" + url);
        } else if (!this.pluginManager.shouldOpenExternalUrl(url).booleanValue()) {
            LOG.w(TAG, "showWebPage: Refusing to send intent for URL since it is not in the <allow-intent> whitelist. URL=" + url);
        } else {
            Intent intent2 = null;
            try {
                if (url.startsWith("intent://")) {
                    intent = Intent.parseUri(url, 1);
                } else {
                    intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.BROWSABLE");
                    Uri uri = Uri.parse(url);
                    if ("file".equals(uri.getScheme())) {
                        intent.setDataAndType(uri, this.resourceApi.getMimeType(uri));
                    } else {
                        intent.setData(uri);
                    }
                }
                if (this.cordova.getActivity() != null) {
                    this.cordova.getActivity().startActivity(intent);
                } else {
                    LOG.d(TAG, "Cordova activity does not exist.");
                }
            } catch (URISyntaxException e) {
                LOG.e(TAG, "Error parsing url " + url, e);
            } catch (ActivityNotFoundException e2) {
                if (!url.startsWith("intent://") || 0 == 0 || intent2.getStringExtra("browser_fallback_url") == null) {
                    LOG.e(TAG, "Error loading url " + url, e2);
                    return;
                }
                showWebPage(intent2.getStringExtra("browser_fallback_url"), openExternal, clearHistory, params);
            }
        }
    }

    private static class WrapperView extends FrameLayout {
        private final CordovaWebViewEngine engine;

        public WrapperView(Context context, CordovaWebViewEngine engine2) {
            super(context);
            this.engine = engine2;
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            boolean ret = this.engine.getView().dispatchKeyEvent(event);
            if (!ret) {
                return super.dispatchKeyEvent(event);
            }
            return ret;
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    @Deprecated
    public void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        LOG.d(TAG, "showing Custom View");
        if (this.mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }
        WrapperView wrapperView = new WrapperView(getContext(), this.engine);
        wrapperView.addView(view);
        this.mCustomView = wrapperView;
        this.mCustomViewCallback = callback;
        ViewGroup parent = (ViewGroup) this.engine.getView().getParent();
        parent.addView(wrapperView, new FrameLayout.LayoutParams(-1, -1, 17));
        this.engine.getView().setVisibility(8);
        parent.setVisibility(0);
        parent.bringToFront();
    }

    @Override // org.apache.cordova.CordovaWebView
    @Deprecated
    public void hideCustomView() {
        if (this.mCustomView != null) {
            LOG.d(TAG, "Hiding Custom View");
            this.mCustomView.setVisibility(8);
            ((ViewGroup) this.engine.getView().getParent()).removeView(this.mCustomView);
            this.mCustomView = null;
            this.mCustomViewCallback.onCustomViewHidden();
            this.engine.getView().setVisibility(0);
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    @Deprecated
    public boolean isCustomViewShowing() {
        return this.mCustomView != null;
    }

    @Override // org.apache.cordova.CordovaWebView
    @Deprecated
    public void sendJavascript(String statement) {
        this.nativeToJsMessageQueue.addJavaScript(statement);
    }

    @Override // org.apache.cordova.CordovaWebView
    public void sendPluginResult(PluginResult cr, String callbackId) {
        this.nativeToJsMessageQueue.addPluginResult(cr, callbackId);
    }

    @Override // org.apache.cordova.CordovaWebView
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    @Override // org.apache.cordova.CordovaWebView
    public CordovaPreferences getPreferences() {
        return this.preferences;
    }

    @Override // org.apache.cordova.CordovaWebView
    public ICordovaCookieManager getCookieManager() {
        return this.engine.getCookieManager();
    }

    @Override // org.apache.cordova.CordovaWebView
    public CordovaResourceApi getResourceApi() {
        return this.resourceApi;
    }

    @Override // org.apache.cordova.CordovaWebView
    public CordovaWebViewEngine getEngine() {
        return this.engine;
    }

    @Override // org.apache.cordova.CordovaWebView
    public View getView() {
        return this.engine.getView();
    }

    @Override // org.apache.cordova.CordovaWebView
    public Context getContext() {
        return this.engine.getView().getContext();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void sendJavascriptEvent(String event) {
        if (this.appPlugin == null) {
            this.appPlugin = (CoreAndroid) this.pluginManager.getPlugin(CoreAndroid.PLUGIN_NAME);
        }
        CoreAndroid coreAndroid = this.appPlugin;
        if (coreAndroid == null) {
            LOG.w(TAG, "Unable to fire event without existing plugin");
        } else {
            coreAndroid.fireJavascriptEvent(event);
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void setButtonPlumbedToJs(int keyCode, boolean override) {
        if (keyCode != 4 && keyCode != 82 && keyCode != 24 && keyCode != 25) {
            throw new IllegalArgumentException("Unsupported keycode: " + keyCode);
        } else if (override) {
            this.boundKeyCodes.add(Integer.valueOf(keyCode));
        } else {
            this.boundKeyCodes.remove(Integer.valueOf(keyCode));
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public boolean isButtonPlumbedToJs(int keyCode) {
        return this.boundKeyCodes.contains(Integer.valueOf(keyCode));
    }

    @Override // org.apache.cordova.CordovaWebView
    public Object postMessage(String id, Object data) {
        return this.pluginManager.postMessage(id, data);
    }

    @Override // org.apache.cordova.CordovaWebView
    public String getUrl() {
        return this.engine.getUrl();
    }

    @Override // org.apache.cordova.CordovaWebView
    public void stopLoading() {
        this.loadUrlTimeout++;
    }

    @Override // org.apache.cordova.CordovaWebView
    public boolean canGoBack() {
        return this.engine.canGoBack();
    }

    @Override // org.apache.cordova.CordovaWebView
    public void clearCache() {
        this.engine.clearCache();
    }

    @Override // org.apache.cordova.CordovaWebView
    @Deprecated
    public void clearCache(boolean b) {
        this.engine.clearCache();
    }

    @Override // org.apache.cordova.CordovaWebView
    public void clearHistory() {
        this.engine.clearHistory();
    }

    @Override // org.apache.cordova.CordovaWebView
    public boolean backHistory() {
        return this.engine.goBack();
    }

    @Override // org.apache.cordova.CordovaWebView
    public void onNewIntent(Intent intent) {
        PluginManager pluginManager2 = this.pluginManager;
        if (pluginManager2 != null) {
            pluginManager2.onNewIntent(intent);
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void handlePause(boolean keepRunning) {
        if (isInitialized()) {
            this.hasPausedEver = true;
            this.pluginManager.onPause(keepRunning);
            sendJavascriptEvent("pause");
            if (!keepRunning) {
                this.engine.setPaused(true);
            }
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void handleResume(boolean keepRunning) {
        if (isInitialized()) {
            this.engine.setPaused(false);
            this.pluginManager.onResume(keepRunning);
            if (this.hasPausedEver) {
                sendJavascriptEvent("resume");
            }
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void handleStart() {
        if (isInitialized()) {
            this.pluginManager.onStart();
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void handleStop() {
        if (isInitialized()) {
            this.pluginManager.onStop();
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void handleDestroy() {
        if (isInitialized()) {
            this.loadUrlTimeout++;
            this.pluginManager.onDestroy();
            loadUrl("about:blank");
            this.engine.destroy();
            hideCustomView();
        }
    }

    /* access modifiers changed from: protected */
    public class EngineClient implements CordovaWebViewEngine.Client {
        protected EngineClient() {
        }

        @Override // org.apache.cordova.CordovaWebViewEngine.Client
        public void clearLoadTimeoutTimer() {
            CordovaWebViewImpl.access$108(CordovaWebViewImpl.this);
        }

        @Override // org.apache.cordova.CordovaWebViewEngine.Client
        public void onPageStarted(String newUrl) {
            LOG.d(CordovaWebViewImpl.TAG, "onPageDidNavigate(" + newUrl + ")");
            CordovaWebViewImpl.this.boundKeyCodes.clear();
            CordovaWebViewImpl.this.pluginManager.onReset();
            CordovaWebViewImpl.this.pluginManager.postMessage("onPageStarted", newUrl);
        }

        @Override // org.apache.cordova.CordovaWebViewEngine.Client
        public void onReceivedError(int errorCode, String description, String failingUrl) {
            clearLoadTimeoutTimer();
            JSONObject data = new JSONObject();
            try {
                data.put("errorCode", errorCode);
                data.put("description", description);
                data.put("url", failingUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CordovaWebViewImpl.this.pluginManager.postMessage("onReceivedError", data);
        }

        @Override // org.apache.cordova.CordovaWebViewEngine.Client
        public void onPageFinishedLoading(String url) {
            LOG.d(CordovaWebViewImpl.TAG, "onPageFinished(" + url + ")");
            clearLoadTimeoutTimer();
            CordovaWebViewImpl.this.pluginManager.postMessage("onPageFinished", url);
            if (CordovaWebViewImpl.this.engine.getView().getVisibility() != 0) {
                new Thread(new Runnable() {
                    /* class org.apache.cordova.CordovaWebViewImpl.EngineClient.AnonymousClass1 */

                    public void run() {
                        try {
                            Thread.sleep(2000);
                            if (CordovaWebViewImpl.this.cordova.getActivity() != null) {
                                CordovaWebViewImpl.this.cordova.getActivity().runOnUiThread(new Runnable() {
                                    /* class org.apache.cordova.CordovaWebViewImpl.EngineClient.AnonymousClass1.AnonymousClass1 */

                                    public void run() {
                                        CordovaWebViewImpl.this.pluginManager.postMessage("spinner", "stop");
                                    }
                                });
                            } else {
                                LOG.d(CordovaWebViewImpl.TAG, "Cordova activity does not exist.");
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }
            if (url.equals("about:blank")) {
                CordovaWebViewImpl.this.pluginManager.postMessage("exit", null);
            }
        }

        @Override // org.apache.cordova.CordovaWebViewEngine.Client
        public Boolean onDispatchKeyEvent(KeyEvent event) {
            int keyCode = event.getKeyCode();
            boolean isBackButton = keyCode == 4;
            if (event.getAction() == 0) {
                if ((isBackButton && CordovaWebViewImpl.this.mCustomView != null) || CordovaWebViewImpl.this.boundKeyCodes.contains(Integer.valueOf(keyCode))) {
                    return true;
                }
                if (isBackButton) {
                    return Boolean.valueOf(CordovaWebViewImpl.this.engine.canGoBack());
                }
                return null;
            } else if (event.getAction() != 1) {
                return null;
            } else {
                if (isBackButton && CordovaWebViewImpl.this.mCustomView != null) {
                    CordovaWebViewImpl.this.hideCustomView();
                    return true;
                } else if (CordovaWebViewImpl.this.boundKeyCodes.contains(Integer.valueOf(keyCode))) {
                    String eventName = null;
                    if (keyCode == 4) {
                        eventName = "backbutton";
                    } else if (keyCode == 82) {
                        eventName = "menubutton";
                    } else if (keyCode == 84) {
                        eventName = "searchbutton";
                    } else if (keyCode == 24) {
                        eventName = "volumeupbutton";
                    } else if (keyCode == 25) {
                        eventName = "volumedownbutton";
                    }
                    if (eventName == null) {
                        return null;
                    }
                    CordovaWebViewImpl.this.sendJavascriptEvent(eventName);
                    return true;
                } else if (isBackButton) {
                    return Boolean.valueOf(CordovaWebViewImpl.this.engine.goBack());
                } else {
                    return null;
                }
            }
        }

        @Override // org.apache.cordova.CordovaWebViewEngine.Client
        public boolean onNavigationAttempt(String url) {
            if (CordovaWebViewImpl.this.pluginManager.onOverrideUrlLoading(url)) {
                return true;
            }
            if (CordovaWebViewImpl.this.pluginManager.shouldAllowNavigation(url)) {
                return false;
            }
            if (CordovaWebViewImpl.this.pluginManager.shouldOpenExternalUrl(url).booleanValue()) {
                CordovaWebViewImpl.this.showWebPage(url, true, false, null);
                return true;
            }
            LOG.w(CordovaWebViewImpl.TAG, "Blocked (possibly sub-frame) navigation to non-allowed URL: " + url);
            return true;
        }
    }
}
