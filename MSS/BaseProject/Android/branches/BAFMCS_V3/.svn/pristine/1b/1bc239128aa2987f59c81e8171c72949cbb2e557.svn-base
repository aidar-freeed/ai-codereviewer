package com.adins.mss.base.receipt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Loise on 12/04/2018.
 */

/**
 * Menggambar image bitmap
 */
public class DrawImage implements IDrawItem {
    private Paint paint = new Paint();
    private Bitmap bitmap;

    /**
     * Konstruktor
     * @param bitmap bitmap yang akan digambar
     */
    public DrawImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * Mengatur aligment gambar
     * @param align alignment gambar
     */
    public void setAlign(Paint.Align align) {
        paint.setTextAlign(align);
    }
    /**
     * Mengambil aligment gambar
     * @return alignment gambar
     */
    public Paint.Align getAlign() {
        return paint.getTextAlign();
    }

    /**
     * Menggambar bitmap pada canvas
     * @param canvas objek canvas
     * @param x titik x awal
     * @param y titik y awal
     */
    @Override
    public void drawOnCanvas(Canvas canvas, float x, float y) {
        canvas.drawBitmap(bitmap, getX(canvas, x), getY(y), paint);
    }

    /**
     * titik koordinat terakhir pada gambar  koordinat Y
     * @param y
     * @return
     */
    private float getY(float y) {
        float baseline = -paint.ascent();
        return baseline + y;
    }
    /**
     * titik koordinat terakhir pada gambar  koordinat x
     * @param x
     * @param canvas
     * @return
     */
    private float getX(Canvas canvas, float x) {
        float xPos = x;
        if (paint.getTextAlign().equals(Paint.Align.CENTER)) {
            xPos += (float)(canvas.getWidth() - bitmap.getWidth()) / 2;
        } else if (paint.getTextAlign().equals(Paint.Align.RIGHT)) {
            xPos += canvas.getWidth() - bitmap.getWidth();
        }
        return xPos;
    }

    /**
     * Mengambil tinggi gambar
     * @return tinggi bitmap
     */
    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }
}
