package com.adins.mss.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.DatabaseStatement;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.adins.mss.dao.DepositReportH;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TR_DEPOSITREPORT_H".
*/
public class DepositReportHDao extends AbstractDao<DepositReportH, String> {

    public static final String TABLENAME = "TR_DEPOSITREPORT_H";

    /**
     * Properties of entity DepositReportH.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Uuid_deposit_report_h = new Property(0, String.class, "uuid_deposit_report_h", true, "UUID_DEPOSIT_REPORT_H");
        public final static Property Last_update = new Property(1, java.util.Date.class, "last_update", false, "LAST_UPDATE");
        public final static Property Batch_id = new Property(2, String.class, "batch_id", false, "BATCH_ID");
        public final static Property Bank_account = new Property(3, String.class, "bank_account", false, "BANK_ACCOUNT");
        public final static Property Bank_name = new Property(4, String.class, "bank_name", false, "BANK_NAME");
        public final static Property Cashier_name = new Property(5, String.class, "cashier_name", false, "CASHIER_NAME");
        public final static Property Transfered_date = new Property(6, java.util.Date.class, "transfered_date", false, "TRANSFERED_DATE");
        public final static Property Usr_crt = new Property(7, String.class, "usr_crt", false, "USR_CRT");
        public final static Property Dtm_crt = new Property(8, java.util.Date.class, "dtm_crt", false, "DTM_CRT");
        public final static Property Image = new Property(9, byte[].class, "image", false, "IMAGE");
        public final static Property Uuid_user = new Property(10, String.class, "uuid_user", false, "UUID_USER");
        public final static Property Flag = new Property(11, String.class, "flag", false, "FLAG");
        public final static Property Branch_payment = new Property(12, String.class, "branch_payment", false, "BRANCH_PAYMENT");
        public final static Property Code_channel = new Property(13, String.class, "code_channel", false, "CODE_CHANNEL");
        public final static Property No_transaction = new Property(14, String.class, "no_transaction", false, "NO_TRANSACTION");
    };

    private DaoSession daoSession;

    private Query<DepositReportH> user_DepositReportHListQuery;

    public DepositReportHDao(DaoConfig config) {
        super(config);
    }
    
    public DepositReportHDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TR_DEPOSITREPORT_H\" (" + //
                "\"UUID_DEPOSIT_REPORT_H\" TEXT PRIMARY KEY NOT NULL ," + // 0: uuid_deposit_report_h
                "\"LAST_UPDATE\" INTEGER," + // 1: last_update
                "\"BATCH_ID\" TEXT," + // 2: batch_id
                "\"BANK_ACCOUNT\" TEXT," + // 3: bank_account
                "\"BANK_NAME\" TEXT," + // 4: bank_name
                "\"CASHIER_NAME\" TEXT," + // 5: cashier_name
                "\"TRANSFERED_DATE\" INTEGER," + // 6: transfered_date
                "\"USR_CRT\" TEXT," + // 7: usr_crt
                "\"DTM_CRT\" INTEGER," + // 8: dtm_crt
                "\"IMAGE\" BLOB," + // 9: image
                "\"UUID_USER\" TEXT," + // 10: uuid_user
                "\"FLAG\" TEXT," + // 11: flag
                "\"BRANCH_PAYMENT\" TEXT," + // 12: branch_payment
                "\"CODE_CHANNEL\" TEXT," + // 13: code_channel
                "\"NO_TRANSACTION\" TEXT);"); // 14: no_transaction
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TR_DEPOSITREPORT_H\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(DatabaseStatement stmt, DepositReportH entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getUuid_deposit_report_h());
 
        java.util.Date last_update = entity.getLast_update();
        if (last_update != null) {
            stmt.bindLong(2, last_update.getTime());
        }
 
        String batch_id = entity.getBatch_id();
        if (batch_id != null) {
            stmt.bindString(3, batch_id);
        }
 
        String bank_account = entity.getBank_account();
        if (bank_account != null) {
            stmt.bindString(4, bank_account);
        }
 
        String bank_name = entity.getBank_name();
        if (bank_name != null) {
            stmt.bindString(5, bank_name);
        }
 
        String cashier_name = entity.getCashier_name();
        if (cashier_name != null) {
            stmt.bindString(6, cashier_name);
        }
 
        java.util.Date transfered_date = entity.getTransfered_date();
        if (transfered_date != null) {
            stmt.bindLong(7, transfered_date.getTime());
        }
 
        String usr_crt = entity.getUsr_crt();
        if (usr_crt != null) {
            stmt.bindString(8, usr_crt);
        }
 
        java.util.Date dtm_crt = entity.getDtm_crt();
        if (dtm_crt != null) {
            stmt.bindLong(9, dtm_crt.getTime());
        }
 
        byte[] image = entity.getImage();
        if (image != null) {
            stmt.bindBlob(10, image);
        }
 
        String uuid_user = entity.getUuid_user();
        if (uuid_user != null) {
            stmt.bindString(11, uuid_user);
        }
 
        String flag = entity.getFlag();
        if (flag != null) {
            stmt.bindString(12, flag);
        }
 
        String branch_payment = entity.getBranch_payment();
        if (branch_payment != null) {
            stmt.bindString(13, branch_payment);
        }
 
        String code_channel = entity.getCode_channel();
        if (code_channel != null) {
            stmt.bindString(14, code_channel);
        }
 
        String no_transaction = entity.getNo_transaction();
        if (no_transaction != null) {
            stmt.bindString(15, no_transaction);
        }
    }

    @Override
    protected void attachEntity(DepositReportH entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public DepositReportH readEntity(Cursor cursor, int offset) {
        DepositReportH entity = new DepositReportH( //
            cursor.getString(offset + 0), // uuid_deposit_report_h
            cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)), // last_update
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // batch_id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // bank_account
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // bank_name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // cashier_name
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // transfered_date
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // usr_crt
            cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)), // dtm_crt
            cursor.isNull(offset + 9) ? null : cursor.getBlob(offset + 9), // image
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // uuid_user
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // flag
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // branch_payment
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // code_channel
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14) // no_transaction
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DepositReportH entity, int offset) {
        entity.setUuid_deposit_report_h(cursor.getString(offset + 0));
        entity.setLast_update(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
        entity.setBatch_id(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setBank_account(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setBank_name(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCashier_name(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setTransfered_date(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setUsr_crt(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDtm_crt(cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)));
        entity.setImage(cursor.isNull(offset + 9) ? null : cursor.getBlob(offset + 9));
        entity.setUuid_user(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setFlag(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setBranch_payment(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setCode_channel(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setNo_transaction(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(DepositReportH entity, long rowId) {
        return entity.getUuid_deposit_report_h();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(DepositReportH entity) {
        if(entity != null) {
            return entity.getUuid_deposit_report_h();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "depositReportHList" to-many relationship of User. */
    public List<DepositReportH> _queryUser_DepositReportHList(String uuid_user) {
        synchronized (this) {
            if (user_DepositReportHListQuery == null) {
                QueryBuilder<DepositReportH> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Uuid_user.eq(null));
                user_DepositReportHListQuery = queryBuilder.build();
            }
        }
        Query<DepositReportH> query = user_DepositReportHListQuery.forCurrentThread();
        query.setParameter(0, uuid_user);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserDao().getAllColumns());
            builder.append(" FROM TR_DEPOSITREPORT_H T");
            builder.append(" LEFT JOIN MS_USER T0 ON T.\"UUID_USER\"=T0.\"UUID_USER\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected DepositReportH loadCurrentDeep(Cursor cursor, boolean lock) {
        DepositReportH entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        User user = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setUser(user);

        return entity;    
    }

    public DepositReportH loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<DepositReportH> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<DepositReportH> list = new ArrayList<DepositReportH>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<DepositReportH> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<DepositReportH> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
