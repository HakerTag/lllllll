package org.apache.cordova.splashscreen;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class SplashScreen extends CordovaPlugin {
    private static final int DEFAULT_FADE_DURATION = 500;
    private static final int DEFAULT_SPLASHSCREEN_DURATION = 3000;
    private static final boolean HAS_BUILT_IN_SPLASH_SCREEN;
    private static final String LOG_TAG = "SplashScreen";
    private static boolean firstShow = true;
    private static boolean lastHideAfterDelay;
    private static ProgressDialog spinnerDialog;
    private static Dialog splashDialog;
    private int orientation;
    private ImageView splashImageView;

    static {
        boolean z = false;
        if (Integer.valueOf(CordovaWebView.CORDOVA_VERSION.split("\\.")[0]).intValue() < 4) {
            z = true;
        }
        HAS_BUILT_IN_SPLASH_SCREEN = z;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private View getView() {
        try {
            return (View) this.webView.getClass().getMethod("getView", new Class[0]).invoke(this.webView, new Object[0]);
        } catch (Exception e) {
            return (View) this.webView;
        }
    }

    private int getSplashId() {
        String splashResource = this.preferences.getString(LOG_TAG, "screen");
        if (splashResource == null) {
            return 0;
        }
        int drawableId = this.cordova.getActivity().getResources().getIdentifier(splashResource, "drawable", this.cordova.getActivity().getClass().getPackage().getName());
        if (drawableId == 0) {
            return this.cordova.getActivity().getResources().getIdentifier(splashResource, "drawable", this.cordova.getActivity().getPackageName());
        }
        return drawableId;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.cordova.CordovaPlugin
    public void pluginInitialize() {
        if (!HAS_BUILT_IN_SPLASH_SCREEN) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass1 */

                public void run() {
                    SplashScreen.this.getView().setVisibility(4);
                }
            });
            getSplashId();
            this.orientation = this.cordova.getActivity().getResources().getConfiguration().orientation;
            if (firstShow) {
                showSplashScreen(this.preferences.getBoolean("AutoHideSplashScreen", true));
            }
            if (this.preferences.getBoolean("SplashShowOnlyFirstTime", true)) {
                firstShow = false;
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isMaintainAspectRatio() {
        return this.preferences.getBoolean("SplashMaintainAspectRatio", false);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getFadeDuration() {
        int fadeSplashScreenDuration = this.preferences.getBoolean("FadeSplashScreen", true) ? this.preferences.getInteger("FadeSplashScreenDuration", DEFAULT_FADE_DURATION) : 0;
        if (fadeSplashScreenDuration < 30) {
            return fadeSplashScreenDuration * 1000;
        }
        return fadeSplashScreenDuration;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onPause(boolean multitasking) {
        if (!HAS_BUILT_IN_SPLASH_SCREEN) {
            removeSplashScreen(true);
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onDestroy() {
        if (!HAS_BUILT_IN_SPLASH_SCREEN) {
            removeSplashScreen(true);
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("hide")) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass2 */

                public void run() {
                    SplashScreen.this.webView.postMessage("splashscreen", "hide");
                }
            });
        } else if (!action.equals("show")) {
            return false;
        } else {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass3 */

                public void run() {
                    SplashScreen.this.webView.postMessage("splashscreen", "show");
                }
            });
        }
        callbackContext.success();
        return true;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public Object onMessage(String id, Object data) {
        if (HAS_BUILT_IN_SPLASH_SCREEN) {
            return null;
        }
        if ("splashscreen".equals(id)) {
            if ("hide".equals(data.toString())) {
                removeSplashScreen(false);
            } else {
                showSplashScreen(false);
            }
        } else if ("spinner".equals(id)) {
            if ("stop".equals(data.toString())) {
                getView().setVisibility(0);
            }
        } else if ("onReceivedError".equals(id)) {
            spinnerStop();
        }
        return null;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onConfigurationChanged(Configuration newConfig) {
        int drawableId;
        if (newConfig.orientation != this.orientation) {
            this.orientation = newConfig.orientation;
            if (this.splashImageView != null && (drawableId = getSplashId()) != 0) {
                this.splashImageView.setImageDrawable(this.cordova.getActivity().getResources().getDrawable(drawableId));
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void removeSplashScreen(final boolean forceHideImmediately) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass4 */

            public void run() {
                if (SplashScreen.splashDialog != null && SplashScreen.this.splashImageView != null && SplashScreen.splashDialog.isShowing()) {
                    int fadeSplashScreenDuration = SplashScreen.this.getFadeDuration();
                    if (fadeSplashScreenDuration <= 0 || forceHideImmediately) {
                        SplashScreen.this.spinnerStop();
                        SplashScreen.splashDialog.dismiss();
                        Dialog unused = SplashScreen.splashDialog = null;
                        SplashScreen.this.splashImageView = null;
                        return;
                    }
                    AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                    fadeOut.setInterpolator(new DecelerateInterpolator());
                    fadeOut.setDuration((long) fadeSplashScreenDuration);
                    SplashScreen.this.splashImageView.setAnimation(fadeOut);
                    SplashScreen.this.splashImageView.startAnimation(fadeOut);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass4.AnonymousClass1 */

                        public void onAnimationStart(Animation animation) {
                            SplashScreen.this.spinnerStop();
                        }

                        public void onAnimationEnd(Animation animation) {
                            if (SplashScreen.splashDialog != null && SplashScreen.this.splashImageView != null && SplashScreen.splashDialog.isShowing()) {
                                SplashScreen.splashDialog.dismiss();
                                Dialog unused = SplashScreen.splashDialog = null;
                                SplashScreen.this.splashImageView = null;
                            }
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            }
        });
    }

    private void showSplashScreen(final boolean hideAfterDelay) {
        int splashscreenTime = this.preferences.getInteger("SplashScreenDelay", DEFAULT_SPLASHSCREEN_DURATION);
        final int drawableId = getSplashId();
        final int effectiveSplashDuration = Math.max(0, splashscreenTime - getFadeDuration());
        lastHideAfterDelay = hideAfterDelay;
        if (!this.cordova.getActivity().isFinishing()) {
            Dialog dialog = splashDialog;
            if ((dialog != null && dialog.isShowing()) || drawableId == 0) {
                return;
            }
            if (splashscreenTime > 0 || !hideAfterDelay) {
                this.cordova.getActivity().runOnUiThread(new Runnable() {
                    /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass5 */

                    public void run() {
                        Display display = SplashScreen.this.cordova.getActivity().getWindowManager().getDefaultDisplay();
                        Context context = SplashScreen.this.webView.getContext();
                        SplashScreen.this.splashImageView = new ImageView(context);
                        SplashScreen.this.splashImageView.setImageResource(drawableId);
                        SplashScreen.this.splashImageView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
                        SplashScreen.this.splashImageView.setMinimumHeight(display.getHeight());
                        SplashScreen.this.splashImageView.setMinimumWidth(display.getWidth());
                        SplashScreen.this.splashImageView.setBackgroundColor(SplashScreen.this.preferences.getInteger("backgroundColor", ViewCompat.MEASURED_STATE_MASK));
                        if (SplashScreen.this.isMaintainAspectRatio()) {
                            SplashScreen.this.splashImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        } else {
                            SplashScreen.this.splashImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        Dialog unused = SplashScreen.splashDialog = new Dialog(context, 16973840);
                        if ((SplashScreen.this.cordova.getActivity().getWindow().getAttributes().flags & 1024) == 1024) {
                            SplashScreen.splashDialog.getWindow().setFlags(1024, 1024);
                        }
                        SplashScreen.splashDialog.setContentView(SplashScreen.this.splashImageView);
                        SplashScreen.splashDialog.setCancelable(false);
                        SplashScreen.splashDialog.show();
                        if (SplashScreen.this.preferences.getBoolean("ShowSplashScreenSpinner", true)) {
                            SplashScreen.this.spinnerStart();
                        }
                        if (hideAfterDelay) {
                            new Handler().postDelayed(new Runnable() {
                                /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass5.AnonymousClass1 */

                                public void run() {
                                    if (SplashScreen.lastHideAfterDelay) {
                                        SplashScreen.this.removeSplashScreen(false);
                                    }
                                }
                            }, (long) effectiveSplashDuration);
                        }
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void spinnerStart() {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass6 */

            public void run() {
                String colorName;
                SplashScreen.this.spinnerStop();
                ProgressDialog unused = SplashScreen.spinnerDialog = new ProgressDialog(SplashScreen.this.webView.getContext());
                SplashScreen.spinnerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass6.AnonymousClass1 */

                    public void onCancel(DialogInterface dialog) {
                        ProgressDialog unused = SplashScreen.spinnerDialog = null;
                    }
                });
                SplashScreen.spinnerDialog.setCancelable(false);
                SplashScreen.spinnerDialog.setIndeterminate(true);
                RelativeLayout centeredLayout = new RelativeLayout(SplashScreen.this.cordova.getActivity());
                centeredLayout.setGravity(17);
                centeredLayout.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                ProgressBar progressBar = new ProgressBar(SplashScreen.this.webView.getContext());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
                layoutParams.addRule(13, -1);
                progressBar.setLayoutParams(layoutParams);
                if (Build.VERSION.SDK_INT >= 21 && (colorName = SplashScreen.this.preferences.getString("SplashScreenSpinnerColor", null)) != null) {
                    int progressBarColor = Color.parseColor(colorName);
                    progressBar.setIndeterminateTintList(new ColorStateList(new int[][]{new int[]{16842910}, new int[]{-16842910}, new int[]{-16842912}, new int[]{16842919}}, new int[]{progressBarColor, progressBarColor, progressBarColor, progressBarColor}));
                }
                centeredLayout.addView(progressBar);
                SplashScreen.spinnerDialog.getWindow().clearFlags(2);
                SplashScreen.spinnerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                SplashScreen.spinnerDialog.show();
                SplashScreen.spinnerDialog.setContentView(centeredLayout);
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void spinnerStop() {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass7 */

            public void run() {
                if (SplashScreen.spinnerDialog != null && SplashScreen.spinnerDialog.isShowing()) {
                    SplashScreen.spinnerDialog.dismiss();
                    ProgressDialog unused = SplashScreen.spinnerDialog = null;
                }
            }
        });
    }
}
