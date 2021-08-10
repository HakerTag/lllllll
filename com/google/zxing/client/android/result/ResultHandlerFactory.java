package com.google.zxing.client.android.result;

import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

public final class ResultHandlerFactory {
    private ResultHandlerFactory() {
    }

    /* renamed from: com.google.zxing.client.android.result.ResultHandlerFactory$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$client$result$ParsedResultType;

        /* JADX WARNING: Can't wrap try/catch for region: R(20:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|(3:19|20|22)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(22:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|22) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.google.zxing.client.result.ParsedResultType[] r0 = com.google.zxing.client.result.ParsedResultType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.google.zxing.client.android.result.ResultHandlerFactory.AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType = r0
                com.google.zxing.client.result.ParsedResultType r1 = com.google.zxing.client.result.ParsedResultType.ADDRESSBOOK     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.google.zxing.client.android.result.ResultHandlerFactory.AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType     // Catch:{ NoSuchFieldError -> 0x001d }
                com.google.zxing.client.result.ParsedResultType r1 = com.google.zxing.client.result.ParsedResultType.EMAIL_ADDRESS     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.google.zxing.client.android.result.ResultHandlerFactory.AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.google.zxing.client.result.ParsedResultType r1 = com.google.zxing.client.result.ParsedResultType.PRODUCT     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = com.google.zxing.client.android.result.ResultHandlerFactory.AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.google.zxing.client.result.ParsedResultType r1 = com.google.zxing.client.result.ParsedResultType.URI     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = com.google.zxing.client.android.result.ResultHandlerFactory.AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType     // Catch:{ NoSuchFieldError -> 0x003e }
                com.google.zxing.client.result.ParsedResultType r1 = com.google.zxing.client.result.ParsedResultType.WIFI     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = com.google.zxing.client.android.result.ResultHandlerFactory.AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.google.zxing.client.result.ParsedResultType r1 = com.google.zxing.client.result.ParsedResultType.GEO     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = com.google.zxing.client.android.result.ResultHandlerFactory.AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.google.zxing.client.result.ParsedResultType r1 = com.google.zxing.client.result.ParsedResultType.TEL     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r0 = com.google.zxing.client.android.result.ResultHandlerFactory.AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.google.zxing.client.result.ParsedResultType r1 = com.google.zxing.client.result.ParsedResultType.SMS     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                int[] r0 = com.google.zxing.client.android.result.ResultHandlerFactory.AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType     // Catch:{ NoSuchFieldError -> 0x006c }
                com.google.zxing.client.result.ParsedResultType r1 = com.google.zxing.client.result.ParsedResultType.CALENDAR     // Catch:{ NoSuchFieldError -> 0x006c }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006c }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006c }
            L_0x006c:
                int[] r0 = com.google.zxing.client.android.result.ResultHandlerFactory.AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType     // Catch:{ NoSuchFieldError -> 0x0078 }
                com.google.zxing.client.result.ParsedResultType r1 = com.google.zxing.client.result.ParsedResultType.ISBN     // Catch:{ NoSuchFieldError -> 0x0078 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0078 }
                r2 = 10
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0078 }
            L_0x0078:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.android.result.ResultHandlerFactory.AnonymousClass1.<clinit>():void");
        }
    }

    public static ResultHandler makeResultHandler(CaptureActivity captureActivity, Result result) {
        ParsedResult parseResult = parseResult(result);
        switch (AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType[parseResult.getType().ordinal()]) {
            case 1:
                return new AddressBookResultHandler(captureActivity, parseResult);
            case 2:
                return new EmailAddressResultHandler(captureActivity, parseResult);
            case 3:
                return new ProductResultHandler(captureActivity, parseResult, result);
            case 4:
                return new URIResultHandler(captureActivity, parseResult);
            case 5:
                return new WifiResultHandler(captureActivity, parseResult);
            case 6:
                return new GeoResultHandler(captureActivity, parseResult);
            case 7:
                return new TelResultHandler(captureActivity, parseResult);
            case 8:
                return new SMSResultHandler(captureActivity, parseResult);
            case 9:
                return new CalendarResultHandler(captureActivity, parseResult);
            case 10:
                return new ISBNResultHandler(captureActivity, parseResult, result);
            default:
                return new TextResultHandler(captureActivity, parseResult, result);
        }
    }

    private static ParsedResult parseResult(Result result) {
        return ResultParser.parseResult(result);
    }
}
