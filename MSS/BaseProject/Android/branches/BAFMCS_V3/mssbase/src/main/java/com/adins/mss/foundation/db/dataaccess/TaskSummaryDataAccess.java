package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.TaskSummary;
import com.adins.mss.dao.TaskSummaryDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by olivia.dg on 10/4/2017.
 */

public class TaskSummaryDataAccess {

    public static final String STATUS_FAIL = "Fail";
    public static final String STATUS_PAID = "Paid";

    protected static DaoSession getDaoSession(Context context) {
        return DaoOpenHelper.getDaoSession(context);
    }

    protected static TaskSummaryDao getTaskSummaryDao(Context context) {
        return getDaoSession(context).getTaskSummaryDao();
    }

    public static void add(Context context, TaskSummary taskSummary) {
        getTaskSummaryDao(context).insert(taskSummary);
        getDaoSession(context).clear();
    }

    public static void add(Context context, List<TaskSummary> taskSummaryList) {
        getTaskSummaryDao(context).insertInTx(taskSummaryList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, TaskSummary taskSummary) {
        getTaskSummaryDao(context).insertOrReplaceInTx(taskSummary);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<TaskSummary> taskSummaryList) {
        getTaskSummaryDao(context).insertOrReplaceInTx(taskSummaryList);
        getDaoSession(context).clear();
    }

    public static void clean(Context context) {
        getTaskSummaryDao(context).deleteAll();
    }

    public static void delete(Context context, TaskSummary taskSummary) {
        getTaskSummaryDao(context).delete(taskSummary);
        getDaoSession(context).clear();
    }

    public static void update(Context context, TaskSummary taskSummary) {
        getTaskSummaryDao(context).update(taskSummary);
    }

    public static TaskSummary getOne(Context context, String uuid_task_h, String uuidUser) {
        QueryBuilder<TaskSummary> qb = getTaskSummaryDao(context).queryBuilder();
        qb.where(TaskSummaryDao.Properties.Uuid_task_h.eq(uuid_task_h),
                TaskSummaryDao.Properties.Uuid_user.eq(uuidUser));
        qb.build().forCurrentThread();
        if (qb.list().size() == 0)
            return null;
        return qb.list().get(0);
    }

    public static List<TaskSummary> getAll(Context context, String uuidUser) {
        QueryBuilder<TaskSummary> qb = getTaskSummaryDao(context).queryBuilder();
        qb.where(TaskSummaryDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    public static List<TaskSummary> getAllPaid(Context context, String uuidUser) {
        QueryBuilder<TaskSummary> qb = getTaskSummaryDao(context).queryBuilder();
        qb.where(TaskSummaryDao.Properties.Uuid_user.eq(uuidUser),
                TaskSummaryDao.Properties.Task_status.eq(STATUS_PAID));
        qb.build();
        return qb.list();
    }

    public static List<TaskSummary> getAllFail(Context context, String uuidUser) {
        QueryBuilder<TaskSummary> qb = getTaskSummaryDao(context).queryBuilder();
        qb.where(TaskSummaryDao.Properties.Uuid_user.eq(uuidUser),
                TaskSummaryDao.Properties.Task_status.eq(STATUS_FAIL));
        qb.build();
        return qb.list();
    }
}
