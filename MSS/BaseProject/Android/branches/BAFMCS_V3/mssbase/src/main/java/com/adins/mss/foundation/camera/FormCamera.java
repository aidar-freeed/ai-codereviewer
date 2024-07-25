package com.adins.mss.foundation.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera.Parameters;
import android.widget.ImageView;
import android.widget.Toast;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.ByteFormatter;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.Date;

public class FormCamera extends Camera {

    private QuestionBean questionInFocus;
    private ImageView thumbInFocus;

    public FormCamera(Context context, Activity activity,
                      android.hardware.Camera camera, Parameters params, QuestionBean questionInFocus) {
        super(context, activity, camera, params);
        this.questionInFocus = questionInFocus;
        thumbInFocus = new ImageView(context);
    }

    @Override
    protected void processImage(byte[] dataPicWithExif, LocationInfo locationInfo) {
        // delegate
        this.imageCallBack.onPictureTaken(dataPicWithExif, locationInfo);

        LocationTrackingManager ltm = Global.LTM;
        saveImage(dataPicWithExif);

        boolean getGPS = true;
        boolean isGeoTagged = Global.AT_IMAGE_W_LOCATION.equals(questionInFocus.getAnswer_type());
        boolean isGeoTaggedGPSOnly = Global.AT_IMAGE_W_GPS_ONLY.equals(questionInFocus.getAnswer_type());

        //Glen 7 Oct 2014,  create timestamp, copy logic from MSMTOW, as default value if not using location
        try {
            long date = (new Date()).getTime();
            getQuestionInFocus().setLovId(String.valueOf(date));
        } catch (Exception e) {
            FireCrash.log(e);
            getQuestionInFocus().setLovId("0");
        }


        if (isGeoTagged && ltm != null) {
            LocationInfo locInfo = ltm.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
            getQuestionInFocus().setAnswer(locationInfoToSubmitString(locInfo));
        }

        if (isGeoTaggedGPSOnly && ltm != null) {
            LocationInfo locInfo = ltm.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
            //Glen 7 Oct 2014, add timestamp
            if (Double.parseDouble(locInfo.getLatitude()) == 0.0 || Double.parseDouble(locInfo.getLongitude()) == 0.0) {
                if (getQuestionInFocus().isMandatory() || getQuestionInFocus().isRelevantMandatory()) {
                    String[] msg = {"Can't get GPS location"};
                    String alert2 = Tool.implode(msg, "\n");
                    Toast.makeText(getContext(), alert2, Toast.LENGTH_LONG).show();
                    saveImage(null);
                    getGPS = false;
                }
            } else {
                getQuestionInFocus().setAnswer(locationInfoToSubmitString(locInfo));
            }
        }
        // set thumbnail
        if (thumbInFocus != null && getGPS) {
            Bitmap bm = BitmapFactory.decodeByteArray(dataPicWithExif, 0, dataPicWithExif.length);

            int[] res = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
            Bitmap thumbnail = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
            setThumb(thumbnail);

            //Glen 21 Oct 2014, format byte
            long size = getQuestionInFocus().getImgAnswer().length;
            String formattedSize = ByteFormatter.formatByteSize(size);
            setTxtDetail("   " + bm.getWidth() + " x " + bm.getHeight() + ". Size " + formattedSize);

            questionInFocus = null;
        }
    }

    private QuestionBean getQuestionInFocus() {
        return questionInFocus;
    }

    private void setThumb(Bitmap bitmap) {
        thumbInFocus.setImageBitmap(bitmap);
    }

    private void saveImage(byte[] imgAnswer) {
        questionInFocus.setImgAnswer(imgAnswer);
    }
}
