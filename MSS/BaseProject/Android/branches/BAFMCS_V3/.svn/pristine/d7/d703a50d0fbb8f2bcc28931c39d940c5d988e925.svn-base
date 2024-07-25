package com.adins.mss.base.todolist.form;

import android.content.Context;
import android.graphics.Color;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.todolist.form.helper.ItemTouchHelperAdapter;
import com.adins.mss.base.todolist.form.helper.ItemTouchHelperViewHolder;
import com.adins.mss.base.todolist.form.helper.OnStartDragListener;
import com.adins.mss.dao.TaskH;

import java.util.Collections;
import java.util.List;

/**
 * Created by ACER 471 on 3/22/2017.
 */

public class SurveyListAdapter extends RecyclerView.Adapter<SurveyListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final OnStartDragListener mDragStartListener;
    private Context mContext;
    private List<TaskH> listTaskH;

    public SurveyListAdapter(Context mContext, OnStartDragListener dragStartListener, List<TaskH> listTaskH) {
        mDragStartListener = dragStartListener;
        this.listTaskH = listTaskH;
        this.mContext = mContext;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_plan_item, parent, false);

        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        TaskH taskH = listTaskH.get(position);

        holder.txtPriority.setText(taskH.getPriority());
        holder.txtCustName.setText(taskH.getCustomer_name());
        holder.txtCustAddress.setText(taskH.getCustomer_address());
        holder.txtCustPhone.setText(taskH.getCustomer_phone());
        holder.txtNotes.setText(taskH.getNotes());

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
//        lis.remove(position);
//        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(listTaskH, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
//        TaskHSequenceDataAccess.clean(mContext);
//        TaskHSequenceDataAccess.insertAllNewTaskHSeq(mContext, listTaskH);
//        Toast.makeText(mContext, "Berhasil Memindah Task", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public int getItemCount() {
        return listTaskH.size();
    }

    public List<TaskH> getListTaskH() {
        return listTaskH;
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        public final TextView txtPriority;
        public final TextView txtCustName;
        public final TextView txtCustAddress;
        public final TextView txtCustPhone;
        public final TextView txtNotes;

        //        public final TextView textView;
        public ImageView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            txtPriority = (TextView) itemView.findViewById(R.id.txtPriority);
            txtCustName = (TextView) itemView.findViewById(R.id.txtCustName);
            txtCustAddress = (TextView) itemView.findViewById(R.id.txtCustAddress);
            txtCustPhone = (TextView) itemView.findViewById(R.id.txtCustPhone);
            txtNotes = (TextView) itemView.findViewById(R.id.txtNotes);
            handleView = (ImageView) itemView.findViewById(R.id.dragableIcon);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }
    }

}
