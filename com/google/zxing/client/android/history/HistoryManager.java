package com.google.zxing.client.android.history;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.android.result.ResultHandler;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import kotlin.text.Typography;

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
        SQLiteDatabase sQLiteDatabase;
        Cursor cursor = null;
        try {
            sQLiteDatabase = new DBHelper(this.activity).getReadableDatabase();
            try {
                cursor = sQLiteDatabase.query("history", COUNT_COLUMN, null, null, null, null, null);
                cursor.moveToFirst();
                boolean z = false;
                if (cursor.getInt(0) > 0) {
                    z = true;
                }
                close(cursor, sQLiteDatabase);
                return z;
            } catch (Throwable th) {
                th = th;
                close(cursor, sQLiteDatabase);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteDatabase = null;
            close(cursor, sQLiteDatabase);
            throw th;
        }
    }

    public List<HistoryItem> buildHistoryItems() {
        SQLiteDatabase sQLiteDatabase;
        DBHelper dBHelper = new DBHelper(this.activity);
        ArrayList arrayList = new ArrayList();
        try {
            sQLiteDatabase = dBHelper.getReadableDatabase();
            try {
                Cursor query = sQLiteDatabase.query("history", COLUMNS, null, null, null, null, "timestamp DESC");
                while (query.moveToNext()) {
                    String string = query.getString(0);
                    String string2 = query.getString(1);
                    String string3 = query.getString(2);
                    arrayList.add(new HistoryItem(new Result(string, null, null, BarcodeFormat.valueOf(string3), query.getLong(3)), string2, query.getString(4)));
                }
                close(query, sQLiteDatabase);
                return arrayList;
            } catch (Throwable th) {
                th = th;
                close(null, sQLiteDatabase);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteDatabase = null;
            close(null, sQLiteDatabase);
            throw th;
        }
    }

    public HistoryItem buildHistoryItem(int i) {
        SQLiteDatabase sQLiteDatabase;
        Cursor cursor = null;
        try {
            sQLiteDatabase = new DBHelper(this.activity).getReadableDatabase();
            try {
                cursor = sQLiteDatabase.query("history", COLUMNS, null, null, null, null, "timestamp DESC");
                cursor.move(i + 1);
                String string = cursor.getString(0);
                String string2 = cursor.getString(1);
                String string3 = cursor.getString(2);
                HistoryItem historyItem = new HistoryItem(new Result(string, null, null, BarcodeFormat.valueOf(string3), cursor.getLong(3)), string2, cursor.getString(4));
                close(cursor, sQLiteDatabase);
                return historyItem;
            } catch (Throwable th) {
                th = th;
                close(cursor, sQLiteDatabase);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteDatabase = null;
            close(cursor, sQLiteDatabase);
            throw th;
        }
    }

    public void deleteHistoryItem(int i) {
        SQLiteDatabase sQLiteDatabase;
        Cursor query;
        Cursor cursor = null;
        try {
            sQLiteDatabase = new DBHelper(this.activity).getWritableDatabase();
            try {
                query = sQLiteDatabase.query("history", ID_COL_PROJECTION, null, null, null, null, "timestamp DESC");
            } catch (Throwable th) {
                th = th;
                close(cursor, sQLiteDatabase);
                throw th;
            }
            try {
                query.move(i + 1);
                sQLiteDatabase.delete("history", "id=" + query.getString(0), null);
                close(query, sQLiteDatabase);
            } catch (Throwable th2) {
                th = th2;
                cursor = query;
                close(cursor, sQLiteDatabase);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            sQLiteDatabase = null;
            close(cursor, sQLiteDatabase);
            throw th;
        }
    }

    public void addHistoryItem(Result result, ResultHandler resultHandler) {
        SQLiteDatabase sQLiteDatabase;
        if (this.activity.getIntent().getBooleanExtra(Intents.Scan.SAVE_HISTORY, true) && !resultHandler.areContentsSecure() && this.enableHistory) {
            if (!PreferenceManager.getDefaultSharedPreferences(this.activity).getBoolean(PreferencesActivity.KEY_REMEMBER_DUPLICATES, false)) {
                deletePrevious(result.getText());
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("text", result.getText());
            contentValues.put("format", result.getBarcodeFormat().toString());
            contentValues.put("display", resultHandler.getDisplayContents().toString());
            contentValues.put("timestamp", Long.valueOf(System.currentTimeMillis()));
            try {
                sQLiteDatabase = new DBHelper(this.activity).getWritableDatabase();
                try {
                    sQLiteDatabase.insert("history", "timestamp", contentValues);
                    close(null, sQLiteDatabase);
                } catch (Throwable th) {
                    th = th;
                    close(null, sQLiteDatabase);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                sQLiteDatabase = null;
                close(null, sQLiteDatabase);
                throw th;
            }
        }
    }

    public void addHistoryItemDetails(String str, String str2) {
        SQLiteDatabase sQLiteDatabase;
        String str3;
        String str4;
        Cursor cursor = null;
        try {
            sQLiteDatabase = new DBHelper(this.activity).getWritableDatabase();
            try {
                Cursor query = sQLiteDatabase.query("history", ID_DETAIL_COL_PROJECTION, "text=?", new String[]{str}, null, null, "timestamp DESC", "1");
                try {
                    if (query.moveToNext()) {
                        str4 = query.getString(0);
                        str3 = query.getString(1);
                    } else {
                        str4 = null;
                        str3 = null;
                    }
                    if (str4 != null) {
                        if (str3 != null) {
                            if (str3.contains(str2)) {
                                str2 = null;
                            } else {
                                str2 = str3 + " : " + str2;
                            }
                        }
                        if (str2 != null) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("details", str2);
                            sQLiteDatabase.update("history", contentValues, "id=?", new String[]{str4});
                        }
                    }
                    close(query, sQLiteDatabase);
                } catch (Throwable th) {
                    th = th;
                    cursor = query;
                    close(cursor, sQLiteDatabase);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                close(cursor, sQLiteDatabase);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            sQLiteDatabase = null;
            close(cursor, sQLiteDatabase);
            throw th;
        }
    }

    private void deletePrevious(String str) {
        SQLiteDatabase sQLiteDatabase;
        try {
            sQLiteDatabase = new DBHelper(this.activity).getWritableDatabase();
            try {
                sQLiteDatabase.delete("history", "text=?", new String[]{str});
                close(null, sQLiteDatabase);
            } catch (Throwable th) {
                th = th;
                close(null, sQLiteDatabase);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteDatabase = null;
            close(null, sQLiteDatabase);
            throw th;
        }
    }

    public void trimHistory() {
        Cursor cursor;
        SQLiteDatabase sQLiteDatabase;
        SQLiteException e;
        try {
            sQLiteDatabase = new DBHelper(this.activity).getWritableDatabase();
            try {
                cursor = sQLiteDatabase.query("history", ID_COL_PROJECTION, null, null, null, null, "timestamp DESC");
            } catch (SQLiteException e2) {
                cursor = null;
                e = e2;
                try {
                    Log.w(TAG, e);
                    close(cursor, sQLiteDatabase);
                } catch (Throwable th) {
                    th = th;
                    close(cursor, sQLiteDatabase);
                    throw th;
                }
            } catch (Throwable th2) {
                cursor = null;
                th = th2;
                close(cursor, sQLiteDatabase);
                throw th;
            }
            try {
                cursor.move(MAX_ITEMS);
                while (cursor.moveToNext()) {
                    String string = cursor.getString(0);
                    Log.i(TAG, "Deleting scan history ID " + string);
                    sQLiteDatabase.delete("history", "id=" + string, null);
                }
            } catch (SQLiteException e3) {
                e = e3;
                Log.w(TAG, e);
                close(cursor, sQLiteDatabase);
            }
        } catch (SQLiteException e4) {
            cursor = null;
            e = e4;
            sQLiteDatabase = null;
            Log.w(TAG, e);
            close(cursor, sQLiteDatabase);
        } catch (Throwable th3) {
            cursor = null;
            th = th3;
            sQLiteDatabase = null;
            close(cursor, sQLiteDatabase);
            throw th;
        }
        close(cursor, sQLiteDatabase);
    }

    /* access modifiers changed from: package-private */
    public CharSequence buildHistory() {
        SQLiteDatabase sQLiteDatabase;
        try {
            sQLiteDatabase = new DBHelper(this.activity).getWritableDatabase();
            try {
                Cursor query = sQLiteDatabase.query("history", COLUMNS, null, null, null, null, "timestamp DESC");
                DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(2, 2);
                StringBuilder sb = new StringBuilder(1000);
                while (query.moveToNext()) {
                    sb.append(Typography.quote);
                    sb.append(massageHistoryField(query.getString(0)));
                    sb.append("\",");
                    sb.append(Typography.quote);
                    sb.append(massageHistoryField(query.getString(1)));
                    sb.append("\",");
                    sb.append(Typography.quote);
                    sb.append(massageHistoryField(query.getString(2)));
                    sb.append("\",");
                    sb.append(Typography.quote);
                    sb.append(massageHistoryField(query.getString(3)));
                    sb.append("\",");
                    long j = query.getLong(3);
                    sb.append(Typography.quote);
                    sb.append(massageHistoryField(dateTimeInstance.format(new Date(j))));
                    sb.append("\",");
                    sb.append(Typography.quote);
                    sb.append(massageHistoryField(query.getString(4)));
                    sb.append("\"\r\n");
                }
                close(query, sQLiteDatabase);
                return sb;
            } catch (Throwable th) {
                th = th;
                close(null, sQLiteDatabase);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteDatabase = null;
            close(null, sQLiteDatabase);
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public void clearHistory() {
        SQLiteDatabase sQLiteDatabase;
        try {
            sQLiteDatabase = new DBHelper(this.activity).getWritableDatabase();
            try {
                sQLiteDatabase.delete("history", null, null);
                close(null, sQLiteDatabase);
            } catch (Throwable th) {
                th = th;
                close(null, sQLiteDatabase);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteDatabase = null;
            close(null, sQLiteDatabase);
            throw th;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x00ab A[SYNTHETIC, Splitter:B:22:0x00ab] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b3 A[SYNTHETIC, Splitter:B:29:0x00b3] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.net.Uri saveHistory(java.lang.String r6) {
        /*
            java.io.File r0 = new java.io.File
            java.io.File r1 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r2 = "BarcodeScanner"
            r0.<init>(r1, r2)
            java.io.File r1 = new java.io.File
            java.lang.String r2 = "History"
            r1.<init>(r0, r2)
            boolean r0 = r1.exists()
            r2 = 0
            if (r0 != 0) goto L_0x0036
            boolean r0 = r1.mkdirs()
            if (r0 != 0) goto L_0x0036
            java.lang.String r6 = com.google.zxing.client.android.history.HistoryManager.TAG
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Couldn't make dir "
            r0.append(r3)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Log.w(r6, r0)
            return r2
        L_0x0036:
            java.io.File r0 = new java.io.File
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "history-"
            r3.append(r4)
            long r4 = java.lang.System.currentTimeMillis()
            r3.append(r4)
            java.lang.String r4 = ".csv"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.<init>(r1, r3)
            java.io.OutputStreamWriter r1 = new java.io.OutputStreamWriter     // Catch:{ IOException -> 0x0089, all -> 0x0087 }
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0089, all -> 0x0087 }
            r3.<init>(r0)     // Catch:{ IOException -> 0x0089, all -> 0x0087 }
            java.lang.String r4 = "UTF-8"
            java.nio.charset.Charset r4 = java.nio.charset.Charset.forName(r4)     // Catch:{ IOException -> 0x0089, all -> 0x0087 }
            r1.<init>(r3, r4)     // Catch:{ IOException -> 0x0089, all -> 0x0087 }
            r1.write(r6)     // Catch:{ IOException -> 0x0085 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0085 }
            r6.<init>()     // Catch:{ IOException -> 0x0085 }
            java.lang.String r3 = "file://"
            r6.append(r3)     // Catch:{ IOException -> 0x0085 }
            java.lang.String r3 = r0.getAbsolutePath()     // Catch:{ IOException -> 0x0085 }
            r6.append(r3)     // Catch:{ IOException -> 0x0085 }
            java.lang.String r6 = r6.toString()     // Catch:{ IOException -> 0x0085 }
            android.net.Uri r6 = android.net.Uri.parse(r6)     // Catch:{ IOException -> 0x0085 }
            r1.close()     // Catch:{ IOException -> 0x0084 }
        L_0x0084:
            return r6
        L_0x0085:
            r6 = move-exception
            goto L_0x008b
        L_0x0087:
            r6 = move-exception
            goto L_0x00b1
        L_0x0089:
            r6 = move-exception
            r1 = r2
        L_0x008b:
            java.lang.String r3 = com.google.zxing.client.android.history.HistoryManager.TAG     // Catch:{ all -> 0x00af }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00af }
            r4.<init>()     // Catch:{ all -> 0x00af }
            java.lang.String r5 = "Couldn't access file "
            r4.append(r5)     // Catch:{ all -> 0x00af }
            r4.append(r0)     // Catch:{ all -> 0x00af }
            java.lang.String r0 = " due to "
            r4.append(r0)     // Catch:{ all -> 0x00af }
            r4.append(r6)     // Catch:{ all -> 0x00af }
            java.lang.String r6 = r4.toString()     // Catch:{ all -> 0x00af }
            android.util.Log.w(r3, r6)     // Catch:{ all -> 0x00af }
            if (r1 == 0) goto L_0x00ae
            r1.close()     // Catch:{ IOException -> 0x00ae }
        L_0x00ae:
            return r2
        L_0x00af:
            r6 = move-exception
            r2 = r1
        L_0x00b1:
            if (r2 == 0) goto L_0x00b6
            r2.close()     // Catch:{ IOException -> 0x00b6 }
        L_0x00b6:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.android.history.HistoryManager.saveHistory(java.lang.String):android.net.Uri");
    }

    private static String massageHistoryField(String str) {
        return str == null ? "" : str.replace("\"", "\"\"");
    }

    private static void close(Cursor cursor, SQLiteDatabase sQLiteDatabase) {
        if (cursor != null) {
            cursor.close();
        }
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
        }
    }
}
