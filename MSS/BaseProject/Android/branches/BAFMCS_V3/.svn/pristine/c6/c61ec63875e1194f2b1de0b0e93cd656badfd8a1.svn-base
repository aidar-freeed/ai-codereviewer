package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.GroupTask;
import com.adins.mss.dao.GroupTaskDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by olivia.dg on 11/20/2017.
 */

public class GroupTaskDataAccess {
    protected static DaoSession getDaoSession(Context context){
        return DaoOpenHelper.getDaoSession(context);
    }

    protected static GroupTaskDao getGroupTaskDao(Context context) {
        return getDaoSession(context).getGroupTaskDao();
    }

    public static void add(Context context, GroupTask groupTask){
        getGroupTaskDao(context).insert(groupTask);
        getDaoSession(context).clear();
    }

    public static void add(Context context, List<GroupTask> groupTaskList){
        getGroupTaskDao(context).insertInTx(groupTaskList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, GroupTask groupTask){
        getGroupTaskDao(context).insertOrReplaceInTx(groupTask);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<GroupTask> groupTaskList){
        getGroupTaskDao(context).insertOrReplaceInTx(groupTaskList);
        getDaoSession(context).clear();
    }

    public static void clean(Context context){
        getGroupTaskDao(context).deleteAll();
    }

    public static void delete(Context context, GroupTask groupTask){
        getGroupTaskDao(context).delete(groupTask);
        getDaoSession(context).clear();
    }

    public static void update(Context context, GroupTask groupTask){
        getGroupTaskDao(context).update(groupTask);
    }

    public static List<GroupTask> getAll(Context context){
        QueryBuilder<GroupTask> qb = getGroupTaskDao(context).queryBuilder();
        qb.build();
        return qb.list();
    }

    public static List<GroupTask> getAllByAccount(Context context, String uuidAccount){
        QueryBuilder<GroupTask> qb = getGroupTaskDao(context).queryBuilder();
        qb.where(GroupTaskDao.Properties.Uuid_account.eq(uuidAccount));
        qb.build();
        return qb.list();
    }

    public static GroupTask getOneHeader(Context context, String groupTaskId){
        QueryBuilder<GroupTask> qb = getGroupTaskDao(context).queryBuilder();
        qb.where(GroupTaskDao.Properties.Group_task_id.eq(groupTaskId));
        qb.build();

        if ((qb.list() == null) || qb.list().isEmpty()) return null;
        return qb.list().get(0);
    }
}
