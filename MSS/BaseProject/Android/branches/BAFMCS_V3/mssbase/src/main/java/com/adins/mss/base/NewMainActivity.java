package com.adins.mss.base;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.adins.mss.base.scheduler.ClearPdfBroadcastReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.about.activity.AboutInfoTab;
import com.adins.mss.base.authentication.Authentication;
import com.adins.mss.base.authentication.AuthenticationTask;
import com.adins.mss.base.checkin.activity.CheckInActivity;
import com.adins.mss.base.commons.Helper;
import com.adins.mss.base.commons.SubmitResult;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.dynamictheme.ThemeUtility;
import com.adins.mss.base.mainmenu.NewMenuItem;
import com.adins.mss.base.mainmenu.UpdateMenuGPS;
import com.adins.mss.base.mainmenu.adapter.NewMainMenuAdapter;
import com.adins.mss.base.mainmenu.fragment.NewMenuFragment;
import com.adins.mss.base.mainmenu.settings.UpdateMenuSetting;
import com.adins.mss.base.tasklog.LogResultActivity;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.TaskListTask;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.UserSession;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.LogoPrint;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TimelineType;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.config.ConfigFileReader;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.LogoPrintDataAccess;
import com.adins.mss.foundation.db.dataaccess.MenuDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.AuditDataType;
import com.adins.mss.foundation.http.AuditDataTypeGenerator;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.adins.mss.foundation.notification.Notification;
import com.adins.mss.foundation.oauth2.Token;
import com.adins.mss.foundation.oauth2.store.SharedPreferencesTokenStore;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.adins.mss.foundation.sync.BackgroundServiceSynchronize;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.services.JsonResponseRetrieveTaskList;
import com.services.MainServices;
import com.services.MssJobScheduler;
import com.services.ServiceAutoRestart;
import com.services.plantask.ConnectivityChangeReceiver;

import net.sqlcipher.BuildConfig;

import org.acra.ACRA;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import javax.net.ssl.HttpsURLConnection;

import static android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
import static com.adins.mss.foundation.location.LocationTrackingManager.FAST_INTERVAL_CEILING_IN_MILLISECONDS;
import static com.google.android.gms.common.api.CommonStatusCodes.RESOLUTION_REQUIRED;

public abstract class NewMainActivity extends MssFragmentActivity implements ThemeLoader.ColorSetLoaderCallback {

    public static final String TAG = "ROOT_FRAGMENT";
    public static final String KEY_ACTION = "action";
    public static final int ACTION_MENU = 100;
    public static final int ACTION_TASKLIST = 101;
    public static final int ACTION_TIMELINE = 102;
    private static final String SYNCHRONIZATION_PREFERENCE = "com.adins.mss.base.SynchronizationPreference";
    private static final int REQUEST_CODE_IGNORE_BATTERY_OPTIMIZATION = 98;
    public static Intent AutoSendLocationHistoryService;
    public static Intent RunNotificationService;
    public static Intent StartClearPdfSchedulerService;
    public static MainServices mainServices;
    public static boolean Force_Uninstall = false;
    public static List<NewMenuItem> menuItems;
    public static NewMenuItem mnTaskList;
    public static NewMenuItem mnSurveyAssign;
    public static NewMenuItem mnSurveyVerif;
    public static NewMenuItem mnSurveyApproval;
    public static NewMenuItem mnVerifByBranch;
    public static NewMenuItem mnApprovalByBranch;
    public static MenuHandler menuHandler;
    public static FragmentManager fragmentManager;
    public static int tempPosition = 1;
    public static Class mss;
    public static SubmitHandler submitHandler;
    public static Class mainMenuClass;
    public static Fragment verificationFragment;
    public static Fragment approvalFragment;
    public static Fragment verificationFragmentByBranch;
    public static Fragment approvalFragmentByBranch;
    private static NewMainActivity mainActivity;
    private static NewMenuFragment menuFragment;
    private static Menu mainMenu;
    public boolean isMainMenuOpen = false;
    public NewMenuItem menuItem;
    public LocationTrackingManager manager;
    public BackgroundServiceSynchronize backgroundSync;
    protected FragmentTransaction transaction;
    protected Fragment fragment;
    public static boolean ismnGuideEnabled;
    private FirebaseAnalytics menuCountAnalytics;
    public NewMainMenuAdapter.OnItemClickListener itemClickListener = new NewMainMenuAdapter.OnItemClickListener() {
        @Override
        public NewMenuItem OnItemClick(NewMenuItem menuItem, int position) {
            if (Global.IS_DEV) System.out.println(menuItem.getName());
            setMenuItem(menuItem);
            ApplicationMenu(menuItem);
            return super.OnItemClick(menuItem, position);
        }
    };
    Handler batteryHandler;
    Runnable checkBatteryOptimization;
    protected TextView navCounter;
    protected BottomNavigationView navigation;
    final int REQUEST_LOCATION_MODE_CHECK_SETTINGS = 0x2;
    protected ConnectivityChangeReceiver nCallback;
    protected BroadcastReceiver forceLogoutHandler;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
            if(Global.BACKPRESS_RESTRICTION) return false;

            int i = item.getItemId();
            if (i == R.id.timelineNav) {
                gotoTimeline();
                return true;
            } else if (i == R.id.menuNav) {
                gotoMainMenu();
                return true;
            } else if (i == R.id.taskListNav) {
                if (mnTaskList == null) {
                    getMenu();
                    if (mnTaskList == null) {
                        Toast.makeText(getApplicationContext(), R.string.menuUnavailable, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    gotoTasklist();
                }
                return true;
            }
            return false;
        }
    };
    private Toolbar toolbar;

    public NewMainActivity() {
    }

    public static Class getMss() {
        return mss;
    }

    public static void setMss(Class mss) {
        NewMainActivity.mss = mss;
    }

    public static Class getMainMenuClass() {
        return mainMenuClass;
    }

    @Keep
    public static void setMainMenuClass(Class mainMenuClass) {
        NewMainActivity.mainMenuClass = mainMenuClass;
    }

