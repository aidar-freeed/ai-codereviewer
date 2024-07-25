/**
 *
 */
package com.adins.mss.constant;

import android.app.Activity;
import android.content.Intent;

import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpViewDummy;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpView;
import com.adins.mss.foundation.location.LocationTrackingManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * @author michael.bw
 */
public class Global {

    private static Global sharedGlobal;
    public static boolean isMenuMoreClicked = false;
    public static final boolean AUTOLOGIN_ENABLE = true;
    public static final boolean NEW_FEATURE = true;
    public static final String MSMDB = "msmdb";
    public static final int DB_VERSION = 2;
    //Format
    public static final int MAX_LOG = 30;
    public static final int DEFAULT_MAX_LENGTH = 500;
    public static final int NOTE_MAX_LENGTH = 2048;
    public static final int ROW_PER_PAGE = 5;
    public static final String DATE_STR_FORMAT = "dd/MM/yyyy";
    public static final String DATE_STR_FORMAT1 = "dd-MM-yyyy";
    public static final String DATE_STR_FORMAT2 = "yyyyMMdd";
    public static final String DATE_STR_FORMAT3 = "dd MMM yyyy";
    public static final String DATE_STR_FORMAT4 = "yyyyMMddHHmm";
    public static final String DATE_STR_FORMAT5 = "dd-MMM-yyyy";
    public static final String DATE_STR_FORMAT_GSON = "ddMMyyyyHHmmss";
    public static final String TIME_STR_FORMAT = "HH:mm";
    public static final String TIME_STR_FORMAT2 = "HHmmss";
    public static final String DATE_TIME_STR_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String DATE_TIMESEC_STR_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_TIME_SEC_MS_STR_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_TIMESEC_TIMELINE_FORMAT = "EE, HH:mm";
    public static final String DATE_TIMESEC_TIMELINE_FORMAT_OLD = "dd MMMM, HH:mm";
    public static final String DATE_REGEX = "([0-9]{2})/([0-9]{2})/([0-9]{4})";
    public static final String DATETIME_REGEX = "([0-9]{2})/([0-9]{2})/([0-9]{4}) ([0-9]{2}):([0-9]{2})";
    public static final String TIME_REGEX = "([0-9]{2}):([0-9]{2})";
    public static final int NANOSECOND = 1000000;
    public static final int SECOND = 1000;
    public static final int MINUTE = 60 * SECOND;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;
    public static final int DAYS_KEEP_SENT_SURVEY = 1 * DAY;
    /* BUNDLE KEY */
    public static final String BUND_KEY_MODE_SURVEY = "ModeSurvey";
    public static final String BUND_KEY_MODE_SIMULASI = "ModeSimulasi";
    public static final String BUND_KEY_SURVEY_BEAN = "SurveyHeaderBean";
    public static final String BUND_KEY_FORM_BEAN = "BUND_KEY_FORM_BEAN";
    public static final String BUND_KEY_LOV_CRITERIA = "LovCriteria";
    public static final String BUND_KEY_CALL_MAIN_MENU = "CallMainMenu";
    public static final String BUND_KEY_REFRESHCOUNTER = "BUND_KEY_REFRESHCOUNTER";
    public static final String BUND_KEY_SURVEY_ERROR = "SurveyError";
    public static final String BUND_KEY_SURVEY_ERROR_MSG = "SurveyErrorMsg";
    public static final String BUND_KEY_SEND_TIME = "SendTime";
    public static final String BUND_KEY_SEND_SIZE = "SendSize";
    public static final String BUND_KEY_SEND_RESULT = "SendResult";
    public static final String BUND_KEY_TASK_ID = "TaskId";
    public static final String BUND_KEY_TASK_IS_PRINTABLE = "IsPrintable";
    public static final String BUND_KEY_IMAGE_BYTE = "Image";
    public static final String BUND_KEY_TASK = "Task";
    public static final String BUND_KEY_UUID_TASKH = "uuid_taskH";
    public static final String BUND_KEY_FORM_NAME = "formName";
    public static final String BUND_KEY_IS_NEWLEAD = "newlead";
    public static final String BUND_KEY_AGREEMENT_NO = "AgreementNo";
    //Glen Iglesias, 2 July 2014
    public static final String BUND_KEY_TASK_TYPE = "tasktype";
    public static final String BUND_KEY_DETAIL_DATA = "detaildata";
    public static final String BUND_KEY_IMAGE_PATH = "imagePath";
    public static final String BUND_KEY_ASSIGNEE_VALUE = "asigneevalue";
    public static final String BUND_KEY_ASSIGNEE_JOB = "asigneejob";
    public static final String BUND_KEY_ASSIGNEE_ID = "asigneeid";
    public static final String MAP_KEY_LOOKUP_CRITERIA = "criteria";
    public static final String MAP_KEY_LOOKUP_FILTER = "filter";
    //bangkit 27 oct 2014
    public static final String BUND_KEY_TASK_STATUS_CODE = "statuscode";
    public static final String BUND_KEY_ISERROR = "isError";
    public static final String BUND_KEY_MESSAGE = "message";
    public static final String BUND_KEY_PAGE = "page";
    /* Order */
    public static final String BUND_KEY_ORDERNO = "nomor_order";
    public static final String BUND_KEY_QUESTIONID = "question_id";
    public static final int STATUS_CODE_APPL_CLEANSING = 1153;
    /* ACTIVITY FOR RESULT */
    public static final int REQUEST_CODE_LOOKUP = 1;
    public static final int REQUEST_VOICE_NOTES = 222;
    public static final int REQUEST_LOCATIONTAGGING = 123;
    public static final int REQUEST_DRAWING_QUESTION = 124;
    public static final int REQUEST_EDIT_IMAGE = 125;
    public static final int REQUEST_LOOKUP_ANSWER = 126;
    public static final int REQUEST_CAMERA = 888;
    /* MODE */
    public static final int MODE_SURVEY_TASK = 1;
    public static final int MODE_NEW_SURVEY = 2;
    public static final int MODE_VIEW_SENT_SURVEY = 3;
    public static final int MODE_REQ_PAYMENT = 4;
    /*	NOTIF_ID*/
    public static final int NOTIF_ID_OUTSTANDING = 1;
    //Glen Iglesias
    public static final int NOTIF_ID_ORDEROUTSTANDING = 2;
    //Glen Iglesias, 2 July 2014, new mode specific to MH
    /* MH MODE */
    public static final int TASK_ORDER_ASSIGNMENT = 1;
    public static final int TASK_ORDER_REASSIGNMENT = 2;
    public static final int TASK_CHECK_ORDER = 3;
    public static final int TASK_CANCEL_ORDER = 4;
    public static final String AT_TEXT = "001";
    public static final String AT_TEXT_MULTILINE = "002";
    public static final String AT_CURRENCY = "003";
    public static final String AT_NUMERIC = "004";
    public static final String AT_DECIMAL = "005";
    public static final String AT_MULTIPLE = "006";
    public static final String AT_MULTIPLE_ONE_DESCRIPTION = "007";
    public static final String AT_MULTIPLE_W_DESCRIPTION = "008";
    public static final String AT_RADIO = "009";
    public static final String AT_RADIO_W_DESCRIPTION = "010";
    public static final String AT_DROPDOWN = "011";
    public static final String AT_DROPDOWN_W_DESCRIPTION = "012";
    public static final String AT_DATE = "013";
    public static final String AT_TIME = "014";
    public static final String AT_DATE_TIME = "015";
    public static final String AT_IMAGE = "016";
    public static final String AT_IMAGE_W_LOCATION = "017";
    public static final String AT_IMAGE_W_GPS_ONLY = "018";
    public static final String AT_LOOKUP = "019";
    public static final String AT_LOV = "019";
    public static final String AT_LOV_W_FILTER = "020";
    public static final String AT_DRAWING = "021";
    public static final String AT_RESPONDEN = "022";
    public static final String AT_DATALIST = "023";
    public static final String AT_LOCATION = "024";
    public static final String AT_TEXT_WITH_SUGGESTION = "025";
    public static final String AT_ID_CARD_PHOTO = "000";
    public static final String AT_VALIDATION = "026";
    public static final String AT_RV_MOBILE = "027";
    public static final String AT_LOOKUP_DUKCAPIL = "000";
    public static final String AT_TEXT_ONLINE = "028";
    public static final String AT_PDF = "029";
    public static final String AT_BUTTON_VIEW_URL = "031";
    public static final String AT_LOOKUP_ONLINE = "030";
    //-----
    public static final String AT_CALCULATION = "040";
    public static final String AT_LOV_IMAGE_SURVEY = "041";
    public static final String AT_DROPDOWN_RV = "042";
    public static final String AT_MULTIPLE_RADIO = "043";
    public static final String AT_GPS = "xxx";
    public static final String AT_GPS_N_LBS = "xxx";
    //DELIMITER
    public static final String DELIMETER_DATA = ";";
    public static final String DELIMETER_DATA2 = "^^"; // print
    public static final String DELIMETER_DATA3 = ",";    //Choice Filter
    public static final String DELIMETER_ROW = "|";
    public static final String DELIMETER_SUBDATA = "#";
    public static final String DELIMETER_DATA_LOOKUP = "@@@";
    public static final int FLAG_LOCATION_TRACKING = 0;
    public static final int FLAG_LOCATION_CHECKIN = 1;
    public static final int FLAG_LOCATION_CHECKOUT = 2;
    public static final int FLAG_LOCATION_CAMERA = 3;
    public static final String LOCATION_TYPE_TRACKING = "TRACKING";
    public static final String LOCATION_TYPE_CHECKIN = "CHECK_IN";
    public static final String LOCATION_TYPE_CHECKOUT = "CHECK_OUT";
    public static final String LOCATION_TYPE_CAMERA = "CHECK_OUT";
    public static final String TIMELINE_TYPE_FAILED_SENT_TASK = "Failed Sent Task";
    public static final String TIMELINE_TYPE_PUSH_NOTIFICATION = "Push Notification";
    public static final String TIMELINE_TYPE_TASK = "Task Priority";
    public static final String TIMELINE_TYPE_PENDING = "Failed Submit Task";
    public static final String TIMELINE_TYPE_UPLOADING = "Uploading Task";
    public static final String TIMELINE_TYPE_SUBMITTED = "Submitted Task";
    public static final String TIMELINE_TYPE_SAVEDRAFT = "Draft Task";
    public static final String TIMELINE_TYPE_FAILEDDRAFT = "Failed Draft Task";
    public static final String TIMELINE_TYPE_VERIFIED = "Verified Task";
    public static final String TIMELINE_TYPE_REJECTED = "Rejected Task";
    public static final String TIMELINE_TYPE_CHANGED = "Changed Task";
    public static final String TIMELINE_TYPE_APPROVED = "Approved Task";
    public static final String TIMELINE_TYPE_VERIFICATION = "Verification Task";
    public static final String FORM_TYPE_MARKETING = "Marketing";
    public static final String TIMELINE_TYPE_APPROVAL = "Approval Task";
    public static final String TIMELINE_TYPE_CHECKIN = "Attendance In";
    public static final String TIMELINE_TYPE_CHECKOUT = "Check Out";
    public static final String TIMELINE_TYPE_MESSAGE = "Messages";
    public static final String FORM_TYPE_SURVEY = "Survey";
    public static final String FORM_TYPE_COLL = "Collection";
    public static final String FORM_TYPE_ORDER = "Order";
    public static final String FORM_TYPE_APPROVAL = "Approval";
    public static final String FORM_TYPE_SIMULASI = "Simulasi";
    public static final String FORM_TYPE_VERIFICATION = "Verification";
    public static final String FORM_TYPE_KTP = "KTP";
    public static final String FLAG_BY_ORDERNUMBER = "ORDERNUMBER";
    public static final String FLAG_BY_DATE = "DATE";
    public static final String FLAG_BY_CUSTOMER_NAME = "CUSTOMERNAME";
    public static final String FLAG_BY_DAY = "DAY";
    public static final String FLAG_BY_MONTH = "MONTH";
    public static final String FLAG_FOR_CANCELORDER = "CANCELORDER";
    public static final String FLAG_FOR_ORDERASSIGNMENT = "assign";
    public static final String FLAG_FOR_ORDERREASSIGNMENT = "reassign";
    public static final String TASK_GETONE = "getOne";
    public static final String TASK_GETLIST = "getList";
    public static final String TRUE_STRING = "1";
    public static final String FALSE_STRING = "0";
    //TAG survey asset
    public static final String TAG_HOME = "HOME";
    public static final String TAG_IDENTITY = "IDENTITY";
    public static final String TAG_OFFICE = "OFFICE";
    public static final String TAG_STREET = "STREET";
    public static final String TAG_VEHICLE = "VEHICLE";
    //TAG collection
    public static final String TAG_ANGSURAN = "ANGSURAN";
    public static final String TAG_DENDA = "DENDA";
    public static final String TAG_TITIPAN = "TITIPAN";
    public static final String TAG_TOTAL = "TOTAL BAYAR";
    public static final String TAG_TOTAL_TAGIHAN = "TOTAL TAGIHAN";
    public static final String TAG_PTP = "PTP DATE";
    public static final String TAG_PEMBAYARAN = "PEMBAYARAN";
    public static final String TAG_RV_NUMBER = "RV NUMBER";
    public static final String TAG_AGREEMENT_NO = "No Agreement";
    public static final String TAG_OS_AMOUNT = "TOTAL TAGIHAN";
    public static final String TAG_OD = "OVERDUE DAYS";
    public static final String TAG_INSTALLMENT_NO = "INSTALLMENT NO";
    public static final String TAG_COLLECTION_RESULT = "COLLECTION RESULT";
    public static final String TAG_RV_NUMBER_MOBILE = "RV NUMBER MOBILE";
    public static final String TAG_CUSTOMER_JOB_ADDRESS = "JobAddress";
    public final static String TAG_PHONEVERIF_MESSAGE = "PHONE VALIDATION";
    public final static String TAG_PHONEVERIF_OTP = "OTP";
    public final static String TAG_RECOMMMENDATION_QUESTION = "RECOMMENDATION";
    public final static String TAG_SP_QUESTION = "SP";
    public static final String APPLICATION_COLLECTION = "MC";
    public static final String APPLICATION_SURVEY = "MS";
    public static final String APPLICATION_ORDER = "MO";
    public static final String FLAG_FOR_TEXT = "0";
    public static final String FLAG_FOR_IMAGE = "1";
    public static final String FLAG_FOR_LOCATION = "2";
    public static final String FLAG_FOR_IMAGE_WITH_GPS = "3";
    public static final String FLAG_FOR_REJECTEDTASK_WITHRESURVEY = "2";
    public static final String FLAG_FOR_APPROVALTASK = "1";
    public static final String FLAG_FOR_REJECTEDTASK = "0";
    public static final String TAG_STATUS = "STATUS";
    public static final String BUND_KEY_ACCOUNT_ID = "account_id";
    public static final String BUND_KEY_GROUPTASK_ID = "grouptask_id";
    public static final String BUND_KEY_PRODUCT_ID = "product_id";

