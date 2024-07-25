package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.PrintItemDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class PrintItemDataAccess {
    private PrintItemDataAccess() {
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
     * get printItem dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static PrintItemDao getPrintItemDao(Context context) {
        return getDaoSession(context).getPrintItemDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add printItem as entity
     *
     * @param context
     * @param printItem
     */
    public static void add(Context context, PrintItem printItem) {
        getPrintItemDao(context).insertInTx(printItem);
        getDaoSession(context).clear();
    }

    /**
     * add printItem as list entity
     *
     * @param context
     * @param printItemList
     */
    public static void add(Context context, List<PrintItem> printItemList) {
        getPrintItemDao(context).insertInTx(printItemList);
        getDaoSession(context).clear();
    }

    /**
     * addOrReplace printItem as entity
     *
     * @param context
     * @param printItem
     */
    public static void addOrReplace(Context context, PrintItem printItem) {
        getPrintItemDao(context).insertOrReplaceInTx(printItem);
        getDaoSession(context).clear();
    }

    /**
     * addOrReplace printItem as list entity
     *
     * @param context
     * @param printItemList
     */
    public static void addOrReplace(Context context, List<PrintItem> printItemList) {
        getPrintItemDao(context).insertOrReplaceInTx(printItemList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getPrintItemDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param printItem
     * @param context
     */
    public static void delete(Context context, PrintItem printItem) {
        getPrintItemDao(context).delete(printItem);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by keyScheme
     *
     * @param context
     */
    public static void delete(Context context, String keyScheme) {
        QueryBuilder<PrintItem> qb = getPrintItemDao(context).queryBuilder();
        qb.where(PrintItemDao.Properties.Uuid_scheme.eq(keyScheme));
        qb.build();
        getPrintItemDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param printItem
     * @param context
     */
    public static void update(Context context, PrintItem printItem) {
        getPrintItemDao(context).update(printItem);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_scheme = param
     *
     * @param context
     * @param keyScheme
     * @return
     */
    public static List<PrintItem> getAll(Context context, String keyScheme) {
        QueryBuilder<PrintItem> qb = getPrintItemDao(context).queryBuilder();
        qb.where(PrintItemDao.Properties.Uuid_scheme.eq(keyScheme));
        qb.build();
        return qb.list();
    }

    /**
     * select * from table where uuid_print_item = param
     *
     * @param context
     * @param uuidPrintItem
     * @return
     */
    public static PrintItem getOne(Context context, String uuidPrintItem) {
        QueryBuilder<PrintItem> qb = getPrintItemDao(context).queryBuilder();
        qb.where(PrintItemDao.Properties.Uuid_print_item.eq(uuidPrintItem));
        qb.build();
        if (!qb.list().isEmpty()) {
            return qb.list().get(0);
        } else {
            return null;
        }
    }

}
