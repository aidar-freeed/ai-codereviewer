package com.services.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonRequestLastSync extends MssRequestType {
    @SerializedName("uuid_user")
    private String uuid_user;
    @SerializedName("dtm_req")
    private String dtm_req;
    @SerializedName("data")
    private String data;
    @SerializedName("listOfLOV")
    private List listOfLOV;
    @SerializedName("is_send")
    private int is_send;
    @SerializedName("flag")
    private String flag;

    public List getListOfLOV() {
        return listOfLOV;
    }

    public void setListOfLOV(List listOfLOV) {
        this.listOfLOV = listOfLOV;
    }

    public String getUuid_user() {
        return uuid_user;
    }

    public void setUuid_user(String uuid_user) {
        this.uuid_user = uuid_user;
    }

    public String getDtm_req() {
        return dtm_req;
    }

    public void setDtm_req(String dtm_req) {
        this.dtm_req = dtm_req;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getIs_send() {
        return is_send;
    }

    public void setIs_send(int is_send) {
        this.is_send = is_send;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
