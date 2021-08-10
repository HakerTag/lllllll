package com.google.zxing.qrcode.decoder;

public enum ErrorCorrectionLevel {
    L(1),
    M(0),
    Q(3),
    H(2);
    
    private static final ErrorCorrectionLevel[] FOR_BITS;
    private final int bits;

    static {
        ErrorCorrectionLevel errorCorrectionLevel;
        ErrorCorrectionLevel errorCorrectionLevel2 = L;
        FOR_BITS = new ErrorCorrectionLevel[]{M, errorCorrectionLevel2, errorCorrectionLevel, Q};
    }

    private ErrorCorrectionLevel(int bits2) {
        this.bits = bits2;
    }

    public int getBits() {
        return this.bits;
    }

    public static ErrorCorrectionLevel forBits(int bits2) {
        if (bits2 >= 0) {
            ErrorCorrectionLevel[] errorCorrectionLevelArr = FOR_BITS;
            if (bits2 < errorCorrectionLevelArr.length) {
                return errorCorrectionLevelArr[bits2];
            }
        }
        throw new IllegalArgumentException();
    }
}
