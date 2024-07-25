package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.PrintResultDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class PrintResultDataAccess {

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
     * get printResult dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static PrintResultDao getPrintResulDao(Context context) {
        return getDaoSession(context).getPrintResultDao();
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
     * add printResult as entity
     *
     * @param context
     * @param printResult
     */
    public static void add(Context context, PrintResult printResult) {
        getPrintResulDao(context).insertInTx(printResult);
        getDaoSession(context).clear();
    }

    /**
     * add printResult as list entity
     *
     * @param context
     * @param printResultList
     */
    public static void add(Context context, List<PrintResult> printResultList) {
        getPrintResulDao(context).insertInTx(printResultList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getPrintResulDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param printResult
     */
    public static void delete(Context context, PrintResult printResult) {
        getPrintResulDao(context).delete(printResult);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by keyTaskH
     *
     * @param context
     * @param keyTaskH
     */
    public static void delete(Context context, String keyTaskH) {
        QueryBuilder<PrintResult> qb = getPrintResulDao(context).queryBuilder();
        qb.where(PrintResultDao.Properties.Uuid_task_h.eq(keyTaskH));
        qb.build();
        getPrintResulDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param printResult
     */
    public static void update(Context context, PrintResult printResult) {
        getPrintResulDao(context).update(printResult);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_task_h = param
     *
     * @param context
     * @param keyTaskH
     * @return
     */
    public static List<PrintResult> getAll(Context context, String keyTaskH) {
        QueryBuilder<PrintResult> qb = getPrintResulDao(context).queryBuilder();
        qb.where(PrintResultDao.Properties.Uuid_task_h.eq(keyTaskH));
        qb.build();
        return qb.list();
    }

    /**
     * select printResult per
     *
     * @param context
     * @return
     */
}
