package com.google.zxing.client.android.history;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.Result;
import java.util.ArrayList;

final class HistoryItemAdapter extends ArrayAdapter<HistoryItem> {
    private final Context activity;

    HistoryItemAdapter(Context activity2) {
        super(activity2, R.layout.history_list_item, new ArrayList());
        this.activity = activity2;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        View layout;
        CharSequence title;
        CharSequence title2;
        if (view instanceof LinearLayout) {
            layout = view;
        } else {
            layout = LayoutInflater.from(this.activity).inflate(R.layout.history_list_item, viewGroup, false);
        }
        HistoryItem item = (HistoryItem) getItem(position);
        Result result = item.getResult();
        if (result != null) {
            title2 = result.getText();
            title = item.getDisplayAndDetails();
        } else {
            Resources resources = getContext().getResources();
            CharSequence title3 = resources.getString(R.string.history_empty);
            CharSequence string = resources.getString(R.string.history_empty_detail);
            title2 = title3;
            title = string;
        }
        ((TextView) layout.findViewById(R.id.history_title)).setText(title2);
        ((TextView) layout.findViewById(R.id.history_detail)).setText(title);
        return layout;
    }
}
