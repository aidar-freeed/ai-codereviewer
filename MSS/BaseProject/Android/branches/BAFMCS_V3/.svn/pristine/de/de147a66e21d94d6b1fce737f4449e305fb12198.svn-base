package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.BankAccountOfBranch;
import com.adins.mss.dao.BankAccountOfBranchDao;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class BankAccountOfBranchDataAccess {
	
//	private static DaoOpenHelper daoOpenHelper;
	
	/**
	 * use to generate dao session that you can access modelDao
	 * 
	 * @param context --> context from activity
	 * @return 
	 */
	protected static DaoSession getDaoSession(Context context){
		return DaoOpenHelper.getDaoSession(context);
	}
	
	/**
	 * get lookupDao dao and you can access the DB
	 * 
	 * @param context
	 * @return
	 */
	protected static BankAccountOfBranchDao getBankAccountOfBranchDao(Context context){
		return getDaoSession(context).getBankAccountOfBranchDao();
	}
	
	/**
	 * Clear session, close db and set daoOpenHelper to null
	 *
	 */
	public static void closeAll(){
		DaoOpenHelper.closeAll();
	}
	
	/**
	 * add lookup as list entity
	 * 
	 * @param context
	 * @param bankAccountOfBranchList
	 */
	public static void add(Context context, List<BankAccountOfBranch> bankAccountOfBranchList){
		getBankAccountOfBranchDao(context).insertInTx(bankAccountOfBranchList);
		getDaoSession(context).clear();
	}
	
	/**
	 * 
	 * delete all content in table.
	 * 
	 * @param context
	 */
	public static void clean(Context context){
		getBankAccountOfBranchDao(context).deleteAll();
		getDaoSession(context).clear();
	}

	public static List<BankAccountOfBranch> getAll(Context context){
		QueryBuilder<BankAccountOfBranch> qb = getBankAccountOfBranchDao(context).queryBuilder();
		qb.build();
		return qb.list();
	}

	public static List<BankAccountOfBranch> getOneByBranchCode(Context context, String branch_code){
		QueryBuilder<BankAccountOfBranch> qb = getBankAccountOfBranchDao(context).queryBuilder();
		qb.where(BankAccountOfBranchDao.Properties.Branch_code.eq(branch_code));
		qb.build();
		return qb.list();
	}

}
