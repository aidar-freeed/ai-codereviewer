package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.Contact;
import com.adins.mss.dao.ContactDao;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by olivia.dg on 11/17/2017.
 */

public class ContactDataAccess {
    protected static DaoSession getDaoSession(Context context){
        return DaoOpenHelper.getDaoSession(context);
    }

    protected static ContactDao getAccountDao(Context context) {
        return getDaoSession(context).getContactDao();
    }

    public static void add(Context context, Contact contact){
        getAccountDao(context).insert(contact);
        getDaoSession(context).clear();
    }

    public static void add(Context context, List<Contact> contactList){
        getAccountDao(context).insertInTx(contactList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, Contact contact){
        getAccountDao(context).insertOrReplaceInTx(contact);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<Contact> contactList){
        getAccountDao(context).insertOrReplaceInTx(contactList);
        getDaoSession(context).clear();
    }

    public static void clean(Context context){
        getAccountDao(context).deleteAll();
    }

    public static void delete(Context context, Contact contact){
        getAccountDao(context).delete(contact);
        getDaoSession(context).clear();
    }

    public static void update(Context context, Contact contact){
        getAccountDao(context).update(contact);
    }

    public static List<Contact> getAll(Context context){
        QueryBuilder<Contact> qb = getAccountDao(context).queryBuilder();
        qb.build();
        return qb.list();
    }

    public static Contact getOne(Context context, String uuid){
        QueryBuilder<Contact> qb = getAccountDao(context).queryBuilder();
        qb.where(ContactDao.Properties.Uuid_contact.eq(uuid));
        qb.build();

        if ((qb.list() == null) || qb.list().isEmpty()) return null;
        return qb.list().get(0);
    }

    public static List<Contact> getAllByAccount(Context context, String uuidAccount){
        QueryBuilder<Contact> qb = getAccountDao(context).queryBuilder();
        qb.where(ContactDao.Properties.Uuid_account.eq(uuidAccount));
        qb.build();
        return qb.list();
    }
}
