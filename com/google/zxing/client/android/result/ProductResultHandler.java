package com.google.zxing.client.android.result;

import android.app.Activity;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.Result;
import com.google.zxing.client.result.ExpandedProductParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ProductParsedResult;

public final class ProductResultHandler extends ResultHandler {
    private static final int[] buttons = {R.string.button_product_search, R.string.button_web_search, R.string.button_custom_product_search};

    public ProductResultHandler(Activity activity, ParsedResult parsedResult, Result result) {
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
        String productIDFromResult = getProductIDFromResult(getResult());
        if (i == 0) {
            openProductSearch(productIDFromResult);
        } else if (i == 1) {
            webSearch(productIDFromResult);
        } else if (i == 2) {
            openURL(fillInCustomSearchURL(productIDFromResult));
        }
    }

    private static String getProductIDFromResult(ParsedResult parsedResult) {
        if (parsedResult instanceof ProductParsedResult) {
            return ((ProductParsedResult) parsedResult).getNormalizedProductID();
        }
        if (parsedResult instanceof ExpandedProductParsedResult) {
            return ((ExpandedProductParsedResult) parsedResult).getRawText();
        }
        throw new IllegalArgumentException(parsedResult.getClass().toString());
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getDisplayTitle() {
        return R.string.result_product;
    }
}
