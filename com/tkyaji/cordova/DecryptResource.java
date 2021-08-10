package com.tkyaji.cordova;

import android.net.Uri;
import android.util.Base64;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.LOG;

public class DecryptResource extends CordovaPlugin {
    private static final String CRYPT_IV = "ovI4nCyJHvppxw2E";
    private static final String CRYPT_KEY = "w+Suz6n5byW0Tr86FuOHeLYLAKPzhEEg";
    private static final String[] EXCLUDE_FILES = new String[0];
    private static final String[] INCLUDE_FILES = {"\\.(htm|html|js|css)$"};
    private static final String TAG = "DecryptResource";

    @Override // org.apache.cordova.CordovaPlugin
    public Uri remapUri(Uri uri) {
        if (uri.toString().indexOf("/+++/") > -1) {
            return toPluginUri(uri);
        }
        return uri;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public CordovaResourceApi.OpenForReadResult handleOpenForRead(Uri uri) throws IOException {
        String uriStr = fromPluginUri(uri).toString().replace("/+++/", "/").split("\\?")[0];
        CordovaResourceApi.OpenForReadResult readResult = this.webView.getResourceApi().openForRead(Uri.parse(uriStr), true);
        if (!isCryptFiles(uriStr)) {
            return readResult;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(readResult.inputStream));
        StringBuilder strb = new StringBuilder();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            strb.append(line);
        }
        br.close();
        byte[] bytes = Base64.decode(strb.toString(), 0);
        LOG.d(TAG, "decrypt: " + uriStr);
        ByteArrayInputStream byteInputStream = null;
        try {
            SecretKey skey = new SecretKeySpec(CRYPT_KEY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, skey, new IvParameterSpec(CRYPT_IV.getBytes("UTF-8")));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(cipher.doFinal(bytes));
            byteInputStream = new ByteArrayInputStream(bos.toByteArray());
        } catch (Exception ex) {
            LOG.e(TAG, ex.getMessage());
        }
        return new CordovaResourceApi.OpenForReadResult(readResult.uri, byteInputStream, readResult.mimeType, readResult.length, readResult.assetFd);
    }

    private boolean isCryptFiles(String uri) {
        String checkPath = uri.replace("file:///android_asset/www/", "");
        if (hasMatch(checkPath, INCLUDE_FILES) && !hasMatch(checkPath, EXCLUDE_FILES)) {
            return true;
        }
        return false;
    }

    private boolean hasMatch(String text, String[] regexArr) {
        for (String regex : regexArr) {
            if (Pattern.compile(regex).matcher(text).find()) {
                return true;
            }
        }
        return false;
    }
}
