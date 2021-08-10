package com.google.zxing.client.android.result;

import android.app.Activity;
import android.telephony.PhoneNumberUtils;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.SMSParsedResult;

public final class SMSResultHandler extends ResultHandler {
    private static final int[] buttons = {R.string.button_sms, R.string.button_mms};

    public SMSResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonCount() {
        return buttons.length;
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonText(int index) {
        return buttons[index];
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public void handleButtonPress(int index) {
        SMSParsedResult smsResult = (SMSParsedResult) getResult();
        String number = smsResult.getNumbers()[0];
        if (index == 0) {
            sendSMS(number, smsResult.getBody());
        } else if (index == 1) {
            sendMMS(number, smsResult.getSubject(), smsResult.getBody());
        }
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public CharSequence getDisplayContents() {
        SMSParsedResult smsResult = (SMSParsedResult) getResult();
        String[] rawNumbers = smsResult.getNumbers();
        String[] formattedNumbers = new String[rawNumbers.length];
        for (int i = 0; i < rawNumbers.length; i++) {
            formattedNumbers[i] = PhoneNumberUtils.formatNumber(rawNumbers[i]);
        }
        StringBuilder contents = new StringBuilder(50);
        ParsedResult.maybeAppend(formattedNumbers, contents);
        ParsedResult.maybeAppend(smsResult.getSubject(), contents);
        ParsedResult.maybeAppend(smsResult.getBody(), contents);
        return contents.toString();
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getDisplayTitle() {
        return R.string.result_sms;
    }
}