    public static void updateMenuIcon(boolean isGPS) {
        UpdateMenuIcon uItem = new UpdateMenuIcon();
        uItem.updateGPSIcon(mainMenu);
    }

    public static void updateMenuSettings() {
        UpdateMenuSetting menu = new UpdateMenuSetting(mainActivity.getApplicationContext());
        menu.updateFlagIcon(mainMenu);
    }

    public static Fragment getVerificationFragment() {
        return NewMainActivity.verificationFragment;
    }

    public static void setVerificationFragment(Fragment value) {
        NewMainActivity.verificationFragment = value;
    }

    public static Fragment getApprovalFragment() {
        return NewMainActivity.approvalFragment;
    }

    public static void setApprovalFragment(Fragment value) {
        NewMainActivity.approvalFragment = value;
    }

    public static Fragment getVerificationFragmentByBranch() {
        return NewMainActivity.verificationFragmentByBranch;
    }

    public static void setVerificationFragmentByBranch(Fragment value) {
        NewMainActivity.verificationFragmentByBranch = value;
    }

    public static Fragment getApprovalFragmentByBranch() {
        return NewMainActivity.approvalFragmentByBranch;
    }

    public static void setApprovalFragmentByBranch(Fragment value) {
        NewMainActivity.approvalFragmentByBranch = value;
    }

