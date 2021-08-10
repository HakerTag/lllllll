package com.google.zxing.multi.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.detector.FinderPattern;
import com.google.zxing.qrcode.detector.FinderPatternFinder;
import com.google.zxing.qrcode.detector.FinderPatternInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/* access modifiers changed from: package-private */
public final class MultiFinderPatternFinder extends FinderPatternFinder {
    private static final float DIFF_MODSIZE_CUTOFF = 0.5f;
    private static final float DIFF_MODSIZE_CUTOFF_PERCENT = 0.05f;
    private static final FinderPatternInfo[] EMPTY_RESULT_ARRAY = new FinderPatternInfo[0];
    private static final float MAX_MODULE_COUNT_PER_EDGE = 180.0f;
    private static final float MIN_MODULE_COUNT_PER_EDGE = 9.0f;

    /* access modifiers changed from: private */
    public static final class ModuleSizeComparator implements Comparator<FinderPattern>, Serializable {
        private ModuleSizeComparator() {
        }

        public int compare(FinderPattern center1, FinderPattern center2) {
            float value = center2.getEstimatedModuleSize() - center1.getEstimatedModuleSize();
            if (((double) value) < 0.0d) {
                return -1;
            }
            return ((double) value) > 0.0d ? 1 : 0;
        }
    }

    MultiFinderPatternFinder(BitMatrix image) {
        super(image);
    }

    MultiFinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
        super(image, resultPointCallback);
    }

    private FinderPattern[][] selectMutipleBestPatterns() throws NotFoundException {
        int size;
        List<FinderPattern> possibleCenters;
        List<FinderPattern> possibleCenters2 = getPossibleCenters();
        int size2 = possibleCenters2.size();
        int i = 3;
        if (size2 >= 3) {
            char c = 0;
            if (size2 == 3) {
                return new FinderPattern[][]{new FinderPattern[]{possibleCenters2.get(0), possibleCenters2.get(1), possibleCenters2.get(2)}};
            }
            Collections.sort(possibleCenters2, new ModuleSizeComparator());
            List<FinderPattern[]> results = new ArrayList<>();
            int i1 = 0;
            while (i1 < size2 - 2) {
                FinderPattern p1 = possibleCenters2.get(i1);
                if (p1 != null) {
                    int i2 = i1 + 1;
                    while (i2 < size2 - 1) {
                        FinderPattern p2 = possibleCenters2.get(i2);
                        if (p2 != null) {
                            float vModSize12 = (p1.getEstimatedModuleSize() - p2.getEstimatedModuleSize()) / Math.min(p1.getEstimatedModuleSize(), p2.getEstimatedModuleSize());
                            float vModSize12A = Math.abs(p1.getEstimatedModuleSize() - p2.getEstimatedModuleSize());
                            float f = DIFF_MODSIZE_CUTOFF_PERCENT;
                            float f2 = DIFF_MODSIZE_CUTOFF;
                            if (vModSize12A > DIFF_MODSIZE_CUTOFF && vModSize12 >= DIFF_MODSIZE_CUTOFF_PERCENT) {
                                break;
                            }
                            int i3 = i2 + 1;
                            while (i3 < size2) {
                                FinderPattern p3 = possibleCenters2.get(i3);
                                if (p3 != null) {
                                    float vModSize23 = (p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) / Math.min(p2.getEstimatedModuleSize(), p3.getEstimatedModuleSize());
                                    if (Math.abs(p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) > f2 && vModSize23 >= f) {
                                        break;
                                    }
                                    FinderPattern[] test = new FinderPattern[i];
                                    test[c] = p1;
                                    test[1] = p2;
                                    test[2] = p3;
                                    ResultPoint.orderBestPatterns(test);
                                    FinderPatternInfo info = new FinderPatternInfo(test);
                                    float dA = ResultPoint.distance(info.getTopLeft(), info.getBottomLeft());
                                    float dC = ResultPoint.distance(info.getTopRight(), info.getBottomLeft());
                                    possibleCenters = possibleCenters2;
                                    float dB = ResultPoint.distance(info.getTopLeft(), info.getTopRight());
                                    float estimatedModuleCount = (dA + dB) / (p1.getEstimatedModuleSize() * 2.0f);
                                    if (estimatedModuleCount > MAX_MODULE_COUNT_PER_EDGE) {
                                        size = size2;
                                    } else if (estimatedModuleCount < MIN_MODULE_COUNT_PER_EDGE) {
                                        size = size2;
                                    } else if (Math.abs((dA - dB) / Math.min(dA, dB)) >= 0.1f) {
                                        size = size2;
                                    } else {
                                        size = size2;
                                        float dCpy = (float) Math.sqrt((double) ((dA * dA) + (dB * dB)));
                                        if (Math.abs((dC - dCpy) / Math.min(dC, dCpy)) < 0.1f) {
                                            results.add(test);
                                        }
                                    }
                                } else {
                                    possibleCenters = possibleCenters2;
                                    size = size2;
                                }
                                i3++;
                                possibleCenters2 = possibleCenters;
                                size2 = size;
                                i = 3;
                                c = 0;
                                f = DIFF_MODSIZE_CUTOFF_PERCENT;
                                f2 = DIFF_MODSIZE_CUTOFF;
                            }
                        }
                        i2++;
                        possibleCenters2 = possibleCenters2;
                        size2 = size2;
                        i = 3;
                        c = 0;
                    }
                }
                i1++;
                possibleCenters2 = possibleCenters2;
                size2 = size2;
                i = 3;
                c = 0;
            }
            if (!results.isEmpty()) {
                return (FinderPattern[][]) results.toArray(new FinderPattern[results.size()][]);
            }
            throw NotFoundException.getNotFoundInstance();
        }
        throw NotFoundException.getNotFoundInstance();
    }

    public FinderPatternInfo[] findMulti(Map<DecodeHintType, ?> hints) throws NotFoundException {
        boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        boolean pureBarcode = hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE);
        BitMatrix image = getImage();
        int maxI = image.getHeight();
        int maxJ = image.getWidth();
        int iSkip = (int) ((((float) maxI) / 228.0f) * 3.0f);
        char c = 3;
        if (iSkip < 3 || tryHarder) {
            iSkip = 3;
        }
        int[] stateCount = new int[5];
        int i = iSkip - 1;
        while (i < maxI) {
            stateCount[0] = 0;
            stateCount[1] = 0;
            stateCount[2] = 0;
            stateCount[c] = 0;
            stateCount[4] = 0;
            int currentState = 0;
            for (int j = 0; j < maxJ; j++) {
                if (image.get(j, i)) {
                    if ((currentState & 1) == 1) {
                        currentState++;
                    }
                    stateCount[currentState] = stateCount[currentState] + 1;
                } else if ((currentState & 1) != 0) {
                    stateCount[currentState] = stateCount[currentState] + 1;
                } else if (currentState != 4) {
                    currentState++;
                    stateCount[currentState] = stateCount[currentState] + 1;
                } else if (!foundPatternCross(stateCount) || !handlePossibleCenter(stateCount, i, j, pureBarcode)) {
                    stateCount[0] = stateCount[2];
                    stateCount[1] = stateCount[3];
                    stateCount[2] = stateCount[4];
                    stateCount[3] = 1;
                    stateCount[4] = 0;
                    currentState = 3;
                } else {
                    stateCount[0] = 0;
                    stateCount[1] = 0;
                    stateCount[2] = 0;
                    stateCount[3] = 0;
                    stateCount[4] = 0;
                    currentState = 0;
                }
            }
            if (foundPatternCross(stateCount)) {
                handlePossibleCenter(stateCount, i, maxJ, pureBarcode);
            }
            i += iSkip;
            c = 3;
        }
        FinderPattern[][] patternInfo = selectMutipleBestPatterns();
        List<FinderPatternInfo> result = new ArrayList<>();
        for (FinderPattern[] pattern : patternInfo) {
            ResultPoint.orderBestPatterns(pattern);
            result.add(new FinderPatternInfo(pattern));
        }
        if (result.isEmpty()) {
            return EMPTY_RESULT_ARRAY;
        }
        return (FinderPatternInfo[]) result.toArray(new FinderPatternInfo[result.size()]);
    }
}
