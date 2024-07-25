package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Logger;
import com.adins.mss.dao.LoggerDao;
import com.adins.mss.dao.ReceiptVoucher;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.DaoOpenHelper;
import com.adins.mss.foundation.formatter.Tool;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author michael.bw
 */
public class LoggerDataAccess {
    private LoggerDataAccess() {
    }

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
     * get logger dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static LoggerDao getLoggerDao(Context context) {
        return getDaoSession(context).getLoggerDao();
    }


    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }


    /**
     * @param context
     * @param uuidUser
     * @param screenId --> page of mobile application
     * @param detail   --> detail that can you inform
     */
    public static void addLog(Context context, String uuidUser, String screenId, String detail) {
        Logger logger = new Logger(Tool.getUUID(), screenId, new Date(),
                detail, uuidUser);
        getLoggerDao(context).insert(logger);

    }

    /**
     * add log as entity
     *
     * @param context
     * @param logger
     */
    public static void addLog(Context context, Logger logger) {
        getLoggerDao(context).insert(logger);

    }

    /**
     * add log as list entity
     *
     * @param context
     * @param loggerList
     */
    public static void addLog(Context context, List<Logger> loggerList) {
        getLoggerDao(context).insertInTx(loggerList);

    }


    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getLoggerDao(context).deleteAll();
    }


    /**
     * delete log berdasarkan start date dan end date
     *
     * @param context
     * @param uuidUser
     * @param startDate
     * @param endDate
     */
    public static void deleteLog(Context context, String uuidUser, Date startDate, Date endDate) {

        QueryBuilder<Logger> qb = getLoggerDao(context).queryBuilder();
        qb.where(LoggerDao.Properties.Uuid_user.eq(uuidUser),
                LoggerDao.Properties.Timestamp.between(startDate, endDate));

        qb.build();
        List<Logger> qbList = qb.list();
        getLoggerDao(context).deleteInTx(qbList);

    }

    /**
     * @param logger
     * @param context
     */
    public static void deleteLog(Logger logger, Context context) {
        getLoggerDao(context).delete(logger);
    }

    /**
     * @param logger
     * @param context
     */
    public static void updateLog(Logger logger, Context context) {
        getLoggerDao(context).update(logger);
    }


    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<Logger> getAll(Context context, String uuidUser) {
        QueryBuilder<Logger> qb = getLoggerDao(context).queryBuilder();

        qb.where(LoggerDao.Properties.Uuid_user.eq(uuidUser));

        qb.build();
        return qb.list();
    }

    /**
     * select log for today per user
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<Logger> getToday(Context context, String uuidUser) {
        QueryBuilder<Logger> qb = getLoggerDao(context).queryBuilder();
        qb.where(LoggerDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();

        User user = new User();
        user.getTaskHList();


        ReceiptVoucher kwitansi = new ReceiptVoucher();

        kwitansi.getUser().getFullname();

        return qb.list();
    }

    /**
     * Select log by range date, per user
     *
     * @param context
     * @param uuidUser
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Logger> getByDate(Context context, String uuidUser, Date startDate, Date endDate) {
        QueryBuilder<Logger> qb = getLoggerDao(context).queryBuilder();
        qb.where(LoggerDao.Properties.Uuid_user.eq(uuidUser),
                LoggerDao.Properties.Timestamp.between(startDate, endDate));
        qb.build();
        return qb.list();
    }


    /**
     * Get total row per user
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static long countLog(Context context, String uuidUser) {
        QueryBuilder<Logger> qb = getLoggerDao(context).queryBuilder();
        return qb.where(LoggerDao.Properties.Uuid_user.eq(uuidUser)).count();
    }

}
