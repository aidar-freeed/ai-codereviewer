package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.ThemeItem;
import com.adins.mss.dao.ThemeItemDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by intishar.fa on 01/10/2018.
 */

public class ThemeItemDataAccess {
    protected static DaoSession getDaoSession(Context context) {
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get taskD dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static ThemeItemDao getThemeItemDao(Context context) {
        return getDaoSession(context).getThemeItemDao();
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
     * @param themeitem
     */
    public static void add(Context context, ThemeItem themeitem) {
        getThemeItemDao(context).insertInTx(themeitem);
        getDaoSession(context).clear();
    }

    /**
     * add taskD as list entity
     *
     * @param context
     * @param themeItemList
     */
    public static void add(Context context, List<ThemeItem> themeItemList) {
        getThemeItemDao(context).insertInTx(themeItemList);
        getDaoSession(context).clear();
    }

    /**
     * addOrReplace taskD as entity
     *
     * @param context
     * @param themeitem
     */
    public static void addOrReplace(Context context, ThemeItem themeitem) {
        getThemeItemDao(context).insertOrReplaceInTx(themeitem);
        getDaoSession(context).clear();
    }

    /**
     * addOrReplace taskD as list entity
     *
     * @param context
     * @param themeItemList
     */
    public static void addOrReplace(Context context, List<ThemeItem> themeItemList) {
        getThemeItemDao(context).insertOrReplaceInTx(themeItemList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getThemeItemDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param themeitem
     */
    public static void delete(Context context, ThemeItem themeitem) {
        getThemeItemDao(context).delete(themeitem);
        getDaoSession(context).clear();
    }

    public static void deleteAllItemByUuidTheme(Context context,String uuidTheme){
        DeleteQuery<ThemeItem> deleteQuery = getThemeItemDao(context).queryBuilder()
                .where(ThemeItemDao.Properties.Uuid_theme.eq(uuidTheme))
                .buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        getDaoSession(context).clear();
    }

    public static List<ThemeItem> getAll(Context context){
        QueryBuilder<ThemeItem> qb = getThemeItemDao(context).queryBuilder();
        return qb.list();
    }

    public static List<ThemeItem> getAllByUuidTheme(Context context, String uuidTheme){
        QueryBuilder<ThemeItem> qb = getThemeItemDao(context).queryBuilder();
        qb.where(ThemeItemDao.Properties.Uuid_theme.eq(uuidTheme));
        return qb.list();
    }

}
