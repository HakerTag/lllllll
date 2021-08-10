package com.google.zxing.client.android.result;

import android.app.Activity;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.Result;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;

public final class ISBNResultHandler extends ResultHandler {
    private static final int[] buttons = {R.string.button_product_search, R.string.button_book_search, R.string.button_search_book_contents, R.string.button_custom_product_search};

    public ISBNResultHandler(Activity activity, ParsedResult result, Result rawResult) {
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
        ISBNParsedResult isbnResult = (ISBNParsedResult) getResult();
        if (index == 0) {
            openProductSearch(isbnResult.getISBN());
        } else if (index == 1) {
            openBookSearch(isbnResult.getISBN());
        } else if (index == 2) {
            searchBookContents(isbnResult.getISBN());
        } else if (index == 3) {
            openURL(fillInCustomSearchURL(isbnResult.getISBN()));
        }
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getDisplayTitle() {
        return R.string.result_isbn;
    }
}
