package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;
import android.database.Cursor;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.QuestionSetDao;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskDDao;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskHDao;
import com.adins.mss.foundation.db.DaoOpenHelper;
import com.adins.mss.foundation.formatter.Formatter;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

public class TaskDDataAccess {

    public static final int IMAGE_ONLY = 1;
    public static final int NON_IMAGE_ONLY = 2;
    public static final int ALL_TASK = 3;

    /**
     * use to generate dao session that you can access modelDao
     *
     * @param context --> context from activity
     * @return
     */
    protected static DaoSession getDaoSession(Context context) {
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get taskD dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static TaskDDao getTaskDDao(Context context) {
        return getDaoSession(context).getTaskDDao();
    }

    /**
     * get taskH dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static TaskHDao getTaskHDao(Context context) {
        return getDaoSession(context).getTaskHDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add taskD as entity
     *
     * @param context
     * @param taskD
     */
    public static void add(Context context, TaskD taskD) {
        getTaskDDao(context).insertInTx(taskD);
        getDaoSession(context).clear();
    }

    /**
     * add taskD as list entity
     *
     * @param context
     * @param taskDList
     */
    public static void add(Context context, List<TaskD> taskDList) {
        getTaskDDao(context).insertInTx(taskDList);
        getDaoSession(context).clear();
    }

    /**
     * addOrReplace taskD as entity
     *
     * @param context
     * @param taskD
     */
    public static void addOrReplace(Context context, TaskD taskD) {
        getTaskDDao(context).insertOrReplaceInTx(taskD);
        getDaoSession(context).clear();
    }

    /**
     * addOrReplace taskD as list entity
     *
     * @param context
     * @param taskDList
     */
    public static void addOrReplace(Context context, List<TaskD> taskDList) {
        getTaskDDao(context).insertOrReplaceInTx(taskDList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getTaskDDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param taskD
     */
    public static void delete(Context context, TaskD taskD) {
        getTaskDDao(context).delete(taskD);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by keyTaskH
     *
     * @param context
     * @param keyTaskH
     */
    public static void delete(Context context, String keyTaskH) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(keyTaskH));
        qb.build().forCurrentThread();
        getTaskDDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * update taskD
     *
     * @param context
     * @param taskD
     */
    public static void update(Context context, TaskD taskD) {
        getTaskDDao(context).update(taskD);
        getDaoSession(context).clear();
    }

    /**
     * This method is used to retrieve task detail by taskId
     *
     * @param context
     * @param uuidUser
     * @param taskId    - String taskId
     * @param withImage
     * @return
     */
    public static List<TaskD> getAllByTaskId(Context context, String uuidUser, String taskId, int withImage) {
        QueryBuilder<TaskH> qb0 = getTaskHDao(context).queryBuilder();
        qb0.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Task_id.eq(taskId));
        qb0.build();

        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(qb0.list().get(0).getUuid_task_h()));
        if (withImage == IMAGE_ONLY) {
            qb.and(TaskDDao.Properties.Image.isNotNull(), null);
        } else if (withImage == NON_IMAGE_ONLY) {
            qb.and(TaskDDao.Properties.Image.eq(null), null);
        }
        qb.build();
        return qb.list();
    }

    public static List<TaskD> getAll(Context context, String uuid_task_h, int withImage) {
        QueryBuilder<TaskH> qb0 = getTaskHDao(context).queryBuilder();
        qb0.where(TaskHDao.Properties.Uuid_user.eq(GlobalData.getSharedGlobalData().getUser().getUuid_user()),
                TaskHDao.Properties.Uuid_task_h.eq(uuid_task_h));
        qb0.build().forCurrentThread();

        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        if (!qb0.list().isEmpty()) {
            if (qb0.list().get(0) != null) {
                qb.where(TaskDDao.Properties.Uuid_task_h.eq(qb0.list().get(0).getUuid_task_h()));
                if (withImage == IMAGE_ONLY) {
                    qb.and(TaskDDao.Properties.Image.isNotNull(), null);
                } else if (withImage == NON_IMAGE_ONLY) {
                    qb.and(TaskDDao.Properties.Image.eq(null), null);
                }
                qb.build();
                return qb.list();
            } else {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

    public static List<TaskD> getAll(Context context, String uuid_task_h) {

        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(uuid_task_h));
        qb.build();
        return qb.list();
    }

    /**
     * This is used to get list of pending image from a taskId
     *
     * @param context
     * @param taskId
     * @return
     */
    public static List<TaskD> getImagePendingByTaskId(Context context, String taskId) {
        TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(taskH.getUuid_task_h()),
                TaskDDao.Properties.Image.isNotNull(),
                TaskDDao.Properties.Is_sent.eq(Global.TRUE_STRING));
        qb.build();
        return qb.list();
    }

    /**
     * Get count of pending image of a taskId
     *
     * @param context
     * @param taskId
     * @return
     */
    public static int getCountImagePendingByTaskId(Context context, String taskId) {
        return getImagePendingByTaskId(context, taskId).size();
    }

    /**
     * Change status is_sent for a task detail
     *
     * @param context
     * @param uuidTaskD
     * @param isSent
     */
    public static void changeStatus(Context context, String uuidTaskD, boolean isSent) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_d.eq(uuidTaskD));
        qb.build();
        TaskD taskD = qb.list().get(0);
        taskD.setIs_sent(Formatter.booleanToString(isSent));
        getTaskDDao(context).updateInTx(taskD);
    }

