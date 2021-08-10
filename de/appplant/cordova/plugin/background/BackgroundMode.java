package de.appplant.cordova.plugin.background;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import de.appplant.cordova.plugin.background.ForegroundService;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONObject;

public class BackgroundMode extends CordovaPlugin {
    private static final String JS_NAMESPACE = "cordova.plugins.backgroundMode";
    private static JSONObject defaultSettings = new JSONObject();
    private final ServiceConnection connection = new ServiceConnection() {
        /* class de.appplant.cordova.plugin.background.BackgroundMode.AnonymousClass1 */

        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundMode.this.service = ((ForegroundService.ForegroundBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            BackgroundMode.this.fireEvent(Event.FAILURE, "'service disconnected'");
        }
    };
    private boolean inBackground = false;
    private boolean isBind = false;
    private boolean isDisabled = true;
    private ForegroundService service;

    /* access modifiers changed from: private */
    public enum Event {
        ACTIVATE,
        DEACTIVATE,
        FAILURE
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0039  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0059  */
    @Override // org.apache.cordova.CordovaPlugin
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean execute(java.lang.String r7, org.json.JSONArray r8, org.apache.cordova.CallbackContext r9) {
        /*
            r6 = this;
            r0 = 1
            int r1 = r7.hashCode()
            r2 = -1298848381(0xffffffffb2952583, float:-1.7362941E-8)
            r3 = 0
            r4 = 2
            r5 = 1
            if (r1 == r2) goto L_0x002c
            r2 = -804429082(0xffffffffd00d62e6, float:-9.4882755E9)
            if (r1 == r2) goto L_0x0022
            r2 = 1671308008(0x639e22e8, float:5.8342016E21)
            if (r1 == r2) goto L_0x0018
        L_0x0017:
            goto L_0x0036
        L_0x0018:
            java.lang.String r1 = "disable"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0017
            r1 = 2
            goto L_0x0037
        L_0x0022:
            java.lang.String r1 = "configure"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0017
            r1 = 0
            goto L_0x0037
        L_0x002c:
            java.lang.String r1 = "enable"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0017
            r1 = 1
            goto L_0x0037
        L_0x0036:
            r1 = -1
        L_0x0037:
            if (r1 == 0) goto L_0x0047
            if (r1 == r5) goto L_0x0043
            if (r1 == r4) goto L_0x003f
            r0 = 0
            goto L_0x0053
        L_0x003f:
            r6.disableMode()
            goto L_0x0053
        L_0x0043:
            r6.enableMode()
            goto L_0x0053
        L_0x0047:
            org.json.JSONObject r1 = r8.optJSONObject(r3)
            boolean r2 = r8.optBoolean(r5)
            r6.configure(r1, r2)
        L_0x0053:
            if (r0 == 0) goto L_0x0059
            r9.success()
            goto L_0x006d
        L_0x0059:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid action: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            r9.error(r1)
        L_0x006d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: de.appplant.cordova.plugin.background.BackgroundMode.execute(java.lang.String, org.json.JSONArray, org.apache.cordova.CallbackContext):boolean");
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onPause(boolean multitasking) {
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
    public void onResume(boolean multitasking) {
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

    private void configure(JSONObject settings, boolean update) {
        if (update) {
            updateNotification(settings);
        } else {
            setDefaultSettings(settings);
        }
    }

    private void setDefaultSettings(JSONObject settings) {
        defaultSettings = settings;
    }

    static JSONObject getSettings() {
        return defaultSettings;
    }

    private void updateNotification(JSONObject settings) {
        if (this.isBind) {
            this.service.updateNotification(settings);
        }
    }

    private void startService() {
        Activity context = this.cordova.getActivity();
        if (!this.isDisabled && !this.isBind) {
            Intent intent = new Intent(context, ForegroundService.class);
            try {
                context.bindService(intent, this.connection, 1);
                fireEvent(Event.ACTIVATE, null);
                context.startService(intent);
            } catch (Exception e) {
                fireEvent(Event.FAILURE, String.format("'%s'", e.getMessage()));
            }
            this.isBind = true;
        }
    }

    private void stopService() {
        Activity context = this.cordova.getActivity();
        Intent intent = new Intent(context, ForegroundService.class);
        if (this.isBind) {
            fireEvent(Event.DEACTIVATE, null);
            context.unbindService(this.connection);
            context.stopService(intent);
            this.isBind = false;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void fireEvent(Event event, String params) {
        String eventName = event.name().toLowerCase();
        this.cordova.getActivity().runOnUiThread(new Runnable(String.format("%s;%s.fireEvent('%s',%s);", String.format("%s;%s.on('%s', %s)", String.format("%s._setActive(%b)", JS_NAMESPACE, Boolean.valueOf(event == Event.ACTIVATE)), JS_NAMESPACE, eventName, params), JS_NAMESPACE, eventName, params)) {
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

    public /* synthetic */ void lambda$fireEvent$0$BackgroundMode(String js) {
        CordovaWebView cordovaWebView = this.webView;
        cordovaWebView.loadUrl("javascript:" + js);
    }
}
