package com.adins.mss.base;

import android.content.Context;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.base.tracking.LocationTrackingSchedule;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.config.ConfigFileReader;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.http.AuditDataType;
import com.adins.mss.foundation.http.AuditDataTypeGenerator;
import com.adins.mss.foundation.http.HttpConnection.ConnectionCryptor;
import com.adins.mss.foundation.oauth2.OAuth2Client;
import com.adins.mss.foundation.oauth2.Token;
import com.adins.mss.foundation.services.AutoSendSerivce;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.List;
import java.util.Properties;

/**
 * A singleton to hold basic configurations of MSS like URL and encryption flag and other stored data.
 * Avoid modifying this code to add more properties. Create a new class instead outside of this library.
 * <p>
 * GlobalData can be initialized by loading from .properties file by calling loadFromProperties(), or can load GeneralParameter
 * on runtime with loadGeneralParameters()
 * <p>
 * If a modification to this class inside this library is necessary, things that need to be added
 * for each new property are:
 * <li> The new property along with it's setter and getter
 * <li> A constant String which store the key on .properties file
 * <li> Implementation of getting the value from .properties file (using ConfigFileReader) inside
 * loadFromProperties();
 *
 * @author glen.iglesias
 */
public class GlobalData {

    //=== Constant ===//
    //Holds the key to map from .properties
    public static final String PROPERTY_FILENAME = "application";
    public static final String PROP_ENCRYPT = "encrypt";

    //=== Phone Data ===//
    public static final String PROP_DECRYPT = "decrypt";
    public static final String PROP_IS_DEVELOPER = "is_developer";
    public static final String PROP_IS_REQUIRED_ACCESS_TOKEN = "is_required_access_token";
    public static final String PROP_CAN_ACCESS_DEVELOPER_MODE = "can_access_developer_mode";
    public static final String PROP_CLIENT_ID = "client_id";
    public static final String PROP_OWN_CAMERA = "use_own_camera";
    public static final String PROP_APPLICATION_NAME = "application";
    public static final String PROP_IS_BYPASS_DEVELOPER = "is_bypass_developer";
    public static final String PROP_ENABLE_USER_HELP = "enable_user_help";
    public static final String PROP_ENABLE_LOC_PERMISSION_UI = "enable_loc_permission_ui";
    public static final String PROP_DISABLE_ACRA = "disable_acra";
    private static final String http = "http";
    private static final String https = "https";
    private static final String PROP_URL_MAIN = "url_main";
    private static final String PROP_URL_SERV_SYNC = "url_serv_sync";
    private static final String PROP_URL_SERV_TRACKING = "url_serv_tracking";
    private static final String PROP_URL_SERV_PERSONALIZATION = "url_serv_personalization";
    private static final String PROP_URL_UPLOAD_AVATAR = "url_upload_avatar";
    private static final String PROP_URL_LOGIN = "url_login";
    private static final String PROP_URL_UPDATE_FCM = "url_update_fcm";
    private static final String PROP_URL_CHANGEPASSWORD = "url_changepassword";
    private static final String PROP_URL_GET_ABSENSI = "url_get_absensi";
    private static final String PROP_URL_GET_TASKLIST = "url_get_tasklist";
    private static final String PROP_URL_REFRESHTASK = "url_refreshtask";
    private static final String PROP_URL_SUBMITTASK = "url_submittask";
    private static final String PROP_URL_SUBMITOPENREADTASK = "url_submitopenreadtask";
    private static final String PROP_URL_GET_QUESTIONSET = "url_get_questionset";
    private static final String PROP_URL_GET_VERIFICATION = "url_get_verification";
    private static final String PROP_URL_GET_IMAGE = "url_get_image";
    private static final String PROP_URL_GET_SCHEME = "url_get_scheme";
    private static final String PROP_URL_GET_SVYPERFORMANCE = "url_get_svyperformance";
    private static final String PROP_URL_GET_LOOKUP = "url_get_lookup";
    private static final String PROP_URL_GET_LIST_REASSIGNMENT = "url_get_list_reassignment";
    private static final String PROP_URL_GET_DETAIL_REASSIGNMENT = "url_get_detail_reassignment";
    private static final String PROP_URL_GET_LIST_ASSIGNMENT = "url_get_list_assignment";
    private static final String PROP_URL_GET_DETAIL_ASSIGNMENT = "url_get_detail_assignment";
    private static final String PROP_URL_GET_DETAIL_ORDER = "url_get_detail_order";
    private static final String PROP_URL_GET_DETAIL_TASK = "url_get_detail_Task";
    private static final String PROP_URL_SUBMIT_ASSIGN = "url_submit_assign";
    private static final String PROP_URL_GET_LIST_VERIFICATION = "url_get_list_verification";
    private static final String PROP_URL_GET_LIST_APPROVAL = "url_get_list_approval";
    private static final String PROP_URL_SENDDEPOSITREPORT = "url_senddepositreport";
    private final static String PROP_URL_SENDDEPOSITREPORTAC="url_senddepositreportAC";
    private static final String PROP_URL_GET_PAYMENTHISTORY = "url_get_paymenthistory";
    private static final String PROP_URL_GET_INSTALLMENTSCHEDULE = "url_get_installmentschedule";
    private static final String PROP_URL_GET_COLLECTIONHISTORY = "url_get_collectionhistory";
    private final static String PROP_URL_GET_RECEIPT_HISTORY="url_get_receipt_history";
    private final static String PROP_URL_GET_RECEIPT_HISTORY_PDF="url_get_receipt_history_pdf";
    private static final String PROP_URL_SUBMITVERIFICATIONTASK = "url_submitverificationtask";
    private static final String PROP_URL_SUBMITAPPROVALTASK = "url_submitapprovaltask";
    private static final String PROP_URL_GET_CONTENTNEWS = "url_get_contentnews";
    private static final String PROP_URL_CHECKORDER = "url_checkorder";
    private static final String PROP_URL_GET_TASK = "url_get_task";
    private static final String PROP_URL_GET_NEWSHEADER = "url_get_newsheader";
    private static final String PROP_URL_GET_NEWSCONTENT = "url_get_newscontent";
    private static final String PROP_URL_GET_LIST_CANCELORDER = "url_get_list_cancelorder";
    private static final String PROP_URL_GET_DETAIL_CANCELORDER = "url_get_detail_cancelorder";
    private static final String PROP_URL_GET_CANCELORDER = "url_get_cancelorder";
    private static final String PROP_URL_SUBMIT_TRACK = "url_submit_track";
    private static final String PROP_URL_RETRIECECOLLECTIONTASK = "url_retriececollectiontask";
    private static final String PROP_URL_SYNCPARAM = "url_syncparam";
    private static final String PROP_URL_GET_REPORTSUMMARY = "url_get_reportsummary";
    private static final String PROP_URL_SUBMIT_RESCHEDULE = "url_submit_reschedule";
    private static final String PROP_URL_GET_LIST_USER = "url_get_list_user";
    private static final String PROP_URL_CHECK_UPDATE = "url_check_update";
    private static final String PROP_URL_GET_RECAPITULATE = "url_get_recapitulate";
    private final static String PROP_URL_GET_CODETRANSACTION="url_get_code_transaction";
    private static final String PROP_URL_SYNCPARAM_CONSTRAINT = "url_syncparam_constraint";
    private static final String PROP_URL_UPDATE_CASH_ON_HAND = "url_updatecashonhand";
    private static final String PROP_URL_GET_CLOSING_TASK = "url_get_closing_task";
    private static final String PROP_URL_SUBMIT_PRINT_COUNT = "url_submit_print_count";
    private static final String PROP_URL_GET_MKTPERFORMANCE = "url_get_mktperformance";
    private final static String PROP_URL_CHECK_VALIDATIONQUESTION = "url_check_validationquestion";
    private final static String PROP_URL_GET_DIGITAL_RECEIPT = "url_get_digitalreceipt";
    private final static String PROP_URL_GET_TEXT_ONLINE_ANSWER= "url_get_textonlineanswer";
    private final static String PROP_URL_GET_BANK_ACCOUNT_BRANCH= "url_bank_account_branch";
    private final static String PROP_URL_GET_PDF_DOCUMENT = "url_get_pdf_document";
    private final static String PROP_URL_GET_DOCUMENT_LIST = "url_get_document_list";
    private final static String PROP_URL_GET_FOLLOW_UP_LIST = "url_get_follow_up_list";
    private final static String PROP_URL_SUBMIT_FOLLOW_UP = "url_submit_follow_up";
    private final static String PROP_URL_GET_LU_ONLINE = "url_get_lu_online";
    private final static String PROP_URL_GET_DATA_QUESTION_BUTTON_TEXT = "url_get_Data_question_button_text";

    private static final  String PROP_URL_GET_DETAILKOMPETISI = "url_get_detailkompetisi";
    private static final String PROP_URL_GET_LOGOKOMPETISI = "url_get_logo";
    private static final String PROP_URL_START_VISIT = "url_startvisit";
    private static final String PROP_URL_CHANGE_PLAN = "url_changeplan";
    private static final String PROP_URL_SEND_UPDATE_NOTIFICATION = "url_send_update_notification";
    private static final String PROP_URL_SYNC_PARAM_SUCCESS = "url_sync_param_success";
//	-----------------------------------

    private static final String PROP_URL_GET_ACCOUNT = "url_get_account";
    private static final String PROP_URL_GET_CONTACT = "url_get_contact";
    private static final String PROP_URL_GET_OPPORTUNITY = "url_get_opportunity";
    private static final String PROP_URL_GET_OPPORTUNITY_DETAIL = "url_get_opportunity_detail";
    private static final String PROP_URL_SUBMITTASK_MMA = "url_submittask_mma";
    private static final String PROP_URL_GET_FOLLOWUP = "url_get_followup";
    private static final String PROP_URL_DO_FOLLOWUP = "url_do_followup";
    private static final String PROP_URL_GET_PRODUCT = "url_get_product";
    private static final String PROP_URL_GET_CATALOGUE_HEADER = "url_get_catalogue_header";
    private static final String PROP_URL_GET_CATALOGUE_PDF = "url_get_catalogue_pdf";
    private static final String PROP_URL_GET_CATALOGUE_PROMO = "url_get_catalogue_promo";
    private static final String PROP_URL_GET_PRODUCT_CONTACT = "url_get_product_contact";
    private static final String PROP_URL_GET_PRODUCT_DETAIL = "url_get_product_detail";
    private static final String PROP_URL_GET_PRODUCT_IMAGE = "url_get_product_image";
    private static final String PROP_URL_CHECK_ORDER_REJECTED = "url_check_order_rejected";
    private static final String PROP_URL_EMERGENCY = "url_emergency";
    private static final String PROP_URL_LAST_SYNC = "url_submitlastsync";

    private static final String PROP_URL_RV_NUMBER = "url_submit_rv_number";
    private static final String PROP_SYNC_RV_NUMBERS = "url_sync_rv_numbers";
    private final static String PROP_SYNC_PAYMENTCHANNEL = "url_sync_paymentchannel";
    private static final String PROP_URL_GET_DEALERS = "url_get_dealers";
    private static final String PROP_URL_GET_TASK_LOG = "url_get_task_log";
    private static final String PROP_URL_GET_LOOKUP_ANSWER = "url_get_lookup_answer";
    private final static String PROP_URL_REQUEST_NEW_TASK = "url_request_new_task";
    private final static String PROP_URL_ASSIGN_NEW_TASK = "url_assign_new_task";
    private static final String PROP_URL_CHECK_RESUBMIT = "url_check_resubmit";
    private static final String PROP_IS_SECURE_CONNECTION = "is_secure_connection";
    private static final String PROP_MAX_LOG = "max_log";
    private static final String PROP_LOG_AGE = "log_age";
    private static final String PROP_DEF_MAX_LENGHT = "default_max_lenght";
    private static final String PROP_THUMB_WIDTH = "thumb_width";
    private static final String PROP_THUMB_HEIGHT = "thumb_height";
    private static final String PROP_CAMERA_QUALITY = "camera_quality";
    private static final String PROP_MAX_PHOTO_SIZE = "max_photo_size";
    private static final String PROP_INTERVAL_TRACKING = "interval_tracking";
    private static final String PROP_INTERVAL_AUTOSEND = "interval_autosend";
    private static final String PROP_INTERVAL_GPS_TIMEOUT = "interval_gps_timeout";
    private static final String PROP_CHANGE_UUID = "change_uuid";
    private static final String PROP_UUID_DIVIDER = "divider_uuid";
    private static final String PROP_IS_BYPASSROOT = "is_bypassroot";
    /*Printer Device*/
    private static final String PROP_LIST_PRINTER = "printer_device";
    //CR external DB
    private static final String PROP_URL_SYNC_FILES = "url_sync_file";
    private static final String PROP_SAVE_PATH = "save_path";
    private static final String PROP_USE_EXTERNAL_STORAGE = "use_external_storage";
    private static final String PROP_DEVMODE = "is_developer_mode";

    //GAP DUKCAPIL
    private static final String PROP_URL_GET_DUKCAPIL_VALIDATION = "url_get_dukcapil_validation";
    private static final String PROP_URL_SUBMIT_DKCP = "url_submit_dkcp";
;
    private final static String PROP_URL_GET_BATCHID_LIST="url_get_batchid_list";
    private final static String PROP_URL_SUBMIT_DEPOSIT_PC="url_deposit_paymentchannel";

