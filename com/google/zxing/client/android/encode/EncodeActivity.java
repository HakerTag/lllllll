package com.google.zxing.client.android.encode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public final class EncodeActivity extends Activity {
    private static final int MAX_BARCODE_FILENAME_LENGTH = 24;
    private static final Pattern NOT_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");
    private static final String TAG = EncodeActivity.class.getSimpleName();
    private static final String USE_VCARD_KEY = "USE_VCARD";
    private QRCodeEncoder qrCodeEncoder;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
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
        int encodeNameResource = qRCodeEncoder != null && qRCodeEncoder.isUseVCard() ? R.string.menu_encode_mecard : R.string.menu_encode_vcard;
        MenuItem encodeItem = menu.findItem(R.id.menu_encode);
        encodeItem.setTitle(encodeNameResource);
        Intent intent = getIntent();
        if (intent != null) {
            encodeItem.setVisible(Contents.Type.CONTACT.equals(intent.getStringExtra(Intents.Encode.TYPE)));
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int i = item.getItemId();
        if (i == R.id.menu_share) {
            share();
            return true;
        } else if (i != R.id.menu_encode || (intent = getIntent()) == null) {
            return false;
        } else {
            intent.putExtra(USE_VCARD_KEY, !this.qrCodeEncoder.isUseVCard());
            intent.addFlags(67108864);
            startActivity(intent);
            finish();
            return true;
        }
    }

    private void share() {
        QRCodeEncoder encoder = this.qrCodeEncoder;
        if (encoder == null) {
            Log.w(TAG, "No existing barcode to send?");
            return;
        }
        String contents = encoder.getContents();
        if (contents == null) {
            Log.w(TAG, "No existing barcode to send?");
            return;
        }
        try {
            Bitmap bitmap = encoder.encodeAsBitmap();
            if (bitmap != null) {
                File barcodesRoot = new File(new File(Environment.getExternalStorageDirectory(), "BarcodeScanner"), "Barcodes");
                if (barcodesRoot.exists() || barcodesRoot.mkdirs()) {
                    File barcodeFile = new File(barcodesRoot, ((Object) makeBarcodeFileName(contents)) + ".png");
                    if (!barcodeFile.delete()) {
                        String str = TAG;
                        Log.w(str, "Could not delete " + barcodeFile);
                    }
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(barcodeFile);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                        Intent intent = new Intent("android.intent.action.SEND", Uri.parse("mailto:"));
                        intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name) + " - " + encoder.getTitle());
                        intent.putExtra("android.intent.extra.TEXT", contents);
                        intent.putExtra("android.intent.extra.STREAM", Uri.parse("file://" + barcodeFile.getAbsolutePath()));
                        intent.setType("image/png");
                        intent.addFlags(524288);
                        startActivity(Intent.createChooser(intent, null));
                    } catch (FileNotFoundException fnfe) {
                        String str2 = TAG;
                        Log.w(str2, "Couldn't access file " + barcodeFile + " due to " + fnfe);
                        showErrorMessage(R.string.msg_unmount_usb);
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e2) {
                            }
                        }
                    } catch (Throwable th) {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e3) {
                            }
                        }
                        throw th;
                    }
                } else {
                    String str3 = TAG;
                    Log.w(str3, "Couldn't make dir " + barcodesRoot);
                    showErrorMessage(R.string.msg_unmount_usb);
                }
            }
        } catch (WriterException we) {
            Log.w(TAG, we);
        }
    }

    private static CharSequence makeBarcodeFileName(CharSequence contents) {
        String fileName = NOT_ALPHANUMERIC.matcher(contents).replaceAll("_");
        if (fileName.length() > 24) {
            return fileName.substring(0, 24);
        }
        return fileName;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        Display display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        int width = displaySize.x;
        int height = displaySize.y;
        int smallerDimension = ((width < height ? width : height) * 7) / 8;
        Intent intent = getIntent();
        if (intent != null) {
            try {
                QRCodeEncoder qRCodeEncoder = new QRCodeEncoder(this, intent, smallerDimension, intent.getBooleanExtra(USE_VCARD_KEY, false));
                this.qrCodeEncoder = qRCodeEncoder;
                Bitmap bitmap = qRCodeEncoder.encodeAsBitmap();
                if (bitmap == null) {
                    Log.w(TAG, "Could not encode barcode");
                    showErrorMessage(R.string.msg_encode_contents_failed);
                    this.qrCodeEncoder = null;
                    return;
                }
                ((ImageView) findViewById(R.id.image_view)).setImageBitmap(bitmap);
                TextView contents = (TextView) findViewById(R.id.contents_text_view);
                if (intent.getBooleanExtra(Intents.Encode.SHOW_CONTENTS, true)) {
                    contents.setText(this.qrCodeEncoder.getDisplayContents());
                    setTitle(this.qrCodeEncoder.getTitle());
                    return;
                }
                contents.setText("");
                setTitle("");
            } catch (WriterException e) {
                Log.w(TAG, "Could not encode barcode", e);
                showErrorMessage(R.string.msg_encode_contents_failed);
                this.qrCodeEncoder = null;
            }
        }
    }

    private void showErrorMessage(int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }
}
