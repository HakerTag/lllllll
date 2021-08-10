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
    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext2) throws JSONException {
        this.callbackContext = callbackContext2;
        try {
            if (!str.equals("get")) {
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
    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) throws JSONException {
        if (i == 0) {
            getDeviceId();
        }
    }

    /* access modifiers changed from: protected */
    public void getDeviceId() {
        try {
            Context applicationContext = this.cordova.getActivity().getApplicationContext();
            TelephonyManager telephonyManager = (TelephonyManager) applicationContext.getSystemService("phone");
            String string = Settings.Secure.getString(applicationContext.getContentResolver(), "android_id");
            String deviceId = telephonyManager.getDeviceId();
            String simSerialNumber = telephonyManager.getSimSerialNumber();
            if ("9774d56d682e549c".equals(string) || string == null) {
                string = "";
            }
            if (deviceId == null) {
                deviceId = "";
            }
            if (simSerialNumber == null) {
                simSerialNumber = "";
            }
            this.callbackContext.success(String.format("%32s", string + deviceId + simSerialNumber).replace(' ', '0').substring(0, 32).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        } catch (Exception e) {
            this.callbackContext.error("Exception occurred: ".concat(e.getMessage()));
        }
    }

    private boolean hasPermission(String str) throws Exception {
        try {
            return ((Boolean) this.cordova.getClass().getMethod("hasPermission", str.getClass()).invoke(this.cordova, str)).booleanValue();
        } catch (NoSuchMethodException unused) {
            Log.w(TAG, "Cordova v9.1.0 does not support API 23 runtime permissions so defaulting to GRANTED for " + str);
            return true;
        }
    }

    private void requestPermission(CordovaPlugin cordovaPlugin, int i, String str) throws Exception {
        try {
            this.cordova.getClass().getMethod("requestPermission", CordovaPlugin.class, Integer.TYPE, String.class).invoke(this.cordova, cordovaPlugin, Integer.valueOf(i), str);
        } catch (NoSuchMethodException unused) {
            throw new Exception("requestPermission() method not found in CordovaInterface implementation of Cordova v9.1.0");
        }
    }
}
