package com.adins.mss.coll.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.checkin.CheckInManager;
import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.coll.R;
import com.adins.mss.coll.commons.Toaster;
import com.adins.mss.coll.interfaces.TasksImpl;
import com.adins.mss.coll.interfaces.TasksInterface;
import com.adins.mss.coll.interfaces.callback.SaveDataInstallmentCallback;
import com.adins.mss.coll.models.InstallmentScheduleResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.InstallmentSchedule;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.db.dataaccess.InstallmentScheduleDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.List;
import java.util.Locale;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

/**
 * Created by winy.firdasari on 03/02/2015.
 */

public class InstallmentScheduleFragment extends AppCompatActivity implements TaskListener,SaveDataInstallmentCallback, View.OnClickListener {
    ImageButton imageButton;
    private String taskId;

	InstallmentScheduleResponse scheduleResponse;
    public static InstallmentSchedule detailInstallmentSchedule;
	private TasksInterface Tasks;
	private FirebaseAnalytics screenName;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        setContentView(R.layout.new_fragment_installment_schedule);

		screenName = FirebaseAnalytics.getInstance(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(getString(R.string.title_mn_installmentschedule));
		toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
		setSupportActionBar(toolbar);

        Bundle bundle 	= getIntent().getExtras();
        taskId = bundle.getString(Global.BUND_KEY_TASK_ID);
		Tasks  = new TasksImpl(this);
        
        TaskH taskH = TaskHDataAccess.getOneTaskHeader(this, taskId);
        if(taskH!=null){
	        InstallmentScheduleDataAccess.getAllByTask(this, taskH.getUuid_task_h());

			Tasks.getDataInstallmentSchedule(taskId, this);
	
	        imageButton = (ImageButton) findViewById(R.id.imageBtnDownload);
	        imageButton.setOnClickListener(this);
        }

    }

	@Override
	protected void onResume() {
		super.onResume();
		//Set Firebase screen name
		screenName.setCurrentScreen(this, getString(R.string.screen_name_installment_schedule), null);
	}

