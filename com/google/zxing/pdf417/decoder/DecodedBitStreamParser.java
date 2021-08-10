package com.google.zxing.pdf417.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.pdf417.PDF417ResultMetadata;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;

/* access modifiers changed from: package-private */
public final class DecodedBitStreamParser {
    private static final int AL = 28;
    private static final int AS = 27;
    private static final int BEGIN_MACRO_PDF417_CONTROL_BLOCK = 928;
    private static final int BEGIN_MACRO_PDF417_OPTIONAL_FIELD = 923;
    private static final int BYTE_COMPACTION_MODE_LATCH = 901;
    private static final int BYTE_COMPACTION_MODE_LATCH_6 = 924;
    private static final Charset DEFAULT_ENCODING = Charset.forName("ISO-8859-1");
    private static final int ECI_CHARSET = 927;
    private static final int ECI_GENERAL_PURPOSE = 926;
    private static final int ECI_USER_DEFINED = 925;
    private static final BigInteger[] EXP900;
    private static final int LL = 27;
    private static final int MACRO_PDF417_TERMINATOR = 922;
    private static final int MAX_NUMERIC_CODEWORDS = 15;
    private static final char[] MIXED_CHARS = "0123456789&\r\t,:#-.$/+%*=^".toCharArray();
    private static final int ML = 28;
    private static final int MODE_SHIFT_TO_BYTE_COMPACTION_MODE = 913;
    private static final int NUMBER_OF_SEQUENCE_CODEWORDS = 2;
    private static final int NUMERIC_COMPACTION_MODE_LATCH = 902;
    private static final int PAL = 29;
    private static final int PL = 25;
    private static final int PS = 29;
    private static final char[] PUNCT_CHARS = ";<>@[\\]_`~!\r\t,:\n-.$/\"|*()?{}'".toCharArray();
    private static final int TEXT_COMPACTION_MODE_LATCH = 900;

    /* access modifiers changed from: private */
    public enum Mode {
        ALPHA,
        LOWER,
        MIXED,
        PUNCT,
        ALPHA_SHIFT,
        PUNCT_SHIFT
    }

    static {
        BigInteger[] bigIntegerArr = new BigInteger[16];
        EXP900 = bigIntegerArr;
        bigIntegerArr[0] = BigInteger.ONE;
        BigInteger nineHundred = BigInteger.valueOf(900);
        EXP900[1] = nineHundred;
        int i = 2;
        while (true) {
            BigInteger[] bigIntegerArr2 = EXP900;
            if (i < bigIntegerArr2.length) {
                bigIntegerArr2[i] = bigIntegerArr2[i - 1].multiply(nineHundred);
                i++;
            } else {
                return;
            }
        }
    }

