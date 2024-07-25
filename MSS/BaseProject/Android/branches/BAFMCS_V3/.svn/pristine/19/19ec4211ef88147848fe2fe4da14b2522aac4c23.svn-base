package com.adins.mss.odr.followup;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.adins.mss.dao.Account;
import com.adins.mss.dao.GroupTask;
import com.adins.mss.foundation.db.dataaccess.AccountDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.odr.R;

import java.util.List;

/**
 * Created by olivia.dg on 11/24/2017.
 */

public class FollowUpAdapter extends RecyclerView.Adapter<FollowUpAdapter.FollowUpViewHolder> {
    private Context context;
    private List<GroupTask> groupTasks;
    private final OnCheckedListener onCheckedListener;

    public FollowUpAdapter(Context context, OnCheckedListener listener, List<GroupTask> groupTasks) {
        this.context = context;
        this.groupTasks = groupTasks;
        onCheckedListener = listener;
    }

    public class FollowUpViewHolder extends RecyclerView.ViewHolder {
        private TextView txtId;
        private TextView txtAccount;
        private TextView txtStatus;
        private TextView txtProduct;
        private TextView txtProjectNett;
        public CheckBox checkBox;
        public final View mView;

        public FollowUpViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            txtId = (TextView) itemView.findViewById(R.id.txtId);
            txtAccount = (TextView) itemView.findViewById(R.id.txtAccount);
            txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
            txtProduct = (TextView) itemView.findViewById(R.id.txtProduct);
            txtProjectNett = (TextView) itemView.findViewById(R.id.txtProjectNett);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }

        public void bind(GroupTask task) {
            Account account = AccountDataAccess.getOne(context, task.getUuid_account());

            txtId.setText(task.getGroup_task_id());
            txtAccount.setText(account.getAccount_name());
            txtStatus.setText(task.getLast_status());
            txtProduct.setText(task.getProduct_name());
            txtProjectNett.setText(Tool.separateThousand(task.getProject_nett()));
        }
    }

    @Override
    public FollowUpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.followup_list_item, parent, false);
        FollowUpViewHolder viewHolder = new FollowUpViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FollowUpViewHolder holder, final int position) {
        holder.bind(groupTasks.get(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onCheckedListener != null) {
                    if (isChecked) {
                        onCheckedListener.onChecked(groupTasks.get(position).getGroup_task_id());
                    } else {
                        onCheckedListener.onUnchecked(groupTasks.get(position).getGroup_task_id());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupTasks.size();
    }
}
