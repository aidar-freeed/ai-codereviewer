package com.adins.mss.foundation.camerainapp.helper;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.adins.mss.base.util.Interpolator;
import com.androidquery.AQuery;

/**
 * Created by angga.permadi on 7/27/2016.
 */
public abstract class BaseLinearLayout extends LinearLayout {
    protected AQuery query;

    public BaseLinearLayout(Context context) {
        super(context);
        inflate(context);

        setId((int) (System.currentTimeMillis() / 1000));

        afterCreate();
    }

    private void inflate(Context context) {
        log("on Inflate");
        View view = inflate(context, getLayoutResId(), this);

        if (query == null) {
            query = new AQuery(view);
        } else {
            query = query.recycle(view);
        }
    }

    protected ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Interpolator.createInterpolator(Interpolator.LINEAR_INTERPOLATOR));
        return animator;
    }

    protected abstract void afterCreate();

    protected abstract int getLayoutResId();

    protected void log(String message) {
        Logger.d(this, message);
    }
}
