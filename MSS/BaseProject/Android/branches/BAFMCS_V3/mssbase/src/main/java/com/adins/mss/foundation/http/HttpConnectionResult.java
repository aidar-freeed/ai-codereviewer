package com.adins.mss.foundation.http;

import android.util.Log;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;

public class HttpConnectionResult {


    /**
     * Status Code in HTTP Connection, for example '200' for success, and '500' for server exception
     */
    private int statusCode;

    /**
     * The phrase used for each status code, for example the description of 500 error as server exception
     */
    private String reasonPhrase;

    /**
     * The result string sent from server as a reply
     */
    private String result;

    /**
     * Simple info which is true if statusCode is 200, and false if else
     */
    private boolean OK;

    public HttpConnectionResult(StatusLine statusline, String result) {
        if (statusline != null) {
            this.statusCode = statusline.getStatusCode();
            this.reasonPhrase = statusline.getReasonPhrase();
            setOK(statusCode);
        }
        this.result = result;
    }

    public HttpConnectionResult(int code, String message, String result) {
        this.statusCode = code;
        this.reasonPhrase = message;
        setOK(statusCode);

        this.result = result;
    }

    public void printLog(String tag) {
        String msg;
        if (this.statusCode == HttpStatus.SC_OK) {
            msg = "Connection success";
        } else {
            msg = "Connection to server failed: " + this.statusCode + " " + this.reasonPhrase;
        }
        Log.i(tag, msg);
    }

    //=== Getter Setter ===//

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        setOK(statusCode);
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isOK() {
        return OK;
    }

    private void setOK(int statusCode) {
        if (statusCode == HttpStatus.SC_OK) {
            OK = true;
        } else {
            OK = false;
        }
    }

}
