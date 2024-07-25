package com.adins.mss.base.todolist.form.todaysplan;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.todolist.form.todaysplan.dummytaskplan.DummyPlan;

import java.util.ArrayList;
import java.util.List;

public class TodayPlanDummyAdapter extends RecyclerView.Adapter<TodayPlanDummyAdapter.TodayPlanDummyViewHolder>{

    private Context mContext;
    private List<DummyPlan> dummyDataset;

    public TodayPlanDummyAdapter(Context context,List<DummyPlan> dummyDataset){
        mContext = context;
        this.dummyDataset = dummyDataset;
    }

    public void setDummyDataset(List<DummyPlan> dummyDataset) {
        this.dummyDataset = dummyDataset;
        notifyDataSetChanged();
    }

    public static List<DummyPlan> createDefaultDummyDataset(int count,boolean showDeleteBtn) {
        List<DummyPlan> dummyPlans = new ArrayList<>();
        for(int i=0; i<count; i++){
            DummyPlan dummyPlan = new DummyPlan(showDeleteBtn,"Sample Customer "+(i+1),"Sample Address "+(i+1),"Sample Phone "+(i+1));
            dummyPlans.add(dummyPlan);
        }
        return dummyPlans;
    }

    @NonNull
    @Override
    public TodayPlanDummyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dummy_today_plan_item, parent, false);
        return new TodayPlanDummyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayPlanDummyViewHolder todayPlanDummyViewHolder, int i) {
        DummyPlan data = dummyDataset.get(i);
        todayPlanDummyViewHolder.bind(data);
    }

    @Override
    public int getItemCount() {
        return dummyDataset.size();
    }

    public class TodayPlanDummyViewHolder extends RecyclerView.ViewHolder{
        private ImageView deletePlanBtn;
        private TextView customerNameTxt;
        private TextView addressTxt;
        private TextView phoneTxt;

        public TodayPlanDummyViewHolder(View view){
            super(view);
            deletePlanBtn = view.findViewById(R.id.dummyDeletePlanBtn);
            DrawableCompat.setTint(
                    DrawableCompat.wrap(deletePlanBtn.getDrawable()),
                    ContextCompat.getColor(mContext, R.color.gradient_end)
            );


            customerNameTxt = view.findViewById(R.id.dummyTaskName);
            addressTxt = view.findViewById(R.id.dummyTaskAddress);
            phoneTxt = view.findViewById(R.id.dummyTaskPhone);
        }

        //called when notify data change or bind view holder event
        public void bind(DummyPlan dummyData){
            customerNameTxt.setText(dummyData.getCustomer());
            addressTxt.setText(dummyData.getAddress());
            phoneTxt.setText(dummyData.getPhone());
            if(dummyData.isShowDeleteBtn()){
                deletePlanBtn.setVisibility(View.VISIBLE);
            }
            else {
                deletePlanBtn.setVisibility(View.GONE);
            }
        }
    }
}
