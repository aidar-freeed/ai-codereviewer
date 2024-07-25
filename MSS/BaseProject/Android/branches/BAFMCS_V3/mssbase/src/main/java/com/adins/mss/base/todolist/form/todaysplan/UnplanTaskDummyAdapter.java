package com.adins.mss.base.todolist.form.todaysplan;

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.constant.Global;


public class UnplanTaskDummyAdapter extends RecyclerView.Adapter<UnplanTaskDummyAdapter.UnplanTaskDummyViewHolder> {

    View parentView;

    public UnplanTaskDummyAdapter (View parentView){
        this.parentView = parentView;
    }

    @Override
    public UnplanTaskDummyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unplan_task_list_dummy, parent, false);
        return new UnplanTaskDummyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnplanTaskDummyViewHolder unplanTaskDummyViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class UnplanTaskDummyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout dummyCollInfo;
        private LinearLayout addToPlanBtn;

        public UnplanTaskDummyViewHolder(View view) {
            super(view);
            addToPlanBtn = parentView.findViewById(R.id.addToPlanBtnCont);
            dummyCollInfo = view.findViewById(R.id.dummyCollectionInfo);

            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())) {
                dummyCollInfo.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addToPlanBtn.setVisibility(View.VISIBLE);
                    }
                },0);
            }
        }
    }
}

