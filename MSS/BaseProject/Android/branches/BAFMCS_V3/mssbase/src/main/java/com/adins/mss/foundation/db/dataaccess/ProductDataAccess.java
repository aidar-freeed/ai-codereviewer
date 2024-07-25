package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.constant.Global;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Product;
import com.adins.mss.dao.ProductDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by olivia.dg on 11/16/2017.
 */

public class ProductDataAccess {

    protected static DaoSession getDaoSession(Context context) {
        return DaoOpenHelper.getDaoSession(context);
    }

    protected static ProductDao getProductDao(Context context) {
        return getDaoSession(context).getProductDao();
    }

    public static void add(Context context, Product product) {
        getProductDao(context).insert(product);
        getDaoSession(context).clear();
    }

    public static void add(Context context, List<Product> productList) {
        getProductDao(context).insertInTx(productList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, Product product) {
        getProductDao(context).insertOrReplaceInTx(product);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<Product> productList) {
        getProductDao(context).insertOrReplaceInTx(productList);
        getDaoSession(context).clear();
    }

    public static void clean(Context context) {
        getProductDao(context).deleteAll();
    }

    public static void delete(Context context, Product product) {
        getProductDao(context).delete(product);
        getDaoSession(context).clear();
    }

    public static void update(Context context, Product product) {
        getProductDao(context).update(product);
    }

    public static List<Product> getAll(Context context) {
        QueryBuilder<Product> qb = getProductDao(context).queryBuilder();
        qb.build();
        return qb.list();
    }

    public static Product getOne(Context context, String uuid) {
        QueryBuilder<Product> qb = getProductDao(context).queryBuilder();
        qb.where(ProductDao.Properties.Uuid_product.eq(uuid));
        qb.build();

        if ((qb.list() == null) || qb.list().isEmpty()) return null;
        return qb.list().get(0);
    }

    public static Product getOneByCode(Context context, String productCode) {
        QueryBuilder<Product> qb = getProductDao(context).queryBuilder();
        qb.where(ProductDao.Properties.Product_code.eq(productCode));
        qb.build();

        if ((qb.list() == null) || qb.list().isEmpty()) return null;
        return qb.list().get(0);
    }

    public static List<Product> getAllByIsActive(Context context) {
        List<Product> list = null;
        QueryBuilder<Product> qb = getProductDao(context).queryBuilder();
        qb.where(ProductDao.Properties.Is_active.eq(Global.TRUE_STRING));
        qb.build();

        if (qb != null) {
            list = qb.list();
        }
        return list;
    }

}
