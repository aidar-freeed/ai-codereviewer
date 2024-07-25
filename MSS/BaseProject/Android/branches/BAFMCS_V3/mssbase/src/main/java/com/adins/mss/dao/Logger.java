package com.adins.mss.dao;

import com.adins.mss.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.adins.mss.base.util.ExcludeFromGson;
import com.google.gson.annotations.SerializedName;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "TR_APPLICATION_LOG".
 */
public class Logger {

    /** Not-null value. */
     @SerializedName("uuid_log")
    private String uuid_log;
     @SerializedName("screen")
    private String screen;
     @SerializedName("timestamp")
    private java.util.Date timestamp;
     @SerializedName("detail")
    private String detail;
     @SerializedName("uuid_user")
    private String uuid_user;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient LoggerDao myDao;

    private User user;
    private String user__resolvedKey;


    public Logger() {
    }

    public Logger(String uuid_log) {
        this.uuid_log = uuid_log;
    }

    public Logger(String uuid_log, String screen, java.util.Date timestamp, String detail, String uuid_user) {
        this.uuid_log = uuid_log;
        this.screen = screen;
        this.timestamp = timestamp;
        this.detail = detail;
        this.uuid_user = uuid_user;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLoggerDao() : null;
    }

    /** Not-null value. */
    public String getUuid_log() {
        return uuid_log;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUuid_log(String uuid_log) {
        this.uuid_log = uuid_log;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public java.util.Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(java.util.Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getUuid_user() {
        return uuid_user;
    }

    public void setUuid_user(String uuid_user) {
        this.uuid_user = uuid_user;
    }

    /** To-one relationship, resolved on first access. */
    public User getUser() {
        String __key = this.uuid_user;
        if (user__resolvedKey == null || user__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
            	user__resolvedKey = __key;
            }
        }
        return user;
    }

    public void setUser(User user) {
        synchronized (this) {
            this.user = user;
            uuid_user = user == null ? null : user.getUuid_user();
            user__resolvedKey = uuid_user;
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
