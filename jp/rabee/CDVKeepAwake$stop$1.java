package jp.rabee;

import android.app.Activity;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, d2 = {"<anonymous>", "", "run"}, k = 3, mv = {1, 1, 15})
/* compiled from: CDVKeepAwake.kt */
final class CDVKeepAwake$stop$1 implements Runnable {
    final /* synthetic */ Activity $activity;
    final /* synthetic */ CallbackContext $callbackContext;

    CDVKeepAwake$stop$1(Activity activity, CallbackContext callbackContext) {
        this.$activity = activity;
        this.$callbackContext = callbackContext;
    }

    public final void run() {
        Activity activity = this.$activity;
        Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
        activity.getWindow().clearFlags(128);
        this.$callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
    }
}
