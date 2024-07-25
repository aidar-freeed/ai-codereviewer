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

import com.adins.mss.dao.ImageResult;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TR_IMAGERESULT".
*/
public class ImageResultDao extends AbstractDao<ImageResult, String> {

    public static final String TABLENAME = "TR_IMAGERESULT";

    /**
     * Properties of entity ImageResult.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Uuid_image_result = new Property(0, String.class, "uuid_image_result", true, "UUID_IMAGE_RESULT");
        public final static Property Question_id = new Property(1, String.class, "question_id", false, "QUESTION_ID");
        public final static Property Submit_duration = new Property(2, String.class, "submit_duration", false, "SUBMIT_DURATION");
        public final static Property Submit_size = new Property(3, String.class, "submit_size", false, "SUBMIT_SIZE");
        public final static Property Total_image = new Property(4, Integer.class, "total_image", false, "TOTAL_IMAGE");
        public final static Property Count_image = new Property(5, Integer.class, "count_image", false, "COUNT_IMAGE");
        public final static Property Submit_date = new Property(6, java.util.Date.class, "submit_date", false, "SUBMIT_DATE");
        public final static Property Submit_result = new Property(7, String.class, "submit_result", false, "SUBMIT_RESULT");
        public final static Property Question_group_id = new Property(8, String.class, "question_group_id", false, "QUESTION_GROUP_ID");
        public final static Property Usr_crt = new Property(9, String.class, "usr_crt", false, "USR_CRT");
        public final static Property Dtm_crt = new Property(10, java.util.Date.class, "dtm_crt", false, "DTM_CRT");
        public final static Property Uuid_task_h = new Property(11, String.class, "uuid_task_h", false, "UUID_TASK_H");
    };

    private DaoSession daoSession;

    private Query<ImageResult> taskH_ImageResultListQuery;

    public ImageResultDao(DaoConfig config) {
        super(config);
    }
    
    public ImageResultDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TR_IMAGERESULT\" (" + //
                "\"UUID_IMAGE_RESULT\" TEXT PRIMARY KEY NOT NULL ," + // 0: uuid_image_result
                "\"QUESTION_ID\" TEXT," + // 1: question_id
                "\"SUBMIT_DURATION\" TEXT," + // 2: submit_duration
                "\"SUBMIT_SIZE\" TEXT," + // 3: submit_size
                "\"TOTAL_IMAGE\" INTEGER," + // 4: total_image
                "\"COUNT_IMAGE\" INTEGER," + // 5: count_image
                "\"SUBMIT_DATE\" INTEGER," + // 6: submit_date
                "\"SUBMIT_RESULT\" TEXT," + // 7: submit_result
                "\"QUESTION_GROUP_ID\" TEXT," + // 8: question_group_id
                "\"USR_CRT\" TEXT," + // 9: usr_crt
                "\"DTM_CRT\" INTEGER," + // 10: dtm_crt
                "\"UUID_TASK_H\" TEXT);"); // 11: uuid_task_h
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TR_IMAGERESULT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(DatabaseStatement stmt, ImageResult entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getUuid_image_result());
 
        String question_id = entity.getQuestion_id();
        if (question_id != null) {
            stmt.bindString(2, question_id);
        }
 
        String submit_duration = entity.getSubmit_duration();
        if (submit_duration != null) {
            stmt.bindString(3, submit_duration);
        }
 
        String submit_size = entity.getSubmit_size();
        if (submit_size != null) {
            stmt.bindString(4, submit_size);
        }
 
        Integer total_image = entity.getTotal_image();
        if (total_image != null) {
            stmt.bindLong(5, total_image);
        }
 
        Integer count_image = entity.getCount_image();
        if (count_image != null) {
            stmt.bindLong(6, count_image);
        }
 
        java.util.Date submit_date = entity.getSubmit_date();
        if (submit_date != null) {
            stmt.bindLong(7, submit_date.getTime());
        }
 
        String submit_result = entity.getSubmit_result();
        if (submit_result != null) {
            stmt.bindString(8, submit_result);
        }
 
        String question_group_id = entity.getQuestion_group_id();
        if (question_group_id != null) {
            stmt.bindString(9, question_group_id);
        }
 
        String usr_crt = entity.getUsr_crt();
        if (usr_crt != null) {
            stmt.bindString(10, usr_crt);
        }
 
        java.util.Date dtm_crt = entity.getDtm_crt();
        if (dtm_crt != null) {
            stmt.bindLong(11, dtm_crt.getTime());
        }
 
        String uuid_task_h = entity.getUuid_task_h();
        if (uuid_task_h != null) {
            stmt.bindString(12, uuid_task_h);
        }
    }

    @Override
    protected void attachEntity(ImageResult entity) {
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
    public ImageResult readEntity(Cursor cursor, int offset) {
        ImageResult entity = new ImageResult( //
            cursor.getString(offset + 0), // uuid_image_result
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // question_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // submit_duration
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // submit_size
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // total_image
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // count_image
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // submit_date
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // submit_result
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // question_group_id
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // usr_crt
            cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)), // dtm_crt
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // uuid_task_h
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ImageResult entity, int offset) {
        entity.setUuid_image_result(cursor.getString(offset + 0));
        entity.setQuestion_id(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSubmit_duration(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSubmit_size(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTotal_image(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setCount_image(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setSubmit_date(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setSubmit_result(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setQuestion_group_id(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setUsr_crt(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setDtm_crt(cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)));
        entity.setUuid_task_h(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(ImageResult entity, long rowId) {
        return entity.getUuid_image_result();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(ImageResult entity) {
        if(entity != null) {
            return entity.getUuid_image_result();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "imageResultList" to-many relationship of TaskH. */
    public List<ImageResult> _queryTaskH_ImageResultList(String uuid_task_h) {
        synchronized (this) {
            if (taskH_ImageResultListQuery == null) {
                QueryBuilder<ImageResult> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Uuid_task_h.eq(null));
                taskH_ImageResultListQuery = queryBuilder.build();
            }
        }
        Query<ImageResult> query = taskH_ImageResultListQuery.forCurrentThread();
        query.setParameter(0, uuid_task_h);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getTaskHDao().getAllColumns());
            builder.append(" FROM TR_IMAGERESULT T");
            builder.append(" LEFT JOIN TR_TASK_H T0 ON T.\"UUID_TASK_H\"=T0.\"UUID_TASK_H\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected ImageResult loadCurrentDeep(Cursor cursor, boolean lock) {
        ImageResult entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        TaskH taskH = loadCurrentOther(daoSession.getTaskHDao(), cursor, offset);
        entity.setTaskH(taskH);

        return entity;    
    }

    public ImageResult loadDeep(Long key) {
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
    public List<ImageResult> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<ImageResult> list = new ArrayList<ImageResult>(count);
        
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
    
    protected List<ImageResult> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<ImageResult> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
