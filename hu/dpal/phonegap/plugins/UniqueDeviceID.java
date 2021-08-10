package hu.dpal.phonegap.plugins;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class UniqueDeviceID extends CordovaPlugin {
    public static final int REQUEST_READ_PHONE_STATE = 0;
    public static final String TAG = "UniqueDeviceID";
    protected static final String permission = "android.permission.READ_PHONE_STATE";
    public CallbackContext callbackContext;

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext2) throws JSONException {
        this.callbackContext = callbackContext2;
        try {
            if (!action.equals("get")) {
                this.callbackContext.error("Invalid action");
                return false;
            } else if (hasPermission(permission)) {
                getDeviceId();
                return true;
            } else {
                requestPermission(this, 0, permission);
                return true;
            }
        } catch (Exception e) {
            this.callbackContext.error("Exception occurred: ".concat(e.getMessage()));
            return false;
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (requestCode == 0) {
            getDeviceId();
        }
    }

    /* access modifiers changed from: protected */
    public void getDeviceId() {
        try {
            Context context = this.cordova.getActivity().getApplicationContext();
            TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
            String androidID = Settings.Secure.getString(context.getContentResolver(), "android_id");
            String deviceID = tm.getDeviceId();
            String simID = tm.getSimSerialNumber();
            if ("9774d56d682e549c".equals(androidID) || androidID == null) {
                androidID = "";
            }
            if (deviceID == null) {
                deviceID = "";
            }
            if (simID == null) {
                simID = "";
            }
            this.callbackContext.success(String.format("%32s", androidID + deviceID + simID).replace(' ', '0').substring(0, 32).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        } catch (Exception e) {
            this.callbackContext.error("Exception occurred: ".concat(e.getMessage()));
        }
    }

    private boolean hasPermission(String permission2) throws Exception {
        try {
            return ((Boolean) this.cordova.getClass().getMethod("hasPermission", permission2.getClass()).invoke(this.cordova, permission2)).booleanValue();
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "Cordova v9.1.0 does not support API 23 runtime permissions so defaulting to GRANTED for " + permission2);
            return true;
        }
    }

    private void requestPermission(CordovaPlugin plugin, int requestCode, String permission2) throws Exception {
        try {
            this.cordova.getClass().getMethod("requestPermission", CordovaPlugin.class, Integer.TYPE, String.class).invoke(this.cordova, plugin, Integer.valueOf(requestCode), permission2);
        } catch (NoSuchMethodException e) {
            throw new Exception("requestPermission() method not found in CordovaInterface implementation of Cordova v9.1.0");
        }
    }
}
