package com.adins.mss.svy.models;

import androidx.annotation.Keep;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SuggestOnVerification extends MssResponseType {
	@SerializedName("listUser")
	public List<ListUser> listUser;	

	/**
	 * Gets the listUser
	 */
	public List<ListUser> getListUser() {
		return this.listUser;
	}

	/**
	 * Sets the listUser
	 */
	public void setListUser(List<ListUser> value) {
		this.listUser = value;
	}

	public class ListUser {
		@Keep
		public String uuid_ms_user;
		@Keep
		public String fullname;
		@Keep
		public String is_suggested;

		/**
		 * Gets the uuid_ms_user
		 */
		public String getUuid_ms_user() {
			return this.uuid_ms_user;
		}

		/**
		 * Sets the uuid_ms_user
		 */
		public void setUuid_ms_user(String value) {
			this.uuid_ms_user = value;
		}

		/**
		 * Gets the fullname
		 */
		public String getFullname() {
			return this.fullname;
		}

		/**
		 * Sets the fullname
		 */
		public void setFullname(String value) {
			this.fullname = value;
		}

		/**
		 * Gets the is_suggested
		 */
		public String getIs_suggested() {
			return this.is_suggested;
		}

		/**
		 * Sets the is_suggested
		 */
		public void setIs_suggested(String value) {
			this.is_suggested = value;
		}
	}
}
