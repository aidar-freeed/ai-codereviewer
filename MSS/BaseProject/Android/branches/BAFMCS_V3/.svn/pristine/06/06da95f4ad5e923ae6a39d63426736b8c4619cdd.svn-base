package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportDDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class DepositReportDDataAccess {

//	private static DaoOpenHelper daoOpenHelper;

    /**
     * use to generate dao session that you can access modelDao
     *
     * @param context --> context from activity
     * @return
     */
    protected static DaoSession getDaoSession(Context context) {
        /*if(daoOpenHelper==null){
//			if(daoOpenHelper.getDaoSession()==null)
				daoOpenHelper = new DaoOpenHelper(context);
		}
		DaoSession daoSeesion = daoOpenHelper.getDaoSession();
		return daoSeesion;*/
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get depositReportD dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static DepositReportDDao getDepositReportDDao(Context context) {
        return getDaoSession(context).getDepositReportDDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
		/*if(daoOpenHelper!=null){
			daoOpenHelper.closeAll();
			daoOpenHelper = null;
		}*/
        DaoOpenHelper.closeAll();
    }

    /**
     * add depositReportD as entity
     *
     * @param context
     * @param depositReportD
     */
    public static void add(Context context, DepositReportD depositReportD) {
        getDepositReportDDao(context).insertInTx(depositReportD);
        getDaoSession(context).clear();
    }

    /**
     * add depositReportD as list entity
     *
     * @param context
     * @param depositReportDList
     */
    public static void add(Context context, List<DepositReportD> depositReportDList) {
        getDepositReportDDao(context).insertInTx(depositReportDList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getDepositReportDDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param depositReportD
     */
    public static void delete(Context context, DepositReportD depositReportD) {
        getDepositReportDDao(context).deleteInTx(depositReportD);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by keyDepositReportH
     *
     * @param context
     * @param keyDepositReportH
     */
    public static void delete(Context context, String keyDepositReportH) {
        QueryBuilder<DepositReportD> qb = getDepositReportDDao(context).queryBuilder();
        qb.where(DepositReportDDao.Properties.Uuid_deposit_report_h.eq(keyDepositReportH));
        qb.build();
        getDepositReportDDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    public static void deleteDummy(Context context) {
        QueryBuilder<DepositReportD> qb = getDepositReportDDao(context).queryBuilder();
        qb.where(DepositReportDDao.Properties.Is_sent.eq("10"));
        qb.build();
        getDepositReportDDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param depositReportD
     */
    public static void update(Context context, DepositReportD depositReportD) {
        getDepositReportDDao(context).update(depositReportD);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_depositReportD = param
     *
     * @param context
     * @param keyDepositReportH
     * @return
     */
    public static List<DepositReportD> getAll(Context context, String keyDepositReportH) {
        QueryBuilder<DepositReportD> qb = getDepositReportDDao(context).queryBuilder();
        qb.where(DepositReportDDao.Properties.Uuid_deposit_report_h.eq(keyDepositReportH));
        qb.build();
        return qb.list();
    }

    /**
     * get all rows from deposit report D
     * select * from DepositReportD
     *
     * @param context
     * @return
     */
    public static List<DepositReportD> getAll(Context context) {
        QueryBuilder<DepositReportD> qb = getDepositReportDDao(context).queryBuilder();
        qb.build();
        return qb.list();
    }

    /**
     * select depositReportD per
     *
     * @param context
     * @return
     */

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @return
     */
    public static List<String> getAllUuid(Context context) {

        List<String> result = new ArrayList<String>();

        QueryBuilder<DepositReportD> qb = getDepositReportDDao(context).queryBuilder();
        qb.build();

        for (int i = 0; i < qb.list().size(); i++) {
            result.add(qb.list().get(i).getUuid_task_h());
        }

        return result;
    }

}
