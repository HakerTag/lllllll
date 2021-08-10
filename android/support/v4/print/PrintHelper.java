package android.support.v4.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class PrintHelper {
    public static final int COLOR_MODE_COLOR = 2;
    public static final int COLOR_MODE_MONOCHROME = 1;
    public static final int ORIENTATION_LANDSCAPE = 1;
    public static final int ORIENTATION_PORTRAIT = 2;
    public static final int SCALE_MODE_FILL = 2;
    public static final int SCALE_MODE_FIT = 1;
    private final PrintHelperVersionImpl mImpl;

    @Retention(RetentionPolicy.SOURCE)
    private @interface ColorMode {
    }

    public interface OnPrintFinishCallback {
        void onFinish();
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface Orientation {
    }

    interface PrintHelperVersionImpl {
        int getColorMode();

        int getOrientation();

        int getScaleMode();

        void printBitmap(String str, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback);

        void printBitmap(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback) throws FileNotFoundException;

        void setColorMode(int i);

        void setOrientation(int i);

        void setScaleMode(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface ScaleMode {
    }

    public static boolean systemSupportsPrint() {
        return Build.VERSION.SDK_INT >= 19;
    }

    private static final class PrintHelperStub implements PrintHelperVersionImpl {
        int mColorMode;
        int mOrientation;
        int mScaleMode;

        private PrintHelperStub() {
            this.mScaleMode = 2;
            this.mColorMode = 2;
            this.mOrientation = 1;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setScaleMode(int scaleMode) {
            this.mScaleMode = scaleMode;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getScaleMode() {
            return this.mScaleMode;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getColorMode() {
            return this.mColorMode;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setColorMode(int colorMode) {
            this.mColorMode = colorMode;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setOrientation(int orientation) {
            this.mOrientation = orientation;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getOrientation() {
            return this.mOrientation;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void printBitmap(String jobName, Bitmap bitmap, OnPrintFinishCallback callback) {
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void printBitmap(String jobName, Uri imageFile, OnPrintFinishCallback callback) {
        }
    }

    /* access modifiers changed from: private */
    public static class PrintHelperApi19 implements PrintHelperVersionImpl {
        private static final String LOG_TAG = "PrintHelperApi19";
        private static final int MAX_PRINT_SIZE = 3500;
        int mColorMode = 2;
        final Context mContext;
        BitmapFactory.Options mDecodeOptions = null;
        protected boolean mIsMinMarginsHandlingCorrect = true;
        private final Object mLock = new Object();
        int mOrientation;
        protected boolean mPrintActivityRespectsOrientation = true;
        int mScaleMode = 2;

        PrintHelperApi19(Context context) {
            this.mContext = context;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setScaleMode(int scaleMode) {
            this.mScaleMode = scaleMode;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getScaleMode() {
            return this.mScaleMode;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setColorMode(int colorMode) {
            this.mColorMode = colorMode;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setOrientation(int orientation) {
            this.mOrientation = orientation;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getOrientation() {
            int i = this.mOrientation;
            if (i == 0) {
                return 1;
            }
            return i;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getColorMode() {
            return this.mColorMode;
        }

        /* access modifiers changed from: private */
        public static boolean isPortrait(Bitmap bitmap) {
            return bitmap.getWidth() <= bitmap.getHeight();
        }

        /* access modifiers changed from: protected */
        public PrintAttributes.Builder copyAttributes(PrintAttributes other) {
            PrintAttributes.Builder b = new PrintAttributes.Builder().setMediaSize(other.getMediaSize()).setResolution(other.getResolution()).setMinMargins(other.getMinMargins());
            if (other.getColorMode() != 0) {
                b.setColorMode(other.getColorMode());
            }
            return b;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void printBitmap(final String jobName, final Bitmap bitmap, final OnPrintFinishCallback callback) {
            PrintAttributes.MediaSize mediaSize;
            if (bitmap != null) {
                final int fittingMode = this.mScaleMode;
                PrintManager printManager = (PrintManager) this.mContext.getSystemService("print");
                if (isPortrait(bitmap)) {
                    mediaSize = PrintAttributes.MediaSize.UNKNOWN_PORTRAIT;
                } else {
                    mediaSize = PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE;
                }
                printManager.print(jobName, new PrintDocumentAdapter() {
                    /* class android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass1 */
                    private PrintAttributes mAttributes;

                    public void onLayout(PrintAttributes oldPrintAttributes, PrintAttributes newPrintAttributes, CancellationSignal cancellationSignal, PrintDocumentAdapter.LayoutResultCallback layoutResultCallback, Bundle bundle) {
                        this.mAttributes = newPrintAttributes;
                        layoutResultCallback.onLayoutFinished(new PrintDocumentInfo.Builder(jobName).setContentType(1).setPageCount(1).build(), true ^ newPrintAttributes.equals(oldPrintAttributes));
                    }

                    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor fileDescriptor, CancellationSignal cancellationSignal, PrintDocumentAdapter.WriteResultCallback writeResultCallback) {
                        PrintHelperApi19.this.writeBitmap(this.mAttributes, fittingMode, bitmap, fileDescriptor, cancellationSignal, writeResultCallback);
                    }

                    public void onFinish() {
                        OnPrintFinishCallback onPrintFinishCallback = callback;
                        if (onPrintFinishCallback != null) {
                            onPrintFinishCallback.onFinish();
                        }
                    }
                }, new PrintAttributes.Builder().setMediaSize(mediaSize).setColorMode(this.mColorMode).build());
            }
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private Matrix getMatrix(int imageWidth, int imageHeight, RectF content, int fittingMode) {
            float scale;
            Matrix matrix = new Matrix();
            float scale2 = content.width() / ((float) imageWidth);
            if (fittingMode == 2) {
                scale = Math.max(scale2, content.height() / ((float) imageHeight));
            } else {
                scale = Math.min(scale2, content.height() / ((float) imageHeight));
            }
            matrix.postScale(scale, scale);
            matrix.postTranslate((content.width() - (((float) imageWidth) * scale)) / 2.0f, (content.height() - (((float) imageHeight) * scale)) / 2.0f);
            return matrix;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void writeBitmap(final PrintAttributes attributes, final int fittingMode, final Bitmap bitmap, final ParcelFileDescriptor fileDescriptor, final CancellationSignal cancellationSignal, final PrintDocumentAdapter.WriteResultCallback writeResultCallback) {
            final PrintAttributes pdfAttributes;
            if (this.mIsMinMarginsHandlingCorrect) {
                pdfAttributes = attributes;
            } else {
                pdfAttributes = copyAttributes(attributes).setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0)).build();
            }
            new AsyncTask<Void, Void, Throwable>() {
                /* class android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass2 */

                /* access modifiers changed from: protected */
                public Throwable doInBackground(Void... params) {
                    RectF contentRect;
                    try {
                        if (cancellationSignal.isCanceled()) {
                            return null;
                        }
                        PrintedPdfDocument pdfDocument = new PrintedPdfDocument(PrintHelperApi19.this.mContext, pdfAttributes);
                        Bitmap maybeGrayscale = PrintHelperApi19.this.convertBitmapForColorMode(bitmap, pdfAttributes.getColorMode());
                        if (cancellationSignal.isCanceled()) {
                            return null;
                        }
                        try {
                            PdfDocument.Page page = pdfDocument.startPage(1);
                            if (PrintHelperApi19.this.mIsMinMarginsHandlingCorrect) {
                                contentRect = new RectF(page.getInfo().getContentRect());
                            } else {
                                PrintedPdfDocument dummyDocument = new PrintedPdfDocument(PrintHelperApi19.this.mContext, attributes);
                                PdfDocument.Page dummyPage = dummyDocument.startPage(1);
                                RectF contentRect2 = new RectF(dummyPage.getInfo().getContentRect());
                                dummyDocument.finishPage(dummyPage);
                                dummyDocument.close();
                                contentRect = contentRect2;
                            }
                            Matrix matrix = PrintHelperApi19.this.getMatrix(maybeGrayscale.getWidth(), maybeGrayscale.getHeight(), contentRect, fittingMode);
                            if (!PrintHelperApi19.this.mIsMinMarginsHandlingCorrect) {
                                matrix.postTranslate(contentRect.left, contentRect.top);
                                page.getCanvas().clipRect(contentRect);
                            }
                            page.getCanvas().drawBitmap(maybeGrayscale, matrix, null);
                            pdfDocument.finishPage(page);
                            if (cancellationSignal.isCanceled()) {
                                return null;
                            }
                            pdfDocument.writeTo(new FileOutputStream(fileDescriptor.getFileDescriptor()));
                            pdfDocument.close();
                            if (fileDescriptor != null) {
                                try {
                                    fileDescriptor.close();
                                } catch (IOException e) {
                                }
                            }
                            if (maybeGrayscale != bitmap) {
                                maybeGrayscale.recycle();
                            }
                            return null;
                        } finally {
                            pdfDocument.close();
                            if (fileDescriptor != null) {
                                try {
                                    fileDescriptor.close();
                                } catch (IOException e2) {
                                }
                            }
                            if (maybeGrayscale != bitmap) {
                                maybeGrayscale.recycle();
                            }
                        }
                    } catch (Throwable t) {
                        return t;
                    }
                }

                /* access modifiers changed from: protected */
                public void onPostExecute(Throwable throwable) {
                    if (cancellationSignal.isCanceled()) {
                        writeResultCallback.onWriteCancelled();
                    } else if (throwable == null) {
                        writeResultCallback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                    } else {
                        Log.e(PrintHelperApi19.LOG_TAG, "Error writing printed content", throwable);
                        writeResultCallback.onWriteFailed(null);
                    }
                }
            }.execute(new Void[0]);
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void printBitmap(final String jobName, final Uri imageFile, final OnPrintFinishCallback callback) throws FileNotFoundException {
            final int fittingMode = this.mScaleMode;
            PrintDocumentAdapter printDocumentAdapter = new PrintDocumentAdapter() {
                /* class android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass3 */
                private PrintAttributes mAttributes;
                Bitmap mBitmap = null;
                AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;

                public void onLayout(final PrintAttributes oldPrintAttributes, final PrintAttributes newPrintAttributes, final CancellationSignal cancellationSignal, final PrintDocumentAdapter.LayoutResultCallback layoutResultCallback, Bundle bundle) {
                    synchronized (this) {
                        this.mAttributes = newPrintAttributes;
                    }
                    if (cancellationSignal.isCanceled()) {
                        layoutResultCallback.onLayoutCancelled();
                    } else if (this.mBitmap != null) {
                        layoutResultCallback.onLayoutFinished(new PrintDocumentInfo.Builder(jobName).setContentType(1).setPageCount(1).build(), true ^ newPrintAttributes.equals(oldPrintAttributes));
                    } else {
                        this.mLoadBitmap = new AsyncTask<Uri, Boolean, Bitmap>() {
                            /* class android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass3.AnonymousClass1 */

                            /* access modifiers changed from: protected */
                            public void onPreExecute() {
                                cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
                                    /* class android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass3.AnonymousClass1.AnonymousClass1 */

                                    public void onCancel() {
                                        AnonymousClass3.this.cancelLoad();
                                        AnonymousClass1.this.cancel(false);
                                    }
                                });
                            }

                            /* access modifiers changed from: protected */
                            public Bitmap doInBackground(Uri... uris) {
                                try {
                                    return PrintHelperApi19.this.loadConstrainedBitmap(imageFile);
                                } catch (FileNotFoundException e) {
                                    return null;
                                }
                            }

                            /* access modifiers changed from: protected */
                            /* JADX WARNING: Code restructure failed: missing block: B:11:0x0022, code lost:
                                if (r1 == null) goto L_0x0052;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:13:0x002c, code lost:
                                if (r1.isPortrait() == android.support.v4.print.PrintHelper.PrintHelperApi19.isPortrait(r12)) goto L_0x0052;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:14:0x002e, code lost:
                                r2 = new android.graphics.Matrix();
                                r2.postRotate(90.0f);
                                r12 = android.graphics.Bitmap.createBitmap(r12, 0, 0, r12.getWidth(), r12.getHeight(), r2, true);
                             */
                            /* Code decompiled incorrectly, please refer to instructions dump. */
                            public void onPostExecute(android.graphics.Bitmap r12) {
                                /*
                                    r11 = this;
                                    super.onPostExecute(r12)
                                    r0 = 0
                                    if (r12 == 0) goto L_0x0052
                                    android.support.v4.print.PrintHelper$PrintHelperApi19$3 r1 = android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass3.this
                                    android.support.v4.print.PrintHelper$PrintHelperApi19 r1 = android.support.v4.print.PrintHelper.PrintHelperApi19.this
                                    boolean r1 = r1.mPrintActivityRespectsOrientation
                                    if (r1 == 0) goto L_0x0016
                                    android.support.v4.print.PrintHelper$PrintHelperApi19$3 r1 = android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass3.this
                                    android.support.v4.print.PrintHelper$PrintHelperApi19 r1 = android.support.v4.print.PrintHelper.PrintHelperApi19.this
                                    int r1 = r1.mOrientation
                                    if (r1 != 0) goto L_0x0052
                                L_0x0016:
                                    monitor-enter(r11)
                                    android.support.v4.print.PrintHelper$PrintHelperApi19$3 r1 = android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass3.this     // Catch:{ all -> 0x004a }
                                    android.print.PrintAttributes r1 = android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass3.access$500(r1)     // Catch:{ all -> 0x004a }
                                    android.print.PrintAttributes$MediaSize r1 = r1.getMediaSize()     // Catch:{ all -> 0x004a }
                                    monitor-exit(r11)     // Catch:{ all -> 0x0050 }
                                    if (r1 == 0) goto L_0x0052
                                    boolean r2 = r1.isPortrait()
                                    boolean r3 = android.support.v4.print.PrintHelper.PrintHelperApi19.access$600(r12)
                                    if (r2 == r3) goto L_0x0052
                                    android.graphics.Matrix r2 = new android.graphics.Matrix
                                    r2.<init>()
                                    r3 = 1119092736(0x42b40000, float:90.0)
                                    r2.postRotate(r3)
                                    r4 = 0
                                    r5 = 0
                                    int r6 = r12.getWidth()
                                    int r7 = r12.getHeight()
                                    r9 = 1
                                    r3 = r12
                                    r8 = r2
                                    android.graphics.Bitmap r12 = android.graphics.Bitmap.createBitmap(r3, r4, r5, r6, r7, r8, r9)
                                    goto L_0x0052
                                L_0x004a:
                                    r1 = move-exception
                                    r10 = r1
                                    r1 = r0
                                    r0 = r10
                                L_0x004e:
                                    monitor-exit(r11)
                                    throw r0
                                L_0x0050:
                                    r0 = move-exception
                                    goto L_0x004e
                                L_0x0052:
                                    android.support.v4.print.PrintHelper$PrintHelperApi19$3 r1 = android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass3.this
                                    r1.mBitmap = r12
                                    if (r12 == 0) goto L_0x007d
                                    android.print.PrintDocumentInfo$Builder r1 = new android.print.PrintDocumentInfo$Builder
                                    android.support.v4.print.PrintHelper$PrintHelperApi19$3 r2 = android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass3.this
                                    java.lang.String r2 = r9
                                    r1.<init>(r2)
                                    r2 = 1
                                    android.print.PrintDocumentInfo$Builder r1 = r1.setContentType(r2)
                                    android.print.PrintDocumentInfo$Builder r1 = r1.setPageCount(r2)
                                    android.print.PrintDocumentInfo r1 = r1.build()
                                    android.print.PrintAttributes r3 = r10
                                    android.print.PrintAttributes r4 = r9
                                    boolean r3 = r3.equals(r4)
                                    r2 = r2 ^ r3
                                    android.print.PrintDocumentAdapter$LayoutResultCallback r3 = r12
                                    r3.onLayoutFinished(r1, r2)
                                    goto L_0x0082
                                L_0x007d:
                                    android.print.PrintDocumentAdapter$LayoutResultCallback r1 = r12
                                    r1.onLayoutFailed(r0)
                                L_0x0082:
                                    android.support.v4.print.PrintHelper$PrintHelperApi19$3 r1 = android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass3.this
                                    r1.mLoadBitmap = r0
                                    return
                                */
                                throw new UnsupportedOperationException("Method not decompiled: android.support.v4.print.PrintHelper.PrintHelperApi19.AnonymousClass3.AnonymousClass1.onPostExecute(android.graphics.Bitmap):void");
                            }

                            /* access modifiers changed from: protected */
                            public void onCancelled(Bitmap result) {
                                layoutResultCallback.onLayoutCancelled();
                                AnonymousClass3.this.mLoadBitmap = null;
                            }
                        }.execute(new Uri[0]);
                    }
                }

                /* access modifiers changed from: private */
                /* access modifiers changed from: public */
                private void cancelLoad() {
                    synchronized (PrintHelperApi19.this.mLock) {
                        if (PrintHelperApi19.this.mDecodeOptions != null) {
                            PrintHelperApi19.this.mDecodeOptions.requestCancelDecode();
                            PrintHelperApi19.this.mDecodeOptions = null;
                        }
                    }
                }

                public void onFinish() {
                    super.onFinish();
                    cancelLoad();
                    AsyncTask<Uri, Boolean, Bitmap> asyncTask = this.mLoadBitmap;
                    if (asyncTask != null) {
                        asyncTask.cancel(true);
                    }
                    OnPrintFinishCallback onPrintFinishCallback = callback;
                    if (onPrintFinishCallback != null) {
                        onPrintFinishCallback.onFinish();
                    }
                    Bitmap bitmap = this.mBitmap;
                    if (bitmap != null) {
                        bitmap.recycle();
                        this.mBitmap = null;
                    }
                }

                public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor fileDescriptor, CancellationSignal cancellationSignal, PrintDocumentAdapter.WriteResultCallback writeResultCallback) {
                    PrintHelperApi19.this.writeBitmap(this.mAttributes, fittingMode, this.mBitmap, fileDescriptor, cancellationSignal, writeResultCallback);
                }
            };
            PrintManager printManager = (PrintManager) this.mContext.getSystemService("print");
            PrintAttributes.Builder builder = new PrintAttributes.Builder();
            builder.setColorMode(this.mColorMode);
            int i = this.mOrientation;
            if (i == 1 || i == 0) {
                builder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
            } else if (i == 2) {
                builder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT);
            }
            printManager.print(jobName, printDocumentAdapter, builder.build());
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private Bitmap loadConstrainedBitmap(Uri uri) throws FileNotFoundException {
            Throwable th;
            if (uri == null || this.mContext == null) {
                throw new IllegalArgumentException("bad argument to getScaledBitmap");
            }
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            loadBitmap(uri, opt);
            int w = opt.outWidth;
            int h = opt.outHeight;
            if (w <= 0 || h <= 0) {
                return null;
            }
            int imageSide = Math.max(w, h);
            int sampleSize = 1;
            while (imageSide > MAX_PRINT_SIZE) {
                imageSide >>>= 1;
                sampleSize <<= 1;
            }
            if (sampleSize <= 0 || Math.min(w, h) / sampleSize <= 0) {
                return null;
            }
            synchronized (this.mLock) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    this.mDecodeOptions = options;
                    options.inMutable = true;
                    this.mDecodeOptions.inSampleSize = sampleSize;
                    BitmapFactory.Options decodeOptions = this.mDecodeOptions;
                    try {
                        try {
                            Bitmap loadBitmap = loadBitmap(uri, decodeOptions);
                            synchronized (this.mLock) {
                                this.mDecodeOptions = null;
                            }
                            return loadBitmap;
                        } catch (Throwable th2) {
                            synchronized (this.mLock) {
                                this.mDecodeOptions = null;
                                throw th2;
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    throw th;
                }
            }
        }

        private Bitmap loadBitmap(Uri uri, BitmapFactory.Options o) throws FileNotFoundException {
            Context context;
            if (uri == null || (context = this.mContext) == null) {
                throw new IllegalArgumentException("bad argument to loadBitmap");
            }
            InputStream is = null;
            try {
                is = context.getContentResolver().openInputStream(uri);
                return BitmapFactory.decodeStream(is, null, o);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException t) {
                        Log.w(LOG_TAG, "close fail ", t);
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private Bitmap convertBitmapForColorMode(Bitmap original, int colorMode) {
            if (colorMode != 1) {
                return original;
            }
            Bitmap grayscale = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(grayscale);
            Paint p = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0.0f);
            p.setColorFilter(new ColorMatrixColorFilter(cm));
            c.drawBitmap(original, 0.0f, 0.0f, p);
            c.setBitmap(null);
            return grayscale;
        }
    }

    private static class PrintHelperApi20 extends PrintHelperApi19 {
        PrintHelperApi20(Context context) {
            super(context);
            this.mPrintActivityRespectsOrientation = false;
        }
    }

    private static class PrintHelperApi23 extends PrintHelperApi20 {
        /* access modifiers changed from: protected */
        @Override // android.support.v4.print.PrintHelper.PrintHelperApi19
        public PrintAttributes.Builder copyAttributes(PrintAttributes other) {
            PrintAttributes.Builder b = super.copyAttributes(other);
            if (other.getDuplexMode() != 0) {
                b.setDuplexMode(other.getDuplexMode());
            }
            return b;
        }

        PrintHelperApi23(Context context) {
            super(context);
            this.mIsMinMarginsHandlingCorrect = false;
        }
    }

    private static class PrintHelperApi24 extends PrintHelperApi23 {
        PrintHelperApi24(Context context) {
            super(context);
            this.mIsMinMarginsHandlingCorrect = true;
            this.mPrintActivityRespectsOrientation = true;
        }
    }

    public PrintHelper(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            this.mImpl = new PrintHelperApi24(context);
        } else if (Build.VERSION.SDK_INT >= 23) {
            this.mImpl = new PrintHelperApi23(context);
        } else if (Build.VERSION.SDK_INT >= 20) {
            this.mImpl = new PrintHelperApi20(context);
        } else if (Build.VERSION.SDK_INT >= 19) {
            this.mImpl = new PrintHelperApi19(context);
        } else {
            this.mImpl = new PrintHelperStub();
        }
    }

    public void setScaleMode(int scaleMode) {
        this.mImpl.setScaleMode(scaleMode);
    }

    public int getScaleMode() {
        return this.mImpl.getScaleMode();
    }

    public void setColorMode(int colorMode) {
        this.mImpl.setColorMode(colorMode);
    }

    public int getColorMode() {
        return this.mImpl.getColorMode();
    }

    public void setOrientation(int orientation) {
        this.mImpl.setOrientation(orientation);
    }

    public int getOrientation() {
        return this.mImpl.getOrientation();
    }

    public void printBitmap(String jobName, Bitmap bitmap) {
        this.mImpl.printBitmap(jobName, bitmap, (OnPrintFinishCallback) null);
    }

    public void printBitmap(String jobName, Bitmap bitmap, OnPrintFinishCallback callback) {
        this.mImpl.printBitmap(jobName, bitmap, callback);
    }

    public void printBitmap(String jobName, Uri imageFile) throws FileNotFoundException {
        this.mImpl.printBitmap(jobName, imageFile, (OnPrintFinishCallback) null);
    }

    public void printBitmap(String jobName, Uri imageFile, OnPrintFinishCallback callback) throws FileNotFoundException {
        this.mImpl.printBitmap(jobName, imageFile, callback);
    }
}
