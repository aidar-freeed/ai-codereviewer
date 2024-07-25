package com.adins.mss.coll.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.checkin.CheckInManager;
import com.adins.mss.base.commons.SecondHelper;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.coll.R;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.image.ViewImageActivity;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.androidquery.AQuery;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.List;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class DepositReportDetailActivity extends AppCompatActivity{
	public static DepositReportH report;
	protected int total = 0;
	List<DepositReportD> reportDs;
	String cashierName;
	String bankAccount;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	private FirebaseAnalytics screenName;

	@Override
	public void onBackPressed() {
		if(!Global.BACKPRESS_RESTRICTION) {
			super.onBackPressed();
			try {
				this.getFragmentManager().popBackStack();
			} catch (Exception e) {
				for (int i = 1; i < this.getFragmentManager().getBackStackEntryCount(); i++)
					this.getFragmentManager().popBackStack();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(com.adins.mss.base.R.menu.main_menu, menu);
		mainMenu = menu;
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		updateMenuIcon(Global.isGPS);

		if(Global.ENABLE_USER_HELP &&
				(Global.userHelpGuide.get(DepositReportDetailActivity.this.getClass().getSimpleName())!=null) ||
				Global.userHelpDummyGuide.get(DepositReportDetailActivity.this.getClass().getSimpleName()) != null){
			menu.findItem(com.adins.mss.base.R.id.mnGuide).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private static Menu mainMenu;

	public static void updateMenuIcon(boolean isGPS) {
		UpdateMenuIcon uItem = new UpdateMenuIcon();
		uItem.updateGPSIcon(mainMenu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == com.adins.mss.base.R.id.mnGPS) {
			if (Global.LTM != null) {
				if (Global.LTM.getIsConnected()) {
					Global.LTM.removeLocationListener();
					Global.LTM.connectLocationClient();
				} else {
					CheckInManager.startGPSTracking(getApplicationContext());
				}
				Animation a = AnimationUtils.loadAnimation(this, com.adins.mss.base.R.anim.gps_rotate);
				findViewById(com.adins.mss.base.R.id.mnGPS).startAnimation(a);
			}
		}

		if(id == com.adins.mss.base.R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
			UserHelp.reloadUserHelp(getApplicationContext(), DepositReportDetailActivity.this);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					UserHelp.showAllUserHelp(DepositReportDetailActivity.this, DepositReportDetailActivity.this.getClass().getSimpleName());
				}
			}, SHOW_USERHELP_DELAY_DEFAULT);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_deposit_report_detail);
		screenName = FirebaseAnalytics.getInstance(this);

		Toolbar toolbar = (Toolbar) findViewById(com.adins.mss.base.R.id.toolbar);
		toolbar.setTitle(getString(R.string.title_mn_depositreport));
		toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
		setSupportActionBar(toolbar);

		AQuery query = new AQuery(this);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
		cashierName = report.getCashier_name();
		bankAccount = report.getBank_account();

		reportDs = report.getDepositReportDList();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
						
		query.id(R.id.txtBatchId).text(report.getBatch_id());
		if(cashierName!=null&&cashierName.length()>0){			
			query.id(R.id.txttransferBy).text("Cashier");
			query.id(R.id.txtCashierName).text(cashierName);
			query.id(R.id.rowBankName).visibility(View.GONE);
			query.id(R.id.rowAccountNumber).visibility(View.GONE);
			query.id(R.id.rowEvidenceTransfer).visibility(View.GONE);
		}else{
			query.id(R.id.txttransferBy).text("Bank");
			query.id(R.id.rowCashierName).visibility(View.GONE);			
			query.id(R.id.txtBankName).text(report.getBank_name());
			query.id(R.id.txtAccountNumber).text(report.getBank_account());
			query.id(R.id.imgEvidenceTransfer).image(Utils.byteToBitmap(report.getImage())).clicked(this, "ViewImage");
		}
		
		ImageButton btnPrintDepReport = (ImageButton)findViewById(R.id.btnPrintDepReport);
		btnPrintDepReport.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!mBluetoothAdapter.isEnabled()){
					mBluetoothAdapter.enable();
					Toast.makeText(DepositReportDetailActivity.this, getString(R.string.bluetooth_message), Toast.LENGTH_SHORT).show();
				}else{
					SecondHelper.PrintParams printParams = new SecondHelper.PrintParams(report.getBatch_id(), null, true, null);
					SecondHelper.Companion.doPrint(v.getContext(), printParams);
				}
			}
		});

		TableLayout detailTable = (TableLayout)findViewById(R.id.tableLayout1);
		LayoutParams lp =new LayoutParams(MATCH_PARENT, MATCH_PARENT,0.25f);
		
		LinearLayout detail = new LinearLayout(this);
		detail.setOrientation(LinearLayout.HORIZONTAL);					
		detail.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1.0f));

		TextView lblDetail = new TextView(this);
		lblDetail.setText("Detail");
		lblDetail.setTextColor(Color.BLACK);
		lblDetail.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT,0.25f));
		detail.addView(lblDetail);
		detailTable.addView(detail);

		int total = 0;
		
		for (int i = 0; i < reportDs.size(); i++) {
			try {
				DepositReportD reportD = reportDs.get(i);				
				LinearLayout row = new LinearLayout(this);
				row.setOrientation(LinearLayout.HORIZONTAL);
				row.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1.0f));
				String agreement_no = reportD.getAgreement_no();
				if(agreement_no == null){
					List <PrintResult> pr = PrintResultDataAccess.getAll(getApplicationContext(), report.getBatch_id());
					for(PrintResult res:pr){
						if("Agreement No".equalsIgnoreCase(res.getLabel()) && null!=res.getValue()){
							agreement_no = res.getValue();
						}
					}
				}

				TextView lblLabel = new TextView(this);
				lblLabel.setText(agreement_no);
				lblLabel.setTextColor(Color.BLACK);
				lblLabel.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT,0.25f));
				row.addView(lblLabel);

				TextView lblSpace = new TextView(this);
				lblSpace.setText(" : ");
				lblSpace.setTextColor(Color.BLACK);
				lblSpace.setLayoutParams(new LayoutParams(WRAP_CONTENT, MATCH_PARENT));
				row.addView(lblSpace);

				try {
					TextView lblAnswer = new TextView(this);
					lblAnswer.setText(Tool.separateThousand(reportD.getDeposit_amt()));
					lblAnswer.setTextColor(Color.BLACK);
					lblAnswer.setGravity(Gravity.RIGHT);
					lblAnswer.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT,0.25f));
					row.addView(lblAnswer, lp);
				} catch (Exception e) {
					FireCrash.log(e);
				}

				detailTable.addView(row);

				String value = reportD.getDeposit_amt();
				if(value==null || value.equals("")) value = "0";
				total += Integer.parseInt(value);
			} catch (Exception e) {
				FireCrash.log(e);

			}
		}

		TextView txtTotal = (TextView) findViewById(R.id.txtTotal);
		txtTotal.setText(Tool.separateThousand(total));


		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				UserHelp.showAllUserHelp(DepositReportDetailActivity.this,DepositReportDetailActivity.this.getClass().getSimpleName());
			}
		}, SHOW_USERHELP_DELAY_DEFAULT);

	}

	@Override
	protected void onResume() {
		super.onResume();
		//Set Firebase screen name
		screenName.setCurrentScreen(this, getString(R.string.screen_name_deposit_report_detail), null);
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

	@Keep
	 public void ViewImage(View view){
		 	Global.getSharedGlobal().setIsViewer(true);
			Bundle extras = new Bundle();
			extras.putByteArray(Global.BUND_KEY_IMAGE_BYTE, report.getImage());
			Intent intent = new Intent(this, ViewImageActivity.class);
			intent.putExtras(extras);
			startActivity(intent);
	 }
	
	 @Keep
	 private class RecapitulationListAdapter extends ArrayAdapter<DepositReportD> {

	        private final DepositReportD[] originalItems;

	        public RecapitulationListAdapter(Context context, DepositReportD[] objects) {
	            super(context, R.layout.deposit_report_list_recap, objects);
	            originalItems = objects;
	        }

	        @Override
	        public int getCount() {
	            return super.getCount() + 1;
	        }


	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            View view = null;
	                view = LayoutInflater.from(getContext()).inflate(R.layout.deposit_report_list_recap, parent, false);
	                

	                TextView label = (TextView) view.findViewById(R.id.itemLabel);
	                TextView value = (TextView) view.findViewById(R.id.itemValue);
	                TextView agreement= (TextView) view.findViewById(R.id.itemValueAgreement);

	                if (position == getCount() - 1) {
	                    label.setText("Total");
	                    value.setText(Tool.separateThousand(String.valueOf(sumOfItems())));
	                } else {
	                	DepositReportD item = getItem(position);	                	
	                	TaskH taskHs= TaskHDataAccess.getOneHeader(getApplicationContext(), item.getUuid_task_h());
	                	String agreement_no = "";
	                	if(taskHs!=null)
	                		agreement_no = taskHs.getAppl_no();
	                	agreement.setText(agreement_no);
	                    value.setText(Tool.separateThousand(item.getDeposit_amt()));
	                }	            

	            return view;
	        }

	        private int sumOfItems() {
	            int sum = 0;
	            try {
	            	for (DepositReportD item : originalItems) {
	            		String value = item.getDeposit_amt();
	            		if(value==null || value.equals("")) value = "0";
	            		int finalValue = Integer.parseInt(value);
	                    sum += finalValue;
	                }
				} catch (Exception e) {
					FireCrash.log(e);
				}
	            total = sum;
	            return sum;
	        }
	    }
}