    private DecodedBitStreamParser() {
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0051  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.google.zxing.common.DecoderResult decode(int[] r8, java.lang.String r9) throws com.google.zxing.FormatException {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            int r1 = r8.length
            int r1 = r1 * 2
            r0.<init>(r1)
            java.nio.charset.Charset r1 = com.google.zxing.pdf417.decoder.DecodedBitStreamParser.DEFAULT_ENCODING
            r2 = 1
            int r3 = r2 + 1
            r2 = r8[r2]
            com.google.zxing.pdf417.PDF417ResultMetadata r4 = new com.google.zxing.pdf417.PDF417ResultMetadata
            r4.<init>()
        L_0x0014:
            r5 = 0
            r5 = r8[r5]
            if (r3 >= r5) goto L_0x0072
            r5 = 913(0x391, float:1.28E-42)
            if (r2 == r5) goto L_0x005b
            switch(r2) {
                case 900: goto L_0x0056;
                case 901: goto L_0x0051;
                case 902: goto L_0x004c;
                default: goto L_0x0020;
            }
        L_0x0020:
            switch(r2) {
                case 922: goto L_0x0047;
                case 923: goto L_0x0047;
                case 924: goto L_0x0051;
                case 925: goto L_0x0044;
                case 926: goto L_0x0041;
                case 927: goto L_0x002f;
                case 928: goto L_0x002a;
                default: goto L_0x0023;
            }
        L_0x0023:
            int r3 = r3 + -1
            int r3 = textCompaction(r8, r3, r0)
            goto L_0x0064
        L_0x002a:
            int r3 = decodeMacroBlock(r8, r3, r4)
            goto L_0x0064
        L_0x002f:
            int r5 = r3 + 1
            r3 = r8[r3]
            com.google.zxing.common.CharacterSetECI r3 = com.google.zxing.common.CharacterSetECI.getCharacterSetECIByValue(r3)
            java.lang.String r6 = r3.name()
            java.nio.charset.Charset r1 = java.nio.charset.Charset.forName(r6)
            r3 = r5
            goto L_0x0064
        L_0x0041:
            int r3 = r3 + 2
            goto L_0x0064
        L_0x0044:
            int r3 = r3 + 1
            goto L_0x0064
        L_0x0047:
            com.google.zxing.FormatException r5 = com.google.zxing.FormatException.getFormatInstance()
            throw r5
        L_0x004c:
            int r3 = numericCompaction(r8, r3, r0)
            goto L_0x0064
        L_0x0051:
            int r3 = byteCompaction(r2, r8, r1, r3, r0)
            goto L_0x0064
        L_0x0056:
            int r3 = textCompaction(r8, r3, r0)
            goto L_0x0064
        L_0x005b:
            int r5 = r3 + 1
            r3 = r8[r3]
            char r3 = (char) r3
            r0.append(r3)
            r3 = r5
        L_0x0064:
            int r5 = r8.length
            if (r3 >= r5) goto L_0x006d
            int r5 = r3 + 1
            r2 = r8[r3]
            r3 = r5
            goto L_0x0014
        L_0x006d:
            com.google.zxing.FormatException r5 = com.google.zxing.FormatException.getFormatInstance()
            throw r5
        L_0x0072:
            int r5 = r0.length()
            if (r5 == 0) goto L_0x0086
            com.google.zxing.common.DecoderResult r5 = new com.google.zxing.common.DecoderResult
            java.lang.String r6 = r0.toString()
            r7 = 0
            r5.<init>(r7, r6, r7, r9)
            r5.setOther(r4)
            return r5
        L_0x0086:
            com.google.zxing.FormatException r5 = com.google.zxing.FormatException.getFormatInstance()
            throw r5
            switch-data {900->0x0056, 901->0x0051, 902->0x004c, }
            switch-data {922->0x0047, 923->0x0047, 924->0x0051, 925->0x0044, 926->0x0041, 927->0x002f, 928->0x002a, }
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.pdf417.decoder.DecodedBitStreamParser.decode(int[], java.lang.String):com.google.zxing.common.DecoderResult");
    }

    /* JADX INFO: Multiple debug info for r11v6 int: [D('codeIndex' int), D('code' int)] */
    private static int decodeMacroBlock(int[] codewords, int codeIndex, PDF417ResultMetadata resultMetadata) throws FormatException {
        if (codeIndex + 2 <= codewords[0]) {
            int[] segmentIndexArray = new int[2];
            int i = 0;
            while (i < 2) {
                segmentIndexArray[i] = codewords[codeIndex];
                i++;
                codeIndex++;
            }
            resultMetadata.setSegmentIndex(Integer.parseInt(decodeBase900toBase10(segmentIndexArray, 2)));
            StringBuilder fileId = new StringBuilder();
            int codeIndex2 = textCompaction(codewords, codeIndex, fileId);
            resultMetadata.setFileId(fileId.toString());
            if (codewords[codeIndex2] == BEGIN_MACRO_PDF417_OPTIONAL_FIELD) {
                int code = codeIndex2 + 1;
                int[] additionalOptionCodeWords = new int[(codewords[0] - code)];
                int additionalOptionCodeWordsIndex = 0;
                boolean end = false;
                while (code < codewords[0] && !end) {
                    int codeIndex3 = code + 1;
                    int code2 = codewords[code];
                    if (code2 < TEXT_COMPACTION_MODE_LATCH) {
                        additionalOptionCodeWords[additionalOptionCodeWordsIndex] = code2;
                        code = codeIndex3;
                        additionalOptionCodeWordsIndex++;
                    } else if (code2 == MACRO_PDF417_TERMINATOR) {
                        resultMetadata.setLastSegment(true);
                        end = true;
                        code = codeIndex3 + 1;
                    } else {
                        throw FormatException.getFormatInstance();
                    }
                }
                resultMetadata.setOptionalData(Arrays.copyOf(additionalOptionCodeWords, additionalOptionCodeWordsIndex));
                return code;
            } else if (codewords[codeIndex2] != MACRO_PDF417_TERMINATOR) {
                return codeIndex2;
            } else {
                resultMetadata.setLastSegment(true);
                return codeIndex2 + 1;
            }
        } else {
            throw FormatException.getFormatInstance();
        }
    }

    /* JADX INFO: Multiple debug info for r9v2 int: [D('codeIndex' int), D('code' int)] */
    private static int textCompaction(int[] codewords, int code, StringBuilder result) {
        int[] textCompactionData = new int[((codewords[0] - code) * 2)];
        int[] byteCompactionData = new int[((codewords[0] - code) * 2)];
        int index = 0;
        boolean end = false;
        while (code < codewords[0] && !end) {
            int codeIndex = code + 1;
            int code2 = codewords[code];
            if (code2 < TEXT_COMPACTION_MODE_LATCH) {
                textCompactionData[index] = code2 / 30;
                textCompactionData[index + 1] = code2 % 30;
                index += 2;
                code = codeIndex;
            } else if (code2 != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                if (code2 != 928) {
                    switch (code2) {
                        case TEXT_COMPACTION_MODE_LATCH /*{ENCODED_INT: 900}*/:
                            textCompactionData[index] = TEXT_COMPACTION_MODE_LATCH;
                            code = codeIndex;
                            index++;
                            break;
                        default:
                            switch (code2) {
                                case MACRO_PDF417_TERMINATOR /*{ENCODED_INT: 922}*/:
                                case BEGIN_MACRO_PDF417_OPTIONAL_FIELD /*{ENCODED_INT: 923}*/:
                                case BYTE_COMPACTION_MODE_LATCH_6 /*{ENCODED_INT: 924}*/:
                                    break;
                                default:
                                    code = codeIndex;
                                    break;
                            }
                        case BYTE_COMPACTION_MODE_LATCH /*{ENCODED_INT: 901}*/:
                        case NUMERIC_COMPACTION_MODE_LATCH /*{ENCODED_INT: 902}*/:
                            end = true;
                            code = codeIndex - 1;
                            break;
                    }
                }
                end = true;
                code = codeIndex - 1;
            } else {
                textCompactionData[index] = MODE_SHIFT_TO_BYTE_COMPACTION_MODE;
                byteCompactionData[index] = codewords[codeIndex];
                index++;
                code = codeIndex + 1;
            }
        }
        decodeTextCompaction(textCompactionData, byteCompactionData, index, result);
        return code;
    }

    private static void decodeTextCompaction(int[] textCompactionData, int[] byteCompactionData, int length, StringBuilder result) {
        Mode subMode = Mode.ALPHA;
        Mode priorToShiftMode = Mode.ALPHA;
        for (int i = 0; i < length; i++) {
            int subModeCh = textCompactionData[i];
            char ch = 0;
            switch (AnonymousClass1.$SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[subMode.ordinal()]) {
                case 1:
                    if (subModeCh >= 26) {
                        if (subModeCh != 26) {
                            if (subModeCh != 27) {
                                if (subModeCh != 28) {
                                    if (subModeCh != 29) {
                                        if (subModeCh != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                            if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                                                subMode = Mode.ALPHA;
                                                break;
                                            }
                                        } else {
                                            result.append((char) byteCompactionData[i]);
                                            break;
                                        }
                                    } else {
                                        priorToShiftMode = subMode;
                                        subMode = Mode.PUNCT_SHIFT;
                                        break;
                                    }
                                } else {
                                    subMode = Mode.MIXED;
                                    break;
                                }
                            } else {
                                subMode = Mode.LOWER;
                                break;
                            }
                        } else {
                            ch = ' ';
                            break;
                        }
                    } else {
                        ch = (char) (subModeCh + 65);
                        break;
                    }
                    break;
                case 2:
                    if (subModeCh >= 26) {
                        if (subModeCh != 26) {
                            if (subModeCh != 27) {
                                if (subModeCh != 28) {
                                    if (subModeCh != 29) {
                                        if (subModeCh != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                            if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                                                subMode = Mode.ALPHA;
                                                break;
                                            }
                                        } else {
                                            result.append((char) byteCompactionData[i]);
                                            break;
                                        }
                                    } else {
                                        priorToShiftMode = subMode;
                                        subMode = Mode.PUNCT_SHIFT;
                                        break;
                                    }
                                } else {
                                    subMode = Mode.MIXED;
                                    break;
                                }
                            } else {
                                priorToShiftMode = subMode;
                                subMode = Mode.ALPHA_SHIFT;
                                break;
                            }
                        } else {
                            ch = ' ';
                            break;
                        }
                    } else {
                        ch = (char) (subModeCh + 97);
                        break;
                    }
                    break;
                case 3:
                    if (subModeCh >= 25) {
                        if (subModeCh != 25) {
                            if (subModeCh != 26) {
                                if (subModeCh != 27) {
                                    if (subModeCh != 28) {
                                        if (subModeCh != 29) {
                                            if (subModeCh != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                                if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                                                    subMode = Mode.ALPHA;
                                                    break;
                                                }
                                            } else {
                                                result.append((char) byteCompactionData[i]);
                                                break;
                                            }
                                        } else {
                                            priorToShiftMode = subMode;
                                            subMode = Mode.PUNCT_SHIFT;
                                            break;
                                        }
                                    } else {
                                        subMode = Mode.ALPHA;
                                        break;
                                    }
                                } else {
                                    subMode = Mode.LOWER;
                                    break;
                                }
                            } else {
                                ch = ' ';
                                break;
                            }
                        } else {
                            subMode = Mode.PUNCT;
                            break;
                        }
                    } else {
                        ch = MIXED_CHARS[subModeCh];
                        break;
                    }
                    break;
                case 4:
                    if (subModeCh >= 29) {
                        if (subModeCh != 29) {
                            if (subModeCh != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                                    subMode = Mode.ALPHA;
                                    break;
                                }
                            } else {
                                result.append((char) byteCompactionData[i]);
                                break;
                            }
                        } else {
                            subMode = Mode.ALPHA;
                            break;
                        }
                    } else {
                        ch = PUNCT_CHARS[subModeCh];
                        break;
                    }
                    break;
                case 5:
                    subMode = priorToShiftMode;
                    if (subModeCh >= 26) {
                        if (subModeCh != 26) {
                            if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                                subMode = Mode.ALPHA;
                                break;
                            }
                        } else {
                            ch = ' ';
                            break;
                        }
                    } else {
                        ch = (char) (subModeCh + 65);
                        break;
                    }
                    break;
                case 6:
                    subMode = priorToShiftMode;
                    if (subModeCh >= 29) {
                        if (subModeCh != 29) {
                            if (subModeCh != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                                    subMode = Mode.ALPHA;
                                    break;
                                }
                            } else {
                                result.append((char) byteCompactionData[i]);
                                break;
                            }
                        } else {
                            subMode = Mode.ALPHA;
                            break;
                        }
                    } else {
                        ch = PUNCT_CHARS[subModeCh];
                        break;
                    }
                    break;
            }
            if (ch != 0) {
                result.append(ch);
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.google.zxing.pdf417.decoder.DecodedBitStreamParser$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode;

        static {
            int[] iArr = new int[Mode.values().length];
            $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode = iArr;
            try {
                iArr[Mode.ALPHA.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.LOWER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.MIXED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.PUNCT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.ALPHA_SHIFT.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.PUNCT_SHIFT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    /* JADX INFO: Multiple debug info for r2v4 int: [D('code' int), D('codeIndex' int)] */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00ce, code lost:
        if (r2 == com.google.zxing.pdf417.decoder.DecodedBitStreamParser.MACRO_PDF417_TERMINATOR) goto L_0x00d5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int byteCompaction(int r24, int[] r25, java.nio.charset.Charset r26, int r27, java.lang.StringBuilder r28) {
        /*
            r0 = r24
            java.io.ByteArrayOutputStream r1 = new java.io.ByteArrayOutputStream
            r1.<init>()
            r4 = 928(0x3a0, float:1.3E-42)
            r5 = 902(0x386, float:1.264E-42)
            r6 = 900(0x384, double:4.447E-321)
            r8 = 0
            r9 = 6
            r10 = 924(0x39c, float:1.295E-42)
            r11 = 901(0x385, float:1.263E-42)
            r12 = 900(0x384, float:1.261E-42)
            if (r0 != r11) goto L_0x0097
            r13 = 0
            r14 = 0
            int[] r2 = new int[r9]
            r17 = 0
            int r18 = r27 + 1
            r19 = r25[r27]
            r9 = r19
            r22 = r14
            r14 = r13
            r15 = r17
            r13 = r18
            r17 = r22
        L_0x002d:
            r3 = r25[r8]
            if (r13 >= r3) goto L_0x007e
            if (r15 != 0) goto L_0x007e
            int r3 = r14 + 1
            r2[r14] = r9
            long r20 = r17 * r6
            long r6 = (long) r9
            long r17 = r20 + r6
            int r6 = r13 + 1
            r9 = r25[r13]
            if (r9 == r12) goto L_0x0077
            if (r9 == r11) goto L_0x0077
            if (r9 == r5) goto L_0x0077
            if (r9 == r10) goto L_0x0077
            if (r9 == r4) goto L_0x0077
            r7 = 923(0x39b, float:1.293E-42)
            if (r9 == r7) goto L_0x0077
            r7 = 922(0x39a, float:1.292E-42)
            if (r9 != r7) goto L_0x0053
            goto L_0x0077
        L_0x0053:
            int r7 = r3 % 5
            if (r7 != 0) goto L_0x0072
            if (r3 <= 0) goto L_0x0072
            r7 = 0
        L_0x005a:
            r13 = 6
            if (r7 >= r13) goto L_0x006b
            int r13 = 5 - r7
            int r13 = r13 * 8
            long r13 = r17 >> r13
            int r14 = (int) r13
            byte r13 = (byte) r14
            r1.write(r13)
            int r7 = r7 + 1
            goto L_0x005a
        L_0x006b:
            r17 = 0
            r14 = 0
            r13 = r6
            r6 = 900(0x384, double:4.447E-321)
            goto L_0x002d
        L_0x0072:
            r14 = r3
            r13 = r6
            r6 = 900(0x384, double:4.447E-321)
            goto L_0x002d
        L_0x0077:
            int r13 = r6 + -1
            r15 = 1
            r14 = r3
            r6 = 900(0x384, double:4.447E-321)
            goto L_0x002d
        L_0x007e:
            r3 = r25[r8]
            if (r13 != r3) goto L_0x0089
            if (r9 >= r12) goto L_0x0089
            int r3 = r14 + 1
            r2[r14] = r9
            r14 = r3
        L_0x0089:
            r3 = 0
        L_0x008a:
            if (r3 >= r14) goto L_0x0095
            r4 = r2[r3]
            byte r4 = (byte) r4
            r1.write(r4)
            int r3 = r3 + 1
            goto L_0x008a
        L_0x0095:
            goto L_0x010f
        L_0x0097:
            if (r0 != r10) goto L_0x010d
            r2 = 0
            r6 = 0
            r3 = 0
            r13 = r6
            r6 = r3
            r3 = r2
            r2 = r27
        L_0x00a2:
            r7 = r25[r8]
            if (r2 >= r7) goto L_0x010b
            if (r6 != 0) goto L_0x010b
            int r7 = r2 + 1
            r2 = r25[r2]
            if (r2 >= r12) goto L_0x00bc
            int r3 = r3 + 1
            r17 = 900(0x384, double:4.447E-321)
            long r20 = r13 * r17
            long r8 = (long) r2
            long r13 = r20 + r8
            r8 = 923(0x39b, float:1.293E-42)
            r9 = 922(0x39a, float:1.292E-42)
            goto L_0x00d8
        L_0x00bc:
            r17 = 900(0x384, double:4.447E-321)
            if (r2 == r12) goto L_0x00d1
            if (r2 == r11) goto L_0x00d1
            if (r2 == r5) goto L_0x00d1
            if (r2 == r10) goto L_0x00d1
            if (r2 == r4) goto L_0x00d1
            r8 = 923(0x39b, float:1.293E-42)
            if (r2 == r8) goto L_0x00d3
            r9 = 922(0x39a, float:1.292E-42)
            if (r2 != r9) goto L_0x00d8
            goto L_0x00d5
        L_0x00d1:
            r8 = 923(0x39b, float:1.293E-42)
        L_0x00d3:
            r9 = 922(0x39a, float:1.292E-42)
        L_0x00d5:
            int r7 = r7 + -1
            r6 = 1
        L_0x00d8:
            int r16 = r3 % 5
            if (r16 != 0) goto L_0x0100
            if (r3 <= 0) goto L_0x0100
            r16 = 0
            r4 = r16
        L_0x00e2:
            r5 = 6
            if (r4 >= r5) goto L_0x00f9
            int r19 = 5 - r4
            int r19 = r19 * 8
            r27 = r6
            long r5 = r13 >> r19
            int r6 = (int) r5
            byte r5 = (byte) r6
            r1.write(r5)
            int r4 = r4 + 1
            r6 = r27
            r5 = 902(0x386, float:1.264E-42)
            goto L_0x00e2
        L_0x00f9:
            r27 = r6
            r4 = 0
            r3 = 0
            r13 = r4
            goto L_0x0102
        L_0x0100:
            r27 = r6
        L_0x0102:
            r6 = r27
            r2 = r7
            r4 = 928(0x3a0, float:1.3E-42)
            r5 = 902(0x386, float:1.264E-42)
            r8 = 0
            goto L_0x00a2
        L_0x010b:
            r13 = r2
            goto L_0x010f
        L_0x010d:
            r13 = r27
        L_0x010f:
            java.lang.String r2 = new java.lang.String
            byte[] r3 = r1.toByteArray()
            r4 = r26
            r2.<init>(r3, r4)
            r3 = r28
            r3.append(r2)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.pdf417.decoder.DecodedBitStreamParser.byteCompaction(int, int[], java.nio.charset.Charset, int, java.lang.StringBuilder):int");
    }

    /* JADX INFO: Multiple debug info for r6v2 int: [D('code' int), D('codeIndex' int)] */
    private static int numericCompaction(int[] codewords, int code, StringBuilder result) throws FormatException {
        int count = 0;
        boolean end = false;
        int[] numericCodewords = new int[15];
        while (code < codewords[0] && !end) {
            int codeIndex = code + 1;
            int codeIndex2 = codewords[code];
            if (codeIndex == codewords[0]) {
                end = true;
            }
            if (codeIndex2 < TEXT_COMPACTION_MODE_LATCH) {
                numericCodewords[count] = codeIndex2;
                count++;
            } else if (codeIndex2 == TEXT_COMPACTION_MODE_LATCH || codeIndex2 == BYTE_COMPACTION_MODE_LATCH || codeIndex2 == BYTE_COMPACTION_MODE_LATCH_6 || codeIndex2 == 928 || codeIndex2 == BEGIN_MACRO_PDF417_OPTIONAL_FIELD || codeIndex2 == MACRO_PDF417_TERMINATOR) {
                codeIndex--;
                end = true;
            }
            if ((count % 15 == 0 || codeIndex2 == NUMERIC_COMPACTION_MODE_LATCH || end) && count > 0) {
                result.append(decodeBase900toBase10(numericCodewords, count));
                count = 0;
            }
            code = codeIndex;
        }
        return code;
    }

    private static String decodeBase900toBase10(int[] codewords, int count) throws FormatException {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < count; i++) {
            result = result.add(EXP900[(count - i) - 1].multiply(BigInteger.valueOf((long) codewords[i])));
        }
        String resultString = result.toString();
        if (resultString.charAt(0) == '1') {
            return resultString.substring(1);
        }
        throw FormatException.getFormatInstance();
    }
}
