package com.adins.mss.foundation.camerainapp;

import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.adins.mss.foundation.camerainapp.helper.BasePresenterInterface;
import com.adins.mss.foundation.camerainapp.helper.BaseViewInterface;

import java.util.List;

/**
 * Created by angga.permadi on 7/26/2016.
 */
public interface CameraContract {

    interface View extends BaseViewInterface<Presenter> {
        void initializeViews();

        void cameraHasOpened();

        void cameraMode(int cameraMode, String flashMode);

        void onCameraPreview(int width, int height);

        void reviewMode(byte[] data);

        void setSupportCameraParams(boolean isSupportFrontCamera, List<String> flashMode);

        void onAutoFocus(AutoFocusItem.FocusMode mode, MotionEvent event);

        void onOrientationChanged(int rotation);

        void onTakePicture(boolean safeTakePicture);
    }

    interface Presenter extends BasePresenterInterface {
        void openCamera();

        void openCamera(int mode);

        void startPreview(SurfaceHolder holder, int width, int height, int quality);

        void resumePreview();

        void pausePreview();

        void stopCamera();

        void takePicture();

        void autoFocus(MotionEvent event, CameraSurfaceView view);

        void savePicture(byte[] data);

        void onCameraModeChange(int mode);

        void onFlashModeChange(String flashMode);

        void onOrientationChanged(int arg0);

        boolean onBackPressed();
    }
}
