package com.google.zxing.client.android.result;

import android.app.Activity;
import android.telephony.PhoneNumberUtils;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.TelParsedResult;

public final class TelResultHandler extends ResultHandler {
    private static final int[] buttons = {R.string.button_dial, R.string.button_add_contact};

    public TelResultHandler(Activity activity, ParsedResult result) {
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
        TelParsedResult telResult = (TelParsedResult) getResult();
        if (index == 0) {
            dialPhoneFromUri(telResult.getTelURI());
            getActivity().finish();
        } else if (index == 1) {
            addPhoneOnlyContact(new String[]{telResult.getNumber()}, null);
        }
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public CharSequence getDisplayContents() {
        return PhoneNumberUtils.formatNumber(getResult().getDisplayResult().replace("\r", ""));
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getDisplayTitle() {
        return R.string.result_tel;
    }
}
