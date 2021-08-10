package com.google.zxing.client.android.history;

import com.google.zxing.Result;

public final class HistoryItem {
    private final String details;
    private final String display;
    private final Result result;

    HistoryItem(Result result2, String display2, String details2) {
        this.result = result2;
        this.display = display2;
        this.details = details2;
    }

    public Result getResult() {
        return this.result;
    }

    public String getDisplayAndDetails() {
        StringBuilder displayResult = new StringBuilder();
        String str = this.display;
        if (str == null || str.isEmpty()) {
            displayResult.append(this.result.getText());
        } else {
            displayResult.append(this.display);
        }
        String str2 = this.details;
        if (str2 != null && !str2.isEmpty()) {
            displayResult.append(" : ");
            displayResult.append(this.details);
        }
        return displayResult.toString();
    }
}
