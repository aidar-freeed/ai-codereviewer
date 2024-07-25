package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.adins.mss.constant.Global;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.SchemeDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class SchemeDataAccess {

    private SchemeDataAccess() {
        //EMPTY
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
     * get scheme dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static SchemeDao getSchemeDao(Context context) {
        return getDaoSession(context).getSchemeDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add scheme as entity
     *
     * @param context
     * @param scheme
     */
    public static void add(Context context, Scheme scheme) {
        getSchemeDao(context).insertInTx(scheme);
        getDaoSession(context).clear();
    }

    /**
     * add scheme as list entity
     *
     * @param context
     * @param schemeList
     */
    public static void add(Context context, List<Scheme> schemeList) {
        getSchemeDao(context).insertInTx(schemeList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getSchemeDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param scheme
     */
    public static void delete(Context context, Scheme scheme) {
        getSchemeDao(context).delete(scheme);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by keyScheme
     *
     * @param context
     */
    public static void delete(Context context, String keyScheme) {
        QueryBuilder<Scheme> qb = getSchemeDao(context).queryBuilder();
        qb.where(SchemeDao.Properties.Uuid_scheme.eq(keyScheme));
        qb.build();
        getSchemeDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param scheme
     */
    public static void update(Context context, Scheme scheme) {
        getSchemeDao(context).update(scheme);
        getDaoSession(context).clear();
    }

    /**
     * add scheme as entity
     *
     * @param context
     * @param scheme
     */
    public static void addOrReplace(Context context, Scheme scheme) {
        getSchemeDao(context).insertOrReplaceInTx(scheme);
        getDaoSession(context).clear();
    }

    /**
     * add scheme as list entity
     *
     * @param context
     * @param listScheme
     */
    public static void addOrReplace(Context context, List<Scheme> listScheme) {
        getSchemeDao(context).insertOrReplaceInTx(listScheme);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_scheme = param
     *
     * @param context
     * @param keyScheme
     * @return
     */
    public static Scheme getOne(Context context, String keyScheme) {
        QueryBuilder<Scheme> qb = getSchemeDao(context).queryBuilder();
        qb.where(SchemeDao.Properties.Uuid_scheme.eq(keyScheme));
        qb.build().forCurrentThread();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    /**
     * select * from table where uuid_scheme = param
     *
     * @param context
     * @return
     */
    public static List<Scheme> getAll(Context context) {
        QueryBuilder<Scheme> qb = getSchemeDao(context).queryBuilder();
        qb.build();
        return qb.list();
    }

    public static List<Scheme> getAllActiveScheme(Context context) {
        QueryBuilder<Scheme> qb = getSchemeDao(context).queryBuilder();
        qb.where(SchemeDao.Properties.Is_active.eq(Global.TRUE_STRING));
        qb.build();
        return qb.list();
    }

    public static List<Scheme> getAllActivePriorityScheme(Context context) {
        QueryBuilder<Scheme> qb = getSchemeDao(context).queryBuilder();
        qb.where(SchemeDao.Properties.Is_active.eq(Global.TRUE_STRING));
        qb.where(SchemeDao.Properties.Form_type.notEq(Global.FORM_TYPE_SIMULASI));
        qb.build();
        return qb.list();
    }

    public static String getOneSchemeName(Context context, String uuid){
        List<String> result = new ArrayList<>();
        String SQL_DISTINCT_ENAME = "SELECT "+ SchemeDao.Properties.Scheme_description.columnName+
                " FROM "+ SchemeDao.TABLENAME+
                " WHERE "+ SchemeDao.Properties.Is_active.columnName+"='"+Global.TRUE_STRING+"' "+
                " AND " + SchemeDao.Properties.Uuid_scheme.columnName+"='"+uuid+"' "+
                " ORDER BY "+
                SchemeDao.Properties.Scheme_description.columnName+
                " ASC";
        Cursor c = getSchemeDao(context).getDatabase().rawQuery(SQL_DISTINCT_ENAME, null);
        if (c.moveToFirst()) {
            do {
                result.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        String resultName = "";
        if(result != null && result.size()>0){
            resultName = result.get(0);
        }
        return resultName;
    }

    public static List<String> getAllSchemeName(Context context) {
        List<String> result = new ArrayList<>();
        String SQL_DISTINCT_ENAME = "SELECT " + SchemeDao.Properties.Scheme_description.columnName +
                " FROM " + SchemeDao.TABLENAME +
                " WHERE " + SchemeDao.Properties.Is_active.columnName + "='" + Global.TRUE_STRING + "' " +
                " ORDER BY " +
                SchemeDao.Properties.Scheme_description.columnName +
                " ASC";
        Cursor c = getSchemeDao(context).getDatabase().rawQuery(SQL_DISTINCT_ENAME, null);
        if (c.moveToFirst()) {
            do {
                result.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }

    /**
     * get simulasi scheme
     *
     * @param context
     * @return
     */
    public static List<Scheme> getAllSimulateScheme(Context context) {
        QueryBuilder<Scheme> qb = getSchemeDao(context).queryBuilder();
        qb.where(SchemeDao.Properties.Form_type.eq(Global.FORM_TYPE_SIMULASI));
        qb.build();
        return qb.list();
    }

    /**
     * get Order Scheme
     *
     * @param context
     * @return
     */
    public static List<Scheme> getAllOrderScheme(Context context) {
        QueryBuilder<Scheme> qb = getSchemeDao(context).queryBuilder();
        qb.and(qb.or(SchemeDao.Properties.Form_type.eq(Global.FORM_TYPE_ORDER),
                SchemeDao.Properties.Form_type.eq(Global.FORM_TYPE_KTP)),
                SchemeDao.Properties.Is_active.eq(Global.TRUE_STRING));
        qb.build();
        return qb.list();
    }

    /**
     * get Collection Scheme
     *
     * @param context
     * @return
     */
    public static List<Scheme> getAllCollectionScheme(Context context) {
        QueryBuilder<Scheme> qb = getSchemeDao(context).queryBuilder();
        qb.where(SchemeDao.Properties.Form_type.eq(Global.FORM_TYPE_COLL),
                SchemeDao.Properties.Is_active.eq(Global.TRUE_STRING));
        qb.build();
        return qb.list();
    }

    /**
     * get Survey scheme
     *
     * @param context
     * @return
     */
    public static List<Scheme> getAllSurveyScheme(Context context) {
        QueryBuilder<Scheme> qb = getSchemeDao(context).queryBuilder();
        qb.where(qb.or(SchemeDao.Properties.Form_type.eq(Global.FORM_TYPE_SURVEY),
                SchemeDao.Properties.Form_type.eq(Global.FORM_TYPE_KTP)),
                SchemeDao.Properties.Is_active.eq(Global.TRUE_STRING));
        qb.build();
        return qb.list();
    }

    /**
     * select scheme by last update
     *
     * @param context
     * @return
     */
    public static Scheme getOneByLastUpdate(Context context, String keyScheme, java.util.Date scheme_last_update) {
        QueryBuilder<Scheme> qb = getSchemeDao(context).queryBuilder();
        qb.where(SchemeDao.Properties.Uuid_scheme.eq(keyScheme),
                SchemeDao.Properties.Scheme_last_update.eq(scheme_last_update));
        qb.build().forCurrentThread();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static List<Scheme> getAllMarketingScheme(Context context) {
        QueryBuilder<Scheme> qb = getSchemeDao(context).queryBuilder();
        qb.where(SchemeDao.Properties.Form_type.eq(Global.FORM_TYPE_MARKETING),
                SchemeDao.Properties.Is_active.eq(Global.TRUE_STRING));
        qb.build();
        return qb.list();
    }
}