    /**
     * Change status is_sent for list of task detail
     *
     * @param context
     * @param listTaskD
     * @param isSent
     */
    public static void changeStatusList(Context context, List<TaskD> listTaskD, boolean isSent) {
        for (TaskD taskDBean : listTaskD) {
            changeStatus(context, taskDBean.getUuid_task_d(), isSent);
        }
        getDaoSession(context).clear();
    }

    /**
     * GetOne data by uuidTaskD
     *
     * @param context
     * @param uuidTaskD
     * @return
     */
    public static TaskD getOne(Context context, String uuidTaskD) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_d.eq(uuidTaskD));
        qb.build();
        if (qb.list().size() == 1)
            return qb.list().get(0);
        else
            return null;
    }

    //bong 7 may 15 - for get answer fo a question

    /**
     * GetOne matched answer by uuidTaskH, questionId, and questionGroupId
     *
     * @param context
     * @param uuidTaskH
     * @param questionId
     * @param questionGroupId
     * @return
     */
    public static TaskD getMatchDetailWithQuestion(Context context, String uuidTaskH, String questionId, String questionGroupId) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(uuidTaskH),
                TaskDDao.Properties.Question_id.eq(questionId),
                TaskDDao.Properties.Question_group_id.eq(questionGroupId));
        qb.build();
        if (qb.list().size() == 1)
            return qb.list().get(0);
        else
            return null;
    }

    public static TaskD getMatchDetailForPrint(Context context, String uuidTaskH, String questionId) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(uuidTaskH),
                TaskDDao.Properties.Question_id.eq(questionId));
        qb.build();
        if (qb.list().size() == 1)
            return qb.list().get(0);
        else
            return null;
    }

    /**
     * To get taskD where Tag is TOTAL
     *
     * @param context
     * @return List<TaskD>
     */
    public static List<TaskD> getTaskDTagTotal(Context context, String uuidUser) {
        List<String> depositListUuidTaskH = DepositReportDDataAccess.getAllUuid(context);
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(new WhereCondition.StringCondition
                (" QUESTION_ID in (select QUESTION_ID from  MS_QUESTIONSET where TAG ='" + Global.TAG_TOTAL + "' ) "));
        qb.where(new WhereCondition.StringCondition
                (" UUID_TASK_H in (select UUID_TASK_H from  TR_TASK_H where STATUS ='" + TaskHDataAccess.STATUS_SEND_SENT +
                        "' AND UUID_USER = '" + uuidUser + "' ) "));
        qb.where(TaskDDao.Properties.Uuid_task_h.notIn(depositListUuidTaskH));
        qb.orderAsc(TaskDDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<TaskD> getTaskDTagTotalbyBatchId(Context context, String batchId){

        List<String> depositListUuidTaskH = new ArrayList<String>();
        depositListUuidTaskH =   DepositReportDDataAccess.getAllUuid(context);


        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();

        qb.where(new WhereCondition.StringCondition
                (" QUESTION_ID in (select QUESTION_ID from  MS_QUESTIONSET where TAG ='"+Global.TAG_TOTAL+"' ) "));

        qb.where(new WhereCondition.StringCondition
                (" UUID_TASK_H in (select UUID_TASK_H from  TR_TASK_H where STATUS ='"+TaskHDataAccess.STATUS_SEND_SENT+"' ) "));

        qb.where(new WhereCondition.StringCondition
                (" UUID_TASK_H in (select UUID_TASK_H from TR_TASK_H where BATCH_ID ='"+batchId+"' ) "));

        qb.where(TaskDDao.Properties.Uuid_task_h.notIn(depositListUuidTaskH));


        qb.build();

        return qb.list();
    }

    public static TaskD getTaskDTagTagihanByTaskH(Context context, String uuidTaskH){

        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();

        qb.where(new WhereCondition.StringCondition
                (" QUESTION_ID in (select QUESTION_ID from  MS_QUESTIONSET where TAG ='"+Global.TAG_TOTAL_TAGIHAN+"' ) "));

        qb.where(TaskDDao.Properties.Uuid_task_h.eq(uuidTaskH));


        qb.build();

        if(qb.list().size()==1)
            return qb.list().get(0);
        else
            return null;
    }

    public static List<TaskD> getAgrementNo(Context context, String uuid_task_h) {

        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(uuid_task_h),
                TaskDDao.Properties.Question_label.eq(Global.TAG_AGREEMENT_NO));


        qb.build();

        return qb.list();
    }

    /**
     * Get list of unsent image
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<TaskD> getUnsentImage(Context context, String uuidUser) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.whereOr(TaskDDao.Properties.Is_sent.eq("0"), TaskDDao.Properties.Is_sent.isNull());
        qb.where(TaskDDao.Properties.Image.isNotNull());
        qb.where(new WhereCondition.StringCondition
                (" UUID_TASK_H in (select UUID_TASK_H from  TR_TASK_H where UUID_USER ='" + uuidUser +
                        "' AND " + TaskHDao.Properties.Status.columnName + " = '" + TaskHDataAccess.STATUS_SEND_UPLOADING + "' ) "));
        qb.build();

        return qb.list();
    }

    public static List<TaskD> getUnsentImageByUuidTaskH(Context context, String uuidUser, String uuid_task_h) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.whereOr(TaskDDao.Properties.Is_sent.eq("0"), TaskDDao.Properties.Is_sent.isNull());
        qb.where(TaskDDao.Properties.Image.isNotNull());
        qb.where(new WhereCondition.StringCondition
                (" UUID_TASK_H in (select UUID_TASK_H from  TR_TASK_H where UUID_TASK_H = '" + uuid_task_h + "' AND UUID_USER ='" + uuidUser +
                        "' AND " + TaskHDao.Properties.Status.columnName + " = '" + TaskHDataAccess.STATUS_SEND_UPLOADING + "' ) "));
        qb.build();
        return qb.list();
    }

    /**
     * Get list of unsent image
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<String> getUnsentImageListKey(Context context, String uuidUser) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.whereOr(TaskDDao.Properties.Is_sent.eq("0"), TaskDDao.Properties.Is_sent.isNull());
        qb.where(TaskDDao.Properties.Image.isNotNull());
        qb.where(new WhereCondition.StringCondition
                (" UUID_TASK_H in (select UUID_TASK_H from  TR_TASK_H where UUID_USER ='" + uuidUser +
                        "' AND " + TaskHDao.Properties.Status.columnName + " = '" + TaskHDataAccess.STATUS_SEND_UPLOADING + "' ) "));
        qb.build();

        List<TaskD> taskDList = qb.list();
        List<String> listKey = new ArrayList<>();
        for (TaskD taskD : taskDList) {
            listKey.add(taskD.getUuid_task_h());
        }
        taskDList = null;

        return listKey;
    }


    public static TaskD getOneUnsentImageByUuidTaskH(Context context, String uuidUser, String uuid_task_h) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.whereOr(TaskDDao.Properties.Is_sent.eq("0"), TaskDDao.Properties.Is_sent.isNull());
        qb.where(TaskDDao.Properties.Image.isNotNull());
        qb.where(new WhereCondition.StringCondition
                (" UUID_TASK_H in (select UUID_TASK_H from  TR_TASK_H where UUID_TASK_H = '" + uuid_task_h + "' AND UUID_USER ='" + uuidUser +
                        "' AND " + TaskHDao.Properties.Status.columnName + " = '" + TaskHDataAccess.STATUS_SEND_UPLOADING + "' ) "));
        qb.build();
        if (!qb.list().isEmpty()) {
            return qb.list().get(0);
        } else {
            return null;
        }
    }

    public static List<TaskD> getPendingImageByUuidTaskH(Context context, String uuidUser, String uuid_task_h) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.whereOr(TaskDDao.Properties.Is_sent.eq("0"), TaskDDao.Properties.Is_sent.isNull());
        qb.where(TaskDDao.Properties.Image.isNotNull());
        qb.where(new WhereCondition.StringCondition
                (" UUID_TASK_H in (select UUID_TASK_H from  TR_TASK_H where UUID_TASK_H = '" + uuid_task_h + "' AND UUID_USER ='" + uuidUser +
                        "' AND " + TaskHDao.Properties.Status.columnName + " = '" + TaskHDataAccess.STATUS_SEND_PENDING + "' ) "));
        qb.build();
        return qb.list();
    }

    public static List<TaskD> getTaskDWithImageByUuidTaskH(Context context, String uuid_task_h) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(uuid_task_h)
                , TaskDDao.Properties.Image.isNotNull());
        qb.build();
        return qb.list();
    }

    public static boolean isTaskPaid(Context context, String uuidUser, String uuidTaskH) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(new WhereCondition.StringCondition(" TEXT_ANSWER <> '' AND  TEXT_ANSWER != '0' AND TEXT_ANSWER IS NOT NULL AND" +
                " QUESTION_ID in (select QUESTION_ID FROM MS_QUESTIONSET WHERE TAG = '" + Global.TAG_TOTAL + "' ) AND" +
                " UUID_TASK_H in (select UUID_TASK_H from  TR_TASK_H where UUID_TASK_H = '" + uuidTaskH + "' AND UUID_USER ='" + uuidUser + "' ) "));
        qb.build();
        qb.list();
        return !qb.list().isEmpty();
    }

    public static boolean isTaskPTP(Context context, String uuidUser, String uuidTaskH) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(new WhereCondition.StringCondition(" QUESTION_ID in (select QUESTION_ID FROM MS_QUESTIONSET WHERE TAG = '" + Global.TAG_PTP + "' ) AND" +
                " UUID_TASK_H in (select UUID_TASK_H from  TR_TASK_H where UUID_TASK_H = '" + uuidTaskH + "' AND UUID_USER ='" + uuidUser + "' ) "));
        qb.build();
        qb.list();
        return !qb.list().isEmpty();
    }

    public static List<TaskD> getPaid(Context context) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(new WhereCondition.StringCondition(" TEXT_ANSWER <> '' AND  TEXT_ANSWER IS NOT NULL AND" +
                " QUESTION_ID in (select QUESTION_ID FROM MS_QUESTIONSET WHERE TAG = '" + Global.TAG_TOTAL + "' ) " +
                "AND UUID_TASK_H IN (SELECT UUID_TASK_H FROM TR_TASK_H where strftime('%d',ASSIGNMENT_DATE) = strftime('%d','now'))" +
                "AND UUID_TASK_H IN (SELECT UUID_TASK_H FROM TR_DEPOSITREPORT_D) "));
        qb.build();

        return qb.list();
    }

    public static List<TaskD> getAllPaid(Context context) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(new WhereCondition.StringCondition(" TEXT_ANSWER <> '' AND  TEXT_ANSWER IS NOT NULL AND" +
                " QUESTION_ID in (select QUESTION_ID FROM MS_QUESTIONSET WHERE TAG = '" + Global.TAG_TOTAL + "' ) " +
                "AND UUID_TASK_H IN (SELECT UUID_TASK_H FROM TR_DEPOSITREPORT_D) "));
        qb.build();

        return qb.list();
    }

    public static List<TaskD> getFail(Context context) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(new WhereCondition.StringCondition("TEXT_ANSWER = '' OR  TEXT_ANSWER IS NULL AND" +
                " QUESTION_ID in (select QUESTION_ID FROM MS_QUESTIONSET WHERE TAG = '" + Global.TAG_TOTAL + "' ) " +
                "AND UUID_TASK_H IN (SELECT UUID_TASK_H FROM TR_TASK_H where strftime('%d',ASSIGNMENT_DATE) = strftime('%d',CURRENT_TIMESTAMP))" +
                "AND UUID_TASK_H IN (SELECT UUID_TASK_H FROM TR_DEPOSITREPORT_D) "));
        qb.build();

        return qb.list();
    }

    public static List<TaskD> getAllFail(Context context) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(new WhereCondition.StringCondition(
                " QUESTION_ID in (select QUESTION_ID FROM MS_QUESTIONSET WHERE TAG = '" + Global.TAG_TOTAL + "' ) "
        ));
        qb.build();

        return qb.list();
    }

    public static List<TaskD> listOfBatch(Context context) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(new WhereCondition.StringCondition(" " +
//				"question_id IN  ( select  question_id from MS_QUESTIONSET ) " +
//				"AND " +
                "uuid_task_h IN  (SELECT uuid_task_h FROM tr_task_h WHERE strftime('%d',assignment_date) = strftime('%d',current_timestamp) ) "));
        qb.build();

        return qb.list();
    }

    public static List<TaskD> getTotalTaskCollToday(Context context) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();

        qb.where(new WhereCondition.StringCondition
                (" UUID_TASK_H IN (SELECT UUID_TASK_H FROM TR_TASK_H  WHERE strftime('%d',ASSIGNMENT_DATE) = strftime('%d',CURRENT_TIMESTAMP)  AND " +
                        " UUID_SCHEME IN (SELECT UUID_SCHEME FROM MS_SCHEME WHERE FORM_TYPE = '" + Global.FORM_TYPE_COLL + "')) " +
                        ""));
        qb.build();

        return qb.list();
    }

    public static List<TaskD> getUnsentImageByTaskH(Context context, String uuidUser, String uuidTaskH) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.whereOr(TaskDDao.Properties.Is_sent.eq("0"), TaskDDao.Properties.Is_sent.isNull());
        qb.where(TaskDDao.Properties.Image.isNotNull());
        qb.where(new WhereCondition.StringCondition
                (" UUID_TASK_H in (select UUID_TASK_H from TR_TASK_H where UUID_TASK_H = '" + uuidTaskH + "' AND UUID_USER ='" + uuidUser + "' ) "));
        qb.build();
        return qb.list();
    }

    public static TaskD getOneByTaskH(Context context, String uuidTaskH) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.limit(1);
        qb.build();
        if (qb.list().size() == 1)
            return qb.list().get(0);
        else
            return null;
    }

    public static String tagToId(Context context, String tag) {
        String question_id = "";
        StringBuilder sql_question_id = new StringBuilder().append("Select ").append(QuestionSetDao.Properties.Question_id.columnName)
                .append(" From ").append(QuestionSetDao.TABLENAME).append(" Where ").append(QuestionSetDao.Properties.Tag.columnName)
                .append(" = ?");
        String sql_string_question_id = sql_question_id.toString();
        String[] selectionArgs1 = {tag};
        Cursor c1 = getDaoSession(context).getDatabase().rawQuery(sql_string_question_id, selectionArgs1);
        try {
            if (c1.moveToFirst()) {
                question_id = c1.getString(0);
            }
        } finally {
            c1.close();
        }
        return question_id;
    }

    public static TaskD getOneFromTaskDWithTag(Context context, String uuidTaskH, String tag) {
        String question_id = tagToId(context, tag);
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.where(TaskDDao.Properties.Question_id.eq(question_id));
        qb.limit(1);
        qb.build();
        if (qb.list().size() == 1)
            return qb.list().get(0);
        else
            return null;
    }

    public static boolean reInsert(Context context, TaskH taskH, List<TaskD> taskDList, boolean isSend) {
        boolean success = false;
        Database db = DaoOpenHelper.getDb(context);
        db.beginTransaction();

        try {
            DaoSession daoSession = DaoOpenHelper.getDaoMaster(context).newSession();
            QueryBuilder<TaskD> qb = daoSession.getTaskDDao().queryBuilder();
            qb.where(TaskDDao.Properties.Uuid_task_h.eq(taskH.getUuid_task_h()));
            if (isSend)
                daoSession.getTaskDDao().deleteInTx(qb.list());
            else {
                List<TaskD> tempTaskD = qb.list();
                List<TaskD> deletedTaskD = new ArrayList<>();
                if (tempTaskD != null && !tempTaskD.isEmpty()) {
                    HashMap<String, TaskD> mapTaskD = new LinkedHashMap<>();
                    for (TaskD taskD : tempTaskD) {
                        String idf = taskD.getQuestion_id() + Global.DELIMETER_DATA2 + taskD.getQuestion_group_id();
                        mapTaskD.put(idf, taskD);
                    }
                    for (TaskD taskD : taskDList) {
                        String idf = taskD.getQuestion_id() + Global.DELIMETER_DATA2 + taskD.getQuestion_group_id();
                        TaskD d = mapTaskD.get(idf);
                        if (d != null) {
                            deletedTaskD.add(d);
                        }
                    }
                    daoSession.getTaskDDao().deleteInTx(deletedTaskD);
                }
            }

            //insert taskD baru
            daoSession.getTaskDDao().insertOrReplaceInTx(taskDList);

            db.setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().handleSilentException(new Exception("Error: Save Data Error " + e.getMessage()));
            if (Global.IS_DEV)
                e.printStackTrace();
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    public static TaskD getAllByUUIDTaskHandQuestionId(Context context, String uuidTaskH, String questionId) {
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(uuidTaskH),
                TaskDDao.Properties.Question_id.eq(questionId));

        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    /**
     * To get taskD where Tag is TOTAL
     * @param context
     * @return List<TaskD>
     */
    public static List<TaskD> getTaskDTagTotal(Context context) {
        List<String> depositListUuidTaskH = DepositReportDDataAccess.getAllUuid(context);
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(new WhereCondition.StringCondition
                (" QUESTION_ID in (select QUESTION_ID from MS_QUESTIONSET where TAG = '" + Global.TAG_TOTAL + "' ) "));
        qb.where(new WhereCondition.StringCondition
                (" UUID_TASK_H in (select UUID_TASK_H from TR_TASK_H where STATUS = '" + TaskHDataAccess.STATUS_SEND_SENT + "' ) "));
        qb.where(TaskDDao.Properties.Uuid_task_h.notIn(depositListUuidTaskH));
        qb.build();

        return qb.list();
    }

    public static List<TaskD> getListByTaskH(Context context, String uuidTaskH){
        QueryBuilder<TaskD> qb = getTaskDDao(context).queryBuilder();
        qb.where(TaskDDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if(qb.list().size()!=0)
            return qb.list();
        else
            return null;
    }
}
