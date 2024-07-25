package com.adins.mss.base.errorhandler;

import android.content.Context;

import com.adins.mss.base.R;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.google.gson.JsonParseException;

import java.io.IOException;

/**
 * Created by intishar.fa on 31/10/2018.
 */

public class ErrorMessageHandler {
    IShowError iShowError;
    private int currentNotifType;

    public static final int TOAST_TYPE = 0;
    public static final int DIALOG_TYPE = 1;


    public ErrorMessageHandler(){}

    public ErrorMessageHandler(IShowError iShowError){
        this.iShowError = iShowError;
    }

    public int getCurrentNotifType() {
        return currentNotifType;
    }

    public void setCurrentNotifType(int currentNotifType) {
        this.currentNotifType = currentNotifType;
    }

    public void processError(Context context,HttpConnectionResult connectionResult, int notiftype){
        String errorMessage = "";
        String errorSubject = "";
        this.currentNotifType = notiftype;
        if(connectionResult != null){
            switch (connectionResult.getStatusCode()){
                case 400: errorMessage = context.getString(R.string.http_status_400);
                          break;
                case 401: errorMessage = context.getString(R.string.http_status_401);
                          break;
                case 403: errorMessage = context.getString(R.string.http_status_403);
                          break;
                case 404: errorMessage = context.getString(R.string.http_status_404);
                          break;
                case 408: errorMessage = context.getString(R.string.http_status_408);
                          break;
                case 409: errorMessage = context.getString(R.string.http_status_409);
                          break;
                case 500: errorMessage = context.getString(R.string.http_status_500);
                          break;
                case 502: errorMessage = context.getString(R.string.http_status_502);
                          break;
                case 503: errorMessage = context.getString(R.string.http_status_503);
                          break;
                default: errorMessage = connectionResult.getResult();
            }
        }
        if(iShowError != null){
            iShowError.showError(errorSubject,errorMessage, notiftype);
        }
    }

    public void processError(HttpConnectionResult connectionResult, String customErrorMsg, int notiftype){
        String errorMessage = "";
        String errorSubject = "Http Exception";
        this.currentNotifType = notiftype;
        if(connectionResult != null){
            if(customErrorMsg == null || customErrorMsg.equals("")){
                errorMessage = connectionResult.getResult();
            }
            else {
                errorMessage = customErrorMsg;
            }
        }
        if(iShowError != null){
            iShowError.showError(errorSubject,errorMessage,notiftype);
        }
    }

    public void processError(String errorSubject, String errorMsg, int notiftype){
        this.currentNotifType = notiftype;
        if(iShowError != null && errorMsg != null){
            iShowError.showError(errorSubject,errorMsg, notiftype);
        }
    }

    public void processError(Exception ex,int notiftype){
        String errorMsg = "";
        String errorSubject = "";
        this.currentNotifType = notiftype;
        if(ex != null){
            if(ex instanceof IOException){
                IOException exception = (IOException)ex;
                errorMsg = exception.getMessage();
                errorSubject = exception.toString();
            }
            else if(ex instanceof JsonParseException){
                JsonParseException exception = (JsonParseException)ex;
                errorMsg = exception.getMessage();
                errorSubject = exception.toString();
            }
            else {
                errorMsg = ex.getMessage();
                errorSubject = ex.toString();
            }
        }
        if(iShowError != null){
            iShowError.showError(errorSubject,errorMsg,notiftype);
        }
    }

    public void setiShowError(IShowError iShowError) {
        this.iShowError = iShowError;
    }
}
