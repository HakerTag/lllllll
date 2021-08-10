package com.google.zxing.client.android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import kotlin.UByte;

final class DecodeHandler extends Handler {
    private static final String TAG = DecodeHandler.class.getSimpleName();
    private final CaptureActivity activity;
    private int frameCount;
    private final MultiFormatReader multiFormatReader;
    private boolean running = true;

    DecodeHandler(CaptureActivity captureActivity, Map<DecodeHintType, Object> map) {
        MultiFormatReader multiFormatReader2 = new MultiFormatReader();
        this.multiFormatReader = multiFormatReader2;
        multiFormatReader2.setHints(map);
        this.activity = captureActivity;
    }

    public void handleMessage(Message message) {
        if (this.running) {
            if (message.what == R.id.decode) {
                decode((byte[]) message.obj, message.arg1, message.arg2);
            } else if (message.what == R.id.quit) {
                this.running = false;
                Looper.myLooper().quit();
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0097  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void decode(byte[] r8, int r9, int r10) {
        /*
            r7 = this;
            long r0 = java.lang.System.currentTimeMillis()
            int r2 = r7.frameCount
            r3 = 3
            if (r2 != r3) goto L_0x0023
            r2 = 0
            r7.frameCount = r2
            int r3 = r9 * r10
            int[] r4 = new int[r3]
            YUV_NV21_TO_RGB(r4, r8, r9, r10)
        L_0x0013:
            if (r2 >= r3) goto L_0x0020
            r5 = 16777215(0xffffff, float:2.3509886E-38)
            r6 = r4[r2]
            int r5 = r5 - r6
            r4[r2] = r5
            int r2 = r2 + 1
            goto L_0x0013
        L_0x0020:
            r7.encodeYUV420SP(r8, r4, r9, r10)
        L_0x0023:
            int r2 = r7.frameCount
            int r2 = r2 + 1
            r7.frameCount = r2
            com.google.zxing.client.android.CaptureActivity r2 = r7.activity
            com.google.zxing.client.android.camera.CameraManager r2 = r2.getCameraManager()
            com.google.zxing.PlanarYUVLuminanceSource r8 = r2.buildLuminanceSource(r8, r9, r10)
            if (r8 == 0) goto L_0x0057
            com.google.zxing.BinaryBitmap r9 = new com.google.zxing.BinaryBitmap
            com.google.zxing.common.HybridBinarizer r10 = new com.google.zxing.common.HybridBinarizer
            r10.<init>(r8)
            r9.<init>(r10)
            com.google.zxing.MultiFormatReader r10 = r7.multiFormatReader     // Catch:{ ReaderException -> 0x0052, all -> 0x004b }
            com.google.zxing.Result r9 = r10.decodeWithState(r9)     // Catch:{ ReaderException -> 0x0052, all -> 0x004b }
            com.google.zxing.MultiFormatReader r10 = r7.multiFormatReader
            r10.reset()
            goto L_0x0058
        L_0x004b:
            r8 = move-exception
            com.google.zxing.MultiFormatReader r9 = r7.multiFormatReader
            r9.reset()
            throw r8
        L_0x0052:
            com.google.zxing.MultiFormatReader r9 = r7.multiFormatReader
            r9.reset()
        L_0x0057:
            r9 = 0
        L_0x0058:
            com.google.zxing.client.android.CaptureActivity r10 = r7.activity
            android.os.Handler r10 = r10.getHandler()
            if (r9 == 0) goto L_0x0097
            long r2 = java.lang.System.currentTimeMillis()
            java.lang.String r4 = com.google.zxing.client.android.DecodeHandler.TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Found barcode in "
            r5.append(r6)
            long r2 = r2 - r0
            r5.append(r2)
            java.lang.String r0 = " ms"
            r5.append(r0)
            java.lang.String r0 = r5.toString()
            android.util.Log.d(r4, r0)
            if (r10 == 0) goto L_0x00a2
            int r0 = barcodescanner.xservices.nl.barcodescanner.R.id.decode_succeeded
            android.os.Message r9 = android.os.Message.obtain(r10, r0, r9)
            android.os.Bundle r10 = new android.os.Bundle
            r10.<init>()
            bundleThumbnail(r8, r10)
            r9.setData(r10)
            r9.sendToTarget()
            goto L_0x00a2
        L_0x0097:
            if (r10 == 0) goto L_0x00a2
            int r8 = barcodescanner.xservices.nl.barcodescanner.R.id.decode_failed
            android.os.Message r8 = android.os.Message.obtain(r10, r8)
            r8.sendToTarget()
        L_0x00a2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.android.DecodeHandler.decode(byte[], int, int):void");
    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource planarYUVLuminanceSource, Bundle bundle) {
        int[] renderThumbnail = planarYUVLuminanceSource.renderThumbnail();
        int thumbnailWidth = planarYUVLuminanceSource.getThumbnailWidth();
        Bitmap createBitmap = Bitmap.createBitmap(renderThumbnail, 0, thumbnailWidth, thumbnailWidth, planarYUVLuminanceSource.getThumbnailHeight(), Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        createBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, byteArrayOutputStream.toByteArray());
        bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, ((float) thumbnailWidth) / ((float) planarYUVLuminanceSource.getWidth()));
    }

    private static void YUV_NV21_TO_RGB(int[] iArr, byte[] bArr, int i, int i2) {
        int i3 = i * i2;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (i4 < i2) {
            int i7 = 0;
            int i8 = 0;
            while (i7 < i) {
                int i9 = 255;
                int i10 = bArr[(i6 * i) + i8] & UByte.MAX_VALUE;
                int i11 = ((i6 >> 1) * i) + i3 + (i8 & -2);
                int i12 = bArr[i11 + 0] & UByte.MAX_VALUE;
                int i13 = bArr[i11 + 1] & UByte.MAX_VALUE;
                if (i10 < 16) {
                    i10 = 16;
                }
                int i14 = (i10 - 16) * 1192;
                int i15 = i12 - 128;
                int i16 = i13 - 128;
                int i17 = ((i15 * 1634) + i14) >> 10;
                int i18 = ((i14 - (i15 * 832)) - (i16 * 400)) >> 10;
                int i19 = (i14 + (i16 * 2066)) >> 10;
                if (i17 < 0) {
                    i17 = 0;
                } else if (i17 > 255) {
                    i17 = 255;
                }
                if (i18 < 0) {
                    i18 = 0;
                } else if (i18 > 255) {
                    i18 = 255;
                }
                if (i19 < 0) {
                    i9 = 0;
                } else if (i19 <= 255) {
                    i9 = i19;
                }
                iArr[i5] = i9 | (i17 << 16) | 0 | (i18 << 8);
                i7++;
                i8++;
                i5++;
            }
            i4++;
            i6++;
        }
    }

    /* access modifiers changed from: package-private */
    public void encodeYUV420SP(byte[] bArr, int[] iArr, int i, int i2) {
        int i3 = i * i2;
        int length = ((bArr.length - i3) / 2) + i3;
        PrintStream printStream = System.out;
        printStream.println(bArr.length + " " + i3);
        int i4 = 0;
        int i5 = 0;
        for (int i6 = 0; i6 < i2; i6++) {
            int i7 = 0;
            while (i7 < i) {
                int i8 = iArr[i4];
                int i9 = (iArr[i4] & 16711680) >> 16;
                int i10 = (iArr[i4] & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                int i11 = 255;
                int i12 = (iArr[i4] & 255) >> 0;
                int i13 = (((((i9 * 66) + (i10 * 129)) + (i12 * 25)) + 128) >> 8) + 16;
                int i14 = (((((i9 * -38) - (i10 * 74)) + (i12 * 112)) + 128) >> 8) + 128;
                int i15 = (((((i9 * 112) - (i10 * 94)) - (i12 * 18)) + 128) >> 8) + 128;
                int i16 = i5 + 1;
                if (i13 < 0) {
                    i13 = 0;
                } else if (i13 > 255) {
                    i13 = 255;
                }
                bArr[i5] = (byte) i13;
                if (i6 % 2 == 0 && i4 % 2 == 0) {
                    int i17 = i3 + 1;
                    if (i14 < 0) {
                        i14 = 0;
                    } else if (i14 > 255) {
                        i14 = 255;
                    }
                    bArr[i3] = (byte) i14;
                    int i18 = length + 1;
                    if (i15 < 0) {
                        i11 = 0;
                    } else if (i15 <= 255) {
                        i11 = i15;
                    }
                    bArr[length] = (byte) i11;
                    length = i18;
                    i3 = i17;
                }
                i4++;
                i7++;
                i5 = i16;
            }
        }
    }
}
