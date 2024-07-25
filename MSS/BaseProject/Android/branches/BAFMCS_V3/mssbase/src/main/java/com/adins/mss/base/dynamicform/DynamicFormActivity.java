package com.adins.mss.base.dynamicform;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.checkin.CheckInManager;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.review.QuestionReviewGenerator;
import com.adins.mss.base.util.CustomAnimatorLayout;
import com.adins.mss.base.util.GenericAsyncTask;
import com.adins.mss.base.util.GenericAsyncTask.GenericTaskInterface;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Interpolator;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.LookupDao;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.Scheme;
import com.adins.mss.foundation.camera.Camera;
import com.adins.mss.foundation.camerainapp.CameraActivity;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.image.ExifData;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.image.ViewImageActivity;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.adins.mss.foundation.questiongenerator.DateInputListener;
import com.adins.mss.foundation.questiongenerator.DynamicQuestion;
import com.adins.mss.foundation.questiongenerator.NotEqualSymbol;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.QuestionViewGenerator;
import com.adins.mss.foundation.questiongenerator.QuestionViewValidator;
import com.adins.mss.foundation.questiongenerator.TimeInputListener;
import com.adins.mss.foundation.questiongenerator.form.LabelFieldView;
import com.adins.mss.foundation.questiongenerator.form.LocationTagingView;
import com.adins.mss.foundation.questiongenerator.form.MultiOptionQuestionViewAbstract;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.adins.mss.foundation.sync.api.SynchronizeResponseLookup;
import com.adins.mss.foundation.sync.api.model.SynchronizeRequestModel;
import com.gadberry.utility.expression.Expression;
import com.gadberry.utility.expression.OperatorSet;
import com.gadberry.utility.expression.symbol.AndSymbol;
import com.gadberry.utility.expression.symbol.OrSymbol;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.google.gson.JsonSyntaxException;
import com.soundcloud.android.crop.util.Log;

import org.acra.ACRA;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.provider.MediaStore.MediaColumns.DATA;

//Glen 6 Aug 2014, implement RequestJSONFromSErverTask
//Glen 22/12/14
public class DynamicFormActivity extends DynamicQuestion implements OnClickListener, GenericTaskInterface, LocationListener {
    public static SurveyHeaderBean header;
    private static List<String> listOfIdentifier = new ArrayList<>();
    private static boolean isApproval = false;
    private static boolean isVerified = false;
    public static boolean allowImageEdit = true;
    public static String mCurrentPhotoPath;
    private int idxPosition = 0;
    private static Menu mainMenu;
    private int idxQuestion = 0;
    private LinkedHashMap<String, QuestionBean> listOfQuestion;
    //GIGIN 21/1/2014
    ArrayList<QuestionBean> visibleQuestion = new ArrayList<>();
    ArrayList<String> questionLabel = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Handler nextHandler;
    Runnable nextRunnable;
    Handler reviewHandler;
    Runnable reviewRunnable;
    LocationManager mLocation = null;
    String currentQuestionGroup;
    String currentQuestionGroupInReview;
    private int questionSize = 0;
    private LinearLayout reviewContainer;
    private RelativeLayout searchContainer;
    private ScrollView scrollView;
    private ScrollView scrollView2;
    //use for calculation
    private JexlEngine jexlEngine;
    private int mode;
    private LinearLayout questionContainer;
    private boolean isSimulasi = false;
    private boolean isFinish = false;
    private boolean isStop = false;
    private QuestionViewGenerator viewGenerator;
    //Glen 20 Oct 2014, TEMP, current page
    private List<QuestionBean> currentPageBeans = new ArrayList<>();
    private List<ViewGroup> currentPageViews = new ArrayList<>();
    //Glen 10 Oct 2014, create array to hold preview field, to check index when clicked
    private List<QuestionView> previewFields;
    private LinkedHashMap<String, List<QuestionView>> questionFields;
    private LinkedHashMap<String, List<QuestionView>> reviewFields;
    private List<QuestionView> questionGroupFieldsHeader;
    private List<QuestionView> questionGroupFieldsHeaderPreview;
    //Glen 15 Oct 2014, add preview mode, where user can edit one question and get back to previewScreen without changing the field
    private boolean inPreviewEditMode = false;
    private QuestionBean edittedQuestion;
    private boolean needQuickValidation = false;
    private boolean isSaveAndSending = false;
    private ImageButton btnSearch;
    private ImageButton btnBack;
    private ImageButton btnNext;

    private static final String SIZE = ". Size ";
    private static final String TEMP_DYNAMIC_DATA = "TempDynamicData";
    private static final String DATE_FORMAT = "yyyy.MM.dd G \'at\' HH:mm:ss z";

    private ImageButton btnSave;
    private ImageButton btnSend;
    private ImageButton btnVerified;
    private ImageButton btnReject;
    private ImageButton btnApprove;
    private ImageButton btnClose;
    private ToggleButton btnSearchBar;
    private AutoCompleteTextView txtSearch;
    private QuestionReviewGenerator reviewGenerator;
    private boolean hasLoading = false;

