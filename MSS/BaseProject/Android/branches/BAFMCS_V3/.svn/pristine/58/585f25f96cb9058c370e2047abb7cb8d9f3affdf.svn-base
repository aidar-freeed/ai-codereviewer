package com.adins.mss.foundation.camerainapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.foundation.camerainapp.helper.AspectFrameLayout;
import com.adins.mss.foundation.camerainapp.helper.MovementDetector;
import com.androidquery.AQuery;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by angga.permadi on 7/26/2016.
 */
public class CameraActivity extends Activity implements CameraContract.View {

    public static final String PICTURE_WIDTH = "picture_width";
    public static final String PICTURE_HEIGHT = "picture_height";
    public static final String PICTURE_QUALITY = "picture_quality";
    public static final String PICTURE_URI = "picture_path";
    public static final String NEED_BORDER = "need_border";

    public static final int PICTURE_WIDHT_DEF = 1024;
    public static final int PICTURE_HEIGHT_DEF = 768;
    public static final int PICTURE_QUALITY_DEF = 70;
    private static final int MAX_CLICK_DURATION = 200;

    private AQuery query;
    private OrientationEventListener myOrientationEventListener;
    private CameraContract.Presenter presenter;
    private CameraSurfaceView cameraView;
    private ModeCameraItem cameraModeItem;
    private ModeReviewItem reviewModeItem;
    private AutoFocusItem autoFocusItem;

    private int width;
    private int height;
    private int quality;
    private boolean needBorder;
    private long startClickTime;

