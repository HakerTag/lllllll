package org.apache.cordova.splashscreen;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.Display;
import android.view.View;
import android.view.Window;
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
import org.apache.cordova.LOG;
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
        } catch (Exception unused) {
            return (View) this.webView;
        }
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.cordova.CordovaPlugin
    public void pluginInitialize() {
        String string;
        if (!HAS_BUILT_IN_SPLASH_SCREEN) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass1 */

                public void run() {
                    SplashScreen.this.getView().setVisibility(4);
                }
            });
            if (this.preferences.getInteger("SplashDrawableId", 0) == 0 && (string = this.preferences.getString(LOG_TAG, "screen")) != null) {
                int identifier = this.cordova.getActivity().getResources().getIdentifier(string, "drawable", this.cordova.getActivity().getClass().getPackage().getName());
                if (identifier == 0) {
                    identifier = this.cordova.getActivity().getResources().getIdentifier(string, "drawable", this.cordova.getActivity().getPackageName());
                }
                this.preferences.set("SplashDrawableId", identifier);
            }
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
        int integer = this.preferences.getBoolean("FadeSplashScreen", true) ? this.preferences.getInteger("FadeSplashScreenDuration", DEFAULT_FADE_DURATION) : 0;
        return integer < 30 ? integer * 1000 : integer;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onPause(boolean z) {
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
    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        if (str.equals("hide")) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass2 */

                public void run() {
                    SplashScreen.this.webView.postMessage("splashscreen", "hide");
                }
            });
        } else if (!str.equals("show")) {
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
    public Object onMessage(String str, Object obj) {
        if (HAS_BUILT_IN_SPLASH_SCREEN) {
            return null;
        }
        if ("splashscreen".equals(str)) {
            if ("hide".equals(obj.toString())) {
                removeSplashScreen(false);
            } else {
                showSplashScreen(false);
            }
        } else if ("spinner".equals(str)) {
            if ("stop".equals(obj.toString())) {
                getView().setVisibility(0);
            }
        } else if ("onReceivedError".equals(str)) {
            spinnerStop();
        }
        return null;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onConfigurationChanged(Configuration configuration) {
        int integer;
        if (configuration.orientation != this.orientation) {
            this.orientation = configuration.orientation;
            if (this.splashImageView != null && (integer = this.preferences.getInteger("SplashDrawableId", 0)) != 0) {
                this.splashImageView.setImageDrawable(this.cordova.getActivity().getResources().getDrawable(integer));
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void removeSplashScreen(final boolean z) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass4 */

            public void run() {
                if (SplashScreen.splashDialog != null && SplashScreen.splashDialog.isShowing()) {
                    int fadeDuration = SplashScreen.this.getFadeDuration();
                    if (fadeDuration <= 0 || z) {
                        SplashScreen.this.spinnerStop();
                        SplashScreen.splashDialog.dismiss();
                        Dialog unused = SplashScreen.splashDialog = null;
                        SplashScreen.this.splashImageView = null;
                        return;
                    }
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    alphaAnimation.setInterpolator(new DecelerateInterpolator());
                    alphaAnimation.setDuration((long) fadeDuration);
                    SplashScreen.this.splashImageView.setAnimation(alphaAnimation);
                    SplashScreen.this.splashImageView.startAnimation(alphaAnimation);
                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass4.AnonymousClass1 */

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationStart(Animation animation) {
                            SplashScreen.this.spinnerStop();
                        }

                        public void onAnimationEnd(Animation animation) {
                            if (SplashScreen.splashDialog != null && SplashScreen.splashDialog.isShowing()) {
                                SplashScreen.splashDialog.dismiss();
                                Dialog unused = SplashScreen.splashDialog = null;
                                SplashScreen.this.splashImageView = null;
                            }
                        }
                    });
                }
            }
        });
    }

    private void showSplashScreen(final boolean z) {
        int integer = this.preferences.getInteger("SplashScreenDelay", DEFAULT_SPLASHSCREEN_DURATION);
        final int integer2 = this.preferences.getInteger("SplashDrawableId", 0);
        final int max = Math.max(0, integer - getFadeDuration());
        lastHideAfterDelay = z;
        Dialog dialog = splashDialog;
        if ((dialog != null && dialog.isShowing()) || integer2 == 0) {
            return;
        }
        if (integer > 0 || !z) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass5 */

                public void run() {
                    Display defaultDisplay = SplashScreen.this.cordova.getActivity().getWindowManager().getDefaultDisplay();
                    Context context = SplashScreen.this.webView.getContext();
                    SplashScreen.this.splashImageView = new ImageView(context);
                    SplashScreen.this.splashImageView.setImageResource(integer2);
                    SplashScreen.this.splashImageView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
                    SplashScreen.this.splashImageView.setMinimumHeight(defaultDisplay.getHeight());
                    SplashScreen.this.splashImageView.setMinimumWidth(defaultDisplay.getWidth());
                    SplashScreen.this.splashImageView.setBackgroundColor(SplashScreen.this.preferences.getInteger("backgroundColor", ViewCompat.MEASURED_STATE_MASK));
                    if (SplashScreen.this.isMaintainAspectRatio()) {
                        SplashScreen.this.splashImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } else {
                        SplashScreen.this.splashImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                    Dialog unused = SplashScreen.splashDialog = new Dialog(context, 16973840);
                    Window window = SplashScreen.splashDialog.getWindow();
                    if ((SplashScreen.this.cordova.getActivity().getWindow().getAttributes().flags & 1024) == 1024) {
                        SplashScreen.splashDialog.getWindow().setFlags(1024, 1024);
                    }
                    String string = SplashScreen.this.preferences.getString("SplashStatusBarBackgroundColor", "#000000");
                    if (string != null && !string.isEmpty() && Build.VERSION.SDK_INT >= 19) {
                        window.clearFlags(67108864);
                        window.addFlags(Integer.MIN_VALUE);
                        try {
                            window.getClass().getDeclaredMethod("setStatusBarColor", Integer.TYPE).invoke(window, Integer.valueOf(Color.parseColor(string)));
                        } catch (Exception unused2) {
                            LOG.w("SplashScreen StatusBarColor", "Method window.setStatusBarColor not found for SDK level " + Build.VERSION.SDK_INT);
                        }
                    }
                    String string2 = SplashScreen.this.preferences.getString("SplashNavigationBarBackgroundColor", "#000000");
                    if (string2 != null && !string2.isEmpty() && Build.VERSION.SDK_INT >= 19) {
                        window.clearFlags(134217728);
                        window.addFlags(Integer.MIN_VALUE);
                        try {
                            window.getClass().getDeclaredMethod("setNavigationBarColor", Integer.TYPE).invoke(window, Integer.valueOf(Color.parseColor(string2)));
                        } catch (Exception unused3) {
                            LOG.w("SplashScreen StatusBarColor", "Method window.setNavigationBarColor not found for SDK level " + Build.VERSION.SDK_INT);
                        }
                    }
                    SplashScreen.splashDialog.setContentView(SplashScreen.this.splashImageView);
                    SplashScreen.splashDialog.setCancelable(false);
                    SplashScreen.splashDialog.show();
                    if (SplashScreen.this.preferences.getBoolean("ShowSplashScreenSpinner", true)) {
                        SplashScreen.this.spinnerStart();
                    }
                    if (z) {
                        new Handler().postDelayed(new Runnable() {
                            /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass5.AnonymousClass1 */

                            public void run() {
                                if (SplashScreen.lastHideAfterDelay) {
                                    SplashScreen.this.removeSplashScreen(false);
                                }
                            }
                        }, (long) max);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void spinnerStart() {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass6 */

            public void run() {
                SplashScreen.this.spinnerStop();
                ProgressDialog unused = SplashScreen.spinnerDialog = new ProgressDialog(SplashScreen.this.webView.getContext());
                SplashScreen.spinnerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    /* class org.apache.cordova.splashscreen.SplashScreen.AnonymousClass6.AnonymousClass1 */

                    public void onCancel(DialogInterface dialogInterface) {
                        ProgressDialog unused = SplashScreen.spinnerDialog = null;
                    }
                });
                SplashScreen.spinnerDialog.setCancelable(false);
                SplashScreen.spinnerDialog.setIndeterminate(true);
                RelativeLayout relativeLayout = new RelativeLayout(SplashScreen.this.cordova.getActivity());
                relativeLayout.setGravity(17);
                relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                ProgressBar progressBar = new ProgressBar(SplashScreen.this.webView.getContext());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
                layoutParams.addRule(13, -1);
                progressBar.setLayoutParams(layoutParams);
                relativeLayout.addView(progressBar);
                SplashScreen.spinnerDialog.getWindow().clearFlags(2);
                SplashScreen.spinnerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                SplashScreen.spinnerDialog.show();
                SplashScreen.spinnerDialog.setContentView(relativeLayout);
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
