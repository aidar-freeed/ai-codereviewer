package com.adins.mss.odr.update;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.odr.ExpandableListAdapter;
import com.adins.mss.odr.ResultOrderActivity;
import com.adins.mss.odr.model.JsonResponseServer;
import com.adins.mss.odr.model.JsonResponseServer.ResponseServer;

import org.acra.ACRA;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class ResultUpdateActivity extends ResultOrderActivity implements OnGroupClickListener{
	private int taskType;
	private String searchByStatus;
	private List<ResponseServer> responseServer;
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
	protected String getUsedURL() {
		return GlobalData.getSharedGlobalData().getURL_GET_LIST_CANCELORDER();
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
//		List<NameValuePair> param = RequestJsonFromURLTask.getBasicParam();
		
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
//		String statusCode = statusCodeMap.get(listDataHeader.get(groupPosition));
//		
		gotoDetailData(taskType, nomorOrder, uuid_task_h);
		this.finish();
		return true;
	}

	protected void gotoDetailData(int taskType, String nomorOrder, String uuid_task_h){
		
		Intent intent = null;
		
		
		switch (taskType) {
		case Global.TASK_ORDER_ASSIGNMENT:
//			intent = new Intent(this, OrderAssignmentActivity.class);
			break;
		case Global.TASK_ORDER_REASSIGNMENT:
//			intent = new Intent(this, OrderAssignmentActivity.class);
			break;
		case Global.TASK_CANCEL_ORDER:
			intent = new Intent(this, OrderCancelActivity.class);
			break;
		default:
//			intent = new Intent(this, OrderAssignmentActivity.class);
			break;
		}
		intent.putExtra(Global.BUND_KEY_TASK_TYPE, taskType);
		intent.putExtra(Global.BUND_KEY_ORDERNO, nomorOrder);
		intent.putExtra(Global.BUND_KEY_UUID_TASKH, uuid_task_h);
		startActivity(intent);	
	}
	protected String flagParamForStatusCode(String code){	
		String flagParam;
		
		if("OnSurvey".equalsIgnoreCase(code)){
			flagParam="On Progress Survey";
		}else if ("OnCA".equalsIgnoreCase(code)){
			flagParam="On CA";
		}else{
			flagParam="";
		}
		
		return flagParam;
	}
	@Override
	protected void processServerResponse(String result) {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		try {
//			result = "{\"listResponseServer\":[{\"key\":\"ODRNO1812040004\",\"value\":\"Tes Submit Order\",\"flag\":\"5459915\"}],\"status\":{\"code\":0}}";
			JsonResponseServer resultOrder= GsonHelper.fromJson(result, JsonResponseServer.class);
			responseServer = resultOrder.getListResponseServer();
			if(responseServer!=null){
				if(responseServer.size()>0) {
					for (int i = 0; i < responseServer.size(); i++) {
						ResponseServer mobileBean = responseServer.get(i);

						String orderNo = mobileBean.getKey();
						String customerName = mobileBean.getValue();

						listDataHeader.add(orderNo + " - " + customerName);

						listDataChild.put(listDataHeader.get(i), null);
						ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
						expListView.setAdapter(listAdapter);
					}

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							UserHelp.showAllUserHelpWithListView(ResultUpdateActivity.this,ResultUpdateActivity.this.getClass().getSimpleName(),expListView,0);
						}
					}, SHOW_USERHELP_DELAY_DEFAULT);
				}
				else{
					final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
					builder.withTitle("INFO")
							.withMessage(getString(com.adins.mss.base.R.string.data_not_found))
							.withButton1Text("OK")
							.setButton1Click(new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									builder.dismiss();
									finish();
								}
							}).show();
				}
			}
			else{
				final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
	            builder.withTitle("INFO")
	            	.withMessage(getString(com.adins.mss.base.R.string.data_not_found)+" "+resultOrder.getStatus().getMessage())
	            	.withButton1Text("OK")
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
            builder.withTitle("INFO")
            	.withMessage(e.getMessage())
            	.withButton1Text("OK")
            	.setButton1Click(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {					
						builder.dismiss();
						finish();
					}
				}).show();
		}
	}
	//request task regarding detail data to be displayed on next activity: order detail list

	@Override
	public void onBackPressed() {
		if(!Global.BACKPRESS_RESTRICTION) {
			super.onBackPressed();
		}
	}
}
