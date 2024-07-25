package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.ReceiptVoucher;
import com.adins.mss.dao.ReceiptVoucherDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import de.greenrobot.dao.query.QueryBuilder;

public class ReceiptVoucherDataAccess {
    public static final String STATUS_NEW = "N";
    public static final String STATUS_USED = "U";
//	private static DaoOpenHelper daoOpenHelper;

    /**
     * use to generate dao session that you can access modelDao
     *
     * @param context --> context from activity
     * @return
     */
    protected static DaoSession getDaoSession(Context context) {
        /*if(daoOpenHelper==null){
//			if(daoOpenHelper.getDaoSession()==null)
				daoOpenHelper = new DaoOpenHelper(context);
		}
		DaoSession daoSeesion = daoOpenHelper.getDaoSession();
		return daoSeesion;*/
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get receiptVoucher dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static ReceiptVoucherDao getReceiptVoucherDao(Context context) {
        return getDaoSession(context).getReceiptVoucherDao();
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
     * add receiptVoucher as entity
     *
     * @param context
     * @param receiptVoucher
     */
    public static void add(Context context, ReceiptVoucher receiptVoucher) {
        getReceiptVoucherDao(context).insertInTx(receiptVoucher);
        getDaoSession(context).clear();
    }

    /**
     * add receiptVoucher as list entity
     *
     * @param context
     * @param receiptVoucherList
     */
    public static void add(Context context, List<ReceiptVoucher> receiptVoucherList) {
        getReceiptVoucherDao(context).insertInTx(receiptVoucherList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getReceiptVoucherDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param receiptVoucher
     */
    public static void delete(Context context, ReceiptVoucher receiptVoucher) {
        getReceiptVoucherDao(context).deleteInTx(receiptVoucher);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<ReceiptVoucher> qb = getReceiptVoucherDao(context).queryBuilder();
        qb.where(ReceiptVoucherDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        getReceiptVoucherDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param receiptVoucher
     */
    public static void update(Context context, ReceiptVoucher receiptVoucher) {
        getReceiptVoucherDao(context).updateInTx(receiptVoucher);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<ReceiptVoucher> getAll(Context context, String uuidUser) {
        QueryBuilder<ReceiptVoucher> qb = getReceiptVoucherDao(context).queryBuilder();
        qb.where(ReceiptVoucherDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    /**
     * select * from table where uuid_task_h = param
     *
     * @param context
     * @param uuidUser
     * @param keyTaskH
     * @return
     */
    public static List<ReceiptVoucher> getAll(Context context, String uuidUser, String keyTaskH) {
        QueryBuilder<ReceiptVoucher> qb = getReceiptVoucherDao(context).queryBuilder();
        qb.where(ReceiptVoucherDao.Properties.Uuid_user.eq(uuidUser),
                ReceiptVoucherDao.Properties.Uuid_task_h.eq(keyTaskH));
        qb.build();
        return qb.list();
    }

    public static List<ReceiptVoucher> getByStatus(Context context, String uuidUser, String status) {
        QueryBuilder<ReceiptVoucher> qb = getReceiptVoucherDao(context).queryBuilder();
        qb.where(ReceiptVoucherDao.Properties.Uuid_user.eq(uuidUser));
        qb.where(ReceiptVoucherDao.Properties.Rv_status.eq(status));
        qb.build();
        return qb.list();
    }

    public static ReceiptVoucher getOne(Context context, String uuidUser, String uuid_rv_number) {
        QueryBuilder<ReceiptVoucher> qb = getReceiptVoucherDao(context).queryBuilder();
        qb.where(ReceiptVoucherDao.Properties.Uuid_user.eq(uuidUser));
        qb.where(ReceiptVoucherDao.Properties.Uuid_receipt_voucher.eq(uuid_rv_number));
        qb.build();
        if (qb.list().size() == 0)
            return null;
        return qb.list().get(0);
    }

    public static void updateToUsed(Context context, String uuidUser, ReceiptVoucher receiptVoucher) {
        receiptVoucher.setRv_status(STATUS_USED);
        receiptVoucher.setDtm_use(new Date());

        if (getOne(context, uuidUser, receiptVoucher.getUuid_receipt_voucher()) != null) {
            update(context, receiptVoucher);
        } else {
            receiptVoucher.setUuid_user(uuidUser);
            add(context, receiptVoucher);
        }
        getDaoSession(context).clear();
    }

    public static void updateToNew(Context context, String uuidUser, String uuidReceiptVoucher) {
        ReceiptVoucher receiptVoucher = getOne(context, uuidUser, uuidReceiptVoucher);
        if (receiptVoucher != null) {
            receiptVoucher.setRv_status(STATUS_NEW);
            receiptVoucher.setDtm_use(new Date());

            if (getOne(context, uuidUser, receiptVoucher.getUuid_receipt_voucher()) != null) {
                update(context, receiptVoucher);
            } else {
                receiptVoucher.setUuid_user(uuidUser);
                add(context, receiptVoucher);
            }
            getDaoSession(context).clear();
        }
    }

    public static void updateToUsed(Context context, String uuidUser, String uuidReceiptVoucher) {
        ReceiptVoucher receiptVoucher = getOne(context, uuidUser, uuidReceiptVoucher);
        if (receiptVoucher != null) {
            receiptVoucher.setRv_status(STATUS_USED);
            receiptVoucher.setDtm_use(new Date());

            if (getOne(context, uuidUser, receiptVoucher.getUuid_receipt_voucher()) != null) {
                update(context, receiptVoucher);
            } else {
                receiptVoucher.setUuid_user(uuidUser);
                add(context, receiptVoucher);
            }
            getDaoSession(context).clear();
        }
    }

    public static Date getLastDate(Context context, String uuidUser) {
        QueryBuilder<ReceiptVoucher> qb = getReceiptVoucherDao(context).queryBuilder();
        qb.where(ReceiptVoucherDao.Properties.Uuid_user.eq(uuidUser));
        qb.orderDesc(ReceiptVoucherDao.Properties.Dtm_crt);
        qb.build();

        if (qb.list() == null || qb.list().size() == 0) {
            return null;
        } else {
            return qb.list().get(0).getDtm_crt();
        }
    }

    public static void deleteReceiptVoucherByStatus(Context context, String uuidUser, String status) {
        List<ReceiptVoucher> ReceiptVouchers = getByStatus(context, uuidUser, status);
        if (ReceiptVouchers != null && ReceiptVouchers.size() > 0) {
            for (ReceiptVoucher rv : ReceiptVouchers) {
                delete(context, rv);
            }
        }
        getDaoSession(context).clear();
    }

    public static void addNewReceiptVoucher(Context context, String uuidUser, List<ReceiptVoucher> receiptVouchers) {
        List<ReceiptVoucher> usedReceiverVoucher = getByStatus(context, uuidUser, STATUS_USED);
        List<String> usedUUID = new ArrayList<>();
        for (ReceiptVoucher receiptVoucher : usedReceiverVoucher) {
            usedUUID.add(receiptVoucher.getUuid_receipt_voucher());
        }
        ListIterator<ReceiptVoucher> rvIterator = receiptVouchers.listIterator();
        while (rvIterator.hasNext()){
            ReceiptVoucher rv = rvIterator.next();
            rv.setRv_status(ReceiptVoucherDataAccess.STATUS_NEW);
            rv.setUuid_user(uuidUser);
            if (usedUUID.contains(rv.getUuid_receipt_voucher())){
                rvIterator.remove();
            }
        }
        ReceiptVoucherDataAccess.deleteReceiptVoucherByStatus(context, uuidUser, ReceiptVoucherDataAccess.STATUS_NEW);
        ReceiptVoucherDataAccess.add(context, receiptVouchers);
        getDaoSession(context).clear();
    }

    private static void addOrReplace(Context context, List<ReceiptVoucher> receiptVoucherList) {
        getReceiptVoucherDao(context).insertOrReplaceInTx(receiptVoucherList);
        getDaoSession(context).clear();
    }

}
