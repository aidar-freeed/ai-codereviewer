package com.adins.mss.base.mainmenu;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.legacy.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.MssFragmentActivity;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.about.activity.AboutInfoTab;
import com.adins.mss.base.authentication.Authentication;
import com.adins.mss.base.authentication.AuthenticationTask;
import com.adins.mss.base.checkin.activity.CheckInActivity;
import com.adins.mss.base.checkout.activity.CheckOutActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.mainmenu.settings.SettingActivity;
import com.adins.mss.base.tasklog.LogResultActivity;
import com.adins.mss.base.tasklog.TaskLogImpl;
import com.adins.mss.base.tasklog.TaskLogListTask;
import com.adins.mss.base.timeline.Constants;
import com.adins.mss.base.timeline.MenuAdapter;
import com.adins.mss.base.timeline.MenuModel;
import com.adins.mss.base.timeline.activity.Timeline_Activity;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.util.CustomAnimatorLayout;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.UserSession;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TimelineType;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.config.ConfigFileReader;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.MenuDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.AuditDataType;
import com.adins.mss.foundation.http.AuditDataTypeGenerator;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.adins.mss.foundation.notification.Notification;
import com.adins.mss.foundation.oauth2.Token;
import com.adins.mss.foundation.oauth2.store.SharedPreferencesTokenStore;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.adins.mss.foundation.sync.BackgroundServiceSynchronize;
import com.services.MainServices;
import com.tracking.LocationTrackingService;

import org.acra.ACRA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Stack;

public abstract class MainMenuActivity extends MssFragmentActivity {

    public static final String ACTION_ROUTE_EVENT = "com.adins.mss.coll.ACTION_ROUTE_EVENT";
    private static final String SYNCHRONIZATION_PREFERENCE = "com.adins.mss.base.SynchronizationPreference";
    public static Intent AutoSendLocationHistoryService;
    public static Intent RunNotificationService;
    public static MainServices mainServices;
    public static boolean Force_Uninstall = false;
    public static FragmentManager fragmentManager;
    public static MenuModel mnTaskList;
    public static MenuModel mnLog;
    public static MenuModel mnSVYVerify;
    public static MenuModel mnSVYApproval;
    public static MenuModel mnSVYVerifyByBranch;
    public static MenuModel mnSVYApprovalByBranch;
    public static MenuModel mnSVYAssignment;
    public static MenuAdapter menuAdapter;
    public static int tempPosition = 1;
    public static DrawerLeftHandler leftHandler;
    public static Class mss;
    public static Class mainMenuClass;
    public static Fragment verificationFragment;
    public static Fragment approvalFragment;
    public static Fragment verificationFragmentByBranch;
    public static Fragment approvalFragmentByBranch;
    public static Fragment assignmentFragment;
    public static Fragment statusFragment;
    private static MainMenuActivity mainMenuActivity;
    private static Menu mainMenu;
    public boolean isMainMenuOpen = false;
    public ListView mDrawerListLeft;
    public LocationTrackingManager manager;
    public BackgroundServiceSynchronize backgroundSync;
    public ArrayList<MenuModel> models = new ArrayList<>();
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggleLeft;
    protected Fragment fragment;
    protected CharSequence mTitle;
    protected List<String> allMenu = new ArrayList<>();
    protected String title;
    protected String tmpTitle = "";
    protected String tmpTitle2 = "";
    public static boolean isFromSetting = false;

    public static Class getMss() {
        return mss;
    }

    public static void setMss(Class mss) {
        MainMenuActivity.mss = mss;
    }

    public static Class getMainMenuClass() {
        return mainMenuClass;
    }

    public static void setMainMenuClass(Class mainMenuClass) {
        MainMenuActivity.mainMenuClass = mainMenuClass;
    }

    public static Fragment getVerificationFragment() {
        return MainMenuActivity.verificationFragment;
    }

    public static void setVerificationFragment(Fragment value) {
        MainMenuActivity.verificationFragment = value;
    }

    public static Fragment getApprovalFragment() {
        return MainMenuActivity.approvalFragment;
    }

    public static void setApprovalFragment(Fragment value) {
        MainMenuActivity.approvalFragment = value;
    }

    public static Fragment getAssignmentFragment() {
        return assignmentFragment;
    }