    //LOYALTY
    private String PROP_URL_LOYALTY_DETAIL_POINT = "url_loyalty_detail_point";

    //PLAN TASK
    private TodayPlanRepository todayPlanRepository;

    private static GlobalData sharedGlobalData;
    private static boolean requireRelogin = false;
    private boolean isDevEnabled;
    private boolean byPassDeveloper = false;
    protected String locale;

    private static boolean isNewTaskAvailable = false;
    private static int counterAssignment = 0;

    public int getCounterAssignment() {
        return counterAssignment;
    }

    public void setCounterAssignment(int counterAssignment) {
        this.counterAssignment = counterAssignment;
    }

    public static boolean isRequireRelogin() {
        return requireRelogin;
    }

    public static void setRequireRelogin(boolean requireRelogin) {
        GlobalData.requireRelogin = requireRelogin;

    }

    public boolean isDevEnabled() {
        return isDevEnabled;
    }

    public void setDevMode(boolean devEnabled) {
        isDevEnabled = devEnabled;
    }

    public boolean isByPassDeveloper() {
        return byPassDeveloper;
    }

    public void setByPassDeveloper(boolean byPassDeveloper) {
        this.byPassDeveloper = byPassDeveloper;
    }

    public static boolean isNewTaskAvailable() {
        return isNewTaskAvailable;
    }

    public static void setNewTaskAvailable(boolean newTaskAvailable) {
        isNewTaskAvailable = newTaskAvailable;
    }

    int keepTimelineInDays;
    int maxDataInLog;
    /**
     * Property URL_LOGIN
     */
    String URL_LOGIN;
    /**
     * Property URL_UPDATE_FCM
     */
    String URL_UPDATE_FCM;
    /**
     * Property URL_CHANGEPASSWORD
     */
    String URL_CHANGEPASSWORD;
    /**
     * Property URL_GET_ABSENSI
     */
    String URL_GET_ABSENSI;
    /**
     * Property URL_GET_TASKLIST
     */
    String URL_GET_TASKLIST;
    /**
     * Property URL_REFRESHTASK
     */
    String URL_REFRESHTASK;
    /**
     * Property URL_SUBMITTASK
     */
    String URL_SUBMITTASK;
    /**
     * Property URL_SUBMITOPENREADTASK
     */
    String URL_SUBMITOPENREADTASK;
    /**
     * Property URL_GET_QUESTIONSET
     */
    String URL_GET_QUESTIONSET;
    /**
     * Property URL_GET_VERIFICATION
     */
    String URL_GET_VERIFICATION;
    /**
     * Property URL_GET_IMAGE
     */
    String URL_GET_IMAGE;
    /**
     * Property URL_GET_SCHEME
     */
    String URL_GET_SCHEME;
    /**
     * Property URL_PRESUBMIT
     */
    String URL_PRESUBMIT;
    /**
     * Property URL_GET_SVYPERFORMANCE
     */
    String URL_GET_SVYPERFORMANCE;
    /**
     * Property URL_GET_LOOKUP
     */
    String URL_GET_LOOKUP;
    /**
     * Property URL_GET_LIST_REASSIGNMENT
     */
    String URL_GET_LIST_REASSIGNMENT;
    /**
     * Property URL_GET_DETAIL_REASSIGNMENT
     */
    String URL_GET_DETAIL_REASSIGNMENT;
    /**
     * Property URL_GET_LIST_ASSIGNMENT
     */
    String URL_GET_LIST_ASSIGNMENT;
    /**
     * Property URL_GET_DETAIL_ASSIGNMENT
     */
    String URL_GET_DETAIL_ASSIGNMENT;
    /**
     * Property URL_GET_DETAIL_ORDER
     */
    String URL_GET_DETAIL_ORDER;
    /**
     * Property URL_GET_DETAIL_TASK
     */
    String URL_GET_DETAIL_TASK;
    /**
     * Property URL_SUBMIT_ASSIGN
     */
    String URL_SUBMIT_ASSIGN;
    /**
     * Property URL_GET_LIST_VERIFICATION
     */
    String URL_GET_LIST_VERIFICATION;
    /**
     * Property URL_GET_LIST_APPROVAL
     */
    String URL_GET_LIST_APPROVAL;
    /**
     * Property URL_SENDDEPOSITREPORT
     */
    String URL_SENDDEPOSITREPORT;
    /**
     * Property URL_GET_PAYMENTHISTORY
     */
    String URL_GET_PAYMENTHISTORY;
    /**
     * Property URL_GET_INSTALLMENTSCHEDULE
     */
    String URL_GET_INSTALLMENTSCHEDULE;
//	-----------------------------------
    /**
     * Property URL_GET_COLLECTIONHISTORY
     */
    String URL_GET_COLLECTIONHISTORY;
    /**
     * Property URL_SUBMITVERIFICATIONTASK
     */
    String URL_SUBMITVERIFICATIONTASK;
    /**
     * Property URL_SUBMITAPPROVALTASK
     */
    String URL_SUBMITAPPROVALTASK;
    /**
     * Property URL_GET_CONTENTNEWS
     */
    String URL_GET_CONTENTNEWS;
    /**
     * Property URL_CHECKORDER
     */
    String URL_CHECKORDER;
    /**
     * Property URL_GET_TASK
     */
    String URL_GET_TASK;
    /**
     * Property URL_GET_NEWSHEADER
     */
    String URL_GET_NEWSHEADER;
    /**
     * Property URL_GET_NEWSCONTENT
     */
    String URL_GET_NEWSCONTENT;
    /**
     * Property URL_GET_LIST_CANCELORDER
     */
    String URL_GET_LIST_CANCELORDER;
    /**
     * Property URL_GET_DETAIL_CANCELORDER
     */
    String URL_GET_DETAIL_CANCELORDER;
    /**
     * Property URL_GET_CANCELORDER
     */
    String URL_GET_CANCELORDER;
    /**
     * Property URL_SUBMIT_TRACK
     */
    String URL_SUBMIT_TRACK;
    /**
     * Property URL_UPDATE_CASH_ON_HAND
     */
    String URL_UPDATE_CASH_ON_HAND;
    /**
     * Property URL_SYNC_RV_NUMBERS
     */
    String URL_SYNC_RV_NUMBERS;
    /** Property URL_SYNC_PAYMENTCHANNEL */
    String URL_SYNC_PAYMENTCHANNEL;

    String URL_SUBMIT_DEPOSIT_PC;

    String URL_GET_BATCHID_LIST;

    String URL_GET_CODETRANSACTION;

    String URL_GET_PDF_DOCUMENT;

    String URL_GET_DOCUMENT_LIST;

    String URL_GET_FOLLOW_UP_LIST;

    String URL_SUBMIT_FOLLOW_UP;

    String URL_GET_DATA_QUESTION_BUTTON_TEXT;

    /**
     * Property URL_GET_TASK_LOG
     */
    String URL_GET_TASK_LOG;
    /**
     * Property URL_GET_DEALERS
     */
    String URL_GET_DEALERS;
    /**
     * Property URL_GET_CLOSING_TASK
     */
    String URL_GET_CLOSING_TASK;
    /**
     * Property URL_SUBMIT_PRINT_COUNT
     */
    String URL_SUBMIT_PRINT_COUNT;
    /**
     * Property URL_RV_NUMBER
     */
    String URL_RV_NUMBER;
    /**
     * Property CHECK_RESUBMIT
     */
    String URL_CHECK_RESUBMIT;
    /**
     * Property GET_LOOKUP_ANSWER
     */
    String URL_GET_LOOKUP_ANSWER;
    /**
     * Property URL_RETRIECECOLLECTIONTASK
     */
    String URL_RETRIECECOLLECTIONTASK;
    /**
     * Property URL_SYNCPARAM
     */
    String URL_SYNCPARAM;
    /**
     * Property URL_GET_REPORTSUMMARY
     */
    String URL_GET_REPORTSUMMARY;
    /**
     * Property URL_SUBMIT_RESCHEDULE
     */
    String URL_SUBMIT_RESCHEDULE;
    String URL_GET_LIST_USER;
    /**
     * Property URL_CHECK_UPDATE
     */
    String URL_CHECK_UPDATE;
    String URL_GET_RECAPITULATE;
    String URL_SYNCPARAM_CONSTRAINT;
    String URL_UPLOAD_AVATAR;
    /**
     * Property URL_SUBMIT_DKCP
     */
    String URL_SUBMIT_DKCP;

    /**
     * Property URL_DUKCAPIL_VALIDATION
     */
    String URL_DUKCAPIL_VALIDATION;
    /**
     * Property URL_EMERGENCY
     */
    String URL_EMERGENCY;

    String URL_GET_DETAILKOMPETISI;

    String URL_GET_LOGOKOMPETISI;

    public String getURL_GET_LOGOKOMPETISI() {
        return URL_GET_LOGOKOMPETISI;
    }

    public void setURL_GET_LOGOKOMPETISI(String URL_GET_LOGOKOMPETISI) {
        this.URL_GET_LOGOKOMPETISI = URL_GET_LOGOKOMPETISI; }

    public String getURL_GET_DETAILKOMPETISI() {
        return URL_GET_DETAILKOMPETISI;
    }

    public void setURL_GET_DETAILKOMPETISI(String URL_GET_DETAILKOMPETISI) {
        this.URL_GET_DETAILKOMPETISI = URL_GET_DETAILKOMPETISI;
    }


    String URL_LOYALTY_DETAIL_POINT;

    String URL_LAST_SYNC;

    String URL_GET_BANK_ACCOUNT_BRANCH;

    public void setURL_GET_BANK_ACCOUNT_BRANCH(String URL_GET_BANK_ACCOUNT_BRANCH) {
        this.URL_GET_BANK_ACCOUNT_BRANCH = URL_GET_BANK_ACCOUNT_BRANCH;
    }
    public String getURL_GET_BANK_ACCOUNT_BRANCH() {
        return URL_GET_BANK_ACCOUNT_BRANCH;
    }


    String URL_SENDDEPOSITREPORTAC;
    /**
     * Gets the URL_SENDDEPOSITREPORTAC
     */
    public String getURL_SENDDEPOSITREPORTAC() {
        return this.URL_SENDDEPOSITREPORTAC;
    }

    /**
     * Sets the URL_SENDDEPOSITREPORTAC
     */
    public void setURL_SENDDEPOSITREPORTAC(String value) {
        this.URL_SENDDEPOSITREPORTAC = value;
    }

    int greenAccuracy;
    int yellowAccuracy;
    int maxAccuracySafely;
    private AuditDataType auditData;
    private String osName = "empty";
    private String deviceModel = "empty";
    private String imei = "empty";
    private String imei2 = "";
    private String androidId = "";
    private String imsi = "empty";

    /**
     * Property GET_ACCOUNT
     */
    String URL_GET_ACCOUNT;

    /**
     * Property GET_CONTACT
     */
    String URL_GET_CONTACT;

    /**
     * Property GET_OPPORTUNITY
     */
    String URL_GET_OPPORTUNITY;

    /**
     * Property GET_OPPORTUNITY_DETAIL
     */
    String URL_GET_OPPORTUNITY_DETAIL;

    /**
     * Property SUBMITTASK_MMA
     */
    String URL_SUBMITTASK_MMA;

    /**
     * Property GET_FOLLOWUP
     */
    String URL_GET_FOLLOWUP;

    /**
     * Property DO_FOLLOWUP
     */
    String URL_DO_FOLLOWUP;

    /**
     * Property GET_PRODUCT
     */
    String URL_GET_PRODUCT;

    /**
     * Property GET_MKT_PERFORMANCE
     */
    String URL_GET_MKTPERFORMANCE;

    /**
     * Property GET_CATALOGUE_HEADER
     */
    String URL_GET_CATALOGUE_HEADER;

    /**
     * Property GET_CATALOGUE_PDF
     */
    String URL_GET_CATALOGUE_PDF;

    /**
     * Property GET_CATALOGUE_PROMO
     */
    String URL_GET_CATALOGUE_PROMO;

    /**
     * Property GET_PRODUCT_CONTACT
     */
    String URL_GET_PRODUCT_CONTACT;

    /**
     * Property GET_PRODUCT_DETAIL
     */
    String URL_GET_PRODUCT_DETAIL;

    /**
     * Property GET_PRODUCT_IMAGE
     */
    String URL_GET_PRODUCT_IMAGE;

    /**
     * Property CHECK_ORDER_REJECTED
     */
    String URL_CHECK_ORDER_REJECTED;

    String URL_START_VISIT_PLAN;

    String URL_CHANGE_PLAN;

    /** Property URL_CHECK_VALIDATIONQUESTION */
    String URL_CHECK_VALIDATIONQUESTION;

    /** Property URL_GET_DIGITAL_RECEIPT */
    String URL_GET_DIGITAL_RECEIPT;

    String URL_GET_TEXT_ONLINE_ANSWER;


    String URL_GET_Document_Pdf;


    /** Property URL_GET_LU_ONLINE */
    String URL_GET_LU_ONLINE;

    /** Property URL_GET_RECEIPT_HISTORY */
    String URL_GET_RECEIPT_HISTORY;

    /** Property URL_GET_RECEIPT_HISTORY_PDF */
    String URL_GET_RECEIPT_HISTORY_PDF;

    /** Property URL_SEND_UPDATE_NOTIFICATION */
    String URL_SEND_UPDATE_NOTIFICATION;