    public static final String DEFAULT_EMERGENCY_PENDING_TEXT = "Lokasi kondisi darurat telah dikirimkan kepada supervisor Anda";
    public static final long DEFAULT_EMERGENCY_INTERVAL_SEND = 30000;
    public static final long DEFAULT_EMERGENCY_CANCEL_SEND = 300000;

    //Response code
    public static final int FAILED_DRAFT_TASK_CODE = 2052;

    /**
     * Max retry download treshold download
     */
    public static final String GS_RETRYDOWNLOADTD = "MC_MAXRETRYDOWNLOADTD";

    /**
     * Toleransi accuracy yang hendak disimpan (meter)
     */
    public static final String GS_TENANT_ID = "TENANT";
    /**
     * Toleransi accuracy yang hendak disimpan (meter)
     */
    public static final String GS_ACCURACY = "ACCURACY";
    /**
     * Location tracking enabled (1 = enabled)
     */
    public static final String GS_TRACKING = "PRM01_TRCK";
    /**
     * Location tracking capture interval
     */
    public static final String GS_INTERVAL_TRACKING = "PRM02_TRIN";
    /**
     * Task Result Autosend interval (seconds) (set 0 = OFF)
     */
    public static final String GS_INTERVAL_AUTOSEND = "PRM03_ASIN";
    /**
     * Task refresh interval (seconds)
     */
    public static final String GS_INTERVAL_TASKREFRESH = "PRM04_F5IN";
    /**
     * Camera capture image quality
     * encoding=jpeg&width=640&height=480&quality=normal&jpegquality=70
     */
    public static final String GS_IMG_QUALITY = "PRM06_IMGQ";
    /**
     * Camera capture image high quality
     * encoding=jpeg&width=640&height=480&quality=normal&jpegquality=70
     */
    public static final String GS_IMG_HIGH_QUALITY = "PRM06_IMGHQ";
    /**
     * application build number
     */
    public static final String GS_BUILD_NUMBER = "PRM07_VERS";
    /**
     * Enable login or not, if build version number not the latest
     */
    public static final String GS_VERS_LOGIN = "PRM08_LGIN";
    /**
     * OTA Download Link
     */
    public static final String GS_URL_DOWNLOAD = "PRM09_LINK";
    /**
     * Setting pengiriman image dipisah dengan hasil task text (1 = enabled)
     */
    public static final String GS_PARTIAL_SENT = "PRM12_PART";
    /**
     * Setting jarak perubahan lokasi yang akan dikirim (meter)
     */
    public static final String GS_DISTANCE_TRACKING = "PRM13_DIST";
    /**
     * Vibrate (OFF, SHORT 0.5S, TWICE 2S, LONG 2 Vibrations 0.2s)
     */
    public static final String GS_NOTIF_VIBRATE = "PRM14_VIB";
    /**
     * Tone (OFF, NORMAL inherit device, FORCE)
     */
    public static final String GS_NOTIF_TONE = "PRM15_TON";
    /**
     * Auto Clear Notif  (1=true, 0 false)
     */
    public static final String GS_PRINT_SIZE = "MC_PRINT_FS";
    /**
     * parameter set font size
     */
    public static final String GS_NOTIF_AUTOCLEAR = "PRM16_ACN";
    /**
     * SLA Time for calculate SLA
     */
    public static final String GS_SLA_TIME = "SLA_TIME";
    /**
     * Timeline Keeping parameter on Day
     */
    public static final String GS_TIMELINE_TIME = "PRM17_TMLN";
    /**
     * Log Keeping parameter on counter Task
     */
    public static final String GS_LOG_COUNTER = "PRM18_LOG";
    /**
     * parameter on green accuracy
     */
    public static final String GS_ACCURACY_G = "PRM19_ACC_G";
    /**
     * parameter on yellow accuracy
     */
    public static final String GS_ACCURACY_Y = "PRM20_ACC_Y";
    /**
     * parameter on Cash on Hand
     */
    public static final String GS_CASHONHAND = "MC_LIMIT_COH";
    /**
     * parameter on Input RV
     * 1 = depan
     * 0 = belakang
     */
    public static final String GS_ENABLE_RV_IN_FRONT = "MC_FLAG_RV";
    /*
    * parameter on Currency Type
     */
    public static final String GS_CURRENCY_TYPE = "MC_CURRENCY_TYPE";
    public static final String GS_TASK_LAYOUT_MS = "PRM21_TASK_LIST";
    /**
     * parameter layout tasklist
     * 1 = grid
     * 3 = list
     */

