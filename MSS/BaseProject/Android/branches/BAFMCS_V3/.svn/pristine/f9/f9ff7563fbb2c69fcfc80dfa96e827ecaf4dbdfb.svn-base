package com.adins.mss.base.todolist.form;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Account;
import com.adins.mss.dao.Product;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.AccountDataAccess;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.ProductDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by muhammad.aap on 11/12/2018.
 */

public class NewToDoListViewAdapter extends RecyclerView.Adapter<NewToDoListViewAdapter.ViewHolder>{
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_NORMAL = "NORMAL";
    public static final String PRIORITY_LOW = "LOW";
    private List<TaskH> mValues = new ArrayList<>();
    private List<TaskH> taskPriority = new ArrayList<>();
    private final OnTaskListClickListener mListener;
    private final Context mContext;
    private static String param;
    String initials;

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public NewToDoListViewAdapter(Context context, List<TaskH> items, OnTaskListClickListener listener, String param) {
        mContext=context;

        // olivia : urutan task di timeline: task pending/uploading/draft order by dtm_crt lalu baru task yg belum dikerjakan order by priority
        if(!items.isEmpty()){
            for (TaskH task : items) {
                if (task.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_INIT) ||
                        task.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_DOWNLOAD))
                    taskPriority.add(task);
                else {
                    mValues.add(task);
                }
            }

