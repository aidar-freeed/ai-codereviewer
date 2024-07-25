package com.adins.mss.foundation.image;

import android.content.Context;
import android.widget.Toast;

import java.io.File;

public class FileCache {

    private File cacheDir;
    private Context context;

    public FileCache(Context context) {
        //Find the dir to save cached images
        this.context = context;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(context.getExternalFilesDir(null), "TempImages");
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());
        return new File(cacheDir, filename);

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;

        boolean deleteResult;
        for (File f : files){
             deleteResult = f.delete();
             if(!deleteResult){
                 Toast.makeText(context, "Failed to delete files", Toast.LENGTH_SHORT).show();
                 break;
             }
        }
    }

}