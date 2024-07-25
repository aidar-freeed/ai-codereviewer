package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.PaymentHistoryH;
import com.adins.mss.dao.PaymentHistoryHDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class PaymentHistoryHDataAccess {
    private PaymentHistoryHDataAccess() {
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
    protected static PaymentHistoryHDao getPaymentHistoryDao(Context context) {
        return getDaoSession(context).getPaymentHistoryHDao();
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
     * @param paymentHistoryH
     */
    public static void add(Context context, PaymentHistoryH paymentHistoryH) {
        getPaymentHistoryDao(context).insert(paymentHistoryH);
        getDaoSession(context).clear();
    }

    /**
     * add paymentHistory as list entity
     *
     * @param context
     * @param paymentHistoryHList
     */
    public static void add(Context context, List<PaymentHistoryH> paymentHistoryHList) {
        getPaymentHistoryDao(context).insertInTx(paymentHistoryHList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getPaymentHistoryDao(context).deleteAll();
    }

    /**
     * @param context
     * @param paymentHistoryH
     */
    public static void delete(Context context, PaymentHistoryH paymentHistoryH) {
        getPaymentHistoryDao(context).delete(paymentHistoryH);
        getDaoSession(context).clear();
    }

    /**
     * delete data by uuidTaskH
     *
     * @param context
     * @param uuidTaskH
     */
    public static void delete(Context context, String uuidTaskH) {
        QueryBuilder<PaymentHistoryH> qb = getPaymentHistoryDao(context).queryBuilder();
        qb.where(PaymentHistoryHDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if (!qb.list().isEmpty()) {
            getPaymentHistoryDao(context).deleteInTx(qb.list());
        }
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param paymentHistoryH
     */
    public static void updatePaymentHistory(Context context, PaymentHistoryH paymentHistoryH) {
        getPaymentHistoryDao(context).update(paymentHistoryH);
        getDaoSession(context).clear();
    }

    /**
     * get all data by agreementNo
     *
     * @param context
     * @param agreementNo
     * @return
     */
    public static List<PaymentHistoryH> getAll(Context context, String agreementNo) {
        QueryBuilder<PaymentHistoryH> qb = getPaymentHistoryDao(context).queryBuilder();
        qb.where(PaymentHistoryHDao.Properties.Agreement_no.eq(agreementNo));
        qb.build();
        if (qb.list() != null) {
            if (!qb.list().isEmpty()) {
                return qb.list();
            }
            return Collections.emptyList();
        } else return Collections.emptyList();
    }

    public static List<PaymentHistoryH> getAllbyTask(Context context, String uuidTaskH) {
        QueryBuilder<PaymentHistoryH> qb = getPaymentHistoryDao(context).queryBuilder();
        qb.where(PaymentHistoryHDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if (qb.list() != null) {
            if (!qb.list().isEmpty()) {
                return qb.list();
            }
            return Collections.emptyList();
        } else return Collections.emptyList();
    }

    public static void addOrReplace(Context context, PaymentHistoryH paymentHistoryH) {
        if (getOnePH(context, paymentHistoryH.getUuid_payment_history_h()) != null)
            update(context, paymentHistoryH);
        else
            add(context, paymentHistoryH);
    }

    public static void addOrReplace(Context context, List<PaymentHistoryH> paymentHistoryHList) {
        getPaymentHistoryDao(context).insertOrReplaceInTx(paymentHistoryHList);
        getDaoSession(context).clear();
    }

    public static void update(Context context, PaymentHistoryH paymentHistoryH) {
        getPaymentHistoryDao(context).update(paymentHistoryH);
        getDaoSession(context).clear();
    }

    public static PaymentHistoryH getOne(Context context, String uuid_task_h) {
        QueryBuilder<PaymentHistoryH> qb = getPaymentHistoryDao(context).queryBuilder();
        qb.where(PaymentHistoryHDao.Properties.Uuid_task_h.eq(uuid_task_h));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static PaymentHistoryH getOnePH(Context context, String uuid_paymentHistoryH) {
        QueryBuilder<PaymentHistoryH> qb = getPaymentHistoryDao(context).queryBuilder();
        qb.where(PaymentHistoryHDao.Properties.Uuid_payment_history_h.eq(uuid_paymentHistoryH));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }
    /**
     * select paymentHistory per
     *
     * @param context
     * @return
     */
}
