package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Arrays;
import java.util.Map;

public final class CodaBarReader extends OneDReader {
    static final char[] ALPHABET = ALPHABET_STRING.toCharArray();
    private static final String ALPHABET_STRING = "0123456789-$:/.+ABCD";
    static final int[] CHARACTER_ENCODINGS = {3, 6, 9, 96, 18, 66, 33, 36, 48, 72, 12, 24, 69, 81, 84, 21, 26, 41, 11, 14};
    private static final float MAX_ACCEPTABLE = 2.0f;
    private static final int MIN_CHARACTER_LENGTH = 3;
    private static final float PADDING = 1.5f;
    private static final char[] STARTEND_ENCODING = {'A', 'B', 'C', 'D'};
    private int counterLength = 0;
    private int[] counters = new int[80];
    private final StringBuilder decodeRowResult = new StringBuilder(20);

    /* JADX INFO: Multiple debug info for r13v3 float: [D('left' float), D('i' int)] */
    /* JADX INFO: Multiple debug info for r14v2 float: [D('i' int), D('right' float)] */
    @Override // com.google.zxing.oned.OneDReader
    public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException {
        CodaBarReader codaBarReader = this;
        Map<DecodeHintType, ?> map = hints;
        int i = 0;
        Arrays.fill(codaBarReader.counters, 0);
        codaBarReader.setCounters(row);
        int startOffset = findStartPattern();
        int nextStart = startOffset;
        codaBarReader.decodeRowResult.setLength(0);
        while (true) {
            int charOffset = codaBarReader.toNarrowWidePattern(nextStart);
            if (charOffset != -1) {
                codaBarReader.decodeRowResult.append((char) charOffset);
                nextStart += 8;
                if ((codaBarReader.decodeRowResult.length() <= 1 || !arrayContains(STARTEND_ENCODING, ALPHABET[charOffset])) && nextStart < codaBarReader.counterLength) {
                    i = 0;
                    codaBarReader = this;
                    map = hints;
                }
            } else {
                throw NotFoundException.getNotFoundInstance();
            }
        }
        int trailingWhitespace = codaBarReader.counters[nextStart - 1];
        int lastPatternSize = 0;
        for (int i2 = -8; i2 < -1; i2++) {
            lastPatternSize += codaBarReader.counters[nextStart + i2];
        }
        if (nextStart >= codaBarReader.counterLength || trailingWhitespace >= lastPatternSize / 2) {
            codaBarReader.validatePattern(startOffset);
            for (int i3 = 0; i3 < codaBarReader.decodeRowResult.length(); i3++) {
                StringBuilder sb = codaBarReader.decodeRowResult;
                sb.setCharAt(i3, ALPHABET[sb.charAt(i3)]);
            }
            if (arrayContains(STARTEND_ENCODING, codaBarReader.decodeRowResult.charAt(i))) {
                StringBuilder sb2 = codaBarReader.decodeRowResult;
                if (!arrayContains(STARTEND_ENCODING, sb2.charAt(sb2.length() - 1))) {
                    throw NotFoundException.getNotFoundInstance();
                } else if (codaBarReader.decodeRowResult.length() > 3) {
                    if (map == null || !map.containsKey(DecodeHintType.RETURN_CODABAR_START_END)) {
                        StringBuilder sb3 = codaBarReader.decodeRowResult;
                        sb3.deleteCharAt(sb3.length() - 1);
                        codaBarReader.decodeRowResult.deleteCharAt(i);
                    }
                    int runningCount = 0;
                    for (int i4 = 0; i4 < startOffset; i4++) {
                        runningCount += codaBarReader.counters[i4];
                    }
                    float left = (float) runningCount;
                    for (int i5 = startOffset; i5 < nextStart - 1; i5++) {
                        runningCount += codaBarReader.counters[i5];
                    }
                    return new Result(codaBarReader.decodeRowResult.toString(), null, new ResultPoint[]{new ResultPoint(left, (float) rowNumber), new ResultPoint((float) runningCount, (float) rowNumber)}, BarcodeFormat.CODABAR);
                } else {
                    throw NotFoundException.getNotFoundInstance();
                }
            } else {
                throw NotFoundException.getNotFoundInstance();
            }
        } else {
            throw NotFoundException.getNotFoundInstance();
        }
    }

