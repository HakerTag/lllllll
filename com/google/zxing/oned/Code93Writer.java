package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Map;

public class Code93Writer extends OneDimensionalCodeWriter {
    @Override // com.google.zxing.oned.OneDimensionalCodeWriter, com.google.zxing.Writer
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
        if (format == BarcodeFormat.CODE_93) {
            return super.encode(contents, format, width, height, hints);
        }
        throw new IllegalArgumentException("Can only encode CODE_93, but got " + format);
    }

    @Override // com.google.zxing.oned.OneDimensionalCodeWriter
    public boolean[] encode(String contents) {
        int length = contents.length();
        if (length <= 80) {
            int[] widths = new int[9];
            boolean[] result = new boolean[(((contents.length() + 2 + 2) * 9) + 1)];
            toIntArray(Code93Reader.CHARACTER_ENCODINGS[47], widths);
            int pos = appendPattern(result, 0, widths, true);
            for (int i = 0; i < length; i++) {
                toIntArray(Code93Reader.CHARACTER_ENCODINGS["0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".indexOf(contents.charAt(i))], widths);
                pos += appendPattern(result, pos, widths, true);
            }
            int check1 = computeChecksumIndex(contents, 20);
            toIntArray(Code93Reader.CHARACTER_ENCODINGS[check1], widths);
            int pos2 = pos + appendPattern(result, pos, widths, true);
            toIntArray(Code93Reader.CHARACTER_ENCODINGS[computeChecksumIndex(contents + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".charAt(check1), 15)], widths);
            int pos3 = pos2 + appendPattern(result, pos2, widths, true);
            toIntArray(Code93Reader.CHARACTER_ENCODINGS[47], widths);
            result[pos3 + appendPattern(result, pos3, widths, true)] = true;
            return result;
        }
        throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
    }

    private static void toIntArray(int a, int[] toReturn) {
        for (int i = 0; i < 9; i++) {
            int i2 = 1;
            if (((1 << (8 - i)) & a) == 0) {
                i2 = 0;
            }
            toReturn[i] = i2;
        }
    }

    protected static int appendPattern(boolean[] target, int pos, int[] pattern, boolean startColor) {
        int length = pattern.length;
        int i = 0;
        while (i < length) {
            int pos2 = pos + 1;
            target[pos] = pattern[i] != 0;
            i++;
            pos = pos2;
        }
        return 9;
    }

    private static int computeChecksumIndex(String contents, int maxWeight) {
        int weight = 1;
        int total = 0;
        for (int i = contents.length() - 1; i >= 0; i--) {
            total += "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".indexOf(contents.charAt(i)) * weight;
            weight++;
            if (weight > maxWeight) {
                weight = 1;
            }
        }
        return total % 47;
    }
}