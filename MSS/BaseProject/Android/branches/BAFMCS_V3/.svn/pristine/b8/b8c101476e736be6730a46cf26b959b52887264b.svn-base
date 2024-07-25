package com.adins.mss.base.timeline;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;
import android.util.Log;

import com.adins.mss.constant.Global;
import com.adins.mss.dao.Timeline;

import java.util.List;

public class TimelineLoader extends AsyncTaskLoader<List<Timeline>> {
    public static final String TAG = "TIMELINE_AppListLoader";
    private List<Timeline> objects = null;
    private Context mContextLoader;

    public TimelineLoader(Context context) {
        super(context);
        mContextLoader = context;

    }

    @Override
    public List<Timeline> loadInBackground() {
        if (Global.IS_DEV) Log.i(TAG, "+++ loadInBackground() called! +++");
        TimelineManager manager = new TimelineManager(mContextLoader);
        objects = manager.getAllTimeline(mContextLoader);

        return objects;
    }

    @Override
    public void deliverResult(List<Timeline> result) {
        if (isReset()) {
            if (Global.IS_DEV)
                Log.w(TAG, "+++ Warning! An async query came in while the Loader was reset! +++");
            if (result != null) {
                return;
            }
        }

        objects = result;
        if (isStarted()) {
            if (Global.IS_DEV) Log.i(TAG, "+++ Delivering results to the LoaderManager for" +
                    " the ListFragment to display! +++");
            // If the Loader is in a started state, have the superclass deliver the
            // results to the client.
            super.deliverResult(result);
        }
    }

    @Override
    protected void onStartLoading() {
        if (Global.IS_DEV) Log.i(TAG, "+++ onStartLoading() called! +++");
        if (objects != null) {
            // Deliver any previously loaded data immediately.
            if (Global.IS_DEV) Log.i(TAG, "+++ Delivering previously loaded data to the client...");
            deliverResult(objects);
        }
        if (takeContentChanged()) {
            // When the observer detects a new installed application, it will call
            // onContentChanged() on the Loader, which will cause the next call to
            // takeContentChanged() to return true. If this is ever the case (or if
            // the current data is null), we force a new load.
            if (Global.IS_DEV)
                Log.i(TAG, "+++ A content change has been detected... so force load! +++");
            forceLoad();
        } else if (objects == null) {
            // If the current data is null... then we should make it non-null! :)
            if (Global.IS_DEV)
                Log.i(TAG, "+++ The current data is data is null... so force load! +++");
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        if (Global.IS_DEV) Log.i(TAG, "+++ onStopLoading() called! +++");
        cancelLoad();
    }

    @Override
    protected void onReset() {
        if (Global.IS_DEV) Log.i(TAG, "+++ onReset() called! +++");

        // Ensure the loader is stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'apps'.
        if (objects != null) {
            objects = null;
        }
    }

    @Override
    public void onCanceled(List<Timeline> apps) {
        if (Global.IS_DEV) Log.i(TAG, "+++ onCanceled() called! +++");

        // Attempt to cancel the current asynchronous load.
        super.onCanceled(apps);
    }

    @Override
    public void forceLoad() {
        if (Global.IS_DEV) Log.i(TAG, "+++ forceLoad() called! +++");
        super.forceLoad();
    }
}
