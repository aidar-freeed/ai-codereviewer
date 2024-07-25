package com.adins.mss.base.todolist.form;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.Helper;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskHSequence;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHSequenceDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllHeaderViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllHeaderViewerFragment extends Fragment implements AdapterView.OnItemClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String REQ_PRIORITY_LIST = "REQ_PRIORITY_LIST";
    public static final String REQ_LOG_LIST = "REQ_LOG_LIST";
    public static final String REQ_STATUS_LIST = "REQ_STATUS_LIST";
    public static final String BUND_KEY_REQ = "BUND_KEY_REQ";

    private ListView listView;
    private LinearLayout layoutView;
    private TaskHeaderAdapter listAdapter;


    private String keyRequest;

    public AllHeaderViewerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param keyRequest Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    public static AllHeaderViewerFragment newInstance(String keyRequest) {
        AllHeaderViewerFragment fragment = new AllHeaderViewerFragment();
        Bundle args = new Bundle();
        args.putString(BUND_KEY_REQ, keyRequest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyRequest = getArguments().getString(BUND_KEY_REQ);
        }
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.all_header_viewer_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(android.R.id.list);
        layoutView = (LinearLayout) view.findViewById(R.id.layoutView);
        loadListView();
    }

    public void loadListView() {
        List<TaskH> taskHList = null;
        try {
            if (keyRequest != null) {
                if (REQ_PRIORITY_LIST.equals(keyRequest))
                    taskHList = ToDoList.getListTaskInPriority(getActivity(), 0, null);
                else if (REQ_STATUS_LIST.equals(keyRequest))
                    taskHList = TaskHDataAccess.getAllTaskInStatus(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
                else if (REQ_LOG_LIST.equals(keyRequest))
                    taskHList = TaskHDataAccess.getAllSentTask(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
                else
                    taskHList = new ArrayList<>();
            } else {
                taskHList = new ArrayList<>();
            }
            List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(getContext());
            List<TaskH> taskHLists = new ArrayList<>();
            if (taskHSequences.isEmpty()) {
                TaskHSequenceDataAccess.insertAllNewTaskHSeq(getContext(), taskHList);
                taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(getContext());

            }
            for (int i = 0; i < taskHSequences.size(); i++) {
                taskHLists.add(taskHSequences.get(i).getTaskH());
            }
            taskHList = taskHLists;
            listAdapter = new TaskHeaderAdapter(getActivity(), taskHList);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(this);
            if (taskHList == null || taskHList.isEmpty()) {
                layoutView.setBackgroundResource(R.drawable.bg_notfound);
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorGetListTaskH", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorGetListTaskH", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Get List TaskH"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.title_mn_tasklist));
        loadListView();
        DialogManager.showTimeProviderAlert(getActivity());
        if (Helper.isDevEnabled(getActivity()) && GlobalData.getSharedGlobalData().isDevEnabled() && !GlobalData.getSharedGlobalData().isByPassDeveloper()) {
            DialogManager.showTurnOffDevMode(getActivity());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            TaskH taskH = (TaskH) parent.getAdapter().getItem(position);
            Scheme scheme = taskH.getScheme();
            if (scheme == null && taskH.getUuid_scheme() != null) {
                scheme = SchemeDataAccess.getOne(getActivity(),
                        taskH.getUuid_scheme());
                if (scheme != null)
                    taskH.setScheme(scheme);
            }

            if (scheme == null) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.task_cant_seen),
                        Toast.LENGTH_SHORT).show();
            } else {
                SurveyHeaderBean header = new SurveyHeaderBean(taskH);
                CustomerFragment fragment = CustomerFragment.create(header);
                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorGetScheme", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorGetSchemeTime", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Get Scheme"));
            String message = getActivity().getString(R.string.task_cant_seen2) + " " + e.getMessage();
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    public class TaskHeaderAdapter extends ArrayAdapter<TaskH> {
        public List<TaskH> listTaskH2;
        private Context mContext;
        private List<TaskH> listTaskH;

        public TaskHeaderAdapter(Context c, List<TaskH> listTaskH) {
            super(c, R.layout.all_header_viewer_item, listTaskH);
            mContext = c;
            this.listTaskH = listTaskH;
            this.listTaskH2 = listTaskH;
        }

        @Override
        public int getCount() {
            return listTaskH.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v;
            v = inflater.inflate(R.layout.all_header_viewer_item, null);
            TextView txtPriority = (TextView) v.findViewById(R.id.txtPriority);
            TextView txtAgreementNumber = (TextView) v.findViewById(R.id.txtAgreementNumber);
            TextView txtCustName = (TextView) v.findViewById(R.id.txtCustName);
            TextView txtCustAddress = (TextView) v.findViewById(R.id.txtCustAddress);
            TextView txtCustPhone = (TextView) v.findViewById(R.id.txtCustPhone);
            TextView txtCustJobAddress = (TextView) v.findViewById(R.id.tvJobAddress);
            TableRow tableRowSurveyLocation = (TableRow) v.findViewById(R.id.tableRowSurveyLocation);
            TextView txtSurveyLocation = (TextView) v.findViewById(R.id.txtSurveyLocation);
            TextView txtNotes = (TextView) v.findViewById(R.id.txtNotes);
            TextView txtLicensePlate = (TextView) v.findViewById(R.id.txtLicensePlate);
            TableRow tableRowChassisNumber = (TableRow) v.findViewById(R.id.tableRow7);
            TableRow tableRowEngineNumber = (TableRow) v.findViewById(R.id.tableRow8);
            TextView tvChassisNumber = (TextView) v.findViewById(R.id.txtChassisNumber);
            TextView tvEngineNumber = (TextView) v.findViewById(R.id.txtEngineNumber);
            TableRow tableRowCustJobAddress = (TableRow) v.findViewById(R.id.tableCustJobAddress);
            TableRow tableRowChassisEngineNumber = (TableRow) v.findViewById(R.id.tableRowChassisEngineNumber);
            TextView txtChassisEngineNumber = (TextView) v.findViewById(R.id.txtChassisEngineNumber);
            final TaskH taskH = listTaskH.get(position);

            final List<TaskD> taskD = taskH.getTaskDList();
            final List<QuestionSet> questionSetList = QuestionSetDataAccess.getAll(getContext(), taskH.getUuid_scheme());
            String custJobAddress = null;
            for (int i = 0; i < questionSetList.size(); i++) {
                if (null != questionSetList.get(i).getTag() && questionSetList.get(i).getTag().equals(Global.TAG_CUSTOMER_JOB_ADDRESS)) {
                    for (int j = 0; j < taskD.size(); j++) {
                        if (taskD.get(j).getQuestion_label().equals(questionSetList.get(i).getQuestion_label())) {
                            custJobAddress = taskD.get(j).getText_answer();
                            if (null != custJobAddress) {
                                txtCustJobAddress.setText(custJobAddress);
                            }
                            break;
                        }
                    }
                    break;
                }
            }

            txtPriority.setText(taskH.getPriority());
            try {
                if (taskH.getAppl_no() != null) {
                    txtAgreementNumber.setText(taskH.getAppl_no());
                }
            } catch (NullPointerException e) {
                txtAgreementNumber.setText("-");
            }
            txtCustName.setText(taskH.getCustomer_name());
            txtCustAddress.setText(taskH.getCustomer_address());
            txtCustPhone.setText(taskH.getCustomer_phone());
            if ((null != taskH.getSurvey_location() && !"null".equals(taskH.getSurvey_location()))) {
                SpannableString spannableString = new SpannableString(taskH.getSurvey_location());
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse("https://maps.google.com/maps?q="
                                + taskH.getSurvey_location()));
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
            txtNotes.setText(taskH.getNotes());
            txtLicensePlate.setText(taskH.getNo_plat());

            if (GlobalData.getSharedGlobalData().getUser().getPiloting_branch().equalsIgnoreCase("1")) {
                tableRowSurveyLocation.setVisibility(View.VISIBLE);
                tableRowChassisNumber.setVisibility(View.GONE);
                tableRowEngineNumber.setVisibility(View.GONE);
                tableRowChassisEngineNumber.setVisibility(View.VISIBLE);
                try {
                    if (null != taskH.getNo_rangka() && null != taskH.getNo_mesin()) {
                        txtChassisEngineNumber.setText(taskH.getNo_rangka() + "/" + taskH.getNo_mesin());
                    } else if (null == taskH.getNo_rangka() && null == taskH.getNo_mesin()) {
                        txtChassisEngineNumber.setText("-");
                    } else if (null == taskH.getNo_rangka()) {
                        txtChassisEngineNumber.setText("-" + "/" + taskH.getNo_mesin());
                    } else {
                        txtChassisEngineNumber.setText(taskH.getNo_rangka() + "/" + "-");
                    }
                } catch (NullPointerException e) {
                    txtChassisEngineNumber.setText("-");
                }
            } else {
                tableRowSurveyLocation.setVisibility(View.GONE);
                tableRowCustJobAddress.setVisibility(View.GONE);
                tableRowChassisNumber.setVisibility(View.VISIBLE);
                tableRowEngineNumber.setVisibility(View.VISIBLE);
                tableRowChassisEngineNumber.setVisibility(View.GONE);
                try {
                    if (null != taskH.getNo_rangka()) {
                        tvChassisNumber.setText(taskH.getNo_rangka());
                    } else {
                        tvChassisNumber.setText("-");
                    }
                    if (null != taskH.getNo_mesin()) {
                        tvEngineNumber.setText(taskH.getNo_mesin());
                    } else {
                        tvEngineNumber.setText("-");
                    }
                } catch (NullPointerException e) {
                    tvChassisNumber.setText("-");
                    tvEngineNumber.setText("-");
                }
            }

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (GlobalData.getSharedGlobalData().getUser().getPiloting_branch().equalsIgnoreCase("1")) {
                        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        String priority = taskH.getPriority() != null ? taskH.getPriority() : "-";
                        String agrNum = taskH.getAppl_no() != null ? taskH.getAppl_no() : "-";
                        String custName = taskH.getCustomer_name() != null ? taskH.getCustomer_name() : "-";
                        String custAddr = taskH.getCustomer_address() != null ? taskH.getCustomer_address() : "-";
                        String custPhone = taskH.getCustomer_phone() != null ? taskH.getCustomer_phone() : "-";

                        String custJobAddress = "-";
                        for (int i = 0; i < questionSetList.size(); i++) {
                            if (null != questionSetList.get(i).getTag() && questionSetList.get(i).getTag().equals(Global.TAG_CUSTOMER_JOB_ADDRESS)) {
                                for (int j = 0; j < taskD.size(); j++) {
                                    if (taskD.get(j).getQuestion_label().equals(questionSetList.get(i).getQuestion_label())) {
                                        custJobAddress = taskD.get(j).getText_answer();
                                        if (null != custJobAddress) {
                                            custJobAddress = taskD.get(j).getText_answer();
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                        String surveyLocation = (null != taskH.getSurvey_location() && !"null".equals(taskH.getSurvey_location()))
                                ? taskH.getSurvey_location() : "-";
                        String notes = taskH.getNotes() != null ? taskH.getNotes() : "-";
                        String noPlat = taskH.getNo_plat() != null ? taskH.getNo_plat() : "-";
                        String noRangkaMesin;
                        if (null != taskH.getNo_rangka() && null != taskH.getNo_mesin()) {
                            noRangkaMesin = taskH.getNo_rangka() + "/" + taskH.getNo_mesin();
                        } else if (null == taskH.getNo_rangka() && null == taskH.getNo_mesin()) {
                            noRangkaMesin = "-";
                        } else if (null == taskH.getNo_rangka()) {
                            noRangkaMesin = "-/" + taskH.getNo_mesin();
                        } else {
                            noRangkaMesin = taskH.getNo_rangka() + "/-";
                        }

                        //michael.wijaya 15/02/23 - do not fix the weird white spaces. this is to fix copy paste uneven spacing
                        ClipData clip = ClipData.newPlainText("label",
                                "Priority                               : " + priority + "\n" +
                                        "Agreement Number         : " + agrNum + "\n" +
                                        "Customer Name               : " + custName + "\n" +
                                        "Customer Address           : " + custAddr + "\n" +
                                        "Customer Phone              : " + custPhone + "\n" +
                                        "Customer Job Address   : " + custJobAddress + "\n" +
                                        "Survey Location               : " + surveyLocation + "\n" +
                                        "Notes                                 : " + notes + "\n" +
                                        "No Plat                               : " + noPlat + "\n" +
                                        "No Rangka/No Mesin       : " + noRangkaMesin + "\n");
                        if (clipboardManager == null || clip == null) return false;
                        clipboardManager.setPrimaryClip(clip);
                        Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });

            return v;
        }
    }
}
