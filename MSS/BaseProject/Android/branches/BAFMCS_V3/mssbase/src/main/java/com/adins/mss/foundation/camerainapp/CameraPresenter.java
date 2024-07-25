package com.adins.mss.foundation.camerainapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.SensorEvent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.foundation.camerainapp.helper.CamParaUtil;
import com.adins.mss.foundation.camerainapp.helper.DisplayUtil;
import com.adins.mss.foundation.camerainapp.helper.ExifUtil;
import com.adins.mss.foundation.camerainapp.helper.FileUtil;
import com.adins.mss.foundation.camerainapp.helper.MovementDetector;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by angga.permadi on 7/26/2016.
 */
public class CameraPresenter implements CameraContract.Presenter {

    public static final int BACK_CAMERA = 0;
    public static final int FRONT_CAMERA = 1;
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Logger.i(this, "myShutterCallback:onShutter...");
        }
    };
    Camera.PictureCallback mRawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Logger.i(this, "myRawCallback:onPictureTaken...");

        }
    };
    private CameraContract.View mView;
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Logger.i(this, "myJpegCallback:onPictureTaken...");

            isPreviewing = false;
            mView.reviewMode(data);
            mView.onTakePicture(true);
        }
    };
    private boolean isAutoFocus = false;
    private int cameraMode;
    private String flashMode;
    private int rotation = -1;
    private boolean needBorder;

    public CameraPresenter(CameraContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void start() {
        mView.initializeViews();
        addMovementListener();
    }

    @Override
    public void destroy() {
        stopCamera();
    }

    @Override
    public void openCamera() {
        this.openCamera(cameraMode);
    }

    @Override
    public void openCamera(int mode) {
        this.cameraMode = mode;
        int cameraId = mode;

        if (cameraMode != BACK_CAMERA) {
            cameraId = CamParaUtil.getInstance().findFrontFacingCamera();
        }

        try {
            this.isPreviewing = false;

            if (mCamera != null) {
                mCamera.release();
            }
            mCamera = Camera.open(cameraId);
            mView.cameraHasOpened();
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    @Override
    public void startPreview(SurfaceHolder holder, int width, int height, int quality) {
        Logger.i(this, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }

        if (mCamera != null) {
            mParams = mCamera.getParameters();
            mParams.setPictureFormat(ImageFormat.JPEG);
            CamParaUtil.getInstance().printSupportPictureSize(mParams);
            CamParaUtil.getInstance().printSupportPreviewSize(mParams);

            Camera.Size pictureSize = CamParaUtil.getInstance().
                    getPropPictureSize(mParams.getSupportedPictureSizes(), width, height);
            mParams.setPictureSize(pictureSize.width, pictureSize.height);
            Camera.Size previewSize = CamParaUtil.getInstance().
                    getPropPreviewSize(mParams.getSupportedPreviewSizes(), pictureSize.width, pictureSize.height);
            mParams.setPreviewSize(previewSize.width, previewSize.height);
            mParams.setJpegQuality(quality);
            if(needBorder)
                mParams.setRotation(90);

            mCamera.setDisplayOrientation(90);

            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-picture")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            final List<String> supportedFlashMode = mParams.getSupportedFlashModes();
            if (supportedFlashMode != null && supportedFlashMode.size() != 0) {
                supportedFlashMode.remove(Camera.Parameters.FLASH_MODE_RED_EYE);
                supportedFlashMode.remove(Camera.Parameters.FLASH_MODE_TORCH);
                if (flashMode == null) flashMode = supportedFlashMode.get(0);

                if (cameraMode == BACK_CAMERA) {
                    mParams.setFlashMode(flashMode);
                }
            }

            mCamera.setParameters(mParams);

            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                isPreviewing = true;
            } catch (IOException e) {
                FireCrash.log(e);
                e.printStackTrace();
            }

            mParams = mCamera.getParameters();
            Logger.i(this, "PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Logger.i(this, "PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    try {
                        mView.onCameraPreview(mParams.getPreviewSize().width, mParams.getPreviewSize().height);
                        mView.setSupportCameraParams(true, supportedFlashMode);
                        mView.cameraMode(cameraMode, flashMode);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void resumePreview() {
        if (mCamera != null) {
            mCamera.startPreview();
            isPreviewing = true;
            mView.cameraMode(cameraMode, flashMode);
        }
    }

    @Override
    public void pausePreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    @Override
    public void stopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void takePicture() {
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(mShutterCallback, mRawCallback, mJpegPictureCallback);
            mView.onTakePicture(false);
        }
    }

    @Override
    public void autoFocus(final MotionEvent event, CameraSurfaceView view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return; // deprecated
        if (!isPreviewing || cameraMode == Camera.CameraInfo.CAMERA_FACING_FRONT) return;

        if (mCamera != null && mParams != null) {
            mCamera.cancelAutoFocus();
            Rect focusRect = DisplayUtil.calculateFocusArea(event.getX(), event.getY(),
                    view.getWidth(), view.getHeight());

            if (!mParams.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
                List<String> focusModes = mParams.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
            }
            if (mParams.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> mylist = new ArrayList<>();
                mylist.add(new Camera.Area(focusRect, 1000));
                mParams.setFocusAreas(mylist);
            }

            try {
                mCamera.setParameters(mParams);
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (!mCamera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                            isAutoFocus = false;
                            AutoFocusItem.FocusMode mode = success ? AutoFocusItem.FocusMode.FOCUS_SUCCESS :
                                    AutoFocusItem.FocusMode.FOCUS_FAILED;
                            mView.onAutoFocus(mode, event);
                        }
                    }
                });
                mView.onAutoFocus(AutoFocusItem.FocusMode.START_FOCUS, event);
                isAutoFocus = true;
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void savePicture(byte[] data) {
        Activity activity = ((Activity) mView.getContext());
        if (data != null) {
            try {
                Bitmap b = Utils.byteToBitmap(data);

                Uri path = null;
                if (b != null) {
                    File file = FileUtil.bitmapToFileConverter(activity, b);
                    ExifUtil.setRotationToFileExif(file, ExifUtil.getOrientation(data));

                    path = Uri.fromFile(file);
                    b.recycle();
                }

                Intent intent = new Intent();
                if (path == null) {
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                    return;
                }

                intent.putExtra(CameraActivity.PICTURE_URI, path.toString());
                Bundle bundle = new Bundle();
                bundle.putString(CameraActivity.PICTURE_URI, path.toString());
                intent.putExtras(bundle);
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            } catch (OutOfMemoryError | Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, R.string.faild_save_image, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCameraModeChange(int mode) {
        if (mode == cameraMode) return;

        this.cameraMode = mode;
        openCamera(mode);
    }

    @Override
    public void onFlashModeChange(String flashMode) {
        this.flashMode = flashMode;

        if (mCamera == null || mParams == null) return;
        mParams.setFlashMode(flashMode);
        mCamera.setParameters(mParams);
    }

    @Override
    public void onOrientationChanged(int arg0) {
        try {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraMode, info);
            arg0 = (arg0 + 45) / 90 * 90;
            int currentRotation;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                currentRotation = (info.orientation - arg0 + 360) % 360;
            } else {  // back-facing camera
                currentRotation = (info.orientation + arg0) % 360;
            }

            if (rotation != currentRotation) {
                rotation = currentRotation;
                if (mParams != null && mCamera != null) {
                    if (rotation >= 360) rotation %= 360;
                    if (!needBorder) {
                        mParams.setRotation(rotation);
                        mCamera.setParameters(mParams);
                    }
                }
                mView.onOrientationChanged(arg0);
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (!isPreviewing) {
            resumePreview();
            return false;
        }

        return true;
    }

    private void addMovementListener() {
        MovementDetector.getInstance().addListener(new MovementDetector.Listener() {
            @Override
            public void onMotionDetected(SensorEvent event, float acceleration) {
                if (!isPreviewing || isAutoFocus || cameraMode == Camera.CameraInfo.CAMERA_FACING_FRONT)
                    return;

                if (!mCamera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    List<String> focusModes = mParams.getSupportedFocusModes();
                    if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    }
                    if (mParams.getMaxNumFocusAreas() > 0) {
                        mParams.setFocusAreas(null);
                    }
                    mCamera.setParameters(mParams);

                    mView.onAutoFocus(AutoFocusItem.FocusMode.FOCUS_CONTINUOUS, null);
                }
            }
        });
    }

    public boolean isNeedBorder() {
        return needBorder;
    }

    public void setNeedBorder(boolean needBorder) {
        this.needBorder = needBorder;
    }
}
