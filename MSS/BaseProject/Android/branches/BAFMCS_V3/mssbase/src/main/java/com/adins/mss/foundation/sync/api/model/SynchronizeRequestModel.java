package com.adins.mss.foundation.sync.api.model;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by adityapurwa on 11/03/15.
 */
public class SynchronizeRequestModel extends MssRequestType {
    @SerializedName("tableName")
    protected String tableName;
    @SerializedName("dtm_upd")
    protected Date dtm_upd;
    @SerializedName("list")
    protected List list;
    @SerializedName("init")
    protected int init;
    @SerializedName("LOGIN_ID")
    public String loginId;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public SynchronizeRequestModel() {
    }

    public SynchronizeRequestModel(SynchronizeRequestModel requestModel) {
        this.tableName = requestModel.getTableName();
        if (requestModel.getDtm_upd() != null)
            this.dtm_upd = requestModel.getDtm_upd();
        if (requestModel.getList() != null)
            this.list = requestModel.getList();
        this.init = requestModel.getInit();
    }

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
