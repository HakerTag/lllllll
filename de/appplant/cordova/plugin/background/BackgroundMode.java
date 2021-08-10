package de.appplant.cordova.plugin.background;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import de.appplant.cordova.plugin.background.ForegroundService;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONObject;

public class BackgroundMode extends CordovaPlugin {
    private static final String JS_NAMESPACE = "cordova.plugins.backgroundMode";
    private static JSONObject defaultSettings = new JSONObject();
    private final ServiceConnection connection = new ServiceConnection() {
        /* class de.appplant.cordova.plugin.background.BackgroundMode.AnonymousClass1 */

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackgroundMode.this.service = ((ForegroundService.ForegroundBinder) iBinder).getService();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            BackgroundMode.this.fireEvent(Event.FAILURE, "'service disconnected'");
        }
    };
    private boolean inBackground = false;
    private boolean isBind = false;
    private boolean isDisabled = true;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        /* class de.appplant.cordova.plugin.background.BackgroundMode.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            BackgroundMode.this.cordova.getActivity().finish();
        }
    };
    private ForegroundService service;

    /* access modifiers changed from: private */
    public enum Event {
        ACTIVATE,
        DEACTIVATE,
        FAILURE
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        super.initialize(cordovaInterface, cordovaWebView);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.backgroundmode.close" + cordovaInterface.getContext().getPackageName());
        cordovaInterface.getActivity().registerReceiver(this.receiver, intentFilter);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0057  */
    @Override // org.apache.cordova.CordovaPlugin
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean execute(java.lang.String r6, org.json.JSONArray r7, org.apache.cordova.CallbackContext r8) {
        /*
            r5 = this;
            int r0 = r6.hashCode()
            r1 = -1298848381(0xffffffffb2952583, float:-1.7362941E-8)
            r2 = 2
            r3 = 0
            r4 = 1
            if (r0 == r1) goto L_0x002b
            r1 = -804429082(0xffffffffd00d62e6, float:-9.4882755E9)
            if (r0 == r1) goto L_0x0021
            r1 = 1671308008(0x639e22e8, float:5.8342016E21)
            if (r0 == r1) goto L_0x0017
            goto L_0x0035
        L_0x0017:
            java.lang.String r0 = "disable"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0035
            r0 = 2
            goto L_0x0036
        L_0x0021:
            java.lang.String r0 = "configure"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0035
            r0 = 0
            goto L_0x0036
        L_0x002b:
            java.lang.String r0 = "enable"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0035
            r0 = 1
            goto L_0x0036
        L_0x0035:
            r0 = -1
        L_0x0036:
            if (r0 == 0) goto L_0x0045
            if (r0 == r4) goto L_0x0041
            if (r0 == r2) goto L_0x003d
            goto L_0x0051
        L_0x003d:
            r5.disableMode()
            goto L_0x0050
        L_0x0041:
            r5.enableMode()
            goto L_0x0050
        L_0x0045:
            org.json.JSONObject r0 = r7.optJSONObject(r3)
            boolean r7 = r7.optBoolean(r4)
            r5.configure(r0, r7)
        L_0x0050:
            r3 = 1
        L_0x0051:
            if (r3 == 0) goto L_0x0057
            r8.success()
            goto L_0x006b
        L_0x0057:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r0 = "Invalid action: "
            r7.append(r0)
            r7.append(r6)
            java.lang.String r6 = r7.toString()
            r8.error(r6)
        L_0x006b:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: de.appplant.cordova.plugin.background.BackgroundMode.execute(java.lang.String, org.json.JSONArray, org.apache.cordova.CallbackContext):boolean");
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onPause(boolean z) {
        try {
            this.inBackground = true;
            startService();
        } finally {
            BackgroundModeExt.clearKeyguardFlags(this.cordova.getActivity());
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onStop() {
        BackgroundModeExt.clearKeyguardFlags(this.cordova.getActivity());
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onResume(boolean z) {
        this.inBackground = false;
        stopService();
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onDestroy() {
        stopService();
        Process.killProcess(Process.myPid());
    }

    private void enableMode() {
        this.isDisabled = false;
        if (this.inBackground) {
            startService();
        }
    }

    private void disableMode() {
        stopService();
        this.isDisabled = true;
    }

    private void configure(JSONObject jSONObject, boolean z) {
        if (z) {
            updateNotification(jSONObject);
        } else {
            setDefaultSettings(jSONObject);
        }
    }

    private void setDefaultSettings(JSONObject jSONObject) {
        defaultSettings = jSONObject;
    }

    static JSONObject getSettings() {
        return defaultSettings;
    }

    private void updateNotification(JSONObject jSONObject) {
        if (this.isBind) {
            this.service.updateNotification(jSONObject);
        }
    }

    private void startService() {
        Activity activity = this.cordova.getActivity();
        if (!this.isDisabled && !this.isBind) {
            Intent intent = new Intent(activity, ForegroundService.class);
            try {
                activity.bindService(intent, this.connection, 1);
                fireEvent(Event.ACTIVATE, null);
                activity.startService(intent);
            } catch (Exception e) {
                fireEvent(Event.FAILURE, String.format("'%s'", e.getMessage()));
            }
            this.isBind = true;
        }
    }

    private void stopService() {
        Activity activity = this.cordova.getActivity();
        Intent intent = new Intent(activity, ForegroundService.class);
        if (this.isBind) {
            fireEvent(Event.DEACTIVATE, null);
            activity.unbindService(this.connection);
            activity.stopService(intent);
            this.isBind = false;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void fireEvent(Event event, String str) {
        String lowerCase = event.name().toLowerCase();
        this.cordova.getActivity().runOnUiThread(new Runnable(String.format("%s;%s.fireEvent('%s',%s);", String.format("%s;%s.on('%s', %s)", String.format("%s._setActive(%b)", JS_NAMESPACE, Boolean.valueOf(event == Event.ACTIVATE)), JS_NAMESPACE, lowerCase, str), JS_NAMESPACE, lowerCase, str)) {
            /* class de.appplant.cordova.plugin.background.$$Lambda$BackgroundMode$K6y_lRKzoGM6t6fzblSCZ0XuQ */
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BackgroundMode.this.lambda$fireEvent$0$BackgroundMode(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$fireEvent$0$BackgroundMode(String str) {
        CordovaWebView cordovaWebView = this.webView;
        cordovaWebView.loadUrl("javascript:" + str);
    }
}
