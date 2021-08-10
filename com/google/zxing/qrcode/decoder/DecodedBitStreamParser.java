package com.google.zxing.qrcode.decoder;

import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.common.BitSource;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.StringUtils;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;

/* access modifiers changed from: package-private */
public final class DecodedBitStreamParser {
    private static final char[] ALPHANUMERIC_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:".toCharArray();
    private static final int GB2312_SUBSET = 1;

    private DecodedBitStreamParser() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:63:0x00f6 A[LOOP:0: B:1:0x001c->B:63:0x00f6, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00d2 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.google.zxing.common.DecoderResult decode(byte[] r16, com.google.zxing.qrcode.decoder.Version r17, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel r18, java.util.Map<com.google.zxing.DecodeHintType, ?> r19) throws com.google.zxing.FormatException {
        /*
            r1 = r17
            com.google.zxing.common.BitSource r0 = new com.google.zxing.common.BitSource
            r9 = r16
            r0.<init>(r9)
            r8 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r2 = 50
            r0.<init>(r2)
            r7 = r0
            java.util.ArrayList r14 = new java.util.ArrayList
            r0 = 1
            r14.<init>(r0)
            r2 = -1
            r3 = -1
            r4 = 0
            r5 = 0
        L_0x001c:
            int r6 = r8.available()     // Catch:{ IllegalArgumentException -> 0x0103 }
            r10 = 4
            if (r6 >= r10) goto L_0x002b
            com.google.zxing.qrcode.decoder.Mode r6 = com.google.zxing.qrcode.decoder.Mode.TERMINATOR     // Catch:{ IllegalArgumentException -> 0x0026 }
            goto L_0x0033
        L_0x0026:
            r0 = move-exception
            r12 = r7
            r13 = r8
            goto L_0x0106
        L_0x002b:
            int r6 = r8.readBits(r10)
            com.google.zxing.qrcode.decoder.Mode r6 = com.google.zxing.qrcode.decoder.Mode.forBits(r6)
        L_0x0033:
            com.google.zxing.qrcode.decoder.Mode r11 = com.google.zxing.qrcode.decoder.Mode.TERMINATOR
            if (r6 == r11) goto L_0x00cb
            com.google.zxing.qrcode.decoder.Mode r11 = com.google.zxing.qrcode.decoder.Mode.FNC1_FIRST_POSITION
            if (r6 == r11) goto L_0x00c6
            com.google.zxing.qrcode.decoder.Mode r11 = com.google.zxing.qrcode.decoder.Mode.FNC1_SECOND_POSITION
            if (r6 != r11) goto L_0x0041
            goto L_0x00c6
        L_0x0041:
            com.google.zxing.qrcode.decoder.Mode r11 = com.google.zxing.qrcode.decoder.Mode.STRUCTURED_APPEND
            if (r6 != r11) goto L_0x0062
            int r10 = r8.available()
            r11 = 16
            if (r10 < r11) goto L_0x005d
            r10 = 8
            int r11 = r8.readBits(r10)
            r2 = r11
            int r10 = r8.readBits(r10)
            r3 = r10
            r10 = r2
            r11 = r3
            goto L_0x00cd
        L_0x005d:
            com.google.zxing.FormatException r0 = com.google.zxing.FormatException.getFormatInstance()
            throw r0
        L_0x0062:
            com.google.zxing.qrcode.decoder.Mode r11 = com.google.zxing.qrcode.decoder.Mode.ECI
            if (r6 != r11) goto L_0x007a
            int r10 = parseECIValue(r8)
            com.google.zxing.common.CharacterSetECI r11 = com.google.zxing.common.CharacterSetECI.getCharacterSetECIByValue(r10)
            r4 = r11
            if (r4 == 0) goto L_0x0075
            r10 = r2
            r11 = r3
            goto L_0x00cd
        L_0x0075:
            com.google.zxing.FormatException r0 = com.google.zxing.FormatException.getFormatInstance()
            throw r0
        L_0x007a:
            com.google.zxing.qrcode.decoder.Mode r11 = com.google.zxing.qrcode.decoder.Mode.HANZI
            if (r6 != r11) goto L_0x0090
            int r10 = r8.readBits(r10)
            int r11 = r6.getCharacterCountBits(r1)
            int r11 = r8.readBits(r11)
            if (r10 != r0) goto L_0x008f
            decodeHanziSegment(r8, r7, r11)
        L_0x008f:
            goto L_0x00cb
        L_0x0090:
            int r10 = r6.getCharacterCountBits(r1)
            int r10 = r8.readBits(r10)
            r15 = r10
            com.google.zxing.qrcode.decoder.Mode r10 = com.google.zxing.qrcode.decoder.Mode.NUMERIC
            if (r6 != r10) goto L_0x00a1
            decodeNumericSegment(r8, r7, r15)
            goto L_0x00cb
        L_0x00a1:
            com.google.zxing.qrcode.decoder.Mode r10 = com.google.zxing.qrcode.decoder.Mode.ALPHANUMERIC
            if (r6 != r10) goto L_0x00a9
            decodeAlphanumericSegment(r8, r7, r15, r5)
            goto L_0x00cb
        L_0x00a9:
            com.google.zxing.qrcode.decoder.Mode r10 = com.google.zxing.qrcode.decoder.Mode.BYTE
            if (r6 != r10) goto L_0x00b8
            r10 = r8
            r11 = r7
            r12 = r15
            r13 = r4
            r0 = r15
            r15 = r19
            decodeByteSegment(r10, r11, r12, r13, r14, r15)
            goto L_0x00cb
        L_0x00b8:
            r0 = r15
            com.google.zxing.qrcode.decoder.Mode r10 = com.google.zxing.qrcode.decoder.Mode.KANJI
            if (r6 != r10) goto L_0x00c1
            decodeKanjiSegment(r8, r7, r0)
            goto L_0x00cb
        L_0x00c1:
            com.google.zxing.FormatException r10 = com.google.zxing.FormatException.getFormatInstance()
            throw r10
        L_0x00c6:
            r0 = 1
            r5 = r0
            r10 = r2
            r11 = r3
            goto L_0x00cd
        L_0x00cb:
            r10 = r2
            r11 = r3
        L_0x00cd:
            com.google.zxing.qrcode.decoder.Mode r0 = com.google.zxing.qrcode.decoder.Mode.TERMINATOR     // Catch:{ IllegalArgumentException -> 0x00fd }
            if (r6 != r0) goto L_0x00f6
            com.google.zxing.common.DecoderResult r0 = new com.google.zxing.common.DecoderResult
            java.lang.String r4 = r7.toString()
            boolean r2 = r14.isEmpty()
            r3 = 0
            if (r2 == 0) goto L_0x00e1
            r5 = r3
            goto L_0x00e2
        L_0x00e1:
            r5 = r14
        L_0x00e2:
            if (r18 != 0) goto L_0x00e6
            r6 = r3
            goto L_0x00eb
        L_0x00e6:
            java.lang.String r2 = r18.toString()
            r6 = r2
        L_0x00eb:
            r2 = r0
            r3 = r16
            r12 = r7
            r7 = r10
            r13 = r8
            r8 = r11
            r2.<init>(r3, r4, r5, r6, r7, r8)
            return r0
        L_0x00f6:
            r12 = r7
            r13 = r8
            r2 = r10
            r3 = r11
            r0 = 1
            goto L_0x001c
        L_0x00fd:
            r0 = move-exception
            r12 = r7
            r13 = r8
            r2 = r10
            r3 = r11
            goto L_0x0106
        L_0x0103:
            r0 = move-exception
            r12 = r7
            r13 = r8
        L_0x0106:
            com.google.zxing.FormatException r4 = com.google.zxing.FormatException.getFormatInstance()
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.decoder.DecodedBitStreamParser.decode(byte[], com.google.zxing.qrcode.decoder.Version, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel, java.util.Map):com.google.zxing.common.DecoderResult");
    }

    private static void decodeHanziSegment(BitSource bits, StringBuilder result, int count) throws FormatException {
        int i;
        if (count * 13 <= bits.available()) {
            byte[] buffer = new byte[(count * 2)];
            int offset = 0;
            while (count > 0) {
                int twoBytes = bits.readBits(13);
                int assembledTwoBytes = ((twoBytes / 96) << 8) | (twoBytes % 96);
                if (assembledTwoBytes < 959) {
                    i = 41377;
                } else {
                    i = 42657;
                }
                int assembledTwoBytes2 = assembledTwoBytes + i;
                buffer[offset] = (byte) ((assembledTwoBytes2 >> 8) & 255);
                buffer[offset + 1] = (byte) (assembledTwoBytes2 & 255);
                offset += 2;
                count--;
            }
            try {
                result.append(new String(buffer, StringUtils.GB2312));
            } catch (UnsupportedEncodingException e) {
                throw FormatException.getFormatInstance();
            }
        } else {
            throw FormatException.getFormatInstance();
        }
    }

    private static void decodeKanjiSegment(BitSource bits, StringBuilder result, int count) throws FormatException {
        int i;
        if (count * 13 <= bits.available()) {
            byte[] buffer = new byte[(count * 2)];
            int offset = 0;
            while (count > 0) {
                int twoBytes = bits.readBits(13);
                int assembledTwoBytes = ((twoBytes / 192) << 8) | (twoBytes % 192);
                if (assembledTwoBytes < 7936) {
                    i = 33088;
                } else {
                    i = 49472;
                }
                int assembledTwoBytes2 = assembledTwoBytes + i;
                buffer[offset] = (byte) (assembledTwoBytes2 >> 8);
                buffer[offset + 1] = (byte) assembledTwoBytes2;
                offset += 2;
                count--;
            }
            try {
                result.append(new String(buffer, StringUtils.SHIFT_JIS));
            } catch (UnsupportedEncodingException e) {
                throw FormatException.getFormatInstance();
            }
        } else {
            throw FormatException.getFormatInstance();
        }
    }

    private static void decodeByteSegment(BitSource bits, StringBuilder result, int count, CharacterSetECI currentCharacterSetECI, Collection<byte[]> byteSegments, Map<DecodeHintType, ?> hints) throws FormatException {
        String encoding;
        if (count * 8 <= bits.available()) {
            byte[] readBytes = new byte[count];
            for (int i = 0; i < count; i++) {
                readBytes[i] = (byte) bits.readBits(8);
            }
            if (currentCharacterSetECI == null) {
                encoding = StringUtils.guessEncoding(readBytes, hints);
            } else {
                encoding = currentCharacterSetECI.name();
            }
            try {
                result.append(new String(readBytes, encoding));
                byteSegments.add(readBytes);
            } catch (UnsupportedEncodingException e) {
                throw FormatException.getFormatInstance();
            }
        } else {
            throw FormatException.getFormatInstance();
        }
    }

    private static char toAlphaNumericChar(int value) throws FormatException {
        char[] cArr = ALPHANUMERIC_CHARS;
        if (value < cArr.length) {
            return cArr[value];
        }
        throw FormatException.getFormatInstance();
    }

    private static void decodeAlphanumericSegment(BitSource bits, StringBuilder result, int count, boolean fc1InEffect) throws FormatException {
        int start = result.length();
        while (count > 1) {
            if (bits.available() >= 11) {
                int nextTwoCharsBits = bits.readBits(11);
                result.append(toAlphaNumericChar(nextTwoCharsBits / 45));
                result.append(toAlphaNumericChar(nextTwoCharsBits % 45));
                count -= 2;
            } else {
                throw FormatException.getFormatInstance();
            }
        }
        if (count == 1) {
            if (bits.available() >= 6) {
                result.append(toAlphaNumericChar(bits.readBits(6)));
            } else {
                throw FormatException.getFormatInstance();
            }
        }
        if (fc1InEffect) {
            for (int i = start; i < result.length(); i++) {
                if (result.charAt(i) == '%') {
                    if (i >= result.length() - 1 || result.charAt(i + 1) != '%') {
                        result.setCharAt(i, 29);
                    } else {
                        result.deleteCharAt(i + 1);
                    }
                }
            }
        }
    }

    private static void decodeNumericSegment(BitSource bits, StringBuilder result, int count) throws FormatException {
        while (count >= 3) {
            if (bits.available() >= 10) {
                int threeDigitsBits = bits.readBits(10);
                if (threeDigitsBits < 1000) {
                    result.append(toAlphaNumericChar(threeDigitsBits / 100));
                    result.append(toAlphaNumericChar((threeDigitsBits / 10) % 10));
                    result.append(toAlphaNumericChar(threeDigitsBits % 10));
                    count -= 3;
                } else {
                    throw FormatException.getFormatInstance();
                }
            } else {
                throw FormatException.getFormatInstance();
            }
        }
        if (count == 2) {
            if (bits.available() >= 7) {
                int twoDigitsBits = bits.readBits(7);
                if (twoDigitsBits < 100) {
                    result.append(toAlphaNumericChar(twoDigitsBits / 10));
                    result.append(toAlphaNumericChar(twoDigitsBits % 10));
                    return;
                }
                throw FormatException.getFormatInstance();
            }
            throw FormatException.getFormatInstance();
        } else if (count != 1) {
        } else {
            if (bits.available() >= 4) {
                int digitBits = bits.readBits(4);
                if (digitBits < 10) {
                    result.append(toAlphaNumericChar(digitBits));
                    return;
                }
                throw FormatException.getFormatInstance();
            }
            throw FormatException.getFormatInstance();
        }
    }

    private static int parseECIValue(BitSource bits) throws FormatException {
        int firstByte = bits.readBits(8);
        if ((firstByte & 128) == 0) {
            return firstByte & 127;
        }
        if ((firstByte & 192) == 128) {
            return ((firstByte & 63) << 8) | bits.readBits(8);
        } else if ((firstByte & 224) == 192) {
            return ((firstByte & 31) << 16) | bits.readBits(16);
        } else {
            throw FormatException.getFormatInstance();
        }
    }
}
