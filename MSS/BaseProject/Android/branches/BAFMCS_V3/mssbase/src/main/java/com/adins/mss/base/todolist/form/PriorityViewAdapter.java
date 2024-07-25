package com.adins.mss.base.todolist.form;

import android.content.Context;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.loyalti.sla.LoyaltiSlaTimeHandler;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.Timeline;
import com.adins.mss.dao.TimelineType;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TaskH} and makes a call to the
 * specified {@link OnTaskListClickListener}.
 */
public class PriorityViewAdapter extends RecyclerView.Adapter<PriorityViewAdapter.ViewHolder> {

    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_REMINDER = "REMINDER";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_NORMAL = "NORMAL";
    public static final String PRIORITY_LOW = "LOW";
    protected static String param;
    protected final OnTaskListClickListener mListener;
    protected final Context mContext;
    protected List<TaskH> mValues = new ArrayList<>();
    protected List<TaskH> taskPriority = new ArrayList<>();
    private LoyaltiSlaTimeHandler loyaltiSlaHandler;

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public PriorityViewAdapter(Context context, List<TaskH> items, OnTaskListClickListener listener, String param) {
        mContext = context;
        // olivia : urutan task di timeline: task pending/uploading/draft order by dtm_crt lalu baru task yg belum dikerjakan order by priority
        setThenSortTask(items);

        mListener = listener;
        PriorityViewAdapter.param = param;
        if (isNeedApplyLoyaltySla()) {
            int slaTime = Integer
                    .parseInt(GeneralParameterDataAccess.getOne(mContext, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_SLA_TIME).getGs_value());
            loyaltiSlaHandler = new LoyaltiSlaTimeHandler(slaTime);
        }
    }

    private boolean isNeedApplyLoyaltySla() {
        String userJob = GlobalData.getSharedGlobalData().getUser().getFlag_job();
        boolean isSlaJob = false;
        if (null != Global.SLA_LOYALTI_JOB) {
            for (int i = 0; i < Global.SLA_LOYALTI_JOB.length; i++) {
                if (userJob.equalsIgnoreCase(Global.SLA_LOYALTI_JOB[i])) {
                    isSlaJob = true;
                    break;
                }
            }
        }

        return Global.LOYALTI_ENABLED && isSlaJob && GlobalData.getSharedGlobalData().getApplication().equalsIgnoreCase(Global.APPLICATION_SURVEY);
    }

