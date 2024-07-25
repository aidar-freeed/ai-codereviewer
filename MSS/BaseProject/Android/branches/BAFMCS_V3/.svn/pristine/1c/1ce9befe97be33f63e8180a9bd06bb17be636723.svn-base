package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Emergency;
import com.adins.mss.dao.EmergencyDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class EmergencyDataAccess {



    protected static DaoSession getDaoSession(Context context) {
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get taskD dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static EmergencyDao getEmergencyDao(Context context) {
        return getDaoSession(context).getEmergencyDao();
    }

    public static void closeAll() {

        DaoOpenHelper.closeAll();
    }

    public static void add(Context context, Emergency emergency) {
        getEmergencyDao(context).insertInTx(emergency);
        getDaoSession(context).clear();
    }
    public static void addOrReplace(Context context, Emergency emergency) {
        getEmergencyDao(context).insertOrReplaceInTx(emergency);
        getDaoSession(context).clear();
    }

    public static List<Emergency> getAll(Context context){
        QueryBuilder<Emergency> qb = getEmergencyDao(context).queryBuilder();
        return qb.list();
    }

    public static void clean(Context context) {
        getEmergencyDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    public static List<Emergency> getByUser(Context context, String uuid_user){
        QueryBuilder<Emergency> qb = getEmergencyDao(context).queryBuilder();
        qb.where(EmergencyDao.Properties.Uuid_user.eq(uuid_user));
        qb.build();
        return qb.list();
    }
    public static void delete(Context context, Emergency emergency) {
        getEmergencyDao(context).delete(emergency);
        getDaoSession(context).clear();
    }
    public static void delete(Context context, String uuid_user) {
        QueryBuilder<Emergency> qb = getEmergencyDao(context).queryBuilder();
        qb.where(EmergencyDao.Properties.Uuid_user.eq(uuid_user));
        qb.build().forCurrentThread();
        getEmergencyDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }
}
