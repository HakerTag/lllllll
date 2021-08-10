package com.google.zxing.pdf417.encoder;

import com.google.zxing.WriterException;
import com.google.zxing.common.CharacterSetECI;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;

/* access modifiers changed from: package-private */
public final class PDF417HighLevelEncoder {
    private static final int BYTE_COMPACTION = 1;
    private static final Charset DEFAULT_ENCODING = Charset.forName("ISO-8859-1");
    private static final int ECI_CHARSET = 927;
    private static final int ECI_GENERAL_PURPOSE = 926;
    private static final int ECI_USER_DEFINED = 925;
    private static final int LATCH_TO_BYTE = 924;
    private static final int LATCH_TO_BYTE_PADDED = 901;
    private static final int LATCH_TO_NUMERIC = 902;
    private static final int LATCH_TO_TEXT = 900;
    private static final byte[] MIXED = new byte[128];
    private static final int NUMERIC_COMPACTION = 2;
    private static final byte[] PUNCTUATION = new byte[128];
    private static final int SHIFT_TO_BYTE = 913;
    private static final int SUBMODE_ALPHA = 0;
    private static final int SUBMODE_LOWER = 1;
    private static final int SUBMODE_MIXED = 2;
    private static final int SUBMODE_PUNCTUATION = 3;
    private static final int TEXT_COMPACTION = 0;
    private static final byte[] TEXT_MIXED_RAW = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 38, 13, 9, 44, 58, 35, 45, 46, 36, 47, 43, 37, 42, 61, 94, 0, 32, 0, 0, 0};
    private static final byte[] TEXT_PUNCTUATION_RAW = {59, 60, 62, 64, 91, 92, 93, 95, 96, 126, 33, 13, 9, 44, 58, 10, 45, 46, 36, 47, 34, 124, 42, 40, 41, 63, 123, 125, 39, 0};

    static {
        Arrays.fill(MIXED, (byte) -1);
        int i = 0;
        while (true) {
            byte[] bArr = TEXT_MIXED_RAW;
            if (i >= bArr.length) {
                break;
            }
            byte b = bArr[i];
            if (b > 0) {
                MIXED[b] = (byte) i;
            }
            i++;
        }
        Arrays.fill(PUNCTUATION, (byte) -1);
        int i2 = 0;
        while (true) {
            byte[] bArr2 = TEXT_PUNCTUATION_RAW;
            if (i2 < bArr2.length) {
                byte b2 = bArr2[i2];
                if (b2 > 0) {
                    PUNCTUATION[b2] = (byte) i2;
                }
                i2++;
            } else {
                return;
            }
        }
    }

    private PDF417HighLevelEncoder() {
    }

    static String encodeHighLevel(String msg, Compaction compaction, Charset encoding) throws WriterException {
        CharacterSetECI eci;
        StringBuilder sb = new StringBuilder(msg.length());
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        } else if (!DEFAULT_ENCODING.equals(encoding) && (eci = CharacterSetECI.getCharacterSetECIByName(encoding.name())) != null) {
            encodingECI(eci.getValue(), sb);
        }
        int len = msg.length();
        int p = 0;
        int textSubMode = 0;
        if (compaction == Compaction.TEXT) {
            encodeText(msg, 0, len, sb, 0);
        } else if (compaction == Compaction.BYTE) {
            byte[] bytes = msg.getBytes(encoding);
            encodeBinary(bytes, 0, bytes.length, 1, sb);
        } else if (compaction == Compaction.NUMERIC) {
            sb.append((char) 902);
            encodeNumeric(msg, 0, len, sb);
        } else {
            int encodingMode = 0;
            while (p < len) {
                int n = determineConsecutiveDigitCount(msg, p);
                if (n >= 13) {
                    sb.append((char) 902);
                    encodingMode = 2;
                    textSubMode = 0;
                    encodeNumeric(msg, p, n, sb);
                    p += n;
                } else {
                    int t = determineConsecutiveTextCount(msg, p);
                    if (t >= 5 || n == len) {
                        if (encodingMode != 0) {
                            sb.append((char) 900);
                            encodingMode = 0;
                            textSubMode = 0;
                        }
                        textSubMode = encodeText(msg, p, t, sb, textSubMode);
                        p += t;
                    } else {
                        int b = determineConsecutiveBinaryCount(msg, p, encoding);
                        if (b == 0) {
                            b = 1;
                        }
                        byte[] bytes2 = msg.substring(p, p + b).getBytes(encoding);
                        if (bytes2.length == 1 && encodingMode == 0) {
                            encodeBinary(bytes2, 0, 1, 0, sb);
                        } else {
                            encodeBinary(bytes2, 0, bytes2.length, encodingMode, sb);
                            encodingMode = 1;
                            textSubMode = 0;
                        }
                        p += b;
                    }
                }
            }
        }
        return sb.toString();
    }

    private static int encodeText(CharSequence msg, int startpos, int count, StringBuilder sb, int initialSubmode) {
        StringBuilder tmp = new StringBuilder(count);
        int submode = initialSubmode;
        int idx = 0;
        while (true) {
            char ch = msg.charAt(startpos + idx);
            if (submode != 0) {
                if (submode != 1) {
                    if (submode != 2) {
                        if (isPunctuation(ch)) {
                            tmp.append((char) PUNCTUATION[ch]);
                        } else {
                            submode = 0;
                            tmp.append((char) 29);
                        }
                    } else if (isMixed(ch)) {
                        tmp.append((char) MIXED[ch]);
                    } else if (isAlphaUpper(ch)) {
                        submode = 0;
                        tmp.append((char) 28);
                    } else if (isAlphaLower(ch)) {
                        submode = 1;
                        tmp.append((char) 27);
                    } else if (startpos + idx + 1 >= count || !isPunctuation(msg.charAt(startpos + idx + 1))) {
                        tmp.append((char) 29);
                        tmp.append((char) PUNCTUATION[ch]);
                    } else {
                        submode = 3;
                        tmp.append((char) 25);
                    }
                } else if (isAlphaLower(ch)) {
                    if (ch == ' ') {
                        tmp.append((char) 26);
                    } else {
                        tmp.append((char) (ch - 'a'));
                    }
                } else if (isAlphaUpper(ch)) {
                    tmp.append((char) 27);
                    tmp.append((char) (ch - 'A'));
                } else if (isMixed(ch)) {
                    submode = 2;
                    tmp.append((char) 28);
                } else {
                    tmp.append((char) 29);
                    tmp.append((char) PUNCTUATION[ch]);
                }
            } else if (isAlphaUpper(ch)) {
                if (ch == ' ') {
                    tmp.append((char) 26);
                } else {
                    tmp.append((char) (ch - 'A'));
                }
            } else if (isAlphaLower(ch)) {
                submode = 1;
                tmp.append((char) 27);
            } else if (isMixed(ch)) {
                submode = 2;
                tmp.append((char) 28);
            } else {
                tmp.append((char) 29);
                tmp.append((char) PUNCTUATION[ch]);
            }
            idx++;
            if (idx >= count) {
                break;
            }
        }
        char h = 0;
        int len = tmp.length();
        for (int i = 0; i < len; i++) {
            if (i % 2 != 0) {
                h = (char) ((h * 30) + tmp.charAt(i));
                sb.append(h);
            } else {
                h = tmp.charAt(i);
            }
        }
        if (len % 2 != 0) {
            sb.append((char) ((h * 30) + 29));
        }
        return submode;
    }

    private static void encodeBinary(byte[] bytes, int startpos, int count, int startmode, StringBuilder sb) {
        if (count == 1 && startmode == 0) {
            sb.append((char) 913);
        } else if (count % 6 == 0) {
            sb.append((char) 924);
        } else {
            sb.append((char) 901);
        }
        int idx = startpos;
        if (count >= 6) {
            char[] chars = new char[5];
            while ((startpos + count) - idx >= 6) {
                long t = 0;
                for (int i = 0; i < 6; i++) {
                    t = (t << 8) + ((long) (bytes[idx + i] & 255));
                }
                for (int i2 = 0; i2 < 5; i2++) {
                    chars[i2] = (char) ((int) (t % 900));
                    t /= 900;
                }
                for (int i3 = chars.length - 1; i3 >= 0; i3--) {
                    sb.append(chars[i3]);
                }
                idx += 6;
            }
        }
        for (int i4 = idx; i4 < startpos + count; i4++) {
            sb.append((char) (bytes[i4] & 255));
        }
    }

    private static void encodeNumeric(String msg, int startpos, int count, StringBuilder sb) {
        int idx = 0;
        StringBuilder tmp = new StringBuilder((count / 3) + 1);
        BigInteger num900 = BigInteger.valueOf(900);
        BigInteger num0 = BigInteger.valueOf(0);
        while (idx < count) {
            tmp.setLength(0);
            int len = Math.min(44, count - idx);
            BigInteger bigint = new BigInteger('1' + msg.substring(startpos + idx, startpos + idx + len));
            do {
                tmp.append((char) bigint.mod(num900).intValue());
                bigint = bigint.divide(num900);
            } while (!bigint.equals(num0));
            for (int i = tmp.length() - 1; i >= 0; i--) {
                sb.append(tmp.charAt(i));
            }
            idx += len;
        }
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isAlphaUpper(char ch) {
        return ch == ' ' || (ch >= 'A' && ch <= 'Z');
    }

    private static boolean isAlphaLower(char ch) {
        return ch == ' ' || (ch >= 'a' && ch <= 'z');
    }

    private static boolean isMixed(char ch) {
        return MIXED[ch] != -1;
    }

    private static boolean isPunctuation(char ch) {
        return PUNCTUATION[ch] != -1;
    }

    private static boolean isText(char ch) {
        return ch == '\t' || ch == '\n' || ch == '\r' || (ch >= ' ' && ch <= '~');
    }

    private static int determineConsecutiveDigitCount(CharSequence msg, int startpos) {
        int count = 0;
        int len = msg.length();
        int idx = startpos;
        if (idx < len) {
            char ch = msg.charAt(idx);
            while (isDigit(ch) && idx < len) {
                count++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
        }
        return count;
    }

    private static int determineConsecutiveTextCount(CharSequence msg, int startpos) {
        int len = msg.length();
        int idx = startpos;
        while (idx < len) {
            char ch = msg.charAt(idx);
            int numericCount = 0;
            while (numericCount < 13 && isDigit(ch) && idx < len) {
                numericCount++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
            if (numericCount >= 13) {
                return (idx - startpos) - numericCount;
            }
            if (numericCount <= 0) {
                if (!isText(msg.charAt(idx))) {
                    break;
                }
                idx++;
            }
        }
        return idx - startpos;
    }

    private static int determineConsecutiveBinaryCount(String msg, int startpos, Charset encoding) throws WriterException {
        int i;
        CharsetEncoder encoder = encoding.newEncoder();
        int len = msg.length();
        int idx = startpos;
        while (idx < len) {
            char ch = msg.charAt(idx);
            int numericCount = 0;
            while (numericCount < 13 && isDigit(ch) && (i = idx + (numericCount = numericCount + 1)) < len) {
                ch = msg.charAt(i);
            }
            if (numericCount >= 13) {
                return idx - startpos;
            }
            char ch2 = msg.charAt(idx);
            if (encoder.canEncode(ch2)) {
                idx++;
            } else {
                throw new WriterException("Non-encodable character detected: " + ch2 + " (Unicode: " + ((int) ch2) + ')');
            }
        }
        return idx - startpos;
    }

    private static void encodingECI(int eci, StringBuilder sb) throws WriterException {
        if (eci >= 0 && eci < LATCH_TO_TEXT) {
            sb.append((char) 927);
            sb.append((char) eci);
        } else if (eci < 810900) {
            sb.append((char) 926);
            sb.append((char) ((eci / LATCH_TO_TEXT) - 1));
            sb.append((char) (eci % LATCH_TO_TEXT));
        } else if (eci < 811800) {
            sb.append((char) 925);
            sb.append((char) (810900 - eci));
        } else {
            throw new WriterException("ECI number not in valid range from 0..811799, but was " + eci);
        }
    }
}
