package com.adins.mss.base.syncfile;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.MobileDataFile;
import com.adins.mss.dao.MobileDataFileDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by loise on 10/16/2017.
 * <p>
 * Tambahkan code berikut di mssdaogenerator dan generate database baru.
 * INGAT : setiap kali ada perubahan db harus ditimpa ulang dengan isi berikut karena file akan tergenerate ulang oleh DAOgenerator
 * //MS_MOBILEDATAFILES
 * Entity mobileDataFiles = schema.addEntity("mobiledatafile").addImport("com.google.gson.annotations.Since");
 * mobileDataFiles.setTableName("MS_MOBILEDATAFILES");
 * mobileDataFiles.addLongProperty("id_datafile").notNull().primaryKey();
 * mobileDataFiles.addStringProperty("is_active");
 * mobileDataFiles.addDateProperty("max_timestamp");
 * mobileDataFiles.addStringProperty("file_url");
 * mobileDataFiles.addStringProperty("alternate_file_url");
 * mobileDataFiles.addStringProperty("hash_sha1");
 * mobileDataFiles.addStringProperty("usr_crt");
 * mobileDataFiles.addDateProperty("dtm_crt");
 * mobileDataFiles.addStringProperty("usr_upd");
 * mobileDataFiles.addDateProperty("dtm_upd");
 * mobileDataFiles.addStringProperty("downloaded_file_path");
 * mobileDataFiles.addBooleanProperty("import_flag");
 * <p>
 * ubah isi class object mobiledatafile menjadi seperti berikut:
 * public class mobiledatafile {
 *
 * @SerializedName("idDatafile") private long id_datafile;
 * @SerializedName("isActive") private String is_active;
 * @SerializedName("maxTimestamp") private java.util.Date max_timestamp;
 * @SerializedName("fileUrl") private String file_url;
 * @SerializedName("alternateFileUrl") private String alternate_file_url;
 * @SerializedName("hashSha1") private String hash_sha1;
 * @SerializedName("usrCrt") private String usr_crt;
 * @SerializedName("dtmCrt") private java.util.Date dtm_crt;
 * @SerializedName("usrUpd") private String usr_upd;
 * @SerializedName("dtmUpd") private java.util.Date dtm_upd;
 * private transient String downloaded_file_path;
 * private transient Boolean import_flag;
 * <p>
 * public mobiledatafile() {
 * }
 * <p>
 * public mobiledatafile(long id_datafile) {
 * this.id_datafile = id_datafile;
 * }
 * <p>
 * public mobiledatafile(long id_datafile, String is_active, java.util.Date max_timestamp, String file_url, String alternate_file_url, String hash_sha1, String usr_crt, java.util.Date dtm_crt, String usr_upd, java.util.Date dtm_upd, String downloaded_file_path, Boolean import_flag) {
 * this.id_datafile = id_datafile;
 * this.is_active = is_active;
 * this.max_timestamp = max_timestamp;
 * this.file_url = file_url;
 * this.alternate_file_url = alternate_file_url;
 * this.hash_sha1 = hash_sha1;
 * this.usr_crt = usr_crt;
 * this.dtm_crt = dtm_crt;
 * this.usr_upd = usr_upd;
 * this.dtm_upd = dtm_upd;
 * this.downloaded_file_path = downloaded_file_path;
 * this.import_flag = import_flag;
 * }
 * <p>
 * public long getId_datafile() {
 * return id_datafile;
 * }
 * <p>
 * public void setId_datafile(long id_datafile) {
 * this.id_datafile = id_datafile;
 * }
 * <p>
 * public String getIs_active() {
 * return is_active;
 * }
 * <p>
 * public void setIs_active(String is_active) {
 * this.is_active = is_active;
 * }
 * <p>
 * public java.util.Date getMax_timestamp() {
 * return max_timestamp;
 * }
 * <p>
 * public void setMax_timestamp(java.util.Date max_timestamp) {
 * this.max_timestamp = max_timestamp;
 * }
 * <p>
 * public String getFile_url() {
 * return file_url;
 * }
 * <p>
 * public void setFile_url(String file_url) {
 * this.file_url = file_url;
 * }
 * <p>
 * public String getAlternate_file_url() {
 * return alternate_file_url;
 * }
 * <p>
 * public void setAlternate_file_url(String alternate_file_url) {
 * this.alternate_file_url = alternate_file_url;
 * }
 * <p>
 * public String getHash_sha1() {
 * return hash_sha1;
 * }
 * <p>
 * public void setHash_sha1(String hash_sha1) {
 * this.hash_sha1 = hash_sha1;
 * }
 * <p>
 * public String getUsr_crt() {
 * return usr_crt;
 * }
 * <p>
 * public void setUsr_crt(String usr_crt) {
 * this.usr_crt = usr_crt;
 * }
 * <p>
 * public java.util.Date getDtm_crt() {
 * return dtm_crt;
 * }
 * <p>
 * public void setDtm_crt(java.util.Date dtm_crt) {
 * this.dtm_crt = dtm_crt;
 * }
 * <p>
 * public String getUsr_upd() {
 * return usr_upd;
 * }
 * <p>
 * public void setUsr_upd(String usr_upd) {
 * this.usr_upd = usr_upd;
 * }
 * <p>
 * public java.util.Date getDtm_upd() {
 * return dtm_upd;
 * }
 * <p>
 * public void setDtm_upd(java.util.Date dtm_upd) {
 * this.dtm_upd = dtm_upd;
 * }
 * <p>
 * public String getDownloaded_file_path() {
 * return downloaded_file_path;
 * }
 * <p>
 * public void setDownloaded_file_path(String downloaded_file_path) {
 * this.downloaded_file_path = downloaded_file_path;
 * }
 * <p>
 * public Boolean getImport_flag() {
 * return import_flag;
 * }
 * <p>
 * public void setImport_flag(Boolean import_flag) {
 * this.import_flag = import_flag;
 * }
 * <p>
 * }
 */