    @Keep
    public static void InitializeGlobalDataIfError(Context activity) {
        try {
            switch (activity.getResources().getDisplayMetrics().densityDpi) {
                case DisplayMetrics.DENSITY_LOW:
                    Global.THUMBNAIL_WIDTH = 120;
                    Global.THUMBNAIL_HEIGHT = 160;
                    Global.TRIANGLE_SIZE = 18;
                    break;
                case DisplayMetrics.DENSITY_MEDIUM:
                    Global.THUMBNAIL_WIDTH = 140;
                    Global.THUMBNAIL_HEIGHT = 180;
                    Global.TRIANGLE_SIZE = 22;
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                    Global.THUMBNAIL_WIDTH = 160;
                    Global.THUMBNAIL_HEIGHT = 200;
                    Global.TRIANGLE_SIZE = 26;
                    break;
                case DisplayMetrics.DENSITY_XHIGH:
                    Global.THUMBNAIL_WIDTH = 180;
                    Global.THUMBNAIL_HEIGHT = 220;
                    Global.TRIANGLE_SIZE = 28;
                    break;
                case DisplayMetrics.DENSITY_XXHIGH:
                    Global.THUMBNAIL_WIDTH = 200;
                    Global.THUMBNAIL_HEIGHT = 240;
                    Global.TRIANGLE_SIZE = 32;
                    break;
                case DisplayMetrics.DENSITY_560:
                    Global.THUMBNAIL_WIDTH = 240;
                    Global.THUMBNAIL_HEIGHT = 280;
                    Global.TRIANGLE_SIZE = 36;
                    break;
                case DisplayMetrics.DENSITY_XXXHIGH:
                    Global.THUMBNAIL_WIDTH = 260;
                    Global.THUMBNAIL_HEIGHT = 300;
                    Global.TRIANGLE_SIZE = 36;
                    break;
                default:
                    break;
            }
            if (GlobalData.getSharedGlobalData().getUser() == null) {
                ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(activity,
                        "GlobalData", Context.MODE_PRIVATE);
                String uuidUser = sharedPref.getString("UUID_USER", "");
                String urlHeader = sharedPref.getString("URL_HEADER", "");
                if (uuidUser != null && uuidUser.length() > 0) {
                    Properties prop = ConfigFileReader.propertiesFromFile(activity, GlobalData.PROPERTY_FILENAME);
                    boolean encrypt = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_ENCRYPT, "false"));
                    boolean decrypt = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_DECRYPT, "false"));
                    boolean accessTokenEnable = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_IS_REQUIRED_ACCESS_TOKEN, "false"));
                    String propClientId = prop.getProperty(GlobalData.PROP_CLIENT_ID, "android");

                    boolean hasEncrypt = sharedPref.getBoolean("IS_ENCRYPT", encrypt);
                    boolean hasDecrypt = sharedPref.getBoolean("IS_DECRYPT", decrypt);
                    boolean isTokenEnable = sharedPref.getBoolean("IS_ACCESS_TOKEN_ENABLE", accessTokenEnable);
                    String clientId = sharedPref.getString("CLIENT_ID", propClientId);

                    User tempUser = UserDataAccess.getOne(activity, uuidUser);
                    GlobalData.getSharedGlobalData();
                    GlobalData.getSharedGlobalData().loadFromProperties(activity);
                    GlobalData.getSharedGlobalData().setUser(tempUser);
                    GlobalData.getSharedGlobalData().setApplication(prop.getProperty(GlobalData.PROP_APPLICATION_NAME, ""));
                    String language = LocaleHelper.getLanguage(activity);
                    GlobalData.getSharedGlobalData().setLocale(language);
                    AuditDataType tempAudit = AuditDataTypeGenerator.generateActiveUserAuditData();

                    GlobalData.getSharedGlobalData().setAuditData(tempAudit);
                    GlobalData.getSharedGlobalData().setUrlMain(urlHeader);
                    GlobalData.getSharedGlobalData().reloadUrl(activity);
                    Map<String, String> listImei = AuditDataTypeGenerator.getListImeiFromDevice(activity);
                    GlobalData.getSharedGlobalData().setImei(listImei.get(MssRequestType.UN_KEY_IMEI));
                    if (listImei.get(MssRequestType.UN_KEY_IMEI2) != null) {
                        GlobalData.getSharedGlobalData().setImei2(listImei.get(MssRequestType.UN_KEY_IMEI2));
                    }
                    GlobalData.getSharedGlobalData().setEncrypt(hasDecrypt);
                    GlobalData.getSharedGlobalData().setDecrypt(hasEncrypt);
                    GlobalData.getSharedGlobalData().setRequiresAccessToken(isTokenEnable);

                    if (GlobalData.getSharedGlobalData().isRequiresAccessToken()) {
                        GlobalData.getSharedGlobalData().setClientId(clientId);
                        GlobalData.getSharedGlobalData().setoAuth2Client(tempUser);
                        SharedPreferencesTokenStore tokenStore = new SharedPreferencesTokenStore(activity);
                        Token token = null;
                        try {
                            token = tokenStore.load(GlobalData.getSharedGlobalData().getoAuth2Client().getUsername());
                            GlobalData.getSharedGlobalData().setToken(token);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    List<GeneralParameter> gp = GeneralParameterDataAccess.getAll(activity, uuidUser);
                    GlobalData.getSharedGlobalData().loadGeneralParameters(gp);
                    GlobalData.getSharedGlobalData().getAuditData().setCallerId(uuidUser);
                    boolean isDev = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_IS_DEVELOPER, "false"));
                    Global.IS_DEV = sharedPref.getBoolean("IS_DEV", isDev);

                    Global.FEATURE_RESCHEDULE_SURVEY = MenuDataAccess.isHaveRescheduleMenu(activity);
                    Global.FEATURE_REVISIT_COLLECTION = MenuDataAccess.isHaveReVisitMenu(activity); //new
                    if (Global.positionStack == null) {
                        Global.positionStack = new Stack<Integer>();
                    }
                    ACRA.getErrorReporter().putCustomData("UUID_USER", GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    ACRA.getErrorReporter().putCustomData("LOGIN_ID", GlobalData.getSharedGlobalData().getUser().getLogin_id());
                    ACRA.getErrorReporter().putCustomData("JOB_DESCRIPTION", GlobalData.getSharedGlobalData().getUser().getJob_description());
                    ACRA.getErrorReporter().putCustomData("BRANCH_NAME", GlobalData.getSharedGlobalData().getUser().getBranch_name());
                    ACRA.getErrorReporter().putCustomData("TENANT_ID", GlobalData.getSharedGlobalData().getTenant());
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Keep
    public static void setCounter() {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Global.BUND_KEY_REFRESHCOUNTER, true);
        message.setData(bundle);
        try {
            menuHandler.sendMessage(message);
        } catch (NullPointerException e) {
            FireCrash.log(e);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Global.BACKPRESS_RESTRICTION = false;
        super.onCreate(savedInstanceState);

        menuCountAnalytics = FirebaseAnalytics.getInstance(this);
        String language = LocaleHelper.getLanguage(this);//get previous language if exist
        if(language != null && !language.equals(""))
            LocaleHelper.onCreate(getApplicationContext(), language);
        else
            LocaleHelper.onCreate(getApplicationContext(), Locale.getDefault().getLanguage());

        mainActivity = this;
        setContentView(R.layout.new_main_activity);

        getMenu();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.fontColorWhite));
        setSupportActionBar(toolbar);

        navigation = (BottomNavigationView) findViewById(R.id.bottomNav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // Nendi: 2019.06.20 - Init notification channel
        initNotificationChannel();

        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        StoreGlobalDataTemporary();
        startBackgroundProcess();
        checkAppVersion(this);

        fragmentManager = getSupportFragmentManager();
        submitHandler = new SubmitHandler();
        menuHandler = new MenuHandler();

        navCounter = (TextView) findViewById(R.id.counter);
        setCounter();

        try {
            loadColorSet();
        } catch (Exception e) {
            FireCrash.log(e);
        }
        AutoSendLocationHistoryService = getLocationServiceIntent();
        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
        if (!Global.APPLICATION_ORDER.equals(application)) {
            checkFlagTracking();
        }

        batteryHandler = new Handler();
        checkBatteryOptimization = new Runnable() {
            public void run() {
            try {
                batteryHandler.removeCallbacks(checkBatteryOptimization);
                if (!isBatteryOptimizationIgnored()) {
                    disableBatteryOptimization();
                }
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
            }
            }
        };
        disableBatteryOptimization();


        /**
         * Nendi - 2019.06.20
         * Request autostart permission on chinese devices
         */
        Helper.requestAutostartService(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MssJobScheduler.startAutoRestartService(getBaseContext());
        } else {
            ServiceAutoRestart.startAutoRestartService(getBaseContext());
        }

        if (GlobalData.getSharedGlobalData().getApplication() == null) {
            GlobalData.getSharedGlobalData().loadFromProperties(this);
        }

        if (GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_COLLECTION)) {
            new LoadLogoPrint().execute();
        }

        registerReceiver(locationListener, new IntentFilter(getString(R.string.action_location_listener)));
        registerReceiver(mockLocationListener, new IntentFilter(getString(R.string.action_turn_off_mocklocation)));
        registerNetworkCallbacks();
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
        menuInflater.inflate(R.menu.main_menu, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenuIcon(Global.isGPS);
        updateMenuSettings();

        String currentFragmentName = getSupportFragmentManager()
                .findFragmentById(R.id.content_frame).getClass().getSimpleName();

        if(Global.ENABLE_USER_HELP && (Global.userHelpGuide.get(currentFragmentName) != null ||
                Global.userHelpDummyGuide.get(currentFragmentName) != null)) {
            menu.findItem(R.id.mnGuide).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mnGPS && Global.LTM != null) {
            if (Global.LTM.getIsConnected()) {
                Global.LTM.removeLocationListener();
                Global.LTM.connectLocationClient();
            } else {
                StartLocationTracking();
            }
            Animation a = AnimationUtils.loadAnimation(this, R.anim.gps_rotate);
            findViewById(R.id.mnGPS).startAnimation(a);
        }
        if(item.getItemId() == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
            UserHelp.reloadUserHelp(getApplicationContext(), fragmentManager);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isMainMenuOpen = false;
        unregisterForceLogoutHandler();
    }

    @Override
    public void onResume() {
        super.onResume();
        isMainMenuOpen = true;
        Log.d("newmainactivity","ismainmenuopen "+isMainMenuOpen);
        setCounter();
        navCounter.setText(String.valueOf(ToDoList.getCounterTaskList(NewMainActivity.this)));
        Utility.checkPermissionGranted(this);

        ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(this, Authentication.LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        String restoredText = prefs.getString(Authentication.LOGIN_PREFERENCES_APPLICATION_CLEANSING, null);
        if (restoredText != null && restoredText.equalsIgnoreCase("uninstall")) {
            if(NewMainActivity.Force_Uninstall == true){
                                UninstallerHandler();
                	            }else { }
             	            return;
        }
        if (GlobalData.getSharedGlobalData().getUser() == null) {
            PackageInfo pInfo;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                Global.APP_VERSION = pInfo.versionName;
                Global.BUILD_VERSION = pInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            InitializeGlobalDataIfError(getApplicationContext());
        }

        DialogManager.showGPSAlert(getBaseContext());
        DialogManager.showTimeProviderAlert(this);

        if (Helper.isDevEnabled(this) && GlobalData.getSharedGlobalData().isDevEnabled() && !GlobalData.getSharedGlobalData().isByPassDeveloper()) {
            DialogManager.showTurnOffDevMode(this);
        }

        GlobalData.setRequireRelogin(false);
        if (UserSession.isInvalidToken()) {
            DialogManager.showForceExitAlert(this, getString(R.string.failed_refresh_token));
        }

        checkGPSMode();

        String currentFragmentName = getSupportFragmentManager()
                .findFragmentById(R.id.content_frame).getClass().getSimpleName();

        if(Global.ENABLE_USER_HELP && (Global.userHelpGuide.get(currentFragmentName)!=null ||
                Global.userHelpDummyGuide.get(currentFragmentName) != null)) {
            ismnGuideEnabled = true;
        } else{
            ismnGuideEnabled = false;
        }
        registerForceLogoutHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkCallbacks();

        Notification.getSharedNotification().clearNotifAll(this);

        mnTaskList = null;

        if(mLocation != null) {
            mLocation = null;
        }

        unregisterReceiver(locationListener);
        unregisterReceiver(mockLocationListener);
    }

    public void registerNetworkCallbacks(){
        //set ncallback instance in child class, call this method after child code
        if(nCallback == null)
            return;

        ConnectivityManager conn = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && conn != null) {
            conn.registerDefaultNetworkCallback(nCallback);
        }
    }

    public void unregisterNetworkCallbacks(){
        ConnectivityManager conn = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && (conn != null && nCallback != null)) {
            conn.unregisterNetworkCallback(nCallback);
        }
    }

    public void registerForceLogoutHandler(){
        forceLogoutHandler = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DialogManager.showForceExitAlert(NewMainActivity.this,getString(R.string.msgLogout));
            }
        };

        IntentFilter intentFilter = new IntentFilter(Global.FORCE_LOGOUT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(forceLogoutHandler,intentFilter);
    }

    public void unregisterForceLogoutHandler(){
        if(forceLogoutHandler == null)
            return;

        LocalBroadcastManager.getInstance(this).unregisterReceiver(forceLogoutHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Utility.freeMemory();
        if (NewMainActivity.Force_Uninstall) {
            UninstallerHandler();
        }
    }

    public ArrayList<String> getServerMenuTitle() {
        ArrayList<String> serverMenuTitle = new ArrayList<>();
            try {
            List<com.adins.mss.dao.Menu> menu = MenuDataAccess.getAll(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
            for (com.adins.mss.dao.Menu menu2 : menu) {
                serverMenuTitle.add(menu2.getUuid_menu());
            }
        } catch (Exception e) {
            FireCrash.log(e);

        }
        return serverMenuTitle;
    }

    public void getMenu() {
        List<String> menuTitle = getServerMenuTitle();
        for (int i = 0; i < menuTitle.size(); i++) {
            if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.mn_tasklist)))
                mnTaskList = new NewMenuItem(getString(R.string.title_mn_tasklist), R.drawable.menu_tasklist, "0");
            if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.mn_surveyassign)))
                mnSurveyAssign = new NewMenuItem(getString(R.string.title_mn_surveyassign), R.drawable.menu_surveyassignment, "0");
            if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.mn_surveyverification)))
                mnSurveyVerif = new NewMenuItem(getString(R.string.title_mn_surveyverification), R.drawable.menu_surveyverification, "0");
            if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.mn_surveyapproval)))
                mnSurveyApproval = new NewMenuItem(getString(R.string.title_mn_surveyapproval), R.drawable.menu_surveyapproval, "0");
            if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.mn_verification_bybranch)))
                mnVerifByBranch = new NewMenuItem(getString(R.string.title_mn_verification_bybranch), R.drawable.menu_surveyverification, "0");
            if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.mn_approval_bybranch)))
                mnApprovalByBranch = new NewMenuItem(getString(R.string.title_mn_approval_bybranch), R.drawable.menu_surveyapproval, "0");
        }
    }

    public NewMenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(NewMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    protected void ApplicationMenu(NewMenuItem menuItem) {
        if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_log))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_log));
            gotoLog();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_absentin))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_absentin));
            gotoCheckIn();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_exit))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_exit));
            gotoExit();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_synchronize))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_synchronize));
            gotoSynchronize();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_tasklist))) {
            if(Global.PLAN_TASK_ENABLED)
                logEventCountMenuAccessed(getString(R.string.mn_plantask));
            else
                logEventCountMenuAccessed(getString(R.string.title_mn_tasklist));
            gotoTasklist();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_about))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_about));
            gotoAbout();
        }
    }

    protected void gotoMainMenu() {
        fragment = NewMenuFragment.newInstance(itemClickListener);
        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(TAG);
        transaction.commit();
    }

    protected void gotoCheckIn() {
        fragment = new CheckInActivity();

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, 0, R.anim.slide_up, 0);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void gotoTasklist() {
        TaskListTask task = new TaskListTask(this, this.getString(R.string.progressWait),
                this.getString(R.string.msgNoTaskList), R.id.content_frame);
        task.execute();
    }

    public void gotoTimeline() {
        fragment = new NewTimelineFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoLog() {
        fragment = new LogResultActivity();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, 0, R.anim.slide_up, 0);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void gotoSynchronize() {
        new AuthenticationTask(NewMainActivity.this);
    }

    protected void gotoAbout() {
        fragment = new AboutInfoTab();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, 0, R.anim.slide_up, 0);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoExit() {
        tempPosition = 0;
        DialogManager.showExitAlert(this, getString(R.string.alertExit));
    }

    public void doUnsubscribe() {
        try {
            User user = GlobalData.getSharedGlobalData().getUser();
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            String buildConfig = BuildConfig.FLAVOR.toUpperCase();
            if (buildConfig.length() == 0) {
                buildConfig = Global.FLAVORS;
            }
            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("ALL-MC-" + buildConfig);
                if (null != user.getUuid_branch()) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUuid_branch() + "-" + buildConfig);
                }
                if (null != user.getUuid_group()) {
                    String[] listGroup = user.getUuid_group().split(";");
                    for (int i = 0; i < listGroup.length; i++) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(listGroup[i] + "-" + buildConfig);
                    }
                }
            } else if (Global.APPLICATION_ORDER.equalsIgnoreCase(application)) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("ALL-MO-" + buildConfig);
                if (null != user.getUuid_group()) {
                    String[] listGroup = user.getUuid_group().split(";");
                    for (int i = 0; i < listGroup.length; i++) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(listGroup[i] + "-" + buildConfig);
                    }
                }
                if (null != user.getUuid_branch()) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUuid_branch() + "-" + buildConfig);
                }
                if (null != user.getUuid_dealer()) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUuid_dealer() + "-" + buildConfig);
                }
            } else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("ALL-MS-" + buildConfig);
                if (null != user.getUuid_branch()) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUuid_branch() + "-" + buildConfig);
                }
                if (null != user.getUuid_group()) {
                    String[] listGroup = user.getUuid_group().split(";");
                    for (int i = 0; i < listGroup.length; i++) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(listGroup[i] + "-" + buildConfig);
                    }
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorToSubcribe", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorToSubcribe", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Subcribe Topic"));
        }
    }

    protected abstract Intent getIntentSynchronize();

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                if (Utility.checkPermissionResult(NewMainActivity.this, permissions, grantResults))
                    bindLocationListener();
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void StoreGlobalDataTemporary() {
        try {
            String url_header = GlobalData.getSharedGlobalData().getUrlMain();
            ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getApplicationContext(),
                    "GlobalData", Context.MODE_PRIVATE);

            if (GlobalData.getSharedGlobalData().getUser().getUuid_user() != null) {
                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                sharedPrefEditor.putString("UUID_USER", GlobalData.getSharedGlobalData().getUser().getUuid_user());
                sharedPrefEditor.putBoolean("HAS_LOGGED", true);
                sharedPrefEditor.putString("URL_HEADER", url_header);
                sharedPrefEditor.putString("TENANT_ID", GlobalData.getSharedGlobalData().getTenant());
                sharedPrefEditor.putBoolean("IS_ENCRYPT", GlobalData.getSharedGlobalData().isEncrypt());
                sharedPrefEditor.putBoolean("IS_DECRYPT", GlobalData.getSharedGlobalData().isDecrypt());

                sharedPrefEditor.commit();
            }
        } catch (Exception e) {
            FireCrash.log(e);

        }
    }

    protected void startBackgroundProcess() {
        try {
            startSynchronize();
            ToDoList toDoList = new ToDoList(getApplicationContext());
            List<TaskH> listTaskH = toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, null);
            ToDoList.setListOfSurveyStatus(new ArrayList<SurveyHeaderBean>());
            for (TaskH taskH : listTaskH) {
                ToDoList.getListOfSurveyStatus().add(new SurveyHeaderBean(taskH));
            }

            String usr_crt = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            Date date = Tool.getSystemDateTime();

            TimelineType timelineTypeC = new TimelineType(Tool.getUUID(), "Timeline Type for Check In", Global.TIMELINE_TYPE_CHECKIN, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeC);
            TimelineType timelineTypeS = new TimelineType(Tool.getUUID(), "Timeline Type for Submitted Task", Global.TIMELINE_TYPE_SUBMITTED, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeS);
            TimelineType timelineTypeM = new TimelineType(Tool.getUUID(), "Timeline Type for Message", Global.TIMELINE_TYPE_MESSAGE, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeM);
            TimelineType timelineTypeT = new TimelineType(Tool.getUUID(), "Timeline Type for Server Task", Global.TIMELINE_TYPE_TASK, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeT);
            TimelineType timelineTypeV = new TimelineType(Tool.getUUID(), "Timeline Type for Verified Task", Global.TIMELINE_TYPE_VERIFIED, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeV);
            TimelineType timelineTypeA = new TimelineType(Tool.getUUID(), "Timeline Type for Approved Task", Global.TIMELINE_TYPE_APPROVED, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeA);
            TimelineType timelineTypeVer = new TimelineType(Tool.getUUID(), "Timeline Type for Verification Task", Global.TIMELINE_TYPE_VERIFICATION, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeVer);
            TimelineType timelineTypeApp = new TimelineType(Tool.getUUID(), "Timeline Type for Approval Task", Global.TIMELINE_TYPE_APPROVAL, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeApp);
            TimelineType timelineTypeR = new TimelineType(Tool.getUUID(), "Timeline Type for Rejected Task", Global.TIMELINE_TYPE_REJECTED, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeR);
            TimelineType timelineTypeP = new TimelineType(Tool.getUUID(), "Timeline Type for Pending Task", Global.TIMELINE_TYPE_PENDING, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeP);
            TimelineType timelineTypeU = new TimelineType(Tool.getUUID(), "Timeline Type for Uploading Task", Global.TIMELINE_TYPE_UPLOADING, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeU);
            TimelineType timelineTypePush = new TimelineType(Tool.getUUID(), "Timeline Type for Push Notification", Global.TIMELINE_TYPE_PUSH_NOTIFICATION, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypePush);
            TimelineType timelineTypeFailedSentTask = new TimelineType(Tool.getUUID(), "Timeline Type for Failed Sent Task", Global.TIMELINE_TYPE_FAILED_SENT_TASK, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeFailedSentTask);
            TimelineType timelineTypeD = new TimelineType(Tool.getUUID(), "Timeline Type for SaveDraft Task", Global.TIMELINE_TYPE_SAVEDRAFT, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeD);
            TimelineType timelineTypeFD = new TimelineType(Tool.getUUID(), "Timeline Type for FailedDraft task", Global.TIMELINE_TYPE_FAILEDDRAFT, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeFD);
            TimelineType timelineTypeChanged = new TimelineType(Tool.getUUID(), "Timeline Type for Changed Task", Global.TIMELINE_TYPE_CHANGED, usr_crt, date, null, null);
            TimelineTypeDataAccess.add(getApplicationContext(), timelineTypeChanged);

            Backup backup = new Backup(this);
            backup.performRestore();

            if (Global.IS_DEV)
            System.out.println("Before start autosendlocationhistory");

            if (Global.IS_DEV)
                System.out.println("Start autosendlocationhistory");

            mainServices = new MainServices();
            MainServices.mainClass = mss;
            RunNotificationService = new Intent(this, MainServices.class);
            startService(RunNotificationService);

            Utils.setCameraParameter(getApplicationContext());

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    List<Scheme> schemes = SchemeDataAccess.getAll(getApplicationContext());
                    if (Global.getSharedGlobal().getTempSchemeVersion()!= null)
                        Global.getSharedGlobal().setTempSchemeVersion(null);
                    Global.getSharedGlobal().setTempSchemeVersion(new HashMap<String, Integer>());

                    for (Scheme scheme : schemes) {
                        Global.getSharedGlobal().getTempSchemeVersion().put(scheme.getUuid_scheme(), Integer.valueOf(scheme.getForm_version()));
                    }

                    return null;
                }
            }.execute();

            StartClearPdfSchedulerService = new Intent(this, ClearPdfBroadcastReceiver.class);
            scheduleClearPdf();
        } catch (Exception e) {
            FireCrash.log(e);
        }

    }

    public void StartLocationTracking() {
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            GeneralParameter gp_distance = GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_DISTANCE_TRACKING);
            try {
                if (gp_distance != null) {
                    int distanceTracking = Integer.parseInt(gp_distance.getGs_value());
                    if (distanceTracking != 0) {
                        manager = new LocationTrackingManager(tm, mLocation, this);
                        manager.setMinimalDistanceChangeLocation(Integer.parseInt(GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), "PRM13_DIST").getGs_value()));
                        manager.setMinimalTimeChangeLocation(5);
                        manager.applyLocationListener(getApplicationContext());
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                manager = new LocationTrackingManager(tm, mLocation, this);
                manager.setMinimalDistanceChangeLocation(50);
                manager.setMinimalTimeChangeLocation(5);
                manager.applyLocationListener(getApplicationContext());
            }

            if (Global.LTM == null) {
                Global.LTM = manager;
            } else {
                try {
                    Global.LTM = null;
                    Global.LTM = manager;
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    private void startSynchronize() {
        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
        String day = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
        String month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String todayDate = day + month + year;

        ObscuredSharedPreferences synchronizationPreference = ObscuredSharedPreferences.getPrefs(this, SYNCHRONIZATION_PREFERENCE, Context.MODE_PRIVATE);

        if (Global.APPLICATION_ORDER.equalsIgnoreCase(application)) {
            Global.LAST_SYNC = synchronizationPreference.getString("MOSyncDate", "");
        } else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
            Global.LAST_SYNC = synchronizationPreference.getString("MSSyncDate", "");
        } else if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
            Global.LAST_SYNC = synchronizationPreference.getString("MCSyncDate", "");
        }

        if (!Global.LAST_SYNC.equals(todayDate) && Tool.isInternetconnected(this)) {
            try {
                backgroundSync = new BackgroundServiceSynchronize(this);
                backgroundSync.authentication(this);
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }

    @Override
    public void checkAppVersion(NewMainActivity activity) {
        if (UserSession.getAppVersion() == 0) { // simpan version code untuk pertama kali
            UserSession.setAppVersion(AppContext.getInstance().getVersionCode());
        }

        if (UserSession.getAppVersion() < AppContext.getInstance().getVersionCode()) {
            DialogManager.showForceSyncronize(this);
        }
    }

    protected void scheduleClearPdf(){
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, ClearPdfBroadcastReceiver.REQUEST_CODE,
                StartClearPdfSchedulerService, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        long clearTimeInMillis = cal.getTimeInMillis();

        AlarmManager clearPdfAlarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        assert clearPdfAlarm != null;
        clearPdfAlarm.setRepeating(AlarmManager.RTC_WAKEUP, clearTimeInMillis, AlarmManager.INTERVAL_DAY, pIntent);
        if (Global.IS_DEV) {
            Log.i("PDF_SCHEDULER", "Alarm for Clear PDF Scheduler is set!");
        }
    }

    public void UninstallerHandler() {
        String fullname = GlobalData.getSharedGlobalData().getUser().getFullname() != null ? GlobalData.getSharedGlobalData().getUser().getFullname() : "";
        String message = getString(R.string.inactive_user, fullname);
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder.withTitle(getString(R.string.warning_capital))
                .withMessage(message)
                .withButton1Text("Uninstall")
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        dialogBuilder.dismiss();
                        String  packageName = getPackageName();
                        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        intent.setData(Uri.parse("package:" + packageName));
                        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                        startActivityForResult(intent, 1);
                    }
                })
                .isCancelable(false)
                .isCancelableOnTouchOutside(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Logger.d("TAG", "onActivityResult: user accepted the (un)install");
            } else if (resultCode == RESULT_CANCELED) {
                Logger.d("TAG", "onActivityResult: user canceled the (un)install");
                new CheckActiveUser().execute();
            } else if (resultCode == RESULT_FIRST_USER) {
                Logger.d("TAG", "onActivityResult: failed to (un)install");
            }
        } else if (requestCode == 199) {
            finish();
            startActivity(getIntent());
        } else if (requestCode == REQUEST_CODE_IGNORE_BATTERY_OPTIMIZATION && resultCode == 0) {
            disableBatteryOptimization();
        }
    }

    @Override
    public void onBackPressed() {
        if(!Global.BACKPRESS_RESTRICTION){
            try {
                boolean timelineEnabled = navigation.getMenu().findItem(R.id.timelineNav).isEnabled();
                boolean menuEnabled = navigation.getMenu().findItem(R.id.menuNav).isEnabled();
                boolean taskListEnabled = navigation.getMenu().findItem(R.id.taskListNav).isEnabled();
                if (timelineEnabled && menuEnabled && taskListEnabled) {
                    if (NewMainActivity.fragmentManager.getBackStackEntryCount() > 0) {
                        Log.i("MainActivity", "popping backstack");
                        NewMainActivity.tempPosition = 0;
                        if (Global.positionStack.size() > 1)
                            Global.positionStack.pop();
                        NewMainActivity.fragmentManager.popBackStack();
                    } else {
                        NewMainActivity.tempPosition = 0;
                        this.moveTaskToBack(true);
                    }
                } else {
                    NewMainActivity.tempPosition = 0;
                    super.onBackPressed();
                }
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }

    public void checkFlagTracking() {
        try {
            User user = GlobalData.getSharedGlobalData().getUser();
            if (null != user && null != user.getIs_tracking() && user.getIs_tracking().equals("1")) {
                String trackingDays;
                List trackingDaysList;
                int thisDayInt = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                String thisDay;
                thisDayInt -= 1;
                thisDay = String.valueOf(thisDayInt);

                trackingDays = user.getTracking_days();
                if (null != trackingDays) {
                    trackingDaysList = Arrays.asList(trackingDays.split(";"));
                } else {
                    return;
                }

                String hourFromWebStart = user.getStart_time();
                Calendar calStart = Calendar.getInstance();
                if (null != hourFromWebStart) {
                    String hourSplitStart[] = hourFromWebStart.split(":");
                    int hourStart = Integer.parseInt(hourSplitStart[0]);
                    int minuteStart = Integer.parseInt(hourSplitStart[1]);

                    calStart.set(Calendar.HOUR_OF_DAY, hourStart);
                    calStart.set(Calendar.MINUTE, minuteStart);
                } else {
                    return;
                }

                String hourFromWeb = user.getEnd_time();
                Calendar cal = Calendar.getInstance();
                if (null != hourFromWeb) {
                    String hourSplit[] = hourFromWeb.split(":");
                    int hour = Integer.parseInt(hourSplit[0]);
                    int minute = Integer.parseInt(hourSplit[1]);

                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                } else {
                    return;
                }

                if (trackingDaysList.contains(thisDay)) {
                    if (Calendar.getInstance().after(calStart) && Calendar.getInstance().before(cal)) {
                        if (getLocationServiceIntent() != null) {
                            AutoSendLocationHistoryService = getLocationServiceIntent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(getLocationServiceIntent());
                            } else {
                                startService(getLocationServiceIntent());
                            }
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("NotificationThread", e.getMessage());
            ACRA.getErrorReporter().putCustomData("NotificationThread", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat checkFlagTacking"));
        }
    }

    public abstract Intent getLocationServiceIntent();

    public void disableBatteryOptimization() {
        Intent intent = new Intent();
        String packageName = this.getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (!isBatteryOptimizationIgnored()) {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    this.startActivityForResult(intent, REQUEST_CODE_IGNORE_BATTERY_OPTIMIZATION);
                }
            }catch (Exception e){
                DialogManager.showOptimizeDialog(NewMainActivity.this);
            }
        }
    }

    public boolean isBatteryOptimizationIgnored() {
        String packageName = this.getPackageName();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return pm.isIgnoringBatteryOptimizations(packageName);
        }
        return false;
    }

    private void applyColorTheme(DynamicTheme colorSet){
        int statusBarBgColor = Color.parseColor(ThemeUtility.getColorItemValue(colorSet,"bg_solid"));
        ThemeUtility.setStatusBarColor(this,statusBarBgColor);
        ThemeUtility.setToolbarColor(toolbar,statusBarBgColor);
        ThemeUtility.setViewBackground(navCounter,statusBarBgColor);
    }

    private void loadColorSet(){
        ThemeLoader themeLoader = new ThemeLoader(this);
        themeLoader.loadSavedColorSet(this);
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme) {
        if(dynamicTheme != null){
            applyColorTheme(dynamicTheme);
        }
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {

    }

    public class LoadLogoPrint extends AsyncTask<Void, Void, LogoPrint> {

        @Override
        protected LogoPrint doInBackground(Void... voids) {
            GeneralParameter gs = GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_LOGO_PRINTER);

            String uuid = Tool.getUUID();
            String tenant = GlobalData.getSharedGlobalData().getTenant();
            int pos = tenant.indexOf("@");
            String tenantName = tenant.substring(pos+1, tenant.length());

            Bitmap bitmap;
            try {
                URL url = new URL(gs.getGs_value());
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);
                connection.setInstanceFollowRedirects(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);

            } catch (Exception e) {
                FireCrash.log(e);
                bitmap = null;
            }

            if (bitmap != null) {
                byte[] imageByte = Utils.bitmapToByte(bitmap);
                if (imageByte != null) {
                    LogoPrint logo = new LogoPrint(uuid, tenantName, imageByte);
                    LogoPrintDataAccess.addOrReplace(getApplicationContext(), logo);
                }
            }

            return null;
        }
    }

    public class SubmitHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            int action = bundle.getInt(KEY_ACTION);
            SubmitResult result = (SubmitResult) bundle.getSerializable("submitResult");

            switch (action) {
                case ACTION_MENU:
                    DialogManager.submitResult(NewMainActivity.this, result);
                    break;

                case ACTION_TASKLIST:
                    gotoTasklist();
                    break;

                case ACTION_TIMELINE:
                    break;
                default:
                    break;
            }
        }
    }

    public class MenuHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle bundle = msg.getData();

                boolean refreshCounter = bundle.getBoolean(Global.BUND_KEY_REFRESHCOUNTER, false);

                if (refreshCounter) {
                    if (NewMainActivity.mnTaskList != null) {
                        NewMenuFragment.adapter.getMenuItem(NewMainActivity.mnTaskList.getName()).setCounter(String.valueOf(ToDoList.getCounterTaskList(getApplicationContext())));
                        navCounter.setText(String.valueOf(ToDoList.getCounterTaskList(getApplicationContext())));
                    }
                    if (NewMainActivity.mnSurveyAssign != null) {
                        NewMenuFragment.adapter.getMenuItem(NewMainActivity.mnSurveyAssign.getName()).setCounter(String.valueOf(ToDoList.getCounterAssignment(getApplicationContext())));
                    }
                    if (NewMainActivity.mnSurveyVerif != null) {
                        NewMenuFragment.adapter.getMenuItem(NewMainActivity.mnSurveyVerif.getName()).setCounter(String.valueOf(ToDoList.getCounterVerificationTask(getApplicationContext())));
                    }
                    if (NewMainActivity.mnSurveyApproval != null) {
                        NewMenuFragment.adapter.getMenuItem(NewMainActivity.mnSurveyApproval.getName()).setCounter(String.valueOf(ToDoList.getCounterApprovalTask(getApplicationContext())));
                    }
                    if (NewMainActivity.mnVerifByBranch != null) {
                        NewMenuFragment.adapter.getMenuItem(NewMainActivity.mnVerifByBranch.getName()).setCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getApplicationContext())));
                    }
                    if (NewMainActivity.mnApprovalByBranch != null) {
                        NewMenuFragment.adapter.getMenuItem(NewMainActivity.mnApprovalByBranch.getName()).setCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getApplicationContext())));
                    }
                    if (NewMenuFragment.adapter != null) {
                        NewMenuFragment.adapter.notifyDataSetChanged();
                    }
                    return;
                }
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }

    /**
     * Nendi - 2019.06.20
     * Add initial notification channel
     */
    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Create notification channel for newTasks
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_new_task), getString(R.string.notification_new_task), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Update notification for new task");

            //Add more here for other notification channel

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * Nendi: 2019.07.01
     * Receiver Update GPS Icon from Location Update
     */
    private BroadcastReceiver locationListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UpdateMenuGPS.SetMenuIcon();
                }
            });
        }
    };

    private BroadcastReceiver mockLocationListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DialogManager.showMockDialog(context);
        }
    };

    private ConnectivityChangeReceiver connectivityReceiver = null;

    public void checkGPSMode(){
        try {
            if (Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE) != LOCATION_MODE_HIGH_ACCURACY) {
                locationModeSetting();
            }
        }
        catch (Exception e){
            FireCrash.log(e);
        }
    }

    public void locationModeSetting(){
        LocationRequest mLocationRequest;
        int minTime = LocationTrackingManager.DEFAULT_MIN_TIME_CHANGE_LOCATION;
        int minDistance = LocationTrackingManager.DEFAULT_MIN_DISTANCE_CHANGE_LOCATION;

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(minTime);
        mLocationRequest.setFastestInterval(minTime);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(minDistance);//if u take this to 300 m so u will get location if u walk by 300 m or more
        // devide by 10 to make displacement right and get the right time to update
        mLocationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        NewMainActivity.this,
                                        REQUEST_LOCATION_MODE_CHECK_SETTINGS);
                            }
                            catch (IntentSender.SendIntentException e) { FireCrash.log(e);}
                            catch (ClassCastException e) { FireCrash.log(e);}
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    protected void openTimeline() {
        if (navigation == null) {
            return;
        }

        navigation.setSelectedItemId(R.id.timelineNav);
    }

    private class CheckActiveUser extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String userId = GlobalData.getSharedGlobalData().getUser()
                    .getLogin_id();
            MssRequestType mrt = new MssRequestType();
            mrt.setAudit(GlobalData.getSharedGlobalData().getAuditData());

            String json = GsonHelper.toJson(mrt);
            Boolean is_active = null;

            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(LocationTrackingManager.getContextLocation(), encrypt, decrypt);
            HttpConnectionResult serverResult = null;
            String url = GlobalData.getSharedGlobalData().getURL_GET_TASKLIST();

            //Firebase Performance Trace HTTP Request
            HttpMetric networkMetric =
                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
            Utility.metricStart(networkMetric, json);

            if (Tool.isInternetconnected(LocationTrackingManager.getContextLocation())) {
                try {
                    serverResult = httpConn
                            .requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverResult);
                } catch (Exception ex) {
                    FireCrash.log(ex);
                }
                if (serverResult != null && serverResult.isOK()) {
                    String sentStatus = serverResult.getResult();

                    JsonResponseRetrieveTaskList jrsrtl = GsonHelper.fromJson(sentStatus, JsonResponseRetrieveTaskList.class);

                    if (jrsrtl.getStatus().getCode() == Global.STATUS_CODE_APPL_CLEANSING) {
                        is_active = false;
                    } else {
                        is_active = true;
                    }
                }
            }
            return is_active;
        }

        @Override
        protected void onPostExecute(Boolean isActive) {
            super.onPostExecute(isActive);
            if (Boolean.FALSE.equals(isActive)){
                UninstallerHandler();
                NewMainActivity.Force_Uninstall = true;
            } else {
                NewMainActivity.Force_Uninstall = false;
            }
        }
    }

    protected void logEventCountMenuAccessed(String menuName) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, menuName);
        menuCountAnalytics.logEvent("Menu_Clicked", params);
    }

}
