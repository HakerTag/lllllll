package com.google.zxing.client.android.result;

import android.app.Activity;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.ParsedResult;

public final class EmailAddressResultHandler extends ResultHandler {
    private static final int[] buttons = {R.string.button_email, R.string.button_add_contact};

    public EmailAddressResultHandler(Activity activity, ParsedResult result) {
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
        EmailAddressParsedResult emailResult = (EmailAddressParsedResult) getResult();
        if (index == 0) {
            sendEmail(emailResult.getTos(), emailResult.getCCs(), emailResult.getBCCs(), emailResult.getSubject(), emailResult.getBody());
        } else if (index == 1) {
            addEmailOnlyContact(emailResult.getTos(), null);
        }
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getDisplayTitle() {
        return R.string.result_email_address;
    }
}
