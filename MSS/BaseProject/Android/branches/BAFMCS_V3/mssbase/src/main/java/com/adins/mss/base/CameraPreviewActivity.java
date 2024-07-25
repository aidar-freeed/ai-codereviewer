package com.adins.mss.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.camera.Camera;
import com.adins.mss.foundation.camera.ImageCallBack;
import com.adins.mss.foundation.image.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by winy.firdasari on 14/01/2015.
 */
public class CameraPreviewActivity extends Activity {

    // ---
    private static final int BACK_MENU_ID = Menu.FIRST;
    private static final int OPTIONS_MENU_ID = Menu.FIRST + 1;
    private static final int CAPTURE_MENU_ID = Menu.FIRST + 2;
    RelativeLayout container;
    Context context;
    boolean hasCamera;
    private Preview mPreview;
    private ListAdapter listPicSize;
    private int rotate = 0;
    private Button btCapture;
    private Activity activity;
    private int actualWidht = Utils.picWidth;
    private int actualHeight = Utils.picHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        mPreview = new Preview(this);
        setContentView(mPreview);
        hasCamera = Camera.checkCameraHardware(this);

        setContentView(R.layout.camera_preview_layout);
        container = (RelativeLayout) findViewById(R.id.container);

        LinearLayout previewContainer = (LinearLayout) findViewById(R.id.previewContainer);
        previewContainer.addView(mPreview);

        btCapture = (Button) findViewById(R.id.btCaptureCam);