    public static final String GS_TASK_LAYOUT_MO = "PRM21_TASK_LIST";
    /**
     * parameter layout tasklist
     * 1 = grid
     * 3 = list
     */

    public static final String GS_TASK_LAYOUT_MC = "PRM21_TASK_LIST";
    /**
     * parameter layout tasklist
     * 1 = grid
     * 3 = list
     */
    public static final String GS_PRINT_LOCK_MC = "MC_PRINT_LOCK";
    /**
     * parameter print lock
     * 0 = bypass
     * 1 = lock
     */
    public static final String GS_MO_AUTO_SAVE = "MO_AUTO_SAVE";
    public static final String GS_MS_AUTO_SAVE = "MS_AUTO_SAVE";
    public static final String GS_MC_AUTO_SAVE = "MC_AUTO_SAVE";
    /**
     * NEW--------------------
     * parameter auto save
     * 0 = disable
     * 1 = enable
     */
    public static final String GS_THEME_CONFIG_SURVEY = "MS_THEME_LINK";
    public static final String GS_THEME_CONFIG_ORDER = "MO_THEME_LINK";
    public static final String GS_THEME_CONFIG_COLLECTION = "MC_THEME_LINK";
    public static final String GS_LOGO_PRINTER = "LOGO_PRINT";
    public static final String GS_COMMON_USERHELP_LINK = "COMMON_USERHELP_LINK";
    public static final String GS_MS_USERHELP_LINK = "MS_USERHELP_LINK";
    public static final String GS_MC_USERHELP_LINK = "MC_USERHELP_LINK";
    public static final String GS_MO_USERHELP_LINK = "MO_USERHELP_LINK";
    public static final String GS_INTERVAL_EMERGENCY_MC = "PRM23_AEMERGENCY";
    public static final String GS_TEXT_EMERGENCY_MC = "MC_EMERGENCY_MESSAGE";
    public static final String GS_CANCEL_EMERGENCY_MC = "PRM24_ENDEMERGENCY";
    public static final String GS_ENABLE_EMERGENCY_MC = "MC_ENABLE_EMERGENCY";
    public static final String GS_MS_JOBSPV = "MS_JOBSPV";
    public static final String GS_MAX_PRINT_COUNT_COPY = "MC_MAX_PRINT_COUNT_COPY";
    public static final String GS_PLAN_TASK = "MC_PLAN_TASK";
    public static final String GS_MC_LOYALTY_ENABLED = "MC_LOYALTI_ON";
    public static final String GS_MS_LOYALTY_ENABLED = "MS_LOYALTI_ON";
    public final static String GS_SHOW_PDF_VIEW_BUTTON = "MC_SHOW_PDF_VIEW_BUTTON";
    //2018-08-28 tambahan genset untuk answer type phone validation
    public final static String GS_INTERVAL_CONNECTION = "MC_INTERVAL_CONNECTION";

