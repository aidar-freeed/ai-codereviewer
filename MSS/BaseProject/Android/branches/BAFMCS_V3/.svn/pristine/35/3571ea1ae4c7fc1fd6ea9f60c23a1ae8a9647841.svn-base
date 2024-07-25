package com.adins.mss.base.dynamicform.form.questions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.foundation.image.ImageManipulation;
import com.adins.mss.foundation.image.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Created by gigin.ginanjar on 05/09/2016.
 */
public class ImageViewerActivity extends FragmentActivity implements View.OnClickListener {
    public static final String BUND_KEY_IMAGE = "com.adins.mss.base.dynamicform.form.questions.BUND_KEY_IMAGE";
    public static final String BUND_KEY_IMAGE_TEMP_LOCATION = "com.adins.mss.base.dynamicform.form.questions.BUND_KEY_IMAGE_TEMP_LOCATION";
    public static final String BUND_KEY_IMAGE_QUALITY = "com.adins.mss.base.dynamicform.form.questions.BUND_KEY_IMAGE_QUALITY";
    public static final String BUND_KEY_IMAGE_ISVIEWER = "com.adins.mss.base.dynamicform.form.questions.BUND_KEY_IMAGE_ISVIEWER";
    TouchImageView imageView;
    LinearLayout btnLayout;
    Bitmap tempBitmap;
    Bitmap imgBitmap;
    int imgQuality;

    private FirebaseAnalytics screenName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_viewer_layout);
        imageView = (TouchImageView) findViewById(R.id.imageViewer);
        imageView.setMaxZoom(8f);
        btnLayout = (LinearLayout) findViewById(R.id.btnLayout);

        byte[] imageByte = getIntent().getExtras().getByteArray(BUND_KEY_IMAGE);
        imgQuality = getIntent().getExtras().getInt(BUND_KEY_IMAGE_QUALITY);
        boolean isViewer = getIntent().getExtras().getBoolean(BUND_KEY_IMAGE_ISVIEWER, false);
        screenName = FirebaseAnalytics.getInstance(this);

        //for receiving bitmap with size over 500kb using temporary file storage then convert back to byte array
        if (null == imageByte || 0 == imageByte.length) {
            String imageLoc = getIntent().getExtras().getString(BUND_KEY_IMAGE_TEMP_LOCATION);
            File file = new File(imageLoc);
            imgBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }

        new BitmapWorkerTask(this, imageView, imageByte, imgBitmap, false, false).execute();

        if (isViewer) {
            btnLayout.setVisibility(View.GONE);
        } else {
            btnLayout.setVisibility(View.VISIBLE);
        }

        Button btnRotate = (Button) findViewById(R.id.btnRotate);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnRotate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this, getString(R.string.screen_name_image_viewer), null);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSave) {
            new BitmapWorkerTask(this, imageView, null, null, false, true).execute();
        } else if (id == R.id.btnRotate) {
            new BitmapWorkerTask(this, imageView, null, null, true, false).execute();
        }
    }

    public class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<TouchImageView> imageViewReference;
        private final byte[] data;
        private final WeakReference<FragmentActivity> mContext;
        boolean isRotate;
        boolean isSave;
        private ProgressDialog progressDialog;
        private ByteArrayOutputStream stream;
        private Bitmap bitmapImage;

        public BitmapWorkerTask(FragmentActivity context, TouchImageView imageView, byte[] data) {
            imageViewReference = new WeakReference<>(imageView);
            this.data = data;
            this.mContext = new WeakReference<>(context);
            stream = new ByteArrayOutputStream();
        }

        public BitmapWorkerTask(FragmentActivity context, TouchImageView imageView, byte[] data, Bitmap bitmapImage, boolean isRotate, boolean isSave) {
            imageViewReference = new WeakReference<>(imageView);
            this.data = data;
            this.mContext = new WeakReference<>(context);
            this.isRotate = isRotate;
            this.isSave = isSave;
            this.bitmapImage = bitmapImage;
            stream = new ByteArrayOutputStream();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(mContext.get(), "", mContext.get().getString(R.string.processing_image), true);
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                if (null == bitmapImage){
                    Bitmap bitmap = null;
                    if (isRotate) {
                        bitmap = ImageManipulation.rotateImage(tempBitmap, 90);
                        tempBitmap = bitmap;
                    } else if (isSave) {
                        tempBitmap.compress(Bitmap.CompressFormat.JPEG, imgQuality, stream);
                    } else {
                        bitmap = Utils.byteToBitmap(data);
                        tempBitmap = bitmap;
                    }
                    return bitmap;
                } else {
                    tempBitmap = bitmapImage;
                    return bitmapImage;
                }
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (imageViewReference != null && bitmap != null) {
                final TouchImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.rescalingImage();
                }
            }
            if (isSave) {
                byte[] _data = stream.toByteArray();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putByteArray(BUND_KEY_IMAGE, _data);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }
}
