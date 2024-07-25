package com.adins.mss.coll.dashboardcollection.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.coll.R;
import com.adins.mss.coll.dashboardcollection.model.CollResultDetail;

import java.util.List;

public class DashCollResultItemAdapter extends RecyclerView.Adapter<DashCollResultItemAdapter.DashboardCollResultVH> {

    private Context context;
    private List<CollResultDetail> dataset;

    public DashCollResultItemAdapter(Context context,List<CollResultDetail> dataset) {
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public DashboardCollResultVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.dash_coll_result_item,viewGroup,false);
        DashboardCollResultVH viewHolder = new DashboardCollResultVH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardCollResultVH dashboardCollResultVH, int i) {
        dashboardCollResultVH.bind(dataset.get(i));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void notifyDataChange(List<CollResultDetail> dataset){
        this.dataset = dataset;
        notifyDataSetChanged();
    }

    public class DashboardCollResultVH extends RecyclerView.ViewHolder {

        private TextView agrNoTv;
        private TextView custNoTv;
        private TextView resultTv;

        public DashboardCollResultVH(@NonNull View itemView) {
            super(itemView);
            agrNoTv = itemView.findViewById(R.id.dashResultAgrNo);
            custNoTv = itemView.findViewById(R.id.dashResultCustNo);
            resultTv = itemView.findViewById(R.id.dashResult);
        }

        public void bind(CollResultDetail item){
            if(item == null)
                return;
            agrNoTv.setText(item.getAgrNo());
            custNoTv.setText(item.getCustName());
            resultTv.setText(item.getResult());
        }
    }
}
