package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;
import android.database.Cursor;

import com.adins.mss.base.Backup;
import com.adins.mss.base.scheme.VersionSchemeTaskBean;
import com.adins.mss.base.todolist.form.PriorityTabFragment;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.QuestionSetDao;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskHDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * @author bong.rk
 */
public class TaskHDataAccess {
    public static final String ACCESS_MODE_BRANCH = "ACCESS_MODE_BRANCH";
    public static final String ACCESS_MODE_USER = "ACCESS_MODE_USER";
    public static final String ACCESS_MODE_HYBRID = "ACCESS_MODE_HYBRID";
    public static final String STATUS_SEND_INIT = "New";
    public static final String STATUS_SEND_SENT = "Sent";
    public static final String STATUS_SEND_PENDING = "Failed";
    public static final String STATUS_SEND_SAVEDRAFT = "Draft";
    public static final String STATUS_SEND_FAILEDDRAFT = "FailedDraft";
    public static final String STATUS_SEND_UPLOADING = "Uploading";
    public static final String STATUS_SEND_FAILED = "Failed";
    public static final String STATUS_SEND_DELETED = "Deleted";
    public static final String STATUS_SEND_REMINDER = "Reminder";
    public static final String STATUS_SEND_REVIEW = "Review";
    public static final String STATUS_SEND_DOWNLOAD = "Download";
    public static final String STATUS_TASK_VERIFICATION = "V";
    public static final String STATUS_TASK_APPROVAL = "P";
    public static final String STATUS_TASK_CHANGED = "Changed";
    public static final String STATUS_SEND_REJECTED = "Rejected";

    public static final String STATUS_TASK_VERIFICATION_DOWNLOAD = "VDownload";
    public static final String STATUS_TASK_APPROVAL_DOWNLOAD = "PDownload";

    public static final String STATUS_RV_SENT = "rv_sent";
    public static final String STATUS_RV_PENDING = "rv_pending";

    public static final String PRIORITY_HIGH = "High";
    public static final String PRIORITY_NORMAL = "Normal";
    public static final String PRIORITY_LOW = "Low";

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
     * add taskH as entity
     *
     * @param context
     * @param taskH
     */
    public static void add(Context context, TaskH taskH) {
        getTaskHDao(context).insert(taskH);
        getDaoSession(context).clear();
    }

    /**
     * add taskH as list entity
     *
     * @param context
     * @param taskHList
     */
    public static void add(Context context, List<TaskH> taskHList) {
        getTaskHDao(context).insertInTx(taskHList);
        getDaoSession(context).clear();
    }

    /**
     * add or replace data taskH
     *
     * @param context
     * @param taskH
     */
    public static void addOrReplace(Context context, TaskH taskH) {
        getTaskHDao(context).insertOrReplaceInTx(taskH);
        getDaoSession(context).clear();
    }

