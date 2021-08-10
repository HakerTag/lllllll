package com.google.zxing.client.android.result;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.Result;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.LocaleManager;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.android.book.SearchBookContentsActivity;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ResultParser;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public abstract class ResultHandler {
    private static final String[] ADDRESS_TYPE_STRINGS = {"home", "work"};
    private static final int[] ADDRESS_TYPE_VALUES = {1, 2};
    private static final String[] EMAIL_TYPE_STRINGS = {"home", "work", "mobile"};
    private static final int[] EMAIL_TYPE_VALUES = {1, 2, 4};
    public static final int MAX_BUTTON_COUNT = 4;
    private static final int NO_TYPE = -1;
    private static final String[] PHONE_TYPE_STRINGS = {"home", "work", "mobile", "fax", "pager", "main"};
    private static final int[] PHONE_TYPE_VALUES = {1, 3, 2, 4, 6, 12};
    private static final String TAG = ResultHandler.class.getSimpleName();
    private final Activity activity;
    private final String customProductSearch;
    private final Result rawResult;
    private final ParsedResult result;

    public abstract int getButtonCount();

    public abstract int getButtonText(int i);

    public abstract int getDisplayTitle();

    public abstract void handleButtonPress(int i);

    ResultHandler(Activity activity2, ParsedResult result2) {
        this(activity2, result2, null);
    }

    ResultHandler(Activity activity2, ParsedResult result2, Result rawResult2) {
        this.result = result2;
        this.activity = activity2;
        this.rawResult = rawResult2;
        this.customProductSearch = parseCustomSearchURL();
    }

    public final ParsedResult getResult() {
        return this.result;
    }

    /* access modifiers changed from: package-private */
    public final boolean hasCustomProductSearch() {
        return this.customProductSearch != null;
    }

    /* access modifiers changed from: package-private */
    public final Activity getActivity() {
        return this.activity;
    }

    public Integer getDefaultButtonID() {
        return null;
    }

    public boolean areContentsSecure() {
        return false;
    }

    public CharSequence getDisplayContents() {
        return this.result.getDisplayResult().replace("\r", "");
    }

    public final ParsedResultType getType() {
        return this.result.getType();
    }

    /* access modifiers changed from: package-private */
    public final void addPhoneOnlyContact(String[] phoneNumbers, String[] phoneTypes) {
        addContact(null, null, null, phoneNumbers, phoneTypes, null, null, null, null, null, null, null, null, null, null, null);
    }

    /* access modifiers changed from: package-private */
    public final void addEmailOnlyContact(String[] emails, String[] emailTypes) {
        addContact(null, null, null, null, null, emails, emailTypes, null, null, null, null, null, null, null, null, null);
    }

    /* access modifiers changed from: package-private */
    public final void addContact(String[] names, String[] nicknames, String pronunciation, String[] phoneNumbers, String[] phoneTypes, String[] emails, String[] emailTypes, String note, String instantMessenger, String address, String addressType, String org2, String title, String[] urls, String birthday, String[] geo) {
        int i;
        int type;
        int type2;
        int type3;
        Intent intent = new Intent("android.intent.action.INSERT_OR_EDIT", ContactsContract.Contacts.CONTENT_URI);
        intent.setType("vnd.android.cursor.item/contact");
        putExtra(intent, "name", names != null ? names[0] : null);
        putExtra(intent, "phonetic_name", pronunciation);
        int phoneCount = Math.min(phoneNumbers != null ? phoneNumbers.length : 0, Contents.PHONE_KEYS.length);
        for (int x = 0; x < phoneCount; x++) {
            putExtra(intent, Contents.PHONE_KEYS[x], phoneNumbers[x]);
            if (phoneTypes != null && x < phoneTypes.length && (type3 = toPhoneContractType(phoneTypes[x])) >= 0) {
                intent.putExtra(Contents.PHONE_TYPE_KEYS[x], type3);
            }
        }
        int emailCount = Math.min(emails != null ? emails.length : 0, Contents.EMAIL_KEYS.length);
        for (int x2 = 0; x2 < emailCount; x2++) {
            putExtra(intent, Contents.EMAIL_KEYS[x2], emails[x2]);
            if (emailTypes != null && x2 < emailTypes.length && (type2 = toEmailContractType(emailTypes[x2])) >= 0) {
                intent.putExtra(Contents.EMAIL_TYPE_KEYS[x2], type2);
            }
        }
        ArrayList<ContentValues> data = new ArrayList<>();
        if (urls != null) {
            int length = urls.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                String url = urls[i2];
                if (!(url == null || url.isEmpty())) {
                    ContentValues row = new ContentValues(2);
                    row.put("mimetype", "vnd.android.cursor.item/website");
                    row.put("data1", url);
                    data.add(row);
                    break;
                }
                i2++;
            }
        }
        if (birthday != null) {
            ContentValues row2 = new ContentValues(3);
            row2.put("mimetype", "vnd.android.cursor.item/contact_event");
            row2.put("data2", (Integer) 3);
            row2.put("data1", birthday);
            data.add(row2);
        }
        if (nicknames != null) {
            int length2 = nicknames.length;
            int i3 = 0;
            while (true) {
                if (i3 >= length2) {
                    break;
                }
                String nickname = nicknames[i3];
                if (!(nickname == null || nickname.isEmpty())) {
                    ContentValues row3 = new ContentValues(3);
                    row3.put("mimetype", "vnd.android.cursor.item/nickname");
                    row3.put("data2", (Integer) 1);
                    row3.put("data1", nickname);
                    data.add(row3);
                    break;
                }
                i3++;
            }
        }
        if (!data.isEmpty()) {
            intent.putParcelableArrayListExtra("data", data);
        }
        StringBuilder aggregatedNotes = new StringBuilder();
        if (note != null) {
            aggregatedNotes.append('\n');
            aggregatedNotes.append(note);
        }
        if (geo != null) {
            aggregatedNotes.append('\n');
            aggregatedNotes.append(geo[0]);
            aggregatedNotes.append(',');
            i = 1;
            aggregatedNotes.append(geo[1]);
        } else {
            i = 1;
        }
        if (aggregatedNotes.length() > 0) {
            putExtra(intent, "notes", aggregatedNotes.substring(i));
        }
        putExtra(intent, "im_handle", instantMessenger);
        putExtra(intent, "postal", address);
        if (addressType != null && (type = toAddressContractType(addressType)) >= 0) {
            intent.putExtra("postal_type", type);
        }
        putExtra(intent, "company", org2);
        putExtra(intent, "job_title", title);
        launchIntent(intent);
    }

    private static int toEmailContractType(String typeString) {
        return doToContractType(typeString, EMAIL_TYPE_STRINGS, EMAIL_TYPE_VALUES);
    }

    private static int toPhoneContractType(String typeString) {
        return doToContractType(typeString, PHONE_TYPE_STRINGS, PHONE_TYPE_VALUES);
    }

    private static int toAddressContractType(String typeString) {
        return doToContractType(typeString, ADDRESS_TYPE_STRINGS, ADDRESS_TYPE_VALUES);
    }

    private static int doToContractType(String typeString, String[] types, int[] values) {
        if (typeString == null) {
            return -1;
        }
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            if (typeString.startsWith(type) || typeString.startsWith(type.toUpperCase(Locale.ENGLISH))) {
                return values[i];
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public final void shareByEmail(String contents) {
        sendEmail(null, null, null, null, contents);
    }

    /* access modifiers changed from: package-private */
    public final void sendEmail(String[] to, String[] cc, String[] bcc, String subject, String body) {
        Intent intent = new Intent("android.intent.action.SEND", Uri.parse("mailto:"));
        if (!(to == null || to.length == 0)) {
            intent.putExtra("android.intent.extra.EMAIL", to);
        }
        if (!(cc == null || cc.length == 0)) {
            intent.putExtra("android.intent.extra.CC", cc);
        }
        if (!(bcc == null || bcc.length == 0)) {
            intent.putExtra("android.intent.extra.BCC", bcc);
        }
        putExtra(intent, "android.intent.extra.SUBJECT", subject);
        putExtra(intent, "android.intent.extra.TEXT", body);
        intent.setType("text/plain");
        launchIntent(intent);
    }

    /* access modifiers changed from: package-private */
    public final void shareBySMS(String contents) {
        sendSMSFromUri("smsto:", contents);
    }

    /* access modifiers changed from: package-private */
    public final void sendSMS(String phoneNumber, String body) {
        sendSMSFromUri("smsto:" + phoneNumber, body);
    }

    private void sendSMSFromUri(String uri, String body) {
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse(uri));
        putExtra(intent, "sms_body", body);
        intent.putExtra("compose_mode", true);
        launchIntent(intent);
    }

    /* access modifiers changed from: package-private */
    public final void sendMMS(String phoneNumber, String subject, String body) {
        sendMMSFromUri("mmsto:" + phoneNumber, subject, body);
    }

    private void sendMMSFromUri(String uri, String subject, String body) {
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse(uri));
        if (subject == null || subject.isEmpty()) {
            putExtra(intent, "subject", this.activity.getString(R.string.msg_default_mms_subject));
        } else {
            putExtra(intent, "subject", subject);
        }
        putExtra(intent, "sms_body", body);
        intent.putExtra("compose_mode", true);
        launchIntent(intent);
    }

    /* access modifiers changed from: package-private */
    public final void dialPhone(String phoneNumber) {
        launchIntent(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + phoneNumber)));
    }

    /* access modifiers changed from: package-private */
    public final void dialPhoneFromUri(String uri) {
        launchIntent(new Intent("android.intent.action.DIAL", Uri.parse(uri)));
    }

    /* access modifiers changed from: package-private */
    public final void openMap(String geoURI) {
        launchIntent(new Intent("android.intent.action.VIEW", Uri.parse(geoURI)));
    }

    /* access modifiers changed from: package-private */
    public final void searchMap(String address) {
        launchIntent(new Intent("android.intent.action.VIEW", Uri.parse("geo:0,0?q=" + Uri.encode(address))));
    }

    /* access modifiers changed from: package-private */
    public final void getDirections(double latitude, double longitude) {
        launchIntent(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google." + LocaleManager.getCountryTLD(this.activity) + "/maps?f=d&daddr=" + latitude + ',' + longitude)));
    }

    /* access modifiers changed from: package-private */
    public final void openProductSearch(String upc) {
        launchIntent(new Intent("android.intent.action.VIEW", Uri.parse("http://www.google." + LocaleManager.getProductSearchCountryTLD(this.activity) + "/m/products?q=" + upc + "&source=zxing")));
    }

    /* access modifiers changed from: package-private */
    public final void openBookSearch(String isbn) {
        launchIntent(new Intent("android.intent.action.VIEW", Uri.parse("http://books.google." + LocaleManager.getBookSearchCountryTLD(this.activity) + "/books?vid=isbn" + isbn)));
    }

    /* access modifiers changed from: package-private */
    public final void searchBookContents(String isbnOrUrl) {
        Intent intent = new Intent(Intents.SearchBookContents.ACTION);
        intent.setClassName(this.activity, SearchBookContentsActivity.class.getName());
        putExtra(intent, Intents.SearchBookContents.ISBN, isbnOrUrl);
        launchIntent(intent);
    }

    /* access modifiers changed from: package-private */
    public final void openURL(String url) {
        if (url.startsWith("HTTP://")) {
            url = "http" + url.substring(4);
        } else if (url.startsWith("HTTPS://")) {
            url = "https" + url.substring(5);
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        try {
            launchIntent(intent);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "Nothing available to handle " + intent);
        }
    }

    /* access modifiers changed from: package-private */
    public final void webSearch(String query) {
        Intent intent = new Intent("android.intent.action.WEB_SEARCH");
        intent.putExtra("query", query);
        launchIntent(intent);
    }

    /* access modifiers changed from: package-private */
    public final void rawLaunchIntent(Intent intent) {
        if (intent != null) {
            intent.addFlags(524288);
            String str = TAG;
            Log.d(str, "Launching intent: " + intent + " with extras: " + intent.getExtras());
            this.activity.startActivity(intent);
        }
    }

    /* access modifiers changed from: package-private */
    public final void launchIntent(Intent intent) {
        try {
            rawLaunchIntent(intent);
        } catch (ActivityNotFoundException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.msg_intent_failed);
            builder.setPositiveButton(R.string.button_ok, (DialogInterface.OnClickListener) null);
            builder.show();
        }
    }

    private static void putExtra(Intent intent, String key, String value) {
        if (value != null && !value.isEmpty()) {
            intent.putExtra(key, value);
        }
    }

    private String parseCustomSearchURL() {
        String customProductSearch2 = PreferenceManager.getDefaultSharedPreferences(this.activity).getString(PreferencesActivity.KEY_CUSTOM_PRODUCT_SEARCH, null);
        if (customProductSearch2 == null || !customProductSearch2.trim().isEmpty()) {
            return customProductSearch2;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public final String fillInCustomSearchURL(String text) {
        if (this.customProductSearch == null) {
            return text;
        }
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        String url = this.customProductSearch;
        Result result2 = this.rawResult;
        if (result2 != null) {
            url = url.replaceFirst("%f(?![0-9a-f])", result2.getBarcodeFormat().toString());
            if (url.contains("%t")) {
                url = url.replace("%t", ResultParser.parseResult(this.rawResult).getType().toString());
            }
        }
        return url.replace("%s", text);
    }
}
