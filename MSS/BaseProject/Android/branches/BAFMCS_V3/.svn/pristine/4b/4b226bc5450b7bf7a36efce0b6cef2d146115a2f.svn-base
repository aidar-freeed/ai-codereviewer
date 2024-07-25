package com.adins.mss.base.commons;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Aditya Purwa on 1/6/2015.
 * Used to create activity based model.
 */
public abstract class ActivityModel extends ContextModel {
    /**
     * Initialize a new instance of context model.
     *
     * @param context The context for the model. Must be an activity.
     */
    public ActivityModel(Context context) {
        super(context);
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("ActivityModel only accept activity as the context.");
        }
    }

    /**
     * Get the model activity.
     *
     * @return Model activity.
     */
    public Activity getContextAsActivity() {
        if (!(getContext() instanceof Activity)) {
            throw new IllegalArgumentException("ActivityModel only accept activity as the context.");
        }
        return (Activity) getContext();
    }
}
