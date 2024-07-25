package com.adins.mss.foundation.sync.api;

import com.google.gson.annotations.SerializedName;

class Kompetisi2 {

    @SerializedName("membershipProgramCode")
    private String membershipProgramCode;
    @SerializedName("membershipProgramName")
    private String membershipProgramName;
    @SerializedName("membershipProgramPriorityCode")
    private String membershipProgramPriorityCode;
    @SerializedName("membershipProgramStatus")
    private String membershipProgramStatus;
    @SerializedName("membershipProgramExpiredDate")
    private String membershipProgramExpiredDate;
    @SerializedName("membershipProgramStartDate")
    private String membershipProgramStartDate;
    @SerializedName("gracePeriode")
    private String gracePeriode;

    public String getMembershipProgramCode() {
        return membershipProgramCode;
    }

    public void setMembershipProgramCode(String membershipProgramCode) {
        this.membershipProgramCode = membershipProgramCode;
    }

    public String getMembershipProgramName() {
        return membershipProgramName;
    }

    public void setMembershipProgramName(String membershipProgramName) {
        this.membershipProgramName = membershipProgramName;
    }

    public String getMembershipProgramPriorityCode() {
        return membershipProgramPriorityCode;
    }

    public void setMembershipProgramPriorityCode(String membershipProgramPriorityCode) {
        this.membershipProgramPriorityCode = membershipProgramPriorityCode;
    }

    public String getMembershipProgramStatus() {
        return membershipProgramStatus;
    }

    public void setMembershipProgramStatus(String membershipProgramStatus) {
        this.membershipProgramStatus = membershipProgramStatus;
    }

    public String getMembershipProgramExpiredDate() {
        return membershipProgramExpiredDate;
    }

    public void setMembershipProgramExpiredDate(String membershipProgramExpiredDate) {
        this.membershipProgramExpiredDate = membershipProgramExpiredDate;
    }

    public String getMembershipProgramStartDate() {
        return membershipProgramStartDate;
    }

    public void setMembershipProgramStartDate(String membershipProgramStartDate) {
        this.membershipProgramStartDate = membershipProgramStartDate;
    }

    public String getGracePeriode() {
        return gracePeriode;
    }

    public void setGracePeriode(String gracePeriode) {
        this.gracePeriode = gracePeriode;
    }
}
