package com.adins.mss.foundation.camerainapp.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.adins.mss.foundation.questiongenerator.QuestionViewGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by angga.permadi on 7/28/2016.
 */
public class FileUtil {

    public static File bitmapToFileConverter(Activity activity, Bitmap bitmap, File file) {
        try {
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 88, outStream);
            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File bitmapToFileConverter(Activity activity, Bitmap bitmap) {
        try {
            return bitmapToFileConverter(activity, bitmap, QuestionViewGenerator.createImageFile(activity.getApplicationContext()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
