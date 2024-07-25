package com.adins.mss.foundation.camerainapp;

import android.content.Context;
import android.hardware.Camera;
import android.view.View;
import android.widget.CompoundButton;

import com.adins.mss.base.R;
import com.adins.mss.foundation.camerainapp.helper.BaseLinearLayout;

import java.util.List;

/**
 * Created by angga.permadi on 7/26/2016.
 */
public class ModeCameraItem extends BaseLinearLayout {

    private List<String> supportedFlashMode;
    private int rotation;

    public ModeCameraItem(Context context) {
        super(context);
    }

    @Override
    protected void afterCreate() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.item_camera_mode;
    }

    public void bind(final CameraPresenter presenter, int cameraMode, String flashMode) {
        boolean isFrontCamera = cameraMode != CameraPresenter.BACK_CAMERA;

        query.id(R.id.btn_takepicture).enabled(true).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.takePicture();
            }
        });

        query.id(R.id.cb_camera_mode).checked(isFrontCamera).getCheckBox()
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int mode = isChecked ? CameraPresenter.FRONT_CAMERA : CameraPresenter.BACK_CAMERA;

                        presenter.onCameraModeChange(mode);
                    }
                });

        if (flashMode == null || flashMode.isEmpty() || query.getCheckBox().isChecked()) {
            query.id(R.id.btn_flash_mode).gone();
        } else {
            query.id(R.id.btn_flash_mode).visible().getView().setTag(flashMode);

            setFlashImage(flashMode);

            query.clicked(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currentFlashMode = v.getTag().toString();

                    int sizeSupporttedFlashMode = supportedFlashMode.size();

                    int indexNextFlashMode = supportedFlashMode.indexOf(currentFlashMode) + 1 < sizeSupporttedFlashMode ?
                            supportedFlashMode.indexOf(currentFlashMode) + 1 : 0;
                    String nextFlashMode = supportedFlashMode.get(indexNextFlashMode);
                    query.id(R.id.btn_flash_mode).getView().setTag(nextFlashMode);
                    setFlashImage(nextFlashMode);
                    presenter.onFlashModeChange(nextFlashMode);
                }
            });
        }
    }

    public void bind(List<String> flashMode) {
        this.supportedFlashMode = flashMode;
        if (supportedFlashMode == null || supportedFlashMode.size() == 0) {
            query.id(R.id.btn_flash_mode).gone();
        } else {
            query.id(R.id.btn_flash_mode).visible();
        }
    }

    public void orientationChange(int arg0) {
        int newRotation;
        switch (arg0) {
            case 0:
                newRotation = 0;
                break;
            case 90:
                newRotation = 270;
                break;
            case 180:
                newRotation = 180;
                break;
            case 270:
                newRotation = 90;
                break;
            default:
                newRotation = 0;
                break;
        }

        //query.id(R.id.btn_flash_mode).getView().setRotation(rotation);
        //query.id(R.id.cb_camera_mode).getView().setRotation(rotation);

        int toRotation = newRotation - rotation <= 180 ? newRotation : newRotation - 360;
        rotation = rotation - newRotation <= 180 ? rotation : rotation - 360;
        createRotateAnimator(query.id(R.id.btn_flash_mode).getView(), rotation, toRotation).start();
        createRotateAnimator(query.id(R.id.cb_camera_mode).getView(), rotation, toRotation).start();
        rotation = newRotation;
    }

    private void setFlashImage(String flashImage) {
        switch (flashImage) {
            case Camera.Parameters.FLASH_MODE_AUTO:
                query.id(R.id.btn_flash_mode).background(R.drawable.ic_camera_flash_auto);
                break;
            case Camera.Parameters.FLASH_MODE_ON:
                query.id(R.id.btn_flash_mode).background(R.drawable.ic_camera_flash_on);
                break;
            case Camera.Parameters.FLASH_MODE_OFF:
                query.id(R.id.btn_flash_mode).background(R.drawable.ic_camera_flash_off);
                break;
            default:
                query.id(R.id.btn_flash_mode).gone();
        }
    }

    public void bind(boolean safeTakePicture) {
        query.id(R.id.btn_takepicture).enabled(safeTakePicture);
    }
}
