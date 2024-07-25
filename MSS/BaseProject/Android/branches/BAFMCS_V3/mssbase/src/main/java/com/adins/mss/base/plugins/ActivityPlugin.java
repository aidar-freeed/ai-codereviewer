package com.adins.mss.base.plugins;

import android.app.Activity;
import android.content.Context;

import com.adins.mss.base.R;

/**
 * Created by Aditya Purwa on 1/5/2015.
 * Plugin for activity context.
 */
public abstract class ActivityPlugin extends Plugin {
    /**
     * Initialize a new instance of plugin.
     *
     * @param context Context for the plugin. Must be an activity.
     */
    protected ActivityPlugin(Context context) {
        super(context);
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException(getContext().getString(R.string.plugin_required));
        }
    }

    /**
     * Gets the context as an activity.
     *
     * @return Context as activity.
     */
    public Activity getContextAsActivity() {
        if (!(this.getContext() instanceof Activity)) {
            throw new IllegalArgumentException(getContext().getString(R.string.plugin_required));
        }
        return (Activity) getContext();
    }
}
