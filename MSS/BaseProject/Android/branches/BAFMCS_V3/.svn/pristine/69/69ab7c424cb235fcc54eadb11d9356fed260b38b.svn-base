package com.adins.mss.foundation.services;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.services.ScheduledItem.ScheduledItemHandler;

/**
 * @author glen.iglesias
 *         <p> Subclass of ScheduledItem which is specific to connect to server by interval
 */
public abstract class ScheduledConnectionItem extends ScheduledItem implements ScheduledItemHandler {

    protected String url;
    protected boolean enc;
    protected boolean dec;

    public ScheduledConnectionItem(String id, int interval, String url, boolean enc, boolean dec) {
        super(id, interval);
        setHandler(this);
        this.url = url;
        this.enc = enc;
        this.dec = dec;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean onEventTrigger(ScheduledItem schItem) {

        String jsonString = getData();
        Logger.d("scheduledConnection", "schItem : " + schItem.scheduleId + "json : " + jsonString);

        HttpCryptedConnection httpConn = new HttpCryptedConnection(enc, dec);
        HttpConnectionResult result = null;
        try {
            result = httpConn.requestToServer(url, jsonString);
            Logger.d("scheduledConnection", "schItem : " + schItem.scheduleId + "url : " + url);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }

        //TODO watch for exception if there is no result (internet failure from device)
        boolean skipInterval;
        if (result != null && result.getStatusCode() == 200) {
            skipInterval = onSuccess(result);
        } else {
            skipInterval = onFail(result);
        }
        return skipInterval;
    }

    /**
     * @return provide the string to send to server
     */
    protected abstract String getData();

    /**
     * Provide actions if connection success
     *
     * @param result
     * @return true if ScheduledConnectionItem should trigger without waiting for interval,
     * false if ScheduledConnectionItem should wait for next interval
     */
    protected abstract boolean onSuccess(HttpConnectionResult result);

    /**
     * Provide actions if connection failed
     *
     * @param result
     * @return true if ScheduledConnectionItem should trigger without waiting for interval,
     * false if ScheduledConnectionItem should wait for next interval
     */
    protected abstract boolean onFail(HttpConnectionResult result);

}
