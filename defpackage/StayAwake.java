package defpackage;

import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

/* renamed from: StayAwake  reason: default package */
public class StayAwake extends CordovaPlugin {
    public static final int FLAG_KEEP_SCREEN_ON = 128;
    public static final String TAG = "cordova-android-styawake";

    @Override // org.apache.cordova.CordovaPlugin
    public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        super.initialize(cordovaInterface, cordovaWebView);
        Log.i(TAG, "Init cordova-android-styawake plugin");
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String str, JSONArray jSONArray, final CallbackContext callbackContext) throws JSONException {
        if ("REACTIVATE_TIMEOUT".equals(str)) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class defpackage.StayAwake.AnonymousClass1 */

                public void run() {
                    StayAwake.this.reactivateTimeOut();
                    callbackContext.success();
                }
            });
            return true;
        } else if (!"DISABLE_TIMEOUT".equals(str)) {
            return false;
        } else {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class defpackage.StayAwake.AnonymousClass2 */

                public void run() {
                    StayAwake.this.disableTimeOut();
                    callbackContext.success();
                }
            });
            return true;
        }
    }

    public void disableTimeOut() {
        Log.i(TAG, "cordova-android-styawake --> disableTimeOut");
        this.cordova.getActivity().getWindow().addFlags(128);
    }

    public void reactivateTimeOut() {
        Log.i(TAG, "cordova-android-styawake --> reactivateTimeOut");
        this.cordova.getActivity().getWindow().clearFlags(128);
    }
}