    /**
     * add or replace list data taskH
     *
     * @param context
     * @param taskHList
     */
    public static void addOrReplace(Context context, List<TaskH> taskHList) {
        getTaskHDao(context).insertOrReplaceInTx(taskHList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getTaskHDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param taskH
     */
    public static void delete(Context context, TaskH taskH) {
        getTaskHDao(context).delete(taskH);
        getDaoSession(context).clear();
    }

    /**
     * delete List of taskH and its relational data by taskH status
     *
     * @param context
     * @param uuidUser
     * @param status
     */
    public static void deleteTaskHByStatus(Context context, String uuidUser, String status) {
        List<TaskH> taskHList = getAllTaskByStatus(context, uuidUser, status);
        if (taskHList != null && !taskHList.isEmpty()) {
            for (TaskH taskH : taskHList) {
                deleteWithRelation(context, taskH);
            }
        }
    }

    /**
     * delete row and the relation form other table
     *
     * @param context
     * @param taskH
     */
    public static void deleteWithRelation(Context context, TaskH taskH) {
        TaskDDataAccess.delete(context, taskH.getUuid_task_h());
        ImageResultDataAccess.delete(context, taskH.getUuid_task_h());
        PrintResultDataAccess.delete(context, taskH.getUuid_task_h());
        getTaskHDao(context).delete(taskH);
        getDaoSession(context).clear();
    }

    /**
     * Delete list of taskH with their relation on the other tables
     *
     * @param context
     * @param listTaskHDelete
     */
    public static void deleteListWithRelation(Context context,
                                              List<TaskH> listTaskHDelete) {
        for (TaskH taskHBean : listTaskHDelete)
            deleteWithRelation(context, taskHBean);
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        getTaskHDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    public static void deleteByUuid(Context context, String uuidUser, String uuidTask) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser));
        qb.where(TaskHDao.Properties.Uuid_task_h.eq(uuidTask));
        qb.build();
        getTaskHDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param taskH
     */
    public static void update(Context context, TaskH taskH) {
        getTaskHDao(context).update(taskH);
    }

    /**
     * This method is used to retrieve one object of task header by taskId
     *
     * @param context
     * @param taskId
     * @return
     */
    public static TaskH getOneTaskHeader(Context context, String taskId) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Task_id.eq(taskId));
        qb.build().forCurrentThread();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static TaskH getOneUnsentTaskHeader(Context context, String taskId) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Task_id.eq(taskId)
                ,TaskHDao.Properties.Status.notIn(STATUS_SEND_SENT,STATUS_SEND_DELETED));
        qb.build().forCurrentThread();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    /**
     * This method is used to retrieve one object of task header by uuidTaskH
     *
     * @param context
     * @param uuid_task_h
     * @return
     */
    public static TaskH getOneHeader(Context context, String uuid_task_h) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_task_h.eq(uuid_task_h));
        qb.build().forCurrentThread();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static TaskH getAllHeader(Context context, String batchId){
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Batch_id.eq(batchId));
        qb.build().forCurrentThread();
        if(qb.list().size()==0)
            return null;
        return qb.list().get(0);
    }

    public static TaskH getOneByResurvey(Context context, String uuid_task_h) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_resurvey_user.eq(uuid_task_h));
        qb.build().forCurrentThread();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    /**
     * Get all tasks which has verified scheme
     *
     * @param context
     * @param userId
     * @return List<TaskH>
     */
    public static List<TaskH> getAllVerified(Context context, String userId) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(userId)
                //,TaskHDao.Properties.Uuid_scheme.in(uuidSchemeList)
                , TaskHDao.Properties.Status.in(STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllVerifiedForUser(Context context, String userId) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(userId)
                , TaskHDao.Properties.Access_mode.in(ACCESS_MODE_USER, ACCESS_MODE_HYBRID)
                , TaskHDao.Properties.Status.in(STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.build();
        return qb.list();
    }



    public static List<TaskH> getAllVerifiedForBranch(Context context, String userId) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(userId)
                , TaskHDao.Properties.Access_mode.in(ACCESS_MODE_BRANCH, ACCESS_MODE_HYBRID)
                , TaskHDao.Properties.Status.in(STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.build();
        return qb.list();
    }

    /**
     * Get all tasks which has approval scheme
     *
     * @param context
     * @param userId
     * @return List<TaskH>
     */
    public static List<TaskH> getAllApproval(Context context, String userId) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(userId)
                //,TaskHDao.Properties.Uuid_scheme.in(uuidSchemeList)
                , TaskHDao.Properties.Status.in(STATUS_TASK_APPROVAL, STATUS_TASK_APPROVAL_DOWNLOAD));
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllApprovalForUser(Context context, String userId) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(userId)
                , TaskHDao.Properties.Access_mode.in(ACCESS_MODE_USER, ACCESS_MODE_HYBRID)
                , TaskHDao.Properties.Status.in(STATUS_TASK_APPROVAL, STATUS_TASK_APPROVAL_DOWNLOAD));
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllApprovalForBranch(Context context, String userId) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(userId)
                , TaskHDao.Properties.Access_mode.in(ACCESS_MODE_BRANCH, ACCESS_MODE_HYBRID)
                , TaskHDao.Properties.Status.in(STATUS_TASK_APPROVAL, STATUS_TASK_APPROVAL_DOWNLOAD));
        qb.build();
        return qb.list();
    }

    /**
     * select * from table where uuid_user = global.userid
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<TaskH> getAll(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    public static int getCountAll(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = calendar.getTime();

        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.notIn(TaskHDataAccess.STATUS_SEND_DELETED),
                qb.or(TaskHDao.Properties.Submit_date.isNull(),
                        TaskHDao.Properties.Submit_date.between(startDate, endDate)));
        qb.build();
        return (int) qb.count();
    }

    /**
     * select * from table where uuid_user = global.userid
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<TaskH> getAllWithAssignmentDate(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Assignment_date.isNotNull());
        qb.build();
        return qb.list();
    }


    public static List<TaskH> getAllVerificationTaskWithAssignmentDate(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Assignment_date.isNotNull(),
                TaskHDao.Properties.Status.in(STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllApprovalTaskWithAssignmentDate(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Assignment_date.isNotNull(),
                TaskHDao.Properties.Status.in(STATUS_TASK_APPROVAL, STATUS_TASK_APPROVAL_DOWNLOAD));
        qb.build();
        return qb.list();
    }

    /**
     * select * from table where uuid_scheme = param
     *
     * @param context
     * @param uuidUser
     * @param keyScheme
     * @return
     */
    public static List<TaskH> getAll(Context context, String uuidUser, String keyScheme) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Uuid_scheme.eq(keyScheme));
        qb.build();
        return qb.list();
    }

    /**
     * This method is used to retrieve list of all sent tasks
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<TaskH> getAllSentTask(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_SEND_SENT, STATUS_SEND_REJECTED));
        qb.orderDesc(TaskHDao.Properties.Submit_date);
        qb.build();
        return qb.list();
    }


    public static long getSentTaskCounter(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_SEND_SENT, STATUS_SEND_REJECTED));
        qb.build();
        return qb.list().size();
    }

    public static long getSentTaskCounterAllUser(Context context){
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(
                TaskHDao.Properties.Status.in(STATUS_SEND_SENT, STATUS_SEND_REJECTED));
        qb.build();
        return qb.list().size();
    }

    public static long getVerificationTaskCounter(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Access_mode.in(ACCESS_MODE_USER, ACCESS_MODE_HYBRID),
                TaskHDao.Properties.Status.in(STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.build();
        return qb.list().size();
    }

    public static long getApprovalTaskCounter(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_TASK_APPROVAL, STATUS_TASK_APPROVAL_DOWNLOAD));
        qb.build();
        return qb.list().size();
    }

    public static long getVerificationTaskCounterByUser(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Access_mode.in(ACCESS_MODE_USER, ACCESS_MODE_HYBRID),
                TaskHDao.Properties.Status.in(STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.build();
        return qb.list().size();
    }

    public static long getApprovalTaskCounterByUser(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser)
                , TaskHDao.Properties.Access_mode.in(ACCESS_MODE_USER, ACCESS_MODE_HYBRID)
                , TaskHDao.Properties.Status.in(STATUS_TASK_APPROVAL, STATUS_TASK_APPROVAL_DOWNLOAD));
        qb.build();
        return qb.list().size();
    }

    public static long getVerificationTaskCounterByBranch(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Access_mode.in(ACCESS_MODE_BRANCH, ACCESS_MODE_HYBRID),
                TaskHDao.Properties.Status.in(STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.build();
        return qb.list().size();
    }

    public static long getApprovalTaskCounterByBranch(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Access_mode.in(ACCESS_MODE_BRANCH, ACCESS_MODE_HYBRID),
                TaskHDao.Properties.Status.in(STATUS_TASK_APPROVAL, STATUS_TASK_APPROVAL_DOWNLOAD));
        qb.build();
        return qb.list().size();
    }

    public static long getTaskInPriorityCounter(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT));
        qb.build();
        return qb.list().size();
    }

    public static long getTaskInStatusCounter(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list().size();
    }

    /**
     * This method is sed to get all sent task which will be deleted  if lower minimum of a minimum date
     *
     * @param context
     * @param uuidUser
     * @param batasDel - minimum date
     * @return
     */
    public static List<TaskH> getAllDeleteTask(Context context, String uuidUser,
                                               String batasDel) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.eq(STATUS_SEND_SENT),
                TaskHDao.Properties.Submit_date.le(batasDel));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTask(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING, STATUS_SEND_DOWNLOAD,
                        STATUS_SEND_INIT));
        qb.orderAsc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskByScheme(Context context, String uuidUser, String uuidScheme) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING, STATUS_SEND_DOWNLOAD,
                        STATUS_SEND_INIT),
                TaskHDao.Properties.Uuid_scheme.eq(uuidScheme));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskByStatusByScheme(Context context, String uuidUser, String uuidScheme, String searchType) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(searchType),
                TaskHDao.Properties.Uuid_scheme.eq(uuidScheme));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInStatusByScheme(Context context, String uuidUser, String uuidScheme) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING),
                TaskHDao.Properties.Uuid_scheme.eq(uuidScheme));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    /**
     * This method is used to retrieve list of all task in status bar
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<TaskH> getAllTaskInStatus(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInStatusForMultiUser(Context context) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING));
//				TaskHDao.Properties.Status.notIn(STATUS_SEND_SENT, STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT,
//						STATUS_TASK_APPROVAL,STATUS_TASK_APPROVAL_DOWNLOAD,STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    /**
     * This method is used to retrieve list of all task in status bar by taskId
     *
     * @param context
     * @param uuidUser
     * @param taskId
     * @return List<TaskH>
     */
    public static List<TaskH> getAllTaskInStatusByTask(Context context, String uuidUser, String taskId) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Task_id.like(taskId),
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInStatusByTaskForMultiUser(Context context, String taskId){
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(
                TaskHDao.Properties.Task_id.like(taskId),
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING));
//				TaskHDao.Properties.Status.notIn(STATUS_SEND_SENT, STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT,
//						STATUS_TASK_APPROVAL,STATUS_TASK_APPROVAL_DOWNLOAD,STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    /**
     * This method is used to retrieve list of all task in status bar by Customer Name
     *
     * @param context
     * @param uuidUser
     * @param customerName
     * @return List<TaskH>
     */
    public static List<TaskH> getAllTaskInStatusByCustomer(Context context, String uuidUser, String customerName) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Customer_name.like(customerName),
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInStatusByCustomerForMultiUser(Context context, String customerName){
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(
                TaskHDao.Properties.Customer_name.like(customerName),
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING));
//				TaskHDao.Properties.Status.notIn(STATUS_SEND_SENT, STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT,
//						STATUS_TASK_APPROVAL,STATUS_TASK_APPROVAL_DOWNLOAD,STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    /**
     * This method is used to retrieve list of all task in status bar by Customer Name and taskId
     *
     * @param context
     * @param uuidUser
     * @param searchContent
     * @return
     */
    public static List<TaskH> getAllTaskInStatusByAll(Context context,
                                                      String uuidUser, String searchContent) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                qb.or(TaskHDao.Properties.Customer_name.like(searchContent), TaskHDao.Properties.Task_id.like(searchContent)),
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInStatusByAllForMultiUser(Context context,
                                                                  String searchContent) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(
                qb.or(TaskHDao.Properties.Customer_name.like(searchContent), TaskHDao.Properties.Task_id.like(searchContent)),
                TaskHDao.Properties.Status.in(STATUS_SEND_SAVEDRAFT, STATUS_SEND_PENDING, STATUS_SEND_UPLOADING));
