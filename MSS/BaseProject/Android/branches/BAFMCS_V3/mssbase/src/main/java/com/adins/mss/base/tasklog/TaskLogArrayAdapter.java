package com.adins.mss.base.tasklog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Formatter;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class TaskLogArrayAdapter extends ArrayAdapter<TaskH> {

    boolean isLog = false;
    private Context context;
    private List<TaskH> objects;

    public TaskLogArrayAdapter(Context context,
                               List<TaskH> objects) {
        super(context, R.layout.log_item_layout, objects);
        this.context = context;
        this.objects = objects;
    }

    public TaskLogArrayAdapter(Context context,
                               List<TaskH> objects, boolean isLog) {
        super(context, R.layout.new_log_item, objects);
        this.context = context;
        this.objects = objects;
        this.isLog = isLog;
    }

    @Override
    public int getCount() {
        if (objects != null)
            return objects.size();
        else
            return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.new_log_item, parent, false);
        }

        TextView txtName = (TextView) convertView.findViewById(R.id.taskName);
        TextView txtTime = (TextView) convertView.findViewById(R.id.txtTime);
        TextView txtScheme = (TextView) convertView.findViewById(R.id.taskForm);
        TextView txtCollResult = (TextView) convertView.findViewById(R.id.taskCollResult);
        ImageView logIcon = (ImageView) convertView.findViewById(R.id.logIcon);
        ImageView imgPrint = (ImageView) convertView.findViewById(R.id.imgPrint);

        TaskH task = objects.get(position);
        String taskId = task.getTask_id();
        String custName = task.getCustomer_name();
        String formId = "";
        if (task.getScheme() != null)
            formId = task.getScheme().getForm_id();
        Date dTime = task.getSubmit_date();
        String sTime = "";
        try {
            sTime = Formatter.formatDate(dTime, Global.DATE_TIME_STR_FORMAT);
        } catch (Exception e) {
            FireCrash.log(e);
            try {
                sTime = Formatter.formatDate(task.getDtm_crt(), Global.DATE_TIME_STR_FORMAT);
            } catch (Exception e2) {
                FireCrash.log(e);
            }
        }

        if (taskId != null) {
            if (taskId.contains("belum di-mapping")) {
                txtScheme.setText(taskId);
                txtTime.setVisibility(View.GONE);
            } else {
                txtScheme.setText(formId);
                txtTime.setVisibility(View.VISIBLE);
                txtTime.setText(sTime);
            }
        }

        // olivia : UPDATE - icon priority ditampilkan untuk log
        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
        if (isLog) {
            // olivia : tambahan menampilkan collection result di log
            TaskD taskdCollResult = TaskDDataAccess.getOneFromTaskDWithTag(context, task.getUuid_task_h(), Global.TAG_COLLECTION_RESULT);
            if (taskdCollResult != null) {
                Lookup lookup = LookupDataAccess.getOneByCode(context, taskdCollResult.getUuid_lookup(), taskdCollResult.getLov());
                if (lookup != null) {
                    TaskD taskdTotalBayar = TaskDDataAccess.getOneFromTaskDWithTag(context, task.getUuid_task_h(), Global.TAG_TOTAL);
                    TaskD tasdPTP = TaskDDataAccess.getOneFromTaskDWithTag(context, task.getUuid_task_h(), Global.TAG_PTP);
                    txtCollResult.setVisibility(View.VISIBLE);
                    if (taskdTotalBayar != null) {
                        txtCollResult.setText(lookup.getValue() + " - " + taskdTotalBayar.getText_answer());
                    } else if (tasdPTP != null) {
                        String ptpDate = "";
                        if (!tasdPTP.getText_answer().contains("/")) {
                            String format = Global.DATE_STR_FORMAT_GSON;
                            Date date = null;
                            try {
                                date = Formatter.parseDate(tasdPTP.getText_answer(), format);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            ptpDate = Formatter.formatDate(date, Global.DATE_STR_FORMAT);
                        } else {
                            ptpDate = tasdPTP.getText_answer();
                        }
                        txtCollResult.setText(lookup.getValue() + " - " + ptpDate);
                    } else {
                        txtCollResult.setText(lookup.getValue());
                    }
                } else {
                    txtCollResult.setVisibility(View.GONE);
                }
            } else {
                txtCollResult.setVisibility(View.GONE);
            }

            logIcon.setVisibility(View.VISIBLE);
            if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application) && (NewMainActivity.mnSurveyApproval != null || NewMainActivity.mnSurveyVerif != null
                    || NewMainActivity.mnVerifByBranch != null || NewMainActivity.mnApprovalByBranch != null)) {
                if (task.getIs_prepocessed() != null && task.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                    logIcon.setImageResource(R.drawable.task_verification);
                } else if (task.getIs_prepocessed() != null && task.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL)) {
                    logIcon.setImageResource(R.drawable.task_approval);
                }
            } else if (task.getPriority() != null) {
                if (task.getPriority().equalsIgnoreCase("HIGH")) {
                    logIcon.setImageResource(R.drawable.task_highpriority);
                } else if (task.getPriority().equalsIgnoreCase("NORMAL") || task.getPriority().equalsIgnoreCase("MEDIUM")) {
                    logIcon.setImageResource(R.drawable.task_normalpriority);
                } else {
                    logIcon.setImageResource(R.drawable.task_lowpriority);
                }
            } else
                logIcon.setImageResource(R.drawable.task_new);
        } else {
            logIcon.setVisibility(View.GONE);
            if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application) &&(TaskHDataAccess.STATUS_TASK_APPROVAL.equals(task.getStatus()) || TaskHDataAccess.STATUS_TASK_VERIFICATION.equals(task.getStatus()))) {
                logIcon.setVisibility(View.GONE);
            }

        }

        txtName.setText(custName);
        txtName.setSelected(true);

        Scheme scheme = SchemeDataAccess.getOne(getContext(), task.getUuid_scheme());

        // olivia : menampilkan icon print untuk task yang melakukan pembayaran namun belum diprint untuk collection
        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
            if (scheme.getIs_printable().equals("1")) {
                boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(getContext(), task.getUuid_user());
                if (!isRVinFront) {
                    if (!"".equals(task.getRv_number()) && task.getRv_number() != null)
                        imgPrint.setVisibility(View.GONE);
                    else {
                        if (task.getPrint_count() != 0 && task.getPrint_count() != null)
                            imgPrint.setVisibility(View.GONE);
                        else {
                            List<TaskD> taskDs = TaskDDataAccess.getAll(getContext(), task.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                            if (taskDs != null && !taskDs.isEmpty()) {
                                boolean isTaskPaid = TaskDDataAccess.isTaskPaid(getContext(), task.getUuid_user(), task.getUuid_task_h());
                                if (!isTaskPaid)
                                    imgPrint.setVisibility(View.GONE);
                                else
                                    imgPrint.setVisibility(View.VISIBLE);
                            } else
                                imgPrint.setVisibility(View.VISIBLE);
                        }
                    }
                } else
                    imgPrint.setVisibility(View.GONE);
            } else
                imgPrint.setVisibility(View.GONE);
        }

        return convertView;
    }

    public List<TaskH> getObjects() {
        return objects;
    }

    public void setObjects(List<TaskH> objectList) {
        this.objects.clear();
        if (objectList != null && !objectList.isEmpty()) {
            for (TaskH task : objectList) {
                this.objects.add(task);
            }
        }
    }
}
