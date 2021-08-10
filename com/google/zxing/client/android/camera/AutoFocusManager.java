package com.google.zxing.client.android.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.zxing.client.android.PreferencesActivity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;

/* access modifiers changed from: package-private */
public final class AutoFocusManager implements Camera.AutoFocusCallback {
    private static final long AUTO_FOCUS_INTERVAL_MS = 2000;
    private static final Collection<String> FOCUS_MODES_CALLING_AF;
    private static final String TAG = AutoFocusManager.class.getSimpleName();
    private final Camera camera;
    private boolean focusing;
    private AsyncTask<?, ?, ?> outstandingTask;
    private boolean stopped;
    private final boolean useAutoFocus;

    static {
        ArrayList arrayList = new ArrayList(2);
        FOCUS_MODES_CALLING_AF = arrayList;
        arrayList.add("auto");
        FOCUS_MODES_CALLING_AF.add("macro");
    }

    AutoFocusManager(Context context, Camera camera2) {
        this.camera = camera2;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String currentFocusMode = camera2.getParameters().getFocusMode();
        boolean z = true;
        this.useAutoFocus = (!sharedPrefs.getBoolean(PreferencesActivity.KEY_AUTO_FOCUS, true) || !FOCUS_MODES_CALLING_AF.contains(currentFocusMode)) ? false : z;
        String str = TAG;
        Log.i(str, "Current focus mode '" + currentFocusMode + "'; use auto focus? " + this.useAutoFocus);
        start();
    }

    public synchronized void onAutoFocus(boolean success, Camera theCamera) {
        this.focusing = false;
        autoFocusAgainLater();
    }

    private synchronized void autoFocusAgainLater() {
        if (!this.stopped && this.outstandingTask == null) {
            AutoFocusTask newTask = new AutoFocusTask();
            try {
                newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
                this.outstandingTask = newTask;
            } catch (RejectedExecutionException ree) {
                Log.w(TAG, "Could not request auto focus", ree);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void start() {
        if (this.useAutoFocus) {
            this.outstandingTask = null;
            if (!this.stopped && !this.focusing) {
                try {
                    this.camera.autoFocus(this);
                    this.focusing = true;
                } catch (RuntimeException re) {
                    Log.w(TAG, "Unexpected exception while focusing", re);
                    autoFocusAgainLater();
                }
            }
        }
    }

    private synchronized void cancelOutstandingTask() {
        if (this.outstandingTask != null) {
            if (this.outstandingTask.getStatus() != AsyncTask.Status.FINISHED) {
                this.outstandingTask.cancel(true);
            }
            this.outstandingTask = null;
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void stop() {
        this.stopped = true;
        if (this.useAutoFocus) {
            cancelOutstandingTask();
            try {
                this.camera.cancelAutoFocus();
            } catch (RuntimeException re) {
                Log.w(TAG, "Unexpected exception while cancelling focusing", re);
            }
        }
    }

    /* access modifiers changed from: private */
    public final class AutoFocusTask extends AsyncTask<Object, Object, Object> {
        private AutoFocusTask() {
        }

        /* access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Object doInBackground(Object... voids) {
            try {
                Thread.sleep(AutoFocusManager.AUTO_FOCUS_INTERVAL_MS);
            } catch (InterruptedException e) {
            }
            AutoFocusManager.this.start();
            return null;
        }
    }
}
