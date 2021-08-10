package com.google.zxing.client.android.result;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.wifi.WifiConfigManager;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.WifiParsedResult;

public final class WifiResultHandler extends ResultHandler {
    private static final String TAG = WifiResultHandler.class.getSimpleName();
    private final CaptureActivity parent;

    public WifiResultHandler(CaptureActivity activity, ParsedResult result) {
        super(activity, result);
        this.parent = activity;
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonCount() {
        return 1;
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonText(int index) {
        return R.string.button_wifi;
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public void handleButtonPress(int index) {
        if (index == 0) {
            WifiParsedResult wifiResult = (WifiParsedResult) getResult();
            WifiManager wifiManager = (WifiManager) getActivity().getSystemService("wifi");
            if (wifiManager == null) {
                Log.w(TAG, "No WifiManager available from device");
                return;
            }
            final Activity activity = getActivity();
            activity.runOnUiThread(new Runnable() {
                /* class com.google.zxing.client.android.result.WifiResultHandler.AnonymousClass1 */

                public void run() {
                    Toast.makeText(activity.getApplicationContext(), R.string.wifi_changing_network, 0).show();
                }
            });
            new WifiConfigManager(wifiManager).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, wifiResult);
            this.parent.restartPreviewAfterDelay(0);
        }
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public CharSequence getDisplayContents() {
        WifiParsedResult wifiResult = (WifiParsedResult) getResult();
        return wifiResult.getSsid() + " (" + wifiResult.getNetworkEncryption() + ')';
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getDisplayTitle() {
        return R.string.result_wifi;
    }
}
