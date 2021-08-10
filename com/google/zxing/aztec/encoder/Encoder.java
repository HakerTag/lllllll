package com.google.zxing.aztec.encoder;

import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;

public final class Encoder {
    public static final int DEFAULT_AZTEC_LAYERS = 0;
    public static final int DEFAULT_EC_PERCENT = 33;
    private static final int MAX_NB_BITS = 32;
    private static final int MAX_NB_BITS_COMPACT = 4;
    private static final int[] WORD_SIZE = {4, 6, 6, 8, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12};

    private Encoder() {
    }

    public static AztecCode encode(byte[] data) {
        return encode(data, 33, 0);
    }

    public static AztecCode encode(byte[] data, int minECCPercent, int userSpecifiedLayers) {
        BitArray stuffedBits;
        int layers;
        boolean compact;
        int wordSize;
        int totalBitsInLayer;
        int i;
        int wordSize2;
        int totalSizeBits;
        int eccBits;
        BitArray bits;
        BitArray stuffedBits2;
        int wordSize3;
        BitArray bits2 = new HighLevelEncoder(data).encode();
        int eccBits2 = ((bits2.getSize() * minECCPercent) / 100) + 11;
        int totalSizeBits2 = bits2.getSize() + eccBits2;
        int i2 = 32;
        if (userSpecifiedLayers != 0) {
            compact = userSpecifiedLayers < 0;
            layers = Math.abs(userSpecifiedLayers);
            if (compact) {
                i2 = 4;
            }
            if (layers <= i2) {
                totalBitsInLayer = totalBitsInLayer(layers, compact);
                wordSize = WORD_SIZE[layers];
                int usableBitsInLayers = totalBitsInLayer - (totalBitsInLayer % wordSize);
                stuffedBits = stuffBits(bits2, wordSize);
                if (stuffedBits.getSize() + eccBits2 > usableBitsInLayers) {
                    throw new IllegalArgumentException("Data to large for user specified layer");
                } else if (compact && stuffedBits.getSize() > wordSize * 64) {
                    throw new IllegalArgumentException("Data to large for user specified layer");
                }
            } else {
                throw new IllegalArgumentException(String.format("Illegal value %s for layers", Integer.valueOf(userSpecifiedLayers)));
            }
        } else {
            int wordSize4 = 0;
            BitArray stuffedBits3 = null;
            int i3 = 0;
            while (i3 <= i2) {
                boolean compact2 = i3 <= 3;
                int layers2 = compact2 ? i3 + 1 : i3;
                int totalBitsInLayer2 = totalBitsInLayer(layers2, compact2);
                if (totalSizeBits2 > totalBitsInLayer2) {
                    bits = bits2;
                    eccBits = eccBits2;
                    totalSizeBits = totalSizeBits2;
                } else {
                    int[] iArr = WORD_SIZE;
                    if (wordSize4 != iArr[layers2]) {
                        wordSize3 = iArr[layers2];
                        stuffedBits2 = stuffBits(bits2, wordSize3);
                    } else {
                        wordSize3 = wordSize4;
                        stuffedBits2 = stuffedBits3;
                    }
                    int usableBitsInLayers2 = totalBitsInLayer2 - (totalBitsInLayer2 % wordSize3);
                    if (compact2 && stuffedBits2.getSize() > wordSize3 * 64) {
                        bits = bits2;
                        eccBits = eccBits2;
                        totalSizeBits = totalSizeBits2;
                    } else if (stuffedBits2.getSize() + eccBits2 <= usableBitsInLayers2) {
                        wordSize = wordSize3;
                        layers = layers2;
                        totalBitsInLayer = totalBitsInLayer2;
                        stuffedBits = stuffedBits2;
                        compact = compact2;
                    } else {
                        bits = bits2;
                        eccBits = eccBits2;
                        totalSizeBits = totalSizeBits2;
                    }
                    stuffedBits3 = stuffedBits2;
                    wordSize4 = wordSize3;
                }
                i3++;
                bits2 = bits;
                eccBits2 = eccBits;
                totalSizeBits2 = totalSizeBits;
                i2 = 32;
            }
            throw new IllegalArgumentException("Data too large for an Aztec code");
        }
        BitArray messageBits = generateCheckWords(stuffedBits, totalBitsInLayer, wordSize);
        int messageSizeInWords = stuffedBits.getSize() / wordSize;
        BitArray modeMessage = generateModeMessage(compact, layers, messageSizeInWords);
        int baseMatrixSize = (compact ? 11 : 14) + (layers * 4);
        int[] alignmentMap = new int[baseMatrixSize];
        if (compact) {
            for (int i4 = 0; i4 < alignmentMap.length; i4++) {
                alignmentMap[i4] = i4;
            }
            i = baseMatrixSize;
        } else {
            int matrixSize = baseMatrixSize + 1 + ((((baseMatrixSize / 2) - 1) / 15) * 2);
            int origCenter = baseMatrixSize / 2;
            int center = matrixSize / 2;
            for (int i5 = 0; i5 < origCenter; i5++) {
                int newOffset = i5 + (i5 / 15);
                alignmentMap[(origCenter - i5) - 1] = (center - newOffset) - 1;
                alignmentMap[origCenter + i5] = center + newOffset + 1;
            }
            i = matrixSize;
        }
        BitMatrix matrix = new BitMatrix(i);
        int i6 = 0;
        int rowOffset = 0;
        while (i6 < layers) {
            int rowSize = ((layers - i6) * 4) + (compact ? 9 : 12);
            int j = 0;
            while (j < rowSize) {
                int columnOffset = j * 2;
                int k = 0;
                while (k < 2) {
                    if (messageBits.get(rowOffset + columnOffset + k)) {
                        wordSize2 = wordSize;
                        matrix.set(alignmentMap[(i6 * 2) + k], alignmentMap[(i6 * 2) + j]);
                    } else {
                        wordSize2 = wordSize;
                    }
                    if (messageBits.get(rowOffset + (rowSize * 2) + columnOffset + k)) {
                        matrix.set(alignmentMap[(i6 * 2) + j], alignmentMap[((baseMatrixSize - 1) - (i6 * 2)) - k]);
                    }
                    if (messageBits.get(rowOffset + (rowSize * 4) + columnOffset + k)) {
                        matrix.set(alignmentMap[((baseMatrixSize - 1) - (i6 * 2)) - k], alignmentMap[((baseMatrixSize - 1) - (i6 * 2)) - j]);
                    }
                    if (messageBits.get(rowOffset + (rowSize * 6) + columnOffset + k)) {
                        matrix.set(alignmentMap[((baseMatrixSize - 1) - (i6 * 2)) - j], alignmentMap[(i6 * 2) + k]);
                    }
                    k++;
                    totalBitsInLayer = totalBitsInLayer;
                    wordSize = wordSize2;
                }
                j++;
                totalBitsInLayer = totalBitsInLayer;
                totalSizeBits2 = totalSizeBits2;
            }
            rowOffset += rowSize * 8;
            i6++;
            eccBits2 = eccBits2;
        }
        drawModeMessage(matrix, compact, i, modeMessage);
        if (compact) {
            drawBullsEye(matrix, i / 2, 5);
        } else {
            drawBullsEye(matrix, i / 2, 7);
            int i7 = 0;
            int j2 = 0;
            while (i7 < (baseMatrixSize / 2) - 1) {
                for (int k2 = (i / 2) & 1; k2 < i; k2 += 2) {
                    matrix.set((i / 2) - j2, k2);
                    matrix.set((i / 2) + j2, k2);
                    matrix.set(k2, (i / 2) - j2);
                    matrix.set(k2, (i / 2) + j2);
                }
                i7 += 15;
                j2 += 16;
            }
        }
        AztecCode aztec = new AztecCode();
        aztec.setCompact(compact);
        aztec.setSize(i);
        aztec.setLayers(layers);
        aztec.setCodeWords(messageSizeInWords);
        aztec.setMatrix(matrix);
        return aztec;
    }

