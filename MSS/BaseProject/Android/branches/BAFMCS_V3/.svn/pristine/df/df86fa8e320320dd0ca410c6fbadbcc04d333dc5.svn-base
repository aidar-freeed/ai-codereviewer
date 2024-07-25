package com.adins.mss.base.todolist.form;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TaskH} and makes a call to the
 * specified {@link OnTaskListClickListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class StatusViewAdapter extends RecyclerView.Adapter<StatusViewAdapter.ViewHolder> {

    private final List<TaskH> mValues;
    private final OnTaskListClickListener mListener;

    public StatusViewAdapter(List<TaskH> items, OnTaskListClickListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_status_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.bind(mValues.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onItemClickListener(holder.mItem, position);
                }
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mListener) {
                    mListener.onItemLongClickListener(holder.mItem, position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imgStatus;
        public final TextView txtId;
        public final TextView txtName;
        public final TextView txtDate;
        public final TextView txtStatus;
        public final TextView txtScheme;
        public TaskH mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imgStatus = (ImageView) view.findViewById(R.id.imgStatus);
            txtId = (TextView) view.findViewById(R.id.txtTaskID);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            txtStatus = (TextView) view.findViewById(R.id.txtStatusTask);
            txtScheme = (TextView) view.findViewById(R.id.txtScheme);
        }

        public void bind(TaskH taskH) {
            mItem = taskH;

            if (TaskHDataAccess.STATUS_SEND_PENDING.equals(taskH.getStatus())) {
                imgStatus.setImageResource(R.drawable.ic_pending);
            } else if (TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(taskH.getStatus())) {
                imgStatus.setImageResource(R.drawable.ic_save_status);
            } else if (TaskHDataAccess.STATUS_SEND_UPLOADING.equals(taskH.getStatus())) {
                imgStatus.setImageResource(R.drawable.ic_uploading);
            }
            txtId.setText(taskH.getTask_id());
            txtName.setText(taskH.getCustomer_name());
            txtStatus.setText(taskH.getStatus());
//			    	txtScheme.setText(bean.toUploadingStatus());
            if (taskH.getScheme() != null)
                txtScheme.setText(taskH.getScheme().getForm_id());
            // bong 6 apr 15 - add txtDate below txtname
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy - HH:mm");
            String draftDate = sdf.format(taskH.getDraft_date());
            txtDate.setText(draftDate);
            txtDate.setTextSize(10);
            txtId.setSelected(true);
            txtName.setSelected(true);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getCustomer_name() + "'";
        }
    }
}
