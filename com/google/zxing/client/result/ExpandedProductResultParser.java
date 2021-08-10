package com.google.zxing.client.result;

public final class ExpandedProductResultParser extends ResultParser {
    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x0209, code lost:
        if (r3.equals("10") != false) goto L_0x0221;
     */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0224  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x022b  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0248  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0255  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0265  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0275  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x027b  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0287  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x028d  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0293  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0299  */
    @Override // com.google.zxing.client.result.ResultParser
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.zxing.client.result.ExpandedProductParsedResult parse(com.google.zxing.Result r35) {
        /*
            r34 = this;
            com.google.zxing.BarcodeFormat r0 = r35.getBarcodeFormat()
            com.google.zxing.BarcodeFormat r1 = com.google.zxing.BarcodeFormat.RSS_EXPANDED
            r2 = 0
            if (r0 == r1) goto L_0x000a
            return r2
        L_0x000a:
            java.lang.String r1 = getMassagedText(r35)
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            java.util.HashMap r16 = new java.util.HashMap
            r16.<init>()
            r19 = r16
            r16 = 0
            r20 = r3
            r21 = r4
            r22 = r5
            r23 = r6
            r24 = r7
            r25 = r8
            r26 = r9
            r27 = r10
            r28 = r11
            r29 = r12
            r30 = r13
            r31 = r14
            r32 = r15
            r15 = r16
        L_0x0040:
            int r3 = r1.length()
            if (r15 >= r3) goto L_0x02a2
            java.lang.String r3 = findAIvalue(r15, r1)
            if (r3 != 0) goto L_0x004d
            return r2
        L_0x004d:
            int r4 = r3.length()
            r5 = 2
            int r4 = r4 + r5
            int r15 = r15 + r4
            java.lang.String r4 = findValue(r15, r1)
            int r6 = r4.length()
            int r15 = r15 + r6
            r6 = -1
            int r7 = r3.hashCode()
            r8 = 1536(0x600, float:2.152E-42)
            r9 = 0
            r10 = 4
            r11 = 3
            if (r7 == r8) goto L_0x0216
            r8 = 1537(0x601, float:2.154E-42)
            if (r7 == r8) goto L_0x020c
            r8 = 1567(0x61f, float:2.196E-42)
            if (r7 == r8) goto L_0x0203
            r5 = 1568(0x620, float:2.197E-42)
            if (r7 == r5) goto L_0x01f9
            r5 = 1570(0x622, float:2.2E-42)
            if (r7 == r5) goto L_0x01ef
            r5 = 1572(0x624, float:2.203E-42)
            if (r7 == r5) goto L_0x01e5
            r5 = 1574(0x626, float:2.206E-42)
            if (r7 == r5) goto L_0x01db
            switch(r7) {
                case 1567966: goto L_0x01d1;
                case 1567967: goto L_0x01c6;
                case 1567968: goto L_0x01bb;
                case 1567969: goto L_0x01af;
                case 1567970: goto L_0x01a3;
                case 1567971: goto L_0x0197;
                case 1567972: goto L_0x018b;
                case 1567973: goto L_0x017f;
                case 1567974: goto L_0x0173;
                case 1567975: goto L_0x0167;
                default: goto L_0x0084;
            }
        L_0x0084:
            switch(r7) {
                case 1568927: goto L_0x015b;
                case 1568928: goto L_0x014f;
                case 1568929: goto L_0x0143;
                case 1568930: goto L_0x0137;
                case 1568931: goto L_0x012b;
                case 1568932: goto L_0x011f;
                case 1568933: goto L_0x0113;
                case 1568934: goto L_0x0107;
                case 1568935: goto L_0x00fb;
                case 1568936: goto L_0x00ef;
                default: goto L_0x0087;
            }
        L_0x0087:
            switch(r7) {
                case 1575716: goto L_0x00e3;
                case 1575717: goto L_0x00d7;
                case 1575718: goto L_0x00cb;
                case 1575719: goto L_0x00bf;
                default: goto L_0x008a;
            }
        L_0x008a:
            switch(r7) {
                case 1575747: goto L_0x00b3;
                case 1575748: goto L_0x00a7;
                case 1575749: goto L_0x009b;
                case 1575750: goto L_0x008f;
                default: goto L_0x008d;
            }
        L_0x008d:
            goto L_0x0220
        L_0x008f:
            java.lang.String r5 = "3933"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 34
            goto L_0x0221
        L_0x009b:
            java.lang.String r5 = "3932"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 33
            goto L_0x0221
        L_0x00a7:
            java.lang.String r5 = "3931"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 32
            goto L_0x0221
        L_0x00b3:
            java.lang.String r5 = "3930"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 31
            goto L_0x0221
        L_0x00bf:
            java.lang.String r5 = "3923"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 30
            goto L_0x0221
        L_0x00cb:
            java.lang.String r5 = "3922"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 29
            goto L_0x0221
        L_0x00d7:
            java.lang.String r5 = "3921"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 28
            goto L_0x0221
        L_0x00e3:
            java.lang.String r5 = "3920"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 27
            goto L_0x0221
        L_0x00ef:
            java.lang.String r5 = "3209"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 26
            goto L_0x0221
        L_0x00fb:
            java.lang.String r5 = "3208"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 25
            goto L_0x0221
        L_0x0107:
            java.lang.String r5 = "3207"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 24
            goto L_0x0221
        L_0x0113:
            java.lang.String r5 = "3206"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 23
            goto L_0x0221
        L_0x011f:
            java.lang.String r5 = "3205"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 22
            goto L_0x0221
        L_0x012b:
            java.lang.String r5 = "3204"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 21
            goto L_0x0221
        L_0x0137:
            java.lang.String r5 = "3203"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 20
            goto L_0x0221
        L_0x0143:
            java.lang.String r5 = "3202"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 19
            goto L_0x0221
        L_0x014f:
            java.lang.String r5 = "3201"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 18
            goto L_0x0221
        L_0x015b:
            java.lang.String r5 = "3200"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 17
            goto L_0x0221
        L_0x0167:
            java.lang.String r5 = "3109"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 16
            goto L_0x0221
        L_0x0173:
            java.lang.String r5 = "3108"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 15
            goto L_0x0221
        L_0x017f:
            java.lang.String r5 = "3107"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 14
            goto L_0x0221
        L_0x018b:
            java.lang.String r5 = "3106"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 13
            goto L_0x0221
        L_0x0197:
            java.lang.String r5 = "3105"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 12
            goto L_0x0221
        L_0x01a3:
            java.lang.String r5 = "3104"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 11
            goto L_0x0221
        L_0x01af:
            java.lang.String r5 = "3103"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 10
            goto L_0x0221
        L_0x01bb:
            java.lang.String r5 = "3102"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 9
            goto L_0x0221
        L_0x01c6:
            java.lang.String r5 = "3101"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 8
            goto L_0x0221
        L_0x01d1:
            java.lang.String r5 = "3100"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 7
            goto L_0x0221
        L_0x01db:
            java.lang.String r5 = "17"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 6
            goto L_0x0221
        L_0x01e5:
            java.lang.String r5 = "15"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 5
            goto L_0x0221
        L_0x01ef:
            java.lang.String r5 = "13"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 4
            goto L_0x0221
        L_0x01f9:
            java.lang.String r5 = "11"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 3
            goto L_0x0221
        L_0x0203:
            java.lang.String r7 = "10"
            boolean r7 = r3.equals(r7)
            if (r7 == 0) goto L_0x008d
            goto L_0x0221
        L_0x020c:
            java.lang.String r5 = "01"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 1
            goto L_0x0221
        L_0x0216:
            java.lang.String r5 = "00"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 0
            goto L_0x0221
        L_0x0220:
            r5 = -1
        L_0x0221:
            switch(r5) {
                case 0: goto L_0x0299;
                case 1: goto L_0x0293;
                case 2: goto L_0x028d;
                case 3: goto L_0x0287;
                case 4: goto L_0x0281;
                case 5: goto L_0x027b;
                case 6: goto L_0x0275;
                case 7: goto L_0x0265;
                case 8: goto L_0x0265;
                case 9: goto L_0x0265;
                case 10: goto L_0x0265;
                case 11: goto L_0x0265;
                case 12: goto L_0x0265;
                case 13: goto L_0x0265;
                case 14: goto L_0x0265;
                case 15: goto L_0x0265;
                case 16: goto L_0x0265;
                case 17: goto L_0x0255;
                case 18: goto L_0x0255;
                case 19: goto L_0x0255;
                case 20: goto L_0x0255;
                case 21: goto L_0x0255;
                case 22: goto L_0x0255;
                case 23: goto L_0x0255;
                case 24: goto L_0x0255;
                case 25: goto L_0x0255;
                case 26: goto L_0x0255;
                case 27: goto L_0x0248;
                case 28: goto L_0x0248;
                case 29: goto L_0x0248;
                case 30: goto L_0x0248;
                case 31: goto L_0x022b;
                case 32: goto L_0x022b;
                case 33: goto L_0x022b;
                case 34: goto L_0x022b;
                default: goto L_0x0224;
            }
        L_0x0224:
            r14 = r19
            r14.put(r3, r4)
            goto L_0x029e
        L_0x022b:
            int r5 = r4.length()
            if (r5 >= r10) goto L_0x0232
            return r2
        L_0x0232:
            java.lang.String r5 = r4.substring(r11)
            java.lang.String r6 = r4.substring(r9, r11)
            java.lang.String r7 = r3.substring(r11)
            r30 = r5
            r32 = r6
            r31 = r7
            r14 = r19
            goto L_0x029e
        L_0x0248:
            r5 = r4
            java.lang.String r6 = r3.substring(r11)
            r30 = r5
            r31 = r6
            r14 = r19
            goto L_0x029e
        L_0x0255:
            r5 = r4
            java.lang.String r6 = "LB"
            java.lang.String r7 = r3.substring(r11)
            r27 = r5
            r28 = r6
            r29 = r7
            r14 = r19
            goto L_0x029e
        L_0x0265:
            r5 = r4
            java.lang.String r6 = "KG"
            java.lang.String r7 = r3.substring(r11)
            r27 = r5
            r28 = r6
            r29 = r7
            r14 = r19
            goto L_0x029e
        L_0x0275:
            r5 = r4
            r26 = r5
            r14 = r19
            goto L_0x029e
        L_0x027b:
            r5 = r4
            r25 = r5
            r14 = r19
            goto L_0x029e
        L_0x0281:
            r5 = r4
            r24 = r5
            r14 = r19
            goto L_0x029e
        L_0x0287:
            r5 = r4
            r23 = r5
            r14 = r19
            goto L_0x029e
        L_0x028d:
            r5 = r4
            r22 = r5
            r14 = r19
            goto L_0x029e
        L_0x0293:
            r5 = r4
            r20 = r5
            r14 = r19
            goto L_0x029e
        L_0x0299:
            r5 = r4
            r21 = r5
            r14 = r19
        L_0x029e:
            r19 = r14
            goto L_0x0040
        L_0x02a2:
            com.google.zxing.client.result.ExpandedProductParsedResult r2 = new com.google.zxing.client.result.ExpandedProductParsedResult
            r3 = r2
            r4 = r1
            r5 = r20
            r6 = r21
            r7 = r22
            r8 = r23
            r9 = r24
            r10 = r25
            r11 = r26
            r12 = r27
            r13 = r28
            r14 = r29
            r33 = r15
            r15 = r30
            r16 = r31
            r17 = r32
            r18 = r19
            r3.<init>(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            return r2
            switch-data {1567966->0x01d1, 1567967->0x01c6, 1567968->0x01bb, 1567969->0x01af, 1567970->0x01a3, 1567971->0x0197, 1567972->0x018b, 1567973->0x017f, 1567974->0x0173, 1567975->0x0167, }
            switch-data {1568927->0x015b, 1568928->0x014f, 1568929->0x0143, 1568930->0x0137, 1568931->0x012b, 1568932->0x011f, 1568933->0x0113, 1568934->0x0107, 1568935->0x00fb, 1568936->0x00ef, }
            switch-data {1575716->0x00e3, 1575717->0x00d7, 1575718->0x00cb, 1575719->0x00bf, }
            switch-data {1575747->0x00b3, 1575748->0x00a7, 1575749->0x009b, 1575750->0x008f, }
            switch-data {0->0x0299, 1->0x0293, 2->0x028d, 3->0x0287, 4->0x0281, 5->0x027b, 6->0x0275, 7->0x0265, 8->0x0265, 9->0x0265, 10->0x0265, 11->0x0265, 12->0x0265, 13->0x0265, 14->0x0265, 15->0x0265, 16->0x0265, 17->0x0255, 18->0x0255, 19->0x0255, 20->0x0255, 21->0x0255, 22->0x0255, 23->0x0255, 24->0x0255, 25->0x0255, 26->0x0255, 27->0x0248, 28->0x0248, 29->0x0248, 30->0x0248, 31->0x022b, 32->0x022b, 33->0x022b, 34->0x022b, }
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.result.ExpandedProductResultParser.parse(com.google.zxing.Result):com.google.zxing.client.result.ExpandedProductParsedResult");
    }

    private static String findAIvalue(int i, String rawText) {
        if (rawText.charAt(i) != '(') {
            return null;
        }
        CharSequence rawTextAux = rawText.substring(i + 1);
        StringBuilder buf = new StringBuilder();
        for (int index = 0; index < rawTextAux.length(); index++) {
            char currentChar = rawTextAux.charAt(index);
            if (currentChar == ')') {
                return buf.toString();
            }
            if (currentChar < '0' || currentChar > '9') {
                return null;
            }
            buf.append(currentChar);
        }
        return buf.toString();
    }

    private static String findValue(int i, String rawText) {
        StringBuilder buf = new StringBuilder();
        String rawTextAux = rawText.substring(i);
        for (int index = 0; index < rawTextAux.length(); index++) {
            char c = rawTextAux.charAt(index);
            if (c == '(') {
                if (findAIvalue(index, rawTextAux) != null) {
                    break;
                }
                buf.append('(');
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
