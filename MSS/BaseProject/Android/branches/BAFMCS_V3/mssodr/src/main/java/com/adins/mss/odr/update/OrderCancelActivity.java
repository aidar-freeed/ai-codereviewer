package com.adins.mss.odr.update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.image.ViewImageActivity;
import com.adins.mss.odr.R;
import com.adins.mss.odr.R.string;
import com.adins.mss.odr.tool.ImageThumbnail;
import com.adins.mss.odr.tool.getImageTask;
import com.adins.mss.odr.update.JsonResponseDetailCancelOrder.ResponseServer;

import org.acra.ACRA;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;


public class OrderCancelActivity extends Activity implements OnClickListener {
	String nomorOrder;
	int taskType;
	String flag;
	String uuid_task_h;
	TableLayout detailTable;
	Button btnCancel;
	Button btnReject;
	public static byte[] imageResult=null;
	Map<ImageThumbnail, String> thumbnailMapping = new HashMap<>();
	List<ResponseServer> list;
	public static ImageThumbnail targetThumbnail = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ordercancel_activity);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
		nomorOrder = getIntent().getExtras().getString(Global.BUND_KEY_ORDERNO);
		taskType = getIntent().getExtras().getInt(Global.BUND_KEY_TASK_TYPE);
		uuid_task_h = getIntent().getExtras().getString(Global.BUND_KEY_UUID_TASKH);
		flag = flagParamForType(taskType);
		
		initialize();
		
		new GetRequestDataDetail(nomorOrder, flag).execute();
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
	private void initialize(){
		detailTable = (TableLayout)findViewById(R.id.tableListLayout);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		btnReject = (Button)findViewById(R.id.btnRejectOdr);
		btnReject.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}
	private void loadDataDetailFromServer(String result){
		
		int no =1;
		LayoutParams lpSpace =new LayoutParams(WRAP_CONTENT, MATCH_PARENT);
		LayoutParams lpQuestion =new LayoutParams(MATCH_PARENT, MATCH_PARENT,0.25f);
		LayoutParams lpNo =new LayoutParams(MATCH_PARENT, MATCH_PARENT,0.38f);
		try {
			JsonResponseDetailCancelOrder resultOrder = GsonHelper.fromJson(result, JsonResponseDetailCancelOrder.class);
			list = resultOrder.getListDetailOrder();
			for(ResponseServer responseServer : list){
				String question = responseServer.getKey();
				String answer = responseServer.getValue();
				String flag = responseServer.getFlag();
				
				LinearLayout row = new LinearLayout(this);
				row.setOrientation(LinearLayout.HORIZONTAL);					
				row.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1.0f));
				if (no % 2 == 1) {
                    row.setBackgroundResource(R.color.tv_gray_light);
                }else{
                	row.setBackgroundResource(R.color.tv_gray);
                }
				
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
				if(flag.equals(Global.TRUE_STRING)){
					int w = 200;
					int h = 160;
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
				}else{
					TextView lblAnswer = new TextView(this);
					lblAnswer.setText(answer);
					lblAnswer.setLayoutParams(lpQuestion);					
					row.addView(lblAnswer);
					
				}
				detailTable.addView(row);
				no++;
			}
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					UserHelp.showAllUserHelp(OrderCancelActivity.this,OrderCancelActivity.this.getClass().getSimpleName());
				}
			}, SHOW_USERHELP_DELAY_DEFAULT);
		} catch (Exception e) {
			FireCrash.log(e);
		}
	}
	//=== Generate Flag ===//
	protected String flagParamForType(int taskType){
			switch (taskType) {
			case Global.TASK_ORDER_ASSIGNMENT:
				return "DOA";
			case Global.TASK_ORDER_REASSIGNMENT:
				return "DOR";
			case Global.TASK_CANCEL_ORDER:
				return "DCO";
			default:
				return "";
			}
	}
	protected String flagParamForSubmitForType(int taskType){
		switch (taskType) {
		case Global.TASK_ORDER_ASSIGNMENT:
			return "SOA";
		case Global.TASK_ORDER_REASSIGNMENT:
			return "SOR";
		case Global.TASK_CANCEL_ORDER:
			return "SCO";
		default:
			return "";
		}
	}

	@Override
	public void onBackPressed() {
		if(!Global.BACKPRESS_RESTRICTION) {
			super.onBackPressed();
		}
	}

	private class GetRequestDataDetail extends AsyncTask<Void, Void, String> {
		private ProgressDialog progressDialog;
		private String flag;
		private String nomorOrder;

		public GetRequestDataDetail(String nomorOrder, String flag) {
			this.nomorOrder = nomorOrder;
			this.flag = flag;
		}
		
		@Override
		protected void onPreExecute() {
	    	progressDialog = ProgressDialog.show(OrderCancelActivity.this,
					"", getString(R.string.progressWait), true);
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			JsonRequestDetailCancelOrder request = new JsonRequestDetailCancelOrder();
			request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
			request.setNomor_order(this.nomorOrder);
			request.setUuid_task_h(uuid_task_h);
			
			String json = GsonHelper.toJson(request);
			String url = GlobalData.getSharedGlobalData().getURL_GET_DETAIL_CANCELORDER();
			boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
			boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
			HttpCryptedConnection httpConn = new HttpCryptedConnection(getApplicationContext(), encrypt, decrypt);
    		HttpConnectionResult serverResult = null;
			try {
				serverResult = httpConn.requestToServer(url, json);
				result = serverResult.getResult();			
			}
			catch (Exception e) {
				e.printStackTrace();
				try {
					progressDialog.dismiss();
				} catch (Exception ex) {
					FireCrash.log(e);
				}
			}
			
			return result;
		}
		@Override
		protected void onPostExecute(String result){
			if (progressDialog.isShowing()){
				try {
					progressDialog.dismiss();
				} catch (Exception e) {
					FireCrash.log(e);
				}
			}
			if(!GlobalData.isRequireRelogin()) {
				if ("".equals(result)) {
					NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getApplicationContext());
					dialogBuilder.withTitle(getString(string.info_capital))
							.withMessage(getString(R.string.no_result_from_server))
							.show();
				} else {
					loadDataDetailFromServer(result);
				}
			} else{
				DialogManager.showForceExitAlert(OrderCancelActivity.this, OrderCancelActivity.this.getString(com.adins.mss.base.R.string.msgLogout));
			}
		}
	}
	
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		Global.getSharedGlobal().setIsViewer(false);
		if(id==R.id.btnCancel){
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(Global.BUND_KEY_ORDERNO, nomorOrder));
			params.add(new BasicNameValuePair(Global.BUND_KEY_TASK, "Cancel"));
			new CancelOrderTask(this, params).execute();
		}else if(id==R.id.btnRejectOdr){
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(Global.BUND_KEY_ORDERNO, nomorOrder));
			new RejectOrderTask(this, params).execute();
		}
		
		ImageThumbnail imgThumbnail = null;
		try {
			imgThumbnail = (ImageThumbnail) v;
			if(Tool.isInternetconnected(this)){
				if (imgThumbnail.getResultImg() != null){
					try {
						Global.getSharedGlobal().setIsViewer(true);
						Bundle extras = new Bundle();
						extras.putByteArray(Global.BUND_KEY_IMAGE_BYTE, imgThumbnail.getResultImg());
						Intent intent = new Intent(this, ViewImageActivity.class);
						intent.putExtras(extras);
						startActivity(intent);
					} catch (Exception e) {
						FireCrash.log(e);
					}
				}
				else {
					String fieldKey = thumbnailMapping.get(v);
					if (nomorOrder != null && !"".equals(nomorOrder)) {
						targetThumbnail = (ImageThumbnail) v;        //store thumbnail for further reference after connection finish

						List<NameValuePair> params = new ArrayList<>();
						params.add(new BasicNameValuePair(Global.BUND_KEY_UUID_TASKH, uuid_task_h));
						params.add(new BasicNameValuePair(Global.BUND_KEY_QUESTIONID, fieldKey));
						try {
							new getImageTask(this, params).execute();
							byte[] result = OrderCancelActivity.imageResult;
							try {
								targetThumbnail.setResultImg(result);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else {
						Toast.makeText(this, getString(R.string.path_no_received), Toast.LENGTH_SHORT).show();
					}
				}
			} else{
				Toast.makeText(this, this.getString(com.adins.mss.base.R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			FireCrash.log(e);
		}
		
	}

}
