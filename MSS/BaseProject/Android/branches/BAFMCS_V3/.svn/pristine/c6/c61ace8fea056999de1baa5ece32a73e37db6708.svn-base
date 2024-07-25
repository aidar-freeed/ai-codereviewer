package com.adins.mss.coll.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.NewMCMainActivity;
import com.adins.mss.coll.R;
import com.adins.mss.coll.commons.Toaster;
import com.adins.mss.coll.tool.Constants;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.DepositReportHDataAccess;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.formatter.Tool;

import org.acra.ACRA;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DepositReportRecapitulateFragmentNew extends Fragment {

    private TextView formNameBatch;
    private TextView submitDateBatch;
    private FormAdapter formAdapter;
    private UserAdapter userAdapter;
    private BatchAdapter batchAdapter;
    public static String selectedDepositScheme;
    public static String selectedDepositSchemeName;
    public static String selectedDepositUser;
    public static String selectedBatchId;
    public static User selectedDepositUserObject = null;

    protected double total = 0;
    private int totalNeedPrint;
    private List<TaskH> listTaskH;
    private List<String> listTaskBatch;
    private ToDoList toDoList;
    private RefreshBackgroundTask backgroundTask;
    List<TaskD> reportsReconcile = new ArrayList<TaskD>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        toDoList = new ToDoList(getActivity());
        listTaskH = toDoList.getListTaskInStatusForMultiUser(ToDoList.SEARCH_BY_BATCH_ID, "BATCHID");

        selectedDepositScheme = null;
        return inflater.inflate(R.layout.fragment_deposit_report_recapitulate_new, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());

        List<Scheme> formListName = new ArrayList<>();
        formListName.clear();
        formListName.addAll(SchemeDataAccess.getAllActivePriorityScheme(getActivity()));

        List<TaskH> batchIdList = new ArrayList<>();
        batchIdList.clear();
        List<TaskH> listTask = new ArrayList<>();
        listTaskBatch = getListBatchId();
        if (listTaskBatch != null && listTaskBatch.size() > 0) {
            for (String batch : listTaskBatch) {
                if (batch != null) {
                    TaskH taskHBatch = TaskHDataAccess.getAllHeader(getActivity(), batch);
                    listTask.add(taskHBatch);
                }
            }
        }
        batchIdList.addAll(listTask);

        List<User> userListName = new ArrayList<>();
        userListName.clear();
        userListName.addAll(UserDataAccess.getAllUserActive(getActivity()));

        AppCompatSpinner spinnerForm = (AppCompatSpinner) view.findViewById(R.id.priorityViewByForm);
        formAdapter = new FormAdapter(getActivity(), R.layout.spinner_style, R.id.text_spin, formListName);
        formAdapter.setDropDownViewResource(R.layout.spinner_style);

        AppCompatSpinner spinnerUser = (AppCompatSpinner) view.findViewById(R.id.priorityViewByUser);
        userAdapter = new UserAdapter(getActivity(), R.layout.spinner_style, R.id.text_spin, userListName);
        userAdapter.setDropDownViewResource(R.layout.spinner_style);

        spinnerForm.setVisibility(View.GONE);
        spinnerUser.setVisibility(View.GONE);

        ImageView imgExpandForm = (ImageView) view.findViewById(R.id.img_expand_by_form);
        ImageView imgExpandUser = (ImageView) view.findViewById(R.id.img_expand_by_user);
        imgExpandForm.setVisibility(View.GONE);
        imgExpandUser.setVisibility(View.GONE);

        AppCompatSpinner spinnerBatch = (AppCompatSpinner) view.findViewById(R.id.priorityViewByBatch);
        batchAdapter = new BatchAdapter(getActivity(), R.layout.spinner_style, R.id.text_spin, batchIdList);
        batchAdapter.setDropDownViewResource(R.layout.spinner_style);

        spinnerForm.setAdapter(formAdapter);
        spinnerForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepositScheme = formAdapter.getItem(position).getUuid_scheme();
                selectedDepositSchemeName = formAdapter.getItem(position).getScheme_description();
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerUser.setAdapter(userAdapter);
        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepositUser = userAdapter.getItem(position).getUuid_user();
                selectedDepositUserObject = userAdapter.getItem(position);
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerBatch.setAdapter(batchAdapter);
        spinnerBatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBatchId = batchAdapter.getItem(position).getBatch_id();
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Button transferButton = (Button) view.findViewById(R.id.transferButton);
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total = sumOfItems(reportsReconcile);
                if (total != 0) {
                    transferButton.setEnabled(false);
                    transfer();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.transfer_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //make sure that first selected user is current logged user
        User defaultUser = GlobalData.getSharedGlobalData().getUser();
        int position = -1;
        for (User user : userListName) {
            position++;
            if (user.getUuid_user().equalsIgnoreCase(defaultUser.getUuid_user())) {
                if (selectedDepositUserObject == null) {
                    selectedDepositUser = userListName.get(position).getUuid_user();
                    selectedDepositUserObject = userListName.get(position);
                }
                spinnerUser.setSelection(position);
                break;
            }
        }

        formNameBatch = (TextView) getView().findViewById(R.id.formBatchValue);
        submitDateBatch = (TextView) getView().findViewById(R.id.dateBatchValue);

        initialize();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    private void initialize() {
        loadData();
    }

    private void loadData() {
        reportsReconcile.clear();
        List<TaskD> reports = TaskDDataAccess.getTaskDTagTotalbyBatchId(getActivity(), selectedBatchId);


        for (TaskD taskD : reports) {
            TaskH taskH = TaskHDataAccess.getOneHeader(getActivity(), taskD.getUuid_task_h());
            selectedDepositScheme = taskH.getUuid_scheme();
            selectedDepositSchemeName = SchemeDataAccess.getOneSchemeName(getActivity(), selectedDepositScheme);
            if (taskH != null && taskH.getIs_reconciled() != null) {
                if (taskH.getIs_reconciled().equals("0")) {
                    reportsReconcile.add(taskD);
                }
            }

            if (taskH != null) {
                int printCount = taskH.getPrint_count() != null ? taskH.getPrint_count() : 0;
                String rvNumber = taskH.getRv_number();
                boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
                if (printCount > 0 || (rvNumber != null && !rvNumber.isEmpty()) || isRVinFront) {
                    // do nothing
                } else {
                    try {
                        String uuidScheme = taskH.getUuid_scheme();
                        Scheme scheme = SchemeDataAccess.getOne(getActivity(), uuidScheme);
                        if (scheme != null) {
                            if (scheme.getIs_printable().equals(Global.TRUE_STRING))
                                totalNeedPrint++;
                        }
                    } catch (Exception e) {
                        totalNeedPrint++;
                    }
                }
                formNameBatch.setText(selectedDepositSchemeName);
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                submitDateBatch.setText(df.format(taskH.getSubmit_date()));
            }
        }
        ListView list = (ListView) getView().findViewById(R.id.recapitulationList);
        list.setAdapter(new RecapitulationListAdapter(
                        getActivity(),
                        R.layout.item_recapitulation_detail,
                        reportsReconcile.toArray(new TaskD[reportsReconcile.size()])
                )
        );

    }

    private List<String> getListBatchId() {
        List<String> depositedBatchList = new ArrayList<>();
        List<DepositReportH> depositedBatch = DepositReportHDataAccess.listOfBacth(getActivity());
        if (depositedBatch != null) {
            for (DepositReportH depositHeader : depositedBatch) {
                depositedBatchList.add(depositHeader.getBatch_id());
            }
        }
        List<String> undeposited = TaskHDataAccess.getAllBatchIdList(getActivity(), depositedBatchList);
        return undeposited;
    }


    void transfer() {
        ListView list = (ListView) getView().findViewById(R.id.recapitulationList);
        if (list.getAdapter().getCount() <= 1) {
            Toaster.warning(getActivity(), getString(R.string.nothing_to_report));
            return;
        } else if (totalNeedPrint > 0) {
            Toaster.warning(getActivity(), getActivity().getString(R.string.prompt_printRV));
            return;
        }
        BigDecimal totalValue = BigDecimal.valueOf(total);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_BUND_BATCHID, selectedBatchId);
        bundle.putString("TOTAL_DEPOSIT", totalValue.toString());
        bundle.putString("FORM", selectedDepositSchemeName);
        DepositReportTransferFragmentNew fragment = new DepositReportTransferFragmentNew();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = NewMCMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private class RecapitulationListAdapter extends ArrayAdapter<TaskD> {

        private final TaskD[] originalItems;

        public RecapitulationListAdapter(Context context, int resource, TaskD[] objects) {
            super(context, resource, objects);
            originalItems = objects;
        }

        @Override
        public int getCount() {
            return super.getCount() + 1;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_recapitulation_detail, parent, false);

            TextView label = (TextView) view.findViewById(R.id.itemLabel);
            TextView value = (TextView) view.findViewById(R.id.itemValue);

            if (position == getCount() - 1) {
                label.setText("Total");
                value.setText(Tool.separateThousand(String.valueOf(sumOfItems(new ArrayList<TaskD>(Arrays.asList(originalItems))))));
                value.setText(Tool.separateThousand(String.valueOf(sumOfItems(new ArrayList<TaskD>(Arrays.asList(originalItems))))));
            } else {
                TaskD item = getItem(position);
                label.setText(item.getTaskH().getAppl_no());
                value.setText(Tool.separateThousand(item.getText_answer()));
            }

            return view;
        }
    }

    private double sumOfItems(List<TaskD> items) {
        double sum = 0;
        try {
            for (TaskD item : items) {
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
            e.printStackTrace();
        }
        return sum;
    }

    public class FormAdapter extends ArrayAdapter<Scheme> {
        private Context context;
        private List<Scheme> values;

        public FormAdapter(Context context, int resource, int textViewResourceId, List<Scheme> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context = context;
            this.values = objects;
        }

        public int getCount() {
            return values.size();
        }

        public Scheme getItem(int position) {
            return values.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText("Form : " + values.get(position).getScheme_description());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getScheme_description());
            return label;
        }
    }

    public class BatchAdapter extends ArrayAdapter<TaskH> {
        private Context context;
        private List<TaskH> values;

        public BatchAdapter(Context context, int resource, int textViewResourceId, List<TaskH> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context = context;
            this.values = objects;
        }

        public int getCount() {
            return values.size();
        }

        public TaskH getItem(int position) {
            return values.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText("Batch : " + values.get(position).getBatch_id());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getBatch_id());
            return label;
        }
    }

    public class UserAdapter extends ArrayAdapter<User> {
        private Context context;
        private List<User> values;

        public UserAdapter(Context context, int resource, int textViewResourceId, List<User> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context = context;
            this.values = objects;
        }

        public int getCount() {
            return values.size();
        }

        public User getItem(int position) {
            return values.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText("User : " + values.get(position).getLogin_id());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getLogin_id());
            return label;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listTaskH != null) {
            initiateRefresh();
        }
    }

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
            listTaskH.addAll(toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_BATCH_ID, "BATCHID"));
            ToDoList.listOfSurveyStatus = null;
            List<SurveyHeaderBean> list = new ArrayList<SurveyHeaderBean>();
            for (TaskH h : listTaskH) {
                list.add(new SurveyHeaderBean(h));
            }
            ToDoList.listOfSurveyStatus = list;

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

}
