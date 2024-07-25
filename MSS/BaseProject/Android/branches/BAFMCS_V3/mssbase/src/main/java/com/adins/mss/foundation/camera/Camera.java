package com.adins.mss.foundation.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.ImageManipulation;
import com.adins.mss.foundation.location.LocationTrackingManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author bong.rk
 */
public class Camera {
    private static final String SUBMIT_DELIMITER = ",";
    private static final String TAG = "Camera";
    public static String BUND_KEY_IMAGE_BYTE = "BUND_KEY_IMAGE_BYTE";
    private static Context context;
    ExifInterface exif;
    /**
     *
     */
    ShutterCallback shutterCallback = new ShutterCallback() {

        @Override
        public void onShutter() {
            //EMPTY
        }

    };
    /**
     *
     */
    PictureCallback rawCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
          //EMPTY
        }

    };

    /**
     * autofocus camera
     */
    AutoFocusCallback autoFocusTouch = new AutoFocusCallback() {

        public void onAutoFocus(boolean success, android.hardware.Camera camera) {
            if (!success) {
                String[] msg = {"Autofocus not available"};
                String alert = Tool.implode(msg, "\n");
                Toast.makeText(getContext(), alert, Toast.LENGTH_SHORT).show();
            }

        }
    };
    ImageCallBack imageCallBack;

    private Activity activity;
    /**
     * Handle return from camera.takepicture
     */
    PictureCallback jpegCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
            resizeImage(data, 0, 240, 320, 70);
            LocationTrackingManager ltm = Global.LTM;

            try {
                getPicWithExif(data, ltm.getCurrentLocation(Global.FLAG_LOCATION_CAMERA));
            } catch (Exception e) {
                FireCrash.log(e);
                getPicWithExif(data, null);
            }
            getActivity().finish();
        }
    };
    private android.hardware.Camera camera;
    /**
     * autofocus to capture image
     */
    AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {

        public void onAutoFocus(boolean success, android.hardware.Camera camera) {
            if (!success) {
                String[] msg = {"Autofocus not avaiable"};
                String alert = Tool.implode(msg, "\n");
                Toast.makeText(getContext(), alert, Toast.LENGTH_SHORT).show();

            }
            if (Global.IS_DEV) Log.i(TAG, "autofocus :" + success);
            getCamera().takePicture(shutterCallback, rawCallback, jpegCallback);
        }
    };
    private Parameters params;
    private TextView txtDetailInFocus;

    /**
     * create camera object for getting these functionalities
     *
     * @param context  - application context from activity class
     * @param activity - activity class
     * @param camera   - initialized camera from android.hardware.Camera
     * @param params   - parameter setting from camera
     */
    public Camera(Context context, Activity activity, android.hardware.Camera camera,
                  Parameters params
    ) {
        Camera.context = context;
        this.camera = camera;
        this.params = params;
        this.activity = activity;
        txtDetailInFocus = new TextView(context);
    }

    /**
     * Check if this device has a camera
     */
    public static boolean checkCameraHardware(Context context) {
        // this device has a camera
// no camera on this device
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * This method is used to resize image with a custom size by user
     * <br>Suggested to use fit size according to size of device screen
     *
     * @param img
     * @param rotate       - integer as degree
     * @param actualWidth
     * @param actualHeight
     * @param jpegQuality  - integer as perceint
     * @return
     */
    public static byte[] resizeImage(byte[] img, int rotate, int actualWidth, int actualHeight, int jpegQuality) {
        if (Global.IS_DEV) Log.i(TAG, "image quality : " + jpegQuality);
        Bitmap bm = BitmapFactory.decodeByteArray(img, 0, img.length);
        Bitmap bmp = Bitmap.createScaledBitmap(bm, actualWidth, actualHeight, true);

        //rotate image if potraid
        if (rotate != 0) {
            Matrix mat = new Matrix();
            mat.preRotate(rotate);// /in degree

            // Bitmap
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), mat, true);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, jpegQuality, stream);

        return stream.toByteArray();
    }

    public static byte[] resizeImageWithWatermark(byte[] img, int rotate, int actualWidth, int actualHeight, int jpegQuality, Activity activity) {
        if (Global.IS_DEV) Log.i(TAG, "image quality : " + jpegQuality);
        Bitmap bm = BitmapFactory.decodeByteArray(img, 0, img.length);
        Bitmap bmp = Bitmap.createScaledBitmap(bm, actualWidth, actualHeight, true);

        //rotate image if potraid
        if (rotate != 0) {
            Matrix mat = new Matrix();
            mat.preRotate(rotate);// /in degree

            // Bitmap
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), mat, true);
        }
        //untuk nambah watermark
        Bitmap bmpFinal = ImageManipulation.waterMark(bmp, activity.getString(R.string.watermark), Color.WHITE, 80, 32, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmpFinal.compress(Bitmap.CompressFormat.JPEG, jpegQuality, stream);

        try {
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
            }
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
            if (bmpFinal != null && !bmpFinal.isRecycled()) {
                bmpFinal.recycle();
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return stream.toByteArray();
    }

    public static Context getContext() {
        return context;
    }

    public boolean checkSupportedAutoFocus() {
        boolean isSupported = false;
        List<String> focusMode = params.getSupportedFocusModes();
        if (focusMode.contains(Parameters.FOCUS_MODE_AUTO)) {
            isSupported = true;
        }
        return isSupported;
    }

    public void setAutoFocus() {
        if (checkSupportedAutoFocus())
            this.params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
    }

    public void startFaceDetection() {
        // Try starting Face Detection

        // start face detection only *after* preview has started
        if (params.getMaxNumDetectedFaces() > 0) {
            // camera supports face detection, so can start it:
            getCamera().startFaceDetection();
        }
    }

    /**
     * Set parameter camera to turn auto flash
     */
    public void setFlashAuto() {
        this.params.setFlashMode(Parameters.FLASH_MODE_AUTO);
    }

    /**
     * Set parameter camera to turn auto flash and change backgroundResource spinner
     */
    public void setFlashAuto(Spinner spinner, int iconFlash) {
        spinner.setBackgroundResource(iconFlash);
        this.params.setFlashMode(Parameters.FLASH_MODE_AUTO);
    }

    /**
     * Set parameter camera to turn on flash
     */
    public void setFlashOn() {
        this.params.setFlashMode(Parameters.FLASH_MODE_ON);
    }

    /**
     * Set parameter camera to turn on flash and change backgroundResource spinner
     */
    public void setFlashOn(Spinner spinner, int iconFlash) {
        spinner.setBackgroundResource(iconFlash);
        this.params.setFlashMode(Parameters.FLASH_MODE_ON);
    }

    /**
     * Set parameter camera to turn off flash
     */
    public void setFlashOff() {
        this.params.setFlashMode(Parameters.FLASH_MODE_OFF);
    }

    /**
     * Set parameter camera to turn off flash and change backgroundResource spinner
     */
    public void setFlashOff(Spinner spinner, int iconFlash) {
        spinner.setBackgroundResource(iconFlash);
        this.params.setFlashMode(Parameters.FLASH_MODE_OFF);
    }

    /**
     * @param locationInfo
     * @return
     */
    public String locationInfoToSubmitString(LocationInfo locationInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(locationInfo.getLatitude()).append(SUBMIT_DELIMITER)
                .append(locationInfo.getLongitude()).append(SUBMIT_DELIMITER)
                .append(locationInfo.getCid()).append(SUBMIT_DELIMITER)
                .append(locationInfo.getMcc()).append(SUBMIT_DELIMITER)
                .append(locationInfo.getMnc()).append(SUBMIT_DELIMITER)
                .append(locationInfo.getLac()).append(SUBMIT_DELIMITER)
                .append(locationInfo.getAccuracy());
        return sb.toString();
    }

    public TextView getTxtDetail() {
        return txtDetailInFocus;
    }

    public void setTxtDetail(String textContent) {
        txtDetailInFocus.setText(textContent);
    }

    /**
     * This method is used to get image with set exif to the image
     *
     * @param data         - captured image byte
     * @param locationInfo - location listener to get location in order to get longitude and latitude
     * @return byte - image with exif in byte
     */
    private byte[] getPicWithExif(byte[] data, LocationInfo locationInfo) {
        // prepare for creating file into local storage
        File photo = new File(context.getExternalFilesDir(null)+ "tempImage.jpeg");

        if (photo.exists()) {
            boolean result = photo.delete();
            if(!result){
                Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
            }
        }
        try (FileOutputStream fos = new FileOutputStream(photo.getPath())){

            fos.write(data);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }

        if (locationInfo != null) {
            double latitude = Double.parseDouble(locationInfo.getLatitude());
            double longitude = Double.parseDouble(locationInfo.getLongitude());
            try {
                exif = new ExifInterface(photo.getPath());
                int num1Lat = (int) Math.floor(latitude);
                int num2Lat = (int) Math.floor((latitude - num1Lat) * 60);
                double num3Lat = (latitude - ((double) num1Lat + ((double) num2Lat / 60))) * 3600000;
                int num1Lon = (int) Math.floor(longitude);
                int num2Lon = (int) Math.floor((longitude - num1Lon) * 60);
                double num3Lon = (longitude - ((double) num1Lon + ((double) num2Lon / 60))) * 3600000;
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat + "/1," + num2Lat + "/1," + num3Lat + "/1000");
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon + "/1," + num2Lon + "/1," + num3Lon + "/1000");
                if (latitude > 0) {
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
                } else {
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
                }
                if (longitude > 0) {
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
                } else {
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
                }
                exif.saveAttributes();
            } catch (IOException e) {
                Logger.e("PictureActivity", e.getLocalizedMessage());
            }
        }
        // get byte of pictWithExif
        Logger.e("photo length", photo.length() + "");
        byte[] dataPicWithExif = new byte[(int) photo.length()];

        try( BufferedInputStream buf = new BufferedInputStream(new FileInputStream(photo));) {
            buf.read(dataPicWithExif, 0, dataPicWithExif.length);
        } catch (Exception e) {
            FireCrash.log(e);
        }

        //delete photo from local
        boolean result = photo.delete();
        if(!result){
            Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
        }

        //delegate
        processImage(dataPicWithExif, locationInfo);

        return dataPicWithExif;
    }

    protected void processImage(byte[] dataPicWithExif, LocationInfo locationInfo) {
        // delegate
        this.imageCallBack.onPictureTaken(dataPicWithExif, locationInfo);
    }

    /**
     * This method is to capture image
     * <br>It calls method to process captured image
     * <br>In this framework, that method will call back interface onPictureTaken to activity
     *
     * @param imageCallBack - interface to be called back and receive image with exif and location
     */
    public void getPicture(ImageCallBack imageCallBack) {
        this.imageCallBack = imageCallBack;
        this.camera.setParameters(this.params);
        this.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    public android.hardware.Camera getCamera() {
        return camera;
    }

    public Parameters getParams() {
        return params;
    }

    public void setParams(Parameters params) {
        this.params = params;
    }

    public Activity getActivity() {
        return activity;
    }

}
