package jp.rabee;

import android.app.Activity;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.json.JSONArray;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB\u0005¢\u0006\u0002\u0010\u0002J \u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0004H\u0016J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\u0010\u0010\u0019\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\u0004H\u0002J\u0010\u0010\u001a\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\u0004H\u0002R\u001a\u0010\u0003\u001a\u00020\u0004X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\u0004X.¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0006\"\u0004\b\u000b\u0010\b¨\u0006\u001c"}, d2 = {"Ljp/rabee/CDVKeepAwake;", "Lorg/apache/cordova/CordovaPlugin;", "()V", "context", "Lorg/apache/cordova/CallbackContext;", "getContext", "()Lorg/apache/cordova/CallbackContext;", "setContext", "(Lorg/apache/cordova/CallbackContext;)V", "onProgressCallbackContext", "getOnProgressCallbackContext", "setOnProgressCallbackContext", "execute", "", "action", "", "data", "Lorg/json/JSONArray;", "callbackContext", "initialize", "", "cordova", "Lorg/apache/cordova/CordovaInterface;", "webView", "Lorg/apache/cordova/CordovaWebView;", "start", "stop", "Companion", "app_release"}, k = 1, mv = {1, 1, 15})
/* compiled from: CDVKeepAwake.kt */
public final class CDVKeepAwake extends CordovaPlugin {
    public static final Companion Companion = new Companion(null);
    private static final String TAG = TAG;
    public CallbackContext context;
    public CallbackContext onProgressCallbackContext;

    public final CallbackContext getContext() {
        CallbackContext callbackContext = this.context;
        if (callbackContext == null) {
            Intrinsics.throwUninitializedPropertyAccessException("context");
        }
        return callbackContext;
    }

    public final void setContext(CallbackContext callbackContext) {
        Intrinsics.checkParameterIsNotNull(callbackContext, "<set-?>");
        this.context = callbackContext;
    }

    public final CallbackContext getOnProgressCallbackContext() {
        CallbackContext callbackContext = this.onProgressCallbackContext;
        if (callbackContext == null) {
            Intrinsics.throwUninitializedPropertyAccessException("onProgressCallbackContext");
        }
        return callbackContext;
    }

    public final void setOnProgressCallbackContext(CallbackContext callbackContext) {
        Intrinsics.checkParameterIsNotNull(callbackContext, "<set-?>");
        this.onProgressCallbackContext = callbackContext;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        Intrinsics.checkParameterIsNotNull(cordovaInterface, "cordova");
        Intrinsics.checkParameterIsNotNull(cordovaWebView, "webView");
        LOG.d(TAG, "hi! This is CDVKeepAwake. Now intitilaizing ...");
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) {
        Intrinsics.checkParameterIsNotNull(str, "action");
        Intrinsics.checkParameterIsNotNull(jSONArray, "data");
        Intrinsics.checkParameterIsNotNull(callbackContext, "callbackContext");
        this.context = callbackContext;
        int hashCode = str.hashCode();
        if (hashCode != 3540994) {
            if (hashCode == 109757538 && str.equals("start")) {
                CallbackContext callbackContext2 = this.context;
                if (callbackContext2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("context");
                }
                return start(callbackContext2);
            }
        } else if (str.equals("stop")) {
            CallbackContext callbackContext3 = this.context;
            if (callbackContext3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("context");
            }
            return stop(callbackContext3);
        }
        return true;
    }

    private final boolean start(CallbackContext callbackContext) {
        CordovaInterface cordovaInterface = this.cordova;
        Intrinsics.checkExpressionValueIsNotNull(cordovaInterface, "cordova");
        Activity activity = cordovaInterface.getActivity();
        activity.runOnUiThread(new CDVKeepAwake$start$1(activity, callbackContext));
        return true;
    }

    private final boolean stop(CallbackContext callbackContext) {
        CordovaInterface cordovaInterface = this.cordova;
        Intrinsics.checkExpressionValueIsNotNull(cordovaInterface, "cordova");
        Activity activity = cordovaInterface.getActivity();
        activity.runOnUiThread(new CDVKeepAwake$stop$1(activity, callbackContext));
        return true;
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0014\u0010\u0003\u001a\u00020\u0004XD¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, d2 = {"Ljp/rabee/CDVKeepAwake$Companion;", "", "()V", "TAG", "", "getTAG", "()Ljava/lang/String;", "app_release"}, k = 1, mv = {1, 1, 15})
    /* compiled from: CDVKeepAwake.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* access modifiers changed from: protected */
        public final String getTAG() {
            return CDVKeepAwake.TAG;
        }
    }
}
