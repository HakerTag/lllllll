package com.google.zxing.aztec.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.aztec.AztecDetectorResult;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import java.util.Arrays;

public final class Decoder {
    private static final String[] DIGIT_TABLE = {"CTRL_PS", " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ",", ".", "CTRL_UL", "CTRL_US"};
    private static final String[] LOWER_TABLE = {"CTRL_PS", " ", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "CTRL_US", "CTRL_ML", "CTRL_DL", "CTRL_BS"};
    private static final String[] MIXED_TABLE = {"CTRL_PS", " ", "\u0001", "\u0002", "\u0003", "\u0004", "\u0005", "\u0006", "\u0007", "\b", "\t", "\n", "\u000b", "\f", "\r", "\u001b", "\u001c", "\u001d", "\u001e", "\u001f", "@", "\\", "^", "_", "`", "|", "~", "", "CTRL_LL", "CTRL_UL", "CTRL_PL", "CTRL_BS"};
    private static final String[] PUNCT_TABLE = {"", "\r", "\r\n", ". ", ", ", ": ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/", ":", ";", "<", "=", ">", "?", "[", "]", "{", "}", "CTRL_UL"};
    private static final String[] UPPER_TABLE = {"CTRL_PS", " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "CTRL_LL", "CTRL_ML", "CTRL_DL", "CTRL_BS"};
    private AztecDetectorResult ddata;

    /* access modifiers changed from: private */
    public enum Table {
        UPPER,
        LOWER,
        MIXED,
        DIGIT,
        PUNCT,
        BINARY
    }

    public DecoderResult decode(AztecDetectorResult detectorResult) throws FormatException {
        this.ddata = detectorResult;
        boolean[] correctedBits = correctBits(extractBits(detectorResult.getBits()));
        DecoderResult decoderResult = new DecoderResult(convertBoolArrayToByteArray(correctedBits), getEncodedData(correctedBits), null, null);
        decoderResult.setNumBits(correctedBits.length);
        return decoderResult;
    }

    public static String highLevelDecode(boolean[] correctedBits) {
        return getEncodedData(correctedBits);
    }

    private static String getEncodedData(boolean[] correctedBits) {
        int endIndex = correctedBits.length;
        Table latchTable = Table.UPPER;
        Table shiftTable = Table.UPPER;
        StringBuilder result = new StringBuilder(20);
        int index = 0;
        while (index < endIndex) {
            if (shiftTable != Table.BINARY) {
                int size = shiftTable == Table.DIGIT ? 4 : 5;
                if (endIndex - index < size) {
                    break;
                }
                int code = readCode(correctedBits, index, size);
                index += size;
                String str = getCharacter(shiftTable, code);
                if (str.startsWith("CTRL_")) {
                    latchTable = shiftTable;
                    shiftTable = getTable(str.charAt(5));
                    if (str.charAt(6) == 'L') {
                        latchTable = shiftTable;
                    }
                } else {
                    result.append(str);
                    shiftTable = latchTable;
                }
            } else if (endIndex - index < 5) {
                break;
            } else {
                int length = readCode(correctedBits, index, 5);
                index += 5;
                if (length == 0) {
                    if (endIndex - index < 11) {
                        break;
                    }
                    length = readCode(correctedBits, index, 11) + 31;
                    index += 11;
                }
                int charCount = 0;
                while (true) {
                    if (charCount >= length) {
                        break;
                    } else if (endIndex - index < 8) {
                        index = endIndex;
                        break;
                    } else {
                        result.append((char) readCode(correctedBits, index, 8));
                        index += 8;
                        charCount++;
                    }
                }
                shiftTable = latchTable;
            }
        }
        return result.toString();
    }

    private static Table getTable(char t) {
        if (t == 'B') {
            return Table.BINARY;
        }
        if (t == 'D') {
            return Table.DIGIT;
        }
        if (t == 'P') {
            return Table.PUNCT;
        }
        if (t == 'L') {
            return Table.LOWER;
        }
        if (t != 'M') {
            return Table.UPPER;
        }
        return Table.MIXED;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.google.zxing.aztec.decoder.Decoder$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$aztec$decoder$Decoder$Table;

        static {
            int[] iArr = new int[Table.values().length];
            $SwitchMap$com$google$zxing$aztec$decoder$Decoder$Table = iArr;
            try {
                iArr[Table.UPPER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$zxing$aztec$decoder$Decoder$Table[Table.LOWER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$zxing$aztec$decoder$Decoder$Table[Table.MIXED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$zxing$aztec$decoder$Decoder$Table[Table.PUNCT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$zxing$aztec$decoder$Decoder$Table[Table.DIGIT.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private static String getCharacter(Table table, int code) {
        int i = AnonymousClass1.$SwitchMap$com$google$zxing$aztec$decoder$Decoder$Table[table.ordinal()];
        if (i == 1) {
            return UPPER_TABLE[code];
        }
        if (i == 2) {
            return LOWER_TABLE[code];
        }
        if (i == 3) {
            return MIXED_TABLE[code];
        }
        if (i == 4) {
            return PUNCT_TABLE[code];
        }
        if (i == 5) {
            return DIGIT_TABLE[code];
        }
        throw new IllegalStateException("Bad table");
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v11, resolved type: int */
    /* JADX DEBUG: Multi-variable search result rejected for r0v12, resolved type: int */
    /* JADX DEBUG: Multi-variable search result rejected for r0v13, resolved type: int */
    /* JADX DEBUG: Multi-variable search result rejected for r0v14, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r0v16, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r0v23, resolved type: boolean */
    /* JADX WARN: Multi-variable type inference failed */
    private boolean[] correctBits(boolean[] rawbits) throws FormatException {
        GenericGF gf;
        int codewordSize;
        if (this.ddata.getNbLayers() <= 2) {
            gf = GenericGF.AZTEC_DATA_6;
            codewordSize = 6;
        } else if (this.ddata.getNbLayers() <= 8) {
            gf = GenericGF.AZTEC_DATA_8;
            codewordSize = 8;
        } else if (this.ddata.getNbLayers() <= 22) {
            gf = GenericGF.AZTEC_DATA_10;
            codewordSize = 10;
        } else {
            gf = GenericGF.AZTEC_DATA_12;
            codewordSize = 12;
        }
        int numDataCodewords = this.ddata.getNbDatablocks();
        int numCodewords = rawbits.length / codewordSize;
        if (numCodewords >= numDataCodewords) {
            int[] dataWords = new int[numCodewords];
            int offset = rawbits.length % codewordSize;
            int i = 0;
            while (i < numCodewords) {
                dataWords[i] = readCode(rawbits, offset, codewordSize);
                i++;
                offset += codewordSize;
            }
            try {
                new ReedSolomonDecoder(gf).decode(dataWords, numCodewords - numDataCodewords);
                int i2 = 1;
                int mask = (1 << codewordSize) - 1;
                int stuffedBits = 0;
                for (int i3 = 0; i3 < numDataCodewords; i3++) {
                    int dataWord = dataWords[i3];
                    if (dataWord == 0 || dataWord == mask) {
                        throw FormatException.getFormatInstance();
                    }
                    if (dataWord == 1 || dataWord == mask - 1) {
                        stuffedBits++;
                    }
                }
                boolean[] correctedBits = new boolean[((numDataCodewords * codewordSize) - stuffedBits)];
                int index = 0;
                int i4 = 0;
                while (i4 < numDataCodewords) {
                    int dataWord2 = dataWords[i4];
                    if (dataWord2 == i2 || dataWord2 == mask - 1) {
                        int i5 = (index + codewordSize) - i2;
                        boolean z = i2;
                        if (dataWord2 <= i2) {
                            z = 0;
                        }
                        Arrays.fill(correctedBits, index, i5, z);
                        index += codewordSize - 1;
                    } else {
                        int bit = codewordSize - 1;
                        while (bit >= 0) {
                            int index2 = index + 1;
                            correctedBits[index] = (dataWord2 & (i2 << bit)) != 0;
                            bit--;
                            index = index2;
                        }
                    }
                    i4++;
                    i2 = 1;
                }
                return correctedBits;
            } catch (ReedSolomonException ex) {
                throw FormatException.getFormatInstance(ex);
            }
        } else {
            throw FormatException.getFormatInstance();
        }
    }

    private boolean[] extractBits(BitMatrix matrix) {
        boolean compact = this.ddata.isCompact();
        int layers = this.ddata.getNbLayers();
        int baseMatrixSize = (compact ? 11 : 14) + (layers * 4);
        int[] alignmentMap = new int[baseMatrixSize];
        boolean[] rawbits = new boolean[totalBitsInLayer(layers, compact)];
        int i = 2;
        if (compact) {
            for (int i2 = 0; i2 < alignmentMap.length; i2++) {
                alignmentMap[i2] = i2;
            }
        } else {
            int origCenter = baseMatrixSize / 2;
            int center = ((baseMatrixSize + 1) + ((((baseMatrixSize / 2) - 1) / 15) * 2)) / 2;
            for (int i3 = 0; i3 < origCenter; i3++) {
                int newOffset = (i3 / 15) + i3;
                alignmentMap[(origCenter - i3) - 1] = (center - newOffset) - 1;
                alignmentMap[origCenter + i3] = center + newOffset + 1;
            }
        }
        int i4 = 0;
        int rowOffset = 0;
        while (i4 < layers) {
            int rowSize = ((layers - i4) * 4) + (compact ? 9 : 12);
            int low = i4 * 2;
            int high = (baseMatrixSize - 1) - low;
            int j = 0;
            while (j < rowSize) {
                int columnOffset = j * 2;
                int k = 0;
                while (k < i) {
                    rawbits[rowOffset + columnOffset + k] = matrix.get(alignmentMap[low + k], alignmentMap[low + j]);
                    rawbits[(rowSize * 2) + rowOffset + columnOffset + k] = matrix.get(alignmentMap[low + j], alignmentMap[high - k]);
                    rawbits[(rowSize * 4) + rowOffset + columnOffset + k] = matrix.get(alignmentMap[high - k], alignmentMap[high - j]);
                    rawbits[(rowSize * 6) + rowOffset + columnOffset + k] = matrix.get(alignmentMap[high - j], alignmentMap[low + k]);
                    k++;
                    i = 2;
                    compact = compact;
                }
                j++;
                i = 2;
            }
            rowOffset += rowSize * 8;
            i4++;
            i = 2;
        }
        return rawbits;
    }

    private static int readCode(boolean[] rawbits, int startIndex, int length) {
        int res = 0;
        for (int i = startIndex; i < startIndex + length; i++) {
            res <<= 1;
            if (rawbits[i]) {
                res |= 1;
            }
        }
        return res;
    }

    private static byte readByte(boolean[] rawbits, int startIndex) {
        int n = rawbits.length - startIndex;
        if (n >= 8) {
            return (byte) readCode(rawbits, startIndex, 8);
        }
        return (byte) (readCode(rawbits, startIndex, n) << (8 - n));
    }

    static byte[] convertBoolArrayToByteArray(boolean[] boolArr) {
        byte[] byteArr = new byte[((boolArr.length + 7) / 8)];
        for (int i = 0; i < byteArr.length; i++) {
            byteArr[i] = readByte(boolArr, i * 8);
        }
        return byteArr;
    }

    private static int totalBitsInLayer(int layers, boolean compact) {
        return ((compact ? 88 : 112) + (layers * 16)) * layers;
    }
}
