package com.adins.mss.svy.models;

import androidx.annotation.Keep;

import com.adins.mss.foundation.http.MssResponseType;
import com.adins.mss.svy.tool.JsonResponseServer.ResponseServer;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActionOnVerification extends MssResponseType{
    @SerializedName("listDetail")
	public List<ResponseServer> listDetail;
    @SerializedName("listAction")
    public List<ListAction> listAction;
    
    /**
     * Gets the listDetail
     */
    public List<ResponseServer> getListDetail() {
       return this.listDetail;
    }
  
    /**
     * Sets the listDetail
     */
    public void setListDetail(List<ResponseServer> value) {
       this.listDetail = value;
    }
  
    /**
     * Gets the listAction
     */
    public List<ListAction> getListAction() {
       return this.listAction;
    }
  
    /**
     * Sets the listAction
     */
    public void setListAction(List<ListAction> value) {
       this.listAction = value;
    }


    public class ListAction
    {
        @Keep
        public String uuid_status_task;
        @Keep
        public String status_code;
        @Keep
        public String status_desc;
        
        /**
         * Gets the uuid_status_task
         */
        public String getUuid_status_task() {
           return this.uuid_status_task;
        }
      
        /**
         * Sets the uuid_status_task
         */
        public void setUuid_status_task(String value) {
           this.uuid_status_task = value;
        }
      
        /**
         * Gets the status_code
         */
        public String getStatus_code() {
           return this.status_code;
        }
      
        /**
         * Sets the status_code
         */
        public void setStatus_code(String value) {
           this.status_code = value;
        }
      
        /**
         * Gets the status_desc
         */
        public String getStatus_desc() {
           return this.status_desc;
        }
      
        /**
         * Sets the status_desc
         */
        public void setStatus_desc(String value) {
           this.status_desc = value;
        }
        
        @Override
        public String toString() {
            return this.status_desc;
        }
    }
    
}