    private FirebaseAnalytics screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_camera);
        screenName = FirebaseAnalytics.getInstance(this);

        query = new AQuery(this);

        if (getIntent().getExtras() != null) {
            width = getIntent().getExtras().getInt(PICTURE_WIDTH);
            height = getIntent().getExtras().getInt(PICTURE_HEIGHT);
            quality = getIntent().getExtras().getInt(PICTURE_QUALITY);
            needBorder = getIntent().getExtras().getBoolean(NEED_BORDER, false);
        }

        if (width <= 0) width = PICTURE_WIDHT_DEF;
        if (height <= 0) height = PICTURE_HEIGHT_DEF;
        if (quality <= 0) quality = PICTURE_QUALITY_DEF;

        presenter = new CameraPresenter(this);
        ((CameraPresenter) presenter).setNeedBorder(needBorder);
        presenter.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this,getString(R.string.screen_name_camera),null);

        Thread openThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                presenter.openCamera();
            }
        };
        openThread.start();

        MovementDetector.getInstance().start();
    }

    @Override
    protected void onPause() {
        presenter.stopCamera();
        MovementDetector.getInstance().stop();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (myOrientationEventListener != null && myOrientationEventListener.canDetectOrientation()) {
            myOrientationEventListener.disable();
        }

        if (presenter != null) {
            presenter.destroy();
        }

        removeAutoFocusIdc();
    }

    @Override
    public void onBackPressed() {
        if (presenter != null && presenter.onBackPressed()) {
            super.onBackPressed();
        }
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

    @Override
    public void initializeViews() {
        cameraView = (CameraSurfaceView) query.id(R.id.camera_surfaceview).getView();

        cameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {
                            //click event has occurred
                            presenter.autoFocus(event, cameraView);
                        }
                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
        });

        FrameLayout content = (FrameLayout) query.id(R.id.camera_border).getView();
        View viewR = (View) query.id(R.id.viewRight).getView();
        View viewL = (View) query.id(R.id.viewLeft).getView();
        View viewU = (View) query.id(R.id.viewUp).getView();
        View viewD = (View) query.id(R.id.viewDown).getView();
        TextView txtIdCard = (TextView) query.id(R.id.txtIdCard).getView();
        if (needBorder) {
            content.setVisibility(View.VISIBLE);
            viewR.setVisibility(View.VISIBLE);
            viewL.setVisibility(View.VISIBLE);
            viewU.setVisibility(View.VISIBLE);
            viewD.setVisibility(View.VISIBLE);
            txtIdCard.setVisibility(View.VISIBLE);
        } else {
            content.setVisibility(View.GONE);
            viewR.setVisibility(View.GONE);
            viewL.setVisibility(View.GONE);
            viewU.setVisibility(View.GONE);
            viewD.setVisibility(View.GONE);
            txtIdCard.setVisibility(View.GONE);
        }

        cameraModeItem = new ModeCameraItem(this);
        reviewModeItem = new ModeReviewItem(this);
    }

    @Override
    public void cameraHasOpened() {
        SurfaceHolder holder = cameraView.getSurfaceHolder();
        presenter.startPreview(holder, width, height, quality);
    }

    @Override
    public void cameraMode(int cameraMode, String flashMode) {
        removeAutoFocusIdc();

        FrameLayout content = (FrameLayout) query.id(R.id.footer_content).getView();
        content.removeAllViews();

        cameraModeItem.bind((CameraPresenter) presenter, cameraMode, flashMode);
        content.addView(cameraModeItem);
    }

    @Override
    public void onCameraPreview(int width, int height) {
        AspectFrameLayout layout = (AspectFrameLayout) query.id(R.id.camera_frame).getView();
        layout.setAspectRatio((double) height / width);
    }

    @Override
    public void reviewMode(final byte[] data) {
        presenter.pausePreview();
        removeAutoFocusIdc();

        FrameLayout content = (FrameLayout) query.id(R.id.footer_content).getView();
        content.removeAllViews();

        reviewModeItem.bind((CameraPresenter) presenter, data);
        content.addView(reviewModeItem);
    }

    @Override
    public void setSupportCameraParams(boolean isSupportFrontCamera, List<String> flashMode) {
        if (cameraModeItem == null) {
            cameraModeItem = new ModeCameraItem(this);
        }
        cameraModeItem.bind(flashMode);

        if (myOrientationEventListener != null) return;
        myOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int arg0) {
                presenter.onOrientationChanged(arg0);
            }
        };

        if (myOrientationEventListener.canDetectOrientation()) {
            myOrientationEventListener.enable();
        }
    }

    @Override
    public void onAutoFocus(AutoFocusItem.FocusMode mode, MotionEvent event) {
        RelativeLayout container = (RelativeLayout) query.id(R.id.rl_auto_focus_container).getView();
        switch (mode) {
            case START_FOCUS:
                if (autoFocusItem != null) {
                    container.removeAllViews();
                }
                autoFocusItem = new AutoFocusItem(this);
                autoFocusItem.bind(AutoFocusItem.FocusMode.START_FOCUS, event.getX(), event.getY());
                container.addView(autoFocusItem);
                break;
            case FOCUS_FAILED:
                if (autoFocusItem != null) {
                    autoFocusItem.bind(AutoFocusItem.FocusMode.FOCUS_FAILED, event.getX(), event.getY());
                }
                break;
            case FOCUS_SUCCESS:
                if (autoFocusItem != null) {
                    autoFocusItem.bind(AutoFocusItem.FocusMode.FOCUS_SUCCESS, event.getX(), event.getY());
                }
                break;
            case FOCUS_CONTINUOUS:
                container.removeAllViews();
                break;
            default:
                break;
        }
    }

    @Override
    public void onOrientationChanged(int rotation) {
        if (cameraModeItem != null) cameraModeItem.orientationChange(rotation);
        if (reviewModeItem != null) reviewModeItem.orientationChange(rotation);
    }

    @Override
    public void onTakePicture(boolean safeTakePicture) {
        if (cameraModeItem != null) {
            cameraModeItem.bind(safeTakePicture);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    private void removeAutoFocusIdc() {
        RelativeLayout container = (RelativeLayout) query.id(R.id.rl_auto_focus_container).getView();
        container.removeAllViews();
    }

    public boolean isNeedBorder() {
        return needBorder;
    }

    public void setNeedBorder(boolean needBorder) {
        this.needBorder = needBorder;
    }
}
