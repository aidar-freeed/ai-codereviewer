package com.adins.mss.odr.accounts.adapter;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.odr.R;

import java.util.Date;
import java.util.List;

/**
 * Created by olivia.dg on 11/21/2017.
 */

public class ActivityTodoAdapter extends RecyclerView.Adapter<ActivityTodoAdapter.ActivityTodoViewholder> {
    private static FragmentActivity activity;
    private List<TaskH> listTask;

    public ActivityTodoAdapter(FragmentActivity activity, List<TaskH> listTask) {
        this.activity = activity;
        this.listTask = listTask;
    }

    public class ActivityTodoViewholder extends RecyclerView.ViewHolder {
        public final TextView txtId;
        public final TextView txtDate;
        public final TextView txtStatus;
        public final TextView txtNotes;

        public ActivityTodoViewholder(View itemView) {
            super(itemView);

            txtId = (TextView) itemView.findViewById(R.id.txtId);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
            txtNotes = (TextView) itemView.findViewById(R.id.txtNotes);
        }

        public void bind(TaskH taskH) {
            txtId.setText(taskH.getTask_id());
            txtStatus.setText(taskH.getStatus_code());
            txtNotes.setText(taskH.getNotes());

            String str_dtm_crt = "";
            Date date;

            if (taskH.getSubmit_date() == null) {
                if (taskH.getPts_date() != null)
                    date = taskH.getPts_date();
                else
                    date = taskH.getAssignment_date();

                if (date != null)
                    str_dtm_crt = Formatter.formatDate(date, Global.DATE_STR_FORMAT5);

                txtStatus.setVisibility(View.GONE );
            } else {
                date = taskH.getSubmit_date();
                if (date != null)
                    str_dtm_crt = Formatter.formatDate(date, Global.DATE_TIMESEC_TIMELINE_FORMAT_OLD);
            }

            txtDate.setText(str_dtm_crt);
        }
    }

    @Override
    public ActivityTodoViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.opportunity_detail_item, parent, false);
        ActivityTodoViewholder viewHolder = new ActivityTodoViewholder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ActivityTodoViewholder holder, int position) {
        holder.bind(listTask.get(position));
    }

    @Override
    public int getItemCount() {
        if (listTask == null || listTask.size() == 0)
            return 0;
        else
            return listTask.size();
    }
}
