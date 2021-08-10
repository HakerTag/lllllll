package org.apache.cordova;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    static /* synthetic */ int access$108(CordovaWebViewImpl cordovaWebViewImpl) {
        int i = cordovaWebViewImpl.loadUrlTimeout;
        cordovaWebViewImpl.loadUrlTimeout = i + 1;
        return i;
    }

    public static CordovaWebViewEngine createEngine(Context context, CordovaPreferences cordovaPreferences) {
        try {
            return (CordovaWebViewEngine) Class.forName(cordovaPreferences.getString("webview", SystemWebViewEngine.class.getCanonicalName())).getConstructor(Context.class, CordovaPreferences.class).newInstance(context, cordovaPreferences);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create webview. ", e);
        }
    }

    public CordovaWebViewImpl(CordovaWebViewEngine cordovaWebViewEngine) {
        this.engine = cordovaWebViewEngine;
    }

    public void init(CordovaInterface cordovaInterface) {
        init(cordovaInterface, new ArrayList(), new CordovaPreferences());
    }

    @Override // org.apache.cordova.CordovaWebView
    public void init(CordovaInterface cordovaInterface, List<PluginEntry> list, CordovaPreferences cordovaPreferences) {
        if (this.cordova == null) {
            this.cordova = cordovaInterface;
            this.preferences = cordovaPreferences;
            this.pluginManager = new PluginManager(this, this.cordova, list);
            this.resourceApi = new CordovaResourceApi(this.engine.getView().getContext(), this.pluginManager);
            NativeToJsMessageQueue nativeToJsMessageQueue2 = new NativeToJsMessageQueue();
            this.nativeToJsMessageQueue = nativeToJsMessageQueue2;
            nativeToJsMessageQueue2.addBridgeMode(new NativeToJsMessageQueue.NoOpBridgeMode());
            this.nativeToJsMessageQueue.addBridgeMode(new NativeToJsMessageQueue.LoadUrlBridgeMode(this.engine, cordovaInterface));
            if (cordovaPreferences.getBoolean("DisallowOverscroll", false)) {
                this.engine.getView().setOverScrollMode(2);
            }
            this.engine.init(this, cordovaInterface, this.engineClient, this.resourceApi, this.pluginManager, this.nativeToJsMessageQueue);
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
    public void loadUrlIntoView(final String str, boolean z) {
        LOG.d(TAG, ">>> loadUrl(" + str + ")");
        if (str.equals("about:blank") || str.startsWith("javascript:")) {
            this.engine.loadUrl(str, false);
            return;
        }
        final boolean z2 = z || this.loadedUrl == null;
        if (z2) {
            if (this.loadedUrl != null) {
                this.appPlugin = null;
                this.pluginManager.init();
            }
            this.loadedUrl = str;
        }
        final int i = this.loadUrlTimeout;
        final int integer = this.preferences.getInteger("LoadUrlTimeoutValue", 20000);
        final AnonymousClass1 r0 = new Runnable() {
            /* class org.apache.cordova.CordovaWebViewImpl.AnonymousClass1 */

            public void run() {
                CordovaWebViewImpl.this.stopLoading();
                LOG.e(CordovaWebViewImpl.TAG, "CordovaWebView: TIMEOUT ERROR!");
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("errorCode", -6);
                    jSONObject.put("description", "The connection to the server was unsuccessful.");
                    jSONObject.put("url", str);
                } catch (JSONException unused) {
                }
                CordovaWebViewImpl.this.pluginManager.postMessage("onReceivedError", jSONObject);
            }
        };
        final AnonymousClass2 r6 = new Runnable() {
            /* class org.apache.cordova.CordovaWebViewImpl.AnonymousClass2 */

            public void run() {
                try {
                    synchronized (this) {
                        wait((long) integer);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (CordovaWebViewImpl.this.loadUrlTimeout == i && CordovaWebViewImpl.this.cordova.getActivity() != null) {
                    CordovaWebViewImpl.this.cordova.getActivity().runOnUiThread(r0);
                } else if (CordovaWebViewImpl.this.cordova.getActivity() == null) {
                    LOG.d(CordovaWebViewImpl.TAG, "Cordova activity does not exist.");
                }
            }
        };
        if (this.cordova.getActivity() != null) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class org.apache.cordova.CordovaWebViewImpl.AnonymousClass3 */

                public void run() {
                    if (integer > 0) {
                        CordovaWebViewImpl.this.cordova.getThreadPool().execute(r6);
                    }
                    CordovaWebViewImpl.this.engine.loadUrl(str, z2);
                }
            });
        } else {
            LOG.d(TAG, "Cordova activity does not exist.");
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void loadUrl(String str) {
        loadUrlIntoView(str, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00bb, code lost:
        r3 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00bd, code lost:
        r9 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00cf, code lost:
        showWebPage(r1.getStringExtra("browser_fallback_url"), r9, r10, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00ec, code lost:
        org.apache.cordova.LOG.e(org.apache.cordova.CordovaWebViewImpl.TAG, "Error parsing url " + r8, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00bd A[ExcHandler: URISyntaxException (r9v2 'e' java.net.URISyntaxException A[CUSTOM_DECLARE]), Splitter:B:15:0x0069] */
    @Override // org.apache.cordova.CordovaWebView
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showWebPage(java.lang.String r8, boolean r9, boolean r10, java.util.Map<java.lang.String, java.lang.Object> r11) {
        /*
            r7 = this;
            java.lang.String r0 = "intent://"
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r8
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r9)
            r3 = 1
            r1[r3] = r2
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r10)
            r4 = 2
            r1[r4] = r2
            java.lang.String r2 = "CordovaWebViewImpl"
            java.lang.String r4 = "showWebPage(%s, %b, %b, HashMap)"
            org.apache.cordova.LOG.d(r2, r4, r1)
            if (r10 == 0) goto L_0x0024
            org.apache.cordova.CordovaWebViewEngine r1 = r7.engine
            r1.clearHistory()
        L_0x0024:
            if (r9 != 0) goto L_0x0047
            org.apache.cordova.PluginManager r9 = r7.pluginManager
            boolean r9 = r9.shouldAllowNavigation(r8)
            if (r9 == 0) goto L_0x0032
            r7.loadUrlIntoView(r8, r3)
            return
        L_0x0032:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "showWebPage: Refusing to load URL into webview since it is not in the <allow-navigation> whitelist. URL="
            r9.append(r10)
            r9.append(r8)
            java.lang.String r8 = r9.toString()
            org.apache.cordova.LOG.w(r2, r8)
            return
        L_0x0047:
            org.apache.cordova.PluginManager r1 = r7.pluginManager
            java.lang.Boolean r1 = r1.shouldOpenExternalUrl(r8)
            boolean r1 = r1.booleanValue()
            if (r1 != 0) goto L_0x0068
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "showWebPage: Refusing to send intent for URL since it is not in the <allow-intent> whitelist. URL="
            r9.append(r10)
            r9.append(r8)
            java.lang.String r8 = r9.toString()
            org.apache.cordova.LOG.w(r2, r8)
            return
        L_0x0068:
            r1 = 0
            boolean r4 = r8.startsWith(r0)     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00bb }
            if (r4 == 0) goto L_0x0074
            android.content.Intent r1 = android.content.Intent.parseUri(r8, r3)     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00bb }
            goto L_0x009e
        L_0x0074:
            android.content.Intent r3 = new android.content.Intent     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00bb }
            java.lang.String r4 = "android.intent.action.VIEW"
            r3.<init>(r4)     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00bb }
            java.lang.String r1 = "android.intent.category.BROWSABLE"
            r3.addCategory(r1)     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00b6 }
            android.net.Uri r1 = android.net.Uri.parse(r8)     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00b6 }
            java.lang.String r4 = "file"
            java.lang.String r5 = r1.getScheme()     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00b6 }
            boolean r4 = r4.equals(r5)     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00b6 }
            if (r4 == 0) goto L_0x009a
            org.apache.cordova.CordovaResourceApi r4 = r7.resourceApi     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00b6 }
            java.lang.String r4 = r4.getMimeType(r1)     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00b6 }
            r3.setDataAndType(r1, r4)     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00b6 }
            goto L_0x009d
        L_0x009a:
            r3.setData(r1)     // Catch:{ URISyntaxException -> 0x00bd, ActivityNotFoundException -> 0x00b6 }
        L_0x009d:
            r1 = r3
        L_0x009e:
            org.apache.cordova.CordovaInterface r3 = r7.cordova
            android.app.Activity r3 = r3.getActivity()
            if (r3 == 0) goto L_0x00b0
            org.apache.cordova.CordovaInterface r3 = r7.cordova
            android.app.Activity r3 = r3.getActivity()
            r3.startActivity(r1)
            goto L_0x0100
        L_0x00b0:
            java.lang.String r3 = "Cordova activity does not exist."
            org.apache.cordova.LOG.d(r2, r3)
            goto L_0x0100
        L_0x00b6:
            r1 = move-exception
            r6 = r3
            r3 = r1
            r1 = r6
            goto L_0x00bf
        L_0x00bb:
            r3 = move-exception
            goto L_0x00bf
        L_0x00bd:
            r9 = move-exception
            goto L_0x00ec
        L_0x00bf:
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x00d7
            if (r1 == 0) goto L_0x00d7
            java.lang.String r0 = "browser_fallback_url"
            java.lang.String r4 = r1.getStringExtra(r0)
            if (r4 == 0) goto L_0x00d7
            java.lang.String r8 = r1.getStringExtra(r0)
            r7.showWebPage(r8, r9, r10, r11)
            goto L_0x0100
        L_0x00d7:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Error loading url "
            r9.append(r10)
            r9.append(r8)
            java.lang.String r8 = r9.toString()
            org.apache.cordova.LOG.e(r2, r8, r3)
            goto L_0x0100
        L_0x00ec:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "Error parsing url "
            r10.append(r11)
            r10.append(r8)
            java.lang.String r8 = r10.toString()
            org.apache.cordova.LOG.e(r2, r8, r9)
        L_0x0100:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.CordovaWebViewImpl.showWebPage(java.lang.String, boolean, boolean, java.util.Map):void");
    }

    private static class WrapperView extends FrameLayout {
        private final CordovaWebViewEngine engine;

        public WrapperView(Context context, CordovaWebViewEngine cordovaWebViewEngine) {
            super(context);
            this.engine = cordovaWebViewEngine;
        }

        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            boolean dispatchKeyEvent = this.engine.getView().dispatchKeyEvent(keyEvent);
            return !dispatchKeyEvent ? super.dispatchKeyEvent(keyEvent) : dispatchKeyEvent;
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    @Deprecated
    public void showCustomView(View view, WebChromeClient.CustomViewCallback customViewCallback) {
        LOG.d(TAG, "showing Custom View");
        if (this.mCustomView != null) {
            customViewCallback.onCustomViewHidden();
            return;
        }
        WrapperView wrapperView = new WrapperView(getContext(), this.engine);
        wrapperView.addView(view);
        this.mCustomView = wrapperView;
        this.mCustomViewCallback = customViewCallback;
        ViewGroup viewGroup = (ViewGroup) this.engine.getView().getParent();
        viewGroup.addView(wrapperView, new FrameLayout.LayoutParams(-1, -1, 17));
        this.engine.getView().setVisibility(8);
        viewGroup.setVisibility(0);
        viewGroup.bringToFront();
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
    public void sendJavascript(String str) {
        this.nativeToJsMessageQueue.addJavaScript(str);
    }

    @Override // org.apache.cordova.CordovaWebView
    public void sendPluginResult(PluginResult pluginResult, String str) {
        this.nativeToJsMessageQueue.addPluginResult(pluginResult, str);
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
    private void sendJavascriptEvent(String str) {
        if (this.appPlugin == null) {
            this.appPlugin = (CoreAndroid) this.pluginManager.getPlugin(CoreAndroid.PLUGIN_NAME);
        }
        CoreAndroid coreAndroid = this.appPlugin;
        if (coreAndroid == null) {
            LOG.w(TAG, "Unable to fire event without existing plugin");
        } else {
            coreAndroid.fireJavascriptEvent(str);
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void setButtonPlumbedToJs(int i, boolean z) {
        if (i != 4 && i != 82 && i != 24 && i != 25) {
            throw new IllegalArgumentException("Unsupported keycode: " + i);
        } else if (z) {
            this.boundKeyCodes.add(Integer.valueOf(i));
        } else {
            this.boundKeyCodes.remove(Integer.valueOf(i));
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public boolean isButtonPlumbedToJs(int i) {
        return this.boundKeyCodes.contains(Integer.valueOf(i));
    }

    @Override // org.apache.cordova.CordovaWebView
    public Object postMessage(String str, Object obj) {
        return this.pluginManager.postMessage(str, obj);
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
    public void clearCache(boolean z) {
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
    public void handlePause(boolean z) {
        if (isInitialized()) {
            this.hasPausedEver = true;
            this.pluginManager.onPause(z);
            sendJavascriptEvent("pause");
            if (!z) {
                this.engine.setPaused(true);
            }
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void handleResume(boolean z) {
        if (isInitialized()) {
            this.engine.setPaused(false);
            this.pluginManager.onResume(z);
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
        public void onPageStarted(String str) {
            LOG.d(CordovaWebViewImpl.TAG, "onPageDidNavigate(" + str + ")");
            CordovaWebViewImpl.this.boundKeyCodes.clear();
            CordovaWebViewImpl.this.pluginManager.onReset();
            CordovaWebViewImpl.this.pluginManager.postMessage("onPageStarted", str);
        }

        @Override // org.apache.cordova.CordovaWebViewEngine.Client
        public void onReceivedError(int i, String str, String str2) {
            clearLoadTimeoutTimer();
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("errorCode", i);
                jSONObject.put("description", str);
                jSONObject.put("url", str2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CordovaWebViewImpl.this.pluginManager.postMessage("onReceivedError", jSONObject);
        }

        @Override // org.apache.cordova.CordovaWebViewEngine.Client
        public void onPageFinishedLoading(String str) {
            LOG.d(CordovaWebViewImpl.TAG, "onPageFinished(" + str + ")");
            clearLoadTimeoutTimer();
            CordovaWebViewImpl.this.pluginManager.postMessage("onPageFinished", str);
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
                        } catch (InterruptedException unused) {
                        }
                    }
                }).start();
            }
            if (str.equals("about:blank")) {
                CordovaWebViewImpl.this.pluginManager.postMessage("exit", null);
            }
        }

        @Override // org.apache.cordova.CordovaWebViewEngine.Client
        public Boolean onDispatchKeyEvent(KeyEvent keyEvent) {
            int keyCode = keyEvent.getKeyCode();
            boolean z = keyCode == 4;
            if (keyEvent.getAction() == 0) {
                if ((z && CordovaWebViewImpl.this.mCustomView != null) || CordovaWebViewImpl.this.boundKeyCodes.contains(Integer.valueOf(keyCode))) {
                    return true;
                }
                if (z) {
                    return Boolean.valueOf(CordovaWebViewImpl.this.engine.canGoBack());
                }
            } else if (keyEvent.getAction() == 1) {
                if (z && CordovaWebViewImpl.this.mCustomView != null) {
                    CordovaWebViewImpl.this.hideCustomView();
                    return true;
                } else if (CordovaWebViewImpl.this.boundKeyCodes.contains(Integer.valueOf(keyCode))) {
                    String str = keyCode != 4 ? keyCode != 82 ? keyCode != 84 ? keyCode != 24 ? keyCode != 25 ? null : "volumedownbutton" : "volumeupbutton" : "searchbutton" : "menubutton" : "backbutton";
                    if (str != null) {
                        CordovaWebViewImpl.this.sendJavascriptEvent(str);
                        return true;
                    }
                } else if (z) {
                    return Boolean.valueOf(CordovaWebViewImpl.this.engine.goBack());
                }
            }
            return null;
        }

        @Override // org.apache.cordova.CordovaWebViewEngine.Client
        public boolean onNavigationAttempt(String str) {
            if (CordovaWebViewImpl.this.pluginManager.onOverrideUrlLoading(str)) {
                return true;
            }
            if (CordovaWebViewImpl.this.pluginManager.shouldAllowNavigation(str)) {
                return false;
            }
            if (CordovaWebViewImpl.this.pluginManager.shouldOpenExternalUrl(str).booleanValue()) {
                CordovaWebViewImpl.this.showWebPage(str, true, false, null);
                return true;
            }
            LOG.w(CordovaWebViewImpl.TAG, "Blocked (possibly sub-frame) navigation to non-allowed URL: " + str);
            return true;
        }
    }
}
