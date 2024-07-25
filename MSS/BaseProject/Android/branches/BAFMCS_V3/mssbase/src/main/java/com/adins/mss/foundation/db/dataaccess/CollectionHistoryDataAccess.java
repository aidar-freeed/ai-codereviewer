package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.CollectionHistory;
import com.adins.mss.dao.CollectionHistoryDao;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class CollectionHistoryDataAccess {

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
     * get collectionHistory dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static CollectionHistoryDao getCollectionHistoryDao(Context context) {
        return getDaoSession(context).getCollectionHistoryDao();
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
     * add collectionHistory as entity
     *
     * @param context
     * @param collectionHistory
     */
    public static void add(Context context, CollectionHistory collectionHistory) {
        getCollectionHistoryDao(context).insert(collectionHistory);
        getDaoSession(context).clear();
    }

    /**
     * add collectionHistory as list entity
     *
     * @param context
     * @param collectinoHistoryList
     */
    public static void add(Context context, List<CollectionHistory> collectinoHistoryList) {
        getCollectionHistoryDao(context).insertInTx(collectinoHistoryList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getCollectionHistoryDao(context).deleteAll();
    }

    /**
     * @param context
     * @param collectionHistory
     */
    public static void delete(Context context, CollectionHistory collectionHistory) {
        getCollectionHistoryDao(context).deleteInTx(collectionHistory);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<CollectionHistory> qb = getCollectionHistoryDao(context).queryBuilder();
        qb.where(CollectionHistoryDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        getCollectionHistoryDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param collectionHistory
     */
    public static void update(Context context, CollectionHistory collectionHistory) {
        getCollectionHistoryDao(context).update(collectionHistory);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<CollectionHistory> getAll(Context context, String uuidUser) {
        QueryBuilder<CollectionHistory> qb = getCollectionHistoryDao(context).queryBuilder();
        qb.where(CollectionHistoryDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    /**
     * select collectionHistpry per
     *
     * @param context
     * @return
     */

}
