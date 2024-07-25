package com.adins.mss.base.util;

import android.os.AsyncTask;

import java.util.Map;


/**
 * Generic implementation of AsyncTask which enable usage of AsyncTask without the hassle of creating a new class for each
 * implementation. It is done by creating an instance of GenericAsyncTask and pass the delegate (or callback) on constructor
 * <br>
 * The delegate is responsible of implementing onPreExecute(), doInBackground(), and onPostExecute(). Think of GenericTaskInterface
 * to AsyncTask is like Runnable to Thread
 * <p>
 * Same as AsyncTask, onPreExecute() and onPostExecute() runs on main thread, while doInBackground() runs on new thread. While in AsyncTask
 * we can customize the type of the parameters, GenericAsyncTask only communicate in String
 *
 * @author glen.iglesias
 */
public class GenericAsyncTask extends AsyncTask<String, String, String> {
    protected String errMessage = null;
    /**
     * A way to supply GenericAsyncTask with specific additional object
     */
    private Map<String, Object> additionalObject;
    private GenericTaskInterface delegate;

    public GenericAsyncTask(GenericTaskInterface delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        if (delegate != null) {
            delegate.onPreExecute(this);
        }
    }

    @Override
    protected String doInBackground(String... args) {
        String result = "";
        if (delegate != null) {
            result = delegate.doInBackground(this, args);
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (delegate != null) {
            delegate.onPostExecute(this, result, errMessage);
        }
    }

    public GenericTaskInterface getDelegate() {
        return delegate;
    }

    public void setDelegate(GenericTaskInterface delegate) {
        this.delegate = delegate;
    }

    public Map<String, Object> getAdditionalObject() {
        return additionalObject;
    }

    public void setAdditionalObject(Map<String, Object> additionalObject) {
        this.additionalObject = additionalObject;
    }

    public interface GenericTaskInterface {
        /**
         * Implementation of AsyncTask in GenericAsyncTask
         *
         * @param task
         */
        void onPreExecute(GenericAsyncTask task);

        String doInBackground(GenericAsyncTask task, String... args);

        void onPostExecute(GenericAsyncTask task, String result, String errMsg);
    }
}
