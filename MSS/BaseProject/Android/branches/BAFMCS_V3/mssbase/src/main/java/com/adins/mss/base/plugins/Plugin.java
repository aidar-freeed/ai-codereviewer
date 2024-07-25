package com.adins.mss.base.plugins;

import android.content.Context;

/**
 * Created by Aditya Purwa on 1/5/2015.
 * Plugin.
 */
public abstract class Plugin {
    private final Context context;

    /**
     * Initialize a new instance of plugin.
     *
     * @param context Context for the plugin.
     */
    protected Plugin(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    /**
     * When overridden in a derived class, used to check whether the given context could be plugged.
     *
     * @return True if supported, false otherwise.
     */
    protected abstract boolean checkSupport();

    /**
     * Apply the plugin.
     *
     * @return True if applied successfully, false otherwise.
     */
    public abstract boolean apply();

    /**
     * Cancel the application of the plugin.
     */
    public abstract void cancel();
}
