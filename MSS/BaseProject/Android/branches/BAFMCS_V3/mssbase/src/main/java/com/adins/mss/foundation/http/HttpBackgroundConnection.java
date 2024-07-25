package com.adins.mss.foundation.http;

import android.os.AsyncTask;

import com.adins.mss.base.crashlytics.FireCrash;

/**
 * An AsyncTask specified for running HttpConnection in background thread, and return result within callback. It compliments HttpConnection,
 * not a substitution
 *
 * @author glen.iglesias
 */
public class HttpBackgroundConnection extends AsyncTask<String, Float, HttpConnectionResult> {

    private String url;
    private String data;
    private HttpConnection connection;
    private HttpBackgroundConnectionListener listener;


    public HttpBackgroundConnection(String url, String data,
                                    HttpConnection connection, HttpBackgroundConnectionListener listener) {
        super();
        this.url = url;
        this.data = data;
        this.connection = connection;
        this.listener = listener;
    }

    @Override
    protected HttpConnectionResult doInBackground(String... arg0) {

        HttpConnectionResult result;
        try {
            result = connection.requestHTTPPost(url, data);
        } catch (Exception e) {
            FireCrash.log(e);
            //If I were right, this would never be called, because even if requestHTTPPost got exception, it return result
            //instead
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    @Override
    protected void onPostExecute(HttpConnectionResult result) {
        listener.onConnectionResult(this, connection, result);
    }

    public interface HttpBackgroundConnectionListener {
        public void onConnectionResult(HttpBackgroundConnection connTask, HttpConnection conn, HttpConnectionResult result);
    }

}
