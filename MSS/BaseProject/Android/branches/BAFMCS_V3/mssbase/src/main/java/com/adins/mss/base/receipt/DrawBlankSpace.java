package com.adins.mss.base.receipt;

import android.graphics.Canvas;

/**
 * Created by Loise on 12/04/2018.
 */

/**
 * Mengenerate gambar bitmap kosong
 */
public class DrawBlankSpace implements IDrawItem {

    private int blankSpace;

    /**
     * Konstruktor
     * @param blankSpace tinggi bitmap kosong dalam pixel
     */
    public DrawBlankSpace(int blankSpace) {
        this.blankSpace = blankSpace;
    }

    /**
     * Menggambar pada canvas
     * @param canvas Objek Canvas
     * @param x titik awal pada sumbu x
     * @param y titik awal pada sumbu y
     */
    @Override
    public void drawOnCanvas(Canvas canvas, float x, float y) {
    }

    @Override
    public int getHeight() {
        return blankSpace;
    }
}
