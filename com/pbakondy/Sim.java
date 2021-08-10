package com.pbakondy;

import android.os.Build;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

public class Sim extends CordovaPlugin {
    private static final String GET_SIM_INFO = "getSimInfo";
    private static final String HAS_READ_PERMISSION = "hasReadPermission";
    private static final String LOG_TAG = "CordovaPluginSim";
    private static final String REQUEST_READ_PERMISSION = "requestReadPermission";
    private CallbackContext callback;

    /* JADX DEBUG: Multi-variable search result rejected for r0v12, resolved type: java.lang.String */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: Multiple debug info for r0v16 java.lang.String: [D('subscriberId' java.lang.String), D('phoneType' int)] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x020b  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x022c  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0247  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0255  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0292  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x029d  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x02a8  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x02b9  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x02df  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x02ed  */
    @Override // org.apache.cordova.CordovaPlugin
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean execute(java.lang.String r43, org.json.JSONArray r44, org.apache.cordova.CallbackContext r45) throws org.json.JSONException {
        /*
            r42 = this;
            r1 = r42
            r2 = r43
            r3 = r45
            java.lang.String r4 = "simSerialNumber"
            java.lang.String r5 = "deviceId"
            java.lang.String r6 = "phoneNumber"
            java.lang.String r7 = "isNetworkRoaming"
            java.lang.String r8 = "mnc"
            java.lang.String r9 = "mcc"
            java.lang.String r10 = "countryCode"
            java.lang.String r11 = "carrierName"
            java.lang.String r12 = "android.permission.READ_PHONE_STATE"
            r1.callback = r3
            java.lang.String r0 = "getSimInfo"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0309
            org.apache.cordova.CordovaInterface r0 = r1.cordova
            android.app.Activity r0 = r0.getActivity()
            android.content.Context r15 = r0.getApplicationContext()
            java.lang.String r0 = "phone"
            java.lang.Object r0 = r15.getSystemService(r0)
            r13 = r0
            android.telephony.TelephonyManager r13 = (android.telephony.TelephonyManager) r13
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ JSONException -> 0x01c2, Exception -> 0x01b5 }
            r14 = 23
            if (r0 < r14) goto L_0x0060
            int r0 = r13.getPhoneCount()     // Catch:{ JSONException -> 0x0057, Exception -> 0x004e }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ JSONException -> 0x0057, Exception -> 0x004e }
            r18 = r0
            goto L_0x0060
        L_0x004e:
            r0 = move-exception
            r28 = r7
            r34 = r11
            r29 = r15
            goto L_0x01bc
        L_0x0057:
            r0 = move-exception
            r28 = r7
            r34 = r11
            r29 = r15
            goto L_0x01c9
        L_0x0060:
            int r0 = android.os.Build.VERSION.SDK_INT
            r14 = 22
            if (r0 < r14) goto L_0x01ae
            boolean r0 = r1.simPermissionGranted(r12)
            if (r0 == 0) goto L_0x01a7
            java.lang.String r0 = "telephony_subscription_service"
            java.lang.Object r0 = r15.getSystemService(r0)
            android.telephony.SubscriptionManager r0 = (android.telephony.SubscriptionManager) r0
            int r14 = r0.getActiveSubscriptionInfoCount()
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r19 = r14
            int r14 = r0.getActiveSubscriptionInfoCountMax()
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r20 = r14
            org.json.JSONArray r14 = new org.json.JSONArray
            r14.<init>()
            java.util.List r17 = r0.getActiveSubscriptionInfoList()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            java.util.Iterator r23 = r17.iterator()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
        L_0x0095:
            boolean r24 = r23.hasNext()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            if (r24 == 0) goto L_0x0188
            java.lang.Object r24 = r23.next()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            android.telephony.SubscriptionInfo r24 = (android.telephony.SubscriptionInfo) r24     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            java.lang.CharSequence r25 = r24.getCarrierName()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            java.lang.String r26 = r24.getCountryIso()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            r27 = r26
            int r26 = r24.getDataRoaming()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            r28 = r26
            java.lang.CharSequence r26 = r24.getDisplayName()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            java.lang.String r29 = r24.getIccId()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            r30 = r29
            int r29 = r24.getMcc()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            r31 = r29
            int r29 = r24.getMnc()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            r32 = r29
            java.lang.String r29 = r24.getNumber()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            r33 = r29
            int r29 = r24.getSimSlotIndex()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            r34 = r29
            int r29 = r24.getSubscriptionId()     // Catch:{ JSONException -> 0x019d, Exception -> 0x0193 }
            r35 = r29
            r29 = r15
            r15 = r34
            boolean r34 = r0.isNetworkRoaming(r15)     // Catch:{ JSONException -> 0x0180, Exception -> 0x0178 }
            r36 = r34
            r34 = 0
            r37 = r0
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ JSONException -> 0x0180, Exception -> 0x0178 }
            r2 = 23
            if (r0 < r2) goto L_0x00f4
            java.lang.String r0 = r13.getDeviceId(r15)     // Catch:{ JSONException -> 0x0180, Exception -> 0x0178 }
            r34 = r0
            goto L_0x00f6
        L_0x00f4:
            r0 = r34
        L_0x00f6:
            org.json.JSONObject r22 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0180, Exception -> 0x0178 }
            r22.<init>()     // Catch:{ JSONException -> 0x0180, Exception -> 0x0178 }
            r34 = r22
            java.lang.String r2 = r25.toString()     // Catch:{ JSONException -> 0x0180, Exception -> 0x0178 }
            r3 = r34
            r3.put(r11, r2)     // Catch:{ JSONException -> 0x0180, Exception -> 0x0178 }
            java.lang.String r2 = "displayName"
            r34 = r11
            java.lang.String r11 = r26.toString()     // Catch:{ JSONException -> 0x0174, Exception -> 0x0170 }
            r3.put(r2, r11)     // Catch:{ JSONException -> 0x0174, Exception -> 0x0170 }
            r2 = r27
            r3.put(r10, r2)     // Catch:{ JSONException -> 0x0174, Exception -> 0x0170 }
            r11 = r31
            r3.put(r9, r11)     // Catch:{ JSONException -> 0x0174, Exception -> 0x0170 }
            r27 = r2
            r2 = r32
            r3.put(r8, r2)     // Catch:{ JSONException -> 0x0174, Exception -> 0x0170 }
            r31 = r2
            r2 = r36
            r3.put(r7, r2)     // Catch:{ JSONException -> 0x0174, Exception -> 0x0170 }
            r32 = r2
            java.lang.String r2 = "isDataRoaming"
            r36 = r11
            r11 = r28
            r28 = r7
            r7 = 1
            if (r11 != r7) goto L_0x0138
            r7 = 1
            goto L_0x0139
        L_0x0138:
            r7 = 0
        L_0x0139:
            r3.put(r2, r7)     // Catch:{ JSONException -> 0x016e, Exception -> 0x016c }
            java.lang.String r2 = "simSlotIndex"
            r3.put(r2, r15)     // Catch:{ JSONException -> 0x016e, Exception -> 0x016c }
            r2 = r33
            r3.put(r6, r2)     // Catch:{ JSONException -> 0x016e, Exception -> 0x016c }
            if (r0 == 0) goto L_0x014b
            r3.put(r5, r0)     // Catch:{ JSONException -> 0x016e, Exception -> 0x016c }
        L_0x014b:
            r7 = r30
            r3.put(r4, r7)     // Catch:{ JSONException -> 0x016e, Exception -> 0x016c }
            r30 = r0
            java.lang.String r0 = "subscriptionId"
            r33 = r2
            r2 = r35
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x016e, Exception -> 0x016c }
            r14.put(r3)     // Catch:{ JSONException -> 0x016e, Exception -> 0x016c }
            r2 = r43
            r3 = r45
            r7 = r28
            r15 = r29
            r11 = r34
            r0 = r37
            goto L_0x0095
        L_0x016c:
            r0 = move-exception
            goto L_0x017d
        L_0x016e:
            r0 = move-exception
            goto L_0x0185
        L_0x0170:
            r0 = move-exception
            r28 = r7
            goto L_0x017d
        L_0x0174:
            r0 = move-exception
            r28 = r7
            goto L_0x0185
        L_0x0178:
            r0 = move-exception
            r28 = r7
            r34 = r11
        L_0x017d:
            r17 = r14
            goto L_0x01bc
        L_0x0180:
            r0 = move-exception
            r28 = r7
            r34 = r11
        L_0x0185:
            r17 = r14
            goto L_0x01c9
        L_0x0188:
            r37 = r0
            r28 = r7
            r34 = r11
            r29 = r15
            r17 = r14
            goto L_0x01cc
        L_0x0193:
            r0 = move-exception
            r28 = r7
            r34 = r11
            r29 = r15
            r17 = r14
            goto L_0x01bc
        L_0x019d:
            r0 = move-exception
            r28 = r7
            r34 = r11
            r29 = r15
            r17 = r14
            goto L_0x01c9
        L_0x01a7:
            r28 = r7
            r34 = r11
            r29 = r15
            goto L_0x01cc
        L_0x01ae:
            r28 = r7
            r34 = r11
            r29 = r15
            goto L_0x01cc
        L_0x01b5:
            r0 = move-exception
            r28 = r7
            r34 = r11
            r29 = r15
        L_0x01bc:
            r0.printStackTrace()
            r0 = r17
            goto L_0x01ce
        L_0x01c2:
            r0 = move-exception
            r28 = r7
            r34 = r11
            r29 = r15
        L_0x01c9:
            r0.printStackTrace()
        L_0x01cc:
            r0 = r17
        L_0x01ce:
            r2 = 0
            java.lang.String r3 = r13.getSimCountryIso()
            java.lang.String r7 = r13.getSimOperator()
            java.lang.String r11 = r13.getSimOperatorName()
            r14 = 0
            r15 = 0
            r17 = 0
            r22 = 0
            r23 = r2
            int r2 = r13.getCallState()
            r24 = r14
            int r14 = r13.getDataActivity()
            r25 = r15
            int r15 = r13.getNetworkType()
            r26 = r0
            int r0 = r13.getPhoneType()
            r27 = r4
            int r4 = r13.getSimState()
            r30 = r5
            boolean r5 = r13.isNetworkRoaming()
            boolean r31 = r1.simPermissionGranted(r12)
            if (r31 == 0) goto L_0x022c
            java.lang.String r23 = r13.getLine1Number()
            java.lang.String r24 = r13.getDeviceId()
            java.lang.String r25 = r13.getDeviceSoftwareVersion()
            java.lang.String r17 = r13.getSimSerialNumber()
            java.lang.String r22 = r13.getSubscriberId()
            r31 = r13
            r40 = r17
            r41 = r22
            r13 = r23
            r38 = r24
            r39 = r25
            goto L_0x0238
        L_0x022c:
            r31 = r13
            r40 = r17
            r41 = r22
            r13 = r23
            r38 = r24
            r39 = r25
        L_0x0238:
            java.lang.String r17 = ""
            java.lang.String r22 = ""
            r23 = r6
            int r6 = r7.length()
            r24 = r13
            r13 = 3
            if (r6 < r13) goto L_0x0255
            r6 = 0
            java.lang.String r17 = r7.substring(r6, r13)
            java.lang.String r22 = r7.substring(r13)
            r6 = r17
            r13 = r22
            goto L_0x0259
        L_0x0255:
            r6 = r17
            r13 = r22
        L_0x0259:
            org.json.JSONObject r16 = new org.json.JSONObject
            r16.<init>()
            r17 = r16
            r16 = r7
            r7 = r17
            r1 = r34
            r7.put(r1, r11)
            r7.put(r10, r3)
            r7.put(r9, r6)
            r7.put(r8, r13)
            java.lang.String r1 = "callState"
            r7.put(r1, r2)
            java.lang.String r1 = "dataActivity"
            r7.put(r1, r14)
            java.lang.String r1 = "networkType"
            r7.put(r1, r15)
            java.lang.String r1 = "phoneType"
            r7.put(r1, r0)
            java.lang.String r1 = "simState"
            r7.put(r1, r4)
            r1 = r28
            r7.put(r1, r5)
            if (r18 == 0) goto L_0x029b
            int r1 = r18.intValue()
            java.lang.String r8 = "phoneCount"
            r7.put(r8, r1)
        L_0x029b:
            if (r19 == 0) goto L_0x02a6
            int r1 = r19.intValue()
            java.lang.String r8 = "activeSubscriptionInfoCount"
            r7.put(r8, r1)
        L_0x02a6:
            if (r20 == 0) goto L_0x02b1
            int r1 = r20.intValue()
            java.lang.String r8 = "activeSubscriptionInfoCountMax"
            r7.put(r8, r1)
        L_0x02b1:
            r1 = r42
            boolean r8 = r1.simPermissionGranted(r12)
            if (r8 == 0) goto L_0x02df
            r9 = r23
            r8 = r24
            r7.put(r9, r8)
            r10 = r30
            r9 = r38
            r7.put(r10, r9)
            java.lang.String r10 = "deviceSoftwareVersion"
            r12 = r39
            r7.put(r10, r12)
            r1 = r27
            r10 = r40
            r7.put(r1, r10)
            java.lang.String r1 = "subscriberId"
            r17 = r0
            r0 = r41
            r7.put(r1, r0)
            goto L_0x02eb
        L_0x02df:
            r17 = r0
            r8 = r24
            r9 = r38
            r12 = r39
            r10 = r40
            r0 = r41
        L_0x02eb:
            if (r26 == 0) goto L_0x02fd
            int r1 = r26.length()
            if (r1 == 0) goto L_0x02fd
            java.lang.String r1 = "cards"
            r22 = r0
            r0 = r26
            r7.put(r1, r0)
            goto L_0x0301
        L_0x02fd:
            r22 = r0
            r0 = r26
        L_0x0301:
            r1 = r45
            r1.success(r7)
            r21 = 1
            return r21
        L_0x0309:
            r1 = r3
            r21 = 1
            java.lang.String r0 = "hasReadPermission"
            r2 = r43
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x031a
            r42.hasReadPermission()
            return r21
        L_0x031a:
            java.lang.String r0 = "requestReadPermission"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0326
            r42.requestReadPermission()
            return r21
        L_0x0326:
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pbakondy.Sim.execute(java.lang.String, org.json.JSONArray, org.apache.cordova.CallbackContext):boolean");
    }

    private void hasReadPermission() {
        this.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, simPermissionGranted("android.permission.READ_PHONE_STATE")));
    }

    private void requestReadPermission() {
        requestPermission("android.permission.READ_PHONE_STATE");
    }

    private boolean simPermissionGranted(String type) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        return this.cordova.hasPermission(type);
    }

    private void requestPermission(String type) {
        LOG.i(LOG_TAG, "requestPermission");
        if (!simPermissionGranted(type)) {
            this.cordova.requestPermission(this, 12345, type);
        } else {
            this.callback.success();
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (grantResults.length <= 0 || grantResults[0] != 0) {
            this.callback.error("Permission denied");
        } else {
            this.callback.success();
        }
    }
}
