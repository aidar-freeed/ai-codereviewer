package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskHSequence;
import com.adins.mss.dao.TaskHSequenceDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by ACER 471 on 3/15/2017.
 */

public class TaskHSequenceDataAccess {
    protected static DaoSession getDaoSession(Context context) {
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get Training dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static TaskHSequenceDao getTaskHSequenceDao(Context context) {
        return getDaoSession(context).getTaskHSequenceDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add training as entity
     *
     * @param context
     * @param taskHSequence
     * @return
     */
    public static void addOrReplace(Context context, TaskHSequence taskHSequence) {
        getTaskHSequenceDao(context).insertOrReplaceInTx(taskHSequence);
        getDaoSession(context).clear();
    }

    public static List<TaskHSequence> getAllOrderAsc(Context context) {
        QueryBuilder<TaskHSequence> qb = getTaskHSequenceDao(context).queryBuilder();
        qb.orderAsc(TaskHSequenceDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static TaskHSequence getTaskHSeqByUUIDTaskH(Context context, String uuidTaskH) {
        QueryBuilder<TaskHSequence> qb = getTaskHSequenceDao(context).queryBuilder();
        qb.where(TaskHSequenceDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if (qb.list().size() > 0) {
            return qb.list().get(0);
        } else {
            return null;
        }
    }

    public static TaskHSequence getLastSequence(Context context) {
        QueryBuilder<TaskHSequence> qb = getTaskHSequenceDao(context).queryBuilder();
//        qb.where(TaskHSequenceDao.Properties.Sequence.eq(uuidUser));
        qb.orderAsc(TaskHSequenceDao.Properties.Sequence);
        qb.build();
        if (qb.list() != null) {
            if (qb.list().size() > 0) return qb.list().get(0);
            else return null;
        } else return null;
    }

    public static TaskHSequence getUuidTaskHSequence(Context context, String uuid_task_h) { //still error
        QueryBuilder<TaskHSequence> qb = getTaskHSequenceDao(context).queryBuilder();
        qb.where(TaskHSequenceDao.Properties.Uuid_task_h.eq(uuid_task_h));
        qb.build().forCurrentThread();
        if (qb.list() != null) {
            return qb.list().get(0);
        } else {
            return null;
        }
    }

    public static void clean(Context context) {
        getTaskHSequenceDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    public static void insertAllNewTaskHSeq(Context context, List<TaskH> taskH) {
        for (int i = 0; i < taskH.size(); i++) {
            TaskHSequence taskHSequence = new TaskHSequence();
            taskHSequence.setSequence(i);
            taskHSequence.setUuid_task_h(taskH.get(i).getUuid_task_h());
            TaskHSequenceDataAccess.addOrReplace(context, taskHSequence);
        }
    }

}
