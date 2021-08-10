package org.apache.cordova;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

public class PluginManager {
    private static final int SLOW_EXEC_WARNING_THRESHOLD = (Debug.isDebuggerConnected() ? 60 : 16);
    private static String TAG = "PluginManager";
    private final CordovaWebView app;
    private final CordovaInterface ctx;
    private final Map<String, PluginEntry> entryMap = Collections.synchronizedMap(new LinkedHashMap());
    private boolean isInitialized;
    private CordovaPlugin permissionRequester;
    private final Map<String, CordovaPlugin> pluginMap = Collections.synchronizedMap(new LinkedHashMap());

    public PluginManager(CordovaWebView cordovaWebView, CordovaInterface cordovaInterface, Collection<PluginEntry> collection) {
        this.ctx = cordovaInterface;
        this.app = cordovaWebView;
        setPluginEntries(collection);
    }

    public Collection<PluginEntry> getPluginEntries() {
        return this.entryMap.values();
    }

    public void setPluginEntries(Collection<PluginEntry> collection) {
        if (this.isInitialized) {
            onPause(false);
            onDestroy();
            this.pluginMap.clear();
            this.entryMap.clear();
        }
        for (PluginEntry pluginEntry : collection) {
            addService(pluginEntry);
        }
        if (this.isInitialized) {
            startupPlugins();
        }
    }

    public void init() {
        LOG.d(TAG, "init()");
        this.isInitialized = true;
        onPause(false);
        onDestroy();
        this.pluginMap.clear();
        startupPlugins();
    }

    private void startupPlugins() {
        synchronized (this.entryMap) {
            for (PluginEntry pluginEntry : this.entryMap.values()) {
                if (pluginEntry.onload) {
                    getPlugin(pluginEntry.service);
                } else {
                    String str = TAG;
                    LOG.d(str, "startupPlugins: put - " + pluginEntry.service);
                    this.pluginMap.put(pluginEntry.service, null);
                }
            }
        }
    }

