package com.google.zxing.client.android.result.supplement;

import android.text.Html;
import android.widget.TextView;
import com.google.zxing.client.android.HttpHelper;
import com.google.zxing.client.android.history.HistoryManager;
import com.google.zxing.client.result.URIParsedResult;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* access modifiers changed from: package-private */
public final class TitleRetriever extends SupplementalInfoRetriever {
    private static final int MAX_TITLE_LEN = 100;
    private static final Pattern TITLE_PATTERN = Pattern.compile("<title>([^<]+)");
    private final String httpUrl;

    TitleRetriever(TextView textView, URIParsedResult result, HistoryManager historyManager) {
        super(textView, historyManager);
        this.httpUrl = result.getURI();
    }

    /* access modifiers changed from: package-private */
    @Override // com.google.zxing.client.android.result.supplement.SupplementalInfoRetriever
    public void retrieveSupplementalInfo() {
        String title;
        try {
            CharSequence contents = HttpHelper.downloadViaHttp(this.httpUrl, HttpHelper.ContentType.HTML, 4096);
            if (contents != null && contents.length() > 0) {
                Matcher m = TITLE_PATTERN.matcher(contents);
                if (m.find() && (title = m.group(1)) != null && !title.isEmpty()) {
                    String title2 = Html.fromHtml(title).toString();
                    if (title2.length() > MAX_TITLE_LEN) {
                        title2 = title2.substring(0, MAX_TITLE_LEN) + "...";
                    }
                    String str = this.httpUrl;
                    append(str, null, new String[]{title2}, str);
                }
            }
        } catch (IOException e) {
        }
    }
}
