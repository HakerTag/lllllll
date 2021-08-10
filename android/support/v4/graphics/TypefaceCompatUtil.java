package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.os.Process;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class TypefaceCompatUtil {
    private static final String CACHE_FILE_PREFIX = ".font";
    private static final String TAG = "TypefaceCompatUtil";

    private TypefaceCompatUtil() {
    }

    public static File getTempFile(Context context) {
        String prefix = CACHE_FILE_PREFIX + Process.myPid() + "-" + Process.myTid() + "-";
        for (int i = 0; i < 100; i++) {
            File file = new File(context.getCacheDir(), prefix + i);
            try {
                if (file.createNewFile()) {
                    return file;
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0020, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0021, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0024, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001b, code lost:
        r2 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.nio.ByteBuffer mmap(java.io.File r7) {
        /*
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0025 }
            r0.<init>(r7)     // Catch:{ IOException -> 0x0025 }
            java.nio.channels.FileChannel r1 = r0.getChannel()     // Catch:{ all -> 0x0019 }
            long r5 = r1.size()     // Catch:{ all -> 0x0019 }
            java.nio.channels.FileChannel$MapMode r2 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0019 }
            r3 = 0
            java.nio.MappedByteBuffer r2 = r1.map(r2, r3, r5)     // Catch:{ all -> 0x0019 }
            r0.close()
            return r2
        L_0x0019:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001b }
        L_0x001b:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x0020 }
            goto L_0x0024
        L_0x0020:
            r3 = move-exception
            r1.addSuppressed(r3)
        L_0x0024:
            throw r2
        L_0x0025:
            r0 = move-exception
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.mmap(java.io.File):java.nio.ByteBuffer");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0038, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003d, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003e, code lost:
        r4.addSuppressed(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0041, code lost:
        throw r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0044, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0045, code lost:
        if (r2 != null) goto L_0x0047;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x004b, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x004c, code lost:
        r3.addSuppressed(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x004f, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.nio.ByteBuffer mmap(android.content.Context r10, android.os.CancellationSignal r11, android.net.Uri r12) {
        /*
            android.content.ContentResolver r0 = r10.getContentResolver()
            r1 = 0
            java.lang.String r2 = "r"
            android.os.ParcelFileDescriptor r2 = r0.openFileDescriptor(r12, r2, r11)     // Catch:{ IOException -> 0x0050 }
            if (r2 != 0) goto L_0x0014
            if (r2 == 0) goto L_0x0013
            r2.close()     // Catch:{ IOException -> 0x0050 }
        L_0x0013:
            return r1
        L_0x0014:
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ all -> 0x0042 }
            java.io.FileDescriptor r4 = r2.getFileDescriptor()     // Catch:{ all -> 0x0042 }
            r3.<init>(r4)     // Catch:{ all -> 0x0042 }
            java.nio.channels.FileChannel r4 = r3.getChannel()     // Catch:{ all -> 0x0036 }
            long r8 = r4.size()     // Catch:{ all -> 0x0036 }
            java.nio.channels.FileChannel$MapMode r5 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0036 }
            r6 = 0
            java.nio.MappedByteBuffer r5 = r4.map(r5, r6, r8)     // Catch:{ all -> 0x0036 }
            r3.close()
            if (r2 == 0) goto L_0x0035
            r2.close()
        L_0x0035:
            return r5
        L_0x0036:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x0038 }
        L_0x0038:
            r5 = move-exception
            r3.close()     // Catch:{ all -> 0x003d }
            goto L_0x0041
        L_0x003d:
            r6 = move-exception
            r4.addSuppressed(r6)
        L_0x0041:
            throw r5
        L_0x0042:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0044 }
        L_0x0044:
            r4 = move-exception
            if (r2 == 0) goto L_0x004f
            r2.close()     // Catch:{ all -> 0x004b }
            goto L_0x004f
        L_0x004b:
            r5 = move-exception
            r3.addSuppressed(r5)
        L_0x004f:
            throw r4
        L_0x0050:
            r2 = move-exception
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.mmap(android.content.Context, android.os.CancellationSignal, android.net.Uri):java.nio.ByteBuffer");
    }

    public static ByteBuffer copyToDirectBuffer(Context context, Resources res, int id) {
        File tmpFile = getTempFile(context);
        ByteBuffer byteBuffer = null;
        if (tmpFile == null) {
            return null;
        }
        try {
            if (copyToFile(tmpFile, res, id)) {
                byteBuffer = mmap(tmpFile);
            }
            return byteBuffer;
        } finally {
            tmpFile.delete();
        }
    }

    public static boolean copyToFile(File file, InputStream is) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file, false);
            byte[] buffer = new byte[1024];
            while (true) {
                int readLen = is.read(buffer);
                if (readLen != -1) {
                    os.write(buffer, 0, readLen);
                } else {
                    return true;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error copying resource contents to temp file: " + e.getMessage());
            return false;
        } finally {
            closeQuietly(os);
        }
    }

    public static boolean copyToFile(File file, Resources res, int id) {
        InputStream is = null;
        try {
            is = res.openRawResource(id);
            return copyToFile(file, is);
        } finally {
            closeQuietly(is);
        }
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
            }
        }
    }
}
