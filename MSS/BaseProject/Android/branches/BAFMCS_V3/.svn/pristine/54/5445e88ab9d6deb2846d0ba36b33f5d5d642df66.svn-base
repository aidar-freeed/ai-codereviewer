package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.Catalogue;
import com.adins.mss.dao.CatalogueDao;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by olivia.dg on 11/30/2017.
 */

public class CatalogueDataAccess {
    protected static DaoSession getDaoSession(Context context){
        return DaoOpenHelper.getDaoSession(context);
    }

    protected static CatalogueDao getCatalogueDao(Context context) {
        return getDaoSession(context).getCatalogueDao();
    }

    public static void add(Context context, Catalogue catalogue){
        getCatalogueDao(context).insert(catalogue);
        getDaoSession(context).clear();
    }

    public static void add(Context context, List<Catalogue> catalogueList){
        getCatalogueDao(context).insertInTx(catalogueList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, Catalogue catalogue){
        getCatalogueDao(context).insertOrReplaceInTx(catalogue);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<Catalogue> catalogueList){
        getCatalogueDao(context).insertOrReplaceInTx(catalogueList);
        getDaoSession(context).clear();
    }

    public static void clean(Context context){
        getCatalogueDao(context).deleteAll();
    }

    public static void delete(Context context, Catalogue catalogue){
        getCatalogueDao(context).delete(catalogue);
        getDaoSession(context).clear();
    }

    public static void update(Context context, Catalogue catalogue){
        getCatalogueDao(context).update(catalogue);
    }

    public static List<Catalogue> getAll(Context context){
        QueryBuilder<Catalogue> qb = getCatalogueDao(context).queryBuilder();
        qb.build();
        return qb.list();
    }

    public static Catalogue getOne(Context context, String uuid){
        QueryBuilder<Catalogue> qb = getCatalogueDao(context).queryBuilder();
        qb.where(CatalogueDao.Properties.Uuid_mkt_catalogue.eq(uuid));
        qb.build();
        if(qb.list().size()==0)
            return null;
        return qb.list().get(0);
    }
}
