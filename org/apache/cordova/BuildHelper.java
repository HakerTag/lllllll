package org.apache.cordova;

import android.content.Context;

public class BuildHelper {
    private static String TAG = "BuildHelper";

    public static Object getBuildConfigValue(Context ctx, String key) {
        try {
            return Class.forName(ctx.getClass().getPackage().getName() + ".BuildConfig").getField(key).get(null);
        } catch (ClassNotFoundException e) {
            LOG.d(TAG, "Unable to get the BuildConfig, is this built with ANT?");
            e.printStackTrace();
            return null;
        } catch (NoSuchFieldException e2) {
            String str = TAG;
            LOG.d(str, key + " is not a valid field. Check your build.gradle");
            return null;
        } catch (IllegalAccessException e3) {
            LOG.d(TAG, "Illegal Access Exception: Let's print a stack trace.");
            e3.printStackTrace();
            return null;
        } catch (NullPointerException e4) {
            LOG.d(TAG, "Null Pointer Exception: Let's print a stack trace.");
            e4.printStackTrace();
            return null;
        }
    }
}
