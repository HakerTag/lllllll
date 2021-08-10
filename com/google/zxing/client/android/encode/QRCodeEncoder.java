package com.google.zxing.client.android.encode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.telephony.PhoneNumberUtils;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.common.BitMatrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

final class QRCodeEncoder {
    private static final int BLACK = -16777216;
    private static final String TAG = QRCodeEncoder.class.getSimpleName();
    private static final int WHITE = -1;
    private final Context activity;
    private String contents;
    private final int dimension;
    private String displayContents;
    private BarcodeFormat format;
    private String title;
    private final boolean useVCard;

    QRCodeEncoder(Context context, Intent intent, int i, boolean z) throws WriterException {
        this.activity = context;
        this.dimension = i;
        this.useVCard = z;
        String action = intent.getAction();
        if (Intents.Encode.ACTION.equals(action)) {
            encodeContentsFromZXingIntent(intent);
        } else if ("android.intent.action.SEND".equals(action)) {
            encodeContentsFromShareIntent(intent);
        }
    }

    /* access modifiers changed from: package-private */
    public String getContents() {
        return this.contents;
    }

    /* access modifiers changed from: package-private */
    public String getDisplayContents() {
        return this.displayContents;
    }

    /* access modifiers changed from: package-private */
    public String getTitle() {
        return this.title;
    }

    /* access modifiers changed from: package-private */
    public boolean isUseVCard() {
        return this.useVCard;
    }

    private void encodeContentsFromZXingIntent(Intent intent) {
        String stringExtra = intent.getStringExtra(Intents.Encode.FORMAT);
        this.format = null;
        if (stringExtra != null) {
            try {
                this.format = BarcodeFormat.valueOf(stringExtra);
            } catch (IllegalArgumentException unused) {
            }
        }
        BarcodeFormat barcodeFormat = this.format;
        if (barcodeFormat == null || barcodeFormat == BarcodeFormat.QR_CODE) {
            String stringExtra2 = intent.getStringExtra(Intents.Encode.TYPE);
            if (stringExtra2 != null && !stringExtra2.isEmpty()) {
                this.format = BarcodeFormat.QR_CODE;
                encodeQRCodeContents(intent, stringExtra2);
                return;
            }
            return;
        }
        String stringExtra3 = intent.getStringExtra(Intents.Encode.DATA);
        if (stringExtra3 != null && !stringExtra3.isEmpty()) {
            this.contents = stringExtra3;
            this.displayContents = stringExtra3;
            this.title = this.activity.getString(R.string.contents_text);
        }
    }

    private void encodeContentsFromShareIntent(Intent intent) throws WriterException {
        if (intent.hasExtra("android.intent.extra.STREAM")) {
            encodeFromStreamExtra(intent);
        } else {
            encodeFromTextExtras(intent);
        }
    }

