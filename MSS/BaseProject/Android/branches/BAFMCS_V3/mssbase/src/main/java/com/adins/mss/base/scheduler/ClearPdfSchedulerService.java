package com.adins.mss.base.scheduler;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import com.adins.mss.constant.Global;

import java.io.File;

public class ClearPdfSchedulerService extends IntentService {

    public ClearPdfSchedulerService() {
        super("CleanPdfSchedulerService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/bafmcspdf";
        File dir = new File(dirPath);

        deleteDirectory(dir);
    }

    private void deleteDirectory(File path) {
        if( path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                if (Global.IS_DEV) {
                    Log.i("PDF_SCHEDULER", "No PDF found to delete!");
                }
                return;
            }
            for(File file : files) {
                if(file.isDirectory()) {
                    deleteDirectory(file);
                }
                else {
                    file.delete();
                    if (Global.IS_DEV) {
                        Log.i("PDF_SCHEDULER", "PDF deleted!");
                    }
                }
            }
        }
        if (path.exists()) {
            path.delete();
            if (Global.IS_DEV) {
                Log.i("PDF_SCHEDULER", "All PDF are cleared from local storage!");
            }
        }
    }

}
