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

        /* synthetic */ SetTask(BrightnessPlugin x0, AnonymousClass1 x1) {
            this();
        }

        public void run() {
            this.target.getWindow().setAttributes(this.lp);
        }

        public void setParams(Activity act, WindowManager.LayoutParams params) {
            this.target = act;
            this.lp = params;
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

        /* synthetic */ KeepOnTask(BrightnessPlugin x0, AnonymousClass1 x1) {
            this();
        }

        public void run() {
            if (this.state) {
                this.win.addFlags(128);
            } else {
                this.win.clearFlags(128);
            }
        }

        public void setParams(Window win2, boolean state2) {
            this.win = win2;
            this.state = state2;
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        System.out.println("plugin has been started");
        int i = AnonymousClass1.$SwitchMap$org$apache$cordova$plugin$Brightness$BrightnessPlugin$Action[Action.valueOf(action).ordinal()];
        if (i == 1) {
            setBrightness(args, callbackContext);
            return true;
        } else if (i == 2) {
            getBrightness(args, callbackContext);
            return true;
        } else if (i != 3) {
            return false;
        } else {
            setKeepScreenOn(args, callbackContext);
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: org.apache.cordova.plugin.Brightness.BrightnessPlugin$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$cordova$plugin$Brightness$BrightnessPlugin$Action;

        static {
            int[] iArr = new int[Action.values().length];
            $SwitchMap$org$apache$cordova$plugin$Brightness$BrightnessPlugin$Action = iArr;
            try {
                iArr[Action.setBrightness.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$cordova$plugin$Brightness$BrightnessPlugin$Action[Action.getBrightness.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$cordova$plugin$Brightness$BrightnessPlugin$Action[Action.setKeepScreenOn.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private boolean setBrightness(JSONArray args, CallbackContext callbackContext) {
        try {
            Activity activity = this.cordova.getActivity();
            WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
            layoutParams.screenBrightness = (float) Double.parseDouble(args.getString(0));
            SetTask task = new SetTask(this, null);
            task.setParams(activity, layoutParams);
            activity.runOnUiThread(task);
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

    private boolean getBrightness(JSONArray args, CallbackContext callbackContext) {
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

    private boolean setKeepScreenOn(JSONArray args, CallbackContext callbackContext) {
        try {
            boolean value = args.getBoolean(0);
            Activity activity = this.cordova.getActivity();
            KeepOnTask task = new KeepOnTask(this, null);
            task.setParams(activity.getWindow(), value);
            activity.runOnUiThread(task);
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
