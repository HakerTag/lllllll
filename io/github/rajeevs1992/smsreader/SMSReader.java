package io.github.rajeevs1992.smsreader;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.util.ArrayList;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class SMSReader extends CordovaPlugin {
    private CallbackContext CallbackContext;
    private String[] SMSPermissions = {"android.permission.READ_SMS", "android.permission.RECEIVE_SMS", "android.permission.SEND_SMS"};

    @Override // org.apache.cordova.CordovaPlugin
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    private String[] getStringArrayFromJSONArray(JSONArray array) throws JSONException {
        if (array == null) {
            return new String[0];
        }
        String[] stringArray = new String[array.length()];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = array.getString(i);
        }
        return stringArray;
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        Log.v("SMSReader", "Called action " + action);
        try {
            ArrayList<SMS> sms = new ArrayList<>();
            char c = 65535;
            switch (action.hashCode()) {
                case -1552643814:
                    if (action.equals("filterbody")) {
                        c = 2;
                        break;
                    }
                    break;
                case -517618225:
                    if (action.equals("permission")) {
                        c = 0;
                        break;
                    }
                    break;
                case -383972762:
                    if (action.equals("filtersenders")) {
                        c = 3;
                        break;
                    }
                    break;
                case 96673:
                    if (action.equals("all")) {
                        c = 1;
                        break;
                    }
                    break;
                case 503340481:
                    if (action.equals("filterbodyorsenders")) {
                        c = 4;
                        break;
                    }
                    break;
            }
            if (c != 0) {
                if (c == 1) {
                    sms = fetchSMS(data.getLong(0), new String[0], new String[0]);
                } else if (c == 2) {
                    String[] searchstrings = getStringArrayFromJSONArray(data.getJSONArray(1));
                    if (searchstrings.length > 0) {
                        sms = fetchSMS(data.getLong(0), searchstrings, new String[0]);
                    }
                } else if (c == 3) {
                    String[] senderids = getStringArrayFromJSONArray(data.getJSONArray(2));
                    if (senderids.length > 0) {
                        sms = fetchSMS(data.getLong(0), new String[0], senderids);
                    }
                } else if (c != 4) {
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
                    return false;
                } else {
                    String[] searchstrings2 = getStringArrayFromJSONArray(data.getJSONArray(1));
                    String[] senderids2 = getStringArrayFromJSONArray(data.getJSONArray(2));
                    if (searchstrings2.length + senderids2.length > 0) {
                        sms = fetchSMS(data.getLong(0), searchstrings2, senderids2);
                    }
                }
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, convertToJSONArray(sms)));
                return true;
            }
            ensurePermissions(getStringArrayFromJSONArray(data.getJSONArray(0)), callbackContext);
            return true;
        } catch (JSONException e) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION, e.getMessage()));
            return false;
        }
    }

    private String[] resolvePermissions(String[] permissionCodes) {
        String[] permissions = new String[permissionCodes.length];
        for (int i = 0; i < permissionCodes.length; i++) {
            String str = permissionCodes[i];
            char c = 65535;
            int hashCode = str.hashCode();
            if (hashCode != 3496342) {
                if (hashCode != 3526536) {
                    if (hashCode == 1082290915 && str.equals("receive")) {
                        c = 1;
                    }
                } else if (str.equals("send")) {
                    c = 2;
                }
            } else if (str.equals("read")) {
                c = 0;
            }
            if (c == 0) {
                permissions[i] = this.SMSPermissions[0];
            } else if (c == 1) {
                permissions[i] = this.SMSPermissions[1];
            } else if (c == 2) {
                permissions[i] = this.SMSPermissions[2];
            }
        }
        return permissions;
    }

    private void ensurePermissions(String[] permissionCodes, CallbackContext callbackContext) {
        boolean hasPermission = true;
        String[] permissions = resolvePermissions(permissionCodes);
        int length = permissions.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            if (!this.cordova.hasPermission(permissions[i])) {
                hasPermission = false;
                break;
            }
            i++;
        }
        if (hasPermission) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "PERMISSION_GRANTED"));
            return;
        }
        this.CallbackContext = callbackContext;
        this.cordova.requestPermissions(this, 1, permissions);
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == -1) {
                this.CallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "PERMISSION_DENIED"));
                return;
            }
        }
        this.CallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "PERMISSION_GRANTED"));
        this.CallbackContext = null;
    }

    private JSONArray convertToJSONArray(ArrayList<SMS> sms) throws JSONException {
        JSONArray smsResult = new JSONArray();
        int resultLength = sms.size();
        for (int i = 0; i < resultLength; i++) {
            smsResult.put(sms.get(i).writeJSON());
        }
        return smsResult;
    }

    private ArrayList<SMS> fetchSMS(long since, String[] searchText, String[] senderids) {
        ArrayList<SMS> lstSms = new ArrayList<>();
        Cursor cursor = this.cordova.getActivity().getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int totalSMS = cursor.getCount();
        if (cursor.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                SMS sms = new SMS(cursor);
                if (sms.applyFilters(since, searchText, senderids)) {
                    lstSms.add(sms);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return lstSms;
    }
}
