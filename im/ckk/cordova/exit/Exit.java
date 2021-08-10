package im.ckk.cordova.exit;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class Exit extends CordovaPlugin {
    /* access modifiers changed from: protected */
    @Override // org.apache.cordova.CordovaPlugin
    public void pluginInitialize() {
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        if (!str.equals("exit")) {
            return false;
        }
        try {
            this.cordova.getActivity().finish();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, 0));
        } catch (Exception unused) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, 1));
        }
        return true;
    }
}
