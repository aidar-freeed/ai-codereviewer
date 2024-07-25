package com.adins.mss.base.plugins;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Aditya Purwa on 1/5/2015.
 * Used to create a fading action bar when the activity host a list view.
 */
public class FadingActionBarPlugin extends ActivityPlugin {

    /**
     * Background resource for the action bar.
     */
    private final int actionBarBackground;
    /**
     * List view header. Fading action bar will only work with a list view that have a header.
     */
    private final View listViewHeader;
    /**
     * List view to implement the fading action bar plugin to.
     */
    private final ListView listView;
    /**
     * Used to check whether the list view scroll has already passed the header.
     */
    private boolean isOutOfBounds;
    /**
     * Store reference to the action bar to apply the fade.
     */
    private ActionBar actionBar;
    /**
     * Store reference to the action bar background drawable.
     */
    private Drawable actionBarBackgroundDrawable;
    /**
     * Used to store the last alpha for the action bar background.
     * This is used to increase performance to prevent background invalidation.
     */
    private float lastAlpha;

    /**
     * Initialize a new instance of the fading action bar plugin.
     * This will make the action bar fade in when the list view is scrolled pass through its header.
     *
     * @param context             Context for the plugin. Must be an activity. To prevent unwanted graphical issue,
     *                            request for action bar overlay mode for your activity, as there is unlikely
     *                            any reason that you want a fading action bar for non overlay action bar.
     * @param listView            This could be any list view inside your activity.
     *                            Preferably this list view should be the only view inside your activity.
     *                            The list view must have a header added, leaving the list view header empty
     *                            will result in undesired results.
     * @param listViewHeader      The header that have been added to the list view.
     * @param actionBarBackground Resource id for the action bar drawable. This is the color that you want for your
     *                            action bar.
     */
    public FadingActionBarPlugin(Context context, ListView listView, View listViewHeader, int actionBarBackground) {
        super(context);
        this.listView = listView;
        this.listViewHeader = listViewHeader;
        this.actionBarBackground = actionBarBackground;
    }

    /**
     * Gets the action bar background resource.
     *
     * @return Action bar background resource id.
     */
    public int getActionBarBackground() {
        return actionBarBackground;
    }

    /**
     * Gets the list view for the plugin.
     *
     * @return List view.
     */
    public ListView getListView() {
        return listView;
    }

    @Override
    protected boolean checkSupport() {
        if (!(getContext() instanceof Activity)) {
            return false;
        }
        if (listView == null) {
            return false;
        }
        if (listViewHeader == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean apply() {
        if (!checkSupport()) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBarBackgroundDrawable = getContextAsActivity().getResources().getDrawable(actionBarBackground, getContext().getTheme());
        } else {
            actionBarBackgroundDrawable = getContextAsActivity().getResources().getDrawable(actionBarBackground);
        }
        setDrawableCallbackBelowJellyBeanMr2();

        actionBar = getContextAsActivity().getActionBar();
        actionBar.setBackgroundDrawable(actionBarBackgroundDrawable);


        final float actionBarSize = getActionBarSize();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View header = view.getChildAt(0);
                // The first child may be null, especially during initialization.
                if (header != null) {
                    float peak = listViewHeader.getHeight() - actionBarSize;
                    float top = Math.abs(header.getTop());

                    if (top >= peak || firstVisibleItem > 0) {
                        isOutOfBounds = true;
                        processActionBarAlpha(1);
                    } else {
                        isOutOfBounds = false;
                    }
                    if (!isOutOfBounds) {
                        float normal = Math.abs(header.getTop()) / peak;
                        normal = normal >= 1 ? 1 : normal;
                        processActionBarAlpha(normal);
                    }
                }
            }
        });

        return true;
    }

    /**
     * Get the size for the current active theme action bar.
     *
     * @return Size for the action bar, the height.
     */
    private float getActionBarSize() {
        TypedValue size = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, size, true);
        return size.getDimension(getContextAsActivity().getResources().getDisplayMetrics());
    }

    /**
     * Used to handle drawable issue below JELLY_BEAN_MR2.
     */
    private void setDrawableCallbackBelowJellyBeanMr2() {
        // Invalidation issue below JELLY_BEAN_MR2.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final Handler handler = new Handler();
            actionBarBackgroundDrawable.setCallback(new Drawable.Callback() {
                @Override
                public void invalidateDrawable(Drawable who) {
                    actionBar.setBackgroundDrawable(who);
                }

                @Override
                public void scheduleDrawable(Drawable who, Runnable what, long when) {
                    handler.postAtTime(what, when);
                }

                @Override
                public void unscheduleDrawable(Drawable who, Runnable what) {
                    handler.removeCallbacks(what);
                }
            });
        }
    }

    @Override
    public void cancel() {
        listView.setOnScrollListener(null);
        processActionBarAlpha(1);
    }

    /**
     * Process the action bar alpha and apply it to the action bar drawable.
     *
     * @param normal Normal value for the alpha.
     */
    private void processActionBarAlpha(float normal) {
        // Prevent background drawable invalidation to increase performance.
        if (normal == lastAlpha) {
            return;
        }
        actionBarBackgroundDrawable.setAlpha((int) (normal * 255));
        actionBarBackgroundDrawable.invalidateSelf();
        lastAlpha = normal;
    }
}
