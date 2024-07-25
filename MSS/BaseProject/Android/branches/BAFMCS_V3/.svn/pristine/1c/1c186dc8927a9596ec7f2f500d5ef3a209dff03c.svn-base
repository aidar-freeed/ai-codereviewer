package com.adins.mss.base.dynamicform.form.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.ViewImpl;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.FormBean;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.dynamicform.form.DynamicQuestionActivity;
import com.adins.mss.base.dynamicform.form.FragmentQuestion;
import com.adins.mss.base.todo.Task;
import com.adins.mss.base.util.CustomAnimatorLayout;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by kusnendi.muhamad on 08/08/2017.
 */

public class DynamicQuestionView extends ViewImpl {
    public static FragmentManager fragmentManager;
    public ArrayAdapter<String> adapter;
    public static ProgressDialog progressDialog;
    protected static ArrayList<String> questionLabel = new ArrayList<>();
    public static SurveyHeaderBean header;
    public int mode;
    private ImageButton btnNext;
    private ImageButton btnSave;
    private ImageButton btnSend;
    private ImageButton btnVerified;
    private ImageButton btnReject;
    private ImageButton btnApprove;
    private ImageButton btnClose;
    private ImageButton btnSearch;
    private ToggleButton btnSearchBar;
    private AutoCompleteTextView txtSearch;
    //task abstract class
    private Task task;
    private boolean isSimulasi = false;
    private RelativeLayout searchContainer;
    private User user;

    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(Global.BACKPRESS_RESTRICTION) return;
            if (id == R.id.btnNext) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.NEXT_QUESTION);
                    if (isAutoSave())
                        bundle.putInt(Global.BUND_KEY_MODE_SURVEY, mode);
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
            } else if (id == R.id.btnSave) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Global.BUND_KEY_MODE_SURVEY, mode);
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.SAVE_QUESTION);
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
            } else if (id == R.id.btnSend) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.SEND_QUESTION);
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
            } else if (id == R.id.btnSearchBar) {
                    adapter = new ArrayAdapter<>(activity, R.layout.autotext_list, R.id.textauto, DynamicQuestionActivity.getQuestionLabel());
                    refreshAdapter();
                    adapter.notifyDataSetChanged();
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
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString(FragmentQuestion.BUND_KEY_SEARCH_ACTION, searchKey);
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.SEARCH_QUESTION);
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
            } else if (id == R.id.btnVerified) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.VERIFY_QUESTION);
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
            } else if (id == R.id.btnApprove) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.APPROVE_QUESTION);
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
            } else if (id == R.id.btnReject) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.REJECT_QUESTION);
                    message.setData(bundle);
                    FragmentQuestion.questionHandler.sendMessage(message);
            } else if (id == R.id.btnClose) {
                    GlobalData.getSharedGlobalData().setDoingTask(false);
                    activity.finish();
            }
        }
    };

    public DynamicQuestionView(Activity activity) {
        super(activity);
        adapter = new ArrayAdapter<>(activity, R.layout.autotext_list, R.id.textauto, DynamicQuestionActivity.getQuestionLabel());
    }

    public static void dismissProgressBar() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (DynamicQuestionActivity.progressDialog != null && DynamicQuestionActivity.progressDialog.isShowing()) {
                    try {
                        DynamicQuestionActivity.progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV)
                            e.printStackTrace();
                    }
                }
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initialize();
        initScreenLayout();
        questionLabel.clear();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                List<Scheme> schemes = SchemeDataAccess.getAll(activity.getApplicationContext());
                Global.getSharedGlobal().setTempSchemeVersion(new HashMap<String, Integer>());

                for (Scheme scheme : schemes) {
                    Global.getSharedGlobal().getTempSchemeVersion().put(scheme.getUuid_scheme(), Integer.valueOf(scheme.getForm_version()));
                }

                Global.getSharedGlobal().setSchemeIsChange(true);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                gotoQuestionFragment();
            }
        });
    }

    private void initialize() {
        Bundle extras = activity.getIntent().getExtras();
        mode = extras.getInt(Global.BUND_KEY_MODE_SURVEY);
        user = GlobalData.getSharedGlobalData().getUser();

        // Nendi: 18.12.2019 | Patch Task Header tertukar karena static property
        DynamicFormActivity.setHeader(GsonHelper.fromJson(extras.getString(Global.BUND_KEY_SURVEY_BEAN), SurveyHeaderBean.class));
        DynamicFormActivity.getHeader().setForm(GsonHelper.fromJson(extras.getString(Global.BUND_KEY_FORM_BEAN), FormBean.class));
        header = DynamicFormActivity.getHeader();
        
        task = (Task) extras.getSerializable(Global.BUND_KEY_TASK);
        isSimulasi = extras.getBoolean(Global.BUND_KEY_MODE_SIMULASI, false);

        try {
            if ((header.getPriority() != null && header.getPriority().length() > 0) && !header.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_SENT)) {
                header.setStart_date(Tool.getSystemDateTime());
                new CustomerFragment.SendOpenReadTaskH(activity, header).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {             FireCrash.log(e);
            if (Global.IS_DEV)
                e.printStackTrace();
            String[] msg = {"Failed open questions,\nplease try again"};
            String alert = Tool.implode(msg, "\n");
            Toast.makeText(activity, alert, Toast.LENGTH_SHORT).show();
        }
    }

    private void initScreenLayout() {
        btnNext = (ImageButton) activity.findViewById(R.id.btnNext);
        btnSend = (ImageButton) activity.findViewById(R.id.btnSend);
        btnSave = (ImageButton) activity.findViewById(R.id.btnSave);
        btnSearch = (ImageButton) activity.findViewById(R.id.btnSearch);
        btnVerified = (ImageButton) activity.findViewById(R.id.btnVerified);
        btnReject = (ImageButton) activity.findViewById(R.id.btnReject);
        btnApprove = (ImageButton) activity.findViewById(R.id.btnApprove);
        btnClose = (ImageButton) activity.findViewById(R.id.btnClose);
        btnSearchBar = (ToggleButton) activity.findViewById(R.id.btnSearchBar);

        btnNext.setOnClickListener(clickListener);
        btnVerified.setOnClickListener(clickListener);
        btnSend.setOnClickListener(clickListener);
        btnSave.setOnClickListener(clickListener);
        btnSearch.setOnClickListener(clickListener);
        btnReject.setOnClickListener(clickListener);
        btnApprove.setOnClickListener(clickListener);
        btnSearchBar.setOnClickListener(clickListener);
        btnClose.setOnClickListener(clickListener);

        adapter = new ArrayAdapter<>(activity, R.layout.autotext_list, R.id.textauto, DynamicQuestionActivity.getQuestionLabel());
        txtSearch = (AutoCompleteTextView) activity.findViewById(R.id.autoCompleteSearch);
        txtSearch.setAdapter(adapter);
        txtSearch.setDropDownBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.dropdown_background));
        txtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocused) {

                if (hasFocused) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        LinearLayout sendLayout = (LinearLayout) activity.findViewById(R.id.btnSendLayout);
        LinearLayout verifyLayout = (LinearLayout) activity.findViewById(R.id.btnVerifiedLayout);
        LinearLayout rejectLayout = (LinearLayout) activity.findViewById(R.id.btnRejectLayout);
        LinearLayout approveLayout = (LinearLayout) activity.findViewById(R.id.btnApproveLayout);
        LinearLayout nextLayout = (LinearLayout) activity.findViewById(R.id.btnNextLayout);
        LinearLayout saveLayout = (LinearLayout) activity.findViewById(R.id.btnSaveLayout);
        LinearLayout searchLayout = (LinearLayout) activity.findViewById(R.id.btnSearchLayout);
        LinearLayout closeLayout = (LinearLayout) activity.findViewById(R.id.btnCloseLayout);
        searchContainer = (RelativeLayout) activity.findViewById(R.id.searchLayout);
        searchContainer.setVisibility(View.GONE);
        try {
            if (TaskHDataAccess.STATUS_TASK_VERIFICATION.equalsIgnoreCase(header.getStatus()) ||
                    TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD.equalsIgnoreCase(header.getStatus()) ||
                    user.getFlag_job().equalsIgnoreCase("JOB MH")) {
                sendLayout.setVisibility(View.GONE);
                saveLayout.setVisibility(View.VISIBLE);
                approveLayout.setVisibility(View.GONE);
                //ganti ke halaman baru
                if (!Global.NEW_FEATURE) {
                    rejectLayout.setVisibility(View.VISIBLE);
                    verifyLayout.setVisibility(View.VISIBLE);
                }
            }
            if (TaskHDataAccess.STATUS_TASK_APPROVAL.equalsIgnoreCase(header.getStatus()) ||
                    TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD.equalsIgnoreCase(header.getStatus())) {
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
        } catch (Exception e) {             FireCrash.log(e);

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
    }

    private void gotoQuestionFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment1 = new FragmentQuestion();
        Bundle bundle = new Bundle();
        bundle.putInt(Global.BUND_KEY_MODE_SURVEY, mode);
        bundle.putBoolean(Global.BUND_KEY_MODE_SIMULASI, isSimulasi);
        fragment1.setArguments(bundle);
        transaction.replace(R.id.mainContainer, fragment1);
        transaction.commitAllowingStateLoss();
    }

    public void showProgressBar(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DynamicQuestionActivity.progressDialog = ProgressDialog.show(activity, "", message, false);
            }
        });
    }

    private void refreshAdapter() {
        // 2017/10/05 | olivia : to prevent duplicate result on adapter
        try {
            List<String> list = DynamicQuestionActivity.getQuestionLabel();

            Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            set.addAll(list);
            list = new ArrayList<>(set);

            adapter.clear();

            for (int i = list.size(); i > 0; i--)
                adapter.add(list.get(i - 1));

        } catch (Exception e) {             FireCrash.log(e);
        }
        txtSearch.setAdapter(adapter);
    }

    public boolean isAutoSave(){
        GeneralParameter gpAutoSave = null;

        if (GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_ORDER)) {
            gpAutoSave = GeneralParameterDataAccess.getOne(activity.getApplicationContext(),
                    GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_MO_AUTO_SAVE);
        } else if (GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_SURVEY)) {
            gpAutoSave = GeneralParameterDataAccess.getOne(activity.getApplicationContext(),
                    GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_MS_AUTO_SAVE);
        } else if (GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_COLLECTION)) {
            gpAutoSave = GeneralParameterDataAccess.getOne(activity.getApplicationContext(),
                    GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_MC_AUTO_SAVE);
        }

        if(gpAutoSave == null || !gpAutoSave.getGs_value().equals("1"))
            return false;

        return true;
    }
}
