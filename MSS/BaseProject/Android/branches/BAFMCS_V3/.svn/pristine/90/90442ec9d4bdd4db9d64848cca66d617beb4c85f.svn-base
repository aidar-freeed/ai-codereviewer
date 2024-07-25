package com.adins.mss.odr.update;

import com.adins.mss.foundation.http.KeyValue;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonResponseDetailCancelOrder extends MssResponseType{
		/** Property listDetailOrder */
		@SerializedName("listDetailOrder")
	   List<ResponseServer> listDetailOrder;
	 
	   /**
	    * Gets the listDetailOrder
	    */
	   public List<ResponseServer> getListDetailOrder() {
	      return this.listDetailOrder;
	   }
	 
	   /**
	    * Sets the listDetailOrder
	    */
	   public void setListDetailOrder(List<ResponseServer> value) {
	      this.listDetailOrder = value;
	   }
	   
	   public class ResponseServer extends KeyValue {
		   @SerializedName("flag")
			String flag;
			public ResponseServer(String key, String value) {
				super(key, value);
			}
			List<ResponseServer> subListResponseServer;

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
