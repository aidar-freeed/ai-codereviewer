package com.adins.mss.coll.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.EmergencyLockActivity;
import com.adins.mss.coll.models.EmergencyRequest;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Emergency;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.EmergencyDataAccess;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.lang.ref.WeakReference;
import java.util.Date;

public class EmergencyService extends Service {
    EmergencyThread emergencyThread;
    public EmergencyService(){
    }

    @Override
    public void onCreate() {
        emergencyThread = new EmergencyThread(getApplicationContext());
        emergencyThread.start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        emergencyThread.requestStop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected class EmergencyThread extends Thread {
        private WeakReference<Context> context;
        private volatile boolean keepRunning = true;
        private volatile boolean isWait = false;
        private long interval = Global.DEFAULT_EMERGENCY_INTERVAL_SEND;
        private Date dtm_crt;

        public EmergencyThread(Context context) {
            this.context = new WeakReference<>(context);
            try {
                GeneralParameter gp = GeneralParameterDataAccess.getOne(context,
                        UserDataAccess.getOne(getApplicationContext()).getUuid_user(),
                        Global.GS_INTERVAL_EMERGENCY_MC);
                if (gp != null) {
                    interval = Global.SECOND * Integer.parseInt(gp.getGs_value());
                }
                dtm_crt = new Date(System.currentTimeMillis());
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
                dtm_crt = new Date(System.currentTimeMillis());
            }
        }

        @Override
        public void run() {
            if(null!=GlobalData.getSharedGlobalData().getUser()){
                GlobalData.getSharedGlobalData().getUser().setIs_emergency(Global.EMERGENCY_SEND_PENDING);
                UserDataAccess.addOrReplace(getApplicationContext(),GlobalData.getSharedGlobalData().getUser());
            } else{
                User user = UserDataAccess.getOne(getApplicationContext());
                user.setIs_emergency("2");
                UserDataAccess.addOrReplace(getApplicationContext(), user);
            }
            Emergency emergency = new Emergency();
            emergency.setUser(UserDataAccess.getOne(getApplicationContext()));
            EmergencyRequest emergencyRequest = new EmergencyRequest();
            String url = GlobalData.getSharedGlobalData().getURL_EMERGENCY();
            emergencyRequest.setDtm_emergency(dtm_crt);
            emergency.setDtm_emergency(dtm_crt);
            LocationInfo loc = Global.LTM.getCurrentLocation(Global.FLAG_LOCATION_TRACKING);
            if ("null".equalsIgnoreCase(loc.getLatitude())) {
                emergency.setLatitude("");
            } else {
                emergency.setLatitude(loc.getLatitude());
            }
            if ("null".equalsIgnoreCase(loc.getLongitude())) {
                emergency.setLongitude("");
            } else {
                emergency.setLongitude(loc.getLongitude());
            }
            try {
                GeneralParameter gp = GeneralParameterDataAccess.getOne(context.get(),
                        emergency.getUuid_user(),
                        Global.GS_INTERVAL_EMERGENCY_MC);
                if (gp != null) {
                    interval = Global.SECOND * Integer.parseInt(gp.getGs_value());
                }
                dtm_crt = new Date(System.currentTimeMillis());
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
                dtm_crt = new Date(System.currentTimeMillis());
            }
            while (keepRunning) {
                try {
                    synchronized (this) {
                        if (isWait) {
                            this.wait();
                        }
                    }

                    if (Tool.isInternetconnected(context.get())) {

                        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                        HttpCryptedConnection httpConn = new HttpCryptedConnection(context.get(), encrypt, decrypt);
                        emergencyRequest.setDtm_emergency(emergency.getDtm_emergency());
                        emergencyRequest.setLatitude(emergency.getLatitude());
                        emergencyRequest.setLongitude(emergency.getLongitude());
                        emergencyRequest.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                        User user = UserDataAccess.getOne(getApplicationContext());
                        if(EmergencyDataAccess.getByUser(getApplicationContext(), user.getUuid_user()).size() == 0)
                            EmergencyDataAccess.add(getApplicationContext(), emergency);
                        else{
                            emergency = EmergencyDataAccess.getByUser(getApplicationContext(),user.getUuid_user()).get(0);
                        }

                        String json = GsonHelper.toJson(emergencyRequest);
                        HttpConnectionResult response = null;

                        //Firebase Performance Trace HTTP Request
                        HttpMetric networkMetric =
                                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                        Utility.metricStart(networkMetric, json);

                        try {
                            response = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                            Utility.metricStop(networkMetric, response);
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                        if (response.isOK()) {
                            MssResponseType resp = GsonHelper.fromJson(response.getResult(), MssResponseType.class);
                            if (resp.getStatus().getCode() == 0) {
                                GlobalData.getSharedGlobalData().getUser().setIs_emergency(Global.EMERGENCY_SEND_SUCCESS);
                                UserDataAccess.addOrReplace(getApplicationContext(),GlobalData.getSharedGlobalData().getUser());
                                EmergencyLockActivity.emergencyHandler.sendEmptyMessage(0);
                                requestStop();
                            } else {
                                try {
                                    Log.d("emergency", interval+"");
                                    Thread.sleep(interval);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    FireCrash.log(ex);
                    ex.printStackTrace();
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            super.run();
        }

        public synchronized void requestWait() {
            isWait = true;
        }

        public synchronized void stopWaiting() {
            isWait = false;
            synchronized (this) {
                this.notifyAll();
            }
        }

        public synchronized void requestStop() {
            keepRunning = false;
        }
    }
}
