package org.apache.cordova;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.cordova.PluginResult;

public class NativeToJsMessageQueue {
    private static int COMBINED_RESPONSE_CUTOFF = 16777216;
    static final boolean DISABLE_EXEC_CHAINING = false;
    private static final boolean FORCE_ENCODE_USING_EVAL = false;
    private static final String LOG_TAG = "JsMessageQueue";
    private BridgeMode activeBridgeMode;
    private ArrayList<BridgeMode> bridgeModes = new ArrayList<>();
    private boolean paused;
    private final LinkedList<JsMessage> queue = new LinkedList<>();

    public void addBridgeMode(BridgeMode bridgeMode) {
        this.bridgeModes.add(bridgeMode);
    }

    public boolean isBridgeEnabled() {
        return this.activeBridgeMode != null;
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public void setBridgeMode(int value) {
        if (value < -1 || value >= this.bridgeModes.size()) {
            LOG.d(LOG_TAG, "Invalid NativeToJsBridgeMode: " + value);
            return;
        }
        BridgeMode newMode = value < 0 ? null : this.bridgeModes.get(value);
        if (newMode != this.activeBridgeMode) {
            StringBuilder sb = new StringBuilder();
            sb.append("Set native->JS mode to ");
            sb.append(newMode == null ? "null" : newMode.getClass().getSimpleName());
            LOG.d(LOG_TAG, sb.toString());
            synchronized (this) {
                this.activeBridgeMode = newMode;
                if (newMode != null) {
                    newMode.reset();
                    if (!this.paused && !this.queue.isEmpty()) {
                        newMode.onNativeToJsMessageAvailable(this);
                    }
                }
            }
        }
    }

    public void reset() {
        synchronized (this) {
            this.queue.clear();
            setBridgeMode(-1);
        }
    }

    private int calculatePackedMessageLength(JsMessage message) {
        int messageLen = message.calculateEncodedLength();
        return String.valueOf(messageLen).length() + messageLen + 1;
    }

    private void packMessage(JsMessage message, StringBuilder sb) {
        sb.append(message.calculateEncodedLength());
        sb.append(' ');
        message.encodeAsMessage(sb);
    }

    public String popAndEncode(boolean fromOnlineEvent) {
        synchronized (this) {
            if (this.activeBridgeMode == null) {
                return null;
            }
            this.activeBridgeMode.notifyOfFlush(this, fromOnlineEvent);
            if (this.queue.isEmpty()) {
                return null;
            }
            int totalPayloadLen = 0;
            int numMessagesToSend = 0;
            Iterator<JsMessage> it = this.queue.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                int messageSize = calculatePackedMessageLength(it.next());
                if (numMessagesToSend > 0 && COMBINED_RESPONSE_CUTOFF > 0 && totalPayloadLen + messageSize > COMBINED_RESPONSE_CUTOFF) {
                    break;
                }
                totalPayloadLen += messageSize;
                numMessagesToSend++;
            }
            StringBuilder sb = new StringBuilder(totalPayloadLen);
            for (int i = 0; i < numMessagesToSend; i++) {
                packMessage(this.queue.removeFirst(), sb);
            }
            if (!this.queue.isEmpty()) {
                sb.append('*');
            }
            return sb.toString();
        }
    }

