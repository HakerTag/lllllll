package com.google.zxing.client.android.result.supplement;

import android.content.Context;
import android.widget.TextView;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.client.android.HttpHelper;
import com.google.zxing.client.android.LocaleManager;
import com.google.zxing.client.android.history.HistoryManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/* access modifiers changed from: package-private */
public final class BookResultInfoRetriever extends SupplementalInfoRetriever {
    private final Context context;
    private final String isbn;
    private final String source;

    BookResultInfoRetriever(TextView textView, String isbn2, HistoryManager historyManager, Context context2) {
        super(textView, historyManager);
        this.isbn = isbn2;
        this.source = context2.getString(R.string.msg_google_books);
        this.context = context2;
    }

    /* access modifiers changed from: package-private */
    @Override // com.google.zxing.client.android.result.supplement.SupplementalInfoRetriever
    public void retrieveSupplementalInfo() throws IOException {
        CharSequence contents = HttpHelper.downloadViaHttp("https://www.googleapis.com/books/v1/volumes?q=isbn:" + this.isbn, HttpHelper.ContentType.JSON);
        if (contents.length() != 0) {
            Collection<String> authors = null;
            try {
                JSONArray items = ((JSONObject) new JSONTokener(contents.toString()).nextValue()).optJSONArray("items");
                if (items == null) {
                    return;
                }
                if (!items.isNull(0)) {
                    JSONObject volumeInfo = ((JSONObject) items.get(0)).getJSONObject("volumeInfo");
                    if (volumeInfo != null) {
                        String title = volumeInfo.optString("title");
                        String pages = volumeInfo.optString("pageCount");
                        JSONArray authorsArray = volumeInfo.optJSONArray("authors");
                        if (authorsArray != null && !authorsArray.isNull(0)) {
                            authors = new ArrayList<>(authorsArray.length());
                            for (int i = 0; i < authorsArray.length(); i++) {
                                authors.add(authorsArray.getString(i));
                            }
                        }
                        Collection<String> newTexts = new ArrayList<>();
                        maybeAddText(title, newTexts);
                        maybeAddTextSeries(authors, newTexts);
                        maybeAddText((pages == null || pages.isEmpty()) ? null : pages + "pp.", newTexts);
                        append(this.isbn, this.source, (String[]) newTexts.toArray(new String[newTexts.size()]), ("http://www.google." + LocaleManager.getBookSearchCountryTLD(this.context) + "/search?tbm=bks&source=zxing&q=") + this.isbn);
                    }
                }
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }
    }
}
