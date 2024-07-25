package com.adins.mss.odr.assignment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.timeline.MapsViewer;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.odr.R;
import com.adins.mss.odr.model.JsonRequestDetailOrder;
import com.adins.mss.odr.model.JsonResponseServer;
import com.adins.mss.odr.model.JsonResponseServer.ResponseServer;
import com.adins.mss.odr.tool.ImageThumbnail;
import com.adins.mss.odr.tool.getImageTask;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAssignmentResult extends Activity implements OnClickListener{
	String nomorOrder;
	TableLayout detailTable;
	Button btnLookupCMO;
	Button btnSubmit;
	RelativeLayout bottomLayout;
	int taskType;
	String flag;
	String uuid_task_h;
	TextView txtCMO;
	TextView txtNotes;
	Map<ImageThumbnail, String> thumbnailMapping = new HashMap<ImageThumbnail, String>();
	List<ResponseServer> list;
	String assignId;
	public static ImageThumbnail targetThumbnail = null;
	private FirebaseAnalytics screenName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		screenName = FirebaseAnalytics.getInstance(this);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
		setContentView(R.layout.orderassignment_result_layout);
		nomorOrder = getIntent().getExtras().getString(Global.BUND_KEY_ORDERNO);
		taskType = getIntent().getExtras().getInt(Global.BUND_KEY_TASK_TYPE);
		uuid_task_h = getIntent().getExtras().getString(Global.BUND_KEY_UUID_TASKH);
		flag = flagParamForType(taskType);
		initialize();
		new GetRequestDataDetail(nomorOrder, flag).execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Set Firebase screen name
		screenName.setCurrentScreen(this, getString(R.string.screen_name_order_assignment_result), null);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		Context context = newBase;
		Locale locale;
		try {
			locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
			if (null == locale) {
				locale = new Locale(LocaleHelper.ENGLSIH);
			}
			context = LocaleHelper.wrap(newBase, locale);
		} catch (Exception e) {
			locale = new Locale(LocaleHelper.ENGLSIH);
			context = LocaleHelper.wrap(newBase, locale);
		} finally {
			super.attachBaseContext(context);
		}
	}
	private String flagParamForType(int taskType2) {
		switch (taskType) {
		case Global.TASK_ORDER_ASSIGNMENT:
			return "assign";
		case Global.TASK_ORDER_REASSIGNMENT:
			return "reassign";
		default:
			return "";
		}
	}
	protected void initialize() {
		// TODO Auto-generated method stub
		detailTable = (TableLayout)findViewById(R.id.orderDetailTable);
		btnLookupCMO = (Button)findViewById(R.id.btnLookupCMO);
		btnSubmit = (Button)findViewById(R.id.btnSubmit);
		txtCMO = (TextView)findViewById(R.id.txtCMO);
		txtNotes = (TextView)findViewById(R.id.txtNotes);
		bottomLayout = (RelativeLayout)findViewById(R.id.Bottomlayout);
	}
	
	public class GetRequestDataDetail extends AsyncTask<Void, Void, String> {
		private ProgressDialog progressDialog;
		private String errMessage = null;
		private String flag;
		private String nomorOrder;

		public GetRequestDataDetail(String nomorOrder, String flag) {
			this.nomorOrder = nomorOrder;
			this.flag = flag;
		}
		@Override
		protected void onPreExecute() {
	    	progressDialog = ProgressDialog.show(OrderAssignmentResult.this,
					"", getString(R.string.progressWait), true);
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			JsonRequestDetailOrder request = new JsonRequestDetailOrder();
			request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
//			request.setNomor_order(this.nomorOrder);
			request.setUuid_task_h(uuid_task_h);
//			request.setFlag(this.flag);
			String json = GsonHelper.toJson(request);
			String url = GlobalData.getSharedGlobalData().getURL_GET_DETAIL_ORDER();
			boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
			boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
			HttpCryptedConnection httpConn = new HttpCryptedConnection(getApplicationContext(), encrypt, decrypt);
    		HttpConnectionResult serverResult = null;
			try {
				serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
//				result = "{\"listResponseServer\":[{\"key\":\"Nama\",\"value\":\"Gigin Gnanjar\",\"flag\":\"0\"},{\"key\":\"Alamat\",\"value\":\"Kebon Jeruk\",\"flag\":\"0\"},{\"key\":\"Foto Rumah\",\"value\":\"123456\",\"flag\":\"1\"},{\"key\":\"Location\",\"value\":\"-6.198076,106.763137\",\"flag\":\"2\"}],\"status\":{\"code\":0}}";
				result = serverResult.getResult();						
			}
			catch (Exception e) {
				e.printStackTrace();
				try {
					progressDialog.dismiss();
				} catch (Exception ex) {
				}
				errMessage = e.getMessage();
			}
			finally {
				
			}
			
			return result;
		}
		@Override
		protected void onPostExecute(String result){
			if (progressDialog.isShowing()){
				try {
					progressDialog.dismiss();
				} catch (Exception e) {
				}
			}
			if("".equals(result)){
				NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getApplicationContext());
				dialogBuilder.withTitle(getString(R.string.info_capital))
					.withMessage(getString(R.string.msgUnavaibleLocationCheckIn))
					.show();
			}else{
				loadDataDetailFromServer(result);
			}
		}
	}
	private void loadDataDetailFromServer(String result){
		
		int no =1;
		LayoutParams lpSpace =new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		LayoutParams lpQuestion =new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,0.25f);
		LayoutParams lpNo =new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,0.4f);

		try {
			JsonResponseServer resultOrder = GsonHelper.fromJson(result, JsonResponseServer.class);
			list = resultOrder.getListResponseServer();
			for(ResponseServer responseServer : list){
				String question = responseServer.getKey();
				String answer = responseServer.getValue();
				String flag = responseServer.getFlag();
				
				LinearLayout row = new LinearLayout(this);
				row.setOrientation(LinearLayout.HORIZONTAL);					
				row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));				
				if (no%2 == 0) row.setBackgroundResource(R.color.tv_normal);				
				else row.setBackgroundColor(Color.WHITE);
				
				TextView lblNo = new TextView(this);
				lblNo.setText(no+ ". ");
				lblNo.setGravity(Gravity.CENTER_HORIZONTAL);
				lblNo.setLayoutParams(lpNo);
				row.addView(lblNo);
				
				TextView lblQuestion = new TextView(this);
				lblQuestion.setText(question);
				lblQuestion.setLayoutParams(lpQuestion);					
				row.addView(lblQuestion);
				
				TextView lblSpace = new TextView(this);
				lblSpace.setText(" : ");
				lblSpace.setLayoutParams(lpSpace);
				row.addView(lblSpace);
				if(flag.equals(Global.FLAG_FOR_IMAGE)){
					int w = 200;//GlobalData.getSharedGlobalData().getThumbnailWidth();
					int h = 160;//GlobalData.getSharedGlobalData().getThumbnailHeight();
					int density = getResources().getDisplayMetrics().densityDpi;
					if(density==DisplayMetrics.DENSITY_LOW){
						w = 80;
						h = 50;
					}else if(density==DisplayMetrics.DENSITY_MEDIUM){
						w = 120;
						h = 90;
					}
					final ImageThumbnail thumb = new ImageThumbnail(this, w, h);
					thumb.setOnClickListener(this);
					thumb.setLayoutParams(lpQuestion);
					thumbnailMapping.put(thumb, answer);
					row.addView(thumb);
				}else if(flag.equals(Global.FLAG_FOR_LOCATION)){
					final ImageThumbnail thumb = new ImageThumbnail(this, 90, 90);
					thumb.setImageResource(R.drawable.ic_absent);
					thumb.setOnClickListener(this);
					thumb.setLayoutParams(lpQuestion);
					thumbnailMapping.put(thumb, answer);
					row.addView(thumb);					
				}else{
					TextView lblAnswer = new TextView(this);
					lblAnswer.setText(answer);
					lblAnswer.setLayoutParams(lpQuestion);					
					row.addView(lblAnswer);
				}
				detailTable.addView(row);
				no++;
			}
		} catch (Exception e) {
		}
	}
	public void doSubmit(View view){
		if(txtCMO.getText().toString()!=null){
			String notes = txtNotes.getText().toString();
			SubmitAssignTask task = new SubmitAssignTask(this, notes, uuid_task_h, assignId, flag);
			task.execute();
		}else{
			Toast.makeText(getApplicationContext(), getString(R.string.select_cmo),
					   Toast.LENGTH_LONG).show();
		}
		
	}
	public void doLookupCMO(View view){
		Intent intent = new Intent(this, LookupAssignment.class);
		Bundle extras = new Bundle();
		extras.putString(Global.BUND_KEY_TASK, flag);
		extras.putString(Global.BUND_KEY_UUID_TASKH, uuid_task_h);
		intent.putExtras(extras);
		startActivityForResult(intent, Global.REQUEST_CODE_LOOKUP);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Global.REQUEST_CODE_LOOKUP){		//it's the one we request from the beginning
			String value = data.getStringExtra(Global.BUND_KEY_ASSIGNEE_VALUE);
			assignId = data.getStringExtra(Global.BUND_KEY_ASSIGNEE_ID);
			String job = data.getStringExtra(Global.BUND_KEY_ASSIGNEE_JOB);
			txtCMO.setText(job+" - "+value);
			txtCMO.setVisibility(View.VISIBLE);
		}
	}
	@Override
	public void onClick(View v) {
		ImageThumbnail imgThumbnail = null;
		try {
			imgThumbnail = (ImageThumbnail) v;
			if (imgThumbnail.getResultImg() != null){
				try {
					Bitmap image = Utils.byteToBitmap(imgThumbnail.getResultImg());
					DialogManager.showImageDialog(this, image);
					
				} catch (Exception e) {
					
				}
			}
			else{
				String fieldKey = thumbnailMapping.get(v);
				String[] latLng = Tool.split(fieldKey, ",");
				if(latLng.length>1){
					String lat=latLng[0];
					String lng=latLng[1];
					Intent intent = new Intent(this, MapsViewer.class);
					intent.putExtra("latitude", lat);
					intent.putExtra("longitude", lng);
					startActivity(intent);
				}else{
					if (nomorOrder != null && !"".equals(nomorOrder)){
						targetThumbnail = (ImageThumbnail)v;		//store thumbnail for further reference after connection finish
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair(Global.BUND_KEY_UUID_TASKH, uuid_task_h));
						params.add(new BasicNameValuePair(Global.BUND_KEY_QUESTIONID, fieldKey));
							new getImageTask(this,params).execute();
					}
					else{
						Toast.makeText(this, getString(R.string.path_no_received), Toast.LENGTH_SHORT).show();
					}
				}
			}
		} catch (Exception e) {
		}
	}
}
