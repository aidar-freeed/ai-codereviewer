package com.adins.mss.dummy.userhelp_dummy.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.constant.Global;

public class NewTaskLogDummyAdapter extends RecyclerView.Adapter<NewTaskLogDummyAdapter.NewTaskLogDummyHolder> {
    @NonNull
    @Override
    public NewTaskLogDummyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_log_item,viewGroup,false);
        NewTaskLogDummyHolder viewHolder = new NewTaskLogDummyHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewTaskLogDummyHolder newTaskLogDummyHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class NewTaskLogDummyHolder extends RecyclerView.ViewHolder {
        ImageView imgPrint;
        public NewTaskLogDummyHolder(@NonNull View itemView) {
            super(itemView);
            imgPrint = itemView.findViewById(R.id.imgPrint);

            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            if(Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)){
                imgPrint.setVisibility(View.VISIBLE);
            }
        }
    }
}
