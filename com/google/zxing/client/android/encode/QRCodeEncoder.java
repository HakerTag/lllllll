package com.google.zxing.client.android.encode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.common.BitMatrix;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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

    QRCodeEncoder(Context activity2, Intent intent, int dimension2, boolean useVCard2) throws WriterException {
        this.activity = activity2;
        this.dimension = dimension2;
        this.useVCard = useVCard2;
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
        String formatString = intent.getStringExtra(Intents.Encode.FORMAT);
        this.format = null;
        if (formatString != null) {
            try {
                this.format = BarcodeFormat.valueOf(formatString);
            } catch (IllegalArgumentException e) {
            }
        }
        BarcodeFormat barcodeFormat = this.format;
        if (barcodeFormat == null || barcodeFormat == BarcodeFormat.QR_CODE) {
            String type = intent.getStringExtra(Intents.Encode.TYPE);
            if (type != null && !type.isEmpty()) {
                this.format = BarcodeFormat.QR_CODE;
                encodeQRCodeContents(intent, type);
                return;
            }
            return;
        }
        String data = intent.getStringExtra(Intents.Encode.DATA);
        if (data != null && !data.isEmpty()) {
            this.contents = data;
            this.displayContents = data;
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
        String theContents = ContactEncoder.trim(intent.getStringExtra("android.intent.extra.TEXT"));
        if (theContents == null && (theContents = ContactEncoder.trim(intent.getStringExtra(IntentCompat.EXTRA_HTML_TEXT))) == null && (theContents = ContactEncoder.trim(intent.getStringExtra("android.intent.extra.SUBJECT"))) == null) {
            String[] emails = intent.getStringArrayExtra("android.intent.extra.EMAIL");
            if (emails != null) {
                theContents = ContactEncoder.trim(emails[0]);
            } else {
                theContents = "?";
            }
        }
        if (theContents == null || theContents.isEmpty()) {
            throw new WriterException("Empty EXTRA_TEXT");
        }
        this.contents = theContents;
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

    private void encodeFromStreamExtra(Intent intent) throws WriterException {
        this.format = BarcodeFormat.QR_CODE;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Uri uri = (Uri) bundle.getParcelable("android.intent.extra.STREAM");
            if (uri != null) {
                InputStream stream = null;
                try {
                    InputStream stream2 = this.activity.getContentResolver().openInputStream(uri);
                    if (stream2 != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[2048];
                        while (true) {
                            int bytesRead = stream2.read(buffer);
                            if (bytesRead <= 0) {
                                break;
                            }
                            baos.write(buffer, 0, bytesRead);
                        }
                        byte[] vcard = baos.toByteArray();
                        String vcardString = new String(vcard, 0, vcard.length, "UTF-8");
                        if (stream2 != null) {
                            try {
                                stream2.close();
                            } catch (IOException e) {
                            }
                        }
                        Log.d(TAG, "Encoding share intent content:");
                        Log.d(TAG, vcardString);
                        ParsedResult parsedResult = ResultParser.parseResult(new Result(vcardString, vcard, null, BarcodeFormat.QR_CODE));
                        if (parsedResult instanceof AddressBookParsedResult) {
                            encodeQRCodeContents((AddressBookParsedResult) parsedResult);
                            String str = this.contents;
                            if (str == null || str.isEmpty()) {
                                throw new WriterException("No content to encode");
                            }
                            return;
                        }
                        throw new WriterException("Result was not an address");
                    }
                    throw new WriterException("Can't open stream for " + uri);
                } catch (IOException ioe) {
                    throw new WriterException(ioe);
                } catch (Throwable th) {
                    if (0 != 0) {
                        try {
                            stream.close();
                        } catch (IOException e2) {
                        }
                    }
                    throw th;
                }
            } else {
                throw new WriterException("No EXTRA_STREAM");
            }
        } else {
            throw new WriterException("No extras");
        }
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    private void encodeQRCodeContents(Intent intent, String type) {
        char c;
        Bundle locationBundle;
        switch (type.hashCode()) {
            case -1309271157:
                if (type.equals(Contents.Type.PHONE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -670199783:
                if (type.equals(Contents.Type.CONTACT)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 709220992:
                if (type.equals(Contents.Type.SMS)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1349204356:
                if (type.equals(Contents.Type.LOCATION)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1778595596:
                if (type.equals(Contents.Type.TEXT)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1833351709:
                if (type.equals(Contents.Type.EMAIL)) {
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
            String textData = intent.getStringExtra(Intents.Encode.DATA);
            if (textData != null && !textData.isEmpty()) {
                this.contents = textData;
                this.displayContents = textData;
                this.title = this.activity.getString(R.string.contents_text);
            }
        } else if (c == 1) {
            String emailData = ContactEncoder.trim(intent.getStringExtra(Intents.Encode.DATA));
            if (emailData != null) {
                this.contents = "mailto:" + emailData;
                this.displayContents = emailData;
                this.title = this.activity.getString(R.string.contents_email);
            }
        } else if (c == 2) {
            String phoneData = ContactEncoder.trim(intent.getStringExtra(Intents.Encode.DATA));
            if (phoneData != null) {
                this.contents = "tel:" + phoneData;
                this.displayContents = PhoneNumberUtils.formatNumber(phoneData);
                this.title = this.activity.getString(R.string.contents_phone);
            }
        } else if (c == 3) {
            String smsData = ContactEncoder.trim(intent.getStringExtra(Intents.Encode.DATA));
            if (smsData != null) {
                this.contents = "sms:" + smsData;
                this.displayContents = PhoneNumberUtils.formatNumber(smsData);
                this.title = this.activity.getString(R.string.contents_sms);
            }
        } else if (c == 4) {
            Bundle contactBundle = intent.getBundleExtra(Intents.Encode.DATA);
            if (contactBundle != null) {
                String name = contactBundle.getString("name");
                String organization = contactBundle.getString("company");
                String address = contactBundle.getString("postal");
                List<String> phones = getAllBundleValues(contactBundle, Contents.PHONE_KEYS);
                List<String> phoneTypes = getAllBundleValues(contactBundle, Contents.PHONE_TYPE_KEYS);
                List<String> emails = getAllBundleValues(contactBundle, Contents.EMAIL_KEYS);
                String url = contactBundle.getString(Contents.URL_KEY);
                String[] encoded = (this.useVCard ? new VCardContactEncoder() : new MECARDContactEncoder()).encode(Collections.singletonList(name), organization, Collections.singletonList(address), phones, phoneTypes, emails, url == null ? null : Collections.singletonList(url), contactBundle.getString(Contents.NOTE_KEY));
                if (!encoded[1].isEmpty()) {
                    this.contents = encoded[0];
                    this.displayContents = encoded[1];
                    this.title = this.activity.getString(R.string.contents_contact);
                }
            }
        } else if (c == 5 && (locationBundle = intent.getBundleExtra(Intents.Encode.DATA)) != null) {
            float latitude = locationBundle.getFloat("LAT", Float.MAX_VALUE);
            float longitude = locationBundle.getFloat("LONG", Float.MAX_VALUE);
            if (latitude != Float.MAX_VALUE && longitude != Float.MAX_VALUE) {
                this.contents = "geo:" + latitude + ',' + longitude;
                StringBuilder sb = new StringBuilder();
                sb.append(latitude);
                sb.append(",");
                sb.append(longitude);
                this.displayContents = sb.toString();
                this.title = this.activity.getString(R.string.contents_location);
            }
        }
    }

    private static List<String> getAllBundleValues(Bundle bundle, String[] keys) {
        List<String> values = new ArrayList<>(keys.length);
        for (String key : keys) {
            Object value = bundle.get(key);
            values.add(value == null ? null : value.toString());
        }
        return values;
    }

    private void encodeQRCodeContents(AddressBookParsedResult contact) {
        String[] encoded = (this.useVCard ? new VCardContactEncoder() : new MECARDContactEncoder()).encode(toList(contact.getNames()), contact.getOrg(), toList(contact.getAddresses()), toList(contact.getPhoneNumbers()), null, toList(contact.getEmails()), toList(contact.getURLs()), null);
        if (!encoded[1].isEmpty()) {
            this.contents = encoded[0];
            this.displayContents = encoded[1];
            this.title = this.activity.getString(R.string.contents_contact);
        }
    }

    private static List<String> toList(String[] values) {
        if (values == null) {
            return null;
        }
        return Arrays.asList(values);
    }

    /* access modifiers changed from: package-private */
    public Bitmap encodeAsBitmap() throws WriterException {
        Map<EncodeHintType, Object> hints;
        String contentsToEncode = this.contents;
        if (contentsToEncode == null) {
            return null;
        }
        Object encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            Map<EncodeHintType, Object> hints2 = new EnumMap<>(EncodeHintType.class);
            hints2.put(EncodeHintType.CHARACTER_SET, encoding);
            hints = hints2;
        } else {
            hints = null;
        }
        try {
            BitMatrix result = new MultiFormatWriter().encode(contentsToEncode, this.format, this.dimension, this.dimension, hints);
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[(width * height)];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? -16777216 : -1;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String guessAppropriateEncoding(CharSequence contents2) {
        for (int i = 0; i < contents2.length(); i++) {
            if (contents2.charAt(i) > 255) {
                return "UTF-8";
            }
        }
        return null;
    }
}
