package com.adins.mss.base.timeline;

import android.graphics.Bitmap;

import com.adins.mss.dao.Timeline;

import java.util.List;

/**
 * Created by kusnendi.muhamad on 26/07/2017.
 */

public interface TimelineListener {
    public void onSuccessBackgroundTask(List<Timeline> timelines);

    public void onSuccessImageBitmap(Bitmap bitmap, int imageViewId, int defaultImage);
}
