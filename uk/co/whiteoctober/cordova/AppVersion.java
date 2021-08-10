package uk.co.whiteoctober.cordova;

import android.content.pm.PackageManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class AppVersion extends CordovaPlugin {
    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if (action.equals("getAppName")) {
                PackageManager packageManager = this.cordova.getActivity().getPackageManager();
                callbackContext.success((String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.cordova.getActivity().getPackageName(), 0)));
                return true;
            } else if (action.equals("getPackageName")) {
                callbackContext.success(this.cordova.getActivity().getPackageName());
                return true;
            } else if (action.equals("getVersionNumber")) {
                callbackContext.success(this.cordova.getActivity().getPackageManager().getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionName);
                return true;
            } else if (!action.equals("getVersionCode")) {
                return false;
            } else {
                callbackContext.success(this.cordova.getActivity().getPackageManager().getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionCode);
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            callbackContext.success("N/A");
            return true;
        }
    }
}
