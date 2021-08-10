package com.phonegap.plugins.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.encode.EncodeActivity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BarcodeScanner extends CordovaPlugin {
    private static final String CANCELLED = "cancelled";
    private static final String DATA = "data";
    private static final String DISABLE_BEEP = "disableSuccessBeep";
    private static final String EMAIL_TYPE = "EMAIL_TYPE";
    private static final String ENCODE = "encode";
    private static final String FORMAT = "format";
    private static final String FORMATS = "formats";
    private static final String LOG_TAG = "BarcodeScanner";
    private static final String ORIENTATION = "orientation";
    private static final String PHONE_TYPE = "PHONE_TYPE";
    private static final String PREFER_FRONTCAMERA = "preferFrontCamera";
    private static final String PROMPT = "prompt";
    public static final int REQUEST_CODE = 47740;
    private static final String RESULTDISPLAY_DURATION = "resultDisplayDuration";
    private static final String SAVE_HISTORY = "saveHistory";
    private static final String SCAN = "scan";
    private static final String SHOW_FLIP_CAMERA_BUTTON = "showFlipCameraButton";
    private static final String SHOW_TORCH_BUTTON = "showTorchButton";
    private static final String SMS_TYPE = "SMS_TYPE";
    private static final String TEXT = "text";
    private static final String TEXT_TYPE = "TEXT_TYPE";
    private static final String TORCH_ON = "torchOn";
    private static final String TYPE = "type";
    private CallbackContext callbackContext;
    private String[] permissions = {"android.permission.CAMERA"};
    private JSONArray requestArgs;

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext2) {
        this.callbackContext = callbackContext2;
        this.requestArgs = args;
        if (action.equals(ENCODE)) {
            JSONObject obj = args.optJSONObject(0);
            if (obj != null) {
                String type = obj.optString(TYPE);
                String data = obj.optString(DATA);
                if (type == null) {
                    type = "TEXT_TYPE";
                }
                if (data == null) {
                    callbackContext2.error("User did not specify data to encode");
                    return true;
                }
                encode(type, data);
            } else {
                callbackContext2.error("User did not specify data to encode");
                return true;
            }
        } else if (!action.equals(SCAN)) {
            return false;
        } else {
            if (!hasPermisssion()) {
                requestPermissions(0);
            } else {
                scan(args);
            }
        }
        return true;
    }

    public void scan(final JSONArray args) {
        this.cordova.getThreadPool().execute(new Runnable() {
            /* class com.phonegap.plugins.barcodescanner.BarcodeScanner.AnonymousClass1 */

            public void run() {
                Intent intentScan = new Intent(this.cordova.getActivity().getBaseContext(), CaptureActivity.class);
                intentScan.setAction(Intents.Scan.ACTION);
                intentScan.addCategory("android.intent.category.DEFAULT");
                if (args.length() > 0) {
                    for (int i = 0; i < args.length(); i++) {
                        try {
                            JSONObject obj = args.getJSONObject(i);
                            JSONArray names = obj.names();
                            for (int j = 0; j < names.length(); j++) {
                                try {
                                    String key = names.getString(j);
                                    Object value = obj.get(key);
                                    if (value instanceof Integer) {
                                        intentScan.putExtra(key, (Integer) value);
                                    } else if (value instanceof String) {
                                        intentScan.putExtra(key, (String) value);
                                    }
                                } catch (JSONException e) {
                                    Log.i("CordovaLog", e.getLocalizedMessage());
                                }
                            }
                            intentScan.putExtra(Intents.Scan.CAMERA_ID, obj.optBoolean(BarcodeScanner.PREFER_FRONTCAMERA, false) ? 1 : 0);
                            intentScan.putExtra(Intents.Scan.SHOW_FLIP_CAMERA_BUTTON, obj.optBoolean(BarcodeScanner.SHOW_FLIP_CAMERA_BUTTON, false));
                            intentScan.putExtra(Intents.Scan.SHOW_TORCH_BUTTON, obj.optBoolean(BarcodeScanner.SHOW_TORCH_BUTTON, false));
                            intentScan.putExtra(Intents.Scan.TORCH_ON, obj.optBoolean(BarcodeScanner.TORCH_ON, false));
                            intentScan.putExtra(Intents.Scan.SAVE_HISTORY, obj.optBoolean(BarcodeScanner.SAVE_HISTORY, false));
                            intentScan.putExtra(Intents.Scan.BEEP_ON_SCAN, !obj.optBoolean(BarcodeScanner.DISABLE_BEEP, false));
                            if (obj.has(BarcodeScanner.RESULTDISPLAY_DURATION)) {
                                intentScan.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, "" + obj.optLong(BarcodeScanner.RESULTDISPLAY_DURATION));
                            }
                            if (obj.has(BarcodeScanner.FORMATS)) {
                                intentScan.putExtra(Intents.Scan.FORMATS, obj.optString(BarcodeScanner.FORMATS));
                            }
                            if (obj.has(BarcodeScanner.PROMPT)) {
                                intentScan.putExtra(Intents.Scan.PROMPT_MESSAGE, obj.optString(BarcodeScanner.PROMPT));
                            }
                            if (obj.has(BarcodeScanner.ORIENTATION)) {
                                intentScan.putExtra(Intents.Scan.ORIENTATION_LOCK, obj.optString(BarcodeScanner.ORIENTATION));
                            }
                        } catch (JSONException e2) {
                            Log.i("CordovaLog", e2.getLocalizedMessage());
                        }
                    }
                }
                intentScan.setPackage(this.cordova.getActivity().getApplicationContext().getPackageName());
                this.cordova.startActivityForResult(this, intentScan, BarcodeScanner.REQUEST_CODE);
            }
        });
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        CallbackContext callbackContext2;
        if (requestCode == 47740 && (callbackContext2 = this.callbackContext) != null) {
            if (resultCode == -1) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(TEXT, intent.getStringExtra(Intents.Scan.RESULT));
                    obj.put(FORMAT, intent.getStringExtra(Intents.Scan.RESULT_FORMAT));
                    obj.put(CANCELLED, false);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, "This should never happen");
                }
                this.callbackContext.success(obj);
            } else if (resultCode == 0) {
                JSONObject obj2 = new JSONObject();
                try {
                    obj2.put(TEXT, "");
                    obj2.put(FORMAT, "");
                    obj2.put(CANCELLED, true);
                } catch (JSONException e2) {
                    Log.d(LOG_TAG, "This should never happen");
                }
                this.callbackContext.success(obj2);
            } else {
                callbackContext2.error("Unexpected error");
            }
        }
    }

    public void encode(String type, String data) {
        Intent intentEncode = new Intent(this.cordova.getActivity().getBaseContext(), EncodeActivity.class);
        intentEncode.setAction(Intents.Encode.ACTION);
        intentEncode.putExtra(Intents.Encode.TYPE, type);
        intentEncode.putExtra(Intents.Encode.DATA, data);
        intentEncode.setPackage(this.cordova.getActivity().getApplicationContext().getPackageName());
        this.cordova.getActivity().startActivity(intentEncode);
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean hasPermisssion() {
        for (String p : this.permissions) {
            if (!PermissionHelper.hasPermission(this, p)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void requestPermissions(int requestCode) {
        PermissionHelper.requestPermissions(this, requestCode, this.permissions);
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onRequestPermissionResult(int requestCode, String[] permissions2, int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == -1) {
                Log.d(LOG_TAG, "Permission Denied!");
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
                return;
            }
        }
        if (requestCode == 0) {
            scan(this.requestArgs);
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext2) {
        this.callbackContext = callbackContext2;
    }
}
