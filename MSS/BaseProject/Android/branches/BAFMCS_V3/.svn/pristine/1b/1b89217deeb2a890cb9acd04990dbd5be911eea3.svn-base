package com.adins.mss.base.todolist.form.todaysplan;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adins.mss.base.R;
import com.adins.mss.base.todo.Task;
import com.adins.mss.base.todolist.form.OnTaskListClickListener;
import com.adins.mss.base.todolist.form.PriorityViewAdapter;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PlanTask;
import com.adins.mss.dao.TaskH;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodayPlanAdapter extends PriorityViewAdapter {

    private HashMap<String,Boolean> selectedFlags = new HashMap<>();
    private String highlightColor = "#DEC0C0C0";
    private float highlightElevation = 8f;
    private OnPlanDeletedListener deletedListener;

    public TodayPlanAdapter(Context context, List<TaskH> items, OnTaskListClickListener listener,OnPlanDeletedListener deletedListener, String param) {
        super(context, items, listener, param);
        mValues = items;
        this.deletedListener = deletedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_task_list_item, parent, false);
        return new TodayPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (mValues.size() == 0) {
            holder.noData.setVisibility(View.VISIBLE);
            holder.taskItem.setVisibility(View.GONE);
        } else {
            final TaskH taskH = mValues.get(position);
            holder.noData.setVisibility(View.GONE);
            holder.taskItem.setVisibility(View.VISIBLE);

            holder.mItem = taskH;
            holder.bind(taskH);

            //check selected
            final Boolean selected = selectedFlags.get(taskH.getUuid_task_h());
            if(selected != null && selected){
                holder.taskHeader.setCardBackgroundColor(Color.parseColor(highlightColor));
                holder.taskHeader.setCardElevation(highlightElevation);
            }
            else {
                holder.taskHeader.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.taskHeader.setCardElevation(5f);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onItemClickListener(taskH, position);
                    }
                }
            });
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (null != mListener) {
                        //prevent multiple select item
                        if(Global.isPlanStarted()){
                            mListener.onItemLongClickListener(taskH, position);
                            return true;
                        }

                        if(checkNumSelectedItem() > 0){
                            return false;
                        }

                        selectedFlags.put(taskH.getUuid_task_h(),true);
                        holder.taskHeader.setCardBackgroundColor(Color.parseColor(highlightColor));
                        holder.taskHeader.setCardElevation(highlightElevation);
                        mListener.onItemLongClickListener(taskH, position);
                    }
                    return true;
                }
            });
            ((TodayPlanViewHolder)holder).deletePlanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(deletedListener != null){
                        deletedListener.onPlanDeleted(taskH,position);
                    }
                }
            });
        }
    }

    private int checkNumSelectedItem(){
        int numSelected = 0;
        for(Map.Entry flag :selectedFlags.entrySet()){
            Boolean flagValue = (Boolean) flag.getValue();
            if(flagValue){
                numSelected += 1;
            }
        }
        return numSelected;
    }

    public void changeDataset(List<TaskH> dataset){
        mValues = dataset;
        notifyDataSetChanged();
    }

    public void deselectItem(String uuidTaskh,int position){
        if(selectedFlags.get(uuidTaskh) != null){
            selectedFlags.put(uuidTaskh,false);
            notifyItemChanged(position);
        }
    }

    public void addQueueItem(TaskH data){
        if(mValues.contains(data)){
            return;
        }
        mValues.add(data);
        notifyItemInserted(mValues.size()-1);
    }

    public int moveItemFromTo(int from,int to){
        if(to < 0 || to >= mValues.size())
            return from;

        if(from == to)
            return from;

        TaskH targetSwap = mValues.get(to);
        TaskH swapped = mValues.get(from);
        mValues.set(to,swapped);
        mValues.set(from,targetSwap);
        notifyItemMoved(from,to);
        notifyItemChanged(from);
        notifyItemChanged(to);
        return to;
    }

    public int moveItemToTop(int from){
        if(from < 0 || from >= mValues.size())
            return from;
        //copy and add to head
        mValues.add(0,mValues.get(from));
        mValues.remove(from + 1);
        notifyItemMoved(from,0);
        notifyDataSetChanged();
        return 0;
    }

    public int moveItemToBottom(int from){
        if(from < 0 || from >= mValues.size())
            return from;
        //add to tail
        mValues.add(mValues.get(from));
        mValues.remove(from);
        notifyItemMoved(from,mValues.size()-1);
        notifyDataSetChanged();
        return mValues.size()-1;
    }

    public interface OnPlanDeletedListener{
        void onPlanDeleted(TaskH taskH,int position);
    }

    public class TodayPlanViewHolder extends PriorityViewAdapter.ViewHolder{

        LinearLayout deletePlanBtnCont;
        ImageView deletePlanBtn;

        public TodayPlanViewHolder(View view) {
            super(view);
            deletePlanBtnCont = view.findViewById(R.id.deletePlanBtnCont);
            deletePlanBtn = view.findViewById(R.id.deletePlanBtn);
            DrawableCompat.setTint(
                    DrawableCompat.wrap(deletePlanBtn.getDrawable()),
                    ContextCompat.getColor(mContext, R.color.gradient_end)
            );
        }

        @Override
        public void bind(final TaskH taskH) {
            super.bind(taskH);
            deletePlanBtn.setOnClickListener(null);
            if(Global.isPlanStarted()){
                deletePlanBtnCont.setVisibility(View.GONE);
            }
            else {
                deletePlanBtnCont.setVisibility(View.VISIBLE);
            }
        }
    }

}
