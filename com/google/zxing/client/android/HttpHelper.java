package com.google.zxing.client.android;

import android.util.Log;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import kotlin.jvm.internal.IntCompanionObject;

public final class HttpHelper {
    private static final Collection<String> REDIRECTOR_DOMAINS = new HashSet(Arrays.asList("amzn.to", "bit.ly", "bitly.com", "fb.me", "goo.gl", "is.gd", "j.mp", "lnkd.in", "ow.ly", "R.BEETAGG.COM", "r.beetagg.com", "SCN.BY", "su.pr", "t.co", "tinyurl.com", "tr.im"));
    private static final String TAG = HttpHelper.class.getSimpleName();

    public enum ContentType {
        HTML,
        JSON,
        XML,
        TEXT
    }

    private HttpHelper() {
    }

    public static CharSequence downloadViaHttp(String str, ContentType contentType) throws IOException {
        return downloadViaHttp(str, contentType, (int) IntCompanionObject.MAX_VALUE);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.google.zxing.client.android.HttpHelper$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                com.google.zxing.client.android.HttpHelper$ContentType[] r0 = com.google.zxing.client.android.HttpHelper.ContentType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.google.zxing.client.android.HttpHelper.AnonymousClass1.$SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType = r0
                com.google.zxing.client.android.HttpHelper$ContentType r1 = com.google.zxing.client.android.HttpHelper.ContentType.HTML     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.google.zxing.client.android.HttpHelper.AnonymousClass1.$SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType     // Catch:{ NoSuchFieldError -> 0x001d }
                com.google.zxing.client.android.HttpHelper$ContentType r1 = com.google.zxing.client.android.HttpHelper.ContentType.JSON     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.google.zxing.client.android.HttpHelper.AnonymousClass1.$SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.google.zxing.client.android.HttpHelper$ContentType r1 = com.google.zxing.client.android.HttpHelper.ContentType.XML     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = com.google.zxing.client.android.HttpHelper.AnonymousClass1.$SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.google.zxing.client.android.HttpHelper$ContentType r1 = com.google.zxing.client.android.HttpHelper.ContentType.TEXT     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.android.HttpHelper.AnonymousClass1.<clinit>():void");
        }
    }

    public static CharSequence downloadViaHttp(String str, ContentType contentType, int i) throws IOException {
        int i2 = AnonymousClass1.$SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType[contentType.ordinal()];
        return downloadViaHttp(str, i2 != 1 ? i2 != 2 ? i2 != 3 ? "text/*,*/*" : "application/xml,text/*,*/*" : "application/json,text/*,*/*" : "application/xhtml+xml,text/html,text/*,*/*", i);
    }

    /* JADX INFO: finally extract failed */
    private static CharSequence downloadViaHttp(String str, String str2, int i) throws IOException {
        int i2 = 0;
        while (i2 < 5) {
            HttpURLConnection safelyOpenConnection = safelyOpenConnection(new URL(str));
            safelyOpenConnection.setInstanceFollowRedirects(true);
            safelyOpenConnection.setRequestProperty("Accept", str2);
            safelyOpenConnection.setRequestProperty("Accept-Charset", "utf-8,*");
            safelyOpenConnection.setRequestProperty("User-Agent", "ZXing (Android)");
            try {
                int safelyConnect = safelyConnect(safelyOpenConnection);
                if (safelyConnect == 200) {
                    CharSequence consume = consume(safelyOpenConnection, i);
                    safelyOpenConnection.disconnect();
                    return consume;
                } else if (safelyConnect == 302) {
                    String headerField = safelyOpenConnection.getHeaderField("Location");
                    if (headerField != null) {
                        i2++;
                        safelyOpenConnection.disconnect();
                        str = headerField;
                    } else {
                        throw new IOException("No Location");
                    }
                } else {
                    throw new IOException("Bad HTTP response: " + safelyConnect);
                }
            } catch (Throwable th) {
                safelyOpenConnection.disconnect();
                throw th;
            }
        }
        throw new IOException("Too many redirects");
    }

    private static String getEncoding(URLConnection uRLConnection) {
        int indexOf;
        String headerField = uRLConnection.getHeaderField("Content-Type");
        return (headerField == null || (indexOf = headerField.indexOf("charset=")) < 0) ? "UTF-8" : headerField.substring(indexOf + 8);
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0032 A[SYNTHETIC, Splitter:B:19:0x0032] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.CharSequence consume(java.net.URLConnection r4, int r5) throws java.io.IOException {
        /*
            java.lang.String r0 = getEncoding(r4)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
            java.io.InputStreamReader r3 = new java.io.InputStreamReader     // Catch:{ all -> 0x002f }
            java.io.InputStream r4 = r4.getInputStream()     // Catch:{ all -> 0x002f }
            r3.<init>(r4, r0)     // Catch:{ all -> 0x002f }
            r4 = 1024(0x400, float:1.435E-42)
            char[] r4 = new char[r4]     // Catch:{ all -> 0x002c }
        L_0x0017:
            int r0 = r1.length()     // Catch:{ all -> 0x002c }
            if (r0 >= r5) goto L_0x0028
            int r0 = r3.read(r4)     // Catch:{ all -> 0x002c }
            if (r0 <= 0) goto L_0x0028
            r2 = 0
            r1.append(r4, r2, r0)     // Catch:{ all -> 0x002c }
            goto L_0x0017
        L_0x0028:
            r3.close()     // Catch:{ IOException | NullPointerException -> 0x002b }
        L_0x002b:
            return r1
        L_0x002c:
            r4 = move-exception
            r2 = r3
            goto L_0x0030
        L_0x002f:
            r4 = move-exception
        L_0x0030:
            if (r2 == 0) goto L_0x0035
            r2.close()     // Catch:{ IOException | NullPointerException -> 0x0035 }
        L_0x0035:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.android.HttpHelper.consume(java.net.URLConnection, int):java.lang.CharSequence");
    }

    public static URI unredirect(URI uri) throws IOException {
        if (!REDIRECTOR_DOMAINS.contains(uri.getHost())) {
            return uri;
        }
        HttpURLConnection safelyOpenConnection = safelyOpenConnection(uri.toURL());
        safelyOpenConnection.setInstanceFollowRedirects(false);
        safelyOpenConnection.setDoInput(false);
        safelyOpenConnection.setRequestMethod("HEAD");
        safelyOpenConnection.setRequestProperty("User-Agent", "ZXing (Android)");
        try {
            int safelyConnect = safelyConnect(safelyOpenConnection);
            if (safelyConnect != 307) {
                switch (safelyConnect) {
                    case 300:
                    case 301:
                    case 302:
                    case 303:
                        break;
                    default:
                        return uri;
                }
            }
            String headerField = safelyOpenConnection.getHeaderField("Location");
            if (headerField != null) {
                try {
                    URI uri2 = new URI(headerField);
                    safelyOpenConnection.disconnect();
                    return uri2;
                } catch (URISyntaxException unused) {
                }
            }
            return uri;
        } finally {
            safelyOpenConnection.disconnect();
        }
    }

    private static HttpURLConnection safelyOpenConnection(URL url) throws IOException {
        try {
            URLConnection openConnection = url.openConnection();
            if (openConnection instanceof HttpURLConnection) {
                return (HttpURLConnection) openConnection;
            }
            throw new IOException();
        } catch (NullPointerException e) {
            String str = TAG;
            Log.w(str, "Bad URI? " + url);
            throw new IOException(e);
        }
    }

    private static int safelyConnect(HttpURLConnection httpURLConnection) throws IOException {
        try {
            httpURLConnection.connect();
            try {
                return httpURLConnection.getResponseCode();
            } catch (IllegalArgumentException | NullPointerException | StringIndexOutOfBoundsException e) {
                throw new IOException(e);
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException | NullPointerException | SecurityException e2) {
            throw new IOException(e2);
        }
    }
}
