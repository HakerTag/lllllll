package de.appplant.cordova.plugin.background;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.view.View;
import de.appplant.cordova.plugin.background.BackgroundModeExt;
import java.util.Arrays;
import java.util.List;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

public class BackgroundModeExt extends CordovaPlugin {
    private PowerManager.WakeLock wakeLock;

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String action, JSONArray args, CallbackContext callback) {
        char c;
        boolean validAction = true;
        switch (action.hashCode()) {
            case -1332194002:
                if (action.equals("background")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1331727292:
                if (action.equals("dimmed")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -840442044:
                if (action.equals("unlock")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -795228353:
                if (action.equals("wakeup")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -409429085:
                if (action.equals("tasklist")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -331239923:
                if (action.equals("battery")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1186297569:
                if (action.equals("appstart")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1224424441:
                if (action.equals("webview")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1984457027:
                if (action.equals("foreground")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                disableBatteryOptimizations();
                break;
            case 1:
                disableWebViewOptimizations();
                break;
            case 2:
                openAppStart(args.opt(0));
                break;
            case 3:
                moveToBackground();
                break;
            case 4:
                moveToForeground();
                break;
            case 5:
                excludeFromTaskList();
                break;
            case 6:
                isDimmed(callback);
                break;
            case 7:
                wakeup();
                break;
            case '\b':
                wakeup();
                unlock();
                break;
            default:
                validAction = false;
                break;
        }
        if (validAction) {
            callback.success();
        } else {
            callback.error("Invalid action: " + action);
        }
        return validAction;
    }

    private void moveToBackground() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        getApp().startActivity(intent);
    }

    private void moveToForeground() {
        Activity app = getApp();
        Intent intent = getLaunchIntent();
        intent.addFlags(604110848);
        clearScreenAndKeyguardFlags();
        app.startActivity(intent);
    }

    private void disableWebViewOptimizations() {
        new Thread() {
            /* class de.appplant.cordova.plugin.background.BackgroundModeExt.AnonymousClass1 */

            public void run() {
                try {
                    Thread.sleep(1000);
                    BackgroundModeExt.this.getApp().runOnUiThread(new Runnable() {
                        /* class de.appplant.cordova.plugin.background.$$Lambda$BackgroundModeExt$1$G8BcCUGm1NdL43D3hx0oXWRmZqc */

                        public final void run() {
                            BackgroundModeExt.AnonymousClass1.this.lambda$run$0$BackgroundModeExt$1();
                        }
                    });
                } catch (InterruptedException e) {
                }
            }

            public /* synthetic */ void lambda$run$0$BackgroundModeExt$1() {
                View view = BackgroundModeExt.this.webView.getEngine().getView();
                try {
                    Class.forName("org.crosswalk.engine.XWalkCordovaView").getMethod("onShow", new Class[0]).invoke(view, new Object[0]);
                } catch (Exception e) {
                    view.dispatchWindowVisibilityChanged(0);
                }
            }
        }.start();
    }

    private void disableBatteryOptimizations() {
        Activity activity = this.cordova.getActivity();
        Intent intent = new Intent();
        String pkgName = activity.getPackageName();
        PowerManager pm = (PowerManager) getService("power");
        if (Build.VERSION.SDK_INT >= 23 && !pm.isIgnoringBatteryOptimizations(pkgName)) {
            intent.setAction("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
            intent.setData(Uri.parse("package:" + pkgName));
            this.cordova.getActivity().startActivity(intent);
        }
    }

    private void openAppStart(Object arg) {
        Activity activity = this.cordova.getActivity();
        PackageManager pm = activity.getPackageManager();
        for (Intent intent : getAppStartIntents()) {
            if (pm.resolveActivity(intent, 65536) != null) {
                JSONObject spec = arg instanceof JSONObject ? (JSONObject) arg : null;
                intent.addFlags(268435456);
                if (!(arg instanceof Boolean) || ((Boolean) arg).booleanValue()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity, 16974130);
                    dialog.setPositiveButton(17039370, new DialogInterface.OnClickListener(activity, intent) {
                        /* class de.appplant.cordova.plugin.background.$$Lambda$BackgroundModeExt$ERKfr3wD6vup_JSOqk4oaG0vRD8 */
                        public final /* synthetic */ Activity f$0;
                        public final /* synthetic */ Intent f$1;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            this.f$0.startActivity(this.f$1);
                        }
                    });
                    dialog.setNegativeButton(17039360, $$Lambda$BackgroundModeExt$bUrtZtagSgT3rMs8xgk8HpHR0ic.INSTANCE);
                    dialog.setCancelable(true);
                    if (spec != null && spec.has("title")) {
                        dialog.setTitle(spec.optString("title"));
                    }
                    if (spec == null || !spec.has("text")) {
                        dialog.setMessage("missing text");
                    } else {
                        dialog.setMessage(spec.optString("text"));
                    }
                    dialog.getClass();
                    activity.runOnUiThread(new Runnable(dialog) {
                        /* class de.appplant.cordova.plugin.background.$$Lambda$3Qlr8fEzXfR35vEnzutL7VyLKKA */
                        public final /* synthetic */ AlertDialog.Builder f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void run() {
                            this.f$0.show();
                        }
                    });
                    return;
                }
                activity.startActivity(intent);
                return;
            }
        }
    }

    static /* synthetic */ void lambda$openAppStart$1(DialogInterface o, int d) {
    }

    private void excludeFromTaskList() {
        List<ActivityManager.AppTask> tasks;
        ActivityManager am = (ActivityManager) getService("activity");
        if (am != null && Build.VERSION.SDK_INT >= 21 && (tasks = am.getAppTasks()) != null && !tasks.isEmpty()) {
            tasks.get(0).setExcludeFromRecents(true);
        }
    }

    private void isDimmed(CallbackContext callback) {
        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, isDimmed()));
    }

    private boolean isDimmed() {
        PowerManager pm = (PowerManager) getService("power");
        if (Build.VERSION.SDK_INT < 20) {
            return !pm.isScreenOn();
        }
        return !pm.isInteractive();
    }

    private void wakeup() {
        try {
            acquireWakeLock();
        } catch (Exception e) {
            releaseWakeLock();
        }
    }

    private void unlock() {
        addSreenAndKeyguardFlags();
        getApp().startActivity(getLaunchIntent());
    }

    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getService("power");
        releaseWakeLock();
        if (isDimmed()) {
            PowerManager.WakeLock newWakeLock = pm.newWakeLock(268435462, "backgroundmode:wakelock");
            this.wakeLock = newWakeLock;
            newWakeLock.setReferenceCounted(false);
            this.wakeLock.acquire(1000);
        }
    }

    private void releaseWakeLock() {
        PowerManager.WakeLock wakeLock2 = this.wakeLock;
        if (wakeLock2 != null && wakeLock2.isHeld()) {
            this.wakeLock.release();
            this.wakeLock = null;
        }
    }

    private void addSreenAndKeyguardFlags() {
        getApp().runOnUiThread(new Runnable() {
            /* class de.appplant.cordova.plugin.background.$$Lambda$BackgroundModeExt$8opBXpb8qCacWv2tqu9xcHF59vc */

            public final void run() {
                BackgroundModeExt.this.lambda$addSreenAndKeyguardFlags$2$BackgroundModeExt();
            }
        });
    }

    public /* synthetic */ void lambda$addSreenAndKeyguardFlags$2$BackgroundModeExt() {
        getApp().getWindow().addFlags(6815745);
    }

    private void clearScreenAndKeyguardFlags() {
        getApp().runOnUiThread(new Runnable() {
            /* class de.appplant.cordova.plugin.background.$$Lambda$BackgroundModeExt$vbQiBHN_VJ1jzszXh2Sq7RaEgo */

            public final void run() {
                BackgroundModeExt.this.lambda$clearScreenAndKeyguardFlags$3$BackgroundModeExt();
            }
        });
    }

    public /* synthetic */ void lambda$clearScreenAndKeyguardFlags$3$BackgroundModeExt() {
        getApp().getWindow().clearFlags(6815745);
    }

    static void clearKeyguardFlags(Activity app) {
        app.runOnUiThread(new Runnable(app) {
            /* class de.appplant.cordova.plugin.background.$$Lambda$BackgroundModeExt$dhGWJsWDWa3mV1m10yf3_A_c9oE */
            public final /* synthetic */ Activity f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.getWindow().clearFlags(4194304);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public Activity getApp() {
        return this.cordova.getActivity();
    }

    private Intent getLaunchIntent() {
        Context app = getApp().getApplicationContext();
        return app.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
    }

    private Object getService(String name) {
        return getApp().getSystemService(name);
    }

    private List<Intent> getAppStartIntents() {
        return Arrays.asList(new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")), new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")), new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")), new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")), new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")), new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")), new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")), new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")), new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")), new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")), new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")), new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(Uri.parse("mobilemanager://function/entry/AutoStart")), new Intent().setAction("com.letv.android.permissionautoboot"), new Intent().setComponent(new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.ram.AutoRunActivity")), new Intent().setComponent(ComponentName.unflattenFromString("com.iqoo.secure/.MainActivity")), new Intent().setComponent(ComponentName.unflattenFromString("com.meizu.safe/.permission.SmartBGActivity")), new Intent().setComponent(new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity")), new Intent().setComponent(new ComponentName("cn.nubia.security2", "cn.nubia.security.appmanage.selfstart.ui.SelfStartActivity")), new Intent().setComponent(new ComponentName("com.zui.safecenter", "com.lenovo.safecenter.MainTab.LeSafeMainActivity")));
    }
}
