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

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext2) throws JSONException {
        this.callbackContext = callbackContext2;
        this.executeArgs = args;
        if (!ACTION_LIST_CONTACTS.equals(action)) {
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
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == -1) {
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

    private JSONArray populateContactArray(Cursor c) {
        JSONArray contacts = new JSONArray();
        String oldContactId = null;
        boolean newContact = true;
        JSONObject contact = new JSONObject();
        JSONArray phones = new JSONArray();
        try {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    String contactId = c.getString(c.getColumnIndex("contact_id"));
                    if (c.getPosition() == 0) {
                        oldContactId = contactId;
                    }
                    if (!oldContactId.equals(contactId)) {
                        contact.put("phoneNumbers", phones);
                        contacts.put(contact);
                        contact = new JSONObject();
                        phones = new JSONArray();
                        newContact = true;
                    }
                    if (newContact) {
                        newContact = false;
                        contact.put("id", contactId);
                    }
                    String mimetype = c.getString(c.getColumnIndex("mimetype"));
                    if (mimetype.equals("vnd.android.cursor.item/name")) {
                        contact.put("firstName", c.getString(c.getColumnIndex("data2")));
                        contact.put("lastName", c.getString(c.getColumnIndex("data3")));
                        contact.put("middleName", c.getString(c.getColumnIndex("data5")));
                        contact.put("displayName", c.getString(c.getColumnIndex("display_name")));
                        contact.put("thumbnail", c.getString(c.getColumnIndex("photo_thumb_uri")));
                    } else if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                        phones.put(getPhoneNumber(c));
                    }
                    oldContactId = contactId;
                }
                contact.put("phoneNumbers", phones);
                contacts.put(contact);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        c.close();
        return contacts;
    }

    private JSONObject getPhoneNumber(Cursor cursor) throws JSONException {
        JSONObject phoneNumber = new JSONObject();
        String number = cursor.getString(cursor.getColumnIndex("data1"));
        String normalizedNumber = cursor.getString(cursor.getColumnIndex("data4"));
        phoneNumber.put("number", number);
        phoneNumber.put("normalizedNumber", normalizedNumber == null ? number : normalizedNumber);
        phoneNumber.put("type", getPhoneTypeLabel(cursor.getInt(cursor.getColumnIndex("data2"))));
        return phoneNumber;
    }

    private String getPhoneTypeLabel(int type) {
        if (type == 1) {
            return "HOME";
        }
        if (type == 2) {
            return "MOBILE";
        }
        if (type == 3) {
            return "WORK";
        }
        return "OTHER";
    }
}
