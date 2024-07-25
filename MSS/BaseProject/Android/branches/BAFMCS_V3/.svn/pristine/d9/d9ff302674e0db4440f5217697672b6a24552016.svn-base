package com.adins.mss.odr.opportunities;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.dao.GroupTask;
import com.adins.mss.odr.R;

import java.util.List;
import java.util.Map;

/**
 * Created by muhammad.aap on 11/30/2018.
 */

public class OpportunityMenuListAdapter extends RecyclerView.Adapter<OpportunityMenuListAdapter.OpportunityListViewHolder> {
    private FragmentActivity activity;
    private List<Map<Integer,List<GroupTask>>> groupTasks;
    private List<GroupTask> listGroupTasks;

    public OpportunityMenuListAdapter(FragmentActivity activity, List<Map<Integer,List<GroupTask>>> groupTasks) {
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

        public void bind(List<Map<Integer,List<GroupTask>>> groupTask, final int position) {

//            txtId.setText(groupTask.get(position).get();  getGroup_task_id());
//            txtStatus.setText(groupTask.getLast_status());
//            txtProduct.setText(groupTask.getProduct_name());
        }
    }

    @Override
    public OpportunityMenuListAdapter.OpportunityListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mn_opportunity_list_item, parent, false);
        OpportunityMenuListAdapter.OpportunityListViewHolder viewHolder = new OpportunityMenuListAdapter.OpportunityListViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OpportunityMenuListAdapter.OpportunityListViewHolder holder, final int position) {
        holder.bind(groupTasks,position);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LoadOpportunityDetail request = new LoadOpportunityDetail(activity, groupTasks.get(position));
//                request.execute();
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