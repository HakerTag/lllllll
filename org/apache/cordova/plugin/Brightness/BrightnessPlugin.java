package org.apache.cordova.plugin.Brightness;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class BrightnessPlugin extends CordovaPlugin {

    public enum Action {
        setBrightness,
        getBrightness,
        setKeepScreenOn
    }

    /* access modifiers changed from: private */
    public class SetTask implements Runnable {
        private WindowManager.LayoutParams lp;
        private Activity target;

        private SetTask() {
            this.target = null;
            this.lp = null;
        }

        /* synthetic */ SetTask(BrightnessPlugin brightnessPlugin, AnonymousClass1 r2) {
            this();
        }

        public void run() {
            this.target.getWindow().setAttributes(this.lp);
        }

        public void setParams(Activity activity, WindowManager.LayoutParams layoutParams) {
            this.target = activity;
            this.lp = layoutParams;
        }
    }

    /* access modifiers changed from: private */
    public class KeepOnTask implements Runnable {
        private boolean state;
        private Window win;

        private KeepOnTask() {
            this.win = null;
            this.state = false;
        }

        /* synthetic */ KeepOnTask(BrightnessPlugin brightnessPlugin, AnonymousClass1 r2) {
            this();
        }

        public void run() {
            if (this.state) {
                this.win.addFlags(128);
            } else {
                this.win.clearFlags(128);
            }
        }

        public void setParams(Window window, boolean z) {
            this.win = window;
            this.state = z;
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        System.out.println("plugin has been started");
        int i = AnonymousClass1.$SwitchMap$org$apache$cordova$plugin$Brightness$BrightnessPlugin$Action[Action.valueOf(str).ordinal()];
        if (i == 1) {
            setBrightness(jSONArray, callbackContext);
            return true;
        } else if (i == 2) {
            getBrightness(jSONArray, callbackContext);
            return true;
        } else if (i != 3) {
            return false;
        } else {
            setKeepScreenOn(jSONArray, callbackContext);
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: org.apache.cordova.plugin.Brightness.BrightnessPlugin$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$cordova$plugin$Brightness$BrightnessPlugin$Action;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                org.apache.cordova.plugin.Brightness.BrightnessPlugin$Action[] r0 = org.apache.cordova.plugin.Brightness.BrightnessPlugin.Action.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                org.apache.cordova.plugin.Brightness.BrightnessPlugin.AnonymousClass1.$SwitchMap$org$apache$cordova$plugin$Brightness$BrightnessPlugin$Action = r0
                org.apache.cordova.plugin.Brightness.BrightnessPlugin$Action r1 = org.apache.cordova.plugin.Brightness.BrightnessPlugin.Action.setBrightness     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = org.apache.cordova.plugin.Brightness.BrightnessPlugin.AnonymousClass1.$SwitchMap$org$apache$cordova$plugin$Brightness$BrightnessPlugin$Action     // Catch:{ NoSuchFieldError -> 0x001d }
                org.apache.cordova.plugin.Brightness.BrightnessPlugin$Action r1 = org.apache.cordova.plugin.Brightness.BrightnessPlugin.Action.getBrightness     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = org.apache.cordova.plugin.Brightness.BrightnessPlugin.AnonymousClass1.$SwitchMap$org$apache$cordova$plugin$Brightness$BrightnessPlugin$Action     // Catch:{ NoSuchFieldError -> 0x0028 }
                org.apache.cordova.plugin.Brightness.BrightnessPlugin$Action r1 = org.apache.cordova.plugin.Brightness.BrightnessPlugin.Action.setKeepScreenOn     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.plugin.Brightness.BrightnessPlugin.AnonymousClass1.<clinit>():void");
        }
    }

    private boolean setBrightness(JSONArray jSONArray, CallbackContext callbackContext) {
        try {
            Activity activity = this.cordova.getActivity();
            WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
            attributes.screenBrightness = (float) Double.parseDouble(jSONArray.getString(0));
            SetTask setTask = new SetTask(this, null);
            setTask.setParams(activity, attributes);
            activity.runOnUiThread(setTask);
            callbackContext.success("OK");
            System.out.println("All went fine.");
            return true;
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
            System.out.println(e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        } catch (JSONException e2) {
            System.out.println("JSONException exception");
            System.out.println(e2.getMessage());
            callbackContext.error(e2.getMessage());
            return false;
        }
    }

    private boolean getBrightness(JSONArray jSONArray, CallbackContext callbackContext) {
        try {
            callbackContext.success(Double.valueOf((double) this.cordova.getActivity().getWindow().getAttributes().screenBrightness).toString());
            System.out.println("All went fine.");
            return true;
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
            System.out.println(e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        }
    }

    private boolean setKeepScreenOn(JSONArray jSONArray, CallbackContext callbackContext) {
        try {
            boolean z = jSONArray.getBoolean(0);
            Activity activity = this.cordova.getActivity();
            KeepOnTask keepOnTask = new KeepOnTask(this, null);
            keepOnTask.setParams(activity.getWindow(), z);
            activity.runOnUiThread(keepOnTask);
            callbackContext.success("OK");
            System.out.println("All went fine.");
            return true;
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
            System.out.println(e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        } catch (JSONException e2) {
            System.out.println("JSONException");
            System.out.println(e2.getMessage());
            callbackContext.error(e2.getMessage());
            return false;
        }
    }
}
