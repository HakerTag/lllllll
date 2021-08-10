package com.google.zxing.client.android.result;

import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ResultParser;

public final class ResultHandlerFactory {
    private ResultHandlerFactory() {
    }

    /* renamed from: com.google.zxing.client.android.result.ResultHandlerFactory$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$client$result$ParsedResultType;

        static {
            int[] iArr = new int[ParsedResultType.values().length];
            $SwitchMap$com$google$zxing$client$result$ParsedResultType = iArr;
            try {
                iArr[ParsedResultType.ADDRESSBOOK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.EMAIL_ADDRESS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.PRODUCT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.URI.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.WIFI.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.GEO.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.TEL.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.SMS.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.CALENDAR.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$ParsedResultType[ParsedResultType.ISBN.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    public static ResultHandler makeResultHandler(CaptureActivity activity, Result rawResult) {
        ParsedResult result = parseResult(rawResult);
        switch (AnonymousClass1.$SwitchMap$com$google$zxing$client$result$ParsedResultType[result.getType().ordinal()]) {
            case 1:
                return new AddressBookResultHandler(activity, result);
            case 2:
                return new EmailAddressResultHandler(activity, result);
            case 3:
                return new ProductResultHandler(activity, result, rawResult);
            case 4:
                return new URIResultHandler(activity, result);
            case 5:
                return new WifiResultHandler(activity, result);
            case 6:
                return new GeoResultHandler(activity, result);
            case 7:
                return new TelResultHandler(activity, result);
            case 8:
                return new SMSResultHandler(activity, result);
            case 9:
                return new CalendarResultHandler(activity, result);
            case 10:
                return new ISBNResultHandler(activity, result, rawResult);
            default:
                return new TextResultHandler(activity, result, rawResult);
        }
    }

    private static ParsedResult parseResult(Result rawResult) {
        return ResultParser.parseResult(rawResult);
    }
}