    private static void drawBullsEye(BitMatrix matrix, int center, int size) {
        for (int i = 0; i < size; i += 2) {
            for (int j = center - i; j <= center + i; j++) {
                matrix.set(j, center - i);
                matrix.set(j, center + i);
                matrix.set(center - i, j);
                matrix.set(center + i, j);
            }
        }
        matrix.set(center - size, center - size);
        matrix.set((center - size) + 1, center - size);
        matrix.set(center - size, (center - size) + 1);
        matrix.set(center + size, center - size);
        matrix.set(center + size, (center - size) + 1);
        matrix.set(center + size, (center + size) - 1);
    }

    static BitArray generateModeMessage(boolean compact, int layers, int messageSizeInWords) {
        BitArray modeMessage = new BitArray();
        if (compact) {
            modeMessage.appendBits(layers - 1, 2);
            modeMessage.appendBits(messageSizeInWords - 1, 6);
            return generateCheckWords(modeMessage, 28, 4);
        }
        modeMessage.appendBits(layers - 1, 5);
        modeMessage.appendBits(messageSizeInWords - 1, 11);
        return generateCheckWords(modeMessage, 40, 4);
    }

    private static void drawModeMessage(BitMatrix matrix, boolean compact, int matrixSize, BitArray modeMessage) {
        int center = matrixSize / 2;
        if (compact) {
            for (int i = 0; i < 7; i++) {
                int offset = (center - 3) + i;
                if (modeMessage.get(i)) {
                    matrix.set(offset, center - 5);
                }
                if (modeMessage.get(i + 7)) {
                    matrix.set(center + 5, offset);
                }
                if (modeMessage.get(20 - i)) {
                    matrix.set(offset, center + 5);
                }
                if (modeMessage.get(27 - i)) {
                    matrix.set(center - 5, offset);
                }
            }
            return;
        }
        for (int i2 = 0; i2 < 10; i2++) {
            int offset2 = (center - 5) + i2 + (i2 / 5);
            if (modeMessage.get(i2)) {
                matrix.set(offset2, center - 7);
            }
            if (modeMessage.get(i2 + 10)) {
                matrix.set(center + 7, offset2);
            }
            if (modeMessage.get(29 - i2)) {
                matrix.set(offset2, center + 7);
            }
            if (modeMessage.get(39 - i2)) {
                matrix.set(center - 7, offset2);
            }
        }
    }

