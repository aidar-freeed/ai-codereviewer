package com.adins.mss.base.authentication;

import android.content.Context;
import android.util.Log;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.util.GenericAsyncTask;
import com.adins.mss.base.util.GenericAsyncTask.GenericTaskInterface;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.UserSession;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.Menu;
import com.adins.mss.dao.Timeline;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.MenuDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.AuditDataType;
import com.adins.mss.foundation.http.AuditDataTypeGenerator;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.foundation.http.MssResponseType;
import com.adins.mss.foundation.http.MssResponseType.Status;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> Used to authenticate user, using HttpCryptedConnection to send user credential to server, and receive server response
 *
 * @author glen.iglesias
 */
public class Authentication implements GenericTaskInterface {

    public static final String LOGIN_PREFERENCES = "login_preferences";
    public static final String LOGIN_PREFERENCES_APPLICATION_CLEANSING = "login_preferences.APPLICATION_CLEANSING";
    public static final String SHARED_PREF = "sharedPreferencesAuthentication";
    public static final String SHARED_PREF_KEY_FRESH_INSTALL = "sharedPreferencesKey_freshInstall";
    public static final String SHARED_PREF_KEY_DB_SAVED = "sharedPreferencesKey_dbsaved";
    private static final int LOGIN_SUCCESS = 1;
    private static final int FORCE_UPDATE = 2;
    private static final int FORCE_CLEANSING = 3;
    private static final int LOGIN_FAIL = 0;
    private static final String TASK_OBJ_KEY_CONN = "task_object_key_connection";
    private static final String TASK_OBJ_KEY_URL = "task_object_key_url";
    private static final String TASK_OBJ_KEY_JSON = "task_object_key_json";
    private static final String TASK_OBJ_KEY_DELEGATE = "task_object_key_delegate";
    private static final String TASK_OBJ_KEY_CONTEXT = "task_object_key_context";
    private static final String TASK_OBJ_KEY_RESULT = "task_object_key_result";
    private static final String TASK_OBJ_KEY_BUILD_NUMBER = "task_object_key_build_number";
    private static final String TASK_OBJ_KEY_LOGIN_ID = "task_object_key_login_id";
    //Debug
    public static boolean enableSimulatedResult = false;
    public static int simulatedResult = 1;
    protected static String temp_loginID;
    protected static String temp_password;
    private String url;
    private AuthenticationHandler handler;
    private boolean encrypt;
    private boolean decrypt;

    /**
     * Default constructor. Will fetch data from GlobalData if any
     */
    public Authentication() {
        GlobalData globData = GlobalData.getSharedGlobalData();
        url = globData.getURL_LOGIN();
        encrypt = globData.isEncrypt();
        decrypt = globData.isDecrypt();
    }

    /**
     * Send user credential over HTTP to server. Will replace stored AuditDataType in GlobalData with newly
     * generated one. Will also store successfully logged in user data in GlobalData
     * <br>Connection will run on async task
     *
     * @param context
     * @param url      target URL
     * @param username username to be authenticated
     * @param password password to be authenticated
     * @param encrypt  flag to encrypt sent data to server
     * @param decrypt  flag to decrypt server response message
     */
    public static void authenticateOnBackground(Context context, String url, String username, String password, boolean encrypt, boolean decrypt, int buildNumber, AuthenticationHandler handler) {

        Map<String, String> listImei = AuditDataTypeGenerator.getListImeiFromDevice(context);
        GlobalData.getSharedGlobalData().setImei(listImei.get(MssRequestType.UN_KEY_IMEI));
        if (listImei.get(MssRequestType.UN_KEY_IMEI2) != null) {
            GlobalData.getSharedGlobalData().setImei2(listImei.get(MssRequestType.UN_KEY_IMEI2));
        }

        //generate and store new AuditDataType
        String language = LocaleHelper.getLanguage(context);
        LocaleHelper.setLocale(context,language);
        GlobalData.getSharedGlobalData().setLocale(language);

        AuditDataType auditDataType = AuditDataTypeGenerator.generateAuditDataType(username);
        storeAuditData(auditDataType);

        //freshInstall
        boolean needUpdateVersion = UserSession.needUpdateVersion();
        boolean isFresh = isFreshInstall(context);
        boolean isFreshInstall = needUpdateVersion || isFresh;

        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);

