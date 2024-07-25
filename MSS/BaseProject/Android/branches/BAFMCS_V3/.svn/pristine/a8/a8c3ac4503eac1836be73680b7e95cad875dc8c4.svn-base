package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.GeneralParameterDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class GeneralParameterDataAccess {

    public static final String GS_PARAM_INTERVAL_REFRESH = "intervalRefresh";
    public static final String GS_PARAM_INTERVAL_TRACKING = "intervalTracking";
    public static final String GS_PARAM_INTERVAL_AUTOSEND = "intervalAutoSend";
    public static final String GS_PARAM_INTERVAL_GPS_TIMEOUT = "intervalGPSTimeout";
    public static final String GS_PARAM_SEPARATE_UPLOAD = "separateUpload";
    public static final String GS_PARAM_CAMERA = "camera";

    private GeneralParameterDataAccess() {
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
     * get generalParameter dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static GeneralParameterDao getGeneralParameterDao(Context context) {
        return getDaoSession(context).getGeneralParameterDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }


    /**
     * add generalParameter as entity
     *
     * @param context
     * @param generalParameter
     */
    public static void add(Context context, GeneralParameter generalParameter) {
        getGeneralParameterDao(context).insertInTx(generalParameter);
        getDaoSession(context).clear();
    }

    /**
     * add generalParameter as list entity
     *
     * @param context
     * @param generalParameterList
     */
    public static void add(Context context, List<GeneralParameter> generalParameterList) {
        getGeneralParameterDao(context).insertInTx(generalParameterList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getGeneralParameterDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param generalParameter
     * @param context
     */
    public static void delete(Context context, GeneralParameter generalParameter) {
        getGeneralParameterDao(context).deleteInTx(generalParameter);
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param gsCode
     * @param uuidUser
     */
    public static void delete(Context context, String gsCode, String uuidUser) {

        getGeneralParameterDao(context).delete(getOne(context, uuidUser, gsCode));
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<GeneralParameter> qb = getGeneralParameterDao(context).queryBuilder();
        qb.where(GeneralParameterDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        getGeneralParameterDao(context).deleteInTx(qb.list());
    }

    /**
     * delete by gs code and uuid user, and then insert
     *
     * @param generalParameter,
     * @param context
     */
    public static void update(Context context, GeneralParameter generalParameter) {
        delete(context, generalParameter.getGs_code(), generalParameter.getUuid_user());
        getGeneralParameterDao(context).insertOrReplaceInTx(generalParameter);
    }

    /**
     * @param context
     * @param generalParameter
     */
    public static void addOrReplace(Context context, GeneralParameter generalParameter) {
        getGeneralParameterDao(context).insertOrReplaceInTx(generalParameter);
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param listGeneralParameter
     */
    public static void addOrReplace(Context context, List<GeneralParameter> listGeneralParameter) {
        getGeneralParameterDao(context).insertOrReplaceInTx(listGeneralParameter);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @param gsCode
     * @return
     */
    public static GeneralParameter getOne(Context context, String uuidUser, String gsCode) {
        QueryBuilder<GeneralParameter> qb = getGeneralParameterDao(context).queryBuilder();
        qb.where(GeneralParameterDao.Properties.Gs_code.eq(gsCode),
                GeneralParameterDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @param generalParameter
     * @return
     */
    public static GeneralParameter getOne(Context context, String uuidUser, GeneralParameter generalParameter) {
        QueryBuilder<GeneralParameter> qb = getGeneralParameterDao(context).queryBuilder();
        qb.where(GeneralParameterDao.Properties.Gs_code.eq(generalParameter.getGs_code()),
                GeneralParameterDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<GeneralParameter> getAll(Context context, String uuidUser) {
        QueryBuilder<GeneralParameter> qb = getGeneralParameterDao(context).queryBuilder();
        qb.where(GeneralParameterDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    public static boolean isRvInFrontEnable(Context context, String uuidUser) {
        try {
            GeneralParameter gp = getOne(context, uuidUser, Global.GS_ENABLE_RV_IN_FRONT);
            return gp != null && gp.getGs_value().equals(Global.TRUE_STRING);
        } catch (Exception e) {
            FireCrash.log(e);
            return false;
        }
    }

}
