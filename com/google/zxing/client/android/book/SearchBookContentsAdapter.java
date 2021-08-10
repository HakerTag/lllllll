package com.google.zxing.client.android.book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import barcodescanner.xservices.nl.barcodescanner.R;
import java.util.List;

/* access modifiers changed from: package-private */
public final class SearchBookContentsAdapter extends ArrayAdapter<SearchBookContentsResult> {
    SearchBookContentsAdapter(Context context, List<SearchBookContentsResult> list) {
        super(context, R.layout.search_book_contents_list_item, 0, list);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        SearchBookContentsListItem searchBookContentsListItem;
        SearchBookContentsListItem searchBookContentsListItem2;
        if (view == null) {
            searchBookContentsListItem2 = (SearchBookContentsListItem) LayoutInflater.from(getContext()).inflate(R.layout.search_book_contents_list_item, viewGroup, false);
        } else {
            boolean z = view instanceof SearchBookContentsListItem;
            searchBookContentsListItem = view;
            if (z) {
                searchBookContentsListItem2 = (SearchBookContentsListItem) view;
            }
            return searchBookContentsListItem;
        }
        searchBookContentsListItem2.set((SearchBookContentsResult) getItem(i));
        searchBookContentsListItem = searchBookContentsListItem2;
        return searchBookContentsListItem;
    }
}