    public static final String APP_COLL_IDF = "com.adins.mss.coll";
    public static final String APP_ODR_IDF = "com.adins.mss.odr";
    public static final String APP_SVY_IDF = "com.adins.mss.svy";
    public static final String _TRCK_DAYS = "_TRCK_DAYS";
    public static final String _TRCK_START_TIME = "_TRCK_START_TIME";
    public static final String _TRCK_END_TIME = "_TRCK_END_TIME";
    public static final String IMAGE_HQ = "HQ";
    public static final String IMAGE_NQ = "NQ";
    public static final String IDF_LOGIN_ID = "LOGIN_ID";
    public static final String IDF_BRANCH_ID = "BRANCH_ID";
    public static final String IDF_UUID_USER = "UUID_USER";
    public static final String IDF_BRANCH_NAME = "BRANCH_NAME";
    public static final String IDF_JOB = "FLAG_JOB";
    public static final String IDF_DEALER_NAME = "DEALER_NAME";
    public static final String IDF_UUID_BRANCH = "UUID_BRANCH";
    public static final String IDF_DEALER_ID = "UUID_DEALER";
    public static final String IDF_ANSWER_BEAN = "ANSWER";
    public static final String IDF_THIS_YEAR = "THISYEAR";
    public static final String IDF_NOWADAYS = "NOWADAYS";
    public static final String IDF_YESTERDAY = "YESTERDAY";
    public static final String IDF_HAS_LOGGED = "HAS_LOGGED";
    public static final String IDF_URL_HEADER = "URL_HEADER";
    public static final String IDF_TENANT_ID = "TENANT_ID";
    public static final String IDF_IS_DEV = "IS_DEV";
    public static final String IDF_IS_ENCRYPT = "IS_ENCRYPT";
    public static final String IDF_IS_DECRYPT = "IS_DECRYPT";
    public static boolean IS_NEWLEAD = false;
    public static final String IDF_IS_ACCESS_TOKEN_ENABLE = "IS_ACCESS_TOKEN_ENABLE";
    public static final String IDF_CLIENT_ID = "CLIENT_ID";
    public static final String IDF_HEADER_ID = "HEADER";
    public static String Token = "";
    public static String FLAVORS = "";
    public static boolean IS_DEV = false;
    public static boolean IS_LOGIN = false;
    public static boolean VERIFICATION_BRANCH = true;
    public static boolean APPROVAL_BRANCH = true;
    public static boolean FEATURE_RESCHEDULE_SURVEY = true;
    public static boolean FEATURE_REJECT_WITH_RESURVEY = true;
    public static boolean FEATURE_REVISIT_COLLECTION = true; //new
    public static boolean IS_BYPASSROOT = true;
    public static boolean IS_DBENCRYPT = true;
    public static String MENU_REVISIT_COLLECTION = "Re-Visit"; //new
    public static String MENU_RESCHEDULE_SURVEY = "Promise To Survey";
    public static String MENU_VERIFICATION_BRANCH = "Verification by Branch";
    public static String MENU_APPROVAL_BRANCH = "Approval by Branch";
    //bong 9 apr 15 - for passing class
    public static Class printActivityClass;
    public static Class VerificationActivityClass;
    public static String APP_VERSION = "2.0";

