package com.adins.mss.foundation.image;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;

import java.lang.ref.WeakReference;

public class ThumbnailBitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private final byte[] data;
    private final Activity mContext;
    private ProgressDialog progressDialog;

    public ThumbnailBitmapWorkerTask(Activity context, ImageView imageView, byte[] data) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.data = data;
        this.mContext = context;
    }

    /**
     * Uncomment if using progress bar
     * /* @Override
     * protected void onPreExecute() {
     * super.onPreExecute();
     * progressDialog = ProgressDialog.show(mContext, "", mContext.getString(R.string.processing_image), true);
     * }
     */

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            if (data != null) {
                Bitmap bm = Utils.byteToBitmap(data);
                int[] res = new int[2];
                res[0] = bm.getWidth();
                res[1] = bm.getHeight();
                int[] thumbRes = Utils.getSmallResolution(bm.getWidth(), bm.getHeight());
                Bitmap thumbnail = Bitmap.createScaledBitmap(bm, thumbRes[0], thumbRes[1], true);
                return thumbnail;
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
        /**Uncomment if using progress bar
         /*if (progressDialog != null && progressDialog.isShowing()) {
         progressDialog.dismiss();
         }*/
        if (imageViewReference != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null)
                    imageView.setImageBitmap(bitmap);
                else
                    imageView.setImageResource(R.drawable.ic_camera);
            }
        }
    }
}
