package com.adins.mss.svy.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class JsonRequestTaskWithMode extends MssRequestType {
	/** Property mode */
	@SerializedName("mode")
	String mode;

	/**
	 * Gets the mode
	 */
	public String getMode() {
		return this.mode;
	}

	/**
	 * Sets the mode
	 */
	public void setMode(String value) {
		this.mode = value;
	}
}
