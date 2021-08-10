package com.google.zxing.pdf417.detector;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Detector {
    private static final int BARCODE_MIN_HEIGHT = 10;
    private static final int[] INDEXES_START_PATTERN = {0, 4, 1, 5};
    private static final int[] INDEXES_STOP_PATTERN = {6, 2, 7, 3};
    private static final float MAX_AVG_VARIANCE = 0.42f;
    private static final float MAX_INDIVIDUAL_VARIANCE = 0.8f;
    private static final int MAX_PATTERN_DRIFT = 5;
    private static final int MAX_PIXEL_DRIFT = 3;
    private static final int ROW_STEP = 5;
    private static final int SKIPPED_ROW_COUNT_MAX = 25;
    private static final int[] START_PATTERN = {8, 1, 1, 1, 1, 1, 1, 3};
    private static final int[] STOP_PATTERN = {7, 1, 1, 3, 1, 1, 1, 2, 1};

    private Detector() {
    }

    public static PDF417DetectorResult detect(BinaryBitmap image, Map<DecodeHintType, ?> map, boolean multiple) throws NotFoundException {
        BitMatrix bitMatrix = image.getBlackMatrix();
        List<ResultPoint[]> barcodeCoordinates = detect(multiple, bitMatrix);
        if (barcodeCoordinates.isEmpty()) {
            bitMatrix = bitMatrix.clone();
            bitMatrix.rotate180();
            barcodeCoordinates = detect(multiple, bitMatrix);
        }
        return new PDF417DetectorResult(bitMatrix, barcodeCoordinates);
    }

    private static List<ResultPoint[]> detect(boolean multiple, BitMatrix bitMatrix) {
        List<ResultPoint[]> barcodeCoordinates = new ArrayList<>();
        int row = 0;
        int column = 0;
        boolean foundBarcodeInRow = false;
        while (row < bitMatrix.getHeight()) {
            ResultPoint[] vertices = findVertices(bitMatrix, row, column);
            if (vertices[0] != null || vertices[3] != null) {
                foundBarcodeInRow = true;
                barcodeCoordinates.add(vertices);
                if (!multiple) {
                    break;
                } else if (vertices[2] != null) {
                    column = (int) vertices[2].getX();
                    row = (int) vertices[2].getY();
                } else {
                    column = (int) vertices[4].getX();
                    row = (int) vertices[4].getY();
                }
            } else if (!foundBarcodeInRow) {
                break;
            } else {
                foundBarcodeInRow = false;
                column = 0;
                for (ResultPoint[] barcodeCoordinate : barcodeCoordinates) {
                    if (barcodeCoordinate[1] != null) {
                        row = (int) Math.max((float) row, barcodeCoordinate[1].getY());
                    }
                    if (barcodeCoordinate[3] != null) {
                        row = Math.max(row, (int) barcodeCoordinate[3].getY());
                    }
                }
                row += 5;
            }
        }
        return barcodeCoordinates;
    }

    private static ResultPoint[] findVertices(BitMatrix matrix, int startRow, int startColumn) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        ResultPoint[] result = new ResultPoint[8];
        copyToResult(result, findRowsWithPattern(matrix, height, width, startRow, startColumn, START_PATTERN), INDEXES_START_PATTERN);
        if (result[4] != null) {
            startColumn = (int) result[4].getX();
            startRow = (int) result[4].getY();
        }
        copyToResult(result, findRowsWithPattern(matrix, height, width, startRow, startColumn, STOP_PATTERN), INDEXES_STOP_PATTERN);
        return result;
    }

    private static void copyToResult(ResultPoint[] result, ResultPoint[] tmpResult, int[] destinationIndexes) {
        for (int i = 0; i < destinationIndexes.length; i++) {
            result[destinationIndexes[i]] = tmpResult[i];
        }
    }

    private static ResultPoint[] findRowsWithPattern(BitMatrix matrix, int height, int width, int startRow, int startColumn, int[] pattern) {
        int skippedRowCount;
        int stopRow;
        int startRow2;
        ResultPoint[] result = new ResultPoint[4];
        boolean found = false;
        int[] counters = new int[pattern.length];
        int startRow3 = startRow;
        while (true) {
            if (startRow3 >= height) {
                break;
            }
            int[] loc = findGuardPattern(matrix, startColumn, startRow3, width, false, pattern, counters);
            if (loc != null) {
                int[] loc2 = loc;
                int startRow4 = startRow3;
                while (true) {
                    if (startRow4 <= 0) {
                        startRow2 = startRow4;
                        break;
                    }
                    int startRow5 = startRow4 - 1;
                    int[] previousRowLoc = findGuardPattern(matrix, startColumn, startRow5, width, false, pattern, counters);
                    if (previousRowLoc == null) {
                        startRow2 = startRow5 + 1;
                        break;
                    }
                    loc2 = previousRowLoc;
                    startRow4 = startRow5;
                }
                result[0] = new ResultPoint((float) loc2[0], (float) startRow2);
                result[1] = new ResultPoint((float) loc2[1], (float) startRow2);
                found = true;
                startRow3 = startRow2;
            } else {
                startRow3 += 5;
            }
        }
        int stopRow2 = startRow3 + 1;
        if (found) {
            int stopRow3 = stopRow2;
            int skippedRowCount2 = 0;
            int[] previousRowLoc2 = {(int) result[0].getX(), (int) result[1].getX()};
            while (true) {
                if (stopRow3 >= height) {
                    skippedRowCount = skippedRowCount2;
                    stopRow = stopRow3;
                    break;
                }
                skippedRowCount = skippedRowCount2;
                stopRow = stopRow3;
                int[] loc3 = findGuardPattern(matrix, previousRowLoc2[0], stopRow3, width, false, pattern, counters);
                if (loc3 != null && Math.abs(previousRowLoc2[0] - loc3[0]) < 5 && Math.abs(previousRowLoc2[1] - loc3[1]) < 5) {
                    previousRowLoc2 = loc3;
                    skippedRowCount2 = 0;
                } else if (skippedRowCount > 25) {
                    break;
                } else {
                    skippedRowCount2 = skippedRowCount + 1;
                }
                stopRow3 = stopRow + 1;
            }
            stopRow2 = stopRow - (skippedRowCount + 1);
            result[2] = new ResultPoint((float) previousRowLoc2[0], (float) stopRow2);
            result[3] = new ResultPoint((float) previousRowLoc2[1], (float) stopRow2);
        }
        if (stopRow2 - startRow3 < 10) {
            for (int i = 0; i < result.length; i++) {
                result[i] = null;
            }
        }
        return result;
    }

    private static int[] findGuardPattern(BitMatrix matrix, int column, int row, int width, boolean whiteFirst, int[] pattern, int[] counters) {
        Arrays.fill(counters, 0, counters.length, 0);
        int patternStart = column;
        int pixelDrift = 0;
        while (true) {
            if (!matrix.get(patternStart, row) || patternStart <= 0) {
                break;
            }
            int pixelDrift2 = pixelDrift + 1;
            if (pixelDrift >= 3) {
                break;
            }
            patternStart--;
            pixelDrift = pixelDrift2;
        }
        int x = patternStart;
        int counterPosition = 0;
        int patternLength = pattern.length;
        boolean isWhite = whiteFirst;
        while (x < width) {
            if (matrix.get(x, row) ^ isWhite) {
                counters[counterPosition] = counters[counterPosition] + 1;
            } else {
                if (counterPosition != patternLength - 1) {
                    counterPosition++;
                } else if (patternMatchVariance(counters, pattern, MAX_INDIVIDUAL_VARIANCE) < MAX_AVG_VARIANCE) {
                    return new int[]{patternStart, x};
                } else {
                    patternStart += counters[0] + counters[1];
                    System.arraycopy(counters, 2, counters, 0, patternLength - 2);
                    counters[patternLength - 2] = 0;
                    counters[patternLength - 1] = 0;
                    counterPosition--;
                }
                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
            x++;
        }
        if (counterPosition != patternLength - 1 || patternMatchVariance(counters, pattern, MAX_INDIVIDUAL_VARIANCE) >= MAX_AVG_VARIANCE) {
            return null;
        }
        return new int[]{patternStart, x - 1};
    }

    private static float patternMatchVariance(int[] counters, int[] pattern, float maxIndividualVariance) {
        int numCounters = counters.length;
        int total = 0;
        int patternLength = 0;
        for (int i = 0; i < numCounters; i++) {
            total += counters[i];
            patternLength += pattern[i];
        }
        if (total < patternLength) {
            return Float.POSITIVE_INFINITY;
        }
        float unitBarWidth = ((float) total) / ((float) patternLength);
        float maxIndividualVariance2 = maxIndividualVariance * unitBarWidth;
        float totalVariance = 0.0f;
        for (int x = 0; x < numCounters; x++) {
            int counter = counters[x];
            float scaledPattern = ((float) pattern[x]) * unitBarWidth;
            float variance = ((float) counter) > scaledPattern ? ((float) counter) - scaledPattern : scaledPattern - ((float) counter);
            if (variance > maxIndividualVariance2) {
                return Float.POSITIVE_INFINITY;
            }
            totalVariance += variance;
        }
        return totalVariance / ((float) total);
    }
}
