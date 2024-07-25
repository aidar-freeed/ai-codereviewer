package com.adins.mss.foundation.sync;

import com.adins.mss.foundation.sync.Synchronize.SynchronizeScheme;

import java.util.ArrayList;
import java.util.List;


/**
 * Default scheme with basic table synchronization
 *
 * @author glen.iglesias
 * @see SynchronizeScheme
 */
public class DefaultSynchronizeScheme implements SynchronizeScheme, BackgroundSynchronize.SynchronizeScheme {

    public DefaultSynchronizeScheme() {
    }

    @Override
    public List<SynchronizeItem> getSynchronizeItemList() {
        List<SynchronizeItem> list = new ArrayList<SynchronizeItem>();

        //init default SynchronizeItems and add to list
        list.add(new SynchronizeItem("Test", "Test"));

        return list;
    }

    @Override
    public boolean insertToDb(String json, String syncItemId) {

        if ("".equals(syncItemId)) {

        }

        return false;
    }

}
