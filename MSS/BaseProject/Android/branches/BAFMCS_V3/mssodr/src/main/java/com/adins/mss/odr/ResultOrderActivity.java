package com.adins.mss.odr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.model.JsonRequestCheckOrder;
import com.adins.mss.odr.model.JsonResponseServer;
import com.adins.mss.odr.model.JsonResponseServer.ResponseServer;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.adins.mss.odr.CheckOrderActivity.keyCustName;

/**
 * Created by winy.firdasari on 20/01/2015.
 */
public class ResultOrderActivity extends Activity {
    private static String url = GlobalData.getSharedGlobalData().getURL_CHECKORDER();

    protected String getUsedURL(){
        return GlobalData.getSharedGlobalData().getURL_CHECKORDER();
    }

    ExpandableListAdapter listAdapter;

    protected ExpandableListView expListView;
    protected List<String> listDataHeader;
    protected HashMap<String, List<String>> listDataChild;
    protected String startDate;
    protected String endDate;
    protected String orderNumber;
    protected String flag;
    private String custName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        setContentView(R.layout.result_order_layout);
        initialize();
        new ResultOrderTask().execute();
    }

    protected void initialize() {
        // TODO Auto-generated method stub

        Intent i = getIntent();
        // Receiving the Data
        String mStartDate = i.getStringExtra("startDate");
        String mEndDate = i.getStringExtra("endDate");
        String mNomorOrder = i.getStringExtra("nomorOrder");
        String mCustName = i.getStringExtra(keyCustName);
        String newflag = i.getStringExtra("flag");

        ResultOrderActivity.url = getUsedURL();

        //jika memilih order by No Order
        if(null != mNomorOrder && !mNomorOrder.equalsIgnoreCase("")){
            orderNumber=mNomorOrder;
            flag=Global.FLAG_BY_ORDERNUMBER;
        }

        //jika memilih order by date
        else if (null != mStartDate && !mStartDate.equalsIgnoreCase("")) {
            startDate = mStartDate;
            endDate = mEndDate;
            flag = Global.FLAG_BY_DATE;
        } else if (null != mCustName && !mCustName.equalsIgnoreCase("")) {
            custName = mCustName;
            flag = Global.FLAG_BY_CUSTOMER_NAME;
        }

        if(newflag!=null){
            flag = newflag;
        }

        if ("orderTracking".equalsIgnoreCase(flag)) {
            startDate = "1";
            endDate = "1";
            orderNumber = "1";
            custName = "1";
        }

        expListView = (ExpandableListView) findViewById(R.id.resultOrderlist);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " " + getString(R.string.expanded),
                        Toast.LENGTH_SHORT);
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " " + getString(R.string.collapsed),
                        Toast.LENGTH_SHORT);
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT);
                return false;
            }
        });
    }

    public String getDataFromURL(String url) throws IOException {
        String resp = null;
        try {        	
            List<NameValuePair> param = generateRequestParam();
            
            JsonRequestCheckOrder requestCheckOrder = new JsonRequestCheckOrder();
            requestCheckOrder.setAudit(GlobalData.getSharedGlobalData().getAuditData());
//            requestCheckOrder.addItemToUnstructured(new KeyValue("imei", GlobalData.getSharedGlobalData().getImei()), false);            
            if(param.get(3).getValue().equals(Global.FLAG_BY_DATE)){
            	requestCheckOrder.setStartDate(param.get(0).getValue());
            	requestCheckOrder.setEndDate(param.get(1).getValue());            	
            }
            else if(param.get(3).getValue().equals(Global.FLAG_BY_ORDERNUMBER)){
            	requestCheckOrder.setOrderNumber(param.get(2).getValue());
            }
            else if (null != custName && !custName.equalsIgnoreCase("")) {
                requestCheckOrder.setCustomerName(custName);
            }
            else if(param.get(3).getValue().equals(Global.FLAG_FOR_CANCELORDER)){
            	if(param.get(2).getValue().equals("1")){
            		requestCheckOrder.setStartDate(param.get(0).getValue());
                	requestCheckOrder.setEndDate(param.get(1).getValue());
            	}else{
            		requestCheckOrder.setOrderNumber(param.get(2).getValue());
            	}
//            	requestCheckOrder.setFlag(param.get(3).getValue());
//            	requestCheckOrder.setStatus(param.get(4).getValue());
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
            HttpCryptedConnection httpConn = new HttpCryptedConnection(ResultOrderActivity.this, encrypt, decrypt);
    		HttpConnectionResult serverResult = null;

            //Firebase Performance Trace HTTP Request
            HttpMetric networkMetric =
                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
            Utility.metricStart(networkMetric, json);

    		try {
    			serverResult = httpConn.requestToServer(url, json);
                Utility.metricStop(networkMetric, serverResult);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
            if (null != serverResult && serverResult.isOK()) {
                resp = serverResult.getResult();
            }
        }catch (Exception e) {

            resp=e.getMessage();
        }

        return resp;
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
            sdtSDate = Formatter.formatDate(sDate, Global.DATE_STR_FORMAT_GSON);
            sdtEDate = Formatter.formatDate(eDate, Global.DATE_STR_FORMAT_GSON);
        }


        List<NameValuePair> param = new ArrayList<NameValuePair>();
     //   String userId = ApplicationBean.getInstance().getUserId();
        param.add(new BasicNameValuePair("startdate", sdtSDate));
        param.add(new BasicNameValuePair("enddate", sdtEDate));
        param.add(new BasicNameValuePair("ordernumber", orderNumber));
        param.add(new BasicNameValuePair("flag", flag));
     //   param.add(new BasicNameValuePair("userid", userId ));
        return param;
    }

    protected void processServerResponse(String result) {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        try {
        	JsonResponseServer resultOrder = GsonHelper.fromJson(result, JsonResponseServer.class);
        	List<ResponseServer> responseServers = resultOrder.getListResponseServer();
        	if(responseServers!=null&&responseServers.size()>0){
	        	for(int i=0;i<resultOrder.getListResponseServer().size();i++){
	            	ResponseServer responseServer = responseServers.get(i);
	            	
	                String orderNo = responseServer.getKey();
	                String customerName = responseServer.getValue();
	                
	                listDataHeader.add(orderNo+" - "+customerName);
	                List<ResponseServer> SubResultOrder = responseServer.getSubListResponseServer();
	                List<String> statusOrder = new ArrayList<String>();
	                for(int ii = 0; ii < SubResultOrder.size(); ii++){
	                	ResponseServer subResponseServer = SubResultOrder.get(ii);
	                    statusOrder.add(subResponseServer.getValue());
	                }
	
	                listDataChild.put(listDataHeader.get(i), statusOrder);
	                listAdapter = new ExpandableListAdapter(ResultOrderActivity.this, listDataHeader, listDataChild);
	                expListView.setAdapter(listAdapter);
	            }
        	}else{
        		String message;
        		if(resultOrder.getStatus().getMessage()!=null){
        			message = getString(R.string.data_not_found)+" " +resultOrder.getStatus().getMessage();
        		}else{
        			message = getString(R.string.data_not_found);
        		}
        		final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
	            builder.withTitle(getString(R.string.info_capital))
	            	.withMessage(message)
	            	.withButton1Text(getString(R.string.btnOk))
	            	.isCancelable(false)
	            	.setButton1Click(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {					
							builder.dismiss();
							finish();
						}
					}).show();
        	}
        } catch (Exception e) {
            System.out.println(e);
            final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
            builder.withTitle(getString(R.string.info_capital))
            	.withMessage(getApplicationContext().getString(R.string.request_error))
            	.withButton1Text(getString(R.string.btnOk))
            	.setButton1Click(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {					
						builder.dismiss();
						finish();
					}
				}).show();
        }
    }

    private class ResultOrderTask extends AsyncTask<String, Void, String> {

        private ProgressDialog progressDialog;
        private String errMessage = null;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ResultOrderActivity.this,
                    "", getString(R.string.progressWait), true);

        }

        @Override
        protected String doInBackground(String... arg0) {

            String result = null;
            if(Tool.isInternetconnected(getApplicationContext())){
            	try {
                    result = ResultOrderActivity.this.getDataFromURL(url);
                } catch (IOException ioe) {
                    // TODO Auto-generated catch block
                    ioe.printStackTrace();
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {}

                    errMessage = ioe.getMessage();
                }
            }else{
            	result = getString(R.string.no_internet_connection);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            if (progressDialog!=null&&progressDialog.isShowing()){
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                }
            }

            if(GlobalData.isRequireRelogin()){
                return;
            }
            if(null != result) {
                if (getString(R.string.no_internet_connection).equals(result)) {
                    NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(ResultOrderActivity.this);
                    builder.withTitle(getString(R.string.info_capital))
                            .withMessage(getString(R.string.no_internet_connection))
                            .show();
                } else {
                    processServerResponse(result);
                }
            }
        }
    }
}