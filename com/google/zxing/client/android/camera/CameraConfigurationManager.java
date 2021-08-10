package com.google.zxing.client.android.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.android.camera.open.CameraFacing;
import com.google.zxing.client.android.camera.open.OpenCamera;

/* access modifiers changed from: package-private */
public final class CameraConfigurationManager {
    private static final String TAG = "CameraConfiguration";
    private Point bestPreviewSize;
    private Point cameraResolution;
    private final Context context;
    private int cwNeededRotation;
    private int cwRotationFromDisplayToCamera;
    private Point previewSizeOnScreen;
    private Point screenResolution;

    CameraConfigurationManager(Context context2) {
        this.context = context2.getApplicationContext();
    }

    /* access modifiers changed from: package-private */
    public void initFromCameraParameters(OpenCamera camera) {
        int cwRotationFromNaturalToDisplay;
        Camera.Parameters parameters = camera.getCamera().getParameters();
        Display display = ((WindowManager) this.context.getSystemService("window")).getDefaultDisplay();
        int displayRotation = display.getRotation();
        boolean isPreviewSizePortrait = true;
        if (displayRotation == 0) {
            cwRotationFromNaturalToDisplay = 0;
        } else if (displayRotation == 1) {
            cwRotationFromNaturalToDisplay = 90;
        } else if (displayRotation == 2) {
            cwRotationFromNaturalToDisplay = 180;
        } else if (displayRotation == 3) {
            cwRotationFromNaturalToDisplay = 270;
        } else if (displayRotation % 90 == 0) {
            cwRotationFromNaturalToDisplay = (displayRotation + 360) % 360;
        } else {
            throw new IllegalArgumentException("Bad rotation: " + displayRotation);
        }
        Log.i(TAG, "Display at: " + cwRotationFromNaturalToDisplay);
        int cwRotationFromNaturalToCamera = camera.getOrientation();
        Log.i(TAG, "Camera at: " + cwRotationFromNaturalToCamera);
        if (camera.getFacing() == CameraFacing.FRONT) {
            cwRotationFromNaturalToCamera = (360 - cwRotationFromNaturalToCamera) % 360;
            Log.i(TAG, "Front camera overriden to: " + cwRotationFromNaturalToCamera);
        }
        this.cwRotationFromDisplayToCamera = ((cwRotationFromNaturalToCamera + 360) - cwRotationFromNaturalToDisplay) % 360;
        Log.i(TAG, "Final display orientation: " + this.cwRotationFromDisplayToCamera);
        if (camera.getFacing() == CameraFacing.FRONT) {
            Log.i(TAG, "Compensating rotation for front camera");
            this.cwNeededRotation = (360 - this.cwRotationFromDisplayToCamera) % 360;
        } else {
            this.cwNeededRotation = this.cwRotationFromDisplayToCamera;
        }
        Log.i(TAG, "Clockwise rotation from display to camera: " + this.cwNeededRotation);
        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);
        this.screenResolution = theScreenResolution;
        Log.i(TAG, "Screen resolution in current orientation: " + this.screenResolution);
        this.cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, this.screenResolution);
        Log.i(TAG, "Camera resolution: " + this.cameraResolution);
        this.bestPreviewSize = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, this.screenResolution);
        Log.i(TAG, "Best available preview size: " + this.bestPreviewSize);
        boolean isScreenPortrait = this.screenResolution.x < this.screenResolution.y;
        if (this.bestPreviewSize.x >= this.bestPreviewSize.y) {
            isPreviewSizePortrait = false;
        }
        if (isScreenPortrait == isPreviewSizePortrait) {
            this.previewSizeOnScreen = this.bestPreviewSize;
        } else {
            this.previewSizeOnScreen = new Point(this.bestPreviewSize.y, this.bestPreviewSize.x);
        }
        Log.i(TAG, "Preview size on screen: " + this.previewSizeOnScreen);
    }

    /* access modifiers changed from: package-private */
    public void setDesiredCameraParameters(OpenCamera camera, boolean safeMode) {
        Camera theCamera = camera.getCamera();
        int rotation = this.context.getApplicationContext().getResources().getConfiguration().orientation;
        int deviceSpecificRotation = ((WindowManager) this.context.getSystemService("window")).getDefaultDisplay().getRotation();
        if (rotation == 1) {
            if (deviceSpecificRotation == 0 || deviceSpecificRotation == 1) {
                theCamera.setDisplayOrientation(90);
            } else {
                theCamera.setDisplayOrientation(270);
            }
        } else if (deviceSpecificRotation == 2 || deviceSpecificRotation == 3) {
            theCamera.setDisplayOrientation(180);
        }
        Camera.Parameters parameters = theCamera.getParameters();
        if (parameters == null) {
            Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }
        Log.i(TAG, "Initial camera parameters: " + parameters.flatten());
        if (safeMode) {
            Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        initializeTorch(parameters, prefs, safeMode);
        CameraConfigurationUtils.setFocus(parameters, prefs.getBoolean(PreferencesActivity.KEY_AUTO_FOCUS, true), prefs.getBoolean(PreferencesActivity.KEY_DISABLE_CONTINUOUS_FOCUS, true), safeMode);
        if (!safeMode) {
            if (prefs.getBoolean(PreferencesActivity.KEY_INVERT_SCAN, false)) {
                CameraConfigurationUtils.setInvertColor(parameters);
            }
            if (!prefs.getBoolean(PreferencesActivity.KEY_DISABLE_BARCODE_SCENE_MODE, true)) {
                CameraConfigurationUtils.setBarcodeSceneMode(parameters);
            }
            if (!prefs.getBoolean(PreferencesActivity.KEY_DISABLE_METERING, true)) {
                CameraConfigurationUtils.setVideoStabilization(parameters);
                CameraConfigurationUtils.setFocusArea(parameters);
                CameraConfigurationUtils.setMetering(parameters);
            }
        }
        parameters.setPreviewSize(this.bestPreviewSize.x, this.bestPreviewSize.y);
        theCamera.setParameters(parameters);
        theCamera.setDisplayOrientation(this.cwRotationFromDisplayToCamera);
        Camera.Size afterSize = theCamera.getParameters().getPreviewSize();
        if (afterSize == null) {
            return;
        }
        if (this.bestPreviewSize.x != afterSize.width || this.bestPreviewSize.y != afterSize.height) {
            Log.w(TAG, "Camera said it supported preview size " + this.bestPreviewSize.x + 'x' + this.bestPreviewSize.y + ", but after setting it, preview size is " + afterSize.width + 'x' + afterSize.height);
            this.bestPreviewSize.x = afterSize.width;
            this.bestPreviewSize.y = afterSize.height;
        }
    }

    /* access modifiers changed from: package-private */
    public Point getBestPreviewSize() {
        return this.bestPreviewSize;
    }

    /* access modifiers changed from: package-private */
    public Point getPreviewSizeOnScreen() {
        return this.previewSizeOnScreen;
    }

    /* access modifiers changed from: package-private */
    public Point getCameraResolution() {
        return this.cameraResolution;
    }

    /* access modifiers changed from: package-private */
    public Point getScreenResolution() {
        return this.screenResolution;
    }

    /* access modifiers changed from: package-private */
    public int getCWNeededRotation() {
        return this.cwNeededRotation;
    }

    /* access modifiers changed from: package-private */
    public boolean getTorchState(Camera camera) {
        Camera.Parameters parameters;
        String flashMode;
        if (camera == null || (parameters = camera.getParameters()) == null || (flashMode = parameters.getFlashMode()) == null) {
            return false;
        }
        if ("on".equals(flashMode) || "torch".equals(flashMode)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setTorch(Camera camera, boolean newSetting) {
        Camera.Parameters parameters = camera.getParameters();
        doSetTorch(parameters, newSetting, false);
        camera.setParameters(parameters);
    }

    private void initializeTorch(Camera.Parameters parameters, SharedPreferences prefs, boolean safeMode) {
        doSetTorch(parameters, FrontLightMode.readPref(prefs) == FrontLightMode.ON, safeMode);
    }

    private void doSetTorch(Camera.Parameters parameters, boolean newSetting, boolean safeMode) {
        CameraConfigurationUtils.setTorch(parameters, newSetting);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        if (!safeMode && !prefs.getBoolean(PreferencesActivity.KEY_DISABLE_EXPOSURE, true)) {
            CameraConfigurationUtils.setBestExposure(parameters, newSetting);
        }
    }
}
