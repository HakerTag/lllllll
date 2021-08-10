package com.cordova.plugins.sms;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsManager;
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
    public boolean execute(String action, JSONArray args2, CallbackContext callbackContext2) throws JSONException {
        this.callbackContext = callbackContext2;
        this.args = args2;
        if (action.equals("send")) {
            boolean isIntent = false;
            try {
                isIntent = args2.getString(2).equalsIgnoreCase("INTENT");
            } catch (NullPointerException e) {
            }
            if (isIntent || hasPermission()) {
                sendSMS();
            } else {
                requestPermission(0);
            }
            return true;
        } else if (action.equals("has_permission")) {
            callbackContext2.sendPluginResult(new PluginResult(PluginResult.Status.OK, hasPermission()));
            return true;
        } else if (!action.equals("request_permission")) {
            return false;
        } else {
            requestPermission(1);
            return true;
        }
    }

    private boolean hasPermission() {
        return this.cordova.hasPermission("android.permission.SEND_SMS");
    }

    private void requestPermission(int requestCode) {
        this.cordova.requestPermission(this, requestCode, "android.permission.SEND_SMS");
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == -1) {
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "User has denied permission"));
                return;
            }
        }
        if (requestCode == 0) {
            sendSMS();
        } else {
            this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
        }
    }

    private boolean sendSMS() {
        this.cordova.getThreadPool().execute(new Runnable() {
            /* class com.cordova.plugins.sms.Sms.AnonymousClass1 */

            public void run() {
                String separator = ";";
                try {
                    if (Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
                        separator = ",";
                    }
                    String phoneNumber = Sms.this.args.getJSONArray(0).join(separator).replace("\"", "");
                    String message = Sms.this.args.getString(1);
                    String method = Sms.this.args.getString(2);
                    if (Boolean.parseBoolean(Sms.this.args.getString(3))) {
                        message = message.replace("\\n", System.getProperty("line.separator"));
                    }
                    if (!Sms.this.checkSupport()) {
                        Sms.this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "SMS not supported on this platform"));
                    } else if (method.equalsIgnoreCase("INTENT")) {
                        Sms.this.invokeSMSIntent(phoneNumber, message);
                        Sms.this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                    } else {
                        Sms.this.send(Sms.this.callbackContext, phoneNumber, message);
                    }
                } catch (JSONException e) {
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

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void invokeSMSIntent(String phoneNumber, String message) {
        Intent sendIntent;
        if (!"".equals(phoneNumber) || Build.VERSION.SDK_INT < 19) {
            sendIntent = new Intent("android.intent.action.VIEW");
            sendIntent.putExtra("sms_body", message);
            sendIntent.putExtra("address", phoneNumber);
            sendIntent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
        } else {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this.cordova.getActivity());
            sendIntent = new Intent("android.intent.action.SEND");
            sendIntent.setType("text/plain");
            sendIntent.putExtra("android.intent.extra.TEXT", message);
            if (defaultSmsPackageName != null) {
                sendIntent.setPackage(defaultSmsPackageName);
            }
        }
        this.cordova.getActivity().startActivity(sendIntent);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void send(final CallbackContext callbackContext2, String phoneNumber, String message) {
        SmsManager manager = SmsManager.getDefault();
        final ArrayList<String> parts = manager.divideMessage(message);
        String intentFilterAction = INTENT_FILTER_SMS_SENT + UUID.randomUUID().toString();
        this.cordova.getActivity().registerReceiver(new BroadcastReceiver() {
            /* class com.cordova.plugins.sms.Sms.AnonymousClass2 */
            boolean anyError = false;
            int partsCount = parts.size();

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
        }, new IntentFilter(intentFilterAction));
        PendingIntent sentIntent = PendingIntent.getBroadcast(this.cordova.getActivity(), 0, new Intent(intentFilterAction), 0);
        if (parts.size() > 1) {
            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            for (int i = 0; i < parts.size(); i++) {
                sentIntents.add(sentIntent);
            }
            manager.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, null);
            return;
        }
        manager.sendTextMessage(phoneNumber, null, message, sentIntent, null);
    }
}
