package pl.revo.merchant.utils.barcode;

import android.text.TextUtils;

public class BarcodeRequest {

    private static final String DEFAULT_CHARACTERSET = "ISO-8859-1";
    private static final int DEFAULT_FOREGROUND_COLOR = 0xff000000; //Opaque black
    private static final int DEFAULT_BACKGROUND_COLOR = 0xffffffff; //Opaque white
    private static final BarcodeFormat DEFAULT_BARCODE_FORMAT = BarcodeFormat.QR_CODE;

    private final String mBarcodeText;
    private final BarcodeFormat mBarcodeFormat;
    private final int mWidth;
    private final int mHeight;
    private final String mCharacterSet;
    private final int mForegroundColor;
    private final int mBackgroundColor;

    private BarcodeRequest(String barcodeText, BarcodeFormat barcodeFormat, int width, int height, String characterSet, int foregroundColor, int backgroundColor) {
        mBarcodeText = barcodeText;
        mBarcodeFormat = barcodeFormat;
        mWidth = width;
        mHeight = height;
        mCharacterSet = characterSet;
        mForegroundColor = foregroundColor;
        mBackgroundColor = backgroundColor;
    }

    String getBarcodeText() {
        return mBarcodeText;
    }

    BarcodeFormat getBarcodeFormat() {
        return mBarcodeFormat;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    String getCharacterSet() {
        return mCharacterSet;
    }

    int getForegroundColor() {
        return mForegroundColor;
    }

    int getBackgroundColor() {
        return mBackgroundColor;
    }

    public static class BarcodeRequestBuilder {
        private String mBarcodeText;
        private BarcodeFormat mBarcodeFormat;
        private int mWidth;
        private int mHeight;
        private String mCharacterSet;
        private Integer mForegroundColor;
        private Integer mBackgroundColor;

        /**
         * Contents of the barcode, this must be compatible with the barcode format.
         */
        public BarcodeRequestBuilder barcodeText(String text) {
            mBarcodeText = text;
            return this;
        }

        /**
         * Format or type of barcode to be created, default {@link BarcodeRequest#DEFAULT_BARCODE_FORMAT}.
         */
        public BarcodeRequestBuilder barcodeFormat(BarcodeFormat format) {
            mBarcodeFormat = format;
            return this;
        }

        /**
         * Width of barcode to be created.
         */
        public BarcodeRequestBuilder width(int width) {
            mWidth = width;
            return this;
        }

        /**
         * Height of barcode to be created.
         */
        public BarcodeRequestBuilder height(int height) {
            mHeight = height;
            return this;
        }

        /**
         * Character set of the barcode text string, default {@link BarcodeRequest#DEFAULT_CHARACTERSET}.
         */
        public BarcodeRequestBuilder characterSet(String characterSet) {
            mCharacterSet = characterSet;
            return this;
        }

        /**
         * Sets the background color of the barcode, default {@link BarcodeRequest#DEFAULT_FOREGROUND_COLOR}.
         */
        public BarcodeRequestBuilder foregroundColor(int foregroundColor) {
            mForegroundColor = foregroundColor;
            return this;
        }

        /**
         * Sets the background color of the barcode, default {@link BarcodeRequest#DEFAULT_BACKGROUND_COLOR}.
         */
        public BarcodeRequestBuilder backgroundColor(int backgroundColor) {
            mBackgroundColor = backgroundColor;
            return this;
        }

        public BarcodeRequest build() {

            if (TextUtils.isEmpty(mBarcodeText)) {
                throw new IllegalArgumentException("Barcode text cannot be empty");
            }

            if (mBarcodeFormat == null) {
                mBarcodeFormat = DEFAULT_BARCODE_FORMAT;
            }

            if (mWidth <= 0 || mHeight <= 0) {
                throw new IllegalArgumentException("Width and height must be non-zero positive numbers");
            }

            if (TextUtils.isEmpty(mCharacterSet)) {
                mCharacterSet = DEFAULT_CHARACTERSET;
            }

            if (mForegroundColor == null) {
                mForegroundColor = DEFAULT_FOREGROUND_COLOR;
            }

            if (mBackgroundColor == null) {
                mBackgroundColor = DEFAULT_BACKGROUND_COLOR;
            }

            return new BarcodeRequest(mBarcodeText, mBarcodeFormat, mWidth, mHeight, mCharacterSet, mForegroundColor, mBackgroundColor);
        }
    }

}