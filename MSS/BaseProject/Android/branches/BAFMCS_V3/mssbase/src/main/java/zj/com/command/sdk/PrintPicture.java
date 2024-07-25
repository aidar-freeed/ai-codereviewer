package zj.com.command.sdk;

import android.graphics.Bitmap;

import zj.com.customize.sdk.Other;

public class PrintPicture {

    /**
     * æ‰“å�°ä½�å›¾å‡½æ•°
     * æ­¤å‡½æ•°æ˜¯å°†ä¸€è¡Œä½œä¸ºä¸€ä¸ªå›¾ç‰‡æ‰“å�°ï¼Œè¿™æ ·å¤„ç�†ä¸�å®¹æ˜“å‡ºé�?™
     *
     * @param mBitmap
     * @param nWidth
     * @param nMode
     * @return
     */
    public static byte[] POS_PrintBMP(Bitmap mBitmap, int nWidth, int nMode) {
        // å…ˆè½¬é»‘ç™½ï¼Œå†�è°ƒç�?¨å‡½æ•°ç¼©æ�?¾ä½�å›¾
        int width = ((nWidth + 7) / 8) * 8;
        int height = mBitmap.getHeight() * width / mBitmap.getWidth();
        height = ((height + 7) / 8) * 8;

        Bitmap rszBitmap = mBitmap;
        if (mBitmap.getWidth() != width) {
            rszBitmap = Other.resizeImage(mBitmap, width, height);
        }

        Bitmap grayBitmap = Other.toGrayscale(rszBitmap);

        byte[] dithered = Other.thresholdToBWPic(grayBitmap);

        byte[] data = Other.eachLinePixToCmd(dithered, width, nMode);

        return data;
    }

    /**
     * ä½¿ç�?¨ä¸‹ä¼ ä½�å›¾æ‰“å�°å›¾ç‰‡
     * å…ˆæ�?¶å®Œå†�æ‰“å�°
     *
     * @param bmp
     * @return
     */
    public static byte[] Print_1D2A(Bitmap bmp) {

			/*
			 * ä½¿ç�?¨ä¸‹ä¼ ä½�å›¾æ‰“å�°å›¾ç‰‡
			 * å…ˆæ�?¶å®Œå†�æ‰“å�°
			 */
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        byte data[] = new byte[1024 * 10];
        data[0] = 0x1D;
        data[1] = 0x2A;
        data[2] = (byte) ((width - 1) / 8 + 1);
        data[3] = (byte) ((height - 1) / 8 + 1);
        byte k = 0;
        int position = 4;
        int i;
        int j;
        byte temp = 0;
        for (i = 0; i < width; i++) {

            System.out.println("è¿›æ�¥äº†...I");
            for (j = 0; j < height; j++) {
                System.out.println("è¿›æ�¥äº†...J");
                if (bmp.getPixel(i, j) != -1) {
                    temp |= (0x80 >> k);
                } // end if
                k++;
                if (k == 8) {
                    data[position++] = temp;
                    temp = 0;
                    k = 0;
                } // end if k
            }// end for j
            if (k % 8 != 0) {
                data[position++] = temp;
                temp = 0;
                k = 0;
            }

        }
        System.out.println("data" + data);

        if (width % 8 != 0) {
            i = height / 8;
            if (height % 8 != 0) i++;
            j = 8 - (width % 8);
            for (k = 0; k < i * j; k++) {
                data[position++] = 0;
            }
        }
        return data;
    }

}
