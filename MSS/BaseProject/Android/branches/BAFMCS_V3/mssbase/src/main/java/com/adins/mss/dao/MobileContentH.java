package com.adins.mss.dao;

import java.util.List;
import com.adins.mss.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.adins.mss.base.util.ExcludeFromGson;
import com.google.gson.annotations.SerializedName;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "TR_MOBILECONTENT_H".
 */
public class MobileContentH {

    /** Not-null value. */
     @SerializedName("uuid_mobile_content_h")
    private String uuid_mobile_content_h;
     @SerializedName("content_name")
    private String content_name;
     @SerializedName("last_update")
    private java.util.Date last_update;
     @SerializedName("content_description")
    private String content_description;
     @SerializedName("usr_crt")
    private String usr_crt;
     @SerializedName("dtm_crt")
    private java.util.Date dtm_crt;
     @SerializedName("usr_upd")
    private String usr_upd;
     @SerializedName("dtm_upd")
    private java.util.Date dtm_upd;
     @SerializedName("uuid_user")
    private String uuid_user;
     @SerializedName("uuid_parent_content")
    private String uuid_parent_content;
     @SerializedName("start_date")
    private java.util.Date start_date;
     @SerializedName("end_date")
    private java.util.Date end_date;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MobileContentHDao myDao;

    private MobileContentH mobileContentH;
    private String mobileContentH__resolvedKey;

    private User user;
    private String user__resolvedKey;

    private List<MobileContentD> mobileContentDList;
    private List<MobileContentH> mobileContentHList;

    public MobileContentH() {
    }

    public MobileContentH(String uuid_mobile_content_h) {
        this.uuid_mobile_content_h = uuid_mobile_content_h;
    }

    public MobileContentH(String uuid_mobile_content_h, String content_name, java.util.Date last_update, String content_description, String usr_crt, java.util.Date dtm_crt, String usr_upd, java.util.Date dtm_upd, String uuid_user, String uuid_parent_content, java.util.Date start_date, java.util.Date end_date) {
        this.uuid_mobile_content_h = uuid_mobile_content_h;
        this.content_name = content_name;
        this.last_update = last_update;
        this.content_description = content_description;
        this.usr_crt = usr_crt;
        this.dtm_crt = dtm_crt;
        this.usr_upd = usr_upd;
        this.dtm_upd = dtm_upd;
        this.uuid_user = uuid_user;
        this.uuid_parent_content = uuid_parent_content;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMobileContentHDao() : null;
    }

    /** Not-null value. */
    public String getUuid_mobile_content_h() {
        return uuid_mobile_content_h;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUuid_mobile_content_h(String uuid_mobile_content_h) {
        this.uuid_mobile_content_h = uuid_mobile_content_h;
    }

    public String getContent_name() {
        return content_name;
    }

    public void setContent_name(String content_name) {
        this.content_name = content_name;
    }

    public java.util.Date getLast_update() {
        return last_update;
    }

    public void setLast_update(java.util.Date last_update) {
        this.last_update = last_update;
    }

    public String getContent_description() {
        return content_description;
    }

    public void setContent_description(String content_description) {
        this.content_description = content_description;
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

    public String getUuid_user() {
        return uuid_user;
    }

    public void setUuid_user(String uuid_user) {
        this.uuid_user = uuid_user;
    }

    public String getUuid_parent_content() {
        return uuid_parent_content;
    }

    public void setUuid_parent_content(String uuid_parent_content) {
        this.uuid_parent_content = uuid_parent_content;
    }

    public java.util.Date getStart_date() {
        return start_date;
    }

    public void setStart_date(java.util.Date start_date) {
        this.start_date = start_date;
    }

    public java.util.Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(java.util.Date end_date) {
        this.end_date = end_date;
    }

    /** To-one relationship, resolved on first access. */
    public MobileContentH getMobileContentH() {
        String __key = this.uuid_parent_content;
        if (mobileContentH__resolvedKey == null || mobileContentH__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MobileContentHDao targetDao = daoSession.getMobileContentHDao();
            MobileContentH mobileContentHNew = targetDao.load(__key);
            synchronized (this) {
                mobileContentH = mobileContentHNew;
            	mobileContentH__resolvedKey = __key;
            }
        }
        return mobileContentH;
    }

    public void setMobileContentH(MobileContentH mobileContentH) {
        synchronized (this) {
            this.mobileContentH = mobileContentH;
            uuid_parent_content = mobileContentH == null ? null : mobileContentH.getUuid_mobile_content_h();
            mobileContentH__resolvedKey = uuid_parent_content;
        }
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

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<MobileContentD> getMobileContentDList() {
        if (mobileContentDList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MobileContentDDao targetDao = daoSession.getMobileContentDDao();
            List<MobileContentD> mobileContentDListNew = targetDao._queryMobileContentH_MobileContentDList(uuid_mobile_content_h);
            synchronized (this) {
                if(mobileContentDList == null) {
                    mobileContentDList = mobileContentDListNew;
                }
            }
        }
        return mobileContentDList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetMobileContentDList() {
        mobileContentDList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<MobileContentH> getMobileContentHList() {
        if (mobileContentHList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MobileContentHDao targetDao = daoSession.getMobileContentHDao();
            List<MobileContentH> mobileContentHListNew = targetDao._queryMobileContentH_MobileContentHList(uuid_mobile_content_h);
            synchronized (this) {
                if(mobileContentHList == null) {
                    mobileContentHList = mobileContentHListNew;
                }
            }
        }
        return mobileContentHList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetMobileContentHList() {
        mobileContentHList = null;
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
