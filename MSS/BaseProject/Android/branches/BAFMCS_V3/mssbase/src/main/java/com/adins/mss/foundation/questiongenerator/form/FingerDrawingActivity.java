package com.adins.mss.foundation.questiongenerator.form;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class FingerDrawingActivity extends Activity implements OnClickListener {
    public static final int IMG_SAVE_QUALITY = 80;
    private static final int PAINT_MENU_ID = Menu.FIRST;
    private static final int ERASE_MENU_ID = Menu.FIRST + 1;
    private static final int CLEAR_MENU_ID = Menu.FIRST + 2;
    private static final int SAVE_MENU_ID = Menu.FIRST + 3;
    private static final int BACK_MENU_ID = Menu.FIRST + 4;
    private DrawCanvas mView;
    private int colorbg = Color.BLACK;
    private int colorpen = Color.WHITE;
    private LinearLayout canvasLayout;
    private Button btnClear;
    private Button btnEdit;
    private Button btnErase;
    private Button btnSave;
    private Paint mPaint;
    private QuestionBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//        setContentView(mView);

        mPaint = new Paint();
        defaultPaint();

        //---change to portrait mode---
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toast.makeText(this, getString(R.string.infoDraw), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {

        super.onPause();
        Global.haveLogin = 1;
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

    public void defaultPaint() {
        btnEdit.setBackgroundResource(R.drawable.mediumpriority_background);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

		/*menu.add(0, PAINT_MENU_ID, 0, getString(R.string.mnPaint)).setShortcut('1', 'p');
        menu.add(0, ERASE_MENU_ID, 0, getString(R.string.mnErase)).setShortcut('2', 'e');
		menu.add(0, CLEAR_MENU_ID, 0, getString(R.string.mnClear)).setShortcut('3', 'c');
		menu.add(0, SAVE_MENU_ID, 0, getString(R.string.mnSave)).setShortcut('4', 's');
		menu.add(0, BACK_MENU_ID, 0, getString(R.string.btnBack)).setShortcut('5', 'x');*/

        /****
         * Is this the mechanism to extend with filter effects? Intent intent =
         * new Intent(null, getIntent().getData());
         * intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
         * menu.addIntentOptions( Menu.ALTERNATIVE, 0, new ComponentName(this,
         * NotesList.class), null, intent, 0, null);
         *****/
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {
            case PAINT_MENU_ID:
                defaultPaint();
                return true;
            case ERASE_MENU_ID:
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeCap(Paint.Cap.SQUARE);
                mPaint.setStrokeWidth(30);
                return true;
            case CLEAR_MENU_ID:
                mView.clear();
                defaultPaint();
                return true;
            case SAVE_MENU_ID:
                byte[] b = mView.getByteBitmap();

                DynamicFormActivity.saveImage(b);

                // set thumbnail
                if (DynamicFormActivity.getThumbInFocus() != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(b, 0, b.length);

                    int[] res = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
                    Bitmap thumbnail = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
                    DynamicFormActivity.setThumbInFocusImage(thumbnail);
                }

                FingerDrawingActivity.this.finish();
                return true;
            case BACK_MENU_ID:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            btnErase.setBackgroundResource(R.drawable.mediumpriority_background);
            btnEdit.setBackgroundResource(R.drawable.button_background);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//			mPaint.setColor(colorbg);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.SQUARE);
            mPaint.setStrokeWidth(30);
        } else if (id == R.id.btnSave) {
            try {
                byte[] b = mView.getByteBitmap();
                DynamicFormActivity.saveImage(b);
                // set thumbnail
                if (DynamicFormActivity.getThumbInFocus() != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(b, 0, b.length);

                    int[] res = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
                    Bitmap thumbnail = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
                    DynamicFormActivity.setThumbInFocusImage(thumbnail);
                }
            } catch (OutOfMemoryError e) {
                Toast.makeText(getApplicationContext(), "Processing failed,  Out of memory", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                FireCrash.log(e);
                // TODO: handle exception
            }

            FingerDrawingActivity.this.finish();
        } else if (id == R.id.btnEdit) {
            defaultPaint();
        }
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
                    savedImage = DynamicFormActivity.getQuestionInFocus().getImgAnswer();
                } catch (Exception e) {
                    FireCrash.log(e);

                }

                if (savedImage != null && savedImage.length > 0) {
                    Bitmap source = BitmapFactory.decodeByteArray(savedImage, 0, savedImage.length);
                    mCanvas.drawBitmap(source, 0, 0, mBitmapPaint);
                }
            } catch (OutOfMemoryError e) {
                // TODO: handle exception
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

        public byte[] getByteBitmap() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, IMG_SAVE_QUALITY, baos);
            byte[] b = baos.toByteArray();
            return b;
        }
    }
}
