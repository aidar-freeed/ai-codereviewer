package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.ReceiptHistory;
import com.adins.mss.dao.ReceiptHistoryDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ReceiptHistoryDataAccess {

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
     * get receiptHistory dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static ReceiptHistoryDao getReceiptHistoryDao(Context context) {
        return getDaoSession(context).getReceiptHistoryDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add collectionActivity as entity
     *
     * @param context
     * @param ReceiptHistory
     */
    public static void add(Context context, ReceiptHistory ReceiptHistory) {
        getReceiptHistoryDao(context).insertInTx(ReceiptHistory);
        getDaoSession(context).clear();
    }

    /**
     * add collectionActivity as list entity
     *
     * @param context
     * @param receiptHistoryList
     */
    public static void add(Context context, List<ReceiptHistory> receiptHistoryList) {
        getReceiptHistoryDao(context).insertInTx(receiptHistoryList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getReceiptHistoryDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param receiptHistory
     */
    public static void delete(Context context, ReceiptHistory receiptHistory) {
        getReceiptHistoryDao(context).delete(receiptHistory);
        getDaoSession(context).clear();
    }

    public static void delete(Context context, String uuidTaskH) {
        QueryBuilder<ReceiptHistory> qb = getReceiptHistoryDao(context).queryBuilder();
        qb.where(ReceiptHistoryDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if (qb.list().size() > 0) {
            getReceiptHistoryDao(context).deleteInTx(qb.list());
        }
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param receiptHistory
     */
    public static void update(Context context, ReceiptHistory receiptHistory) {
        getReceiptHistoryDao(context).update(receiptHistory);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, ReceiptHistory receiptHistory) {
        getReceiptHistoryDao(context).insertOrReplaceInTx(receiptHistory);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<ReceiptHistory> receiptHistories) {
        getReceiptHistoryDao(context).insertOrReplaceInTx(receiptHistories);
        getDaoSession(context).clear();
    }

    public static List<ReceiptHistory> getAllByTask(Context context, String uuidTaskH) {
        QueryBuilder<ReceiptHistory> qb = getReceiptHistoryDao(context).queryBuilder();
        qb.where(ReceiptHistoryDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if (!qb.list().isEmpty())
            return qb.list();
        else return null;
    }

}
