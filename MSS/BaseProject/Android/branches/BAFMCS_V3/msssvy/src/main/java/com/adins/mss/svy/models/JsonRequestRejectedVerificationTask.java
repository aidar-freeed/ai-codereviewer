package com.adins.mss.svy.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class JsonRequestRejectedVerificationTask extends MssRequestType {
	@SerializedName("uuid_task_h")
	String uuid_task_h;
	@SerializedName("flag")
	String flag;
	@SerializedName("notes")
	String notes;

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String value) {
		this.notes = value;
	}

	public String getUuid_task_h() {
		return this.uuid_task_h;
	}

	public void setUuid_task_h(String value) {
		this.uuid_task_h = value;
	}

	public String getFlag() {
		return this.flag;
	}

	public void setFlag(String value) {
		this.flag = value;
	}
}
