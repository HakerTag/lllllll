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
    public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        super.initialize(cordovaInterface, cordovaWebView);
    }

    private String[] getStringArrayFromJSONArray(JSONArray jSONArray) throws JSONException {
        if (jSONArray == null) {
            return new String[0];
        }
        int length = jSONArray.length();
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = jSONArray.getString(i);
        }
        return strArr;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) {
        Log.v("SMSReader", "Called action " + str);
        try {
            ArrayList<SMS> arrayList = new ArrayList<>();
            char c = 65535;
            switch (str.hashCode()) {
                case -1552643814:
                    if (str.equals("filterbody")) {
                        c = 2;
                        break;
                    }
                    break;
                case -517618225:
                    if (str.equals("permission")) {
                        c = 0;
                        break;
                    }
                    break;
                case -383972762:
                    if (str.equals("filtersenders")) {
                        c = 3;
                        break;
                    }
                    break;
                case 96673:
                    if (str.equals("all")) {
                        c = 1;
                        break;
                    }
                    break;
                case 503340481:
                    if (str.equals("filterbodyorsenders")) {
                        c = 4;
                        break;
                    }
                    break;
            }
            if (c != 0) {
                if (c == 1) {
                    arrayList = fetchSMS(jSONArray.getLong(0), new String[0], new String[0]);
                } else if (c == 2) {
                    String[] stringArrayFromJSONArray = getStringArrayFromJSONArray(jSONArray.getJSONArray(1));
                    if (stringArrayFromJSONArray.length > 0) {
                        arrayList = fetchSMS(jSONArray.getLong(0), stringArrayFromJSONArray, new String[0]);
                    }
                } else if (c == 3) {
                    String[] stringArrayFromJSONArray2 = getStringArrayFromJSONArray(jSONArray.getJSONArray(2));
                    if (stringArrayFromJSONArray2.length > 0) {
                        arrayList = fetchSMS(jSONArray.getLong(0), new String[0], stringArrayFromJSONArray2);
                    }
                } else if (c != 4) {
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
                    return false;
                } else {
                    String[] stringArrayFromJSONArray3 = getStringArrayFromJSONArray(jSONArray.getJSONArray(1));
                    String[] stringArrayFromJSONArray4 = getStringArrayFromJSONArray(jSONArray.getJSONArray(2));
                    if (stringArrayFromJSONArray3.length + stringArrayFromJSONArray4.length > 0) {
                        arrayList = fetchSMS(jSONArray.getLong(0), stringArrayFromJSONArray3, stringArrayFromJSONArray4);
                    }
                }
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, convertToJSONArray(arrayList)));
                return true;
            }
            ensurePermissions(getStringArrayFromJSONArray(jSONArray.getJSONArray(0)), callbackContext);
            return true;
        } catch (JSONException e) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION, e.getMessage()));
            return false;
        }
    }

    private String[] resolvePermissions(String[] strArr) {
        String[] strArr2 = new String[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            String str = strArr[i];
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
                strArr2[i] = this.SMSPermissions[0];
            } else if (c == 1) {
                strArr2[i] = this.SMSPermissions[1];
            } else if (c == 2) {
                strArr2[i] = this.SMSPermissions[2];
            }
        }
        return strArr2;
    }

    private void ensurePermissions(String[] strArr, CallbackContext callbackContext) {
        String[] resolvePermissions = resolvePermissions(strArr);
        int length = resolvePermissions.length;
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = true;
                break;
            }
            if (!this.cordova.hasPermission(resolvePermissions[i])) {
                break;
            }
            i++;
        }
        if (z) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "PERMISSION_GRANTED"));
            return;
        }
        this.CallbackContext = callbackContext;
        this.cordova.requestPermissions(this, 1, resolvePermissions);
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) throws JSONException {
        for (int i2 : iArr) {
            if (i2 == -1) {
                this.CallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "PERMISSION_DENIED"));
                return;
            }
        }
        this.CallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "PERMISSION_GRANTED"));
        this.CallbackContext = null;
    }

    private JSONArray convertToJSONArray(ArrayList<SMS> arrayList) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            jSONArray.put(arrayList.get(i).writeJSON());
        }
        return jSONArray;
    }

    private ArrayList<SMS> fetchSMS(long j, String[] strArr, String[] strArr2) {
        ArrayList<SMS> arrayList = new ArrayList<>();
        Cursor query = this.cordova.getActivity().getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int count = query.getCount();
        if (query.moveToFirst()) {
            for (int i = 0; i < count; i++) {
                SMS sms = new SMS(query);
                if (sms.applyFilters(j, strArr, strArr2)) {
                    arrayList.add(sms);
                }
                query.moveToNext();
            }
        }
        query.close();
        return arrayList;
    }
}
