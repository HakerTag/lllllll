package com.google.zxing.maxicode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.maxicode.decoder.Decoder;
import java.util.Map;

public final class MaxiCodeReader implements Reader {
    private static final int MATRIX_HEIGHT = 33;
    private static final int MATRIX_WIDTH = 30;
    private static final ResultPoint[] NO_POINTS = new ResultPoint[0];
    private final Decoder decoder = new Decoder();

    @Override // com.google.zxing.Reader
    public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
        return decode(image, null);
    }

    /* JADX INFO: Multiple debug info for r0v5 com.google.zxing.common.DecoderResult: [D('bits' com.google.zxing.common.BitMatrix), D('decoderResult' com.google.zxing.common.DecoderResult)] */
    @Override // com.google.zxing.Reader
    public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
        if (hints == null || !hints.containsKey(DecodeHintType.PURE_BARCODE)) {
            throw NotFoundException.getNotFoundInstance();
        }
        DecoderResult decoderResult = this.decoder.decode(extractPureBits(image.getBlackMatrix()), hints);
        Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), NO_POINTS, BarcodeFormat.MAXICODE);
        String ecLevel = decoderResult.getECLevel();
        if (ecLevel != null) {
            result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
        }
        return result;
    }

    @Override // com.google.zxing.Reader
    public void reset() {
    }

    private static BitMatrix extractPureBits(BitMatrix image) throws NotFoundException {
        int[] enclosingRectangle = image.getEnclosingRectangle();
        if (enclosingRectangle != null) {
            int left = enclosingRectangle[0];
            int top = enclosingRectangle[1];
            int width = enclosingRectangle[2];
            int height = enclosingRectangle[3];
            BitMatrix bits = new BitMatrix(MATRIX_WIDTH, 33);
            for (int y = 0; y < 33; y++) {
                int iy = (((y * height) + (height / 2)) / 33) + top;
                for (int x = 0; x < MATRIX_WIDTH; x++) {
                    if (image.get(((((x * width) + (width / 2)) + (((y & 1) * width) / 2)) / MATRIX_WIDTH) + left, iy)) {
                        bits.set(x, y);
                    }
                }
            }
            return bits;
        }
        throw NotFoundException.getNotFoundInstance();
    }
}
