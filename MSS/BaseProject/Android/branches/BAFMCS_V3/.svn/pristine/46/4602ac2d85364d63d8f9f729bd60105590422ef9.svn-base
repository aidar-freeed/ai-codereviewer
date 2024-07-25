package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.ErrorLog;
import com.adins.mss.dao.ErrorLogDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by ACER 471 on 3/27/2017.
 */

public class ErrorLogDataAccess {
    protected static DaoSession getDaoSession(Context context) {
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get Training dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static ErrorLogDao getErrorLogDao(Context context) {
        return getDaoSession(context).getErrorLogDao();
    }

    public static List<ErrorLog> getAllErrorLog(Context context) {
        QueryBuilder<ErrorLog> qb = getErrorLogDao(context).queryBuilder();
        qb.orderAsc(ErrorLogDao.Properties.Dtm_activity);
        qb.build();
        return qb.list();
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
     * @param errorLog
     * @return
     */
    public static void addOrReplace(Context context, ErrorLog errorLog) {
        getErrorLogDao(context).insertOrReplaceInTx(errorLog);
        getDaoSession(context).clear();
    }
}
