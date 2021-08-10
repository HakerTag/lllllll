package com.google.zxing.client.android.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public final class CameraConfigurationUtils {
    private static final int AREA_PER_1000 = 400;
    private static final double MAX_ASPECT_DISTORTION = 0.15d;
    private static final float MAX_EXPOSURE_COMPENSATION = 1.5f;
    private static final int MAX_FPS = 20;
    private static final float MIN_EXPOSURE_COMPENSATION = 0.0f;
    private static final int MIN_FPS = 10;
    private static final int MIN_PREVIEW_PIXELS = 153600;
    private static final Pattern SEMICOLON = Pattern.compile(";");
    private static final String TAG = "CameraConfiguration";

    private CameraConfigurationUtils() {
    }

    public static void setFocus(Camera.Parameters parameters, boolean autoFocus, boolean disableContinuous, boolean safeMode) {
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        String focusMode = null;
        if (autoFocus) {
            if (safeMode || disableContinuous) {
                focusMode = findSettableValue("focus mode", supportedFocusModes, "auto");
            } else {
                focusMode = findSettableValue("focus mode", supportedFocusModes, "continuous-picture", "continuous-video", "auto");
            }
        }
        if (!safeMode && focusMode == null) {
            focusMode = findSettableValue("focus mode", supportedFocusModes, "macro", "edof");
        }
        if (focusMode == null) {
            return;
        }
        if (focusMode.equals(parameters.getFocusMode())) {
            Log.i(TAG, "Focus mode already set to " + focusMode);
            return;
        }
        parameters.setFocusMode(focusMode);
    }

    public static void setTorch(Camera.Parameters parameters, boolean on) {
        String flashMode;
        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (on) {
            flashMode = findSettableValue("flash mode", supportedFlashModes, "torch", "on");
        } else {
            flashMode = findSettableValue("flash mode", supportedFlashModes, "off");
        }
        if (flashMode == null) {
            return;
        }
        if (flashMode.equals(parameters.getFlashMode())) {
            Log.i(TAG, "Flash mode already set to " + flashMode);
            return;
        }
        Log.i(TAG, "Setting flash mode to " + flashMode);
        parameters.setFlashMode(flashMode);
    }

    public static void setBestExposure(Camera.Parameters parameters, boolean lightOn) {
        int minExposure = parameters.getMinExposureCompensation();
        int maxExposure = parameters.getMaxExposureCompensation();
        float step = parameters.getExposureCompensationStep();
        if (!(minExposure == 0 && maxExposure == 0)) {
            float targetCompensation = 0.0f;
            if (step > 0.0f) {
                if (!lightOn) {
                    targetCompensation = MAX_EXPOSURE_COMPENSATION;
                }
                int compensationSteps = Math.round(targetCompensation / step);
                float actualCompensation = ((float) compensationSteps) * step;
                int compensationSteps2 = Math.max(Math.min(compensationSteps, maxExposure), minExposure);
                if (parameters.getExposureCompensation() == compensationSteps2) {
                    Log.i(TAG, "Exposure compensation already set to " + compensationSteps2 + " / " + actualCompensation);
                    return;
                }
                Log.i(TAG, "Setting exposure compensation to " + compensationSteps2 + " / " + actualCompensation);
                parameters.setExposureCompensation(compensationSteps2);
                return;
            }
        }
        Log.i(TAG, "Camera does not support exposure compensation");
    }

    public static void setBestPreviewFPS(Camera.Parameters parameters) {
        setBestPreviewFPS(parameters, 10, 20);
    }

    public static void setBestPreviewFPS(Camera.Parameters parameters, int minFPS, int maxFPS) {
        List<int[]> supportedPreviewFpsRanges = parameters.getSupportedPreviewFpsRange();
        Log.i(TAG, "Supported FPS ranges: " + toString((Collection<int[]>) supportedPreviewFpsRanges));
        if (supportedPreviewFpsRanges != null && !supportedPreviewFpsRanges.isEmpty()) {
            int[] suitableFPSRange = null;
            Iterator<int[]> it = supportedPreviewFpsRanges.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                int[] fpsRange = it.next();
                int thisMin = fpsRange[0];
                int thisMax = fpsRange[1];
                if (thisMin >= minFPS * 1000 && thisMax <= maxFPS * 1000) {
                    suitableFPSRange = fpsRange;
                    break;
                }
            }
            if (suitableFPSRange == null) {
                Log.i(TAG, "No suitable FPS range?");
                return;
            }
            int[] currentFpsRange = new int[2];
            parameters.getPreviewFpsRange(currentFpsRange);
            if (Arrays.equals(currentFpsRange, suitableFPSRange)) {
                Log.i(TAG, "FPS range already set to " + Arrays.toString(suitableFPSRange));
                return;
            }
            Log.i(TAG, "Setting FPS range to " + Arrays.toString(suitableFPSRange));
            parameters.setPreviewFpsRange(suitableFPSRange[0], suitableFPSRange[1]);
        }
    }

    public static void setFocusArea(Camera.Parameters parameters) {
        if (parameters.getMaxNumFocusAreas() > 0) {
            Log.i(TAG, "Old focus areas: " + toString((Iterable<Camera.Area>) parameters.getFocusAreas()));
            List<Camera.Area> middleArea = buildMiddleArea(AREA_PER_1000);
            Log.i(TAG, "Setting focus area to : " + toString((Iterable<Camera.Area>) middleArea));
            parameters.setFocusAreas(middleArea);
            return;
        }
        Log.i(TAG, "Device does not support focus areas");
    }

    public static void setMetering(Camera.Parameters parameters) {
        if (parameters.getMaxNumMeteringAreas() > 0) {
            Log.i(TAG, "Old metering areas: " + parameters.getMeteringAreas());
            List<Camera.Area> middleArea = buildMiddleArea(AREA_PER_1000);
            Log.i(TAG, "Setting metering area to : " + toString((Iterable<Camera.Area>) middleArea));
            parameters.setMeteringAreas(middleArea);
            return;
        }
        Log.i(TAG, "Device does not support metering areas");
    }

    private static List<Camera.Area> buildMiddleArea(int areaPer1000) {
        return Collections.singletonList(new Camera.Area(new Rect(-areaPer1000, -areaPer1000, areaPer1000, areaPer1000), 1));
    }

    public static void setVideoStabilization(Camera.Parameters parameters) {
        if (!parameters.isVideoStabilizationSupported()) {
            Log.i(TAG, "This device does not support video stabilization");
        } else if (parameters.getVideoStabilization()) {
            Log.i(TAG, "Video stabilization already enabled");
        } else {
            Log.i(TAG, "Enabling video stabilization...");
            parameters.setVideoStabilization(true);
        }
    }

    public static void setBarcodeSceneMode(Camera.Parameters parameters) {
        if ("barcode".equals(parameters.getSceneMode())) {
            Log.i(TAG, "Barcode scene mode already set");
            return;
        }
        String sceneMode = findSettableValue("scene mode", parameters.getSupportedSceneModes(), "barcode");
        if (sceneMode != null) {
            parameters.setSceneMode(sceneMode);
        }
    }

    public static void setZoom(Camera.Parameters parameters, double targetZoomRatio) {
        if (parameters.isZoomSupported()) {
            Integer zoom = indexOfClosestZoom(parameters, targetZoomRatio);
            if (zoom != null) {
                if (parameters.getZoom() == zoom.intValue()) {
                    Log.i(TAG, "Zoom is already set to " + zoom);
                    return;
                }
                Log.i(TAG, "Setting zoom to " + zoom);
                parameters.setZoom(zoom.intValue());
                return;
            }
            return;
        }
        Log.i(TAG, "Zoom is not supported");
    }

    private static Integer indexOfClosestZoom(Camera.Parameters parameters, double targetZoomRatio) {
        List<Integer> ratios = parameters.getZoomRatios();
        Log.i(TAG, "Zoom ratios: " + ratios);
        int maxZoom = parameters.getMaxZoom();
        if (ratios == null || ratios.isEmpty() || ratios.size() != maxZoom + 1) {
            Log.w(TAG, "Invalid zoom ratios!");
            return null;
        }
        double target100 = targetZoomRatio * 100.0d;
        double smallestDiff = Double.POSITIVE_INFINITY;
        int closestIndex = 0;
        for (int i = 0; i < ratios.size(); i++) {
            double diff = Math.abs(((double) ratios.get(i).intValue()) - target100);
            if (diff < smallestDiff) {
                smallestDiff = diff;
                closestIndex = i;
            }
        }
        Log.i(TAG, "Chose zoom ratio of " + (((double) ratios.get(closestIndex).intValue()) / 100.0d));
        return Integer.valueOf(closestIndex);
    }

    public static void setInvertColor(Camera.Parameters parameters) {
        if ("negative".equals(parameters.getColorEffect())) {
            Log.i(TAG, "Negative effect already set");
            return;
        }
        String colorMode = findSettableValue("color effect", parameters.getSupportedColorEffects(), "negative");
        if (colorMode != null) {
            parameters.setColorEffect(colorMode);
        }
    }

    public static Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {
        Point point = screenResolution;
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPreviewSize();
            if (defaultSize != null) {
                return new Point(defaultSize.width, defaultSize.height);
            }
            throw new IllegalStateException("Parameters contained no preview size!");
        }
        List<Camera.Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);
        Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
            /* class com.google.zxing.client.android.camera.CameraConfigurationUtils.AnonymousClass1 */

            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });
        if (Log.isLoggable(TAG, 4)) {
            StringBuilder previewSizesString = new StringBuilder();
            for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
                previewSizesString.append(supportedPreviewSize.width);
                previewSizesString.append('x');
                previewSizesString.append(supportedPreviewSize.height);
                previewSizesString.append(' ');
            }
            Log.i(TAG, "Supported preview sizes: " + ((Object) previewSizesString));
        }
        double screenAspectRatio = ((double) point.x) / ((double) point.y);
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        while (true) {
            boolean isOrientationMatch = false;
            if (it.hasNext()) {
                Camera.Size supportedPreviewSize2 = it.next();
                int realWidth = supportedPreviewSize2.width;
                int realHeight = supportedPreviewSize2.height;
                if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
                    it.remove();
                } else {
                    boolean isCandidatePortrait = realWidth < realHeight;
                    boolean isScreenPortrait = point.x < point.y;
                    if ((isCandidatePortrait && isScreenPortrait) || (!isCandidatePortrait && !isScreenPortrait)) {
                        isOrientationMatch = true;
                    }
                    int maybeFlippedWidth = isOrientationMatch ? realWidth : realHeight;
                    int maybeFlippedHeight = isOrientationMatch ? realHeight : realWidth;
                    if (Math.abs((((double) maybeFlippedWidth) / ((double) maybeFlippedHeight)) - screenAspectRatio) > MAX_ASPECT_DISTORTION) {
                        it.remove();
                    } else if (maybeFlippedWidth == point.x && maybeFlippedHeight == point.y) {
                        Point exactPoint = new Point(realWidth, realHeight);
                        Log.i(TAG, "Found preview size exactly matching screen size: " + exactPoint);
                        return exactPoint;
                    } else {
                        point = screenResolution;
                        rawSupportedSizes = rawSupportedSizes;
                    }
                }
            } else if (!supportedPreviewSizes.isEmpty()) {
                Camera.Size largestPreview = supportedPreviewSizes.get(0);
                Point largestSize = new Point(largestPreview.width, largestPreview.height);
                Log.i(TAG, "Using largest suitable preview size: " + largestSize);
                return largestSize;
            } else {
                Camera.Size defaultPreview = parameters.getPreviewSize();
                if (defaultPreview != null) {
                    Point defaultSize2 = new Point(defaultPreview.width, defaultPreview.height);
                    Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize2);
                    return defaultSize2;
                }
                throw new IllegalStateException("Parameters contained no preview size!");
            }
        }
    }

    private static String findSettableValue(String name, Collection<String> supportedValues, String... desiredValues) {
        Log.i(TAG, "Requesting " + name + " value from among: " + Arrays.toString(desiredValues));
        Log.i(TAG, "Supported " + name + " values: " + supportedValues);
        if (supportedValues != null) {
            for (String desiredValue : desiredValues) {
                if (supportedValues.contains(desiredValue)) {
                    Log.i(TAG, "Can set " + name + " to: " + desiredValue);
                    return desiredValue;
                }
            }
        }
        Log.i(TAG, "No supported values match");
        return null;
    }

    private static String toString(Collection<int[]> arrays) {
        if (arrays == null || arrays.isEmpty()) {
            return "[]";
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        Iterator<int[]> it = arrays.iterator();
        while (it.hasNext()) {
            buffer.append(Arrays.toString(it.next()));
            if (it.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append(']');
        return buffer.toString();
    }

    private static String toString(Iterable<Camera.Area> areas) {
        if (areas == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (Camera.Area area : areas) {
            result.append(area.rect);
            result.append(':');
            result.append(area.weight);
            result.append(' ');
        }
        return result.toString();
    }

    public static String collectStats(Camera.Parameters parameters) {
        return collectStats(parameters.flatten());
    }

    public static String collectStats(CharSequence flattenedParams) {
        StringBuilder result = new StringBuilder(1000);
        result.append("BOARD=");
        result.append(Build.BOARD);
        result.append('\n');
        result.append("BRAND=");
        result.append(Build.BRAND);
        result.append('\n');
        result.append("CPU_ABI=");
        result.append(Build.CPU_ABI);
        result.append('\n');
        result.append("DEVICE=");
        result.append(Build.DEVICE);
        result.append('\n');
        result.append("DISPLAY=");
        result.append(Build.DISPLAY);
        result.append('\n');
        result.append("FINGERPRINT=");
        result.append(Build.FINGERPRINT);
        result.append('\n');
        result.append("HOST=");
        result.append(Build.HOST);
        result.append('\n');
        result.append("ID=");
        result.append(Build.ID);
        result.append('\n');
        result.append("MANUFACTURER=");
        result.append(Build.MANUFACTURER);
        result.append('\n');
        result.append("MODEL=");
        result.append(Build.MODEL);
        result.append('\n');
        result.append("PRODUCT=");
        result.append(Build.PRODUCT);
        result.append('\n');
        result.append("TAGS=");
        result.append(Build.TAGS);
        result.append('\n');
        result.append("TIME=");
        result.append(Build.TIME);
        result.append('\n');
        result.append("TYPE=");
        result.append(Build.TYPE);
        result.append('\n');
        result.append("USER=");
        result.append(Build.USER);
        result.append('\n');
        result.append("VERSION.CODENAME=");
        result.append(Build.VERSION.CODENAME);
        result.append('\n');
        result.append("VERSION.INCREMENTAL=");
        result.append(Build.VERSION.INCREMENTAL);
        result.append('\n');
        result.append("VERSION.RELEASE=");
        result.append(Build.VERSION.RELEASE);
        result.append('\n');
        result.append("VERSION.SDK_INT=");
        result.append(Build.VERSION.SDK_INT);
        result.append('\n');
        if (flattenedParams != null) {
            String[] params = SEMICOLON.split(flattenedParams);
            Arrays.sort(params);
            for (String param : params) {
                result.append(param);
                result.append('\n');
            }
        }
        return result.toString();
    }
}
