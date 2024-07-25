package com.adins.mss.odr.news;

import java.io.Serializable;

import com.adins.mss.dao.MobileContentH;

public class ContentHeaderBean extends MobileContentH implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7312670270610210391L;
	public ContentHeaderBean(){		
	}
	MobileContentH contentH;
	public ContentHeaderBean(MobileContentH contentH){
		setContent_description(contentH.getContent_description());
		setContent_name(contentH.getContent_name());
		setDtm_crt(contentH.getDtm_crt());
		setDtm_upd(contentH.getDtm_upd());		
		setStart_date(contentH.getStart_date());
		setEnd_date(contentH.getEnd_date());
		setLast_update(contentH.getLast_update());		
		setUsr_crt(contentH.getUsr_crt());
		setUsr_upd(contentH.getUsr_upd());
		setUuid_mobile_content_h(contentH.getUuid_mobile_content_h());
		setUuid_parent_content(contentH.getUuid_parent_content());
		setUuid_user(contentH.getUuid_user());
		setUser(contentH.getUser());		
	}
	public MobileContentH getMobileContentH(){
		return this;
	}
	public void setMobileContentH(MobileContentH contentH){
		this.contentH = contentH;
	}
}