//				TaskHDao.Properties.Status.notIn(STATUS_SEND_SENT, STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT,
//						STATUS_TASK_APPROVAL,STATUS_TASK_APPROVAL_DOWNLOAD,STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskSentByAllForMultiUser(Context context,
                                                              String searchContent) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Status.in(STATUS_SEND_SENT));
//				TaskHDao.Properties.Status.notIn(STATUS_SEND_SENT, STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT,
//						STATUS_TASK_APPROVAL,STATUS_TASK_APPROVAL_DOWNLOAD,STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInStatusByBatchIdForMultiUser(Context context,
                                                                      String searchContent) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Batch_id.isNotNull());
//				TaskHDao.Properties.Status.notIn(STATUS_SEND_SENT, STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT,
//						STATUS_TASK_APPROVAL,STATUS_TASK_APPROVAL_DOWNLOAD,STATUS_TASK_VERIFICATION, STATUS_TASK_VERIFICATION_DOWNLOAD));
        qb.orderDesc(TaskHDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    /**
     * This method is used to retrieve list of all task in priority bar
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<TaskH> getAllTaskInPriority(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT));

        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInPriorityByScheme(Context context, String uuidUser, String uuidScheme) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT),
                TaskHDao.Properties.Uuid_scheme.eq(uuidScheme));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    /**
     * This method is used to retrieve list of all task in priority bar by taskId
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<TaskH> getAllTaskInPriorityByTask(Context context, String uuidUser, String taskId) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Task_id.like(taskId),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInPriorityByTaskAndScheme(Context context, String uuidUser, String taskId, String uuidScheme) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Task_id.like(taskId),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT),
                TaskHDao.Properties.Uuid_scheme.eq(uuidScheme));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    /**
     * This method is used to retrieve list of all task in priority bar by customerName
     *
     * @param context
     * @param uuidUser
     * @param customerName
     * @return
     */
    public static List<TaskH> getAllTaskInPriorityByCustomer(Context context, String uuidUser, String customerName) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Customer_name.like(customerName),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInPriorityByCustomerAndScheme(Context context, String uuidUser, String customerName, String uuidScheme) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Customer_name.like(customerName),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT),
                TaskHDao.Properties.Uuid_scheme.eq(uuidScheme));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    /**
     * This method is used to retrieve list of all task in priority bar by customerName and taskId
     *
     * @param context
     * @param uuidUser
     * @param searchContent
     * @return
     */
    public static List<TaskH> getAllTaskInPriorityByAll(Context context,
                                                        String uuidUser, String searchContent) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                qb.or(TaskHDao.Properties.Customer_name.like(searchContent), TaskHDao.Properties.Task_id.like(searchContent)),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInPriorityByAllAndScheme(Context context, String uuidUser, String searchContent, String uuidScheme) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                qb.or(TaskHDao.Properties.Customer_name.like(searchContent), TaskHDao.Properties.Task_id.like(searchContent)),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT),
                TaskHDao.Properties.Uuid_scheme.eq(uuidScheme));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    /**
     * Get list of task header by status in
     * ("New","Sent","Pending","Draft"."Sent","Uploading","Download", "Reminder")
     *
     * @param context
     * @param uuidUser
     * @param status
     * @return
     */
    public static List<TaskH> getAllTaskByStatus(Context context, String uuidUser, String status) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.eq(status));
        qb.build();
        return qb.list();
    }

    /**
     * Update status task header
     * <br> Status = ("New","Sent","Pending","Draft"."Sent","Uploading","Download", "Reminder")
     *
     * @param context
     * @param taskH
     * @param status
     */
    public static void updateStatus(Context context, TaskH taskH, String status) {
        taskH.setStatus(status);
        getTaskHDao(context).update(taskH);
    }

    public static void updateStatusClosingTask(Context context, String status, String uuid) {
        String statement = String.format("UPDATE TR_TASK_H SET STATUS = '%s' WHERE STATUS IN ('New', 'Download') AND uuid_user='%s'", status, uuid);
        getTaskHDao(context).getDatabase().execSQL(statement);
    }

    public static String getBatchByUuiTask(Context context, String uuidScheme, String dtmCrt){
        List<String> result = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String currentDate = formatter.format(new Date());
        String SQL_DISTINCT_BATCH = "SELECT DISTINCT "+ TaskHDao.Properties.Batch_id.columnName+
                " FROM "+ TaskHDao.TABLENAME+
                " WHERE "+ TaskHDao.Properties.Uuid_scheme.columnName+" = '"+ uuidScheme +"' "+
                " AND "+TaskHDao.Properties.Batch_id.columnName+" like '%"+currentDate+"%'"+
                " AND ("+ TaskHDao.Properties.Is_reconciled.columnName+ " = '0'"+
                " OR "+ TaskHDao.Properties.Is_reconciled.columnName+ " IS NULL)"+
                " AND "+TaskHDao.Properties.Status.columnName+ " = '"+ STATUS_SEND_SENT +"'";
        Cursor c = getTaskHDao(context).getDatabase().rawQuery(SQL_DISTINCT_BATCH, null);
        if (c.moveToFirst()) {
            do {
                result.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        if (result != null && result.size() >0 ){
            return result.get(0);
        } else {
            return null;
        }
    }

    /**
     * Update status task header by taskId
     * <br> Status = ("New","Sent","Pending","Draft"."Sent","Uploading","Download", "Reminder")
     *
     * @param context
     * @param taskId
     * @param status
     */
    public static void updateStatusByTaskId(Context context, String taskId, String status) {
        TaskH taskH = getOneTaskHeader(context, taskId);
        if(taskH != null){
            taskH.setStatus(status);
            getTaskHDao(context).update(taskH);
        }
    }

    /**
     * select TaskH where TaskH not in DepositReportD
     *
     * @return TaskHList
     */
    public static List<TaskH> getUnreportedTaskH(Context context, String uuidUser) {
        List<String> uuidList = new ArrayList<>();
        List<DepositReportD> depositReportDList = DepositReportDDataAccess.getAll(context);

        for (DepositReportD depositReportD : depositReportDList) {
            uuidList.add(depositReportD.getUuid_task_h());
        }

        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_task_h.notIn(uuidList));
        qb.build();

        return qb.list();
    }

    public static List<String> getAllBatchIdList(Context context, List<String> depositedBatch){
        List<String> result = new ArrayList<>();
        String SQL_DISTINCT_BATCH = "SELECT DISTINCT "+ TaskHDao.Properties.Batch_id.columnName+
                " FROM "+ TaskHDao.TABLENAME+
                " WHERE "+ TaskHDao.Properties.Batch_id.columnName+" IS NOT NULL "+
                " AND "+ TaskHDao.Properties.Is_reconciled.columnName+" = '0' ";
        Cursor c = getTaskHDao(context).getDatabase().rawQuery(SQL_DISTINCT_BATCH, null);
        if (c.moveToFirst()) {
            do {
                result.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        result.removeAll(depositedBatch);
        return result;
    }

    public static List<TaskH> getTaskById(Context context, String uuidTask){
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();

        qb.where(TaskHDao.Properties.Uuid_task_h.eq(uuidTask));
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getTaskCollToday(Context context) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();

        qb.where(new WhereCondition.StringCondition
//				("strftime('%d',ASSIGNMENT_DATE) = strftime('%d',CURRENT_TIMESTAMP) and UUID_SCHEME IN " +
                ("date(ASSIGNMENT_DATE) = CURRENT_DATE and UUID_SCHEME IN " +
                        "(SELECT UUID_SCHEME FROM MS_SCHEME WHERE FORM_TYPE = '" + Global.FORM_TYPE_COLL + "')"));
        qb.build();
        return qb.list();
    }

    public static int getTotalTaskCollToday(Context context) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();

        qb.where(new WhereCondition.StringCondition
                ("strftime('%d',ASSIGNMENT_DATE) = strftime('%d',CURRENT_TIMESTAMP, 'localtime') and UUID_SCHEME IN " +
                        "(SELECT UUID_SCHEME FROM MS_SCHEME WHERE FORM_TYPE = '" + Global.FORM_TYPE_COLL + "')"));
        return (int) qb.count();
    }


    public static List<TaskH> getAllTaskCollection(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser));
        qb.where(new WhereCondition.StringCondition
                ("UUID_SCHEME IN " +
                        "(SELECT UUID_SCHEME FROM MS_SCHEME WHERE FORM_TYPE = '" + Global.FORM_TYPE_COLL + "')"));
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInHighPriority(Context context,
                                                       String uuid_user) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuid_user),
                TaskHDao.Properties.Priority.eq(PRIORITY_HIGH),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInNormalPriority(Context context,
                                                         String uuid_user) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuid_user),
                TaskHDao.Properties.Priority.eq(PRIORITY_NORMAL),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInLowPriority(Context context,
                                                      String uuid_user) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuid_user),
                TaskHDao.Properties.Priority.eq(PRIORITY_LOW),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInHighPriorityByScheme(Context context,
                                                               String uuid_user, String uuid_scheme) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuid_user),
                TaskHDao.Properties.Priority.eq(PRIORITY_HIGH),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT),
                TaskHDao.Properties.Uuid_scheme.eq(uuid_scheme));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInNormalPriorityByScheme(Context context,
                                                                 String uuid_user, String uuid_scheme) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuid_user),
                TaskHDao.Properties.Priority.eq(PRIORITY_NORMAL),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT),
                TaskHDao.Properties.Uuid_scheme.eq(uuid_scheme));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    public static List<TaskH> getAllTaskInLowPriorityByScheme(Context context,
                                                              String uuid_user, String uuid_scheme) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuid_user),
                TaskHDao.Properties.Priority.eq(PRIORITY_LOW),
                TaskHDao.Properties.Status.in(STATUS_SEND_DOWNLOAD, STATUS_SEND_INIT),
                TaskHDao.Properties.Uuid_scheme.eq(uuid_scheme));
        qb.orderAsc(TaskHDao.Properties.Assignment_date);
        qb.build();
        return qb.list();
    }

    public static List<VersionSchemeTaskBean> getAllVersionSchemeTask(Context context) {
        List<VersionSchemeTaskBean> result = new ArrayList<>();
        String SQL_DISTINCT_VERSION =
                "SELECT DISTINCT tth." + TaskHDao.Properties.Uuid_scheme.columnName +
                        ", tth." + TaskHDao.Properties.Form_version.columnName +
                        " FROM " + TaskHDao.TABLENAME + " tth" +
                        " LEFT JOIN (SELECT DISTINCT " + QuestionSetDao.Properties.Uuid_scheme.columnName +
                        ", " + QuestionSetDao.Properties.Form_version.columnName +
                        " FROM " + QuestionSetDao.TABLENAME + ") qs ON tth." + TaskHDao.Properties.Uuid_scheme.columnName +
                        " = qs." + QuestionSetDao.Properties.Uuid_scheme.columnName + " AND " +
                        " tth." + TaskHDao.Properties.Form_version.columnName + " = qs." + QuestionSetDao.Properties.Form_version.columnName +
                        " WHERE qs." + QuestionSetDao.Properties.Uuid_scheme.columnName + " IS NULL " +
                        " AND qs." + QuestionSetDao.Properties.Form_version.columnName + " IS NULL";
        Cursor c = getTaskHDao(context).getDatabase().rawQuery(SQL_DISTINCT_VERSION, null);
        if (c.moveToFirst()) {
            do {
                VersionSchemeTaskBean versionSchemeTask = new VersionSchemeTaskBean();
                versionSchemeTask.setUuidScheme(c.getString(0));
                versionSchemeTask.setFormVersion(c.getString(1));
                result.add(versionSchemeTask);
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }

    public static List<String> getAllVersionSchemeTaskByUuidScheme(Context context, String uuidScheme) {
        List<String> result = new ArrayList<>();
        String SQL_DISTINCT_VERSION = "SELECT DISTINCT " + TaskHDao.Properties.Form_version.columnName +
                " FROM " + TaskHDao.TABLENAME +
                " WHERE " + TaskHDao.Properties.Uuid_scheme.columnName + "='" + uuidScheme + "' ";
        Cursor c = getTaskHDao(context).getDatabase().rawQuery(SQL_DISTINCT_VERSION, null);
        if (c.moveToFirst()) {
            do {
                result.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }

    public static List<TaskH> getAllTaskByStatusNewAndDownload(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.in(STATUS_SEND_INIT, STATUS_SEND_DOWNLOAD));
        qb.build();
        return qb.list();
    }


    public static TaskH getOneTaskByStatusRV(Context context, String uuidUser, String status) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.eq(STATUS_SEND_SENT),
                TaskHDao.Properties.Status_rv.eq(status));
        qb.build();

        if ((qb.list() == null) || qb.list().isEmpty()) return null;
        return qb.list().get(0);
    }

    public static boolean isTaskHDisabled(String status){
        return status.equals(TaskHDataAccess.STATUS_SEND_DELETED);
    }

    public static List<String> getTaskHPrintRequired(Context context, String uuidUser) {
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_user.eq(uuidUser),
                TaskHDao.Properties.Status.eq(TaskHDataAccess.STATUS_SEND_SENT),
                TaskHDao.Properties.Print_count.isNull(),
                TaskHDao.Properties.Rv_number.isNull());
        qb.build();

        List<String> results = new ArrayList<>();
        List<TaskH> listTaskH = qb.list();
        if (listTaskH != null && !listTaskH.isEmpty()) {
            for (TaskH taskH : listTaskH) {
                results.add(taskH.getUuid_task_h());
            }
        }
        return results;
    }

    public static RvValue getRvValue(Context context, String taskId) {
        TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
        if (taskH != null) {
            if (taskH.getRv_number() == null || taskH.getRv_number().isEmpty())
                return RvValue.RV_EMPTY;
            else return RvValue.RV_NOT_EMPTY;
        } else {
            return RvValue.TASK_H_NOT_FOUND;
        }
    }

    private static StringBuilder getPlanTaskJoinQuery(){
        StringBuilder query = new StringBuilder();

        query.append("select taskH.UUID_TASK_H,planTask.VIEW_SEQUENCE from TR_TASK_H taskH ")
                .append("inner join TR_PLAN_TASK planTask ")
                .append("on taskH.TASK_ID = planTask.UUID_TASK_H ")
                .append("join TR_TASK_D taskD ")
                .append("on taskH.UUID_TASK_H = taskD.UUID_TASK_H ")
                .append("join MS_QUESTIONSET MQ ")
                .append("on MQ.QUESTION_ID = taskD.QUESTION_ID ")
                .append("where planTask.PLAN_STATUS not in ('Finish') ")
                .append("and MQ.IDENTIFIER_NAME = 'COL_LAST_PRMS_PAY_DT' ");
        return query;
    }

    private static StringBuilder getAllTaskH(){
        StringBuilder query = new StringBuilder();

        query.append("select taskH.UUID_TASK_H from TR_TASK_H taskH ")
                .append("join TR_TASK_D taskD ")
                .append("on taskH.UUID_TASK_H = taskD.UUID_TASK_H ")
                .append("join MS_QUESTIONSET MQ ")
                .append("on MQ.QUESTION_ID = taskD.QUESTION_ID ")
                .append("where MQ.IDENTIFIER_NAME = 'COL_LAST_PRMS_PAY_DT' ");

        return query;
    }

    public static List<TaskH> getTaskByFilter(Context context, String uuidScheme, int searchType, int ptp, String tenorFrom, String tenorTo, String osFrom, String osTo, String custName){
        Database db = DaoOpenHelper.getDb(context);

        StringBuilder query = getAllTaskH();
        query.append("and taskH.STATUS not in ('Sent', 'Deleted') ");
        String orderBy = "order by taskH.assignment_date ASC";

        boolean hasTenor = !tenorFrom.isEmpty() && !tenorTo.isEmpty();
        boolean hasOs = !osFrom.isEmpty() && !osTo.isEmpty();
        boolean hasCustName = !custName.isEmpty();

        if(!uuidScheme.equals(PriorityTabFragment.uuidSchemeDummy)){
            query.append("and taskH.UUID_SCHEME = '").append(uuidScheme).append("' ");
        }

        if(searchType != 0){
            if(searchType == 1){
                query.append("and taskH.PRIORITY = '").append("High").append("' ");
            }
            else if(searchType == 2){
                query.append("and taskH.PRIORITY = '").append("Normal").append("' ");
            }
            else if(searchType == 3){
                query.append("and taskH.PRIORITY = '").append("Low").append("' ");
            }
            else if(searchType == 4){
                query.append("and taskH.STATUS = '").append("Pending").append("' ");
            }
            else if(searchType == 5){
                query.append("and taskH.STATUS = '").append("Uploading").append("' ");
            }
            else if(searchType == 6){
                query.append("and taskH.STATUS = '").append("Draft").append("' ");
            }
        }

        if(ptp != 0){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = formatter.format(new Date());
            if(ptp == 1){
                query.append("and taskD.text_answer < '").append(currentDate).append("' ");
            }
            else if(ptp == 2){
                query.append("and taskD.text_answer = '").append(currentDate).append("' ");
            }
            else if(ptp == 3){
                query.append("and taskD.text_answer > '").append(currentDate).append("' ");
            }
        }

        if(hasCustName){
            query.append("and taskH.customer_name like '%").append(custName).append("%' ");
        }

        if(hasOs){
            query.append("and CAST(taskH.amt_due AS REAL) BETWEEN '").append(osFrom).append("' AND '").append(osTo).append("' ");
        }

        if(hasTenor){
            query.append("and CAST(taskH.inst_no AS REAL) BETWEEN '").append(tenorFrom).append("' AND '").append(tenorTo).append("' ");
        }

        query.append(orderBy);


        Cursor result = db.rawQuery(query.toString(),new String[]{});
        if(result == null){
            return new ArrayList<>();
        }

        List<String> listUuidTaskh = new ArrayList<>();
        while (result.moveToNext()){
            String uuidTaskH  = result.getString(0);
            if(uuidTaskH != null && !uuidTaskH.equals("")){
                listUuidTaskh.add(uuidTaskH);
            }
        }

        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_task_h.in(listUuidTaskh));

        return qb.list();
    }

    public static List<TaskH> getTaskhPlanFilter(Context context, String uuidScheme, int searchType, int ptp, String tenorFrom, String tenorTo, String osFrom, String osTo, String custName){
        Database db = DaoOpenHelper.getDb(context);

        StringBuilder query = getPlanTaskJoinQuery();
        String orderBy = "order by planTask.VIEW_SEQUENCE";
        query.append("and taskH.STATUS not in ('Sent','Deleted') ");

        boolean hasTenor = !tenorFrom.isEmpty() && !tenorTo.isEmpty();
        boolean hasOs = !osFrom.isEmpty() && !osTo.isEmpty();
        boolean hasCustName = !custName.isEmpty();

        if(!uuidScheme.equals(PriorityTabFragment.uuidSchemeDummy)){
            query.append("and taskH.UUID_SCHEME = '").append(uuidScheme).append("' ");
        }

        if(searchType != 0){
            if(searchType == 1){
                query.append("and taskH.PRIORITY = '").append("High").append("' ");
            }
            else if(searchType == 2){
                query.append("and taskH.PRIORITY = '").append("Normal").append("' ");
            }
            else if(searchType == 3){
                query.append("and taskH.PRIORITY = '").append("Low").append("' ");
            }
            else if(searchType == 4){
                query.append("and taskH.STATUS = '").append("Pending").append("' ");
            }
            else if(searchType == 5){
                query.append("and taskH.STATUS = '").append("Uploading").append("' ");
            }
            else if(searchType == 6){
                query.append("and taskH.STATUS = '").append("Draft").append("' ");
            }
        }

        if(ptp != 0){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = formatter.format(new Date());
            if(ptp == 1){
                query.append("and taskD.text_answer < '").append(currentDate).append("' ");
            }
            else if(ptp == 2){
                query.append("and taskD.text_answer = '").append(currentDate).append("' ");
            }
            else if(ptp == 3){
                query.append("and taskD.text_answer > '").append(currentDate).append("' ");
            }
        }

        if(hasCustName){
            query.append("and taskH.customer_name like '%").append(custName).append("%' ");
        }

        if(hasOs){
            query.append("and CAST(taskH.amt_due AS REAL) BETWEEN '").append(osFrom).append("' AND '").append(osTo).append("' ");
        }

        if(hasTenor){
            query.append("and CAST(taskH.inst_no AS REAL) BETWEEN '").append(tenorFrom).append("' AND '").append(tenorTo).append("' ");
        }

        query.append(orderBy);


        Cursor result = db.rawQuery(query.toString(),new String[]{});
        if(result == null){
            return new ArrayList<>();
        }

        List<String> listUuidTaskh = new ArrayList<>();
        while (result.moveToNext()){
            String uuidTaskH  = result.getString(0);
            if(uuidTaskH != null && !uuidTaskH.equals("")){
                listUuidTaskh.add(uuidTaskH);
            }
        }

        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_task_h.in(listUuidTaskh));

        return qb.list();
    }

    public static List<TaskH> getTaskhPlanFilterStatus(Context context, String uuidScheme, String status, int ptp, String tenorFrom, String tenorTo, String osFrom, String osTo, String custName){
        Database db = DaoOpenHelper.getDb(context);
        String orderBy = "order by planTask.VIEW_SEQUENCE";
        StringBuilder query = getPlanTaskJoinQuery();

        boolean hasTenor = !tenorFrom.isEmpty() && !tenorTo.isEmpty();
        boolean hasOs = !osFrom.isEmpty() && !osTo.isEmpty();
        boolean hasCustName = !custName.isEmpty();


        if(uuidScheme.equals(PriorityTabFragment.uuidSchemeDummy)){//ignore scheme/form filter if it's value is dummy
            query.append("and taskH.STATUS = '").append(status).append("' ");
        }
        else if(!status.equals("All")) {
            query.append("and taskH.STATUS = '").append(status).append("' ")
                    .append("and taskH.UUID_SCHEME = '").append(uuidScheme).append("'");
        }
        else {
            query.append("and taskH.STATUS not in ('Sent','Deleted') ")
                    .append("and taskH.UUID_SCHEME = '").append(uuidScheme).append("'");
        }

        if(ptp != 0){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = formatter.format(new Date());
            if(ptp == 1){
                query.append("and taskD.text_answer < '").append(currentDate).append("' ");
            }
            else if(ptp == 2){
                query.append("and taskD.text_answer = '").append(currentDate).append("' ");
            }
            else if(ptp == 3){
                query.append("and taskD.text_answer > '").append(currentDate).append("' ");
            }
        }

        if(hasCustName){
            query.append("and taskH.customer_name like '%").append(custName).append("%' ");
        }

        if(hasOs){
            query.append("and CAST(taskH.amt_due AS REAL) BETWEEN '").append(osFrom).append("' AND '").append(osTo).append("' ");
        }

        if(hasTenor){
            query.append("and CAST(taskH.inst_no AS REAL) BETWEEN '").append(tenorFrom).append("' AND '").append(tenorTo).append("' ");
        }

        query.append(orderBy);


        Cursor result = db.rawQuery(query.toString(),new String[]{});
        if(result == null){
            return new ArrayList<>();
        }

        List<String> listUuidTaskh = new ArrayList<>();
        while (result.moveToNext()){
            String uuidTaskH  = result.getString(0);
            if(uuidTaskH != null && !uuidTaskH.equals("")){
                listUuidTaskh.add(uuidTaskH);
            }
        }

        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Uuid_task_h.in(listUuidTaskh));

        return qb.list();
    }

    public static List<TaskH> getAllTaskInFailedSubmitted(Context context){
        QueryBuilder<TaskH> qb = getTaskHDao(context).queryBuilder();
        qb.where(TaskHDao.Properties.Status.in(STATUS_SEND_PENDING, STATUS_SEND_UPLOADING));
        qb.build();
        return qb.list();
    }

    public enum RvValue {
        RV_EMPTY,
        RV_NOT_EMPTY,
        TASK_H_NOT_FOUND
    }

    public static void doBackup(Context context, TaskH taskH) {
        if(!taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_REVIEW)) {
            try {
                if (!taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT) && !taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_REJECTED) && !taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_FAILED)) {
                    if (null != TaskHDataAccess.getOneHeader(context, taskH.getUuid_task_h())) {
                        Backup backup = new Backup(context);
                        if (!Backup.updatingTask.contains(taskH.getUuid_task_h())) {
                            backup.performBackup(taskH);
                        }
                    } else {
                        Backup backup = new Backup(context);
                        List<TaskH> taskHList = new ArrayList<>();
                        taskHList.add(taskH);
                        backup.removeTask(new ArrayList<TaskH>(taskHList));
                    }
                } else {
                    Backup backup = new Backup(context);
                    List<TaskH> taskHList = new ArrayList<>();
                    taskHList.add(taskH);
                    backup.removeTask(new ArrayList<TaskH>(taskHList));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
