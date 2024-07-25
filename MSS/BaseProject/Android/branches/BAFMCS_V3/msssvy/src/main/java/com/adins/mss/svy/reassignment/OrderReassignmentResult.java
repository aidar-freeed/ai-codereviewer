package com.adins.mss.svy.reassignment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.svy.assignment.OrderAssignmentResult;
import com.adins.mss.svy.models.ExpandableListAdapter;
import com.adins.mss.svy.reassignment.JsonResponseServer.ResponseServer;

import org.acra.ACRA;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class OrderReassignmentResult extends ResultOrderActivity implements OnGroupClickListener{
	public static final String ON_SURVEY = "OnSurvey";
	public static final String ON_PROGRESS_SURVEY = "On Progress Survey";
	public static final String ON_CA = "OnCA";
	public static final String ON_CA1 = "On CA";
	private int taskType;
	private String searchByStatus;
	private List<ResponseServer> responseServer;
	ExpandableListAdapter listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
		taskType = this.getIntent().getIntExtra(Global.BUND_KEY_TASK_TYPE, 0);
	}
	@Override
	protected void initialize() {
		super.initialize();
		expListView.setGroupIndicator(null);  
		expListView.setOnGroupClickListener(this);

		Intent i = getIntent();
	    searchByStatus = i.getStringExtra("status");
	    if(searchByStatus==null){
	        searchByStatus="1";
	    }
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
	@Override
    public void onResume(){
    	super.onResume();
    }

	@Override
	protected String getUsedURL() {
		return GlobalData.getSharedGlobalData().getURL_GET_LIST_REASSIGNMENT();
	}
	
	@Override
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
		
		if (orderNumber == null) orderNumber = "1";
		
		String flag = flagParamForType(this.taskType);
		
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		
		param.add(new BasicNameValuePair("startdate", sdtSDate));
        param.add(new BasicNameValuePair("enddate", sdtEDate));
        param.add(new BasicNameValuePair("ordernumber", orderNumber));
        param.add(new BasicNameValuePair("flag", flag));
		param.add(new BasicNameValuePair("status", searchByStatus ));
	
		return param;
	}
	
	protected String flagParamForType(int taskType){
		
		String flagParam;
		switch (taskType) {
		case Global.TASK_ORDER_ASSIGNMENT:
			flagParam = Global.FLAG_FOR_ORDERASSIGNMENT;
			break;
		case Global.TASK_ORDER_REASSIGNMENT:
			flagParam = Global.FLAG_FOR_ORDERREASSIGNMENT;
			break;
		case Global.TASK_CANCEL_ORDER:
			//bangkit 20141027 change param from UO to UOC
			flagParam = Global.FLAG_FOR_CANCELORDER;
			break;
		default:
			flagParam = "";
			break;
		}
		return flagParam;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
			long id) {
		// TODO Auto-generated method stub
		String nomorOrder = responseServer.get(groupPosition).getKey();
		String uuid_task_h = responseServer.get(groupPosition).getFlag();
		String formName = responseServer.get(groupPosition).getFormName();
		gotoDetailData(taskType, nomorOrder, uuid_task_h, formName);
		return true;
	}

	protected void gotoDetailData(int taskType, String nomorOrder, String uuid_task_h, String formName){

		Intent intent = null;

		switch (taskType) {
		case Global.TASK_ORDER_ASSIGNMENT:
			break;
		case Global.TASK_ORDER_REASSIGNMENT:
			intent = new Intent(this, OrderAssignmentResult.class);
			intent.putExtra(Global.BUND_KEY_ORDERNO, nomorOrder);
			intent.putExtra(Global.BUND_KEY_FORM_NAME, formName);
			break;
		default:
			break;
		}
		intent.putExtra(Global.BUND_KEY_TASK_TYPE, taskType);
		intent.putExtra(Global.BUND_KEY_ORDERNO, nomorOrder);
		intent.putExtra(Global.BUND_KEY_UUID_TASKH, uuid_task_h);
		startActivity(intent);
	}

	protected String flagParamForStatusCode(String code){	
		String flagParam;
		
		if(ON_SURVEY.equalsIgnoreCase(code)){
			flagParam = ON_PROGRESS_SURVEY;
		}else if (ON_CA.equalsIgnoreCase(code)){
			flagParam = ON_CA1;
		}else{
			flagParam = "";
		}
		
		return flagParam;
	}
	@Override
	protected void processServerResponse(String result) {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		LinkedHashMap<String, String> listResult = new LinkedHashMap<>();

		try {
			JsonResponseServer resultOrder= GsonHelper.fromJson(result, JsonResponseServer.class);
			if(resultOrder.getStatus().getCode() == 0){
				responseServer = resultOrder.getListResponseServer();
				if(responseServer != null && responseServer.size() > 0){
					for(int i=0; i<responseServer.size(); i++){
						ResponseServer mobileBean = responseServer.get(i);                	
							
				        String orderNo = "";
				        if(mobileBean.getKey()!=null)
				        	orderNo = mobileBean.getKey();
				        String customerName = mobileBean.getValue();
				        listResult.put(orderNo, customerName);
						listAdapter = new ExpandableListAdapter(this, listResult);
						expListView.setAdapter(listAdapter);
					}
				}
				else{
					String message;
	        		if(resultOrder.getStatus().getMessage()!=null){
	        			message = getString(com.adins.mss.base.R.string.data_not_found) +resultOrder.getStatus().getMessage();
	        		}else{
	        			message = getString(com.adins.mss.base.R.string.data_not_found);
	        		}
					final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
		            builder.withTitle(getString(com.adins.mss.base.R.string.info_capital))
		            	.withMessage(message)
		            	.withButton1Text(getString(com.adins.mss.base.R.string.btnOk))
		            	.setButton1Click(new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {					
								builder.dismiss();
								finish();
							}
						}).show();
				}
			}else{
				if(GlobalData.isRequireRelogin()){
					return;
				}
				final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
	            builder.withTitle(getString(com.adins.mss.base.R.string.info_capital))
	            	.withMessage(getString(com.adins.mss.base.R.string.data_not_found))
	            	.withButton1Text(getString(com.adins.mss.base.R.string.btnOk))
	            	.setButton1Click(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {					
							builder.dismiss();
							finish();
						}
					}).show();
			}
		} catch (Exception e) {
			FireCrash.log(e);
            if(Global.IS_DEV) System.out.println(e);
            final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
            builder.withTitle(getString(com.adins.mss.base.R.string.info_capital))
            	.withMessage(e.getMessage())
            	.withButton1Text(getString(com.adins.mss.base.R.string.btnOk))
            	.setButton1Click(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {					
						builder.dismiss();
						finish();
					}
				}).show();
		}
	}
}
