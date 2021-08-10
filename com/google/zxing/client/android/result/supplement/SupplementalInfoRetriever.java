package com.google.zxing.client.android.result.supplement;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;
import com.google.zxing.client.android.history.HistoryManager;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ProductParsedResult;
import com.google.zxing.client.result.URIParsedResult;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;

public abstract class SupplementalInfoRetriever extends AsyncTask<Object, Object, Object> {
    private static final String TAG = "SupplementalInfo";
    private final WeakReference<HistoryManager> historyManagerRef;
    private final Collection<Spannable> newContents = new ArrayList();
    private final Collection<String[]> newHistories = new ArrayList();
    private final WeakReference<TextView> textViewRef;

    /* access modifiers changed from: package-private */
    public abstract void retrieveSupplementalInfo() throws IOException;

    public static void maybeInvokeRetrieval(TextView textView, ParsedResult result, HistoryManager historyManager, Context context) {
        try {
            if (result instanceof URIParsedResult) {
                new URIResultInfoRetriever(textView, (URIParsedResult) result, historyManager, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
                new TitleRetriever(textView, (URIParsedResult) result, historyManager).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
            } else if (result instanceof ProductParsedResult) {
                new ProductResultInfoRetriever(textView, ((ProductParsedResult) result).getProductID(), historyManager, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
            } else if (result instanceof ISBNParsedResult) {
                String isbn = ((ISBNParsedResult) result).getISBN();
                new ProductResultInfoRetriever(textView, isbn, historyManager, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
                new BookResultInfoRetriever(textView, isbn, historyManager, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
            }
        } catch (RejectedExecutionException e) {
        }
    }

    SupplementalInfoRetriever(TextView textView, HistoryManager historyManager) {
        this.textViewRef = new WeakReference<>(textView);
        this.historyManagerRef = new WeakReference<>(historyManager);
    }

    @Override // android.os.AsyncTask
    public final Object doInBackground(Object... args) {
        try {
            retrieveSupplementalInfo();
            return null;
        } catch (IOException e) {
            Log.w(TAG, e);
            return null;
        }
    }

    /* access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public final void onPostExecute(Object arg) {
        TextView textView = this.textViewRef.get();
        if (textView != null) {
            for (CharSequence content : this.newContents) {
                textView.append(content);
            }
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        HistoryManager historyManager = this.historyManagerRef.get();
        if (historyManager != null) {
            for (String[] text : this.newHistories) {
                historyManager.addHistoryItemDetails(text[0], text[1]);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final void append(String itemID, String source, String[] newTexts, String linkURL) {
        StringBuilder newTextCombined = new StringBuilder();
        if (source != null) {
            newTextCombined.append(source);
            newTextCombined.append(' ');
        }
        int linkStart = newTextCombined.length();
        boolean first = true;
        for (String newText : newTexts) {
            if (first) {
                newTextCombined.append(newText);
                first = false;
            } else {
                newTextCombined.append(" [");
                newTextCombined.append(newText);
                newTextCombined.append(']');
            }
        }
        int linkEnd = newTextCombined.length();
        String newText2 = newTextCombined.toString();
        Spannable content = new SpannableString(newText2 + "\n\n");
        if (linkURL != null) {
            if (linkURL.startsWith("HTTP://")) {
                linkURL = "http" + linkURL.substring(4);
            } else if (linkURL.startsWith("HTTPS://")) {
                linkURL = "https" + linkURL.substring(5);
            }
            content.setSpan(new URLSpan(linkURL), linkStart, linkEnd, 33);
        }
        this.newContents.add(content);
        this.newHistories.add(new String[]{itemID, newText2});
    }

    static void maybeAddText(String text, Collection<String> texts) {
        if (text != null && !text.isEmpty()) {
            texts.add(text);
        }
    }

    static void maybeAddTextSeries(Collection<String> textSeries, Collection<String> texts) {
        if (!(textSeries == null || textSeries.isEmpty())) {
            boolean first = true;
            StringBuilder authorsText = new StringBuilder();
            for (String author : textSeries) {
                if (first) {
                    first = false;
                } else {
                    authorsText.append(", ");
                }
                authorsText.append(author);
            }
            texts.add(authorsText.toString());
        }
    }
}
