package com.adins.mss.odr.reassignment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Keep;
import android.view.View;
import android.widget.AdapterView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.assignment.OrderAssignmentAdapter;
import com.adins.mss.odr.assignment.OrderAssignmentResult;
import com.adins.mss.odr.model.JsonRequestCheckOrder;
import com.adins.mss.odr.model.JsonResponseServer;
import com.adins.mss.odr.model.JsonResponseServer.ResponseServer;
import com.androidquery.AQuery;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderReassignmentResult extends Activity{
	private String url;
	protected String startDate;
    protected String endDate;
    protected String orderNumber;
    protected String flag;
    private Bundle mArguments;
    protected JsonResponseServer results;
	private List<ResponseServer> responseServer;
	OrderAssignmentAdapter adapter;
	AQuery query;
	protected String getUsedURL(){
        return GlobalData.getSharedGlobalData().getURL_CHECKORDER();
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.default_listview);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        mArguments = getIntent().getExtras();
        query = new AQuery(this);
        initialize(); 
        query.id(android.R.id.list).adapter(adapter);
		query.id(android.R.id.list).itemClicked(this, "itemClick");
        new ResultOrderTask().execute();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try {
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }
	@Override
	public void onResume(){
		super.onResume();
		getActionBar().setTitle(getString(R.string.title_mn_orderreassign));
	}
	
	protected void initialize() {
		String mStartDate = mArguments.getString("startDate");
        String mEndDate = mArguments.getString("endDate");
        String mNomorOrder= mArguments.getString("nomorOrder");
        String status= mArguments.getString("status");
        url = getUsedURL();
        
        if(mStartDate==null ||  "".equalsIgnoreCase(mStartDate)){
            startDate="1";
            endDate="1";
            orderNumber=mNomorOrder;
            flag="orderNumber";
        }

        //jika memilih order by date
        else if(mNomorOrder==null || "".equalsIgnoreCase(mNomorOrder)){
            startDate=mStartDate;
            endDate=mEndDate;
            orderNumber="1";
            flag="date";
        }

        if(status!=null){
            flag = status;
        }

        if("orderTracking".equalsIgnoreCase(flag)){
            startDate="1";
            endDate="1";
            orderNumber="1";
        }
	}
	
	protected List<NameValuePair> generateRequestParam() throws ParseException {
        String sdtSDate = "1";
        String sdtEDate = "1";
        if(startDate==null||endDate==null||"".equalsIgnoreCase(startDate)||"".equalsIgnoreCase(endDate)
                ||"1".equalsIgnoreCase(startDate)||"1".equalsIgnoreCase(endDate)){

        }else{
            //convert format date biar bisa di tranlate di DB servernya
            SimpleDateFormat f = new SimpleDateFormat(Global.DATE_STR_FORMAT);

            Date sDate,eDate;
            sDate = f.parse(startDate);
            eDate = f.parse(endDate);
            sdtSDate = Formatter.formatDate(sDate, Global.DATE_STR_FORMAT2);
            sdtEDate = Formatter.formatDate(eDate, Global.DATE_STR_FORMAT2);
        }


        List<NameValuePair> param = new ArrayList<>();

        param.add(new BasicNameValuePair("startdate", sdtSDate));
        param.add(new BasicNameValuePair("enddate", sdtEDate));
        param.add(new BasicNameValuePair("ordernumber", orderNumber));
        param.add(new BasicNameValuePair("flag", flag));
        return param;
    }
	
	public class ResultOrderTask extends AsyncTask<String, Void, String> {

        private String errMessage = null;

        @Override
        protected String doInBackground(String... arg0) {
        	String resp = null;
            try {  	
                List<NameValuePair> param = generateRequestParam();
                JsonRequestCheckOrder requestCheckOrder = new JsonRequestCheckOrder();
                requestCheckOrder.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                if(param.get(3).getValue().equals(Global.FLAG_BY_DATE)){
                	requestCheckOrder.setStartDate(param.get(0).getValue());
                	requestCheckOrder.setEndDate(param.get(1).getValue());            	
                }
                else if(param.get(3).getValue().equals(Global.FLAG_BY_ORDERNUMBER)){
                	requestCheckOrder.setOrderNumber(param.get(2).getValue());
                }
                else if(param.get(3).getValue().equals(Global.FLAG_FOR_CANCELORDER)){
                	if(param.get(2).getValue().equals("1")){
                		requestCheckOrder.setStartDate(param.get(0).getValue());
                    	requestCheckOrder.setEndDate(param.get(1).getValue());
                	}else{
                		requestCheckOrder.setOrderNumber(param.get(2).getValue());
                	}
                }
                else if(param.get(3).getValue().equals(Global.FLAG_FOR_ORDERREASSIGNMENT)){
                	if(param.get(2).getValue().equals("1")){
                		requestCheckOrder.setStartDate(param.get(0).getValue());
                    	requestCheckOrder.setEndDate(param.get(1).getValue());
                	}else{
                		requestCheckOrder.setOrderNumber(param.get(2).getValue());
                	}
                	requestCheckOrder.setFlag(param.get(3).getValue());
                }

                String json = GsonHelper.toJson(requestCheckOrder);
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(getApplicationContext(), encrypt, decrypt);
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
        		}
        		
                resp = serverResult.getResult();
            }catch (Exception e) {

                resp=e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {

            if (errMessage == null) {
                results = GsonHelper.fromJson(result, JsonResponseServer.class);
                responseServer = results.getListResponseServer();
                adapter = new OrderAssignmentAdapter(getApplicationContext(), responseServer,false);
                adapter.notifyDataSetChanged();
                query.id(android.R.id.list).adapter(adapter);
            }
        }
    }
    @Keep
	public void itemClick(AdapterView<?> parent, View v, int position, long id){
		String nomorOrder = responseServer.get(position).getKey();
		String uuid_task_h = responseServer.get(position).getFlag();
		gotoDetailData(Global.TASK_ORDER_REASSIGNMENT, nomorOrder, uuid_task_h);
	}

	private void gotoDetailData(int taskType, String nomorOrder, String uuid_task_h){
		Intent intent = new Intent(this, OrderAssignmentResult.class);
		intent.putExtra(Global.BUND_KEY_ORDERNO, nomorOrder);
		intent.putExtra(Global.BUND_KEY_TASK_TYPE, taskType);
		intent.putExtra(Global.BUND_KEY_UUID_TASKH, uuid_task_h);
		startActivity(intent);	
	}
}
