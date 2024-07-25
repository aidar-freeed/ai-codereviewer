package com.adins.mss.foundation.image;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.Locale;

public class ViewImageActivity extends Activity {
    // We can be in one of these 3 states
    static final int NONE = 0;
    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();

    //data gambar
    byte[] img;
    ImageView view;
    Bitmap temp_bmp;
    private int jpegQuality = 70;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (temp_bmp != null) {
            temp_bmp.recycle();
            temp_bmp = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (temp_bmp != null) temp_bmp.recycle();
        setContentView(R.layout.view_image_layout);
        view = (ImageView) findViewById(R.id.image_view);
        Bundle extras = getIntent().getExtras();
        Button save = (Button) findViewById(R.id.Save);
        Button rotate = (Button) findViewById(R.id.Rotate);

        if (Global.getSharedGlobal().getIsViewer()) {
            save.setVisibility(View.GONE);
            rotate.setVisibility(View.GONE);
        } else {
            save.setVisibility(View.VISIBLE);
            rotate.setVisibility(View.VISIBLE);
        }

        img = extras.getByteArray(Global.BUND_KEY_IMAGE_BYTE);
        new BitmapWorkerTask(view, false, false).execute(img);
        view.setOnTouchListener(new ImageViewer());

        matrix.setTranslate(1f, 1f);
        view.setImageMatrix(matrix);
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

    public void rotateImage(View v) {
        new BitmapWorkerTask(view, true, false).execute();
    }

    public void saveImage(View v) {
        new BitmapWorkerTask(DynamicFormActivity.getThumbInFocus(), false, true).execute();
    }

    class BitmapWorkerTask extends AsyncTask<byte[], Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        QuestionBean bean;
        private byte[] data;
        private boolean isRotate;
        private boolean isSave;
        private ProgressDialog progressDialog;

        public BitmapWorkerTask(ImageView imageView, boolean isRotate, boolean isSave) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
            this.isRotate = isRotate;
            this.isSave = isSave;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ViewImageActivity.this, "", ViewImageActivity.this.getString(R.string.processing_image), true);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(byte[]... params) {
            Utility.freeMemory();
            Bitmap bm = null;
            if (isRotate) {
                bm = ImageManipulation.rotateImage(temp_bmp, 90);
                temp_bmp = bm;
            } else if (isSave) {
                bean = DynamicFormActivity.getQuestionInFocus();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                temp_bmp.compress(Bitmap.CompressFormat.JPEG, jpegQuality, stream);

                DynamicFormActivity.saveImage(stream.toByteArray());

                int[] res = Tool.getThumbnailResolution(temp_bmp.getWidth(), temp_bmp.getHeight());
                bm = Bitmap.createScaledBitmap(temp_bmp, res[0], res[1], true);
            } else {
                data = params[0];
                bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                temp_bmp = bm;
            }

            return bm;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
                if (isSave) {
                    String indicatorGPS = "";
                    boolean isGeoTagged = Global.AT_IMAGE_W_LOCATION.equals(bean.getAnswer_type());
                    boolean isGeoTaggedGPSOnly = Global.AT_IMAGE_W_GPS_ONLY.equals(bean.getAnswer_type());
                    long size = bean.getImgAnswer().length;
                    String formattedSize = Formatter.formatByteSize(size);
                    if (isGeoTagged || isGeoTaggedGPSOnly) {
                        indicatorGPS = bean.getAnswer();
                        if (!"".equals(bean.getAnswer()) || bean.getAnswer() != null)
                            DynamicFormActivity.setTxtDetailInFocus(temp_bmp.getWidth() + " x " + temp_bmp.getHeight() + ". Size " + formattedSize + "\n" + indicatorGPS);
                        else
                            DynamicFormActivity.setTxtDetailInFocus(temp_bmp.getWidth() + " x " + temp_bmp.getHeight() + ". Size " + formattedSize);
                    } else {
                        DynamicFormActivity.setTxtDetailInFocus(temp_bmp.getWidth() + " x " + temp_bmp.getHeight() + ". Size " + formattedSize);
                    }
                    finish();
                }
            }
        }
    }
}