    // general setting
    public static int BUILD_VERSION = 0;
    public static User user;
    public static int THUMBNAIL_WIDTH = 120;
    public static int THUMBNAIL_HEIGHT = 160;
    public static int TRIANGLE_SIZE = 24;
    public static Activity currentActivity = null;
    //bong 29 apr 15 - to set activity after force changePassword
    public static Intent syncIntent = null;
    public static Intent installmentSchIntent = null;
    public static Intent paymentHisIntent = null;
    public static Intent collectionActIntent = null;
    public static Intent receiptHistoryIntent = null;
    /* PRINT ITEM TYPE */
    public static String PRINT_ANSWER = "001";
    public static String PRINT_TIMESTAMP = "002";
    public static String PRINT_LOGO = "003";
    public static String PRINT_USER_NAME = "004";
    public static String PRINT_LABEL_CENTER = "005";
    public static String PRINT_LABEL_CENTER_BOLD = "006";
    public static String PRINT_LABEL = "007";
    public static String PRINT_LABEL_BOLD = "008";
    public static String PRINT_BRANCH_NAME = "009";
    public static String PRINT_BRANCH_ADDRESS = "010";
    public static String PRINT_BT_ID = "011";
    public static String PRINT_NEW_LINE = "012";
    public static String PRINT_LOGIN_ID = "013";
    public static String PRINT_ANSWER_NO = "999";
    public static String PRINT_UNIQUE_RV = "014";
    public static String PRINT_BARCODE = "015";
    public static String PRINT_LABEL_COPY = "016";
    private boolean isVerifiedByUser = false;
    public static int haveLogin = 0;
    /*
     * Gigin, flag for terminate Collect data tracking
     */
    public static boolean TRACKING_ENDED = true;
    /**
     * Flag for location type
     * 0 for tracking
     * 1 for absent in
     * 2 for absent out
     */
    public static int FLAG_LOCATION_TYPE = 0;
    public static int FLAG_TIMELINE_TYPE = 0;
    public static LocationTrackingManager LTM;
    public static int DEFAULTCONNECTIONTIMEOUT = 120000;
    public static int DOWNLOADUPDATECONNECTIONTIMEOUT = 10000;
    public static int SORTCONNECTIONTIMEOUT = 60000;
    public static int SIMULATED_REFRESH_LENGTH = 5000;
    public static double CASH_LIMIT = 0;
    public static double CASH_ON_HAND = 0;
    public static List<TaskH> listOfSentTask = new ArrayList<>();
    public static boolean isUploading = false;
    public static boolean isManualUploading = false;
    public static boolean isManualSubmit = false;
    public static boolean isGPS = false;
    public static boolean isViewer = false;
    public static Stack<Integer> positionStack = new Stack<>();
    public static boolean isApproval = false;
    public static String LAST_SYNC = null;
    public static boolean isNewlead = false;
    public static boolean isOfflineMode = false;
    public static HashMap<String, Date> TempScheme;
    private HashMap<String, Integer> TempSchemeVersion;
    private boolean SchemeIsChange = true;
    public static Intent verifyNotivIntent;
    public static Intent approvalNotivIntent;
    public static String APPROVAL_FLAG = "APPROVAL_FLAG";
    public static String VERIFICATION_FLAG = "VERIFICATION_FLAG";
    public static String MAINMENU_NOTIFICATION_KEY = "MAINMENU_NOTIFICATION_KEY";
    public static final String EXTRA_ACTION_MENU = "ACTION_MENU";
    //Nendi: 2019.06.28 | Lock from triggering new task
    public static boolean lockTask = false;
    public static String SQLITE_CIPHER_UNSUPPORTED = "A37f;vivo Y51L;CPH1803";

