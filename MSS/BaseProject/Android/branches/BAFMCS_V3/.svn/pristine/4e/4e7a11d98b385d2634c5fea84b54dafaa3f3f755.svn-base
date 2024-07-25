package com.adins.mss.base.checkin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.image.ImageLoader;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @author gigin.ginanjar
 */
public class CheckInLocationTask extends AsyncTask<Void, Void, String> implements IShowError {
    private static String TAG = "CHECK IN LOCATION";
    LocationInfo locationInfo;
    String result;
    String errMessage = null;
    private ProgressDialog progressDialog;
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String messageWait;
    private String messageUnavaiableLocation;
    private String[] address;
    private byte[] bitmapArray;
    private String attdAddress;
    private ErrorMessageHandler errorMessageHandler;
    /**
     * Inisialisasi Check In Location Task
     *
     * @param activity                  -Context
     * @param msgWait                   - Waiting Message
     * @param messageUnavaiableLocation - message if unavailable
     * @param locationInfo              - Location Info
     * @param address                   - String[] Address
     */
    public CheckInLocationTask(Activity activity, String msgWait, String messageUnavaiableLocation, LocationInfo locationInfo, String[] address) {
        this.activity = new WeakReference<>(activity);
        this.context = new WeakReference<Context>(activity);
        this.messageWait = msgWait;
        this.messageUnavaiableLocation = messageUnavaiableLocation;
        this.address = address;
        this.locationInfo = locationInfo;
    }

    public CheckInLocationTask(Context activity, String msgWait, String messageUnavaiableLocation, LocationInfo locationInfo, String[] address) {
        this.context = new WeakReference<>(activity);
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
        String lat;
        String lng;
        HttpConnectionResult resultServer = null;
        if (Tool.isInternetconnected(context.get())) {
            if (locationInfo != null) {
                lat = locationInfo.getLatitude();
                lng = locationInfo.getLongitude();

                List<LocationInfo> list = new ArrayList<>();
                list.add(locationInfo);

                attdAddress = context.get().getString(R.string.address_not_found);
                if (address != null)
                    attdAddress = address[1] != null ? address[1] : context.get().getString(R.string.address_not_found);

                JsonRequestAbsensi requestAbsensi = new JsonRequestAbsensi();
                requestAbsensi.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                requestAbsensi.addImeiAndroidIdToUnstructured();
                requestAbsensi.setLocationInfo(list);
                requestAbsensi.setAttd_address(attdAddress);

                String json = GsonHelper.toJson(requestAbsensi);

                try {
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(context.get(), encrypt, decrypt);
                    String url = GlobalData.getSharedGlobalData().getURL_GET_ABSENSI();

                    //Firebase Performance Trace HTTP Request
                    HttpMetric networkMetric =
                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                    resultServer = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                    try {
                        Utility.metricStop(networkMetric, resultServer);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }

                    result = resultServer.getResult();

                    try {
                        String image_url = "https://maps.googleapis.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=15&size=720x300&maptype=roadmap&markers=color:green%7Clabel:I%7C" + lat + "," + lng;

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
                    errMessage = context.get().getString(R.string.msgUnavaibleLocationCheckIn);
                }
            }
        } else {
            errMessage = context.get().getString(R.string.no_internet_connection);
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

        if (errMessage != null) {
            Log.i(TAG, "check in " + locationInfo.toString());
            if (errMessage.equals(context.get().getString(R.string.no_internet_connection))) {
                errorMessageHandler.processError(context.get().getString(R.string.info_capital)
                        ,errMessage, ErrorMessageHandler.DIALOG_TYPE);
            } else {
                errorMessageHandler.processError(context.get().getString(R.string.error_capital)
                        ,context.get().getString(R.string.connection_failed)
                        , ErrorMessageHandler.DIALOG_TYPE);
            }
        } else {
            if (result != null) {
                try {
                    JsonResponseAbsensi response = GsonHelper.fromJson(result, JsonResponseAbsensi.class);
                    int code = response.getStatus().getCode();
                    if (code == 0) {
                        String resultResponse = null;
                        try {
                            resultResponse = response.getResult();
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                        if (("1").equalsIgnoreCase(response.getSuccess())) {
                            if (Pattern.compile(Pattern.quote("Tidak berhasil"), Pattern.CASE_INSENSITIVE).matcher(resultResponse).find()) {
                                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context.get());
                                dialogBuilder.withTitle(context.get().getString(R.string.info_capital))
                                        .withMessage(resultResponse)
                                        .withButton1Text(context.get().getString(R.string.btnOk))
                                        .setButton1Click(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogBuilder.dismiss();
                                            }
                                        })
                                        .isCancelable(true)
                                        .show();
                            } else {
                                try {
                                    String message = "";
                                    message = resultResponse;
                                    locationInfo.setLocation_type(Global.LOCATION_TYPE_CHECKIN);
                                    TimelineManager.insertTimeline(context.get(), locationInfo, message, attdAddress, bitmapArray);

                                    CheckInManager.insertLocationCheckInToDB(context.get(), locationInfo);

                                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context.get());
                                    dialogBuilder.withTitle(context.get().getString(R.string.success))
                                            .withMessage(message)
                                            .withButton1Text(context.get().getString(R.string.btnOk))
                                            .setButton1Click(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialogBuilder.dismiss();
                                                }
                                            })
                                            .isCancelable(true)
                                            .show();
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                    try {
                                        CheckInManager.insertLocationCheckInToDB(context.get(), locationInfo);
                                        String message = "";
                                        message = resultResponse;
                                        TimelineManager.insertTimeline(context.get(), locationInfo, message, attdAddress, bitmapArray);
                                    } catch (Exception en) {
                                        FireCrash.log(e);
                                    }
                                }
                            }
                        } else if(response.getSuccess().equalsIgnoreCase("0")) {//gagal karena sudah pernah login
                            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context.get());
                            dialogBuilder.withTitle(context.get().getString(R.string.info_capital))
                                    .withMessage(resultResponse)
                                    .isCancelable(true)
                                    .show();
                        } else {
                            if (resultResponse == null) {
                                resultResponse = context.get().getString(R.string.empty_data);
                                errorMessageHandler.processError(context.get().getString(R.string.info_capital), resultResponse, ErrorMessageHandler.DIALOG_TYPE);
                            }
                        }
                    } else {
                        errorMessageHandler.processError(context.get().getString(R.string.warning_capital)
                                ,response.getStatus().getMessage()
                                , ErrorMessageHandler.DIALOG_TYPE);
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    errorMessageHandler.processError(context.get().getString(R.string.error_capital)
                            ,result
                            , ErrorMessageHandler.DIALOG_TYPE);

                }
            }
        }
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        if(GlobalData.isRequireRelogin()){
            return;
        }
        if(notifType == ErrorMessageHandler.DIALOG_TYPE){
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context.get());
            dialogBuilder.withTitle(errorSubject)
                    .withMessage(errorMsg)
                    .withButton1Text(context.get().getString(R.string.btnOk))
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(true)
                    .show();
        }
    }
}
