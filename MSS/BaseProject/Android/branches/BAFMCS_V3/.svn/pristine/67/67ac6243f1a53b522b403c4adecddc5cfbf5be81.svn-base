package com.adins.mss.base.checkout;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.image.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;


public class CheckOutLocationTask extends AsyncTask<Void, Void, String> implements IShowError {
    private static String messageSuccess = "Check Out location success : ";
    private static String TAG = "CHECK OUT LOCATION";
    LocationInfo locationInfo;
    private ProgressDialog progressDialog;
    private WeakReference<Context> context;
    private String messageWait;
    private String messageUnavaiableLocation;
    private String[] address;
    private byte[] bitmapArray;
    private ErrorMessageHandler errorMessageHandler;

    public CheckOutLocationTask(Context context, String msgWait, String messageUnavaiableLocation, LocationInfo locationInfo, String[] address) {
        this.context = new WeakReference<Context>(context);
        this.messageWait = msgWait;
        this.messageUnavaiableLocation = messageUnavaiableLocation;
        this.address = address;
        this.locationInfo = locationInfo;
        errorMessageHandler = new ErrorMessageHandler(this);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context.get(), "", this.messageWait, true);
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        String lat;
        String lng;
        lat = locationInfo.getLatitude();
        lng = locationInfo.getLongitude();

        if (locationInfo != null) {

            String data = CheckOutManager.toLocationCheckOutString(locationInfo);
            try {
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(context.get(), encrypt, decrypt);
                HttpConnectionResult resultServer = httpConn.requestToServer(GlobalData.getSharedGlobalData().getURL_SUBMIT_TRACK(), data, Global.DEFAULTCONNECTIONTIMEOUT);

                result = data;

                try {
                    String image_url = "https://maps.googleapis.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=15&size=720x300&maptype=roadmap&markers=color:red%7Clabel:O%7C" + lat + "," + lng;

                    ImageLoader imgLoader = new ImageLoader(context.get());
                    Bitmap bitmap = imgLoader.getBitmap(image_url);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

                    bitmapArray = stream.toByteArray();

                } catch (Exception e) {
                    FireCrash.log(e);
                }

                //** debug version
                if (Global.IS_DEV) {
                    Log.i(TAG, "check in " + locationInfo.toString());
                }
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV)
                    e.printStackTrace();
//				System.out.println("cekinLocation "+e);
                CheckOutManager.insertLocationCheckOutToDB(context.get(), locationInfo);
            }

        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }

        if ("".equals(result)) {
            errorMessageHandler.processError("INFO",messageUnavaiableLocation, ErrorMessageHandler.DIALOG_TYPE);
            /*NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context.get());
            dialogBuilder.withTitle("INFO")
                    .withMessage(messageUnavaiableLocation)
                    .show();*/
        } else {
            // result sementara tidak hanya digunakan sebagai parameter suksestidaknya

            String time = Formatter.formatDate(locationInfo.getHandset_time(), Global.TIME_STR_FORMAT);
            String date = Formatter.formatDate(locationInfo.getHandset_time(), Global.DATE_STR_FORMAT3);
            String message = "Time : " + time + "\nDate : " + date + "\n" + address[1];
            String attdAddress = address[1];

//			TimelineManager.insertTimeline(context, message, locationInfo.getLatitude(), 
//					locationInfo.getLongitude(), locationInfo.getUsr_crt(), 
//					locationInfo.getDtm_crt(), Global.TIMELINE_TYPE_CHECKOUT, bitmapArray);

            TimelineManager.insertTimeline(context.get(), locationInfo, message, attdAddress, bitmapArray);

            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context.get());
            dialogBuilder.withTitle("Check In Success")
                    .withMessage(message)
                    .show();
        }


        //** debug version
        if (Global.IS_DEV) {
            Log.i(TAG, "check in " + result);
        }

    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {

    }
}