            try {
                for (TaskH taskH : taskPriority) {
                    if (PRIORITY_LOW.equalsIgnoreCase(taskH.getPriority())) {
                        taskH.setPriority("1");
                    } else if (PRIORITY_NORMAL.equalsIgnoreCase(taskH.getPriority())) {
                        taskH.setPriority("2");
                    } else if (PRIORITY_MEDIUM.equalsIgnoreCase(taskH.getPriority())) {
                        taskH.setPriority("3");
                    } else if (PRIORITY_HIGH.equalsIgnoreCase(taskH.getPriority())) {
                        taskH.setPriority("4");
                    }
                }

                Collections.sort(taskPriority, new Comparator<TaskH>() {
                    @Override
                    public int compare(TaskH taskH, TaskH t1) {
                        return t1.getPriority().compareTo(taskH.getPriority());
                    }
                });

                for (TaskH taskH : taskPriority) {
                    if ("1".equalsIgnoreCase(taskH.getPriority())) {
                        taskH.setPriority(PRIORITY_LOW);
                    } else if ("2".equalsIgnoreCase(taskH.getPriority())) {
                        taskH.setPriority(PRIORITY_NORMAL);
                    } else if ("3".equalsIgnoreCase(taskH.getPriority())) {
                        taskH.setPriority(PRIORITY_MEDIUM);
                    } else if ("4".equalsIgnoreCase(taskH.getPriority())) {
                        taskH.setPriority(PRIORITY_HIGH);
                    }
                }
            }catch (NullPointerException e){
                FireCrash.log(e);
            }
        }

        if (taskPriority != null) {
            for (TaskH taskH : taskPriority)
                mValues.add(taskH);
        }

        mListener = listener;
        NewToDoListViewAdapter.param = param;
    }

    @Override
    public NewToDoListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_todolist_item, parent, false);

        return new NewToDoListViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewToDoListViewAdapter.ViewHolder holder, final int position) {
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
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onItemLongClickListener(holder.mItem, position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues != null)
            return mValues.size();
        else

            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final LinearLayout layout ;
        public final TextView txtName ;
        public final TextView txtScheme ;
        public final TextView txtAddress;
        public final TextView txtPhone;
        public final TextView txtAgreement;
        public final TextView txtAmount;
        public final TextView txtOverdue;
        public final TextView txtInstallment;
        public final TextView slaTime;
        public final TextView draftDate;
        public final CardView taskHeader;
        public final LinearLayout collInfo;
        public TaskH mItem;
        public final TextView accName;
        public final TextView lastStatus;
        public final TextView prodName;
        public final LinearLayout mmaLayout;
        public final LinearLayout taskLayout;
        public String phoneNumber;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            layout = (LinearLayout) view.findViewById(R.id.taskListLayout);
            txtName = (TextView) view.findViewById(R.id.taskName);
            txtAddress = (TextView) view.findViewById(R.id.taskAddress);
            txtPhone = (TextView) view.findViewById(R.id.taskPhone);
            txtScheme = (TextView) view.findViewById(R.id.taskForm);
            txtAgreement = (TextView) view.findViewById(R.id.taskAgreement);
            txtAmount = (TextView) view.findViewById(R.id.taskAmount);
            txtOverdue = (TextView) view.findViewById(R.id.taskOverdue);
            txtInstallment = (TextView) view.findViewById(R.id.taskInst);
            slaTime = (TextView) view.findViewById(R.id.txtslatime);
            draftDate = (TextView) view.findViewById(R.id.txtSaveDate);
            taskHeader = (CardView) view.findViewById(R.id.taskHeader);
            collInfo = (LinearLayout) view.findViewById(R.id.collectionInfo);
            taskLayout = (LinearLayout) view.findViewById(R.id.taskLayout);
            mmaLayout = (LinearLayout) view.findViewById(R.id.mmaLayout);
            accName = (TextView) view.findViewById(R.id.accountName);
            lastStatus = (TextView) view.findViewById(R.id.lastStatus);
            prodName = (TextView) view.findViewById(R.id.productName);
        }

        public void bind(TaskH taskH){
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();

            if (Global.APPLICATION_ORDER.equals(application)) {
                taskLayout.setVisibility(View.GONE);

                if (taskH.getStatus_code() != null) {
                    lastStatus.setText(taskH.getStatus_code());
                } else {
                    TaskD taskd = TaskDDataAccess.getOneFromTaskDWithTag(mContext, taskH.getUuid_task_h(), Global.TAG_STATUS);
                    if (taskd != null)
                        lastStatus.setText(taskd.getLov());
                    else
                        lastStatus.setText("-");
                }

                if (taskH.getUuid_account() != null) {
                    Account account = AccountDataAccess.getOne(mContext, taskH.getUuid_account());
                    if(account != null) {
                        initials = "";
                        initials=initial(account.getAccount_name());
                        accName.setText(account.getAccount_name());

                        phoneNumber="";
                        phoneNumber=taskH.getCustomer_phone();
                    }
                    else
                        accName.setText("-");
                } else
                    accName.setText("-");

                if (taskH.getUuid_product() != null) {
                    Product product = ProductDataAccess.getOne(mContext, taskH.getUuid_product());
                    if (product != null) {
                        prodName.setText(product.getProduct_name());
                    } else {
                        prodName.setText("-");
                    }
                } else
                    prodName.setText("-");
            } else {
                draftDate.setVisibility(View.GONE);
                txtName.setText(taskH.getCustomer_name());
                txtAddress.setText(taskH.getCustomer_address());
                txtPhone.setText(taskH.getCustomer_phone());
                txtName.setSelected(true);

                if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                    collInfo.setVisibility(View.VISIBLE);
                    txtScheme.setVisibility(View.GONE);
                    txtAgreement.setText(taskH.getAppl_no());

                    TaskD taskdAmount = TaskDDataAccess.getOneFromTaskDWithTag(mContext, taskH.getUuid_task_h(), Global.TAG_OS_AMOUNT);
                    TaskD taskdOD = TaskDDataAccess.getOneFromTaskDWithTag(mContext, taskH.getUuid_task_h(), Global.TAG_OD);
                    TaskD taskdInstallmentNo = TaskDDataAccess.getOneFromTaskDWithTag(mContext, taskH.getUuid_task_h(), Global.TAG_INSTALLMENT_NO);

                    if (taskdAmount == null) {
                        txtAmount.setText("-");
                    } else {
                        txtAmount.setText(Tool.separateThousand(taskdAmount.getText_answer()));
                    }

                    if (taskdOD == null) {
                        txtOverdue.setText("-");
                    } else {
                        txtOverdue.setText(taskdOD.getText_answer());
                    }

                    if (taskdInstallmentNo == null) {
                        txtInstallment.setText("-");
                    } else {
                        txtInstallment.setText(taskdInstallmentNo.getText_answer());
                    }
                } else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                    collInfo.setVisibility(View.GONE);
                    txtScheme.setVisibility(View.VISIBLE);

                    Scheme scheme = taskH.getScheme();
                    if (scheme == null) {
                        scheme = SchemeDataAccess.getOne(mContext,
                                taskH.getUuid_scheme());
                    }
                    if(scheme!=null)
                        txtScheme.setText(scheme.getForm_id());

                    int SLA_time = Integer.parseInt(GeneralParameterDataAccess.getOne(
                            mContext,
                            GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                            Global.GS_SLA_TIME).getGs_value());

                    Date assignDate = taskH.getAssignment_date();
                    Date dSlaTime;
                    if (assignDate != null) {
                        Long assDateMs = assignDate.getTime();

                        Date now = Tool.getSystemDateTime();
                        Long nowMs = now.getTime();

                        Long SLAMs = SLA_time * Long.valueOf(Global.HOUR);

                        Long sla_late = assDateMs + SLAMs;

                        dSlaTime = new Date(sla_late);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String slaDate = sdf.format(dSlaTime);
                        slaTime.setVisibility(View.VISIBLE);
                        slaTime.setText(slaDate);
                        if (nowMs > sla_late) {
                            slaTime.setBackgroundResource(R.color.slaRed);
                        } else
                            slaTime.setBackgroundResource(R.color.slaGreen);
                    }else{
                        slaTime.setVisibility(View.GONE);
                    }
                } else {
                    collInfo.setVisibility(View.GONE);
                    txtScheme.setVisibility(View.VISIBLE);

                    Scheme scheme = taskH.getScheme();
                    if (scheme == null) {
                        scheme = SchemeDataAccess.getOne(mContext,
                                taskH.getUuid_scheme());
                    }
                    if(scheme!=null)
                        txtScheme.setText(scheme.getForm_id());
                }
            }

            if (taskH.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_INIT) || taskH.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_DOWNLOAD)) {
                // olivia : jika task belum terdownload warna backgroundnya abu
                if (TaskHDataAccess.STATUS_SEND_INIT.equalsIgnoreCase(taskH.getStatus())) {
                    taskHeader.setCardBackgroundColor(mContext.getResources().getColor(R.color.timelineLine));
                    taskHeader.setCardElevation(0);
                }
                else
                    taskHeader.setCardBackgroundColor(mContext.getResources().getColor(R.color.fontColorWhite));
            }
            else if (TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equalsIgnoreCase(taskH.getStatus())) {
                draftDate.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy - HH:mm");
                draftDate.setText(sdf.format(taskH.getDraft_date()));
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getCustomer_name() + "'";
        }

        @NonNull
        private String initial(String initial) {
            Pattern p = Pattern.compile("((^| )[A-Za-z])");
            Matcher m = p.matcher(initial);
            StringBuilder init = new StringBuilder();
            int counter =0;
            while (m.find() && counter<2) {
                init.append(m.group().trim());
                init.append(" ") ;
                counter++;
            }

            return init.toString().toUpperCase();
        }
    }
}
