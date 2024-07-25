package com.adins.mss.base.dynamicform.form;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Keep;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.AppInfo;
import com.adins.mss.base.commons.Helper;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.dynamicform.form.view.DynamicQuestionView;
import com.adins.mss.base.payment.PaxPayment;
import com.adins.mss.base.util.EventBusHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpView;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class DynamicQuestionActivity extends AppCompatActivity implements LocationListener,
        AdapterView.OnItemClickListener, AppInfo {
    public static FragmentManager fragmentManager;
    public static ProgressDialog progressDialog;
    protected static ArrayList<String> questionLabel = new ArrayList<>();
    LocationManager mLocation = null;
    private DynamicQuestionView view;

    private Trace detailTrace;
    private FirebaseAnalytics screenName;

    public static ArrayList<String> getQuestionLabel() {
        return questionLabel;
    }

    public static void setQuestionLabel(ArrayList<String> questionLabel) {
        DynamicQuestionActivity.questionLabel = questionLabel;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Firebase Custom Trace
        detailTrace.start();
        screenName.setCurrentScreen(this, getString(R.string.screen_name_customer_detail),null);
        if (Global.getSharedGlobal().getIsVerifiedByUser()) {
            Global.getSharedGlobal().setIsVerifiedByUser(false);
            this.finish();
        }
        DialogManager.showGPSAlert(DynamicQuestionActivity.this);
        DialogManager.showTimeProviderAlert(this);
        if ((Helper.isDevEnabled(this) && GlobalData.getSharedGlobalData().isDevEnabled()) && !GlobalData.getSharedGlobalData().isByPassDeveloper()) {
            DialogManager.showTurnOffDevMode(this);
        }

        registerReceiver(paymentResultReceiver, new IntentFilter("com.broadcast.BNI_APP"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Firebase Custom Trace
        detailTrace.stop();
        unregisterReceiver(paymentResultReceiver);
    }
    @Keep // subcribe
    public void onEvent(SurveyHeaderBean task) {
        Global.getSharedGlobal().setIsVerifiedByUser(false);
        this.finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Utility.freeMemory();
        Constant.setListOfQuestion(null);
        CustomerFragment.setHeader(null);
        DynamicFormActivity.setListOfIdentifier(null);
        DynamicFormActivity.setIsApproval(false);
        DynamicFormActivity.setIsVerified(false);
        DynamicFormActivity.setAllowImageEdit(true);
        DynamicQuestionView.fragmentManager = null;
        fragmentManager = null;

        if (mLocation != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    mLocation.removeUpdates(this);
                    mLocation = null;
                }
            } else {
                mLocation.removeUpdates(this);
                mLocation = null;
            }
        }
        EventBusHelper.unregisterEventBus(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Firebase custom trace
        detailTrace = FirebasePerformance.getInstance().newTrace(getString(R.string.customer_detail_trace));
        screenName = FirebaseAnalytics.getInstance(this);

        EventBusHelper.registerEventBus(this);

        setContentView(R.layout.activity_dynamic_question);
        fragmentManager = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.fontColorWhite));
        setSupportActionBar(toolbar);

        view = new DynamicQuestionView(this);
        view.fragmentManager = fragmentManager;
        view.onCreate();

        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());

        LinearLayout btnContainer = findViewById(R.id.buttonContainer);
        btnContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!UserHelp.isActive) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            UserHelp.showAllUserHelp(DynamicQuestionActivity.this, DynamicQuestionActivity.this.getClass().getSimpleName());
                        }
                    }, SHOW_USERHELP_DELAY_DEFAULT);
                }
            }
        });
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
                (Global.userHelpGuide.get(DynamicQuestionActivity.class.getSimpleName())!=null) ||
                Global.userHelpDummyGuide.get(DynamicQuestionActivity.class.getSimpleName()) != null){
            menu.findItem(R.id.mnGuide).setVisible(true);
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
        if(item.getItemId() == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
            LinearLayout btnContainer = findViewById(R.id.buttonContainer);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UserHelp.showAllUserHelp(DynamicQuestionActivity.this, DynamicQuestionActivity.this.getClass().getSimpleName());
                }
            }, SHOW_USERHELP_DELAY_DEFAULT);
            btnContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!UserHelp.isActive)
                        UserHelp.showAllUserHelp(DynamicQuestionActivity.this, DynamicQuestionActivity.this.getClass().getSimpleName());
                }
            });

            String activityName = this.getClass().getSimpleName();
            ObscuredSharedPreferences sharedPref =
                    ObscuredSharedPreferences.getPrefs(getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
            String tooltipString = sharedPref.getString("TOOLTIP", null);
            Gson gson = new Gson();

            Map<String, ArrayList<UserHelpView>> tempTooltip;
            java.lang.reflect.Type type = new TypeToken<Map<String, ArrayList<UserHelpView>>>() {
            }.getType();
            tempTooltip = gson.fromJson(tooltipString, type);

            Global.userHelpGuide.put(activityName, tempTooltip.get(activityName));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //
    }

    @Override
    public void onBackPressed() {
        if(!Global.BACKPRESS_RESTRICTION) {
            if (view.mode == Global.MODE_VIEW_SENT_SURVEY) {
                super.onBackPressed();
                GlobalData.getSharedGlobalData().setDoingTask(false);
            } else {
                DialogManager.showExitAlertQuestion(this, getString(R.string.alertExitSurvey));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Global.REQUEST_DRAWING_QUESTION) {
                Bundle bundle = data.getExtras();
                bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.RESULT_FROM_DRAWING_QUESTION);
                Message message = new Message();
                message.setData(bundle);
                FragmentQuestion.questionHandler.sendMessage(message);
            } else if (requestCode == Utils.REQUEST_IN_APP_CAMERA) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.RESULT_FROM_BUILT_IN_CAMERA);
                    Message message = new Message();
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
                }
            } else if (requestCode == Utils.REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.RESULT_FROM_ANDROID_CAMERA);
                    Message message = new Message();
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
                }
            } else if (requestCode == Global.REQUEST_LOCATIONTAGGING) {
                Bundle bundle = new Bundle();
                bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.RESULT_FROM_LOCATION_QUESTION);
                Message message = new Message();
                message.setData(bundle);
                FragmentQuestion.questionHandler.sendMessage(message);
            } else if (requestCode == Global.REQUEST_VOICE_NOTES) {
                byte[] voiceNotes = data.getByteArrayExtra(Global.BUND_KEY_DETAIL_DATA);
                if (voiceNotes != null && voiceNotes.length > 0) {
                    view.header.setVoice_note(voiceNotes);
                }
            } else if (requestCode == Global.REQUEST_EDIT_IMAGE) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.RESULT_FROM_EDIT_IMAGE);
                    Message message = new Message();
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
                }
            } else if (requestCode == Global.REQUEST_LOOKUP_ANSWER) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.RESULT_FROM_LOOKUP_CRITERIA);
                    Message message = new Message();
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utility.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (Utility.checkPermissionResult(this, permissions, grantResults))
                    bindLocationListener();
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void bindLocationListener() {
        mLocation = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Utility.checkPermissionGranted(this);
                    return;
                } else {
                    if (mLocation.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                        mLocation.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                }
            } else {
                if (mLocation.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                    mLocation.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
            }
        } catch (Exception e) {
            FireCrash.log(e);
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
    public void onLocationChanged(Location location) {
        if (location != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && location.isFromMockProvider()) {
            DialogManager.showMockDialog(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //EMPTY
    }

    @Override
    public void onProviderEnabled(String provider) {
        DialogManager.closeGPSAlert();
    }

    @Override
    public void onProviderDisabled(String provider) {
        DialogManager.showGPSAlert(DynamicQuestionActivity.this);
    }

    @Override
    public void checkAppVersion(NewMainActivity activity) {
        //EMPTY
    }

    private BroadcastReceiver paymentResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            assert action != null;
            if (action.equalsIgnoreCase(PaxPayment.ACTION_SUCCESS_PAYMENT)) {
                String result   = intent.getStringExtra("Result");
                if (result.toUpperCase().contains("SALE")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.SUBMIT_FORM);
                    Message message = new Message();
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
                }
            }
        }
    };
}
