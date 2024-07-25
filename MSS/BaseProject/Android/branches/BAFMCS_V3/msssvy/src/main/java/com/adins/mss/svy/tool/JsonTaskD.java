package com.adins.mss.svy.tool;

import com.adins.mss.dao.TaskD;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonTaskD extends MssResponseType {
	@SerializedName("listTaskD")
	List<TaskD> listTaskD;
	
	public List<TaskD> getListTaskD(){
		return listTaskD;
	}
	public void setListTaskD (List<TaskD> listTaskD){
		this.listTaskD = listTaskD;
	}
}
