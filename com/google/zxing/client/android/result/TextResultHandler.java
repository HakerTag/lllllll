package com.google.zxing.client.android.result;

import android.app.Activity;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;

public final class TextResultHandler extends ResultHandler {
    private static final int[] buttons = {R.string.button_web_search, R.string.button_share_by_email, R.string.button_share_by_sms, R.string.button_custom_product_search};

    public TextResultHandler(Activity activity, ParsedResult result, Result rawResult) {
        super(activity, result, rawResult);
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonCount() {
        return hasCustomProductSearch() ? buttons.length : buttons.length - 1;
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonText(int index) {
        return buttons[index];
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public void handleButtonPress(int index) {
        String text = getResult().getDisplayResult();
        if (index == 0) {
            webSearch(text);
        } else if (index == 1) {
            shareByEmail(text);
        } else if (index == 2) {
            shareBySMS(text);
        } else if (index == 3) {
            openURL(fillInCustomSearchURL(text));
        }
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getDisplayTitle() {
        return R.string.result_text;
    }
}