        temp_loginID = username;
        temp_password = password;
        String json = getStringForSending(username, password, isFreshInstall);
        if (Global.IS_DEV)
            System.out.println(json);

        //use asynctask instead
        HashMap<String, Object> params = new HashMap<>();
        params.put(TASK_OBJ_KEY_CONN, httpConn);
        params.put(TASK_OBJ_KEY_URL, url);
        params.put(TASK_OBJ_KEY_JSON, json);
        params.put(TASK_OBJ_KEY_DELEGATE, handler);
        params.put(TASK_OBJ_KEY_CONTEXT, context);
        params.put(TASK_OBJ_KEY_BUILD_NUMBER, buildNumber);
        params.put(TASK_OBJ_KEY_LOGIN_ID, username);

        GenericAsyncTask task = new GenericAsyncTask(new Authentication());
        task.setAdditionalObject(params);
        task.execute();
    }

    /**
     * Send user credential over HTTP to server using GlobalData settings, like url, encryption, and decryption
     * <br>Connection will run on async task
     *
     * @param username username to be authenticated
     * @param password password to be authenticated
     */
    public static void authenticateOnBackground(Context context, String username, String password, int buildNumber, AuthenticationHandler handler) {
        GlobalData globData = GlobalData.getSharedGlobalData();

        String url = globData.getURL_LOGIN();
        boolean enc = globData.isEncrypt();
        boolean dec = globData.isDecrypt();

        authenticateOnBackground(context, url, username, password, enc, dec, buildNumber, handler);
    }

    /**
     * Define a way to interpret server response and translate into AuthenticationResultBean
     * <p>Should you choose a different way, override this method
     *
     * @param result a string received from server
     * @return an AuthenticationResultBean as standard result
     */
    protected static AuthenticationResultBean mapResultToBean(String result, String login_id_tenant) {
        //Default, treat result as JSON with format of LoginUserResponse
        LoginUserResponse responseBean;
        try {
            responseBean = GsonHelper.fromJson(result, LoginUserResponse.class);
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                e.printStackTrace();
            return null;
        }

        Status resultStatus = responseBean.getStatus();

        List<GeneralParameter> generalParameterss = responseBean.getListGeneralParameter();

        //add tenant id to general parameters
        GeneralParameter gP = new GeneralParameter();
        if (!generalParameterss.isEmpty()) {
            gP.setUuid_general_parameter(Tool.getUUID());
            gP.setGs_code(Global.GS_TENANT_ID);
            gP.setGs_value(login_id_tenant);
            generalParameterss.add(0, gP);
        }


        //20 Jan 2015, adapt to backend system
        AuthenticationResultBean resultBean = new AuthenticationResultBean();

        if (resultStatus.getCode() == 0) {            //success login
            User user = responseBean.getUser();

            resultBean.setActiveUser(true);
            String loginGenParam = getGeneralParameter(Global.GS_VERS_LOGIN, generalParameterss);
            if(loginGenParam != null && loginGenParam.equals("1")){
                resultBean.setForceUpdate(true);
            }
            resultBean.setGeneralParameters(responseBean.getListGeneralParameter());
            resultBean.setListMenu(responseBean.getListMenu());
            resultBean.setLastUpdateVersion(getGeneralParameter(Global.GS_BUILD_NUMBER, generalParameterss));
            resultBean.setLoginResult(LOGIN_SUCCESS);
            resultBean.setNeedUpdatePassword(user.getChg_pwd().equals(Global.TRUE_STRING));
            resultBean.setOtaLink(getGeneralParameter(Global.GS_URL_DOWNLOAD, generalParameterss));
            resultBean.setUserInfo(user);
        } else {                                        //login failed
            resultBean.setLoginResult(LOGIN_FAIL);
            resultBean.setMessage(resultStatus.getMessage());
        }
        return resultBean;
    }

    private static String getGeneralParameter(String name, List<GeneralParameter> generalParameterss) {
        for (GeneralParameter param : generalParameterss) {
            if (param.getGs_code().equals(name)) {
                return param.getGs_value();
            }
        }
        return null;
    }

    /**
     * Override to implement different method of storing AuditData
     *
     * @param auditData
     */
    protected static void storeAuditData(AuditDataType auditData) {
        GlobalData.getSharedGlobalData().setAuditData(auditData);
    }

    /**
     * Override this method to provide other format of String to send to server
     * <p>Default is using AuthenticationModel as the format
     *
     * @param username       username the user input
     * @param password       password the user input
     * @param isFreshInstall true if this application never had any user login yet
     * @return string of data (JSON or not) to send to server
     * @see MssRequestType
     */
    protected static String getStringForSending(String username, String password, boolean isFreshInstall) {
        //Initiate audit data
        AuditDataType audit = AuditDataTypeGenerator.generateActiveUserAuditData();
        GlobalData.getSharedGlobalData().setAuditData(audit);

        String strFlagFreshInstall = isFreshInstall ? "1" : "0";

        //Create LoginUserRequest
        LoginUserRequest data = new LoginUserRequest();
        data.setAudit(audit);
        data.setUsername(username);
        data.setPassword(password);
        data.setFlagFreshInstall(strFlagFreshInstall);
        data.setFcmTokenId(Global.Token);

        //GlobalData.imei were set from method authenticate
        data.addImeiAndroidIdToUnstructured();

        String json = Formatter.getJsonFromObject(data);
        return json;
    }

    /**
     * Set as no longer a fresh install to application preferences. On next call to authenticate(), method would give
     * false to isFreshInstall
     *
     * @param context
     */
    public static void setAsNonFreshInstall(Context context) {
        ObscuredSharedPreferences pref = ObscuredSharedPreferences.getPrefs(context, SHARED_PREF, Context.MODE_PRIVATE);
        pref.edit().putString(SHARED_PREF_KEY_FRESH_INSTALL, "false").apply();
    }

    /**
     * Set as a fresh install to application preferences. On next call to authenticate(), method would give
     * true to isFreshInstall
     *
     * @param context
     */
    public static void setAsFreshInstall(Context context) {
        ObscuredSharedPreferences pref = ObscuredSharedPreferences.getPrefs(context, SHARED_PREF, Context.MODE_PRIVATE);
        pref.edit().putString(SHARED_PREF_KEY_FRESH_INSTALL, "true").apply();
    }


    /**
     * Method to check if application is first-time running after installation and has device's IMEI not registered yet
     * based on whether the sharedPreference exist or not.
     * <p/>
     * Override this method to implement other method of determining if it's a fresh install
     *
     * @param context
     * @return true if it is a fresh install application, false if it is not
     */
    public static boolean isFreshInstall(Context context) {
        ObscuredSharedPreferences pref = ObscuredSharedPreferences.getPrefs(context, SHARED_PREF, Context.MODE_PRIVATE);
        String isFreshInstall = pref.getString(SHARED_PREF_KEY_FRESH_INSTALL, "true");                //if no preference exist, meaning it's a new install
        return Boolean.parseBoolean(isFreshInstall);
    }

    /**
     * Call this method and pass parameters object to save it to database
     * <p/>
     * <p>Override this method to change how application save the parameters
     */
    protected static void saveServerParameters(Context context, List<GeneralParameter> generalParameters) {


        for (GeneralParameter gp : generalParameters) {
            String temp_uuid_user = gp.getUuid_user();
            User user = UserDataAccess.getOne(context, temp_uuid_user);
            gp.setUser(user);
            GeneralParameterDataAccess.addOrReplace(context, gp);
        }

        //save to GlobalData
        GlobalData.getSharedGlobalData().loadGeneralParameters(generalParameters);
    }

    protected static void saveUserInfo(Context context, User authenticatedUser) {
        String temp_uuid_user = authenticatedUser.getUuid_user();
        User user = UserDataAccess.getOne(context, temp_uuid_user);
        if (user != null) {
            user.setLogin_id(temp_loginID);
            user.setPassword(temp_password);
            if (Global.IS_LOGIN) {
                user.setBranch_name(authenticatedUser.getBranch_name());
                Global.IS_LOGIN = false;
            }

            //olivia : update jam start dan end tracking
            user.setIs_tracking(authenticatedUser.getIs_tracking());
            if (authenticatedUser.getIs_tracking() != null && authenticatedUser.getIs_tracking().equals("1")) {
                user.setStart_time(authenticatedUser.getStart_time());
                user.setEnd_time(authenticatedUser.getEnd_time());
            }
            if(authenticatedUser.getTracking_days() != null && !authenticatedUser.getTracking_days().equals("")){
                user.setTracking_days(authenticatedUser.getTracking_days());
            }

            user.setCash_on_hand(authenticatedUser.getCash_on_hand());
            user.setCash_limit(authenticatedUser.getCash_limit());

            UserDataAccess.addOrReplace(context, user);
            // save to GlobalData
            GlobalData.getSharedGlobalData().setUser(user);
            Global.user = user;
        } else {
            authenticatedUser.setLogin_id(temp_loginID);
            authenticatedUser.setPassword(temp_password);
            UserDataAccess.addOrReplace(context, authenticatedUser);
            // save to GlobalData
            GlobalData.getSharedGlobalData().setUser(authenticatedUser);
            Global.user = authenticatedUser;
        }
    }

    protected static void saveMenu(Context context, List<Menu> listMenu) {
        for (Menu menu : listMenu) {
            String temp_uuid_user = menu.getUuid_user();
            User user = UserDataAccess.getOne(context, temp_uuid_user);
            menu.setUser(user);
            MenuDataAccess.addOrReplace(context, menu);
        }
    }

    protected static void updateTimeline(Context context) {
        // 2017/10/09 | olivia : hapus timeline yang sudah lebih dari range di general parameter
        int range = GlobalData.getSharedGlobalData().getKeepTimelineInDays();
        List<Timeline> timelines = TimelineManager.getAllTimeline(context);
        for (Timeline timeline : timelines) {
            if (timeline.getDtm_crt().before(Tool.getIncrementDate(-range)))
                TimelineDataAccess.delete(context, timeline);
        }
    }

    /**
     * Send user credential over HTTP to server
     * <br>Will run connection on background thread
     *
     * @param context
     * @param username
     * @param password
     */
    public void authenticateOnBackground(Context context, String username, String password, int buildNumber) {
        authenticateOnBackground(context, url, username, password, encrypt, decrypt, buildNumber, handler);
    }

    //=== Generic Task Interface Callbacks ===//

    @Override
    public void onPreExecute(GenericAsyncTask task) {
        Log.d("Log","OnPreExecute");//must override this
    }

    @Override
    public String doInBackground(GenericAsyncTask task, String... args) {

        HttpCryptedConnection httpConn = (HttpCryptedConnection) task.getAdditionalObject().get(TASK_OBJ_KEY_CONN);
        String json = (String) task.getAdditionalObject().get(TASK_OBJ_KEY_JSON);
        String url = (String) task.getAdditionalObject().get(TASK_OBJ_KEY_URL);
        HttpConnectionResult result = null;

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            result = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, result);
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                e.printStackTrace();
        }

        if (result != null) task.getAdditionalObject().put(TASK_OBJ_KEY_RESULT, result);
        return "";
    }

    @Override
    public void onPostExecute(GenericAsyncTask task, String result,
                              String errMsg) {

        HttpConnectionResult connResult = (HttpConnectionResult) task.getAdditionalObject().get(TASK_OBJ_KEY_RESULT);
        AuthenticationHandler handler = (AuthenticationHandler) task.getAdditionalObject().get(TASK_OBJ_KEY_DELEGATE);
        Context context = (Context) task.getAdditionalObject().get(TASK_OBJ_KEY_CONTEXT);
        String loginId = (String) task.getAdditionalObject().get(TASK_OBJ_KEY_LOGIN_ID);

        if (handler != null) {
            if (connResult != null && connResult.isOK()) {
                //success

                //convert to bean
                AuthenticationResultBean authResultBean = mapResultToBean(connResult.getResult(), loginId);

                //bong 30 mar 15 - penjagaan jika belum log in di router tapi sudah dapat ip
                try {
                    if (authResultBean == null) {
                        String error = context.getString(R.string.request_error) + ".\n" + context.getString(R.string.connection_failed_wifi);
                        connResult.setResult(error);
                        handler.onConnectionFail(null, connResult);
                        return;
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    if (Global.IS_DEV)
                        e.printStackTrace();
                    handler.onConnectionFail(null, connResult);
                    return;
                }

                MssResponseType responseFromServer = GsonHelper.fromJson(connResult.getResult(), MssResponseType.class);

                if (responseFromServer.getStatus().getCode() == Global.STATUS_CODE_APPL_CLEANSING) {
                    NewMainActivity.Force_Uninstall = true;
                    handler.onInactiveUser(null);

                    ObscuredSharedPreferences.Editor editor = ObscuredSharedPreferences.getPrefs(context, LOGIN_PREFERENCES, Context.MODE_PRIVATE).edit();
                    editor.putString(LOGIN_PREFERENCES_APPLICATION_CLEANSING, "uninstall");
                    editor.apply();
                    return;
                } else {
                    try {
                        ObscuredSharedPreferences.Editor editor = ObscuredSharedPreferences.getPrefs(context, LOGIN_PREFERENCES, Context.MODE_PRIVATE).edit();
                        editor.remove(LOGIN_PREFERENCES_APPLICATION_CLEANSING);
                        editor.apply();
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }


                String message = authResultBean.getMessage();
                String otaLink = authResultBean.getOtaLink();

                //get login status
                int status = 0;
                status = authResultBean.getLoginResult();

                //debug
                if (enableSimulatedResult) {
                    status = simulatedResult;
                }

                if (status != LOGIN_FAIL) {                //Login authorized
                    boolean needUpdatePassword = authResultBean.needUpdatePassword();
                    boolean pwdExp = "1".equals(authResultBean.getUserInfo().getPwd_exp());
                    boolean forceUpdate = authResultBean.isForceUpdate();
                    boolean isActiveUser = authResultBean.isActiveUser();
                    List<GeneralParameter> generalParameters = authResultBean.getGeneralParameters();
                    List<Menu> listMenu = authResultBean.getListMenu();
                    User authenticatedUser = authResultBean.getUserInfo();

                    // bong - 30 jan 15 need set uuid_user to general parameter
                    for (GeneralParameter generalParameter : generalParameters) {
                        if (generalParameter.getUuid_user() == null || "".equals(generalParameter.getUuid_user()))
                            generalParameter.setUuid_user(authResultBean.getUserInfo().getUuid_user());
                    }

                    for (Menu menu : listMenu) {
                        if (menu.getUuid_user() == null || "".equals(menu.getUuid_user()))
                            menu.setUuid_user(authResultBean.getUserInfo().getUuid_user());
                    }

                    boolean softUpdate = false;

                    String listVersion = authResultBean.getLastUpdateVersion();
                    String[] versions = Tool.split(listVersion, Global.DELIMETER_DATA);
                    String thisVersion = Tool.split(Global.APP_VERSION, "-")[0];
                    String lastVersionFromServer = null;
                    for (int i = 0; i < versions.length; i++) {
                        lastVersionFromServer = versions[i];
                        if (thisVersion.equals(lastVersionFromServer)) {
                            softUpdate = true;
                        }
                    }

                    if (!softUpdate) {
                        forceUpdate = true;
                    } else {
                        if (thisVersion.equals(lastVersionFromServer))
                            softUpdate = false;
                    }

                    GeneralParameterDataAccess.clean(context);
                    MenuDataAccess.clean(context);

                    saveUserInfo(context, authenticatedUser);
                    saveServerParameters(context, generalParameters);
                    saveMenu(context, listMenu);

                    updateTimeline(context);

                    //GIGIN ~ SAVE USER INFO TO ACRA
                    ACRA.getErrorReporter().putCustomData("UUID_USER", GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    ACRA.getErrorReporter().putCustomData("LOGIN_ID", GlobalData.getSharedGlobalData().getUser().getLogin_id());
                    ACRA.getErrorReporter().putCustomData("JOB_DESCRIPTION", GlobalData.getSharedGlobalData().getUser().getJob_description());
                    ACRA.getErrorReporter().putCustomData("BRANCH_NAME", GlobalData.getSharedGlobalData().getUser().getBranch_name());
                    ACRA.getErrorReporter().putCustomData("TENANT_ID", GlobalData.getSharedGlobalData().getTenant());

                    // bong 11 march 15 - stored callerId in audit
                    GlobalData.getSharedGlobalData().getAuditData().setCallerId(authResultBean.getUserInfo().getUuid_user());
                    if (!isActiveUser) {
                        status = FORCE_CLEANSING;
                    } else if (forceUpdate /*&& latestVersion > buildNumber*/) {
                        status = FORCE_UPDATE;
                    }

                    //proceed with status
                    if (status == LOGIN_SUCCESS) {
                        //Set as non fresh install
                        setAsNonFreshInstall(context);

                        // simpan app version ketika sukses auth
                        UserSession.setAppVersion(AppContext.getInstance().getVersionCode());

                        //Store successfully logged in user's data to GlobalData
                        authenticatedUser.setLogin_id(temp_loginID);
                        authenticatedUser.setPassword(temp_password);
                        String temp_uuid_user = authenticatedUser.getUuid_user();
                        User user = UserDataAccess.getOne(context, temp_uuid_user);
                        if (user != null) {
                            user.setLogin_id(temp_loginID);
                            user.setPassword(temp_password);
                            GlobalData.getSharedGlobalData().setUser(user);
                        } else {
                            GlobalData.getSharedGlobalData().setUser(authenticatedUser);
                        }
                        checkIsEnableLoyalty(generalParameters);
                        if(Global.ENABLE_USER_HELP) {
                            fetchUserHelp(context, generalParameters);
                        }
                        //load theme
                        fetchTheme(context,generalParameters,handler,null, otaLink,
                                needUpdatePassword, pwdExp, softUpdate,
                                message, GlobalData.getSharedGlobalData().getUser());

                    } else if (status == FORCE_UPDATE) {
                        handler.onForceUpdate(null, message, otaLink);
                    } else if (status == FORCE_CLEANSING) {
                        handler.onInactiveUser(null);
                    }
                } else {                                //login unauthorized
                    handler.onLoginFail(null, message);
                }

            } else {                                    //fail
                handler.onConnectionFail(null, connResult);
            }

        }

    }

    private void fetchTheme(Context context, List<GeneralParameter> generalParameters,
                            final AuthenticationHandler handler, final Authentication auth, final String otaLink,
                            final boolean needUpdatePassword, final boolean pwdExp,
                            final boolean softUpdate, final String message, final User user) {
        String themeConfigUrl = null;
        if(GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_SURVEY)){
            themeConfigUrl = getGeneralParameter(Global.GS_THEME_CONFIG_SURVEY,generalParameters);
        }
        else if(GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_COLLECTION)){
            themeConfigUrl = getGeneralParameter(Global.GS_THEME_CONFIG_COLLECTION,generalParameters);
        }
        else if(GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_ORDER)){
            themeConfigUrl = getGeneralParameter(Global.GS_THEME_CONFIG_ORDER,generalParameters);
        }
        ThemeLoader themeLoader = new ThemeLoader(context);
        themeLoader.loadThemeFromUrl(themeConfigUrl, new ThemeLoader.ColorSetLoaderCallback() {
            @Override
            public void onHasLoaded(DynamicTheme dynamicTheme) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {
                handler.onLoginSuccess(auth, otaLink, needUpdatePassword, pwdExp, softUpdate, message, user);
            }
        });
    }

    private void fetchUserHelp(Context context, List<GeneralParameter> generalParameters){
        String userHelpUrl = null;
        String commonUserHelpUrl = getGeneralParameter(Global.GS_COMMON_USERHELP_LINK, generalParameters);
        if(GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_SURVEY)){
            userHelpUrl = getGeneralParameter(Global.GS_MS_USERHELP_LINK,generalParameters);
        }
        else if(GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_COLLECTION)){
            userHelpUrl = getGeneralParameter(Global.GS_MC_USERHELP_LINK,generalParameters);
        }
        else if(GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_ORDER)){
            userHelpUrl = getGeneralParameter(Global.GS_MO_USERHELP_LINK,generalParameters);
        }
        UserHelp.initializeUserHelp(context, commonUserHelpUrl, userHelpUrl);
    }

    private void checkIsEnableLoyalty(List<GeneralParameter> generalParameters){
        String gsCode = null;
        String svyJob = null;
        if(GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_SURVEY)){
            gsCode = Global.GS_MS_LOYALTY_ENABLED;
            svyJob = getGeneralParameter("MS_JOBSVY",generalParameters);
        }
        else if(GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_COLLECTION)){
            gsCode = Global.GS_MC_LOYALTY_ENABLED;
        }
        String value = getGeneralParameter(gsCode,generalParameters);
        if(value == null){
            Global.LOYALTI_ENABLED = false;
            return;
        }

        if(value.equals("1")){
            Global.LOYALTI_ENABLED = true;
            if(svyJob != null && !svyJob.isEmpty()){
                svyJob = svyJob.trim();
                String[] svyJobParts = svyJob.split(";");
                Global.SLA_LOYALTI_JOB = svyJobParts[0].split(",");
            }
        }
        else {
            Global.LOYALTI_ENABLED = false;
        }
    }

    //=== Setter and Getter ===//
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AuthenticationHandler getHandler() {
        return handler;
    }

    public void setHandler(AuthenticationHandler handler) {
        this.handler = handler;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public boolean isDecrypt() {
        return decrypt;
    }

    public void setDecrypt(boolean decrypt) {
        this.decrypt = decrypt;
    }

    public static String getTemp_loginID() {
        return temp_loginID;
    }

    /**
     * Callback interface to handle authentication result. When authentication is done, user info need to be registered
     * to GlobalData manually
     *
     * @author glen.iglesias
     */
    public interface AuthenticationHandler {

        /**
         * Called when connection fail
         *
         * @param auth
         * @param result HttpConnectionResult containing error code and reason phrase
         */
        void onConnectionFail(Authentication auth, HttpConnectionResult result);

        /**
         * Called when server reject credential sent by application
         *
         * @param auth
         * @param message message returned by the server
         */
        void onLoginFail(Authentication auth, String message);

        /**
         * Called when server force application update
         *
         * @param auth
         * @param message message returned by the server
         * @param otaLink OTA link to download update
         */
        void onForceUpdate(Authentication auth, String message, String otaLink);

        /**
         * Called when server report sent credential belong to inactive user
         *
         * @param auth
         */
        void onInactiveUser(Authentication auth);

        /**
         * Called when server has successfully authenticate credential.
         * Authenticated user need to be registered to GlobalData manually
         *
         * @param auth
         * @param otaLink            OTA link to download update, or null if no update needed
         * @param needUpdatePassword flag if password change is needed
         * @param message            message returned by the server
         * @param authenticatedUser  user info which successfully authenticated with server
         */
        void onLoginSuccess(Authentication auth, String otaLink, boolean needUpdatePassword, boolean pwdExp, boolean needUpdateApplication, String message, User authenticatedUser);

    }


}
