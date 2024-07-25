package com.adins.mss.base.timeline;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.Timeline;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by olivia.dg on 8/28/2017.
 */

public class NewTimelineAdapter extends RecyclerView.Adapter<NewTimelineAdapter.TimelineViewHolder> implements IShowError {

    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_NORMAL = "NORMAL";
    private static Context mContext;
    protected AQuery query;
    private List<Timeline> objects = null;
    private List<Timeline> objectSort = new ArrayList<>();
    protected static OnItemListener listener;

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public NewTimelineAdapter(Context context, List<Timeline> objects, OnItemListener listener) {
        mContext = context;
        this.objects = objects;
        this.listener= listener;

        List<String> taskId = new ArrayList<>();

        for (int i = objects.size() - 1; i >= 0; i--) {
            boolean wasInTimeline = false;
            Timeline object = objects.get(i);

            if (!taskId.isEmpty()) {
                for (int j = 0; j < taskId.size(); j++) {
                    if (!object.getTimelineType().getTimeline_type().equalsIgnoreCase(Global.TIMELINE_TYPE_CHECKIN) &&
                            !object.getTimelineType().getTimeline_type().equalsIgnoreCase(Global.TIMELINE_TYPE_PUSH_NOTIFICATION)) {
                        if (taskId.get(j).equalsIgnoreCase(object.getUuid_task_h()))
                            wasInTimeline = true;
                    }
                }
            }
            if (!wasInTimeline) {
                objectSort.add(object);
                taskId.add(object.getUuid_task_h());
            }
        }
    }

    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_timeline_item_task, parent, false);
        TimelineViewHolder viewHolder = new TimelineViewHolder(v);
        query = new AQuery(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TimelineViewHolder holder, final int position) {
        if (objectSort.isEmpty()) {
            holder.noData.setVisibility(View.VISIBLE);
            holder.timelineItem.setVisibility(View.GONE);
        } else {
            final Timeline timeline = objectSort.get(position);
            holder.timelineItem.setVisibility(View.VISIBLE);
            holder.noData.setVisibility(View.GONE);

            holder.mItem = timeline;
            holder.bind(timeline);

            holder.taskAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAttendanceClick(timeline, position);
                }
            });

            holder.taskHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(timeline, position);
                }
            });
        }
    }

    public void deleteTimeline(int position) {
        objectSort.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (objectSort.isEmpty() || objectSort == null)
            count = 1;
        else
            count = objectSort.size();
        return count;
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        if(notifType == ErrorMessageHandler.DIALOG_TYPE){
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
            dialogBuilder.withTitle(errorSubject)
                    .withMessage(errorMsg)
                    .isCancelableOnTouchOutside(true)
                    .show();
        }
    }

    static class TimelineViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Timeline mItem;
        ImageView timelineIcon;
        TextView taskName;
        TextView taskAddress;
        TextView taskAgreement;
        TextView taskAmount;
        TextView taskOverdue;
        TextView taskInstl;
        TextView taskTime;
        TextView txtTitle;
        TextView txtDesc;
        TextView attTime;
        LinearLayout timelineItem;
        LinearLayout taskStatus;
        CardView noData;
        CardView taskHeader;
        CardView taskAttendance;
        LinearLayout collInfo;
        TextView collResult;
        RecyclerView rvStatus;
        public TimelineViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            timelineIcon = (ImageView) itemView.findViewById(R.id.timelineIcon);
            taskName = (TextView) itemView.findViewById(R.id.taskName);
            taskAddress = (TextView) itemView.findViewById(R.id.taskAddress);
            taskAgreement = (TextView) itemView.findViewById(R.id.taskAgreement);
            taskAmount = (TextView) itemView.findViewById(R.id.taskAmount);
            taskOverdue = (TextView) itemView.findViewById(R.id.taskOverdue);
            taskInstl = (TextView) itemView.findViewById(R.id.taskInst);
            taskTime = (TextView) itemView.findViewById(R.id.taskTime);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
            attTime = (TextView) itemView.findViewById(R.id.attendanceTime);
            rvStatus = (RecyclerView) itemView.findViewById(R.id.rv_status);
            timelineItem = (LinearLayout) itemView.findViewById(R.id.timelineItem);
            taskStatus = (LinearLayout) itemView.findViewById(R.id.taskStatus);
            noData = (CardView) itemView.findViewById(R.id.noData);
            taskHeader = (CardView) itemView.findViewById(R.id.taskHeader);
            taskAttendance = (CardView) itemView.findViewById(R.id.taskAttendance);
            collInfo = (LinearLayout) itemView.findViewById(R.id.collectionInfo);
            collResult = (TextView) itemView.findViewById(R.id.taskCollResult);
        }

        public void bind(Timeline timeline) {
            mItem = timeline;

            int icon = 0;
            String str_dtm_crt = "";
            String timelineType = timeline.getTimelineType().getTimeline_type();
            String description = timeline.getDescription();
            String uuid_task_h = timeline.getUuid_task_h();
            Date dtm_crt = timeline.getDtm_crt();
            Date today = Tool.getSystemDate();
            Date now = new Date();
            try {
                if (dtm_crt.before(today)) {
                    str_dtm_crt = Formatter.formatDate(dtm_crt, Global.DATE_TIMESEC_TIMELINE_FORMAT_OLD);
                } else {
                    long times = now.getTime() - dtm_crt.getTime();
                    if (times < 60000) {
                        str_dtm_crt = mContext.getString(R.string.moment_ago);
                    } else if (times < 3600000) {
                        times = times / 60000;
                        str_dtm_crt = times + " " + mContext.getString(R.string.minute_ago);
                    } else {
                        times = times / 3600000;
                        str_dtm_crt = times + " " + mContext.getString(R.string.hour_ago);
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
            }

            if (timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_CHECKIN)) {
                icon = R.drawable.attendance;
            } else if (timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_PUSH_NOTIFICATION)) {
                icon = R.drawable.notification;
            } else if (timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_VERIFICATION) || timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_VERIFIED)) {
                icon = R.drawable.task_verification;
            } else if (timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_APPROVAL) || timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_APPROVED)) {
                icon = R.drawable.task_approval;
            } else if (timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_REJECTED)) {
                if (timeline.getIsVerificationTask() != null && timeline.getIsVerificationTask().equals(Global.FORM_TYPE_VERIFICATION))
                    icon = R.drawable.task_verification;
                else
                    icon = R.drawable.task_approval;
            }
            else if (timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_CHANGED)) {
                if (timeline.getIsVerificationTask() != null && timeline.getIsVerificationTask().equals(Global.FORM_TYPE_VERIFICATION))
                    icon = R.drawable.task_verification;
                else
                    icon = R.drawable.task_approval;
            } else {
                if (timeline.getIsVerificationTask() != null && timeline.getIsVerificationTask().equals(Global.FORM_TYPE_VERIFICATION))
                    icon = R.drawable.task_verification;
                else if (timeline.getIsVerificationTask() != null && timeline.getIsVerificationTask().equals(Global.FORM_TYPE_APPROVAL))
                    icon = R.drawable.task_approval;
                else{
                    String taskPriority = timeline.getPriority();
                    if (taskPriority != null) {
                        if (timeline.getPriority().equalsIgnoreCase(PRIORITY_HIGH)) {
                            icon = R.drawable.task_highpriority;
                        } else if (timeline.getPriority().equalsIgnoreCase(PRIORITY_NORMAL)) {
                            icon = R.drawable.task_normalpriority;
                        } else if (timeline.getPriority().equalsIgnoreCase(PRIORITY_MEDIUM)) {
                            icon = R.drawable.task_normalpriority;
                        } else
                            icon = R.drawable.task_lowpriority;
                    }else {
                        TaskH taskH = TaskHDataAccess.getOneHeader(mContext,uuid_task_h);
                        if(taskH != null && taskH.getPriority() != null && !"".equals(taskH.getPriority())){
                            taskPriority = taskH.getPriority();
                            if (taskPriority != null) {
                                if (taskPriority.equalsIgnoreCase(PRIORITY_HIGH)) {
                                    icon = R.drawable.task_highpriority;
                                } else if (taskPriority.equalsIgnoreCase(PRIORITY_NORMAL)) {
                                    icon = R.drawable.task_normalpriority;
                                } else if (taskPriority.equalsIgnoreCase(PRIORITY_MEDIUM)) {
                                    icon = R.drawable.task_normalpriority;
                                } else
                                    icon = R.drawable.task_lowpriority;
                            }
                        }
                        else {
                            icon = R.drawable.task_new;
                        }
                    }
                }

            }

            timelineIcon.setImageResource(icon);

            if (timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_CHECKIN)) {
                taskHeader.setVisibility(View.GONE);
                taskAttendance.setVisibility(View.VISIBLE);

                txtTitle.setText(mContext.getString(R.string.timeline_type_checkin));
                txtDesc.setText(description + "\n" + timeline.getAttd_address());
                attTime.setText(str_dtm_crt);
            } else if (timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_PUSH_NOTIFICATION)) {
                taskHeader.setVisibility(View.GONE);
                taskAttendance.setVisibility(View.VISIBLE);

                txtTitle.setText(mContext.getString(R.string.timeline_type_push_notification));
                String desc = description;
                String[] message = description.split("\\|");
                if (message.length > 1) {
                    desc = message[1];
                }
                txtDesc.setText(desc);
                attTime.setText(str_dtm_crt);
            } else {
                taskHeader.setVisibility(View.VISIBLE);
                taskAttendance.setVisibility(View.GONE);
                rvStatus.setVisibility(View.GONE);

                //check timeline data if exist
                if(timeline.getName() == null || timeline.getAddress() == null
                        || timeline.getAgreement_no() == null || timeline.getAmount_due() == null
                        || timeline.getOverdue() == null || timeline.getInstallment_no() == null){
                    TaskH taskH = TaskHDataAccess.getOneHeader(mContext,timeline.getUuid_task_h());
                    if(taskH != null){
                        if(taskH.getCustomer_name() != null)
                            timeline.setName(taskH.getCustomer_name());
                        if(taskH.getCustomer_address() != null)
                            timeline.setAddress(taskH.getCustomer_address());
                        if(taskH.getAppl_no() != null)
                            timeline.setAgreement_no(taskH.getAppl_no());
                        if(taskH.getInst_no() != null)
                            timeline.setInstallment_no(taskH.getInst_no());
                        if(taskH.getAmt_due() != null)
                            timeline.setAmount_due(taskH.getAmt_due());
                        if(taskH.getOd() != null)
                            timeline.setOverdue(taskH.getOd());
                        TimelineDataAccess.addOrReplace(mContext,timeline);
                    }
                }

                if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                    // olivia : tambahan menampilkan collection result di timeline
                    if (null != timeline.getCollResult() && !("").equals(timeline.getCollResult())) {
                        collResult.setVisibility(View.VISIBLE);
                        collResult.setText(timeline.getCollResult());
                    } else {
                        collResult.setVisibility(View.GONE);
                    }
                    collInfo.setVisibility(View.VISIBLE);
                    taskAgreement.setText(timeline.getAgreement_no());
                    taskAmount.setText(timeline.getAmount_due());
                    taskOverdue.setText(timeline.getOverdue());
                    taskInstl.setText(timeline.getInstallment_no());
                } else {
                    taskAgreement.setVisibility(View.GONE);
                    taskAmount.setVisibility(View.GONE);
                    taskOverdue.setVisibility(View.GONE);
                    taskInstl.setVisibility(View.GONE);
                }

                taskName.setText(timeline.getName());
                taskAddress.setText(timeline.getAddress());
                taskTime.setText(str_dtm_crt);

                if (timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_TASK)) {
                    rvStatus.setVisibility(View.GONE);
                    TaskH taskH = timeline.getTaskH();
                    // olivia : untuk task yang sudah deleted/closed warnanya jadi abu"
                    if (taskH == null || TaskHDataAccess.isTaskHDisabled(taskH.getStatus())) {
                        taskHeader.setCardBackgroundColor(mContext.getResources().getColor(R.color.timelineLine));
                        taskHeader.setCardElevation(0);
                    } else
                        taskHeader.setCardBackgroundColor(mContext.getResources().getColor(R.color.fontColorWhite));
                } else if (timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_VERIFICATION) || timelineType.equalsIgnoreCase(Global.TIMELINE_TYPE_APPROVAL)) {
                    rvStatus.setVisibility(View.GONE);
                    TaskH taskH = timeline.getTaskH();
                    // olivia : untuk task yang sudah deleted/closed warnanya jadi abu"
                    if (taskH == null) {
                        taskHeader.setCardBackgroundColor(mContext.getResources().getColor(R.color.timelineLine));
                        taskHeader.setCardElevation(0);
                    } else
                        taskHeader.setCardBackgroundColor(mContext.getResources().getColor(R.color.fontColorWhite));
                } else {
                    rvStatus.setVisibility(View.VISIBLE);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                    rvStatus.setLayoutManager(layoutManager);
                    rvStatus.setHasFixedSize(true);
                    NewTimelineStatusAdapter statusAdapter = new NewTimelineStatusAdapter(mContext,timeline);
                    rvStatus.setAdapter(statusAdapter);
                    taskHeader.setCardBackgroundColor(mContext.getResources().getColor(R.color.fontColorWhite));
                }
            }
        }
    }

    public interface OnItemListener {
        void onItemClick(Timeline timeline, int position);
        void onAttendanceClick(Timeline timeline, int position);
        void onStatusClick(Timeline timeline, int position);
        void onStatusLongClick(Timeline timeline, int position);
    }

    public List<Timeline> getObjects() {
        return objects;
    }

    public void setObjects(List<Timeline> objects) {
        this.objects = objects;

        objectSort.clear();
        List<String> taskId = new ArrayList<>();

        for (int i = objects.size() - 1; i >= 0; i--) {
            boolean wasInTimeline = false;
            Timeline object = objects.get(i);

            if (!taskId.isEmpty()) {
                for (int j = 0; j < taskId.size(); j++) {
                    if (!object.getTimelineType().getTimeline_type().equalsIgnoreCase(Global.TIMELINE_TYPE_CHECKIN) &&
                            !object.getTimelineType().getTimeline_type().equalsIgnoreCase(Global.TIMELINE_TYPE_PUSH_NOTIFICATION)) {
                        if (taskId.get(j).equalsIgnoreCase(object.getUuid_task_h()))
                            wasInTimeline = true;
                    }
                }
            }
            if (!wasInTimeline) {
                objectSort.add(object);
                taskId.add(object.getUuid_task_h());
            }
        }
    }
}

