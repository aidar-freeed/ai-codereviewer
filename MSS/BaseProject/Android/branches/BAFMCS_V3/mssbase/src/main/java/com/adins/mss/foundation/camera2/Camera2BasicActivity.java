package com.adins.mss.foundation.camera2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.foundation.camerainapp.CameraActivity;
import com.adins.mss.foundation.camerainapp.helper.ExifUtil;
import com.adins.mss.foundation.camerainapp.helper.FileUtil;
import com.adins.mss.foundation.image.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.hardware.camera2.CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH;
import static android.hardware.camera2.CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH;
import static android.hardware.camera2.CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START;
import static android.hardware.camera2.CameraMetadata.CONTROL_AE_STATE_FLASH_REQUIRED;
import static android.hardware.camera2.CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE;
import static android.hardware.camera2.CameraMetadata.CONTROL_AF_STATE_FOCUSED_LOCKED;
import static android.hardware.camera2.CaptureResult.CONTROL_AE_STATE;
import static android.hardware.camera2.CaptureResult.CONTROL_AE_STATE_CONVERGED;
import static android.hardware.camera2.CaptureResult.CONTROL_AE_STATE_PRECAPTURE;
import static android.hardware.camera2.CaptureResult.CONTROL_AF_STATE;
import static android.hardware.camera2.CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED;
import static android.hardware.camera2.CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED;
import static android.hardware.camera2.CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED;

