package com.adins.mss.base.timeline;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.adins.mss.dao.Timeline;

import java.util.List;

/**
 * Created by kusnendi.muhamad on 26/07/2017.
 */

public interface TimelineInterface {
    public AsyncTask<Void, Void, List<Timeline>> refreshBackgroundTask();

    public AsyncTask<Void, Void, Bitmap> refreshImageBitmap(int viewId, int defaultDrawable, byte[] byteImage);

    public List<Timeline> getTimelines();

    public void setCashOnHand();

    public boolean isCOHAktif();

    public double getLimit();

    public double getCashOnHand();

    public String getsCOH();

    public String getsLimit();

    public String getCoh();

    public String getCashLimit();

    public Bitmap getBitmap();
}
