package com.google.zxing.client.android.camera.open;

import android.hardware.Camera;
import android.util.Log;

public final class OpenCameraInterface {
    public static final int NO_REQUESTED_CAMERA = -1;
    private static final String TAG = OpenCameraInterface.class.getName();

    private OpenCameraInterface() {
    }

    public static OpenCamera open(int cameraId) {
        int index;
        Camera camera;
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            Log.w(TAG, "No cameras!");
            return null;
        }
        boolean explicitRequest = true;
        if (numCameras == 1) {
            cameraId = 0;
        }
        if (cameraId < 0) {
            explicitRequest = false;
        }
        Camera.CameraInfo selectedCameraInfo = null;
        if (!explicitRequest) {
            index = 0;
            while (true) {
                if (index >= numCameras) {
                    break;
                }
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(index, cameraInfo);
                if (CameraFacing.values()[cameraInfo.facing] == CameraFacing.BACK) {
                    selectedCameraInfo = cameraInfo;
                    break;
                }
                index++;
            }
        } else {
            index = cameraId;
            selectedCameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, selectedCameraInfo);
        }
        if (index < numCameras) {
            String str = TAG;
            Log.i(str, "Opening camera #" + index);
            camera = Camera.open(index);
        } else if (explicitRequest) {
            String str2 = TAG;
            Log.w(str2, "Requested camera does not exist: " + cameraId);
            camera = null;
        } else {
            String str3 = TAG;
            Log.i(str3, "No camera facing " + CameraFacing.BACK + "; returning camera #0");
            Camera camera2 = Camera.open(0);
            selectedCameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(0, selectedCameraInfo);
            camera = camera2;
        }
        if (camera == null) {
            return null;
        }
        return new OpenCamera(index, camera, CameraFacing.values()[selectedCameraInfo.facing], selectedCameraInfo.orientation);
    }
}
