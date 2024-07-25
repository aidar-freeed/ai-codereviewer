package com.adins.mss.foundation.sync;

public class SynchronizeItem {

    private String syncItemId;
    private String action;

    public SynchronizeItem(String syncItemId, String action) {
        this.syncItemId = syncItemId;
        this.action = action;
    }


    //=== Getter Setter ===//

    public String getSyncItemId() {
        return syncItemId;
    }

    public void setSyncItemId(String syncItemId) {
        this.syncItemId = syncItemId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
//
//	public Class<?> getJsonType() {
//		return jsonType;
//	}
//
//	public void setJsonType(Class<?> jsonType) {
//		this.jsonType = jsonType;
//	}

}
