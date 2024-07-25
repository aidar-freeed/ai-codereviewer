package com.adins.mss.base.loyalti.mypointdashboard;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DetailKompetisiResponse extends MssResponseType {

    @SerializedName("resultList")
    public ArrayList<MemberShipData> resultList;

    public ArrayList<MemberShipData> getResultList() {
        return resultList;
    }

    public void setResultList(ArrayList<MemberShipData> resultList) {
        this.resultList = resultList;
    }

}

class MemberShipData {

    public String getMEMBERSHIP_PROGRAM_CODE() {
        return MEMBERSHIP_PROGRAM_CODE;
    }

    public void setMEMBERSHIP_PROGRAM_CODE(String MEMBERSHIP_PROGRAM_CODE) {
        this.MEMBERSHIP_PROGRAM_CODE = MEMBERSHIP_PROGRAM_CODE;
    }

    public String getINFORMATION_TYPE() {
        return INFORMATION_TYPE;
    }

    public void setINFORMATION_TYPE(String INFORMATION_TYPE) {
        this.INFORMATION_TYPE = INFORMATION_TYPE;
    }

    public ArrayList<TeamMember> getTEAM_MEMBER() {
        return TEAM_MEMBER;
    }

    public void setTEAM_MEMBER(ArrayList<TeamMember> TEAM_MEMBER) {
        this.TEAM_MEMBER = TEAM_MEMBER;
    }
    public String getLOGO() {
        return LOGO;
    }

    public void setLOGO(String LOGO) {
        this.LOGO = LOGO;
    }
    @SerializedName("MEMBERSHIP_PROGRAM_CODE")
    protected String MEMBERSHIP_PROGRAM_CODE;

    @SerializedName("INFORMATION_TYPE")
    protected String INFORMATION_TYPE;

    @SerializedName("MEMBERSHIP_PROGRAM_NAME")
    protected String MEMBERSHIP_PROGRAM_NAME;

    @SerializedName("MEMBERSHIP_PROGRAM_DESCRIPTION")
    protected String MEMBERSHIP_PROGRAM_DESCRIPTION;

    @SerializedName("MEMBERSHIP_PROGRAM_STATUS")
    protected String MEMBERSHIP_PROGRAM_STATUS;

    @SerializedName("MEMBERSHIP_PROGRAM_START_DATE")
    protected String MEMBERSHIP_PROGRAM_START_DATE;

    @SerializedName("GRACE_PERIODE")
    protected String GRACE_PERIODE;

    @SerializedName("MEMBERSHIP_PROGRAM_EXPIRED_DATE")
    protected String MEMBERSHIP_PROGRAM_EXPIRED_DATE;

    public String getMEMBERSHIP_PROGRAM_NAME() {
        return MEMBERSHIP_PROGRAM_NAME;
    }

    public void setMEMBERSHIP_PROGRAM_NAME(String MEMBERSHIP_PROGRAM_NAME) {
        this.MEMBERSHIP_PROGRAM_NAME = MEMBERSHIP_PROGRAM_NAME;
    }

    public String getMEMBERSHIP_PROGRAM_DESCRIPTION() {
        return MEMBERSHIP_PROGRAM_DESCRIPTION;
    }

    public void setMEMBERSHIP_PROGRAM_DESCRIPTION(String MEMBERSHIP_PROGRAM_DESCRIPTION) {
        this.MEMBERSHIP_PROGRAM_DESCRIPTION = MEMBERSHIP_PROGRAM_DESCRIPTION;
    }

    public String getMEMBERSHIP_PROGRAM_STATUS() {
        return MEMBERSHIP_PROGRAM_STATUS;
    }

    public void setMEMBERSHIP_PROGRAM_STATUS(String MEMBERSHIP_PROGRAM_STATUS) {
        this.MEMBERSHIP_PROGRAM_STATUS = MEMBERSHIP_PROGRAM_STATUS;
    }

    public String getMEMBERSHIP_PROGRAM_START_DATE() {
        return MEMBERSHIP_PROGRAM_START_DATE;
    }

    public void setMEMBERSHIP_PROGRAM_START_DATE(String MEMBERSHIP_PROGRAM_START_DATE) {
        this.MEMBERSHIP_PROGRAM_START_DATE = MEMBERSHIP_PROGRAM_START_DATE;
    }

    public String getGRACE_PERIODE() {
        return GRACE_PERIODE;
    }

    public void setGRACE_PERIODE(String GRACE_PERIODE) {
        this.GRACE_PERIODE = GRACE_PERIODE;
    }

    public String getMEMBERSHIP_PROGRAM_EXPIRED_DATE() {
        return MEMBERSHIP_PROGRAM_EXPIRED_DATE;
    }

    public void setMEMBERSHIP_PROGRAM_EXPIRED_DATE(String MEMBERSHIP_PROGRAM_EXPIRED_DATE) {
        this.MEMBERSHIP_PROGRAM_EXPIRED_DATE = MEMBERSHIP_PROGRAM_EXPIRED_DATE;
    }

    @SerializedName("TEAM_MEMBER")
    protected ArrayList<TeamMember> TEAM_MEMBER;


    @SerializedName("LOGO")
    protected String LOGO;


}
