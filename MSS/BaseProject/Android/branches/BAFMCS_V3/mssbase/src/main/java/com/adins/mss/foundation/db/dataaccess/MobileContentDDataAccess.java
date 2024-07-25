package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.MobileContentD;
import com.adins.mss.dao.MobileContentDDao;
import com.adins.mss.foundation.db.DaoOpenHelper;
import com.adins.mss.foundation.formatter.Tool;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class MobileContentDDataAccess {
    private MobileContentDDataAccess() {
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
     * get mobileContentD dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static MobileContentDDao getMobileContentDDao(Context context) {
        return getDaoSession(context).getMobileContentDDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add or replace one mobileContentD
     *
     * @param context
     * @param mobileContentD
     */
    public static void addOrReplace(Context context, MobileContentD mobileContentD) {
        getMobileContentDDao(context).insertOrReplaceInTx(mobileContentD);
        getDaoSession(context).clear();
    }

    /**
     * add or replace mobileContentDList
     *
     * @param context
     * @param mobileContentDList
     */
    public static void addOrReplace(Context context, List<MobileContentD> mobileContentDList) {
        getMobileContentDDao(context).insertOrReplaceInTx(mobileContentDList);
        getDaoSession(context).clear();
    }

    /**
     * add mobileContentD as entity
     *
     * @param context
     * @param mobileContentD
     */
    public static void add(Context context, MobileContentD mobileContentD) {
        getMobileContentDDao(context).insertInTx(mobileContentD);
        getDaoSession(context).clear();
    }

    /**
     * add mobileContentD as list entity
     *
     * @param context
     * @param mobileContentD
     */
    public static void add(Context context, List<MobileContentD> mobileContentD) {
        getMobileContentDDao(context).insertInTx(mobileContentD);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getMobileContentDDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param mobileCOntentD
     */
    public static void delete(Context context, MobileContentD mobileCOntentD) {
        getMobileContentDDao(context).deleteInTx(mobileCOntentD);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by keyMobileCOntentH
     *
     * @param context
     * @param keyMobileContentH
     */
    public static void delete(Context context, String keyMobileContentH) {
        QueryBuilder<MobileContentD> qb = getMobileContentDDao(context).queryBuilder();
        qb.where(MobileContentDDao.Properties.Uuid_mobile_content_h.eq(keyMobileContentH));
        qb.build();
        getMobileContentDDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param mobileContentD
     */
    public static void update(Context context, MobileContentD mobileContentD) {
        getMobileContentDDao(context).updateInTx(mobileContentD);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_mobile_content_h = param
     *
     * @param context
     * @param keyMobileContentH
     * @return
     */
    public static List<MobileContentD> getAll(Context context, String keyMobileContentH) {
        QueryBuilder<MobileContentD> qb = getMobileContentDDao(context).queryBuilder();
        qb.where(MobileContentDDao.Properties.Uuid_mobile_content_h.eq(keyMobileContentH));
        qb.build();
        return qb.list();
    }

    public static List<MobileContentD> getAllOnDate(Context context, String keyMobileContentH) {
        QueryBuilder<MobileContentD> qb = getMobileContentDDao(context).queryBuilder();
        qb.where(MobileContentDDao.Properties.Uuid_mobile_content_h.eq(keyMobileContentH)
                , MobileContentDDao.Properties.Start_date.lt(Tool.getSystemDateTime())
                , MobileContentDDao.Properties.End_date.gt(Tool.getSystemDateTime()));
        qb.build();
        return qb.list();
    }

    /**
     * getOne mobileContentD
     *
     * @param context
     * @param keyMobileContentD
     * @return
     */
    public static MobileContentD getOne(Context context, String keyMobileContentD) {
        QueryBuilder<MobileContentD> qb = getMobileContentDDao(context).queryBuilder();
        qb.where(MobileContentDDao.Properties.Uuid_mobile_content_d.eq(keyMobileContentD));
        qb.build();
        if (!qb.list().isEmpty())
            return qb.list().get(0);
        else return null;
    }

    public static List<MobileContentD> getAllContent(Context context){
        QueryBuilder<MobileContentD> qb = getMobileContentDDao(context).queryBuilder();
        qb.build();
        return qb.list();
    }
}
