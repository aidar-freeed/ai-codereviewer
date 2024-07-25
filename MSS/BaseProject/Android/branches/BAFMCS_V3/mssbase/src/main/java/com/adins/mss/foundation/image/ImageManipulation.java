package com.adins.mss.foundation.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * @author gigin.ginanjar
 */
public class ImageManipulation {

    public static Bitmap resizeBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    /**
     * Create Watermark on Left Bottom Of Bitmap
     *
     * @param src       - Bitmap
     * @param watermark - watermark String
     * @param color     - int color
     * @param alpha     - int
     * @param size      - font size
     * @param underline - Boolean
     * @return Bitmap
     */
    public static Bitmap waterMark(Bitmap src, String watermark, int color, int alpha, int size, boolean underline) {
        //get source image width and height
        int w = src.getWidth();
        int h = src.getHeight();

        //set position watermark (Left-Bottom)
        int x = 10;
        int y = h - 10;

        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        //create canvas object
        Canvas canvas = new Canvas(result);
        //draw bitmap on canvas
        canvas.drawBitmap(src, 0, 0, null);
        //create paint object
        Paint paint = new Paint();
        //apply color
        paint.setColor(color);
        //set transparency
        paint.setAlpha(alpha);
        //set text size
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        //set should be underlined or not
        paint.setUnderlineText(underline);
        //draw text on given location
        canvas.drawText(watermark, x, y, paint);

        return result;
    }

    /**
     * Rotate Image
     *
     * @param src    - Bitmap
     * @param degree - degree
     * @return Bitmap
     */
    public static Bitmap rotateImage(Bitmap src, int degree) {
        //get source image width and height
        int w = src.getWidth();
        int h = src.getHeight();

        //create canvas object
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap result = Bitmap.createBitmap(src, 0, 0, w, h, matrix, true);

        return result;
    }

}
