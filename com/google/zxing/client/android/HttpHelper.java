package com.google.zxing.client.android;

import android.util.Log;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

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

    public static CharSequence downloadViaHttp(String uri, ContentType type) throws IOException {
        return downloadViaHttp(uri, type, Integer.MAX_VALUE);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.google.zxing.client.android.HttpHelper$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType;

        static {
            int[] iArr = new int[ContentType.values().length];
            $SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType = iArr;
            try {
                iArr[ContentType.HTML.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType[ContentType.JSON.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType[ContentType.XML.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType[ContentType.TEXT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static CharSequence downloadViaHttp(String uri, ContentType type, int maxChars) throws IOException {
        String contentTypes;
        int i = AnonymousClass1.$SwitchMap$com$google$zxing$client$android$HttpHelper$ContentType[type.ordinal()];
        if (i == 1) {
            contentTypes = "application/xhtml+xml,text/html,text/*,*/*";
        } else if (i == 2) {
            contentTypes = "application/json,text/*,*/*";
        } else if (i != 3) {
            contentTypes = "text/*,*/*";
        } else {
            contentTypes = "application/xml,text/*,*/*";
        }
        return downloadViaHttp(uri, contentTypes, maxChars);
    }

    private static CharSequence downloadViaHttp(String uri, String contentTypes, int maxChars) throws IOException {
        int redirects = 0;
        while (redirects < 5) {
            HttpURLConnection connection = safelyOpenConnection(new URL(uri));
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Accept", contentTypes);
            connection.setRequestProperty("Accept-Charset", "utf-8,*");
            connection.setRequestProperty("User-Agent", "ZXing (Android)");
            try {
                int responseCode = safelyConnect(connection);
                if (responseCode == 200) {
                    CharSequence consume = consume(connection, maxChars);
                    connection.disconnect();
                    return consume;
                } else if (responseCode == 302) {
                    String location = connection.getHeaderField("Location");
                    if (location != null) {
                        uri = location;
                        redirects++;
                    } else {
                        throw new IOException("No Location");
                    }
                } else {
                    throw new IOException("Bad HTTP response: " + responseCode);
                }
            } finally {
                connection.disconnect();
            }
        }
        throw new IOException("Too many redirects");
    }

    private static String getEncoding(URLConnection connection) {
        int charsetStart;
        String contentTypeHeader = connection.getHeaderField("Content-Type");
        if (contentTypeHeader == null || (charsetStart = contentTypeHeader.indexOf("charset=")) < 0) {
            return "UTF-8";
        }
        return contentTypeHeader.substring("charset=".length() + charsetStart);
    }

    private static CharSequence consume(URLConnection connection, int maxChars) throws IOException {
        int charsRead;
        String encoding = getEncoding(connection);
        StringBuilder out = new StringBuilder();
        Reader in = null;
        try {
            Reader in2 = new InputStreamReader(connection.getInputStream(), encoding);
            char[] buffer = new char[1024];
            while (out.length() < maxChars && (charsRead = in2.read(buffer)) > 0) {
                out.append(buffer, 0, charsRead);
            }
            try {
                in2.close();
            } catch (IOException | NullPointerException e) {
            }
            return out;
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    in.close();
                } catch (IOException | NullPointerException e2) {
                }
            }
            throw th;
        }
    }

    public static URI unredirect(URI uri) throws IOException {
        if (!REDIRECTOR_DOMAINS.contains(uri.getHost())) {
            return uri;
        }
        HttpURLConnection connection = safelyOpenConnection(uri.toURL());
        connection.setInstanceFollowRedirects(false);
        connection.setDoInput(false);
        connection.setRequestMethod("HEAD");
        connection.setRequestProperty("User-Agent", "ZXing (Android)");
        try {
            int responseCode = safelyConnect(connection);
            if (responseCode != 307) {
                switch (responseCode) {
                    case 300:
                    case 301:
                    case 302:
                    case 303:
                        break;
                    default:
                        return uri;
                }
            }
            String location = connection.getHeaderField("Location");
            if (location != null) {
                try {
                    URI uri2 = new URI(location);
                    connection.disconnect();
                    return uri2;
                } catch (URISyntaxException e) {
                }
            }
            return uri;
        } finally {
            connection.disconnect();
        }
    }

    private static HttpURLConnection safelyOpenConnection(URL url) throws IOException {
        try {
            URLConnection conn = url.openConnection();
            if (conn instanceof HttpURLConnection) {
                return (HttpURLConnection) conn;
            }
            throw new IOException();
        } catch (NullPointerException npe) {
            String str = TAG;
            Log.w(str, "Bad URI? " + url);
            throw new IOException(npe);
        }
    }

    private static int safelyConnect(HttpURLConnection connection) throws IOException {
        try {
            connection.connect();
            try {
                return connection.getResponseCode();
            } catch (IllegalArgumentException | NullPointerException | StringIndexOutOfBoundsException e) {
                throw new IOException(e);
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException | NullPointerException | SecurityException e2) {
            throw new IOException(e2);
        }
    }
}
