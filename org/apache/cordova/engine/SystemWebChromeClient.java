package org.apache.cordova.engine;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import java.util.Arrays;
import org.apache.cordova.CordovaDialogsHelper;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;

public class SystemWebChromeClient extends WebChromeClient {
    private static final int FILECHOOSER_RESULTCODE = 5173;
    private static final String LOG_TAG = "SystemWebChromeClient";
    private long MAX_QUOTA = 104857600;
    private Context appContext;
    private CordovaDialogsHelper dialogsHelper;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private View mVideoProgressView;
    protected final SystemWebViewEngine parentEngine;

    public SystemWebChromeClient(SystemWebViewEngine parentEngine2) {
        this.parentEngine = parentEngine2;
        Context context = parentEngine2.webView.getContext();
        this.appContext = context;
        this.dialogsHelper = new CordovaDialogsHelper(context);
    }

    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        this.dialogsHelper.showAlert(message, new CordovaDialogsHelper.Result() {
            /* class org.apache.cordova.engine.SystemWebChromeClient.AnonymousClass1 */

            @Override // org.apache.cordova.CordovaDialogsHelper.Result
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        this.dialogsHelper.showConfirm(message, new CordovaDialogsHelper.Result() {
            /* class org.apache.cordova.engine.SystemWebChromeClient.AnonymousClass2 */

            @Override // org.apache.cordova.CordovaDialogsHelper.Result
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    public boolean onJsPrompt(WebView view, String origin, String message, String defaultValue, final JsPromptResult result) {
        String handledRet = this.parentEngine.bridge.promptOnJsPrompt(origin, message, defaultValue);
        if (handledRet != null) {
            result.confirm(handledRet);
            return true;
        }
        this.dialogsHelper.showPrompt(message, defaultValue, new CordovaDialogsHelper.Result() {
            /* class org.apache.cordova.engine.SystemWebChromeClient.AnonymousClass3 */

            @Override // org.apache.cordova.CordovaDialogsHelper.Result
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm(value);
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
        LOG.d(LOG_TAG, "onExceededDatabaseQuota estimatedSize: %d  currentQuota: %d  totalUsedQuota: %d", Long.valueOf(estimatedSize), Long.valueOf(currentQuota), Long.valueOf(totalUsedQuota));
        quotaUpdater.updateQuota(this.MAX_QUOTA);
    }

    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
        callback.invoke(origin, true, false);
        CordovaPlugin geolocation = this.parentEngine.pluginManager.getPlugin("Geolocation");
        if (geolocation != null && !geolocation.hasPermisssion()) {
            geolocation.requestPermissions(0);
        }
    }

    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        this.parentEngine.getCordovaWebView().showCustomView(view, callback);
    }

    public void onHideCustomView() {
        this.parentEngine.getCordovaWebView().hideCustomView();
    }

    public View getVideoLoadingProgressView() {
        if (this.mVideoProgressView == null) {
            LinearLayout layout = new LinearLayout(this.parentEngine.getView().getContext());
            layout.setOrientation(1);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams.addRule(13);
            layout.setLayoutParams(layoutParams);
            ProgressBar bar = new ProgressBar(this.parentEngine.getView().getContext());
            LinearLayout.LayoutParams barLayoutParams = new LinearLayout.LayoutParams(-2, -2);
            barLayoutParams.gravity = 17;
            bar.setLayoutParams(barLayoutParams);
            layout.addView(bar);
            this.mVideoProgressView = layout;
        }
        return this.mVideoProgressView;
    }

    @Override // android.webkit.WebChromeClient
    public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathsCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        boolean selectMultiple = false;
        if (fileChooserParams.getMode() == 1) {
            selectMultiple = true;
        }
        Intent intent = fileChooserParams.createIntent();
        intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", selectMultiple);
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        if (acceptTypes.length > 1) {
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.MIME_TYPES", acceptTypes);
        }
        try {
            this.parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
                /* class org.apache.cordova.engine.SystemWebChromeClient.AnonymousClass4 */

                @Override // org.apache.cordova.CordovaPlugin
                public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                    Uri[] result = null;
                    if (resultCode == -1 && intent != null) {
                        if (intent.getClipData() != null) {
                            int numSelectedFiles = intent.getClipData().getItemCount();
                            result = new Uri[numSelectedFiles];
                            for (int i = 0; i < numSelectedFiles; i++) {
                                result[i] = intent.getClipData().getItemAt(i).getUri();
                                LOG.d(SystemWebChromeClient.LOG_TAG, "Receive file chooser URL: " + result[i]);
                            }
                        } else if (intent.getData() != null) {
                            result = WebChromeClient.FileChooserParams.parseResult(resultCode, intent);
                            LOG.d(SystemWebChromeClient.LOG_TAG, "Receive file chooser URL: " + result);
                        }
                    }
                    filePathsCallback.onReceiveValue(result);
                }
            }, intent, FILECHOOSER_RESULTCODE);
        } catch (ActivityNotFoundException e) {
            LOG.w("No activity found to handle file chooser intent.", e);
            filePathsCallback.onReceiveValue(null);
        }
        return true;
    }

    public void onPermissionRequest(PermissionRequest request) {
        LOG.d(LOG_TAG, "onPermissionRequest: " + Arrays.toString(request.getResources()));
        request.grant(request.getResources());
    }

    public void destroyLastDialog() {
        this.dialogsHelper.destroyLastDialog();
    }
}
