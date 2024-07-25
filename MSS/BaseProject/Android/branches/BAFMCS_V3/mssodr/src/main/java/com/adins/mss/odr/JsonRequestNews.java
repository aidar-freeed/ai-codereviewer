package com.adins.mss.odr;

import com.adins.mss.dao.MobileContentH;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonRequestNews extends MssRequestType{

	@SerializedName("uuid_mobile_content_h")
	String uuid_mobile_content_h;
	@SerializedName("listContentHeader")
	List<MobileContentH> listContentHeader;
	
	public List<MobileContentH> getListContentHeader() {
		return listContentHeader;
	}
	public void setListContentHeader(List<MobileContentH> listContentHeader) {
		this.listContentHeader = listContentHeader;
	}
	public String getuuid_mobile_content_h(){
		return uuid_mobile_content_h;
	}
	public void setuuid_mobile_content_h(String uuid_mobile_content_h){
		this.uuid_mobile_content_h = uuid_mobile_content_h;
	}
//	public class ContentHeader extends ContentHeaderBean{
//		public ContentHeader(MobileContentH contentH){
//			super(contentH);
//		}
//	}
}
