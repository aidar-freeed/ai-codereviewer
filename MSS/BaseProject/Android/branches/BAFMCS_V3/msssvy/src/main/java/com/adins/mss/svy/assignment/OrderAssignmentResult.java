package com.adins.mss.svy.assignment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.checkin.CheckInManager;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.timeline.MapsViewer;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.adins.mss.svy.R;
import com.adins.mss.svy.reassignment.JsonRequestDetailOrder;
import com.adins.mss.svy.reassignment.JsonResponseServer;
import com.adins.mss.svy.reassignment.JsonResponseServer.ResponseServer;
import com.adins.mss.svy.tool.ImageThumbnail;
import com.adins.mss.svy.tool.getImageTask;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class OrderAssignmentResult extends AppCompatActivity implements OnClickListener{
	String nomorOrder;
	TableLayout detailTable;
	Button btnLookupCMO;
	Button btnSubmit;
	TextView form_name;
	ScrollView scrollView;
	int taskType;
	String flag;
	String uuid_task_h;
	String formName;
	TextView txtCMO;
	EditText txtNotes;
	CardView lookupCMOLayout;
	Map<ImageThumbnail, String> thumbnailMapping = new HashMap<>();
	List<ResponseServer> list;
	String assignId;
	public static ImageThumbnail targetThumbnail = null;		
	public boolean isSoftKeyboardDisplayed = false;
	private FirebaseAnalytics screenName;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(com.adins.mss.base.R.menu.main_menu, menu);
		mainMenu = menu;
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		updateMenuIcon();

		if(Global.ENABLE_USER_HELP &&
				(Global.userHelpGuide.get(OrderAssignmentResult.this.getClass().getSimpleName())!=null) ||
				Global.userHelpDummyGuide.get(OrderAssignmentResult.this.getClass().getSimpleName()) != null){
			menu.findItem(com.adins.mss.base.R.id.mnGuide).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private static Menu mainMenu;

	public static void updateMenuIcon() {
		UpdateMenuIcon uItem = new UpdateMenuIcon();
		uItem.updateGPSIcon(mainMenu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == com.adins.mss.base.R.id.mnGPS && Global.LTM != null) {
			if (Global.LTM.getIsConnected()) {
				Global.LTM.removeLocationListener();
				Global.LTM.connectLocationClient();
			} else {
				CheckInManager.startGPSTracking(getApplicationContext());
			}
			Animation a = AnimationUtils.loadAnimation(this, com.adins.mss.base.R.anim.gps_rotate);
			findViewById(com.adins.mss.base.R.id.mnGPS).startAnimation(a);
		}

		if(id==R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
			UserHelp.reloadUserHelp(getApplicationContext(), OrderAssignmentResult.this);
			scrollView.scrollTo(0, findViewById(R.id.Bottomlayout).getBottom());
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					UserHelp.showAllUserHelp(OrderAssignmentResult.this, OrderAssignmentResult.this.getClass().getSimpleName());
				}
			}, SHOW_USERHELP_DELAY_DEFAULT);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_survey_assignment_result_activity);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
		nomorOrder = getIntent().getExtras().getString(Global.BUND_KEY_ORDERNO);
		taskType = getIntent().getExtras().getInt(Global.BUND_KEY_TASK_TYPE);
		uuid_task_h = getIntent().getExtras().getString(Global.BUND_KEY_UUID_TASKH);
		formName = getIntent().getExtras().getString(Global.BUND_KEY_FORM_NAME);
		flag = flagParamForType(taskType);

		scrollView = findViewById(R.id.scrollView);

		screenName = FirebaseAnalytics.getInstance(this);

		Toolbar toolbar = (Toolbar) findViewById(com.adins.mss.base.R.id.toolbar);
		toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
        toolbar.setTitle(getString(R.string.title_mn_surveyassign));
		setSupportActionBar(toolbar);

		switch (taskType) {
			case Global.TASK_ORDER_ASSIGNMENT:
				toolbar.setTitle(getString(R.string.title_mn_surveyassign));
				break;
			case Global.TASK_ORDER_REASSIGNMENT:
				toolbar.setTitle(getString(R.string.title_mn_surveyreassign));
				break;
			default:
				break;
		}

		initialize();
		new GetRequestDataDetail(this,nomorOrder, flag).execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Set Firebase screen name
		screenName.setCurrentScreen(this, getString(R.string.screen_name_survey_assignment_result), null);
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
		detailTable = (TableLayout)findViewById(R.id.orderDetailTable);
		btnLookupCMO = (Button)findViewById(R.id.btnLookupCMO);
		btnSubmit = (Button)findViewById(R.id.btnSubmit);
		form_name = (TextView)findViewById(R.id.formName);
		txtCMO = (TextView)findViewById(R.id.txtCMO);
		txtNotes = (EditText)findViewById(R.id.txtNotes);

		txtNotes.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				isSoftKeyboardDisplayed = true;
				txtNotes.requestFocus();
				txtNotes.setFocusable(true);
				txtNotes.setFocusableInTouchMode(true);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(v, 0);
				return false;
			}
		});
		
		btnSubmit.requestFocus();
		btnSubmit.setFocusable(true);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		View v = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);
		if (v instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];
			if (event.getAction() == MotionEvent.ACTION_UP
					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
							.getBottom())) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
				isSoftKeyboardDisplayed = false;
			}
		}
		return ret;
	}
	
	@Override
	public void onBackPressed(){
		if (isSoftKeyboardDisplayed) {
			isSoftKeyboardDisplayed = false;
		}else{
			if(!Global.BACKPRESS_RESTRICTION) {
				super.onBackPressed();
			}
		}
	}
	
	public class GetRequestDataDetail extends AsyncTask<Void, Void, String> {
		private ProgressDialog progressDialog;
		private WeakReference<Activity> activity;
		private String flag;
		private String nomorOrder;

		public GetRequestDataDetail(Activity activity,String nomorOrder, String flag) {
			this.activity = new WeakReference<>(activity);
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
			request.setUuid_task_h(uuid_task_h);
			String json = GsonHelper.toJson(request);
			String url = GlobalData.getSharedGlobalData().getURL_GET_DETAIL_ORDER();
			boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
			boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
			HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
    		HttpConnectionResult serverResult = null;    		
			try {
				serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
				if(serverResult.isOK())
					result = serverResult.getResult();
			} catch (Exception e) {
				FireCrash.log(e);
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
				} catch (Exception e) {             FireCrash.log(e);
				}
			}
			if("".equals(result)){
				NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getApplicationContext());
				dialogBuilder.withTitle(getString(R.string.info_capital))
					.withMessage(getString(R.string.msgNoDetail))
					.show();
			}else if (null != result){
				loadDataDetailFromServer(result);
			}
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					scrollView.scrollTo(0, findViewById(R.id.Bottomlayout).getBottom());
					UserHelp.showAllUserHelp(OrderAssignmentResult.this,OrderAssignmentResult.this.getClass().getSimpleName());
				}
			}, SHOW_USERHELP_DELAY_DEFAULT);
		}
	}
	private void loadDataDetailFromServer(String result){
		
		int no =1;
		LayoutParams lpSpace =new LayoutParams(WRAP_CONTENT, MATCH_PARENT);
		LayoutParams lpQuestion =new LayoutParams(MATCH_PARENT, MATCH_PARENT,0.3f);
		LayoutParams lpImage =new LayoutParams(MATCH_PARENT, MATCH_PARENT, 0.4f);
		LayoutParams lpNo =new LayoutParams(WRAP_CONTENT, MATCH_PARENT);
		try {
			JsonResponseServer resultOrder = GsonHelper.fromJson(result, JsonResponseServer.class);
			form_name.setText("Form : "+formName);
			list = resultOrder.getListResponseServer();
			for(ResponseServer responseServer : list){
				String question = responseServer.getKey();
				final String answer = responseServer.getValue();
				String flag = responseServer.getFlag();
				
				LinearLayout row = new LinearLayout(this);
				row.setOrientation(LinearLayout.HORIZONTAL);					
				row.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1.0f));
				TextView lblNo = new TextView(this);
				lblNo.setText(no+ ". ");
				lblNo.setGravity(Gravity.LEFT);
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
				if(flag.equals(Global.FLAG_FOR_IMAGE) || flag.equals(Global.FLAG_FOR_IMAGE_WITH_GPS)) {
					final ImageThumbnail thumb = new ImageThumbnail(this, 100, 100);
					thumb.setOnClickListener(this);
					if (flag.equals(Global.FLAG_FOR_IMAGE_WITH_GPS)) {
						thumb.setLayoutParams(lpImage);
					} else {
						thumb.setLayoutParams(lpQuestion);
					}
					thumbnailMapping.put(thumb, answer);
					row.addView(thumb);

					if (flag.equals(Global.FLAG_FOR_IMAGE_WITH_GPS)) {
						final ImageThumbnail thumbLoc = new ImageThumbnail(this, 100, 100);
						thumbLoc.setImageResource(R.drawable.ic_absent);
						thumbLoc.setLayoutParams(lpImage);
						row.addView(thumbLoc);

						thumbLoc.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								try {
									String[] value = Tool.split(answer, ",");
									if (value.length > 2) {
										String lat = value[1];
										String lng = value[2];
										Intent intent = new Intent(getApplicationContext(), MapsViewer.class);
										intent.putExtra("latitude", lat);
										intent.putExtra("longitude", lng);
										startActivity(intent);
									}
								} catch (Exception e) {
									FireCrash.log(e);
								}
							}
						});
					}

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
		} catch (Exception e) {             FireCrash.log(e);
		}
	}
	public void doSubmit(View view){
		if(assignId != null && assignId.length()>0){
			String notes = txtNotes.getText().toString();
			if (notes.length() == 0 && notes.equals("")) {
				Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.notes_required),
						Toast.LENGTH_LONG).show();
			} else {
				SubmitAssignTask task = new SubmitAssignTask(this, notes, uuid_task_h, assignId, flag);
				task.execute();
			}
		}else{
			Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.select_cmo),
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
			btnLookupCMO.setText(job+" - "+value);
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
					FireCrash.log(e);
				}
			}
			else{
				String fieldKey = thumbnailMapping.get(v);
				String[] value = Tool.split(fieldKey, ",");

				if (value.length == 1) {
					if (nomorOrder != null && !"".equals(nomorOrder)){
						targetThumbnail = (ImageThumbnail)v;		//store thumbnail for further reference after connection finish

						List<NameValuePair> params = new ArrayList<>();
						params.add(new BasicNameValuePair(Global.BUND_KEY_UUID_TASKH, uuid_task_h));
						params.add(new BasicNameValuePair(Global.BUND_KEY_QUESTIONID, fieldKey));

						new getImageTask(this,params).execute();
					}
					else{
						Toast.makeText(this, "ERROR: path not received", Toast.LENGTH_SHORT).show();
					}
				} else if (value.length > 2) {
					String img = value[0];

					if (nomorOrder != null && !"".equals(nomorOrder)){
						targetThumbnail = (ImageThumbnail)v;		//store thumbnail for further reference after connection finish

						List<NameValuePair> params = new ArrayList<>();
						params.add(new BasicNameValuePair(Global.BUND_KEY_UUID_TASKH, uuid_task_h));
						params.add(new BasicNameValuePair(Global.BUND_KEY_QUESTIONID, img));

						new getImageTask(this,params).execute();

					}
					else{
						Toast.makeText(this, "ERROR: path not received", Toast.LENGTH_SHORT).show();
					}
				} else {
					String lat = value[0];
					String lng = value[1];
					Intent intent = new Intent(this, MapsViewer.class);
					intent.putExtra("latitude", lat);
					intent.putExtra("longitude", lng);
					startActivity(intent);
				}
			}
		} catch (Exception e) {             FireCrash.log(e);
		}
	}
}
