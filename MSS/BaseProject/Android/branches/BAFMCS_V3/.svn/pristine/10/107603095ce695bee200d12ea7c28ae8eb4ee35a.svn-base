package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.constant.Global;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Menu;
import com.adins.mss.dao.MenuDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class MenuDataAccess {

    private MenuDataAccess() {
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
     * get menuDao dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static MenuDao getMenuDao(Context context) {
        return getDaoSession(context).getMenuDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add menu as entity
     *
     * @param context
     * @param menu
     */
    public static void add(Context context, Menu menu) {
        getMenuDao(context).insert(menu);
    }

    /**
     * add menu as list entity
     *
     * @param context
     * @param menuList
     */
    public static void add(Context context, List<Menu> menuList) {
        getMenuDao(context).insertInTx(menuList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, Menu menu) {
        getMenuDao(context).insertOrReplaceInTx(menu);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<Menu> menuList) {
        getMenuDao(context).insertOrReplaceInTx(menuList);
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
     * @param menu
     * @param context
     */
    public static void delete(Context context, Menu menu) {
        getMenuDao(context).deleteInTx(menu);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<Menu> qb = getMenuDao(context).queryBuilder();
        qb.where(MenuDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        getMenuDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param menu
     * @param context
     */
    public static void update(Context context, Menu menu) {
        getMenuDao(context).updateInTx(menu);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<Menu> getAll(Context context, String uuidUser) {
        QueryBuilder<Menu> qb = getMenuDao(context).queryBuilder();
        qb.where(MenuDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    /**
     * get one menu by menu uuid
     *
     * @param context
     * @param uuidMenu
     * @return
     */
    public static Menu getOne(Context context, String uuidMenu) {
        QueryBuilder<Menu> qb = getMenuDao(context).queryBuilder();
        qb.where(MenuDao.Properties.Uuid_menu.eq(uuidMenu));
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

    public static boolean isHaveRescheduleMenu(Context context) {
        QueryBuilder<Menu> qb = getMenuDao(context).queryBuilder();
        qb.where(MenuDao.Properties.Uuid_menu.eq(Global.MENU_RESCHEDULE_SURVEY));
        qb.build();
        if (qb.list() != null) {
            return qb.list().size() > 0;
        } else {
            return false;
        }
    }

    //new
    public static boolean isHaveReVisitMenu(Context context) {
        QueryBuilder<Menu> qb = getMenuDao(context).queryBuilder();
        qb.where(MenuDao.Properties.Uuid_menu.eq(Global.MENU_REVISIT_COLLECTION));
        qb.build();
        if (qb.list() != null) {
            return qb.list().size() > 0;
        } else {
            return false;
        }
    }

    public static boolean isHaveVerificationBranchMenu(Context context) {
        QueryBuilder<Menu> qb = getMenuDao(context).queryBuilder();
        qb.where(MenuDao.Properties.Uuid_menu.eq(Global.MENU_VERIFICATION_BRANCH));
        qb.build();
        if (qb.list() != null) {
            return qb.list().size() > 0;
        } else {
            return false;
        }
    }

    public static boolean isHaveApprovalBranchMenu(Context context) {
        QueryBuilder<Menu> qb = getMenuDao(context).queryBuilder();
        qb.where(MenuDao.Properties.Uuid_menu.eq(Global.MENU_APPROVAL_BRANCH));
        qb.build();
        if (qb.list() != null) {
            return qb.list().size() > 0;
        } else {
            return false;
        }
    }

}
