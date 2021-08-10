package com.google.zxing.client.android.clipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

public final class ClipboardInterface {
    private static final String TAG = ClipboardInterface.class.getSimpleName();

    private ClipboardInterface() {
    }

    public static CharSequence getText(Context context) {
        ClipData clip = getManager(context).getPrimaryClip();
        if (hasText(context)) {
            return clip.getItemAt(0).coerceToText(context);
        }
        return null;
    }

    public static void setText(CharSequence text, Context context) {
        if (text != null) {
            try {
                getManager(context).setPrimaryClip(ClipData.newPlainText(null, text));
            } catch (IllegalStateException | NullPointerException e) {
                Log.w(TAG, "Clipboard bug", e);
            }
        }
    }

    public static boolean hasText(Context context) {
        ClipData clip = getManager(context).getPrimaryClip();
        return clip != null && clip.getItemCount() > 0;
    }

    private static ClipboardManager getManager(Context context) {
        return (ClipboardManager) context.getSystemService("clipboard");
    }
}
