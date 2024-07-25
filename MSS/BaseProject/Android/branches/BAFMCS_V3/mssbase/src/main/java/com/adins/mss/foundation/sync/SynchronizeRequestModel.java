package com.adins.mss.foundation.sync;

import com.adins.mss.base.BaseCommunicationModel;

/**
 * JSON for Synchronize process
 *
 * @author glen.iglesias
 * @deprecated as of 17 Dec 2014, as BaseCommunicationModel
 */
public class SynchronizeRequestModel extends BaseCommunicationModel {

    private String action;
    private String lastupdate;

    public SynchronizeRequestModel() {
    }

    public SynchronizeRequestModel(boolean useDefault, String table, String lastupdate) {
        super(useDefault);
        this.action = table;
        this.lastupdate = lastupdate;
    }

    public String getTable() {
        return action;
    }

    public void setTable(String table) {
        this.action = table;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

}
