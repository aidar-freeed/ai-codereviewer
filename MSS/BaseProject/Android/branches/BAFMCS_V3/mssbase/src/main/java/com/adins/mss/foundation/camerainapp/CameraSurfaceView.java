package com.adins.mss.foundation.camerainapp;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.adins.mss.foundation.camerainapp.helper.Logger;

/**
 * Created by angga.permadi on 7/26/2016.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private CameraPresenter presenter;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Logger.i(this, "surfaceCreated...");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        Logger.i(this, "surfaceChanged...");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Logger.i(this, "surfaceDestroyed...");
        //CameraInterface.getInstance().doStopCamera();

        if (presenter != null) {
            presenter.stopCamera();
        }
    }

    public void setPresenter(CameraPresenter presenter) {
        this.presenter = presenter;
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

}