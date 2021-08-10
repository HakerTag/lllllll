package com.google.zxing.client.android.result;

import android.app.Activity;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.Result;
import com.google.zxing.client.result.ExpandedProductParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ProductParsedResult;

public final class ProductResultHandler extends ResultHandler {
    private static final int[] buttons = {R.string.button_product_search, R.string.button_web_search, R.string.button_custom_product_search};

    public ProductResultHandler(Activity activity, ParsedResult result, Result rawResult) {
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
        String productID = getProductIDFromResult(getResult());
        if (index == 0) {
            openProductSearch(productID);
        } else if (index == 1) {
            webSearch(productID);
        } else if (index == 2) {
            openURL(fillInCustomSearchURL(productID));
        }
    }

    private static String getProductIDFromResult(ParsedResult rawResult) {
        if (rawResult instanceof ProductParsedResult) {
            return ((ProductParsedResult) rawResult).getNormalizedProductID();
        }
        if (rawResult instanceof ExpandedProductParsedResult) {
            return ((ExpandedProductParsedResult) rawResult).getRawText();
        }
        throw new IllegalArgumentException(rawResult.getClass().toString());
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getDisplayTitle() {
        return R.string.result_product;
    }
}
