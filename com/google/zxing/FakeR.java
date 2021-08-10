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

    public int getId(String group, String key) {
        return this.context.getResources().getIdentifier(key, group, this.packageName);
    }

    public static int getId(Context context2, String group, String key) {
        return context2.getResources().getIdentifier(key, group, context2.getPackageName());
    }
}
