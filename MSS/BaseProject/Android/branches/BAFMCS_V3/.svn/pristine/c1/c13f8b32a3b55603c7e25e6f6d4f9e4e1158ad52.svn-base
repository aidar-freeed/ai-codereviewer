package com.adins.mss.base.timeline;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.Message;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.Timeline;
import com.adins.mss.dao.TimelineType;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gigin.ginanjar
 */
public class TimelineManager {
    private Context context;

    public TimelineManager(Context context) {
        this.context = context;
    }

    /**
     * Gets List of Timeline from Database
     *
     * @param context
     * @return all Timeline
     */
    public static List<Timeline> getAllTimeline(Context context) {
        return TimelineDataAccess.getAll(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
    }

    public static List<Timeline> getAllTimelineWithLimitedDay(Context context, int range) {
        int minRange = 0 - range;
        try {
            if (minRange != 0) {
                List<Timeline> alltimeline = TimelineDataAccess.getAllDeletedTimeline(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), minRange);
                for (Timeline timeline : alltimeline) {
                    TimelineDataAccess.delete(context, timeline);
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
        if (minRange != 0) {
            return TimelineDataAccess.getAllWithLimitedDay(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), minRange);
        } else {
            return getAllTimeline(context);
        }
    }

    public static List<Timeline> getAllTimelineByTypeWithLimitedDay(Context context, int range, String timelineType) {
        int minRange = 0 - range;
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        List<Timeline> listTimelines;
        List<Timeline> newTimelines = new ArrayList<>();
        try {
            if (minRange != 0) {
                List<Timeline> alltimeline = TimelineDataAccess.getAllDeletedTimelineByType(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), minRange, timelineType);
                for (Timeline timeline : alltimeline) {
                    TimelineDataAccess.delete(context, timeline);
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
        if (minRange != 0) {
            listTimelines = TimelineDataAccess.getAllWithLimitedDayByType(context, uuidUser, minRange, timelineType);
        } else {
            listTimelines = TimelineDataAccess.getAllTimelineByType(context, uuidUser, timelineType);
        }

        if (listTimelines != null) {
            for (Timeline temp : listTimelines) {
                List<Timeline> groupTimeline = TimelineDataAccess.getTimelineByTask(context, uuidUser, temp.getUuid_task_h());
                Timeline timeline = groupTimeline.get(groupTimeline.size()-1);
                TimelineType type = TimelineTypeDataAccess.getTimelineTypebyType(context, timelineType);
                if (timeline.getUuid_timeline_type().equalsIgnoreCase(type.getUuid_timeline_type())) {
                    newTimelines.add(timeline);
                }
            }
        }

        return newTimelines;
    }

    public static List<Timeline> getAllTimelineTaskWithLimitedDay(Context context, int range, String timelineType, String priority) {
        int minRange = 0 - range;
        try {
            if (minRange != 0) {
                List<Timeline> alltimeline = TimelineDataAccess.getAllDeletedTimelineTask(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), minRange, timelineType, priority);
                for (Timeline timeline : alltimeline) {
                    TimelineDataAccess.delete(context, timeline);
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
        if (minRange != 0) {
            return TimelineDataAccess.getAllWithLimitedDayTask(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), minRange, timelineType, priority);
        } else {
            return TimelineDataAccess.getAllTimelineTask(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), timelineType, priority);
        }
    }

    public static Timeline getTimeline(Context context, String uuidUser, String uuidTimeline) {
        return TimelineDataAccess.getOneTimeline(context, uuidUser, uuidTimeline);
    }

    public static List<Timeline> getAllTimeline(Context context, String uuidUser) {
        return TimelineDataAccess.getAll(context, uuidUser);
    }

    public static Timeline getTimelineByDescription(Context context, String Description) {
        List<Timeline> list = getAllTimeline(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
        Timeline timeline = null;
        for (Timeline tempTimeline : list) {
            if (tempTimeline.getDescription().equals(Description)) {
                timeline = tempTimeline;
            }
        }
        return timeline;
    }

    /**
     * Method for insert Timeline to Database by timeline
     *
     * @param context  - Context
     * @param timeline - Timeline
     */
    public static void insertTimeline(Context context, Timeline timeline) {
        TimelineDataAccess.add(context, timeline);
    }

    /**
     * Method for insert Timeline to Database, use by Checkin and Checkout
     *
     * @param context       - context
     * @param description   - String description
     * @param latitude      - String latitude
     * @param longitude     - String longitude
     * @param usr_crt       - String User Create
     * @param dtm_crt       - String date time create
     * @param timeline_type - String timeline type
     * @param bitmapArray   - byte[] byte image
     */
    public static void insertTimeline(Context context, String description, String latitude, String longitude, String usr_crt, Date dtm_crt, String name, String address, String agreement_no, String amount_due, String overdue, String installment_no, String timeline_type, byte[] bitmapArray) {
        Context mContext;
        mContext = context;
        String uuid_timeline = Tool.getUUID();
        Date dtm_crt_server = null;
        String uuid_task_h = "";
        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        String uuid_message = "";
        String attdAddress = "";
        String priority = "";
        String isVerification = "";
        String collResult = "";
        String accountName = "";
        String productName = "";
        String statusCode = "";
        TimelineType timelineType = TimelineTypeDataAccess.getTimelineTypebyType(mContext, timeline_type);

        Timeline temptimeline = new Timeline(uuid_timeline, description, latitude, longitude, dtm_crt_server, name, address, agreement_no, amount_due, overdue, installment_no, attdAddress, priority, isVerification, collResult, accountName, productName, statusCode, usr_crt, dtm_crt, uuid_task_h, uuid_user, null, uuid_message, bitmapArray);
        temptimeline.setTimelineType(timelineType);
        User nUser = null;
        try {
            nUser = GlobalData.getSharedGlobalData().getUser();
        } catch (Exception e) {
            FireCrash.log(e);
        }
        if (nUser != null)
            temptimeline.setUser(nUser);
        else
            temptimeline.setUser(GlobalData.getSharedGlobalData().getUser());
        TimelineDataAccess.add(mContext, temptimeline);
    }

    /**
     * Method for insert Timeline to Database, use by Submitted task and Server Task
     *
     * @param context       - Context
     * @param description   - String Description
     * @param usr_crt       - String user create
     * @param dtm_crt       - Date date time create
     * @param timeline_type - String timeline type
     * @param uuid_task_h   - String uuid  task H
     */
    public static void insertTimeline(Context context, String uuid_task_h, String description, String usr_crt, Date dtm_crt, String name, String address, String agreement_no, String amount_due, String overdue, String installment_no, String timeline_type) {
        Context mContext;
        mContext = context;
        String uuid_timeline = Tool.getUUID();
        Date dtm_crt_server = null;
        String latitude = "0.0";
        String longitude = "0.0";
        String attdAddress = "";
        String uuid_message = "";
        String priority = "";
        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        String isVerification = "";
        String collResult = "";
        String accountName = "";
        String productName = "";
        String statusCode = "";

        TimelineType timelineType = TimelineTypeDataAccess.getTimelineTypebyType(mContext, timeline_type);

        Timeline temptimeline = new Timeline(uuid_timeline, description, latitude, longitude, dtm_crt_server, name, address, agreement_no, amount_due, overdue, installment_no, attdAddress, priority, isVerification, collResult, accountName, productName, statusCode, usr_crt, dtm_crt, uuid_task_h, uuid_user, null, uuid_message, null);
        temptimeline.setTimelineType(timelineType);
        User nUser = null;
        try {
            nUser = GlobalData.getSharedGlobalData().getUser();
        } catch (Exception e) {
            FireCrash.log(e);
        }
        if (nUser != null)
            temptimeline.setUser(nUser);
        else
            temptimeline.setUser(GlobalData.getSharedGlobalData().getUser());
        TimelineDataAccess.add(mContext, temptimeline);
    }

    /**
     * Method for insert Timeline to Database by Message
     *
     * @param context - Context
     * @param message
     */
    public static void insertTimeline(Context context, Message message) {
        Context mContext;
        mContext = context;
        String uuid_timeline = Tool.getUUID();
        Date dtm_crt_server = message.getDtm_crt_server();
        String uuid_task_h = "";
        String description = message.getMessage();
        String latitude = "0.0";
        String longitude = "0.0";
        String name = "";
        String address = "";
        String agreement_no = "";
        String amount_due = "";
        String overdue = "";
        String installment_no = "";
        String attdAddress = "";
        String priority = "";
        String uuid_message = message.getUuid_message();
        String usr_crt = message.getUsr_crt();
        Date dtm_crt = message.getDtm_crt_server();
        String timeline_type = Global.TIMELINE_TYPE_MESSAGE;
        String uuid_user = message.getUuid_user();
        String isVerification = "";
        String collResult = "";
        String accountName = "";
        String productName = "";
        String statusCode = "";

        TimelineType timelineType = TimelineTypeDataAccess.getTimelineTypebyType(mContext, timeline_type);

        Timeline temptimeline = new Timeline(uuid_timeline, description, latitude, longitude, dtm_crt_server, name, address, agreement_no, amount_due, overdue, installment_no, attdAddress, priority, isVerification, collResult, accountName, productName, statusCode, usr_crt, dtm_crt, uuid_task_h, uuid_user, null, uuid_message, null);
        temptimeline.setTimelineType(timelineType);
        User nUser = null;
        try {
            nUser = GlobalData.getSharedGlobalData().getUser();
        } catch (Exception e) {
            FireCrash.log(e);
        }
        if (nUser != null)
            temptimeline.setUser(nUser);
        else
            temptimeline.setUser(GlobalData.getSharedGlobalData().getUser());
        temptimeline.setMessage(message);
        TimelineDataAccess.add(mContext, temptimeline);
    }

    /**
     * Method for insert Timeline to Database by Message
     *
     * @param context - Context
     */
    public static void insertTimelinePushNotification(Context context, String description) {
        Context mContext;
        mContext = context;
        String uuid_timeline = Tool.getUUID();
        Date dtm_crt_server = new Date();
        String uuid_task_h = "";
        String latitude = "0.0";
        String longitude = "0.0";
        String name = "";
        String address = "";
        String agreement_no = "";
        String amount_due = "";
        String overdue = "";
        String installment_no = "";
        String attdAddress = "";
        String priority = "";
        String uuid_message = Tool.getUUID();
        String usr_crt = new Date().toString();
        Date dtm_crt = new Date();
        String timeline_type = Global.TIMELINE_TYPE_PUSH_NOTIFICATION;
        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        String isVerification = "";
        String collResult = "";
        String accountName = "";
        String productName = "";
        String statusCode = "";

        TimelineType timelineType = TimelineTypeDataAccess.getTimelineTypebyType(mContext, timeline_type);

        Timeline temptimeline = new Timeline(uuid_timeline, description, latitude, longitude, dtm_crt_server, name, address, agreement_no, amount_due, overdue, installment_no, attdAddress, priority, isVerification, collResult, accountName, productName, statusCode, usr_crt, dtm_crt, uuid_task_h, uuid_user, null, uuid_message, null);
        temptimeline.setTimelineType(timelineType);
        User nUser = null;
        try {
            nUser = GlobalData.getSharedGlobalData().getUser();
        } catch (Exception e) {
            FireCrash.log(e);
        }
        if (nUser != null)
            temptimeline.setUser(nUser);
        else
            temptimeline.setUser(GlobalData.getSharedGlobalData().getUser());

        TimelineDataAccess.add(mContext, temptimeline);
    }

    /**
     * @param context
     * @param taskH
     */
    public static void insertTimeline(Context context, TaskH taskH) {
        Context mContext;
        mContext = context;
        String uuid_timeline = Tool.getUUID();
        Date dtm_crt_server = taskH.getAssignment_date();
        String uuid_task_h = taskH.getUuid_task_h();
        String description = "";
        String name = taskH.getCustomer_name();
        String address = taskH.getCustomer_address();
        String agreement_no = "";
        String amount_due = "";
        String overdue = "";
        String installment_no = "";
        String isVerification = "";
        String collResult = "";
        String accountName = "";
        String productName = "";
        String statusCode = "";

        if (TaskHDataAccess.STATUS_TASK_VERIFICATION.equals(taskH.getStatus()) ||
                TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD.equals(taskH.getStatus()) ||
                (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION))) {
            isVerification = Global.FORM_TYPE_VERIFICATION;
        } else if (TaskHDataAccess.STATUS_TASK_APPROVAL.equals(taskH.getStatus()) ||
                TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD.equals(taskH.getStatus()) ||
                (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL))) {
            isVerification = Global.FORM_TYPE_APPROVAL;
        }

        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
            // olivia : tambahan simpan collection result di timeline
            TaskD taskdCollResult = TaskDDataAccess.getOneFromTaskDWithTag(context, taskH.getUuid_task_h(), Global.TAG_COLLECTION_RESULT);
            if (taskdCollResult != null) {
                Lookup lookup = LookupDataAccess.getOneByCode(context, taskdCollResult.getUuid_lookup(), taskdCollResult.getLov());
                if (lookup != null) {
                    TaskD taskdTotalBayar = TaskDDataAccess.getOneFromTaskDWithTag(context, taskH.getUuid_task_h(), Global.TAG_TOTAL);
                    TaskD tasdPTP = TaskDDataAccess.getOneFromTaskDWithTag(context, taskH.getUuid_task_h(), Global.TAG_PTP);
                    if (taskdTotalBayar != null) {
                        collResult = lookup.getValue() + " - " + taskdTotalBayar.getText_answer();
                    } else if (tasdPTP != null) {
                        String ptpDate = "";
                        if (!tasdPTP.getText_answer().contains("/")) {
                            String format = Global.DATE_STR_FORMAT_GSON;
                            Date date = null;
                            try {
                                date = Formatter.parseDate(tasdPTP.getText_answer(), format);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            ptpDate = Formatter.formatDate(date, Global.DATE_STR_FORMAT);
                        } else {
                            ptpDate = tasdPTP.getText_answer();
                        }
                        collResult = lookup.getValue() + " - " + ptpDate;
                    } else {
                        collResult = lookup.getValue();
                    }
                }
            }

            agreement_no = taskH.getAppl_no();
            if (taskH.getAmt_due() == null || "".equals(taskH.getAmt_due())) {
                amount_due = "-";
            } else {
                amount_due = Tool.separateThousand(taskH.getAmt_due());
            }

            if (taskH.getOd() == null || "".equals(taskH.getOd())) {
                overdue = "-";
            } else {
                overdue = taskH.getOd() + " " + context.getString(R.string.txtDay);
            }

            if (taskH.getInst_no() == null || "".equals(taskH.getInst_no())) {
                installment_no = "-";
            } else {
                installment_no = taskH.getInst_no();
            }
        }

        if (taskH.getMessage() != null)
            description = taskH.getMessage();

        String latitude = taskH.getLatitude();
        String longitude = taskH.getLongitude();
        String attdAddress = "";
        String uuid_message = "";
        String usr_crt = taskH.getUsr_crt();
        String priority = taskH.getPriority();

        Date dtm_crt = null;

        String timeline_type = null;
        if (TaskHDataAccess.STATUS_SEND_SENT.equals(taskH.getStatus())) {
            dtm_crt = Tool.getSystemDateTime();
            timeline_type = Global.TIMELINE_TYPE_SUBMITTED;

        } else if (TaskHDataAccess.STATUS_SEND_INIT.equals(taskH.getStatus())) {
            timeline_type = Global.TIMELINE_TYPE_TASK;
            dtm_crt = taskH.getAssignment_date();
            if (dtm_crt == null)
                dtm_crt = Tool.getSystemDateTime();

        } else if (TaskHDataAccess.STATUS_TASK_VERIFICATION.equals(taskH.getStatus()) ||
                TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD.equals(taskH.getStatus())) {
            timeline_type = Global.TIMELINE_TYPE_VERIFICATION;
            dtm_crt = taskH.getAssignment_date();
            if (dtm_crt == null) {
                dtm_crt = taskH.getDtm_crt();
            }
            if (dtm_crt == null) {
                dtm_crt = Tool.getSystemDateTime();
            }

        } else if (TaskHDataAccess.STATUS_TASK_APPROVAL.equals(taskH.getStatus()) ||
                TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD.equals(taskH.getStatus())) {
            timeline_type = Global.TIMELINE_TYPE_APPROVAL;
            dtm_crt = taskH.getAssignment_date();
            if (dtm_crt == null) {
                dtm_crt = taskH.getDtm_crt();
            }
            if (dtm_crt == null) {
                dtm_crt = Tool.getSystemDateTime();
            }

        } else if (TaskHDataAccess.STATUS_SEND_DOWNLOAD.equals(taskH.getStatus())) {
            timeline_type = Global.TIMELINE_TYPE_TASK;
            dtm_crt = taskH.getAssignment_date();

        }
        else if (TaskHDataAccess.STATUS_SEND_PENDING.equals(taskH.getStatus())) {
            dtm_crt = Tool.getSystemDateTime();
            timeline_type = Global.TIMELINE_TYPE_PENDING;

        } else if (TaskHDataAccess.STATUS_SEND_UPLOADING.equals(taskH.getStatus())) {
            dtm_crt = Tool.getSystemDateTime();
            timeline_type = Global.TIMELINE_TYPE_UPLOADING;

        } else if (TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(taskH.getStatus())) {
            dtm_crt = Tool.getSystemDateTime();
            timeline_type = Global.TIMELINE_TYPE_SAVEDRAFT;

        } else if (TaskHDataAccess.STATUS_SEND_FAILEDDRAFT.equals(taskH.getStatus())) {
            dtm_crt = Tool.getSystemDateTime();
            taskH.setSubmit_date(null);
            taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
            TaskHDataAccess.addOrReplace(context, taskH);
            timeline_type = Global.TIMELINE_TYPE_FAILEDDRAFT;

        }  else if (TaskHDataAccess.STATUS_TASK_CHANGED.equals(taskH.getStatus())) {
            if (taskH.getSubmit_date() != null)
                dtm_crt = taskH.getSubmit_date();
            else {
                dtm_crt = Tool.getSystemDateTime();
            }
            timeline_type = Global.TIMELINE_TYPE_CHANGED;
        }

        String uuid_user = taskH.getUuid_user();
        TimelineType timelineType = TimelineTypeDataAccess.getTimelineTypebyType(mContext, timeline_type);
        TimelineType timelineFailedType = TimelineTypeDataAccess.getTimelineTypebyType(mContext, Global.TIMELINE_TYPE_FAILEDDRAFT);

        Timeline timeline = TimelineDataAccess.getOneTimelineByTaskH(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), uuid_task_h, timelineType.getUuid_timeline_type());
        List<Timeline> listTimeline = TimelineDataAccess.getTimelineByTaskH(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), uuid_task_h, timelineType.getUuid_timeline_type());
        List<Timeline> listTimelineDraftFailed = TimelineDataAccess.getTimelineByTaskH(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), uuid_task_h, timelineFailedType.getUuid_timeline_type());

        if (timeline == null || ((!listTimelineDraftFailed.isEmpty()) && (listTimeline.size()< listTimelineDraftFailed.size()+1))) {

            Timeline temptimeline = new Timeline(uuid_timeline, description, latitude, longitude, dtm_crt_server, name, address, agreement_no, amount_due, overdue, installment_no, attdAddress, priority, isVerification, collResult, accountName, productName, statusCode, usr_crt, dtm_crt, uuid_task_h, uuid_user, null, uuid_message, null);
            temptimeline.setTimelineType(timelineType);
            User nUser = null;
            try {
                nUser = GlobalData.getSharedGlobalData().getUser();
            } catch (Exception e) {
                FireCrash.log(e);
            }
            if (nUser != null)
                temptimeline.setUser(nUser);
            else
                temptimeline.setUser(GlobalData.getSharedGlobalData().getUser());
            temptimeline.setTaskH(taskH);
            TimelineDataAccess.add(mContext, temptimeline);
        } else {
            timeline.setName(taskH.getCustomer_name());
            timeline.setAddress(taskH.getCustomer_address());
            timeline.setTimelineType(timelineType);
            timeline.setDescription(description);
            timeline.setDtm_crt(dtm_crt);
            TimelineDataAccess.addOrReplace(mContext, timeline);
        }
    }

