package com.adins.mss.coll.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.GetLogoKompetisiResponse;
import com.adins.mss.coll.NewMCMainActivity;
import com.adins.mss.coll.R;
import com.adins.mss.coll.dummy.MyDashboardItemDummyAdapter;
import com.adins.mss.coll.loyalti.pointacquisitionmonthly.MonthlyPointsChartView;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Kompetisi;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class GetLogoKompetisi extends AsyncTask<Void, Void, String> {
    private Context context;
    String errMsg = "";
    public GetLogoKompetisiResponse logoKompetisiResponse;
    String memberCode;
    ImageView imageLogo;


    public GetLogoKompetisi(Context mContext, String membership_program_code, ImageView logoKompetisi) {
        context = mContext;
        memberCode = membership_program_code;
        imageLogo = logoKompetisi;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(Void... voids) {
        if (Tool.isInternetconnected(context)) {

            Gson gson = new GsonBuilder().setDateFormat("ddMMyyyyHHmmss").registerTypeHierarchyAdapter(byte[].class,
                    new GsonHelper.ByteArrayToBase64TypeAdapter()).create();
            String result;
            GetLogoRequest requestType = new GetLogoRequest();
//                ArrayList<String> data = null;
//                data = new ArrayList<>();
//                for(int i = 0 ; i < dataKompetisireq.size() ; i++){
//                    data.add(dataKompetisireq.get(i).getMembershipProgramCode());
//                }
            requestType.setMEMBERSHIP_PROGRAM_CODE(memberCode);
            requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
//                    requestType.addItemToUnstructured(new KeyValue("imei", GlobalData.getSharedGlobalData().getImei()), false);

            String json = GsonHelper.toJson(requestType);
            String url = GlobalData.getSharedGlobalData().getURL_GET_LOGOKOMPETISI();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
            HttpConnectionResult serverResult = null;

            //Firebase Performance Trace HTTP Request
            HttpMetric networkMetric =
                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
            Utility.metricStart(networkMetric, json);

            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                Utility.metricStop(networkMetric, serverResult);
            } catch (Exception e) {
                e.printStackTrace();
                errMsg = e.getMessage();
                return errMsg;
            }

            if(serverResult != null && serverResult.getStatusCode() == 200){
                try {
                    logoKompetisiResponse = gson.fromJson(serverResult.getResult(), GetLogoKompetisiResponse.class);
                } catch (Exception e) {
                    return e.getMessage();
                }
            }else {
                errMsg = context.getString(R.string.failed_get_data_try_again);
            }

            return errMsg;
        } else {
            errMsg = context.getString(R.string.no_internet_connection);
            return errMsg;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (errMsg.length() > 0) {
            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        String base64Logo = logoKompetisiResponse.getLOGO();
        if(base64Logo != null && !base64Logo.equals("")){
            byte[] decodedString = Base64.decode(base64Logo, Base64.DEFAULT );
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageLogo.setImageBitmap(decodedByte);
        }
    }

}