    private void encodeFromTextExtras(Intent intent) throws WriterException {
        String trim = ContactEncoder.trim(intent.getStringExtra("android.intent.extra.TEXT"));
        if (trim == null && (trim = ContactEncoder.trim(intent.getStringExtra(IntentCompat.EXTRA_HTML_TEXT))) == null && (trim = ContactEncoder.trim(intent.getStringExtra("android.intent.extra.SUBJECT"))) == null) {
            String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.EMAIL");
            trim = stringArrayExtra != null ? ContactEncoder.trim(stringArrayExtra[0]) : "?";
        }
        if (trim == null || trim.isEmpty()) {
            throw new WriterException("Empty EXTRA_TEXT");
        }
        this.contents = trim;
        this.format = BarcodeFormat.QR_CODE;
        if (intent.hasExtra("android.intent.extra.SUBJECT")) {
            this.displayContents = intent.getStringExtra("android.intent.extra.SUBJECT");
        } else if (intent.hasExtra("android.intent.extra.TITLE")) {
            this.displayContents = intent.getStringExtra("android.intent.extra.TITLE");
        } else {
            this.displayContents = this.contents;
        }
        this.title = this.activity.getString(R.string.contents_text);
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x00ab A[SYNTHETIC, Splitter:B:41:0x00ab] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void encodeFromStreamExtra(android.content.Intent r7) throws com.google.zxing.WriterException {
        /*
            r6 = this;
            com.google.zxing.BarcodeFormat r0 = com.google.zxing.BarcodeFormat.QR_CODE
            r6.format = r0
            android.os.Bundle r7 = r7.getExtras()
            if (r7 == 0) goto L_0x00b7
            java.lang.String r0 = "android.intent.extra.STREAM"
            android.os.Parcelable r7 = r7.getParcelable(r0)
            android.net.Uri r7 = (android.net.Uri) r7
            if (r7 == 0) goto L_0x00af
            r0 = 0
            android.content.Context r1 = r6.activity     // Catch:{ IOException -> 0x00a2 }
            android.content.ContentResolver r1 = r1.getContentResolver()     // Catch:{ IOException -> 0x00a2 }
            java.io.InputStream r1 = r1.openInputStream(r7)     // Catch:{ IOException -> 0x00a2 }
            if (r1 == 0) goto L_0x0089
            java.io.ByteArrayOutputStream r7 = new java.io.ByteArrayOutputStream     // Catch:{ IOException -> 0x0086, all -> 0x0083 }
            r7.<init>()     // Catch:{ IOException -> 0x0086, all -> 0x0083 }
            r2 = 2048(0x800, float:2.87E-42)
            byte[] r2 = new byte[r2]     // Catch:{ IOException -> 0x0086, all -> 0x0083 }
        L_0x002a:
            int r3 = r1.read(r2)     // Catch:{ IOException -> 0x0086, all -> 0x0083 }
            r4 = 0
            if (r3 <= 0) goto L_0x0035
            r7.write(r2, r4, r3)     // Catch:{ IOException -> 0x0086, all -> 0x0083 }
            goto L_0x002a
        L_0x0035:
            byte[] r7 = r7.toByteArray()     // Catch:{ IOException -> 0x0086, all -> 0x0083 }
            java.lang.String r2 = new java.lang.String     // Catch:{ IOException -> 0x0086, all -> 0x0083 }
            int r3 = r7.length     // Catch:{ IOException -> 0x0086, all -> 0x0083 }
            java.lang.String r5 = "UTF-8"
            r2.<init>(r7, r4, r3, r5)     // Catch:{ IOException -> 0x0086, all -> 0x0083 }
            if (r1 == 0) goto L_0x0048
            r1.close()     // Catch:{ IOException -> 0x0047 }
            goto L_0x0048
        L_0x0047:
        L_0x0048:
            java.lang.String r1 = com.google.zxing.client.android.encode.QRCodeEncoder.TAG
            java.lang.String r3 = "Encoding share intent content:"
            android.util.Log.d(r1, r3)
            java.lang.String r1 = com.google.zxing.client.android.encode.QRCodeEncoder.TAG
            android.util.Log.d(r1, r2)
            com.google.zxing.Result r1 = new com.google.zxing.Result
            com.google.zxing.BarcodeFormat r3 = com.google.zxing.BarcodeFormat.QR_CODE
            r1.<init>(r2, r7, r0, r3)
            com.google.zxing.client.result.ParsedResult r7 = com.google.zxing.client.result.ResultParser.parseResult(r1)
            boolean r0 = r7 instanceof com.google.zxing.client.result.AddressBookParsedResult
            if (r0 == 0) goto L_0x007b
            com.google.zxing.client.result.AddressBookParsedResult r7 = (com.google.zxing.client.result.AddressBookParsedResult) r7
            r6.encodeQRCodeContents(r7)
            java.lang.String r7 = r6.contents
            if (r7 == 0) goto L_0x0073
            boolean r7 = r7.isEmpty()
            if (r7 != 0) goto L_0x0073
            return
        L_0x0073:
            com.google.zxing.WriterException r7 = new com.google.zxing.WriterException
            java.lang.String r0 = "No content to encode"
            r7.<init>(r0)
            throw r7
        L_0x007b:
            com.google.zxing.WriterException r7 = new com.google.zxing.WriterException
            java.lang.String r0 = "Result was not an address"
            r7.<init>(r0)
            throw r7
        L_0x0083:
            r7 = move-exception
            r0 = r1
            goto L_0x00a9
        L_0x0086:
            r7 = move-exception
            r0 = r1
            goto L_0x00a3
        L_0x0089:
            com.google.zxing.WriterException r0 = new com.google.zxing.WriterException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Can't open stream for "
            r2.append(r3)
            r2.append(r7)
            java.lang.String r7 = r2.toString()
            r0.<init>(r7)
            throw r0
        L_0x00a0:
            r7 = move-exception
            goto L_0x00a9
        L_0x00a2:
            r7 = move-exception
        L_0x00a3:
            com.google.zxing.WriterException r1 = new com.google.zxing.WriterException     // Catch:{ all -> 0x00a0 }
            r1.<init>(r7)     // Catch:{ all -> 0x00a0 }
            throw r1     // Catch:{ all -> 0x00a0 }
        L_0x00a9:
            if (r0 == 0) goto L_0x00ae
            r0.close()     // Catch:{ IOException -> 0x00ae }
        L_0x00ae:
            throw r7
        L_0x00af:
            com.google.zxing.WriterException r7 = new com.google.zxing.WriterException
            java.lang.String r0 = "No EXTRA_STREAM"
            r7.<init>(r0)
            throw r7
        L_0x00b7:
            com.google.zxing.WriterException r7 = new com.google.zxing.WriterException
            java.lang.String r0 = "No extras"
            r7.<init>(r0)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.android.encode.QRCodeEncoder.encodeFromStreamExtra(android.content.Intent):void");
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    private void encodeQRCodeContents(Intent intent, String str) {
        char c;
        List<String> list;
        Bundle bundleExtra;
        switch (str.hashCode()) {
            case -1309271157:
                if (str.equals(Contents.Type.PHONE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -670199783:
                if (str.equals(Contents.Type.CONTACT)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 709220992:
                if (str.equals(Contents.Type.SMS)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1349204356:
                if (str.equals(Contents.Type.LOCATION)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1778595596:
                if (str.equals(Contents.Type.TEXT)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1833351709:
                if (str.equals(Contents.Type.EMAIL)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            String stringExtra = intent.getStringExtra(Intents.Encode.DATA);
            if (stringExtra != null && !stringExtra.isEmpty()) {
                this.contents = stringExtra;
                this.displayContents = stringExtra;
                this.title = this.activity.getString(R.string.contents_text);
            }
        } else if (c == 1) {
            String trim = ContactEncoder.trim(intent.getStringExtra(Intents.Encode.DATA));
            if (trim != null) {
                this.contents = "mailto:" + trim;
                this.displayContents = trim;
                this.title = this.activity.getString(R.string.contents_email);
            }
        } else if (c == 2) {
            String trim2 = ContactEncoder.trim(intent.getStringExtra(Intents.Encode.DATA));
            if (trim2 != null) {
                this.contents = "tel:" + trim2;
                this.displayContents = PhoneNumberUtils.formatNumber(trim2);
                this.title = this.activity.getString(R.string.contents_phone);
            }
        } else if (c == 3) {
            String trim3 = ContactEncoder.trim(intent.getStringExtra(Intents.Encode.DATA));
            if (trim3 != null) {
                this.contents = "sms:" + trim3;
                this.displayContents = PhoneNumberUtils.formatNumber(trim3);
                this.title = this.activity.getString(R.string.contents_sms);
            }
        } else if (c == 4) {
            Bundle bundleExtra2 = intent.getBundleExtra(Intents.Encode.DATA);
            if (bundleExtra2 != null) {
                String string = bundleExtra2.getString("name");
                String string2 = bundleExtra2.getString("company");
                String string3 = bundleExtra2.getString("postal");
                List<String> allBundleValues = getAllBundleValues(bundleExtra2, Contents.PHONE_KEYS);
                List<String> allBundleValues2 = getAllBundleValues(bundleExtra2, Contents.PHONE_TYPE_KEYS);
                List<String> allBundleValues3 = getAllBundleValues(bundleExtra2, Contents.EMAIL_KEYS);
                String string4 = bundleExtra2.getString(Contents.URL_KEY);
                if (string4 == null) {
                    list = null;
                } else {
                    list = Collections.singletonList(string4);
                }
                String[] encode = (this.useVCard ? new VCardContactEncoder() : new MECARDContactEncoder()).encode(Collections.singletonList(string), string2, Collections.singletonList(string3), allBundleValues, allBundleValues2, allBundleValues3, list, bundleExtra2.getString(Contents.NOTE_KEY));
                if (!encode[1].isEmpty()) {
                    this.contents = encode[0];
                    this.displayContents = encode[1];
                    this.title = this.activity.getString(R.string.contents_contact);
                }
            }
        } else if (c == 5 && (bundleExtra = intent.getBundleExtra(Intents.Encode.DATA)) != null) {
            float f = bundleExtra.getFloat("LAT", Float.MAX_VALUE);
            float f2 = bundleExtra.getFloat("LONG", Float.MAX_VALUE);
            if (f != Float.MAX_VALUE && f2 != Float.MAX_VALUE) {
                this.contents = "geo:" + f + ',' + f2;
                StringBuilder sb = new StringBuilder();
                sb.append(f);
                sb.append(",");
                sb.append(f2);
                this.displayContents = sb.toString();
                this.title = this.activity.getString(R.string.contents_location);
            }
        }
    }

    private static List<String> getAllBundleValues(Bundle bundle, String[] strArr) {
        String str;
        ArrayList arrayList = new ArrayList(strArr.length);
        for (String str2 : strArr) {
            Object obj = bundle.get(str2);
            if (obj == null) {
                str = null;
            } else {
                str = obj.toString();
            }
            arrayList.add(str);
        }
        return arrayList;
    }

    private void encodeQRCodeContents(AddressBookParsedResult addressBookParsedResult) {
        String[] encode = (this.useVCard ? new VCardContactEncoder() : new MECARDContactEncoder()).encode(toList(addressBookParsedResult.getNames()), addressBookParsedResult.getOrg(), toList(addressBookParsedResult.getAddresses()), toList(addressBookParsedResult.getPhoneNumbers()), null, toList(addressBookParsedResult.getEmails()), toList(addressBookParsedResult.getURLs()), null);
        if (!encode[1].isEmpty()) {
            this.contents = encode[0];
            this.displayContents = encode[1];
            this.title = this.activity.getString(R.string.contents_contact);
        }
    }

    private static List<String> toList(String[] strArr) {
        if (strArr == null) {
            return null;
        }
        return Arrays.asList(strArr);
    }

    /* access modifiers changed from: package-private */
    public Bitmap encodeAsBitmap() throws WriterException {
        EnumMap enumMap;
        String str = this.contents;
        if (str == null) {
            return null;
        }
        String guessAppropriateEncoding = guessAppropriateEncoding(str);
        if (guessAppropriateEncoding != null) {
            EnumMap enumMap2 = new EnumMap(EncodeHintType.class);
            enumMap2.put((Object) EncodeHintType.CHARACTER_SET, (Object) guessAppropriateEncoding);
            enumMap = enumMap2;
        } else {
            enumMap = null;
        }
        try {
            BitMatrix encode = new MultiFormatWriter().encode(str, this.format, this.dimension, this.dimension, enumMap);
            int width = encode.getWidth();
            int height = encode.getHeight();
            int[] iArr = new int[(width * height)];
            for (int i = 0; i < height; i++) {
                int i2 = i * width;
                for (int i3 = 0; i3 < width; i3++) {
                    iArr[i2 + i3] = encode.get(i3, i) ? -16777216 : -1;
                }
            }
            Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            createBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
            return createBitmap;
        } catch (IllegalArgumentException unused) {
            return null;
        }
    }

    private static String guessAppropriateEncoding(CharSequence charSequence) {
        for (int i = 0; i < charSequence.length(); i++) {
            if (charSequence.charAt(i) > 255) {
                return "UTF-8";
            }
        }
        return null;
    }
}
