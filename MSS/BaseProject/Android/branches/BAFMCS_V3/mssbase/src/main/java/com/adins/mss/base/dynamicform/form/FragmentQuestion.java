package com.adins.mss.base.dynamicform.form;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.checkin.CheckInManager;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dukcapil.SubmitImageDkcp;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.LocationInfo2;
import com.adins.mss.base.dynamicform.TaskManager;
import com.adins.mss.base.dynamicform.VoiceNotePage;
import com.adins.mss.base.dynamicform.form.models.CheckRejectedOrderRequest;
import com.adins.mss.base.dynamicform.form.models.CheckRejectedOrderResponse;
import com.adins.mss.base.dynamicform.form.models.CriteriaParameter;
import com.adins.mss.base.dynamicform.form.models.LookupAnswerBean;
import com.adins.mss.base.dynamicform.form.models.LookupCriteriaBean;
import com.adins.mss.base.dynamicform.form.models.Parameter;
import com.adins.mss.base.dynamicform.form.models.ParameterAnswer;
import com.adins.mss.base.dynamicform.form.models.ReviewTask;
import com.adins.mss.base.dynamicform.form.questions.DrawingCanvasActivity;
import com.adins.mss.base.dynamicform.form.questions.ImageViewerActivity;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.base.dynamicform.form.questions.QuestionReviewAdapter;
import com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter;
import com.adins.mss.base.dynamicform.form.questions.QuestionsValidator;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ExpandableRecyclerView;
import com.adins.mss.base.dynamicform.form.questions.viewholder.LookupFilterActivity;
import com.adins.mss.base.dynamicform.form.questions.viewholder.TextQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.view.DynamicQuestionView;
import com.adins.mss.base.ktpValidation.JsonResponseKtpValidation;
import com.adins.mss.base.payment.PaxPayment;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.LookupDao;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.ReceiptVoucher;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.camerainapp.CameraActivity;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.ReceiptVoucherDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.formatter.DateFormatter;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.image.ExifData;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.adins.mss.foundation.operators.IfElseFunctionDummy;
import com.adins.mss.foundation.operators.IfElseFunctionForCopyValue;
import com.adins.mss.foundation.print.rv.syncs.SyncRVRequest;
import com.adins.mss.foundation.print.rv.syncs.SyncRVResponse;
import com.adins.mss.foundation.print.rv.syncs.SyncRVTask;
import com.adins.mss.foundation.print.rv.syncs.SyncRvListener;
import com.adins.mss.foundation.questiongenerator.NotEqualSymbol;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.LocationTagingView;
import com.adins.mss.foundation.sync.api.SynchronizeResponseLookup;
import com.adins.mss.foundation.sync.api.model.SynchronizeRequestModel;
import com.gadberry.utility.expression.Expression;
import com.gadberry.utility.expression.OperatorSet;
import com.gadberry.utility.expression.symbol.AndSymbol;
import com.gadberry.utility.expression.symbol.OrSymbol;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.google.firebase.perf.metrics.Trace;
import com.google.gson.JsonSyntaxException;
import com.soundcloud.android.crop.util.Log;

import org.acra.ACRA;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import java.io.File;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;

import static android.provider.MediaStore.MediaColumns.DATA;
import static com.adins.mss.base.dynamicform.form.questions.viewholder.LookupCriteriaOnlineActivity.KEY_SELECTED_CRITERIA;


