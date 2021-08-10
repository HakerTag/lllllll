package com.google.zxing.client.android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

final class DecodeHandler extends Handler {
    private static final String TAG = DecodeHandler.class.getSimpleName();
    private final CaptureActivity activity;
    private int frameCount;
    private final MultiFormatReader multiFormatReader;
    private boolean running = true;

    DecodeHandler(CaptureActivity activity2, Map<DecodeHintType, Object> hints) {
        MultiFormatReader multiFormatReader2 = new MultiFormatReader();
        this.multiFormatReader = multiFormatReader2;
        multiFormatReader2.setHints(hints);
        this.activity = activity2;
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

    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        Result rawResult = null;
        if (this.frameCount == 3) {
            this.frameCount = 0;
            int[] argb = new int[(width * height)];
            YUV_NV21_TO_RGB(argb, data, width, height);
            for (int i = 0; i < argb.length; i++) {
                argb[i] = ViewCompat.MEASURED_SIZE_MASK - argb[i];
            }
            encodeYUV420SP(data, argb, width, height);
        }
        this.frameCount++;
        PlanarYUVLuminanceSource source = this.activity.getCameraManager().buildLuminanceSource(data, width, height);
        if (source != null) {
            try {
                rawResult = this.multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
            } catch (ReaderException e) {
            } catch (Throwable th) {
                this.multiFormatReader.reset();
                throw th;
            }
            this.multiFormatReader.reset();
        }
        Handler handler = this.activity.getHandler();
        if (rawResult != null) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode in " + (end - start) + " ms");
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, rawResult);
                Bundle bundle = new Bundle();
                bundleThumbnail(source, bundle);
                message.setData(bundle);
                message.sendToTarget();
            }
        } else if (handler != null) {
            Message.obtain(handler, R.id.decode_failed).sendToTarget();
        }
    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, source.getThumbnailHeight(), Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
        bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, ((float) width) / ((float) source.getWidth()));
    }

    /* JADX INFO: Multiple debug info for r0v6 int: [D('a1' int), D('r' int)] */
    /* JADX INFO: Multiple debug info for r1v5 int: [D('a2' int), D('g' int)] */
    /* JADX INFO: Multiple debug info for r2v5 int: [D('a3' int), D('b' int)] */
    private static void YUV_NV21_TO_RGB(int[] argb, byte[] yuv, int width, int height) {
        int b;
        int i = width;
        int i2 = height;
        int frameSize = i * i2;
        int a = 0;
        int a2 = 0;
        int i3 = 0;
        int ci = 0;
        while (i3 < i2) {
            int j = 0;
            int cj = 0;
            while (j < i) {
                int y = yuv[(ci * i) + cj] & 255;
                int v = yuv[((ci >> 1) * i) + frameSize + (cj & -2) + 0] & 255;
                int u = yuv[frameSize + ((ci >> 1) * i) + (cj & -2) + 1] & 255;
                int y2 = 16;
                if (y >= 16) {
                    y2 = y;
                }
                int a0 = (y2 - 16) * 1192;
                int r = (a0 + ((v - 128) * 1634)) >> 10;
                int g = ((a0 - ((v - 128) * 832)) - ((u - 128) * 400)) >> 10;
                int b2 = (a0 + ((u - 128) * 2066)) >> 10;
                int a4 = r < 0 ? 0 : r > 255 ? 255 : r;
                int g2 = g < 0 ? 0 : g > 255 ? 255 : g;
                if (b2 < 0) {
                    b = 0;
                } else {
                    b = 255;
                    if (b2 <= 255) {
                        b = b2;
                    }
                }
                argb[a2] = (a4 << 16) | 0 | (g2 << 8) | b;
                j++;
                cj++;
                i = width;
                a2++;
                frameSize = frameSize;
                a = a;
            }
            i3++;
            ci++;
            i = width;
            i2 = height;
        }
    }

    /* access modifiers changed from: package-private */
    public void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        int i;
        int V = width;
        int i2 = height;
        int frameSize = V * i2;
        int yIndex = 0;
        int uIndex = frameSize;
        int vIndex = ((yuv420sp.length - frameSize) / 2) + frameSize;
        PrintStream printStream = System.out;
        printStream.println(yuv420sp.length + " " + frameSize);
        int index = 0;
        int j = 0;
        while (j < i2) {
            int i3 = 0;
            while (i3 < V) {
                int i4 = (argb[index] & ViewCompat.MEASURED_STATE_MASK) >> 24;
                int R = (argb[index] & 16711680) >> 16;
                int G = (argb[index] & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                int B = (argb[index] & 255) >> 0;
                int Y = (((((R * 66) + (G * 129)) + (B * 25)) + 128) >> 8) + 16;
                int U = (((((R * -38) - (G * 74)) + (B * 112)) + 128) >> 8) + 128;
                int V2 = (((((R * 112) - (G * 94)) - (B * 18)) + 128) >> 8) + 128;
                int yIndex2 = yIndex + 1;
                yuv420sp[yIndex] = (byte) (Y < 0 ? 0 : Y > 255 ? 255 : Y);
                if (j % 2 == 0 && index % 2 == 0) {
                    int uIndex2 = uIndex + 1;
                    yuv420sp[uIndex] = (byte) (U < 0 ? 0 : U > 255 ? 255 : U);
                    int vIndex2 = vIndex + 1;
                    if (V2 < 0) {
                        i = 0;
                    } else {
                        i = 255;
                        if (V2 <= 255) {
                            i = V2;
                        }
                    }
                    yuv420sp[vIndex] = (byte) i;
                    uIndex = uIndex2;
                    vIndex = vIndex2;
                }
                index++;
                i3++;
                V = width;
                yIndex = yIndex2;
            }
            j++;
            V = width;
            i2 = height;
        }
    }
}
