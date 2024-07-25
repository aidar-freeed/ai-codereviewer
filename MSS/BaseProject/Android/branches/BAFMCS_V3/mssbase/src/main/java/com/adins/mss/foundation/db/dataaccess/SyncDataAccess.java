package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Sync;
import com.adins.mss.dao.SyncDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class SyncDataAccess {

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
     * get Holiday dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static SyncDao getSyncDao(Context context) {
        return getDaoSession(context).getSyncDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add holiday as entity
     *
     * @param context
     * @param sync
     */
    public static void add(Context context, Sync sync) {
        getSyncDao(context).insert(sync);
        getDaoSession(context).clear();
    }

    /**
     * add holiday as list entity
     *
     * @param context
     * @param listSync
     */
    public static void add(Context context, List<Sync> listSync) {
        getSyncDao(context).insertInTx(listSync);
        getDaoSession(context).clear();
    }


    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getSyncDao(context).deleteAll();
    }


    /**
     * @param context
     * @param sync
     */
    public static void delete(Context context, Sync sync) {
        getSyncDao(context).delete(sync);
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param sync
     */
    public static void update(Context context, Sync sync) {
        getSyncDao(context).update(sync);
        getDaoSession(context).clear();
    }


    /**
     * add or replace data taskH
     *
     * @param context
     * @param sync
     */
    public static void addOrReplace(Context context, Sync sync) {

        Sync syncDb = getOneFromLov(context, sync.getLov_group());
        if (syncDb != null) {
            delete(context, syncDb);
        }
        add(context, sync);
        getDaoSession(context).clear();
    }

    public static void addOrReplaceAll(Context context, List<Sync> syncs) {
        getSyncDao(context).insertOrReplaceInTx(syncs);
        getDaoSession(context).clear();
    }


    public static Sync getOne(Context context, String uuid_sync) {
        QueryBuilder<Sync> qb = getSyncDao(context).queryBuilder();
        qb.where(SyncDao.Properties.Uuid_sync.eq(uuid_sync));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static Sync getOneFromLov(Context context, String lov_group) {
        QueryBuilder<Sync> qb = getSyncDao(context).queryBuilder();
        qb.where(SyncDao.Properties.Lov_group.eq(lov_group));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static Sync getOneByLovGroupName(Context context, String lov_group) {
        QueryBuilder<Sync> qb = getSyncDao(context).queryBuilder();
        qb.where(SyncDao.Properties.Lov_group.eq(lov_group));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static List<Sync> getAll(Context context) {
        QueryBuilder<Sync> qb = getSyncDao(context).queryBuilder();
        qb.build();
        if (qb.list().isEmpty()) {
            return Collections.emptyList();
        }

        return qb.list();
    }

    public static Sync getLastUpated(Context context) {
        QueryBuilder<Sync> qb = getSyncDao(context).queryBuilder();
        qb.orderDesc(SyncDao.Properties.Dtm_upd);
        qb.limit(1);
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }


    public static Sync getDay(Context context, Date date) {


        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        QueryBuilder<Sync> qb = getSyncDao(context).queryBuilder();
        qb.where(SyncDao.Properties.Dtm_upd.ge(cal.getTime()));
        qb.orderAsc(SyncDao.Properties.Dtm_upd);
        qb.limit(1);
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

}
