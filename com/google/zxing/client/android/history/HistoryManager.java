package com.google.zxing.client.android.history;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.android.result.ResultHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class HistoryManager {
    private static final String[] COLUMNS = {"text", "display", "format", "timestamp", "details"};
    private static final String[] COUNT_COLUMN = {"COUNT(1)"};
    private static final String[] ID_COL_PROJECTION = {"id"};
    private static final String[] ID_DETAIL_COL_PROJECTION = {"id", "details"};
    private static final int MAX_ITEMS = 2000;
    private static final String TAG = HistoryManager.class.getSimpleName();
    private final Activity activity;
    private final boolean enableHistory;

    public HistoryManager(Activity activity2) {
        this.activity = activity2;
        this.enableHistory = PreferenceManager.getDefaultSharedPreferences(activity2).getBoolean(PreferencesActivity.KEY_ENABLE_HISTORY, true);
    }

    public boolean hasHistoryItems() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new DBHelper(this.activity).getReadableDatabase();
            cursor = db.query("history", COUNT_COLUMN, null, null, null, null, null);
            cursor.moveToFirst();
            boolean z = false;
            if (cursor.getInt(0) > 0) {
                z = true;
            }
            return z;
        } finally {
            close(cursor, db);
        }
    }

    public List<HistoryItem> buildHistoryItems() {
        SQLiteOpenHelper helper = new DBHelper(this.activity);
        List<HistoryItem> items = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query("history", COLUMNS, null, null, null, null, "timestamp DESC");
            while (cursor.moveToNext()) {
                String text = cursor.getString(0);
                String display = cursor.getString(1);
                String format = cursor.getString(2);
                long timestamp = cursor.getLong(3);
                items.add(new HistoryItem(new Result(text, null, null, BarcodeFormat.valueOf(format), timestamp), display, cursor.getString(4)));
            }
            return items;
        } finally {
            close(cursor, db);
        }
    }

    public HistoryItem buildHistoryItem(int number) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new DBHelper(this.activity).getReadableDatabase();
            cursor = db.query("history", COLUMNS, null, null, null, null, "timestamp DESC");
            cursor.move(number + 1);
            String text = cursor.getString(0);
            String display = cursor.getString(1);
            String format = cursor.getString(2);
            long timestamp = cursor.getLong(3);
            return new HistoryItem(new Result(text, null, null, BarcodeFormat.valueOf(format), timestamp), display, cursor.getString(4));
        } finally {
            close(cursor, db);
        }
    }

    public void deleteHistoryItem(int number) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new DBHelper(this.activity).getWritableDatabase();
            cursor = db.query("history", ID_COL_PROJECTION, null, null, null, null, "timestamp DESC");
            cursor.move(number + 1);
            db.delete("history", "id=" + cursor.getString(0), null);
        } finally {
            close(cursor, db);
        }
    }

    public void addHistoryItem(Result result, ResultHandler handler) {
        if (this.activity.getIntent().getBooleanExtra(Intents.Scan.SAVE_HISTORY, true) && !handler.areContentsSecure() && this.enableHistory) {
            if (!PreferenceManager.getDefaultSharedPreferences(this.activity).getBoolean(PreferencesActivity.KEY_REMEMBER_DUPLICATES, false)) {
                deletePrevious(result.getText());
            }
            ContentValues values = new ContentValues();
            values.put("text", result.getText());
            values.put("format", result.getBarcodeFormat().toString());
            values.put("display", handler.getDisplayContents().toString());
            values.put("timestamp", Long.valueOf(System.currentTimeMillis()));
            SQLiteDatabase db = null;
            try {
                db = new DBHelper(this.activity).getWritableDatabase();
                db.insert("history", "timestamp", values);
            } finally {
                close(null, db);
            }
        }
    }

    public void addHistoryItemDetails(String itemID, String itemDetails) {
        String newDetails;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new DBHelper(this.activity).getWritableDatabase();
            cursor = db.query("history", ID_DETAIL_COL_PROJECTION, "text=?", new String[]{itemID}, null, null, "timestamp DESC", "1");
            String oldID = null;
            String oldDetails = null;
            if (cursor.moveToNext()) {
                oldID = cursor.getString(0);
                oldDetails = cursor.getString(1);
            }
            if (oldID != null) {
                if (oldDetails == null) {
                    newDetails = itemDetails;
                } else if (oldDetails.contains(itemDetails)) {
                    newDetails = null;
                } else {
                    newDetails = oldDetails + " : " + itemDetails;
                }
                if (newDetails != null) {
                    ContentValues values = new ContentValues();
                    values.put("details", newDetails);
                    db.update("history", values, "id=?", new String[]{oldID});
                }
            }
        } finally {
            close(cursor, db);
        }
    }

    private void deletePrevious(String text) {
        SQLiteDatabase db = null;
        try {
            db = new DBHelper(this.activity).getWritableDatabase();
            db.delete("history", "text=?", new String[]{text});
        } finally {
            close(null, db);
        }
    }

    public void trimHistory() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new DBHelper(this.activity).getWritableDatabase();
            cursor = db.query("history", ID_COL_PROJECTION, null, null, null, null, "timestamp DESC");
            cursor.move(MAX_ITEMS);
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String str = TAG;
                Log.i(str, "Deleting scan history ID " + id);
                db.delete("history", "id=" + id, null);
            }
        } catch (SQLiteException sqle) {
            Log.w(TAG, sqle);
        } catch (Throwable th) {
            close(null, null);
            throw th;
        }
        close(cursor, db);
    }

    /* access modifiers changed from: package-private */
    public CharSequence buildHistory() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new DBHelper(this.activity).getWritableDatabase();
            cursor = db.query("history", COLUMNS, null, null, null, null, "timestamp DESC");
            DateFormat format = DateFormat.getDateTimeInstance(2, 2);
            StringBuilder historyText = new StringBuilder(1000);
            while (cursor.moveToNext()) {
                historyText.append('\"');
                historyText.append(massageHistoryField(cursor.getString(0)));
                historyText.append("\",");
                historyText.append('\"');
                historyText.append(massageHistoryField(cursor.getString(1)));
                historyText.append("\",");
                historyText.append('\"');
                historyText.append(massageHistoryField(cursor.getString(2)));
                historyText.append("\",");
                historyText.append('\"');
                historyText.append(massageHistoryField(cursor.getString(3)));
                historyText.append("\",");
                long timestamp = cursor.getLong(3);
                historyText.append('\"');
                historyText.append(massageHistoryField(format.format(new Date(timestamp))));
                historyText.append("\",");
                historyText.append('\"');
                historyText.append(massageHistoryField(cursor.getString(4)));
                historyText.append("\"\r\n");
            }
            return historyText;
        } finally {
            close(cursor, db);
        }
    }

    /* access modifiers changed from: package-private */
    public void clearHistory() {
        SQLiteDatabase db = null;
        try {
            db = new DBHelper(this.activity).getWritableDatabase();
            db.delete("history", null, null);
        } finally {
            close(null, db);
        }
    }

    static Uri saveHistory(String history) {
        File historyRoot = new File(new File(Environment.getExternalStorageDirectory(), "BarcodeScanner"), "History");
        if (historyRoot.exists() || historyRoot.mkdirs()) {
            File historyFile = new File(historyRoot, "history-" + System.currentTimeMillis() + ".csv");
            OutputStreamWriter out = null;
            try {
                out = new OutputStreamWriter(new FileOutputStream(historyFile), Charset.forName("UTF-8"));
                out.write(history);
                Uri parse = Uri.parse("file://" + historyFile.getAbsolutePath());
                try {
                    out.close();
                } catch (IOException e) {
                }
                return parse;
            } catch (IOException ioe) {
                String str = TAG;
                Log.w(str, "Couldn't access file " + historyFile + " due to " + ioe);
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e2) {
                    }
                }
                return null;
            } catch (Throwable th) {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e3) {
                    }
                }
                throw th;
            }
        } else {
            String str2 = TAG;
            Log.w(str2, "Couldn't make dir " + historyRoot);
            return null;
        }
    }

    private static String massageHistoryField(String value) {
        return value == null ? "" : value.replace("\"", "\"\"");
    }

    private static void close(Cursor cursor, SQLiteDatabase database) {
        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            database.close();
        }
    }
}