    private void validatePattern(int start) throws NotFoundException {
        int[] sizes = {0, 0, 0, 0};
        int[] counts = {0, 0, 0, 0};
        int end = this.decodeRowResult.length() - 1;
        int pos = start;
        int i = 0;
        while (true) {
            int pattern = CHARACTER_ENCODINGS[this.decodeRowResult.charAt(i)];
            for (int j = 6; j >= 0; j--) {
                int category = (j & 1) + ((pattern & 1) * 2);
                sizes[category] = sizes[category] + this.counters[pos + j];
                counts[category] = counts[category] + 1;
                pattern >>= 1;
            }
            if (i >= end) {
                break;
            }
            pos += 8;
            i++;
        }
        float[] maxes = new float[4];
        float[] mins = new float[4];
        for (int i2 = 0; i2 < 2; i2++) {
            mins[i2] = 0.0f;
            mins[i2 + 2] = ((((float) sizes[i2]) / ((float) counts[i2])) + (((float) sizes[i2 + 2]) / ((float) counts[i2 + 2]))) / MAX_ACCEPTABLE;
            maxes[i2] = mins[i2 + 2];
            maxes[i2 + 2] = ((((float) sizes[i2 + 2]) * MAX_ACCEPTABLE) + PADDING) / ((float) counts[i2 + 2]);
        }
        int pos2 = start;
        int i3 = 0;
        loop3:
        while (true) {
            int pattern2 = CHARACTER_ENCODINGS[this.decodeRowResult.charAt(i3)];
            for (int j2 = 6; j2 >= 0; j2--) {
                int category2 = (j2 & 1) + ((pattern2 & 1) * 2);
                int size = this.counters[pos2 + j2];
                if (((float) size) >= mins[category2] && ((float) size) <= maxes[category2]) {
                    pattern2 >>= 1;
                }
            }
            if (i3 < end) {
                pos2 += 8;
                i3++;
            } else {
                return;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private void setCounters(BitArray row) throws NotFoundException {
        this.counterLength = 0;
        int i = row.getNextUnset(0);
        int end = row.getSize();
        if (i < end) {
            boolean isWhite = true;
            int count = 0;
            while (i < end) {
                if (row.get(i) ^ isWhite) {
                    count++;
                } else {
                    counterAppend(count);
                    count = 1;
                    isWhite = !isWhite;
                }
                i++;
            }
            counterAppend(count);
            return;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private void counterAppend(int e) {
        int[] iArr = this.counters;
        int i = this.counterLength;
        iArr[i] = e;
        int i2 = i + 1;
        this.counterLength = i2;
        if (i2 >= iArr.length) {
            int[] temp = new int[(i2 * 2)];
            System.arraycopy(iArr, 0, temp, 0, i2);
            this.counters = temp;
        }
    }

    private int findStartPattern() throws NotFoundException {
        for (int i = 1; i < this.counterLength; i += 2) {
            int charOffset = toNarrowWidePattern(i);
            if (charOffset != -1 && arrayContains(STARTEND_ENCODING, ALPHABET[charOffset])) {
                int patternSize = 0;
                for (int j = i; j < i + 7; j++) {
                    patternSize += this.counters[j];
                }
                if (i == 1 || this.counters[i - 1] >= patternSize / 2) {
                    return i;
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    static boolean arrayContains(char[] array, char key) {
        if (array != null) {
            for (char c : array) {
                if (c == key) {
                    return true;
                }
            }
        }
        return false;
    }

    private int toNarrowWidePattern(int position) {
        int end = position + 7;
        if (end >= this.counterLength) {
            return -1;
        }
        int[] theCounters = this.counters;
        int maxBar = 0;
        int minBar = Integer.MAX_VALUE;
        for (int j = position; j < end; j += 2) {
            int currentCounter = theCounters[j];
            if (currentCounter < minBar) {
                minBar = currentCounter;
            }
            if (currentCounter > maxBar) {
                maxBar = currentCounter;
            }
        }
        int thresholdBar = (minBar + maxBar) / 2;
        int maxSpace = 0;
        int minSpace = Integer.MAX_VALUE;
        for (int j2 = position + 1; j2 < end; j2 += 2) {
            int currentCounter2 = theCounters[j2];
            if (currentCounter2 < minSpace) {
                minSpace = currentCounter2;
            }
            if (currentCounter2 > maxSpace) {
                maxSpace = currentCounter2;
            }
        }
        int thresholdSpace = (minSpace + maxSpace) / 2;
        int bitmask = 128;
        int pattern = 0;
        for (int i = 0; i < 7; i++) {
            bitmask >>= 1;
            if (theCounters[position + i] > ((i & 1) == 0 ? thresholdBar : thresholdSpace)) {
                pattern |= bitmask;
            }
        }
        int i2 = 0;
        while (true) {
            int[] iArr = CHARACTER_ENCODINGS;
            if (i2 >= iArr.length) {
                return -1;
            }
            if (iArr[i2] == pattern) {
                return i2;
            }
            i2++;
        }
    }
}