    public static final String CHECK_REJECT_NAME = "NAME";
    public static final String CHECK_REJECT_NIK = "NIK";
    public static final String CHECK_REJECT_NOHP = "NO HP";
    public static final String CHECK_REJECT_COMPANY_NAME = "COMPANY NAME";
    public static final String CHECK_REJECT_NPWP = "NPWP";

    public static Map<String, ArrayList<UserHelpView>> userHelpGuide = new LinkedHashMap<>();
    public static Map<String, ArrayList<UserHelpViewDummy>> userHelpDummyGuide = new LinkedHashMap<>();
    public static List<UserHelpView> activeUserHelpGuide = new ArrayList<>();
    public static final int SHOW_USERHELP_DELAY_DEFAULT = 0;
    public static final int SHOW_USERHELP_DELAY_LONG = 1000;
    public static boolean ENABLE_USER_HELP  = false;
    public static boolean ENABLE_LOC_PERMISSION_UI = false;
    public static boolean BACKPRESS_RESTRICTION = false;
    public static String REDIRECT = null;
    public static String REDIRECT_TIMELINE = "TIMELINE";

    public static String EMERGENCY_SEND_PENDING = "2";
    public static String EMERGENCY_SEND_SUCCESS = "1";
    public static String NO_EMERGENCY = "0";
    public static boolean ACRA_DISABLED = false;
    public static boolean PLAN_TASK_ENABLED = false;
    public static boolean PLAN_STARTED = false;
    public static String CURRENT_PLAN_TASK = null;
    public static boolean LOYALTI_ENABLED = false;
    public static String[] SLA_LOYALTI_JOB;

