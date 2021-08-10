package com.google.zxing.client.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ViewfinderView extends View {
    private static final long ANIMATION_DELAY = 80;
    private static final int CURRENT_POINT_OPACITY = 160;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private CameraManager cameraManager;
    private final int laserColor;
    private List<ResultPoint> lastPossibleResultPoints;
    private final int maskColor;
    private final Paint paint = new Paint(1);
    private List<ResultPoint> possibleResultPoints;
    private Bitmap resultBitmap;
    private final int resultColor;
    private final int resultPointColor;
    private int scannerAlpha;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources resources = getResources();
        this.maskColor = resources.getColor(R.color.viewfinder_mask);
        this.resultColor = resources.getColor(R.color.result_view);
        this.laserColor = resources.getColor(R.color.viewfinder_laser);
        this.resultPointColor = resources.getColor(R.color.possible_result_points);
        this.scannerAlpha = 0;
        this.possibleResultPoints = new ArrayList(5);
        this.lastPossibleResultPoints = null;
    }

    public void setCameraManager(CameraManager cameraManager2) {
        this.cameraManager = cameraManager2;
    }

    public void onDraw(Canvas canvas) {
        List<ResultPoint> currentPossible;
        CameraManager cameraManager2 = this.cameraManager;
        if (cameraManager2 != null) {
            Rect frame = cameraManager2.getFramingRect();
            Rect previewFrame = this.cameraManager.getFramingRectInPreview();
            if (!(frame == null || previewFrame == null)) {
                int width = canvas.getWidth();
                int height = canvas.getHeight();
                this.paint.setColor(this.resultBitmap != null ? this.resultColor : this.maskColor);
                canvas.drawRect(0.0f, 0.0f, (float) width, (float) frame.top, this.paint);
                canvas.drawRect(0.0f, (float) frame.top, (float) frame.left, (float) (frame.bottom + 1), this.paint);
                canvas.drawRect((float) (frame.right + 1), (float) frame.top, (float) width, (float) (frame.bottom + 1), this.paint);
                canvas.drawRect(0.0f, (float) (frame.bottom + 1), (float) width, (float) height, this.paint);
                if (this.resultBitmap != null) {
                    this.paint.setAlpha(CURRENT_POINT_OPACITY);
                    canvas.drawBitmap(this.resultBitmap, (Rect) null, frame, this.paint);
                    return;
                }
                this.paint.setColor(this.laserColor);
                this.paint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
                this.scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
                int middle = (frame.height() / 2) + frame.top;
                canvas.drawRect((float) (frame.left + 2), (float) (middle - 1), (float) (frame.right - 1), (float) (middle + 2), this.paint);
                float scaleX = ((float) frame.width()) / ((float) previewFrame.width());
                float scaleY = ((float) frame.height()) / ((float) previewFrame.height());
                List<ResultPoint> currentPossible2 = this.possibleResultPoints;
                List<ResultPoint> currentLast = this.lastPossibleResultPoints;
                int frameLeft = frame.left;
                int frameTop = frame.top;
                if (currentPossible2.isEmpty()) {
                    this.lastPossibleResultPoints = null;
                } else {
                    this.possibleResultPoints = new ArrayList(5);
                    this.lastPossibleResultPoints = currentPossible2;
                    this.paint.setAlpha(CURRENT_POINT_OPACITY);
                    this.paint.setColor(this.resultPointColor);
                    synchronized (currentPossible2) {
                        try {
                            for (Iterator<ResultPoint> it = currentPossible2.iterator(); it.hasNext(); it = it) {
                                try {
                                    ResultPoint point = it.next();
                                    canvas.drawCircle((float) (((int) (point.getX() * scaleX)) + frameLeft), (float) (((int) (point.getY() * scaleY)) + frameTop), 6.0f, this.paint);
                                } catch (Throwable th) {
                                    th = th;
                                    currentPossible = currentPossible2;
                                    while (true) {
                                        try {
                                            break;
                                        } catch (Throwable th2) {
                                            th = th2;
                                        }
                                    }
                                    throw th;
                                }
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            currentPossible = currentPossible2;
                            while (true) {
                                break;
                            }
                            throw th;
                        }
                    }
                }
                if (currentLast != null) {
                    this.paint.setAlpha(80);
                    this.paint.setColor(this.resultPointColor);
                    synchronized (currentLast) {
                        for (Iterator<ResultPoint> it2 = currentLast.iterator(); it2.hasNext(); it2 = it2) {
                            ResultPoint point2 = it2.next();
                            canvas.drawCircle((float) (((int) (point2.getX() * scaleX)) + frameLeft), (float) (((int) (point2.getY() * scaleY)) + frameTop), 3.0f, this.paint);
                        }
                    }
                }
                postInvalidateDelayed(ANIMATION_DELAY, frame.left - 6, frame.top - 6, frame.right + 6, frame.bottom + 6);
            }
        }
    }

    public void drawViewfinder() {
        Bitmap resultBitmap2 = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap2 != null) {
            resultBitmap2.recycle();
        }
        invalidate();
    }

    public void drawResultBitmap(Bitmap barcode) {
        this.resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = this.possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > 20) {
                points.subList(0, size - 10).clear();
            }
        }
    }
}
