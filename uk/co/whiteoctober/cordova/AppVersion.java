package uk.co.whiteoctober.cordova;

import android.content.pm.PackageManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class AppVersion extends CordovaPlugin {
    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        try {
            if (str.equals("getAppName")) {
                PackageManager packageManager = this.cordova.getActivity().getPackageManager();
                callbackContext.success((String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.cordova.getActivity().getPackageName(), 0)));
                return true;
            } else if (str.equals("getPackageName")) {
                callbackContext.success(this.cordova.getActivity().getPackageName());
                return true;
            } else if (str.equals("getVersionNumber")) {
                callbackContext.success(this.cordova.getActivity().getPackageManager().getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionName);
                return true;
            } else if (!str.equals("getVersionCode")) {
                return false;
            } else {
                callbackContext.success(this.cordova.getActivity().getPackageManager().getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionCode);
                return true;
            }
        } catch (PackageManager.NameNotFoundException unused) {
            callbackContext.success("N/A");
            return true;
        }
    }
}
