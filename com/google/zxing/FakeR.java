package com.google.zxing;

import android.app.Activity;
import android.content.Context;

public class FakeR {
    private Context context;
    private String packageName;

    public FakeR(Activity activity) {
        Context applicationContext = activity.getApplicationContext();
        this.context = applicationContext;
        this.packageName = applicationContext.getPackageName();
    }

    public FakeR(Context context2) {
        this.context = context2;
        this.packageName = context2.getPackageName();
    }

    public int getId(String str, String str2) {
        return this.context.getResources().getIdentifier(str2, str, this.packageName);
    }

    public static int getId(Context context2, String str, String str2) {
        return context2.getResources().getIdentifier(str2, str, context2.getPackageName());
    }
}
