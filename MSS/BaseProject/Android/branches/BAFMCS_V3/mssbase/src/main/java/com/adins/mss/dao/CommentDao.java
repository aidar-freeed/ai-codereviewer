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

import com.adins.mss.dao.Comment;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TR_COMMENT".
*/
public class CommentDao extends AbstractDao<Comment, String> {

    public static final String TABLENAME = "TR_COMMENT";

    /**
     * Properties of entity Comment.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Uuid_comment = new Property(0, String.class, "uuid_comment", true, "UUID_COMMENT");
        public final static Property Comment = new Property(1, String.class, "comment", false, "COMMENT");
        public final static Property Dtm_crt_server = new Property(2, java.util.Date.class, "dtm_crt_server", false, "DTM_CRT_SERVER");
        public final static Property Sender_id = new Property(3, String.class, "sender_id", false, "SENDER_ID");
        public final static Property Sender_name = new Property(4, String.class, "sender_name", false, "SENDER_NAME");
        public final static Property Usr_crt = new Property(5, String.class, "usr_crt", false, "USR_CRT");
        public final static Property Dtm_crt = new Property(6, java.util.Date.class, "dtm_crt", false, "DTM_CRT");
        public final static Property Usr_upd = new Property(7, String.class, "usr_upd", false, "USR_UPD");
        public final static Property Dtm_upd = new Property(8, java.util.Date.class, "dtm_upd", false, "DTM_UPD");
        public final static Property Uuid_timeline = new Property(9, String.class, "uuid_timeline", false, "UUID_TIMELINE");
    };

    private DaoSession daoSession;

    private Query<Comment> timeline_CommentListQuery;

    public CommentDao(DaoConfig config) {
        super(config);
    }
    
    public CommentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TR_COMMENT\" (" + //
                "\"UUID_COMMENT\" TEXT PRIMARY KEY NOT NULL ," + // 0: uuid_comment
                "\"COMMENT\" TEXT," + // 1: comment
                "\"DTM_CRT_SERVER\" INTEGER," + // 2: dtm_crt_server
                "\"SENDER_ID\" TEXT," + // 3: sender_id
                "\"SENDER_NAME\" TEXT," + // 4: sender_name
                "\"USR_CRT\" TEXT," + // 5: usr_crt
                "\"DTM_CRT\" INTEGER," + // 6: dtm_crt
                "\"USR_UPD\" TEXT," + // 7: usr_upd
                "\"DTM_UPD\" INTEGER," + // 8: dtm_upd
                "\"UUID_TIMELINE\" TEXT);"); // 9: uuid_timeline
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TR_COMMENT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(DatabaseStatement stmt, Comment entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getUuid_comment());
 
        String comment = entity.getComment();
        if (comment != null) {
            stmt.bindString(2, comment);
        }
 
        java.util.Date dtm_crt_server = entity.getDtm_crt_server();
        if (dtm_crt_server != null) {
            stmt.bindLong(3, dtm_crt_server.getTime());
        }
 
        String sender_id = entity.getSender_id();
        if (sender_id != null) {
            stmt.bindString(4, sender_id);
        }
 
        String sender_name = entity.getSender_name();
        if (sender_name != null) {
            stmt.bindString(5, sender_name);
        }
 
        String usr_crt = entity.getUsr_crt();
        if (usr_crt != null) {
            stmt.bindString(6, usr_crt);
        }
 
        java.util.Date dtm_crt = entity.getDtm_crt();
        if (dtm_crt != null) {
            stmt.bindLong(7, dtm_crt.getTime());
        }
 
        String usr_upd = entity.getUsr_upd();
        if (usr_upd != null) {
            stmt.bindString(8, usr_upd);
        }
 
        java.util.Date dtm_upd = entity.getDtm_upd();
        if (dtm_upd != null) {
            stmt.bindLong(9, dtm_upd.getTime());
        }
 
        String uuid_timeline = entity.getUuid_timeline();
        if (uuid_timeline != null) {
            stmt.bindString(10, uuid_timeline);
        }
    }

    @Override
    protected void attachEntity(Comment entity) {
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
    public Comment readEntity(Cursor cursor, int offset) {
        Comment entity = new Comment( //
            cursor.getString(offset + 0), // uuid_comment
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // comment
            cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)), // dtm_crt_server
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // sender_id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // sender_name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // usr_crt
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // dtm_crt
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // usr_upd
            cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)), // dtm_upd
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // uuid_timeline
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Comment entity, int offset) {
        entity.setUuid_comment(cursor.getString(offset + 0));
        entity.setComment(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDtm_crt_server(cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)));
        entity.setSender_id(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSender_name(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUsr_crt(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDtm_crt(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setUsr_upd(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDtm_upd(cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)));
        entity.setUuid_timeline(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Comment entity, long rowId) {
        return entity.getUuid_comment();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Comment entity) {
        if(entity != null) {
            return entity.getUuid_comment();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "commentList" to-many relationship of Timeline. */
    public List<Comment> _queryTimeline_CommentList(String uuid_timeline) {
        synchronized (this) {
            if (timeline_CommentListQuery == null) {
                QueryBuilder<Comment> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Uuid_timeline.eq(null));
                timeline_CommentListQuery = queryBuilder.build();
            }
        }
        Query<Comment> query = timeline_CommentListQuery.forCurrentThread();
        query.setParameter(0, uuid_timeline);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getTimelineDao().getAllColumns());
            builder.append(" FROM TR_COMMENT T");
            builder.append(" LEFT JOIN TR_TIMELINE T0 ON T.\"UUID_TIMELINE\"=T0.\"UUID_TIMELINE\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Comment loadCurrentDeep(Cursor cursor, boolean lock) {
        Comment entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Timeline timeline = loadCurrentOther(daoSession.getTimelineDao(), cursor, offset);
        entity.setTimeline(timeline);

        return entity;    
    }

    public Comment loadDeep(Long key) {
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
    public List<Comment> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Comment> list = new ArrayList<Comment>(count);
        
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
    
    protected List<Comment> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Comment> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