    public String popAndEncodeAsJs() {
        synchronized (this) {
            if (this.queue.size() == 0) {
                return null;
            }
            int totalPayloadLen = 0;
            int numMessagesToSend = 0;
            Iterator<JsMessage> it = this.queue.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                int messageSize = it.next().calculateEncodedLength() + 50;
                if (numMessagesToSend > 0 && COMBINED_RESPONSE_CUTOFF > 0 && totalPayloadLen + messageSize > COMBINED_RESPONSE_CUTOFF) {
                    break;
                }
                totalPayloadLen += messageSize;
                numMessagesToSend++;
            }
            int i = 0;
            boolean willSendAllMessages = numMessagesToSend == this.queue.size();
            StringBuilder sb = new StringBuilder((willSendAllMessages ? 0 : 100) + totalPayloadLen);
            for (int i2 = 0; i2 < numMessagesToSend; i2++) {
                JsMessage message = this.queue.removeFirst();
                if (!willSendAllMessages || i2 + 1 != numMessagesToSend) {
                    sb.append("try{");
                    message.encodeAsJsMessage(sb);
                    sb.append("}finally{");
                } else {
                    message.encodeAsJsMessage(sb);
                }
            }
            if (!willSendAllMessages) {
                sb.append("window.setTimeout(function(){cordova.require('cordova/plugin/android/polling').pollOnce();},0);");
            }
            if (willSendAllMessages) {
                i = 1;
            }
            while (i < numMessagesToSend) {
                sb.append('}');
                i++;
            }
            return sb.toString();
        }
    }

    public void addJavaScript(String statement) {
        enqueueMessage(new JsMessage(statement));
    }

    public void addPluginResult(PluginResult result, String callbackId) {
        if (callbackId == null) {
            LOG.e(LOG_TAG, "Got plugin result with no callbackId", new Throwable());
            return;
        }
        boolean noResult = result.getStatus() == PluginResult.Status.NO_RESULT.ordinal();
        boolean keepCallback = result.getKeepCallback();
        if (!noResult || !keepCallback) {
            enqueueMessage(new JsMessage(result, callbackId));
        }
    }

    private void enqueueMessage(JsMessage message) {
        synchronized (this) {
            if (this.activeBridgeMode == null) {
                LOG.d(LOG_TAG, "Dropping Native->JS message due to disabled bridge");
                return;
            }
            this.queue.add(message);
            if (!this.paused) {
                this.activeBridgeMode.onNativeToJsMessageAvailable(this);
            }
        }
    }

    public void setPaused(boolean value) {
        if (this.paused && value) {
            LOG.e(LOG_TAG, "nested call to setPaused detected.", new Throwable());
        }
        this.paused = value;
        if (!value) {
            synchronized (this) {
                if (!this.queue.isEmpty() && this.activeBridgeMode != null) {
                    this.activeBridgeMode.onNativeToJsMessageAvailable(this);
                }
            }
        }
    }

    public static abstract class BridgeMode {
        public abstract void onNativeToJsMessageAvailable(NativeToJsMessageQueue nativeToJsMessageQueue);

        public void notifyOfFlush(NativeToJsMessageQueue queue, boolean fromOnlineEvent) {
        }

        public void reset() {
        }
    }

    public static class NoOpBridgeMode extends BridgeMode {
        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(NativeToJsMessageQueue queue) {
        }
    }

    public static class LoadUrlBridgeMode extends BridgeMode {
        private final CordovaInterface cordova;
        private final CordovaWebViewEngine engine;

        public LoadUrlBridgeMode(CordovaWebViewEngine engine2, CordovaInterface cordova2) {
            this.engine = engine2;
            this.cordova = cordova2;
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class org.apache.cordova.NativeToJsMessageQueue.LoadUrlBridgeMode.AnonymousClass1 */

                public void run() {
                    String js = queue.popAndEncodeAsJs();
                    if (js != null) {
                        CordovaWebViewEngine cordovaWebViewEngine = LoadUrlBridgeMode.this.engine;
                        cordovaWebViewEngine.loadUrl("javascript:" + js, false);
                    }
                }
            });
        }
    }

    public static class OnlineEventsBridgeMode extends BridgeMode {
        private final OnlineEventsBridgeModeDelegate delegate;
        private boolean ignoreNextFlush;
        private boolean online;

        public interface OnlineEventsBridgeModeDelegate {
            void runOnUiThread(Runnable runnable);

            void setNetworkAvailable(boolean z);
        }

        public OnlineEventsBridgeMode(OnlineEventsBridgeModeDelegate delegate2) {
            this.delegate = delegate2;
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void reset() {
            this.delegate.runOnUiThread(new Runnable() {
                /* class org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode.AnonymousClass1 */

                public void run() {
                    OnlineEventsBridgeMode.this.online = false;
                    OnlineEventsBridgeMode.this.ignoreNextFlush = true;
                    OnlineEventsBridgeMode.this.delegate.setNetworkAvailable(true);
                }
            });
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue) {
            this.delegate.runOnUiThread(new Runnable() {
                /* class org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode.AnonymousClass2 */

                public void run() {
                    if (!queue.isEmpty()) {
                        OnlineEventsBridgeMode.this.ignoreNextFlush = false;
                        OnlineEventsBridgeMode.this.delegate.setNetworkAvailable(OnlineEventsBridgeMode.this.online);
                    }
                }
            });
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void notifyOfFlush(NativeToJsMessageQueue queue, boolean fromOnlineEvent) {
            if (fromOnlineEvent && !this.ignoreNextFlush) {
                this.online = !this.online;
            }
        }
    }

    public static class EvalBridgeMode extends BridgeMode {
        private final CordovaInterface cordova;
        private final CordovaWebViewEngine engine;

        public EvalBridgeMode(CordovaWebViewEngine engine2, CordovaInterface cordova2) {
            this.engine = engine2;
            this.cordova = cordova2;
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                /* class org.apache.cordova.NativeToJsMessageQueue.EvalBridgeMode.AnonymousClass1 */

                public void run() {
                    String js = queue.popAndEncodeAsJs();
                    if (js != null) {
                        EvalBridgeMode.this.engine.evaluateJavascript(js, null);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public static class JsMessage {
        final String jsPayloadOrCallbackId;
        final PluginResult pluginResult;

        JsMessage(String js) {
            if (js != null) {
                this.jsPayloadOrCallbackId = js;
                this.pluginResult = null;
                return;
            }
            throw null;
        }

        JsMessage(PluginResult pluginResult2, String callbackId) {
            if (callbackId == null || pluginResult2 == null) {
                throw null;
            }
            this.jsPayloadOrCallbackId = callbackId;
            this.pluginResult = pluginResult2;
        }

        static int calculateEncodedLengthHelper(PluginResult pluginResult2) {
            switch (pluginResult2.getMessageType()) {
                case 1:
                    return pluginResult2.getStrMessage().length() + 1;
                case 2:
                default:
                    return pluginResult2.getMessage().length();
                case 3:
                    return pluginResult2.getMessage().length() + 1;
                case 4:
                case 5:
                    return 1;
                case 6:
                    return pluginResult2.getMessage().length() + 1;
                case 7:
                    return pluginResult2.getMessage().length() + 1;
                case 8:
                    int ret = 1;
                    for (int i = 0; i < pluginResult2.getMultipartMessagesSize(); i++) {
                        int length = calculateEncodedLengthHelper(pluginResult2.getMultipartMessage(i));
                        ret += String.valueOf(length).length() + 1 + length;
                    }
                    return ret;
            }
        }

        /* access modifiers changed from: package-private */
        public int calculateEncodedLength() {
            PluginResult pluginResult2 = this.pluginResult;
            if (pluginResult2 == null) {
                return this.jsPayloadOrCallbackId.length() + 1;
            }
            return calculateEncodedLengthHelper(this.pluginResult) + String.valueOf(pluginResult2.getStatus()).length() + 2 + 1 + this.jsPayloadOrCallbackId.length() + 1;
        }

        static void encodeAsMessageHelper(StringBuilder sb, PluginResult pluginResult2) {
            switch (pluginResult2.getMessageType()) {
                case 1:
                    sb.append('s');
                    sb.append(pluginResult2.getStrMessage());
                    return;
                case 2:
                default:
                    sb.append(pluginResult2.getMessage());
                    return;
                case 3:
                    sb.append('n');
                    sb.append(pluginResult2.getMessage());
                    return;
                case 4:
                    sb.append(pluginResult2.getMessage().charAt(0));
                    return;
                case 5:
                    sb.append('N');
                    return;
                case 6:
                    sb.append('A');
                    sb.append(pluginResult2.getMessage());
                    return;
                case 7:
                    sb.append('S');
                    sb.append(pluginResult2.getMessage());
                    return;
                case 8:
                    sb.append('M');
                    for (int i = 0; i < pluginResult2.getMultipartMessagesSize(); i++) {
                        PluginResult multipartMessage = pluginResult2.getMultipartMessage(i);
                        sb.append(String.valueOf(calculateEncodedLengthHelper(multipartMessage)));
                        sb.append(' ');
                        encodeAsMessageHelper(sb, multipartMessage);
                    }
                    return;
            }
        }

        /* access modifiers changed from: package-private */
        public void encodeAsMessage(StringBuilder sb) {
            PluginResult pluginResult2 = this.pluginResult;
            if (pluginResult2 == null) {
                sb.append('J');
                sb.append(this.jsPayloadOrCallbackId);
                return;
            }
            int status = pluginResult2.getStatus();
            boolean resultOk = true;
            boolean noResult = status == PluginResult.Status.NO_RESULT.ordinal();
            if (status != PluginResult.Status.OK.ordinal()) {
                resultOk = false;
            }
            boolean keepCallback = this.pluginResult.getKeepCallback();
            sb.append((noResult || resultOk) ? 'S' : 'F');
            sb.append(keepCallback ? '1' : '0');
            sb.append(status);
            sb.append(' ');
            sb.append(this.jsPayloadOrCallbackId);
            sb.append(' ');
            encodeAsMessageHelper(sb, this.pluginResult);
        }

        /* access modifiers changed from: package-private */
        public void buildJsMessage(StringBuilder sb) {
            int messageType = this.pluginResult.getMessageType();
            if (messageType == 5) {
                sb.append("null");
            } else if (messageType == 6) {
                sb.append("cordova.require('cordova/base64').toArrayBuffer('");
                sb.append(this.pluginResult.getMessage());
                sb.append("')");
            } else if (messageType == 7) {
                sb.append("atob('");
                sb.append(this.pluginResult.getMessage());
                sb.append("')");
            } else if (messageType != 8) {
                sb.append(this.pluginResult.getMessage());
            } else {
                int size = this.pluginResult.getMultipartMessagesSize();
                for (int i = 0; i < size; i++) {
                    new JsMessage(this.pluginResult.getMultipartMessage(i), this.jsPayloadOrCallbackId).buildJsMessage(sb);
                    if (i < size - 1) {
                        sb.append(",");
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void encodeAsJsMessage(StringBuilder sb) {
            PluginResult pluginResult2 = this.pluginResult;
            if (pluginResult2 == null) {
                sb.append(this.jsPayloadOrCallbackId);
                return;
            }
            int status = pluginResult2.getStatus();
            boolean success = status == PluginResult.Status.OK.ordinal() || status == PluginResult.Status.NO_RESULT.ordinal();
            sb.append("cordova.callbackFromNative('");
            sb.append(this.jsPayloadOrCallbackId);
            sb.append("',");
            sb.append(success);
            sb.append(",");
            sb.append(status);
            sb.append(",[");
            buildJsMessage(sb);
            sb.append("],");
            sb.append(this.pluginResult.getKeepCallback());
            sb.append(");");
        }
    }
}
