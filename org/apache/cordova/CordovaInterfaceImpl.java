package org.apache.cordova;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class CordovaInterfaceImpl implements CordovaInterface {
    private static final String TAG = "CordovaInterfaceImpl";
    protected Activity activity;
    protected CordovaPlugin activityResultCallback;
    protected int activityResultRequestCode;
    protected boolean activityWasDestroyed;
    protected String initCallbackService;
    protected CallbackMap permissionResultCallbacks;
    protected PluginManager pluginManager;
    protected Bundle savedPluginState;
    protected ActivityResultHolder savedResult;
    protected ExecutorService threadPool;

    public CordovaInterfaceImpl(Activity activity2) {
        this(activity2, Executors.newCachedThreadPool());
    }

    public CordovaInterfaceImpl(Activity activity2, ExecutorService threadPool2) {
        this.activityWasDestroyed = false;
        this.activity = activity2;
        this.threadPool = threadPool2;
        this.permissionResultCallbacks = new CallbackMap();
    }

    @Override // org.apache.cordova.CordovaInterface
    public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {
        setActivityResultCallback(command);
        try {
            this.activity.startActivityForResult(intent, requestCode);
        } catch (RuntimeException e) {
            this.activityResultCallback = null;
            throw e;
        }
    }

    @Override // org.apache.cordova.CordovaInterface
    public void setActivityResultCallback(CordovaPlugin plugin) {
        CordovaPlugin cordovaPlugin = this.activityResultCallback;
        if (cordovaPlugin != null) {
            cordovaPlugin.onActivityResult(this.activityResultRequestCode, 0, null);
        }
        this.activityResultCallback = plugin;
    }

    @Override // org.apache.cordova.CordovaInterface
    public Activity getActivity() {
        return this.activity;
    }

    @Override // org.apache.cordova.CordovaInterface
    public Context getContext() {
        return this.activity;
    }

    @Override // org.apache.cordova.CordovaInterface
    public Object onMessage(String id, Object data) {
        if (!"exit".equals(id)) {
            return null;
        }
        this.activity.finish();
        return null;
    }

    @Override // org.apache.cordova.CordovaInterface
    public ExecutorService getThreadPool() {
        return this.threadPool;
    }

    public void onCordovaInit(PluginManager pluginManager2) {
        CoreAndroid appPlugin;
        this.pluginManager = pluginManager2;
        ActivityResultHolder activityResultHolder = this.savedResult;
        if (activityResultHolder != null) {
            onActivityResult(activityResultHolder.requestCode, this.savedResult.resultCode, this.savedResult.intent);
        } else if (this.activityWasDestroyed) {
            this.activityWasDestroyed = false;
            if (pluginManager2 != null && (appPlugin = (CoreAndroid) pluginManager2.getPlugin(CoreAndroid.PLUGIN_NAME)) != null) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("action", "resume");
                } catch (JSONException e) {
                    LOG.e(TAG, "Failed to create event message", e);
                }
                appPlugin.sendResumeEvent(new PluginResult(PluginResult.Status.OK, obj));
            }
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        CordovaPlugin callback = this.activityResultCallback;
        if (callback == null && this.initCallbackService != null) {
            this.savedResult = new ActivityResultHolder(requestCode, resultCode, intent);
            PluginManager pluginManager2 = this.pluginManager;
            if (!(pluginManager2 == null || (callback = pluginManager2.getPlugin(this.initCallbackService)) == null)) {
                callback.onRestoreStateForActivityResult(this.savedPluginState.getBundle(callback.getServiceName()), new ResumeCallback(callback.getServiceName(), this.pluginManager));
            }
        }
        this.activityResultCallback = null;
        if (callback != null) {
            LOG.d(TAG, "Sending activity result to plugin");
            this.initCallbackService = null;
            this.savedResult = null;
            callback.onActivityResult(requestCode, resultCode, intent);
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Got an activity result, but no plugin was registered to receive it");
        sb.append(this.savedResult != null ? " yet!" : ".");
        LOG.w(TAG, sb.toString());
        return false;
    }

    public void setActivityResultRequestCode(int requestCode) {
        this.activityResultRequestCode = requestCode;
    }

    public void onSaveInstanceState(Bundle outState) {
        CordovaPlugin cordovaPlugin = this.activityResultCallback;
        if (cordovaPlugin != null) {
            outState.putString("callbackService", cordovaPlugin.getServiceName());
        }
        PluginManager pluginManager2 = this.pluginManager;
        if (pluginManager2 != null) {
            outState.putBundle("plugin", pluginManager2.onSaveInstanceState());
        }
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        this.initCallbackService = savedInstanceState.getString("callbackService");
        this.savedPluginState = savedInstanceState.getBundle("plugin");
        this.activityWasDestroyed = true;
    }

    /* access modifiers changed from: private */
    public static class ActivityResultHolder {
        private Intent intent;
        private int requestCode;
        private int resultCode;

        public ActivityResultHolder(int requestCode2, int resultCode2, Intent intent2) {
            this.requestCode = requestCode2;
            this.resultCode = resultCode2;
            this.intent = intent2;
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        Pair<CordovaPlugin, Integer> callback = this.permissionResultCallbacks.getAndRemoveCallback(requestCode);
        if (callback != null) {
            ((CordovaPlugin) callback.first).onRequestPermissionResult(((Integer) callback.second).intValue(), permissions, grantResults);
        }
    }

    @Override // org.apache.cordova.CordovaInterface
    public void requestPermission(CordovaPlugin plugin, int requestCode, String permission) {
        requestPermissions(plugin, requestCode, new String[]{permission});
    }

    @Override // org.apache.cordova.CordovaInterface
    @SuppressLint({"NewApi"})
    public void requestPermissions(CordovaPlugin plugin, int requestCode, String[] permissions) {
        getActivity().requestPermissions(permissions, this.permissionResultCallbacks.registerCallback(plugin, requestCode));
    }

    @Override // org.apache.cordova.CordovaInterface
    public boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT < 23 || this.activity.checkSelfPermission(permission) == 0) {
            return true;
        }
        return false;
    }
}
