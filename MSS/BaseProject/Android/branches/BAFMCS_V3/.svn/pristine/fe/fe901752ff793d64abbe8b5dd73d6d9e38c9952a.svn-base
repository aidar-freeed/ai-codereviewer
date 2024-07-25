package com.adins.mss.svy.tool;

import com.adins.mss.foundation.http.KeyValue;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonResponseServer extends MssResponseType{
	@SerializedName("listResponseServer")
	List<ResponseServer> listResponseServer;

	public List<ResponseServer> getListResponseServer() {
		return listResponseServer;
	}
	public void setListResponseServer(List<ResponseServer> listResponseServer) {
		this.listResponseServer = listResponseServer;
	}
	
	public class ResponseServer extends KeyValue {
		@SerializedName("flag")
		String flag;
		@SerializedName("subListResponseServer")
		List<ResponseServer> subListResponseServer;

		public ResponseServer(String key, String value) {
			super(key, value);
		}
		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

		public List<ResponseServer> getSubListResponseServer() {
			return subListResponseServer;
		}

		public void setSubListResponseServer(List<ResponseServer> subListResponseServer) {
			this.subListResponseServer = subListResponseServer;
		}
	}
}
