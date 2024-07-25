package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Theme;
import com.adins.mss.dao.ThemeDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by intishar.fa on 26/09/2018.
 */

public class ThemeDataAccess {



    protected static DaoSession getDaoSession(Context context) {
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get taskD dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static ThemeDao getThemeDao(Context context) {
        return getDaoSession(context).getThemeDao();
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
     * add taskD as entity
     *
     * @param context
     * @param theme
     */
    public static void add(Context context, Theme theme) {
        getThemeDao(context).insertInTx(theme);
        getDaoSession(context).clear();
    }

    /**
     * add taskD as list entity
     *
     * @param context
     * @param themeList
     */
    public static void add(Context context, List<Theme> themeList) {
        getThemeDao(context).insertInTx(themeList);
        getDaoSession(context).clear();
    }

    /**
     * addOrReplace taskD as entity
     *
     * @param context
     * @param theme
     */
    public static void addOrReplace(Context context, Theme theme) {
        getThemeDao(context).insertOrReplaceInTx(theme);
        getDaoSession(context).clear();
    }

    /**
     * addOrReplace taskD as list entity
     *
     * @param context
     * @param themeList
     */
    public static void addOrReplace(Context context, List<Theme> themeList) {
        getThemeDao(context).insertOrReplaceInTx(themeList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getThemeDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param theme
     */
    public static void delete(Context context, Theme theme) {
        getThemeDao(context).delete(theme);
        getDaoSession(context).clear();
    }

    public static List<Theme> getAll(Context context){
        QueryBuilder<Theme> qb = getThemeDao(context).queryBuilder();
        return qb.list();
    }

    public static List<Theme> getThemeWithLimit(Context context, int limit){
        QueryBuilder<Theme> qb = getThemeDao(context).queryBuilder();
        qb.limit(1);
        return qb.list();
    }

    public static List<Theme> getThemeByApplicationType(Context context, String applicationType){
        QueryBuilder<Theme> qb = getThemeDao(context).queryBuilder();
        qb.where(ThemeDao.Properties.Application_type.eq(applicationType));
        return qb.list();
    }


}
