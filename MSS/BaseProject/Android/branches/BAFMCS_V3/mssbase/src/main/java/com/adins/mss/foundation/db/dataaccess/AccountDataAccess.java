package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.Account;
import com.adins.mss.dao.AccountDao;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by olivia.dg on 11/16/2017.
 */

public class AccountDataAccess {

    protected static DaoSession getDaoSession(Context context){
        return DaoOpenHelper.getDaoSession(context);
    }

    protected static AccountDao getAccountDao(Context context) {
        return getDaoSession(context).getAccountDao();
    }

    public static void add(Context context, Account account){
        getAccountDao(context).insert(account);
        getDaoSession(context).clear();
    }

    public static void add(Context context, List<Account> accountList){
        getAccountDao(context).insertInTx(accountList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, Account account){
        getAccountDao(context).insertOrReplaceInTx(account);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<Account> accountList){
        getAccountDao(context).insertOrReplaceInTx(accountList);
        getDaoSession(context).clear();
    }

    public static void clean(Context context){
        getAccountDao(context).deleteAll();
    }

    public static void delete(Context context, Account account){
        getAccountDao(context).delete(account);
        getDaoSession(context).clear();
    }

    public static void update(Context context, Account account){
        getAccountDao(context).update(account);
    }

    public static List<Account> getAll(Context context){
        QueryBuilder<Account> qb = getAccountDao(context).queryBuilder();
        qb.build();
        return qb.list();
    }

    public static Account getOne(Context context, String uuid){
        QueryBuilder<Account> qb = getAccountDao(context).queryBuilder();
        qb.where(AccountDao.Properties.Uuid_account.eq(uuid));
        qb.build();

        if ((qb.list() == null) || qb.list().isEmpty()) return null;
        return qb.list().get(0);
    }
}
