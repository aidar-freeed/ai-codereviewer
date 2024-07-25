/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
 ***/


package com.adins.mss.foundation.image;

import android.os.Build;
import android.view.MotionEvent;

import com.adins.mss.foundation.camerainapp.helper.Logger;

public class WrapMotionEvent {
    protected MotionEvent event;


    protected WrapMotionEvent(MotionEvent event) {
        this.event = event;
    }

    static public WrapMotionEvent wrap(MotionEvent event) {
        // Use Build.VERSION.SDK_INT if you don't have to support Cupcake
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            Logger.d("WrapMotionEvent", "Using Eclair version");
            return new EclairMotionEvent(event);
        } else {
            Logger.d("WrapMotionEvent", "Using Cupcake/Donut version");
            return new WrapMotionEvent(event);
        }
    }


    public int getAction() {
        return event.getAction();
    }

    public float getX() {
        return event.getX();
    }

    public float getX(int pointerIndex) {
        verifyPointerIndex(pointerIndex);
        return getX();
    }

    public float getY() {
        return event.getY();
    }

    public float getY(int pointerIndex) {
        verifyPointerIndex(pointerIndex);
        return getY();
    }

    public int getPointerCount() {
        return 1;
    }

    public int getPointerId(int pointerIndex) {
        verifyPointerIndex(pointerIndex);
        return 0;
    }

    private void verifyPointerIndex(int pointerIndex) {
        if (pointerIndex > 0) {
            throw new IllegalArgumentException(
                    "Invalid pointer index for Donut/Cupcake");
        }
    }

}

