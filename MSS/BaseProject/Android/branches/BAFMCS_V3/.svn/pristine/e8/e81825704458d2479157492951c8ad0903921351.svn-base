package com.adins.mss.coll.models;

import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Aditya Purwa on 3/4/2015.
 */
public class DepositReportRequest extends MssRequestType {
    @SerializedName("reportHeader")
    private DepositReportH reportHeader;
    @SerializedName("listReportDetail")
    private List<DepositReportD> listReportDetail;

    public DepositReportH getReportHeader() {
        return reportHeader;
    }

    public void setReportHeader(DepositReportH reportHeader) {
        this.reportHeader = reportHeader;
    }

    public List<DepositReportD> getListReportDetail() {
        return listReportDetail;
    }

    public void setListReportDetail(List<DepositReportD> listReportDetail) {
        this.listReportDetail = listReportDetail;
    }
}
