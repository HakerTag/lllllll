package com.google.zxing.client.android.book;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import com.google.zxing.client.android.LocaleManager;
import java.util.List;

/* access modifiers changed from: package-private */
public final class BrowseBookListener implements AdapterView.OnItemClickListener {
    private final SearchBookContentsActivity activity;
    private final List<SearchBookContentsResult> items;

    BrowseBookListener(SearchBookContentsActivity activity2, List<SearchBookContentsResult> items2) {
        this.activity = activity2;
        this.items = items2;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
        int itemOffset;
        if (position >= 1 && position - 1 < this.items.size()) {
            String pageId = this.items.get(itemOffset).getPageId();
            String query = SearchBookContentsResult.getQuery();
            if (LocaleManager.isBookSearchUrl(this.activity.getISBN()) && !pageId.isEmpty()) {
                String uri = this.activity.getISBN();
                String volumeId = uri.substring(uri.indexOf(61) + 1);
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://books.google." + LocaleManager.getBookSearchCountryTLD(this.activity) + "/books?id=" + volumeId + "&pg=" + pageId + "&vq=" + query));
                intent.addFlags(524288);
                this.activity.startActivity(intent);
            }
        }
    }
}
