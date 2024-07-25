package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.LastSync;
import com.adins.mss.dao.LastSyncDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class LastSyncDataAccess {
    protected static DaoSession getDaoSession(Context context) {
//        if (daoOpenHelper == null) {
//            if (daoOpenHelper.getDaoSession() == null)
//                daoOpenHelper = new DaoOpenHelper(context);
//        }
//        DaoSession daoSeesion = daoOpenHelper.getDaoSession();
//        return daoSeesion;
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get menuDao dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static LastSyncDao getMenuDao(Context context) {
        return getDaoSession(context).getLastSyncDao();
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

    public static void add(Context context, List<LastSync> lastSyncList) {
        getMenuDao(context).insertInTx(lastSyncList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, LastSync lastSync) {
         	                /*if(getOne(context, menu.getUuid_menu())==null)
 	                        add(context, menu);
 	                else update(context, menu);*/
        getMenuDao(context).insertOrReplaceInTx(lastSync);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<LastSync> lastSyncList) {
         	                /*for(Menu menu : menuList){
 	                        if(getOne(context, menu.getUuid_menu())==null)
 	                                add(context, menu);
 	                        else update(context, menu);
 	                }*/
        getMenuDao(context).insertOrReplaceInTx(lastSyncList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getMenuDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param lastSync
     * @param context
     */
    public static void delete(Context context, LastSync lastSync) {
        getMenuDao(context).deleteInTx(lastSync);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by lastSync
     *
     * @param context
     * @param uuidLastSync
     */
    public static void delete(Context context, String uuidLastSync) {
        QueryBuilder<LastSync> qb = getMenuDao(context).queryBuilder();
        qb.where(LastSyncDao.Properties.Uuid_last_sync.eq(uuidLastSync));
        qb.build();
        getMenuDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param lastSync
     * @param context
     */
    public static void update(Context context, LastSync lastSync) {
        getMenuDao(context).updateInTx(lastSync);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_last_sync = param
     *
     * @param uuidLastSync
     * @return
     */
    public static List<LastSync> getAll(Context context, String uuidLastSync) {
        QueryBuilder<LastSync> qb = getMenuDao(context).queryBuilder();
        qb.where(LastSyncDao.Properties.Uuid_last_sync.eq(uuidLastSync));
        qb.build();
        return qb.list();
    }

    /**
     * get one menu by menu uuid_last_sync
     *
     * @param context
     * @param uuidLastSync
     * @return
     */
    public static LastSync getOne(Context context, String uuidLastSync) {
        QueryBuilder<LastSync> qb = getMenuDao(context).queryBuilder();
        qb.where(LastSyncDao.Properties.Uuid_last_sync.eq(uuidLastSync));
        qb.build();
        if (qb.list() != null) {
            if (!qb.list().isEmpty()) {
                return qb.list().get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static List<LastSync> getAllBySentStatus(Context context, int status) {
        QueryBuilder<LastSync> qb = getMenuDao(context).queryBuilder();
        qb.where(LastSyncDao.Properties.Is_send.eq(status));
        qb.build();
        return qb.list();
    }
}

