package com.adins.mss.base.syncfile;

import com.adins.mss.dao.MobileDataFile;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by loise on 10/16/2017.
 */


/**
 * template for response from server
 */
public class JsonResponseSyncFile implements Serializable {

    @SerializedName("data")
    List<MobileDataFile> listMobileDatafile;
    @SerializedName("status")
    MssResponseType.Status status;

    public MssResponseType.Status getStatus() {
        return status;
    }

    public void setStatus(MssResponseType.Status status) {
        this.status = status;
    }

    public List<MobileDataFile> getListMobileDataFile() {
        return this.listMobileDatafile;
    }

    public void setListMobileDataFile(List<MobileDataFile> value) {
        this.listMobileDatafile = value;
    }

}