    public static void setAssignmentFragment(Fragment assignmentFragment) {
        MainMenuActivity.assignmentFragment = assignmentFragment;
    }

    public static Fragment getVerificationFragmentByBranch() {
        return MainMenuActivity.verificationFragmentByBranch;
    }

    public static void setVerificationFragmentByBranch(Fragment value) {
        MainMenuActivity.verificationFragmentByBranch = value;
    }

    public static Fragment getApprovalFragmentByBranch() {
        return MainMenuActivity.approvalFragmentByBranch;
    }

    public static void setApprovalFragmentByBranch(Fragment value) {
        MainMenuActivity.approvalFragmentByBranch = value;
    }

    public static Fragment getStatusFragment() {
        return MainMenuActivity.statusFragment;
    }

    public static void setStatusFragment(Fragment value) {
        MainMenuActivity.statusFragment = value;
    }

    public static void InitializeGlobalDataIfError(FragmentActivity activity) {
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

                    if (Global.positionStack == null) {
                        Global.positionStack = new Stack<Integer>();
                    }
                    try {
                        if (MainMenuActivity.fragmentManager == null) {
                            MainMenuActivity.fragmentManager = activity.getSupportFragmentManager();
                        }

                        Fragment fragment = new Timeline_Activity();

                        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.content_frame, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } catch (Exception e) {
                        FireCrash.log(e);
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

    public static void updateMenuIcon(boolean isGPS) {
        UpdateMenuIcon uItem = new UpdateMenuIcon();
        uItem.updateGPSIcon(mainMenu);
    }

    public static void gotoSurveyAssignmentTask() {
        mainMenuActivity.gotoSurveyAssignment(0);
    }

    public static void setDrawerPosition(final String title) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(Global.BUND_KEY_CALL_MAIN_MENU, title);
                message.setData(bundle);
                MainMenuActivity.leftHandler.sendMessage(message);
            }
        }, 500);
    }

    public static void setDrawerCounter() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Global.BUND_KEY_REFRESHCOUNTER, true);
                message.setData(bundle);
                try {
                    MainMenuActivity.leftHandler.sendMessage(message);
                } catch (NullPointerException e) {
                    FireCrash.log(e);
                }
            }
        }, 500);
    }

    /**
     * This is used to be implemented in each application to add menu list
     */
    protected abstract ArrayList<MenuModel> getModels();

    protected abstract String getTitleGroup();

    //bong 10 apr 15 - to prevent menu slider when chg password
    protected abstract Fragment getChgPassFragment();

    public HashMap<String, Integer> getTemplateIcon() {
        HashMap<String, Integer> templateIcon = new HashMap<>();
        templateIcon.put(getString(R.string.title_mn_home), R.drawable.ic_home);
        templateIcon.put(getString(R.string.title_mn_newtask), R.drawable.ic_new);
        templateIcon.put(getString(R.string.title_mn_tasklist), R.drawable.ic_odrprogress);
        templateIcon.put(getString(R.string.title_mn_checkorder), R.drawable.ic_checkodr);
        templateIcon.put(getString(R.string.title_mn_log), R.drawable.ic_log);
        templateIcon.put(getString(R.string.title_mn_creditsimulation), R.drawable.ic_simulation);
        templateIcon.put(getString(R.string.title_mn_cancelorder), R.drawable.ic_updateodr);
        templateIcon.put(getString(R.string.title_mn_absentin), R.drawable.ic_checkin);
        templateIcon.put(getString(R.string.title_mn_news), R.drawable.ic_news);
        templateIcon.put(getString(R.string.title_mn_promo), R.drawable.ic_news);
        templateIcon.put(getString(R.string.title_mn_changepassword), R.drawable.ic_changepassword);
        templateIcon.put(getString(R.string.title_mn_synchronize), R.drawable.ic_synchronize);
        templateIcon.put(getString(R.string.title_mn_exit), R.drawable.ic_exit);
        templateIcon.put(getString(R.string.title_mn_about), R.drawable.ic_about);
        templateIcon.put(getString(R.string.title_mn_surveyperformance), R.drawable.ic_performance);
        templateIcon.put(getString(R.string.title_mn_surveyverification), R.drawable.ic_verification);
        templateIcon.put(getString(R.string.title_mn_surveyapproval), R.drawable.ic_approval);
        templateIcon.put(getString(R.string.title_mn_surveyassign), R.drawable.ic_oder_ass);
        templateIcon.put(getString(R.string.title_mn_surveyreassign), R.drawable.ic_odr_reass);
        templateIcon.put(getString(R.string.title_mn_orderassign), R.drawable.ic_oder_ass);
        templateIcon.put(getString(R.string.title_mn_orderreassign), R.drawable.ic_odr_reass);
        templateIcon.put(getString(R.string.title_mn_reportsummary), R.drawable.ic_reprotsummary);
        templateIcon.put(getString(R.string.title_mn_depositreport), R.drawable.ic_depositreport);
        templateIcon.put(getString(R.string.title_mn_paymenthistory), R.drawable.ic_payment_history);
        templateIcon.put(getString(R.string.title_mn_installmentschedule), R.drawable.ic_installment_schedule);
        templateIcon.put(getString(R.string.title_mn_verification_bybranch), R.drawable.ic_verification);
        templateIcon.put(getString(R.string.title_mn_approval_bybranch), R.drawable.ic_approval);
        templateIcon.put(getString(R.string.title_mn_closing_task), R.drawable.ic_approval);
        templateIcon.put(getString(R.string.title_mn_setting), R.drawable.ic_setting);

        return templateIcon;
    }

    public ArrayList<String> getTemplateMenuTitle() {
        ArrayList<String> templateMenuTitle = new ArrayList<>();
        templateMenuTitle.add(getString(R.string.title_mn_home));
        templateMenuTitle.add(getString(R.string.title_mn_newtask));
        templateMenuTitle.add(getString(R.string.title_mn_tasklist));
        templateMenuTitle.add(getString(R.string.title_mn_checkorder));
        templateMenuTitle.add(getString(R.string.title_mn_log));
        templateMenuTitle.add(getString(R.string.title_mn_creditsimulation));
        templateMenuTitle.add(getString(R.string.title_mn_orderassign));
        templateMenuTitle.add(getString(R.string.title_mn_orderreassign));
        templateMenuTitle.add(getString(R.string.title_mn_surveyverification));
        templateMenuTitle.add(getString(R.string.title_mn_surveyapproval));
        templateMenuTitle.add(getString(R.string.title_mn_surveyperformance));
        templateMenuTitle.add(getString(R.string.title_mn_surveyassign));
        templateMenuTitle.add(getString(R.string.title_mn_surveyreassign));
        templateMenuTitle.add(getString(R.string.title_mn_reportsummary));
        templateMenuTitle.add(getString(R.string.title_mn_depositreport));
        templateMenuTitle.add(getString(R.string.title_mn_cancelorder));
        templateMenuTitle.add(getString(R.string.title_mn_absentin));
        templateMenuTitle.add(getString(R.string.title_mn_news));
        templateMenuTitle.add(getString(R.string.title_mn_promo));
        templateMenuTitle.add(getString(R.string.title_mn_verification_bybranch));
        templateMenuTitle.add(getString(R.string.title_mn_approval_bybranch));
        templateMenuTitle.add(getString(R.string.title_mn_closing_task));

        return templateMenuTitle;
    }

    public ArrayList<String> getTemplateOtherMenuTitle() {
        ArrayList<String> otherMenuTitle = new ArrayList<>();
        otherMenuTitle.add(getString(R.string.title_mn_changepassword));
        otherMenuTitle.add(getString(R.string.title_mn_synchronize));
        otherMenuTitle.add(getString(R.string.title_mn_setting));
        otherMenuTitle.add(getString(R.string.title_mn_about));
        otherMenuTitle.add(getString(R.string.title_mn_exit));
        return otherMenuTitle;
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

    public List<String> getMainMenuTitle() {
        if (GlobalData.getSharedGlobalData().getUser() == null) {
            MainMenuActivity.InitializeGlobalDataIfError(getApplicationContext());
        }
        List<String> newMenu = MainMenuHelper.matchingMenu(getServerMenuTitle(), getTemplateMenuTitle());
        return newMenu;
    }

    public List<Integer> getMainMenuIcon() {
        List<Integer> newIcon = MainMenuHelper.matchingIcon(getMainMenuTitle(), getTemplateIcon());
        return newIcon;
    }

    public List<String> getOtherMenuTitle() {
        if (GlobalData.getSharedGlobalData().getUser() == null) {
            MainMenuActivity.InitializeGlobalDataIfError(getApplicationContext());
        }
        List<String> otherMenu = MainMenuHelper.matchingMenu(getServerMenuTitle(), getTemplateOtherMenuTitle());
        return otherMenu;
    }

    public List<Integer> getOtherMenuIcon() {
        List<Integer> otherIcon = MainMenuHelper.matchingIcon(getOtherMenuTitle(), getTemplateIcon());
        return otherIcon;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.onCreate(getApplicationContext(), LocaleHelper.ENGLSIH);

        mainMenuActivity = this;
        setContentView(R.layout.main_menu_layout);
        leftHandler = new DrawerLeftHandler();
        ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(getApplicationContext(), Authentication.LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        String restoredText = prefs.getString(Authentication.LOGIN_PREFERENCES_APPLICATION_CLEANSING, null);
        if (restoredText != null && restoredText.equalsIgnoreCase("uninstall")) {
            UninstallerHandler();
        }

        fragmentManager = getSupportFragmentManager();
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        StoreGlobalDataTemporary();
        mTitle = getTitle();
        Global.positionStack = new Stack<Integer>();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListLeft = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggleLeft = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {

            @Override
            public void onDrawerClosed(View view) {
                if (!getActionBar().isShowing()) getActionBar().show();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset == .05) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDrawerListLeft.setItemChecked(Global.positionStack.lastElement(), true);
                        }
                    });
                }
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggleLeft);
        startBackgroundProcess();
    }

    private void StoreGlobalDataTemporary() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                if (Utility.checkPermissionResult(MainMenuActivity.this, permissions, grantResults))
                    bindLocationListener();
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isMainMenuOpen = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isMainMenuOpen = true;
        Utility.checkPermissionGranted(this);
        try {
            MainMenuActivity.setDrawerCounter();
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
        }

        ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(this, Authentication.LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        String restoredText = prefs.getString(Authentication.LOGIN_PREFERENCES_APPLICATION_CLEANSING, null);
        if (restoredText != null && restoredText.equalsIgnoreCase("uninstall")) {
            UninstallerHandler();
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
            mDrawerListLeft.setItemChecked(0, true);
        }

        if (Global.LTM == null) {
            manager = null;
            StartLocationTracking();
        }
        DialogManager.showGPSAlert(this);
        DialogManager.showTimeProviderAlert(this);

        if (UserSession.isInvalidToken()) {
            DialogManager.showForceExitAlert(this, getString(R.string.failed_refresh_token));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            try {
                mDrawerLayout.openDrawer(mDrawerListLeft);
                return true;
            } catch (ClassCastException e) {
                return super.onKeyDown(keyCode, event);
            } catch (Exception e) {
                FireCrash.log(e);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenuIcon(Global.isGPS);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (mDrawerToggleLeft.onOptionsItemSelected(item)) {
            return true;
        } else if (id == R.id.mnGPS) {
            if (Global.LTM != null) {
                if (Global.LTM.getIsConnected()) {
                    Global.LTM.removeLocationListener();
                    Global.LTM.connectLocationClient();
                } else {
                    StartLocationTracking();
                }
                Animation a = AnimationUtils.loadAnimation(this, R.anim.icon_rotate);
                findViewById(R.id.mnGPS).startAnimation(a);

            }
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggleLeft.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggleLeft.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        try {
            Utility.freeMemory();
            if (MainMenuActivity.Force_Uninstall) {
                UninstallerHandler();
            } else {
                getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                if (Global.positionStack == null) {
                    Global.positionStack = new Stack<Integer>();
                }
                if (tempPosition == position) {
                    mDrawerLayout.closeDrawers();
                    if (getString(R.string.title_mn_log).equalsIgnoreCase(allMenu.get(position)) ||
                            getString(R.string.title_mn_surveyverification).equalsIgnoreCase(allMenu.get(position))
                            || getString(R.string.title_mn_surveyapproval).equalsIgnoreCase(allMenu.get(position))) {
                        if (!models.get(position).getCounter().equals("0")) {
                            mDrawerListLeft.setItemChecked(position, true);

                            Global.positionStack.push(position);
                            setTitle(models.get(position).getTitle());
                        } else {
                            try {
                                mDrawerListLeft.setItemChecked(Global.positionStack.lastElement(), true);
                                tempPosition = Global.positionStack.lastElement();
                            } catch (NoSuchElementException ex) {
                                mDrawerListLeft.setItemChecked(position, true);
                            }
                            gotoSelectedPage(position);
                        }
                    }
                } else {
                    tempPosition = position;
                    if (getString(R.string.title_mn_log).equalsIgnoreCase(allMenu.get(position)) ||
                            getString(R.string.title_mn_surveyverification).equalsIgnoreCase(allMenu.get(position))
                            || getString(R.string.title_mn_surveyapproval).equalsIgnoreCase(allMenu.get(position))) {
                        if (!models.get(position).getCounter().equals("0")) {
                            mDrawerListLeft.setItemChecked(position, true);

                            Global.positionStack.push(position);
                            setTitle(models.get(position).getTitle());
                        } else {
                            try {
                                mDrawerListLeft.setItemChecked(Global.positionStack.lastElement(), true);
                                tempPosition = Global.positionStack.lastElement();
                            } catch (NoSuchElementException ex) {
                                mDrawerListLeft.setItemChecked(position, true);
                            }
                        }
                    } else if (getString(R.string.title_mn_main_menu).equalsIgnoreCase(allMenu.get(position)) ||
                            getString(R.string.title_mn_other).equalsIgnoreCase(allMenu.get(position)) ||
                            getString(R.string.title_mn_synchronize).equalsIgnoreCase(allMenu.get(position)) ||
                            getString(R.string.title_mn_about).equalsIgnoreCase(allMenu.get(position)) ||
                            getString(R.string.title_mn_exit).equalsIgnoreCase(allMenu.get(position)) ||
                            getString(R.string.title_mn_setting).equalsIgnoreCase(allMenu.get(position))) {
                        try {
                            mDrawerListLeft.setItemChecked(Global.positionStack.lastElement(), true);
                            tempPosition = Global.positionStack.lastElement();
                            setTitle(models.get(Global.positionStack.lastElement()).getTitle());
                        } catch (NoSuchElementException ex) {
                            mDrawerListLeft.setItemChecked(position, true);
                        }
                    } else {
                        mDrawerListLeft.setItemChecked(position, true);
                        Global.positionStack.push(position);
                        if (!getString(R.string.title_mn_closing_task).equalsIgnoreCase(allMenu.get(position))) {
                            setTitle(models.get(position).getTitle());
                        }
                    }
                    mDrawerLayout.closeDrawers();
                    gotoSelectedPage(position);

                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    private void gotoSelectedPage(final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (getString(R.string.title_mn_home).equalsIgnoreCase(allMenu.get(position))) {
                    goTimeline(position);
                } else if (getString(R.string.title_mn_newtask).equalsIgnoreCase(allMenu.get(position))) {
                    gotoNewTask(position);
                } else if (getString(R.string.title_mn_tasklist).equalsIgnoreCase(allMenu.get(position))) {
                    gotoTaskList(position);
                } else if (getString(R.string.title_mn_checkorder).equalsIgnoreCase(allMenu.get(position))) {
                    gotoCheckOrder(position);
                } else if (getString(R.string.title_mn_log).equalsIgnoreCase(allMenu.get(position))) {
                    gotoLog(position);
                } else if (getString(R.string.title_mn_creditsimulation).equalsIgnoreCase(allMenu.get(position))) {
                    gotoCreditSimulation(position);
                } else if (getString(R.string.title_mn_cancelorder).equalsIgnoreCase(allMenu.get(position))) {
                    gotoCancelOrder(position);
                } else if (getString(R.string.title_mn_absentin).equalsIgnoreCase(allMenu.get(position))) {
                    gotoCheckIn(position);
                } else if (getString(R.string.title_mn_news).equalsIgnoreCase(allMenu.get(position))) {
                    gotoNews(position);
                } else if (getString(R.string.title_mn_promo).equalsIgnoreCase(allMenu.get(position))) {
                    gotoPromo(position);
                } else if (getString(R.string.title_mn_synchronize).equalsIgnoreCase(allMenu.get(position))) {
                    gotoSynchronize();
                } else if (getString(R.string.title_mn_about).equalsIgnoreCase(allMenu.get(position))) {
                    gotoAbout();
                } else if (getString(R.string.title_mn_exit).equalsIgnoreCase(allMenu.get(position))) {
                    gotoExit();
                } else if (getString(R.string.title_mn_surveyperformance).equalsIgnoreCase(allMenu.get(position))) {
                    gotoSurveyPerformance(position);
                } else if (getString(R.string.title_mn_surveyverification).equalsIgnoreCase(allMenu.get(position))) {
                    gotoSurveyVerification(position);
                } else if (getString(R.string.title_mn_surveyapproval).equalsIgnoreCase(allMenu.get(position))) {
                    gotoSurveyApproval(position);
                } else if (getString(R.string.title_mn_surveyassign).equalsIgnoreCase(allMenu.get(position))) {
                    gotoSurveyAssignment(position);
                } else if (getString(R.string.title_mn_surveyreassign).equalsIgnoreCase(allMenu.get(position))) {
                    gotoSurveyReassignment(position);
                } else if (getString(R.string.title_mn_orderassign).equalsIgnoreCase(allMenu.get(position))) {
                    gotoOrderAssignment(position);
                } else if (getString(R.string.title_mn_orderreassign).equalsIgnoreCase(allMenu.get(position))) {
                    gotoOrderReassignment(position);
                } else if (getString(R.string.title_mn_reportsummary).equalsIgnoreCase(allMenu.get(position))) {
                    gotoReportSummary(position);
                } else if (getString(R.string.title_mn_depositreport).equalsIgnoreCase(allMenu.get(position))) {
                    gotoDepositReport(position);
                } else if (getString(R.string.title_mn_paymenthistory).equalsIgnoreCase(allMenu.get(position))) {
                    gotoPaymentHistory(position);
                } else if (getString(R.string.title_mn_installmentschedule).equalsIgnoreCase(allMenu.get(position))) {
                    gotoInstallmentSchedule(position);
                } else if (getString(R.string.title_mn_verification_bybranch).equalsIgnoreCase(allMenu.get(position))) {
                    gotoSurveyVerificationByBranch(position);
                } else if (getString(R.string.title_mn_approval_bybranch).equalsIgnoreCase(allMenu.get(position))) {
                    gotoSurveyApprovalByBranch(position);
                } else if (getString(R.string.title_mn_closing_task).equalsIgnoreCase(allMenu.get(position))) {
                    gotoClosingTask(position);
                } else if (getString(R.string.title_mn_setting).equalsIgnoreCase(allMenu.get(position))) {
                    gotoSettings();
                }
            }
        }, 500);
    }

    protected abstract void gotoNewTask(int position);

    protected abstract void gotoCheckOrder(int position);

    protected abstract void gotoCreditSimulation(int position);

    protected abstract void gotoCancelOrder(int position);

    protected abstract void gotoTaskList(int position);

    protected abstract void gotoPromo(int position);

    protected abstract void gotoNews(int position);

    protected abstract void gotoOrderAssignment(int position);

    protected abstract void gotoOrderReassignment(int position);

    protected abstract void gotoSurveyPerformance(int position);

    protected abstract void gotoSurveyVerification(int position);

    protected abstract void gotoSurveyApproval(int position);

    protected abstract void gotoSurveyVerificationByBranch(int position);

    protected abstract void gotoSurveyApprovalByBranch(int position);

    protected abstract void gotoSurveyAssignment(int position);

    protected abstract void gotoSurveyReassignment(int position);

    protected abstract void gotoReportSummary(int position);

    protected abstract void gotoDepositReport(int position);

    protected abstract void gotoPaymentHistory(int position);

    protected abstract void gotoInstallmentSchedule(int position);

    protected abstract void gotoClosingTask(int position);

    @Override
    public void onBackPressed() {
        try {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                if (Constants.inSearchMode) {
                    if (Constants.inSearchMode) {
                        CustomAnimatorLayout animatorLayout = new CustomAnimatorLayout(1, 0, 1, 1, 500, Timeline_Activity.searchLayout, true);
                        Timeline_Activity.searchLayout.startAnimation(animatorLayout);
                    }
                    Constants.inSearchMode = false;
                } else {
                    if (MainMenuActivity.fragmentManager.getBackStackEntryCount() > 1) {
                        Log.i("MainActivity", "popping backstack");
                        MainMenuActivity.tempPosition = 0;
                        if (Global.positionStack.size() > 1)
                            Global.positionStack.pop();
                        mDrawerListLeft.setItemChecked(Global.positionStack.lastElement(), true);
                        MainMenuActivity.fragmentManager.popBackStack();
                    } else {
                        MainMenuActivity.tempPosition = 0;
                        try {
                            mDrawerListLeft.setItemChecked(1, true);
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                        Log.i("MainActivity", "nothing on backstack, calling super");
                        this.moveTaskToBack(true);
                    }
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    protected void gotoExit() {
        mDrawerLayout.closeDrawers();
        tempPosition = 0;
        DialogManager.showExitAlert(this, getString(R.string.alertExit));
    }

    protected void gotoLog(int position) {
        fragment = new LogResultActivity();
        TaskLogListTask task = new TaskLogListTask(this, getString(R.string.progressWait),
                getString(R.string.msgNoSent), R.id.content_frame, fragment);
        task.execute();
    }

    protected void gotoCheckIn(int position) {
        fragment = new CheckInActivity();

        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoCheckOut(int position) {
        fragment = new CheckOutActivity();
        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    protected void gotoSettings() {
        isFromSetting = true;
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
    }

    protected void gotoAbout() {
        fragment = new AboutInfoTab();
        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void goTimeline(int position) {
        getActionBar().removeAllTabs();
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        fragment = new Timeline_Activity();

        if (MainMenuActivity.fragmentManager == null)
            MainMenuActivity.fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        mDrawerListLeft.setItemChecked(position, true);
        setTitle(models.get(position).getTitle());
        mDrawerLayout.closeDrawers();
    }

    public void gotoSynchronize() {
        new AuthenticationTask(MainMenuActivity.this);
    }

    protected abstract Intent getIntentSynchronize();

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mnTaskList = null;
        mnLog = null;
        mnSVYVerify = null;
        mnSVYApproval = null;
        mnSVYVerifyByBranch = null;
        mnSVYApprovalByBranch = null;

        if (AutoSendLocationHistoryService != null) {
            stopService(AutoSendLocationHistoryService);
        }
        if (RunNotificationService != null) {
            stopService(RunNotificationService);
        }

        Notification.getSharedNotification().clearNotifAll(this);
    }

    private void StartLocationTracking() {
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            GeneralParameter gp_distance = GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_DISTANCE_TRACKING);
            try {
                if (gp_distance != null) {
                    int distanceTracking = Integer.parseInt(gp_distance.getGs_value());
                    if (distanceTracking != 0) {
                        manager = new LocationTrackingManager(tm, lm, getApplicationContext());
                        manager.setMinimalDistanceChangeLocation(Integer.parseInt(GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), "PRM13_DIST").getGs_value()));
                        manager.setMinimalTimeChangeLocation(5);
                        manager.applyLocationListener(getApplicationContext());
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                manager = new LocationTrackingManager(tm, lm, getApplicationContext());
                manager.setMinimalDistanceChangeLocation(50);
                manager.setMinimalTimeChangeLocation(5);
                manager.applyLocationListener(getApplicationContext());
            }
//do not need to do apply

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

        if (!Global.LAST_SYNC.equals(todayDate)) {
            if (Tool.isInternetconnected(this)) {
                try {
                    backgroundSync = new BackgroundServiceSynchronize(this);
                    backgroundSync.authentication(this);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
        }
    }

    protected void startBackgroundProcess() {
        try {
            startSynchronize();
            StartLocationTracking();
            ToDoList toDoList = new ToDoList(getApplicationContext());
            List<TaskH> listTaskH = toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, null);
            ToDoList.setListOfSurveyStatus(new ArrayList<SurveyHeaderBean>());
            for (TaskH taskH : listTaskH) {
                ToDoList.getListOfSurveyStatus().add(new SurveyHeaderBean(taskH));
            }

            //ngisi tabel tymeline type jika masih fresh install
            ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getApplicationContext(),
                    "wasInserted", Context.MODE_PRIVATE);
            String isInsert = sharedPref.getString("isInsert", Global.FALSE_STRING);
            if (isInsert.equals(Global.FALSE_STRING)) {
                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                sharedPrefEditor.putString("isInsert", Global.TRUE_STRING);
                sharedPrefEditor.commit();
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
            }
            //--------------------------------------------------------


            if (Global.IS_DEV)
                System.out.println("Before start autosendlocationhistory");
            AutoSendLocationHistoryService = new Intent(this, LocationTrackingService.class);
            startService(AutoSendLocationHistoryService);
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
                    if (Global.TempScheme != null)
                        Global.TempScheme = null;
                    Global.TempScheme = new HashMap<String, Date>();

                    for (Scheme scheme : schemes) {
                        Global.TempScheme.put(scheme.getUuid_scheme(), scheme.getScheme_last_update());
                    }

                    return null;
                }
            }.execute();
        } catch (Exception e) {
            FireCrash.log(e);
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
                        String packageName = getPackageName();
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
                Log.d("TAG", "onActivityResult: user accepted the (un)install");
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("TAG", "onActivityResult: user canceled the (un)install");
                UninstallerHandler();
            } else if (resultCode == RESULT_FIRST_USER) {
                Log.d("TAG", "onActivityResult: failed to (un)install");
            }
        }
    }

    @Override
    public void checkAppVersion(NewMainActivity activity) {
        if (UserSession.getAppVersion() == 0) { // simpan version code untuk pertama kali
            UserSession.setAppVersion(AppContext.getInstance().getVersionCode());
        }
    }

    public class DrawerLeftHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle bundle = msg.getData();

                boolean refreshCounter = bundle.getBoolean(Global.BUND_KEY_REFRESHCOUNTER, false);
                if (refreshCounter && isMainMenuOpen) {
                    if (MainMenuActivity.mnLog != null)
                        MainMenuActivity.menuAdapter.getMenuModel(MainMenuActivity.mnLog.getTitle()).setCounter(String.valueOf(TaskLogImpl.getCounterLog(MainMenuActivity.this)));
                    if (MainMenuActivity.mnTaskList != null)
                        MainMenuActivity.menuAdapter.getMenuModel(MainMenuActivity.mnTaskList.getTitle()).setCounter(String.valueOf(ToDoList.getCounterTaskList(MainMenuActivity.this)));
                    if (MainMenuActivity.mnSVYVerify != null)
                        MainMenuActivity.menuAdapter.getMenuModel(MainMenuActivity.mnSVYVerify.getTitle()).setCounter(String.valueOf(ToDoList.getCounterVerificationTask(MainMenuActivity.this)));
                    if (MainMenuActivity.mnSVYApproval != null)
                        MainMenuActivity.menuAdapter.getMenuModel(MainMenuActivity.mnSVYApproval.getTitle()).setCounter(String.valueOf(ToDoList.getCounterApprovalTask(MainMenuActivity.this)));
                    if (MainMenuActivity.mnSVYVerifyByBranch != null)
                        MainMenuActivity.menuAdapter.getMenuModel(MainMenuActivity.mnSVYVerifyByBranch.getTitle()).setCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(MainMenuActivity.this)));
                    if (MainMenuActivity.mnSVYApprovalByBranch != null)
                        MainMenuActivity.menuAdapter.getMenuModel(MainMenuActivity.mnSVYApprovalByBranch.getTitle()).setCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(MainMenuActivity.this)));

                    if (MainMenuActivity.menuAdapter != null)
                        MainMenuActivity.menuAdapter.notifyDataSetChanged();
                    return;
                }
                String title = bundle.getString(Global.BUND_KEY_CALL_MAIN_MENU);
                int position = 0;
                if (getModels() != null && title != null) {
                    int i = 0;
                    for (MenuModel model : getModels()) {
                        if (model.getTitle().equals(title))
                            position = i;
                        i++;
                    }
                }
                tempPosition = position;
                mDrawerListLeft.setItemChecked(position, true);

            } catch (Exception e) {
                FireCrash.log(e);
                Log.d("refresh_test", e.getMessage());
            }
        }
    }
}