    private static BitArray generateCheckWords(BitArray bitArray, int totalBits, int wordSize) {
        ReedSolomonEncoder rs = new ReedSolomonEncoder(getGF(wordSize));
        int totalWords = totalBits / wordSize;
        int[] messageWords = bitsToWords(bitArray, wordSize, totalWords);
        rs.encode(messageWords, totalWords - (bitArray.getSize() / wordSize));
        BitArray messageBits = new BitArray();
        messageBits.appendBits(0, totalBits % wordSize);
        for (int messageWord : messageWords) {
            messageBits.appendBits(messageWord, wordSize);
        }
        return messageBits;
    }

    private static int[] bitsToWords(BitArray stuffedBits, int wordSize, int totalWords) {
        int[] message = new int[totalWords];
        int n = stuffedBits.getSize() / wordSize;
        for (int i = 0; i < n; i++) {
            int value = 0;
            for (int j = 0; j < wordSize; j++) {
                value |= stuffedBits.get((i * wordSize) + j) ? 1 << ((wordSize - j) - 1) : 0;
            }
            message[i] = value;
        }
        return message;
    }

    private static GenericGF getGF(int wordSize) {
        if (wordSize == 4) {
            return GenericGF.AZTEC_PARAM;
        }
        if (wordSize == 6) {
            return GenericGF.AZTEC_DATA_6;
        }
        if (wordSize == 8) {
            return GenericGF.AZTEC_DATA_8;
        }
        if (wordSize == 10) {
            return GenericGF.AZTEC_DATA_10;
        }
        if (wordSize == 12) {
            return GenericGF.AZTEC_DATA_12;
        }
        throw new IllegalArgumentException("Unsupported word size " + wordSize);
    }

    static BitArray stuffBits(BitArray bits, int wordSize) {
        BitArray out = new BitArray();
        int n = bits.getSize();
        int mask = (1 << wordSize) - 2;
        int i = 0;
        while (i < n) {
            int word = 0;
            for (int j = 0; j < wordSize; j++) {
                if (i + j >= n || bits.get(i + j)) {
                    word |= 1 << ((wordSize - 1) - j);
                }
            }
            if ((word & mask) == mask) {
                out.appendBits(word & mask, wordSize);
                i--;
            } else if ((word & mask) == 0) {
                out.appendBits(word | 1, wordSize);
                i--;
            } else {
                out.appendBits(word, wordSize);
            }
            i += wordSize;
        }
        return out;
    }

    private static int totalBitsInLayer(int layers, boolean compact) {
        return ((compact ? 88 : 112) + (layers * 16)) * layers;
    }
}
