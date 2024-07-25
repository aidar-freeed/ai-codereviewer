package com.adins.mss.base.dynamicform.form.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class SummaryAnswerBean {
    @SerializedName("listHeader")
    private List<ListHeader> listHeader;
    @SerializedName("listDetail")
    private List<Map<String, Object>> listDetail;

    public List<ListHeader> getListHeader() {
        return listHeader;
    }

    public void setListHeader(List<ListHeader> listHeader) {
        this.listHeader = listHeader;
    }

    public List<Map<String, Object>> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<Map<String, Object>> listDetail) {
        this.listDetail = listDetail;
    }

    public class ListHeader {
        @SerializedName("refId")
        private String refId;
        @SerializedName("label")
        private String label;
//        @SerializedName("ansType")
//        private String ansType;

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

//        public String getAnsType() {
//            return ansType;
//        }
//
//        public void setAnsType(String ansType) {
//            this.ansType = ansType;
//        }
    }
}