    /** Property URL_SYNC_PARAM_SUCCESS */
    String URL_SYNC_PARAM_SUCCESS;

    //=== URL ===//
    private String urlMain;
    private String urlSync;
    private String urlTracking;
    private String urlPersonalization;
    private String savePath;
    private Boolean useExternalStorage;
    private String urlSyncFiles;
    //=== Security ===//
    private boolean encrypt;
    private boolean decrypt;
    private boolean isSecureConnection;
    private boolean isRequiresAccessToken;
    private String clientId;
    private OAuth2Client oAuth2Client;
    private Token token;
    private ConnectionCryptor connectionCryptor;
    //=== Format ===//
    private String application;
    private int maxLog;
    private int logAge;
    private int defMaxLenght;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private String cameraQuality;
    private String currencyType;
    private boolean isPartialSending;
    private int maxPhotoSize;            //in bytes
    //=== Background Processes ===//
    private AutoSendSerivce autoSendService;
    private LocationTrackingSchedule locationTrackingSchedule;
    private int intervalTracking = -1;
    private int intervalAutoSend = -1;
    private int intervalGPSTimeout = -1;
    //=== Application State and Data ===//
    private User user;
    private Boolean isDoingTask = false;

    public Boolean getButtonClicked() {
        return isButtonClicked;
    }

    public void setButtonClicked(Boolean buttonClicked) {
        isButtonClicked = buttonClicked;
    }

    private Boolean isButtonClicked = false;
    //=== tenant ===//
    private String tenant = "";
    private String listPrinter;
    private boolean uuidChange;
    private String uuidDivider;
    private boolean useOwnCamera;
    private FirebaseRemoteConfig remoteConfig;

    public GlobalData() {
        //EMPTY
    }

    public static synchronized GlobalData getSharedGlobalData() {
        if (sharedGlobalData == null) {
            sharedGlobalData = new GlobalData();
        }
        return sharedGlobalData;
    }

    public FirebaseRemoteConfig getRemoteConfig() {
        return remoteConfig;
    }

