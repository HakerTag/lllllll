package de.appplant.cordova.plugin.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.view.ViewCompat;
import org.json.JSONObject;

public class ForegroundService extends Service {
    private static final String NOTIFICATION_ICON = "icon";
    public static final int NOTIFICATION_ID = -574543954;
    private static final String NOTIFICATION_TEXT = "Doing heavy tasks.";
    private static final String NOTIFICATION_TITLE = "App is running in background";
    private final IBinder binder = new ForegroundBinder();
    private PowerManager.WakeLock wakeLock;

    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    class ForegroundBinder extends Binder {
        ForegroundBinder() {
        }

        /* access modifiers changed from: package-private */
        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }

    public void onCreate() {
        super.onCreate();
        keepAwake();
    }

    public void onDestroy() {
        super.onDestroy();
        sleepWell();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return 1;
    }

    private void keepAwake() {
        if (!BackgroundMode.getSettings().optBoolean("silent", false)) {
            startForeground(NOTIFICATION_ID, makeNotification());
        }
        PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "backgroundmode:wakelock");
        this.wakeLock = newWakeLock;
        newWakeLock.acquire();
    }

    private void sleepWell() {
        stopForeground(true);
        getNotificationManager().cancel(NOTIFICATION_ID);
        PowerManager.WakeLock wakeLock2 = this.wakeLock;
        if (wakeLock2 != null) {
            wakeLock2.release();
            this.wakeLock = null;
        }
    }

    private Notification makeNotification() {
        return makeNotification(BackgroundMode.getSettings());
    }

    private Notification makeNotification(JSONObject settings) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel mChannel = new NotificationChannel("cordova-plugin-background-mode-id", "cordova-plugin-background-mode", 2);
            mChannel.setDescription("cordova-plugin-background-moden notification");
            getNotificationManager().createNotificationChannel(mChannel);
        }
        String title = settings.optString("title", NOTIFICATION_TITLE);
        String text = settings.optString("text", NOTIFICATION_TEXT);
        boolean bigText = settings.optBoolean("bigText", false);
        Context context = getApplicationContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        Notification.Builder notification = new Notification.Builder(context).setContentTitle(title).setContentText(text).setOngoing(true).setSmallIcon(getIconResId(settings));
        if (Build.VERSION.SDK_INT >= 26) {
            notification.setChannelId("cordova-plugin-background-mode-id");
        }
        if (settings.optBoolean("hidden", true)) {
            notification.setPriority(-2);
        }
        if (bigText || text.contains("\n")) {
            notification.setStyle(new Notification.BigTextStyle().bigText(text));
        }
        setColor(notification, settings);
        if (intent != null && settings.optBoolean("resume")) {
            intent.addFlags(603979776);
            notification.setContentIntent(PendingIntent.getActivity(context, NOTIFICATION_ID, intent, 134217728));
        }
        return notification.build();
    }

    /* access modifiers changed from: protected */
    public void updateNotification(JSONObject settings) {
        if (settings.optBoolean("silent", false)) {
            stopForeground(true);
            return;
        }
        getNotificationManager().notify(NOTIFICATION_ID, makeNotification(settings));
    }

    private int getIconResId(JSONObject settings) {
        String icon = settings.optString(NOTIFICATION_ICON, NOTIFICATION_ICON);
        int resId = getIconResId(icon, "mipmap");
        if (resId == 0) {
            return getIconResId(icon, "drawable");
        }
        return resId;
    }

    private int getIconResId(String icon, String type) {
        Resources res = getResources();
        String pkgName = getPackageName();
        int resId = res.getIdentifier(icon, type, pkgName);
        if (resId == 0) {
            return res.getIdentifier(NOTIFICATION_ICON, type, pkgName);
        }
        return resId;
    }

    private void setColor(Notification.Builder notification, JSONObject settings) {
        String hex = settings.optString("color", null);
        if (Build.VERSION.SDK_INT >= 21 && hex != null) {
            try {
                notification.setColor(Integer.parseInt(hex, 16) + ViewCompat.MEASURED_STATE_MASK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService("notification");
    }
}