/**
 * Created by ahmadkamilalmasyhur on 25/01/2018.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2BasicActivity extends Fragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String PICTURE_WIDTH = "picture_width";
    public static final String PICTURE_HEIGHT = "picture_height";
    public static final String PICTURE_QUALITY = "picture_quality";
    public static final String PICTURE_URI = "picture_path";
    public static final int PICTURE_WIDTH_DEF = 1024;
    public static final int PICTURE_HEIGHT_DEF = 768;
    public static final int PICTURE_QUALITY_DEF = 70;
    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;
    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;
    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;
    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;
    /**
     * Camera state: Picture was taken by front camera.
     */
    private static final int STATE_PICTURE_TAKEN_FRONT = 5;
    private static int width;
    private static int height;
    private static int quality;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 270);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 90);
    }

    private String[] cameraIdString = {"Front", "Rear"};
    private String[] flashString = {"On", "Off"};
    private boolean isCameraFacingBack = true;
    private boolean isFlashOn = false;
    private byte[] picture = null;
    private boolean isManualAutoFocus = false;
    private boolean isGetFocus = false;
    private boolean isHasGotPicture = false;
    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            try {
                image = reader.acquireLatestImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);
                onPictureTaken(bytes);
            } catch (Exception ee) {
                FireCrash.log(ee);
            } finally {
                if (image != null)
                    image.close();
            }
        }

    };
    private OrientationEventListener myOrientationEventListener;
    private CameraCharacteristics mCameraCharacteristics;
    private CameraManager mCameraManager;
    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;
    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;
    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;
    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;
    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size mPreviewSize;
    private CaptureRequest.Builder captureRequestBuilder;
    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;
    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;
    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;
    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mCaptureRequestBuilder;
    /**
     * {@link CaptureRequest} generated by {@link #mCaptureRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;
    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int mState = STATE_PREVIEW;
    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    /**
     * Whether the current camera device supports Flash or not.
     */
    private boolean isFlashSupported;
    /**
     * Orientation of the camera sensor
     */
    private int mSensorOrientation;
    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            if (isCameraFacingBack) {
                if (isManualAutoFocus) {
                    Integer afState = result.get(CONTROL_AF_STATE);
                    if (afState == null) {
                        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);
                        setFlash(mCaptureRequestBuilder);
                        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
                        try {
                            mCaptureSession.capture(mCaptureRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            FireCrash.log(e);
                            e.printStackTrace();
                            Log.i("updatePreview", "ExceptionExceptionException");
                        }
                    } else if (afState == CONTROL_AF_STATE_FOCUSED_LOCKED ||
                            afState == CONTROL_AF_STATE_PASSIVE_FOCUSED) {
                        try {
                            isGetFocus = true;
                            Thread.sleep(1000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ImageView iv = (ImageView) getActivity().findViewById(R.id.iv_auto_focus);
                                        iv.setVisibility(View.VISIBLE);
                                        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
                                        Drawable d = getResources().getDrawable(R.drawable.ic_camera_auto_focus);
                                        if (d != null)
                                            d.setColorFilter(getActivity().getResources().getColor(R.color.autoFocusSuccess), mode);
                                        iv.setImageDrawable(d);

                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            FireCrash.log(e);
                            e.printStackTrace();
                        }
                    } else if (afState == CONTROL_AF_STATE_NOT_FOCUSED_LOCKED ||
                            afState == CONTROL_AF_STATE_PASSIVE_UNFOCUSED) {
                        try {
                            isGetFocus = false;
                            Thread.sleep(1000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ImageView iv = (ImageView) getActivity().findViewById(R.id.iv_auto_focus);
                                        iv.setVisibility(View.VISIBLE);
                                        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
                                        Drawable d = getResources().getDrawable(R.drawable.ic_camera_auto_focus);
                                        if (d != null)
                                            d.setColorFilter(getActivity().getResources().getColor(R.color.autoFocusFailed), mode);
                                        iv.setImageDrawable(d);

                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            FireCrash.log(e);
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(1500);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ImageView iv = (ImageView) getActivity().findViewById(R.id.iv_auto_focus);
                                    iv.setVisibility(View.GONE);
                                    isManualAutoFocus = false;
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                }
                            }
                        });
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }

                }

                switch (mState) {
                    case STATE_PREVIEW:
                        // We have nothing to do when the camera preview is working normally.
                        break;
                    case STATE_WAITING_LOCK:
                        if (isGetFocus) {
                            captureStillPicture();
                            isGetFocus = false;
                            return;
                        }
                        Integer afState = result.get(CONTROL_AF_STATE);
                        if (afState == null) {
                            captureStillPicture();
                        } else if (CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                                CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                            // CONTROL_AE_STATE can be null on some devices
                            Integer aeState = result.get(CONTROL_AE_STATE);
                            if (aeState == null ||
                                    aeState == CONTROL_AE_STATE_CONVERGED) {
                                mState = STATE_PICTURE_TAKEN;
                                isGetFocus = true;
                                captureStillPicture();
                            } else {
                                runPrecaptureSequence();
                            }
                        }
                        break;
                    case STATE_WAITING_PRECAPTURE:
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CONTROL_AE_STATE_PRECAPTURE ||
                                aeState == CONTROL_AE_STATE_FLASH_REQUIRED) {
                            mState = STATE_WAITING_NON_PRECAPTURE;
                            isGetFocus = false;
                        }
                        break;
                    case STATE_WAITING_NON_PRECAPTURE:
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aState = result.get(CONTROL_AE_STATE);
                        if (aState == null || aState != CONTROL_AE_STATE_PRECAPTURE) {
                            mState = STATE_PICTURE_TAKEN;
                            isGetFocus = true;
                            captureStillPicture();
                        }
                        break;
                    default:
                        break;
                }
            } else {
                if (mState == STATE_PICTURE_TAKEN_FRONT) {
                    captureStillPicture();
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }
    };
    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            if (error == CameraDevice.StateCallback.ERROR_CAMERA_IN_USE ||
                    error == CameraDevice.StateCallback.ERROR_MAX_CAMERAS_IN_USE) {
                showToast(getResources().getString(R.string.error_camera_in_use));
            } else if (error == CameraDevice.StateCallback.ERROR_CAMERA_DISABLED) {
                showToast(getResources().getString(R.string.error_camera_disabled));
            } else if (error == CameraDevice.StateCallback.ERROR_CAMERA_DEVICE ||
                    error == CameraDevice.StateCallback.ERROR_CAMERA_SERVICE) {
                showToast(getResources().getString(R.string.error_camera_device));
            }
            Activity activity = getActivity();
            if (null != activity) {
                Intent intent = new Intent();
                intent.putExtra("error_code", error);
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        }

    };
    private String cbCameraMode = "Rear";
    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            //EMPTY
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
            //EMPTY
        }

    };

    public Camera2BasicActivity() {
        //EMPTY
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (!bigEnough.isEmpty()) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (!notBigEnough.isEmpty()) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }

    public static Camera2BasicActivity newInstance() {
        return new Camera2BasicActivity();
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    void onPictureTaken(byte[] image) {
        picture = image;
        isHasGotPicture = true;
    }

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera2_basic, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        view.findViewById(R.id.btn_takepicture).setOnClickListener(this);
        view.findViewById(R.id.btn_flash_mode).setOnClickListener(this);
        view.findViewById(R.id.cb_camera_mode).setOnClickListener(this);
        view.findViewById(R.id.btn_save_picture).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.item_camera_mode).setVisibility(View.VISIBLE);
        view.findViewById(R.id.item_review_mode).setVisibility(View.GONE);
        view.findViewById(R.id.item_camera_auto_focus).setVisibility(View.GONE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            width = bundle.getInt(PICTURE_WIDTH);
            height = bundle.getInt(PICTURE_HEIGHT);
            quality = bundle.getInt(PICTURE_QUALITY);
        }

        if (width <= 0) width = PICTURE_WIDTH_DEF;
        if (height <= 0) height = PICTURE_HEIGHT_DEF;
        if (quality <= 0) quality = PICTURE_QUALITY_DEF;
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
        mTextureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isCameraFacingBack && !isManualAutoFocus && !isHasGotPicture) {
                            isManualAutoFocus = true;
                            Rect rect = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                            int areaSize = 200;
                            int right = rect.right;
                            int bottom = rect.bottom;
                            int viewWidth = mTextureView.getWidth();
                            int viewHeight = mTextureView.getHeight();
                            int ll, rr;
                            Rect newRect;
                            int centerX = (int) event.getX();
                            int centerY = (int) event.getY();
                            ll = ((centerX * right) - areaSize) / viewWidth;
                            rr = ((centerY * bottom) - areaSize) / viewHeight;
                            int focusLeft = clamp(ll, 0, right);
                            int focusBottom = clamp(rr, 0, bottom);
                            newRect = new Rect(focusLeft, focusBottom, focusLeft + areaSize, focusBottom + areaSize);
                            MeteringRectangle meteringRectangle = new MeteringRectangle(newRect, 1000);
                            MeteringRectangle[] meteringRectangleArr = {meteringRectangle};
                            try {
                                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                                mCaptureSession.capture(mCaptureRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                FireCrash.log(e);
                                e.printStackTrace();
                                Log.i("updatePreview", "ExceptionExceptionException");
                            }
                            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, meteringRectangleArr);
                            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);
                            setFlash(mCaptureRequestBuilder);
                            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
                            try {
                                mCaptureSession.capture(mCaptureRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                FireCrash.log(e);
                                e.printStackTrace();
                                Log.i("updatePreview", "ExceptionExceptionException");
                            }
                            try {
                                ImageView iv = (ImageView) getActivity().findViewById(R.id.iv_auto_focus);
                                view.findViewById(R.id.item_camera_auto_focus).setVisibility(View.VISIBLE);
                                iv.setVisibility(View.VISIBLE);
                                PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
                                Drawable d = getResources().getDrawable(R.drawable.ic_camera_auto_focus);
                                RelativeLayout.LayoutParams params;
                                params = new RelativeLayout.LayoutParams(300, 300);
                                params.leftMargin = (int) event.getX() > 150 ? (int) event.getX() - 150 : 10;
                                params.topMargin = (int) event.getY() > 150 ? (int) event.getY() - 150 : 10;
                                iv.setLayoutParams(params);
                                if (d != null)
                                    d.setColorFilter(getActivity().getResources().getColor(android.R.color.black), mode);
                                iv.setImageDrawable(d);
                            } catch (Exception e) {
                                FireCrash.log(e);
                                e.printStackTrace();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        setOrientationsListener();
    }

    void setOrientationsListener() {
        if (myOrientationEventListener != null) return;
        myOrientationEventListener = new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int arg0) {
                arg0 = (arg0 + 45) / 90 * 90;
                int currentRotation;
                if (!isCameraFacingBack) {
                    currentRotation = (arg0 + 360) % 360;
                } else {  // back-facing camera
                    currentRotation = (arg0) % 360;
                }
                mSensorOrientation = currentRotation;
            }
        };
        if (myOrientationEventListener.canDetectOrientation()) {
            myOrientationEventListener.enable();
        }
    }

    private int clamp(int x, int min, int max) {
        if (x < min) {
            return min;
        } else if (x > max) {
            return max;
        } else {
            return x;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        if (myOrientationEventListener != null && myOrientationEventListener.canDetectOrientation()) {
            myOrientationEventListener.disable();
        }

        mTextureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        super.onPause();
    }

    private void requestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            return;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        mCameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : mCameraManager.getCameraIdList()) {
                if (cbCameraMode.equalsIgnoreCase("Rear")) {
                    mCameraId = findBackFacingCameraId();
                } else if (cbCameraMode.equalsIgnoreCase("Front")) {
                    mCameraId = findFrontFacingCameraId();
                }
                mCameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
                StreamConfigurationMap map = mCameraCharacteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                mImageReader = ImageReader.newInstance(this.width, this.height,
                        ImageFormat.JPEG, /*maxImages*/1);
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                mSensorOrientation = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                width = this.width;
                height = this.height;

                maxPreviewHeight = height;
                maxPreviewWidth = width;
                rotatedPreviewHeight = height;
                rotatedPreviewWidth = width;

                if (maxPreviewWidth > PICTURE_WIDTH_DEF) {
                    maxPreviewWidth = PICTURE_WIDTH_DEF;
                }

                if (maxPreviewHeight > PICTURE_HEIGHT_DEF) {
                    maxPreviewHeight = PICTURE_HEIGHT_DEF;
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[Integer.parseInt(mCameraId)];

                // Check if the flash is supported.
                Boolean available = mCameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                isFlashSupported = available == null ? false : available;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
        }
    }

    /**
     * Opens the camera specified by {}.
     */
    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }

        setUpCameraOutputs(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mCaptureRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(surface);
            mCaptureRequestBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) quality);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                setFlash(mCaptureRequestBuilder);

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mCaptureRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                            getActivity().findViewById(R.id.btn_cancel).setClickable(true);
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            showToast("Failed to Configure Camera");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate((float)(90 * (rotation - 2)), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    /**
     * Initiate a still image capture.
     */
    private void takePicture() {
        lockFocus();
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try {
            if (isCameraFacingBack) {
                // This is how to tell the camera to lock focus.
                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                        CameraMetadata.CONTROL_AF_TRIGGER_START);
                // Tell #mCaptureCallback to wait for the lock.
                if (isManualAutoFocus) {
                    captureStillPicture();
                    return;
                } else {
                    mState = STATE_WAITING_LOCK;
                }
            } else {
                mState = STATE_PICTURE_TAKEN_FRONT;
            }
            mCaptureSession.capture(mCaptureRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mCaptureRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        try {
            if (!isCameraFacingBack) {
                mState = STATE_PREVIEW;
            }
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(mImageReader.getSurface());

//            // Use the same AE and AF modes as the preview.
//            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            setFlash(captureRequestBuilder);

            if (isCameraFacingBack) {
                int orientation = mSensorOrientation + 90;
                orientation %= 360; // hasil tidak boleh 360, harus 0, 90, 180, 270
                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, (orientation));
            } else {
                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(mSensorOrientation));
            }
            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    showToast("Finish");
//                    Log.d(TAG, mFile.toString());
                    unlockFocus();
                }
            };
