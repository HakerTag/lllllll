package com.google.zxing.client.android.camera.open;

import android.hardware.Camera;

public final class OpenCamera {
    private final Camera camera;
    private final CameraFacing facing;
    private final int index;
    private final int orientation;

    public OpenCamera(int index2, Camera camera2, CameraFacing facing2, int orientation2) {
        this.index = index2;
        this.camera = camera2;
        this.facing = facing2;
        this.orientation = orientation2;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public CameraFacing getFacing() {
        return this.facing;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public String toString() {
        return "Camera #" + this.index + " : " + this.facing + ',' + this.orientation;
    }
}
