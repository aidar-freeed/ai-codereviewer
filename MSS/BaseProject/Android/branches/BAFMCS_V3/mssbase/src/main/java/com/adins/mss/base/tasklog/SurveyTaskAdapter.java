package com.adins.mss.base.tasklog;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.todolist.form.OnTaskListClickListener;
import com.adins.mss.constant.Global;

import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.Timeline;

import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.formatter.Formatter;


import java.util.Date;
import java.util.List;

/**
 * Created by olivia.dg on 3/16/2018.
 */

public class SurveyTaskAdapter extends RecyclerView.Adapter<SurveyTaskAdapter.SurveyTaskViewHolder> {

    private static boolean isLog = false;
    private static Context context;
    private List<TaskH> objects;
    private final OnTaskListClickListener mListener;

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public SurveyTaskAdapter(Context context, List<TaskH> objects, OnTaskListClickListener listener) {
        this.context = context;
        this.objects = objects;
        mListener = listener;
    }

    static class SurveyTaskViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        TextView txtName;
        TextView txtTime;
        TextView txtScheme;
        TextView txtCollResult;
        ImageView logIcon;
        ImageView imgPrint;

        public SurveyTaskViewHolder(View itemView) {
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

            logIcon.setVisibility(View.VISIBLE);
            List<Timeline> timeline = TimelineDataAccess.getTimelineByTask(context, task.getUuid_user(), task.getUuid_task_h());
            if (null!=timeline && timeline.get(timeline.size()-1).getTimelineType().getTimeline_type().equals(Global.TIMELINE_TYPE_FAILEDDRAFT)){
                logIcon.setImageResource(R.drawable.task_failed_draft);
            } else if (task.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)){
                logIcon.setImageResource(R.drawable.task_draft);
            } else if (task.getIs_prepocessed() != null && task.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                logIcon.setImageResource(R.drawable.task_verification);
            } else if (task.getIs_prepocessed() != null && task.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL)) {
                logIcon.setImageResource(R.drawable.task_approval);
            } else if (task.getPriority()  != null && task.getPriority().equals(TaskHDataAccess.PRIORITY_HIGH)){
                logIcon.setImageResource(R.drawable.task_highpriority);
            } else if (task.getPriority()  != null && task.getPriority().equals(TaskHDataAccess.PRIORITY_NORMAL)){
                logIcon.setImageResource(R.drawable.task_normalpriority);
            } else if (task.getPriority()  != null && task.getPriority().equals(TaskHDataAccess.PRIORITY_LOW)){
                logIcon.setImageResource(R.drawable.task_lowpriority);
            }
        }
    }

    @Override
    public SurveyTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_log_item, parent, false);
        SurveyTaskViewHolder viewHolder = new SurveyTaskViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SurveyTaskViewHolder holder, final int position) {
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
