package com.google.zxing.client.android.result;

import android.app.Activity;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ParsedResult;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class AddressBookResultHandler extends ResultHandler {
    private static final int[] BUTTON_TEXTS = {R.string.button_add_contact, R.string.button_show_map, R.string.button_dial, R.string.button_email};
    private static final DateFormat[] DATE_FORMATS;
    private int buttonCount;
    private final boolean[] fields;

    static {
        DateFormat[] dateFormatArr = {new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH), new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH), new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)};
        DATE_FORMATS = dateFormatArr;
        for (DateFormat format : dateFormatArr) {
            format.setLenient(false);
        }
    }

    private int mapIndexToAction(int index) {
        if (index >= this.buttonCount) {
            return -1;
        }
        int count = -1;
        for (int x = 0; x < 4; x++) {
            if (this.fields[x]) {
                count++;
            }
            if (count == index) {
                return x;
            }
        }
        return -1;
    }

    public AddressBookResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
        AddressBookParsedResult addressResult = (AddressBookParsedResult) result;
        String[] addresses = addressResult.getAddresses();
        String[] phoneNumbers = addressResult.getPhoneNumbers();
        String[] emails = addressResult.getEmails();
        boolean[] zArr = new boolean[4];
        this.fields = zArr;
        zArr[0] = true;
        zArr[1] = addresses != null && addresses.length > 0 && addresses[0] != null && !addresses[0].isEmpty();
        this.fields[2] = phoneNumbers != null && phoneNumbers.length > 0;
        this.fields[3] = emails != null && emails.length > 0;
        this.buttonCount = 0;
        for (int x = 0; x < 4; x++) {
            if (this.fields[x]) {
                this.buttonCount++;
            }
        }
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonCount() {
        return this.buttonCount;
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonText(int index) {
        return BUTTON_TEXTS[mapIndexToAction(index)];
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public void handleButtonPress(int index) {
        AddressBookParsedResult addressResult = (AddressBookParsedResult) getResult();
        String[] addresses = addressResult.getAddresses();
        String address1Type = null;
        String address1 = (addresses == null || addresses.length < 1) ? null : addresses[0];
        String[] addressTypes = addressResult.getAddressTypes();
        if (addressTypes != null && addressTypes.length >= 1) {
            address1Type = addressTypes[0];
        }
        int action = mapIndexToAction(index);
        if (action == 0) {
            addContact(addressResult.getNames(), addressResult.getNicknames(), addressResult.getPronunciation(), addressResult.getPhoneNumbers(), addressResult.getPhoneTypes(), addressResult.getEmails(), addressResult.getEmailTypes(), addressResult.getNote(), addressResult.getInstantMessenger(), address1, address1Type, addressResult.getOrg(), addressResult.getTitle(), addressResult.getURLs(), addressResult.getBirthday(), addressResult.getGeo());
        } else if (action == 1) {
            searchMap(address1);
        } else if (action == 2) {
            dialPhone(addressResult.getPhoneNumbers()[0]);
        } else if (action == 3) {
            sendEmail(addressResult.getEmails(), null, null, null, null);
        }
    }

    private static Date parseDate(String s) {
        DateFormat[] dateFormatArr = DATE_FORMATS;
        for (int i = 0; i < dateFormatArr.length; i++) {
            try {
                return dateFormatArr[i].parse(s);
            } catch (ParseException e) {
            }
        }
        return null;
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public CharSequence getDisplayContents() {
        Date date;
        AddressBookParsedResult result = (AddressBookParsedResult) getResult();
        StringBuilder contents = new StringBuilder(100);
        ParsedResult.maybeAppend(result.getNames(), contents);
        int namesLength = contents.length();
        String pronunciation = result.getPronunciation();
        if (pronunciation != null && !pronunciation.isEmpty()) {
            contents.append("\n(");
            contents.append(pronunciation);
            contents.append(')');
        }
        ParsedResult.maybeAppend(result.getTitle(), contents);
        ParsedResult.maybeAppend(result.getOrg(), contents);
        ParsedResult.maybeAppend(result.getAddresses(), contents);
        String[] numbers = result.getPhoneNumbers();
        if (numbers != null) {
            for (String number : numbers) {
                if (number != null) {
                    ParsedResult.maybeAppend(PhoneNumberUtils.formatNumber(number), contents);
                }
            }
        }
        ParsedResult.maybeAppend(result.getEmails(), contents);
        ParsedResult.maybeAppend(result.getURLs(), contents);
        String birthday = result.getBirthday();
        if (!(birthday == null || birthday.isEmpty() || (date = parseDate(birthday)) == null)) {
            ParsedResult.maybeAppend(DateFormat.getDateInstance(2).format(Long.valueOf(date.getTime())), contents);
        }
        ParsedResult.maybeAppend(result.getNote(), contents);
        if (namesLength <= 0) {
            return contents.toString();
        }
        Spannable styled = new SpannableString(contents.toString());
        styled.setSpan(new StyleSpan(1), 0, namesLength, 0);
        return styled;
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getDisplayTitle() {
        return R.string.result_address_book;
    }
}