    //broadcast name/event name
    public static String FORCE_LOGOUT_ACTION = "com.adins.mss.base.FORCE_LOGOUT";

    public static synchronized Global getSharedGlobal() {
        if (sharedGlobal == null) {
            sharedGlobal = new Global();
        }
        return sharedGlobal;
    }

    public HashMap<String, Integer> getTempSchemeVersion() {
        return TempSchemeVersion;
    }

    public void setTempSchemeVersion(HashMap<String, Integer> tempSchemeVersion) {
        this.TempSchemeVersion = tempSchemeVersion;
    }

    public boolean getSchemeIsChange() {
        return SchemeIsChange;
    }

    public void setSchemeIsChange(boolean schemeIsChange) {
        this.SchemeIsChange = schemeIsChange;
    }

    public boolean getIsVerifiedByUser() {
        return isVerifiedByUser;
    }

    public void setIsVerifiedByUser(boolean isVerifiedByUser) {
        this.isVerifiedByUser = isVerifiedByUser;
    }

    public static boolean isIsManualSubmit() {
        return isManualSubmit;
    }

    public static void setIsManualSubmit(boolean isManualSubmit) {
        Global.isManualSubmit = isManualSubmit;
    }

    public static boolean isIsUploading() {
        return isUploading;
    }

    public static void setIsUploading(boolean isUploading) {
        Global.isUploading = isUploading;
    }

