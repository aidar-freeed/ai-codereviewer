package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.PaymentHistoryD;
import com.adins.mss.dao.PaymentHistoryDDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class PaymentHistoryDDataAccess {
    private PaymentHistoryDDataAccess() {
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
     * get paymentHistory dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static PaymentHistoryDDao getPaymentHistoryDao(Context context) {
        return getDaoSession(context).getPaymentHistoryDDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add paymentHistory as entity
     *
     * @param context
     * @param PaymentHistoryD
     */
    public static void add(Context context, PaymentHistoryD PaymentHistoryD) {
        getPaymentHistoryDao(context).insert(PaymentHistoryD);
        getDaoSession(context).clear();
    }

    /**
     * add paymentHistory as list entity
     *
     * @param context
     * @param PaymentHistoryDList
     */
    public static void add(Context context, List<PaymentHistoryD> PaymentHistoryDList) {
        getPaymentHistoryDao(context).insertInTx(PaymentHistoryDList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, PaymentHistoryD paymentHistoryD) {
        getPaymentHistoryDao(context).insertOrReplaceInTx(paymentHistoryD);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<PaymentHistoryD> paymentHistoryDList) {
        getPaymentHistoryDao(context).insertOrReplaceInTx(paymentHistoryDList);
        getDaoSession(context).clear();
    }

    public static void update(Context context, PaymentHistoryD paymentHistoryD) {
        getPaymentHistoryDao(context).updateInTx(paymentHistoryD);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getPaymentHistoryDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param PaymentHistoryD
     */
    public static void delete(Context context, PaymentHistoryD PaymentHistoryD) {
        getPaymentHistoryDao(context).deleteInTx(PaymentHistoryD);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidTaskH
     */
    public static void delete(Context context, String uuidTaskH) {
        QueryBuilder<PaymentHistoryD> qb = getPaymentHistoryDao(context).queryBuilder();
        qb.where(PaymentHistoryDDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if (!qb.list().isEmpty()) {
            getPaymentHistoryDao(context).deleteInTx(qb.list());
        }
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param PaymentHistoryD
     */
    public static void updatePaymentHistory(Context context, PaymentHistoryD PaymentHistoryD) {
        getPaymentHistoryDao(context).update(PaymentHistoryD);
        getDaoSession(context).clear();
    }

    /**
     * get all data by uuidTaskH
     *
     * @param context
     * @param uuidTaskH
     * @return
     */
    public static List<PaymentHistoryD> getAll(Context context, String uuidTaskH) {
        QueryBuilder<PaymentHistoryD> qb = getPaymentHistoryDao(context).queryBuilder();
        qb.where(PaymentHistoryDDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if (!qb.list().isEmpty()) {
            return qb.list();
        } else return Collections.emptyList();
    }

    public static List<PaymentHistoryD> getAllByHistoryH(Context context, String uuidPaymentHistoryH) {
        QueryBuilder<PaymentHistoryD> qb = getPaymentHistoryDao(context).queryBuilder();
        qb.where(PaymentHistoryDDao.Properties.Uuid_payment_history_h.eq(uuidPaymentHistoryH));
        qb.build();
        if (!qb.list().isEmpty()) {
            return qb.list();
        } else return Collections.emptyList();
    }

    public static PaymentHistoryD getOne(Context context, String uuid_task_h) {
        QueryBuilder<PaymentHistoryD> qb = getPaymentHistoryDao(context).queryBuilder();
        qb.where(PaymentHistoryDDao.Properties.Uuid_task_h.eq(uuid_task_h));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static PaymentHistoryD getOneHistory(Context context, String uuidHistoryD) {
        QueryBuilder<PaymentHistoryD> qb = getPaymentHistoryDao(context).queryBuilder();
        qb.where(PaymentHistoryDDao.Properties.Uuid_payment_history_d.eq(uuidHistoryD));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }
}