/**
 * Tambahkan code berikut di mssdaogenerator dan generate database baru.
 * INGAT : setiap kali ada perubahan db harus ditimpa ulang dengan isi berikut karena file akan tergenerate ulang oleh DAOgenerator
 * //MS_MOBILEDATAFILES
 Entity mobileDataFiles = schema.addEntity("mobiledatafile").addImport("com.google.gson.annotations.Since");
 mobileDataFiles.setTableName("MS_MOBILEDATAFILES");
 mobileDataFiles.addLongProperty("id_datafile").notNull().primaryKey();
 mobileDataFiles.addStringProperty("is_active");
 mobileDataFiles.addDateProperty("max_timestamp");
 mobileDataFiles.addStringProperty("file_url");
 mobileDataFiles.addStringProperty("alternate_file_url");
 mobileDataFiles.addStringProperty("hash_sha1");
 mobileDataFiles.addStringProperty("usr_crt");
 mobileDataFiles.addDateProperty("dtm_crt");
 mobileDataFiles.addStringProperty("usr_upd");
 mobileDataFiles.addDateProperty("dtm_upd");
 mobileDataFiles.addStringProperty("downloaded_file_path");
 mobileDataFiles.addBooleanProperty("import_flag");
 */

/**
 * ubah isi class object mobiledatafile menjadi seperti berikut:
 public class mobiledatafile {

@SerializedName("idDatafile") private long id_datafile;
@SerializedName("isActive") private String is_active;
@SerializedName("maxTimestamp") private java.util.Date max_timestamp;
@SerializedName("fileUrl") private String file_url;
@SerializedName("alternateFileUrl") private String alternate_file_url;
@SerializedName("hashSha1") private String hash_sha1;
@SerializedName("usrCrt") private String usr_crt;
@SerializedName("dtmCrt") private java.util.Date dtm_crt;
@SerializedName("usrUpd") private String usr_upd;
@SerializedName("dtmUpd") private java.util.Date dtm_upd;
private transient String downloaded_file_path;
private transient Boolean import_flag;

public mobiledatafile() {
}

public mobiledatafile(long id_datafile) {
this.id_datafile = id_datafile;
}

public mobiledatafile(long id_datafile, String is_active, java.util.Date max_timestamp, String file_url, String alternate_file_url, String hash_sha1, String usr_crt, java.util.Date dtm_crt, String usr_upd, java.util.Date dtm_upd, String downloaded_file_path, Boolean import_flag) {
this.id_datafile = id_datafile;
this.is_active = is_active;
this.max_timestamp = max_timestamp;
this.file_url = file_url;
this.alternate_file_url = alternate_file_url;
this.hash_sha1 = hash_sha1;
this.usr_crt = usr_crt;
this.dtm_crt = dtm_crt;
this.usr_upd = usr_upd;
this.dtm_upd = dtm_upd;
this.downloaded_file_path = downloaded_file_path;
this.import_flag = import_flag;
}

public long getId_datafile() {
return id_datafile;
}

public void setId_datafile(long id_datafile) {
this.id_datafile = id_datafile;
}

public String getIs_active() {
return is_active;
}

public void setIs_active(String is_active) {
this.is_active = is_active;
}

public java.util.Date getMax_timestamp() {
return max_timestamp;
}

public void setMax_timestamp(java.util.Date max_timestamp) {
this.max_timestamp = max_timestamp;
}

public String getFile_url() {
return file_url;
}

public void setFile_url(String file_url) {
this.file_url = file_url;
}

public String getAlternate_file_url() {
return alternate_file_url;
}

public void setAlternate_file_url(String alternate_file_url) {
this.alternate_file_url = alternate_file_url;
}

public String getHash_sha1() {
return hash_sha1;
}

public void setHash_sha1(String hash_sha1) {
this.hash_sha1 = hash_sha1;
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

public String getDownloaded_file_path() {
return downloaded_file_path;
}

public void setDownloaded_file_path(String downloaded_file_path) {
this.downloaded_file_path = downloaded_file_path;
}

public Boolean getImport_flag() {
return import_flag;
}

public void setImport_flag(Boolean import_flag) {
this.import_flag = import_flag;
}

}
 */

