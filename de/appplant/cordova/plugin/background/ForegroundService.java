package de.appplant.cordova.plugin.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewCompat;
import org.json.JSONObject;

public class ForegroundService extends Service {
    private static final String NOTIFICATION_ICON = "icon";
    public static final int NOTIFICATION_ID = -574543954;
    private static final String NOTIFICATION_TEXT = "Doing heavy tasks.";
    private static final String NOTIFICATION_TITLE = "App is running in background";
    private final IBinder binder = new ForegroundBinder();
    private PowerManager.WakeLock wakeLock;

    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

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

    private Notification makeNotification(JSONObject jSONObject) {
        if (Build.VERSION.SDK_INT >= 26) {
            String optString = jSONObject.optString("channelName", "cordova-plugin-background-mode");
            String optString2 = jSONObject.optString("channelDescription", "cordova-plugin-background-moden notification");
            NotificationChannel notificationChannel = new NotificationChannel("cordova-plugin-background-mode-id", optString, 2);
            notificationChannel.setDescription(optString2);
            getNotificationManager().createNotificationChannel(notificationChannel);
        }
        String optString3 = jSONObject.optString("title", NOTIFICATION_TITLE);
        String optString4 = jSONObject.optString("text", NOTIFICATION_TEXT);
        boolean optBoolean = jSONObject.optBoolean("bigText", false);
        String optString5 = jSONObject.optString("subText", "");
        String optString6 = jSONObject.optString("visibility", "");
        Context applicationContext = getApplicationContext();
        String packageName = applicationContext.getPackageName();
        Intent launchIntentForPackage = applicationContext.getPackageManager().getLaunchIntentForPackage(packageName);
        int iconResId = getIconResId(jSONObject);
        if (iconResId == 0) {
            iconResId = applicationContext.getApplicationInfo().icon;
        }
        NotificationCompat.Builder showWhen = new NotificationCompat.Builder(applicationContext, "cordova-plugin-background-mode-id").setContentTitle(optString3).setContentText(optString4).setOngoing(true).setSmallIcon(iconResId).setShowWhen(jSONObject.optBoolean("showWhen", true));
        if (!optString5.equals("")) {
            showWhen.setSubText(optString5);
        }
        if (jSONObject.optBoolean("allowClose", false)) {
            showWhen.addAction(new NotificationCompat.Action.Builder(getIconResId(jSONObject.optString("closeIcon", "power")), jSONObject.optString("closeTitle", "Close"), PendingIntent.getBroadcast(applicationContext, 1337, new Intent("com.backgroundmode.close" + packageName), 0)).build());
        }
        if (jSONObject.optBoolean("hidden", true)) {
            showWhen.setPriority(-2);
        }
        if (optBoolean || optString4.contains("\n")) {
            showWhen.setStyle(new NotificationCompat.BigTextStyle().bigText(optString4));
        }
        if (!optString6.equals("")) {
            showWhen.setVisibility(getVisibility(optString6));
        }
        setColor(showWhen, jSONObject);
        if (launchIntentForPackage != null && jSONObject.optBoolean("resume")) {
            launchIntentForPackage.addFlags(603979776);
            showWhen.setContentIntent(PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, launchIntentForPackage, 134217728));
        }
        return showWhen.build();
    }

    /* access modifiers changed from: protected */
    public void updateNotification(JSONObject jSONObject) {
        if (jSONObject.optBoolean("silent", false)) {
            stopForeground(true);
            return;
        }
        getNotificationManager().notify(NOTIFICATION_ID, makeNotification(jSONObject));
    }

    private int getIconResId(String str) {
        int iconResId = getIconResId(str, "mipmap");
        if (iconResId == 0) {
            iconResId = getIconResId(str, "drawable");
        }
        if (iconResId == 0) {
            iconResId = getIconResId(NOTIFICATION_ICON, "mipmap");
        }
        return iconResId == 0 ? getIconResId(NOTIFICATION_ICON, "drawable") : iconResId;
    }

    private int getIconResId(JSONObject jSONObject) {
        return getIconResId(jSONObject.optString(NOTIFICATION_ICON, NOTIFICATION_ICON));
    }

    private int getIconResId(String str, String str2) {
        return getResources().getIdentifier(str, str2, getPackageName());
    }

    private int getVisibility(String str) {
        if (str.equals("public")) {
            return 1;
        }
        return str.equals("secret") ? -1 : 0;
    }

    private void setColor(NotificationCompat.Builder builder, JSONObject jSONObject) {
        String optString = jSONObject.optString("color", null);
        if (Build.VERSION.SDK_INT >= 21 && optString != null) {
            try {
                builder.setColor(Integer.parseInt(optString, 16) + ViewCompat.MEASURED_STATE_MASK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService("notification");
    }
}
