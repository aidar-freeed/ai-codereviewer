package com.adins.mss.foundation.camerainapp;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adins.mss.base.R;
import com.adins.mss.foundation.camerainapp.helper.BaseLinearLayout;

/**
 * Created by angga.permadi on 8/4/2016.
 */
public class AutoFocusItem extends BaseLinearLayout {

    public AutoFocusItem(Context context) {
        super(context);
    }

    @Override
    protected void afterCreate() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.item_camera_auto_focus;
    }

    public void bind(FocusMode mMode, float x, float y) {
        ImageView iv = (ImageView) query.id(R.id.iv_auto_focus).getView();

        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
        Drawable d = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            d = getContext().getResources().getDrawable(R.drawable.ic_camera_auto_focus, getContext().getTheme());
        } else {
            d = getContext().getResources().getDrawable(R.drawable.ic_camera_auto_focus);
        }
        switch (mMode) {
            case START_FOCUS:
                // FIXME : image indicator autofocus jadi strech/kecil jika fokus di paling kanan atau bawah view
                RelativeLayout.LayoutParams params;
                params = new RelativeLayout.LayoutParams(300, 300);
                params.leftMargin = (int) x > 150 ? (int) x - 150 : 10;
                params.topMargin = (int) y > 150 ? (int) y - 150 : 10;
                iv.setLayoutParams(params);
                if (d != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        d.setColorFilter(getContext().getResources().getColor(android.R.color.black, getContext().getTheme()), mode);
                    } else {
                        d.setColorFilter(getContext().getResources().getColor(android.R.color.black), mode);
                    }
                }
                break;
            case FOCUS_FAILED:
                if (d != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        d.setColorFilter(getContext().getResources().getColor(R.color.autoFocusFailed, getContext().getTheme()), mode);
                    } else {
                        d.setColorFilter(getContext().getResources().getColor(R.color.autoFocusFailed), mode);
                    }
                }
                break;
            case FOCUS_SUCCESS:
                if (d != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        d.setColorFilter(getContext().getResources().getColor(R.color.autoFocusSuccess, getContext().getTheme()), mode);
                    } else {
                        d.setColorFilter(getContext().getResources().getColor(R.color.autoFocusSuccess), mode);
                    }
                }
                break;
            default:
                if (d != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        d.setColorFilter(getContext().getResources().getColor(android.R.color.black, getContext().getTheme()), mode);
                    } else {
                        d.setColorFilter(getContext().getResources().getColor(android.R.color.black), mode);
                    }
                }
                break;
        }
        iv.setImageDrawable(d);
    }

    public enum FocusMode {
        START_FOCUS,
        FOCUS_FAILED,
        FOCUS_SUCCESS,
        FOCUS_CONTINUOUS
    }
}
