package com.adins.mss.foundation.print;

import android.content.Context;
import android.content.SharedPreferences;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.logger.Logger;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by angga.permadi on 5/2/2016.
 */
public class CopyBitmapLogo {
    public static final int LOGO_VERSION = 1; // naikin kalau ada perubahan print logo
    public static final String PREF_BITMAP_PRINT = "pref_bitmap_print";
    public static final int PREF_BITMAP_PRINT_DEFAULT = 0;

    private Context context;

    public CopyBitmapLogo(Context context) {
        this.context = context;
    }

    public void copyLogoPrint() {

        final String DBDestination = "/data/data/" + context.getPackageName() + "/print_logo.bmp";

        try(OutputStream os = new FileOutputStream(DBDestination);
            InputStream is = context.getAssets().open("print_logo.bmp")) {
            SharedPreferences sharedPref = context.getSharedPreferences(
                    "GlobalData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            int logoVersion = sharedPref.getInt(PREF_BITMAP_PRINT, PREF_BITMAP_PRINT_DEFAULT);

            if (logoVersion < LOGO_VERSION) {
                Logger.d(this, "copy print logo from assets");

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();

                editor.putInt(PREF_BITMAP_PRINT, LOGO_VERSION);
                editor.apply();

                Logger.d(this, "Copy print logo from assets success");
            } else {
                Logger.d(this, "print logo already exist");
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }

    }


}
