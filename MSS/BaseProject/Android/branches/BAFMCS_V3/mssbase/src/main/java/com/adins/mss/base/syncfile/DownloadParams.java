package com.adins.mss.base.syncfile;

import android.content.Context;

import com.adins.mss.dao.MobileDataFile;

/**
 * Created by loise on 10/13/2017.
 */

/**
 * class untuk menampung parameter untuk proses download file
 */
public class DownloadParams {

    Context context;
    String outputfilepath;
    MobileDataFile metadata;
    String message;

    public DownloadParams(String outputfilepath, Context context, String message, MobileDataFile metadata) {

        this.context = context;
        this.outputfilepath = outputfilepath;
        this.message = message;
        this.metadata = metadata;
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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getOutputfilepath() {
        return outputfilepath;
    }

    public void setOutputfilepath(String outputfilepath) {
        this.outputfilepath = outputfilepath;
    }
}