    public static String getREDIRECT() {
        return REDIRECT;
    }

    public static void setREDIRECT(String REDIRECT) {
        Global.REDIRECT = REDIRECT;
    }

    public static boolean isIsManualUploading() {
        return isManualUploading;
    }

    public static void setIsManualUploading(boolean isManualUploading) {
        Global.isManualUploading = isManualUploading;
    }

    public boolean getIsViewer() {
        return isViewer;
    }

    public void setIsViewer(boolean isViewer) {
        this.isViewer = isViewer;
    }

    public static boolean isLockTask() {
        return lockTask;
    }

    public static void setLockTask(boolean lockTask) {
        Global.lockTask = lockTask;
    }

    public static String getCurrentPlanTask() {
        return CURRENT_PLAN_TASK;
    }

    public static void setCurrentPlanTask(String currentPlanTask) {
        CURRENT_PLAN_TASK = currentPlanTask;
    }

    public static boolean isPlanStarted() {
        return PLAN_STARTED;
    }

    public static void setPlanStarted(boolean planStarted) {
        PLAN_STARTED = planStarted;
    }

    public static List<TaskH> getListOfSentTask() {
        return listOfSentTask;
    }

    public static void setListOfSentTask(List<TaskH> listOfSentTask) {
        Global.listOfSentTask = listOfSentTask;
    }

}