class NewTimelineStatusAdapter extends RecyclerView.Adapter<NewTimelineStatusAdapter.TimelineStatusViewHolder> {

    Context mContext;
    Timeline timeline;
    List<Timeline> timelines;
    String description;
    List<Integer> statusDivider = new ArrayList<>();

    NewTimelineStatusAdapter(Context context, Timeline timeline){
        this.mContext = context;
        this.timeline = timeline;
        String uuid_task_h = timeline.getUuid_task_h();
        String uuid_user = timeline.getUuid_user();
        timelines = TimelineDataAccess.getTimelineByTask(mContext, uuid_user, uuid_task_h);
        if(timelines!=null){
            for(Timeline tl: timelines){
                if(tl.getTimelineType().getTimeline_type().equalsIgnoreCase(Global.TIMELINE_TYPE_FAILEDDRAFT)){
                    statusDivider.add(timelines.indexOf(tl));
                }
            }
        }
    }
    @NonNull
    @Override
    public TimelineStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_timeline_item_status_task, parent, false);
        TimelineStatusViewHolder viewHolder = new TimelineStatusViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineStatusViewHolder timelineStatusViewHolder, final int position) {
        timelineStatusViewHolder.bind(position);
        timelineStatusViewHolder.taskStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTimelineAdapter.listener.onStatusClick(timeline, position);
            }
        });

        timelineStatusViewHolder.taskStatus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                NewTimelineAdapter.listener.onStatusLongClick(timeline, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {

        if(statusDivider.isEmpty()){
            return 1;
        }else {
            return statusDivider.size()+1;
        }
    }


    public class TimelineStatusViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout0;
        LinearLayout layout1;
        LinearLayout layout2;
        LinearLayout layout3;
        LinearLayout layout4;
        TextView taskStatus0;
        TextView taskStatus1;
        TextView taskStatus2;
        TextView taskStatus3;
        TextView taskStatus4;
        TextView taskDesc0;
        TextView taskDesc1;
        TextView taskDesc2;
        TextView taskDesc3;
        TextView taskDesc4;
        TextView txtTime0;
        TextView txtTime1;
        TextView txtTime2;
        TextView txtTime3;
        TextView txtTime4;
        ImageView statusIcon0;
        ImageView statusIcon1;
        ImageView statusIcon2;
        ImageView statusIcon3;
        ImageView statusIcon4;
        LinearLayout taskStatus;
        public TimelineStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            taskStatus = itemView.findViewById(R.id.taskStatus);
            layout0 = (LinearLayout) itemView.findViewById(R.id.taskStatusItem0);
            layout1 = (LinearLayout) itemView.findViewById(R.id.taskStatusItem1);
            layout2 = (LinearLayout) itemView.findViewById(R.id.taskStatusItem2);
            layout3 = (LinearLayout) itemView.findViewById(R.id.taskStatusItem3);
            layout4 = (LinearLayout) itemView.findViewById(R.id.taskStatusItem4);
            taskStatus0 = (TextView) itemView.findViewById(R.id.txtStatus0);
            taskStatus1 = (TextView) itemView.findViewById(R.id.txtStatus1);
            taskStatus2 = (TextView) itemView.findViewById(R.id.txtStatus2);
            taskStatus3 = (TextView) itemView.findViewById(R.id.txtStatus3);
            taskStatus4 = (TextView) itemView.findViewById(R.id.txtStatus4);
            taskDesc0 = (TextView) itemView.findViewById(R.id.txtDesc0);
            taskDesc1 = (TextView) itemView.findViewById(R.id.txtDesc1);
            taskDesc2 = (TextView) itemView.findViewById(R.id.txtDesc2);
            taskDesc3 = (TextView) itemView.findViewById(R.id.txtDesc3);
            taskDesc4 = (TextView) itemView.findViewById(R.id.txtDesc4);
            txtTime0 = (TextView) itemView.findViewById(R.id.txtTime0);
            txtTime1 = (TextView) itemView.findViewById(R.id.txtTime1);
            txtTime2 = (TextView) itemView.findViewById(R.id.txtTime2);
            txtTime3 = (TextView) itemView.findViewById(R.id.txtTime3);
            txtTime4 = (TextView) itemView.findViewById(R.id.txtTime4);
            statusIcon0 = (ImageView) itemView.findViewById(R.id.taskStatusIcon0);
            statusIcon1 = (ImageView) itemView.findViewById(R.id.taskStatusIcon1);
            statusIcon2 = (ImageView) itemView.findViewById(R.id.taskStatusIcon2);
            statusIcon3 = (ImageView) itemView.findViewById(R.id.taskStatusIcon3);
            statusIcon4 = (ImageView) itemView.findViewById(R.id.taskStatusIcon4);
        }
        public void bind(int position){
            Date todayDate = Tool.getSystemDate();
            Date date = new Date();
            if (timelines != null && !timelines.isEmpty()) {
                for (Timeline tl : timelines){
                    if(position < statusDivider.size()) {
                        if (statusDivider.get(position) == timelines.indexOf(tl)) {
                            break;
                        }else if(position > 0) {
                            if (timelines.indexOf(tl) < statusDivider.get(position-1)) {
                                continue;
                            }
                        }
                    }else{
                        if ((position >0) && (timelines.indexOf(tl) < statusDivider.get(position-1))) {
                            continue;
                        }
                    }
                    Date taskDate = tl.getDtm_crt();
                    String str_date = "";
                    if (taskDate != null) {
                        if (taskDate.before(todayDate)) {
                            str_date = Formatter.formatDate(taskDate, Global.DATE_TIMESEC_TIMELINE_FORMAT_OLD);
                        } else {
                            long times = date.getTime() - taskDate.getTime();
                            if (times < 60000) {
                                str_date = mContext.getString(R.string.moment_ago);
                            } else if (times < 3600000) {
                                times = times / 60000;
                                str_date = times + " " + mContext.getString(R.string.minute_ago);
                            } else {
                                times = times / 3600000;
                                str_date = times + " " + mContext.getString(R.string.hour_ago);
                            }
                        }
                    }
                    String timeline_type = tl.getTimelineType().getTimeline_type();

                    if (timeline_type.equalsIgnoreCase(Global.TIMELINE_TYPE_PENDING)) {
                        layout2.setVisibility(View.VISIBLE);
                        statusIcon2.setImageResource(R.drawable.task_pending);
                        taskStatus2.setText(mContext.getString(R.string.timeline_type_pending));
                        taskDesc2.setVisibility(View.VISIBLE);
                        description = tl.getDescription();
                        taskDesc2.setText(description);
                        txtTime2.setText(str_date);
                    } else if (timeline_type.equalsIgnoreCase(Global.TIMELINE_TYPE_UPLOADING)) {
                        layout3.setVisibility(View.VISIBLE);
                        statusIcon3.setImageResource(R.drawable.task_uploading);
                        taskStatus3.setText(mContext.getString(R.string.timeline_type_uploading));
                        txtTime3.setText(str_date);
                    } else if (timeline_type.equalsIgnoreCase(Global.TIMELINE_TYPE_SUBMITTED)) {
                        layout4.setVisibility(View.VISIBLE);
                        statusIcon4.setImageResource(R.drawable.task_submitted);
                        taskStatus4.setText(mContext.getString(R.string.timeline_type_submitted));
                        txtTime4.setText(str_date);
                    } else if (timeline_type.equalsIgnoreCase(Global.TIMELINE_TYPE_SAVEDRAFT)) {
                        layout1.setVisibility(View.VISIBLE);
                        statusIcon1.setImageResource(R.drawable.task_draft);
                        taskStatus1.setText(mContext.getString(R.string.timeline_type_savedraft));
                        txtTime1.setText(str_date);
                    } else if (timeline_type.equalsIgnoreCase(Global.TIMELINE_TYPE_FAILEDDRAFT)) {
                        layout0.setVisibility(View.VISIBLE);
                        statusIcon0.setImageResource(R.drawable.task_failed_draft);
                        taskStatus0.setText(mContext.getString(R.string.timeline_type_failed_draft_task));
                        taskDesc0.setVisibility(View.VISIBLE);
                        description = tl.getDescription();
                        taskDesc0.setText(description);
                        txtTime0.setText(str_date);
                    } else if (timeline_type.equalsIgnoreCase(Global.TIMELINE_TYPE_APPROVED)) {
                        layout4.setVisibility(View.VISIBLE);
                        statusIcon4.setImageResource(R.drawable.task_submitted);
                        taskStatus4.setText(mContext.getString(R.string.timeline_type_approved));
                        txtTime4.setText(str_date);
                    } else if (timeline_type.equalsIgnoreCase(Global.TIMELINE_TYPE_VERIFIED)) {
                        layout4.setVisibility(View.VISIBLE);
                        statusIcon4.setImageResource(R.drawable.task_submitted);
                        taskStatus4.setText(mContext.getString(R.string.timeline_type_verified));
                        txtTime4.setText(str_date);
                    } else if (timeline_type.equalsIgnoreCase(Global.TIMELINE_TYPE_REJECTED)) {
                        layout4.setVisibility(View.VISIBLE);
                        statusIcon4.setImageResource(R.drawable.task_rejected);
                        taskStatus4.setText(mContext.getString(R.string.timeline_type_rejected));
                        txtTime4.setText(str_date);
                    } else if (timeline_type.equalsIgnoreCase(Global.TIMELINE_TYPE_CHANGED)) {
                        layout4.setVisibility(View.VISIBLE);
                        statusIcon4.setImageResource(R.drawable.task_rejected);
                        taskStatus4.setText(mContext.getString(R.string.timeline_type_changed));
                        description = tl.getDescription();
                        taskDesc2.setText(description);
                        txtTime4.setText(str_date);
                    }
                }
            }
        }
    }
}

