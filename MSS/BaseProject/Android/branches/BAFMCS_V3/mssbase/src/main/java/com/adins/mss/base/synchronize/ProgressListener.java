package com.adins.mss.base.synchronize;

/**
 * Created by kusnendi.muhamad on 26/07/2017.
 */

public interface ProgressListener {
    public void onUpdatedValue(float value);

    public void onSyncScheme(boolean value);

    public void onSyncQuestion(boolean value);

    public void onSyncLookup(boolean value);

    public void onSyncHoliday(boolean value);

    public void onSyncPaymentChannel(boolean value);
}