	@Override
	public void onBackPressed() {
		if(!Global.BACKPRESS_RESTRICTION) {
			super.onBackPressed();
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
				(Global.userHelpGuide.get(InstallmentScheduleFragment.this.getClass().getSimpleName())!=null) ||
				Global.userHelpDummyGuide.get(InstallmentScheduleFragment.this.getClass().getSimpleName()) != null){
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
	public void onCompleteTask(Object result) {
		InstallmentScheduleResponse installmentScheduleResponse = (InstallmentScheduleResponse) result;
		scheduleResponse = installmentScheduleResponse;
		TextView agreementNumber = (TextView) findViewById(R.id.agreementNumber);
		agreementNumber.setText(installmentScheduleResponse.getAgreementNo());

		TableLayout table = (TableLayout) findViewById(R.id.tableHeaders);
		int index = 1;

		if(installmentScheduleResponse.getInstallmentScheduleList().isEmpty()){
			NiftyDialogBuilder.getInstance(InstallmentScheduleFragment.this)
					.withMessage(R.string.no_data_from_server)
					.withTitle(getString(R.string.info_capital))
					.isCancelableOnTouchOutside(false)
					.withButton1Text(getString(R.string.btnClose))
					.setButton1Click(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
						}
					})
					.show();
		}

		for (InstallmentSchedule item : installmentScheduleResponse.getInstallmentScheduleList()) {

			View row = LayoutInflater.from(InstallmentScheduleFragment.this).inflate(R.layout.view_row_installment_schedule, table, false);
			TextView no = (TextView) row.findViewById(R.id.no);
			TextView dueDate = (TextView) row.findViewById(R.id.dueDate);
			TextView amountInstallment = (TextView) row.findViewById(R.id.amountInstallment);
			TextView amountPaid = (TextView) row.findViewById(R.id.amountPaid);

			row.setTag(item);

			no.setText(String.valueOf(index++));
			dueDate.setText(Formatter.formatDate(item.getDue_date(), Global.DATE_STR_FORMAT));
			amountInstallment.setText(item.getInstallment_amount());
			amountPaid.setText(item.getInstallment_paid_amount());

			String amtIns = item.getInstallment_amount();
			if(amtIns!=null && amtIns.length()>3){
				String part1 = amtIns.substring(amtIns.length()-3);
				if(part1.substring(0, 1).equals("."))
				{
					amountInstallment.setGravity(Gravity.RIGHT);
				}
			}


			String amtPaid = item.getInstallment_paid_amount();
			if(amtPaid!=null && amtPaid.length()>3){
				String part2 = amtPaid.substring(amtPaid.length()-3);
				if(part2.substring(0, 1).equals("."))
				{
					amountPaid.setGravity(Gravity.RIGHT);
				}
			}

			row.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					detailInstallmentSchedule = (InstallmentSchedule) v.getTag();
					Intent intent = new Intent(InstallmentScheduleFragment.this, InstallmentScheduleDetailFragment.class);
					startActivity(intent);
				}
			});

			table.addView(row);

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					UserHelp.showAllUserHelp(InstallmentScheduleFragment.this,InstallmentScheduleFragment.this.getClass().getSimpleName());
				}
			}, SHOW_USERHELP_DELAY_DEFAULT);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == com.adins.mss.base.R.id.mnGPS && Global.LTM != null) {
			if (Global.LTM.getIsConnected()) {
				Global.LTM.removeLocationListener();
				Global.LTM.connectLocationClient();
			} else {
				CheckInManager.startGPSTracking(getApplicationContext());
			}
			Animation a = AnimationUtils.loadAnimation(this, com.adins.mss.base.R.anim.gps_rotate);
			findViewById(com.adins.mss.base.R.id.mnGPS).startAnimation(a);
		}
		if(item.getItemId() == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
			UserHelp.reloadUserHelp(getApplicationContext(), InstallmentScheduleFragment.this);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					UserHelp.showAllUserHelp(InstallmentScheduleFragment.this, InstallmentScheduleFragment.this.getClass().getSimpleName());
				}
			}, SHOW_USERHELP_DELAY_DEFAULT);
		}
    	return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCancelTask(boolean value) {
		//EMPTY
	}

	@Override
	public void onLocalData(Object result) {
		List<InstallmentSchedule> installmentScheduleList = (List<InstallmentSchedule>) result;
		TextView agreementNumber = (TextView) findViewById(R.id.agreementNumber);
		agreementNumber.setText(installmentScheduleList.get(0).getAgreement_no());

		TableLayout table = (TableLayout) findViewById(R.id.tableHeaders);
		int index = 1;

		for (InstallmentSchedule item : installmentScheduleList) {

			View row = LayoutInflater.from(InstallmentScheduleFragment.this).inflate(R.layout.view_row_installment_schedule, table, false);
			TextView no = (TextView) row.findViewById(R.id.no);
			TextView dueDate = (TextView) row.findViewById(R.id.dueDate);
			TextView amountInstallment = (TextView) row.findViewById(R.id.amountInstallment);
			TextView amountPaid = (TextView) row.findViewById(R.id.amountPaid);

			row.setTag(item);

			no.setText(String.valueOf(index++));
			dueDate.setText(Formatter.formatDate(item.getDue_date(), Global.DATE_STR_FORMAT));

			String amtIns = item.getInstallment_amount();
			if(amtIns!=null && amtIns.length()>3){
				String part1 = amtIns.substring(amtIns.length()-3);
				if(part1.substring(0, 1).equals("."))
				{
					amountInstallment.setGravity(Gravity.RIGHT);
				}
			}


			String amtPaid = item.getInstallment_paid_amount();
			if(amtPaid!=null && amtPaid.length()>3){
				String part2 = amtPaid.substring(amtPaid.length()-3);
				if(part2.substring(0, 1).equals("."))
				{
					amountPaid.setGravity(Gravity.RIGHT);
				}
			}


			amountInstallment.setText(item.getInstallment_amount());
			amountPaid.setText(item.getInstallment_paid_amount());

			row.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					detailInstallmentSchedule = (InstallmentSchedule) v.getTag();
					Intent intent = new Intent(InstallmentScheduleFragment.this, InstallmentScheduleDetailFragment.class);
					startActivity(intent);
				}
			});

			table.addView(row);
		}
	}

	@Override
	public void onSaveFinished(boolean result) {
		if(result){
			Toast.makeText(this, getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
		}
		else{
			Toaster.warning(this,getString(R.string.failed_save_data));
		}
		imageButton.setEnabled(true);
		imageButton.setClickable(true);
	}

	@Override
	public void onClick(View v) {
		imageButton.setEnabled(false);
		imageButton.setClickable(false);
		try {
			Tasks.saveDataInstallmentSchedule(this, scheduleResponse, taskId,this);
		} catch (Exception e) {
			FireCrash.log(e);
			ACRA.getErrorReporter().handleSilentException(new Exception("Error: Insert Installment schedule Error. "+ e.getMessage()));
		}
	}
}

