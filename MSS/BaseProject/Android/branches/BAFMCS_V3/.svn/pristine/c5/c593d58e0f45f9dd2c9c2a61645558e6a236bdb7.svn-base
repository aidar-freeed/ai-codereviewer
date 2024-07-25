package com.adins.mss.coll.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.coll.R;
import com.adins.mss.coll.api.CollectionActivityApi;
import com.adins.mss.coll.commons.Toaster;
import com.adins.mss.coll.models.CollectionActivityResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.CollectionActivity;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.db.dataaccess.CollectionActivityDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

/**
 * Created by dian.ina on 07/05/2015.
 */
public class CollectionActivityFragment extends AppCompatActivity {
    public CollectionActivityResponse collActivityResponse;
    ImageButton button;
    private String taskId;

    private ConnectivityManager connectivityManager;
	private NetworkInfo activeNetworkInfo;
    private List<CollectionActivity> collectionActivityLocalList = null;

	public static CollectionActivity itemCollectionActivity;


	private FirebaseAnalytics screenName;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		screenName = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.new_fragment_collection_activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        Bundle bundle = getIntent().getExtras();
        taskId = bundle.getString(Global.BUND_KEY_TASK_ID);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(getString(R.string.title_mn_collectionactivity));
		toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
		setSupportActionBar(toolbar);

        TaskH taskH = TaskHDataAccess.getOneTaskHeader(this, taskId);
        collectionActivityLocalList = CollectionActivityDataAccess.getAllbyTask(this, taskH.getUuid_task_h());

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        loadData();

