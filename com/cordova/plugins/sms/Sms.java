package com.cordova.plugins.sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import java.util.ArrayList;
import java.util.UUID;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class Sms extends CordovaPlugin {
    private static final String INTENT_FILTER_SMS_SENT = "SMS_SENT";
    private static final int REQUEST_PERMISSION_REQ_CODE = 1;
    private static final int SEND_SMS_REQ_CODE = 0;
    public final String ACTION_HAS_PERMISSION = "has_permission";
    public final String ACTION_REQUEST_PERMISSION = "request_permission";
    public final String ACTION_SEND_SMS = "send";
    private JSONArray args;
    private CallbackContext callbackContext;

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext2) throws JSONException {
        boolean z;
        this.callbackContext = callbackContext2;
        this.args = jSONArray;
        if (str.equals("send")) {
            try {
                z = jSONArray.getString(2).equalsIgnoreCase("INTENT");
            } catch (NullPointerException unused) {
                z = false;
            }
            if (z || hasPermission()) {
                sendSMS();
            } else {
                requestPermission(0);
            }
            return true;
        } else if (str.equals("has_permission")) {
            callbackContext2.sendPluginResult(new PluginResult(PluginResult.Status.OK, hasPermission()));
            return true;
        } else if (!str.equals("request_permission")) {
            return false;
        } else {
            requestPermission(1);
            return true;
        }
    }

    private boolean hasPermission() {
        return this.cordova.hasPermission("android.permission.SEND_SMS");
    }

    private void requestPermission(int i) {
        this.cordova.requestPermission(this, i, "android.permission.SEND_SMS");
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) throws JSONException {
        for (int i2 : iArr) {
            if (i2 == -1) {
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "User has denied permission"));
                return;
            }
        }
        if (i == 0) {
            sendSMS();
        } else {
            this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
        }
    }

    private boolean sendSMS() {
        this.cordova.getThreadPool().execute(new Runnable() {
            /* class com.cordova.plugins.sms.Sms.AnonymousClass1 */

            public void run() {
                String str = ";";
                try {
                    if (Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
                        str = ",";
                    }
                    String replace = Sms.this.args.getJSONArray(0).join(str).replace("\"", "");
                    String string = Sms.this.args.getString(1);
                    String string2 = Sms.this.args.getString(2);
                    String string3 = Sms.this.args.getString(4);
                    if (Boolean.parseBoolean(Sms.this.args.getString(3))) {
                        string = string.replace("\\n", System.getProperty("line.separator"));
                    }
                    if (!Sms.this.checkSupport()) {
                        Sms.this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "SMS not supported on this platform"));
                    } else if (string2.equalsIgnoreCase("INTENT")) {
                        Sms.this.invokeSMSIntent(replace, string);
                        Sms.this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                    } else {
                        Sms.this.send(Sms.this.callbackContext, replace, string, string3);
                    }
                } catch (JSONException unused) {
                    Sms.this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
                }
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean checkSupport() {
        return this.cordova.getActivity().getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    private int getSubscriptionId(String str) {
        if (str == null) {
            return -1;
        }
        try {
            int parseInt = Integer.parseInt(str);
            if (Build.VERSION.SDK_INT < 22) {
                return -1;
            }
            int i = -1;
            for (SubscriptionInfo subscriptionInfo : SubscriptionManager.from(this.cordova.getActivity()).getActiveSubscriptionInfoList()) {
                if (parseInt == subscriptionInfo.getSimSlotIndex()) {
                    i = subscriptionInfo.getSubscriptionId();
                }
            }
            return i;
        } catch (Exception unused) {
            return -1;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void invokeSMSIntent(String str, String str2) {
        Intent intent;
        if (!"".equals(str) || Build.VERSION.SDK_INT < 19) {
            intent = new Intent("android.intent.action.VIEW");
            intent.putExtra("sms_body", str2);
            intent.putExtra("address", str);
            intent.setData(Uri.parse("smsto:" + Uri.encode(str)));
        } else {
            String defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(this.cordova.getActivity());
            intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", str2);
            if (defaultSmsPackage != null) {
                intent.setPackage(defaultSmsPackage);
            }
        }
        this.cordova.getActivity().startActivity(intent);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void send(final CallbackContext callbackContext2, String str, String str2, String str3) {
        SmsManager smsManager;
        int subscriptionId = getSubscriptionId(str3);
        if (Build.VERSION.SDK_INT < 22 || subscriptionId < 0) {
            smsManager = SmsManager.getDefault();
        } else {
            smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId);
        }
        final ArrayList<String> divideMessage = smsManager.divideMessage(str2);
        AnonymousClass2 r10 = new BroadcastReceiver() {
            /* class com.cordova.plugins.sms.Sms.AnonymousClass2 */
            boolean anyError = false;
            int partsCount = divideMessage.size();

            public void onReceive(Context context, Intent intent) {
                int resultCode = getResultCode();
                if (resultCode == 1 || resultCode == 2 || resultCode == 3 || resultCode == 4) {
                    this.anyError = true;
                }
                int i = this.partsCount - 1;
                this.partsCount = i;
                if (i == 0) {
                    if (this.anyError) {
                        callbackContext2.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
                    } else {
                        callbackContext2.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                    }
                    Sms.this.cordova.getActivity().unregisterReceiver(this);
                }
            }
        };
        String str4 = INTENT_FILTER_SMS_SENT + UUID.randomUUID().toString();
        this.cordova.getActivity().registerReceiver(r10, new IntentFilter(str4));
        Activity activity = this.cordova.getActivity();
        Intent intent = new Intent(str4);
        PendingIntent broadcast = PendingIntent.getBroadcast(activity, 0, intent, 0);
        if (divideMessage.size() > 1) {
            ArrayList<PendingIntent> arrayList = new ArrayList<>();
            for (int i = 0; i < divideMessage.size(); i++) {
                arrayList.add(broadcast);
            }
            smsManager.sendMultipartTextMessage(str, null, divideMessage, arrayList, null);
            return;
        }
        smsManager.sendTextMessage(str, null, str2, broadcast, null);
    }
}
