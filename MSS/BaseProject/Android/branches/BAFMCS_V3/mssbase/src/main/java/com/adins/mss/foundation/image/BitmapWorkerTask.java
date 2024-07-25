package com.adins.mss.foundation.image;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import androidx.fragment.app.FragmentActivity;
import android.widget.ImageView;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;

import java.lang.ref.WeakReference;

public class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private final byte[] data;
    private final FragmentActivity mContext;
    boolean isRotate;
    boolean isSave;
    private ProgressDialog progressDialog;

    public BitmapWorkerTask(FragmentActivity context, ImageView imageView, byte[] data) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.data = data;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(mContext, "", mContext.getString(R.string.processing_image), true);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            Bitmap bitmap = Utils.byteToBitmap(data);
            return bitmap;
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
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
