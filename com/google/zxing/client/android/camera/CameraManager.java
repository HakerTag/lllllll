package com.google.zxing.client.android.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.client.android.camera.open.OpenCamera;
import com.google.zxing.client.android.camera.open.OpenCameraInterface;
import java.io.IOException;

public final class CameraManager {
    private static final int MAX_FRAME_HEIGHT = 675;
    private static final int MAX_FRAME_WIDTH = 1200;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MIN_FRAME_WIDTH = 240;
    private static final String TAG = CameraManager.class.getSimpleName();
    private AutoFocusManager autoFocusManager;
    private OpenCamera camera;
    private final CameraConfigurationManager configManager;
    private final Context context;
    private Rect framingRect;
    private Rect framingRectInPreview;
    private boolean initialized;
    private final PreviewCallback previewCallback;
    private boolean previewing;
    private int requestedCameraId = -1;
    private int requestedFramingRectHeight;
    private int requestedFramingRectWidth;
    private boolean torchInitiallyOn;
    private WindowManager windowManager;

    public CameraManager(Context context2) {
        this.context = context2.getApplicationContext();
        this.configManager = new CameraConfigurationManager(context2);
        this.previewCallback = new PreviewCallback(this.configManager);
        this.windowManager = (WindowManager) this.context.getSystemService("window");
    }

    public synchronized void openDriver(SurfaceHolder holder) throws IOException {
        OpenCamera theCamera = this.camera;
        if (theCamera == null) {
            theCamera = OpenCameraInterface.open(this.requestedCameraId);
            if (theCamera != null) {
                this.camera = theCamera;
            } else {
                throw new IOException("Camera.open() failed to return object from driver");
            }
        }
        if (!this.initialized) {
            this.initialized = true;
            this.configManager.initFromCameraParameters(theCamera);
            if (this.requestedFramingRectWidth > 0 && this.requestedFramingRectHeight > 0) {
                setManualFramingRect(this.requestedFramingRectWidth, this.requestedFramingRectHeight);
                this.requestedFramingRectWidth = 0;
                this.requestedFramingRectHeight = 0;
            }
        }
        Camera cameraObject = theCamera.getCamera();
        Camera.Parameters parameters = cameraObject.getParameters();
        String parametersFlattened = parameters == null ? null : parameters.flatten();
        try {
            this.configManager.setDesiredCameraParameters(theCamera, false);
        } catch (RuntimeException e) {
            Log.w(TAG, "Camera rejected parameters. Setting only minimal safe-mode parameters");
            String str = TAG;
            Log.i(str, "Resetting to saved camera params: " + parametersFlattened);
            if (parametersFlattened != null) {
                Camera.Parameters parameters2 = cameraObject.getParameters();
                parameters2.unflatten(parametersFlattened);
                try {
                    cameraObject.setParameters(parameters2);
                    this.configManager.setDesiredCameraParameters(theCamera, true);
                } catch (RuntimeException e2) {
                    Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
                }
            }
        }
        cameraObject.setPreviewDisplay(holder);
        if (this.torchInitiallyOn) {
            setTorch(true);
        }
    }

    public synchronized boolean isOpen() {
        return this.camera != null;
    }

    public synchronized void closeDriver() {
        if (this.camera != null) {
            this.camera.getCamera().release();
            this.camera = null;
            this.framingRect = null;
            this.framingRectInPreview = null;
        }
    }

    public synchronized void startPreview() {
        OpenCamera theCamera = this.camera;
        if (theCamera != null && !this.previewing) {
            theCamera.getCamera().startPreview();
            this.previewing = true;
            this.autoFocusManager = new AutoFocusManager(this.context, theCamera.getCamera());
        }
    }

    public synchronized void stopPreview() {
        if (this.autoFocusManager != null) {
            this.autoFocusManager.stop();
            this.autoFocusManager = null;
        }
        if (this.camera != null && this.previewing) {
            this.camera.getCamera().stopPreview();
            this.previewCallback.setHandler(null, 0);
            this.previewing = false;
        }
    }

    public synchronized boolean isTorchOn() {
        return this.camera != null && this.configManager.getTorchState(this.camera.getCamera());
    }

