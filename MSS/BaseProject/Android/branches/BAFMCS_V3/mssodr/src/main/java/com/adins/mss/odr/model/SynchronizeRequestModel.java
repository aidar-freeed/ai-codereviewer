package com.adins.mss.odr.model;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by adityapurwa on 11/03/15.
 */
public class SynchronizeRequestModel extends MssRequestType {
    @SerializedName("tableName")
    private String tableName;
    @SerializedName("dtm_upd")
    private Date dtm_upd;
    @SerializedName("list")
    private List list;
    @SerializedName("init")
    private int init;

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Date getDtm_upd() {
        return dtm_upd;
    }

    public void setDtm_upd(Date dtm_upd) {
        this.dtm_upd = dtm_upd;
    }

    public int getInit() {
        return init;
    }

    public void setInit(int init) {
        this.init = init;
    }
}
