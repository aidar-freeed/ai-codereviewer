package com.adins.mss.base.todolist.form.todaysplan;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.todolist.form.OnTaskListClickListener;
import com.adins.mss.base.todolist.form.PriorityViewAdapter;
import com.adins.mss.dao.TaskH;

import org.acra.util.BoundedLinkedList;

import java.util.HashMap;
import java.util.List;

public class UnplanTaskAdapter extends PriorityViewAdapter {

    private OnTaskCheckedListener checkedListener;
    private HashMap<String,Integer> selectedTasks;
    private String selectedTaskColor = "#DBA9A9A9";

    public UnplanTaskAdapter(Context context, List<TaskH> items,HashMap<String,Integer> selectedTasks
            ,OnTaskCheckedListener checkedListener, OnTaskListClickListener listener, String param) {
        super(context, items, listener, param);
        mValues = items;
        this.checkedListener = checkedListener;
        this.selectedTasks = selectedTasks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.unplan_task_list_item, parent, false);
        return new UnplanTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        UnplanTaskViewHolder unplanTaskVH = (UnplanTaskViewHolder) holder;
        if (mValues.size() == 0) {
            unplanTaskVH.noData.setVisibility(View.VISIBLE);
            unplanTaskVH.taskItem.setVisibility(View.GONE);
        } else {
            final TaskH taskH = mValues.get(position);
            unplanTaskVH.noData.setVisibility(View.GONE);
            unplanTaskVH.taskItem.setVisibility(View.VISIBLE);
            unplanTaskVH.mItem = taskH;
            unplanTaskVH.selectPlanTaskCB.setOnCheckedChangeListener(null);
            unplanTaskVH.bind(taskH);
            unplanTaskVH.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onItemClickListener(taskH, position);
                    }
                }
            });

            unplanTaskVH.selectPlanTaskCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(checkedListener!=null){
                        if(isChecked){
                            checkedListener.onTaskChecked(taskH,position);
                        }
                        else{
                            checkedListener.onTaskUnchecked(taskH,position);
                        }
                    }
                }
            });
        }
    }

    public HashMap<String, Integer> getSelectedTasks() {
        return selectedTasks;
    }

    public void notifySelectedSequenceChanged(HashMap<String,Integer> newSelectSequences){
        selectedTasks = newSelectSequences;
        notifyDataSetChanged();
    }

    public class UnplanTaskViewHolder extends PriorityViewAdapter.ViewHolder{

        //checkbox
        private CheckBox selectPlanTaskCB;
        private TextView selectedSequence;

        public UnplanTaskViewHolder(View view) {
            super(view);
            selectPlanTaskCB = view.findViewById(R.id.selectPlanTaskCB);
            selectedSequence = view.findViewById(R.id.selectedSequence);
        }

        @Override
        public void bind(TaskH taskH) {
            super.bind(taskH);
            //set checkbox state depend on data
            if(selectedTasks.containsKey(taskH.getUuid_task_h())){
                Integer seq = selectedTasks.get(taskH.getUuid_task_h());
                if(seq != null){
                    selectedSequence.setVisibility(View.VISIBLE);
                    selectedSequence.setText(seq+"");
                }
                selectPlanTaskCB.setChecked(true);
                taskHeader.setCardBackgroundColor(Color.parseColor(selectedTaskColor));
            }
            else {
                selectedSequence.setVisibility(View.GONE);
                selectPlanTaskCB.setChecked(false);
                taskHeader.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
    }
}
