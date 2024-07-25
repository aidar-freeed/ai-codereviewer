package com.adins.mss.odr.catalogue.api;

import com.google.gson.annotations.SerializedName;

public class PromoCatalogBean{
	@SerializedName("uuid_mobile_content_d") private String uuidMobileContentD;
	@SerializedName("uuid_mobile_content_h") private String uuidMobileContentH;
	@SerializedName("content_name") private String contentName;
	@SerializedName("content_description") private String contentDescription;
	@SerializedName("content") private String content;

	public String getUuidMobileContentD() {
		return uuidMobileContentD;
	}
	public void setUuidMobileContentD(String uuidMobileContentD) {
		this.uuidMobileContentD = uuidMobileContentD;
	}
	public String getUuidMobileContentH() {
		return uuidMobileContentH;
	}
	public void setUuidMobileContentH(String uuidMobileContentH) {
		this.uuidMobileContentH = uuidMobileContentH;
	}
	public String getContentName() {
		return contentName;
	}
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}
	public String getContentDescription() {
		return contentDescription;
	}
	public void setContentDescription(String contentDescription) {
		this.contentDescription = contentDescription;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
