package com.adins.mss.dao;

import com.adins.mss.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.adins.mss.base.util.ExcludeFromGson;
import com.google.gson.annotations.SerializedName;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "TR_COMMENT".
 */
public class Comment {

    /** Not-null value. */
     @SerializedName("uuid_comment")
    private String uuid_comment;
     @SerializedName("comment")
    private String comment;
     @SerializedName("dtm_crt_server")
    private java.util.Date dtm_crt_server;
     @SerializedName("sender_id")
    private String sender_id;
     @SerializedName("sender_name")
    private String sender_name;
     @SerializedName("usr_crt")
    private String usr_crt;
     @SerializedName("dtm_crt")
    private java.util.Date dtm_crt;
     @SerializedName("usr_upd")
    private String usr_upd;
     @SerializedName("dtm_upd")
    private java.util.Date dtm_upd;
     @SerializedName("uuid_timeline")
    private String uuid_timeline;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient CommentDao myDao;

    private Timeline timeline;
    private String timeline__resolvedKey;


    public Comment() {
    }

    public Comment(String uuid_comment) {
        this.uuid_comment = uuid_comment;
    }

    public Comment(String uuid_comment, String comment, java.util.Date dtm_crt_server, String sender_id, String sender_name, String usr_crt, java.util.Date dtm_crt, String usr_upd, java.util.Date dtm_upd, String uuid_timeline) {
        this.uuid_comment = uuid_comment;
        this.comment = comment;
        this.dtm_crt_server = dtm_crt_server;
        this.sender_id = sender_id;
        this.sender_name = sender_name;
        this.usr_crt = usr_crt;
        this.dtm_crt = dtm_crt;
        this.usr_upd = usr_upd;
        this.dtm_upd = dtm_upd;
        this.uuid_timeline = uuid_timeline;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCommentDao() : null;
    }

    /** Not-null value. */
    public String getUuid_comment() {
        return uuid_comment;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUuid_comment(String uuid_comment) {
        this.uuid_comment = uuid_comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public java.util.Date getDtm_crt_server() {
        return dtm_crt_server;
    }

    public void setDtm_crt_server(java.util.Date dtm_crt_server) {
        this.dtm_crt_server = dtm_crt_server;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getUsr_crt() {
        return usr_crt;
    }

    public void setUsr_crt(String usr_crt) {
        this.usr_crt = usr_crt;
    }

    public java.util.Date getDtm_crt() {
        return dtm_crt;
    }

    public void setDtm_crt(java.util.Date dtm_crt) {
        this.dtm_crt = dtm_crt;
    }

    public String getUsr_upd() {
        return usr_upd;
    }

    public void setUsr_upd(String usr_upd) {
        this.usr_upd = usr_upd;
    }

    public java.util.Date getDtm_upd() {
        return dtm_upd;
    }

    public void setDtm_upd(java.util.Date dtm_upd) {
        this.dtm_upd = dtm_upd;
    }

    public String getUuid_timeline() {
        return uuid_timeline;
    }

    public void setUuid_timeline(String uuid_timeline) {
        this.uuid_timeline = uuid_timeline;
    }

    /** To-one relationship, resolved on first access. */
    public Timeline getTimeline() {
        String __key = this.uuid_timeline;
        if (timeline__resolvedKey == null || timeline__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TimelineDao targetDao = daoSession.getTimelineDao();
            Timeline timelineNew = targetDao.load(__key);
            synchronized (this) {
                timeline = timelineNew;
            	timeline__resolvedKey = __key;
            }
        }
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        synchronized (this) {
            this.timeline = timeline;
            uuid_timeline = timeline == null ? null : timeline.getUuid_timeline();
            timeline__resolvedKey = uuid_timeline;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
