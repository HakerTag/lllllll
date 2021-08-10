package com.google.zxing.client.android.result;

import android.app.Activity;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;

public final class TextResultHandler extends ResultHandler {
    private static final int[] buttons = {R.string.button_web_search, R.string.button_share_by_email, R.string.button_share_by_sms, R.string.button_custom_product_search};

    public TextResultHandler(Activity activity, ParsedResult parsedResult, Result result) {
        super(activity, parsedResult, result);
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonCount() {
        return hasCustomProductSearch() ? buttons.length : buttons.length - 1;
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonText(int i) {
        return buttons[i];
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public void handleButtonPress(int i) {
        String displayResult = getResult().getDisplayResult();
        if (i == 0) {
            webSearch(displayResult);
        } else if (i == 1) {
            shareByEmail(displayResult);
        } else if (i == 2) {
            shareBySMS(displayResult);
        } else if (i == 3) {
            openURL(fillInCustomSearchURL(displayResult));
        }
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getDisplayTitle() {
        return R.string.result_text;
    }
}
