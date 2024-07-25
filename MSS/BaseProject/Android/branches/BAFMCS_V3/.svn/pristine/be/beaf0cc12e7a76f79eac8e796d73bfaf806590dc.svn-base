package com.adins.mss.coll.models;

import com.adins.mss.dao.CollectionActivity;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dian.ina on 08/05/2015.
 */
public class CollectionActivityResponse extends MssResponseType {
	//private List<CollectionActivityItem> collectionHistoryList;
    @SerializedName("collectionHistoryList")
	private List<CollectionActivity> collectionHistoryList;

    @SerializedName("agreementNo")
    private String agreementNo;
    
    public String getAgreementNo() {
       return this.agreementNo;
    }
  
    public void setAgreementNo(String value) {
       this.agreementNo = value;
    }
    
    public CollectionActivityResponse() {
    }

	public List<CollectionActivity> getCollectionHistoryList() {
		return collectionHistoryList;
	}

	public void setCollectionHistoryList(
			List<CollectionActivity> collectionHistoryList) {
		this.collectionHistoryList = collectionHistoryList;
	}

//    public List<CollectionActivityItem> getCollectionHistoryList() {
//        return collectionHistoryList;
//    }
//
//    public void setCollectionHistoryList(List<CollectionActivityItem> collectionHistoryList) {
//        this.collectionHistoryList = collectionHistoryList;
//    }
    
    
}
