package com.adins.mss.foundation.image;

/**
 * Created by gigin.ginanjar on 31/03/2016.
 */
public class ExifData {
    public int orientation;
    public int width;
    public int height;

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
