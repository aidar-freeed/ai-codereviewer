package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.LogoPrint;
import com.adins.mss.dao.LogoPrintDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class LogoPrintDataAccess {

    protected static DaoSession getDaoSession(Context context) {
        return DaoOpenHelper.getDaoSession(context);
    }

    protected static LogoPrintDao getLogoPrintDao(Context context) {
        return getDaoSession(context).getLogoPrintDao();
    }

    public static void add(Context context, LogoPrint logoPrint) {
        getLogoPrintDao(context).insert(logoPrint);
        getDaoSession(context).clear();
    }

    public static void add(Context context, List<LogoPrint> logoPrintList) {
        getLogoPrintDao(context).insertInTx(logoPrintList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, LogoPrint logoPrint) {
        getLogoPrintDao(context).insertOrReplaceInTx(logoPrint);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<LogoPrint> logoPrintList) {
        getLogoPrintDao(context).insertOrReplaceInTx(logoPrintList);
        getDaoSession(context).clear();
    }

    public static void clean(Context context) {
        getLogoPrintDao(context).deleteAll();
    }

    public static void delete(Context context, LogoPrint logoPrint) {
        getLogoPrintDao(context).delete(logoPrint);
        getDaoSession(context).clear();
    }

    public static void update(Context context, LogoPrint logoPrint) {
        getLogoPrintDao(context).update(logoPrint);
    }

    public static LogoPrint getOne(Context context, String tenant) {
        QueryBuilder<LogoPrint> qb = getLogoPrintDao(context).queryBuilder();
        qb.where(LogoPrintDao.Properties.Tenant.eq(tenant));
        qb.build().forCurrentThread();
        if (qb.list().size() == 0)
            return null;
        return qb.list().get(0);
    }

    public static List<LogoPrint> getAll(Context context) {
        QueryBuilder<LogoPrint> qb = getLogoPrintDao(context).queryBuilder();
        qb.build();
        return qb.list();
    }
}