    public List<TaskH> getListTaskh() {
        return mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_task_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (mValues.isEmpty()) {
            holder.noData.setVisibility(View.VISIBLE);
            holder.taskItem.setVisibility(View.GONE);
        } else {
            final TaskH taskH = mValues.get(position);
            holder.noData.setVisibility(View.GONE);
            holder.taskItem.setVisibility(View.VISIBLE);

            holder.mItem = taskH;
            holder.bind(taskH);
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
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.

                        mListener.onItemLongClickListener(taskH, position);
                    }
                    return true;
                }
            });
        }
    }

    public void changeDataset(List<TaskH> taskHList) {
        setThenSortTask(taskHList);
        notifyDataSetChanged();
    }

    public void delete(int position) {
        mValues.remove(position);
        notifyDataSetChanged();
    }

    public void deleteMany(List<TaskH> taskhToDeleted) {
        for (TaskH task : taskhToDeleted) {
            mValues.remove(task);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues != null && !mValues.isEmpty() ? mValues.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imgStatus;
        public final TextView txtName;
        public final TextView txtScheme;
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
        public final CardView noData;
        public final LinearLayout taskItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            taskItem = (LinearLayout) view.findViewById(R.id.taskItem);
            imgStatus = (ImageView) view.findViewById(R.id.timelineIcon);
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
            noData = (CardView) view.findViewById(R.id.noData);
        }

        public void bind(TaskH taskH) {
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            draftDate.setVisibility(View.GONE);
            mItem = taskH;
            txtName.setText(taskH.getCustomer_name());
            txtAddress.setText(taskH.getCustomer_address());
            txtPhone.setText(taskH.getCustomer_phone());
            txtName.setSelected(true);

            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                collInfo.setVisibility(View.VISIBLE);
                txtScheme.setVisibility(View.GONE);
                txtAgreement.setText(taskH.getAppl_no());
                if (taskH.getAmt_due() == null || "".equals(taskH.getAmt_due())) {
                    txtAmount.setText("-");
                } else {
                    txtAmount.setText(Tool.separateThousand(taskH.getAmt_due()));
                }

                if (taskH.getOd() == null || "".equals(taskH.getOd())) {
                    txtOverdue.setText("-");
                } else {
                    String od = taskH.getOd() + " " + mContext.getString(R.string.txtDay);
                    txtOverdue.setText(od);
                }

                if (taskH.getInst_no() == null || "".equals(taskH.getInst_no())) {
                    txtInstallment.setText("-");
                } else {
                    txtInstallment.setText(taskH.getInst_no());
                }
            } else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                collInfo.setVisibility(View.GONE);
                txtScheme.setVisibility(View.VISIBLE);

                Scheme scheme = taskH.getScheme();
                if (scheme == null) {
                    scheme = SchemeDataAccess.getOne(mContext, taskH.getUuid_scheme());
                }
                if (scheme != null) {
                    txtScheme.setText(scheme.getForm_id());
                }

                Date assignDate = taskH.getAssignment_date();
                Date today = Tool.getSystemDate();
                Date now = Tool.getSystemDateTime();
                Date dSlaTime;
                String slaDate = "";

                if (assignDate != null) {
                    slaTime.setVisibility(View.VISIBLE);
                    if (loyaltiSlaHandler == null) {
                        int SLA_time = Integer
                                .parseInt(GeneralParameterDataAccess.getOne(mContext, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_SLA_TIME).getGs_value());

                        if (assignDate.before(today)) {
                            slaDate = Formatter.formatDate(assignDate, Global.DATE_TIMESEC_TIMELINE_FORMAT_OLD);
                            slaTime.setBackgroundResource(R.drawable.sla_shape_red);
                        } else if (assignDate.after(today)) {
                            if (assignDate.compareTo(Tool.getIncrementDate(1)) == 0) {
                                Long tempTime = assignDate.getTime();
                                Date time = new Date(tempTime);
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                                slaDate = "TOM " + sdf.format(time);
                                slaTime.setBackgroundResource(R.drawable.sla_shape_green);
                            } else if (assignDate.compareTo(Tool.getIncrementDate(1)) > 0) {
                                slaDate = Formatter.formatDate(assignDate, Global.DATE_TIMESEC_TIMELINE_FORMAT_OLD);
                                slaTime.setBackgroundResource(R.drawable.sla_shape_green);
                            } else {
                                Long assDateMs = assignDate.getTime();
                                Long nowMs = now.getTime();
                                Long SLAMs = SLA_time * Long.valueOf(Global.HOUR);
                                Long sla_late = assDateMs + SLAMs;

                                dSlaTime = new Date(sla_late);
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                slaDate = sdf.format(dSlaTime);

                                if (nowMs > sla_late) {
                                    slaTime.setBackgroundResource(R.drawable.sla_shape_red);
                                } else {
                                    slaTime.setBackgroundResource(R.drawable.sla_shape_green);
                                }
                            }
                        }
                    } else {
                        dSlaTime = loyaltiSlaHandler.calculateSLATime(assignDate);
                        if (dSlaTime.before(now)) {
                            slaTime.setBackgroundResource(R.drawable.sla_shape_red);
                        } else {
                            slaTime.setBackgroundResource(R.drawable.sla_shape_green);
                        }

                        slaDate = formatSlaTime(dSlaTime);
                    }
                    slaTime.setText(slaDate);
                } else {
                    slaTime.setVisibility(View.GONE);
                }
            } else {
                collInfo.setVisibility(View.GONE);
                txtScheme.setVisibility(View.VISIBLE);

                Scheme scheme = taskH.getScheme();
                if (scheme == null) {
                    scheme = SchemeDataAccess.getOne(mContext, taskH.getUuid_scheme());
                }
                if (scheme != null) {
                    txtScheme.setText(scheme.getForm_id());
                }
            }

            String priority = taskH.getPriority();
            if (taskH.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_INIT) || taskH.getStatus().equalsIgnoreCase(TaskHDataAccess.STATUS_SEND_DOWNLOAD)) {
                if (priority != null) {
                    if (PRIORITY_HIGH.equalsIgnoreCase(priority)) {
                        imgStatus.setImageResource(R.drawable.task_highpriority);
                    } else if (PRIORITY_MEDIUM.equalsIgnoreCase(priority)) {
                        imgStatus.setImageResource(R.drawable.task_normalpriority);
                    } else if (PRIORITY_NORMAL.equalsIgnoreCase(priority)) {
                        imgStatus.setImageResource(R.drawable.task_normalpriority);
                    } else if (PRIORITY_LOW.equalsIgnoreCase(priority)) {
                        imgStatus.setImageResource(R.drawable.task_lowpriority);
                    } else if (PRIORITY_REMINDER.equalsIgnoreCase(priority)) {
                        imgStatus.setImageResource(R.drawable.task_highpriority);
                    }
                }

                // olivia : jika task belum terdownload warna backgroundnya abu
                if (TaskHDataAccess.STATUS_SEND_INIT.equalsIgnoreCase(taskH.getStatus())) {
                    taskHeader.setCardBackgroundColor(mContext.getResources().getColor(R.color.timelineLine));
                    taskHeader.setCardElevation(0);
                } else {
                    taskHeader.setCardBackgroundColor(mContext.getResources().getColor(R.color.fontColorWhite));
                }
            } else if (TaskHDataAccess.STATUS_SEND_PENDING.equalsIgnoreCase(taskH.getStatus())) {
                imgStatus.setImageResource(R.drawable.task_pending);
            } else if (TaskHDataAccess.STATUS_SEND_UPLOADING.equalsIgnoreCase(taskH.getStatus())) {
                imgStatus.setImageResource(R.drawable.task_uploading);
            } else if (TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equalsIgnoreCase(taskH.getStatus())) {
                TimelineType typeFailedDraft = TimelineTypeDataAccess.getTimelineTypebyType(mContext, Global.TIMELINE_TYPE_FAILEDDRAFT);
                List<Timeline> timelineList = taskH.getTimelineList();
                TimelineType taskHLastTimelineType = null;
                if (!timelineList.isEmpty()) {
                    taskHLastTimelineType = timelineList.get(timelineList.size() - 1).getTimelineType();
                }
                if (taskHLastTimelineType != null && taskHLastTimelineType.getUuid_timeline_type().equals(typeFailedDraft.getUuid_timeline_type())) {
                    imgStatus.setImageResource(R.drawable.task_failed_draft);
                } else {
                    imgStatus.setImageResource(R.drawable.task_draft);
                }
                draftDate.setVisibility(View.VISIBLE);
                if (null != taskH.getDraft_date() && !"".equals(taskH.getDraft_date())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy - HH:mm");
                    draftDate.setText(sdf.format(taskH.getDraft_date()));
                }
            }
        }

        private String formatSlaTime(Date slaTime) {
            Calendar slaTimeCal = Calendar.getInstance();
            slaTimeCal.setTime(slaTime);
            String result = null;
            SimpleDateFormat simpleDateFormat;
            if (DateUtils.isToday(slaTimeCal.getTimeInMillis())) {
                simpleDateFormat = new SimpleDateFormat(Global.TIME_STR_FORMAT, Locale.getDefault());
            } else {
                simpleDateFormat = new SimpleDateFormat(Global.DATE_TIME_STR_FORMAT, Locale.getDefault());
            }
            result = simpleDateFormat.format(slaTime);
            return result;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getCustomer_name() + "'";
        }
    }

    private void setThenSortTask(List<TaskH> items){
        //Bersihkan data nya terlebih dahulu agar tidak double
        taskPriority = new ArrayList<>();
        mValues = new ArrayList<>();
        if (!items.isEmpty()) {
            for (TaskH task : items) {
                if (null != task) {
                    if (null != task.getStatus()  && (TaskHDataAccess.STATUS_SEND_INIT.equalsIgnoreCase(task.getStatus()) ||
                            TaskHDataAccess.STATUS_SEND_DOWNLOAD.equalsIgnoreCase(task.getStatus()))) {
                        taskPriority.add(task);
                    } else {
                        mValues.add(task);
                    }
                }
            }

            try {
                if (!taskPriority.isEmpty()) {
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
                }
            } catch (NullPointerException e) {
                Log.w("SORT","There is something wrong with sort in class : " + PriorityViewAdapter.class.getSimpleName());
            }
        }

        if (null != taskPriority) {
            mValues.addAll(taskPriority);
        }
    }
}
