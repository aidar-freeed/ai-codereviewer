package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.constant.Global;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.PaymentChannel;
import com.adins.mss.dao.PaymentChannelDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class PaymentChannelDataAccess {
	
//	private static DaoOpenHelper daoOpenHelper;
	
	/**
	 * use to generate dao session that you can access modelDao
	 * 
	 * @param context --> context from activity
	 * @return 
	 */
	protected static DaoSession getDaoSession(Context context){
		/*if(daoOpenHelper==null){
//			if(daoOpenHelper.getDaoSession()==null)
				daoOpenHelper = new DaoOpenHelper(context);
		}
		DaoSession daoSeesion = daoOpenHelper.getDaoSession();
		return daoSeesion;*/
		return DaoOpenHelper.getDaoSession(context);
	}
	
	/**
	 * get lookupDao dao and you can access the DB
	 * 
	 * @param context
	 * @return
	 */
	protected static PaymentChannelDao getPaymentChannelDao(Context context){
		return getDaoSession(context).getPaymentChannelDao();
	}
	
	/**
	 * Clear session, close db and set daoOpenHelper to null
	 *
	 */
	public static void closeAll(){
		/*if(daoOpenHelper!=null){
			daoOpenHelper.closeAll();
			daoOpenHelper = null;
		}*/
		DaoOpenHelper.closeAll();
	}
	
	/**
	 * add lookup as entity
	 * 
	 * @param context
	 * @param paymentChannel
	 * 
	 * 
	 */
	public static void add(Context context, PaymentChannel paymentChannel){
		getPaymentChannelDao(context).insertInTx(paymentChannel);
		getDaoSession(context).clear();
	}
	
	/**
	 * add lookup as list entity
	 * 
	 * @param context
	 * @param paymentChannelList
	 */
	public static void add(Context context, List<PaymentChannel> paymentChannelList){
		getPaymentChannelDao(context).insertInTx(paymentChannelList);
		getDaoSession(context).clear();
	}
	
	/**
	 * 
	 * delete all content in table.
	 * 
	 * @param context
	 */
	public static void clean(Context context){
		getPaymentChannelDao(context).deleteAll();
		getDaoSession(context).clear();
	}
	
	/**
	 * @param paymentChannel
	 * @param context
	 */
	public static void delete(Context context, PaymentChannel paymentChannel){
		getPaymentChannelDao(context).delete(paymentChannel);
		getDaoSession(context).clear();
	}
	
	/**
	 * delete all record by keyQuestionSet
	 * 
	 * @param context
	 */
	public static void delete(Context context, String codeChannel){
		QueryBuilder<PaymentChannel> qb = getPaymentChannelDao(context).queryBuilder();
		qb.where(PaymentChannelDao.Properties.Code.eq(codeChannel));
		qb.build();
		getPaymentChannelDao(context).deleteInTx(qb.list());
		getDaoSession(context).clear();
	}

	/**
	 * @param paymentChannel
	 * @param context
	 */
	public static void update(Context context, PaymentChannel paymentChannel){
		getPaymentChannelDao(context).updateInTx(paymentChannel);
		getDaoSession(context).clear();
	}

	public static void addOrUpdate(final Context context, PaymentChannel paymentChannel){
		getPaymentChannelDao(context).insertOrReplaceInTx(paymentChannel);
		getDaoSession(context).clear();
	}

	public static Date getLastDateChannel(Context context) {
		QueryBuilder<PaymentChannel> qb = getPaymentChannelDao(context).queryBuilder();
		qb.orderDesc(PaymentChannelDao.Properties.Dtm_crt);
		qb.build();

		if (qb.list() == null || qb.list().size() == 0) {
			return null;
		} else {
			return qb.list().get(0).getDtm_crt();
		}
	}

	public static List<PaymentChannel> getReachedMaxLimit(Context context, Double reachedLimit) {
		QueryBuilder<PaymentChannel> qb = getPaymentChannelDao(context).queryBuilder();
		qb.where(PaymentChannelDao.Properties.Is_active.eq(Global.TRUE_STRING),
				PaymentChannelDao.Properties.Payment_limit.lt(reachedLimit),
				PaymentChannelDao.Properties.Payment_limit.notEq(reachedLimit));
		qb.build();
		return qb.list();
	}

	public static List<PaymentChannel> getAllPaymentChannel(Context context , Double sumDeposit){
		QueryBuilder<PaymentChannel> qb = getPaymentChannelDao(context).queryBuilder();
		qb.where(PaymentChannelDao.Properties.Is_active.eq(Global.TRUE_STRING),
				qb.or(PaymentChannelDao.Properties.Payment_limit.gt(sumDeposit), PaymentChannelDao.Properties.Payment_limit.eq(sumDeposit)));
		qb.build();
		return qb.list();
	}

    public static PaymentChannel getOnePaymentChannel(Context context , String codeChannel){
        QueryBuilder<PaymentChannel> qb = getPaymentChannelDao(context).queryBuilder();
        qb.where(PaymentChannelDao.Properties.Code.eq(codeChannel));
        qb.build();
        if (qb.list() == null || qb.list().size() == 0) {
            return null;
        } else {
            return qb.list().get(0);
        }
    }

	public static List<PaymentChannel> getAllChannel(Context context){
		QueryBuilder<PaymentChannel> qb = getPaymentChannelDao(context).queryBuilder();
		qb.where(PaymentChannelDao.Properties.Is_active.eq(Global.TRUE_STRING));
		qb.build();
		return qb.list();
	}

}