    public static void insertTimeline(Context context, TaskH taskH, boolean isVerified, boolean isReject) {
        Context mContext;
        mContext = context;
        String uuid_timeline = Tool.getUUID();
        Date dtm_crt_server = taskH.getAssignment_date();
        String uuid_task_h = taskH.getUuid_task_h();
        String description = "";
        String name = taskH.getCustomer_name();
        String address = taskH.getCustomer_address();
        String agreement_no = "";
        String amount_due = "";
        String overdue = "";
        String installment_no = "";
        String priority = taskH.getPriority();
        String isVerification = "";
        String collResult = "";
        String accountName = "";
        String productName = "";
        String statusCode = "";

        if (TaskHDataAccess.STATUS_TASK_VERIFICATION.equals(taskH.getStatus()) ||
                TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD.equals(taskH.getStatus()) ||
                (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION))) {
            isVerification = Global.FORM_TYPE_VERIFICATION;
        } else if (TaskHDataAccess.STATUS_TASK_APPROVAL.equals(taskH.getStatus()) ||
                TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD.equals(taskH.getStatus()) ||
                (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL))) {
            isVerification = Global.FORM_TYPE_APPROVAL;
        }

        if (taskH.getMessage() != null)
            description = taskH.getMessage();

        String latitude = taskH.getLatitude();
        String longitude = taskH.getLongitude();
        String attdAddress = "";
        String uuid_message = "";
        String usr_crt = taskH.getUsr_crt();

        Date dtm_crt = null;

        String timeline_type = null;
        if (isVerified) {
            if (isReject) {
                dtm_crt = taskH.getSubmit_date();
                timeline_type = Global.TIMELINE_TYPE_REJECTED;
            } else {
                dtm_crt = taskH.getSubmit_date();
                timeline_type = Global.TIMELINE_TYPE_VERIFIED;
            }
        } else {
            if (isReject) {
                dtm_crt = taskH.getSubmit_date();
                timeline_type = Global.TIMELINE_TYPE_REJECTED;
            } else {
                dtm_crt = taskH.getSubmit_date();
                timeline_type = Global.TIMELINE_TYPE_APPROVED;
            }
        }

        String uuid_user = taskH.getUuid_user();

        TimelineType timelineType = TimelineTypeDataAccess.getTimelineTypebyType(mContext, timeline_type);

        Timeline timeline = TimelineDataAccess.getOneTimelineByTaskH(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), uuid_task_h, timelineType.getUuid_timeline_type());
        if (timeline == null) {
            Timeline temptimeline = new Timeline(uuid_timeline, description, latitude, longitude, dtm_crt_server, name, address, agreement_no, amount_due, overdue, installment_no, attdAddress, priority, isVerification, collResult, accountName, productName, statusCode, usr_crt, dtm_crt, uuid_task_h, uuid_user, null, uuid_message, null);
            temptimeline.setTimelineType(timelineType);
            User nUser = null;
            try {
                nUser = GlobalData.getSharedGlobalData().getUser();
            } catch (Exception e) {
                FireCrash.log(e);
            }
            if (nUser != null)
                temptimeline.setUser(nUser);
            else
                temptimeline.setUser(GlobalData.getSharedGlobalData().getUser());
            temptimeline.setTaskH(taskH);
            TimelineDataAccess.add(mContext, temptimeline);
        }
    }


    public static void insertTimeline(Context context, LocationInfo locationInfo, String description, String attdAddress, byte[] bitmapArray) {
        Context mContext;
        mContext = context;
        String uuid_timeline = Tool.getUUID();
        Date dtm_crt_server = null;
        String uuid_task_h = "";
        String latitude = locationInfo.getLatitude();
        String longitude = locationInfo.getLongitude();
        String uuid_message = "";
        String name = "";
        String address = "";
        String agreement_no = "";
        String amount_due = "";
        String overdue = "";
        String installment_no = "";
        String priority = "";
        String usr_crt = locationInfo.getUsr_crt();
        Date dtm_crt = locationInfo.getDtm_crt();
        String timeline_type = null;
        String isVerification = "";
        String collResult = "";
        String accountName = "";
        String productName = "";
        String statusCode = "";

        if (Global.LOCATION_TYPE_CHECKIN.equals(locationInfo.getLocation_type()))
            timeline_type = Global.TIMELINE_TYPE_CHECKIN;
        else if (Global.TIMELINE_TYPE_CHECKOUT.equals(locationInfo.getLocation_type()))
            timeline_type = Global.TIMELINE_TYPE_CHECKOUT;
        String uuid_user = locationInfo.getUuid_user();

        TimelineType timelineType = TimelineTypeDataAccess.getTimelineTypebyType(mContext, timeline_type);

        Timeline temptimeline = new Timeline(uuid_timeline, description, latitude, longitude, dtm_crt_server, name, address, agreement_no, amount_due, overdue, installment_no, attdAddress, priority, isVerification, collResult, accountName, productName, statusCode, usr_crt, dtm_crt, uuid_task_h, uuid_user, null, uuid_message, bitmapArray);
        temptimeline.setTimelineType(timelineType);
        User nUser = null;
        try {
            nUser = GlobalData.getSharedGlobalData().getUser();
        } catch (Exception e) {
            FireCrash.log(e);
        }
        if (nUser != null)
            temptimeline.setUser(nUser);
        else
            temptimeline.setUser(GlobalData.getSharedGlobalData().getUser());
        TimelineDataAccess.add(mContext, temptimeline);
    }

    /**
     * Method for insert Timeline List to Database
     *
     * @param context  - Context
     * @param timeline - Timeline
     */
    public static void insertTimeline(Context context, List<Timeline> timeline) {
        TimelineDataAccess.add(context, timeline);
    }

    /**
     * Gets List of Timeline from Database
     *
     * @param context
     * @param timelineType
     * @return all timeline by type
     */
    public List<Timeline> getAllTimelineByType(Context context, String timelineType) {
        TimelineType type = TimelineTypeDataAccess.getTimelineTypebyType(context, timelineType);
        String uuid_timeline_type = type.getUuid_timeline_type();
        return TimelineDataAccess.getAll(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), uuid_timeline_type);
    }
}