/**
 * class containing methods to access local database
 */
public class MobileDataFileDataAccess {

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

    protected static MobileDataFileDao getMobileDataFileDao(Context context) {
        return getDaoSession(context).getMobileDataFileDao();
    }

    public static void closeAll() {
		/*if(daoOpenHelper!=null){
			daoOpenHelper.closeAll();
			daoOpenHelper = null;
		}*/
        DaoOpenHelper.closeAll();
    }

    /**
     * insert or replace record
     * @param context
     * @param mobileDataFile
     */
    public static void addOrReplace(Context context, MobileDataFile mobileDataFile) {
        if (getOne(context, mobileDataFile.getId_datafile()) == null) {
            add(context, mobileDataFile);
        } else {
            update(context, mobileDataFile);
        }
    }

    /**
     * bulk insert list
     * @param context
     * @param mobileDataFileList
     */
    public static void addOrReplace(Context context, List<MobileDataFile> mobileDataFileList) {
        for (MobileDataFile mdf : mobileDataFileList)
            if (getOne(context, mdf.getId_datafile()) == null)
                add(context, mdf);
            else update(context, mdf);
    }

    /**
     * insert single row
     *
     * @param context
     * @param mobileDataFile
     *
     *
     */
    public static void add(Context context, MobileDataFile mobileDataFile) {
        getMobileDataFileDao(context).insert(mobileDataFile);
    }

    /**
     * bulk insert
     *
     * @param context
     * @param mobileDataFileList
     */
    public static void add(Context context, List<MobileDataFile> mobileDataFileList) {
        getMobileDataFileDao(context).insertInTx(mobileDataFileList);
    }

    /**
     *
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getMobileDataFileDao(context).deleteAll();
    }

    /**
     * @param context
     * @param mobileDataFile
     */
    public static void delete(Context context, MobileDataFile mobileDataFile) {
        getMobileDataFileDao(context).delete(mobileDataFile);
    }

    /**
     * @param context
     * @param mobileDataFile
     */
    public static void update(Context context, MobileDataFile mobileDataFile) {
        getMobileDataFileDao(context).update(mobileDataFile);
    }

    /**
     * get one mobileDataFile
     *
     * @param context
     * @param keymobiledatafile
     * @return
     */
    public static MobileDataFile getOne(Context context, long keymobiledatafile) {
        QueryBuilder<MobileDataFile> qb = getMobileDataFileDao(context).queryBuilder();
        qb.where(MobileDataFileDao.Properties.Id_datafile.eq(keymobiledatafile));
        qb.build();
        if (qb.list().size() > 0)
            return qb.list().get(0);
        else return null;
    }

    /**
     * select * from table where id_datafile = param
     *
     * @param context
     * @param idDataFile
     * @return
     */
    public static List<MobileDataFile> getAll(Context context, long idDataFile) {
        QueryBuilder<MobileDataFile> qb = getMobileDataFileDao(context).queryBuilder();
        qb.where(MobileDataFileDao.Properties.Id_datafile.eq(idDataFile));
        qb.build();
        return qb.list();
    }

    /**
     * get list of all active files that need to be imported to local db
     * @param context
     * @return
     */
    public static List<MobileDataFile> getAllActive(Context context) {
        QueryBuilder<MobileDataFile> qb = getMobileDataFileDao(context).queryBuilder();
        qb.where(MobileDataFileDao.Properties.Is_active.eq("1"), MobileDataFileDao.Properties.Import_flag.eq("TRUE"));
        qb.build();
        return qb.list();
    }

    /**
     * get highest timestamp
     * @param context
     * @return
     */
    public static Date getMaxTimestamp(Context context) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        QueryBuilder<MobileDataFile> qb = getMobileDataFileDao(context).queryBuilder();
        qb.orderDesc(MobileDataFileDao.Properties.Max_timestamp);
        qb.build();
        if (qb.list().size() > 0)
            return qb.list().get(0).getMax_timestamp();
        else return cal.getTime();
    }
}
