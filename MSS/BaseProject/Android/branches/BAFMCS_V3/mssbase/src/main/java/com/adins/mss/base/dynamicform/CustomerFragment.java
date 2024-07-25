package com.adins.mss.base.dynamicform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.cardview.widget.CardView;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.api.CheckResubmitApi;
import com.adins.mss.base.commons.SecondHelper;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.databinding.NewFragmentCustomerBinding;
import com.adins.mss.base.depositreport.DetailTaskHRequest;
import com.adins.mss.base.depositreport.DetailTaskHResponse;
import com.adins.mss.base.dialogfragments.RescheduleDialog;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.dynamictheme.ThemeUtility;
import com.adins.mss.base.todo.form.JsonRequestScheme;
import com.adins.mss.base.todo.form.JsonResponseScheme;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.RescheduleFragment;
import com.adins.mss.base.todolist.todayplanrepository.IPlanTaskDataSource;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PaymentChannel;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.audio.AudioRecord;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.PaymentChannelDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.google.firebase.perf.metrics.Trace;

import org.acra.ACRA;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import de.greenrobot.dao.DaoException;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class CustomerFragment extends Fragment implements OnClickListener, ThemeLoader.ColorSetLoaderCallback {
    public static final String SURVEY_MODE = "com.adins.mss.base.dynamicform.SURVEY_MODE";
    public static final String SURVEY_HEADER = "com.adins.mss.base.dynamicform.SURVEY_HEADER";
    public static final String SURVEY_UUID = "com.adins.mss.base.dynamicform.SURVEY_UUID";
    public static final String CUSTOMER_NAME = "com.adins.mss.base.dynamicform.CUSTOMER_NAME";
    public static final String CUSTOMER_PHONE = "com.adins.mss.base.dynamicform.CUSTOMER_PHONE";
    public static final String CUSTOMER_ADDRESS = "com.adins.mss.base.dynamicform.CUSTOMER_ADDRESS";
    public static final String CUSTOMER_ZIPCODE = "com.adins.mss.base.dynamicform.CUSTOMER_ZIPCODE";
    public static final String CUSTOMER_NOTES = "com.adins.mss.base.dynamicform.CUSTOMER_NOTES";
    public static final String SURVEY_LOCATION = "com.adins.mss.base.dynamicform.SURVEY_LOCATION";
    private static final String DATE_TIME_FORMAT = "yyyy.MM.dd G 'at' HH:mm:ss z";
    private static final String ERROR_GET_SCHEME_TAG = "errorGetScheme";
    private static final String ERROR_GET_SCHEME_TIME_TAG = "errorGetSchemeTime";
    private static final String GET_SCHEME_EXCEPTION = "Exception saat Get Scheme";
    private static final String ERROR_GET_MSG_FRM_SERVER = "errorGetMessageFromServer";
    private static final String ERROR_REQ_TO_SERVER = "errorRequestToServer";
    public static SurveyHeaderBean header;
    private static boolean isEditable = false;
    private static boolean viewTask = false;
    private static CustomerFragment instance = null;
    private static Menu mainMenu;
    public List<TaskH> listTaskH;
    public ToDoList toDoList;
    double limit = 0;
    double cashOnHand = 0;
    private static Scheme lastUpdateScheme;
    TaskD taskdOSAmount;
    TaskD taskdOD;
    TaskD taskdInstallmentNo;
    CardView agreementLayout;
    CardView osAmountLayout;
    CardView overdueLayout;
    CardView installmentNoLayout;
    CardView ptsLayout;
    EditText txtAgreement;
    EditText txtOSAmount;
    EditText txtOD;
    EditText txtInstallmentNo;
    Button callNumber;
    TextView txtSurveyLocationTitle;
    EditText txtSurveyLocation;
    public static String isFlag = "0";
    private Activity activity;
    private AudioRecord record;
    private Bundle bundleParams;
    private Context context;
    private CheckScheme checkScheme;
    private NewFragmentCustomerBinding mBinding;
    private RefreshBackgroundTask backgroundTask;
    private BluetoothAdapter mBluetoothAdapter = null;

    public interface AsyncTaskCallback{
        void onTaskStarted(String message);
    }

    public static CustomerFragment getInstance() {
        return instance;
    }
    //Firebase Custom Trace
    private Trace headerTrace;

    public static CustomerFragment create(Bundle data) {
        CustomerFragment fragment = new CustomerFragment();
        fragment.setArguments(data);
        return fragment;
    }

    public static boolean getIsEditable() {
        return isEditable;
    }

    public static void setIsEditable(boolean isEditable) {
        CustomerFragment.isEditable = isEditable;
    }

    /**
     * @param header
     * @author gigin.ginanjar
     */
    public static CustomerFragment create(SurveyHeaderBean header) {
        CustomerFragment.setHeader(null);
        Bundle bundle = new Bundle();

        bundle.putString(CUSTOMER_NAME, header.getCustomer_name());
        bundle.putString(CUSTOMER_PHONE, header.getCustomer_phone());
        bundle.putString(CUSTOMER_ADDRESS, header.getCustomer_address());
        bundle.putString(CUSTOMER_ZIPCODE, header.getZip_code());
        bundle.putString(CUSTOMER_NOTES, header.getNotes());
        bundle.putString(SURVEY_LOCATION, header.getSurvey_location());
        String status = header.getStatus();
        int mode = 0;
        if (status.equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT) ||
                status.equals(TaskHDataAccess.STATUS_SEND_DOWNLOAD) ||
                status.equals(TaskHDataAccess.STATUS_SEND_PENDING) ||
                status.equals(TaskHDataAccess.STATUS_SEND_UPLOADING) ||
                status.equals(TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD) ||
                status.equals(TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD))
            mode = Global.MODE_SURVEY_TASK;
        else if (status.equals(TaskHDataAccess.STATUS_SEND_SENT) ||
                status.equals(TaskHDataAccess.STATUS_SEND_REJECTED) ||
                status.equals(TaskHDataAccess.STATUS_TASK_CHANGED)) {
            mode = Global.MODE_VIEW_SENT_SURVEY;
        } else if (status.equals(TaskHDataAccess.STATUS_TASK_VERIFICATION) ||
                status.equals(TaskHDataAccess.STATUS_TASK_APPROVAL)) {
            mode = Global.MODE_SURVEY_TASK;
        } else {
            if (header.getStatus().equals(TaskHDataAccess.STATUS_SEND_INIT) &&
                    header.getPriority() != null) {
                mode = Global.MODE_SURVEY_TASK;
            } else mode = Global.MODE_NEW_SURVEY;
        }

        bundle.putInt(SURVEY_MODE, mode);
        bundle.putSerializable(SURVEY_HEADER, header);
        bundle.putString(SURVEY_UUID, header.getUuid_scheme());

        return create(bundle);
    }

    public static CustomerFragment create(String name, String phone, String address, String zipcode, String notes, String surveyLocation, int mode,
                                          SurveyHeaderBean header) {
        CustomerFragment fragment = new CustomerFragment();
        CustomerFragment.header = null;
        Bundle bundle = new Bundle();

        bundle.putString(CUSTOMER_NAME, name);
        bundle.putString(CUSTOMER_PHONE, phone);
        bundle.putString(CUSTOMER_ADDRESS, address);
        bundle.putString(CUSTOMER_ZIPCODE, zipcode);
        bundle.putString(CUSTOMER_NOTES, notes);
        bundle.putString(SURVEY_LOCATION, surveyLocation);
        bundle.putInt(SURVEY_MODE, mode);
        bundle.putSerializable(SURVEY_HEADER, header);
        bundle.putString(SURVEY_UUID, header.getUuid_scheme());

        return create(bundle);
    }

    public static CustomerFragment create(String name, String phone, String address, String zipcode, String notes,String surveyLocation, int mode,
                                          String uuid) {
        CustomerFragment fragment = new CustomerFragment();
        CustomerFragment.setHeader(null);
        Bundle bundle = new Bundle();

        bundle.putString(CUSTOMER_NAME, name);
        bundle.putString(CUSTOMER_PHONE, phone);
        bundle.putString(CUSTOMER_ADDRESS, address);
        bundle.putString(CUSTOMER_ZIPCODE, zipcode);
        bundle.putString(CUSTOMER_NOTES, notes);
        bundle.putString(SURVEY_LOCATION, surveyLocation);
        bundle.putInt(SURVEY_MODE, mode);
        bundle.putSerializable(SURVEY_HEADER, new SurveyHeaderBean());
        bundle.putString(SURVEY_UUID, uuid);

        return create(bundle);
    }

    public static void doBack(Activity activity) {
        try {
            if (NewMainActivity.fragmentManager.getBackStackEntryCount() > 0) {
                NewMainActivity.fragmentManager.popBackStackImmediate();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errordDoBack", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errordDoBack", DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception do back in Customer Fragment"));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTheme();

        try {
            checkTLS();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if(getHeader() != null){
            mBinding.txtCustomerName.setText(getHeader().getCustomer_name());
            mBinding.txtCustomerPhone.setText(getHeader().getCustomer_phone());
            mBinding.txtCustomerAddress.setText(getHeader().getCustomer_address());
            mBinding.txtNotes.setText(getHeader().getNotes());
            if (GlobalData.getSharedGlobalData().getUser().getPiloting_branch().equalsIgnoreCase("1")) {
                txtSurveyLocationTitle.setVisibility(View.VISIBLE);
                txtSurveyLocation.setVisibility(View.VISIBLE);
            } else {
                txtSurveyLocationTitle.setVisibility(View.GONE);
                txtSurveyLocation.setVisibility(View.GONE);
            }
            if (null != bundleParams.getString(SURVEY_LOCATION) && !"null".equals(bundleParams.getString(SURVEY_LOCATION))) {
                SpannableString spannableString = new SpannableString(bundleParams.getString(SURVEY_LOCATION));
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse("https://maps.google.com/maps?q="
                                + bundleParams.getString(SURVEY_LOCATION)));
                        startActivity(browserIntent);
                    }
                };
                spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                txtSurveyLocation.setText(spannableString);
                txtSurveyLocation.setMovementMethod(LinkMovementMethod.getInstance());
                txtSurveyLocation.setHighlightColor(Color.TRANSPARENT);
            } else {
                txtSurveyLocation.setText("-");
            }
        }
        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
            agreementLayout.setVisibility(View.VISIBLE);
            overdueLayout.setVisibility(View.VISIBLE);
            osAmountLayout.setVisibility(View.VISIBLE);
            installmentNoLayout.setVisibility(View.VISIBLE);

            if (getHeader().getAppl_no() == null) {
                txtAgreement.setText("-");
            } else {
                txtAgreement.setText(getHeader().getAppl_no());
            }

            if (getHeader().getAmt_due() == null) {
                txtOSAmount.setText("-");
            } else {
                txtOSAmount.setText(Tool.separateThousand(getHeader().getAmt_due()));
            }

            if (getHeader().getOd() == null) {
                txtOD.setText("-");
            } else {
                String od = getHeader().getOd() + " " + context.getString(R.string.txtDay);
                txtOD.setText(od);
            }

            if (getHeader().getInst_no() == null) {
                txtInstallmentNo.setText("-");
            } else {
                txtInstallmentNo.setText(getHeader().getInst_no());
            }
        }

        if (bundleParams.getInt(SURVEY_MODE) == Global.MODE_VIEW_SENT_SURVEY) {
            mBinding.btnReset.setVisibility(View.GONE);
            mBinding.btnStartSurvey.setVisibility(View.GONE);
            mBinding.btnViewTask.setVisibility(View.VISIBLE);

           try {
                String uuidScheme = getHeader().getUuid_scheme();
                Scheme scheme = SchemeDataAccess.getOne(getActivity(), uuidScheme);
                if (scheme != null) {
                    String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();

                    List<TaskD> taskDs = TaskDDataAccess.getAll(getActivity(), getHeader().getUuid_task_h(),
                            TaskDDataAccess.ALL_TASK);
                    if (taskDs != null && !taskDs.isEmpty()) {
                        boolean isTaskPaid = TaskDDataAccess.isTaskPaid(getActivity(), uuidUser, getHeader().getUuid_task_h());
                        boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(getActivity(), uuidUser);
                        if (isRVinFront) {
                            mBinding.btnPrint.setVisibility(View.GONE);
                        } else if (!scheme.getIs_printable().equals("1") || !isTaskPaid) {
                            mBinding.btnPrint.setVisibility(View.GONE);
                        } else {
                            if (getHeader().getRv_number() != null && !getHeader().getRv_number().isEmpty())
                                mBinding.btnPrint.setVisibility(View.GONE);
                            else
                                mBinding.btnPrint.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {
                            new GetTaskDOnline(getActivity(), getHeader()).execute();
                        }
                    }
                }
            } catch (DaoException e) {
                if (Global.IS_DEV)
                    e.printStackTrace();
                ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TAG, e.getMessage());
                ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TIME_TAG, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                Toast.makeText(getActivity(), getActivity().getString(R.string.request_error),
                        Toast.LENGTH_SHORT).show();
                doBack(getActivity());
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV)
                    e.printStackTrace();
                ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TAG, e.getMessage());
                ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TIME_TAG, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                Toast.makeText(getActivity(), getActivity().getString(R.string.request_error),
                        Toast.LENGTH_SHORT).show();
                doBack(getActivity());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setInstance(this);

        headerTrace = FirebasePerformance.getInstance().newTrace(getString(R.string.customer_header_trace));

        mBinding = DataBindingUtil.inflate(inflater, R.layout.new_fragment_customer, container, false);
        View view = mBinding.getRoot();

        getActivity().findViewById(R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(R.string.title_mn_customer_form));

        overdueLayout = (CardView) view.findViewById(R.id.customerODLayout);
        agreementLayout = (CardView) view.findViewById(R.id.customerAgreementLayout);
        osAmountLayout = (CardView) view.findViewById(R.id.customerOSAmountLayout);
        installmentNoLayout = (CardView) view.findViewById(R.id.customerInstallmentNoLayout);
        ptsLayout = (CardView) view.findViewById(R.id.ptsLayout);
        txtAgreement = (EditText) view.findViewById(R.id.txtAgreement);
        txtOSAmount = (EditText) view.findViewById(R.id.txtCustomerOSAmount);
        txtOD = (EditText) view.findViewById(R.id.txtCustomerOD);
        txtInstallmentNo = (EditText) view.findViewById(R.id.txtCustomerInstallmentNo);
        mBinding.txtNotes.setMaxLines(Global.NOTE_MAX_LENGTH);
        txtSurveyLocation = (EditText) view.findViewById(R.id.txtSurveyLocation);
        txtSurveyLocationTitle = (TextView) view.findViewById(R.id.txtSurveyLocationTitle) ;

        callNumber = view.findViewById(R.id.callPhoneNumber);
        if(getHeader().getCustomer_phone() != null && !getHeader().getCustomer_phone().equals("")){
            initListenerForDialIcon();
        }
        else{
            ConstraintLayout constraintLayout = view.findViewById(R.id.customerPhoneContainer);
            ConstraintSet newConstrain = new ConstraintSet();
            EditText eTextCustPhone = view.findViewById(R.id.txtCustomerPhone);
            newConstrain.clone(constraintLayout);
            newConstrain.constrainPercentWidth(eTextCustPhone.getId(),1);//make edit text match parent
            newConstrain.applyTo(constraintLayout);
            callNumber.setVisibility(View.GONE);
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //bong 16 apr 15 - button for print - move to print page
        mBinding.btnPrint.setOnClickListener(this);
        mBinding.btnReset.setOnClickListener(this);
        mBinding.btnStartSurvey.setOnClickListener(this);
        mBinding.btnViewTask.setOnClickListener(this);
        mBinding.btnRevisit.setOnClickListener(this);
        mBinding.btnChangePlan.setOnClickListener(this);

        setHeader((SurveyHeaderBean) bundleParams.getSerializable(SURVEY_HEADER));
        mBinding.setSurveyHeader(getHeader());

        if (bundleParams.getInt(SURVEY_MODE) == Global.MODE_SURVEY_TASK ||
                bundleParams.getInt(SURVEY_MODE) == Global.MODE_VIEW_SENT_SURVEY) {

            if (!(TaskHDataAccess.STATUS_SEND_INIT.equals(getHeader().getIs_prepocessed()) &&
                    TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(getHeader().getStatus()))) {
                mBinding.txtCustomerName.setEnabled(false);
                mBinding.txtCustomerPhone.setEnabled(false);
                mBinding.txtCustomerAddress.setEnabled(false);
                mBinding.txtNotes.setEnabled(false);
                mBinding.btnReset.setClickable(false);
                mBinding.btnReset.setVisibility(View.GONE);

                txtSurveyLocation.setFocusable(false);
                Drawable drawableCompat = txtSurveyLocation.getBackground();
                DrawableCompat.setTint(drawableCompat, Color.parseColor("#AAAAAA"));
            }

            if (TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(getHeader().getStatus()) ||
                    TaskHDataAccess.STATUS_SEND_DOWNLOAD.equals(getHeader().getStatus()) ||
                    TaskHDataAccess.STATUS_SEND_INIT.equals(getHeader().getStatus())) {
                Boolean isEnableRevisit = Global.FEATURE_RESCHEDULE_SURVEY;

                if (TaskHDataAccess.STATUS_SEND_INIT.equals(header.getStatus())) {
                    txtSurveyLocationTitle.setVisibility(View.GONE);
                    txtSurveyLocation.setVisibility(View.GONE);
                }

                String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application) &&
                        getHeader().getPriority() != null) {
                    if (Global.NEW_FEATURE) {
                        if (Boolean.TRUE.equals(isEnableRevisit)) {
                            mBinding.btnPts.setVisibility(View.VISIBLE);
                            mBinding.btnPts.setOnClickListener(this);
                            if (getHeader().getPts_date() != null) {
                                String ptsDate = Formatter.formatDate(getHeader().getPts_date(), Global.DATE_TIME_STR_FORMAT);
                                mBinding.lblPtsDate.setVisibility(View.VISIBLE);
                                mBinding.txtPtsDate.setVisibility(View.VISIBLE);
                                mBinding.txtPtsDate.setText(ptsDate);
                            }
                        } else {
                            mBinding.btnPts.setVisibility(View.GONE);
                        }
                    }
                }
                if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application) &&
                        getHeader().getPriority() != null) {
                    if(Global.PLAN_TASK_ENABLED && Global.isPlanStarted()){
                        if(Global.getCurrentPlanTask() != null && !Global.getCurrentPlanTask().equals(getHeader().getTaskH().getTask_id())){
                            mBinding.btnStartSurvey.setVisibility(View.GONE);
                            TodayPlanRepository planRepository = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                            if(planRepository != null){
                                boolean allowedChangePlan = planRepository.isAllowChangePlan();
                                if(allowedChangePlan){
                                    mBinding.btnChangePlan.setVisibility(View.VISIBLE);
                                }
                                else {
                                    mBinding.btnChangePlan.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
            } else if (TaskHDataAccess.STATUS_SEND_SENT.equals(getHeader().getStatus())) {
                if (getHeader().getIs_prepocessed() != null && getHeader().getIs_prepocessed().equals(RescheduleFragment.TASK_RESCHEDULE)) {
                    if (getHeader().getPts_date() != null) {
                        String ptsDate = Formatter.formatDate(getHeader().getPts_date(), Global.DATE_TIME_STR_FORMAT);
                        ptsLayout.setVisibility(View.VISIBLE);
                        mBinding.lblPtsDate.setVisibility(View.VISIBLE);
                        mBinding.txtPtsDate.setVisibility(View.VISIBLE);
                        mBinding.txtPtsDate.setText(ptsDate);
                    }
                }
                String application = GlobalData.getSharedGlobalData().getApplication();
                if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application) &&
                        getHeader().getPriority() != null) {
                    if (Global.NEW_FEATURE) {
                        if (Global.FEATURE_REVISIT_COLLECTION) { //new
                            if(getHeader().getAssignment_date() != null) {
                                mBinding.btnRevisit.setVisibility(View.VISIBLE);
                                mBinding.btnRevisit.setOnClickListener(this);
                            }
                        } else {
                            mBinding.btnRevisit.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }

        if (TaskHDataAccess.STATUS_SEND_PENDING.equals(getHeader().getStatus()) ||
                TaskHDataAccess.STATUS_SEND_UPLOADING.equals(getHeader().getStatus())) {
            LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.buttons);
            buttonLayout.setVisibility(View.GONE);
        }
        if (getHeader().getPriority() != null && getHeader().getPriority().length() > 0) {
            if (!getHeader().getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_SENT)) {
                getHeader().setOpen_date(Tool.getSystemDateTime());
                new CustomerFragment.SendOpenReadTaskH(getActivity(), getHeader()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
        record = new AudioRecord(getActivity());

        toDoList = new ToDoList(getActivity());
        listTaskH = toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, "");

        setIsEditable(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserHelp.showAllUserHelp(CustomerFragment.this.getActivity(),CustomerFragment.this.getClass().getSimpleName());
            }
        }, SHOW_USERHELP_DELAY_DEFAULT);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            bundleParams = savedInstanceState.getBundle("save_header_instance");
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("save_header_instance", bundleParams);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        setHasOptionsMenu(true);
        Utility.freeMemory();
        bundleParams = getArguments();
        context = activity;
        setHeader((SurveyHeaderBean) bundleParams.getSerializable(SURVEY_HEADER));
        if (bundleParams.getInt(SURVEY_MODE) != Global.MODE_VIEW_SENT_SURVEY) {
            checkScheme = new CheckScheme(this.getActivity());
            checkScheme.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Global.getSharedGlobal().setSchemeIsChange(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    private void initListenerForDialIcon(){
        //set click listener pada icon dial phone
        callNumber.setOnClickListener(this);
    }

    private void startDialIntent(){
        try{
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+getHeader().getCustomer_phone()));
            context.startActivity(dialIntent);
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    private boolean isCOHAktif() {
        String parameter = GeneralParameterDataAccess.getOne(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                Global.GS_CASHONHAND).getGs_value();
        return parameter != null && parameter.equals(Global.TRUE_STRING);
    }

    @Override
    public void onClick(View v) {
        int button = v.getId();
        if (button == R.id.btnPts) {
            cancelCheckScheme();
            RescheduleDialog fragment = new RescheduleDialog();
            Bundle args = new Bundle();
            args.putString("taskId", getHeader().getTask_id());
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragment.show(fragmentManager, "Reschedule");
        } else if (button == R.id.btnStartSurvey) {
            mBinding.btnStartSurvey.setEnabled(false);
            if(getHeader() == null){
                setHeader((SurveyHeaderBean) bundleParams.getSerializable(SURVEY_HEADER));
            }
            try {
                if ((header.getPriority() != null && header.getPriority().length() > 0) && !header.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_SENT)) {
                    header.setStart_date(Tool.getSystemDateTime());
                    final SendOpenReadTaskH task = new CustomerFragment.SendOpenReadTaskH(getActivity(), header);
                    task.setCallback(new AsyncTaskCallback() {
                        @Override
                        public void onTaskStarted(String message) {
                            if(null != message && "" != message) {
                                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                                dialogBuilder.withTitle(getString(R.string.failed_start_task))
                                        .isCancelableOnTouchOutside(false)
                                        .withMessage(message)
                                        .withButton1Text(getString(R.string.btnOk))
                                        .setButton1Click(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogBuilder.dismiss();
                                                mBinding.btnStartSurvey.setEnabled(true);
                                                return;
                                            }
                                        })
                                        .show();
                            }
                            else{
                                String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application) && isCOHAktif() && cashOnHand > limit) {
                                    //DialogManager.showAlertNotif(getActivity(), getActivity().getString(R.string.limit_coh), "Cash On Hand");
                                    Map resultMap = anyReachedLimitChannel(header.getUuid_task_h());
                                    List result = (List) resultMap.get("list");
                                    if (result != null && result.size() > 0) {
                                        Double amountInBatch = (Double) resultMap.get("amountInBatch");
                                        Double tagihan = (Double) resultMap.get("tagihan");
                                        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                                        dialogBuilder.withTitle(context.getString(R.string.info_capital))
                                                .isCancelableOnTouchOutside(false)
                                                .withIcon(android.R.drawable.ic_dialog_alert)
                                                .withMessage(getActivity().getString(R.string.paymentchannel_alert, Tool.separateThousand(amountInBatch), Tool.separateThousand(tagihan), result.toString()))
                                                .withMessage(getActivity().getString(R.string.paymentchannel_alert))
                                                .withButton1Text(context.getString(R.string.btnOk))
                                                .setButton1Click(new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialogBuilder.dismiss();
                                                        isEditable=false;
                                                        isFlag = "0";
                                                        GlobalData.getSharedGlobalData().setDoingTask(true);
                                                        gotoNextDynamicForm(getActivity());
                                                    }
                                                })
                                                .show();
                                    } else {
                                        isEditable=false;
                                        isFlag = "0";
                                        GlobalData.getSharedGlobalData().setDoingTask(true);
                                        gotoNextDynamicForm(getActivity());
                                    }
                                } else {
                                    if(getHeader().getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_DELETED)){
                                        DialogManager.showAlertNotif(getActivity(), getString(R.string.closedTaskMsg), getString(R.string.warning_capital));
                                        mBinding.btnStartSurvey.setEnabled(true);
                                    }else {
                                        setIsEditable(false);
                                        GlobalData.getSharedGlobalData().setDoingTask(true);
                                        gotoNextDynamicForm(getActivity());
                                    }
                                }
                            }
                        }
                    });
                    task.execute();
                }
            } catch (Exception e) {             FireCrash.log(e);
                if (Global.IS_DEV)
                    e.printStackTrace();
                String[] msg = {"Failed open questions,\nplease try again"};
                String alert = Tool.implode(msg, "\n");
                Toast.makeText(activity, alert, Toast.LENGTH_SHORT).show();
            }
        } else if (button == R.id.btnReset) {
            mBinding.txtCustomerName.setText("");
            mBinding.txtCustomerAddress.setText("");
            mBinding.txtNotes.setText("");
            mBinding.txtCustomerPhone.setText("");
            txtAgreement.setText("");
            txtOSAmount.setText("");
            txtOD.setText("");
            txtInstallmentNo.setText("");
        } else if (button == R.id.btnPrint) {
            cancelCheckScheme();
            List<PrintResult> results = PrintResultDataAccess.getAll(getActivity(), getHeader().getUuid_task_h());
            if (results == null || results.isEmpty()) {
                TaskManager.generatePrintResult(getActivity(), getHeader().getTaskH());
            }

            SecondHelper.Companion.doPrint(context, getHeader().getTask_id(), "log");
        } else if (button == R.id.btnViewTask) {
            GlobalData.getSharedGlobalData().setDoingTask(true);
            Global.getSharedGlobal().setIsViewer(true);
            setViewTask(true);
            setHeader((SurveyHeaderBean) bundleParams.getSerializable(SURVEY_HEADER));
            if (getHeader().getIs_prepocessed() != null && getHeader().getIs_prepocessed().equals(RescheduleFragment.TASK_RESCHEDULE)) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.view_task_reschedule),
                        Toast.LENGTH_SHORT).show();
                GlobalData.getSharedGlobalData().setDoingTask(false);
            } else {
                GlobalData.getSharedGlobalData().setDoingTask(true);
                cancelCheckScheme();
                Bundle extras = new Bundle();
                extras.putInt(Global.BUND_KEY_MODE_SURVEY, bundleParams.getInt(SURVEY_MODE));
                extras.putSerializable(Global.BUND_KEY_SURVEY_BEAN, bundleParams.getSerializable(SURVEY_HEADER));
                FormBean formBean = new FormBean(getHeader().getScheme());
                extras.putSerializable(Global.BUND_KEY_FORM_BEAN, formBean);
                QuestionSetTask task = new QuestionSetTask(getActivity(), extras);
                task.execute();
            }
        }
        else if(button == R.id.btnChangePlan){
            if(Global.PLAN_TASK_ENABLED && Global.isPlanStarted()){
                TodayPlanRepository planRepository = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                if(planRepository == null)
                    return;

                boolean allowedChangePlan = planRepository.isAllowChangePlan();
                if(allowedChangePlan){
                    planRepository.changePlan(Global.getCurrentPlanTask(),getHeader().getTaskH().getTask_id(), new IPlanTaskDataSource.Result<Boolean>() {
                        @Override
                        public void onResult(Boolean result) {
                            if(result == null)
                                return;
                            if(!result){
                                Toast.makeText(context, "Failed to change plan", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            mBinding.btnChangePlan.setVisibility(View.GONE);
                            mBinding.btnStartSurvey.setVisibility(View.VISIBLE);
                            Toast.makeText(context, context.getString(R.string.change_plan_success), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            //ON ERROR INSERT CODE HERE
                            if(GlobalData.isRequireRelogin()){
                                DialogManager.showForceExitAlert(getActivity(),getString(R.string.msgLogout));
                            } else {
                                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                                dialogBuilder.withTitle(getString(R.string.failed_change_plan))
                                        .isCancelableOnTouchOutside(false)
                                        .withMessage(error)
                                        .withButton1Text(getString(R.string.btnOk))
                                        .setButton1Click(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogBuilder.dismiss();
                                                return;
                                            }
                                        })
                                        .show();
                            }
                        }
                    });
                }
                else {
                    //hide change plan and start survey when not allowed to change plan
                    mBinding.btnStartSurvey.setVisibility(View.GONE);
                    mBinding.btnChangePlan.setVisibility(View.GONE);
                }
            }
        }

        else if (button == R.id.btnRevisit) {
            mBinding.btnRevisit.setEnabled(false);
            new AsyncTask<Void, Void, MssResponseType>() {
                private ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    setIsEditable(false);
                    progressDialog = ProgressDialog.show(context,
                            "", getString(R.string.progressWait), true);
                }

                @Override
                protected MssResponseType doInBackground(Void... params) {
                    CheckResubmitApi api = new CheckResubmitApi(context);
                    try {
                        if (Tool.isInternetconnected(context)) {
                            return api.request(getHeader().getUuid_task_h());
                        }
                        return null;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(MssResponseType checkResubmitResponse) {
                    super.onPostExecute(checkResubmitResponse);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                    }
                    if (checkResubmitResponse != null) {
                        if (checkResubmitResponse.getStatus().getMessage().equalsIgnoreCase("OK")) {
                            setIsEditable(true);
                            for (TaskH task : listTaskH) {
                                if (getHeader().getTask_id().equalsIgnoreCase(task.getTask_id())) {
                                    setIsEditable(false);
                                }
                            }
                            if (getIsEditable()) {
                                isFlag = "1";
                                GlobalData.getSharedGlobalData().setDoingTask(true);
                                gotoNextDynamicForm(CustomerFragment.this.getActivity());
                            } else {
                                mBinding.btnRevisit.setEnabled(true);
                                Toast.makeText(getActivity(), getActivity().getString(R.string.edit_still_pending),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                            dialogBuilder.withTitle(context.getString(R.string.info_capital))
                                    .isCancelableOnTouchOutside(false)
                                    .withMessage(checkResubmitResponse.getStatus().getMessage())
                                    .withButton1Text(context.getString(R.string.btnOk))
                                    .setButton1Click(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogBuilder.dismiss();
                                            doBack(getActivity());
                                        }
                                    })
                                    .show();
                        }
                    } else {
                        mBinding.btnRevisit.setEnabled(true);
                        Toast.makeText(getActivity(), getActivity().getString(R.string.msgErrorParsingJson),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
        else if(button == R.id.callPhoneNumber){
            startDialIntent();
        }
    }

    public void gotoNextDynamicForm(FragmentActivity activity) {
        Global.getSharedGlobal().setIsViewer(false);
        String emptyMessage = "";
        String regexMessage = "";
        if(bundleParams == null) {
            bundleParams = getArguments();
            setHeader((SurveyHeaderBean) bundleParams.getSerializable(SURVEY_HEADER));
            if (bundleParams.getInt(SURVEY_MODE) != Global.MODE_VIEW_SENT_SURVEY) {
                checkScheme = new CheckScheme(activity);
                checkScheme.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                Global.getSharedGlobal().setSchemeIsChange(false);
            }
        }
        if (bundleParams.getInt(SURVEY_MODE) == Global.MODE_SURVEY_TASK) {
            try {
                if (getHeader().getPriority() == null || getHeader().getPriority().length() == 0) {
                    emptyMessage = checkForEmpty();
                    regexMessage = checkRegex();
                    getHeader().setCustomer_name(mBinding.txtCustomerName.getText().toString());
                    getHeader().setCustomer_address(mBinding.txtCustomerAddress.getText().toString());
                    getHeader().setCustomer_phone(mBinding.txtCustomerPhone.getText().toString());
                    getHeader().setNotes(mBinding.txtNotes.getText().toString());
                }
            } catch (Exception e) {
                FireCrash.log(e);
            }
        } else if (bundleParams.getInt(SURVEY_MODE) == Global.MODE_VIEW_SENT_SURVEY) {
            if (getIsEditable() && (getHeader().getPriority() == null || getHeader().getPriority().length() == 0)) {
                emptyMessage = checkForEmpty();
                regexMessage = checkRegex();
            }
        } else {
            emptyMessage = checkForEmpty();
            regexMessage = checkRegex();
            getHeader().setCustomer_name(mBinding.txtCustomerName.getText().toString());
            getHeader().setCustomer_address(mBinding.txtCustomerAddress.getText().toString());
            getHeader().setCustomer_phone(mBinding.txtCustomerPhone.getText().toString());
            getHeader().setNotes(mBinding.txtNotes.getText().toString());
        }

        if (emptyMessage.length() > 0) {
            Toast.makeText(activity, emptyMessage,
                    Toast.LENGTH_SHORT).show();
        }
        else if(regexMessage.length() > 0){
            Toast.makeText(activity, regexMessage,
                    Toast.LENGTH_SHORT).show();
        }
        else {
            if (bundleParams.getInt(SURVEY_MODE) != Global.MODE_VIEW_SENT_SURVEY || (bundleParams.getInt(SURVEY_MODE) == Global.MODE_VIEW_SENT_SURVEY && getIsEditable())) {
                cancelCheckScheme();
                Scheme scheme = null;
                try {
                    String uuidScheme = getHeader().getUuid_scheme();
                    scheme = SchemeDataAccess.getOne(activity, uuidScheme);
                } catch (DaoException e) {
                    ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TAG, e.getMessage());
                    ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TIME_TAG, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception(GET_SCHEME_EXCEPTION));
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TAG, e.getMessage());
                    ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TIME_TAG, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception(GET_SCHEME_EXCEPTION));
                }

                if (scheme != null) {
                    getHeader().setScheme(scheme);
                    FormBean formBean = new FormBean(scheme);
                    if (getLastUpdateScheme() != null) {
                        formBean = new FormBean(getLastUpdateScheme());
                    }

                    if (getHeader().getStart_date() == null && (getHeader().getPriority() == null || getHeader().getPriority().length() == 0)) {
                        getHeader().setStart_date(Tool.getSystemDateTime());
                    }
                    getHeader().setForm(formBean);
                    getHeader().setIs_preview_server(formBean.getIs_preview_server());

                    Bundle extras = new Bundle();
                    if (getIsEditable()) {
                        bundleParams.putInt(SURVEY_MODE, Global.MODE_SURVEY_TASK);
                        getHeader().setStatus(TaskHDataAccess.STATUS_SEND_DOWNLOAD);
                    }
                    extras.putInt(Global.BUND_KEY_MODE_SURVEY, bundleParams.getInt(SURVEY_MODE));
                    extras.putString(Global.BUND_KEY_UUID_TASKH, getHeader().getUuid_task_h());
                    extras.putSerializable(Global.BUND_KEY_SURVEY_BEAN, getHeader());
                    extras.putSerializable(Global.BUND_KEY_FORM_BEAN, formBean);

                    QuestionSetTask task = new QuestionSetTask(activity, extras);
                    task.execute();
                    onDetach();
                } else {
                    Toast.makeText(activity, activity.getString(R.string.request_error),
                            Toast.LENGTH_SHORT).show();
                    doBack(activity);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        headerTrace.stop();
        cancelCheckScheme();
    }

    private String checkForEmpty() {
        String name = mBinding.txtCustomerName.getText().toString();
        String address = mBinding.txtCustomerAddress.getText().toString();
        String phone = mBinding.txtCustomerPhone.getText().toString();
        StringBuffer emptyMessage = new StringBuffer();
        String isRequired = " " + getString(R.string.msgRequired) + "\n";
        if (isEmpty(name)) {
            emptyMessage.append(getString(R.string.customer_name) + isRequired);
        }
        if (isEmpty(address)) {
            emptyMessage.append(getString(R.string.customer_address) + isRequired);
        }

        if (isEmpty(phone)) {
            emptyMessage.append(getString(R.string.customer_phone_number) + isRequired);
        } else if (phone.length() < 8) {
            emptyMessage.append(getString(R.string.phone_validasi));
        }

        return emptyMessage.toString();
    }

    private String checkRegex(){
        StringBuilder message = new StringBuilder();
        String notValidMsg = " " + getString(R.string.msgInvalid) + "\n";
        //check regex for customer name
        String name = mBinding.txtCustomerName.getText().toString();
        if(!Utility.isRegexCheckValid(name,"^[A-Za-z 0-9.,'_-]*$")){
            message.append(getString(R.string.customer_name)+ notValidMsg);
        }

        return message.toString();
    }

    private boolean isEmpty(String str) {
        boolean is_Empty = true;
        if (str.length() > 0)
            is_Empty = false;
        return is_Empty;
    }

    //new
    private void initiateRefresh() {
        cancelRefreshTask();
        backgroundTask = new RefreshBackgroundTask();
        backgroundTask.execute();
    }

    private void cancelRefreshTask() {
        if (backgroundTask != null) {
            backgroundTask.cancel(true);
            backgroundTask = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Firebase custom trace
        headerTrace.start();

        if (null != getHeader() && null != getHeader().getRv_number() && !getHeader().getRv_number().isEmpty())
            mBinding.btnPrint.setVisibility(View.GONE);
        getActivity().findViewById(R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(R.string.title_mn_customer_form));

        if (listTaskH != null) {
            initiateRefresh();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        setMainMenu(menu);
        try {
            if (GlobalData.getSharedGlobalData().getAuditData() == null)
                NewMainActivity.InitializeGlobalDataIfError(getActivity().getApplicationContext());

            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                menu.findItem(R.id.menuMore).setVisible(true);
                getMainMenu().findItem(R.id.mnViewMap).setVisible(false);
                getMainMenu().findItem(R.id.mnViewAllHeader).setVisible(false);
                getMainMenu().findItem(R.id.mnInstallmentSchedule).setVisible(true);
                getMainMenu().findItem(R.id.mnPaymentHistory).setVisible(true);
                getMainMenu().findItem(R.id.mnCollectionActivity).setVisible(true);
                getMainMenu().findItem(R.id.mnReceiptHistory).setVisible(true);
            } else {
                menu.findItem(R.id.menuMore).setVisible(false);
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorPrepareOptionMenu", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorPrepareOptionMenu", DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Prepare Option Menu"));
            if (Global.IS_DEV)
                e.printStackTrace();
        }
    }

    public void checkTLS() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContextcontext = SSLContext.getInstance("TLS");
        sslContextcontext.init(null, null, null);
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                SSLContext.getInstance("TLS");
            }
            ProviderInstaller.installIfNeeded(getContext());
        } catch (NoSuchAlgorithmException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if (id == R.id.menuMore) {
            getMainMenu().findItem(R.id.mnViewMap).setVisible(false);
            getMainMenu().findItem(R.id.mnViewAllHeader).setVisible(false);
        }
        if (id == R.id.mnInstallmentSchedule) {
            if (Global.installmentSchIntent != null) {
                Global.installmentSchIntent.putExtra(Global.BUND_KEY_TASK_ID, getHeader().getTask_id());
                startActivity(Global.installmentSchIntent);
            }
        } else if (id == R.id.mnPaymentHistory) {
            if (Global.paymentHisIntent != null) {
                Global.paymentHisIntent.putExtra(Global.BUND_KEY_TASK_ID, getHeader().getTask_id());
                startActivity(Global.paymentHisIntent);
            }
        } else if (id == R.id.mnCollectionActivity && Global.collectionActIntent != null) {
            Global.collectionActIntent.putExtra(Global.BUND_KEY_TASK_ID, getHeader().getTask_id());
            startActivity(Global.collectionActIntent);
        } else if (id == R.id.mnReceiptHistory) {
            if (null != Global.receiptHistoryIntent) {
                Global.receiptHistoryIntent.putExtra(Global.BUND_KEY_AGREEMENT_NO, header.getAppl_no());
                Global.receiptHistoryIntent.putExtra(Global.BUND_KEY_TASK_ID, header.getTask_id());
                startActivity(Global.receiptHistoryIntent);
            }
        }
        if(id == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UserHelp.showAllUserHelp(CustomerFragment.this.getActivity(), CustomerFragment.this.getClass().getSimpleName());
                }
            }, SHOW_USERHELP_DELAY_DEFAULT);
        }

        return super.onOptionsItemSelected(item);
    }

    public void cancelCheckScheme() {
        try {
            if (checkScheme != null) {
                checkScheme.cancel(true);
                checkScheme = null;
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TAG, e.getMessage());
            ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TIME_TAG, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception(GET_SCHEME_EXCEPTION));
            e.printStackTrace();
        }
    }

    private void loadTheme(){
        ThemeLoader themeLoader = new ThemeLoader(context);
        themeLoader.loadSavedColorSet(this);
    }

    private void applyTheme(DynamicTheme dynamicTheme){
        int btnColorNormal = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"btn_bg_normal"));
        int btnColorPress = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"btn_bg_pressed"));
        //create color state list for button states
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_pressed},  // pressed
                new int[] {}  // normal
        };

        int[] colorlist = new int[]{
                btnColorPress,
                btnColorNormal
        };
        ColorStateList colorStateList = new ColorStateList(states,colorlist);
        ThemeUtility.setViewBackground(mBinding.btnViewTask,colorStateList);
        ThemeUtility.setViewBackground(mBinding.btnPrint,colorStateList);
        ThemeUtility.setViewBackground(mBinding.btnPts,colorStateList);
        ThemeUtility.setViewBackground(mBinding.btnReset,colorStateList);
        ThemeUtility.setViewBackground(mBinding.btnRevisit,colorStateList);
        ThemeUtility.setViewBackground(mBinding.btnStartSurvey,colorStateList);
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme) {
        if(dynamicTheme == null)
            return;
        applyTheme(dynamicTheme);
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {
        //EMPTY
    }

    public static class SendOpenReadTaskH extends AsyncTask<Void, Void, String> {
        private TaskH taskH;
        private SurveyHeaderBean bean;
        private WeakReference<Context> context;
        private WeakReference<Activity> activity;
        private AsyncTaskCallback callback;

        public void setCallback(AsyncTaskCallback callback){
            this.callback = callback;
        }

        public SendOpenReadTaskH(Activity activity, SurveyHeaderBean bean) {
            this.bean = bean;
            this.context = new WeakReference<Context>(activity);
            this.activity = new WeakReference<Activity>(activity);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            if(!(bean.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_TASK_APPROVAL) ||
                    bean.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD) ||
                    bean.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_TASK_VERIFICATION)||
                    bean.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD))
            ){
                if (Tool.isInternetconnected(context.get())) {
                    if(Global.PLAN_TASK_ENABLED && !bean.getTask_id().equals(bean.getUuid_task_h())){
                        taskH = TaskHDataAccess.getOneHeader(context.get(),bean.getTask_id());
                        taskH.setOpen_date(bean.getOpen_date());//set open date from revisit task
                        taskH.setStart_date(bean.getStart_date());
                    }
                    else {
                        taskH = bean.getTaskH();
                    }

                    try {
                        if (null == taskH.getFlag()) {
                            taskH.setFlag("");
                        }
                    } catch (NullPointerException e) {
                        taskH.setFlag("");
                    }

                    JsonRequestOpenStartTask task = new JsonRequestOpenStartTask();
                    task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    task.addImeiAndroidIdToUnstructured();
                    task.setTaskH(taskH);

                    String json = GsonHelper.toJson(task);

                    String url = GlobalData.getSharedGlobalData().getURL_SUBMITOPENREADTASK();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
                    HttpConnectionResult serverResult = null;

                    //Firebase Performance Trace HTTP Request
                    HttpMetric networkMetric =
                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                        try {
                            serverResult = httpConn.requestToServer(url, json);
                            Utility.metricStop(networkMetric, serverResult);
                            String response = serverResult.getResult();
                            MssResponseType responseType = GsonHelper.fromJson(response, MssResponseType.class);
                            if (responseType.getStatus().getCode() == 0) {
                                try {
                                    if(Global.PLAN_TASK_ENABLED && CustomerFragment.getIsEditable())
                                        TaskHDataAccess.addOrReplace(context.get(), taskH);
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                    ACRA.getErrorReporter().putCustomData(ERROR_GET_MSG_FRM_SERVER, e.getMessage());
                                    ACRA.getErrorReporter().putCustomData(ERROR_GET_MSG_FRM_SERVER, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat convert json dari Server dan addOrReplace taskH"));
                                }
                            }else{
                                return responseType.getStatus().getMessage();
                            }
                        } catch (Exception e) {
                            FireCrash.log(e);
                            ACRA.getErrorReporter().putCustomData(ERROR_REQ_TO_SERVER, e.getMessage());
                            ACRA.getErrorReporter().putCustomData(ERROR_REQ_TO_SERVER, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat request to server"));
                            if (Global.IS_DEV)
                                e.printStackTrace();
                        }
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);

            if(callback != null && message != null) {
                callback.onTaskStarted(message);
            }
        }
    }

    private class GetTaskDOnline extends AsyncTask<Void, Void, List<TaskD>> {
        private ProgressDialog dialog;
        private SurveyHeaderBean bean;
        private WeakReference<Context> context;

        public GetTaskDOnline(Context context, SurveyHeaderBean bean) {
            this.context = new WeakReference<Context>(context);
            this.bean = bean;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(context.get(), "", context.get().getString(R.string.progressWait), true, false);
        }

        @Override
        protected List<TaskD> doInBackground(Void... params) {
            DetailTaskHResponse response = null;

            if (Tool.isInternetconnected(context.get())) {
                DetailTaskHRequest request = new DetailTaskHRequest();
                request.setUuidTaskH(bean.getUuid_task_h());
                request.setFlag(bean.getFlag());
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                HttpCryptedConnection httpConn = new HttpCryptedConnection(context.get(),
                        GlobalData.getSharedGlobalData().isEncrypt(), GlobalData.getSharedGlobalData().isDecrypt());
                String url = GlobalData.getSharedGlobalData().getURL_GET_TASK_LOG();
                HttpConnectionResult serverResult;

                HttpMetric networkMetric =
                        FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, GsonHelper.toJson(request));

                try {
                    serverResult = httpConn.requestToServer(url, GsonHelper.toJson(request), Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverResult);

                    if (serverResult != null && serverResult.isOK()) {
                        try {
                            response = GsonHelper.fromJson(serverResult.getResult(), DetailTaskHResponse.class);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            ACRA.getErrorReporter().putCustomData(ERROR_GET_MSG_FRM_SERVER, e.getMessage());
                            ACRA.getErrorReporter().putCustomData(ERROR_GET_MSG_FRM_SERVER, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat convert json dari Server"));
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData(ERROR_REQ_TO_SERVER, e.getMessage());
                    ACRA.getErrorReporter().putCustomData(ERROR_REQ_TO_SERVER, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat request to server"));
                    e.printStackTrace();
                }
            }
            if (response != null) {
                List<TaskD> taskDList = new ArrayList<>();

                for (TaskD taskD : response.getTaskDs()) {
                    taskDList.add(taskD);
                }

                return taskDList;
            }
            return Collections.emptyList();
        }

        @Override
        protected void onPostExecute(List<TaskD> results) {
            super.onPostExecute(results);

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (results != null) {
                //olivia: jika tidak bisa insert TaskD akan generate uuid baru
                for (TaskD taskD : results) {
                    try {
                        TaskDDataAccess.add(context.get(), taskD);
                    } catch (Exception e) {
                        taskD.setUuid_task_d(Tool.getUUID());
                        TaskDDataAccess.add(context.get(), taskD);
                    }
                }

                boolean isTaskPaid = TaskDDataAccess.isTaskPaid(getActivity(),
                        GlobalData.getSharedGlobalData().getUser().getUuid_user(), bean.getUuid_task_h());
                if (!bean.getScheme().getIs_printable().equals("1") || !isTaskPaid) {
                    mBinding.btnPrint.setVisibility(View.GONE);
                } else {
                    if (bean.getRv_number() != null && !bean.getRv_number().isEmpty())
                        mBinding.btnPrint.setVisibility(View.GONE);
                    else
                        mBinding.btnPrint.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.failed_get_taskd),
                        Toast.LENGTH_SHORT).show();
                doBack(getActivity());
            }
        }
    }

    public static class CheckScheme extends AsyncTask<Void, Void, Boolean> {
        Activity activity;
        public CheckScheme(Activity activity){
            this.activity = activity;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String uuidScheme = getHeader().getUuid_scheme();
                Scheme schema = SchemeDataAccess.getOne(activity, uuidScheme);
                if (schema != null) {
                    JsonRequestScheme requestScheme = new JsonRequestScheme();
                    requestScheme.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    requestScheme.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    requestScheme.setUuid_scheme(schema.getUuid_scheme());
                    requestScheme.setTask(Global.TASK_GETONE);

                    String json = GsonHelper.toJson(requestScheme);
                    String url = GlobalData.getSharedGlobalData().getURL_GET_SCHEME();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
                    HttpConnectionResult serverResult = null;

                    HttpMetric networkMetric =
                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                    try {
                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetric, serverResult);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        ACRA.getErrorReporter().putCustomData(ERROR_GET_MSG_FRM_SERVER, e.getMessage());
                        ACRA.getErrorReporter().putCustomData(ERROR_GET_MSG_FRM_SERVER, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat request to Server"));
                        if (Global.IS_DEV)
                            e.printStackTrace();
                    }
                    if (serverResult != null && serverResult.isOK()) {
                        try {
                            String result = serverResult.getResult();
                            JsonResponseScheme responseScheme = GsonHelper.fromJson(result, JsonResponseScheme.class);
                            List<Scheme> schemes = responseScheme.getListScheme();

                            Scheme scheme = schemes.get(0);
                            try {
                                setLastUpdateScheme(scheme);
                                //olivia : pengecekan form version
                                if(scheme!=null) {
                                    Integer new_last_update = Integer.valueOf(scheme.getForm_version());
                                    Integer temp_last_update = (Global.getSharedGlobal().getTempSchemeVersion().get(scheme.getUuid_scheme())!= null) ? Global.getSharedGlobal().getTempSchemeVersion().get(scheme.getUuid_scheme()): 0 ;
                                    Global.getSharedGlobal().setSchemeIsChange(new_last_update > temp_last_update);
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                            }

                        } catch (Exception e) {
                            FireCrash.log(e);
                            ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TAG, e.getMessage());
                            ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TIME_TAG, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                            ACRA.getErrorReporter().handleSilentException(e);
                            Global.getSharedGlobal().setSchemeIsChange(false);
                        }
                    } else {
                        Global.getSharedGlobal().setSchemeIsChange(false);
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TAG, e.getMessage());
                ACRA.getErrorReporter().putCustomData(ERROR_GET_SCHEME_TIME_TAG, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(e);
                Global.getSharedGlobal().setSchemeIsChange(false);
            }
            return true;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Global.getSharedGlobal().setSchemeIsChange(false);
        }
    }

    private class RefreshBackgroundTask extends AsyncTask<Void, Void, List<TaskH>> {

        static final int TASK_DURATION = 2 * 1000; // 2 seconds

        @Override
        protected List<TaskH> doInBackground(Void... params) {
            // Sleep for a small amount of time to simulate a background-task
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            listTaskH.clear();
            listTaskH.addAll(toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, ""));
            ToDoList.setListOfSurveyStatus(null);
            List<SurveyHeaderBean> list = new ArrayList<>();
            for (TaskH h : listTaskH) {
                list.add(new SurveyHeaderBean(h));
            }
            ToDoList.setListOfSurveyStatus(list);

            // Return a new random list of cheeses
            return listTaskH;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(List<TaskH> result) {
            super.onPostExecute(result);
        }

    }

    public static void setHeader(SurveyHeaderBean header) {
        CustomerFragment.header = header;
    }

    public static SurveyHeaderBean getHeader() {
        return header;
    }

    public static void setInstance(CustomerFragment instance) {
        CustomerFragment.instance = instance;
    }

    public static boolean isViewTask() {
        return viewTask;
    }

    public static void setViewTask(boolean viewTask) {
        CustomerFragment.viewTask = viewTask;
    }

    public static Menu getMainMenu() {
        return mainMenu;
    }

    public static void setMainMenu(Menu mainMenu) {
        CustomerFragment.mainMenu = mainMenu;
    }

    public static Scheme getLastUpdateScheme() {
        return lastUpdateScheme;
    }

    public static void setLastUpdateScheme(Scheme lastUpdateScheme) {
        CustomerFragment.lastUpdateScheme = lastUpdateScheme;
    }

    private Map anyReachedLimitChannel(String uuidTaskH){
        List<TaskD> taskReconciled = new ArrayList<>();
        double sum = 0;
        TaskH onGoingTask = TaskHDataAccess.getOneHeader(getActivity(), uuidTaskH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdf.format(onGoingTask.getDtm_crt());
        String taskBatch = TaskHDataAccess.getBatchByUuiTask(getActivity(), onGoingTask.getUuid_scheme(), sDate);
        if(taskBatch != null && !taskBatch.isEmpty()) {
            List<TaskD> reports = TaskDDataAccess.getTaskDTagTotalbyBatchId(getActivity(), taskBatch);
            for (TaskD taskD : reports) {
                TaskH taskH = TaskHDataAccess.getOneHeader(getActivity(), taskD.getUuid_task_h());
                if (taskH != null ){
                    if(taskH.getIs_reconciled() == null || (taskH.getIs_reconciled() != null && taskH.getIs_reconciled().equals("0"))) {
                        taskReconciled.add(taskD);
                    }
                }
            }

            try {
                for (TaskD item : taskReconciled) {
                    String value = item.getText_answer();
                    if (value == null || value.equals("")) value = "0";
                    String tempAnswer = Tool.deleteAll(value, ",");
                    String[] intAnswer = Tool.split(tempAnswer, ".");
                    if (intAnswer.length > 1) {
                        if (intAnswer[1].equals("00"))
                            value = intAnswer[0];
                        else {
                            value = tempAnswer;
                        }
                    } else {
                        value = tempAnswer;
                    }
                    double finalValue = Double.parseDouble(value);
                    sum += finalValue;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        TaskD taskD = TaskDDataAccess.getTaskDTagTagihanByTaskH(getActivity(), uuidTaskH);
        double taskTagihan = sum + Double.parseDouble(taskD.getText_answer());
        List<PaymentChannel> channel = PaymentChannelDataAccess.getReachedMaxLimit(getActivity(), taskTagihan );

        Map resultMap = new HashMap<>();
        List result = new ArrayList<>();
        for(PaymentChannel channelDesc : channel){
            result.add("["+channelDesc.getDescription()+" - "+Tool.separateThousand(channelDesc.getPayment_limit())+"]");
        }
        resultMap.put("list", result);
        resultMap.put("amountInBatch", sum);
        resultMap.put("tagihan", Double.parseDouble(taskD.getText_answer()));
        return resultMap;
    }

}