    public synchronized void setTorch(boolean newSetting) {
        OpenCamera theCamera = this.camera;
        if (!(theCamera == null || newSetting == this.configManager.getTorchState(theCamera.getCamera()))) {
            boolean wasAutoFocusManager = this.autoFocusManager != null;
            if (wasAutoFocusManager) {
                this.autoFocusManager.stop();
                this.autoFocusManager = null;
            }
            this.configManager.setTorch(theCamera.getCamera(), newSetting);
            if (wasAutoFocusManager) {
                AutoFocusManager autoFocusManager2 = new AutoFocusManager(this.context, theCamera.getCamera());
                this.autoFocusManager = autoFocusManager2;
                autoFocusManager2.start();
            }
        }
    }

    public synchronized void requestPreviewFrame(Handler handler, int message) {
        OpenCamera theCamera = this.camera;
        if (theCamera != null && this.previewing) {
            this.previewCallback.setHandler(handler, message);
            theCamera.getCamera().setOneShotPreviewCallback(this.previewCallback);
        }
    }

    public synchronized Rect getFramingRect() {
        if (this.framingRect == null) {
            if (this.camera == null) {
                return null;
            }
            Point screenResolution = this.configManager.getScreenResolution();
            if (screenResolution == null) {
                return null;
            }
            int width = findDesiredDimensionInRange(screenResolution.x, 240, MAX_FRAME_WIDTH);
            int height = findDesiredDimensionInRange(screenResolution.y, 240, MAX_FRAME_HEIGHT);
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            this.framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
            String str = TAG;
            Log.d(str, "Calculated framing rect: " + this.framingRect);
        }
        return this.framingRect;
    }

    private static int findDesiredDimensionInRange(int resolution, int hardMin, int hardMax) {
        int dim = (resolution * 5) / 8;
        if (dim < hardMin) {
            return hardMin;
        }
        if (dim > hardMax) {
            return hardMax;
        }
        return dim;
    }

    public synchronized Rect getFramingRectInPreview() {
        if (this.framingRectInPreview == null) {
            Rect framingRect2 = getFramingRect();
            if (framingRect2 == null) {
                return null;
            }
            Rect rect = new Rect(framingRect2);
            Point cameraResolution = this.configManager.getCameraResolution();
            Point screenResolution = this.configManager.getScreenResolution();
            if (cameraResolution == null || screenResolution == null) {
                return null;
            }
            if (this.context.getApplicationContext().getResources().getConfiguration().orientation == 1) {
                rect.left = (rect.left * cameraResolution.y) / screenResolution.x;
                rect.right = (rect.right * cameraResolution.y) / screenResolution.x;
                rect.top = (rect.top * cameraResolution.x) / screenResolution.y;
                rect.bottom = (rect.bottom * cameraResolution.x) / screenResolution.y;
            } else {
                rect.left = (rect.left * cameraResolution.x) / screenResolution.x;
                rect.right = (rect.right * cameraResolution.x) / screenResolution.x;
                rect.top = (rect.top * cameraResolution.y) / screenResolution.y;
                rect.bottom = (rect.bottom * cameraResolution.y) / screenResolution.y;
            }
            this.framingRectInPreview = rect;
        }
        return this.framingRectInPreview;
    }

    public synchronized void setManualCameraId(int cameraId) {
        this.requestedCameraId = cameraId;
    }

    public synchronized void setTorchInitiallyOn(boolean on) {
        this.torchInitiallyOn = on;
    }

    public synchronized void setManualFramingRect(int width, int height) {
        if (this.initialized) {
            this.framingRect = getFramingRect();
            String str = TAG;
            Log.d(str, "Calculated manual framing rect: " + this.framingRect);
            this.framingRectInPreview = null;
        } else {
            this.requestedFramingRectWidth = width;
            this.requestedFramingRectHeight = height;
        }
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        int width2 = width;
        int height2 = height;
        byte[] rotatedData = new byte[data.length];
        int rotation = this.context.getApplicationContext().getResources().getConfiguration().orientation;
        if (rotation == 1) {
            for (int y = 0; y < height2; y++) {
                for (int x = 0; x < width2; x++) {
                    rotatedData[(((x * height2) + height2) - y) - 1] = data[(y * width2) + x];
                }
            }
            width2 = height;
            height2 = width;
        } else {
            rotatedData = null;
        }
        Rect rect = getFramingRectInPreview();
        if (rect == null) {
            return null;
        }
        return new PlanarYUVLuminanceSource(rotation == 1 ? rotatedData : data, width2, height2, rect.left, rect.top, rect.width(), rect.height(), false);
    }
}
