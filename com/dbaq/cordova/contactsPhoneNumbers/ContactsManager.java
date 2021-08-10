package com.dbaq.cordova.contactsPhoneNumbers;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactsManager extends CordovaPlugin {
    public static final String ACTION_LIST_CONTACTS = "list";
    private static final String LOG_TAG = "Contact Phone Numbers";
    private static final int READ_CONTACTS_REQ_CODE = 0;
    private CallbackContext callbackContext;
    private JSONArray executeArgs;

    private String getPhoneTypeLabel(int i) {
        return i == 1 ? "HOME" : i == 2 ? "MOBILE" : i == 3 ? "WORK" : "OTHER";
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext2) throws JSONException {
        this.callbackContext = callbackContext2;
        this.executeArgs = jSONArray;
        if (!ACTION_LIST_CONTACTS.equals(str)) {
            return false;
        }
        if (this.cordova.hasPermission("android.permission.READ_CONTACTS")) {
            execHelper();
            return true;
        }
        this.cordova.requestPermission(this, 0, "android.permission.READ_CONTACTS");
        return true;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) throws JSONException {
        for (int i2 : iArr) {
            if (i2 == -1) {
                Log.d(LOG_TAG, "Permission denied");
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "User has denied permission"));
                return;
            }
        }
        execHelper();
    }

    private void execHelper() {
        this.cordova.getThreadPool().execute(new Runnable() {
            /* class com.dbaq.cordova.contactsPhoneNumbers.ContactsManager.AnonymousClass1 */

            public void run() {
                ContactsManager.this.callbackContext.success(ContactsManager.this.list());
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private JSONArray list() {
        new JSONArray();
        return populateContactArray(this.cordova.getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{"display_name", "photo_thumb_uri", "data3", "data5", "data2", "has_phone_number", "data1", "data4", "data2", "contact_id", "mimetype"}, "has_phone_number = 1", null, "contact_id ASC"));
    }

    private JSONArray populateContactArray(Cursor cursor) {
        JSONArray jSONArray = new JSONArray();
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray2 = new JSONArray();
        try {
            if (cursor.getCount() > 0) {
                String str = null;
                boolean z = true;
                while (cursor.moveToNext()) {
                    String string = cursor.getString(cursor.getColumnIndex("contact_id"));
                    if (cursor.getPosition() == 0) {
                        str = string;
                    }
                    if (!str.equals(string)) {
                        jSONObject.put("phoneNumbers", jSONArray2);
                        jSONArray.put(jSONObject);
                        jSONObject = new JSONObject();
                        jSONArray2 = new JSONArray();
                        z = true;
                    }
                    if (z) {
                        jSONObject.put("id", string);
                        z = false;
                    }
                    String string2 = cursor.getString(cursor.getColumnIndex("mimetype"));
                    if (string2.equals("vnd.android.cursor.item/name")) {
                        jSONObject.put("firstName", cursor.getString(cursor.getColumnIndex("data2")));
                        jSONObject.put("lastName", cursor.getString(cursor.getColumnIndex("data3")));
                        jSONObject.put("middleName", cursor.getString(cursor.getColumnIndex("data5")));
                        jSONObject.put("displayName", cursor.getString(cursor.getColumnIndex("display_name")));
                        jSONObject.put("thumbnail", cursor.getString(cursor.getColumnIndex("photo_thumb_uri")));
                    } else if (string2.equals("vnd.android.cursor.item/phone_v2")) {
                        jSONArray2.put(getPhoneNumber(cursor));
                    }
                    str = string;
                }
                jSONObject.put("phoneNumbers", jSONArray2);
                jSONArray.put(jSONObject);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        cursor.close();
        return jSONArray;
    }

    private JSONObject getPhoneNumber(Cursor cursor) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        String string = cursor.getString(cursor.getColumnIndex("data1"));
        String string2 = cursor.getString(cursor.getColumnIndex("data4"));
        jSONObject.put("number", string);
        if (string2 != null) {
            string = string2;
        }
        jSONObject.put("normalizedNumber", string);
        jSONObject.put("type", getPhoneTypeLabel(cursor.getInt(cursor.getColumnIndex("data2"))));
        return jSONObject;
    }
}
