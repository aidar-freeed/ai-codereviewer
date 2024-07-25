package com.adins.mss.base.dynamicform.form.questions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * Created by gigin.ginanjar on 01/09/2016.
 */
public class DrawingCanvasActivity extends FragmentActivity implements View.OnClickListener {
    public static final int IMG_SAVE_QUALITY = 80;
    public static final String BUND_KEY_IMAGE_RESULT = "com.adins.mss.base.dynamicform.form.questions.BUND_KEY_IMAGE_RESULT";
    public static QuestionBean bean;
    private DrawCanvas mView;
    private int colorbg = Color.BLACK;
    private int colorpen = Color.WHITE;
    private LinearLayout canvasLayout;
    private Button btnClear;
    private Button btnEdit;
    private Button btnErase;
    private Button btnSave;
    private Paint mPaint;

    private FirebaseAnalytics screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.drawing_layout);
        mView = new DrawCanvas(this);
        canvasLayout = (LinearLayout) findViewById(R.id.canvasLayout);
        canvasLayout.addView(mView);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnErase = (Button) findViewById(R.id.btnErase);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnClear.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnErase.setOnClickListener(this);
        btnSave.setOnClickListener(this);


        mPaint = new Paint();
        defaultPaint();

        Toast.makeText(this, getString(R.string.infoDraw), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this, getString(R.string.screen_name_signature_canvas), null);
    }

    @Override
    protected void onPause() {

        super.onPause();
        Global.haveLogin = 1;
    }

    public void defaultPaint() {
        btnEdit.setBackgroundResource(R.drawable.button_background2);
        btnErase.setBackgroundResource(R.drawable.button_background);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(colorpen);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);
    }

    public void setColorPen(int color) {
        mPaint.setColor(color);
    }

    @Override
    public void onClick(View v) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);
        int id = v.getId();
        if (id == R.id.btnClear) {
            mView.clear();
            defaultPaint();
        } else if (id == R.id.btnErase) {
            btnErase.setBackgroundResource(R.drawable.button_background2);
            btnEdit.setBackgroundResource(R.drawable.button_background);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.SQUARE);
            mPaint.setStrokeWidth(30);
        } else if (id == R.id.btnSave) {
            try {
                byte[] b = mView.getByteBitmap();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putByteArray(BUND_KEY_IMAGE_RESULT, b);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
            } catch (OutOfMemoryError e) {
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.processing_failed), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                FireCrash.log(e);
            }
            mView.recycleView();
            finish();
        } else if (id == R.id.btnEdit) {
            defaultPaint();
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
    protected void onDestroy() {
        super.onDestroy();
        DrawingCanvasActivity.bean = null;
        Utility.freeMemory();
    }

    public class DrawCanvas extends View {
        private static final float TOUCH_TOLERANCE = 4;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        private float mX, mY;

        public DrawCanvas(Context c) {
            super(c);
            // --
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int widht = displaymetrics.widthPixels;

            try {
                mBitmap = Bitmap.createBitmap(widht, height, Bitmap.Config.RGB_565);
                mCanvas = new Canvas(mBitmap);
                mPath = new Path();
                mBitmapPaint = new Paint(Paint.DITHER_FLAG);

                byte[] savedImage = null;
                try {
                    savedImage = bean.getImgAnswer();
                } catch (Exception e) {
                    FireCrash.log(e);
                }

                if (savedImage != null && savedImage.length > 0) {
                    Bitmap source = BitmapFactory.decodeByteArray(savedImage, 0, savedImage.length);
                    mCanvas.drawBitmap(source, 0, 0, mBitmapPaint);
                }
            } catch (OutOfMemoryError e) {
                FireCrash.log(e);
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(colorbg);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
                default:
                    break;
            }
            return true;
        }

        // -----
        public void clear() {
            if (mCanvas != null) {
                mCanvas.drawColor(colorbg);
                invalidate();
            }
        }

        public void recycleView() {
            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
            }
            if (mCanvas != null) {
                mCanvas = null;
                mPath = null;
                mBitmapPaint = null;
            }
        }

        public byte[] getByteBitmap() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, IMG_SAVE_QUALITY, baos);
            byte[] b = baos.toByteArray();
            return b;
        }
    }
}
