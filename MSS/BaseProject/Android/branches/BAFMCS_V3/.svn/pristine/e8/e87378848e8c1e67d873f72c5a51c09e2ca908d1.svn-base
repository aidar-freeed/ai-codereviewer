package com.adins.mss.coll.commons;

import android.os.AsyncTask;

public class SecondWorker extends AsyncTask<Object,Void,Object> {

    private SecondWorkListener listener;

    public SecondWorker(SecondWorkListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object... objects) {
        return null;
    }

    public interface SecondWorkListener {
        void onPreExecute();
        Object doWork();
        void onPostExecute(Object object);
    }
}
