package com.adins.mss.base.receipt;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by Loise on 12/04/2018.
 */

/**
 * menggambar teks pada canvas
 */
public class DrawText implements IDrawItem {
    private Paint paint = new Paint();
    private String text;
    private boolean newLine;

    /**
     * konstruktor
     * @param text teks yang akan digambar
     */
    public DrawText(String text) {
        this.text = text;
    }

    /**
     * menggambarkan teks pada canvas
     * @param canvas
     * @param x
     * @param y
     */
    @Override
    public void drawOnCanvas(Canvas canvas, float x, float y) {
        canvas.drawText(text, getX(canvas, x), getY(y), paint);
    }

    /**
     * mengembalikan titik awal pada sumbu y
     * @param y
     * @return
     */
    private float getY(float y) {
        float baseline = -paint.ascent();
        return baseline + y;
    }

    /**
     * mengembalikan titik awal pada sumbu x
     * @param canvas
     * @param x
     * @return
     */
    private float getX(Canvas canvas, float x) {
        float xPos = x;
        if (paint.getTextAlign().equals(Paint.Align.CENTER)) {
            xPos = ((float)canvas.getWidth() / 2);
        } else if (paint.getTextAlign().equals(Paint.Align.RIGHT)) {
            xPos = canvas.getWidth();
        }
        return xPos;
    }

    /**
     * mengembalikan tinggi objek teks
     * @return
     */
    @Override
    public int getHeight() {
        return (newLine ? (int) getTextSize() : 0);
    }

    /**
     * mengembalikan teks
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * setter teks
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * mengembalikan warna teks
     * @return
     */
    public int getColor() {
        return paint.getColor();
    }

    /**
     * mengubah warna teks
     * @param color
     */
    public void setColor(int color) {
        paint.setColor(color);
    }

    /**
     * mengembalikan ukuran teks
     * @return
     */
    public float getTextSize() {
        return paint.getTextSize();
    }

    /**
     * mengubah ukuran teks
     * @param textSize
     */
    public void setTextSize(float textSize) {
        paint.setTextSize(textSize);
    }

    /**
     * mengembalikan font teks
     */
    public void getTypeface() {
        paint.getTypeface();
    }

    /**
     * mengubah font teks
     * @param typeface Typeface
     */
    public void setTypeface(Typeface typeface) {
        paint.setTypeface(typeface);
    }

    /**
     * mengubah akignment teks
     * @param align
     */
    public void setAlign(Paint.Align align) {
        paint.setTextAlign(align);
    }

    /**
     * mengembalikan alignment teks
     * @return
     */
    public Paint.Align getAlign() {
        return paint.getTextAlign();
    }

    /**
     * menentukan apakah teks akan pindah baris atau tidak
     * @param newLine
     */
    public void setNewLine(boolean newLine) {
        this.newLine = newLine;
    }

    /**
     * mengembalikan status teks pindah baris atau tidak
     * @return
     */
    public boolean getNewLine() {
        return newLine;
    }
}
