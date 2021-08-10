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

    /* JADX DEBUG: Multi-variable search result rejected for r31v1, resolved type: java.lang.String */
    /* JADX DEBUG: Multi-variable search result rejected for r32v1, resolved type: java.lang.String */
    /* JADX DEBUG: Multi-variable search result rejected for r33v1, resolved type: java.lang.String */
    /* JADX DEBUG: Multi-variable search result rejected for r34v1, resolved type: java.lang.String */
    /* JADX DEBUG: Multi-variable search result rejected for r35v1, resolved type: java.lang.String */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0324  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0345  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x035a  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0367  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x039d  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x03a8  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x03b3  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x03c6  */
    @Override // org.apache.cordova.CordovaPlugin
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean execute(java.lang.String r37, org.json.JSONArray r38, org.apache.cordova.CallbackContext r39) throws org.json.JSONException {
        /*
            r36 = this;
            r1 = r36
            r0 = r37
            r2 = r39
            java.lang.String r3 = "simSerialNumber"
            java.lang.String r4 = "deviceId"
            java.lang.String r5 = "phoneNumber"
            java.lang.String r6 = "isNetworkRoaming"
            java.lang.String r7 = "mnc"
            java.lang.String r8 = "mcc"
            java.lang.String r9 = "countryCode"
            java.lang.String r10 = "carrierName"
            java.lang.String r11 = "android.permission.READ_PHONE_STATE"
            r1.callback = r2
            java.lang.String r12 = "getSimInfo"
            boolean r12 = r12.equals(r0)
            if (r12 == 0) goto L_0x03ff
            org.apache.cordova.CordovaInterface r0 = r1.cordova
            android.app.Activity r0 = r0.getActivity()
            android.content.Context r0 = r0.getApplicationContext()
            java.lang.String r12 = "phone"
            java.lang.Object r12 = r0.getSystemService(r12)
            android.telephony.TelephonyManager r12 = (android.telephony.TelephonyManager) r12
            int r15 = android.os.Build.VERSION.SDK_INT     // Catch:{ JSONException -> 0x02da, Exception -> 0x02c9 }
            r13 = 23
            if (r15 < r13) goto L_0x0043
            int r15 = r12.getPhoneCount()     // Catch:{ JSONException -> 0x02da, Exception -> 0x02c9 }
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)     // Catch:{ JSONException -> 0x02da, Exception -> 0x02c9 }
            goto L_0x0044
        L_0x0043:
            r15 = 0
        L_0x0044:
            int r14 = android.os.Build.VERSION.SDK_INT     // Catch:{ JSONException -> 0x02ba, Exception -> 0x02ab }
            r13 = 22
            if (r14 < r13) goto L_0x029b
            boolean r13 = r1.simPermissionGranted(r11)     // Catch:{ JSONException -> 0x02ba, Exception -> 0x02ab }
            if (r13 == 0) goto L_0x029b
            java.lang.String r13 = "telephony_subscription_service"
            java.lang.Object r0 = r0.getSystemService(r13)     // Catch:{ JSONException -> 0x02ba, Exception -> 0x02ab }
            android.telephony.SubscriptionManager r0 = (android.telephony.SubscriptionManager) r0     // Catch:{ JSONException -> 0x02ba, Exception -> 0x02ab }
            int r13 = r0.getActiveSubscriptionInfoCount()     // Catch:{ JSONException -> 0x02ba, Exception -> 0x02ab }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ JSONException -> 0x02ba, Exception -> 0x02ab }
            int r14 = r0.getActiveSubscriptionInfoCountMax()     // Catch:{ JSONException -> 0x028c, Exception -> 0x027d }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ JSONException -> 0x028c, Exception -> 0x027d }
            r17 = r13
            org.json.JSONArray r13 = new org.json.JSONArray     // Catch:{ JSONException -> 0x026b, Exception -> 0x0259 }
            r13.<init>()     // Catch:{ JSONException -> 0x026b, Exception -> 0x0259 }
            java.util.List r18 = r0.getActiveSubscriptionInfoList()     // Catch:{ JSONException -> 0x0247, Exception -> 0x0235 }
            java.util.Iterator r18 = r18.iterator()     // Catch:{ JSONException -> 0x0247, Exception -> 0x0235 }
        L_0x0077:
            boolean r19 = r18.hasNext()     // Catch:{ JSONException -> 0x0247, Exception -> 0x0235 }
            if (r19 == 0) goto L_0x0224
            java.lang.Object r19 = r18.next()     // Catch:{ JSONException -> 0x0247, Exception -> 0x0235 }
            android.telephony.SubscriptionInfo r19 = (android.telephony.SubscriptionInfo) r19     // Catch:{ JSONException -> 0x0247, Exception -> 0x0235 }
            java.lang.CharSequence r20 = r19.getCarrierName()     // Catch:{ JSONException -> 0x0247, Exception -> 0x0235 }
            r21 = r14
            java.lang.String r14 = r19.getCountryIso()     // Catch:{ JSONException -> 0x0212, Exception -> 0x0200 }
            r22 = r15
            int r15 = r19.getDataRoaming()     // Catch:{ JSONException -> 0x01ee, Exception -> 0x01dc }
            java.lang.CharSequence r23 = r19.getDisplayName()     // Catch:{ JSONException -> 0x01ee, Exception -> 0x01dc }
            java.lang.String r2 = r19.getIccId()     // Catch:{ JSONException -> 0x01ee, Exception -> 0x01dc }
            int r1 = r19.getMcc()     // Catch:{ JSONException -> 0x01ee, Exception -> 0x01dc }
            r24 = r11
            int r11 = r19.getMnc()     // Catch:{ JSONException -> 0x01d6, Exception -> 0x01d0 }
            r25 = r13
            java.lang.String r13 = r19.getNumber()     // Catch:{ JSONException -> 0x01c6, Exception -> 0x01bc }
            r26 = r2
            int r2 = r19.getSimSlotIndex()     // Catch:{ JSONException -> 0x01c6, Exception -> 0x01bc }
            r27 = r3
            int r3 = r19.getSubscriptionId()     // Catch:{ JSONException -> 0x01b1, Exception -> 0x01a6 }
            r19 = r3
            boolean r3 = r0.isNetworkRoaming(r2)     // Catch:{ JSONException -> 0x01b1, Exception -> 0x01a6 }
            r28 = r0
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ JSONException -> 0x01b1, Exception -> 0x01a6 }
            r29 = r4
            r4 = 23
            if (r0 < r4) goto L_0x00f2
            java.lang.String r0 = r12.getDeviceId(r2)     // Catch:{ JSONException -> 0x00df, Exception -> 0x00cc }
            goto L_0x00f3
        L_0x00cc:
            r0 = move-exception
            r20 = r10
            r30 = r12
            r13 = r17
            r14 = r21
            r15 = r22
            r3 = r25
            r2 = r27
            r1 = r29
            goto L_0x02d6
        L_0x00df:
            r0 = move-exception
            r20 = r10
            r30 = r12
            r13 = r17
            r14 = r21
            r15 = r22
            r3 = r25
            r2 = r27
            r1 = r29
            goto L_0x02e7
        L_0x00f2:
            r0 = 0
        L_0x00f3:
            org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0199, Exception -> 0x018c }
            r4.<init>()     // Catch:{ JSONException -> 0x0199, Exception -> 0x018c }
            r30 = r12
            java.lang.String r12 = r20.toString()     // Catch:{ JSONException -> 0x0188, Exception -> 0x0184 }
            r4.put(r10, r12)     // Catch:{ JSONException -> 0x0188, Exception -> 0x0184 }
            java.lang.String r12 = "displayName"
            r20 = r10
            java.lang.String r10 = r23.toString()     // Catch:{ JSONException -> 0x0182, Exception -> 0x0180 }
            r4.put(r12, r10)     // Catch:{ JSONException -> 0x0182, Exception -> 0x0180 }
            r4.put(r9, r14)     // Catch:{ JSONException -> 0x0182, Exception -> 0x0180 }
            r4.put(r8, r1)     // Catch:{ JSONException -> 0x0182, Exception -> 0x0180 }
            r4.put(r7, r11)     // Catch:{ JSONException -> 0x0182, Exception -> 0x0180 }
            r4.put(r6, r3)     // Catch:{ JSONException -> 0x0182, Exception -> 0x0180 }
            java.lang.String r1 = "isDataRoaming"
            r3 = 1
            if (r15 != r3) goto L_0x011f
            r3 = 1
            goto L_0x0120
        L_0x011f:
            r3 = 0
        L_0x0120:
            r4.put(r1, r3)     // Catch:{ JSONException -> 0x0182, Exception -> 0x0180 }
            java.lang.String r1 = "simSlotIndex"
            r4.put(r1, r2)     // Catch:{ JSONException -> 0x0182, Exception -> 0x0180 }
            r4.put(r5, r13)     // Catch:{ JSONException -> 0x0182, Exception -> 0x0180 }
            r1 = r29
            if (r0 == 0) goto L_0x014d
            r4.put(r1, r0)     // Catch:{ JSONException -> 0x0140, Exception -> 0x0133 }
            goto L_0x014d
        L_0x0133:
            r0 = move-exception
            r13 = r17
            r14 = r21
            r15 = r22
            r3 = r25
            r2 = r27
            goto L_0x02d6
        L_0x0140:
            r0 = move-exception
            r13 = r17
            r14 = r21
            r15 = r22
            r3 = r25
            r2 = r27
            goto L_0x02e7
        L_0x014d:
            r0 = r26
            r2 = r27
            r4.put(r2, r0)     // Catch:{ JSONException -> 0x017d, Exception -> 0x017b }
            java.lang.String r0 = "subscriptionId"
            r3 = r19
            r4.put(r0, r3)     // Catch:{ JSONException -> 0x017d, Exception -> 0x017b }
            r3 = r25
            r3.put(r4)     // Catch:{ JSONException -> 0x0178, Exception -> 0x0175 }
            r4 = r1
            r13 = r3
            r10 = r20
            r14 = r21
            r15 = r22
            r11 = r24
            r0 = r28
            r12 = r30
            r1 = r36
            r3 = r2
            r2 = r39
            goto L_0x0077
        L_0x0175:
            r0 = move-exception
            goto L_0x01e6
        L_0x0178:
            r0 = move-exception
            goto L_0x01f8
        L_0x017b:
            r0 = move-exception
            goto L_0x01c3
        L_0x017d:
            r0 = move-exception
            goto L_0x01cd
        L_0x0180:
            r0 = move-exception
            goto L_0x0191
        L_0x0182:
            r0 = move-exception
            goto L_0x019e
        L_0x0184:
            r0 = move-exception
            r20 = r10
            goto L_0x0191
        L_0x0188:
            r0 = move-exception
            r20 = r10
            goto L_0x019e
        L_0x018c:
            r0 = move-exception
            r20 = r10
            r30 = r12
        L_0x0191:
            r3 = r25
            r2 = r27
            r1 = r29
            goto L_0x01e6
        L_0x0199:
            r0 = move-exception
            r20 = r10
            r30 = r12
        L_0x019e:
            r3 = r25
            r2 = r27
            r1 = r29
            goto L_0x01f8
        L_0x01a6:
            r0 = move-exception
            r1 = r4
            r20 = r10
            r30 = r12
            r3 = r25
            r2 = r27
            goto L_0x01e6
        L_0x01b1:
            r0 = move-exception
            r1 = r4
            r20 = r10
            r30 = r12
            r3 = r25
            r2 = r27
            goto L_0x01f8
        L_0x01bc:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r30 = r12
        L_0x01c3:
            r3 = r25
            goto L_0x01e6
        L_0x01c6:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r30 = r12
        L_0x01cd:
            r3 = r25
            goto L_0x01f8
        L_0x01d0:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            goto L_0x01e3
        L_0x01d6:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            goto L_0x01f5
        L_0x01dc:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
        L_0x01e3:
            r30 = r12
            r3 = r13
        L_0x01e6:
            r13 = r17
            r14 = r21
            r15 = r22
            goto L_0x02d6
        L_0x01ee:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
        L_0x01f5:
            r30 = r12
            r3 = r13
        L_0x01f8:
            r13 = r17
            r14 = r21
            r15 = r22
            goto L_0x02e7
        L_0x0200:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r3 = r13
            r22 = r15
            r13 = r17
            r14 = r21
            goto L_0x02d6
        L_0x0212:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r3 = r13
            r22 = r15
            r13 = r17
            r14 = r21
            goto L_0x02e7
        L_0x0224:
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r3 = r13
            r21 = r14
            r22 = r15
            r13 = r17
            goto L_0x02a8
        L_0x0235:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r3 = r13
            r21 = r14
            r22 = r15
            r13 = r17
            goto L_0x02d6
        L_0x0247:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r3 = r13
            r21 = r14
            r22 = r15
            r13 = r17
            goto L_0x02e7
        L_0x0259:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r21 = r14
            r22 = r15
            r13 = r17
            r3 = 0
            goto L_0x02d6
        L_0x026b:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r21 = r14
            r22 = r15
            r13 = r17
            r3 = 0
            goto L_0x02e7
        L_0x027d:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r17 = r13
            r22 = r15
            r3 = 0
            goto L_0x02b8
        L_0x028c:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r17 = r13
            r22 = r15
            r3 = 0
            goto L_0x02c7
        L_0x029b:
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r22 = r15
            r3 = 0
            r13 = 0
            r14 = 0
        L_0x02a8:
            r15 = r22
            goto L_0x02ea
        L_0x02ab:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r22 = r15
            r3 = 0
            r13 = 0
        L_0x02b8:
            r14 = 0
            goto L_0x02d6
        L_0x02ba:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r22 = r15
            r3 = 0
            r13 = 0
        L_0x02c7:
            r14 = 0
            goto L_0x02e7
        L_0x02c9:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r3 = 0
            r13 = 0
            r14 = 0
            r15 = 0
        L_0x02d6:
            r0.printStackTrace()
            goto L_0x02ea
        L_0x02da:
            r0 = move-exception
            r2 = r3
            r1 = r4
            r20 = r10
            r24 = r11
            r30 = r12
            r3 = 0
            r13 = 0
            r14 = 0
            r15 = 0
        L_0x02e7:
            r0.printStackTrace()
        L_0x02ea:
            java.lang.String r0 = r30.getSimCountryIso()
            java.lang.String r4 = r30.getSimOperator()
            java.lang.String r10 = r30.getSimOperatorName()
            int r11 = r30.getCallState()
            int r12 = r30.getDataActivity()
            r16 = r3
            int r3 = r30.getNetworkType()
            r27 = r2
            int r2 = r30.getPhoneType()
            r29 = r1
            int r1 = r30.getSimState()
            r17 = r5
            boolean r5 = r30.isNetworkRoaming()
            r19 = r13
            r18 = r14
            r13 = r24
            r14 = r36
            boolean r21 = r14.simPermissionGranted(r13)
            if (r21 == 0) goto L_0x0345
            java.lang.String r21 = r30.getLine1Number()
            java.lang.String r22 = r30.getDeviceId()
            java.lang.String r23 = r30.getDeviceSoftwareVersion()
            java.lang.String r24 = r30.getSimSerialNumber()
            java.lang.String r25 = r30.getSubscriberId()
            r31 = r21
            r32 = r22
            r33 = r23
            r34 = r24
            r35 = r25
            r24 = r13
            goto L_0x0351
        L_0x0345:
            r24 = r13
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
        L_0x0351:
            int r13 = r4.length()
            java.lang.String r21 = ""
            r14 = 3
            if (r13 < r14) goto L_0x0367
            r13 = 0
            java.lang.String r21 = r4.substring(r13, r14)
            java.lang.String r4 = r4.substring(r14)
            r13 = r4
            r4 = r21
            goto L_0x036a
        L_0x0367:
            r4 = r21
            r13 = r4
        L_0x036a:
            org.json.JSONObject r14 = new org.json.JSONObject
            r14.<init>()
            r37 = r15
            r15 = r20
            r14.put(r15, r10)
            r14.put(r9, r0)
            r14.put(r8, r4)
            r14.put(r7, r13)
            java.lang.String r0 = "callState"
            r14.put(r0, r11)
            java.lang.String r0 = "dataActivity"
            r14.put(r0, r12)
            java.lang.String r0 = "networkType"
            r14.put(r0, r3)
            java.lang.String r0 = "phoneType"
            r14.put(r0, r2)
            java.lang.String r0 = "simState"
            r14.put(r0, r1)
            r14.put(r6, r5)
            if (r37 == 0) goto L_0x03a6
            int r0 = r37.intValue()
            java.lang.String r1 = "phoneCount"
            r14.put(r1, r0)
        L_0x03a6:
            if (r19 == 0) goto L_0x03b1
            int r0 = r19.intValue()
            java.lang.String r1 = "activeSubscriptionInfoCount"
            r14.put(r1, r0)
        L_0x03b1:
            if (r18 == 0) goto L_0x03bc
            int r0 = r18.intValue()
            java.lang.String r1 = "activeSubscriptionInfoCountMax"
            r14.put(r1, r0)
        L_0x03bc:
            r1 = r36
            r2 = r24
            boolean r0 = r1.simPermissionGranted(r2)
            if (r0 == 0) goto L_0x03e9
            r2 = r17
            r0 = r31
            r14.put(r2, r0)
            r2 = r29
            r0 = r32
            r14.put(r2, r0)
            java.lang.String r0 = "deviceSoftwareVersion"
            r2 = r33
            r14.put(r0, r2)
            r2 = r27
            r0 = r34
            r14.put(r2, r0)
            java.lang.String r0 = "subscriberId"
            r2 = r35
            r14.put(r0, r2)
        L_0x03e9:
            if (r16 == 0) goto L_0x03f8
            int r0 = r16.length()
            if (r0 == 0) goto L_0x03f8
            java.lang.String r0 = "cards"
            r3 = r16
            r14.put(r0, r3)
        L_0x03f8:
            r2 = r39
            r2.success(r14)
            r2 = 1
            return r2
        L_0x03ff:
            r2 = 1
            java.lang.String r3 = "hasReadPermission"
            boolean r3 = r3.equals(r0)
            if (r3 == 0) goto L_0x040c
            r36.hasReadPermission()
            return r2
        L_0x040c:
            java.lang.String r3 = "requestReadPermission"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0418
            r36.requestReadPermission()
            return r2
        L_0x0418:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pbakondy.Sim.execute(java.lang.String, org.json.JSONArray, org.apache.cordova.CallbackContext):boolean");
    }

    private void hasReadPermission() {
        this.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, simPermissionGranted("android.permission.READ_PHONE_STATE")));
    }

    private void requestReadPermission() {
        requestPermission("android.permission.READ_PHONE_STATE");
    }

    private boolean simPermissionGranted(String str) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        return this.cordova.hasPermission(str);
    }

    private void requestPermission(String str) {
        LOG.i(LOG_TAG, "requestPermission");
        if (!simPermissionGranted(str)) {
            this.cordova.requestPermission(this, 12345, str);
        } else {
            this.callback.success();
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) throws JSONException {
        if (iArr.length <= 0 || iArr[0] != 0) {
            this.callback.error("Permission denied");
        } else {
            this.callback.success();
        }
    }
}
