package com.adins.mss.base.tasklog;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.todolist.form.OnTaskListClickListener;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.formatter.Formatter;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by olivia.dg on 3/16/2018.
 */

public class NewTaskLogAdapter extends RecyclerView.Adapter<NewTaskLogAdapter.TaskLogViewHolder> {

    private static boolean isLog = false;
    private static Context context;
    private List<TaskH> objects;
    private final OnTaskListClickListener mListener;

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public NewTaskLogAdapter(Context context, List<TaskH> objects, boolean isLog, OnTaskListClickListener listener) {
        this.context = context;
        this.objects = objects;
        this.isLog = isLog;
        mListener = listener;
    }

    static class TaskLogViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        TextView txtName;
        TextView txtTime;
        TextView txtScheme;
        TextView txtCollResult;
        ImageView logIcon;
        ImageView imgPrint;

        public TaskLogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            txtName = (TextView) itemView.findViewById(R.id.taskName);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtScheme = (TextView) itemView.findViewById(R.id.taskForm);
            txtCollResult = (TextView) itemView.findViewById(R.id.taskCollResult);
            logIcon = (ImageView) itemView.findViewById(R.id.logIcon);
            imgPrint = (ImageView) itemView.findViewById(R.id.imgPrint);
        }

        public void bind(TaskH task) {
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
                }
            }

            txtName.setText(custName);

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
//            if (isLog) {
                logIcon.setVisibility(View.VISIBLE);
                if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application) && (NewMainActivity.mnSurveyApproval != null || NewMainActivity.mnSurveyVerif != null
                        || NewMainActivity.mnVerifByBranch != null || NewMainActivity.mnApprovalByBranch != null)) {
                    if (task.getIs_prepocessed() != null && task.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                        logIcon.setImageResource(R.drawable.task_verification);
                    } else if (task.getIs_prepocessed() != null && task.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL)) {
                        logIcon.setImageResource(R.drawable.task_approval);
                    } else {
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
//            } else {
//                logIcon.setVisibility(View.GONE);
//            }

            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
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

                // olivia : menampilkan icon print untuk task yang melakukan pembayaran namun belum diprint untuk collection
                Scheme scheme = SchemeDataAccess.getOne(context, task.getUuid_scheme());
                if (scheme != null && "1".equals(scheme.getIs_printable())) {
                    boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(context, task.getUuid_user());
                    if (!isRVinFront) {
                        if (!"".equals(task.getRv_number()) && task.getRv_number() != null) {
                            imgPrint.setVisibility(View.GONE);
                        } else {
                            if (task.getPrint_count() != 0 && task.getPrint_count() != null) {
                                imgPrint.setVisibility(View.GONE);
                            }
                            else {
                                List<TaskD> taskDs = TaskDDataAccess.getAll(context, task.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                                if (taskDs != null && taskDs.size() > 0) {
                                    boolean isTaskPaid = TaskDDataAccess.isTaskPaid(context, task.getUuid_user(), task.getUuid_task_h());
                                    if (!isTaskPaid) {
                                        imgPrint.setVisibility(View.GONE);
                                    } else {
                                        imgPrint.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    imgPrint.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        imgPrint.setVisibility(View.GONE);
                    }
                } else {
                    imgPrint.setVisibility(View.GONE);
                }
            } else {
                imgPrint.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public TaskLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_log_item, parent, false);
        TaskLogViewHolder viewHolder = new TaskLogViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TaskLogViewHolder holder, final int position) {
        holder.bind(objects.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onItemClickListener(objects.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (objects != null)
            return objects.size();
        else
            return 0;
    }

    public List<TaskH> getObjects() {
        return objects;
    }

    public void setObjects(List<TaskH> objectList) {
        this.objects.clear();
        if (objectList != null) {
            if (objectList.size() > 0) {
                for (TaskH task : objectList) {
                    this.objects.add(task);
                }
            }
        }
    }
}
