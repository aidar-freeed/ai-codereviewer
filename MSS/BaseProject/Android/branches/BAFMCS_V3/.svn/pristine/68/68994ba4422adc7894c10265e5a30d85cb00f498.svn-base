package com.adins.mss.coll.models;

import com.adins.mss.dao.InstallmentSchedule;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by adityapurwa on 06/05/15.
 */
public class InstallmentScheduleResponse extends MssResponseType {

    //private List<InstallmentScheduleItem> installmentScheduleList;
    @SerializedName("installmentScheduleList")
	private List<InstallmentSchedule> installmentScheduleList;
    @SerializedName("agreementNo")
    private String agreementNo;
    
    public String getAgreementNo() {
       return this.agreementNo;
    }
  
    public void setAgreementNo(String value) {
       this.agreementNo = value;
    }
    
    public InstallmentScheduleResponse() {
    }

	public List<InstallmentSchedule> getInstallmentScheduleList() {
		return installmentScheduleList;
	}

	public void setInstallmentScheduleList(
			List<InstallmentSchedule> installmentScheduleList) {
		this.installmentScheduleList = installmentScheduleList;
	}

//    public List<InstallmentScheduleItem> getInstallmentScheduleList() {
//        return installmentScheduleList;
//    }
//
//    public void setInstallmentScheduleList(List<InstallmentScheduleItem> installmentScheduleList) {
//        this.installmentScheduleList = installmentScheduleList;
//    }
    
    
    
}
