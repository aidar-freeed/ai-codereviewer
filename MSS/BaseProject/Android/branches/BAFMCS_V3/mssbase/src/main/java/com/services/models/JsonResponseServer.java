package com.services.models;

import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.foundation.http.KeyValue;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonResponseServer extends MssResponseType {
    @SerializedName("listResponseServer")
    List<ResponseServer> listResponseServer;

    public List<ResponseServer> getListResponseServer() {
        return listResponseServer;
    }

    public void setListResponseServer(List<ResponseServer> listResponseServer) {
        this.listResponseServer = listResponseServer;
    }

    @Override
    public String toString() {
        return GsonHelper.toJson(this);
    }

    public class ResponseServer extends KeyValue {
        @SerializedName("flag")
        String flag;
        @SerializedName("subListResponseServer")
        List<ResponseServer> subListResponseServer;
        @SerializedName("formName")
        String formName;

        public ResponseServer(String key, String value) {
            super(key, value);
        }

        public String getFormName() {
            return this.formName;
        }

        public void setFormName(String value) {
            this.formName = value;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public List<ResponseServer> getSubListResponseServer() {
            return subListResponseServer;
        }

        public void setSubListResponseServer(List<ResponseServer> subListResponseServer) {
            this.subListResponseServer = subListResponseServer;
        }
    }
}
