package com.adins.mss.foundation.camera2;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.util.LocaleHelper;

import java.util.Locale;

/**
 * Created by ahmadkamilalmasyhur on 25/01/2018.
 */

public class Camera2BasicRealActivity extends Activity {
    public static final String PICTURE_WIDTH = "picture_width";
    public static final String PICTURE_HEIGHT = "picture_height";
    public static final String PICTURE_QUALITY = "picture_quality";
    public static final String PICTURE_URI = "picture_path";

    public static final int PICTURE_WIDHT_DEF = 1024;
    public static final int PICTURE_HEIGHT_DEF = 768;
    public static final int PICTURE_QUALITY_DEF = 70;

    private static int width;
    private static int height;
    private static int quality;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        Bundle bundle = getIntent().getExtras();

        if (getIntent().getExtras() != null) {
            width = getIntent().getExtras().getInt(PICTURE_WIDTH);
            height = getIntent().getExtras().getInt(PICTURE_HEIGHT);
            quality = getIntent().getExtras().getInt(PICTURE_QUALITY);
        }

        if (width <= 0) width = PICTURE_WIDHT_DEF;
        if (height <= 0) height = PICTURE_HEIGHT_DEF;
        if (quality <= 0) quality = PICTURE_QUALITY_DEF;

        Camera2BasicActivity camera2BasicActivity = Camera2BasicActivity.newInstance();
        camera2BasicActivity.setArguments(bundle);

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, camera2BasicActivity)
                    .commit();
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }
}
