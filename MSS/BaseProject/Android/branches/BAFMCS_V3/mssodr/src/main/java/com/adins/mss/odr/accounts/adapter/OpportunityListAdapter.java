package com.adins.mss.odr.accounts.adapter;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.dao.GroupTask;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.LoadOpportunityDetail;

import java.util.List;

/**
 * Created by olivia.dg on 11/17/2017.
 */

public class OpportunityListAdapter extends RecyclerView.Adapter<OpportunityListAdapter.OpportunityListViewHolder> {
    private static FragmentActivity activity;
    private List<GroupTask> groupTasks;

    public OpportunityListAdapter(FragmentActivity activity, List<GroupTask> groupTasks) {
        this.activity = activity;
        this.groupTasks = groupTasks;
    }

    public class OpportunityListViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView txtId;
        public final TextView txtStatus;
        public final TextView txtProduct;

        public OpportunityListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            txtId = (TextView) itemView.findViewById(R.id.txtId);
            txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
            txtProduct = (TextView) itemView.findViewById(R.id.txtProduct);
        }

        public void bind(GroupTask groupTask) {
            txtId.setText(groupTask.getGroup_task_id());
            txtStatus.setText(groupTask.getLast_status());
            txtProduct.setText(groupTask.getProduct_name());
        }
    }

    @Override
    public OpportunityListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.opportunity_list_item, parent, false);
        OpportunityListViewHolder viewHolder = new OpportunityListViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OpportunityListViewHolder holder, final int position) {
        holder.bind(groupTasks.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadOpportunityDetail request = new LoadOpportunityDetail(activity, groupTasks.get(position));
                request.execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (groupTasks == null || groupTasks.size() == 0)
            return 0;
        else
            return groupTasks.size();
    }
}
