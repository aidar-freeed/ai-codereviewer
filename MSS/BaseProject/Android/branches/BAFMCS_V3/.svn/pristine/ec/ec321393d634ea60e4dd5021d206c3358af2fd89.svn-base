package com.adins.mss.foundation.sync;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.http.HttpBackgroundConnection;
import com.adins.mss.foundation.http.HttpBackgroundConnection.HttpBackgroundConnectionListener;
import com.adins.mss.foundation.http.HttpConnection;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;

import java.util.List;

/**
 * A class to perform a sequence of synchronization. Items to be synchronized are defined in SynchronizeScheme, which
 * will provide Synchronize with the list of SynchronizeItem and the process of inserting it to database. Connection will be made
 * on background thread, while the progress update will be done on UI Thread
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
 *
 * @author glen.iglesias
 * @see Synchronize
 * @see SynchronizeScheme
 * @see SynchronizeItem
 */
public class BackgroundSynchronize implements HttpBackgroundConnectionListener {

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
     * Current SynchonizeItem which being processed
     */
    private SynchronizeItem currSyncItem;

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

    public BackgroundSynchronize() {
    }

    public SynchronizeListener getListener() {
        return listener;
    }

    public void setListener(SynchronizeListener listener) {
        this.listener = listener;
    }

    public void setSynchronizeScheme(SynchronizeScheme scheme) {
        this.scheme = scheme;
        syncItems = scheme.getSynchronizeItemList();
        initialize();
    }

    public void startSynchronize() {
        initialize();
        synchronizeCurrentProgress();
    }

    private void initialize() {
        currProgress = 0;
        numOfRetries = 0;
    }

    private void synchronizeCurrentProgress() {
        SynchronizeItem syncItem = syncItems.get(currProgress);
        synchronize(syncItem);
    }

    private void synchronize(SynchronizeItem syncItem) {
        //register as currSyncItem, so the connection callback can access currSyncItem
        currSyncItem = syncItem;

        //get last_update
        //TODO

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
        HttpBackgroundConnection connTask = new HttpBackgroundConnection(url, data, conn, this);
        connTask.execute();
    }

    /**
     * Call this to resume synchronize form the last step tried but failed. It counts the number of retries up.
     * <br>This should only be used to resume, not as the start of synchronize process
     */
    public void resumeSync() {
        numOfRetries++;
        synchronizeCurrentProgress();
    }

    //=== Background Connection Listener ===//
    @Override
    public void onConnectionResult(HttpBackgroundConnection connTask,
                                   HttpConnection conn, HttpConnectionResult result) {
        if (!result.isOK()) {
            //numOfRetries are incremented on resumeSync()
            listener.synchronizeFailed(currSyncItem, result, numOfRetries);
            return;
        }

        //reset numOfRetries
        numOfRetries = 0;

        //process server return
        String resultString = result.getResult();

        //save to db
        scheme.insertToDb(resultString, currSyncItem.getSyncItemId());

        //update progress
        currProgress++;

        int totalItem = syncItems.size();
        float progress = ((float) currProgress / totalItem);
        listener.progressUpdated(progress);

        synchronizeCurrentProgress();
    }

    //=== Interfaces ===//

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
