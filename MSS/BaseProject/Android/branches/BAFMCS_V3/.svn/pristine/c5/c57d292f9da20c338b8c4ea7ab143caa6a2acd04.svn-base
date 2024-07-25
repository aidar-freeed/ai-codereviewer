package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.InstallmentSchedule;
import com.adins.mss.dao.InstallmentScheduleDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class InstallmentScheduleDataAccess {

    private InstallmentScheduleDataAccess() {
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
     * get installmentSchedule dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static InstallmentScheduleDao getInstallmentScheduleDao(Context context) {
        return getDaoSession(context).getInstallmentScheduleDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add installmentSchedule as entity
     *
     * @param context
     * @param installmentSchedule
     */
    public static void add(Context context, InstallmentSchedule installmentSchedule) {
        getInstallmentScheduleDao(context).insertInTx(installmentSchedule);
        getDaoSession(context).clear();
    }

    /**
     * add installmentSchedule as list entity
     *
     * @param context
     * @param installmentScheduleList
     */
    public static void add(Context context, List<InstallmentSchedule> installmentScheduleList) {
        getInstallmentScheduleDao(context).insertInTx(installmentScheduleList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getInstallmentScheduleDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param installmentSchedule
     */
    public static void delete(Context context, InstallmentSchedule installmentSchedule) {
        getInstallmentScheduleDao(context).deleteInTx(installmentSchedule);
        getDaoSession(context).clear();
    }

    /**
     * delete data by uuidTaskH
     *
     * @param context
     * @param uuidTaskH
     */
    public static void delete(Context context, String uuidTaskH) {
        QueryBuilder<InstallmentSchedule> qb = getInstallmentScheduleDao(context).queryBuilder();
        qb.where(InstallmentScheduleDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if (!qb.list().isEmpty()) {
            getInstallmentScheduleDao(context).deleteInTx(qb.list());
        }
        getDaoSession(context).clear();
    }

    public static void deleteByAgreementNo(Context context, String agreement_no) {
        QueryBuilder<InstallmentSchedule> qb = getInstallmentScheduleDao(context).queryBuilder();
        qb.where(InstallmentScheduleDao.Properties.Agreement_no.eq(agreement_no));
        qb.build();
        if (!qb.list().isEmpty()) {
            getInstallmentScheduleDao(context).deleteInTx(qb.list());
        }
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param installmentSchedule
     */
    public static void update(Context context, InstallmentSchedule installmentSchedule) {
        getInstallmentScheduleDao(context).update(installmentSchedule);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, InstallmentSchedule installmentSchedule) {
        getInstallmentScheduleDao(context).insertOrReplaceInTx(installmentSchedule);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<InstallmentSchedule> installmentSchedules) {
        getInstallmentScheduleDao(context).insertOrReplaceInTx(installmentSchedules);
        getDaoSession(context).clear();
    }

    private static InstallmentSchedule getOne(Context context, String agreementNo) {
        QueryBuilder<InstallmentSchedule> qb = getInstallmentScheduleDao(context).queryBuilder();
        qb.where(InstallmentScheduleDao.Properties.Agreement_no.eq(agreementNo));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    private static InstallmentSchedule getOneInst(Context context, String uuid_installment) {
        QueryBuilder<InstallmentSchedule> qb = getInstallmentScheduleDao(context).queryBuilder();
        qb.where(InstallmentScheduleDao.Properties.Uuid_installment_schedule.eq(uuid_installment));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    //
//	/**
//	 * select * from table where uuid_user = param
//	 *
//	 * @param context
//	 * @param uuidUser
//	 * @return
//	 */
    public static List<InstallmentSchedule> getAll(Context context) {
        QueryBuilder<InstallmentSchedule> qb = getInstallmentScheduleDao(context).queryBuilder();
        qb.build();
        return qb.list();
    }

    /**
     * get all data by agreementNo
     *
     * @param context
     * @param agreementNo
     * @return
     */
    public static List<InstallmentSchedule> getAll(Context context, String agreementNo) {
        QueryBuilder<InstallmentSchedule> qb = getInstallmentScheduleDao(context).queryBuilder();
        qb.where(InstallmentScheduleDao.Properties.Agreement_no.eq(agreementNo));
        qb.build();
        if (!qb.list().isEmpty()) {
            return qb.list();
        } else return Collections.emptyList();
    }

    /**
     * select installmentSchedule per
     *
     * @param context
     * @return
     */
    public static List<InstallmentSchedule> getAllByTask(Context context, String uuidTaskH) {
        QueryBuilder<InstallmentSchedule> qb = getInstallmentScheduleDao(context).queryBuilder();
        qb.where(InstallmentScheduleDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if (!qb.list().isEmpty()) {
            return qb.list();
        } else return Collections.emptyList();
    }
}
