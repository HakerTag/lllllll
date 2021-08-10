package com.google.zxing.client.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.clipboard.ClipboardInterface;
import com.google.zxing.client.android.history.HistoryActivity;
import com.google.zxing.client.android.history.HistoryManager;
import com.google.zxing.client.android.result.ResultButtonListener;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;
import com.google.zxing.client.android.result.supplement.SupplementalInfoRetriever;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;

public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {
    private static final long BULK_MODE_SCAN_DELAY_MS = 1000;
    private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500;
    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet.of(ResultMetadataType.ISSUE_NUMBER, ResultMetadataType.SUGGESTED_PRICE, ResultMetadataType.ERROR_CORRECTION_LEVEL, ResultMetadataType.POSSIBLE_COUNTRY);
    private static final int HISTORY_REQUEST_CODE = 47820;
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final String[] ZXING_URLS = {"http://zxing.appspot.com/scan", "zxing://scan/"};
    private AmbientLightManager ambientLightManager;
    private BeepManager beepManager;
    private boolean beepOnScan;
    private CameraManager cameraManager;
    private String characterSet;
    private boolean copyToClipboard;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private Button flipButton;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private HistoryManager historyManager;
    private InactivityTimer inactivityTimer;
    private Result lastResult;
    private View resultView;
    private Result savedResultToShow;
    private ScanFromWebPageManager scanFromWebPageManager;
    private IntentSource source;
    private String sourceUrl;
    private TextView statusView;
    BroadcastReceiver stopReceiver;
    private Button torchButton;
    private ViewfinderView viewfinderView;

    /* access modifiers changed from: package-private */
    public ViewfinderView getViewfinderView() {
        return this.viewfinderView;
    }

    public Handler getHandler() {
        return this.handler;
    }

    /* access modifiers changed from: package-private */
    public CameraManager getCameraManager() {
        return this.cameraManager;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().addFlags(128);
        setContentView(R.layout.capture);
        this.hasSurface = false;
        this.inactivityTimer = new InactivityTimer(this);
        this.beepManager = new BeepManager(this);
        this.ambientLightManager = new AmbientLightManager(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.stopReceiver = new BroadcastReceiver() {
            /* class com.google.zxing.client.android.CaptureActivity.AnonymousClass1 */

            public void onReceive(Context context, Intent intent) {
                CaptureActivity.this.finish();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(this.stopReceiver, new IntentFilter("barcode-scanner-stop"));
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        int cameraId;
        super.onResume();
        HistoryManager historyManager2 = new HistoryManager(this);
        this.historyManager = historyManager2;
        historyManager2.trimHistory();
        this.cameraManager = new CameraManager(getApplication());
        ViewfinderView viewfinderView2 = (ViewfinderView) findViewById(R.id.viewfinder_view);
        this.viewfinderView = viewfinderView2;
        viewfinderView2.setCameraManager(this.cameraManager);
        this.resultView = findViewById(R.id.result_view);
        this.statusView = (TextView) findViewById(R.id.status_view);
        this.flipButton = (Button) findViewById(R.id.flip_button);
        this.torchButton = (Button) findViewById(R.id.torch_button);
        this.handler = null;
        this.lastResult = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String scanOrientationLock = getIntent().getStringExtra(Intents.Scan.ORIENTATION_LOCK);
        if ("landscape".equalsIgnoreCase(scanOrientationLock)) {
            setRequestedOrientation(0);
        } else if ("portrait".equalsIgnoreCase(scanOrientationLock)) {
            setRequestedOrientation(1);
        }
        resetStatusView();
        this.beepManager.updatePrefs();
        this.ambientLightManager.start(this.cameraManager);
        this.inactivityTimer.onResume();
        Intent intent = getIntent();
        this.copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true) && (intent == null || intent.getBooleanExtra(Intents.Scan.SAVE_HISTORY, true));
        this.beepOnScan = intent == null || intent.getBooleanExtra(Intents.Scan.BEEP_ON_SCAN, true);
        this.source = IntentSource.NONE;
        this.sourceUrl = null;
        this.scanFromWebPageManager = null;
        this.decodeFormats = null;
        this.characterSet = null;
        if (intent != null) {
            String action = intent.getAction();
            String dataString = intent.getDataString();
            if (Intents.Scan.ACTION.equals(action)) {
                this.source = IntentSource.NATIVE_APP_INTENT;
                this.decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                this.decodeHints = DecodeHintManager.parseDecodeHints(intent);
                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        this.cameraManager.setManualFramingRect(width, height);
                    }
                }
                if (intent.hasExtra(Intents.Scan.CAMERA_ID) && (cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1)) >= 0) {
                    this.cameraManager.setManualCameraId(cameraId);
                }
                if (intent.getBooleanExtra(Intents.Scan.TORCH_ON, false)) {
                    this.cameraManager.setTorchInitiallyOn(true);
                }
                String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
                if (customPromptMessage != null) {
                    this.statusView.setText(customPromptMessage);
                }
            } else if (dataString != null && dataString.contains("http://www.google") && dataString.contains("/m/products/scan")) {
                this.source = IntentSource.PRODUCT_SEARCH_LINK;
                this.sourceUrl = dataString;
                this.decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;
            } else if (isZXingURL(dataString)) {
                this.source = IntentSource.ZXING_LINK;
                this.sourceUrl = dataString;
                Uri inputUri = Uri.parse(dataString);
                this.scanFromWebPageManager = new ScanFromWebPageManager(inputUri);
                this.decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
                this.decodeHints = DecodeHintManager.parseDecodeHints(inputUri);
            }
            this.characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
        }
        SurfaceHolder surfaceHolder = ((SurfaceView) findViewById(R.id.preview_view)).getHolder();
        if (this.hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }
    }

    private int getCurrentOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if (getResources().getConfiguration().orientation == 2) {
            if (rotation == 0 || rotation == 1) {
                return 0;
            }
            return 8;
        } else if (rotation == 0 || rotation == 3) {
            return 1;
        } else {
            return 9;
        }
    }

    private static boolean isZXingURL(String dataString) {
        if (dataString == null) {
            return false;
        }
        for (String url : ZXING_URLS) {
            if (dataString.startsWith(url)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        CaptureActivityHandler captureActivityHandler = this.handler;
        if (captureActivityHandler != null) {
            captureActivityHandler.quitSynchronously();
            this.handler = null;
        }
        this.inactivityTimer.onPause();
        this.ambientLightManager.stop();
        this.beepManager.close();
        this.cameraManager.closeDriver();
        if (!this.hasSurface) {
            ((SurfaceView) findViewById(R.id.preview_view)).getHolder().removeCallback(this);
        }
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.inactivityTimer.shutdown();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.stopReceiver);
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            if (keyCode == 27 || keyCode == 80) {
                return true;
            }
            if (keyCode == 24) {
                this.cameraManager.setTorch(true);
                return true;
            } else if (keyCode == 25) {
                this.cameraManager.setTorch(false);
                return true;
            }
        } else if (this.source == IntentSource.NATIVE_APP_INTENT) {
            setResult(0);
            finish();
            return true;
        } else if ((this.source == IntentSource.NONE || this.source == IntentSource.ZXING_LINK) && this.lastResult != null) {
            restartPreviewAfterDelay(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(524288);
        int itemId = item.getItemId();
        if (itemId == R.id.menu_history) {
            intent.setClassName(this, HistoryActivity.class.getName());
            startActivityForResult(intent, HISTORY_REQUEST_CODE);
            return true;
        } else if (itemId == R.id.menu_settings) {
            intent.setClassName(this, PreferencesActivity.class.getName());
            startActivity(intent);
            return true;
        } else if (itemId != R.id.menu_help) {
            return super.onOptionsItemSelected(item);
        } else {
            intent.setClassName(this, HelpActivity.class.getName());
            startActivity(intent);
            return true;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        int itemNumber;
        if (resultCode == -1 && requestCode == HISTORY_REQUEST_CODE && this.historyManager != null && (itemNumber = intent.getIntExtra(Intents.History.ITEM_NUMBER, -1)) >= 0) {
            decodeOrStoreSavedBitmap(null, this.historyManager.buildHistoryItem(itemNumber).getResult());
        }
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        if (this.handler == null) {
            this.savedResultToShow = result;
            return;
        }
        if (result != null) {
            this.savedResultToShow = result;
        }
        if (this.savedResultToShow != null) {
            this.handler.sendMessage(Message.obtain(this.handler, R.id.decode_succeeded, this.savedResultToShow));
        }
        this.savedResultToShow = null;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!this.hasSurface) {
            this.hasSurface = true;
            initCamera(holder);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.hasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        this.inactivityTimer.onActivity();
        this.lastResult = rawResult;
        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            this.historyManager.addHistoryItem(rawResult, resultHandler);
            if (this.beepOnScan) {
                this.beepManager.playBeepSoundAndVibrate();
            }
            drawResultPoints(barcode, scaleFactor, rawResult);
        }
        int i = AnonymousClass4.$SwitchMap$com$google$zxing$client$android$IntentSource[this.source.ordinal()];
        if (i == 1 || i == 2) {
            if (!fromLiveScan || !getIntent().getBooleanExtra(Intents.Scan.BULK_SCAN, false)) {
                handleDecodeExternally(rawResult, resultHandler, barcode);
                return;
            }
            Intent intermediateResult = new Intent("bulk-barcode-result");
            intermediateResult.putExtra(Intents.Scan.RESULT, rawResult.toString());
            intermediateResult.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intermediateResult);
            restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
        } else if (i == 3) {
            ScanFromWebPageManager scanFromWebPageManager2 = this.scanFromWebPageManager;
            if (scanFromWebPageManager2 == null || !scanFromWebPageManager2.isScanFromWebPage()) {
                handleDecodeInternally(rawResult, resultHandler, barcode);
            } else {
                handleDecodeExternally(rawResult, resultHandler, barcode);
            }
        } else if (i == 4) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (!fromLiveScan || !prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
                handleDecodeInternally(rawResult, resultHandler, barcode);
                return;
            }
            Context applicationContext = getApplicationContext();
            Toast.makeText(applicationContext, getResources().getString(R.string.msg_bulk_mode_scanned) + " (" + rawResult.getText() + ')', 0).show();
            restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.google.zxing.client.android.CaptureActivity$4  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$client$android$IntentSource;

        static {
            int[] iArr = new int[IntentSource.values().length];
            $SwitchMap$com$google$zxing$client$android$IntentSource = iArr;
            try {
                iArr[IntentSource.NATIVE_APP_INTENT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$zxing$client$android$IntentSource[IntentSource.PRODUCT_SEARCH_LINK.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$zxing$client$android$IntentSource[IntentSource.ZXING_LINK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$zxing$client$android$IntentSource[IntentSource.NONE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4 && (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    if (point != null) {
                        canvas.drawPoint(point.getX() * scaleFactor, point.getY() * scaleFactor, paint);
                    }
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
        if (a != null && b != null) {
            canvas.drawLine(scaleFactor * a.getX(), scaleFactor * a.getY(), scaleFactor * b.getX(), scaleFactor * b.getY(), paint);
        }
    }

    private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
        int buttonCount;
        CharSequence displayContents = resultHandler.getDisplayContents();
        if (this.copyToClipboard && !resultHandler.areContentsSecure()) {
            ClipboardInterface.setText(displayContents, this);
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (resultHandler.getDefaultButtonID() == null || !prefs.getBoolean(PreferencesActivity.KEY_AUTO_OPEN_WEB, false)) {
            this.statusView.setVisibility(8);
            this.viewfinderView.setVisibility(8);
            this.resultView.setVisibility(0);
            ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
            if (barcode == null) {
                barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.launcher_icon));
            } else {
                barcodeImageView.setImageBitmap(barcode);
            }
            ((TextView) findViewById(R.id.format_text_view)).setText(rawResult.getBarcodeFormat().toString());
            ((TextView) findViewById(R.id.type_text_view)).setText(resultHandler.getType().toString());
            ((TextView) findViewById(R.id.time_text_view)).setText(DateFormat.getDateTimeInstance(3, 3).format(new Date(rawResult.getTimestamp())));
            TextView metaTextView = (TextView) findViewById(R.id.meta_text_view);
            View metaTextViewLabel = findViewById(R.id.meta_text_view_label);
            metaTextView.setVisibility(8);
            metaTextViewLabel.setVisibility(8);
            Map<ResultMetadataType, Object> metadata = rawResult.getResultMetadata();
            if (metadata != null) {
                StringBuilder metadataText = new StringBuilder(20);
                for (Map.Entry<ResultMetadataType, Object> entry : metadata.entrySet()) {
                    if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
                        metadataText.append(entry.getValue());
                        metadataText.append('\n');
                    }
                }
                if (metadataText.length() > 0) {
                    metadataText.setLength(metadataText.length() - 1);
                    metaTextView.setText(metadataText);
                    metaTextView.setVisibility(0);
                    metaTextViewLabel.setVisibility(0);
                }
            }
            TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
            contentsTextView.setText(displayContents);
            contentsTextView.setTextSize(2, (float) Math.max(22, 32 - (displayContents.length() / 4)));
            TextView supplementTextView = (TextView) findViewById(R.id.contents_supplement_text_view);
            supplementTextView.setText("");
            supplementTextView.setOnClickListener(null);
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PreferencesActivity.KEY_SUPPLEMENTAL, true)) {
                SupplementalInfoRetriever.maybeInvokeRetrieval(supplementTextView, resultHandler.getResult(), this.historyManager, this);
            }
            int buttonCount2 = resultHandler.getButtonCount();
            ViewGroup buttonView = (ViewGroup) findViewById(R.id.result_button_view);
            buttonView.requestFocus();
            int x = 0;
            while (x < 4) {
                TextView button = (TextView) buttonView.getChildAt(x);
                if (x < buttonCount2) {
                    buttonCount = buttonCount2;
                    button.setVisibility(0);
                    button.setText(resultHandler.getButtonText(x));
                    button.setOnClickListener(new ResultButtonListener(resultHandler, x));
                } else {
                    buttonCount = buttonCount2;
                    button.setVisibility(8);
                }
                x++;
                buttonCount2 = buttonCount;
            }
            return;
        }
        resultHandler.handleButtonPress(resultHandler.getDefaultButtonID().intValue());
    }

    private void handleDecodeExternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
        String durationStr;
        if (barcode != null) {
            this.viewfinderView.drawResultBitmap(barcode);
        }
        long resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
        resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
        resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
        resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
        if (!(getIntent() == null || !getIntent().hasExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS) || (durationStr = getIntent().getStringExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS)) == null)) {
            try {
                resultDurationMS = Long.parseLong(durationStr);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Could not parse " + durationStr + " to Long", e);
            }
        }
        if (resultDurationMS > 0) {
            String rawResultString = String.valueOf(rawResult);
            if (rawResultString.length() > 32) {
                rawResultString = rawResultString.substring(0, 32) + " ...";
            }
            this.statusView.setText(getString(resultHandler.getDisplayTitle()) + " : " + rawResultString);
        }
        if (this.copyToClipboard && !resultHandler.areContentsSecure()) {
            ClipboardInterface.setText(resultHandler.getDisplayContents(), this);
        }
        if (this.source == IntentSource.NATIVE_APP_INTENT) {
            Intent intent = new Intent(getIntent().getAction());
            intent.addFlags(524288);
            intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
            intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
            byte[] rawBytes = rawResult.getRawBytes();
            if (rawBytes != null && rawBytes.length > 0) {
                intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
            }
            Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
            if (metadata != null) {
                if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
                    intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION, metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
                }
                Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
                if (orientation != null) {
                    intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
                }
                String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
                if (ecLevel != null) {
                    intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
                }
                Iterable<byte[]> byteSegments = (Iterable) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
                if (byteSegments != null) {
                    int i = 0;
                    for (byte[] byteSegment : byteSegments) {
                        intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
                        i++;
                    }
                }
            }
            sendReplyMessage(R.id.return_scan_result, intent, resultDurationMS);
        } else if (this.source == IntentSource.PRODUCT_SEARCH_LINK) {
            int end = this.sourceUrl.lastIndexOf("/scan");
            sendReplyMessage(R.id.launch_product_query, this.sourceUrl.substring(0, end) + "?q=" + ((Object) resultHandler.getDisplayContents()) + "&source=zxing", resultDurationMS);
        } else if (this.source == IntentSource.ZXING_LINK) {
            ScanFromWebPageManager scanFromWebPageManager2 = this.scanFromWebPageManager;
            if (scanFromWebPageManager2 != null && scanFromWebPageManager2.isScanFromWebPage()) {
                String replyURL = this.scanFromWebPageManager.buildReplyURL(rawResult, resultHandler);
                this.scanFromWebPageManager = null;
                sendReplyMessage(R.id.launch_product_query, replyURL, resultDurationMS);
            }
        }
    }

    private void sendReplyMessage(int id, Object arg, long delayMS) {
        CaptureActivityHandler captureActivityHandler = this.handler;
        if (captureActivityHandler != null) {
            Message message = Message.obtain(captureActivityHandler, id, arg);
            if (delayMS > 0) {
                this.handler.sendMessageDelayed(message, delayMS);
            } else {
                this.handler.sendMessage(message);
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        } else if (this.cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
        } else {
            try {
                this.cameraManager.openDriver(surfaceHolder);
                if (this.handler == null) {
                    this.handler = new CaptureActivityHandler(this, this.decodeFormats, this.decodeHints, this.characterSet, this.cameraManager);
                }
                decodeOrStoreSavedBitmap(null, null);
            } catch (IOException ioe) {
                Log.w(TAG, ioe);
                displayFrameworkBugMessageAndExit();
            } catch (RuntimeException e) {
                Log.w(TAG, "Unexpected error initializing camera", e);
                displayFrameworkBugMessageAndExit();
            }
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        CaptureActivityHandler captureActivityHandler = this.handler;
        if (captureActivityHandler != null) {
            captureActivityHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {
        this.resultView.setVisibility(8);
        this.statusView.setText(R.string.msg_default_status);
        this.statusView.setVisibility(0);
        this.viewfinderView.setVisibility(0);
        this.lastResult = null;
        if (getIntent().getBooleanExtra(Intents.Scan.SHOW_FLIP_CAMERA_BUTTON, false) && Camera.getNumberOfCameras() > 1) {
            this.flipButton.setVisibility(0);
            this.flipButton.setOnClickListener(new View.OnClickListener() {
                /* class com.google.zxing.client.android.CaptureActivity.AnonymousClass2 */

                public void onClick(View v) {
                    CaptureActivity.this.getIntent().putExtra(Intents.Scan.CAMERA_ID, CaptureActivity.this.getIntent().getIntExtra(Intents.Scan.CAMERA_ID, -1) == 1 ? 0 : 1);
                    CaptureActivity.this.getIntent().putExtra(Intents.Scan.SHOW_FLIP_CAMERA_BUTTON, true);
                    CaptureActivity.this.recreate();
                }
            });
        }
        if (getIntent().getBooleanExtra(Intents.Scan.SHOW_TORCH_BUTTON, false) && getIntent().getIntExtra(Intents.Scan.CAMERA_ID, -1) != 1) {
            for (FeatureInfo feature : getPackageManager().getSystemAvailableFeatures()) {
                if ("android.hardware.camera.flash".equalsIgnoreCase(feature.name)) {
                    this.torchButton.setVisibility(0);
                    this.torchButton.setOnClickListener(new View.OnClickListener() {
                        /* class com.google.zxing.client.android.CaptureActivity.AnonymousClass3 */

                        public void onClick(View v) {
                            CaptureActivity.this.cameraManager.setTorch(!CaptureActivity.this.cameraManager.isTorchOn());
                        }
                    });
                    return;
                }
            }
        }
    }

    public void drawViewfinder() {
        this.viewfinderView.drawViewfinder();
    }
}
