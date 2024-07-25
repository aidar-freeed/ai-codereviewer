package com.adins.mss.foundation.http.net;

import android.content.Context;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Handle http client request.
 */
public class HttpClient {
    protected final OkHttpClient client;
    protected final Context context;

    /**
     * Initialize a new instance of HTTP client.
     *
     * @param context Context for the client.
     */
    public HttpClient(Context context) {
        this.context = context;
        client = new OkHttpClient();
    }

    public HttpClient() {
        this.context = null;
        client = new OkHttpClient();
    }

    public void setConnectionTimeout(long timeout) {
        this.client.setConnectTimeout(timeout, TimeUnit.MILLISECONDS);
        this.client.setReadTimeout(timeout, TimeUnit.MILLISECONDS);
        this.client.setWriteTimeout(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Build a request object, that can be later executed by the client.
     *
     * @param url The target url to request.
     * @return A request builder.
     */
    public Request.Builder request(String url) {
        return new Request.Builder().url(url);
    }

    /**
     * Build a request object, that can be later executed by the client.
     *
     * @param url The target url to request.
     * @return A request builder.
     */
    public Request.Builder request(URL url) {
        return request(url.toString());
    }

    /**
     * Prepare a request, this method allows you to get the invoker to invoke the request manually.
     *
     * @param request The request to execute.
     * @return A request invoker.
     */
    public Call prepare(Request request) {
        return client.newCall(request);
    }

    /**
     * Execute a request, you won't have control over the invoker using this method.
     * Note that this method runs on current thread, to run the request execution asynchronously,
     * use enqueue.
     *
     * @param request The request to execute.
     * @return A response.
     * @throws java.io.IOException
     */
    public Response execute(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    /**
     * Enqueue a request execution, this will invoke the request later if the resource for
     * invoking the request is available. Though the invoker is automatically invoked, this method
     * returns a request invoker so you can deal with the invoker later.
     *
     * @param request  The request to execute.
     * @param callback Callback when the request completed (failed, or succeeded)
     * @return A request invoker.
     * @throws java.io.IOException
     */
    public Call enqueue(Request request, Callback callback) throws IOException {
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

}
