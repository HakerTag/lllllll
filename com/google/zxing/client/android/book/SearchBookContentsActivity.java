package com.google.zxing.client.android.book;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.client.android.HttpHelper;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.LocaleManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class SearchBookContentsActivity extends Activity {
    private static final Pattern GT_ENTITY_PATTERN = Pattern.compile("&gt;");
    private static final Pattern LT_ENTITY_PATTERN = Pattern.compile("&lt;");
    private static final Pattern QUOTE_ENTITY_PATTERN = Pattern.compile("&#39;");
    private static final Pattern QUOT_ENTITY_PATTERN = Pattern.compile("&quot;");
    private static final String TAG = SearchBookContentsActivity.class.getSimpleName();
    private static final Pattern TAG_PATTERN = Pattern.compile("\\<.*?\\>");
    private final View.OnClickListener buttonListener = new View.OnClickListener() {
        /* class com.google.zxing.client.android.book.SearchBookContentsActivity.AnonymousClass1 */

        public void onClick(View view) {
            SearchBookContentsActivity.this.launchSearch();
        }
    };
    private TextView headerView;
    private String isbn;
    private final View.OnKeyListener keyListener = new View.OnKeyListener() {
        /* class com.google.zxing.client.android.book.SearchBookContentsActivity.AnonymousClass2 */

        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (keyCode != 66 || event.getAction() != 0) {
                return false;
            }
            SearchBookContentsActivity.this.launchSearch();
            return true;
        }
    };
    private AsyncTask<String, ?, ?> networkTask;
    private View queryButton;
    private EditText queryTextView;
    private ListView resultListView;

    /* access modifiers changed from: package-private */
    public String getISBN() {
        return this.isbn;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeExpiredCookie();
        Intent intent = getIntent();
        if (intent == null || !Intents.SearchBookContents.ACTION.equals(intent.getAction())) {
            finish();
            return;
        }
        String stringExtra = intent.getStringExtra(Intents.SearchBookContents.ISBN);
        this.isbn = stringExtra;
        if (LocaleManager.isBookSearchUrl(stringExtra)) {
            setTitle(getString(R.string.sbc_name));
        } else {
            setTitle(getString(R.string.sbc_name) + ": ISBN " + this.isbn);
        }
        setContentView(R.layout.search_book_contents);
        this.queryTextView = (EditText) findViewById(R.id.query_text_view);
        String initialQuery = intent.getStringExtra(Intents.SearchBookContents.QUERY);
        if (initialQuery != null && !initialQuery.isEmpty()) {
            this.queryTextView.setText(initialQuery);
        }
        this.queryTextView.setOnKeyListener(this.keyListener);
        View findViewById = findViewById(R.id.query_button);
        this.queryButton = findViewById;
        findViewById.setOnClickListener(this.buttonListener);
        this.resultListView = (ListView) findViewById(R.id.result_list_view);
        TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.search_book_contents_header, (ViewGroup) this.resultListView, false);
        this.headerView = textView;
        this.resultListView.addHeaderView(textView);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.queryTextView.selectAll();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        AsyncTask<?, ?, ?> oldTask = this.networkTask;
        if (oldTask != null) {
            oldTask.cancel(true);
            this.networkTask = null;
        }
        super.onPause();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void launchSearch() {
        String query = this.queryTextView.getText().toString();
        if (query != null && !query.isEmpty()) {
            AsyncTask<?, ?, ?> oldTask = this.networkTask;
            if (oldTask != null) {
                oldTask.cancel(true);
            }
            NetworkTask networkTask2 = new NetworkTask();
            this.networkTask = networkTask2;
            networkTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query, this.isbn);
            this.headerView.setText(R.string.msg_sbc_searching_book);
            this.resultListView.setAdapter((ListAdapter) null);
            this.queryTextView.setEnabled(false);
            this.queryButton.setEnabled(false);
        }
    }

    /* access modifiers changed from: private */
    public final class NetworkTask extends AsyncTask<String, Object, JSONObject> {
        private NetworkTask() {
        }

        /* access modifiers changed from: protected */
        public JSONObject doInBackground(String... args) {
            String uri;
            try {
                String theQuery = args[0];
                String theIsbn = args[1];
                if (LocaleManager.isBookSearchUrl(theIsbn)) {
                    uri = "http://www.google.com/books?id=" + theIsbn.substring(theIsbn.indexOf(61) + 1) + "&jscmd=SearchWithinVolume2&q=" + theQuery;
                } else {
                    uri = "http://www.google.com/books?vid=isbn" + theIsbn + "&jscmd=SearchWithinVolume2&q=" + theQuery;
                }
                return new JSONObject(HttpHelper.downloadViaHttp(uri, HttpHelper.ContentType.JSON).toString());
            } catch (IOException ioe) {
                Log.w(SearchBookContentsActivity.TAG, "Error accessing book search", ioe);
                return null;
            } catch (JSONException je) {
                Log.w(SearchBookContentsActivity.TAG, "Error accessing book search", je);
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(JSONObject result) {
            if (result == null) {
                SearchBookContentsActivity.this.headerView.setText(R.string.msg_sbc_failed);
            } else {
                handleSearchResults(result);
            }
            SearchBookContentsActivity.this.queryTextView.setEnabled(true);
            SearchBookContentsActivity.this.queryTextView.selectAll();
            SearchBookContentsActivity.this.queryButton.setEnabled(true);
        }

        private void handleSearchResults(JSONObject json) {
            try {
                int count = json.getInt("number_of_results");
                TextView textView = SearchBookContentsActivity.this.headerView;
                textView.setText(SearchBookContentsActivity.this.getString(R.string.msg_sbc_results) + " : " + count);
                if (count > 0) {
                    JSONArray results = json.getJSONArray(MediaBrowserServiceCompat.KEY_SEARCH_RESULTS);
                    SearchBookContentsResult.setQuery(SearchBookContentsActivity.this.queryTextView.getText().toString());
                    List<SearchBookContentsResult> items = new ArrayList<>(count);
                    for (int x = 0; x < count; x++) {
                        items.add(parseResult(results.getJSONObject(x)));
                    }
                    SearchBookContentsActivity.this.resultListView.setOnItemClickListener(new BrowseBookListener(SearchBookContentsActivity.this, items));
                    SearchBookContentsActivity.this.resultListView.setAdapter((ListAdapter) new SearchBookContentsAdapter(SearchBookContentsActivity.this, items));
                    return;
                }
                if ("false".equals(json.optString("searchable"))) {
                    SearchBookContentsActivity.this.headerView.setText(R.string.msg_sbc_book_not_searchable);
                }
                SearchBookContentsActivity.this.resultListView.setAdapter((ListAdapter) null);
            } catch (JSONException e) {
                Log.w(SearchBookContentsActivity.TAG, "Bad JSON from book search", e);
                SearchBookContentsActivity.this.resultListView.setAdapter((ListAdapter) null);
                SearchBookContentsActivity.this.headerView.setText(R.string.msg_sbc_failed);
            }
        }

        private SearchBookContentsResult parseResult(JSONObject json) {
            String pageNumber;
            String snippet;
            boolean valid = false;
            try {
                String pageId = json.getString("page_id");
                String pageNumber2 = json.optString("page_number");
                String snippet2 = json.optString("snippet_text");
                if (pageNumber2 == null || pageNumber2.isEmpty()) {
                    pageNumber = "";
                } else {
                    pageNumber = SearchBookContentsActivity.this.getString(R.string.msg_sbc_page) + ' ' + pageNumber2;
                }
                if (snippet2 != null && !snippet2.isEmpty()) {
                    valid = true;
                }
                if (valid) {
                    snippet = SearchBookContentsActivity.QUOT_ENTITY_PATTERN.matcher(SearchBookContentsActivity.QUOTE_ENTITY_PATTERN.matcher(SearchBookContentsActivity.GT_ENTITY_PATTERN.matcher(SearchBookContentsActivity.LT_ENTITY_PATTERN.matcher(SearchBookContentsActivity.TAG_PATTERN.matcher(snippet2).replaceAll("")).replaceAll("<")).replaceAll(">")).replaceAll("'")).replaceAll("\"");
                } else {
                    snippet = '(' + SearchBookContentsActivity.this.getString(R.string.msg_sbc_snippet_unavailable) + ')';
                }
                return new SearchBookContentsResult(pageId, pageNumber, snippet, valid);
            } catch (JSONException e) {
                Log.w(SearchBookContentsActivity.TAG, e);
                return new SearchBookContentsResult(SearchBookContentsActivity.this.getString(R.string.msg_sbc_no_page_returned), "", "", false);
            }
        }
    }
}