        button = (ImageButton) findViewById(R.id.imageBtnDownload);
		button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	button.setEnabled(false);
            	button.setClickable(false);
				try {
					saveData(getApplicationContext(), collActivityResponse);
				} catch (Exception e) {
					FireCrash.log(e);
					ACRA.getErrorReporter().handleSilentException(new Exception("Error: download collection activity Error. "+ e.getMessage()));
				}
            }
        });
    }

	@Override
	protected void onResume() {
		super.onResume();
		//Set Firebase screen name
		screenName.setCurrentScreen(this, getString(R.string.screen_name_collection_act), null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(com.adins.mss.base.R.menu.main_menu, menu);
		mainMenu = menu;
		return true;
	}

	@Override
	public void onBackPressed() {
		if(!Global.BACKPRESS_RESTRICTION) {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		updateMenuIcon(Global.isGPS);

		if(Global.ENABLE_USER_HELP &&
				(Global.userHelpGuide.get(CollectionActivityFragment.this.getClass().getSimpleName())!=null) ||
				Global.userHelpDummyGuide.get(CollectionActivityFragment.this.getClass().getSimpleName()) != null){
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
			UserHelp.reloadUserHelp(getApplicationContext(), CollectionActivityFragment.this);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					UserHelp.showAllUserHelp(CollectionActivityFragment.this, CollectionActivityFragment.this.getClass().getSimpleName());
				}
			}, SHOW_USERHELP_DELAY_DEFAULT);
		}
    	return super.onOptionsItemSelected(item);
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

    protected void saveData(Context context, CollectionActivityResponse collActResp) {
    	if(collActResp!=null && collActResp.getStatus().getCode()==0){
	    	List<CollectionActivity> collectionActivityList = collActResp.getCollectionHistoryList();
	        if(collectionActivityList!=null&&!collectionActivityList.isEmpty()){
	        	TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
	        	CollectionActivityDataAccess.delete(context, taskH.getUuid_task_h());
	        	for(CollectionActivity collectionActivity : collectionActivityList){
					if (collectionActivity.getUuid_collection_activity() == null){
						collectionActivity.setUuid_collection_activity(Tool.getUUID());
					}
					collectionActivity.setUuid_task_h(taskH.getUuid_task_h());
	        	}
	        	CollectionActivityDataAccess.add(getApplicationContext(), collectionActivityList);
	        }
	        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
	    }else{
	    	Toaster.warning(context, context.getString(R.string.failed_save_data));
	    }
		button.setEnabled(true);
		button.setClickable(true);
	}

	public void loadData() {
        new AsyncTask<Void, Void, CollectionActivityResponse>() {
        	private ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(CollectionActivityFragment.this,
                        "", getString(R.string.progressWait), true);

            }
            @Override
            protected CollectionActivityResponse doInBackground(Void... params) {
                CollectionActivityApi api = new CollectionActivityApi(CollectionActivityFragment.this);
                try {

                	//bong 21 mei 15 - check internet connection
                	if(Tool.isInternetconnected(getApplicationContext())){
                		return api.request(taskId);
                	}
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(CollectionActivityResponse collectionActivityResponse) {
                super.onPostExecute(collectionActivityResponse);
                if (progressDialog!=null&&progressDialog.isShowing()){
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                }
                if(!GlobalData.isRequireRelogin()) {
					if (collectionActivityResponse == null) {

						TaskH taskH = TaskHDataAccess.getOneTaskHeader(getApplicationContext(), taskId);
						if (taskH != null) {
							List<CollectionActivity> collectionActivityList = CollectionActivityDataAccess.getAllbyTask(getApplicationContext(), taskH.getUuid_task_h());
							if (collectionActivityList != null && !collectionActivityList.isEmpty()) {
								TextView agreementNumber = (TextView) findViewById(R.id.agreementNumber);
								agreementNumber.setText(collectionActivityList.get(0).getAgreement_no());

								TableLayout table = (TableLayout) findViewById(R.id.tableHeaders);
								int index = 1;

								for (CollectionActivity item : collectionActivityList) {
									View row = LayoutInflater.from(CollectionActivityFragment.this).inflate(R.layout.view_row_collection_activity, table, false);
									TextView no = (TextView) row.findViewById(R.id.no);
									TextView activityDate = (TextView) row.findViewById(R.id.activityDate);
									TextView action = (TextView) row.findViewById(R.id.action);
									TextView collectorName = (TextView) row.findViewById(R.id.collectorName);

									row.setTag(item);

									no.setText(String.valueOf(index++));
									activityDate.setText(Formatter.formatDate(item.getActivity_date(), Global.DATE_STR_FORMAT));
									action.setText(item.getActivity());
									collectorName.setText(item.getCollector_name());

									row.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											itemCollectionActivity = (CollectionActivity) v.getTag();
											Intent intent = new Intent(CollectionActivityFragment.this, CollectionActivityDetailFragment.class);
											startActivity(intent);
										}
									});
									table.addView(row);
								}
								Handler handler = new Handler();
								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										UserHelp.showAllUserHelp(CollectionActivityFragment.this, CollectionActivityFragment.this.getClass().getSimpleName());
									}
								}, SHOW_USERHELP_DELAY_DEFAULT);
							} else {
								NiftyDialogBuilder.getInstance(CollectionActivityFragment.this)
										.withMessage(getString(R.string.no_data_found_offline))
										.withTitle(getString(R.string.no_data_found_offline))
										.isCancelableOnTouchOutside(false)
										.withButton1Text(getString(R.string.btnClose))
										.setButton1Click(new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												NiftyDialogBuilder.getInstance(CollectionActivityFragment.this).dismiss();
											}
										})
										.show();
							}
						}
					}
					else if (collectionActivityResponse.getStatus().getCode() != 0) {
						NiftyDialogBuilder.getInstance(CollectionActivityFragment.this)
								.withMessage(collectionActivityResponse.getStatus().getMessage())
								.withTitle(getString(R.string.server_error))
								.isCancelableOnTouchOutside(false)
								.withButton1Text(getString(R.string.btnClose))
								.setButton1Click(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										finish();
									}
								})
								.show();
						return;
					}
					else {
						collActivityResponse = collectionActivityResponse;
						TextView agreementNumber = (TextView) findViewById(R.id.agreementNumber);
						agreementNumber.setText(collectionActivityResponse.getAgreementNo());

						TableLayout table = (TableLayout) findViewById(R.id.tableHeaders);
						int index = 1;

						for (CollectionActivity item : collectionActivityResponse.getCollectionHistoryList()) {
							View row = LayoutInflater.from(CollectionActivityFragment.this).inflate(R.layout.view_row_collection_activity, table, false);
							TextView no = (TextView) row.findViewById(R.id.no);
							TextView activityDate = (TextView) row.findViewById(R.id.activityDate);
							TextView action = (TextView) row.findViewById(R.id.action);
							TextView collectorName = (TextView) row.findViewById(R.id.collectorName);
							row.setTag(item);

							no.setText(String.valueOf(index++));
							activityDate.setText(Formatter.formatDate(item.getActivity_date(), Global.DATE_STR_FORMAT));
							action.setText(item.getActivity());
							collectorName.setText(item.getCollector_name());

							row.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									itemCollectionActivity = (CollectionActivity) v.getTag();
									Intent intent = new Intent(CollectionActivityFragment.this, CollectionActivityDetailFragment.class);
									startActivity(intent);
								}
							});
							table.addView(row);
						}
						if (collectionActivityResponse.getCollectionHistoryList().isEmpty()) {
							NiftyDialogBuilder.getInstance(CollectionActivityFragment.this)
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
						} else {
							Handler handler = new Handler();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									UserHelp.showAllUserHelp(CollectionActivityFragment.this, CollectionActivityFragment.this.getClass().getSimpleName());
								}
							}, SHOW_USERHELP_DELAY_DEFAULT);
						}
					}
				}
            }
        }.execute();
    }
}
