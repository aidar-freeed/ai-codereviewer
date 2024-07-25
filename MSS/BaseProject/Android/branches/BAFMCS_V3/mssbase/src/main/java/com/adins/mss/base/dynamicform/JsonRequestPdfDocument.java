package com.adins.mss.base.dynamicform;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class JsonRequestPdfDocument extends MssRequestType {

    @SerializedName("loginId")
    private String loginId;
    @SerializedName("agreementNo")
    private String agreementNo;
    @SerializedName("docType")
    private String docType;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getAgreementNo() {
        return agreementNo;
    }

    public void setAgreementNo(String agreementNo) {
        this.agreementNo = agreementNo;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }
}
