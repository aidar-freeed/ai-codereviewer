package com.adins.mss.base.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.image.ImageLoader;

import java.io.ByteArrayOutputStream;

public class GetLocationImage extends AsyncTask<Void, Void, byte[]> {

    private Context context;
    private LocationInfo info;
    private String lat;
    private String lng;

    public GetLocationImage(Context context, LocationInfo info) {
        this.context = context;
        this.info = info;
    }

    public GetLocationImage(Context context, String lat, String lng) {
        this.context = context;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected byte[] doInBackground(Void... params) {
        // TODO Auto-generated method stub
        byte[] bitmapArray = null;
        if (info != null) {
            lat = info.getLatitude();
            lng = info.getLongitude();

            try {
                String image_url = "https://maps.googleapis.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=15&size=640x480&maptype=roadmap&markers=color:green%7Clabel:I%7C" + lat + "," + lng;

                ImageLoader imgLoader = new ImageLoader(context);
                Bitmap bitmap = imgLoader.getBitmap(image_url);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

                bitmapArray = stream.toByteArray();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        } else {
            try {
                String image_url = "https://maps.googleapis.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=15&size=640x480&maptype=roadmap&markers=color:green%7Clabel:I%7C" + lat + "," + lng;

                ImageLoader imgLoader = new ImageLoader(context);
                Bitmap bitmap = imgLoader.getBitmap(image_url);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

                bitmapArray = stream.toByteArray();
            } catch (Exception e) {
                FireCrash.log(e);

            }
        }
        return bitmapArray;
    }

}
