package com.google.zxing.oned;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Arrays;
import java.util.Map;

public abstract class OneDReader implements Reader {
    public abstract Result decodeRow(int i, BitArray bitArray, Map<DecodeHintType, ?> map) throws NotFoundException, ChecksumException, FormatException;

    @Override // com.google.zxing.Reader
    public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
        return decode(image, null);
    }

    @Override // com.google.zxing.Reader
    public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
        try {
            return doDecode(image, hints);
        } catch (NotFoundException nfe) {
            if (!(hints != null && hints.containsKey(DecodeHintType.TRY_HARDER)) || !image.isRotateSupported()) {
                throw nfe;
            }
            BinaryBitmap rotatedImage = image.rotateCounterClockwise();
            Result result = doDecode(rotatedImage, hints);
            Map<ResultMetadataType, ?> metadata = result.getResultMetadata();
            int orientation = 270;
            if (metadata != null && metadata.containsKey(ResultMetadataType.ORIENTATION)) {
                orientation = (((Integer) metadata.get(ResultMetadataType.ORIENTATION)).intValue() + 270) % 360;
            }
            result.putMetadata(ResultMetadataType.ORIENTATION, Integer.valueOf(orientation));
            ResultPoint[] points = result.getResultPoints();
            if (points != null) {
                int height = rotatedImage.getHeight();
                for (int i = 0; i < points.length; i++) {
                    points[i] = new ResultPoint((((float) height) - points[i].getY()) - 1.0f, points[i].getX());
                }
            }
            return result;
        }
    }

    @Override // com.google.zxing.Reader
    public void reset() {
    }

    /* JADX WARNING: Code restructure failed: missing block: B:70:0x00fb, code lost:
        return r0;
     */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x008e A[SYNTHETIC, Splitter:B:43:0x008e] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x00f7 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.zxing.Result doDecode(com.google.zxing.BinaryBitmap r25, java.util.Map<com.google.zxing.DecodeHintType, ?> r26) throws com.google.zxing.NotFoundException {
        /*
            r24 = this;
            r0 = r26
            int r1 = r25.getWidth()
            int r2 = r25.getHeight()
            com.google.zxing.common.BitArray r3 = new com.google.zxing.common.BitArray
            r3.<init>(r1)
            int r4 = r2 >> 1
            r6 = 1
            if (r0 == 0) goto L_0x001e
            com.google.zxing.DecodeHintType r7 = com.google.zxing.DecodeHintType.TRY_HARDER
            boolean r7 = r0.containsKey(r7)
            if (r7 == 0) goto L_0x001e
            r7 = 1
            goto L_0x001f
        L_0x001e:
            r7 = 0
        L_0x001f:
            if (r7 == 0) goto L_0x0024
            r8 = 8
            goto L_0x0025
        L_0x0024:
            r8 = 5
        L_0x0025:
            int r8 = r2 >> r8
            int r8 = java.lang.Math.max(r6, r8)
            if (r7 == 0) goto L_0x002f
            r9 = r2
            goto L_0x0031
        L_0x002f:
            r9 = 15
        L_0x0031:
            r10 = 0
            r11 = r10
            r10 = r3
            r3 = r0
        L_0x0035:
            if (r11 >= r9) goto L_0x0143
            int r0 = r11 + 1
            r12 = 2
            int r13 = r0 / 2
            r0 = r11 & 1
            if (r0 != 0) goto L_0x0042
            r0 = 1
            goto L_0x0043
        L_0x0042:
            r0 = 0
        L_0x0043:
            r14 = r0
            if (r14 == 0) goto L_0x0048
            r0 = r13
            goto L_0x0049
        L_0x0048:
            int r0 = -r13
        L_0x0049:
            int r0 = r0 * r8
            int r15 = r4 + r0
            if (r15 < 0) goto L_0x013c
            if (r15 >= r2) goto L_0x0135
            r5 = r25
            com.google.zxing.common.BitArray r0 = r5.getBlackRow(r15, r10)     // Catch:{ NotFoundException -> 0x011f }
            r10 = r0
            r0 = 0
            r23 = r3
            r3 = r0
            r0 = r23
        L_0x005f:
            if (r3 >= r12) goto L_0x0114
            if (r3 != r6) goto L_0x0082
            r10.reverse()
            if (r0 == 0) goto L_0x0082
            com.google.zxing.DecodeHintType r12 = com.google.zxing.DecodeHintType.NEED_RESULT_POINT_CALLBACK
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x0082
            java.util.EnumMap r12 = new java.util.EnumMap
            java.lang.Class<com.google.zxing.DecodeHintType> r6 = com.google.zxing.DecodeHintType.class
            r12.<init>(r6)
            r6 = r12
            r6.putAll(r0)
            com.google.zxing.DecodeHintType r12 = com.google.zxing.DecodeHintType.NEED_RESULT_POINT_CALLBACK
            r6.remove(r12)
            r0 = r6
            goto L_0x0083
        L_0x0082:
            r6 = r0
        L_0x0083:
            r12 = r24
            com.google.zxing.Result r0 = r12.decodeRow(r15, r10, r6)     // Catch:{ ReaderException -> 0x00fc }
            r18 = r2
            r2 = 1
            if (r3 != r2) goto L_0x00f7
            com.google.zxing.ResultMetadataType r2 = com.google.zxing.ResultMetadataType.ORIENTATION     // Catch:{ ReaderException -> 0x00ee }
            r19 = 180(0xb4, float:2.52E-43)
            r20 = r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r19)     // Catch:{ ReaderException -> 0x00e7 }
            r0.putMetadata(r2, r4)     // Catch:{ ReaderException -> 0x00e7 }
            com.google.zxing.ResultPoint[] r2 = r0.getResultPoints()     // Catch:{ ReaderException -> 0x00e7 }
            if (r2 == 0) goto L_0x00e4
            com.google.zxing.ResultPoint r4 = new com.google.zxing.ResultPoint     // Catch:{ ReaderException -> 0x00e7 }
            float r5 = (float) r1
            r16 = 0
            r19 = r2[r16]     // Catch:{ ReaderException -> 0x00df }
            float r19 = r19.getX()     // Catch:{ ReaderException -> 0x00df }
            float r5 = r5 - r19
            r19 = 1065353216(0x3f800000, float:1.0)
            float r5 = r5 - r19
            r21 = r2[r16]     // Catch:{ ReaderException -> 0x00df }
            r22 = r6
            float r6 = r21.getY()     // Catch:{ ReaderException -> 0x00dc }
            r4.<init>(r5, r6)     // Catch:{ ReaderException -> 0x00dc }
            r2[r16] = r4     // Catch:{ ReaderException -> 0x00dc }
            com.google.zxing.ResultPoint r4 = new com.google.zxing.ResultPoint     // Catch:{ ReaderException -> 0x00dc }
            float r5 = (float) r1     // Catch:{ ReaderException -> 0x00dc }
            r6 = 1
            r17 = r2[r6]     // Catch:{ ReaderException -> 0x00dc }
            float r17 = r17.getX()     // Catch:{ ReaderException -> 0x00dc }
            float r5 = r5 - r17
            float r5 = r5 - r19
            r17 = r2[r6]     // Catch:{ ReaderException -> 0x00dc }
            float r6 = r17.getY()     // Catch:{ ReaderException -> 0x00dc }
            r4.<init>(r5, r6)     // Catch:{ ReaderException -> 0x00dc }
            r5 = 1
            r2[r5] = r4     // Catch:{ ReaderException -> 0x00da }
            goto L_0x00fb
        L_0x00da:
            r0 = move-exception
            goto L_0x0106
        L_0x00dc:
            r0 = move-exception
            r5 = 1
            goto L_0x0106
        L_0x00df:
            r0 = move-exception
            r22 = r6
            r5 = 1
            goto L_0x00ed
        L_0x00e4:
            r22 = r6
            goto L_0x00fb
        L_0x00e7:
            r0 = move-exception
            r22 = r6
            r5 = 1
            r16 = 0
        L_0x00ed:
            goto L_0x0106
        L_0x00ee:
            r0 = move-exception
            r20 = r4
            r22 = r6
            r5 = 1
            r16 = 0
            goto L_0x0106
        L_0x00f7:
            r20 = r4
            r22 = r6
        L_0x00fb:
            return r0
        L_0x00fc:
            r0 = move-exception
            r18 = r2
            r20 = r4
            r22 = r6
            r5 = 1
            r16 = 0
        L_0x0106:
            int r3 = r3 + 1
            r5 = r25
            r2 = r18
            r4 = r20
            r0 = r22
            r6 = 1
            r12 = 2
            goto L_0x005f
        L_0x0114:
            r12 = r24
            r18 = r2
            r20 = r4
            r5 = 1
            r16 = 0
            r3 = r0
            goto L_0x012c
        L_0x011f:
            r0 = move-exception
            r12 = r24
            r18 = r2
            r20 = r4
            r5 = 1
            r16 = 0
            r2 = r0
            r0 = r2
        L_0x012c:
            int r11 = r11 + 1
            r2 = r18
            r4 = r20
            r6 = 1
            goto L_0x0035
        L_0x0135:
            r12 = r24
            r18 = r2
            r20 = r4
            goto L_0x0149
        L_0x013c:
            r12 = r24
            r18 = r2
            r20 = r4
            goto L_0x0149
        L_0x0143:
            r12 = r24
            r18 = r2
            r20 = r4
        L_0x0149:
            com.google.zxing.NotFoundException r0 = com.google.zxing.NotFoundException.getNotFoundInstance()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.oned.OneDReader.doDecode(com.google.zxing.BinaryBitmap, java.util.Map):com.google.zxing.Result");
    }

    protected static void recordPattern(BitArray row, int start, int[] counters) throws NotFoundException {
        int numCounters = counters.length;
        Arrays.fill(counters, 0, numCounters, 0);
        int end = row.getSize();
        if (start < end) {
            boolean isWhite = !row.get(start);
            int counterPosition = 0;
            int i = start;
            while (i < end) {
                if (row.get(i) ^ isWhite) {
                    counters[counterPosition] = counters[counterPosition] + 1;
                } else {
                    counterPosition++;
                    if (counterPosition == numCounters) {
                        break;
                    }
                    counters[counterPosition] = 1;
                    isWhite = !isWhite;
                }
                i++;
            }
            if (counterPosition == numCounters) {
                return;
            }
            if (counterPosition != numCounters - 1 || i != end) {
                throw NotFoundException.getNotFoundInstance();
            }
            return;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    protected static void recordPatternInReverse(BitArray row, int start, int[] counters) throws NotFoundException {
        int numTransitionsLeft = counters.length;
        boolean last = row.get(start);
        while (start > 0 && numTransitionsLeft >= 0) {
            start--;
            if (row.get(start) != last) {
                numTransitionsLeft--;
                last = !last;
            }
        }
        if (numTransitionsLeft < 0) {
            recordPattern(row, start + 1, counters);
            return;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    protected static float patternMatchVariance(int[] counters, int[] pattern, float maxIndividualVariance) {
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
