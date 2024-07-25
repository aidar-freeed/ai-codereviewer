package com.adins.mss.foundation.sync;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;

import java.util.List;

/**
 * @author glen.iglesias
 * @see SynchronizeScheme
 * @see SynchronizeItem
 * @deprecated use BackgroundSynchronize instead
 * A class to perform a sequence of synchronization. Items to be synchronized are defined in SynchronizeScheme, which
 * will provide Synchronize with the list of SynchronizeItem and the process of inserting it to database
 * <p>
 * <p>The steps needed to use Synchronize to do a synchronization process are:
 * <li>Create a Synchronize object
 * <li>Set a SynchronizeScheme to the object. Use DefaultSynchronizeScheme for MSS default scheme, or use own implementation of SynchronizeScheme
 * <li>Set a SychronizeListener to respond on synchronize progress
 * <li>Call startSynchronize() to start sync process. Method would return true/false based on result of the process. Synchronize
 * object will also call progressUpdated() or synchronizeFailed(). It is recommended to handle the progress from the callback
 * instead of using the method return
 * <li>When sync process failed in one of the process, sync process will stopped. To resume from the last sync process, call
 * resumeSync(). As long as no changes are made to the scheme, resumeSync() will resume process from the last step of synchronization
 */
public class Synchronize {

    /**
     * To store last progress of sync, so sync process can be resumed based on this parameter.
     */
    private int currProgress;


    /**
     * To store how many retries or resume has been made after a synchronize failure
     */
    private int numOfRetries;

    /**
     * A list of SynchronizeItem to get information on what and where to request to server, and on how and where to store
     * data returned from server. This list determines the steps of synchronize process.
     * <br>This is automatically set on set SynchronizeScheme
     */
    private List<SynchronizeItem> syncItems;


    /**
     * A scheme which tells Synchronize object what are the items that need to be synchronized, in a form of a
     * SynchronizeItem object, and tells how to process received data from server
     * <p>
     * <P>When the scheme is changed, it is considered as a new synchronize process, thus reset it's state
     */
    private SynchronizeScheme scheme;


    /**
     * The listener to update synchronize progress
     */
    private SynchronizeListener listener;

    public Synchronize() {
        //EMPTY
    }

    public void setSynchronizeScheme(SynchronizeScheme scheme) {
        this.scheme = scheme;
        syncItems = scheme.getSynchronizeItemList();
        currProgress = 0;
        numOfRetries = 0;
    }

    public SynchronizeListener getListener() {
        return listener;
    }

    public void setListener(SynchronizeListener listener) {
        this.listener = listener;
    }

    /**
     * Start the synchronize process.
     *
     * @return true if all synchronize step are done successfully, false if it failed in one of the step
     */
    public boolean startSynchronize() {
        List<SynchronizeItem> items = syncItems;
        for (SynchronizeItem item : items) {
            boolean result = synchronize(item);
            if (!result) return false;
        }
        return true;
    }
    /**
     * Do synchronize process of one SynchronizeItem.
     *
     * @param syncItem
     * @return true if it is connected to server successfully, false if it failed
     */
    private boolean synchronize(SynchronizeItem syncItem) {
        //get last_update

        //create json to send to server
        String data;
        SynchronizeRequestModel model = new SynchronizeRequestModel(true, syncItem.getAction(), "lastupdate");
        data = GsonHelper.toJson(model);
        Logger.d("Sync", "JSON = " + data);

        //request connection to server
        GlobalData gd = GlobalData.getSharedGlobalData();
        String url = gd.getUrlSync();
        boolean enc = gd.isEncrypt();
        boolean dec = gd.isDecrypt();

        HttpCryptedConnection conn = new HttpCryptedConnection(enc, dec);
        HttpConnectionResult result;
        try {
            result = conn.requestToServer(url, data);
        } catch (Exception e) {
            FireCrash.log(e);
            //If I were right, this would never be called, because even if requestHTTPPost got exception, it return result
            //instead
            e.printStackTrace();
            result = null;
        }

        if(result == null){
            listener.synchronizeFailed(syncItem,null,numOfRetries);
            return false;
        }

        if (result.getStatusCode() != 200) {
            listener.synchronizeFailed(syncItem, result, numOfRetries);
            return false;
        }

        //process server return
        String resultString = result.getResult();

        //save to db
        scheme.insertToDb(resultString, syncItem.getSyncItemId());

        //update progress
        currProgress++;

        //publish progress
        List<SynchronizeItem> syncItems = scheme.getSynchronizeItemList();

        int totalItem = syncItems.size();
        float progress = ((float) currProgress / totalItem);
        listener.progressUpdated(progress);

        return true;
    }

    /**
     * Call this to resume synchronize form the last step tried but failed. It counts the number of retries up.
     * <br>This should only be used to resume, not as the start of synchronize process
     *
     * @return true if all synchronize steps passed successfully, false if it fail in one of the steps
     */
    public boolean resumeSync() {
        numOfRetries++;
        for (int i = currProgress; i < syncItems.size(); i++) {
            SynchronizeItem syncItem = syncItems.get(i);
            boolean result = synchronize(syncItem);
            if (!result) return false;
        }
        return true;
    }


    /**
     * Interface definition for a callback to be invoked on Synchronize process, which are when progress updated successfully
     * and when it failed
     *
     * @author glen.iglesias
     */
    public interface SynchronizeListener {
        /**
         * Called when a step of synchronization succeed.
         *
         * @param progress the updated current progress of synchronization in percentage
         */
        void progressUpdated(float progress);

        /**
         * Called when a step of synchronization failed.
         *
         * @param syncItem     SynchronizeItem in which step is failed
         * @param errorResult  the connection error. Null if the cause of error is not connection
         * @param numOfRetries how many time
         */
        void synchronizeFailed(SynchronizeItem syncItem, HttpConnectionResult errorResult, int numOfRetries);
    }

    /**
     * Interface definition on which Synchronize object will call when in need of Synchronize info.
     *
     * @author glen.iglesias
     */
    public interface SynchronizeScheme {

        /**
         * Provide Synchronize with a list of SynchronizeItem
         *
         * @return a list of SynchronizeItem to be used by Synchronize object
         */
        List<SynchronizeItem> getSynchronizeItemList();

        /**
         * Responsible to insert data returned from server to specific database table
         * <p>
         * <p>We are supposed to create the implementation of how to process the returned data in form of a JSON
         * <br>The JSON format depends on how server return the data, thus an agreement on JSON format is needed
         *
         * @param json       returned data from server in a form of JSON
         * @param syncItemId corresponding SynchonizeItem id for returned JSON
         * @return true if successfully processed the data
         */
        boolean insertToDb(String json, String syncItemId);

    }


}