    public void setRemoteConfig(FirebaseRemoteConfig remoteConfig) {
        this.remoteConfig = remoteConfig;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public Boolean getUseExternalStorage() {
        return useExternalStorage;
    }

    public void setUseExternalStorage(Boolean useExternalStorage) {
        this.useExternalStorage = useExternalStorage;
    }

    public String getUrlSyncFiles() {
        return urlSyncFiles;
    }

    public void setUrlSyncFiles(String urlSyncFiles) {
        this.urlSyncFiles = urlSyncFiles;
    }

    public boolean isRequiresAccessToken() {
        return isRequiresAccessToken;
    }

    public void setRequiresAccessToken(boolean requiresAccessToken) {
        isRequiresAccessToken = requiresAccessToken;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public OAuth2Client getoAuth2Client() {
        return oAuth2Client;
    }

    public void setoAuth2Client(User user) {
        String urlService = GlobalData.getSharedGlobalData().getUrlMain();
        int idx = urlService.indexOf("/services");
        String urlMain = urlService.substring(0, idx);
        OAuth2Client client = new OAuth2Client(user.getLogin_id(), user.getPassword(), GlobalData.getSharedGlobalData().getClientId(), null, urlMain);
        this.oAuth2Client = client;
    }

    public void setoAuth2Client(OAuth2Client oAuth2Client) {
        this.oAuth2Client = oAuth2Client;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getListPrinter() {
        return listPrinter;
    }

    public void setListPrinter(String listPrinter) {
        this.listPrinter = listPrinter;
    }

    /**
     * reload url after url main has been set from serverLinkActivity
     *
     * @param context
     */
    public void reloadUrl(Context context) {
        Properties prop = ConfigFileReader.propertiesFromFile(context, PROPERTY_FILENAME);

        setUrlSync(urlMain + prop.getProperty(PROP_URL_SERV_SYNC, ""));
        setUrlTracking(urlMain + prop.getProperty(PROP_URL_SERV_TRACKING, ""));
        setUrlPersonalization(urlMain + prop.getProperty(PROP_URL_SERV_PERSONALIZATION, ""));
        setURL_LOGIN(urlMain + prop.getProperty(PROP_URL_LOGIN, ""));
        setUrlUploadAvatar(urlMain + prop.getProperty(PROP_URL_UPLOAD_AVATAR, ""));
        setURL_UPDATE_FCM(urlMain + prop.getProperty(PROP_URL_UPDATE_FCM, ""));
        setUrlPersonalization(urlMain + prop.getProperty(PROP_URL_SERV_PERSONALIZATION, ""));
        setURL_CHANGEPASSWORD(urlMain + prop.getProperty(PROP_URL_CHANGEPASSWORD, ""));
        setURL_GET_ABSENSI(urlMain + prop.getProperty(PROP_URL_GET_ABSENSI, ""));
        setURL_GET_TASKLIST(urlMain + prop.getProperty(PROP_URL_GET_TASKLIST, ""));
        setURL_REFRESHTASK(urlMain + prop.getProperty(PROP_URL_REFRESHTASK, ""));
        setURL_SUBMITTASK(urlMain + prop.getProperty(PROP_URL_SUBMITTASK, ""));
        setURL_SUBMITOPENREADTASK(urlMain + prop.getProperty(PROP_URL_SUBMITOPENREADTASK, ""));
        setURL_GET_QUESTIONSET(urlMain + prop.getProperty(PROP_URL_GET_QUESTIONSET, ""));
        setURL_GET_VERIFICATION(urlMain + prop.getProperty(PROP_URL_GET_VERIFICATION, ""));
        setURL_GET_IMAGE(urlMain + prop.getProperty(PROP_URL_GET_IMAGE, ""));
        setURL_GET_SCHEME(urlMain + prop.getProperty(PROP_URL_GET_SCHEME, ""));
        setURL_GET_SVYPERFORMANCE(urlMain + prop.getProperty(PROP_URL_GET_SVYPERFORMANCE, ""));
        setURL_GET_LOOKUP(urlMain + prop.getProperty(PROP_URL_GET_LOOKUP, ""));
        setURL_GET_LIST_REASSIGNMENT(urlMain + prop.getProperty(PROP_URL_GET_LIST_REASSIGNMENT, ""));
        setURL_GET_DETAIL_REASSIGNMENT(urlMain + prop.getProperty(PROP_URL_GET_DETAIL_REASSIGNMENT, ""));
        setURL_GET_LIST_ASSIGNMENT(urlMain + prop.getProperty(PROP_URL_GET_LIST_ASSIGNMENT, ""));
        setURL_GET_DETAIL_ASSIGNMENT(urlMain + prop.getProperty(PROP_URL_GET_DETAIL_ASSIGNMENT, ""));
        setURL_GET_DETAIL_ORDER(urlMain + prop.getProperty(PROP_URL_GET_DETAIL_ORDER, ""));
        setURL_GET_DETAIL_TASK(urlMain + prop.getProperty(PROP_URL_GET_DETAIL_TASK, ""));
        setURL_SUBMIT_ASSIGN(urlMain + prop.getProperty(PROP_URL_SUBMIT_ASSIGN, ""));
        setURL_GET_LIST_VERIFICATION(urlMain + prop.getProperty(PROP_URL_GET_LIST_VERIFICATION, ""));
        setURL_GET_LIST_APPROVAL(urlMain + prop.getProperty(PROP_URL_GET_LIST_APPROVAL, ""));
        setURL_SENDDEPOSITREPORT(urlMain + prop.getProperty(PROP_URL_SENDDEPOSITREPORT, ""));
        setURL_SENDDEPOSITREPORTAC(urlMain + prop.getProperty(PROP_URL_SENDDEPOSITREPORTAC, ""));
        setURL_GET_PAYMENTHISTORY(urlMain + prop.getProperty(PROP_URL_GET_PAYMENTHISTORY, ""));
        setURL_GET_INSTALLMENTSCHEDULE(urlMain + prop.getProperty(PROP_URL_GET_INSTALLMENTSCHEDULE, ""));
        setURL_GET_COLLECTIONHISTORY(urlMain + prop.getProperty(PROP_URL_GET_COLLECTIONHISTORY, ""));
        setURL_SUBMITVERIFICATIONTASK(urlMain + prop.getProperty(PROP_URL_SUBMITVERIFICATIONTASK, ""));
        setURL_SUBMITAPPROVALTASK(urlMain + prop.getProperty(PROP_URL_SUBMITAPPROVALTASK, ""));
        setURL_GET_CONTENTNEWS(urlMain + prop.getProperty(PROP_URL_GET_CONTENTNEWS, ""));
        setURL_CHECKORDER(urlMain + prop.getProperty(PROP_URL_CHECKORDER, ""));
        setURL_GET_TASK(urlMain + prop.getProperty(PROP_URL_GET_TASK, ""));
        setURL_GET_NEWSHEADER(urlMain + prop.getProperty(PROP_URL_GET_NEWSHEADER, ""));
        setURL_GET_NEWSCONTENT(urlMain + prop.getProperty(PROP_URL_GET_NEWSCONTENT, ""));
        setURL_GET_LIST_CANCELORDER(urlMain + prop.getProperty(PROP_URL_GET_LIST_CANCELORDER, ""));
        setURL_GET_DETAIL_CANCELORDER(urlMain + prop.getProperty(PROP_URL_GET_DETAIL_CANCELORDER, ""));
        setURL_GET_CANCELORDER(urlMain + prop.getProperty(PROP_URL_GET_CANCELORDER, ""));
        setURL_SUBMIT_TRACK(urlMain + prop.getProperty(PROP_URL_SUBMIT_TRACK, ""));
        setURL_RETRIECECOLLECTIONTASK(urlMain + prop.getProperty(PROP_URL_RETRIECECOLLECTIONTASK, ""));
        setURL_SYNCPARAM(urlMain + prop.getProperty(PROP_URL_SYNCPARAM, ""));
        setURL_GET_REPORTSUMMARY(urlMain + prop.getProperty(PROP_URL_GET_REPORTSUMMARY, ""));
        setURL_SUBMIT_RESCHEDULE(urlMain + prop.getProperty(PROP_URL_SUBMIT_RESCHEDULE, ""));
        setURL_GET_LIST_USER(urlMain + prop.getProperty(PROP_URL_GET_LIST_USER, ""));
        setURL_CHECK_UPDATE(urlMain + prop.getProperty(PROP_URL_CHECK_UPDATE, ""));
        setURL_SYNCPARAM_CONSTRAINT(urlMain + prop.getProperty(PROP_URL_SYNCPARAM_CONSTRAINT, ""));
        setURL_UPDATE_CASH_ON_HAND(urlMain + prop.getProperty(PROP_URL_UPDATE_CASH_ON_HAND, ""));
        setURL_CLOSING_TASK(urlMain + prop.getProperty(PROP_URL_GET_CLOSING_TASK, ""));
        setURL_SUBMIT_PRINT_COUNT(urlMain + prop.getProperty(PROP_URL_SUBMIT_PRINT_COUNT, ""));
        setURL_RV_NUMBER(urlMain + prop.getProperty(PROP_URL_RV_NUMBER, ""));
        setURL_SYNC_RV_NUMBERS(urlMain + prop.getProperty(PROP_SYNC_RV_NUMBERS, ""));
        setURL_GET_RECAPITULATE(urlMain + prop.getProperty(PROP_URL_GET_RECAPITULATE, ""));
        setURL_GET_CODETRANSACTION(urlMain + prop.getProperty(PROP_URL_GET_CODETRANSACTION, ""));
        setURL_SYNC_PAYMENTCHANNEL(urlMain + prop.getProperty(PROP_SYNC_PAYMENTCHANNEL, ""));
        setURL_GET_DEALERS(urlMain + prop.getProperty(PROP_URL_GET_DEALERS, ""));
        setURL_GET_TASK_LOG(urlMain + prop.getProperty(PROP_URL_GET_TASK_LOG, ""));
        setURL_LOOKUP_ANSWER(urlMain + prop.getProperty(PROP_URL_GET_LOOKUP_ANSWER, ""));
        setURL_REQUEST_NEW_TASK(urlMain + prop.getProperty(PROP_URL_REQUEST_NEW_TASK, ""));
        setURL_ASSIGN_NEW_TASK(urlMain + prop.getProperty(PROP_URL_ASSIGN_NEW_TASK, ""));
        setURL_CHECK_RESUBMIT(urlMain + prop.getProperty(PROP_URL_CHECK_RESUBMIT, ""));
        setURL_GET_BATCHID_LIST(urlMain + prop.getProperty(PROP_URL_GET_BATCHID_LIST, ""));
        setURL_SUBMIT_DEPOSIT_PC(urlMain + prop.getProperty(PROP_URL_SUBMIT_DEPOSIT_PC, ""));
        setURL_SUBMIT_DKCP(urlMain + prop.getProperty(PROP_URL_SUBMIT_DKCP,""));
        setURL_DUKCAPIL_VALIDATION(urlMain + prop.getProperty(PROP_URL_GET_DUKCAPIL_VALIDATION, ""));
        setURL_GET_MKTPERFORMANCE(urlMain + prop.getProperty(PROP_URL_GET_MKTPERFORMANCE, ""));
        setURL_GET_ACCOUNT(urlMain + prop.getProperty(PROP_URL_GET_ACCOUNT, ""));
        setURL_GET_CONTACT(urlMain + prop.getProperty(PROP_URL_GET_CONTACT, ""));
        setURL_GET_OPPORTUNITY(urlMain + prop.getProperty(PROP_URL_GET_OPPORTUNITY, ""));
        setURL_GET_OPPORTUNITY_DETAIL(urlMain + prop.getProperty(PROP_URL_GET_OPPORTUNITY_DETAIL, ""));
        setURL_SUBMITTASK_MMA(urlMain + prop.getProperty(PROP_URL_SUBMITTASK_MMA, ""));
        setURL_GET_FOLLOWUP(urlMain + prop.getProperty(PROP_URL_GET_FOLLOWUP, ""));
        setURL_DO_FOLLOWUP(urlMain + prop.getProperty(PROP_URL_DO_FOLLOWUP, ""));
        setURL_GET_PRODUCT(urlMain + prop.getProperty(PROP_URL_GET_PRODUCT, ""));
        setURL_GET_CATALOGUE_HEADER(urlMain + prop.getProperty(PROP_URL_GET_CATALOGUE_HEADER, ""));
        setURL_GET_CATALOGUE_PDF(urlMain + prop.getProperty(PROP_URL_GET_CATALOGUE_PDF, ""));
        setURL_GET_CATALOGUE_PROMO(urlMain + prop.getProperty(PROP_URL_GET_CATALOGUE_PROMO, ""));
        setURL_GET_PRODUCT_CONTACT(urlMain + prop.getProperty(PROP_URL_GET_PRODUCT_CONTACT, ""));
        setURL_GET_PRODUCT_DETAIL(urlMain + prop.getProperty(PROP_URL_GET_PRODUCT_DETAIL, ""));
        setURL_GET_PRODUCT_IMAGE(urlMain + prop.getProperty(PROP_URL_GET_PRODUCT_IMAGE, ""));
        setURL_CHECK_ORDER_REJECTED(urlMain + prop.getProperty(PROP_URL_CHECK_ORDER_REJECTED, ""));
        setURL_EMERGENCY(urlMain + prop.getProperty(PROP_URL_EMERGENCY, ""));
        setURL_LOYALTY_DETAIL_POINT(urlMain + prop.getProperty(PROP_URL_LOYALTY_DETAIL_POINT,""));
        setURL_GET_DETAILKOMPETISI(urlMain + prop.getProperty(PROP_URL_GET_DETAILKOMPETISI, ""));
        setURL_LAST_SYNC(urlMain + prop.getProperty(PROP_URL_LAST_SYNC, ""));
        setURL_GET_LOGOKOMPETISI(urlMain + prop.getProperty(PROP_URL_GET_LOGOKOMPETISI, ""));
        setURL_START_VISIT_PLAN(urlMain + prop.getProperty(PROP_URL_START_VISIT,""));
        setURL_CHANGE_PLAN(urlMain + prop.getProperty(PROP_URL_CHANGE_PLAN,""));
        setURL_GET_BANK_ACCOUNT_BRANCH(urlMain + prop.getProperty(PROP_URL_GET_BANK_ACCOUNT_BRANCH, ""));
        setURL_CHECK_VALIDATIONQUESTION(urlMain + prop.getProperty(PROP_URL_CHECK_VALIDATIONQUESTION, ""));
        setURL_GET_DIGITAL_RECEIPT(urlMain + prop.getProperty(PROP_URL_GET_DIGITAL_RECEIPT, ""));
        setURL_GET_TEXT_ONLINE(urlMain + prop.getProperty(PROP_URL_GET_TEXT_ONLINE_ANSWER, ""));
        setURL_GET_DOCUMENT_LIST(urlMain + prop.getProperty(PROP_URL_GET_DOCUMENT_LIST, ""));
        setURL_GET_FOLLOW_UP_LIST(urlMain + prop.getProperty(PROP_URL_GET_FOLLOW_UP_LIST, ""));
        setURL_SUBMIT_FOLLOW_UP(urlMain + prop.getProperty(PROP_URL_SUBMIT_FOLLOW_UP, ""));
        setURL_GET_LU_ONLINE(urlMain + prop.getProperty(PROP_URL_GET_LU_ONLINE, ""));
        setURL_GET_RECEIPT_HISTORY(urlMain + prop.getProperty(PROP_URL_GET_RECEIPT_HISTORY, ""));
        setURL_GET_RECEIPT_HISTORY_PDF(urlMain + prop.getProperty(PROP_URL_GET_RECEIPT_HISTORY_PDF, ""));
        setURL_GET_DATA_QUESTION_BUTTON_TEXT(urlMain + prop.getProperty(PROP_URL_GET_DATA_QUESTION_BUTTON_TEXT, ""));
        setURL_SEND_UPDATE_NOTIFICATION(urlMain + prop.getProperty(PROP_URL_SEND_UPDATE_NOTIFICATION, ""));
        setURL_SYNC_PARAM_SUCCESS(urlMain + prop.getProperty(PROP_URL_SYNC_PARAM_SUCCESS, ""));

        //CR External DB Embed
        setUrlSyncFiles(urlMain + prop.getProperty(PROP_URL_SYNC_FILES, ""));
        setUseExternalStorage(Boolean.parseBoolean(prop.getProperty(PROP_USE_EXTERNAL_STORAGE, "")));
        if (Boolean.TRUE.equals(getUseExternalStorage())) {
            setSavePath(context.getExternalFilesDir(null) + prop.getProperty(PROP_SAVE_PATH, ""));
        } else
            setSavePath(context.getFilesDir() + prop.getProperty(PROP_SAVE_PATH, ""));

        try {
            String link = urlMain;
            String[] index = link.split("://");
            if (index != null) {
                if (index[0].equals(http)) {
                    setSecureConnection(false);
                } else if (index[0].equals(https)) {
                    setSecureConnection(true);
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);

        }
    }

    /**
     * Load values from .properties.
     * <br>Place the implementation of fetching value and storing to local property here
     *
     * @param context
     */
    public void loadFromProperties(Context context) {
        Properties prop = ConfigFileReader.propertiesFromFile(context, PROPERTY_FILENAME);
        urlMain = prop.getProperty(PROP_URL_MAIN, "");

        setDevMode(Boolean.parseBoolean(prop.getProperty(PROP_DEVMODE, "false")));
        setUrlSync(urlMain + prop.getProperty(PROP_URL_SERV_SYNC, ""));
        setUrlTracking(urlMain + prop.getProperty(PROP_URL_SERV_TRACKING, ""));
        setUrlPersonalization(urlMain + prop.getProperty(PROP_URL_SERV_PERSONALIZATION, ""));
        setURL_LOGIN(urlMain + prop.getProperty(PROP_URL_LOGIN, ""));
        setURL_UPDATE_FCM(urlMain + prop.getProperty(PROP_URL_UPDATE_FCM, ""));
        setUrlPersonalization(urlMain + prop.getProperty(PROP_URL_SERV_PERSONALIZATION, ""));
        setURL_CHANGEPASSWORD(urlMain + prop.getProperty(PROP_URL_CHANGEPASSWORD, ""));
        setURL_GET_ABSENSI(urlMain + prop.getProperty(PROP_URL_GET_ABSENSI, ""));
        setURL_GET_TASKLIST(urlMain + prop.getProperty(PROP_URL_GET_TASKLIST, ""));
        setURL_REFRESHTASK(urlMain + prop.getProperty(PROP_URL_REFRESHTASK, ""));
        setURL_SUBMITTASK(urlMain + prop.getProperty(PROP_URL_SUBMITTASK, ""));
        setURL_SUBMITOPENREADTASK(urlMain + prop.getProperty(PROP_URL_SUBMITOPENREADTASK, ""));
        setURL_GET_QUESTIONSET(urlMain + prop.getProperty(PROP_URL_GET_QUESTIONSET, ""));
        setURL_GET_VERIFICATION(urlMain + prop.getProperty(PROP_URL_GET_VERIFICATION, ""));
        setURL_GET_IMAGE(urlMain + prop.getProperty(PROP_URL_GET_IMAGE, ""));
        setURL_GET_SCHEME(urlMain + prop.getProperty(PROP_URL_GET_SCHEME, ""));
        setURL_GET_SVYPERFORMANCE(urlMain + prop.getProperty(PROP_URL_GET_SVYPERFORMANCE, ""));
        setURL_GET_LOOKUP(urlMain + prop.getProperty(PROP_URL_GET_LOOKUP, ""));
        setURL_GET_LIST_REASSIGNMENT(urlMain + prop.getProperty(PROP_URL_GET_LIST_REASSIGNMENT, ""));
        setURL_GET_DETAIL_REASSIGNMENT(urlMain + prop.getProperty(PROP_URL_GET_DETAIL_REASSIGNMENT, ""));
        setUrlUploadAvatar(urlMain + prop.getProperty(PROP_URL_UPLOAD_AVATAR, ""));
        setURL_GET_LIST_ASSIGNMENT(urlMain + prop.getProperty(PROP_URL_GET_LIST_ASSIGNMENT, ""));
        setURL_GET_DETAIL_ASSIGNMENT(urlMain + prop.getProperty(PROP_URL_GET_DETAIL_ASSIGNMENT, ""));
        setURL_GET_DETAIL_ORDER(urlMain + prop.getProperty(PROP_URL_GET_DETAIL_ORDER, ""));
        setURL_SUBMIT_ASSIGN(urlMain + prop.getProperty(PROP_URL_SUBMIT_ASSIGN, ""));
        setURL_GET_LIST_VERIFICATION(urlMain + prop.getProperty(PROP_URL_GET_LIST_VERIFICATION, ""));
        setURL_GET_LIST_APPROVAL(urlMain + prop.getProperty(PROP_URL_GET_LIST_APPROVAL, ""));
        setURL_SENDDEPOSITREPORT(urlMain + prop.getProperty(PROP_URL_SENDDEPOSITREPORT, ""));
        setURL_SENDDEPOSITREPORTAC(urlMain + prop.getProperty(PROP_URL_SENDDEPOSITREPORTAC, ""));
        setURL_GET_PAYMENTHISTORY(urlMain + prop.getProperty(PROP_URL_GET_PAYMENTHISTORY, ""));
        setURL_GET_INSTALLMENTSCHEDULE(urlMain + prop.getProperty(PROP_URL_GET_INSTALLMENTSCHEDULE, ""));
        setURL_GET_COLLECTIONHISTORY(urlMain + prop.getProperty(PROP_URL_GET_COLLECTIONHISTORY, ""));
        setURL_SUBMITVERIFICATIONTASK(urlMain + prop.getProperty(PROP_URL_SUBMITVERIFICATIONTASK, ""));
        setURL_SUBMITAPPROVALTASK(urlMain + prop.getProperty(PROP_URL_SUBMITAPPROVALTASK, ""));
        setURL_GET_CONTENTNEWS(urlMain + prop.getProperty(PROP_URL_GET_CONTENTNEWS, ""));
        setURL_CHECKORDER(urlMain + prop.getProperty(PROP_URL_CHECKORDER, ""));
        setURL_GET_TASK(urlMain + prop.getProperty(PROP_URL_GET_TASK, ""));
        setURL_GET_NEWSHEADER(urlMain + prop.getProperty(PROP_URL_GET_NEWSHEADER, ""));
        setURL_GET_NEWSCONTENT(urlMain + prop.getProperty(PROP_URL_GET_NEWSCONTENT, ""));
        setURL_GET_LIST_CANCELORDER(urlMain + prop.getProperty(PROP_URL_GET_LIST_CANCELORDER, ""));
        setURL_GET_DETAIL_CANCELORDER(urlMain + prop.getProperty(PROP_URL_GET_DETAIL_CANCELORDER, ""));
        setURL_GET_CANCELORDER(urlMain + prop.getProperty(PROP_URL_GET_CANCELORDER, ""));
        setURL_SUBMIT_TRACK(urlMain + prop.getProperty(PROP_URL_SUBMIT_TRACK, ""));
        setURL_RETRIECECOLLECTIONTASK(urlMain + prop.getProperty(PROP_URL_RETRIECECOLLECTIONTASK, ""));
        setURL_SYNCPARAM(urlMain + prop.getProperty(PROP_URL_SYNCPARAM, ""));
        setURL_GET_REPORTSUMMARY(urlMain + prop.getProperty(PROP_URL_GET_REPORTSUMMARY, ""));
        setURL_SUBMIT_RESCHEDULE(urlMain + prop.getProperty(PROP_URL_SUBMIT_RESCHEDULE, ""));
        setURL_GET_LIST_USER(urlMain + prop.getProperty(PROP_URL_GET_LIST_USER, ""));
        setURL_CHECK_UPDATE(urlMain + prop.getProperty(PROP_URL_CHECK_UPDATE, ""));
        setURL_SYNCPARAM_CONSTRAINT(urlMain + prop.getProperty(PROP_URL_SYNCPARAM_CONSTRAINT, ""));
        setURL_UPDATE_CASH_ON_HAND(urlMain + prop.getProperty(PROP_URL_UPDATE_CASH_ON_HAND, ""));
        setURL_CLOSING_TASK(urlMain + prop.getProperty(PROP_URL_GET_CLOSING_TASK, ""));
        setURL_SUBMIT_PRINT_COUNT(urlMain + prop.getProperty(PROP_URL_SUBMIT_PRINT_COUNT, ""));
        setURL_RV_NUMBER(urlMain + prop.getProperty(PROP_URL_RV_NUMBER, ""));
        setURL_SYNC_RV_NUMBERS(urlMain + prop.getProperty(PROP_SYNC_RV_NUMBERS, ""));
        setURL_GET_RECAPITULATE(urlMain + prop.getProperty(PROP_URL_GET_RECAPITULATE, ""));
        setURL_GET_CODETRANSACTION(urlMain + prop.getProperty(PROP_URL_GET_CODETRANSACTION, ""));
        setURL_SYNC_PAYMENTCHANNEL(urlMain + prop.getProperty(PROP_SYNC_PAYMENTCHANNEL, ""));
        setURL_GET_DEALERS(urlMain + prop.getProperty(PROP_URL_GET_DEALERS, ""));
        setURL_GET_TASK_LOG(urlMain + prop.getProperty(PROP_URL_GET_TASK_LOG, ""));
        setURL_LOOKUP_ANSWER(urlMain + prop.getProperty(PROP_URL_GET_LOOKUP_ANSWER, ""));
        setURL_CHECK_RESUBMIT(urlMain + prop.getProperty(PROP_URL_CHECK_RESUBMIT, ""));
        setURL_GET_BATCHID_LIST(urlMain + prop.getProperty(PROP_URL_GET_BATCHID_LIST, ""));
        setURL_SUBMIT_DEPOSIT_PC(urlMain + prop.getProperty(PROP_URL_SUBMIT_DEPOSIT_PC, ""));
        setURL_GET_MKTPERFORMANCE(urlMain + prop.getProperty(PROP_URL_GET_MKTPERFORMANCE, ""));
        setURL_GET_ACCOUNT(urlMain + prop.getProperty(PROP_URL_GET_ACCOUNT, ""));
        setURL_GET_CONTACT(urlMain + prop.getProperty(PROP_URL_GET_CONTACT, ""));
        setURL_GET_OPPORTUNITY(urlMain + prop.getProperty(PROP_URL_GET_OPPORTUNITY, ""));
        setURL_GET_OPPORTUNITY_DETAIL(urlMain + prop.getProperty(PROP_URL_GET_OPPORTUNITY_DETAIL, ""));
        setURL_SUBMITTASK_MMA(urlMain + prop.getProperty(PROP_URL_SUBMITTASK_MMA, ""));
        setURL_GET_FOLLOWUP(urlMain + prop.getProperty(PROP_URL_GET_FOLLOWUP, ""));
        setURL_DO_FOLLOWUP(urlMain + prop.getProperty(PROP_URL_DO_FOLLOWUP, ""));
        setURL_GET_CATALOGUE_HEADER(urlMain + prop.getProperty(PROP_URL_GET_CATALOGUE_HEADER, ""));
        setURL_GET_CATALOGUE_PDF(urlMain + prop.getProperty(PROP_URL_GET_CATALOGUE_PDF, ""));
        setURL_GET_CATALOGUE_PROMO(urlMain + prop.getProperty(PROP_URL_GET_CATALOGUE_PROMO, ""));
        setURL_GET_PRODUCT_DETAIL(urlMain + prop.getProperty(PROP_URL_GET_PRODUCT_DETAIL, ""));
        setURL_GET_PRODUCT_CONTACT(urlMain + prop.getProperty(PROP_URL_GET_PRODUCT_CONTACT, ""));
        setURL_GET_PRODUCT_IMAGE(urlMain + prop.getProperty(PROP_URL_GET_PRODUCT_IMAGE, ""));
        setURL_CHECK_ORDER_REJECTED(urlMain + prop.getProperty(PROP_URL_CHECK_ORDER_REJECTED, ""));
        setURL_SUBMIT_DKCP(urlMain + prop.getProperty(PROP_URL_SUBMIT_DKCP,""));
        setURL_DUKCAPIL_VALIDATION(urlMain + prop.getProperty(PROP_URL_GET_DUKCAPIL_VALIDATION, ""));
        setURL_EMERGENCY(urlMain + prop.getProperty(PROP_URL_EMERGENCY, ""));
        setURL_GET_DETAILKOMPETISI(urlMain + prop.getProperty(PROP_URL_GET_DETAILKOMPETISI, ""));
        setURL_LAST_SYNC(urlMain + prop.getProperty(PROP_URL_LAST_SYNC, ""));
        setURL_GET_LOGOKOMPETISI(urlMain + prop.getProperty(PROP_URL_GET_LOGOKOMPETISI, ""));
        setURL_START_VISIT_PLAN(urlMain + prop.getProperty(PROP_URL_START_VISIT,""));
        setURL_CHANGE_PLAN(urlMain + prop.getProperty(PROP_URL_CHANGE_PLAN,""));
        setURL_LOYALTY_DETAIL_POINT(urlMain + prop.getProperty(PROP_URL_LOYALTY_DETAIL_POINT,""));
        setURL_GET_BANK_ACCOUNT_BRANCH(urlMain + prop.getProperty(PROP_URL_GET_BANK_ACCOUNT_BRANCH, ""));
        setURL_CHECK_VALIDATIONQUESTION(urlMain + prop.getProperty(PROP_URL_CHECK_VALIDATIONQUESTION, ""));
        setURL_GET_DIGITAL_RECEIPT(urlMain + prop.getProperty(PROP_URL_GET_DIGITAL_RECEIPT, ""));
        setURL_GET_TEXT_ONLINE(urlMain + prop.getProperty(PROP_URL_GET_TEXT_ONLINE_ANSWER, ""));
        setURL_GET_BANK_ACCOUNT_BRANCH(urlMain + prop.getProperty(PROP_URL_GET_BANK_ACCOUNT_BRANCH, ""));
        setURL_GET_PDF_DOCUMENT(urlMain + prop.getProperty(PROP_URL_GET_PDF_DOCUMENT, ""));
        setURL_GET_DOCUMENT_LIST(urlMain + prop.getProperty(PROP_URL_GET_DOCUMENT_LIST, ""));
        setURL_GET_FOLLOW_UP_LIST(urlMain + prop.getProperty(PROP_URL_GET_FOLLOW_UP_LIST, ""));
        setURL_SUBMIT_FOLLOW_UP(urlMain + prop.getProperty(PROP_URL_SUBMIT_FOLLOW_UP, ""));
        setURL_GET_LU_ONLINE(urlMain + prop.getProperty(PROP_URL_GET_LU_ONLINE, ""));
        setURL_GET_RECEIPT_HISTORY(urlMain + prop.getProperty(PROP_URL_GET_RECEIPT_HISTORY, ""));
        setURL_GET_RECEIPT_HISTORY_PDF(urlMain + prop.getProperty(PROP_URL_GET_RECEIPT_HISTORY_PDF, ""));
        setURL_GET_DATA_QUESTION_BUTTON_TEXT(urlMain + prop.getProperty(PROP_URL_GET_DATA_QUESTION_BUTTON_TEXT, ""));
        setURL_SEND_UPDATE_NOTIFICATION(urlMain + prop.getProperty(PROP_URL_SEND_UPDATE_NOTIFICATION, ""));
        setURL_SYNC_PARAM_SUCCESS(urlMain + prop.getProperty(PROP_URL_SYNC_PARAM_SUCCESS, ""));

        //CR External DB Embed
        setUseExternalStorage(Boolean.parseBoolean(prop.getProperty(PROP_USE_EXTERNAL_STORAGE)));
        setUrlSyncFiles(urlMain + prop.getProperty(PROP_URL_SYNC_FILES, ""));
        if (Boolean.TRUE.equals(getUseExternalStorage())) {
            setSavePath(context.getExternalFilesDir(null) + prop.getProperty(PROP_SAVE_PATH, ""));
        } else
            setSavePath(context.getFilesDir() + prop.getProperty(PROP_SAVE_PATH, ""));

        encrypt = Boolean.parseBoolean(prop.getProperty(PROP_ENCRYPT, "false"));
        decrypt = Boolean.parseBoolean(prop.getProperty(PROP_DECRYPT, "false"));
        Global.IS_DEV = Boolean.parseBoolean(prop.getProperty(PROP_IS_DEVELOPER, "false"));
        Global.IS_BYPASSROOT = Boolean.parseBoolean(prop.getProperty(PROP_IS_BYPASSROOT, "false"));
        setSecureConnection(Boolean.parseBoolean(prop.getProperty(PROP_IS_SECURE_CONNECTION, "false")));
        setRequiresAccessToken(Boolean.parseBoolean(prop.getProperty(PROP_IS_REQUIRED_ACCESS_TOKEN, "false")));
        setUseOwnCamera(Boolean.parseBoolean(prop.getProperty(PROP_OWN_CAMERA, "false")));
        setByPassDeveloper(Boolean.parseBoolean(prop.getProperty(PROP_IS_BYPASS_DEVELOPER, "false")));
        setClientId(prop.getProperty(PROP_CLIENT_ID, "android"));
        Global.ENABLE_USER_HELP = Boolean.parseBoolean(prop.getProperty(PROP_ENABLE_USER_HELP,"false"));
        Global.ENABLE_LOC_PERMISSION_UI = Boolean.parseBoolean(prop.getProperty(PROP_ENABLE_LOC_PERMISSION_UI,"false"));

        listPrinter = prop.getProperty(PROP_LIST_PRINTER, "");

        try {
            String link = urlMain;
            String[] index = link.split("://");
            if (index != null) {
                if (index[0].equals(http)) {
                    setSecureConnection(false);
                } else if (index[0].equals(https)) {
                    setSecureConnection(true);
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);

        }

        setApplication(prop.getProperty(PROP_APPLICATION_NAME, ""));
        maxLog = Integer.parseInt(prop.getProperty(PROP_MAX_LOG, "0"));
        logAge = Integer.parseInt(prop.getProperty(PROP_LOG_AGE, "0"));
        defMaxLenght = Integer.parseInt(prop.getProperty(PROP_DEF_MAX_LENGHT, "0"));
        thumbnailWidth = Integer.parseInt(prop.getProperty(PROP_THUMB_WIDTH, "0"));
        thumbnailHeight = Integer.parseInt(prop.getProperty(PROP_THUMB_HEIGHT, "0"));
        cameraQuality = prop.getProperty(PROP_CAMERA_QUALITY, "0");
        maxPhotoSize = Integer.parseInt(prop.getProperty(PROP_MAX_PHOTO_SIZE, "1024"));
        intervalTracking = Integer.parseInt(prop.getProperty(PROP_INTERVAL_TRACKING, "0"));
        intervalAutoSend = Integer.parseInt(prop.getProperty(PROP_INTERVAL_AUTOSEND, "0"));
        intervalGPSTimeout = Integer.parseInt(prop.getProperty(PROP_INTERVAL_GPS_TIMEOUT, "0"));
        setUuidChange(Boolean.parseBoolean(prop.getProperty(PROP_CHANGE_UUID, "false")));
        setUuidDivider(prop.getProperty(PROP_UUID_DIVIDER, ""));
    }

    public boolean isUuidChange() {
        return uuidChange;
    }

    public void setUuidChange(boolean uuidChange) {
        this.uuidChange = uuidChange;
    }

    public String getUuidDivider() {
        return uuidDivider;
    }

    public void setUuidDivider(String uuidDivider) {
        this.uuidDivider = uuidDivider;
    }

    public void loadGeneralParameters(List<GeneralParameter> generalParameters) {
        for (GeneralParameter generalParameter : generalParameters) {

            String param = generalParameter.getGs_code();
            String value = generalParameter.getGs_value();
            int intValue = 0;

            try {
                if (value.matches("^[0-9]{0,9}$")) {
                    intValue = Integer.parseInt(value);
                } else {
                    intValue = 0;
                }
            } catch (Exception e) {
                FireCrash.log(e);
                intValue = 0;
            }

            if (GeneralParameterDataAccess.GS_PARAM_INTERVAL_TRACKING.equals(param)) {
                setIntervalTracking(intValue);
            } else if (GeneralParameterDataAccess.GS_PARAM_INTERVAL_AUTOSEND.equals(param)) {
                setIntervalAutoSend(intValue);
            } else if (GeneralParameterDataAccess.GS_PARAM_INTERVAL_GPS_TIMEOUT.equals(param)) {
                setIntervalGPSTimeout(intValue);
            } else if (GeneralParameterDataAccess.GS_PARAM_CAMERA.equals(param)) {
                setCameraQuality(value);
            } else if (com.adins.mss.constant.Global.GS_TENANT_ID.equals(param)) {
                setTenant(value);
            } else if (com.adins.mss.constant.Global.GS_TIMELINE_TIME.equals(param)) {
                setKeepTimelineInDays(intValue);
            } else if (com.adins.mss.constant.Global.GS_LOG_COUNTER.equals(param)) {
                setMaxDataInLog(intValue);
            } else if (com.adins.mss.constant.Global.GS_ACCURACY_G.equals(param)) {
                setGreenAccuracy(intValue);
            } else if (com.adins.mss.constant.Global.GS_ACCURACY_Y.equals(param)) {
                setYellowAccuracy(intValue);
            } else if (com.adins.mss.constant.Global.GS_ACCURACY.equals(param)) {
                setMaxAccuracySafely(intValue);
            } else if (Global.GS_CURRENCY_TYPE.equals(param)) {
                setCurrencyType(value);
            }
        }
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public int getKeepTimelineInDays() {
        return this.keepTimelineInDays;
    }

    public void setKeepTimelineInDays(int value) {
        this.keepTimelineInDays = value;
    }

    public int getMaxDataInLog() {
        return this.maxDataInLog;
    }

    public void setMaxDataInLog(int value) {
        this.maxDataInLog = value;
    }

    public boolean isUseOwnCamera() {
        return useOwnCamera;
    }

    public void setUseOwnCamera(boolean useOwnCamera) {
        this.useOwnCamera = useOwnCamera;
    }

    //=== Getter Setter ===//
    public String getUrlMain() {
        return urlMain;
    }

    public void setUrlMain(String urlMain) {
        this.urlMain = urlMain;
    }

    public String getUrlSync() {
        return urlSync;
    }

    public void setUrlSync(String urlSync) {
        this.urlSync = urlSync;
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

    public boolean isSecureConnection() {
        return isSecureConnection;
    }

    public void setSecureConnection(boolean isSecureConnection) {
        this.isSecureConnection = isSecureConnection;
    }

    public int getMaxLog() {
        return maxLog;
    }

    public void setMaxLog(int maxLog) {
        this.maxLog = maxLog;
    }

    public int getLogAge() {
        return logAge;
    }

    public void setLogAge(int logAge) {
        this.logAge = logAge;
    }

    public int getMaxLenght() {
        return defMaxLenght;
    }

    public void setMaxLenght(int maxLenght) {
        this.defMaxLenght = maxLenght;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public AutoSendSerivce getAutoSendThread() {
        return autoSendService;
    }

    public void setAutoSendThread(AutoSendSerivce autoSendThread) {
        this.autoSendService = autoSendThread;
    }

    public LocationTrackingSchedule getLocationTrackingSchedule() {
        return locationTrackingSchedule;
    }

    public void setLocationTrackingSchedule(LocationTrackingSchedule locationTrackingSchedule) {
        this.locationTrackingSchedule = locationTrackingSchedule;
    }

    public int getIntervalTracking() {
        return intervalTracking;
    }

    public void setIntervalTracking(int intervalTracking) {
        this.intervalTracking = intervalTracking;
    }

    public int getIntervalAutoSend() {
        return intervalAutoSend;
    }

    public void setIntervalAutoSend(int intervalAutoSend) {
        this.intervalAutoSend = intervalAutoSend;
    }

    public int getIntervalGPSTimeout() {
        return intervalGPSTimeout;
    }

    public void setIntervalGPSTimeout(int intervalGPSTimeout) {
        this.intervalGPSTimeout = intervalGPSTimeout;
    }

    public String getCameraQuality() {
        return cameraQuality;
    }

    public void setCameraQuality(String cameraQuality) {
        this.cameraQuality = cameraQuality;
    }

    public Boolean getDoingTask() {
        return isDoingTask;
    }

    public void setDoingTask(Boolean doingTask) {
        isDoingTask = doingTask;
    }

    public String getUrlTracking() {
        return urlTracking;
    }

    public void setUrlTracking(String urlTracking) {
        this.urlTracking = urlTracking;
    }
    
    public ConnectionCryptor getConnectionCryptor() {
        return connectionCryptor;
    }

    public void setConnectionCryptor(ConnectionCryptor connectionCryptor) {
        this.connectionCryptor = connectionCryptor;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImei2() {
        return imei2;
    }

    public void setImei2(String imei2) {
        this.imei2 = imei2;
    }

    public String getAndroidId() {
        if (androidId == null || androidId.isEmpty()) {
            androidId = AuditDataTypeGenerator.getAndroidId();
        }

        return androidId;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public int getDefMaxLenght() {
        return defMaxLenght;
    }

    public void setDefMaxLenght(int defMaxLenght) {
        this.defMaxLenght = defMaxLenght;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AuditDataType getAuditData() {
        return auditData;
    }

    public void setAuditData(AuditDataType auditData) {
        this.auditData = auditData;
    }

    public boolean isPartialSending() {
        return isPartialSending;
    }

    public void setPartialSending(boolean isPartialSending) {
        this.isPartialSending = isPartialSending;
    }

    public String getUrlPersonalization() {
        return urlPersonalization;
    }

    public void setUrlPersonalization(String urlPersonalization) {
        this.urlPersonalization = urlPersonalization;
    }

    public String getURL_UPLOAD_AVATAR() {
        return this.URL_UPLOAD_AVATAR;
    }

    public void setUrlUploadAvatar(String urlUploadAvatar) {
        this.URL_UPLOAD_AVATAR = urlUploadAvatar;
    }

    public String getURL_SUBMIT_DKCP() {
        return URL_SUBMIT_DKCP;
    }

    public void setURL_SUBMIT_DKCP(String URL_SUBMIT_DKCP) {
        this.URL_SUBMIT_DKCP = URL_SUBMIT_DKCP;
    }

    public int getMaxPhotoSize() {
        return maxPhotoSize;
    }

    public void setMaxPhotoSize(int maxPhotoSize) {
        this.maxPhotoSize = maxPhotoSize;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getURL_LOOKUP_ANSWER() {
        return URL_GET_LOOKUP_ANSWER;
    }

    public void setURL_LOOKUP_ANSWER(String URL_GET_LOOKUP_ANSWER) {
        this.URL_GET_LOOKUP_ANSWER = URL_GET_LOOKUP_ANSWER;
    }

    /**
     * Gets URL_GET_CLOSING_TASK
     */
    public String getURL_CLOSING_TASK() {
        return this.URL_GET_CLOSING_TASK;
    }

    /**
     * Sets URL_SET_CLOSING_TASK
     */
    public void setURL_CLOSING_TASK(String value) {
        this.URL_GET_CLOSING_TASK = value;
    }

    /** Property REQUEST_NEW_TASK */
    String URL_REQUEST_NEW_TASK;

    public String getURL_REQUEST_NEW_TASK() {
        return URL_REQUEST_NEW_TASK;
    }

    public void setURL_REQUEST_NEW_TASK(String URL_REQUEST_NEW_TASK) {
        this.URL_REQUEST_NEW_TASK = URL_REQUEST_NEW_TASK;
    }

    /** Property REQUEST_NEW_TASK */
    String URL_ASSIGN_NEW_TASK;

    public String getURL_ASSIGN_NEW_TASK() {
        return URL_ASSIGN_NEW_TASK;
    }

    public void setURL_ASSIGN_NEW_TASK(String URL_ASSIGN_NEW_TASK) {
        this.URL_ASSIGN_NEW_TASK = URL_ASSIGN_NEW_TASK;
    }

    public String getURL_SUBMIT_PRINT_COUNT() {
        return URL_SUBMIT_PRINT_COUNT;
    }

    public void setURL_SUBMIT_PRINT_COUNT(String URL_SUBMIT_PRINT_COUNT) {
        this.URL_SUBMIT_PRINT_COUNT = URL_SUBMIT_PRINT_COUNT;
    }

    public String getURL_RV_NUMBER() {
        return URL_RV_NUMBER;
    }

    public void setURL_RV_NUMBER(String URL_RV_NUMBER) {
        this.URL_RV_NUMBER = URL_RV_NUMBER;
    }

    public String getURL_GET_DEALERS() {
        return URL_GET_DEALERS;
    }

    public void setURL_GET_DEALERS(String URL_GET_DEALERS) {
        this.URL_GET_DEALERS = URL_GET_DEALERS;
    }

    public String getURL_GET_TASK_LOG() {
        return this.URL_GET_TASK_LOG;
    }

    public void setURL_GET_TASK_LOG(String URL_GET_TASK_LOG) {
        this.URL_GET_TASK_LOG = URL_GET_TASK_LOG;
    }

    public String getURL_SYNC_RV_NUMBERS() {
        return URL_SYNC_RV_NUMBERS;
    }

    public void setURL_SYNC_RV_NUMBERS(String URL_SYNC_RV_NUMBERS) {
        this.URL_SYNC_RV_NUMBERS = URL_SYNC_RV_NUMBERS;
    }

    public String getURL_SYNC_PAYMENTCHANNEL() {
        return URL_SYNC_PAYMENTCHANNEL;
    }

    public void setURL_SYNC_PAYMENTCHANNEL(String URL_SYNC_PAYMENTCHANNEL) {
        this.URL_SYNC_PAYMENTCHANNEL = URL_SYNC_PAYMENTCHANNEL;
    }

    public String getURL_GET_BATCHID_LIST() {
        return URL_GET_BATCHID_LIST;
    }

    public void setURL_GET_BATCHID_LIST(String URL_GET_BATCHID_LIST) {
        this.URL_GET_BATCHID_LIST= URL_GET_BATCHID_LIST;
    }

    public String getURL_SUBMIT_DEPOSIT_PC() {
        return URL_SUBMIT_DEPOSIT_PC;
    }

    public void setURL_SUBMIT_DEPOSIT_PC(String URL_SUBMIT_DEPOSIT_PC) {
        this.URL_SUBMIT_DEPOSIT_PC = URL_SUBMIT_DEPOSIT_PC;
    }

    /**
     * Gets the URL_CHECK_VALIDATIONQUESTION
     */
    public String getURL_CHECK_VALIDATIONQUESTION() {
        return URL_CHECK_VALIDATIONQUESTION;
    }

    /**
     * Sets the URL_CHECK_VALIDATIONQUESTION
     */
    public void setURL_CHECK_VALIDATIONQUESTION(String URL_CHECK_VALIDATIONQUESTION) {
        this.URL_CHECK_VALIDATIONQUESTION = URL_CHECK_VALIDATIONQUESTION;
    }

    /**
     * Gets the URL_GET_DIGITAL_RECEIPT
     */
    public String getURL_GET_DIGITAL_RECEIPT() {
        return URL_GET_DIGITAL_RECEIPT;
    }

    public String getURL_GET_TEXT_ONLINE_ANSWER() {
        return URL_GET_TEXT_ONLINE_ANSWER;
    }

    /**
     * Sets the URL_GET_DIGITAL_RECEIPT
     */
    public void setURL_GET_DIGITAL_RECEIPT(String URL_GET_DIGITAL_RECEIPT) {
        this.URL_GET_DIGITAL_RECEIPT = URL_GET_DIGITAL_RECEIPT;
    }
    public void setURL_GET_TEXT_ONLINE(String URL_GET_TEXT_ONLINE_ANSWER) {
        this.URL_GET_TEXT_ONLINE_ANSWER = URL_GET_TEXT_ONLINE_ANSWER;
    }

    public String getURL_GET_CODETRANSACTION() {
        return URL_GET_CODETRANSACTION;
    }

    public void setURL_GET_CODETRANSACTION(String URL_GET_CODETRANSACTION) {
        this.URL_GET_CODETRANSACTION = URL_GET_CODETRANSACTION;
    }

    public String getURL_GET_PDF_DOCUMENT() {
        return URL_GET_PDF_DOCUMENT;
    }

    public void setURL_GET_PDF_DOCUMENT(String URL_GET_PDF_DOCUMENT) {
        this.URL_GET_PDF_DOCUMENT = URL_GET_PDF_DOCUMENT;
    }

    public String getURL_GET_DOCUMENT_LIST() {
        return URL_GET_DOCUMENT_LIST;
    }

    public void setURL_GET_DOCUMENT_LIST(String URL_GET_DOCUMENT_LIST) {
        this.URL_GET_DOCUMENT_LIST = URL_GET_DOCUMENT_LIST;
    }

    public String getURL_GET_FOLLOW_UP_LIST() {
        return URL_GET_FOLLOW_UP_LIST;
    }

    public void setURL_GET_FOLLOW_UP_LIST(String URL_GET_FOLLOW_UP_LIST) {
        this.URL_GET_FOLLOW_UP_LIST = URL_GET_FOLLOW_UP_LIST;
    }

    public String getURL_SUBMIT_FOLLOW_UP() {
        return URL_SUBMIT_FOLLOW_UP;
    }

    public void setURL_SUBMIT_FOLLOW_UP(String URL_SUBMIT_FOLLOW_UP) {
        this.URL_SUBMIT_FOLLOW_UP = URL_SUBMIT_FOLLOW_UP;
    }

    public String getURL_GET_DATA_QUESTION_BUTTON_TEXT() {
        return URL_GET_DATA_QUESTION_BUTTON_TEXT;
    }

    public void setURL_GET_DATA_QUESTION_BUTTON_TEXT(String URL_GET_DATA_QUESTION_BUTTON_TEXT) {
        this.URL_GET_DATA_QUESTION_BUTTON_TEXT = URL_GET_DATA_QUESTION_BUTTON_TEXT;
    }


    public String getURL_UPDATE_CASH_ON_HAND() {
        return URL_UPDATE_CASH_ON_HAND;
    }

    public void setURL_UPDATE_CASH_ON_HAND(String URL_UPDATE_CASH_ON_HAND) {
        this.URL_UPDATE_CASH_ON_HAND = URL_UPDATE_CASH_ON_HAND;
    }

    /**
     * Gets the URL_LOGIN
     */
    public String getURL_LOGIN() {
        return this.URL_LOGIN;
    }

    /**
     * Sets the URL_LOGIN
     */
    public void setURL_LOGIN(String value) {
        this.URL_LOGIN = value;
    }

    /**
     * Gets the URL_UPDATE_FCM
     */
    public String getURL_UPDATE_FCM() {
        return this.URL_UPDATE_FCM;
    }

    /**
     * Sets the URL_UPDATE_FCM
     */
    public void setURL_UPDATE_FCM(String value) {
        this.URL_UPDATE_FCM = value;
    }

    /**
     * Gets the URL_CHANGEPASSWORD
     */
    public String getURL_CHANGEPASSWORD() {
        return this.URL_CHANGEPASSWORD;
    }

    /**
     * Sets the URL_CHANGEPASSWORD
     */
    public void setURL_CHANGEPASSWORD(String value) {
        this.URL_CHANGEPASSWORD = value;
    }

    /**
     * Gets the URL_GET_ABSENSI
     */
    public String getURL_GET_ABSENSI() {
        return this.URL_GET_ABSENSI;
    }

    /**
     * Sets the URL_GET_ABSENSI
     */
    public void setURL_GET_ABSENSI(String value) {
        this.URL_GET_ABSENSI = value;
    }

    /**
     * Gets the URL_GET_TASKLIST
     */
    public String getURL_GET_TASKLIST() {
        return this.URL_GET_TASKLIST;
    }

    /**
     * Sets the URL_GET_TASKLIST
     */
    public void setURL_GET_TASKLIST(String value) {
        this.URL_GET_TASKLIST = value;
    }

    /**
     * Gets the URL_REFRESHTASK
     */
    public String getURL_REFRESHTASK() {
        return this.URL_REFRESHTASK;
    }

    /**
     * Sets the URL_REFRESHTASK
     */
    public void setURL_REFRESHTASK(String value) {
        this.URL_REFRESHTASK = value;
    }

    /**
     * Gets the URL_SUBMITTASK
     */
    public String getURL_SUBMITTASK() {
        return this.URL_SUBMITTASK;
    }

    /**
     * Sets the URL_SUBMITTASK
     */
    public void setURL_SUBMITTASK(String value) {
        this.URL_SUBMITTASK = value;
    }

    /**
     * Gets the URL_SUBMITOPENREADTASK
     */
    public String getURL_SUBMITOPENREADTASK() {
        return this.URL_SUBMITOPENREADTASK;
    }

    /**
     * Sets the URL_SUBMITOPENREADTASK
     */
    public void setURL_SUBMITOPENREADTASK(String value) {
        this.URL_SUBMITOPENREADTASK = value;
    }

    /**
     * Gets the URL_GET_QUESTIONSET
     */
    public String getURL_GET_QUESTIONSET() {
        return this.URL_GET_QUESTIONSET;
    }

    /**
     * Sets the URL_GET_QUESTIONSET
     */
    public void setURL_GET_QUESTIONSET(String value) {
        this.URL_GET_QUESTIONSET = value;
    }

    /**
     * Gets the URL_GET_VERIFICATION
     */
    public String getURL_GET_VERIFICATION() {
        return this.URL_GET_VERIFICATION;
    }

    /**
     * Sets the URL_GET_VERIFICATION
     */
    public void setURL_GET_VERIFICATION(String value) {
        this.URL_GET_VERIFICATION = value;
    }

    /**
     * Gets the URL_GET_IMAGE
     */
    public String getURL_GET_IMAGE() {
        return this.URL_GET_IMAGE;
    }

    /**
     * Sets the URL_GET_IMAGE
     */
    public void setURL_GET_IMAGE(String value) {
        this.URL_GET_IMAGE = value;
    }

    /**
     * Gets the URL_GET_SCHEME
     */
    public String getURL_GET_SCHEME() {
        return this.URL_GET_SCHEME;
    }

    /**
     * Sets the URL_GET_SCHEME
     */
    public void setURL_GET_SCHEME(String value) {
        this.URL_GET_SCHEME = value;
    }

    /**
     * Gets the URL_PRESUBMIT
     */
    public String getURL_PRESUBMIT() {
        return this.URL_PRESUBMIT;
    }

    /**
     * Sets the URL_PRESUBMIT
     */
    public void setURL_PRESUBMIT(String value) {
        this.URL_PRESUBMIT = value;
    }

    /**
     * Gets the URL_GET_SVYPERFORMANCE
     */
    public String getURL_GET_SVYPERFORMANCE() {
        return this.URL_GET_SVYPERFORMANCE;
    }

    /**
     * Sets the URL_GET_SVYPERFORMANCE
     */
    public void setURL_GET_SVYPERFORMANCE(String value) {
        this.URL_GET_SVYPERFORMANCE = value;
    }

    /**
     * Gets the URL_GET_LOOKUP
     */
    public String getURL_GET_LOOKUP() {
        return this.URL_GET_LOOKUP;
    }

    /**
     * Sets the URL_GET_LOOKUP
     */
    public void setURL_GET_LOOKUP(String value) {
        this.URL_GET_LOOKUP = value;
    }

    /**
     * Gets the URL_GET_LIST_REASSIGNMENT
     */
    public String getURL_GET_LIST_REASSIGNMENT() {
        return this.URL_GET_LIST_REASSIGNMENT;
    }

    /**
     * Sets the URL_GET_LIST_REASSIGNMENT
     */
    public void setURL_GET_LIST_REASSIGNMENT(String value) {
        this.URL_GET_LIST_REASSIGNMENT = value;
    }

    /**
     * Gets the URL_GET_DETAIL_REASSIGNMENT
     */
    public String getURL_GET_DETAIL_REASSIGNMENT() {
        return this.URL_GET_DETAIL_REASSIGNMENT;
    }

    /**
     * Sets the URL_GET_DETAIL_REASSIGNMENT
     */
    public void setURL_GET_DETAIL_REASSIGNMENT(String value) {
        this.URL_GET_DETAIL_REASSIGNMENT = value;
    }

    /**
     * Gets the URL_GET_LIST_ASSIGNMENT
     */
    public String getURL_GET_LIST_ASSIGNMENT() {
        return this.URL_GET_LIST_ASSIGNMENT;
    }

    /**
     * Sets the URL_GET_LIST_ASSIGNMENT
     */
    public void setURL_GET_LIST_ASSIGNMENT(String value) {
        this.URL_GET_LIST_ASSIGNMENT = value;
    }

    /**
     * Gets the URL_GET_DETAIL_ASSIGNMENT
     */
    public String getURL_GET_DETAIL_ASSIGNMENT() {
        return this.URL_GET_DETAIL_ASSIGNMENT;
    }

    /**
     * Sets the URL_GET_DETAIL_ASSIGNMENT
     */
    public void setURL_GET_DETAIL_ASSIGNMENT(String value) {
        this.URL_GET_DETAIL_ASSIGNMENT = value;
    }

    /**
     * Gets the URL_GET_DETAIL_ORDER
     */
    public String getURL_GET_DETAIL_ORDER() {
        return this.URL_GET_DETAIL_ORDER;
    }

    /**
     * Sets the URL_GET_DETAIL_ORDER
     */
    public void setURL_GET_DETAIL_ORDER(String value) {
        this.URL_GET_DETAIL_ORDER = value;
    }

    /**
     * Gets the URL_GET_DETAIL_TASK
     */
    public String getURL_GET_DETAIL_TASK() {
        return this.URL_GET_DETAIL_TASK;
    }

    /**
     * Sets the URL_GET_DETAIL_TASK
     */
    public void setURL_GET_DETAIL_TASK(String value) {
        this.URL_GET_DETAIL_TASK = value;
    }

    /**
     * Gets the URL_SUBMIT_ASSIGN
     */
    public String getURL_SUBMIT_ASSIGN() {
        return this.URL_SUBMIT_ASSIGN;
    }

    /**
     * Sets the URL_SUBMIT_ASSIGN
     */
    public void setURL_SUBMIT_ASSIGN(String value) {
        this.URL_SUBMIT_ASSIGN = value;
    }

    /**
     * Gets the URL_GET_LIST_VERIFICATION
     */
    public String getURL_GET_LIST_VERIFICATION() {
        return this.URL_GET_LIST_VERIFICATION;
    }

    /**
     * Sets the URL_GET_LIST_VERIFICATION
     */
    public void setURL_GET_LIST_VERIFICATION(String value) {
        this.URL_GET_LIST_VERIFICATION = value;
    }

    /**
     * Gets the URL_GET_LIST_APPROVAL
     */
    public String getURL_GET_LIST_APPROVAL() {
        return this.URL_GET_LIST_APPROVAL;
    }

    /**
     * Sets the URL_GET_LIST_APPROVAL
     */
    public void setURL_GET_LIST_APPROVAL(String value) {
        this.URL_GET_LIST_APPROVAL = value;
    }

    /**
     * Gets the URL_SENDDEPOSITREPORT
     */
    public String getURL_SENDDEPOSITREPORT() {
        return this.URL_SENDDEPOSITREPORT;
    }

    /**
     * Sets the URL_SENDDEPOSITREPORT
     */
    public void setURL_SENDDEPOSITREPORT(String value) {
        this.URL_SENDDEPOSITREPORT = value;
    }

    /**
     * Gets the URL_GET_PAYMENTHISTORY
     */
    public String getURL_GET_PAYMENTHISTORY() {
        return this.URL_GET_PAYMENTHISTORY;
    }

    /**
     * Sets the URL_GET_PAYMENTHISTORY
     */
    public void setURL_GET_PAYMENTHISTORY(String value) {
        this.URL_GET_PAYMENTHISTORY = value;
    }

    /**
     * Gets the URL_GET_INSTALLMENTSCHEDULE
     */
    public String getURL_GET_INSTALLMENTSCHEDULE() {
        return this.URL_GET_INSTALLMENTSCHEDULE;
    }

    /**
     * Sets the URL_GET_INSTALLMENTSCHEDULE
     */
    public void setURL_GET_INSTALLMENTSCHEDULE(String value) {
        this.URL_GET_INSTALLMENTSCHEDULE = value;
    }

    /**
     * Gets the URL_GET_COLLECTIONHISTORY
     */
    public String getURL_GET_COLLECTIONHISTORY() {
        return this.URL_GET_COLLECTIONHISTORY;
    }

    /**
     * Sets the URL_GET_COLLECTIONHISTORY
     */
    public void setURL_GET_COLLECTIONHISTORY(String value) {
        this.URL_GET_COLLECTIONHISTORY = value;
    }

    /**
     * Gets the URL_SUBMITVERIFICATIONTASK
     */
    public String getURL_SUBMITVERIFICATIONTASK() {
        return this.URL_SUBMITVERIFICATIONTASK;
    }

    /**
     * Sets the URL_SUBMITVERIFICATIONTASK
     */
    public void setURL_SUBMITVERIFICATIONTASK(String value) {
        this.URL_SUBMITVERIFICATIONTASK = value;
    }

    /**
     * Gets the URL_SUBMITAPPROVALTASK
     */
    public String getURL_SUBMITAPPROVALTASK() {
        return this.URL_SUBMITAPPROVALTASK;
    }

    /**
     * Sets the URL_SUBMITAPPROVALTASK
     */
    public void setURL_SUBMITAPPROVALTASK(String value) {
        this.URL_SUBMITAPPROVALTASK = value;
    }

    /**
     * Gets the URL_GET_CONTENTNEWS
     */
    public String getURL_GET_CONTENTNEWS() {
        return this.URL_GET_CONTENTNEWS;
    }

    /**
     * Sets the URL_GET_CONTENTNEWS
     */
    public void setURL_GET_CONTENTNEWS(String value) {
        this.URL_GET_CONTENTNEWS = value;
    }

    /**
     * Gets the URL_CHECKORDER
     */
    public String getURL_CHECKORDER() {
        return this.URL_CHECKORDER;
    }

    /**
     * Sets the URL_CHECKORDER
     */
    public void setURL_CHECKORDER(String value) {
        this.URL_CHECKORDER = value;
    }

    /**
     * Gets the URL_GET_TASK
     */
    public String getURL_GET_TASK() {
        return this.URL_GET_TASK;
    }

    /**
     * Sets the URL_GET_TASK
     */
    public void setURL_GET_TASK(String value) {
        this.URL_GET_TASK = value;
    }

    /**
     * Gets the URL_GET_NEWSHEADER
     */
    public String getURL_GET_NEWSHEADER() {
        return this.URL_GET_NEWSHEADER;
    }

    /**
     * Sets the URL_GET_NEWSHEADER
     */
    public void setURL_GET_NEWSHEADER(String value) {
        this.URL_GET_NEWSHEADER = value;
    }

    /**
     * Gets the URL_GET_NEWSCONTENT
     */
    public String getURL_GET_NEWSCONTENT() {
        return this.URL_GET_NEWSCONTENT;
    }

    /**
     * Sets the URL_GET_NEWSCONTENT
     */
    public void setURL_GET_NEWSCONTENT(String value) {
        this.URL_GET_NEWSCONTENT = value;
    }

    /**
     * Gets the URL_GET_LIST_CANCELORDER
     */
    public String getURL_GET_LIST_CANCELORDER() {
        return this.URL_GET_LIST_CANCELORDER;
    }

    /**
     * Sets the URL_GET_LIST_CANCELORDER
     */
    public void setURL_GET_LIST_CANCELORDER(String value) {
        this.URL_GET_LIST_CANCELORDER = value;
    }

    /**
     * Gets the URL_GET_DETAIL_CANCELORDER
     */
    public String getURL_GET_DETAIL_CANCELORDER() {
        return this.URL_GET_DETAIL_CANCELORDER;
    }

    /**
     * Sets the URL_GET_DETAIL_CANCELORDER
     */
    public void setURL_GET_DETAIL_CANCELORDER(String value) {
        this.URL_GET_DETAIL_CANCELORDER = value;
    }

    /**
     * Gets the URL_GET_CANCELORDER
     */
    public String getURL_GET_CANCELORDER() {
        return this.URL_GET_CANCELORDER;
    }

    /**
     * Sets the URL_GET_CANCELORDER
     */
    public void setURL_GET_CANCELORDER(String value) {
        this.URL_GET_CANCELORDER = value;
    }

    /**
     * Gets the URL_SUBMIT_TRACK
     */
    public String getURL_SUBMIT_TRACK() {
        return this.URL_SUBMIT_TRACK;
    }

    /**
     * Sets the URL_SUBMIT_TRACK
     */
    public void setURL_SUBMIT_TRACK(String value) {
        this.URL_SUBMIT_TRACK = value;
    }

    /**
     * Gets the URL_RETRIECECOLLECTIONTASK
     */
    public String getURL_RETRIECECOLLECTIONTASK() {
        return this.URL_RETRIECECOLLECTIONTASK;
    }

    /**
     * Sets the URL_RETRIECECOLLECTIONTASK
     */
    public void setURL_RETRIECECOLLECTIONTASK(String value) {
        this.URL_RETRIECECOLLECTIONTASK = value;
    }

    /**
     * Gets the URL_SYNCPARAM
     */
    public String getURL_SYNCPARAM() {
        return this.URL_SYNCPARAM;
    }

    /**
     * Sets the URL_SYNCPARAM
     */
    public void setURL_SYNCPARAM(String value) {
        this.URL_SYNCPARAM = value;
    }

    /**
     * Gets the URL_GET_REPORTSUMMARY
     */
    public String getURL_GET_REPORTSUMMARY() {
        return this.URL_GET_REPORTSUMMARY;
    }

    /**
     * Sets the URL_GET_REPORTSUMMARY
     */
    public void setURL_GET_REPORTSUMMARY(String value) {
        this.URL_GET_REPORTSUMMARY = value;
    }

    /**
     * Gets the URL_SUBMIT_RESCHEDULE
     */
    public String getURL_SUBMIT_RESCHEDULE() {
        return this.URL_SUBMIT_RESCHEDULE;
    }

    /**
     * Sets the URL_SUBMIT_RESCHEDULE
     */
    public void setURL_SUBMIT_RESCHEDULE(String value) {
        this.URL_SUBMIT_RESCHEDULE = value;
    }

    /**
     * Gets the URL_GET_LIST_USER
     */
    public String getURL_GET_LIST_USER() {
        return this.URL_GET_LIST_USER;
    }

    /**
     * Sets the URL_GET_LIST_USER
     */
    public void setURL_GET_LIST_USER(String value) {
        this.URL_GET_LIST_USER = value;
    }

    /**
     * Gets the URL_CHECK_UPDATE
     */
    public String getURL_CHECK_UPDATE() {
        return this.URL_CHECK_UPDATE;
    }

    /**
     * Sets the URL_CHECK_UPDATE
     */
    public void setURL_CHECK_UPDATE(String value) {
        this.URL_CHECK_UPDATE = value;
    }

    /**
     * Gets the URL_CHECK_RESUBMIT
     */
    public String getURL_CHECK_RESUBMIT() {
        return this.URL_CHECK_RESUBMIT;
    }

    /**
     * Sets the URL_CHECK_RESUBMIT
     */
    public void setURL_CHECK_RESUBMIT(String value) {
        this.URL_CHECK_RESUBMIT = value;
    }

    public String getURL_GET_RECAPITULATE() {
        return this.URL_GET_RECAPITULATE;
    }

    public void setURL_GET_RECAPITULATE(String value) {
        this.URL_GET_RECAPITULATE = value;
    }

    public String getURL_SYNCPARAM_CONSTRAINT() {
        return this.URL_SYNCPARAM_CONSTRAINT;
    }

    public void setURL_SYNCPARAM_CONSTRAINT(String URL_SYNCPARAM_CONSTRAINT) {
        this.URL_SYNCPARAM_CONSTRAINT = URL_SYNCPARAM_CONSTRAINT;
    }

    public int getGreenAccuracy() {
        return this.greenAccuracy;
    }

    public void setGreenAccuracy(int value) {
        this.greenAccuracy = value;
    }

    public int getYellowAccuracy() {
        return this.yellowAccuracy;
    }

    public void setYellowAccuracy(int value) {
        this.yellowAccuracy = value;
    }

    public int getMaxAccuracySafely() {
        return this.maxAccuracySafely;
    }

    public void setMaxAccuracySafely(int value) {
        this.maxAccuracySafely = value;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String type) {
        this.currencyType = type;
    }

    public String getURL_GET_ACCOUNT() {
        return URL_GET_ACCOUNT;
    }

    public void setURL_GET_ACCOUNT(String URL_GET_ACCOUNT) {
        this.URL_GET_ACCOUNT = URL_GET_ACCOUNT;
    }

    public String getURL_GET_CONTACT() {
        return URL_GET_CONTACT;
    }

    public void setURL_GET_CONTACT(String URL_GET_CONTACT) {
        this.URL_GET_CONTACT = URL_GET_CONTACT;
    }

    public String getURL_GET_OPPORTUNITY() {
        return URL_GET_OPPORTUNITY;
    }

    public void setURL_GET_OPPORTUNITY(String URL_GET_OPPORTUNITY) {
        this.URL_GET_OPPORTUNITY = URL_GET_OPPORTUNITY;
    }

    public String getURL_GET_OPPORTUNITY_DETAIL() {
        return URL_GET_OPPORTUNITY_DETAIL;
    }

    public void setURL_GET_OPPORTUNITY_DETAIL(String URL_GET_OPPORTUNITY_DETAIL) {
        this.URL_GET_OPPORTUNITY_DETAIL = URL_GET_OPPORTUNITY_DETAIL;
    }

    public String getURL_SUBMITTASK_MMA() {
        return URL_SUBMITTASK_MMA;
    }

    public void setURL_SUBMITTASK_MMA(String URL_SUBMITTASK_MMA) {
        this.URL_SUBMITTASK_MMA = URL_SUBMITTASK_MMA;
    }

    public String getURL_GET_FOLLOWUP() {
        return URL_GET_FOLLOWUP;
    }

    public void setURL_GET_FOLLOWUP(String URL_GET_FOLLOWUP) {
        this.URL_GET_FOLLOWUP = URL_GET_FOLLOWUP;
    }

    public String getURL_DO_FOLLOWUP() {
        return URL_DO_FOLLOWUP;
    }

    public void setURL_DO_FOLLOWUP(String URL_DO_FOLLOWUP) {
        this.URL_DO_FOLLOWUP = URL_DO_FOLLOWUP;
    }

    public String getURL_GET_PRODUCT() {
        return URL_GET_PRODUCT;
    }

    public void setURL_GET_PRODUCT(String URL_GET_PRODUCT) {
        this.URL_GET_PRODUCT = URL_GET_PRODUCT;
    }

    public String getURL_GET_MKTPERFORMANCE() {
        return URL_GET_MKTPERFORMANCE;
    }

    public void setURL_GET_MKTPERFORMANCE(String URL_GET_MKTPERFORMANCE) {
        this.URL_GET_MKTPERFORMANCE = URL_GET_MKTPERFORMANCE;
    }

    public String getURL_GET_CATALOGUE_HEADER() {
        return URL_GET_CATALOGUE_HEADER;
    }

    public void setURL_GET_CATALOGUE_HEADER(String URL_GET_CATALOGUE_HEADER) {
        this.URL_GET_CATALOGUE_HEADER = URL_GET_CATALOGUE_HEADER;
    }

    public String getURL_GET_CATALOGUE_PDF() {
        return URL_GET_CATALOGUE_PDF;
    }

    public void setURL_GET_CATALOGUE_PDF(String URL_GET_CATALOGUE_PDF) {
        this.URL_GET_CATALOGUE_PDF = URL_GET_CATALOGUE_PDF;
    }

    public String getURL_GET_CATALOGUE_PROMO() {
        return URL_GET_CATALOGUE_PROMO;
    }

    public void setURL_GET_CATALOGUE_PROMO(String URL_GET_CATALOGUE_PROMO) {
        this.URL_GET_CATALOGUE_PROMO = URL_GET_CATALOGUE_PROMO;
    }

    public String getURL_GET_PRODUCT_CONTACT() {
        return URL_GET_PRODUCT_CONTACT;
    }

    public void setURL_GET_PRODUCT_CONTACT(String URL_GET_PRODUCT_CONTACT) {
        this.URL_GET_PRODUCT_CONTACT = URL_GET_PRODUCT_CONTACT;
    }

    public String getURL_GET_PRODUCT_DETAIL() {
        return URL_GET_PRODUCT_DETAIL;
    }

    public void setURL_GET_PRODUCT_DETAIL(String URL_GET_PRODUCT_DETAIL) {
        this.URL_GET_PRODUCT_DETAIL = URL_GET_PRODUCT_DETAIL;
    }

    public String getURL_GET_PRODUCT_IMAGE() {
        return URL_GET_PRODUCT_IMAGE;
    }

    public void setURL_GET_PRODUCT_IMAGE(String URL_GET_PRODUCT_IMAGE) {
        this.URL_GET_PRODUCT_IMAGE = URL_GET_PRODUCT_IMAGE;
    }

    public String getURL_CHECK_ORDER_REJECTED() {
        return URL_CHECK_ORDER_REJECTED;
    }

    public void setURL_CHECK_ORDER_REJECTED(String URL_CHECK_ORDER_REJECTED) {
        this.URL_CHECK_ORDER_REJECTED = URL_CHECK_ORDER_REJECTED;
    }

    public String getURL_DUKCAPIL_VALIDATION() {
        return URL_DUKCAPIL_VALIDATION;
    }

    public void setURL_DUKCAPIL_VALIDATION(String URL_DUKCAPIL_VALIDATION) {
        this.URL_DUKCAPIL_VALIDATION = URL_DUKCAPIL_VALIDATION;
    }

    public String getURL_EMERGENCY() {
        return URL_EMERGENCY;
    }

    public void setURL_EMERGENCY(String URL_EMERGENCY) {
        this.URL_EMERGENCY = URL_EMERGENCY;
    }

    public String getURL_LOYALTY_DETAIL_POINT() {
        return URL_LOYALTY_DETAIL_POINT;
    }

    public void setURL_LOYALTY_DETAIL_POINT(String URL_LOYALTY_DETAIL_POINT) {
        this.URL_LOYALTY_DETAIL_POINT = URL_LOYALTY_DETAIL_POINT;
    }

    public String getURL_LAST_SYNC() {
        return URL_LAST_SYNC;
    }

    public void setURL_LAST_SYNC(String URL_LAST_SYNC) {
        this.URL_LAST_SYNC = URL_LAST_SYNC;
    }

    public String getURL_START_VISIT_PLAN() {
        return URL_START_VISIT_PLAN;
    }

    public void setURL_START_VISIT_PLAN(String URL_START_VISIT_PLAN) {
        this.URL_START_VISIT_PLAN = URL_START_VISIT_PLAN;
    }

    public String getURL_CHANGE_PLAN() {
        return URL_CHANGE_PLAN;
    }

    public void setURL_CHANGE_PLAN(String URL_CHANGE_PLAN) {
        this.URL_CHANGE_PLAN = URL_CHANGE_PLAN;
    }

    public TodayPlanRepository getTodayPlanRepo() {
        return todayPlanRepository;
    }

    public void setTodayPlanRepo(TodayPlanRepository todayPlanRepository) {
        this.todayPlanRepository = todayPlanRepository;
    }

    public String getURL_GET_LU_ONLINE() {
        return URL_GET_LU_ONLINE;
    }

    public void setURL_GET_LU_ONLINE(String URL_GET_LU_ONLINE) {
        this.URL_GET_LU_ONLINE = URL_GET_LU_ONLINE;
    }

    public String getURL_GET_RECEIPT_HISTORY() {
        return URL_GET_RECEIPT_HISTORY;
    }

    public void setURL_GET_RECEIPT_HISTORY(String URL_GET_RECEIPT_HISTORY) {
        this.URL_GET_RECEIPT_HISTORY = URL_GET_RECEIPT_HISTORY;
    }

    public String getURL_GET_RECEIPT_HISTORY_PDF() {
        return URL_GET_RECEIPT_HISTORY_PDF;
    }

    public void setURL_GET_RECEIPT_HISTORY_PDF(String URL_GET_RECEIPT_HISTORY_PDF) {
        this.URL_GET_RECEIPT_HISTORY_PDF = URL_GET_RECEIPT_HISTORY_PDF;
    }

    public String getURL_GET_Document_Pdf() {
        return URL_GET_Document_Pdf;
    }

    public void setURL_GET_Document_Pdf(String URL_GET_Document_Pdf) {
        this.URL_GET_Document_Pdf = URL_GET_Document_Pdf;
    }
    public String getURL_SEND_UPDATE_NOTIFICATION() {
        return URL_SEND_UPDATE_NOTIFICATION;
    }

    public void setURL_SEND_UPDATE_NOTIFICATION(String URL_SEND_UPDATE_NOTIFICATION) {
        this.URL_SEND_UPDATE_NOTIFICATION = URL_SEND_UPDATE_NOTIFICATION;
    }

    public String getURL_SYNC_PARAM_SUCCESS() {
        return URL_SYNC_PARAM_SUCCESS;
    }

    public void setURL_SYNC_PARAM_SUCCESS(String URL_SYNC_PARAM_SUCCESS) {
        this.URL_SYNC_PARAM_SUCCESS = URL_SYNC_PARAM_SUCCESS;
    }

}
