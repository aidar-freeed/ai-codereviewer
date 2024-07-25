package com.adins.mss.base.todolist.form.followup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.todolist.form.OnTaskListClickListener;
import com.adins.mss.base.todolist.form.PriorityViewAdapter;
import com.adins.mss.dao.TaskH;

import java.util.ArrayList;
import java.util.List;

public class FollowUpAdapter extends PriorityViewAdapter {

    private List<TaskH> mValues;
    private List<FollowUpTask> followUpTasks;
    private Context mContext;
    private OnTaskListClickListener mListener;

    public FollowUpAdapter(Context context, List<TaskH> items, OnTaskListClickListener listener, String param) {
        super(context, items, listener, param);
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    public void setFollowUpTasks (List<FollowUpTask> followUpTasks){
        this.followUpTasks = followUpTasks;
        List<TaskH> taskHList = new ArrayList<>();
        for(FollowUpTask task : followUpTasks){
            taskHList.add(task.getFollowUpTaskHeader());
        }
        changeDataset(taskHList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_task_list_item, parent, false);
        return new FollowUpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final TaskH taskH = followUpTasks.get(position).getFollowUpTaskHeader();
        holder.bind(taskH);
        holder.imgStatus.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("uuidTaskH", taskH.getUuid_task_h());
                bundle.putString("customerName",  taskH.getCustomer_name());
                bundle.putString("agreementNo", taskH.getAppl_no());
                bundle.putString("flagTask", followUpTasks.get(position).getFlagTask());
                bundle.putString("tglJanjiBayar", followUpTasks.get(position).getTglJanjiBayar());

                FollowUpFormFragment fragment = new FollowUpFormFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return followUpTasks != null ? followUpTasks.size() : 0;
    }

    public class FollowUpViewHolder extends PriorityViewAdapter.ViewHolder{

        public FollowUpViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(TaskH taskH) {
            super.bind(taskH);
        }
    }
}
