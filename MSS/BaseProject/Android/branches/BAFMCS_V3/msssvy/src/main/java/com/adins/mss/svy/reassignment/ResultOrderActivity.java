package com.adins.mss.svy.reassignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.svy.R;
import com.adins.mss.svy.models.ExpandableListAdapter;
import com.adins.mss.svy.reassignment.JsonResponseServer.ResponseServer;

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

/**
 * Created by winy.firdasari on 20/01/2015.
 */
public class ResultOrderActivity extends AppCompatActivity {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_survey_reassign_activity);

        // olivia : set toolbar
        Toolbar toolbar = (Toolbar) findViewById(com.adins.mss.base.R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_mn_surveyreassign));
        toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
        setSupportActionBar(toolbar);

        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        initialize();

        try {
            generateRequestParam();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        new ResultOrderTask().execute();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }

    TaskListener listener = new TaskListener() {
        @Override
        public void onCompleteTask(Object result) {
            processServerResponse(result.toString());
        }

        @Override
        public void onCancelTask(boolean value) {
            //EMPTY
        }

        @Override
        public void onLocalData(Object result) {
            //EMPTY
        }
    };

    protected void initialize() {
        Intent i = getIntent();
        // Receiving the Data
        String mStartDate = i.getStringExtra("startDate");
        String mEndDate = i.getStringExtra("endDate");
        String newflag= i.getStringExtra("flag");

        ResultOrderActivity.url = getUsedURL();
        startDate=mStartDate;
        endDate=mEndDate;
        flag=Global.FLAG_BY_DATE;

        if(newflag!=null){
            flag = newflag;
        }

        if("orderTracking".equalsIgnoreCase(flag)){
            startDate="1";
            endDate="1";
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
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT);
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
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
            HttpCryptedConnection httpConn = new HttpCryptedConnection(ResultOrderActivity.this, encrypt, decrypt);
    		HttpConnectionResult serverResult = null;
    		try {
    			serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
    		} catch (Exception e) {
                FireCrash.log(e);
    			e.printStackTrace();
    		}

            resp = serverResult.getResult();
        } catch (Exception e) {
            FireCrash.log(e);

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


        List<NameValuePair> param = new ArrayList<>();
        param.add(new BasicNameValuePair("startdate", sdtSDate));
        param.add(new BasicNameValuePair("enddate", sdtEDate));
        param.add(new BasicNameValuePair("ordernumber", orderNumber));
        param.add(new BasicNameValuePair("flag", flag));
        return param;
    }

    protected void processServerResponse(String result) {
        if(GlobalData.isRequireRelogin()){
            return;
        }

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        try {
        	JsonResponseServer resultOrder = GsonHelper.fromJson(result, JsonResponseServer.class);
            for(int i=0;i<resultOrder.getListResponseServer().size();i++){
            	ResponseServer responseServer = resultOrder.getListResponseServer().get(i);
            	
                String orderNo = responseServer.getKey();
                String customerName = responseServer.getValue();
                
                listDataHeader.add(orderNo+" - "+customerName);
                List<ResponseServer> SubResultOrder = responseServer.getSubListResponseServer();
                List<String> statusOrder = new ArrayList<>();
                for(int ii = 0; ii < SubResultOrder.size(); ii++){
                	ResponseServer subResponseServer = SubResultOrder.get(ii);
                    statusOrder.add(subResponseServer.getValue());
                }

                listDataChild.put(listDataHeader.get(i), statusOrder);
                listAdapter = new ExpandableListAdapter(ResultOrderActivity.this, listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);
            }
        } catch (Exception e) {             FireCrash.log(e);
            if(Global.IS_DEV) System.out.println(e);
            final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
            builder.withTitle(getString(R.string.info_capital))
            	.withMessage(this.getString(R.string.jsonParseFailed))
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
	                ioe.printStackTrace();
	                try {
	                    progressDialog.dismiss();
	                } catch (Exception e) {             FireCrash.log(e);}

	                getApplicationContext().getString(R.string.jsonParseFailed);
	            }
            }
            else{
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
                    FireCrash.log(e);
                }
            }
            if (getString(R.string.no_internet_connection).equals(result)) {
	        	NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(ResultOrderActivity.this);
	        	builder.withTitle(getString(R.string.info_capital))
	        	.withMessage(getString(R.string.no_internet_connection))
	        	.show();
            }else{
                processServerResponse(result);
            }
        }
    }
}