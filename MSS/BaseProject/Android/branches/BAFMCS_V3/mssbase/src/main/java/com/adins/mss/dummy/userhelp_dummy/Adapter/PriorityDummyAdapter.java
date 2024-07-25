package com.adins.mss.dummy.userhelp_dummy.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.constant.Global;

public class PriorityDummyAdapter extends RecyclerView.Adapter<PriorityDummyAdapter.PriorityDummyHolder> {
    @NonNull
    @Override
    public PriorityDummyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dummy_userhelp_task_list_item,viewGroup,false);
        PriorityDummyHolder viewHolder = new PriorityDummyHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PriorityDummyHolder priorityDummyHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class PriorityDummyHolder extends RecyclerView.ViewHolder {
        public PriorityDummyHolder(@NonNull View itemView) {
            super(itemView);
            TextView txtSla = itemView.findViewById(R.id.txtslatime);
            if(Global.APPLICATION_SURVEY.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())){
                txtSla.setVisibility(View.VISIBLE);
            }
        }
    }
}