/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class FragmentQuestion extends Fragment implements OnQuestionClickListener, DynamicQuestion {
    private static String TAG = "FragmentQuestion";
    public static final int SUBMIT_FORM = 120;
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static int NEXT_QUESTION = 111;
    public static int SAVE_QUESTION = 112;
    public static int SEND_QUESTION = 113;
    public static int SEARCH_QUESTION = 114;
    public static int VERIFY_QUESTION = 115;
    public static int APPROVE_QUESTION = 116;
    public static int REJECT_QUESTION = 117;

    public static int RESULT_FROM_DRAWING_QUESTION = 222;
    public static int RESULT_FROM_LOCATION_QUESTION = 224;
    public static int RESULT_FROM_BUILT_IN_CAMERA = 225;
    public static int RESULT_FROM_ANDROID_CAMERA = 226;
    public static int RESULT_FROM_EDIT_IMAGE = 227;
    public static int RESULT_FROM_LOOKUP_CRITERIA = 228;
    public static String BUND_KEY_ACTION = "BUND_KEY_ACTION";
    public static String BUND_KEY_SEARCH_ACTION = "BUND_KEY_SEARCH_ACTION";
    public static QuestionHandler questionHandler;
    public QuestionViewAdapter questionAdapter;
    private static Menu mainMenu;
    List<QuestionBean> listOfQuestions;
    ExpandableRecyclerView qRecyclerView;
    ExpandableRecyclerView rRecyclerView;
    QuestionsValidator questionsValidator;
    String lastQuestionGroup;
    private int mColumnCount = 1;
    private QuestionReviewAdapter reviewAdapter;
    private List<String> listOfQuestionGroup;
    private LinkedHashMap<String, List<QuestionBean>> listOfQuestionBean;
    private QuestionBean focusBean;
    private int focusGroup;
    private int focusPosition;
    private int questionSize = 0;
    private JexlEngine jexlEngine;
    private boolean needStop = false;
    private boolean hasLoading = false;
    private boolean isSimulasi = false;
    private boolean isFinish = false;
    private boolean isAutoSaveRunning = false;
    private boolean haveSent = false;
    private int mode;
    private boolean isSaveAndSending = false;
    private LinkedHashMap<String, List<LookupCriteriaBean>> listOfLookupOnlineStored;
    private DynamicQuestionView form;
    private boolean firstLoadKonvergen;
    private LinkedHashMap<String, String> defaultCopyValueBean = new LinkedHashMap<>();
    private GeneralParameter gp_jobspv;
    private static final String SCRIPT_CHECK_PAYMENT_METHOD = "{MC_BAYAR}==YA && {MC_PAYMENT_BY}==DEBIT";
    private Trace questionFormTrace;

    public FragmentQuestion() {
        questionAdapter = new QuestionViewAdapter(getActivity(), qRecyclerView, listOfQuestionGroup, listOfQuestionBean, this);
    }

    public static FragmentQuestion newInstance(int columnCount) {
        FragmentQuestion fragment = new FragmentQuestion();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static void updateMenuIcon() {
        UpdateMenuIcon uItem = new UpdateMenuIcon();
        uItem.updateGPSIcon(mainMenu);
    }

    private static String removeLastChar(String str) {
        if (str != null && str.length() > 0) {
            return str.substring(0, str.length() - 1);
        } else {
            return "";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainMenu = null;
        questionHandler = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //EMPTY
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questionFormTrace = FirebasePerformance.getInstance().newTrace(getString(R.string.question_form_trace));
        GlobalData.getSharedGlobalData().setDoingTask(false);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        questionHandler = new QuestionHandler();
        listOfQuestions = new ArrayList<>();
        listOfQuestionBean = new LinkedHashMap<>();
        listOfQuestionGroup = new ArrayList<>();
        String msgRequired = getActivity().getString(R.string.msgRequired);
        questionsValidator = new QuestionsValidator(msgRequired, getActivity());
        listOfLookupOnlineStored = new LinkedHashMap<>();
        if (DynamicFormActivity.getHeader().getForm().getForm_type().equals(Global.FORM_TYPE_KTP)) {
            firstLoadKonvergen = true;
        }
        if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_SURVEY)) {
            gp_jobspv = GeneralParameterDataAccess.getOne(getContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_MS_JOBSPV);
        } else {
            gp_jobspv = new GeneralParameter();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
        mainMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        updateMenuIcon();
        if (Global.IS_DEV) {
            mainMenu.findItem(R.id.menuMore).setVisible(true);
            if (isFinish && mode != Global.MODE_VIEW_SENT_SURVEY && !isSimulasi) {
                mainMenu.findItem(R.id.mnPendingTask).setVisible(true);
            } else {
                mainMenu.findItem(R.id.mnPendingTask).setVisible(false);
            }
        }

        /*20170411 - Hilangkan voice note*/
        mainMenu.findItem(R.id.menuMore).setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.mnPendingTask) {
            if (Global.IS_DEV) {
                doPending();
            }
            return true;
        } else if (id == R.id.mnRecord) {
            Intent intent = new Intent(getActivity(), VoiceNotePage.class);
            Bundle extras = new Bundle();
            extras.putInt(Global.BUND_KEY_MODE_SURVEY, mode);
            extras.putSerializable(Global.BUND_KEY_SURVEY_BEAN, DynamicFormActivity.getHeader());
            intent.putExtras(extras);
            getActivity().startActivityForResult(intent, Global.REQUEST_VOICE_NOTES);
            return true;
        } else if (id == R.id.mnGPS) {
            if (Global.LTM != null) {
                if (Global.LTM.getIsConnected()) {
                    Global.LTM.removeLocationListener();
                    Global.LTM.connectLocationClient();
                } else {
                    CheckInManager.startGPSTracking(getActivity());
                }
                Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.gps_rotate);
                getActivity().findViewById(R.id.mnGPS).startAnimation(a);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_list, container, false);

        // Set the questionAdapter
        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            qRecyclerView = (ExpandableRecyclerView) view.findViewById(R.id.questionList);
            if (mColumnCount <= 1) {
                int duration = getResources().getInteger(R.integer.scroll_duration);
                qRecyclerView.setLayoutManager(new ScrollingLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false, duration));
            } else {
                qRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            questionAdapter = new QuestionViewAdapter(getActivity(), qRecyclerView, listOfQuestionGroup, listOfQuestionBean, this);
            qRecyclerView.setAdapter(questionAdapter);

            rRecyclerView = (ExpandableRecyclerView) view.findViewById(R.id.reviewList);
            showReviewScreen(false);

            initByStatus();
            setHasOptionsMenu(true);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalData.getSharedGlobalData().setDoingTask(false);

        //Nendi: 2019.06.21 - Prevent from task swiped.
        Global.setLockTask(!Global.isLockTask());
        Utility.freeMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        questionFormTrace = FirebasePerformance.getInstance().newTrace(getString(R.string.question_form_trace));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        jexlEngine = new JexlEngine();
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mode = bundle.getInt(Global.BUND_KEY_MODE_SURVEY);
            isSimulasi = bundle.getBoolean(Global.BUND_KEY_MODE_SIMULASI);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSetLocationClick(QuestionBean bean, int group, int position) {
        focusBean = bean;
        focusGroup = group;
        focusPosition = position;
    }

    @Override
    public void onEditDrawingClick(QuestionBean bean, int group, int position) {
        focusBean = bean;
        focusGroup = group;
        focusPosition = position;
    }

    @Override
    public void onCapturePhotoClick(QuestionBean bean, int group, int position) {
        focusBean = bean;
        focusGroup = group;
        focusPosition = position;
    }

    @Override
    public void onLookupSelectedListener(QuestionBean bean, int group, int position) {
        focusBean = bean;
        focusGroup = group;
        focusPosition = position;
        if (bean.getQuestion_value() != null && !bean.getQuestion_value().isEmpty()) {
            try {
                CriteriaParameter criteriaParameter = GsonHelper.fromJson(bean.getQuestion_value(), CriteriaParameter.class);
                List<Parameter> parameters = criteriaParameter.getParameters();
                List<LookupAnswerBean> beanList = new ArrayList<>();
                for (Parameter parameter : parameters) {
                    String identifier = parameter.getRefId();
                    QuestionBean questionBean = Constant.getListOfQuestion().get(identifier);
                    LookupAnswerBean answerBean = new LookupAnswerBean(questionBean);
                    answerBean.setCanEdit(parameter.getFlag().equals(Global.TRUE_STRING));
                    answerBean.setReadOnly(answerBean.isCanEdit());
                    beanList.add(answerBean);
                }
                bean.setLookupsAnswerBean(beanList);
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV) {
                    e.printStackTrace();
                }
            }
        }
        LookupFilterActivity.selectedBean = bean;
        Intent intent = new Intent(getActivity(), LookupFilterActivity.class);
        intent.putExtra(Global.BUND_KEY_MODE_SURVEY, mode);
        getActivity().startActivityForResult(intent, Global.REQUEST_LOOKUP_ANSWER);
    }

    @Override
    public void onReviewClickListener(QuestionBean bean, int group, int position) {
        if (!TaskHDataAccess.STATUS_TASK_APPROVAL.equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus()) && !TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD
                .equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus()) && mode != Global.MODE_VIEW_SENT_SURVEY) {
            isFinish = false;
            focusBean = bean;
            focusGroup = group;
            focusPosition = position;
            showReviewScreen(false);

            getActivity().findViewById(R.id.btnReject).setClickable(false);
            getActivity().findViewById(R.id.btnClose).setClickable(true);
            getActivity().findViewById(R.id.btnNextLayout).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.btnNext).setClickable(true);
            getActivity().findViewById(R.id.btnSendLayout).setVisibility(View.GONE);
            getActivity().findViewById(R.id.btnSend).setClickable(false);
            getActivity().findViewById(R.id.btnVerified).setClickable(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    questionAdapter.expand(focusGroup);
                    int finalPosition = focusPosition + getCounterListBeforeGroup(focusGroup, false);
                    setFocusable(finalPosition, 800);
                }
            }, 200);
        }
    }

    @Override
    public void onValidasiDukcapilListener(QuestionBean bean, int group, int position) {
        focusBean = bean;
        focusGroup = group;
        focusPosition = position;
        int positions = focusPosition + getCounterListBeforeGroup(focusGroup, false);
        changeItem(positions);
        Utility.freeMemory();
        doNext(true);
    }

    private void initByStatus() {
        try {
            if (TaskHDataAccess.STATUS_TASK_APPROVAL.equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus()) || TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD
                    .equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus()) || mode == Global.MODE_VIEW_SENT_SURVEY) {
                try {
                    DynamicFormActivity.setAllowImageEdit(false);
                    DynamicFormActivity.setIsApproval(true);
                    getActivity().findViewById(R.id.btnClose).setClickable(false);
                    isFinish = true;
                    if (mode != Global.MODE_VIEW_SENT_SURVEY) {
                        DynamicFormActivity.setIsVerified(true);
                    }
                    showFinishScreen();
                    DynamicQuestionView.dismissProgressBar();
                } catch (Exception e) {
                    FireCrash.log(e);
                    loadQuestionForm();
                }
            } else if (TaskHDataAccess.STATUS_TASK_VERIFICATION.equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus()) || TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD
                    .equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus())) {
                try {
                    DynamicFormActivity.setAllowImageEdit(false);
                    DynamicFormActivity.setIsVerified(true);
                    hasLoading = true;
                    loadDraftData(true);
                } catch (Exception e) {
                    FireCrash.log(e);
                    loadQuestionForm();
                }
            } else if (TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(DynamicFormActivity.getHeader().getStatus())) {
                try {
                    hasLoading = true;
                    loadDraftData(false);
                } catch (Exception e) {
                    FireCrash.log(e);
                    loadQuestionForm();
                }

            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadQuestionForm();
                        firstLoadKonvergen = false;
                        DynamicQuestionView.dismissProgressBar();
                    }
                }, 10);
            }
        } catch (Exception e) {
            FireCrash.log(e);
            Toast.makeText(getActivity(), getString(R.string.request_error), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void loadReviewData() {
        new AsyncTask<Void, Void, Boolean>() {
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(getActivity(), "", getActivity().getString(R.string.please_wait), true);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                Log.e("TEST VALUE CONSTANT");
                android.util.Log.i(TAG, Constant.getListOfQuestion().size() + " = Value Constant \n");
                return loadQuestionForReview(Constant.getListOfQuestion().size(), true);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) {
                            e.printStackTrace();
                        }
                    }
                }
                if (Boolean.TRUE.equals(aBoolean)) {
                    try {
                        int range = getCounterListBeforeGroup(reviewAdapter.getGroupItemCount(), true) + 1;
                        reviewAdapter.notifyItemRangeInserted(0, range);
                        reviewAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) {
                            e.printStackTrace();
                        }
                    }
                }
                showReviewScreen(true);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.execute();
    }

    private void loadDraftData(final boolean forVerification) {
        new AsyncTask<Void, Void, Boolean>() {
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(getActivity(), "", getActivity().getString(R.string.please_wait), true);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                if (forVerification) {
                    return loadQuestionForReview(Constant.getListOfQuestion().size(), false);
                } else {
                    return loadQuestionForReview(DynamicFormActivity.getHeader().getLast_saved_question(), true);
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) {
                            e.printStackTrace();
                        }
                    }
                }
                validateAllMandatory(true);
                removeNextCallback();
                needStop = true;
                int range = getCounterListBeforeGroup(questionAdapter.getGroupItemCount(), false) + 1;
                questionAdapter.notifyItemRangeInserted(0, range);
                questionAdapter.notifyDataSetChanged();

                // 2017/10/08 | olivia : set question group jadi expand dan focus di last question
                for (int i = 0; i <= questionAdapter.getGroupItemCount(); i++) {
                    questionAdapter.expand(i);
                }
                focusToView(listOfQuestions.size());
                firstLoadKonvergen = false;
            }
        }.execute();
    }

    private void loadSingleQuestion(QuestionBean bean, int position, boolean isThreadActive) {
        questionFormTrace.start();
        bean.setVisible(true);
        if (null != bean.getIntTextAnswer() && !"".equalsIgnoreCase(bean.getIntTextAnswer())) {
            bean.setAnswer(bean.getIntTextAnswer());
        }
        if (Tool.isOptions(bean.getAnswer_type())) {
            List<OptionAnswerBean> options = getOptionsForQuestion(bean, true);
            bean.setOptionAnswers(options);
            if (bean.getSelectedOptionAnswers() != null && !bean.getSelectedOptionAnswers().isEmpty()) {//validate whether in new task / save draft
                for (int i = 0; i < bean.getOptionAnswers().size(); i++) {
                    if (!bean.getOptionAnswers().get(i).getCode().equals(bean.getSelectedOptionAnswers().get(0).getCode())) { // if LOV changed, set isChange to true
                        bean.setIsCanChange(true);
                        bean.setChange(true);
                    } else {
                        bean.setIsCanChange(false);
                        bean.setChange(false);
                        break;
                    }
                }
            }
        }
        if (bean.getQuestion_value() != null && !bean.getQuestion_value().isEmpty() && !Global.AT_PDF.equals(bean.getAnswer_type()) /*&& !QuestionViewAdapter.IsLookupQuestion(bean.getAnswer_type())*/) {
            boolean isCopyFromLookup = false;
            boolean isCopyValue = false;
            boolean isCopyValueFromIdentifier = false;
            try {
                if (bean.getQuestion_value().contains("copyFromLookup(")) {
                    isCopyFromLookup = bean.getQuestion_value().substring(0, 15).equals("copyFromLookup(");
                }
                isCopyValue = bean.getQuestion_value().substring(0, 4).equals("copy");
                isCopyValueFromIdentifier = bean.getQuestion_value().substring(0, 1).equals("{");
            } catch (Exception e) {
                FireCrash.log(e);
                isCopyFromLookup = false;
                if (Global.IS_DEV) {
                    e.printStackTrace();
                }
            }
            if (bean.isReadOnly() || !QuestionBean.isHaveAnswer(bean) || (QuestionBean.isHaveAnswer(bean) && !QuestionViewAdapter.IsTextQuestion(bean.getAnswer_type())) || Tool
                    .isOptions(bean.getAnswer_type()) || (isCopyFromLookup) || (isCopyValue) || (isCopyValueFromIdentifier)) {

                String script = bean.getQuestion_value();
                if (!(script.contains("{\"parameters\":[") && script.contains("]}")) && !copyQuestionValue(bean, bean.getQuestion_value()) && (Global.IS_DEV && !isThreadActive)) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.copy_value_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }
        calculateQuestionBean(bean);
        if (QuestionViewAdapter.IsLookupQuestion(bean.getAnswer_type()) && (bean.getAnswer() != null && !bean.getAnswer().isEmpty())) {
            bean.setAnswer(bean.getAnswer());
        }
        addQuestionLabel(bean.getQuestion_label() + " " + Global.DELIMETER_ROW + " " + bean.getQuestion_group_name());
        addItem(bean, position, isThreadActive);
        if (!isThreadActive && (!bean.isRelevanted() && !hasLoading)) {
            if ((!bean.isMandatory() && !needStop && validateCurrentPage(true, false)) || (QuestionBean.isHaveAnswer(bean) && !needStop && ((!Tool
                    .isOptions(bean.getAnswer_type()) || bean.isReadOnly()) && validateCurrentPage(true, false)))) {
                loadQuestionForm();
            }
            needStop = false;
        }
        questionFormTrace.stop();
    }

    public boolean loadQuestionForm() {
        boolean isLastQuestion = true;
        if (!listOfQuestions.isEmpty()) {
            QuestionBean mbean = listOfQuestions.get(listOfQuestions.size() - 1);
            questionSize = DynamicFormActivity.getListOfIdentifier().indexOf(mbean.getIdentifier_name()) + 1;
        }
        int lastposition = questionSize;
        for (; lastposition < Constant.getListOfQuestion().size(); lastposition++) {
            QuestionBean bean = Constant.getListOfQuestion().get(DynamicFormActivity.getListOfIdentifier().get(lastposition));
            questionSize++;
            if (bean.isVisible()) {

                String relevantExpression = bean.getRelevant_question();
                String relevantMandatory = bean.getRelevant_mandatory();
                if (relevantExpression == null) {
                    relevantExpression = "";
                }
                if (relevantMandatory == null) {
                    relevantMandatory = "";
                }
                bean.setRelevantMandatory(isQuestionVisibleIfRelevant(relevantMandatory, bean, true));
                if (isQuestionVisibleIfRelevant(relevantExpression, bean, false) || bean.isRelevantMandatory()) {
                    loadSingleQuestion(bean, lastposition, false);
                    isLastQuestion = false;
                    break;
                } else {
                    bean.setVisible(false);
                    QuestionBean.resetAnswer(bean);
                    removeQuestionLabel(lastposition);
                    calculateQuestionBean(bean);
                }
            } else {
                if (lastposition == 0) {
                    doNext(false);
                } else {
                    String relevantExpression = bean.getRelevant_question();
                    String relevantMandatory = bean.getRelevant_mandatory();
                    if (relevantExpression == null) {
                        relevantExpression = "";
                    }
                    if (relevantMandatory == null) {
                        relevantMandatory = "";
                    }
                    boolean isMandatory = isQuestionVisibleIfRelevant(relevantMandatory, bean, true);
                    bean.setRelevantMandatory(isMandatory);
                    if (isQuestionVisibleIfRelevant(relevantExpression, bean, false) || isMandatory) {
                        QuestionSet tempQuestion = QuestionSetDataAccess
                                .getOne(getContext(), DynamicFormActivity.getHeader().getUuid_scheme(), bean.getQuestion_id(), bean.getQuestion_group_id());
                        if (tempQuestion != null && (tempQuestion.getIs_visible().equals(Global.TRUE_STRING) || tempQuestion.getIs_mandatory()
                                .equals(com.adins.mss.constant.Global.TRUE_STRING))) {
                            loadSingleQuestion(bean, lastposition, false);
                            isLastQuestion = false;
                            break;
                        }
                    } else {
                        bean.setVisible(false);
                        QuestionBean.resetAnswer(bean);
                    }
                    calculateQuestionBean(bean);
                }
            }
            if (Constant.getListOfQuestion().size() == lastposition) {
                isLastQuestion = true;
            }
        }
        return isLastQuestion;
    }

    public boolean loadQuestionForReview(int targetLastPosition, boolean loadToFinish) {
        boolean isLastQuestion = false;
        if (!listOfQuestions.isEmpty()) {
            QuestionBean mbean = listOfQuestions.get(listOfQuestions.size() - 1);
            questionSize = DynamicFormActivity.getListOfIdentifier().indexOf(mbean.getIdentifier_name()) + 1;
        }
        int lastposition = questionSize;
        for (; lastposition < targetLastPosition; lastposition++) {
            try {
                if (DynamicFormActivity.getListOfIdentifier().size() > lastposition) {
                    QuestionBean bean = Constant.getListOfQuestion().get(DynamicFormActivity.getListOfIdentifier().get(lastposition));
                    questionSize++;
                    if (bean.isVisible()) {

                        String relevantExpression = bean.getRelevant_question();
                        if (relevantExpression == null) {
                            relevantExpression = "";
                        }
                        if (isQuestionVisibleIfRelevant(relevantExpression, bean, false)) {
                            loadSingleQuestion(bean, lastposition, true);
                        } else {
                            bean.setVisible(false);
                            QuestionBean.resetAnswer(bean);
                            removeQuestionLabel(lastposition);
                            calculateQuestionBean(bean);
                        }
                    } else {
                        if (lastposition == 0) {
                            doNext(false);
                        } else {
                            String relevantExpression = bean.getRelevant_question();
                            if (relevantExpression == null) {
                                relevantExpression = "";
                            }
                            if (isQuestionVisibleIfRelevant(relevantExpression, bean, false)) {
                                QuestionSet tempQuestion = QuestionSetDataAccess
                                        .getOne(getContext(), DynamicFormActivity.getHeader().getUuid_scheme(), bean.getQuestion_id(), bean.getQuestion_group_id());
                                if (tempQuestion != null && tempQuestion.getIs_visible().equals(Global.TRUE_STRING)) {
                                    loadSingleQuestion(bean, lastposition, true);
                                } else {
                                    calculateQuestionBean(bean);
                                }
                            } else {
                                bean.setVisible(false);
                                calculateQuestionBean(bean);
                            }
                        }
                    }
                    if (!loadToFinish && needStop) {
                        isLastQuestion = false;
                        break;
                    }
                } else {
                    break;
                }
            } catch (IndexOutOfBoundsException eob) {
                if (Global.IS_DEV) {
                    eob.printStackTrace();
                }
                break;
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV) {
                    e.printStackTrace();
                }
            }
        }
        if (targetLastPosition == lastposition) {
            isLastQuestion = true;
        }
        return isLastQuestion;
    }

    private void showReviewScreen(boolean doShow) {
        if (doShow) {
            getActivity().setTitle(getString(R.string.title_question_review));
            rRecyclerView.setVisibility(View.VISIBLE);
            reviewAdapter = new QuestionReviewAdapter(getActivity(), rRecyclerView, listOfQuestionGroup, listOfQuestionBean, this);
            rRecyclerView.setAdapter(reviewAdapter);
            reviewAdapter.notifyDataSetChanged();
            qRecyclerView.setVisibility(View.GONE);
            if (Global.IS_DEV && mainMenu != null && (mainMenu.findItem(R.id.mnPendingTask) != null && mode != Global.MODE_VIEW_SENT_SURVEY && !isSimulasi)) {
                mainMenu.findItem(R.id.mnPendingTask).setVisible(true);
            }
            for (int i = 0; i <= reviewAdapter.getGroupItemCount(); i++) {
                reviewAdapter.expand(i);
            }
        } else {
            if (mainMenu != null && mainMenu.findItem(R.id.mnPendingTask) != null) {
                mainMenu.findItem(R.id.mnPendingTask).setVisible(false);
            }

            getActivity().setTitle(getString(R.string.title_question_form));
            rRecyclerView.setVisibility(View.GONE);
            qRecyclerView.setVisibility(View.VISIBLE);
        }
        hideKeyboard();
    }

    private void showFinishScreen() {
        if (Global.NEW_FEATURE) {
            if (Global.FEATURE_REJECT_WITH_RESURVEY) {
                //if not svy spv or no more question, btnNext GONE.
                if (!DynamicFormActivity.getIsVerified()) {
                    getActivity().findViewById(R.id.btnNextLayout).setVisibility(View.GONE);
                } else {
                    getActivity().findViewById(R.id.btnNextLayout).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.btnNext).setEnabled(true);
                    getActivity().findViewById(R.id.btnNext).setClickable(true);
                }
            }
        } else {
            getActivity().findViewById(R.id.btnNextLayout).setVisibility(View.GONE);
        }

        if (mode != Global.MODE_VIEW_SENT_SURVEY) {
            if (TaskHDataAccess.STATUS_TASK_APPROVAL.equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus()) || TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD
                    .equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus()) || TaskHDataAccess.STATUS_TASK_VERIFICATION
                    .equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus()) || TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD
                    .equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus()) || GlobalData.getSharedGlobalData().getUser().getFlag_job()
                    .equalsIgnoreCase(gp_jobspv.getGs_value())) {
                getActivity().findViewById(R.id.btnSendLayout).setVisibility(View.GONE);
                getActivity().findViewById(R.id.btnNextLayout).setVisibility(View.VISIBLE);
            } else {
                getActivity().findViewById(R.id.btnSendLayout).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.btnSend).setEnabled(true);
                getActivity().findViewById(R.id.btnSend).setClickable(true);
            }
        }

        getActivity().findViewById(R.id.btnReject).setClickable(true);
        getActivity().findViewById(R.id.btnVerified).setClickable(true);

        getActivity().findViewById(R.id.btnClose).setClickable(true);
        if (questionSize == 0) {
            loadReviewData();
        } else {
            showReviewScreen(true);
        }
    }

    public void addItem(QuestionBean bean, int position, boolean fromDraft) {
        String newQuestionGroup = bean.getQuestion_group_name();
        if (lastQuestionGroup == null || !newQuestionGroup.equals(lastQuestionGroup)) {
            lastQuestionGroup = newQuestionGroup;
            listOfQuestionGroup.add(lastQuestionGroup);
            List<QuestionBean> beanList = listOfQuestionBean.get(lastQuestionGroup);
            if (beanList != null) {
                beanList.add(bean);
            } else {
                beanList = new ArrayList<>();
                beanList.add(bean);
            }
            listOfQuestionBean.put(lastQuestionGroup, beanList);
        } else {
            List<QuestionBean> beanList = listOfQuestionBean.get(lastQuestionGroup);
            if (beanList != null) {
                beanList.add(bean);
            } else {
                beanList = new ArrayList<>();
                beanList.add(bean);
            }
            listOfQuestionBean.put(lastQuestionGroup, beanList);
        }
        position += listOfQuestionGroup.size();
        listOfQuestions.add(bean);
        if (!fromDraft) {
            notifyInsert(position);
            try {
                int groupIdx = listOfQuestionGroup.indexOf(lastQuestionGroup);
                questionAdapter.expand(groupIdx);
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }

    public void changeItem(int position) {
        questionAdapter.notifyItemChanged(position);
    }

    public void removeItem(int position) {
        QuestionBean bean = listOfQuestions.get(position);
        String newQuestionGroup = bean.getQuestion_group_name();
        List<QuestionBean> beanList = listOfQuestionBean.get(newQuestionGroup);
        if (beanList != null) {
            if (bean.getAnswer_type().equals(Global.AT_TEXT_WITH_SUGGESTION)) {
                bean.setSelectedOptionAnswers(null);
                bean.setAnswer(null);
            }
            if (!beanList.isEmpty()) {
                beanList.remove(bean);
                listOfQuestionBean.put(newQuestionGroup, beanList);
            }
            if (beanList.isEmpty()) {
                listOfQuestionGroup.remove(newQuestionGroup);
                listOfQuestionBean.remove(newQuestionGroup);
                lastQuestionGroup = listOfQuestionGroup.get(listOfQuestionGroup.size() - 1);
            }
        } else {
            listOfQuestionGroup.remove(newQuestionGroup);
            listOfQuestionBean.remove(newQuestionGroup);
            lastQuestionGroup = listOfQuestionGroup.get(listOfQuestionGroup.size() - 1);
        }

        listOfQuestions.remove(position);
        removeQuestionLabel(position);
        int lastPosition = questionAdapter.getItemCount() - 1;
        questionAdapter.notifyItemRemoved(lastPosition);
        questionAdapter.notifyDataSetChanged();
    }

    public void removeQuestionLabel(int position) {
        if (DynamicQuestionActivity.getQuestionLabel().size() > position) {
            DynamicQuestionActivity.getQuestionLabel().remove(DynamicQuestionActivity.getQuestionLabel().size() - 1);
        }
        form = new DynamicQuestionView(getActivity());
        form.adapter.notifyDataSetChanged();
    }

    public void addQuestionLabel(String label) {
        DynamicQuestionActivity.questionLabel.add(label);
        form = new DynamicQuestionView(getActivity());
        form.adapter.notifyDataSetChanged();
    }

    public boolean validateCurrentPage(boolean isCekValidate, boolean isSave) {
        List<String> errMessage = new ArrayList<>();
        boolean isSkipLastQuestValid = false;
        if (isCekValidate) {
            for (int i = 0; i < listOfQuestions.size(); i++) {
                try {
                    QuestionBean qBean = listOfQuestions.get(i);
                    String relevantMandatory = qBean.getRelevant_mandatory();

                    if (relevantMandatory == null) {
                        relevantMandatory = "";
                    }
                    if (!relevantMandatory.equalsIgnoreCase("")) {
                        if (isQuestionVisibleIfRelevant(relevantMandatory, qBean, true)) {
                            if (!qBean.isMandatory() && (null == qBean.getAnswer() || qBean.getAnswer().equalsIgnoreCase(""))) {
                                isSkipLastQuestValid = true;
                                boolean isLastQuestion = true;
                                for (int x = (listOfQuestions.size() - 1); x >= i; x--) {
                                    if (!isSave) {
                                        removeItem(x);
                                    } else {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        final int finalX = x;
                                        handler.post(new Runnable() {
                                            public void run() {
                                                removeItem(finalX);
                                            }
                                        });
                                    }
                                    isLastQuestion = false;
                                }
                                if (isSave && !isLastQuestion) {
                                    String err2 = getString(R.string.save_on_relevant);
                                    errMessage.add(err2);
                                }
                            }
                        } else if (qBean.isChange()) {
                            boolean isLastQuestion = true;
                            for (int x = (listOfQuestions.size() - 1); x > i; x--) {
                                if (!isSave) {
                                    removeItem(x);
                                } else {
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    final int finalX = x;
                                    handler.post(new Runnable() {
                                        public void run() {
                                            removeItem(finalX);
                                        }
                                    });
                                }
                                isLastQuestion = false;
                            }
                            if (isSave && !isLastQuestion) {
                                String err2 = getString(R.string.save_on_relevant);
                                errMessage.add(err2);
                            }
                            qBean.setChange(false);
                        } else {
                            String answer = null == qBean.getAnswer() ? "" : qBean.getAnswer();
                            if (!relevantMandatory.equalsIgnoreCase("") && qBean.isRelevantMandatory() && answer.equalsIgnoreCase("")) {
                                boolean isLastQuestion = true;
                                for (int x = (listOfQuestions.size() - 1); x >= i; x--) {
                                    if (!isSave) {
                                        removeItem(x);
                                    } else {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        final int finalX = x;
                                        handler.post(new Runnable() {
                                            public void run() {
                                                removeItem(finalX);
                                            }
                                        });
                                    }
                                    isLastQuestion = false;
                                }
                                if (isSave && !isLastQuestion) {
                                    String err2 = getString(R.string.save_on_relevant);
                                    errMessage.add(err2);
                                }
                                qBean.setRelevantMandatory(false);
                            }
                        }
                    }
                    qBean.setRelevantMandatory(isQuestionVisibleIfRelevant(relevantMandatory, qBean, true));

                    if (qBean.isChange()) {
                        if (qBean.isRelevanted() || qBean.isRelevantMandatory()) {
                            qBean.setChange(false);
                            if (qBean.getAffectedQuestionBeanOptions() != null && !qBean.getAffectedQuestionBeanOptions().isEmpty()) {
                                for (int idx = 0; idx < qBean.getAffectedQuestionBeanOptions().size(); idx++) {
                                    QuestionBean qBeanAffected = qBean.getAffectedQuestionBeanOptions().get(idx);
                                    if (Global.AT_LOOKUP_DUKCAPIL.equals(qBeanAffected.getAnswer_type())) {
                                        qBeanAffected.setAnswer(null);
                                        qBeanAffected.setDataDukcapil(null);
                                    } else if (Global.AT_LOOKUP.equals(qBeanAffected.getAnswer_type())) {
                                        qBeanAffected.setAnswer(null);
                                        qBeanAffected.setIntTextAnswer(null);
                                    }
                                }
                            }
                            if (qBean.getAffectedQuestionBeanCopyValue() != null && !qBean.getAffectedQuestionBeanCopyValue().isEmpty()) {
                                for (int idx = 0; idx < qBean.getAffectedQuestionBeanCopyValue().size(); idx++) {
                                    QuestionBean qBeanAffected = qBean.getAffectedQuestionBeanCopyValue().get(idx);
                                    copyQuestionValueAffectedBean(qBeanAffected, qBeanAffected.getQuestion_value());
                                }
                            }
                            boolean isLastQuestion = true;
                            for (int x = (listOfQuestions.size() - 1); x > i; x--) {
                                if (!isSave) {
                                    if ((listOfQuestions.get(x).getChoice_filter() != null && !listOfQuestions.get(x).getChoice_filter().equals(""))) {
                                        String choice_filter = listOfQuestions.get(x).getChoice_filter();
                                        choice_filter = choice_filter.replace("{", "").trim();
                                        choice_filter = choice_filter.replace("}", "").trim();

                                        if (QuestionViewAdapter.IsLookupQuestion(listOfQuestions.get(x).getAnswer_type()) || QuestionViewAdapter
                                                .IsTextWithSuggestionQuestion(Integer.parseInt(listOfQuestions.get(x).getAnswer_type()))) {
                                            if (Constant.getListOfQuestion().get(choice_filter).isChange()) {
                                                QuestionBean.resetAnswer(listOfQuestions.get(x));
                                            }
                                            removeQuestionLabel(x);
                                        }
                                    }
                                    removeItem(x);
                                } else {
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    final int finalX = x;
                                    handler.post(new Runnable() {
                                        public void run() {
                                            removeItem(finalX);
                                        }
                                    });
                                }
                                isLastQuestion = false;
                            }
                            if (isSave && !isLastQuestion) {
                                String err2 = getString(R.string.save_on_relevant);
                                errMessage.add(err2);
                            }
                            qBean.setChange(false);
                        } else {
                            qBean.setChange(false);
                        }
                    }

                    if (isSkipLastQuestValid) {
                        isSkipLastQuestValid = false;
                    } else {
                        List<String> err = questionsValidator.validateGeneratedQuestionView(qBean);
                        if (err != null && !err.isEmpty()) {
                            errMessage.addAll(err);
                        }
                    }

                } catch (Exception e) {
                    FireCrash.log(e);
                    if (Global.IS_DEV) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            for (int i = 0; i < listOfQuestions.size(); i++) {
                try {
                    QuestionBean qBean = listOfQuestions.get(i);
                    boolean answer = QuestionBean.isHaveAnswer(qBean);
                    if (!answer && (qBean.isMandatory() || qBean.isRelevantMandatory())) {        //tidak ada isi
                        String err = qBean.getQuestion_label() + " " + getString(R.string.msgRequired);
                        errMessage.add(err);
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    if (Global.IS_DEV) {
                        e.printStackTrace();
                    }
                }
            }
        }


        if (!errMessage.isEmpty() && isCekValidate) {
            String[] msg = errMessage.toArray(new String[errMessage.size()]);
            final String alert = Tool.implode(msg, "\n");
            if (!DynamicFormActivity.getIsApproval()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), alert, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return false;
        }
        ScrollingLinearLayoutManager layoutManager = (ScrollingLinearLayoutManager) qRecyclerView.getLayoutManager();
        layoutManager.setScrollEnable(true);
        return true;
    }

    private boolean validateAllMandatory(boolean displayMessage) {
        boolean result = true;
        for (QuestionBean bean : listOfQuestions) {
            if (bean.getIs_mandatory().equals(Global.TRUE_STRING) && bean.getIs_visible().equals(Global.TRUE_STRING)) {
                boolean isHaveAnswer = QuestionBean.isHaveAnswer(bean);
                if (!isHaveAnswer) {        //tidak ada isi
                    if (displayMessage) {
                        Toast.makeText(getActivity(), bean.getQuestion_label() + " " + getString(R.string.msgRequired), Toast.LENGTH_SHORT).show();
                    }
                    result = false;
                }
            }
        }
        return result;
    }

    private boolean doNext(boolean validate) {
        boolean result = false;
        Utility.freeMemory();
        if (!DynamicFormActivity.getIsApproval()) {
            if (validateCurrentPage(validate, false)) {
                while (loadQuestionForm()) {
                    isFinish = false;
                    if (Constant.getListOfQuestion().size() == questionSize) {
                        DynamicFormActivity.setIsApproval(false);
                        isFinish = true;
                        if (Global.IS_DEV && (mainMenu != null && mode != Global.MODE_VIEW_SENT_SURVEY && !isSimulasi)) {
                            mainMenu.findItem(R.id.mnPendingTask).setVisible(true);
                        }

                        if (Global.APPLICATION_ORDER
                                .equals(GlobalData.getSharedGlobalData().getApplication()) && (mode == Global.MODE_NEW_SURVEY || mode == Global.MODE_SURVEY_TASK)) {
                            RejectOrderTask task = new RejectOrderTask();
                            task.execute();
                            return false;
                        }
                        if (validate) {
                            showFinishScreen();
                        } else {
                            if (this.validateCurrentPage(true, false)) {
                                showFinishScreen();
                            }
                        }
                        break;
                    } else {
                        loadQuestionForm();
                        break;
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

    private void doPending() {
        new AsyncTask<Void, Void, Boolean>() {
            boolean isOK = true;
            boolean isSuccess = false;
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                this.progressDialog = ProgressDialog.show(getActivity(), "", getActivity().getString(R.string.progressWait), true);
            }

            @Override
            public Boolean doInBackground(Void... params) {

                if (!isFinish) {
                    isOK = validateCurrentPage(true, false);
                }
                if (isOK == true) {
                    DynamicFormActivity.getHeader().setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                    isSuccess = new TaskManager().saveTask(getActivity(), mode, DynamicFormActivity.getHeader(), listOfQuestions, "1", true, false, false);
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
                        if (Global.IS_DEV) {
                            e.printStackTrace();
                        }
                    }
                }

                if (result && isSuccess) {
                    DynamicFormActivity.setHeader(null);
                    getActivity().finish();
                }
            }
        }.execute();
    }

    private void doSave(final int mode) {
        new AsyncTask<Void, Void, Boolean>() {
            boolean isOK = true;
            boolean isSuccess = false;
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                this.progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.progressWait), true);
            }

            @Override
            public Boolean doInBackground(Void... params) {
                if (!isFinish) {
                    if (GlobalData.isRequireRelogin()) {
                        isOK = validateCurrentPage(false, true);
                    } else {
                        isOK = validateCurrentPage(true, true);
                    }
                }
                if (isOK) {
                    try {
                        DynamicFormActivity.getHeader().setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                        if (DynamicFormActivity.getHeader().getForm().getForm_type().equals(Global.FORM_TYPE_KTP)) {
                            TaskH taskH = DynamicFormActivity.getHeader().getTaskH();
                            for (Map.Entry<String, List<QuestionBean>> entry : listOfQuestionBean.entrySet()) {
                                for (QuestionBean bean : listOfQuestionBean.get(entry.getKey())) {
                                    if (bean.getTag() != null) {
                                        if (bean.getTag().equalsIgnoreCase("alamat") && (bean.getAnswer() != null || bean.getAnswer().equals(""))) {
                                            taskH.setCustomer_address(bean.getAnswer());
                                        } else if (bean.getTag().equalsIgnoreCase("customer name") && (bean.getAnswer() != null || bean.getAnswer().equals(""))) {
                                            taskH.setCustomer_name(bean.getAnswer());
                                        } else if (bean.getTag().equalsIgnoreCase("customer phone") && (bean.getAnswer() != null || bean.getAnswer().equals(""))) {
                                            taskH.setCustomer_phone(bean.getAnswer());
                                        }
                                    }
                                }
                            }
                        }
                        String uuid_last_question = listOfQuestions.get(listOfQuestions.size() - 1).getUuid_question_set();
                        isSuccess = TaskManager.saveTask(getActivity(), mode, DynamicFormActivity.getHeader(), listOfQuestions, uuid_last_question, false, true, false);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        isOK = false;
                        ACRA.getErrorReporter().putCustomData("errorSaveTask", "Pernah error saat save Task");
                        ACRA.getErrorReporter()
                                .putCustomData("errorSaveTaskTime", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
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
                        if (Global.IS_DEV) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!GlobalData.isRequireRelogin()) {
                    if (result && isSuccess) {
                        try {
                            if (!isSimulasi) {
                                Global.setREDIRECT(Global.REDIRECT_TIMELINE); // Kembalikan ke Timeline
                                DynamicFormActivity.setHeader(null);
                                getActivity().finish();
                            }
                        } catch (Exception e) {
                            Global.setREDIRECT(Global.REDIRECT_TIMELINE); // Kembalikan ke Timeline
                            FireCrash.log(e);
                            getActivity().finish();
                        }
                    } else {
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getString(R.string.data_saved_failed), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            Global.setREDIRECT(Global.REDIRECT_TIMELINE); // Kembalikan ke Timeline
                            FireCrash.log(e);
                            getActivity().finish();
                        }
                    }
                }
            }
        }.execute();
    }

    private void doSaveNoValidate(final int mode) {
        questionFormTrace.start();
        new AsyncTask<Void, Void, Boolean>() {
            boolean isOK = true;
            boolean isSuccess = false;

            @Override
            protected void onPreExecute() {
                isAutoSaveRunning = true;
            }

            @Override
            public Boolean doInBackground(Void... params) {
                try {
                    if (DynamicFormActivity.getHeader().getForm().getForm_type().equals(Global.FORM_TYPE_KTP)) {
                        TaskH taskH = DynamicFormActivity.getHeader().getTaskH();
                        for (Map.Entry<String, List<QuestionBean>> entry : listOfQuestionBean.entrySet()) {
                            for (QuestionBean bean : listOfQuestionBean.get(entry.getKey())) {
                                if (bean.getTag() != null) {
                                    if ("alamat".equalsIgnoreCase(bean.getTag()) && (null != bean.getAnswer() || "".equals(bean.getAnswer()))) {
                                        taskH.setCustomer_address(bean.getAnswer());
                                    } else if ("customer name".equalsIgnoreCase(bean.getTag()) && (null != bean.getAnswer() || "".equals(bean.getAnswer()))) {
                                        taskH.setCustomer_name(bean.getAnswer());
                                    } else if ("customer phone".equalsIgnoreCase(bean.getTag()) && (null != bean.getAnswer() || "".equals(bean.getAnswer()))) {
                                        taskH.setCustomer_phone(bean.getAnswer());
                                    }
                                }
                            }
                        }
                    }
                    String uuidLastQuestion;
                    if (GlobalData.isRequireRelogin()) {
                        uuidLastQuestion = listOfQuestions.get(listOfQuestions.size() - 2).getUuid_question_set();
                    } else {
                        uuidLastQuestion = listOfQuestions.get(listOfQuestions.size() - 1).getUuid_question_set();
                    }
                    isSuccess = TaskManager.saveTask(getActivity(), mode, DynamicFormActivity.getHeader(), listOfQuestions, uuidLastQuestion, false, true, true);
                } catch (Exception e) {
                    FireCrash.log(e);
                    isOK = false;
                    ACRA.getErrorReporter().putCustomData("errorSaveTask", "Error occurred when auto save");
                    ACRA.getErrorReporter().putCustomData("errorSaveTaskTime", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(e);
                }
                return isOK;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (!result || !isSuccess) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), getString(R.string.data_saved_failed), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                } else {
                    questionFormTrace.stop();
                }
                isAutoSaveRunning = false;
            }
        }.execute();
    }

    private void doReject() {
        boolean isApprovalTask = true;
        getActivity().findViewById(R.id.btnReject).setEnabled(false);
        if (TaskHDataAccess.STATUS_TASK_VERIFICATION.equals(DynamicFormActivity.getHeader().getStatus()) || TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD
                .equals(DynamicFormActivity.getHeader().getStatus())) {
            DynamicFormActivity.getHeader().setIs_prepocessed(Global.FORM_TYPE_VERIFICATION);
            isApprovalTask = false;
        }
        if (TaskHDataAccess.STATUS_TASK_APPROVAL.equals(DynamicFormActivity.getHeader().getStatus()) || TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD
                .equals(DynamicFormActivity.getHeader().getStatus())) {
            DynamicFormActivity.getHeader().setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
            isApprovalTask = true;
        }
        if (DynamicFormActivity.getHeader().getSubmit_date() == null) {
            DynamicFormActivity.getHeader().setSubmit_date(Tool.getSystemDateTime());
        }
        new TaskManager().sendApprovalTask(getActivity(), DynamicFormActivity.getHeader(), Global.FLAG_FOR_REJECTEDTASK, isApprovalTask);
    }

    private void doApprove() {
        boolean isApprovalTask = true;
        getActivity().findViewById(R.id.btnApprove).setEnabled(false);
        if (DynamicFormActivity.getHeader().getSubmit_date() == null) {
            DynamicFormActivity.getHeader().setSubmit_date(Tool.getSystemDateTime());
        }
        new TaskManager().sendApprovalTask(getActivity(), DynamicFormActivity.getHeader(), Global.FLAG_FOR_APPROVALTASK, isApprovalTask);
    }

    private void doVerify() {
        getActivity().findViewById(R.id.btnVerified).setEnabled(false);
        if (validateAllMandatory(true)) {
            sendVerification();
        }
    }

    private void doSend() {
        //Pencegahan failed draft tanpa message dikarenakan menekan next dan send secara beruntun
        if (!isAutoSaveRunning) {
            if (!haveSent) {
                haveSent = true;
                getActivity().findViewById(R.id.btnSend).setEnabled(false);
                if (!validateAllMandatory(true)) {
                    return;
                }

                // is Payment using Payment Channel
                if (isPaymentChannel(SCRIPT_CHECK_PAYMENT_METHOD) && Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                    String tag = "";
                    String amount = "0";
                    for (int i = 0; i < listOfQuestions.size(); i++) {
                        QuestionBean mbean = listOfQuestions.get(i);
                        tag = mbean.getTag();
                        if (tag != null && "TOTAL BAYAR".equalsIgnoreCase(tag)) {
                            amount = mbean.getAnswer();
                            break;
                        }
                    }

                    PaxPayment paxPayment = PaxPayment.getInstance(getActivity());
                    Toast.makeText(getActivity(), "Amount: " + amount + ", Tip Amount: 0", Toast.LENGTH_SHORT).show();
                    paxPayment.sale(amount.equals("0") ? "5" : amount, "0");
                } else {
                    saveAndSendSurvey();
                }
            } else {
                Toast.makeText(getActivity(), "Task is sending, please wait", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.auto_save_still_running, Toast.LENGTH_SHORT).show();
        }
    }

    private void doSearch(String key, boolean needValidation) {
        if (this.validateCurrentPage(needValidation, false)) {
            int group = 0;
            int position = 0;

            for (int i = 0; i <= questionAdapter.getGroupItemCount(); i++) {
                for (int j = 0; j < questionAdapter.getChildItemCount(i); j++) {
                    QuestionBean bean = questionAdapter.getChildItem(i, j);
                    if (bean.isVisible()) {
                        String[] txtSearch = Tool.split(key, Global.DELIMETER_ROW);
                        String txtQLabel = txtSearch[0].trim();
                        String label = bean.getQuestion_label().toLowerCase();
                        String groupName = bean.getQuestion_group_name();
                        if (label.indexOf(txtQLabel) != -1) {
                            if (txtSearch.length == 2) {
                                String txtgroupName = txtSearch[1].trim();
                                if (groupName.equalsIgnoreCase(txtgroupName)) {
                                    group = i;
                                    position = j;
                                }
                            } else {
                                group = i;
                                position = j;
                            }
                        }
                    }
                }
            }
            if (isFinish) {
                showReviewScreen(false);
                isFinish = false;
                ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
                anim.setDuration(500);
                anim.setFillAfter(true);
                getActivity().findViewById(R.id.btnNextLayout).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.btnNext).setClickable(true);
                getActivity().findViewById(R.id.btnSendLayout).setVisibility(View.GONE);
                getActivity().findViewById(R.id.btnSend).setClickable(false);
            }
            ((AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteSearch)).setText("");
            try {
                questionAdapter.expand(group);
                position = position + getCounterListBeforeGroup(group, false);
                setFocusable(position, 500);
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }

    private void setImageForDrawingQuestion(byte[] value) {
        QuestionBean mBean = focusBean;
        mBean.setImgAnswer(value);
        mBean.setImgTimestamp(new Date(System.currentTimeMillis()));
        int position = focusPosition + getCounterListBeforeGroup(focusGroup, false);
        changeItem(position);
        Utility.freeMemory();
    }

    private void calculateQuestionBean(QuestionBean bean) {
        if (null != bean.getCalculate() && !"".equalsIgnoreCase(bean.getCalculate())) {
            String resultCalculate = doCalculate(bean);
            if (bean.getAnswer_type().equals(Global.AT_DECIMAL)) {
                try {
                    NumberFormat nf = NumberFormat.getInstance(Locale.US);
                    Double finalAnswer = nf.parse(resultCalculate).doubleValue();
                    resultCalculate = finalAnswer.toString();
                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                }
            }
            bean.setAnswer(resultCalculate);
        }
    }

    private boolean copyQuestionValue(QuestionBean mBean, String valueScript) {
        String convertedExpression = valueScript.trim();
        if (convertedExpression == null || convertedExpression.length() == 0) {
            return true;
        } else {
            boolean isCopyFromLookup = false;
            boolean isCopyWithCondition = false;
            boolean isCopyFromDkcp = false;
            try {
                if (convertedExpression.contains("copyFromLookup(")) {
                    isCopyFromLookup = convertedExpression.substring(0, 15).equals("copyFromLookup(");
                } else if (convertedExpression.contains("copyFromDkcp(")) {
                    isCopyFromDkcp = true;
                }
            } catch (Exception e) {
                FireCrash.log(e);
                isCopyFromLookup = false;
                if (Global.IS_DEV) {
                    e.printStackTrace();
                }
            }
            try {
                isCopyWithCondition = convertedExpression.substring(0, 5).equals("copy(");
            } catch (Exception e) {
                FireCrash.log(e);
                isCopyWithCondition = false;
                if (Global.IS_DEV) {
                    e.printStackTrace();
                }
            }

            if (isCopyFromLookup) {
                return copyValueFromLookup(mBean, convertedExpression);
            } else if (isCopyWithCondition) {
                return copyValueWithCondition(mBean, convertedExpression);
            } else if (isCopyFromDkcp) {
                if (firstLoadKonvergen) {
                    return true;
                } else {
                    return copyValueFromDkcp(mBean, convertedExpression);
                }
            } else {
                return copyValue(mBean, convertedExpression);
            }
        }
    }


    public boolean copyQuestionValueAffectedBean(QuestionBean mBean, String valueScript) {
        String convertedExpression = valueScript.trim();
        if (convertedExpression == null || convertedExpression.length() == 0) {
            return true;
        } else {
            boolean isCopyFromLookup = false;
            boolean isCopyFromDkcp = false;
            boolean isCopyWithCondition = false;
            try {
                if (convertedExpression.contains("copyFromLookup(")) {
                    isCopyFromLookup = convertedExpression.substring(0, 15).equals("copyFromLookup(");
                } else if (convertedExpression.contains("copyFromDkcp(")) {
                    isCopyFromDkcp = convertedExpression.substring(0, 13).equals("copyFromDkcp(");
                }
            } catch (Exception e) {
                FireCrash.log(e);
                isCopyFromLookup = false;
                isCopyFromDkcp = false;
                if (Global.IS_DEV) {
                    e.printStackTrace();
                }
            }
            try {
                isCopyWithCondition = convertedExpression.substring(0, 5).equals("copy(");
            } catch (Exception e1) {
                isCopyWithCondition = false;
                if (Global.IS_DEV) {
                    e1.printStackTrace();
                }
            }

            if (isCopyFromLookup) {
                return copyValueFromLookup(mBean, convertedExpression);
            } else if (isCopyFromDkcp) {
                return copyValueFromDkcp(mBean, convertedExpression);
            } else if (isCopyWithCondition) {
                return copyValueWithCondition(mBean, convertedExpression);
            } else {
                return copyValue(mBean, convertedExpression);
            }
        }
    }

    public boolean copyValue(QuestionBean mBean, String valueScript) {
        String script = valueScript;
        String answerType = mBean.getAnswer_type();
        if (script == null || script.length() == 0) {
            return true;
        } else {
            int idxOfOpenBrace = script.indexOf('{');
            if (idxOfOpenBrace != -1) {
                int idxOfCloseBrace = script.indexOf('}');
                String identifier = script.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                int idxOfOpenAbs = identifier.indexOf("$");
                if (idxOfOpenAbs != -1) {
                    String flatAnswer = getAnswer(identifier, idxOfOpenAbs);
                    mBean.setAnswer(flatAnswer);
                    return true;
                } else {
                    QuestionBean bean = Constant.getListOfQuestion().get(identifier);
                    boolean isSourceChange = true;
                    if (!defaultCopyValueBean.containsKey(identifier)) {
                        defaultCopyValueBean.put(identifier, QuestionBean.getAnswer(bean));
                        if (mBean.getAnswer() != null) {
                            isSourceChange = false;
                        }
                    } else {
                        if (defaultCopyValueBean.get(identifier).equals(QuestionBean.getAnswer(bean))) {
                            isSourceChange = false;
                        }
                    }
                    if (bean != null) {
                        if (QuestionBean.isHaveAnswer(bean) && isSourceChange) {
                            try {
                                setAnswer(bean, mBean, answerType);
                                return true;
                            } catch (Exception e) {
                                FireCrash.log(e);
                                if (Global.IS_DEV) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        } else {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
    }

    public boolean copyValueWithCondition(QuestionBean mBean, String valueScript) {
        String script = valueScript;
        String answerType = mBean.getAnswer_type();
        boolean needReplacing = true;
        while (needReplacing) {
            int idxOfOpenBrace = script.indexOf('{');
            if (idxOfOpenBrace != -1) {
                int idxOfCloseBrace = script.indexOf('}');
                String identifier = script.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                int idxOfOpenAbs = identifier.indexOf("$");
                if (idxOfOpenAbs != -1) {
                    String flatAnswer = getAnswer(identifier, idxOfOpenAbs);
                    if (flatAnswer != null && flatAnswer.length() > 0) {
                        script = script.replace("{" + identifier + "}", flatAnswer);
                    } else {
                        script = script.replace("{" + identifier + "}", "\"\"");
                    }
                } else {
                    QuestionBean bean = Constant.getListOfQuestion().get(identifier);
                    boolean isSourceChange = true;
                    if (!defaultCopyValueBean.containsKey(identifier)) {
                        defaultCopyValueBean.put(identifier, QuestionBean.getAnswer(bean));
                        if (mBean.getAnswer() != null) {
                            isSourceChange = false;
                        }
                    } else {
                        if (defaultCopyValueBean.get(identifier).equals(QuestionBean.getAnswer(bean))) {
                            isSourceChange = false;
                        }
                    }
                    if (bean != null) {
                        String flatAnswer = QuestionBean.getAnswer(bean);
                        if (flatAnswer != null && flatAnswer.length() > 0 && isSourceChange) {
                            String[] answers = Tool.split(flatAnswer, Global.DELIMETER_DATA);
                            if (answers.length == 1) {
                                try {
                                    bean.setRelevanted(true);
                                    flatAnswer = isDateTime(bean, answers);
                                    if (flatAnswer == null) {
                                        flatAnswer = QuestionBean.getAnswer(bean);
                                    }
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                    e.printStackTrace();
                                }
                                script = script.replace("{" + identifier + "}", flatAnswer);
                            } else {
                                for (int i = 0; i < answers.length; i++) {
                                    String convertedSubExpression = script.replace("{" + identifier + "}", answers[i]);
                                    boolean isVisible = copyQuestionValue(mBean, convertedSubExpression);
                                    if (isVisible) {
                                        return true;
                                    }
                                }
                                script = script.replace("{" + identifier + "}", "");
                            }
                        } else {
                            script = script.replace("{" + identifier + "}", "");
                        }
                    } else {
                        if (identifier.contains("#")) {
                            String exactValue = identifier.replace("{#", "").trim();
                            exactValue = exactValue.replace("}", "").trim();
                            script = script.replace("{" + identifier + "}", exactValue);
                        } else {
                            script = script.replace("{" + identifier + "}", "");
                        }
                    }
                }
            } else {
                needReplacing = false;
            }
        }
        return ifHaveCondition(mBean, script, valueScript, answerType);
    }

    public boolean copyValueFromLookup(QuestionBean mBean, String convertedExpression) {
        String lastScript = convertedExpression;
        String[] script = Tool.split(lastScript, Global.DELIMETER_DATA3);
        String mainIdf = script[0].trim();
        mainIdf = mainIdf.replace("copyFromLookup(", "").trim();
        mainIdf = mainIdf.replace("{", "").trim();
        mainIdf = mainIdf.replace("}", "").trim();
        String valueIdf = script[1].trim();
        valueIdf = valueIdf.replace(")", "").trim();
        valueIdf = valueIdf.replace("{", "").trim();
        valueIdf = valueIdf.replace("}", "").trim();
        QuestionBean bean = Constant.getListOfQuestion().get(mainIdf);
        boolean isSourceChange = true;
        if (!defaultCopyValueBean.containsKey(mainIdf)) {
            defaultCopyValueBean.put(mainIdf, QuestionBean.getAnswer(bean));
            if (mBean.getAnswer() != null) {
                isSourceChange = false;
            }
        } else {
            if (defaultCopyValueBean.get(mainIdf).equals(QuestionBean.getAnswer(bean))) {
                isSourceChange = false;
            }
        }
        bean.setRelevanted(true);
        String answer = null;
        LookupCriteriaBean criteriaBean = bean.getSelectedCriteriaBean();
        if (criteriaBean != null) {
            List<ParameterAnswer> parameterList = criteriaBean.getParameterAnswers();
            if (parameterList != null) {
                for (ParameterAnswer parameterAnswer : parameterList) {
                    if (parameterAnswer.getRefId().equals(valueIdf)) {
                        answer = parameterAnswer.getAnswer();
                        break;
                    }
                }
            }
        }
        if (answer != null && isSourceChange) {
            if (Tool.isOptions(mBean.getAnswer_type())) {
                Lookup lookup = LookupDataAccess.getOne(getActivity(), answer);
                OptionAnswerBean answerBean = new OptionAnswerBean(lookup);
                List<OptionAnswerBean> selectedOptionAnswer = new ArrayList<>();
                selectedOptionAnswer.add(answerBean);
                mBean.setSelectedOptionAnswers(selectedOptionAnswer);
            } else {
                mBean.setAnswer(answer);
            }
            return true;
        }
        return false;
    }

    //gunakan ini untuk DKCP look up
    public boolean copyValueFromDkcp2(QuestionBean mBean, String convertedExpression) {
        String lastScript = convertedExpression;
        String[] script = Tool.split(lastScript, Global.DELIMETER_DATA3);
        boolean result = false;
        if (script.length < 3) {
            String mainIdf = script[0].trim();
            mainIdf = mainIdf.replace("copyFromDkcp(", "").trim();
            mainIdf = mainIdf.replace("{", "").trim();
            mainIdf = mainIdf.replace("}", "").trim();

            String valueIdf = script[1].trim();
            valueIdf = valueIdf.replace(")", "").trim();
            valueIdf = valueIdf.replace("{", "").trim();
            valueIdf = valueIdf.replace("}", "").trim();

            String answer = getAnswerFromDukcapil(mainIdf, valueIdf);
            if (answer != null && !"".equals(answer)) {
                result = setAnswerFromDukcapil(mBean, answer);
            } else {
                result = false;
            }
        } else {
            result = copyValueFromDkcpWithCOndition(mBean, script);
        }
        return result;
    }

    private boolean copyValueFromDkcp(QuestionBean mBean, String convertedExpression) {
        String lastScript = convertedExpression;
        String[] script = Tool.split(lastScript, Global.DELIMETER_DATA3);
        String mainIdf = script[0].trim();
        mainIdf = mainIdf.replace("copyFromDkcp(", "").trim();
        mainIdf = mainIdf.replace("{", "").trim();
        mainIdf = mainIdf.replace("}", "").trim();
        String valueIdf = script[1].trim();
        valueIdf = valueIdf.replace(")", "").trim();
        valueIdf = valueIdf.replace("{", "").trim();
        valueIdf = valueIdf.replace("}", "").trim();

        QuestionBean bean = Constant.getListOfQuestion().get(mainIdf);
        bean.setRelevanted(true);

        try {
            String answer = bean.getResponseImageDkcp().getDataDkcp().getValueRead(valueIdf);
            if (mBean.getAnswer_type().equals(Global.AT_DROPDOWN)) {
                List<OptionAnswerBean> optionAnswerBeans = mBean.getOptionAnswers();
                List<OptionAnswerBean> selectedAnswer = new ArrayList<>();
                for (OptionAnswerBean option : optionAnswerBeans) {
                    if (option.getValue().equalsIgnoreCase(answer)) {
                        selectedAnswer.add(option);
                        mBean.setSelectedOptionAnswers(selectedAnswer);
                        break;
                    }
                }
                if (bean.getSelectedOptionAnswers() == null) {
                    Toast.makeText(getContext(), getActivity().getString(R.string.copy_value_dkcp_failed), Toast.LENGTH_SHORT).show();
                }
            } else if (mBean.getAnswer_type().equals(Global.AT_DATE)) {
                Date date = null;
                answer = answer.replace("-", "/");
                try {
                    date = Formatter.parseDate(answer, Global.DATE_STR_FORMAT);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String finalAnswer = Formatter.formatDate(date, Global.DATE_STR_FORMAT_GSON);
                mBean.setAnswer(finalAnswer);
            } else {
                mBean.setAnswer(answer);
            }

            if (answer == null || "".equals(answer)) {
                Toast.makeText(getContext(), getActivity().getString(R.string.copy_value_dkcp_failed), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return true;
    }

    public boolean copyValueFromDkcpWithCOndition(QuestionBean mBean, String[] script) {
        String condition = script[0].trim();
        condition = condition.replace("copyFromDkcp(", "").trim();
        boolean needReplacing = true;
        while (needReplacing) {
            int idxOfOpenBrace = condition.indexOf('{');
            if (idxOfOpenBrace != -1) {
                int idxOfCloseBrace = condition.indexOf('}');
                String identifier = condition.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                identifier = identifier.replace("{", "");
                identifier = identifier.replace("}", "");
                int idxOfOpenAbs = identifier.indexOf("$");
                if (idxOfOpenAbs != -1) {
                    if (identifier.contains(".")) {
                        String[] splitIdf = identifier.split("\\.");
                        String flatAnswer = getAnswerFromDukcapil(splitIdf[0], splitIdf[1]);
                        if (flatAnswer == null) {
                            flatAnswer = "";
                        }
                        condition = condition.replace("{" + identifier + "}", flatAnswer);
                    } else {
                        String flatAnswer = getAnswer(identifier, idxOfOpenAbs);
                        if (flatAnswer != null && flatAnswer.length() > 0) {
                            condition = condition.replace("{" + identifier + "}", flatAnswer);
                        } else {
                            condition = condition.replace("{" + identifier + "}", "\"\"");
                        }
                    }
                } else {
                    if (identifier.contains(".")) {
                        String[] splitIdf = identifier.split("\\.");
                        String flatAnswer = getAnswerFromDukcapil(splitIdf[0], splitIdf[1]);
                        if (flatAnswer == null) {
                            flatAnswer = "";
                        }
                        condition = condition.replace("{" + identifier + "}", flatAnswer);
                    } else {
                        String flatAnswer = "";
                        QuestionBean beanAnswer = Constant.getListOfQuestion().get(identifier);
                        if (beanAnswer != null) {
                            if (Tool.isOptions(beanAnswer.getAnswer_type())) {
                                flatAnswer = beanAnswer.getLovCode();
                            } else {
                                flatAnswer = beanAnswer.getAnswer();
                            }
                        }
                        condition = condition.replace("{" + identifier + "}", flatAnswer);
                    }
                }
            } else {
                needReplacing = false;
            }
        }

        try {
            OperatorSet opSet = OperatorSet.getStandardOperatorSet();
            Expression exp = new Expression(condition);
            exp.setOperatorSet(opSet);
            Object result = exp.evaluate().getObject();
            String value = "";
            if ((boolean) result) {
                value = script[1].trim();
            } else {
                value = script[2].trim();
                value = value.replace(")", "");
            }
            if (!"".equals(value)) {
                String answer = "";
                int idxOfOpenBrace = condition.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    int idxOfCloseBrace = condition.indexOf('}');
                    String identifier = condition.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                    identifier = identifier.replace("{", "");
                    identifier = identifier.replace("}", "");
                    int idxOfOpenAbs = identifier.indexOf("$");
                    if (idxOfOpenAbs != -1) {
                        if (identifier.contains(".")) {
                            String[] splitIdf = identifier.split("\\.");
                            answer = getAnswerFromDukcapil(splitIdf[0], splitIdf[1]);
                        } else {
                            answer = getAnswer(identifier, idxOfOpenAbs);
                        }
                    } else {
                        if (identifier.contains(".")) {
                            String[] splitIdf = identifier.split("\\.");
                            answer = getAnswerFromDukcapil(splitIdf[0], splitIdf[1]);
                        } else {
                            QuestionBean beanAnswer = Constant.getListOfQuestion().get(identifier);
                            if (beanAnswer != null) {
                                if (Tool.isOptions(beanAnswer.getAnswer_type())) {
                                    answer = beanAnswer.getLovCode();
                                } else {
                                    answer = beanAnswer.getAnswer();
                                }
                            }
                        }
                    }
                } else {
                    answer = value;
                }
                if (answer != null && !"".equals(answer)) {
                    setAnswerFromDukcapil(mBean, answer);
                    return true;
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return false;
    }

    public boolean setAnswerFromDukcapil(QuestionBean mBean, String answer) {
        mBean.setIsCanChange(true);
        mBean.setChange(true);
        if (Tool.isOptions(mBean.getAnswer_type()) || Global.AT_TEXT_WITH_SUGGESTION.equals(mBean.getAnswer_type())) {
            String[] arrSelectedAnswer = null;
            try {
                arrSelectedAnswer = Tool.split(answer, Global.DELIMETER_DATA);
            } catch (Exception e) {
                FireCrash.log(e);
                arrSelectedAnswer = new String[0];
            }
            OptionAnswerBean selectedOption = null;

            List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<>();

            for (int j = 0; j < arrSelectedAnswer.length; j++) {
                String selectedAnswer = arrSelectedAnswer[j];
                String[] lookupAnswer = selectedAnswer.split("@");
                if (lookupAnswer != null && lookupAnswer.length > 0) {
                    Lookup lookup = null;
                    if (lookupAnswer.length == 1) {
                        lookup = LookupDataAccess.getOneByCodeAndlovGroup(getActivity(), mBean.getLov_group(), lookupAnswer[0]);
                    } else if (lookupAnswer.length == 2) {
                        List<Lookup> lookupList = LookupDataAccess.getAllByFilter(getActivity(), mBean.getLov_group(), lookupAnswer[1]);
                        if (lookupList != null && !lookupList.isEmpty()) {
                            lookup = lookupList.get(0);
                        }
                    } else if (lookupAnswer.length == 3) {
                        List<Lookup> lookupList = LookupDataAccess.getAllByFilter(getActivity(), mBean.getLov_group(), lookupAnswer[1], lookupAnswer[2]);
                        if (lookupList != null && !lookupList.isEmpty()) {
                            lookup = lookupList.get(0);
                        }
                    } else if (lookupAnswer.length == 4) {
                        List<Lookup> lookupList = LookupDataAccess.getAllByFilter(getActivity(), mBean.getLov_group(), lookupAnswer[1], lookupAnswer[2], lookupAnswer[3]);
                        if (lookupList != null && !lookupList.isEmpty()) {
                            lookup = lookupList.get(0);
                        }
                    } else if (lookupAnswer.length == 5) {
                        List<Lookup> lookupList = LookupDataAccess
                                .getAllByFilter(getActivity(), mBean.getLov_group(), lookupAnswer[1], lookupAnswer[2], lookupAnswer[3], lookupAnswer[4]);
                        if (lookupList != null && !lookupList.isEmpty()) {
                            lookup = lookupList.get(0);
                        }
                    } else if (lookupAnswer.length == 6) {
                        List<Lookup> lookupList = LookupDataAccess
                                .getAllByFilter(getActivity(), mBean.getLov_group(), lookupAnswer[1], lookupAnswer[2], lookupAnswer[3], lookupAnswer[4], lookupAnswer[5]);
                        if (lookupList != null && !lookupList.isEmpty()) {
                            lookup = lookupList.get(0);
                        }
                    }
                    if (lookup != null && lookup.getCode() != null) {
                        selectedOption = new OptionAnswerBean(lookup);
                        selectedOption.setSelected(true);
                        selectedOptionAnswers.add(selectedOption);
                    }
                }
            }
            mBean.setSelectedOptionAnswers(selectedOptionAnswers);
            if (Global.AT_TEXT_WITH_SUGGESTION.equals(mBean.getAnswer_type()) && !selectedOptionAnswers.isEmpty()) {
                mBean.setAnswer(selectedOptionAnswers.get(0).getValue());
            }
        } else {
            mBean.setAnswer(answer);
        }
        return true;
    }

    public String getAnswerFromDukcapil(String mainIdf, String valueIdf) {
        String dataDukcapil = null;

        mainIdf = mainIdf.replace("copyFromDkcp(", "").trim();
        mainIdf = mainIdf.replace("{", "").trim();
        mainIdf = mainIdf.replace("}", "").trim();

        valueIdf = valueIdf.replace(")", "").trim();
        valueIdf = valueIdf.replace("{", "").trim();
        valueIdf = valueIdf.replace("}", "").trim();

        int idxOfOpenAbs = mainIdf.indexOf("$");
        if (idxOfOpenAbs != -1) {
            String finalIdentifier = mainIdf.substring(idxOfOpenAbs + 1);
            if (finalIdentifier.equals(Global.IDF_HEADER_ID)) {
                dataDukcapil = DynamicFormActivity.getHeader().getData_dukcapil();
            }
        } else {
            QuestionBean bean = Constant.getListOfQuestion().get(mainIdf);
            bean.setRelevanted(true);
            dataDukcapil = bean.getDataDukcapil();
        }

        if (dataDukcapil != null && !"".equals(dataDukcapil)) {
            JsonResponseKtpValidation data = GsonHelper.fromJson(dataDukcapil, JsonResponseKtpValidation.class);
            Map dataValidation = data.getMapValue();

            String answer = null;
            if ("result".equals(valueIdf)) {
                answer = data.getResult() != null ? data.getResult() : null;
            } else if ("message".equals(valueIdf)) {
                answer = data.getMessage() != null ? data.getResult() : null;
            } else {
                if (dataValidation == null) {
                    return null;
                }
                answer = dataValidation.get(valueIdf) != null ? (String) dataValidation.get(valueIdf) : null;
            }
            return answer;
        }
        return null;
    }

    public boolean ifHaveCondition(QuestionBean mBean, String script, String valueScript, String answerType) {
        try {
            OperatorSet opSet = OperatorSet.getStandardOperatorSet();
            opSet.addOperator("!=", NotEqualSymbol.class);
            opSet.addOperator("and", AndSymbol.class);
            opSet.addOperator("or", OrSymbol.class);
            if (QuestionViewAdapter.IsTextQuestion(answerType) && !valueScript.contains("copyFromLookup")) {
                opSet.addOperator("copy", IfElseFunctionForCopyValue.class);
            } else {
                opSet.addOperator("copy", IfElseFunctionDummy.class);
            }

            Expression exp = new Expression(script);
            exp.setOperatorSet(opSet);
            Object result = exp.evaluate().getObject();
            if (QuestionViewAdapter.IsTextQuestion(answerType)) {
                mBean.setAnswer((String) result);
            } else {
                String lastScript = valueScript;
                String[] tempScript = Tool.split(lastScript, Global.DELIMETER_DATA3);
                int i = 1;
                String trueValue = tempScript[i].trim();
                i++;
                String falseValue = tempScript[i].trim();
                i++;
                for (; i < tempScript.length; i++) {
                    String tempSCript = tempScript[i];
                    if (falseValue.contains("copy(")) {
                        falseValue += Global.DELIMETER_DATA3;
                        falseValue += tempSCript;
                    }
                }
                falseValue = removeLastChar(falseValue);

                QuestionBean bean = null;
                if (Boolean.TRUE.equals(result)) {
                    if (trueValue.isEmpty()) {
                        return true;
                    }
                    if (trueValue.contains("#")) {
                        trueValue = trueValue.replace("{#", "").trim();
                        trueValue = trueValue.replace("}", "").trim();
                        Date tempDate = Formatter.parseDate(trueValue, Global.DATE_STR_FORMAT);
                        trueValue = Formatter.formatDate(tempDate, Global.DATE_STR_FORMAT_GSON);
                        mBean.setAnswer(trueValue);
                        return true;
                    } else if (trueValue.contains("$")) {
                        return copyQuestionValue(mBean, trueValue);
                    } else if (trueValue.substring(0, 1).equals("{")) {
                        trueValue = trueValue.replace("{", "").trim();
                        trueValue = trueValue.replace("}", "").trim();
                        bean = Constant.getListOfQuestion().get(trueValue);

                    } else {
                        return copyQuestionValue(mBean, trueValue);
                    }
                } else {
                    if (falseValue.isEmpty()) {
                        return true;
                    }
                    if (falseValue.contains("#")) {
                        falseValue = falseValue.replace("{#", "").trim();
                        falseValue = falseValue.replace("}", "").trim();
                        Date tempDate = Formatter.parseDate(falseValue, Global.DATE_STR_FORMAT);
                        falseValue = Formatter.formatDate(tempDate, Global.DATE_STR_FORMAT_GSON);
                        mBean.setAnswer(falseValue);
                        return true;
                    } else if (falseValue.contains("$")) {
                        return copyQuestionValue(mBean, falseValue);
                    } else if (falseValue.substring(0, 1).equals("{")) {
                        falseValue = falseValue.replace("{", "").trim();
                        falseValue = falseValue.replace("}", "").trim();
                        bean = Constant.getListOfQuestion().get(falseValue);
                    } else {
                        return copyQuestionValue(mBean, falseValue);
                    }
                }
                if (bean != null) {
                    if (QuestionBean.isHaveAnswer(bean)) {
                        try {
                            setAnswer(bean, mBean, answerType);
                            return true;
                        } catch (Exception e) {
                            FireCrash.log(e);
                            if (Global.IS_DEV) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public String getAnswer(String identifier, int idxOfOpenAbs) {
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
        } else if (finalIdentifier.equals(Global.IDF_UUID_BRANCH)) {
            flatAnswer = GlobalData.getSharedGlobalData().getUser().getUuid_branch();
        } else if (finalIdentifier.equals(Global.IDF_DEALER_ID)) {
            flatAnswer = GlobalData.getSharedGlobalData().getUser().getUuid_dealer();
        } else if (finalIdentifier.equals(Global.IDF_THIS_YEAR)) {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            flatAnswer = String.valueOf(cal.get(Calendar.YEAR));
        } else if (finalIdentifier.equals(Global.IDF_NOWADAYS)) {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            String format2 = Global.DATE_STR_FORMAT;
            flatAnswer = Formatter.formatDate(cal.getTime(), format2);
        }
        return flatAnswer;
    }

    public void setAnswer(QuestionBean bean, QuestionBean mBean, String answerType) {
        bean.setRelevanted(true);
        String flatAnswer = QuestionBean.getAnswer(bean);

        if (Tool.isOptions(answerType) && Tool.isOptions(bean.getAnswer_type())) {
            mBean.setSelectedOptionAnswers(bean.getSelectedOptionAnswers());
            if (Tool.isOptionsWithDescription(answerType) && Tool.isOptionsWithDescription(bean.getAnswer_type())) {
                mBean.setAnswer(bean.getAnswer());
            }
        } else if (QuestionViewAdapter.IsLookupQuestion(answerType) && QuestionViewAdapter.IsLookupQuestion(bean.getAnswer_type())) {
            mBean.setAnswer(bean.getAnswer());
            mBean.setSelectedCriteriaBean(bean.getSelectedCriteriaBean());
            mBean.setLookupCriteriaList(bean.getLookupCriteriaList());
        } else if (bean.getAnswer_type().equals(Global.AT_TEXT_WITH_SUGGESTION)) {
            if (answerType.equals(Global.AT_TEXT_WITH_SUGGESTION)) {
                mBean.setSelectedOptionAnswers(bean.getSelectedOptionAnswers());
            }
            mBean.setAnswer(bean.getSelectedOptionAnswers().get(0).toString());
        } else if ((Tool.isImage(bean.getAnswer_type()) || bean.getAnswer_type().equals(Global.AT_DRAWING)) && (Tool.isImage(answerType) || answerType.equals(Global.AT_DRAWING))) {
            mBean.setImgAnswer(bean.getImgAnswer());
            if (Tool.isHaveLocation(bean.getAnswer_type()) && Tool.isHaveLocation(answerType)) {
                mBean.setLocationInfo(bean.getLocationInfo());
            }
            if (bean.getImgTimestamp() != null) {
                mBean.setImgTimestamp(bean.getImgTimestamp());
            }
            mBean.setAnswer(flatAnswer);
        } else if (Tool.isHaveLocation(bean.getAnswer_type())) {
            if (Tool.isHaveLocation(answerType)) {
                mBean.setLocationInfo(bean.getLocationInfo());
            }
            mBean.setAnswer(flatAnswer);
        } else {
            mBean.setAnswer(flatAnswer);
        }
    }

    public String isDateTime(QuestionBean bean, String[] answers) {
        String flatAnswer = "";
        String format;
        try {
            if (bean.getAnswer_type().equals(Global.AT_TIME)) {
                format = Global.TIME_STR_FORMAT;
                String formatDate = Global.TIME_STR_FORMAT2;
                Date date2 = Formatter.parseDate(answers[0], formatDate);
                Calendar now = Calendar.getInstance(TimeZone.getDefault());
                Calendar date = Calendar.getInstance(TimeZone.getDefault());
                date.setTime(date2);
                date.set(Calendar.YEAR, now.get(Calendar.YEAR));
                date.set(Calendar.MONTH, now.get(Calendar.MONTH));
                date.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
                flatAnswer = Formatter.formatDate(date.getTime(), format);
            } else if (bean.getAnswer_type().equals(Global.AT_DATE_TIME)) {
                format = Global.DATE_TIME_STR_FORMAT;
                String formatDate = Global.DATE_STR_FORMAT_GSON;
                Date date2 = Formatter.parseDate(answers[0], formatDate);
                Calendar date = Calendar.getInstance(TimeZone.getDefault());
                date.setTime(date2);
                flatAnswer = Formatter.formatDate(date.getTime(), format);
            } else if (bean.getAnswer_type().equals(Global.AT_DATE)) {
                format = Global.DATE_STR_FORMAT;
                String formatDate = Global.DATE_STR_FORMAT_GSON;
                Date date2 = Formatter.parseDate(answers[0], formatDate);
                Calendar date = Calendar.getInstance(TimeZone.getDefault());
                date.setTime(date2);
                flatAnswer = Formatter.formatDate(date.getTime(), format);
            } else {
                flatAnswer = answers[0];
            }
            return flatAnswer;
        } catch (Exception e) {
            FireCrash.log(e);
            return null;
        }
    }

    protected QuestionBean getQuestionBeanForIdentifier(String identifier) {
        return Constant.getListOfQuestion().get(identifier);
    }

    public boolean isRelevantMandatorySubString(String expression) {
        Stack<Boolean> results = new Stack<>();
        Stack<Boolean> operators = new Stack<>();
        String temp = "";
        String ans1 = "";
        String ans2 = "";
        QuestionBean tempbean;
        boolean flag = false;
        boolean finalResult = false;
        boolean tempResult1 = false;
        int i;
        int start = 0;
        int end = 0;
        int operator1 = 0;
        expression = expression.replaceAll("\\s+", "");
        expression = expression.replaceAll(",", ".");
        for (i = 0; i < expression.length(); i++) {
            switch (expression.charAt(i)) {
                case '{':
                    start = i + 1;
                    break;
                case '}':
                    end = i;
                    temp = expression.substring(start, end).toUpperCase();
                    if ((getQuestionBeanForIdentifier(temp) == null)) {
                        ans2 = "";
                    } else {
                        tempbean = getQuestionBeanForIdentifier(temp);
                        if (tempbean.getAnswer() != null && !tempbean.getAnswer().equals("")) {
                            ans2 = tempbean.getAnswer();
                        } else if (tempbean.getLovCode() != null && !tempbean.getLovCode().equals("")) {
                            ans2 = tempbean.getLovCode();
                        } else {
                            ans2 = "";
                        }
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
                    if (expression.charAt(i + 1) == '=') {
                        i++;
                        operator1 = 2;
                    } else {
                        operator1 = 3;
                    }
                    break;
                case '<':
                    if (expression.charAt(i + 1) == '=') {
                        i++;
                        operator1 = 4;
                    } else {
                        operator1 = 5;
                    }
                    break;
                case '\'':
                    if (flag) {
                        flag = false;
                        end = i;
                        ans1 = expression.substring(start, end);
                        if (operator1 == 0) {
                            results.push(ans1.equals(ans2));
                        } else if (operator1 == 1) {
                            results.push(!ans1.equals(ans2));
                        } else if (operator1 == 2) {
                            results.push(Double.parseDouble(ans2.replaceAll(",", ".")) >= Double.parseDouble(ans1.replaceAll(",", ".")));
                        } else if (operator1 == 3) {
                            results.push(Double.parseDouble(ans2.replaceAll(",", ".")) > Double.parseDouble(ans1.replaceAll(",", ".")));
                        } else if (operator1 == 4) {
                            results.push(Double.parseDouble(ans2.replaceAll(",", ".")) <= Double.parseDouble(ans1.replaceAll(",", ".")));
                        } else if (operator1 == 5) {
                            results.push(Double.parseDouble(ans2.replaceAll(",", ".")) < Double.parseDouble(ans1.replaceAll(",", ".")));
                        }
                        start = 0;
                        end = 0;
                        ans1 = "";
                        ans2 = "";
                        temp = "";
                        tempbean = null;
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
                if (results.isEmpty()) {
                    return finalResult;
                } else {
                    tempResult1 = results.pop();
                    if (Boolean.TRUE.equals(operators.pop())) {
                        finalResult = finalResult && tempResult1;
                    } else {
                        finalResult = finalResult || tempResult1;
                    }
                    flag = false;
                }
            } else {
                tempResult1 = results.pop();
                if (Boolean.TRUE.equals(operators.pop())) {
                    finalResult = finalResult && tempResult1;
                } else {
                    finalResult = finalResult || tempResult1;
                }
            }
        }
        return finalResult;
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
    public List<OptionAnswerBean> getOptionsForQuestion(QuestionBean bean, boolean firstRequest) {
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
                            } else if (finalIdentifier.equals(Global.IDF_UUID_BRANCH)) {
                                String uuidBranch = GlobalData.getSharedGlobalData().getUser().getUuid_branch();
                                filters.add(uuidBranch);
                            } else if (finalIdentifier.equals(Global.IDF_DEALER_ID)) {
                                String dealerId = GlobalData.getSharedGlobalData().getUser().getUuid_dealer();
                                filters.add(dealerId);
                            }
                            constraintAmount++;
                        } else {
                            QuestionBean bean2 = Constant.getListOfQuestion().get(tempIdentifier);
                            if (bean2 != null) {
                                if (Global.AT_TEXT_WITH_SUGGESTION.equals(bean2.getAnswer_type())) {
                                    filters.add(bean2.getLovCode());
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
        List<OptionAnswerBean> optionAnswers;
        if (bean.getTag() != null && bean.getTag().equals(Global.TAG_RV_NUMBER)) {
            List<ReceiptVoucher> receiptVouchers = ReceiptVoucherDataAccess
                    .getByStatus(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), ReceiptVoucherDataAccess.STATUS_NEW);
            optionAnswers = OptionAnswerBean.getOptionListFromRv(receiptVouchers);
        } else {
            optionAnswers = GetLookupFromDB(bean, filters);
        }
        boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());

        if (firstRequest && (optionAnswers == null || optionAnswers.isEmpty())) {
            try {
                needStop = true;
                if (isRVinFront && bean.getTag() != null && bean.getTag().equals(Global.TAG_RV_NUMBER) && Global.APPLICATION_COLLECTION
                        .equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                    syncRvNumber(bean);
                } else {
                    new GetLookupOnDemand(bean, bean.getLov_group(), filters, constraintAmount).execute();
                }
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV) {
                    e.printStackTrace();
                }
            }
            optionAnswers = GetLookupFromDB(bean, filters);
        }
        if ((Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication()) && bean.getTag() != null) && (isRVinFront && bean.getTag()
                .equals(Global.TAG_RV_NUMBER)) && (bean.getSelectedOptionAnswers() != null && !bean.getSelectedOptionAnswers().isEmpty())) {
            if (DynamicFormActivity.header != null && DynamicFormActivity.header.getDraft_date() != null) {
                List<OptionAnswerBean> opBean = bean.getSelectedOptionAnswers();
                OptionAnswerBean answerBean = opBean.get(0);
                ReceiptVoucher selectedRv = ReceiptVoucherDataAccess.getOne(getContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), answerBean.getUuid_lookup());
                if (selectedRv != null || (null != bean.getAnswer() && !bean.getAnswer().isEmpty())) {
                    optionAnswers.add(answerBean);
                }
            }
        }
        return optionAnswers;
    }

    public void syncRvNumber(final QuestionBean bean) {
        final String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        SyncRVRequest request = new SyncRVRequest();
        request.setLastDtmCrt(ReceiptVoucherDataAccess.getLastDate(getActivity(), uuidUser));

        SyncRVTask task = new SyncRVTask(getActivity(), request, new SyncRvListener() {
            ProgressDialog dialog;

            @Override
            public void onProgress() {
                dialog = ProgressDialog.show(getActivity(), "Sync RV Number", getString(R.string.please_wait), true, false);
            }

            @Override
            public void onError(SyncRVResponse response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (response.getErrorMessage() != null) {
                    String message = getString(R.string.get_lookup_failed, Global.TAG_RV_NUMBER);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    needStop = true;
                }
            }

            @Override
            public void onSuccess(SyncRVResponse response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                List<ReceiptVoucher> rvNumbers = response.getListReceiptVoucher();

                if (rvNumbers != null && !rvNumbers.isEmpty()) {
                    try {
                        ReceiptVoucherDataAccess.addNewReceiptVoucher(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), rvNumbers);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                        ACRA.getErrorReporter().putCustomData("errorRV", e.getMessage());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Error: Insert RV Error. " + e.getMessage()));
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    try {
                        boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(getActivity(), uuidUser);
                        if (isRVinFront) {
                            List<Lookup> lookupRVList = new ArrayList<>();
                            for (int i = 0; i < rvNumbers.size(); i++) {
                                ReceiptVoucher rv = rvNumbers.get(i);
                                Lookup lookup = new Lookup();

                                lookup.setUuid_lookup(rv.getUuid_receipt_voucher());
                                lookup.setCode(rv.getUuid_receipt_voucher());
                                lookup.setValue(rv.getRv_number());
                                lookup.setSequence(i);
                                lookup.setDtm_upd(rv.getDtm_crt());
                                lookup.setLov_group(rv.getFlag_sources());

                                lookup.setIs_active(Global.TRUE_STRING);
                                lookup.setIs_deleted(Global.FALSE_STRING);
                                lookup.setFilter1(rv.getUuid_user());
                                lookupRVList.add(lookup);
                            }
                            LookupDataAccess.addOrUpdateAll(getActivity(), lookupRVList);
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                }

                List<OptionAnswerBean> options = getOptionsForQuestion(bean, false);

                if (options.size() != 0) {
                    if (Global.TAG_RV_NUMBER.equals(bean.getTag())) {
                        for (int i = 0; i < options.size(); i++) {
                            if ("INIT".equals(options.get(i).getCode())) {
                                options.remove(i);
                            }
                        }
                    }
                }

                for (int i = 0; i < listOfQuestions.size(); i++) {
                    QuestionBean mBean = listOfQuestions.get(i);
                    if (mBean.getUuid_question_set().equals(bean.getUuid_question_set())) {
                        mBean.setOptionAnswers(options);
                        int groupIdx = listOfQuestionGroup.indexOf(mBean.getQuestion_group_name()) + 1;
                        int position = i + groupIdx;
                        changeItem(position);
                    }
                }
            }
        });
        task.execute();
    }

    public List<OptionAnswerBean> GetLookupFromDB(QuestionBean bean, List<String> filters) {
        List<OptionAnswerBean> optionAnswers = new ArrayList<>();
        if (!filters.isEmpty()) {
            if (filters.size() == 1) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(getActivity(), bean.getLov_group(), filters.get(0));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 2) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(getActivity(), bean.getLov_group(), filters.get(0), filters.get(1));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 3) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(getActivity(), bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 4) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(getActivity(), bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2), filters.get(3));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 5) {
                List<Lookup> nLookups = LookupDataAccess
                        .getAllByFilter(getActivity(), bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2), filters.get(3), filters.get(4));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            }

        } else {
            if (bean.getChoice_filter() != null && bean.getChoice_filter().length() > 0) {
                List<Lookup> lookups = new ArrayList<>();
                optionAnswers = OptionAnswerBean.getOptionList(lookups);
            } else {
                String lovGroup = bean.getLov_group();
                if (lovGroup != null) {
                    List<Lookup> lookups = LookupDataAccess.getAllByLovGroup(getActivity(), lovGroup);
                    if (lookups != null) {
                        optionAnswers = OptionAnswerBean.getOptionList(lookups);
                    }
                }
            }
        }
        return optionAnswers;
    }

    public String doCalculate(QuestionBean bean) {
        String formula = bean.getCalculate();
        String expression = formula;
        double total = 0;

        try {
            if (!("var").equalsIgnoreCase(expression.substring(0, 3))) {
                return "0";
            }
        } catch (Exception e) {
            FireCrash.log(e);
            Logger.e("FragmentQuestion", e);
            return "0";
        }

        List<QuestionBean> questionList = new ArrayList<>();
        String resultformula2 = formula.substring(0, formula.indexOf("for"));
        resultformula2 = resultformula2.replace("_var = 0", "");
        resultformula2 = resultformula2.replace("var ", "");
        resultformula2 = resultformula2.replace(" ", "");
        resultformula2 = resultformula2.replace("\r", "");
        resultformula2 = resultformula2.replace("\n", "");
        String[] nFormula2 = resultformula2.split(";");

        for (String idf : nFormula2) {
            //setRelevanted should be here
            //alternative for isSpecialCaseChangeFlag
            QuestionBean tempBean = Constant.getListOfQuestion().get(idf.toUpperCase());
            if (tempBean == null) {
                continue;
            }

            if (tempBean.getAnswer_type().equals(Global.AT_DECIMAL) || tempBean.getAnswer_type().equals(Global.AT_CURRENCY)) {
                try {
                    NumberFormat nf = NumberFormat.getInstance(Locale.US);
                    if (tempBean.getAnswer() != null && !"".equals(tempBean.getAnswer())) {
                        Double finalAnswer = nf.parse(tempBean.getAnswer()).doubleValue();
                        tempBean.setAnswer(finalAnswer.toString());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            questionList.add(tempBean);
        }

        JexlContext context = new MapContext();
        context.set("listOfQuestion", questionList);
        context.set("qBean", new QuestionBean(bean));
        context.set("dateformatter", new DateFormatter());
        context.set("bean", bean);
        context.set("result", total);
        Object value = null;
        try {
            value = jexlEngine.createScript(expression).execute(context);
        } catch (Exception e) {
            FireCrash.log(e);
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getString(R.string.calculate_failed), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e2) {
                e.printStackTrace();
            }
        }
        if (value != null) {
            try {
                Double answer = (Double) context.get("result");
                return String.format(Locale.US, "%.2f", answer);
            } catch (Exception e) {
                FireCrash.log(e);
                try {
                    double answer = (Double) value;
                    return String.format(Locale.US, "%.2f", answer);
                } catch (Exception e1) {
                    try {
                        int answer = (int) value;
                        return String.format(Locale.US, "%d", answer);
                    } catch (Exception e2) {
                        FireCrash.log(e2);
                        return value.toString();
                    }
                }
            }
        } else {
            return "0";
        }
    }

    private boolean isFilterHaveAnswer(String choiceFilterExpression) {
        boolean result = false;
        String[] tempfilters = Tool.split(choiceFilterExpression, Global.DELIMETER_DATA3);
        for (String newFilter : tempfilters) {
            int idxOfOpenBrace = newFilter.indexOf('{');
            if (idxOfOpenBrace != -1) {
                int idxOfCloseBrace = newFilter.indexOf('}');
                String tempIdentifier = newFilter.substring(idxOfOpenBrace + 1, idxOfCloseBrace).toUpperCase();
                int idxOfOpenAbs = tempIdentifier.indexOf("$");
                if (idxOfOpenAbs != -1) {
                    String flatAnswer = getAnswer(tempIdentifier, idxOfOpenAbs);
                    if (flatAnswer != null && flatAnswer.length() > 0) {
                        result = true;
                    } else {
                        result = false;
                    }
                } else {
                    QuestionBean filterBean = Constant.getListOfQuestion().get(tempIdentifier);
                    if (Global.AT_TEXT.equals(filterBean.getAnswer_type())) {
                        if (filterBean.getAnswer() != null && filterBean.getAnswer().length() > 0) {
                            result = true;
                        } else {
                            result = false;
                            break;
                        }
                    } else if (Global.AT_TEXT_WITH_SUGGESTION.equals(filterBean.getAnswer_type())) {
                        if (filterBean.getSelectedOptionAnswers() != null && !filterBean.getSelectedOptionAnswers().isEmpty()) {
                            result = true;
                        } else {
                            result = false;
                            break;
                        }
                    } else if (Global.AT_DROPDOWN.equals(filterBean.getAnswer_type())) {
                        if (filterBean.getSelectedOptionAnswers() != null && !filterBean.getSelectedOptionAnswers().isEmpty()) {
                            result = true;
                        } else {
                            result = false;
                            break;
                        }
                    } else if (Global.AT_RADIO.equals(filterBean.getAnswer_type())) {
                        if (filterBean.getSelectedOptionAnswers() != null && !filterBean.getSelectedOptionAnswers().isEmpty()) {
                            result = true;
                        } else {
                            result = false;
                            break;
                        }
                    } else if (Global.AT_LOOKUP.equals(filterBean.getAnswer_type())) {
                        if (filterBean.getAnswer() != null && filterBean.getAnswer().length() > 0) {
                            result = true;
                        } else {
                            result = false;
                            break;
                        }
                    }
                }

            }
        }
        return result;
    }

    public boolean isQuestionVisibleIfRelevant(String relevantExpression, QuestionBean question, boolean mandatory) {
        boolean result;
        String convertedExpression = relevantExpression;
        if (convertedExpression == null || convertedExpression.length() == 0) {
            if (mandatory) {
                return false;
            } else {
                if (question.getChoice_filter() != null && question.getChoice_filter().length() > 0) {
                    return isFilterHaveAnswer(question.getChoice_filter());
                } else {
                    return true;
                }
            }
        } else {
            boolean needReplacing = true;
            while (needReplacing) {
                convertedExpression = replaceModifiers(convertedExpression);
                int idxOfOpenBrace = convertedExpression.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    int idxOfCloseBrace = convertedExpression.indexOf('}');
                    String identifier = convertedExpression.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                    int idxOfOpenAbs = identifier.indexOf("$");
                    if (idxOfOpenAbs != -1) {
                        String flatAnswer = getAnswer(identifier, idxOfOpenAbs);

                        if (flatAnswer != null && flatAnswer.length() > 0) {
                            if (convertedExpression.matches("\\d+")) {
                                convertedExpression = convertedExpression.replace("{" + identifier + "}", flatAnswer);
                            } else {
                                convertedExpression = convertedExpression.replace("{" + identifier + "}", '"' + flatAnswer + '"');
                            }
                        } else {
                            return false;
                        }
                    } else {
                        QuestionBean bean = Constant.getListOfQuestion().get(identifier);
                        if (bean != null) {
                            String flatAnswer = "";
                            flatAnswer = QuestionBean.getAnswer(bean);
                            if (flatAnswer != null) {
                                String[] answers = Tool.split(flatAnswer, Global.DELIMETER_DATA);
                                if (answers.length == 1) {
                                    if (answers[0].length() <= 0) {
                                        answers[0] = "''";
                                    }
                                    if (convertedExpression.matches("\\d+")) {
                                        convertedExpression = convertedExpression.replace("{" + identifier + "}", answers[0]);
                                    } else {
                                        convertedExpression = convertedExpression.replace("{" + identifier + "}", '"' + answers[0] + '"');
                                    }
                                } else {
                                    for (int i = 0; i < answers.length; i++) {
                                        if (answers[i].length() <= 0) {
                                            answers[i] = "''";
                                        }
                                        String convertedSubExpression = "";
                                        if (convertedExpression.matches("\\d+")) {
                                            convertedSubExpression = convertedExpression.replace("{" + identifier + "}", answers[i]);
                                        } else {
                                            convertedSubExpression = convertedExpression.replace("{" + identifier + "}", '"' + answers[i] + '"');
                                        }
                                        boolean isVisible;
                                        if (mandatory) {
                                            isVisible = isQuestionVisibleIfRelevant(convertedExpression, question, true);
                                        } else {
                                            isVisible = isQuestionVisibleIfRelevant(convertedSubExpression, question, false);
                                        }
                                        if (isVisible) {
                                            return true;
                                        }
                                    }
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            convertedExpression = convertedExpression.replace("{" + identifier + "}", "\"\"");
                        }
                    }
                } else {
                    needReplacing = false;
                }
            }
            try {
                OperatorSet opSet = OperatorSet.getStandardOperatorSet();
                opSet.addOperator("!=", NotEqualSymbol.class);
                opSet.addOperator("and", AndSymbol.class);
                opSet.addOperator("or", OrSymbol.class);
                convertedExpression = convertedExpression.replace("\"", "");
                Expression exp = new Expression(convertedExpression);
                exp.setOperatorSet(opSet);
                result = exp.evaluate().toBoolean();
                return result;
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV) {
                    e.printStackTrace();
                }
                return false;
            }
        }
    }

    public boolean isPaymentChannel(String expression) {
        boolean result = false;
        String convertedExpression = expression;
        if (convertedExpression.length() == 0) {
            return result;
        }

        boolean needReplacing = true;
        while (needReplacing) {
            convertedExpression = replaceModifiers(convertedExpression);
            int idxOfOpenBrace = convertedExpression.indexOf('{');
            if (idxOfOpenBrace != -1) {
                int idxOfCloseBrace = convertedExpression.indexOf('}');
                String identifier = convertedExpression.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                int idxOfOpenAbs = identifier.indexOf("$");
                if (idxOfOpenAbs != -1) {
                    String flatAnswer = getAnswer(identifier, idxOfOpenAbs);

                    if (flatAnswer != null && flatAnswer.length() > 0) {
                        convertedExpression = convertedExpression.replace("{" + identifier + "}", flatAnswer);
                    } else {
                        return false;
                    }
                } else {
                    QuestionBean bean = Constant.getListOfQuestion().get(identifier);
                    if (bean != null) {
                        String flatAnswer = "";
                        if (bean.getIs_visible().equals(Global.FALSE_STRING)) {
                            flatAnswer = "false";
                        } else {
                            flatAnswer = QuestionBean.getAnswer(bean);
                        }

                        if (flatAnswer != null) {
                            convertedExpression = convertedExpression.replace("{" + identifier + "}", flatAnswer);
                        } else {
                            return false;
                        }
                    } else {
                        convertedExpression = convertedExpression.replace("{" + identifier + "}", "\"\"");
                    }
                }
            } else {
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
            if (Global.IS_DEV) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public String replaceModifiers(String sourceString) {
        //replace branch modifier
        String branch = GlobalData.getSharedGlobalData().getUser().getBranch_id();
        //replace user modifier
        String user = GlobalData.getSharedGlobalData().getUser().getUuid_user();

        return sourceString.replace(QuestionBean.PLACEMENT_KEY_BRANCH, branch).replace(QuestionBean.PLACEMENT_KEY_USER, user);
    }

    public void setLocationForLocationQuestion() {
        QuestionBean mBean = focusBean;
        LocationInfo info = LocationTagingView.locationInfo;
        LocationInfo2 infoFinal = new LocationInfo2(info);
        mBean.setAnswer(LocationTrackingManager.toAnswerStringShort(infoFinal));
        mBean.setLocationInfo(infoFinal);
        int position = focusPosition + getCounterListBeforeGroup(focusGroup, false);
        changeItem(position);
        Utility.freeMemory();
    }

    public int getCounterListBeforeGroup(int group, boolean isReview) {
        int countListBefore = 0;
        ExpandableRecyclerView.Adapter adapter;
        for (int i = 0; i < group; i++) {
            if (isReview) {
                adapter = reviewAdapter;
            } else {
                adapter = questionAdapter;
            }
            if (adapter.isExpanded(i)) {
                countListBefore += adapter.getChildItemCount(i);
            }
        }
        countListBefore += (group + 1);
        return countListBefore;
    }

    public void setImageForImageQuestion(String path) {
        if (path != null) {
            Uri uri = Uri.parse(path);
            File file = new File(uri.getPath());
            processImageFile(file);
            Utility.freeMemory();
        }
    }

    public void processImageFile(File file) {
        try {
            QuestionBean mBean = focusBean;
            String formattedSize;
            String indicatorGPS = "";
            boolean isGeoTagged;
            boolean isGeoTaggedGPSOnly;
            boolean isImageDkcp;
            int[] res;
            ExifData exifData = Utils.getDataOnExif(file);
            int rotate = exifData.getOrientation();
            float scale;
            int newSize = 0;
            int quality = Utils.picQuality;
            int thumbHeight = Utils.picHeight;
            int thumbWidht = Utils.picWidth;

            isImageDkcp = Global.AT_ID_CARD_PHOTO.equals(mBean.getAnswer_type());
            boolean isHQ = false;
            if (mBean.getImg_quality() != null && mBean.getImg_quality().equalsIgnoreCase(Global.IMAGE_HQ)) {
                thumbHeight = Utils.picHQHeight;
                thumbWidht = Utils.picHQWidth;
                quality = Utils.picHQQuality;
                isHQ = true;
            }

            boolean isHeightScale = thumbHeight >= thumbWidht;

            byte[] _data = null;
            try {
                if (isHeightScale) {
                    scale = (float) thumbHeight / exifData.getHeight();
                    newSize = Math.round(exifData.getWidth() * scale);
                    _data = Utils.resizeBitmapFileWithWatermark(file, rotate, newSize, thumbHeight, quality, getActivity(), isHQ);
                } else {
                    scale = (float) thumbWidht / exifData.getWidth();
                    newSize = Math.round(exifData.getHeight() * scale);
                    _data = Utils.resizeBitmapFileWithWatermark(file, rotate, thumbWidht, newSize, quality, getActivity(), isHQ);
                }
            } catch (Exception e) {
                FireCrash.log(e);
                _data = null;
            }
            if (_data != null) {
                if (isImageDkcp && mBean.getImgAnswer() != null) {
                    mBean.setIsCanChange(true);
                    mBean.setChange(true);
                }
                mBean.setImgAnswer(_data);
                mBean.setImgTimestamp(new Date(System.currentTimeMillis()));
                deleteLatestPictureCreate(getContext());
                try {
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT && !GlobalData.getSharedGlobalData().isUseOwnCamera()) {
                        Utils.deleteLatestPicture(getContext());
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    try {
                        String manufacture = android.os.Build.MANUFACTURER;
                        if (manufacture.contains("LGE") && !GlobalData.getSharedGlobalData().isUseOwnCamera()) {
                            Utils.deleteLatestPictureLGE(getContext());
                        }
                    } catch (Exception e2) {
                        FireCrash.log(e);
                    }
                }

                boolean getGPS = true;
                LocationInfo locBean;

                isGeoTagged = Global.AT_IMAGE_W_LOCATION.equals(mBean.getAnswer_type());
                isGeoTaggedGPSOnly = Global.AT_IMAGE_W_GPS_ONLY.equals(mBean.getAnswer_type());
                if (isGeoTagged) {
                    LocationTrackingManager pm = Global.LTM;
                    if (pm != null) {
                        locBean = pm.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
                        LocationInfo2 infoFinal = new LocationInfo2(locBean);
                        if (infoFinal.getLatitude().equals("0.0") || infoFinal.getLongitude().equals("0.0")) {
                            if (infoFinal.getMcc().equals("0") || infoFinal.getMnc().equals("0")) {
                                if (mBean.isMandatory() || mBean.isRelevantMandatory()) {
                                    mBean.setLocationInfo(infoFinal);
                                    String geodataError = getString(R.string.geodata_error);
                                    String[] msg = {geodataError};
                                    String alert2 = Tool.implode(msg, "\n");
                                    Toast.makeText(getActivity(), alert2, Toast.LENGTH_LONG).show();
                                    getGPS = false;
                                }
                            } else {
                                mBean.setLocationInfo(infoFinal);
                                if (mBean.isMandatory() || mBean.isRelevantMandatory()) {
                                    String gpsError = getString(R.string.gps_gd_error);
                                    String[] msg = {gpsError};
                                    String alert2 = Tool.implode(msg, "\n");
                                    Toast.makeText(getActivity(), alert2, Toast.LENGTH_LONG).show();
                                }
                            }
                            indicatorGPS = getString(R.string.coordinat_not_available);
                        } else {
                            mBean.setAnswer(LocationTrackingManager.toAnswerStringShort(infoFinal));
                            mBean.setLocationInfo(infoFinal);
                            indicatorGPS = mBean.getAnswer();
                        }
                    }
                }

                if (isGeoTaggedGPSOnly && Global.LTM != null) {
                    locBean = Global.LTM.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
                    LocationInfo2 infoFinal = new LocationInfo2(locBean);
                    if (infoFinal.getLatitude().equals("0.0") || infoFinal.getLongitude().equals("0.0")) {

                        if (mBean.isMandatory() || mBean.isRelevantMandatory()) {
                            mBean.setLocationInfo(infoFinal);
                            String gpsError = getString(R.string.gps_error);
                            String[] msg = {gpsError};
                            String alert2 = Tool.implode(msg, "\n");
                            Toast.makeText(getActivity(), alert2, Toast.LENGTH_LONG).show();
                            getGPS = false;
                        } else {
                            indicatorGPS = getString(R.string.coordinat_not_available);
                        }

                    } else {
                        mBean.setAnswer(LocationTrackingManager.toAnswerStringShort(infoFinal));
                        mBean.setLocationInfo(infoFinal);
                        indicatorGPS = mBean.getAnswer();
                    }
                }

                // set thumbnail
                if (mBean.getImgAnswer() != null && getGPS) {
                    Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);
                    res = new int[2];
                    res[0] = bm.getWidth();
                    res[1] = bm.getHeight();

                    long size = mBean.getImgAnswer().length;
                    formattedSize = Formatter.formatByteSize(size);

                    if (isGeoTagged || isGeoTaggedGPSOnly) {
                        String text = res[0] + " x " + res[1] + ". Size " + formattedSize + "\n" + indicatorGPS;
                        mBean.setAnswer(text);
                    } else {
                        String text = res[0] + " x " + res[1] + ". Size " + formattedSize;
                        mBean.setAnswer(text);
                    }
                }
                if (isImageDkcp) {
                    String imageBase64 = Utils.byteToBase64(_data);
                    new SubmitImageDkcp(getActivity(), getContext(), listOfQuestionBean, focusBean).execute(imageBase64);
                }
            }

        } catch (OutOfMemoryError e) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.processing_image_error), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            FireCrash.log(e);
            Toast.makeText(getActivity(), getActivity().getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
        }
        int position = focusPosition + getCounterListBeforeGroup(focusGroup, false);
        changeItem(position);
    }

    public void deleteLatestPictureCreate(Context context) {
        try {
            getActivity().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, DATA + "='" + DynamicFormActivity.getmCurrentPhotoPath() + "'", null);

            File f = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!f.exists()) {
                f.mkdir();
            }
            File[] files = f.listFiles();
            Arrays.sort(files, new Comparator() {
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
            boolean deleteResult = files[0].delete();
            if (!deleteResult) {
                Toast.makeText(context, "Failed to delete last saved photo", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV) {
                e.printStackTrace();
            }
        }
    }

    private void sendVerification() {
        DynamicFormActivity.getHeader().setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
        DynamicFormActivity.getHeader().setIs_prepocessed(Global.FORM_TYPE_VERIFICATION);
        if (DynamicFormActivity.getHeader().getSubmit_date() == null) {
            DynamicFormActivity.getHeader().setSubmit_date(Tool.getSystemDateTime());
        }
        new TaskManager().saveAndSendTask(getActivity(), mode, DynamicFormActivity.getHeader(), listOfQuestions);
    }

    private void saveAndSendSurvey() {
        if (!isSaveAndSending) {
            isSaveAndSending = true;
            if (TaskHDataAccess.STATUS_SEND_INIT.equals(DynamicFormActivity.getHeader().getStatus())) {
                Constant.notifCount--;
            }
            doSubmit();
        }
    }

    private void doSubmit() {
        DynamicFormActivity.getHeader().setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
        DynamicFormActivity.getHeader().setSubmit_date(Tool.getSystemDateTime());

        getActivity().findViewById(R.id.btnSend).setEnabled(true);
        getActivity().findViewById(R.id.btnSend).setClickable(true);

        isSaveAndSending = false;

        String tag = "";
        for (int i = 0; i < listOfQuestions.size(); i++) {
            QuestionBean mbean = listOfQuestions.get(i);
            tag = mbean.getTag();
            if (tag != null && tag.equalsIgnoreCase("Total Bayar")) {
                break;
            }
        }
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.progressSend));
        progressDialog.setCancelable(false);
        progressDialog.show();
        // olivia : utk task yg melakukan pembayaran set isTaskPaid = true dan dilakukan scr online lalu tampil dialog Submit Result, selain itu jalan di background
        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication()) && tag != null && tag.equalsIgnoreCase("Total Bayar")) {
            TaskManager.SendTaskOnBackground submitTask = new TaskManager.SendTaskOnBackground(getActivity(), mode, DynamicFormActivity.getHeader(), listOfQuestions, true);
            submitTask.setProgressDialog(progressDialog);
            //asynctask parallel execution, so others asynctask can work in the separated thread
            submitTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            if (DynamicFormActivity.getHeader().getForm().getForm_type().equals(Global.FORM_TYPE_KTP)) {
                TaskH taskH = DynamicFormActivity.getHeader().getTaskH();
                for (Map.Entry<String, List<QuestionBean>> entry : listOfQuestionBean.entrySet()) {
                    for (QuestionBean bean : listOfQuestionBean.get(entry.getKey())) {
                        if (bean.getTag() != null) {
                            if (bean.getTag().equalsIgnoreCase("alamat")) {
                                taskH.setCustomer_address(bean.getAnswer());
                            } else if (bean.getTag().equalsIgnoreCase("customer name")) {
                                taskH.setCustomer_name(bean.getAnswer());
                            } else if (bean.getTag().equalsIgnoreCase("customer phone")) {
                                taskH.setCustomer_phone(bean.getAnswer());
                            }
                        }
                    }
                }
            }

            TaskManager.SendTaskOnBackground submitTask = new TaskManager.SendTaskOnBackground(getActivity(), mode, DynamicFormActivity.getHeader(), listOfQuestions, false);
            submitTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            progressDialog.dismiss();
            Global.setREDIRECT(Global.REDIRECT_TIMELINE);
            getActivity().finish();
        }
    }

    private void setAnswerForLookupQuestion(Serializable selectedCriteria) {
        if (selectedCriteria instanceof LookupCriteriaBean) {
            LookupCriteriaBean bean = (LookupCriteriaBean) selectedCriteria;
            bean.setSelected(true);
            QuestionBean mBean = focusBean;
            mBean.setSelectedCriteriaBean(bean);
            listOfLookupOnlineStored.put(mBean.getIdentifier_name(), mBean.getLookupCriteriaList());
            String newAnswer = bean.getCode() + Global.DELIMETER_ROW + bean.getValue();
            mBean.setIsCanChange(true);
            mBean.setChange(true);
            mBean.setAnswer(newAnswer);

            int position = focusPosition + getCounterListBeforeGroup(focusGroup, false);
            changeItem(position);
            Utility.freeMemory();
        }
    }

    private void setImageFromEditViewer(byte[] value) {
        QuestionBean mBean = focusBean;
        mBean.setImgAnswer(value);
        boolean isGeoTagged = Global.AT_IMAGE_W_LOCATION.equals(mBean.getAnswer_type());
        boolean isGeoTaggedGPSOnly = Global.AT_IMAGE_W_GPS_ONLY.equals(mBean.getAnswer_type());
        long size = value.length;
        String formattedSize = Formatter.formatByteSize(size);
        Bitmap temp_bmp = Utils.byteToBitmap(value);
        if (isGeoTagged || isGeoTaggedGPSOnly) {
            if (mBean.getLocationInfo() != null) {
                String indicatorGPS = LocationTrackingManager.toAnswerStringShort(mBean.getLocationInfo());
                String answer = temp_bmp.getWidth() + " x " + temp_bmp.getHeight() + ". Size " + formattedSize + "\n" + indicatorGPS;
                mBean.setAnswer(answer);
            } else {
                String answer = temp_bmp.getWidth() + " x " + temp_bmp.getHeight() + ". Size " + formattedSize + "\n" + getActivity().getString(R.string.coordinat_not_available);
                mBean.setAnswer(answer);
            }
        } else {
            String answer = temp_bmp.getWidth() + " x " + temp_bmp.getHeight() + ". Size " + formattedSize;
            mBean.setAnswer(answer);
        }
        int position = focusPosition + getCounterListBeforeGroup(focusGroup, false);
        changeItem(position);
        Utility.freeMemory();
    }

    private void removeNextCallback() {

        hasLoading = false;
        getActivity().findViewById(R.id.btnNext).setClickable(true);
        getActivity().findViewById(R.id.btnSave).setClickable(true);
        getActivity().findViewById(R.id.btnSearch).setClickable(true);

        Utility.freeMemory();
    }

    public void notifyInsert(int position) {
        questionAdapter.notifyItemInserted(position);
    }

    private void focusToView(int position) {
        ScrollingLinearLayoutManager layoutManager = (ScrollingLinearLayoutManager) qRecyclerView.getLayoutManager();
        layoutManager.scrollToPosition(position);
    }

    private void setFocusable(final int position, final int duration) {
        focusToView(position);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder view = qRecyclerView.findViewHolderForAdapterPosition(position);
                if (view instanceof TextQuestionViewHolder) {
                    ((TextQuestionViewHolder) view).mQuestionAnswer.requestFocus();
                    ((TextQuestionViewHolder) view).mQuestionAnswer.setFocusable(true);
                    InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(((TextQuestionViewHolder) view).mQuestionAnswer, 0);
                } else {
                    hideKeyboard();
                }
            }
        }, duration);
    }

    public void hideKeyboard() {
        try {
            InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV) {
                e.printStackTrace();
            }
        }
    }

    public class QuestionHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            final Bundle bundle = msg.getData();
            int action = bundle.getInt(BUND_KEY_ACTION);
            if (action == NEXT_QUESTION) {
                if (isFinish && (DynamicFormActivity.getIsVerified() || GlobalData.getSharedGlobalData().getUser().getFlag_job().equalsIgnoreCase(gp_jobspv.getGs_value()))) {
                    if (Global.NEW_FEATURE) {
                        if (Tool.isInternetconnected(getActivity())) {
                            if (validateAllMandatory(true)) {
                                Intent intent = new Intent(getActivity(), Global.VerificationActivityClass);
                                intent.putExtra(Global.BUND_KEY_UUID_TASKH, DynamicFormActivity.getHeader().getUuid_task_h());
                                intent.putExtra(Global.BUND_KEY_MODE_SURVEY, mode);
                                if (DynamicFormActivity.getIsApproval()) {
                                    intent.putExtra(Global.BUND_KEY_FORM_NAME, Global.APPROVAL_FLAG);
                                    DynamicFormActivity.getHeader().setStatus(TaskHDataAccess.STATUS_TASK_APPROVAL);
                                } else {
                                    intent.putExtra(Global.BUND_KEY_FORM_NAME, Global.VERIFICATION_FLAG);
                                    DynamicFormActivity.getHeader().setStatus(TaskHDataAccess.STATUS_TASK_VERIFICATION);
                                }
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (doNext(true)) {
                        getActivity().findViewById(R.id.btnNext).setClickable(false);
                        if (Global.NEW_FEATURE) {
                            if (Global.FEATURE_REJECT_WITH_RESURVEY) {
                                if (!DynamicFormActivity.getIsVerified() && !GlobalData.getSharedGlobalData().getUser().getFlag_job().equalsIgnoreCase(gp_jobspv.getGs_value())) {
                                    getActivity().findViewById(R.id.btnNext).setClickable(false);
                                } else {
                                    getActivity().findViewById(R.id.btnNext).setClickable(true);
                                }
                            }
                        } else {
                            getActivity().findViewById(R.id.btnNext).setClickable(false);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                qRecyclerView.smoothScrollToPosition(qRecyclerView.getAdapter().getItemCount());
                                int lastPosition = qRecyclerView.getAdapter().getItemCount() - 1;
                                if (!isFinish) {
                                    setFocusable(lastPosition, 500);
                                }
                                if (new DynamicQuestionView(getActivity()).isAutoSave()) {
                                    final int mode = bundle.getInt(Global.BUND_KEY_MODE_SURVEY);
                                    doSaveNoValidate(mode);
                                }
                            }
                        }, 100);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinish) {
                                    getActivity().findViewById(R.id.btnNextLayout).setVisibility(View.VISIBLE);
                                    getActivity().findViewById(R.id.btnNext).setClickable(true);
                                }
                            }
                        }, 500);
                    }
                }
            } else if (action == SAVE_QUESTION) {
                int mode = bundle.getInt(Global.BUND_KEY_MODE_SURVEY);
                doSave(mode);
            } else if (action == SEND_QUESTION) {
                doSend();
                GlobalData.getSharedGlobalData().setDoingTask(false);
            } else if (action == SEARCH_QUESTION) {
                String searchKey = bundle.getString(BUND_KEY_SEARCH_ACTION);
                doSearch(searchKey, false);
            } else if (action == VERIFY_QUESTION) {
                doVerify();
            } else if (action == APPROVE_QUESTION) {
                doApprove();
            } else if (action == REJECT_QUESTION) {
                doReject();
            } else if (action == RESULT_FROM_DRAWING_QUESTION) {
                setImageForDrawingQuestion(bundle.getByteArray(DrawingCanvasActivity.BUND_KEY_IMAGE_RESULT));
            } else if (action == RESULT_FROM_BUILT_IN_CAMERA) {
                setImageForImageQuestion(bundle.getString(CameraActivity.PICTURE_URI));
            } else if (action == RESULT_FROM_ANDROID_CAMERA) {
                setImageForImageQuestion(DynamicFormActivity.getmCurrentPhotoPath());
            } else if (action == RESULT_FROM_LOCATION_QUESTION) {
                setLocationForLocationQuestion();
            } else if (action == RESULT_FROM_EDIT_IMAGE) {
                setImageFromEditViewer(bundle.getByteArray(ImageViewerActivity.BUND_KEY_IMAGE));
            } else if (action == RESULT_FROM_LOOKUP_CRITERIA) {
                setAnswerForLookupQuestion(bundle.getSerializable(KEY_SELECTED_CRITERIA));
            } else if (action == SUBMIT_FORM) {
                String rawResult = bundle.getString("Result");
                assert rawResult != null;
                String[] result = parsePaymentResult(rawResult);
                DynamicFormActivity.getHeader().setReference_number(result[11]);
                saveAndSendSurvey();
            }
        }
    }

    private class GetLookupOnDemand extends AsyncTask<Void, Void, Boolean> {

        String errMessage = null;
        String lov_group = null;
        int constraintAmount = 0;
        QuestionBean bean;
        List<String> filters;
        private ProgressDialog progressDialog;

        public GetLookupOnDemand(QuestionBean bean, String lov_group, List<String> filters, int constraintAmount) {
            this.bean = bean;
            this.lov_group = lov_group;
            if (filters != null) {
                this.filters = filters;
            } else {
                this.filters = new ArrayList<>();
            }
            this.constraintAmount = constraintAmount;
        }

        @Override
        protected void onPreExecute() {
            String message = getString(R.string.lookup_progress, lov_group);
            this.progressDialog = ProgressDialog.show(getActivity(), "", message, true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (Tool.isInternetconnected(getActivity())) {
                //----------------------Get Lookup Parameter----------------------
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(1971, 1, 1, 1, 1, 1);
                Date date = cal.getTime();

                String url = GlobalData.getSharedGlobalData().getURL_SYNCPARAM_CONSTRAINT();
                List<HashMap<String, Object>> lookupArgs = new ArrayList<>();
                HashMap<String, Object> forms = new HashMap<>();
                forms.put(LookupDao.Properties.Lov_group.name, lov_group);
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

                if (!lookupArgs.isEmpty()) {
                    request.setList(lookupArgs);
                }
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                request.setTableName("MS_LOV");
                request.setDtm_upd(date);


                String jsonRequest = GsonHelper.toJson(request);
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
                HttpConnectionResult serverResult = null;

                //Firebase Performance Trace HTTP Request
                HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, jsonRequest);

                try {
                    serverResult = httpConn.requestToServer(url, jsonRequest, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverResult);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                if (serverResult != null && serverResult.isOK()) {
                    String body = serverResult.getResult();
                    try {
                        SynchronizeResponseLookup entityLookup = GsonHelper.fromJson(body, SynchronizeResponseLookup.class);
                        List<Lookup> entitiesLookup = entityLookup.getListSync();
                        if (entitiesLookup != null && !entitiesLookup.isEmpty()) {
                            LookupDataAccess.addOrUpdateAll(getActivity(), entitiesLookup);
                        } else {
                            errMessage = getString(R.string.lookup_not_available, lov_group);
                        }
                    } catch (JsonSyntaxException e) {
                        ACRA.getErrorReporter().putCustomData("errorGetLookupOnDemand", e.getMessage());
                        ACRA.getErrorReporter()
                                .putCustomData("errorGetLookupOnDemandDate", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Just for the stacktrace, kena exception saat Get Lookup on Demand"));
                        return false;
                    } catch (IllegalStateException e) {
                        ACRA.getErrorReporter().putCustomData("errorGetLookupOnDemand", e.getMessage());
                        ACRA.getErrorReporter()
                                .putCustomData("errorGetLookupOnDemandDate", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Just for the stacktrace, kena exception saat Get Lookup on Demand"));
                        return false;
                    }
                    return true;
                } else if (serverResult != null && !serverResult.isOK()) {
                    errMessage = String.valueOf(serverResult.getStatusCode());
                    return false;
                }
            } else {
                errMessage = getString(R.string.connection_failed).toString();
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    FireCrash.log(e);
                    if (Global.IS_DEV) {
                        e.printStackTrace();
                    }
                }
            }
            if (errMessage != null) {
                Toast.makeText(getActivity(), errMessage, Toast.LENGTH_LONG).show();
                needStop = true;
            } else {
                if (Boolean.FALSE.equals(result)) {
                    String message = getString(R.string.get_lookup_failed, lov_group);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    needStop = true;
                } else {
                    List<OptionAnswerBean> options = getOptionsForQuestion(bean, false);
                    for (int i = 0; i < listOfQuestions.size(); i++) {
                        QuestionBean mBean = listOfQuestions.get(i);
                        if (mBean.getUuid_question_set().equals(bean.getUuid_question_set())) {
                            mBean.setOptionAnswers(options);
                            int groupIdx = listOfQuestionGroup.indexOf(mBean.getQuestion_group_name()) + 1;
                            int position = i + groupIdx;
                            changeItem(position);
                        }
                    }
                }
            }
        }
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
                        } else {
                            //if there's no answer, just hide the question
                            return false;
                        }

                    } else {
                        QuestionBean bean = Constant.getListOfQuestion().get(identifier);
                        if (bean != null) {
                            String flatAnswer = "";
                            if (bean.getIs_visible().equals(Global.FALSE_STRING)) {
                                flatAnswer = "false";
                            } else {
                                flatAnswer = QuestionBean.getAnswer(bean);
                            }
                            if (flatAnswer != null && flatAnswer.length() > 0) {
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
                            } else {
                                //if there's no answer, just hide the question
                                return false;
                            }
                        } else {
                            convertedExpression = convertedExpression.replace("{" + identifier + "}", "\"\"");
                        }
                    }
                } else {
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
    public boolean isQuestVisibleIfRelevantMandatory(String relevantExpression, QuestionBean question) {
        boolean result = false;
        String convertedExpression = relevantExpression;        //make a copy of
        if (convertedExpression == null || convertedExpression.length() == 0) {
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
                        } else {
                            //if there's no answer, just hide the question
                            return false;
                        }

                    } else {
                        QuestionBean bean = Constant.getListOfQuestion().get(identifier);
                        if (bean != null) {
                            String flatAnswer = "";
                            if (bean.getIs_visible().equals(Global.FALSE_STRING)) {
                                flatAnswer = "false";
                            } else {
                                flatAnswer = QuestionBean.getAnswer(bean);
                            }
                            if (flatAnswer != null && flatAnswer.length() > 0) {
                                //NOTE: though it's possible to just iterate on flatAnswer substrings, we prefer to stay on method if size is 1
                                String[] answers = Tool.split(flatAnswer, Global.DELIMETER_DATA);
                                if (answers.length == 1) {
                                    convertedExpression = convertedExpression.replace("{" + identifier + "}", answers[0]);
                                } else {
                                    //NOTE: going into in-depth loop, won't go outside of this 'else'
                                    for (int i = 0; i < answers.length; i++) {
                                        String convertedSubExpression = convertedExpression.replace("{" + identifier + "}", answers[i]);
                                        boolean isVisible = isQuestVisibleIfRelevantMandatory(convertedSubExpression, question);
                                        if (isVisible) {
                                            return true;
                                        }
                                    }
                                    return false;
                                }
                            } else {
                                //if there's no answer, just hide the question
                                return false;
                            }
                        } else {
                            convertedExpression = convertedExpression.replace("{" + identifier + "}", "\"\"");
                        }
                    }
                } else {
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

    private String[] parsePaymentResult(String result) {
        return result.split("\\|");
    }

    private class RejectOrderTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog progressDialog;
        private String errMessage;
        private String message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(), "", getString(R.string.progressWait));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String type = DynamicFormActivity.getHeader().getForm().getForm_type();
            if (type.equals("Personal") || type.equals("Company")) {
                if (Tool.isInternetconnected(getActivity())) {
                    ReviewTask task = new ReviewTask();
                    task.setUuidForm(DynamicFormActivity.getHeader().getUuid_scheme());
                    for (QuestionBean bean : listOfQuestions) {
                        if (bean.getTag() != null) {
                            if (type.equals("Personal")) {
                                if (bean.getTag().equals(Global.CHECK_REJECT_NAME)) {
                                    task.setCustomerName(bean.getAnswer());
                                } else if (bean.getTag().equals(Global.CHECK_REJECT_NIK)) {
                                    task.setNik(bean.getAnswer());
                                } else if (bean.getTag().equals(Global.CHECK_REJECT_NOHP)) {
                                    task.setPhone(bean.getAnswer());
                                }
                            } else {
                                if (bean.getTag().equals(Global.CHECK_REJECT_COMPANY_NAME)) {
                                    task.setCompanyName(bean.getAnswer());
                                } else if (bean.getTag().equals(Global.CHECK_REJECT_NPWP)) {
                                    task.setNpwp(bean.getAnswer());
                                } else if (bean.getTag().equals(Global.CHECK_REJECT_NOHP)) {
                                    task.setPhone(bean.getAnswer());
                                }
                            }
                        }
                    }

                    CheckRejectedOrderRequest request = new CheckRejectedOrderRequest();
                    request.setReviewTask(task);
                    request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    request.addImeiAndroidIdToUnstructured();

                    String json = GsonHelper.toJson(request);
                    String url = GlobalData.getSharedGlobalData().getURL_CHECK_ORDER_REJECTED();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
                    HttpConnectionResult serverResult = null;

                    try {
                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (serverResult != null && serverResult.isOK()) {
                        try {
                            String responseBody = serverResult.getResult();
                            CheckRejectedOrderResponse response = GsonHelper.fromJson(responseBody, CheckRejectedOrderResponse.class);

                            if (response.getStatus().getCode() != 1) {
                                message = response.getStatus().getMessage();
                                return false;
                            } else {
                                return true;
                            }
                        } catch (Exception e) {
                            if (Global.IS_DEV) {
                                e.printStackTrace();
                                errMessage = e.getMessage();
                            }
                        }
                    } else {
                        errMessage = getString(R.string.server_down);
                    }

                } else {
                    errMessage = getString(R.string.no_internet_connection);
                }
            } else {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();

            if (Boolean.TRUE.equals(aBoolean)) {
                showFinishScreen();
            } else {
                if (errMessage != null) {
                    Toast.makeText(getContext(), errMessage, Toast.LENGTH_SHORT).show();
                } else {
                    getActivity().findViewById(R.id.btnNext).setClickable(false);
                    getActivity().findViewById(R.id.btnSend).setClickable(false);
                    DialogManager.showTaskRejected(getActivity(), message);
                }
            }
        }
    }
}