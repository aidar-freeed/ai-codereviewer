package com.adins.mss.base.receipt;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Loise on 12/04/2018.
 */

/**
 * Menggambar garis
 */
public class DrawLine implements IDrawItem {
    private Paint paint = new Paint();
    private int size;

    /**
     * Konstruktor
     * @param size panjang garis
     */
    public DrawLine(int size) {
        this.size = size;
    }

    public DrawLine(int size, int weight) {
        this.size = size;
        this.paint.setStrokeWidth(weight);
    }

    /**
     * Menggambar garis pada canvas
     * @param canvas
     * @param x
     * @param y
     */
    @Override
    public void drawOnCanvas(Canvas canvas, float x, float y) {
        float xPos = getX(canvas, x);
        canvas.drawLine(xPos, y + 5, xPos + size, y + 5, paint);
    }

    /**
     * mengambil posisi awal untuk menggambar pada sumbu x
     * @param canvas
     * @param x
     * @return
     */
    private float getX(Canvas canvas, float x) {
        float xPos = x;
        if (paint.getTextAlign().equals(Paint.Align.CENTER)) {
            xPos += (float)(canvas.getWidth() - size) / 2;
        } else if (paint.getTextAlign().equals(Paint.Align.RIGHT)) {
            xPos += canvas.getWidth() - size;
        }
        return xPos;
    }

    /**
     * mengembalikan tinggi garis
     * @return
     */
    @Override
    public int getHeight() {
        return 6;
    }

    /**
     * mangambil warna garis
     * @return warna garis
     */
    public int getColor() {
        return paint.getColor();
    }

    /**
     * mengubah warna garis
     * @param color warna garis yang diinginkan
     */
    public void setColor(int color) {
        paint.setColor(color);
    }

    /**
     * mengubah alignment garis
     * @param align
     */
    public void setAlign(Paint.Align align) {
        paint.setTextAlign(align);
    }

    /**
     * mengembalikan alignment garis
     * @return alignment
     */
    public Paint.Align getAlign() {
        return paint.getTextAlign();
    }

}
