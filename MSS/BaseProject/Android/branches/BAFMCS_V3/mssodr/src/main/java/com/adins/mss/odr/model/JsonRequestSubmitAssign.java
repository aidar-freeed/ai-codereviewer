package com.adins.mss.odr.model;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class JsonRequestSubmitAssign extends MssRequestType{
		/** Property uuid_user */
		@SerializedName("uuid_user")
	   String uuid_user;
	 
	   /** Property uuid_task_h */
	   @SerializedName("uuid_task_h")
	   String uuid_task_h;
	 
	   /** Property flag */
	   @SerializedName("flag")
	   String flag;
	 
	   /** Property notes */
	   @SerializedName("notes")
	   String notes;
	 
	   /**
	    * Gets the uuid_user
	    */
	   public String getUuid_user() {
	      return this.uuid_user;
	   }
	 
	   /**
	    * Sets the uuid_user
	    */
	   public void setUuid_user(String value) {
	      this.uuid_user = value;
	   }
	 
	   /**
	    * Gets the uuid_task_h
	    */
	   public String getUuid_task_h() {
	      return this.uuid_task_h;
	   }
	 
	   /**
	    * Sets the uuid_task_h
	    */
	   public void setUuid_task_h(String value) {
	      this.uuid_task_h = value;
	   }
	 
	   /**
	    * Gets the flag
	    */
	   public String getFlag() {
	      return this.flag;
	   }
	 
	   /**
	    * Sets the flag
	    */
	   public void setFlag(String value) {
	      this.flag = value;
	   }
	 
	   /**
	    * Gets the notes
	    */
	   public String getNotes() {
	      return this.notes;
	   }
	 
	   /**
	    * Sets the notes
	    */
	   public void setNotes(String value) {
	      this.notes = value;
	   }
}
