package com.google.zxing.common.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;

@Deprecated
public final class MonochromeRectangleDetector {
    private static final int MAX_MODULES = 32;
    private final BitMatrix image;

    public MonochromeRectangleDetector(BitMatrix image2) {
        this.image = image2;
    }

    public ResultPoint[] detect() throws NotFoundException {
        int height = this.image.getHeight();
        int width = this.image.getWidth();
        int halfHeight = height / 2;
        int halfWidth = width / 2;
        int deltaY = Math.max(1, height / 256);
        int deltaX = Math.max(1, width / 256);
        int top = ((int) findCornerFromCenter(halfWidth, 0, 0, width, halfHeight, -deltaY, 0, height, halfWidth / 2).getY()) - 1;
        ResultPoint pointB = findCornerFromCenter(halfWidth, -deltaX, 0, width, halfHeight, 0, top, height, halfHeight / 2);
        int left = ((int) pointB.getX()) - 1;
        ResultPoint pointC = findCornerFromCenter(halfWidth, deltaX, left, width, halfHeight, 0, top, height, halfHeight / 2);
        int right = ((int) pointC.getX()) + 1;
        ResultPoint pointD = findCornerFromCenter(halfWidth, 0, left, right, halfHeight, deltaY, top, height, halfWidth / 2);
        return new ResultPoint[]{findCornerFromCenter(halfWidth, 0, left, right, halfHeight, -deltaY, top, ((int) pointD.getY()) + 1, halfWidth / 4), pointB, pointC, pointD};
    }

    /* JADX INFO: Multiple debug info for r6v0 int: [D('lastY' int), D('lastX' int)] */
    private ResultPoint findCornerFromCenter(int centerX, int deltaX, int left, int right, int centerY, int deltaY, int top, int bottom, int maxWhiteRun) throws NotFoundException {
        int[] range;
        int[] lastRange = null;
        int y = centerY;
        int x = centerX;
        while (true) {
            if (y < bottom) {
                if (y >= top) {
                    if (x < right) {
                        if (x < left) {
                            break;
                        }
                        if (deltaX == 0) {
                            range = blackWhiteRange(y, maxWhiteRun, left, right, true);
                        } else {
                            range = blackWhiteRange(x, maxWhiteRun, top, bottom, false);
                        }
                        if (range != null) {
                            lastRange = range;
                            y += deltaY;
                            x += deltaX;
                        } else if (lastRange != null) {
                            char c = 1;
                            if (deltaX == 0) {
                                int lastY = y - deltaY;
                                if (lastRange[0] >= centerX) {
                                    return new ResultPoint((float) lastRange[1], (float) lastY);
                                }
                                if (lastRange[1] <= centerX) {
                                    return new ResultPoint((float) lastRange[0], (float) lastY);
                                }
                                if (deltaY > 0) {
                                    c = 0;
                                }
                                return new ResultPoint((float) lastRange[c], (float) lastY);
                            }
                            int lastX = x - deltaX;
                            if (lastRange[0] >= centerY) {
                                return new ResultPoint((float) lastX, (float) lastRange[1]);
                            }
                            if (lastRange[1] <= centerY) {
                                return new ResultPoint((float) lastX, (float) lastRange[0]);
                            }
                            float f = (float) lastX;
                            if (deltaX < 0) {
                                c = 0;
                            }
                            return new ResultPoint(f, (float) lastRange[c]);
                        } else {
                            throw NotFoundException.getNotFoundInstance();
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private int[] blackWhiteRange(int fixedDimension, int maxWhiteRun, int minDim, int maxDim, boolean horizontal) {
        int center = (minDim + maxDim) / 2;
        int start = center;
        while (true) {
            if (start < minDim) {
                break;
            }
            BitMatrix bitMatrix = this.image;
            if (!horizontal ? !bitMatrix.get(fixedDimension, start) : !bitMatrix.get(start, fixedDimension)) {
                while (true) {
                    start--;
                    if (start < minDim) {
                        break;
                    }
                    BitMatrix bitMatrix2 = this.image;
                    if (horizontal) {
                        if (bitMatrix2.get(start, fixedDimension)) {
                            break;
                        }
                    } else if (bitMatrix2.get(fixedDimension, start)) {
                        break;
                    }
                }
                int whiteRunSize = start - start;
                if (start < minDim || whiteRunSize > maxWhiteRun) {
                    start = start;
                }
            } else {
                start--;
            }
        }
        start = start;
        int start2 = start + 1;
        int end = center;
        while (true) {
            if (end >= maxDim) {
                break;
            }
            BitMatrix bitMatrix3 = this.image;
            if (!horizontal ? !bitMatrix3.get(fixedDimension, end) : !bitMatrix3.get(end, fixedDimension)) {
                while (true) {
                    end++;
                    if (end >= maxDim) {
                        break;
                    }
                    BitMatrix bitMatrix4 = this.image;
                    if (horizontal) {
                        if (bitMatrix4.get(end, fixedDimension)) {
                            break;
                        }
                    } else if (bitMatrix4.get(fixedDimension, end)) {
                        break;
                    }
                }
                int whiteRunSize2 = end - end;
                if (end >= maxDim || whiteRunSize2 > maxWhiteRun) {
                    end = end;
                }
            } else {
                end++;
            }
        }
        int end2 = end - 1;
        if (end2 <= start2) {
            return null;
        }
        return new int[]{start2, end2};
    }
}
