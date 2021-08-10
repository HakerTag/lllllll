package io.github.rajeevs1992.smsreader;

import android.database.Cursor;
import org.json.JSONException;
import org.json.JSONObject;

public class SMS {
    public String address;
    public String body;
    public long date;
    public int id;
    public Boolean read;

    public SMS(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        this.address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
        this.body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
        this.read = Boolean.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("read")) != 1 ? false : true);
        this.date = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
    }

    private boolean applySenderFilter(String[] senderids) {
        for (String str : senderids) {
            if (this.address.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean applyFilters(long sinceDate, String[] searchKeys, String[] senderids) {
        if (this.date <= sinceDate) {
            return false;
        }
        if (senderids.length + searchKeys.length != 0 && !applyBodySearchFilters(searchKeys) && !applySenderFilter(senderids)) {
            return false;
        }
        return true;
    }

    private boolean applyBodySearchFilters(String[] searchKeys) {
        for (String str : searchKeys) {
            if (this.body.toLowerCase().contains(str.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public JSONObject writeJSON() throws JSONException {
        JSONObject sms = new JSONObject();
        sms.put("id", this.id);
        sms.put("address", this.address);
        sms.put("body", this.body);
        sms.put("read", this.read);
        sms.put("date", this.date);
        return sms;
    }
}
