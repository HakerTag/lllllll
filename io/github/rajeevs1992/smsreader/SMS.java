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

    private boolean applySenderFilter(String[] strArr) {
        for (String str : strArr) {
            if (this.address.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean applyFilters(long j, String[] strArr, String[] strArr2) {
        if (this.date <= j) {
            return false;
        }
        if (strArr2.length + strArr.length != 0 && !applyBodySearchFilters(strArr) && !applySenderFilter(strArr2)) {
            return false;
        }
        return true;
    }

    private boolean applyBodySearchFilters(String[] strArr) {
        for (String str : strArr) {
            if (this.body.toLowerCase().contains(str.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public JSONObject writeJSON() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("id", this.id);
        jSONObject.put("address", this.address);
        jSONObject.put("body", this.body);
        jSONObject.put("read", this.read);
        jSONObject.put("date", this.date);
        return jSONObject;
    }
}
