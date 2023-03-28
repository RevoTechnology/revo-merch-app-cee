package merchant.mokka.utils.barcode;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.EnumMap;
import java.util.Map;

public class BarcodeBitmapGenerator {
    private static final String TAG = BarcodeBitmapGenerator.class.getSimpleName();

    public Bitmap generateCode128(BarcodeRequest barcodeRequest) {
        Code128Writer multiFormatWriter = new Code128Writer();
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, barcodeRequest.getCharacterSet());
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(
                    barcodeRequest.getBarcodeText(),
                    barcodeRequest.getBarcodeFormat(),
                    barcodeRequest.getWidth(),
                    barcodeRequest.getHeight(),
                    hints);
            return convertBitMatrixToBitmap(bitMatrix, barcodeRequest.getForegroundColor(), barcodeRequest.getBackgroundColor());
        } catch (WriterException e) {
            Log.e(TAG, "Caught com.google.zxing.WriterException", e);
        }
        return null;
    }

    private Bitmap convertBitMatrixToBitmap(BitMatrix bitMatrix, int foregroundColor, int backgroundColor) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] =  bitMatrix.get(x, y) ? foregroundColor : backgroundColor;
            }
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }
}