    public static void updateMenuIcon() {
        UpdateMenuIcon uItem = new UpdateMenuIcon();
        uItem.updateGPSIcon(mainMenu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utility.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS && Utility.checkPermissionResult(DynamicFormActivity.this, permissions, grantResults))
            bindLocationListener();
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void bindLocationListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Utility.checkPermissionGranted(DynamicFormActivity.this);
            } else {
                mLocation = (LocationManager) getSystemService(LOCATION_SERVICE);
                try {
                    if (mLocation.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                        mLocation.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                } catch (Exception e) {
                    FireCrash.log(e);

                }
            }
        } else {
            mLocation = (LocationManager) getSystemService(LOCATION_SERVICE);
            try {
                if (mLocation.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                    mLocation.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
            } catch (Exception e) {
                FireCrash.log(e);

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                List<Scheme> schemes = SchemeDataAccess.getAll(getApplicationContext());
                Global.getSharedGlobal().setTempSchemeVersion(new HashMap<String, Integer>());

                for (Scheme scheme : schemes) {
                    Global.getSharedGlobal().getTempSchemeVersion().put(scheme.getUuid_scheme(), Integer.valueOf(scheme.getForm_version()));
                }

                Global.getSharedGlobal().setSchemeIsChange(true);
                return null;
            }
        }.execute();
        bindLocationListener();
        initialize();
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
    protected void onDestroy() {
        super.onDestroy();
        Utility.freeMemory();
        Constant.setListOfQuestion(null);
        CustomerFragment.setHeader(null);
        setListOfIdentifier(null);
        listOfQuestion = null;
        idxQuestion = 0;
        idxPosition = 0;
        DynamicFormActivity.setIsApproval(false);
        setIsVerified(false);
        setAllowImageEdit(true);
        if (mLocation != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    mLocation.removeUpdates(this);
                }
            } else {
                mLocation.removeUpdates(this);
            }
        }
    }

    private void deleteLatestPictureCreate(Context context) {
        try {
            getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    DATA
                            + "='"
                            + mCurrentPhotoPath
                            + "'", null);

            File f = context.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES);
            if (!f.exists()) {
                f.mkdir();
            }
            File[] files = f.listFiles();
            Arrays.sort(files, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {

                    if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                        return -1;
                    } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                }
            });
            if(!files[0].delete()){
                throw new IOException("File failed to delete");
            }
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        Utility.freeMemory();
        if (requestCode == Utils.REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            try {
                Bundle extras = getIntent().getExtras();
                if (null != extras) {
                    int quality = Utils.picQuality;
                    int thumbHeight = Utils.picHeight;
                    int thumbWidht = Utils.picWidth;
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra(CameraActivity.PICTURE_WIDTH, thumbWidht);
                    intent.putExtra(CameraActivity.PICTURE_HEIGHT, thumbHeight);
                    intent.putExtra(CameraActivity.PICTURE_QUALITY, quality);
                    startActivityForResult(intent, Utils.REQUEST_IN_APP_CAMERA);
                }

                File file = new File(mCurrentPhotoPath);
                processImageFile(file);

            } catch (OutOfMemoryError e) {
                Toast.makeText(getApplicationContext(), getString(R.string.processing_image_error), Toast.LENGTH_SHORT).show();
                Utility.freeMemory();
            } catch (Exception e) {
                FireCrash.log(e);
                Toast.makeText(getApplicationContext(), getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Utils.REQUEST_IN_APP_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = Uri.parse(result.getStringExtra(CameraActivity.PICTURE_URI));
                    File file = new File(uri.getPath());
                    processImageFile(file);
                } catch (OutOfMemoryError e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.processing_image_error), Toast.LENGTH_SHORT).show();
                    Utility.freeMemory();
                } catch (Exception e) {
                    FireCrash.log(e);
                    Toast.makeText(getApplicationContext(), getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == Global.REQUEST_LOCATIONTAGGING && resultCode == Activity.RESULT_OK) {
            QuestionBean bean = DynamicQuestion.getQuestionInFocus();
            LocationInfo info = LocationTagingView.locationInfo;
            LocationInfo2 infoFinal = new LocationInfo2(info);
            bean.setAnswer(LocationTrackingManager.toAnswerStringShort(infoFinal));
            bean.setLocationInfo(infoFinal);
            DynamicQuestion.setTxtInFocusText(bean.getAnswer());

        } else if (requestCode == Global.REQUEST_VOICE_NOTES && resultCode == Activity.RESULT_OK) {
            byte[] voiceNotes = result.getByteArrayExtra(Global.BUND_KEY_DETAIL_DATA);
            if (voiceNotes != null && voiceNotes.length > 0) {
                getHeader().setVoice_note(voiceNotes);
            }
        }
        Utility.freeMemory();
    }

    private void processImageFile(File file) {
        try {
            String formattedSize;
            String indicatorGPS = "";
            boolean isGeoTagged;
            boolean isGeoTaggedGPSOnly;
            int[] res;
            ExifData exifData = Utils.getDataOnExif(file);
            int rotate = exifData.getOrientation();
            int quality = Utils.picQuality;
            int thumbHeight = Utils.picHeight;
            int thumbWidht = Utils.picWidth;
            QuestionBean bean = DynamicQuestion.getQuestionInFocus();
            boolean isHQ = false;
            if (bean.getImg_quality() != null && bean.getImg_quality().equalsIgnoreCase(Global.IMAGE_HQ)) {
                thumbHeight = Utils.picHQHeight;
                thumbWidht = Utils.picHQWidth;
                quality = Utils.picHQQuality;
                isHQ = true;
            }

            byte[] data = setImageData(thumbHeight,exifData,file,rotate,thumbWidht,quality,isHQ);

            if (data != null) {
                deleteLatestPictureCreate(getApplicationContext());

                deleteLatestPicture();

                DynamicQuestion.saveImage(data);

                boolean getGPS = true;

                LocationInfo locBean;

                isGeoTagged = Global.AT_IMAGE_W_LOCATION.equals(bean.getAnswer_type());
                isGeoTaggedGPSOnly = Global.AT_IMAGE_W_GPS_ONLY.equals(bean.getAnswer_type());
                if (isGeoTagged) {
                    LocationTrackingManager pm = Global.LTM;
                    if (pm != null) {
                        locBean = pm.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
                        LocationInfo2 infoFinal = new LocationInfo2(locBean);

                        if (infoFinal.getLatitude().equals("0.0") || infoFinal.getLongitude().equals("0.0")) {
                            if (infoFinal.getMcc().equals("0") || infoFinal.getMnc().equals("0")) {
                                if (bean.isMandatory()) {
                                    bean.setLocationInfo(infoFinal);
                                    String geodataError = getString(R.string.geodata_error);
                                    String[] msg = {geodataError};
                                    String alert2 = Tool.implode(msg, "\n");
                                    Toast.makeText(this, alert2, Toast.LENGTH_LONG).show();
                                    DynamicQuestion.saveImage(null);
                                    DynamicQuestion.saveImageLocation(null);
                                    getGPS = false;
                                }
                            } else {
                                bean.setAnswer(getString(R.string.coordinat_not_available));
                                bean.setLocationInfo(infoFinal);
                                indicatorGPS = bean.getAnswer();
                                if (bean.isMandatory()) {
                                    String gpsError = getString(R.string.gps_gd_error);
                                    String[] msg = {gpsError};
                                    String alert2 = Tool.implode(msg, "\n");
                                    Toast.makeText(this, alert2, Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            bean.setAnswer(LocationTrackingManager.toAnswerStringShort(infoFinal));
                            bean.setLocationInfo(infoFinal);
                            indicatorGPS = bean.getAnswer();
                        }
                    }
                }

                if (isGeoTaggedGPSOnly) {
                    LocationTrackingManager pm = Global.LTM;
                    if (pm != null) {
                        locBean = pm.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
                        LocationInfo2 infoFinal = new LocationInfo2(locBean);
                        if (infoFinal.getLatitude().equals("0.0") || infoFinal.getLongitude().equals("0.0")) {

                            if (bean.isMandatory()) {
                                bean.setLocationInfo(infoFinal);
                                String gpsError = getString(R.string.gps_error);
                                String[] msg = {gpsError};
                                String alert2 = Tool.implode(msg, "\n");
                                Toast.makeText(this, alert2, Toast.LENGTH_LONG).show();
                                DynamicQuestion.saveImage(null);
                                DynamicQuestion.saveImageLocation(null);
                                getGPS = false;
                            }

                        } else {
                            bean.setAnswer(LocationTrackingManager.toAnswerStringShort(infoFinal));
                            bean.setLocationInfo(infoFinal);
                            indicatorGPS = bean.getAnswer();
                        }
                    }
                }

                // set thumbnail
                if (DynamicQuestion.getThumbInFocus() != null && getGPS) {
                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                    res = new int[2];
                    res[0] = bm.getWidth();
                    res[1] = bm.getHeight();
                    int[] thumbRes = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
                    Bitmap bitmap = Bitmap.createScaledBitmap(bm, thumbRes[0], thumbRes[1], true);

                    long size = bean.getImgAnswer().length;
                    formattedSize = Formatter.formatByteSize(size);

                    DynamicQuestion.setThumbInFocusImage(bitmap);
                    if (isGeoTagged || isGeoTaggedGPSOnly) {

                        setThumbLocationInfoImg();

                        String text = res[0] + " x " + res[1] +
                                SIZE + formattedSize + "\n" + indicatorGPS;
                        DynamicQuestion.setTxtDetailInFocus(text);

                    } else {

                        String text = res[0] + " x " + res[1] +
                                SIZE + formattedSize;
                        DynamicQuestion.setTxtDetailInFocus(text);

                    }
                }
            }
        } catch (OutOfMemoryError e) {
            Toast.makeText(DynamicFormActivity.this, DynamicFormActivity.this.getString(R.string.processing_image_error), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            FireCrash.log(e);
            Toast.makeText(DynamicFormActivity.this, DynamicFormActivity.this.getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void setThumbLocationInfoImg(){
        try {
            Bitmap thumbLocation = BitmapFactory.decodeResource(getResources(), R.drawable.ic_absent);
            DynamicQuestion.setThumbLocationInfoImage(thumbLocation);

        } catch (Exception e) {
            FireCrash.log(e);

        }
    }

    private byte[] setImageData(int thumbHeight, ExifData exifData,File file, int rotate, int thumbWidth, int quality, boolean isHQ){
        float scale;
        int newSize;
        byte[] data;
        byte[] emptyData = {};

        boolean isHeightScale = thumbHeight >= thumbWidth;

        try{
            if (isHeightScale) {
                scale = (float) thumbHeight / exifData.getHeight();
                newSize = Math.round(exifData.getWidth() * scale);
                data = Utils.resizeBitmapFileWithWatermark(file, rotate, newSize, thumbHeight, quality, DynamicFormActivity.this, isHQ);
            } else {
                scale = (float) thumbWidth / exifData.getWidth();
                newSize = Math.round(exifData.getHeight() * scale);
                data = Utils.resizeBitmapFileWithWatermark(file, rotate, thumbWidth, newSize, quality, DynamicFormActivity.this, isHQ);
            }
            return data;
        }catch(Exception e){
            FireCrash.log(e);
            return emptyData;
        }
    }

    private void deleteLatestPicture(){
        try {
            if (!GlobalData.getSharedGlobalData().isUseOwnCamera()) {
                Utils.deleteLatestPicture(getApplicationContext());
            }
        } catch (Exception e) {
            FireCrash.log(e);
            try {
                String manufacture = android.os.Build.MANUFACTURER;
                if (manufacture.contains("LGE") && !GlobalData.getSharedGlobalData().isUseOwnCamera()) {
                    Utils.deleteLatestPictureLGE(getApplicationContext());
                }
            } catch (Exception e2) {
                FireCrash.log(e2);
                e2.printStackTrace();
            }
        }
    }

    private void initialize() {
        Bundle extras = getIntent().getExtras();
        mode = extras.getInt(Global.BUND_KEY_MODE_SURVEY);
        setHeader(null);
        setHeader(CustomerFragment.getHeader());
        listOfQuestion = new LinkedHashMap<>();

        listOfQuestion = Constant.getListOfQuestion();

        viewGenerator = new QuestionViewGenerator();
        reviewGenerator = new QuestionReviewGenerator();
        jexlEngine = new JexlEngine();
        isSimulasi = extras.getBoolean(Global.BUND_KEY_MODE_SIMULASI, false);

        try {
            if (getHeader().getPriority() != null && getHeader().getPriority().length() > 0 &&
                    (!getHeader().getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_SENT))) {
                    getHeader().setStart_date(Tool.getSystemDateTime());
                    new CustomerFragment.SendOpenReadTaskH(DynamicFormActivity.this, getHeader()).execute();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            String[] msg = {"Failed open questions,\nplease try again"};
            String alert = Tool.implode(msg, "\n");
            Toast.makeText(this, alert, Toast.LENGTH_SHORT).show();
        }

        initScreenLayout();

        try {
            if (TaskHDataAccess.STATUS_TASK_APPROVAL.equalsIgnoreCase(getHeader().getStatus())
                    || TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD.equalsIgnoreCase(getHeader().getStatus())
                    || mode == Global.MODE_VIEW_SENT_SURVEY) {

                initializeFinishScreenImageEditAndApproval();

            } else if (TaskHDataAccess.STATUS_TASK_VERIFICATION.equalsIgnoreCase(getHeader().getStatus()) ||
                    TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD.equalsIgnoreCase(getHeader().getStatus())) {

                initializeNextRunnable();

            } else if (TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(getHeader().getStatus())) {

                disableButtons();

            } else {
                loadDynamicForm();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            Toast.makeText(getApplicationContext(), getString(R.string.request_error), Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }

    private void disableButtons(){
        try {
            hasLoading = true;
            btnBack.setClickable(false);
            btnNext.setClickable(false);
            btnSend.setClickable(false);
            btnSave.setClickable(false);
            btnSearch.setClickable(false);

            nextRunnable = new Runnable() {
                @Override
                public void run() {
                    nextHandler.postDelayed(nextRunnable, 400);
                    if (!doNext(false) || isStop) {
                        removeNextCallback();
                        synchronized (this) {
                            this.notifyAll();
                        }
                    } else if (idxPosition >= getHeader().getLast_saved_question()) {
                        removeNextCallback();
                        isStop = true;
                        synchronized (this) {
                            this.notifyAll();
                        }
                    }
                }
            };
            nextHandler = new Handler();
            nextHandler.postDelayed(nextRunnable, 500);
        } catch (Exception e) {
            FireCrash.log(e);
            loadDynamicForm();
        }
    }

    private void initializeFinishScreenImageEditAndApproval(){
        try {
            setAllowImageEdit(false);
            setIsApproval(true);
            btnClose.setClickable(false);
            isFinish = true;
            if (mode != Global.MODE_VIEW_SENT_SURVEY) {
                setIsVerified(true);
            }
            showFinishScreen();
        } catch (Exception e) {
            FireCrash.log(e);
            loadDynamicForm();
        }
    }

    private void initializeNextRunnable(){
        try {
            setAllowImageEdit(false);
            setIsVerified(true);
            hasLoading = true;
            nextRunnable = new Runnable() {
                @Override
                public void run() {
                    nextHandler.postDelayed(nextRunnable, 400);
                    if (!doNext(false) || isStop) {
                        removeNextCallback();
                        btnVerified.setClickable(false);
                        btnVerified.setImageResource(R.drawable.ic_verified_off);
                        btnReject.setClickable(false);
                        btnReject.setImageResource(R.drawable.ic_reject_off);
                    } else if (isFinish)
                        removeNextCallback();
                }
            };
            nextHandler = new Handler();
            nextHandler.postDelayed(nextRunnable, 500);
        } catch (Exception e) {
            FireCrash.log(e);
            loadDynamicForm();
        }
    }

    public void restoreDataToTemporary(Context context) {
        try {
            if (listOfQuestion == null) {
                ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(context,
                        TEMP_DYNAMIC_DATA, Context.MODE_PRIVATE);
                String sTempDynamicdata = sharedPref.getString("SelectedDynamicData", "");
                if (sTempDynamicdata != null) {
                    new RestoreGlobalData().execute();
                    DataForDynamicQuestion tempDynamicdata = GsonHelper.fromJson(sTempDynamicdata, DataForDynamicQuestion.class);
                    mode = tempDynamicdata.getMode();
                    setHeader(tempDynamicdata.getSelectedHeader());
                    CustomerFragment.setHeader(getHeader());
                    for (QuestionBean bean : tempDynamicdata.getListOfQuestion()) {
                        if (listOfQuestion == null)
                            listOfQuestion = new LinkedHashMap<>();
                        if (getListOfIdentifier() == null)
                            setListOfIdentifier(new ArrayList<String>());
                        listOfQuestion.put(bean.getIdentifier_name(), bean);
                        getListOfIdentifier().add(bean.getIdentifier_name());
                    }
                    if (listOfQuestion == null)
                        listOfQuestion = new LinkedHashMap<>();
                    Constant.setListOfQuestion(listOfQuestion);
                    Constant.setSelectedForm(tempDynamicdata.getSelectedForm());
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    public void loadDraftData() {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(getApplicationContext(),
                        "", getString(R.string.progressWait), true);

            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    int i = 0;
                    while (i < getHeader().getLast_saved_question()) {
                        if (!doNext(true))
                            break;
                        i++;
                    }
                    return null;
                } catch (Exception e) {
                    FireCrash.log(e);
                    if (Global.IS_DEV)
                        e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute() {
                super.onPostExecute(null);
                if (progressDialog != null && progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
            }
        }.execute();
    }

    private void initScreenLayout() {

        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSave = (ImageButton) findViewById(R.id.btnSave);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        btnVerified = (ImageButton) findViewById(R.id.btnVerified);
        btnReject = (ImageButton) findViewById(R.id.btnReject);
        btnApprove = (ImageButton) findViewById(R.id.btnApprove);
        btnClose = (ImageButton) findViewById(R.id.btnClose);
        btnSearchBar = (ToggleButton) findViewById(R.id.btnSearchBar);

        adapter = new ArrayAdapter<>(this, R.layout.autotext_list, questionLabel);
        txtSearch = (AutoCompleteTextView) findViewById(R.id.autoCompleteSearch);
        txtSearch.setAdapter(adapter);
        txtSearch.setDropDownBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext() ,R.drawable.actionbar_background));
        txtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocused) {

                if (hasFocused) {
                    adapter.notifyDataSetChanged();
                    txtSearch.setAdapter(adapter);
                }
            }
        });

        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnVerified.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnReject.setOnClickListener(this);
        btnApprove.setOnClickListener(this);
        btnSearchBar.setOnClickListener(this);
        btnClose.setOnClickListener(this);

        searchContainer = (RelativeLayout) findViewById(R.id.searchLayout);
        searchContainer.setVisibility(View.GONE);

        LinearLayout sendLayout = (LinearLayout) findViewById(R.id.btnSendLayout);
        LinearLayout verifyLayout = (LinearLayout) findViewById(R.id.btnVerifiedLayout);
        LinearLayout rejectLayout = (LinearLayout) findViewById(R.id.btnRejectLayout);
        LinearLayout approveLayout = (LinearLayout) findViewById(R.id.btnApproveLayout);
        LinearLayout nextLayout = (LinearLayout) findViewById(R.id.btnNextLayout);
        LinearLayout saveLayout = (LinearLayout) findViewById(R.id.btnSaveLayout);
        LinearLayout searchLayout = (LinearLayout) findViewById(R.id.btnSearchLayout);
        LinearLayout closeLayout = (LinearLayout) findViewById(R.id.btnCloseLayout);

        try {
            if (TaskHDataAccess.STATUS_TASK_VERIFICATION.equalsIgnoreCase(getHeader().getStatus()) ||
                    TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD.equalsIgnoreCase(getHeader().getStatus())) {
                sendLayout.setVisibility(View.GONE);
                saveLayout.setVisibility(View.GONE);
                approveLayout.setVisibility(View.GONE);
                //ganti ke halaman baaru
                if (!Global.NEW_FEATURE) {
                    rejectLayout.setVisibility(View.VISIBLE);
                    verifyLayout.setVisibility(View.VISIBLE);
                }

            }
            if (TaskHDataAccess.STATUS_TASK_APPROVAL.equalsIgnoreCase(getHeader().getStatus()) ||
                    TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD.equalsIgnoreCase(getHeader().getStatus())) {
                sendLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.GONE);
                verifyLayout.setVisibility(View.GONE);
                saveLayout.setVisibility(View.GONE);
                if (!Global.NEW_FEATURE) {
                    nextLayout.setVisibility(View.GONE);
                    rejectLayout.setVisibility(View.VISIBLE);
                    approveLayout.setVisibility(View.VISIBLE);
                }
                searchContainer.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            FireCrash.log(e);

        }
        if (mode == Global.MODE_VIEW_SENT_SURVEY) {
            nextLayout.setVisibility(View.GONE);
            sendLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.GONE);
            verifyLayout.setVisibility(View.GONE);
            rejectLayout.setVisibility(View.GONE);
            approveLayout.setVisibility(View.GONE);
            saveLayout.setVisibility(View.GONE);
            closeLayout.setVisibility(View.VISIBLE);
            searchContainer.setVisibility(View.GONE);
        }
        if (isSimulasi) {
            saveLayout.setVisibility(View.GONE);
            sendLayout.setVisibility(View.GONE);
        }

        if (questionGroupFieldsHeader == null) questionGroupFieldsHeader = new ArrayList<>();
        questionGroupFieldsHeader.clear();
    }

    private void loadOptionsToView(MultiOptionQuestionViewAbstract view) {
        List<OptionAnswerBean> options = getOptionsForQuestion(view.getQuestionBean());
        view.setOptions(this, options);
    }

    public boolean isRelevantMandatory2(String relevantExpression, QuestionBean question) {
        boolean result = false;
        String convertedExpression = relevantExpression;        //make a copy of
        if (question.isMandatory()) {
            return true;
        } else if (convertedExpression == null || convertedExpression.length() == 0) {
            return false;
        } else {
            boolean needReplacing = true;
            while (needReplacing) {

                //replace application modifier
                convertedExpression = replaceModifiers(convertedExpression);

                int idxOfOpenBrace = convertedExpression.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    int idxOfCloseBrace = convertedExpression.indexOf('}');
                    String identifier = convertedExpression.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                    int idxOfOpenAbs = identifier.indexOf("$");
                    if (idxOfOpenAbs != -1) {
                        String finalIdentifier = identifier.substring(idxOfOpenAbs + 1);
                        String flatAnswer = "";

                        if (finalIdentifier.equals(Global.IDF_LOGIN_ID)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                            int idxOfOpenAt = flatAnswer.indexOf('@');
                            if (idxOfOpenAt != -1) {
                                flatAnswer = flatAnswer.substring(0, idxOfOpenAt);
                            }
                        } else if (finalIdentifier.equals(Global.IDF_BRANCH_ID)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getBranch_id();
                        } else if (finalIdentifier.equals(Global.IDF_BRANCH_NAME)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getBranch_name();
                        } else if (finalIdentifier.equals(Global.IDF_UUID_USER)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                        } else if (finalIdentifier.equals(Global.IDF_JOB)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getFlag_job();
                        } else if (finalIdentifier.equals(Global.IDF_DEALER_NAME)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getDealer_name();
                        }


                        if (flatAnswer != null && flatAnswer.length() > 0) {
                            convertedExpression = convertedExpression.replace("{" + identifier + "}", flatAnswer);
                        } else {            //if there's no answer, just hide the question
                            return false;
                        }

                    } else {
                        QuestionBean bean = listOfQuestion.get(identifier);

                        if (bean != null) {
                            String flatAnswer = QuestionBean.getAnswer(bean);

                            if (Tool.isOptions(bean.getAnswer_type())) {
                                try {
                                    flatAnswer = bean.getSelectedOptionAnswers().get(0).getCode();
                                } catch (Exception e) {
                                    FireCrash.log(e);

                                }
                            }

                            if (flatAnswer != null && flatAnswer.length() > 0) {
                                //Glen 22 Oct 2014, enable multi-depth checking for 'multiple' question
                                //NOTE: though it's possible to just iterate on flatAnswer substrings, we prefer to stay on method if size is 1
                                String[] answers = Tool.split(flatAnswer, Global.DELIMETER_DATA);
                                if (answers.length == 1) {
                                    convertedExpression = convertedExpression.replace("{" + identifier + "}", answers[0]);
                                } else {
                                    //NOTE: going into in-depth loop, won't go outside of this 'else'
                                    for (int i = 0; i < answers.length; i++) {
                                        String convertedSubExpression = convertedExpression.replace("{" + identifier + "}", answers[i]);
                                        boolean isVisible = isQuestVisibleIfRelevant(convertedSubExpression, question);
                                        if (isVisible) {
                                            return true;
                                        }
                                    }
                                    return false;
                                }
                            } else {            //if there's no answer, just hide the question
                                return false;
                            }
                        } else {
                            convertedExpression = convertedExpression.replace("{" + identifier + "}", "\"\"");
                        }
                    }
                }
                //moved up

                //no more replacing needed
                else {
                    needReplacing = false;
                }

            }
            try {
                OperatorSet opSet = OperatorSet.getStandardOperatorSet();
                opSet.addOperator("!=", NotEqualSymbol.class);
                opSet.addOperator("and", AndSymbol.class);
                opSet.addOperator("or", OrSymbol.class);
                Expression exp = new Expression(convertedExpression);
                exp.setOperatorSet(opSet);
                result = exp.evaluate().toBoolean();
                return result;
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
                return false;
            }

        }
    }

    public boolean isRelevantMandatory(String expression) {
        boolean flag;
        boolean finalResult = false;
        int i;
        int start = 0;
        int end;
        boolean tempResult;
        String tempString;
        Deque<Boolean> results = new ArrayDeque<>();
        Deque<Boolean> operators = new ArrayDeque<>();
        expression = expression.replaceAll("\\s+", "");
        if (expression.equals("")) return false;
        else if (expression.charAt(1) == '{') {
            for (i = 0; i < expression.length(); i++) {
                switch (expression.charAt(i)) {
                    case '{':
                        if (expression.charAt(i + 1) == '{') {
                            i++;
                            start = i;
                        }
                        break;
                    case '}':
                        if (i == expression.length() - 1) {
                            end = i;
                            tempString = expression.substring(start, end);
                            tempResult = isRelevantMandatorySubString(tempString);
                            results.push(tempResult);
                        } else {
                            if (expression.charAt(i + 1) == '&' || expression.charAt(i + 1) == '|') {
                                end = i;
                                tempString = expression.substring(start, end);
                                tempResult = isRelevantMandatorySubString(tempString);
                                results.push(tempResult);
                            }
                        }

                        break;
                    case '|':
                        if (expression.charAt(i - 1) == '}') operators.push(false);

                        i++;
                        break;
                    case '&':
                        if (expression.charAt(i - 1) == '}') operators.push(true);
                        i++;
                        break;
                    default:
                        break;
                }
            }
            flag = true;
            while (!results.isEmpty()) {
                if (flag) {
                    finalResult = results.pop();
                    if (results.isEmpty()) return finalResult;
                    else {
                        tempResult = results.pop();
                        if (Boolean.TRUE.equals(operators.pop())) finalResult = finalResult && tempResult;
                        else finalResult = finalResult || tempResult;
                        flag = false;
                    }
                } else {
                    tempResult = results.pop();
                    if (Boolean.TRUE.equals(operators.pop())) finalResult = finalResult && tempResult;
                    else finalResult = finalResult || tempResult;
                }
            }
            return finalResult;
        } else return isRelevantMandatorySubString(expression);
    }

    public boolean isRelevantMandatorySubString(String expression) {
        Deque<Boolean> results = new ArrayDeque<>();
        Deque<Boolean> operators = new ArrayDeque<>();
        String temp = "";
        String ans1 = "";
        String ans2 = "";
        QuestionBean tempbean;
        boolean flag = false;
        boolean finalResult = false;
        boolean tempResult1 = false;
        int i;
        int start = 0;
        int end;
        int operator1 = 0;
        String formattedExpression = expression.replaceAll("\\s+", "");
        formattedExpression = formattedExpression.replace(",", ".");
        for (i = 0; i < formattedExpression.length(); i++) {
            switch (formattedExpression.charAt(i)) {
                case '{':
                    start = i + 1;
                    break;
                case '}':
                    end = i;
                    temp = formattedExpression.substring(start, end).toUpperCase();
                    if ((getQuestionBeanForIdentifier(temp) == null)) {
                        ans2 = "";
                    } else {
                        tempbean = getQuestionBeanForIdentifier(temp);
                        if (tempbean.getAnswer() != null && !tempbean.getAnswer().equals("")) {
                            ans2 = tempbean.getAnswer();
                        } else if (tempbean.getLovCode() != null && !tempbean.getLovCode().equals("")) {
                            ans2 = tempbean.getLovCode();
                        } else ans2 = "";
                    }
                    start = 0;
                    break;
                case '=':
                    i++;
                    operator1 = 0;
                    break;
                case '!':
                    i++;
                    operator1 = 1;
                    break;
                case '>':
                    if (formattedExpression.charAt(i + 1) == '=') {
                        i++;
                        operator1 = 2;
                    } else operator1 = 3;
                    break;
                case '<':
                    if (formattedExpression.charAt(i + 1) == '=') {
                        i++;
                        operator1 = 4;
                    } else operator1 = 5;
                    break;
                case '\'':
                    if (flag) {
                        flag = false;
                        end = i;
                        ans1 = formattedExpression.substring(start, end);
                        if (operator1 == 0) {
                            results.push(ans1.equals(ans2));
                        } else if (operator1 == 1) results.push(!ans1.equals(ans2));
                        else if (operator1 == 2)
                            results.push(Double.parseDouble(ans2.replace(",", ".")) >= Double.parseDouble(ans1.replace(",", ".")));
                        else if (operator1 == 3)
                            results.push(Double.parseDouble(ans2.replace(",", ".")) > Double.parseDouble(ans1.replace(",", ".")));
                        else if (operator1 == 4)
                            results.push(Double.parseDouble(ans2.replace(",", ".")) <= Double.parseDouble(ans1.replace(",", ".")));
                        else if (operator1 == 5)
                            results.push(Double.parseDouble(ans2.replace(",", ".")) < Double.parseDouble(ans1.replace(",", ".")));
                        start = 0;
                        ans2 = "";
                    } else {
                        start = i + 1;
                        flag = true;
                    }
                    break;
                case '&':
                    i++;
                    operators.push(true);
                    break;
                case '|':
                    i++;
                    operators.push(false);
                    break;
                default:
                    break;
            }

        }
        flag = true;
        while (!results.isEmpty()) {
            if (flag) {
                finalResult = results.pop();
                if (results.isEmpty()) return finalResult;
                else {
                    tempResult1 = results.pop();
                    if (Boolean.TRUE.equals(operators.pop())) finalResult = finalResult && tempResult1;
                    else finalResult = finalResult || tempResult1;
                    flag = false;
                }
            } else {
                tempResult1 = results.pop();
                if (Boolean.TRUE.equals(operators.pop())) finalResult = finalResult && tempResult1;
                else finalResult = finalResult || tempResult1;
            }
        }
        return finalResult;
    }

    private boolean loadDynamicForm() {
        boolean isLastQuestion = true;

        int start = -1;
        int x = 0;
        int end = listOfQuestion.size();

        try {
            x = questionContainer.getChildCount();
            if (x != 0) {
                QuestionView questionView = (QuestionView) questionContainer.getChildAt(x - 1);
                if (questionView.isTitleOnly())
                    start = questionView.getSequence() + 1;
                else
                    start = questionView.getSequence();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                Log.e(e.toString());
        }

        start++;
        if (questionFields == null) {
            questionFields = new LinkedHashMap<>();
        }

        for (; start < end; start++) {
            QuestionBean bean = listOfQuestion.get(getListOfIdentifier().get(start));
            questionSize = start + 1;

            String newQuestionGroup = bean.getQuestion_group_id();

            idxPosition++;
            if (bean.isVisible()) {

                String relevantExpression = bean.getRelevant_question();
                String relevantMandatory = bean.getRelevant_mandatory();
                if (relevantExpression == null) relevantExpression = "";
                if (relevantMandatory == null) relevantMandatory = "";
                if (isRelevantMandatory(relevantMandatory)) {
                    bean.setMandatory(true);
                    bean.setReadOnly(false);
                }
                if (isQuestVisibleIfRelevant(relevantExpression, bean)) {
                    bean.setVisible(true);
                    if (currentQuestionGroup == null || !newQuestionGroup.equals(currentQuestionGroup)) {
                        currentQuestionGroup = newQuestionGroup;
                        QuestionGroup group = new QuestionGroup(bean);
                        QuestionView view = this.getQuestionViewWithIcon(bean, start + 1, R.drawable.icon_camera, group);
                        if(view != null){
                            view.setQuestionGroup(group);
                            ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
                            anim.setDuration(200);
                            anim.setFillAfter(true);
                            view.startAnimation(anim);
                            view.setOnClickListener(DynamicFormActivity.this);
                            questionContainer.addView(view, LayoutParams.MATCH_PARENT,
                                    LayoutParams.WRAP_CONTENT);
                            questionGroupFieldsHeader.add(view);
                        }
                    }
                    actionOnQuestionGroupClicked(questionFields, newQuestionGroup, false, false);

                    if (null != bean.getCalculate() && !"".equalsIgnoreCase(bean.getCalculate())) {
                        String resultCalculate = doCalculate(bean);
                        bean.setAnswer(resultCalculate);
                    }

                    QuestionView view = this.getQuestionViewWithIcon(bean, start + 1, R.drawable.icon_camera, null);
                    if(view == null){
                        return false;
                    }
                    try {
                        view.setSequence(start);
                    } catch (NullPointerException e) {
                        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(this,
                                TEMP_DYNAMIC_DATA, Context.MODE_PRIVATE);
                        int tempStartPosition = sharedPref.getInt("StartPosition", 0);
                        start = tempStartPosition;
                        view.setSequence(start);
                    }

                    view.setFocusableInTouchMode(true);
                    view.requestFocus();

                    ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
                    anim.setDuration(200);
                    anim.setFillAfter(true);
                    view.startAnimation(anim);
                    String answerType = bean.getAnswer_type();

                    if (Tool.isOptions(answerType)) {
                        if (bean.getOptionAnswers() == null || bean.getOptionAnswers().isEmpty() || bean.getOptionRelevances().length > 0) {

                            MultiOptionQuestionViewAbstract multiOptionView = (MultiOptionQuestionViewAbstract) view;
                            loadOptionsToView(multiOptionView);
                        }
                    } else if (Global.AT_CALCULATION.equals(answerType)) {
                        setCalculationResult(bean);
                        ((LabelFieldView) view).updateValue();
                    }


                    questionContainer.addView(view, LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    view.getChildAt(1).requestFocus();
                    isLastQuestion = false;

                    List<QuestionView> tempList = questionFields.get(bean.getQuestion_group_id());
                    if (tempList != null) {
                        tempList.add(view);
                    } else {
                        tempList = new ArrayList<>();
                        tempList.add(view);
                    }
                    questionFields.put(bean.getQuestion_group_id(), tempList);

                    currentPageBeans.add(bean);
                    currentPageViews.add(view);
                    visibleQuestion.add(bean);
                    questionLabel.add(bean.getQuestion_label());
                    adapter.notifyDataSetChanged();
                    if (!bean.isRelevanted() && !hasLoading) {
                        if (!bean.isMandatory() && !isStop || (QuestionBean.isHaveAnswer(bean) && !isStop && (!Tool.isOptions(bean.getAnswer_type())))) {
                            loadQuestionView();
                        }
                        isStop = false;
                    }
                    break;
                } else {
                    bean.setVisible(false);
                    if (questionLabel.size() > start)
                        questionLabel.remove(questionLabel.size() - 1);
                    adapter.notifyDataSetChanged();
                }
            } else {
                if (start == 0) {
                    doNext(false);
                } else {
                    String relevantExpression = bean.getRelevant_question();
                    if (isQuestVisibleIfRelevant(relevantExpression, bean)) {
                        QuestionSet tempQuestion = QuestionSetDataAccess.getOne(getApplicationContext(), getHeader().getUuid_scheme(), bean.getQuestion_id(), bean.getQuestion_group_id());
                        if (tempQuestion != null) {
                            if (tempQuestion.getIs_visible().equals(Global.TRUE_STRING)) {
                                bean.setVisible(true);

                                if (currentQuestionGroup == null || !newQuestionGroup.equals(currentQuestionGroup)) {
                                    currentQuestionGroup = newQuestionGroup;
                                    QuestionGroup group = new QuestionGroup(bean);
                                    QuestionView view = this.getQuestionViewWithIcon(bean, start + 1, R.drawable.icon_camera, group);
                                    view.setQuestionGroup(group);
                                    ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
                                    anim.setDuration(200);
                                    anim.setFillAfter(true);
                                    view.startAnimation(anim);
                                    view.setOnClickListener(DynamicFormActivity.this);
                                    questionContainer.addView(view, LayoutParams.MATCH_PARENT,
                                            LayoutParams.WRAP_CONTENT);
                                    questionGroupFieldsHeader.add(view);
                                }
                                actionOnQuestionGroupClicked(questionFields, newQuestionGroup, false, false);
                                QuestionView view = this.getQuestionViewWithIcon(bean, start + 1, R.drawable.icon_camera, null);
                                view.setSequence(start);

                                view.setFocusableInTouchMode(true);
                                view.requestFocus();

                                ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
                                anim.setDuration(200);
                                anim.setFillAfter(true);
                                view.startAnimation(anim);
                                String answerType = bean.getAnswer_type();

                                if (Tool.isOptions(answerType)) {
                                    if (bean.getOptionAnswers() == null || bean.getOptionAnswers().isEmpty() || bean.getOptionRelevances().length > 0) {

                                        MultiOptionQuestionViewAbstract multiOptionView = (MultiOptionQuestionViewAbstract) view;
                                        loadOptionsToView(multiOptionView);

                                    }
                                } else if (Global.AT_CALCULATION.equals(answerType)) {
                                    setCalculationResult(bean);
                                    ((LabelFieldView) view).updateValue();
                                }

                                questionContainer.addView(view, LayoutParams.MATCH_PARENT,
                                        LayoutParams.WRAP_CONTENT);
                                view.getChildAt(1).requestFocus();
                                isLastQuestion = false;

                                List<QuestionView> tempList = questionFields.get(bean.getQuestion_group_id());
                                if (tempList != null) {
                                    tempList.add(view);
                                } else {
                                    tempList = new ArrayList<>();
                                    tempList.add(view);
                                }
                                questionFields.put(bean.getQuestion_group_id(), tempList);


                                currentPageBeans.add(bean);
                                currentPageViews.add(view);
                                visibleQuestion.add(bean);
                                questionLabel.add(bean.getQuestion_label());
                                adapter.notifyDataSetChanged();
                                if (!bean.isRelevanted() && !hasLoading) {
                                    if (!bean.isMandatory() && !isStop || (QuestionBean.isHaveAnswer(bean) && !isStop && (!Tool.isOptions(bean.getAnswer_type())))) {
                                        loadQuestionView();
                                    }
                                    isStop = false;
                                }
                                break;
                            } else {
                                bean.setVisible(false);
                            }
                        }
                    }
                }

            }
            if (end == start) {
                isLastQuestion = true;
            }
        }
        return isLastQuestion;
    }

    private void loadQuestionView() {
        final Handler control = new Handler();

        control.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDynamicForm();
            }
        }, 400);
    }

    private void deleteQuestionGroupView() {
        final int pos = questionContainer.getChildCount() - 1;
        final QuestionView view = (QuestionView) questionContainer.getChildAt(pos);
        ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0);
        anim.setDuration(200);
        anim.setFillAfter(true);
        view.startAnimation(anim);
        if (view.getQuestionGroup() != null) {
            questionContainer.removeViewAt(pos);
            questionGroupFieldsHeader.remove(view);
            questionFields.remove(view.getQuestionGroup().getQuestion_group_id());
        }
    }

    private boolean loadBackDynamicForm() {
        boolean isCurrentPage = true;

        final int pos = questionContainer.getChildCount() - 1;
        final QuestionView minView = (QuestionView) questionContainer.getChildAt(pos - 1);
        final QuestionView view = (QuestionView) questionContainer.getChildAt(pos);
        if (view.isTitleOnly()) {
            deleteQuestionGroupView();
            return false;
        }
        ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0);
        anim.setDuration(200);
        anim.setFillAfter(true);
        view.startAnimation(anim);
        questionContainer.removeViewAt(pos);
        if (!visibleQuestion.isEmpty()) {
            visibleQuestion.remove(visibleQuestion.size() - 1);
        }
        if (!questionLabel.isEmpty()) {
            questionLabel.remove(questionLabel.size() - 1);
        }
        adapter.notifyDataSetChanged();
        questionSize--;
        if (minView.isTitleOnly()) {
            deleteQuestionGroupView();
            return true;
        }

        minView.setFocusableInTouchMode(true);
        minView.requestFocus();
        QuestionView questionView = (QuestionView) questionContainer.getChildAt(questionContainer.getChildCount() - 1);
        questionView.getChildAt(1).requestFocus();
        return isCurrentPage;
    }

    private String doCalculate(QuestionBean bean) {
        String formula = bean.getCalculate();
        String expression = formula;
        String total = "0";

        List<QuestionBean> questionList = new ArrayList<>();
        String resultformula2 = formula.substring(0, formula.indexOf("for"));
        resultformula2 = resultformula2.replace("_var = 0", "");
        resultformula2 = resultformula2.replace("var ", "");
        resultformula2 = resultformula2.replace(" ", "");
        String[] nFormula2 = resultformula2.split(";");
        for (String idf : nFormula2) {
            questionList.add(listOfQuestion.get(idf.toUpperCase()));
        }


        JexlContext context = new MapContext();

        context.set("listOfQuestion", questionList);
        context.set("qBean", new QuestionBean(bean));
        context.set("bean", bean);
        context.set("result", total);

        Object value = jexlEngine.createScript(expression).execute(context);

        if (value != null) {
            try {
                return String.format(Locale.US, "%.2f", value);
            } catch (Exception e) {
                FireCrash.log(e);
                return value.toString();
            }
        } else {
            return "0";
        }


    }

    // Glen 17 Oct 2014
    protected QuestionBean getQuestionBeanForIdentifier(String identifier) {
        return listOfQuestion.get(identifier);
    }

    protected String replaceModifiers(String sourceString) {
        //replace branch modifier
        String branch = GlobalData.getSharedGlobalData().getUser().getBranch_id();
        //replace user modifier
        String user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        return sourceString.replace(QuestionBean.PLACEMENT_KEY_BRANCH, branch).replace(QuestionBean.PLACEMENT_KEY_USER, user);
    }

    /**
     * Get Options from Database and check filter from choice filters.
     * ex : {identifier1},{identifier2}
     * ex : {$LOGIN_ID} : for get login Id from active User
     * ex : {$UUID_USER} : for get uuid user from active User
     * ex : {$BRANCH_ID} : for get branch Id from active User
     * ex : {$BRANCH_NAME} : for get branch name from active User
     * ex : {$FLAG_JOB} : for get flag job from active User
     * ex : {$DEALER_NAME} : for get dealer name from active User
     *
     * @param bean : Question Bean
     * @return
     */
    protected List<OptionAnswerBean> getOptionsForQuestion(QuestionBean bean) {

        //Gigin, validasi for Choice Filter
        List<String> filters = new ArrayList<>();
        int constraintAmount = 0;
        if (bean.getChoice_filter() != null) {
            String[] tempfilters = Tool.split(bean.getChoice_filter(), Global.DELIMETER_DATA3);

            for (String newFilter : tempfilters) {
                int idxOfOpenBrace = newFilter.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    int idxOfCloseBrace = newFilter.indexOf('}');
                    String tempIdentifier = newFilter.substring(idxOfOpenBrace + 1, idxOfCloseBrace).toUpperCase();
                    if (tempIdentifier.contains("%")) {
                        filters.add(tempIdentifier);
                    } else {
                        int idxOfOpenAbs = tempIdentifier.indexOf("$");
                        if (idxOfOpenAbs != -1) {
                            String finalIdentifier = tempIdentifier.substring(idxOfOpenAbs + 1);
                            if (finalIdentifier.equals(Global.IDF_LOGIN_ID)) {
                                String loginId = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                                int idxOfOpenAt = loginId.indexOf('@');
                                if (idxOfOpenAt != -1) {
                                    loginId = loginId.substring(0, idxOfOpenAt);
                                }
                                filters.add(loginId);
                            } else if (finalIdentifier.equals(Global.IDF_BRANCH_ID)) {
                                String branchId = GlobalData.getSharedGlobalData().getUser().getBranch_id();
                                filters.add(branchId);
                            } else if (finalIdentifier.equals(Global.IDF_BRANCH_NAME)) {
                                String branchName = GlobalData.getSharedGlobalData().getUser().getBranch_name();
                                filters.add(branchName);
                            } else if (finalIdentifier.equals(Global.IDF_UUID_USER)) {
                                String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                                filters.add(uuidUser);
                            } else if (finalIdentifier.equals(Global.IDF_JOB)) {
                                String job = GlobalData.getSharedGlobalData().getUser().getFlag_job();
                                filters.add(job);
                            } else if (finalIdentifier.equals(Global.IDF_DEALER_NAME)) {
                                String dealerName = GlobalData.getSharedGlobalData().getUser().getDealer_name();
                                filters.add(dealerName);
                            }
                            constraintAmount++;
                        } else {
                            QuestionBean bean2 = listOfQuestion.get(tempIdentifier);
                            if (bean2 != null) {
                                if (Global.AT_TEXT_WITH_SUGGESTION.equals(bean2.getAnswer_type())) {
                                    filters.add(bean2.getAnswer());
                                } else {
                                    for (OptionAnswerBean answerBean : bean2.getSelectedOptionAnswers()) {
                                        filters.add(answerBean.getCode());
                                    }
                                }
                                bean2.setRelevanted(true);
                                constraintAmount++;
                            }
                        }
                    }
                }
            }
        }
        List<OptionAnswerBean> optionAnswers = getLookupFromDB(bean, filters);

        if (optionAnswers == null || optionAnswers.isEmpty()) {
            try {
                isStop = true;
                new GetLookupOnDemand(bean.getLov_group(), filters, constraintAmount).execute();

            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV)
                    e.printStackTrace();
            }
            optionAnswers = getLookupFromDB(bean, filters);
        }

        return optionAnswers;
    }

    protected List<OptionAnswerBean> getOptionsForQuestion2(QuestionBean bean) {

        //Gigin, validasi for Choice Filter
        List<String> filters = new ArrayList<>();
        if (bean.getChoice_filter() != null) {
            String[] tempfilters = Tool.split(bean.getChoice_filter(), Global.DELIMETER_DATA3);

            for (String newFilter : tempfilters) {
                int idxOfOpenBrace = newFilter.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    int idxOfCloseBrace = newFilter.indexOf('}');
                    String tempIdentifier = newFilter.substring(idxOfOpenBrace + 1, idxOfCloseBrace).toUpperCase();
                    if (tempIdentifier.contains("%")) {
                        filters.add(tempIdentifier);
                    } else {
                        int idxOfOpenAbs = tempIdentifier.indexOf("$");
                        if (idxOfOpenAbs != -1) {
                            String finalIdentifier = tempIdentifier.substring(idxOfOpenAbs + 1);
                            if (finalIdentifier.equals(Global.IDF_LOGIN_ID)) {
                                String loginId = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                                int idxOfOpenAt = loginId.indexOf('@');
                                if (idxOfOpenAt != -1) {
                                    loginId = loginId.substring(0, idxOfOpenAt);
                                }
                                filters.add(loginId);
                            } else if (finalIdentifier.equals(Global.IDF_BRANCH_ID)) {
                                String branchId = GlobalData.getSharedGlobalData().getUser().getBranch_id();
                                filters.add(branchId);
                            } else if (finalIdentifier.equals(Global.IDF_BRANCH_NAME)) {
                                String branchName = GlobalData.getSharedGlobalData().getUser().getBranch_name();
                                filters.add(branchName);
                            } else if (finalIdentifier.equals(Global.IDF_UUID_USER)) {
                                String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                                filters.add(uuidUser);
                            } else if (finalIdentifier.equals(Global.IDF_JOB)) {
                                String job = GlobalData.getSharedGlobalData().getUser().getFlag_job();
                                filters.add(job);
                            } else if (finalIdentifier.equals(Global.IDF_DEALER_NAME)) {
                                String dealerName = GlobalData.getSharedGlobalData().getUser().getDealer_name();
                                filters.add(dealerName);
                            }
                        } else {
                            QuestionBean bean2 = listOfQuestion.get(tempIdentifier);
                            if (bean2 != null) {
                                if (Global.AT_TEXT_WITH_SUGGESTION.equals(bean2.getAnswer_type())) {
                                    filters.add(bean2.getAnswer());
                                } else {
                                    for (OptionAnswerBean answerBean : bean2.getSelectedOptionAnswers()) {
                                        filters.add(answerBean.getCode());
                                    }
                                }
                                bean2.setRelevanted(true);
                            }
                        }
                    }
                }
            }
        }
        return getLookupFromDB(bean, filters);
    }

    private List<OptionAnswerBean> getLookupFromDB(QuestionBean bean, List<String> filters) {
        List<OptionAnswerBean> optionAnswers = new ArrayList<>();
        if (!filters.isEmpty()) {
            if (filters.size() == 1) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(getApplicationContext(), bean.getLov_group(), filters.get(0));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 2) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(getApplicationContext(), bean.getLov_group(), filters.get(0), filters.get(1));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 3) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(getApplicationContext(), bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 4) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(getApplicationContext(), bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2), filters.get(3));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 5) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(getApplicationContext(), bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2), filters.get(3), filters.get(4));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            }

        } else {
            if (bean.getChoice_filter() != null && bean.getChoice_filter().length() > 0) {
                List<Lookup> lookups = new ArrayList<>();
                optionAnswers = OptionAnswerBean.getOptionList(lookups);
            } else {
                List<Lookup> lookups = LookupDataAccess.getAllByLovGroup(getApplicationContext(), bean.getLov_group());
                if (lookups != null)
                    optionAnswers = OptionAnswerBean.getOptionList(lookups);
            }
        }
        return optionAnswers;
    }

    public List<String> extractIdentifierFromString(String rawString) {
        List<String> extractedIdentifiers = new ArrayList<>();
        boolean needExtract = true;

        while (needExtract) {

            int idxOfOpenBrace = rawString.indexOf('{');
            if (idxOfOpenBrace != -1) {
                int idxOfCloseBrace = rawString.indexOf('}');
                String identifier = rawString.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                if (identifier != null) extractedIdentifiers.add(identifier);
                rawString = rawString.substring(idxOfCloseBrace + 1);            //cut extracted part
            }
            //no more extracting needed
            else {
                needExtract = false;
            }

        }
        return extractedIdentifiers;
    }

    /**
     * Check Relevant from Question Set.
     * ex : {identifier}==value : for get value from Question Set
     * ex : {$LOGIN_ID}==value : for get value from Login ID in active User
     * ex : {$UUID_USER}==value : for get value from uuid user in active User
     * ex : {$BRANCH_ID}==value : for get value from Branch ID in active User
     * ex : {$BRANCH_NAME}==value : for get value from Branch Name in active User
     * ex : {$FLAG_JOB}==value : for get value from Branch Name in active User
     * ex : {$DEALER_NAME}==value : for get value from Dealer Name in active User
     *
     * @param relevantExpression - Relevant expression from Question Set
     * @param question           - Question Bean
     * @return boolean - True if Relevant and False if not Relevant
     */
    public boolean isQuestVisibleIfRelevant(String relevantExpression, QuestionBean question) {
        boolean result = false;
        String convertedExpression = relevantExpression;        //make a copy of
        if (convertedExpression == null || convertedExpression.length() == 0) {
            return true;
        } else {
            boolean needReplacing = true;
            while (needReplacing) {

                //replace application modifier
                convertedExpression = replaceModifiers(convertedExpression);

                int idxOfOpenBrace = convertedExpression.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    int idxOfCloseBrace = convertedExpression.indexOf('}');
                    String identifier = convertedExpression.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                    int idxOfOpenAbs = identifier.indexOf("$");
                    if (idxOfOpenAbs != -1) {
                        String finalIdentifier = identifier.substring(idxOfOpenAbs + 1);
                        String flatAnswer = "";

                        if (finalIdentifier.equals(Global.IDF_LOGIN_ID)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                            int idxOfOpenAt = flatAnswer.indexOf('@');
                            if (idxOfOpenAt != -1) {
                                flatAnswer = flatAnswer.substring(0, idxOfOpenAt);
                            }
                        } else if (finalIdentifier.equals(Global.IDF_BRANCH_ID)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getBranch_id();
                        } else if (finalIdentifier.equals(Global.IDF_BRANCH_NAME)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getBranch_name();
                        } else if (finalIdentifier.equals(Global.IDF_UUID_USER)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                        } else if (finalIdentifier.equals(Global.IDF_JOB)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getFlag_job();
                        } else if (finalIdentifier.equals(Global.IDF_DEALER_NAME)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getDealer_name();
                        }


                        if (flatAnswer != null && flatAnswer.length() > 0) {
                            convertedExpression = convertedExpression.replace("{" + identifier + "}", flatAnswer);
                        } else {            //if there's no answer, just hide the question
                            return false;
                        }

                    } else {
                        QuestionBean bean = listOfQuestion.get(identifier);

                        if (bean != null) {

                            //Glen 21 Oct 2014, if it relate to question which is not visible, make it not visible too
                            if (bean.getIs_visible().equals(Global.FALSE_STRING)) return false;

                            String flatAnswer = QuestionBean.getAnswer(bean);

                            if (flatAnswer != null && flatAnswer.length() > 0) {
                                //Glen 22 Oct 2014, enable multi-depth checking for 'multiple' question
                                //NOTE: though it's possible to just iterate on flatAnswer substrings, we prefer to stay on method if size is 1
                                String[] answers = Tool.split(flatAnswer, Global.DELIMETER_DATA);
                                if (answers.length == 1) {
                                    convertedExpression = convertedExpression.replace("{" + identifier + "}", answers[0]);
                                } else {
                                    //NOTE: going into in-depth loop, won't go outside of this 'else'
                                    for (int i = 0; i < answers.length; i++) {
                                        String convertedSubExpression = convertedExpression.replace("{" + identifier + "}", answers[i]);
                                        boolean isVisible = isQuestVisibleIfRelevant(convertedSubExpression, question);
                                        if (isVisible) {
                                            return true;
                                        }
                                    }
                                    return false;
                                }

                                bean.addToAffectedQuestionBeanVisibility(question);
                            } else {            //if there's no answer, just hide the question
                                return false;
                            }
                        } else {
                            convertedExpression = convertedExpression.replace("{" + identifier + "}", "\"\"");
                        }
                    }
                }
                //no more replacing needed
                else {
                    needReplacing = false;
                }

            }
            try {
                OperatorSet opSet = OperatorSet.getStandardOperatorSet();
                opSet.addOperator("!=", NotEqualSymbol.class);
                Expression exp = new Expression(convertedExpression);
                exp.setOperatorSet(opSet);
                result = exp.evaluate().toBoolean();
                return result;
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV)
                    e.printStackTrace();
                return false;
            }

        }
    }

    //Glen 17 Oct 2014
    private double getCalculationResult(QuestionBean bean) {
        String expression = bean.getCalculate();
        if (expression == null || expression.length() == 0) return 0;

        String convertedExpression = expression;

        List<String> identifiers = extractIdentifierFromString(expression);
        for (String identifier : identifiers) {
            QuestionBean lookupBean = getQuestionBeanForIdentifier(identifier);
            boolean haveAnswer = QuestionBean.isHaveAnswer(lookupBean);

            if (!haveAnswer) return 0;

            convertedExpression = convertedExpression.replace("{" + identifier + "}", String.valueOf(haveAnswer));

            lookupBean.addToAffectedQuestionBeanCalculation(bean);
        }

        try {
            return  Expression.evaluate(convertedExpression).toDouble();
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                e.printStackTrace();
            return 0;
        }

    }

    private double setCalculationResult(QuestionBean bean) {
        double result = getCalculationResult(bean);
        bean.setAnswer(String.valueOf(result));
        return result;
    }

    private QuestionView getQuestionViewWithIcon(QuestionBean qBean, int number, int resIdIcon, QuestionGroup group) {
        QuestionView linear = null;
        try {
            linear = viewGenerator.generateByAnswerType(this, qBean, number, resIdIcon, ViewImageActivity.class, Camera.class, group);
        } catch (Exception ex) {
            if (Global.IS_DEV)
                ex.printStackTrace();
        }
        return linear;
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (Global.getSharedGlobal().getIsVerifiedByUser()) {
            Global.getSharedGlobal().setIsVerifiedByUser(false);
            this.finish();
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        try {
            if (!(previewFields != null && previewFields.contains(v))) {
                removeNextCallback();
                removeReviewCallback();
            }
        } catch (Exception e) {
            FireCrash.log(e);

        }

        if (id == R.id.btnNext) {
            if (isFinish && getIsVerified()) {
                if (Global.NEW_FEATURE) {
                    if (Tool.isInternetconnected(getApplicationContext())) {
                        if (needQuickValidation) {
                            if (validateAllMandatory()) {
                                Intent intent = new Intent(getApplicationContext(), Global.VerificationActivityClass);
                                intent.putExtra(Global.BUND_KEY_UUID_TASKH, getHeader().getUuid_task_h());
                                intent.putExtra(Global.BUND_KEY_MODE_SURVEY, mode);
                                if (getIsApproval())
                                    intent.putExtra(Global.BUND_KEY_FORM_NAME, Global.APPROVAL_FLAG);
                                else
                                    intent.putExtra(Global.BUND_KEY_FORM_NAME, Global.VERIFICATION_FLAG);
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(getApplicationContext(), Global.VerificationActivityClass);
                            intent.putExtra(Global.BUND_KEY_UUID_TASKH, getHeader().getUuid_task_h());
                            intent.putExtra(Global.BUND_KEY_MODE_SURVEY, mode);
                            if (getIsApproval())
                                intent.putExtra(Global.BUND_KEY_FORM_NAME, Global.APPROVAL_FLAG);
                            else
                                intent.putExtra(Global.BUND_KEY_FORM_NAME, Global.VERIFICATION_FLAG);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(DynamicFormActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        btnNext.setEnabled(false);
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        Handler control = new Handler(Looper.getMainLooper());
                        control.post(new Runnable() {
                            public void run() {
                                // UI code goes here
                                try {
                                    doNext(true);
                                } catch (Exception e) {
                                    FireCrash.log(e);

                                }
                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        btnNext.setEnabled(true);
                        btnNext.setImageResource(R.drawable.ic_next);
                        if (isFinish) {
                            if (Global.NEW_FEATURE) {
                                if (Global.FEATURE_REJECT_WITH_RESURVEY && !getIsVerified()) {
                                    btnNext.setClickable(false);
                                    btnNext.setImageResource(R.drawable.ic_next_off);
                                }
                            } else {
                                btnNext.setClickable(false);
                                btnNext.setImageResource(R.drawable.ic_next_off);
                            }
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else if (id == R.id.btnSend) {
            doSend();
        } else if (id == R.id.btnSearchBar) {
            if (btnSearchBar.isChecked()) {
                CustomAnimatorLayout animatorLayout = new CustomAnimatorLayout(0, 1, 0, 1, 500, searchContainer, false);
                searchContainer.setVisibility(View.VISIBLE);
                searchContainer.startAnimation(animatorLayout);
            } else {
                CustomAnimatorLayout animatorLayout = new CustomAnimatorLayout(1, 0, 1, 0, 500, searchContainer, true);
                searchContainer.startAnimation(animatorLayout);
            }
        } else if (id == R.id.btnSearch) {
            btnSearchBar.setChecked(false);
            CustomAnimatorLayout animatorLayout = new CustomAnimatorLayout(1, 0, 1, 0, 500, searchContainer, true);
            searchContainer.startAnimation(animatorLayout);
            String searchKey = "";
            if (txtSearch.getText().length() > 0)
                searchKey = txtSearch.getText().toString().toLowerCase();
            searchQuestion(searchKey, false);
        } else if (id == R.id.btnSave) {
            doSave();
        } else if (id == R.id.btnVerified) {
            doVerify();
        } else if (id == R.id.btnApprove) {
            doApprove();
        } else if (id == R.id.btnReject) {
            doReject();
        } else if (id == R.id.btnClose) {
            this.finish();
        } else if (questionGroupFieldsHeader.contains(v)) {
            onClickInQuestionGroup(v);
        } else if (previewFields.contains(v)) {
            onClickInPreview(v);
            if (!(TaskHDataAccess.STATUS_TASK_APPROVAL.equalsIgnoreCase(getHeader().getStatus())
                    || TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD.equalsIgnoreCase(getHeader().getStatus())
                    || mode == Global.MODE_VIEW_SENT_SURVEY)) {
                mainMenu.findItem(R.id.mnPendingTask).setVisible(false);
            }
        }
    }

    private ValueAnimator slideAnimator(final View view, int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void actionOnQuestionGroupClicked(LinkedHashMap<String, List<QuestionView>> fields, final String questionGroupId, boolean isClick, boolean isPreview) {
        QuestionView qv = null;
        if (isPreview) {
            if (isClick) {
                for (QuestionView view : questionGroupFieldsHeaderPreview) {
                    if (view.getQuestionGroup() != null && view.getQuestionGroup().getQuestion_group_id().equals(questionGroupId)) {
                        qv = view;
                        break;
                    }
                }
            } else {
                for (QuestionView view : questionGroupFieldsHeader) {
                    if (view.getQuestionGroup().getQuestion_group_id().equals(questionGroupId)) {
                        qv = view;
                        break;
                    }
                }
            }
        } else if (isClick) {
            for (QuestionView view : questionGroupFieldsHeader) {
                if (view.getQuestionGroup().getQuestion_group_id().equals(questionGroupId)) {
                    qv = view;
                    break;
                }
            }
        }
        if (qv != null) {
            QuestionView qview = (QuestionView) qv.getChildAt(0);
            View view2 = qview.getChildAt(1);
            if (qv.isExpanded()) {
                createRotateAnimator(view2, 180f, 0f).start();
                qv.setExpanded(false);
            } else {
                createRotateAnimator(view2, 0f, 180f).start();
                qv.setExpanded(true);
            }
        }

        List<QuestionView> questionViews = fields.get(questionGroupId);
        if (questionViews != null) {
            for (final QuestionView view : questionViews) {
                if (view.getVisibility() == View.INVISIBLE) {
                    final int widthSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
                    final int heightSpec = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST);
                    view.measure(widthSpec, heightSpec);
                    ValueAnimator mAnimator = slideAnimator(view, 0, view.getMeasuredHeight());
                    mAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            //Animation Start
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            //Animation Cancel
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            //Animation Repeat
                        }
                    });
                    mAnimator.start();
                } else if (isClick) {
                    int finalHeight = view.getHeight();
                    ValueAnimator mAnimator = slideAnimator(view, finalHeight, 0);

                    mAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            //Animation Start
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            //Height=0, but it set visibility to GONE
                            view.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            //Animation Cancel
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            //Animation Repeat
                        }

                    });
                    mAnimator.start();
                }
            }
        }
    }

    private void onClickInQuestionGroup(View v) {
        QuestionView qv = (QuestionView) v;
        QuestionGroup group = qv.getQuestionGroup();
        actionOnQuestionGroupClicked(questionFields, group.getQuestion_group_id(), true, false);

    }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Interpolator.createInterpolator(Interpolator.LINEAR_INTERPOLATOR));
        return animator;
    }

    private void doReject() {

        boolean isApprovalTask = true;
        btnReject.setEnabled(false);
        if (TaskHDataAccess.STATUS_TASK_VERIFICATION.equals(getHeader().getStatus()) ||
                TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD.equals(getHeader().getStatus())) {
            getHeader().setIs_prepocessed(Global.FORM_TYPE_VERIFICATION);
            isApprovalTask = false;
        }
        if (TaskHDataAccess.STATUS_TASK_APPROVAL.equals(getHeader().getStatus()) ||
                TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD.equals(getHeader().getStatus())) {
            getHeader().setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
            isApprovalTask = true;
        }
        if (getHeader().getSubmit_date() == null)
            getHeader().setSubmit_date(Tool.getSystemDateTime());
        new TaskManager().sendApprovalTask(this, getHeader(), Global.FLAG_FOR_REJECTEDTASK, isApprovalTask);
    }

    private void doApprove() {

        boolean isApprovalTask = true;
        btnApprove.setEnabled(false);
        if (getHeader().getSubmit_date() == null)
            getHeader().setSubmit_date(Tool.getSystemDateTime());
        new TaskManager().sendApprovalTask(this, getHeader(), Global.FLAG_FOR_APPROVALTASK, isApprovalTask);
    }

    private void doVerify() {
        btnVerified.setEnabled(false);
        if (needQuickValidation) {
            if (validateAllMandatory()) {
                sendVerification();
            }
        } else {
            sendVerification();
        }
    }

    private void sendVerification() {
        getHeader().setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
        getHeader().setIs_prepocessed(Global.FORM_TYPE_VERIFICATION);
        List<QuestionBean> questions = new ArrayList<>(listOfQuestion.values());
        if (getHeader().getSubmit_date() == null)
            getHeader().setSubmit_date(Tool.getSystemDateTime());
        new TaskManager().saveAndSendTask(this, mode, getHeader(), questions);
    }

    private void onClickInPreview(Object v) {
        try {
            QuestionView view = (QuestionView) v;
            int idx = previewFields.indexOf(view);
            if (view.isTitleOnly()) {
                actionOnQuestionGroupClicked(reviewFields, view.getQuestionGroup().getQuestion_group_id(), true, true);
            } else {
                if (!(TaskHDataAccess.STATUS_TASK_APPROVAL.equalsIgnoreCase(getHeader().getStatus())
                        || TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD.equalsIgnoreCase(getHeader().getStatus())
                        || mode == Global.MODE_VIEW_SENT_SURVEY)) {
                    idxPosition = 0;
                    idxQuestion = 0;
                    btnReject.setClickable(false);
                    btnReject.setImageResource(R.drawable.ic_reject_off);
                    removeNextCallback();
                    removeReviewCallback();
                    QuestionBean bean = view.getQuestionBean();
                    doBack();
                    QuestionView questionView = (QuestionView) questionContainer.getChildAt(idx);
                    if (idx > 1) {
                        QuestionView questionView2 = previewFields.get(idx - 1);
                        if (questionContainer.getChildAt(idx - 1).getVisibility() == View.INVISIBLE) {
                            actionOnQuestionGroupClicked(questionFields, questionView2.getQuestionBean().getQuestion_group_id(), false, true);
                        }
                        questionContainer.getChildAt(idx - 1).requestFocus();
                    } else {
                        if (questionView.getVisibility() == View.INVISIBLE) {
                            actionOnQuestionGroupClicked(questionFields, bean.getQuestion_group_id(), false, true);
                        }
                        questionView.requestFocus();
                    }
                    if (Global.AT_DROPDOWN.equals(bean.getAnswer_type()) ||
                            Global.AT_DROPDOWN_W_DESCRIPTION.equals(bean.getAnswer_type())) {
                        Spinner spinner = (Spinner) questionView.getChildAt(1);
                        spinner.requestFocusFromTouch();
                    } else {
                        questionView.getChildAt(1).requestFocus();
                    }
                }
            }

        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                e.printStackTrace();
        }
    }

    protected void searchQuestion(String key, boolean needValidation) {
        if (this.validateCurrentPage(needValidation, false)) {
            int start = -1;
            int end = listOfQuestion.size();
            int idx = -1;
            int page = 0;
            start++;
            for (; start < end; start++) {
                QuestionBean bean = listOfQuestion.get(getListOfIdentifier().get(start));

                if (bean.isVisible()) {
                    idx++;
                    String label = bean.getQuestion_label().toLowerCase();
                    if (label.indexOf(key) != -1) {
                        page = idx;
                        break;
                    }
                }
            }
            if (isFinish) {
                isFinish = false;
                ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
                anim.setDuration(500);
                anim.setFillAfter(true);
                questionContainer.startAnimation(anim);
                btnNext.setClickable(true);
                btnSend.setClickable(false);
                if (listOfQuestion.size() == questionSize) {
                    scrollView2.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    int childCount = reviewContainer.getChildCount();
                    if (childCount > 0)
                        reviewContainer.removeViews(0, childCount);
                }
            }
            txtSearch.setText("");
            try {
                QuestionView questionView = (QuestionView) currentPageViews.get(page);
                if (questionView.getVisibility() == View.INVISIBLE) {
                    actionOnQuestionGroupClicked(questionFields, visibleQuestion.get(page).getQuestion_group_id(), false, false);
                }
                questionView.requestFocus();
                questionView.getChildAt(1).requestFocus();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }

    private void doSend() {
        //Glen 22 Oct 2014, try to avoid double tap
        btnSend.setEnabled(false);

        //Glen 16 Oct 2014, validate all before sending, if edit preview happened before
        if (needQuickValidation) {
            if (validateAllMandatory()) {
                saveAndSendSurvey();
            }
        } else {
            saveAndSendSurvey();
        }
    }

    //Glen 17 Oct 2014 send method
    //Glen 12/12/14 constant moced from Global to TaskHDataAccess
    public synchronized void saveAndSendSurvey() {
        if (!isSaveAndSending) {
            isSaveAndSending = true;
            if (TaskHDataAccess.STATUS_SEND_INIT.equals(getHeader().getStatus())) {
                Constant.notifCount--;
            }

            getHeader().setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
            if (getHeader().getSubmit_date() == null)
                getHeader().setSubmit_date(Tool.getSystemDateTime());

            List<QuestionBean> questions = new ArrayList<>(listOfQuestion.values());

            new TaskManager().saveAndSendTask(this, mode, getHeader(), questions);

            isSaveAndSending = false;
        }

    }

    @Override
    /**
     * @deprecated (This method is deprecated as of API: 16 Android 4.1 (Jelly Bean))
     */
    @Deprecated
    protected Dialog onCreateDialog(int id) {

        Date sysdate = Tool.getSystemDateTime();
        switch (id) {
            case QuestionViewGenerator.TYPE_DATE:
                String dt = Formatter.formatDate(sysdate, Global.DATE_STR_FORMAT);
                String[] temp1 = dt.split("/");
                int dayOfMonth = Integer.parseInt(temp1[0]);
                int month = Integer.parseInt((temp1[1])) - 1;
                int year = Integer.parseInt(temp1[2]);
                DateInputListener dtListener = new DateInputListener();
                return new DatePickerDialog(this, dtListener.getmDateSetListener(),
                        year, month, dayOfMonth);
            case QuestionViewGenerator.TYPE_TIME:
                String tm = Formatter.formatDate(sysdate, Global.TIME_STR_FORMAT);
                String[] temp2 = tm.split(":");
                int hourOfDay = Integer.parseInt(temp2[0]);
                int minute = Integer.parseInt(temp2[1]);
                TimeInputListener tmListener = new TimeInputListener();
                return new TimePickerDialog(this, tmListener.getmTimeSetListener(),
                        hourOfDay, minute, true);
            case QuestionViewGenerator.TYPE_DATE_TIME:
                DialogManager.showDateTimePicker(DynamicFormActivity.this);
                break;
            default:
                break;
        }
        return null;
    }

    private void doBack() {

        //Glen 15 Oct 2014, when in previewEditMode, gotoNext without validation
        if (inPreviewEditMode) {
            //Glen 16 OCt 2014, changed to true, because we no longer use btnNext
            doNext(true);
            edittedQuestion = null;
            inPreviewEditMode = false;
            return;
        }

        if (isFinish) {
            isFinish = false;
            ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
            anim.setDuration(500);
            anim.setFillAfter(true);
            questionContainer.startAnimation(anim);
            btnNext.setClickable(true);
            btnSend.setClickable(false);
            btnVerified.setClickable(false);
            btnVerified.setImageResource(R.drawable.ic_verified_off);
            if (listOfQuestion.size() == questionSize) {
                scrollView2.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                int childCount = reviewContainer.getChildCount();
                if (childCount > 0)
                    reviewContainer.removeViews(0, childCount);
            }

            QuestionView questionView = (QuestionView) questionContainer.getChildAt(questionContainer.getChildCount() - 1);

            questionView.getChildAt(1).requestFocus();
        } else {
            if (questionContainer.getChildCount() == 1)
                DialogManager.showExitAlertQuestion(this, getString(R.string.alertExitSurvey));
            else {
                loadBackDynamicForm();
            }
        }
    }

    //Glen 15 Oct 2014, new version for previewMode
    private void updateAffectedBeanAnswer(QuestionBean bean) {

        if (bean == null) return;

        List<QuestionBean> iteratedList;

        //empty answer for all affected bean
        //Glen 16 Oct 2014, empty bean which option may be affected by the changes
        //Glen 22 Oct 2014, copy database to prevent concurrentmodifyexception
        iteratedList = new ArrayList<>(bean.getAffectedQuestionBeanOptions());
        for (QuestionBean affectedBean : iteratedList) {
            affectedBean.setAnswer(null);
            affectedBean.setLovCode(null);
            affectedBean.setSelectedOptionAnswers(null);
            updateAffectedBeanAnswer(affectedBean);                //loop
        }

        iteratedList = new ArrayList<>(bean.getAffectedQuestionBeanVisibility());
        for (QuestionBean affectedBean : iteratedList) {
            String relevantExpression = affectedBean.getRelevant_question();
            boolean isVisible = isQuestVisibleIfRelevant(relevantExpression, affectedBean);
            affectedBean.setIs_visible(Formatter.booleanToString(isVisible));
            updateAffectedBeanAnswer(affectedBean);                //loop
        }

        iteratedList = new ArrayList<>(bean.getAffectedQuestionBeanCalculation());
        for (QuestionBean affectedBean : iteratedList) {
            setCalculationResult(affectedBean);
            updateAffectedBeanAnswer(affectedBean);
        }

    }

    //Glen 15 Oct 2014, add new param for validation
    private boolean doNext(boolean validate) {
        boolean result = false;
        Utility.freeMemory();
        if (getIsApproval()) {
            loadDynamicForm();
            isFinish = false;
            if (listOfQuestion.size() == questionSize) {
                DynamicFormActivity.setIsApproval(false);
                isFinish = true;
                showFinishScreen();
            } else {
                loadDynamicForm();
            }
            result = true;
        } else {
            if (this.validateCurrentPage(validate, false)) {

                if (inPreviewEditMode) {

                    needQuickValidation = true;            //flag as need to re-validate all

                    updateAffectedBeanAnswer(edittedQuestion);
                    edittedQuestion = null;
                    inPreviewEditMode = false;
                } else {
                    loadDynamicForm();
                    isFinish = false;
                    if (listOfQuestion.size() == questionSize) {
                        DynamicFormActivity.setIsApproval(false);
                        isFinish = true;
                        if (Global.IS_DEV)
                            mainMenu.findItem(R.id.mnPendingTask).setVisible(true);
                        if (validate) {
                            showFinishScreen();
                        } else {
                            if (this.validateCurrentPage(true, false))
                                showFinishScreen();
                        }
                    } else {
                        loadDynamicForm();
                    }
                }
                result = true;
            } else {
                DynamicFormActivity.setIsApproval(false);
                result = false;
            }
        }
        return result;
    }

    private void doSave() {
        new AsyncTask<Void, Void, Boolean>() {
            boolean isOK = true;
            boolean isSuccess = false;
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                this.progressDialog = ProgressDialog.show(DynamicFormActivity.this, "", DynamicFormActivity.this.getString(R.string.progressWait), true);
            }

            @Override
            public Boolean doInBackground(Void... params) {

                Handler control = new Handler(Looper.getMainLooper());
                control.post(new Runnable() {
                    public void run() {

                        if (!isFinish) {
                            isOK = validateCurrentPage(true, true);
                        }
                    }
                });
                if (isOK) {
                    try {
                        getHeader().setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                        List<QuestionBean> questions = new ArrayList<>(listOfQuestion.values());
                        String uuidLastQuestion = visibleQuestion.get(visibleQuestion.size() - 1).getUuid_question_set();
                        isSuccess = TaskManager.saveTask(DynamicFormActivity.this, mode, getHeader(), questions, uuidLastQuestion, false, true, false);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        isOK = false;
                        ACRA.getErrorReporter().putCustomData("errorSaveTask", "Pernah error saat save Task");
                        ACRA.getErrorReporter().putCustomData("errorSaveTaskTime", DateFormat.format(DATE_FORMAT, Calendar.getInstance().getTime()).toString());
                        ACRA.getErrorReporter().handleSilentException(e);
                    }
                }
                return isOK;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV)
                            e.printStackTrace();
                    }
                }

                if (Boolean.TRUE.equals(result) && isSuccess) {
                    try {
                        setHeader(null);
                        questionContainer = null;
                        reviewContainer = null;
                        searchContainer = null;
                        scrollView = null;
                        scrollView2 = null;
                        DynamicFormActivity.this.finish();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        finish();
                    }
                } else {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getString(R.string.data_saved_failed), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        FireCrash.log(e);
                        finish();
                    }
                }
            }
        }.execute();
    }

    //Glen 16 Oct 2014, validate all page quickly without saving
    private boolean validateAllMandatory() {
        boolean result = true;
        for (Map.Entry<String, QuestionBean> entry : listOfQuestion.entrySet()) {
            //Glen 10 Nov 2014, fix unanswered question skipped validation, and prevent validation on invisible field
            QuestionBean bean = entry.getValue();
            if (bean.getIs_mandatory().equals(Global.TRUE_STRING) && bean.getIs_visible().equals(Global.TRUE_STRING)) {
                boolean isHaveAnswer = QuestionBean.isHaveAnswer(bean);
                if (!isHaveAnswer) {        //tidak ada isi
                    Toast.makeText(this, bean.getQuestion_label() + " " + getString(R.string.msgRequired), Toast.LENGTH_SHORT).show();
                    result = false;
                }
            }
        }
        return result;
    }

    private boolean validateCurrentPage(boolean isCekValidate, boolean isSave) {
        List<String> errMessage = new ArrayList<>();
        final String msgRequired = getString(R.string.msgRequired);
        QuestionViewValidator validator = new QuestionViewValidator(msgRequired, getApplicationContext());

        if (isCekValidate) {
            for (int i = 0; i < currentPageViews.size(); i++) {
                try {
                    QuestionBean qBean = currentPageBeans.get(i);
                    QuestionView qContainer = (QuestionView) currentPageViews.get(i);
                    List<String> err = validator.validateGeneratedQuestionView(qBean, 0, qContainer);        //idx don't seem to be used, so we put 0
                    if (err != null && !err.isEmpty()) errMessage.addAll(err);

                    if (qContainer.isTitleOnly()) {
                        deleteQuestionGroupView();
                    }
                    if (qContainer.isChanged()) {
                        if (qBean.isRelevanted()) {
                            QuestionView view = (QuestionView) questionContainer.getChildAt(i);
                            if (view.isTitleOnly())
                                deleteQuestionGroupView();
                            qContainer.setChanged(false);
                            int childCount = currentPageViews.size();
                            boolean isLastQuestion = true;
                            for (int x = (childCount - 1); x > i; x--) {
                                currentQuestionGroup = qBean.getQuestion_group_id();
                                if (loadBackDynamicForm()) {
                                    currentPageViews.remove(x);
                                    currentPageBeans.remove(x);
                                }
                                isLastQuestion = false;
                            }
                            if (isSave && !isLastQuestion) {
                                String err2 = getString(R.string.save_on_relevant);
                                errMessage.add(err2);
                            }
                        } else {
                            qContainer.setChanged(false);
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    if (Global.IS_DEV)
                        e.printStackTrace();
                }
            }
        } else {
            for (int i = 0; i < currentPageViews.size(); i++) {
                try {
                    QuestionBean qBean = currentPageBeans.get(i);
                    boolean answer = QuestionBean.isHaveAnswer(qBean);
                    if (!answer && qBean.isMandatory()) {        //tidak ada isi
                        if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval()) {
                            if (!Tool.isImage(qBean.getAnswer_type())) {
                                String err = qBean.getQuestion_label() + " " + getString(R.string.msgRequired);
                                errMessage.add(err);
                            }
                        } else {
                            String err = qBean.getQuestion_label() + " " + getString(R.string.msgRequired);
                            errMessage.add(err);
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    if (Global.IS_DEV)
                        e.printStackTrace();
                }
            }
        }


        if (!errMessage.isEmpty() && isCekValidate) {
            String[] msg = errMessage.toArray(new String[errMessage
                    .size()]);
            String alert = Tool.implode(msg, "\n");
            if (!getIsApproval()) {
                Toast.makeText(this, alert, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        setMainMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenuIcon();
        if (Global.IS_DEV) {
            mainMenu.findItem(R.id.menuMore).setVisible(true);
            setMnPendingTaskVisibility(isFinish);
        }

        if (!isSimulasi) {
            mainMenu.findItem(R.id.menuMore).setVisible(true);
            mainMenu.findItem(R.id.mnRecord).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void setMnPendingTaskVisibility(Boolean bool){
        mainMenu.findItem(R.id.mnPendingTask).setVisible(bool);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if (id == R.id.mnPendingTask) {
            if (Global.IS_DEV) {
                doPending();
            }
        } else if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.mnRecord) {
            Intent intent = new Intent(this, VoiceNotePage.class);
            Bundle extras = new Bundle();
            extras.putInt(Global.BUND_KEY_MODE_SURVEY, mode);
            extras.putSerializable(Global.BUND_KEY_SURVEY_BEAN, DynamicFormActivity.getHeader());
            intent.putExtras(extras);
            startActivityForResult(intent, Global.REQUEST_VOICE_NOTES);
    } else if (id == R.id.mnGPS && Global.LTM != null) {

            if (Global.LTM.getIsConnected()) {
                Global.LTM.removeLocationListener();
                Global.LTM.connectLocationClient();
            } else {
                CheckInManager.startGPSTracking(getApplicationContext());
            }
            Animation a = AnimationUtils.loadAnimation(this, R.anim.gps_rotate);
            findViewById(R.id.mnGPS).startAnimation(a);


        }
        return true;

    }

    private void doPending() {
        new AsyncTask<Void, Void, Boolean>() {
            boolean isOK = true;
            boolean isSuccess = false;
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                this.progressDialog = ProgressDialog.show(DynamicFormActivity.this, "", DynamicFormActivity.this.getString(R.string.progressWait), true);
            }

            @Override
            public Boolean doInBackground(Void... params) {

                Handler control = new Handler(Looper.getMainLooper());
                control.post(new Runnable() {
                    public void run() {

                        if (!isFinish) {
                            isOK = validateCurrentPage(true, false);
                        }
                    }
                });
                if (isOK) {
                    getHeader().setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                    List<QuestionBean> questions = new ArrayList<>(listOfQuestion.values());
                    isSuccess = TaskManager.saveTask(DynamicFormActivity.this, mode, getHeader(), questions, "1", true, false, false);
                }
                return isOK;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV)
                            e.printStackTrace();
                    }
                }

                if (Boolean.TRUE.equals(result) && isSuccess) {
                    setHeader(null);
                    questionContainer = null;
                    reviewContainer = null;
                    searchContainer = null;
                    scrollView = null;
                    scrollView2 = null;
                    DynamicFormActivity.this.finish();
                }
            }
        }.execute();
    }

    private void showFinishScreen() {
        removeNextCallback();
        showFinishedScreen();
    }

    private void removeNextCallback() {
        if (nextHandler != null) {
            hasLoading = false;
            nextHandler.removeCallbacks(nextRunnable);
            btnBack.setClickable(true);
            btnNext.setClickable(true);
            btnSend.setClickable(true);
            btnSave.setClickable(true);
            btnSearch.setClickable(true);
        }
        Utility.freeMemory();
    }

    private void removeReviewCallback() {
        if (reviewHandler != null) {
            reviewHandler.removeCallbacks(reviewRunnable);
            btnClose.setClickable(true);
            if (previewFields != null) {
                for (QuestionView view : previewFields) {
                    if (view.isTitleOnly()) {
                        view.setClickable(true);
                    }
                }
            }
        }
        Utility.freeMemory();
    }

    //Glen 6 Aug 2014, show finish screen with message (add parameter, and extract an empty parameter method)
    private void showFinishedScreen() {
        idxQuestion = 0;
        reviewContainer.removeAllViews();
        setTitle("Review Task");
        scrollView.setVisibility(View.GONE);
        ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
        anim.setDuration(500);
        anim.setFillAfter(true);
        reviewContainer.startAnimation(anim);
        if (Global.NEW_FEATURE) {
            if (Global.FEATURE_REJECT_WITH_RESURVEY && !getIsVerified()) {
                btnNext.setClickable(false);
            }
        } else {
            btnNext.setClickable(false);
        }

        btnSend.setEnabled(true);
        btnSend.setClickable(true);
        btnSend.setOnClickListener(this);
        btnReject.setClickable(true);
        btnReject.setImageResource(R.drawable.ic_reject);
        btnVerified.setClickable(true);
        btnVerified.setImageResource(R.drawable.ic_verified);
        //Glen 6 August 2014, generate preview
        if (previewFields == null) previewFields = new ArrayList<>();
        if (questionGroupFieldsHeaderPreview == null)
            questionGroupFieldsHeaderPreview = new ArrayList<>();
        if (reviewFields == null) reviewFields = new LinkedHashMap<>();
        previewFields.clear();
        questionGroupFieldsHeaderPreview.clear();
        scrollView2.setVisibility(View.VISIBLE);

        reviewRunnable = new Runnable() {
            @Override
            public void run() {
                try {

                    if (idxQuestion < listOfQuestion.size()) {
                        QuestionBean questionBean = listOfQuestion.get(getListOfIdentifier().get(idxQuestion));
                        String newQuestionGroup = questionBean.getQuestion_group_id();
                        if (questionBean.isVisible()) {
                            String relevantExpression = questionBean.getRelevant_question();
                            if (relevantExpression == null) relevantExpression = "";
                            if (isQuestVisibleIfRelevant(relevantExpression, questionBean)) {
                                if (currentQuestionGroupInReview == null || !newQuestionGroup.equals(currentQuestionGroupInReview)) {
                                    currentQuestionGroupInReview = newQuestionGroup;
                                    QuestionGroup group = new QuestionGroup(questionBean);
                                    QuestionView view = reviewGenerator.generateReviewQuestion(DynamicFormActivity.this, questionBean, ViewImageActivity.class, group);
                                    ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
                                    anim.setDuration(200);
                                    anim.setFillAfter(true);
                                    view.startAnimation(anim);
                                    reviewContainer.addView(view, LayoutParams.MATCH_PARENT,
                                            LayoutParams.WRAP_CONTENT);
                                    view.setOnClickListener(DynamicFormActivity.this);
                                    view.setClickable(false);
                                    previewFields.add(view);
                                    questionGroupFieldsHeaderPreview.add(view);
                                }
                                reviewHandler.postDelayed(reviewRunnable, 200);

                                QuestionView field = setQuestionViewField(questionBean);

                                if (field != null) {
                                    reviewContainer.addView(field, LayoutParams.MATCH_PARENT,
                                            LayoutParams.WRAP_CONTENT);
                                    field.setOnClickListener(DynamicFormActivity.this);
                                    previewFields.add(field);
                                    List<QuestionView> tempList = reviewFields.get(questionBean.getQuestion_group_id());
                                    if (tempList != null) {
                                        tempList.add(field);
                                    } else {
                                        tempList = new ArrayList<>();
                                        tempList.add(field);
                                    }
                                    reviewFields.put(questionBean.getQuestion_group_id(), tempList);
                                }
                            } else {
                                reviewHandler.post(reviewRunnable);
                            }
                        } else if (!questionBean.isVisible()) {
                            String relevantExpression = questionBean.getRelevant_question();
                            if (relevantExpression == null) {
                                relevantExpression = "";
                            }
                            if (isQuestVisibleIfRelevant(relevantExpression, questionBean)) {
                                QuestionSet tempQuestion = QuestionSetDataAccess.getOne(getApplicationContext(), getHeader().getUuid_scheme(), questionBean.getQuestion_id(), questionBean.getQuestion_group_id());
                                if (tempQuestion != null) {
                                    if (tempQuestion.getIs_visible().equals(Global.TRUE_STRING)) {
                                        if (currentQuestionGroupInReview == null || !newQuestionGroup.equals(currentQuestionGroupInReview)) {
                                            currentQuestionGroupInReview = newQuestionGroup;
                                            QuestionGroup group = new QuestionGroup(questionBean);
                                            QuestionView view = reviewGenerator.generateReviewQuestion(DynamicFormActivity.this, questionBean, ViewImageActivity.class, group);
                                            ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
                                            anim.setDuration(200);
                                            anim.setFillAfter(true);
                                            view.startAnimation(anim);
                                            reviewContainer.addView(view, LayoutParams.MATCH_PARENT,
                                                    LayoutParams.WRAP_CONTENT);
                                            view.setOnClickListener(DynamicFormActivity.this);
                                            view.setClickable(false);
                                            previewFields.add(view);
                                            questionGroupFieldsHeaderPreview.add(view);
                                        }
                                        reviewHandler.postDelayed(reviewRunnable, 200);
                                        questionBean.setVisible(true);

                                        QuestionView field = setQuestionViewField(questionBean);

                                        if (field != null) {
                                            reviewContainer.addView(field, LayoutParams.MATCH_PARENT,
                                                    LayoutParams.WRAP_CONTENT);
                                            field.setOnClickListener(DynamicFormActivity.this);
                                            previewFields.add(field);
                                            List<QuestionView> tempList = reviewFields.get(questionBean.getQuestion_group_id());
                                            if (tempList != null) {
                                                tempList.add(field);
                                            } else {
                                                tempList = new ArrayList<>();
                                                tempList.add(field);
                                            }
                                            reviewFields.put(questionBean.getQuestion_group_id(), tempList);
                                        }
                                    } else {
                                        reviewHandler.post(reviewRunnable);
                                        questionBean.setVisible(false);
                                    }
                                } else {
                                    reviewHandler.post(reviewRunnable);
                                }
                            } else {
                                reviewHandler.post(reviewRunnable);
                            }
                        }
                        idxQuestion++;
                    } else {
                        removeReviewCallback();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    idxQuestion = 0;
                    if (listOfQuestion == null) {
                        listOfQuestion = Constant.getListOfQuestion();
                        if (listOfQuestion == null) {
                            removeReviewCallback();
                        }
                    }
                }
            }
        };
        if (reviewHandler == null)
            reviewHandler = new Handler();
        reviewHandler.postDelayed(reviewRunnable, 200);
    }

    private QuestionView setQuestionViewField(QuestionBean questionBean){
        try{
            return reviewGenerator.generateReviewQuestion(DynamicFormActivity.this, questionBean, ViewImageActivity.class, null);
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            removeNextCallback();
            removeReviewCallback();
        } catch (Exception e) {
            FireCrash.log(e);

        }
        if (mode == Global.MODE_VIEW_SENT_SURVEY)
            super.onBackPressed();
        else {
            DialogManager.showExitAlertQuestion(this, getString(R.string.alertExitSurvey));
        }
    }

    @Override
    public void onPreExecute(GenericAsyncTask task) {
        //On Pre Execute
    }

    //Glen 7 Jan 2015, updated GenericAsyncTask
    @Override
    public String doInBackground(GenericAsyncTask task, String... args) {
        String[] result = null;
        try {
            getHeader().setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
            TaskHDataAccess.doBackup(this, header);
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                e.printStackTrace();
            return null;
        }

        return Tool.implode(result, Global.DELIMETER_DATA2);
    }

    @Override
    public void onPostExecute(GenericAsyncTask task, String result,
                              String errMsg) {
        if (result != null && result.length() > 0) {
            //index0 = server message, index1 = null, index2 = status: 1=success -1=fail
            showFinishedScreen();
        }
        //Glen 11 Aug 2014, return toast message for failure
        else {
            Toast.makeText(this, getString(R.string.msgConnectionFailed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Location Changed
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Status Changed
    }

    @Override
    public void onProviderEnabled(String provider) {
        DialogManager.closeGPSAlert();
    }

    @Override
    public void onProviderDisabled(String provider) {
        //EMPTY
    }

    private class RestoreGlobalData extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... urls) {
            NewMainActivity.InitializeGlobalDataIfError(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Post Executed
        }
    }

    private class GetLookupOnDemand extends AsyncTask<Void, Void, Boolean> {

        String errMessage = null;
        String lovGroup = null;
        int constraintAmount = 0;
        List<String> filters;
        private ProgressDialog progressDialog;


        public GetLookupOnDemand(String lovGroup) {
            this.lovGroup = lovGroup;
        }

        public GetLookupOnDemand(String lovGroup, List<String> filters) {
            this.lovGroup = lovGroup;
            if (filters != null)
                this.filters = filters;
            else
                this.filters = new ArrayList<>();
        }

        public GetLookupOnDemand(String lovGroup, List<String> filters, int constraintAmount) {
            this.lovGroup = lovGroup;
            if (filters != null)
                this.filters = filters;
            else
                this.filters = new ArrayList<>();
            this.constraintAmount = constraintAmount;
        }

        @Override
        protected void onPreExecute() {
            String message = getString(R.string.lookup_progress, lovGroup);
            this.progressDialog = ProgressDialog.show(DynamicFormActivity.this, "", message, true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (Tool.isInternetconnected(getApplicationContext())) {
                //----------------------Get Lookup Parameter----------------------
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(1971, 1, 1, 1, 1, 1);
                Date date = cal.getTime();

                String url = GlobalData.getSharedGlobalData().getURL_SYNCPARAM_CONSTRAINT();
                List<HashMap<String, Object>> lookupArgs = new ArrayList<>();
                HashMap<String, Object> forms = new HashMap<>();
                forms.put(LookupDao.Properties.Lov_group.name, lovGroup);
                forms.put(LookupDao.Properties.Dtm_upd.name, date);
                if (!filters.isEmpty()) {
                    if (filters.size() == 1) {
                        forms.put(LookupDao.Properties.Filter1.name, filters.get(0));
                    } else if (filters.size() == 2) {
                        forms.put(LookupDao.Properties.Filter1.name, filters.get(0));
                        forms.put(LookupDao.Properties.Filter2.name, filters.get(1));
                    } else if (filters.size() == 3) {
                        forms.put(LookupDao.Properties.Filter1.name, filters.get(0));
                        forms.put(LookupDao.Properties.Filter2.name, filters.get(1));
                        forms.put(LookupDao.Properties.Filter3.name, filters.get(2));
                    } else if (filters.size() == 4) {
                        forms.put(LookupDao.Properties.Filter1.name, filters.get(0));
                        forms.put(LookupDao.Properties.Filter2.name, filters.get(1));
                        forms.put(LookupDao.Properties.Filter3.name, filters.get(2));
                        forms.put(LookupDao.Properties.Filter4.name, filters.get(3));
                    } else if (filters.size() == 5) {
                        forms.put(LookupDao.Properties.Filter1.name, filters.get(0));
                        forms.put(LookupDao.Properties.Filter2.name, filters.get(1));
                        forms.put(LookupDao.Properties.Filter3.name, filters.get(2));
                        forms.put(LookupDao.Properties.Filter4.name, filters.get(3));
                        forms.put(LookupDao.Properties.Filter5.name, filters.get(4));
                    }
                }
                forms.put("constraintAmount", constraintAmount);

                lookupArgs.add(forms);

                SynchronizeRequestModel request = new SynchronizeRequestModel();
                request.setInit(0);

                if (!lookupArgs.isEmpty())
                    request.setList(lookupArgs);
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                request.setTableName("MS_LOV");
                request.setDtm_upd(date);


                String jsonRequest = GsonHelper.toJson(request);
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(DynamicFormActivity.this, encrypt, decrypt);
                HttpConnectionResult serverResult = null;

                //Firebase Performance Trace HTTP Request
                HttpMetric networkMetric =
                        FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, jsonRequest);

                try {
                    serverResult = httpConn.requestToServer(url, jsonRequest, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverResult);
                } catch (Exception e) {             FireCrash.log(e);
                }
                if (serverResult != null && serverResult.isOK()) {
                    String body = serverResult.getResult();
                    try {
                        SynchronizeResponseLookup entityLookup = GsonHelper.fromJson(body, SynchronizeResponseLookup.class);
                        List<Lookup> entitiesLookup = entityLookup.getListSync();
                        if (entitiesLookup != null && !entitiesLookup.isEmpty())
                            LookupDataAccess.addOrUpdateAll(getApplicationContext(), entitiesLookup);
                        else
                            errMessage = getString(R.string.lookup_not_available, lovGroup);
                    } catch (JsonSyntaxException | IllegalStateException e) {
                        ACRA.getErrorReporter().putCustomData("errorGetLookupOnDemand", e.getMessage());
                        ACRA.getErrorReporter().putCustomData("errorGetLookupOnDemandDate", DateFormat.format(DATE_FORMAT, Calendar.getInstance().getTime()).toString());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Just for the stacktrace, kena exception saat Get Lookup on Demand"));
                        return false;
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                errMessage = getString(R.string.connection_failed);
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {             FireCrash.log(e);
                    if (Global.IS_DEV)
                        e.printStackTrace();
                }
            }
            if (errMessage != null) {
                Toast.makeText(getApplicationContext(), errMessage, Toast.LENGTH_LONG).show();
                isStop = true;
            } else {
                if (Boolean.FALSE.equals(result)) {
                    String message = getString(R.string.get_lookup_failed, lovGroup);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    isStop = true;

                    if (questionContainer.getChildCount() != 1 && loadBackDynamicForm()) {
                        currentPageBeans.remove(currentPageBeans.size() - 1);
                        currentPageViews.remove(currentPageViews.size() - 1);
                    }
                } else {
                    QuestionBean qBean = currentPageBeans.get(currentPageBeans.size() - 1);
                    MultiOptionQuestionViewAbstract qContainer = (MultiOptionQuestionViewAbstract) currentPageViews.get(currentPageViews.size() - 1);
                    if (qContainer != null) {
                        String answerType = qBean.getAnswer_type();

                        if (Tool.isOptions(answerType) &&
                                (qBean.getOptionAnswers() == null || qBean.getOptionAnswers().isEmpty() || qBean.getOptionRelevances().length > 0)) {
                            List<OptionAnswerBean> options = getOptionsForQuestion2(qContainer.getQuestionBean());
                            qContainer.setOptions(getApplicationContext(), options);
                        }
                    }
                }
            }
        }
    }

    final class DynamicSurveyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            this.setQuestionInFocusByModel(msg.what);
        }

        private void setQuestionInFocusByModel(int idx) {
            setQuestionInFocus(listOfQuestion.get(getListOfIdentifier().get(idx)));

            int mod = ++idx % listOfQuestion.size();
            int position = (mod == 0) ? listOfQuestion.size() : mod;

            int start = 0;
            int end = listOfQuestion.size();

            if (idx >= start && idx <= end) {
                LinearLayout qContainer = (LinearLayout) questionContainer
                        .getChildAt(--position);
                setThumbInFocus((ImageView) qContainer.getChildAt(1));
                if (qContainer.getChildCount() > 2) {
                    setThumbLocationInfo((ImageView) qContainer.getChildAt(2));
                    setTxtDetailInFocus((TextView) qContainer.getChildAt(3));
                } else {
                    setTxtDetailInFocus((TextView) qContainer.getChildAt(2));
                }
            } else {
                setThumbInFocus(null);
            }
        }
    }

    public class ProcessingBitmap extends AsyncTask<File, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private final WeakReference<ImageView> imageLocViewReference;
        private final WeakReference<TextView> txtViewReference;
        private ProgressDialog progressDialog;
        private String errMessage = "";
        private String formattedSize;
        private String indicatorGPS = "";
        private boolean isGeoTagged;
        private boolean isGeoTaggedGPSOnly;
        private int[] res;

        public ProcessingBitmap(ImageView imageView, ImageView imageViewLcoation) {
            this.imageLocViewReference = new WeakReference<>(imageViewLcoation);
            this.imageViewReference = new WeakReference<>(imageView);
            this.txtViewReference = new WeakReference<>(DynamicQuestion.getTxtDetailInFocus());
        }

        public ProcessingBitmap() {
            this.imageViewReference = new WeakReference<>(DynamicQuestion.getThumbInFocus());
            this.imageLocViewReference = new WeakReference<>(DynamicQuestion.getThumbLocationInfo());
            this.txtViewReference = new WeakReference<>(DynamicQuestion.getTxtDetailInFocus());
        }

        private void imageViewSetImageBitmap(WeakReference<ImageView> imageLocViewReference, Bitmap thumbLocation){
            try {
                if (imageLocViewReference != null && thumbLocation != null) {
                    final ImageView imageViewLoc = imageLocViewReference.get();
                    if (imageViewLoc != null) {
                        imageViewLoc.setImageBitmap(thumbLocation);
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV)
                    e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(DynamicFormActivity.this, "", DynamicFormActivity.this.getString(R.string.processing_image), true);
        }

        @Override
        protected Bitmap doInBackground(File... params) {
            try {
                File file = params[0];
                ExifData exifData = Utils.getDataOnExif(file);
                int rotate = exifData.getOrientation();
                int quality = Utils.picQuality;
                int thumbHeight = Utils.picHeight;
                int thumbWidht = Utils.picWidth;
                QuestionBean bean = DynamicQuestion.getQuestionInFocus();
                boolean isHQ = false;
                if (bean.getImg_quality() != null && bean.getImg_quality().equalsIgnoreCase(Global.IMAGE_HQ)) {
                    thumbHeight = Utils.picHQHeight;
                    thumbWidht = Utils.picHQWidth;
                    quality = Utils.picHQQuality;
                    isHQ = true;
                }

                byte[] data = setImageData(thumbHeight,exifData,file,rotate,thumbWidht,quality,isHQ);

                if (data != null) {
                    deleteLatestPictureCreate(getApplicationContext());

                    deleteLatestPicture();

                    DynamicQuestion.saveImage(data);
                    boolean getGPS = true;
                    LocationInfo locBean;

                    isGeoTagged = Global.AT_IMAGE_W_LOCATION.equals(bean.getAnswer_type());
                    isGeoTaggedGPSOnly = Global.AT_IMAGE_W_GPS_ONLY.equals(bean.getAnswer_type());
                    if (isGeoTagged) {
                        LocationTrackingManager pm = Global.LTM;
                        if (pm != null) {
                            locBean = pm.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
                            LocationInfo2 infoFinal = new LocationInfo2(locBean);

                            if (infoFinal.getLatitude().equals("0.0") || infoFinal.getLongitude().equals("0.0")) {
                                if (infoFinal.getMcc().equals("0") || infoFinal.getMnc().equals("0")) {
                                    if (bean.isMandatory()) {
                                        bean.setLocationInfo(infoFinal);
                                        String geodataError = getString(R.string.geodata_error);
                                        String[] msg = {geodataError};
                                        String alert2 = Tool.implode(msg, "\n");
                                        errMessage = alert2;
                                        DynamicQuestion.saveImage(null);
                                        DynamicQuestion.saveImageLocation(null);
                                        return null;
                                    }
                                } else {
                                    bean.setAnswer(getString(R.string.coordinat_not_available));
                                    bean.setLocationInfo(infoFinal);
                                    indicatorGPS = bean.getAnswer();
                                    if (bean.isMandatory()) {
                                        String gpsError = getString(R.string.gps_gd_error);
                                        String[] msg = {gpsError};
                                        String alert2 = Tool.implode(msg, "\n");
                                        errMessage = alert2;
                                        return null;
                                    }
                                }
                            } else {
                                bean.setAnswer(LocationTrackingManager.toAnswerStringShort(infoFinal));
                                bean.setLocationInfo(infoFinal);
                                indicatorGPS = bean.getAnswer();
                            }
                        }
                    }

                    if (isGeoTaggedGPSOnly) {
                        LocationTrackingManager pm = Global.LTM;
                        if (pm != null) {
                            locBean = pm.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
                            LocationInfo2 infoFinal = new LocationInfo2(locBean);
                            if (infoFinal.getLatitude().equals("0.0") || infoFinal.getLongitude().equals("0.0")) {

                                if (bean.isMandatory()) {
                                    bean.setLocationInfo(infoFinal);
                                    String gpsError = getString(R.string.gps_error);
                                    String[] msg = {gpsError};
                                    String alert2 = Tool.implode(msg, "\n");
                                    errMessage = alert2;
                                    DynamicQuestion.saveImage(null);
                                    DynamicQuestion.saveImageLocation(null);
                                    return null;
                                }

                            } else {
                                bean.setAnswer(LocationTrackingManager.toAnswerStringShort(infoFinal));
                                bean.setLocationInfo(infoFinal);
                                indicatorGPS = bean.getAnswer();
                            }
                        }
                    }

                    // set thumbnail
                    if (DynamicQuestion.getThumbInFocus() != null && getGPS) {
                        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                        res = new int[2];
                        res[0] = bm.getWidth();
                        res[1] = bm.getHeight();
                        int[] thumbRes = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
                        Bitmap thumbnail = Bitmap.createScaledBitmap(bm, thumbRes[0], thumbRes[1], true);

                        long size = bean.getImgAnswer().length;
                        formattedSize = Formatter.formatByteSize(size);

                        return thumbnail;

                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } catch (OutOfMemoryError e) {
                errMessage = DynamicFormActivity.this.getString(R.string.processing_image_error);
                return null;
            } catch (Exception e) {             FireCrash.log(e);
                errMessage = DynamicFormActivity.this.getString(R.string.camera_error);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (errMessage != null && !errMessage.isEmpty()) {
                Toast.makeText(DynamicFormActivity.this, errMessage, Toast.LENGTH_SHORT).show();
            } else if (imageViewReference != null && bitmap != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ImageView imageView = imageViewReference.get();
                        if (imageView != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                        if (isGeoTagged || isGeoTaggedGPSOnly) {
                            try {
                                Bitmap thumbLocation = BitmapFactory.decodeResource(getResources(), R.drawable.ic_absent);

                                imageViewSetImageBitmap(imageLocViewReference, thumbLocation);

                            } catch (Exception e) {             FireCrash.log(e);
                                if (Global.IS_DEV)
                                    e.printStackTrace();
                            }
                            if (txtViewReference != null) {
                                final TextView textView = txtViewReference.get();
                                if (textView != null) {
                                    String text = res[0] + " x " + res[1] +
                                            SIZE + formattedSize + "\n" + indicatorGPS;
                                    textView.setText(text);
                                }
                            }
                        } else {
                            if (txtViewReference != null) {
                                final TextView textView = txtViewReference.get();
                                if (textView != null) {
                                    String text = res[0] + " x " + res[1] +
                                            SIZE + formattedSize;
                                    textView.setText(text);
                                }
                            }
                        }
                        Utility.freeMemory();
                    }
                });

            }
        }
    }

    public static List<String> getListOfIdentifier() {
        return listOfIdentifier;
    }

    public static void setListOfIdentifier(List<String> listOfIdentifier) {
        DynamicFormActivity.listOfIdentifier = listOfIdentifier;
    }

    public static boolean getIsApproval() {
        return isApproval;
    }

    public static void setIsApproval(boolean isApproval) {
        DynamicFormActivity.isApproval = isApproval;
    }

    public static boolean getIsVerified() {
        return isVerified;
    }

    public static void setIsVerified(boolean isVerified) {
        DynamicFormActivity.isVerified = isVerified;
    }

    public static boolean isAllowImageEdit() {
        return allowImageEdit;
    }

    public static void setAllowImageEdit(boolean allowImageEdit) {
        DynamicFormActivity.allowImageEdit = allowImageEdit;
    }

    public static Menu getMainMenu() {
        return mainMenu;
    }

    public static void setMainMenu(Menu mainMenu) {
        DynamicFormActivity.mainMenu = mainMenu;
    }

    public static SurveyHeaderBean getHeader() {
        return header;
    }

    public static void setHeader(SurveyHeaderBean header) {
        DynamicFormActivity.header = header;
    }

    public static String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public static void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        DynamicFormActivity.mCurrentPhotoPath = mCurrentPhotoPath;
    }

}