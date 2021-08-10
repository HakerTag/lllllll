package com.google.zxing.client.android.encode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.FinishListener;
import com.google.zxing.client.android.Intents;
import java.util.regex.Pattern;

public final class EncodeActivity extends Activity {
    private static final int MAX_BARCODE_FILENAME_LENGTH = 24;
    private static final Pattern NOT_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");
    private static final String TAG = EncodeActivity.class.getSimpleName();
    private static final String USE_VCARD_KEY = "USE_VCARD";
    private QRCodeEncoder qrCodeEncoder;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        String action = intent.getAction();
        if (Intents.Encode.ACTION.equals(action) || "android.intent.action.SEND".equals(action)) {
            setContentView(R.layout.encode);
        } else {
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.encode, menu);
        QRCodeEncoder qRCodeEncoder = this.qrCodeEncoder;
        int i = qRCodeEncoder != null && qRCodeEncoder.isUseVCard() ? R.string.menu_encode_mecard : R.string.menu_encode_vcard;
        MenuItem findItem = menu.findItem(R.id.menu_encode);
        findItem.setTitle(i);
        Intent intent = getIntent();
        if (intent != null) {
            findItem.setVisible(Contents.Type.CONTACT.equals(intent.getStringExtra(Intents.Encode.TYPE)));
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Intent intent;
        int itemId = menuItem.getItemId();
        if (itemId == R.id.menu_share) {
            share();
            return true;
        } else if (itemId != R.id.menu_encode || (intent = getIntent()) == null) {
            return false;
        } else {
            intent.putExtra(USE_VCARD_KEY, !this.qrCodeEncoder.isUseVCard());
            intent.addFlags(67108864);
            startActivity(intent);
            finish();
            return true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x0131 A[SYNTHETIC, Splitter:B:40:0x0131] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0137 A[SYNTHETIC, Splitter:B:44:0x0137] */
    /* JADX WARNING: Removed duplicated region for block: B:53:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void share() {
        /*
            r8 = this;
            com.google.zxing.client.android.encode.QRCodeEncoder r0 = r8.qrCodeEncoder
            java.lang.String r1 = "No existing barcode to send?"
            if (r0 != 0) goto L_0x000c
            java.lang.String r0 = com.google.zxing.client.android.encode.EncodeActivity.TAG
            android.util.Log.w(r0, r1)
            return
        L_0x000c:
            java.lang.String r2 = r0.getContents()
            if (r2 != 0) goto L_0x0018
            java.lang.String r0 = com.google.zxing.client.android.encode.EncodeActivity.TAG
            android.util.Log.w(r0, r1)
            return
        L_0x0018:
            android.graphics.Bitmap r1 = r0.encodeAsBitmap()     // Catch:{ WriterException -> 0x013b }
            if (r1 != 0) goto L_0x001f
            return
        L_0x001f:
            java.io.File r3 = new java.io.File
            java.io.File r4 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r5 = "BarcodeScanner"
            r3.<init>(r4, r5)
            java.io.File r4 = new java.io.File
            java.lang.String r5 = "Barcodes"
            r4.<init>(r3, r5)
            boolean r3 = r4.exists()
            if (r3 != 0) goto L_0x0059
            boolean r3 = r4.mkdirs()
            if (r3 != 0) goto L_0x0059
            java.lang.String r0 = com.google.zxing.client.android.encode.EncodeActivity.TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Couldn't make dir "
            r1.append(r2)
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            android.util.Log.w(r0, r1)
            int r0 = barcodescanner.xservices.nl.barcodescanner.R.string.msg_unmount_usb
            r8.showErrorMessage(r0)
            return
        L_0x0059:
            java.io.File r3 = new java.io.File
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.CharSequence r6 = makeBarcodeFileName(r2)
            r5.append(r6)
            java.lang.String r6 = ".png"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r3.<init>(r4, r5)
            boolean r4 = r3.delete()
            if (r4 != 0) goto L_0x008f
            java.lang.String r4 = com.google.zxing.client.android.encode.EncodeActivity.TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Could not delete "
            r5.append(r6)
            r5.append(r3)
            java.lang.String r5 = r5.toString()
            android.util.Log.w(r4, r5)
        L_0x008f:
            r4 = 0
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x010b }
            r5.<init>(r3)     // Catch:{ FileNotFoundException -> 0x010b }
            android.graphics.Bitmap$CompressFormat r6 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ FileNotFoundException -> 0x0106, all -> 0x0103 }
            r7 = 0
            r1.compress(r6, r7, r5)     // Catch:{ FileNotFoundException -> 0x0106, all -> 0x0103 }
            r5.close()     // Catch:{ IOException -> 0x009e }
        L_0x009e:
            android.content.Intent r1 = new android.content.Intent
            java.lang.String r5 = "mailto:"
            android.net.Uri r5 = android.net.Uri.parse(r5)
            java.lang.String r6 = "android.intent.action.SEND"
            r1.<init>(r6, r5)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            int r6 = barcodescanner.xservices.nl.barcodescanner.R.string.app_name
            java.lang.String r6 = r8.getString(r6)
            r5.append(r6)
            java.lang.String r6 = " - "
            r5.append(r6)
            java.lang.String r0 = r0.getTitle()
            r5.append(r0)
            java.lang.String r0 = r5.toString()
            java.lang.String r5 = "android.intent.extra.SUBJECT"
            r1.putExtra(r5, r0)
            java.lang.String r0 = "android.intent.extra.TEXT"
            r1.putExtra(r0, r2)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "file://"
            r0.append(r2)
            java.lang.String r2 = r3.getAbsolutePath()
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r0)
            java.lang.String r2 = "android.intent.extra.STREAM"
            r1.putExtra(r2, r0)
            java.lang.String r0 = "image/png"
            r1.setType(r0)
            r0 = 524288(0x80000, float:7.34684E-40)
            r1.addFlags(r0)
            android.content.Intent r0 = android.content.Intent.createChooser(r1, r4)
            r8.startActivity(r0)
            return
        L_0x0103:
            r0 = move-exception
            r4 = r5
            goto L_0x0135
        L_0x0106:
            r0 = move-exception
            r4 = r5
            goto L_0x010c
        L_0x0109:
            r0 = move-exception
            goto L_0x0135
        L_0x010b:
            r0 = move-exception
        L_0x010c:
            java.lang.String r1 = com.google.zxing.client.android.encode.EncodeActivity.TAG     // Catch:{ all -> 0x0109 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0109 }
            r2.<init>()     // Catch:{ all -> 0x0109 }
            java.lang.String r5 = "Couldn't access file "
            r2.append(r5)     // Catch:{ all -> 0x0109 }
            r2.append(r3)     // Catch:{ all -> 0x0109 }
            java.lang.String r3 = " due to "
            r2.append(r3)     // Catch:{ all -> 0x0109 }
            r2.append(r0)     // Catch:{ all -> 0x0109 }
            java.lang.String r0 = r2.toString()     // Catch:{ all -> 0x0109 }
            android.util.Log.w(r1, r0)     // Catch:{ all -> 0x0109 }
            int r0 = barcodescanner.xservices.nl.barcodescanner.R.string.msg_unmount_usb     // Catch:{ all -> 0x0109 }
            r8.showErrorMessage(r0)     // Catch:{ all -> 0x0109 }
            if (r4 == 0) goto L_0x0134
            r4.close()     // Catch:{ IOException -> 0x0134 }
        L_0x0134:
            return
        L_0x0135:
            if (r4 == 0) goto L_0x013a
            r4.close()     // Catch:{ IOException -> 0x013a }
        L_0x013a:
            throw r0
        L_0x013b:
            r0 = move-exception
            java.lang.String r1 = com.google.zxing.client.android.encode.EncodeActivity.TAG
            android.util.Log.w(r1, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.android.encode.EncodeActivity.share():void");
    }

    private static CharSequence makeBarcodeFileName(CharSequence charSequence) {
        String replaceAll = NOT_ALPHANUMERIC.matcher(charSequence).replaceAll("_");
        return replaceAll.length() > 24 ? replaceAll.substring(0, 24) : replaceAll;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int i = point.x;
        int i2 = point.y;
        if (i >= i2) {
            i = i2;
        }
        int i3 = (i * 7) / 8;
        Intent intent = getIntent();
        if (intent != null) {
            try {
                QRCodeEncoder qRCodeEncoder = new QRCodeEncoder(this, intent, i3, intent.getBooleanExtra(USE_VCARD_KEY, false));
                this.qrCodeEncoder = qRCodeEncoder;
                Bitmap encodeAsBitmap = qRCodeEncoder.encodeAsBitmap();
                if (encodeAsBitmap == null) {
                    Log.w(TAG, "Could not encode barcode");
                    showErrorMessage(R.string.msg_encode_contents_failed);
                    this.qrCodeEncoder = null;
                    return;
                }
                ((ImageView) findViewById(R.id.image_view)).setImageBitmap(encodeAsBitmap);
                TextView textView = (TextView) findViewById(R.id.contents_text_view);
                if (intent.getBooleanExtra(Intents.Encode.SHOW_CONTENTS, true)) {
                    textView.setText(this.qrCodeEncoder.getDisplayContents());
                    setTitle(this.qrCodeEncoder.getTitle());
                    return;
                }
                textView.setText("");
                setTitle("");
            } catch (WriterException e) {
                Log.w(TAG, "Could not encode barcode", e);
                showErrorMessage(R.string.msg_encode_contents_failed);
                this.qrCodeEncoder = null;
            }
        }
    }

    private void showErrorMessage(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(i);
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }
}
