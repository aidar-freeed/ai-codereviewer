package com.adins.mss.base.syncfile;

import android.content.Context;

import com.adins.mss.dao.MobileDataFile;

/**
 * Created by loise on 10/18/2017.
 */

/**
 * class for holding parameters to pass in asynctask for db import process
 */
public class ImportDbParams {
    public static Context context;
    MobileDataFile metadata;
    String message;

    public ImportDbParams(Context context, String message, MobileDataFile metadata) {
        this.context = context;
        this.metadata = metadata;
        this.message = message;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MobileDataFile getMetadata() {
        return metadata;
    }

    public void setMetadata(MobileDataFile metadata) {
        this.metadata = metadata;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
