package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.DepositReportHDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class DepositReportHDataAccess {

    private DepositReportHDataAccess() {
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
     * get depositReportH dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static DepositReportHDao getDepositReportHDao(Context context) {
        return getDaoSession(context).getDepositReportHDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add depositReportH as entity
     *
     * @param context
     * @param depositReportH
     */
    public static void add(Context context, DepositReportH depositReportH) {
        getDepositReportHDao(context).insertInTx(depositReportH);
        getDaoSession(context).clear();
    }

    /**
     * add depositReportH as list entity
     *
     * @param context
     * @param depositReportHList
     */
    public static void add(Context context, List<DepositReportH> depositReportHList) {
        getDepositReportHDao(context).insertInTx(depositReportHList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getDepositReportHDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param depositReprtH
     */
    public static void delete(Context context, DepositReportH depositReprtH) {
        getDepositReportHDao(context).deleteInTx(depositReprtH);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<DepositReportH> qb = getDepositReportHDao(context).queryBuilder();
        qb.where(DepositReportHDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        getDepositReportHDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    public static void deleteDummy(Context context) {
        QueryBuilder<DepositReportH> qb = getDepositReportHDao(context).queryBuilder();
        qb.where(DepositReportHDao.Properties.Batch_id.eq("-"));
        qb.build();
        deleteListWithRelation(context, qb.list());
    }

    /**
     * @param context
     * @param depositReportH
     */
    public static void update(Context context, DepositReportH depositReportH) {
        getDepositReportHDao(context).update(depositReportH);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<DepositReportH> getAll(Context context, String uuidUser) {
        QueryBuilder<DepositReportH> qb = getDepositReportHDao(context).queryBuilder();
        qb.where(DepositReportHDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<String> getAllUuid(Context context, String uuidUser) {

        List<String> result = new ArrayList<>();

        QueryBuilder<DepositReportH> qb = getDepositReportHDao(context).queryBuilder();
        qb.where(DepositReportHDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();

        for (int i = 0; i < qb.list().size(); i++) {
            result.add(qb.list().get(i).getUuid_deposit_report_h());
        }

        return result;
    }


    public static void deleteDepositReport(Context context,
                                           Date today) {

        QueryBuilder<DepositReportH> qb = getDepositReportHDao(context).queryBuilder();
        qb.where(DepositReportHDao.Properties.Transfered_date.lt(today));
        qb.build();
        deleteListWithRelation(context, qb.list());
    }


    /**
     * delete row and the relation form other table
     *
     * @param context
     * @param depositReportH
     */
    public static void deleteWithRelation(Context context,
                                          DepositReportH depositReportH) {
        DepositReportDDataAccess.delete(context, depositReportH.getUuid_deposit_report_h());
        getDepositReportHDao(context).delete(depositReportH);
        getDaoSession(context).clear();
    }

    /**
     * Delete list of DepositReportH with their relation on the other tables
     *
     * @param context
     * @param listDepositReportH
     */
    public static void deleteListWithRelation(Context context,
                                              List<DepositReportH> listDepositReportH) {
        getDepositReportHDao(context).deleteInTx(listDepositReportH);
        getDaoSession(context).clear();

    }


    /**
     * Delete list of DepositReportH with their relation on the other tables
     *
     * @param context
     * @param uuidUser
     */
    public static List<DepositReportH> listOfBacth(Context context, String uuidUser) {
        Query<DepositReportH> query = getDepositReportHDao(context)
                .queryRawCreate(", TR_DEPOSITREPORT_D D WHERE " +
                        "T.UUID_DEPOSIT_REPORT_H = D.UUID_DEPOSIT_REPORT_H " +
                        "AND T.UUID_USER=?", uuidUser);
        return query.list();
    }

    public static List<DepositReportH> listOfBacth(Context context){
//		QueryBuilder<DepositReportH> qb = getDepositReportHDao(context).queryBuilder();
//		qb.where(DepositReportHDao.Properties.Uuid_user.eq(uuidUser));
//
//		qb.build();


        Query<DepositReportH> query  =  getDepositReportHDao(context)
                .queryRawCreate(", TR_DEPOSITREPORT_D D WHERE " +
                        "T.UUID_DEPOSIT_REPORT_H = D.UUID_DEPOSIT_REPORT_H ");

        //query.list();


        return query.list();
    }

    public static List<DepositReportH> getAllForAllUser(Context context) {
        QueryBuilder<DepositReportH> qb = getDepositReportHDao(context).queryBuilder();
        qb.where(DepositReportHDao.Properties.Branch_payment.isNull());
        qb.where(DepositReportHDao.Properties.Code_channel.isNull());
        qb.build();
        return qb.list();
    }

    public static List<DepositReportH> getAllForAllUserAC(Context context){
        QueryBuilder<DepositReportH> qb = getDepositReportHDao(context).queryBuilder();
        qb.where(DepositReportHDao.Properties.Branch_payment.isNotNull());
        qb.build();
        return qb.list();
    }

    public static List<DepositReportH> getAllForAllUserPC(Context context){
        QueryBuilder<DepositReportH> qb = getDepositReportHDao(context).queryBuilder();
        qb.where(DepositReportHDao.Properties.Code_channel.isNotNull());
        qb.build();
        return qb.list();
    }

}
