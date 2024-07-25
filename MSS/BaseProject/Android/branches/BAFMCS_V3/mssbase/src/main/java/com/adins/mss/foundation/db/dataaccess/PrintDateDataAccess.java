package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.PrintDate;
import com.adins.mss.dao.PrintDateDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by gigin.ginanjar on 03/10/2016.
 */

public class PrintDateDataAccess {
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
     * get PrintDate dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static PrintDateDao getPrintDateDao(Context context) {
        return getDaoSession(context).getPrintDateDao();
    }

    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    public static void add(Context context, PrintDate printDate) {
        getPrintDateDao(context).insertInTx(printDate);
        getDaoSession(context).clear();
    }

    /**
     * add PrintDate as list entity
     *
     * @param context
     * @param printDateList
     */
    public static void add(Context context, List<PrintDate> printDateList) {
        getPrintDateDao(context).insertInTx(printDateList);
        getDaoSession(context).clear();
    }

    /**
     * addOrReplace PrintDate as entity
     *
     * @param context
     * @param printDate
     */
    public static void addOrReplace(Context context, PrintDate printDate) {
        getPrintDateDao(context).insertOrReplaceInTx(printDate);
        getDaoSession(context).clear();
    }

    /**
     * addOrReplace PrintDate as list entity
     *
     * @param context
     * @param printDateList
     */
    public static void addOrReplace(Context context, List<PrintDate> printDateList) {
        getPrintDateDao(context).insertOrReplaceInTx(printDateList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getPrintDateDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param PrintDate
     * @param context
     */
    public static void delete(Context context, PrintDate PrintDate) {
        getPrintDateDao(context).delete(PrintDate);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by keyScheme
     *
     * @param context
     */
    public static void delete(Context context, String uuid_task_h) {
        QueryBuilder<PrintDate> qb = getPrintDateDao(context).queryBuilder();
        qb.where(PrintDateDao.Properties.Uuid_task_h.eq(uuid_task_h));
        qb.build();
        getPrintDateDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }
    public static void delete(Context context, long dtm_crt) {
        DeleteQuery<PrintDate> dq = getPrintDateDao(context).queryBuilder()
                .where(PrintDateDao.Properties.Dtm_print.eq(dtm_crt))
                .buildDelete();
        dq.executeDeleteWithoutDetachingEntities();
        getDaoSession(context).clear();
    }

    /**
     * @param PrintDate
     * @param context
     */
    public static void update(Context context, PrintDate PrintDate) {
        getPrintDateDao(context).update(PrintDate);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_scheme = param
     *
     * @param context
     * @param uuid_task_h
     * @return
     */
    public static List<PrintDate> getAll(Context context, String uuid_task_h) {
        QueryBuilder<PrintDate> qb = getPrintDateDao(context).queryBuilder();
        qb.where(PrintDateDao.Properties.Uuid_task_h.eq(uuid_task_h));
        qb.build();
        return qb.list();
    }

    public static List<PrintDate> getAll(Context context) {
        QueryBuilder<PrintDate> qb = getPrintDateDao(context).queryBuilder();
        qb.where( new WhereCondition.StringCondition //2018-12-17 : Nendi - Exclude Revisit
                ("UUID_TASK_H NOT IN " +
                        "(SELECT UUID_TASK_H FROM TR_TASK_H WHERE TR_TASK_H.UUID_TASK_H != TR_TASK_H.TASK_ID)"));
        qb.build();
        return qb.list();
    }

    /**
     * select * from table where uuid_print_item = param
     *
     * @param context
     * @param uuid_task_h
     * @return
     */
    public static PrintDate getOne(Context context, String uuid_task_h) {
        QueryBuilder<PrintDate> qb = getPrintDateDao(context).queryBuilder();
        qb.where(PrintDateDao.Properties.Uuid_task_h.eq(uuid_task_h));
        qb.build();
        if (qb.list().size() > 0) {
            return qb.list().get(0);
        } else {
            return null;
        }
    }

    public static PrintDate getOneHeader(Context context, Date dtm_print) {
        QueryBuilder<PrintDate> qb = getPrintDateDao(context).queryBuilder();
        qb.where(PrintDateDao.Properties.Dtm_print.eq(dtm_print));
        qb.build();
        if (qb.list().size() == 0)
            return null;
        return qb.list().get(0);
    }
}
