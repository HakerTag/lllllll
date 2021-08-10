package com.google.zxing.client.android.history;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

public final class HistoryActivity extends ListActivity {
    private static final String TAG = HistoryActivity.class.getSimpleName();
    private ArrayAdapter<HistoryItem> adapter;
    private HistoryManager historyManager;
    private CharSequence originalTitle;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.historyManager = new HistoryManager(this);
        HistoryItemAdapter historyItemAdapter = new HistoryItemAdapter(this);
        this.adapter = historyItemAdapter;
        setListAdapter(historyItemAdapter);
        registerForContextMenu(getListView());
        this.originalTitle = getTitle();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        reloadHistoryItems();
    }

    private void reloadHistoryItems() {
        Iterable<HistoryItem> items = this.historyManager.buildHistoryItems();
        this.adapter.clear();
        for (HistoryItem item : items) {
            this.adapter.add(item);
        }
        setTitle(((Object) this.originalTitle) + " (" + this.adapter.getCount() + ')');
        if (this.adapter.isEmpty()) {
            this.adapter.add(new HistoryItem(null, null, null));
        }
    }

    /* access modifiers changed from: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (this.adapter.getItem(position).getResult() != null) {
            Intent intent = new Intent(this, CaptureActivity.class);
            intent.putExtra(Intents.History.ITEM_NUMBER, position);
            setResult(-1, intent);
            finish();
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        if (position >= this.adapter.getCount() || this.adapter.getItem(position).getResult() != null) {
            menu.add(0, position, position, R.string.history_clear_one_history_text);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        this.historyManager.deleteHistoryItem(item.getItemId());
        reloadHistoryItems();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.historyManager.hasHistoryItems()) {
            getMenuInflater().inflate(R.menu.history, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_history_send) {
            Parcelable historyFile = HistoryManager.saveHistory(this.historyManager.buildHistory().toString());
            if (historyFile == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.msg_unmount_usb);
                builder.setPositiveButton(R.string.button_ok, (DialogInterface.OnClickListener) null);
                builder.show();
            } else {
                Intent intent = new Intent("android.intent.action.SEND", Uri.parse("mailto:"));
                intent.addFlags(524288);
                String subject = getResources().getString(R.string.history_email_title);
                intent.putExtra("android.intent.extra.SUBJECT", subject);
                intent.putExtra("android.intent.extra.TEXT", subject);
                intent.putExtra("android.intent.extra.STREAM", historyFile);
                intent.setType("text/csv");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    Log.w(TAG, anfe.toString());
                }
            }
        } else if (i != R.id.menu_history_clear_text) {
            return super.onOptionsItemSelected(item);
        } else {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setMessage(R.string.msg_sure);
            builder2.setCancelable(true);
            builder2.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                /* class com.google.zxing.client.android.history.HistoryActivity.AnonymousClass1 */

                public void onClick(DialogInterface dialog, int i2) {
                    HistoryActivity.this.historyManager.clearHistory();
                    dialog.dismiss();
                    HistoryActivity.this.finish();
                }
            });
            builder2.setNegativeButton(R.string.button_cancel, (DialogInterface.OnClickListener) null);
            builder2.show();
        }
        return true;
    }
}