        btCapture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPreview.mCamera.setAutoFocus();
                mPreview.mCamera.setFlashAuto();
                mPreview.mCamera.getPicture(new ImageCallBack() {
                    @Override
                    public void onPictureTaken(byte[] bytes, Object o) {
                        Intent intent = new Intent();
                        intent.putExtra(Camera.BUND_KEY_IMAGE_BYTE, bytes);
                        setResult(Global.REQUEST_CAMERA, intent);
                    }

                });
            }
        });
        this.activity = this;
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
    public boolean getLCDSceenTipeLandscape() {
        boolean result = false;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        String str_ScreenSize = "The Android Screen is: W="
                + dm.widthPixels
                + " x H="
                + dm.heightPixels;

        result = dm.widthPixels > dm.heightPixels;

        return result;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, BACK_MENU_ID, 0, "Back");
        menu.add(0, OPTIONS_MENU_ID, 0, "Options");
        menu.add(0, CAPTURE_MENU_ID, 0, "Capture");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case BACK_MENU_ID:
                this.finish();
                return true;
            case OPTIONS_MENU_ID:
                this.showOptions();
                return true;
            case CAPTURE_MENU_ID:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showOptions() {
        this.loadSupportedPicSize();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Picture Size");
        builder.setSingleChoiceItems(listPicSize, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String resolution = (String) listPicSize.getItem(item);
                String[] sz = resolution.split("x");
                int w = Integer.parseInt(sz[0]);
                int h = Integer.parseInt(sz[1]);

                mPreview.setPictureSize(w, h);
                mPreview.mCamera.getCamera().stopPreview();
                android.hardware.Camera.Size previewSize = mPreview.getOptimalPreviewSize(w, h);
                mPreview.setPreviewSize(previewSize.width, previewSize.height);
                mPreview.mCamera.getCamera().startPreview();
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                Toast.makeText(getApplicationContext(), getString(R.string.picture_resolution, resolution), Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    private void loadSupportedPicSize() {
        if (listPicSize == null) {
            List<String> listSize = mPreview.getSupportedPictureSize();
            listPicSize = new ArrayAdapter<String>(this, R.layout.picture_size_list, listSize);
        }
    }

    public class Preview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public Preview(Context context) {
            super(context);
            mHolder = getHolder();
            mHolder.addCallback(this);
        }

        public List<String> getSupportedPictureSize() {
            List<android.hardware.Camera.Size> listSize = mCamera.getCamera().getParameters().getSupportedPictureSizes();
            List<String> listSizeStr = new ArrayList<>();

            for (android.hardware.Camera.Size size : listSize) {
                listSizeStr.add(size.width + "x" + size.height);
            }

            return listSizeStr;
        }

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                android.hardware.Camera cam = android.hardware.Camera.open();
                mCamera = new Camera(context, activity, cam, cam.getParameters());
            } catch (Exception e) {
                FireCrash.log(e);
            }


            // default to set picture size to lowest resolution
            List<android.hardware.Camera.Size> listSupportedSize = mCamera.getCamera().getParameters().getSupportedPictureSizes();
            if (listSupportedSize != null && !listSupportedSize.isEmpty()) {
                android.hardware.Camera.Size minimumSize = listSupportedSize.get(searchMostSuportedSizeFromParam(listSupportedSize));
                android.hardware.Camera.Parameters parameters = mCamera.getCamera().getParameters();
                parameters.setPictureSize(minimumSize.width, minimumSize.height);
                mCamera.getCamera().setParameters(parameters);
            }

            try {
                mCamera.getCamera().setPreviewDisplay(holder);
                mCamera.getCamera().setFaceDetectionListener(new com.adins.mss.foundation.camera.FaceDetectionListener());
                mCamera.startFaceDetection();
            } catch (IOException exception) {
                mCamera.getCamera().release();
                mCamera = null;
            }
        }

        public int searchMostSuportedSizeFromParam(List<android.hardware.Camera.Size> listSupportedSize) {
            android.hardware.Camera.Size suportedSize;
            int idx = 0;
            //search for match w x h
            for (int i = 0; i < listSupportedSize.size(); i++) {
                if (listSupportedSize.get(i).width == actualWidht && listSupportedSize.get(i).height == actualHeight) {

                    return i;
                }
            }

            //search for match h
            for (int i = 0; i < listSupportedSize.size(); i++) {
                if (listSupportedSize.get(i).height == actualHeight) {

                    return i;
                }
            }


            //search for match w
            for (int i = 0; i < listSupportedSize.size(); i++) {
                if (listSupportedSize.get(i).width == actualWidht) {

                    return i;
                }
            }


            return listSupportedSize.size() - 1;
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            mCamera.getCamera().stopPreview();
            mCamera.getCamera().release();
            mCamera = null;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            android.hardware.Camera.Parameters parameters = mCamera.getCamera().getParameters();
            android.hardware.Camera.Size resultSize = null;

            try {
                resultSize = getOptimalPreviewSize(w, h);
                if (resultSize != null) {
                    w = resultSize.width;
                    h = resultSize.height;
                }
                if (resultSize != null)
                    parameters.setPreviewSize(w, h);

            } catch (Exception e) {
                FireCrash.log(e);
                resultSize = null;
            }

            parameters.setPictureFormat(ImageFormat.JPEG);
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
            parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);

            if (resultSize != null) {
                int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                android.hardware.Camera.CameraInfo info =
                        new android.hardware.Camera.CameraInfo();
                android.hardware.Camera.getCameraInfo(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK, info);
                int degrees = 0;
                switch (rotation) {
                    case Surface.ROTATION_0:
                        degrees = 0;
                        break;
                    case Surface.ROTATION_90:
                        degrees = 90;
                        break;
                    case Surface.ROTATION_180:
                        degrees = 180;
                        break;
                    case Surface.ROTATION_270:
                        degrees = 270;
                        break;
                    default:
                        break;
                }

                int result;
                if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    result = (info.orientation + degrees) % 360;
                    result = (360 - result) % 360;  // compensate the mirror
                } else {  // back-facing
                    result = (info.orientation - degrees + 360) % 360;
                }

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    rotate = 90;
                }

                //khusus untuk device dengan lcd landscape, ex: htc chacha
                if (result == 90 && getLCDSceenTipeLandscape()) {
                    result = 0;
                } else if (result == 0 && !getLCDSceenTipeLandscape()) {
                    result = 270;
                } else if (result == 180 && !getLCDSceenTipeLandscape()) {
                    result = 90;
                } else if (result == 270 && getLCDSceenTipeLandscape()) {
                    result = 180;
                }

                mCamera.getCamera().setDisplayOrientation(result);
            }


            try {
                mCamera.getCamera().setParameters(parameters);
                mCamera.getCamera().setFaceDetectionListener(new com.adins.mss.foundation.camera.FaceDetectionListener());
                mCamera.startFaceDetection();
            } catch (Exception e) {
                FireCrash.log(e);
                parameters = mCamera.getCamera().getParameters();
                parameters.setPictureFormat(ImageFormat.JPEG);
                parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
                parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_AUTO);

                try {
                    mCamera.getCamera().setParameters(parameters);
                    mCamera.getCamera().setFaceDetectionListener(new com.adins.mss.foundation.camera.FaceDetectionListener());
                    mCamera.startFaceDetection();
                } catch (Exception e2) {
                    parameters = mCamera.getCamera().getParameters();
                    mCamera.getCamera().setParameters(parameters);
                }

            }

            mCamera.getCamera().startPreview();
        }

        public void setPictureSize(int width, int height) {
            android.hardware.Camera.Parameters cp = mCamera.getCamera().getParameters();
            cp.setPictureSize(width, height);
            mCamera.getCamera().setParameters(cp);
        }

        public android.hardware.Camera.Size getOptimalPreviewSize(int picWidth, int picHeight) {
            List<android.hardware.Camera.Size> sizes = mCamera.getCamera().getParameters().getSupportedPreviewSizes();
            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) picWidth / picHeight;
            if (sizes == null)
                return null;

            android.hardware.Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = picHeight;

            // Try to find an size match aspect ratio and size
            for (android.hardware.Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                    continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            // Cannot find the one match the aspect ratio, ignore the
            // requirement
            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (android.hardware.Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }

        public void setPreviewSize(int width, int height) {
            android.hardware.Camera.Parameters cp = mCamera.getCamera().getParameters();
            cp.setPreviewSize(width, height);
            mCamera.getCamera().setParameters(cp);
        }
    }


}