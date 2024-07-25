package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.LocationInfoDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class LocationInfoDataAccess {
    private LocationInfoDataAccess() {
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
     * get locationTracking dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static LocationInfoDao getLocationInfoDao(Context context) {
        return getDaoSession(context).getLocationInfoDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add locationTracking as entity
     *
     * @param context
     * @param locationInfo
     */
    public static void add(Context context, LocationInfo locationInfo) {
        getLocationInfoDao(context).insertInTx(locationInfo);
        getDaoSession(context).clear();
    }

    /**
     * add locationTracking as list entity
     *
     * @param context
     * @param locationInfoList
     */
    public static void add(Context context, List<LocationInfo> locationInfoList) {
        getLocationInfoDao(context).insertInTx(locationInfoList);
        getDaoSession(context).clear();
    }

    /**
     * addOrEplace locationTracking as entity
     *
     * @param context
     * @param locationInfo
     */
    public static void addOrReplace(Context context, LocationInfo locationInfo) {
        getLocationInfoDao(context).insertOrReplaceInTx(locationInfo);
        getDaoSession(context).clear();
    }

    /**
     * addOrReplace locationTracking as list entity
     *
     * @param context
     * @param locationInfoList
     */
    public static void addOrReplace(Context context, List<LocationInfo> locationInfoList) {
        getLocationInfoDao(context).insertOrReplaceInTx(locationInfoList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getLocationInfoDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param locationInfo
     */
    public static void delete(Context context, LocationInfo locationInfo) {
        getLocationInfoDao(context).deleteInTx(locationInfo);
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param listLocationInfo
     */
    public static void deleteList(Context context, List<LocationInfo> listLocationInfo) {
        getLocationInfoDao(context).deleteInTx(listLocationInfo);
        getDaoSession(context).clear();
    }

    public static void deleteListByKey(Context context, List<String> keys) {
        getLocationInfoDao(context).deleteByKeyInTx(keys);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<LocationInfo> qb = getLocationInfoDao(context).queryBuilder();
        qb.where(LocationInfoDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        getLocationInfoDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * delete where location_type = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static void deleteAllbyType(Context context, String uuidUser, String locationType) {
        QueryBuilder<LocationInfo> qb = getLocationInfoDao(context).queryBuilder();
        qb.where(LocationInfoDao.Properties.Uuid_user.eq(uuidUser),
                (LocationInfoDao.Properties.Location_type.eq(locationType)));
        qb.build();
        getLocationInfoDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param locationInfo
     */
    public static void update(Context context, LocationInfo locationInfo) {
        getLocationInfoDao(context).updateInTx(locationInfo);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_user = Global.user.Uuid_user
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<LocationInfo> getAll(Context context, String uuidUser) {
        QueryBuilder<LocationInfo> qb = getLocationInfoDao(context).queryBuilder();
        qb.where(LocationInfoDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    /**
     * select * from table where uuid_user = Global.user.Uuid_user
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<LocationInfo> getAllbyType(Context context, String uuidUser, String locationType) {
        QueryBuilder<LocationInfo> qb = getLocationInfoDao(context).queryBuilder();
        qb.where(LocationInfoDao.Properties.Uuid_user.eq(uuidUser),
                (LocationInfoDao.Properties.Location_type.eq(locationType)));
        qb.build();
        return qb.list();
    }

    public static List<LocationInfo> getAllbyTypewithlimited(Context context, String uuidUser, String locationType, int limited) {
        QueryBuilder<LocationInfo> qb = getLocationInfoDao(context).queryBuilder();
        qb.where(LocationInfoDao.Properties.Uuid_user.eq(uuidUser),
                (LocationInfoDao.Properties.Location_type.eq(locationType)));
        qb.orderAsc(LocationInfoDao.Properties.Handset_time);
        qb.limit(limited);
        qb.build();
        return qb.list();
    }

    /**
     * getOne location info
     *
     * @param context
     * @param uuidLocationInfo
     * @return
     */
    public static LocationInfo getOne(Context context, String uuidLocationInfo) {
        QueryBuilder<LocationInfo> qb = getLocationInfoDao(context).queryBuilder();
        qb.where(LocationInfoDao.Properties.Uuid_location_info.eq(uuidLocationInfo));
        qb.build();
        if (qb.list().size() == 1)
            return qb.list().get(0);
        else return null;
    }

    public static LocationInfo getLastOne(Context context) {
        QueryBuilder<LocationInfo> qb = getLocationInfoDao(context).queryBuilder();
        qb.orderDesc(LocationInfoDao.Properties.Handset_time);
        qb.build();
        if (!qb.list().isEmpty())
            return qb.list().get(0);
        else return null;
    }
}