// what to do here
            if (isGetFocus) {
                mCaptureSession.stopRepeating();
                mCaptureSession.capture(captureRequestBuilder.build(), null, null);
            } else {
                if (!isCameraFacingBack) {
                    mCaptureSession.stopRepeating();
                    mCaptureSession.capture(captureRequestBuilder.build(), null, null);
                }
                runPrecaptureSequence();
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private int getJpegOrientation(CameraCharacteristics c, int deviceOrientation, int sensorRotation, int sensorOrientation, int mSensorOrientation) {
        int newRotation = 0;
        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN)
            return 0;
        if (isCameraFacingBack) {
//            Log.d("What", String.valueOf(mSensorOrientation));
            switch (mSensorOrientation) {
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
            mSensorOrientation = newRotation;
        }
        mSensorOrientation = (mSensorOrientation + 45) / 90 * 90;
        int jpegOrientation = (mSensorOrientation + 360) % 360;
        return jpegOrientation;
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        int x = rotation;
        switch (rotation) {
            case 0:
                x = 270;
                break;
            case 90:
                x = 180;
                break;
            case 180:
                x = 90;
                break;
            case 270:
                x = 0;
                break;
            default:
                x = 0;
                break;
        }
        return x;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setFlash(mCaptureRequestBuilder);
            mCaptureSession.capture(mCaptureRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);


            getActivity().findViewById(R.id.item_camera_mode).setVisibility(View.GONE);
            getActivity().findViewById(R.id.item_review_mode).setVisibility(View.VISIBLE);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_takepicture) {
            view.setClickable(false);
            takePicture();
            getActivity().findViewById(R.id.item_camera_mode).setVisibility(View.GONE);
            getActivity().findViewById(R.id.item_review_mode).setVisibility(View.VISIBLE);
        } else if (i == R.id.btn_flash_mode) {
            view.setClickable(false);
            String tag = view.getTag().toString();
            if (tag.equalsIgnoreCase(flashString[0])) { //on
                tag = flashString[1];
                view.setTag(flashString[1]);
            } else if (tag.equalsIgnoreCase(flashString[1])) { //off
                tag = flashString[0];
                view.setTag(flashString[0]);
            }
            switch (tag) {
                case "On":
                    view.setBackground(getResources().getDrawable(R.drawable.ic_camera_flash_on, getActivity().getTheme()));
                    turnOnFlashLight();
                    break;
                case "Off":
                    view.setBackground(getResources().getDrawable(R.drawable.ic_camera_flash_off, getActivity().getTheme()));
                    turnOffFlashLight();
                    break;
                default:
                    view.setVisibility(View.GONE);
                    turnOffFlashLight();
                    break;
            }
            view.setClickable(true);
        } else if (i == R.id.btn_save_picture) {
            view.setClickable(false);
            try {
                if (picture != null) {
                    Bitmap b = Utils.byteToBitmap(picture);//BitmapFactory.decodeByteArray(data, 0, data.length);
                    Uri path = null;
                    if (b != null) {
                        File file = FileUtil.bitmapToFileConverter(getActivity(), b);
                        long length = file.length() / 1024; // Size in KB
                        if (length > 500) {
                            b = scaleDown(b, 500, false);
                            file = FileUtil.bitmapToFileConverter(getActivity(), b);
                        }
                        ExifUtil.setRotationToFileExif(file, ExifUtil.getOrientation(picture));
                        path = Uri.fromFile(file);
                        b.recycle();
                    }

                    Intent intent = new Intent();
                    if (path == null) {
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                        return;
                    }

                    intent.putExtra(CameraActivity.PICTURE_URI, path.toString());
                    Bundle bundle = new Bundle();
                    bundle.putString(CameraActivity.PICTURE_URI, path.toString());
                    intent.putExtras(bundle);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                } else {
                    getActivity().findViewById(R.id.item_camera_mode).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.item_review_mode).setVisibility(View.GONE);
                }
            } catch (Exception e) {
                FireCrash.log(e);
            }
        } else if (i == R.id.btn_cancel) {
            view.setClickable(false);
            getActivity().findViewById(R.id.btn_takepicture).setClickable(true);
            onCancelSave();
        } else if (i == R.id.cb_camera_mode) {
            view.setClickable(false);
            String tag = view.getTag().toString();
            if (tag.equalsIgnoreCase(cameraIdString[0])) { //front
                tag = cameraIdString[1];
                view.setTag(cameraIdString[1]);
            } else if (tag.equalsIgnoreCase(cameraIdString[1])) { //rear
                tag = cameraIdString[0];
                view.setTag(cameraIdString[0]);
            }
            cbCameraMode = tag;
            switch (tag) {
                case "Front":
                    getActivity().findViewById(R.id.btn_flash_mode).setVisibility(View.GONE);
                    view.setBackground(getResources().getDrawable(R.drawable.ic_camera_front, getActivity().getTheme()));
                    try {
                        changeCamera(tag);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                        view.setTag(cameraIdString[0]); //keep front
                    }
                    break;
                case "Rear":
                    getActivity().findViewById(R.id.btn_flash_mode).setVisibility(View.VISIBLE);
                    view.setBackground(getResources().getDrawable(R.drawable.ic_camera_rear, getActivity().getTheme()));
                    try {
                        changeCamera(tag);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        view.setTag(cameraIdString[1]); //keep rear
                        e.printStackTrace();
                    }
                    break;
                default:
                    view.setVisibility(View.GONE);
                    view.setTag(cameraIdString[0]);
                    break;
            }
        }
        view.setClickable(true);
    }

    void onCancelSave() {
        getActivity().findViewById(R.id.item_camera_mode).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.item_review_mode).setVisibility(View.GONE);
        picture = null;
        isHasGotPicture = false;
        mState = STATE_PREVIEW;
        createCameraPreviewSession();
    }

    public void changeCamera(String data) throws CameraAccessException {
        if (data.equalsIgnoreCase("Front")) {
            mCameraId = findFrontFacingCameraId();
            closeCamera();
            if (mTextureView.isAvailable()) {
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            } else {
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            }
            isCameraFacingBack = false;
        } else if (data.equalsIgnoreCase("Rear")) {
            mCameraId = findBackFacingCameraId();
            closeCamera();
            if (mTextureView.isAvailable()) {
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            } else {
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            }
            isCameraFacingBack = true;
        }
    }

    private String findFrontFacingCameraId() throws CameraAccessException { //kamera depan
        String[] cameraIds = mCameraManager.getCameraIdList();

        for (String cameraId : cameraIds) {
            mCameraCharacteristics = this.mCameraManager.getCameraCharacteristics(cameraId);

            if (CameraMetadata.LENS_FACING_FRONT == mCameraCharacteristics.get(CameraCharacteristics.LENS_FACING).intValue()) {
                Log.i("MainActivity", String.format("%s: %s", "findFrontFacingCameraId", cameraId));
                return cameraId;
            }
        }

        if (cameraIds.length > 0) {
            return cameraIds[0];
        }

        return null;
    }

    private String findBackFacingCameraId() throws CameraAccessException { //kamera belakang
        String[] cameraIds = this.mCameraManager.getCameraIdList();

        for (String cameraId : cameraIds) {
            mCameraCharacteristics = this.mCameraManager.getCameraCharacteristics(cameraId);

            if (CameraMetadata.LENS_FACING_BACK == mCameraCharacteristics.get(CameraCharacteristics.LENS_FACING).intValue()) {
                Log.i("MainActivity", String.format("%s: %s", "findFrontFacingCameraId", cameraId));
                return cameraId;
            }
        }

        if (cameraIds.length > 0) {
            return cameraIds[0];
        }

        return null;
    }


    public void turnOnFlashLight() {
        isFlashOn = true;
    }

    public void turnOffFlashLight() {
        isFlashOn = false;
    }

    private void setFlash(CaptureRequest.Builder requestBuilder) {
        if (isFlashSupported) {
            if (isFlashOn) {
                requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                        CONTROL_AE_MODE_ON_ALWAYS_FLASH);
            } else {
                requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                        CONTROL_AE_MODE_ON_AUTO_FLASH);
            }
        }
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        public ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            try(FileOutputStream output = new FileOutputStream(mFile)) {
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
            }
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }
}
