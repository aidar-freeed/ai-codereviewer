package com.adins.mss.foundation.camerainapp;

import android.content.Context;
import android.view.View;

import com.adins.mss.base.R;
import com.adins.mss.foundation.camerainapp.helper.BaseLinearLayout;

/**
 * Created by angga.permadi on 7/26/2016.
 */
public class ModeReviewItem extends BaseLinearLayout {

    private int rotation;

    public ModeReviewItem(Context context) {
        super(context);
    }

    @Override
    protected void afterCreate() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.item_review_mode;
    }

    public void bind(final CameraPresenter presenter, final byte[] data) {
        query.id(R.id.btn_save_picture).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.savePicture(data);
            }
        });

        query.id(R.id.btn_cancel).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.resumePreview();
            }
        });
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

        //query.id(R.id.btn_save_picture).getView().setRotation(rotation);
        //query.id(R.id.btn_cancel).getView().setRotation(rotation);

        int toRotation = newRotation - rotation <= 180 ? newRotation : newRotation - 360;
        rotation = rotation - newRotation <= 180 ? rotation : rotation - 360;
        createRotateAnimator(query.id(R.id.btn_save_picture).getView(), rotation, toRotation).start();
        createRotateAnimator(query.id(R.id.btn_cancel).getView(), rotation, toRotation).start();
        rotation = newRotation;
    }
}