    public void exec(String str, String str2, String str3, String str4) {
        CordovaPlugin plugin = getPlugin(str);
        if (plugin == null) {
            String str5 = TAG;
            LOG.d(str5, "exec() call to unknown plugin: " + str);
            this.app.sendPluginResult(new PluginResult(PluginResult.Status.CLASS_NOT_FOUND_EXCEPTION), str3);
            return;
        }
        CallbackContext callbackContext = new CallbackContext(str3, this.app);
        try {
            long currentTimeMillis = System.currentTimeMillis();
            boolean execute = plugin.execute(str2, str4, callbackContext);
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            if (currentTimeMillis2 > ((long) SLOW_EXEC_WARNING_THRESHOLD)) {
                String str6 = TAG;
                LOG.w(str6, "THREAD WARNING: exec() call to " + str + "." + str2 + " blocked the main thread for " + currentTimeMillis2 + "ms. Plugin should use CordovaInterface.getThreadPool().");
            }
            if (!execute) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
            }
        } catch (JSONException unused) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
        } catch (Exception e) {
            LOG.e(TAG, "Uncaught exception from plugin", e);
            callbackContext.error(e.getMessage());
        }
    }

    public CordovaPlugin getPlugin(String str) {
        CordovaPlugin cordovaPlugin = this.pluginMap.get(str);
        if (cordovaPlugin == null) {
            PluginEntry pluginEntry = this.entryMap.get(str);
            if (pluginEntry == null) {
                return null;
            }
            if (pluginEntry.plugin != null) {
                cordovaPlugin = pluginEntry.plugin;
            } else {
                cordovaPlugin = instantiatePlugin(pluginEntry.pluginClass);
            }
            CordovaInterface cordovaInterface = this.ctx;
            CordovaWebView cordovaWebView = this.app;
            cordovaPlugin.privateInitialize(str, cordovaInterface, cordovaWebView, cordovaWebView.getPreferences());
            String str2 = TAG;
            LOG.d(str2, "getPlugin - put: " + str);
            this.pluginMap.put(str, cordovaPlugin);
        }
        return cordovaPlugin;
    }

    public void addService(String str, String str2) {
        addService(new PluginEntry(str, str2, false));
    }

    public void addService(PluginEntry pluginEntry) {
        this.entryMap.put(pluginEntry.service, pluginEntry);
        if (pluginEntry.plugin != null) {
            CordovaPlugin cordovaPlugin = pluginEntry.plugin;
            String str = pluginEntry.service;
            CordovaInterface cordovaInterface = this.ctx;
            CordovaWebView cordovaWebView = this.app;
            cordovaPlugin.privateInitialize(str, cordovaInterface, cordovaWebView, cordovaWebView.getPreferences());
            String str2 = TAG;
            LOG.d(str2, "addService: put - " + pluginEntry.service);
            this.pluginMap.put(pluginEntry.service, pluginEntry.plugin);
        }
    }

    public void onPause(boolean z) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (cordovaPlugin != null) {
                    cordovaPlugin.onPause(z);
                }
            }
        }
    }

    public boolean onReceivedHttpAuthRequest(CordovaWebView cordovaWebView, ICordovaHttpAuthHandler iCordovaHttpAuthHandler, String str, String str2) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (cordovaPlugin != null && cordovaPlugin.onReceivedHttpAuthRequest(this.app, iCordovaHttpAuthHandler, str, str2)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean onReceivedClientCertRequest(CordovaWebView cordovaWebView, ICordovaClientCertRequest iCordovaClientCertRequest) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (cordovaPlugin != null && cordovaPlugin.onReceivedClientCertRequest(this.app, iCordovaClientCertRequest)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void onResume(boolean z) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (cordovaPlugin != null) {
                    cordovaPlugin.onResume(z);
                }
            }
        }
    }

    public void onStart() {
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (cordovaPlugin != null) {
                    cordovaPlugin.onStart();
                }
            }
        }
    }

    public void onStop() {
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (cordovaPlugin != null) {
                    cordovaPlugin.onStop();
                }
            }
        }
    }

    public void onDestroy() {
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (cordovaPlugin != null) {
                    cordovaPlugin.onDestroy();
                }
            }
        }
    }

    public Object postMessage(String str, Object obj) {
        Object onMessage;
        String str2 = TAG;
        LOG.d(str2, "postMessage: " + str);
        synchronized (this.pluginMap) {
            if (Build.VERSION.SDK_INT >= 24) {
                this.pluginMap.forEach(new BiConsumer(str, obj) {
                    /* class org.apache.cordova.$$Lambda$PluginManager$xcuSTHftHalL3lGLtwuvtLz7M */
                    public final /* synthetic */ String f$0;
                    public final /* synthetic */ Object f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        PluginManager.lambda$postMessage$0(this.f$0, this.f$1, (String) obj, (CordovaPlugin) obj2);
                    }
                });
            } else {
                for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                    if (!(cordovaPlugin == null || (onMessage = cordovaPlugin.onMessage(str, obj)) == null)) {
                        return onMessage;
                    }
                }
            }
            return this.ctx.onMessage(str, obj);
        }
    }

    static /* synthetic */ void lambda$postMessage$0(String str, Object obj, String str2, CordovaPlugin cordovaPlugin) {
        if (cordovaPlugin != null) {
            cordovaPlugin.onMessage(str, obj);
        }
    }

    public void onNewIntent(Intent intent) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (cordovaPlugin != null) {
                    cordovaPlugin.onNewIntent(intent);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0039, code lost:
        if (r5.startsWith("blob:") != false) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0041, code lost:
        if (r5.startsWith("data:") != false) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0049, code lost:
        if (r5.startsWith("about:blank") == false) goto L_0x004c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0052, code lost:
        if (r5.startsWith("https://ssl.gstatic.com/accessibility/javascript/android/") == false) goto L_0x0055;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0054, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005b, code lost:
        if (r5.startsWith("file://") == false) goto L_0x0065;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0064, code lost:
        return !r5.contains("/app_webview/");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0065, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0067, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shouldAllowRequest(java.lang.String r5) {
        /*
            r4 = this;
            java.util.Map<java.lang.String, org.apache.cordova.PluginEntry> r0 = r4.entryMap
            monitor-enter(r0)
            java.util.Map<java.lang.String, org.apache.cordova.PluginEntry> r1 = r4.entryMap     // Catch:{ all -> 0x0068 }
            java.util.Collection r1 = r1.values()     // Catch:{ all -> 0x0068 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0068 }
        L_0x000d:
            boolean r2 = r1.hasNext()     // Catch:{ all -> 0x0068 }
            if (r2 == 0) goto L_0x0031
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x0068 }
            org.apache.cordova.PluginEntry r2 = (org.apache.cordova.PluginEntry) r2     // Catch:{ all -> 0x0068 }
            java.util.Map<java.lang.String, org.apache.cordova.CordovaPlugin> r3 = r4.pluginMap     // Catch:{ all -> 0x0068 }
            java.lang.String r2 = r2.service     // Catch:{ all -> 0x0068 }
            java.lang.Object r2 = r3.get(r2)     // Catch:{ all -> 0x0068 }
            org.apache.cordova.CordovaPlugin r2 = (org.apache.cordova.CordovaPlugin) r2     // Catch:{ all -> 0x0068 }
            if (r2 == 0) goto L_0x000d
            java.lang.Boolean r2 = r2.shouldAllowRequest(r5)     // Catch:{ all -> 0x0068 }
            if (r2 == 0) goto L_0x000d
            boolean r5 = r2.booleanValue()     // Catch:{ all -> 0x0068 }
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            return r5
        L_0x0031:
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            java.lang.String r0 = "blob:"
            boolean r0 = r5.startsWith(r0)
            r1 = 1
            if (r0 != 0) goto L_0x0067
            java.lang.String r0 = "data:"
            boolean r0 = r5.startsWith(r0)
            if (r0 != 0) goto L_0x0067
            java.lang.String r0 = "about:blank"
            boolean r0 = r5.startsWith(r0)
            if (r0 == 0) goto L_0x004c
            goto L_0x0067
        L_0x004c:
            java.lang.String r0 = "https://ssl.gstatic.com/accessibility/javascript/android/"
            boolean r0 = r5.startsWith(r0)
            if (r0 == 0) goto L_0x0055
            return r1
        L_0x0055:
            java.lang.String r0 = "file://"
            boolean r0 = r5.startsWith(r0)
            if (r0 == 0) goto L_0x0065
            java.lang.String r0 = "/app_webview/"
            boolean r5 = r5.contains(r0)
            r5 = r5 ^ r1
            return r5
        L_0x0065:
            r5 = 0
            return r5
        L_0x0067:
            return r1
        L_0x0068:
            r5 = move-exception
            monitor-exit(r0)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.PluginManager.shouldAllowRequest(java.lang.String):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0038, code lost:
        if (r5.startsWith("file://") != false) goto L_0x0045;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0040, code lost:
        if (r5.startsWith("about:blank") == false) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0043, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0045, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shouldAllowNavigation(java.lang.String r5) {
        /*
            r4 = this;
            java.util.Map<java.lang.String, org.apache.cordova.PluginEntry> r0 = r4.entryMap
            monitor-enter(r0)
            java.util.Map<java.lang.String, org.apache.cordova.PluginEntry> r1 = r4.entryMap     // Catch:{ all -> 0x0047 }
            java.util.Collection r1 = r1.values()     // Catch:{ all -> 0x0047 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0047 }
        L_0x000d:
            boolean r2 = r1.hasNext()     // Catch:{ all -> 0x0047 }
            if (r2 == 0) goto L_0x0031
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x0047 }
            org.apache.cordova.PluginEntry r2 = (org.apache.cordova.PluginEntry) r2     // Catch:{ all -> 0x0047 }
            java.util.Map<java.lang.String, org.apache.cordova.CordovaPlugin> r3 = r4.pluginMap     // Catch:{ all -> 0x0047 }
            java.lang.String r2 = r2.service     // Catch:{ all -> 0x0047 }
            java.lang.Object r2 = r3.get(r2)     // Catch:{ all -> 0x0047 }
            org.apache.cordova.CordovaPlugin r2 = (org.apache.cordova.CordovaPlugin) r2     // Catch:{ all -> 0x0047 }
            if (r2 == 0) goto L_0x000d
            java.lang.Boolean r2 = r2.shouldAllowNavigation(r5)     // Catch:{ all -> 0x0047 }
            if (r2 == 0) goto L_0x000d
            boolean r5 = r2.booleanValue()     // Catch:{ all -> 0x0047 }
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            return r5
        L_0x0031:
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            java.lang.String r0 = "file://"
            boolean r0 = r5.startsWith(r0)
            if (r0 != 0) goto L_0x0045
            java.lang.String r0 = "about:blank"
            boolean r5 = r5.startsWith(r0)
            if (r5 == 0) goto L_0x0043
            goto L_0x0045
        L_0x0043:
            r5 = 0
            goto L_0x0046
        L_0x0045:
            r5 = 1
        L_0x0046:
            return r5
        L_0x0047:
            r5 = move-exception
            monitor-exit(r0)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.PluginManager.shouldAllowNavigation(java.lang.String):boolean");
    }

    public boolean shouldAllowBridgeAccess(String str) {
        Boolean shouldAllowBridgeAccess;
        synchronized (this.entryMap) {
            for (PluginEntry pluginEntry : this.entryMap.values()) {
                CordovaPlugin cordovaPlugin = this.pluginMap.get(pluginEntry.service);
                if (!(cordovaPlugin == null || (shouldAllowBridgeAccess = cordovaPlugin.shouldAllowBridgeAccess(str)) == null)) {
                    return shouldAllowBridgeAccess.booleanValue();
                }
            }
            return str.startsWith("file://");
        }
    }

    public Boolean shouldOpenExternalUrl(String str) {
        Boolean shouldOpenExternalUrl;
        synchronized (this.entryMap) {
            for (PluginEntry pluginEntry : this.entryMap.values()) {
                CordovaPlugin cordovaPlugin = this.pluginMap.get(pluginEntry.service);
                if (!(cordovaPlugin == null || (shouldOpenExternalUrl = cordovaPlugin.shouldOpenExternalUrl(str)) == null)) {
                    return shouldOpenExternalUrl;
                }
            }
            return false;
        }
    }

    public boolean onOverrideUrlLoading(String str) {
        synchronized (this.entryMap) {
            for (PluginEntry pluginEntry : this.entryMap.values()) {
                CordovaPlugin cordovaPlugin = this.pluginMap.get(pluginEntry.service);
                if (cordovaPlugin != null && cordovaPlugin.onOverrideUrlLoading(str)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void onReset() {
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (cordovaPlugin != null) {
                    cordovaPlugin.onReset();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Uri remapUri(Uri uri) {
        Uri remapUri;
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (!(cordovaPlugin == null || (remapUri = cordovaPlugin.remapUri(uri)) == null)) {
                    return remapUri;
                }
            }
            return null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0015 A[Catch:{ Exception -> 0x0010 }] */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0017 A[Catch:{ Exception -> 0x0010 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0021 A[Catch:{ Exception -> 0x0010 }] */
    /* JADX WARNING: Removed duplicated region for block: B:19:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.apache.cordova.CordovaPlugin instantiatePlugin(java.lang.String r5) {
        /*
            r4 = this;
            r0 = 0
            if (r5 == 0) goto L_0x0012
            java.lang.String r1 = ""
            boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x0010 }
            if (r1 != 0) goto L_0x0012
            java.lang.Class r1 = java.lang.Class.forName(r5)     // Catch:{ Exception -> 0x0010 }
            goto L_0x0013
        L_0x0010:
            r1 = move-exception
            goto L_0x0029
        L_0x0012:
            r1 = r0
        L_0x0013:
            if (r1 == 0) goto L_0x0017
            r2 = 1
            goto L_0x0018
        L_0x0017:
            r2 = 0
        L_0x0018:
            java.lang.Class<org.apache.cordova.CordovaPlugin> r3 = org.apache.cordova.CordovaPlugin.class
            boolean r3 = r3.isAssignableFrom(r1)     // Catch:{ Exception -> 0x0010 }
            r2 = r2 & r3
            if (r2 == 0) goto L_0x0047
            java.lang.Object r1 = r1.newInstance()     // Catch:{ Exception -> 0x0010 }
            org.apache.cordova.CordovaPlugin r1 = (org.apache.cordova.CordovaPlugin) r1     // Catch:{ Exception -> 0x0010 }
            r0 = r1
            goto L_0x0047
        L_0x0029:
            r1.printStackTrace()
            java.io.PrintStream r1 = java.lang.System.out
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Error adding plugin "
            r2.append(r3)
            r2.append(r5)
            java.lang.String r5 = "."
            r2.append(r5)
            java.lang.String r5 = r2.toString()
            r1.println(r5)
        L_0x0047:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.PluginManager.instantiatePlugin(java.lang.String):org.apache.cordova.CordovaPlugin");
    }

    public void onConfigurationChanged(Configuration configuration) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (cordovaPlugin != null) {
                    cordovaPlugin.onConfigurationChanged(configuration);
                }
            }
        }
    }

    public Bundle onSaveInstanceState() {
        Bundle onSaveInstanceState;
        Bundle bundle = new Bundle();
        synchronized (this.pluginMap) {
            for (CordovaPlugin cordovaPlugin : this.pluginMap.values()) {
                if (!(cordovaPlugin == null || (onSaveInstanceState = cordovaPlugin.onSaveInstanceState()) == null)) {
                    bundle.putBundle(cordovaPlugin.getServiceName(), onSaveInstanceState);
                }
            }
        }
        return bundle;
    }
}
