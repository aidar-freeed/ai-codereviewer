package com.adins.mss.foundation.http;

import com.adins.mss.base.GlobalData;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Base template for mobile request to server, with slight modification to store imei in unstructured,
 * <br> and add method to manage unique item inside unstructured based on key
 *
 * @author sumatris
 */
public class MssRequestType implements Serializable {
    public static final String UN_KEY_IMEI = "imei";
    public static final String UN_KEY_IMEI2 = "imei2";
    public static final String UN_KEY_ANDROID_ID = "androidId";
    @SerializedName("audit")
    protected AuditDataType audit;
    @SerializedName("unstructured")
    protected KeyValue[] unstructured = new KeyValue[0];

    public AuditDataType getAudit() {
        return audit;
    }

    public void setAudit(AuditDataType audit) {
        this.audit = audit;
    }

    public KeyValue[] getUnstructured() {
        return unstructured;
    }

    public void setUnstructured(KeyValue[] unstructured) {
        this.unstructured = unstructured;
    }

    public String getImei() {
        KeyValue item = getUnstructuredItem(UN_KEY_IMEI);
        if (item != null) {
            String imei = item.value;
            return imei;
        }
        return "";
    }

    //Glen store imei inside unstructured
    public void setImei(String imei) {
        KeyValue imeiKv = new KeyValue(UN_KEY_IMEI, imei);
        addItemToUnstructured(imeiKv, true);
    }

    //=== Glen - Unstructured Item Manipulation Regarding Unique Key ===//

    /**
     * Add item to unstructured with regards of existing key
     *
     * @param item
     * @param replaceExisting to set behavior of replacing any existing key or stop when existing key is found
     */
    public void addItemToUnstructured(KeyValue item, boolean replaceExisting) {
        int existingItemIdx = getUnstructuredItemIndex(item.key);
        if (existingItemIdx != -1) {
            //Item is already existing
            if (replaceExisting) {
                replaceUnstructuredItemValue(existingItemIdx, item.value);
            }
            //if replacing is unallowed, no process is done
        } else {
            addNewUnstructuredItem(item);
        }
    }

    public void addImeiAndroidIdToUnstructured() {
        addItemToUnstructured(new KeyValue(UN_KEY_IMEI, GlobalData.getSharedGlobalData().getImei()), false);
        if (GlobalData.getSharedGlobalData().getImei2() != null && !GlobalData.getSharedGlobalData().getImei2().isEmpty())
            addItemToUnstructured(new KeyValue(UN_KEY_IMEI2, GlobalData.getSharedGlobalData().getImei2()), false);
        addItemToUnstructured(new KeyValue(UN_KEY_ANDROID_ID, GlobalData.getSharedGlobalData().getAndroidId()), false);
    }

    /**
     * Get unstructured item index by key
     *
     * @param key
     * @return index of unstructured item with such key
     */
    public int getUnstructuredItemIndex(String key) {
        for (int i = 0; i < unstructured.length; i++) {
            KeyValue item = unstructured[i];
            if (item.key.equals(key)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get unstructured item by key
     *
     * @param key
     * @return unstructured item with
     */
    public KeyValue getUnstructuredItem(String key) {
        for (KeyValue item : unstructured) {
            if (item.key.equals(key)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Add new unstructured item without checking of existing item. It is encouraged to use addItemToUnstructured
     *
     * @param item
     */
    private void addNewUnstructuredItem(KeyValue item) {
        KeyValue[] newKV = new KeyValue[unstructured.length + 1];
        System.arraycopy(unstructured, 0, newKV, 0, unstructured.length);
        newKV[newKV.length - 1] = item;
        unstructured = newKV;
    }

    public void deleteUnstructuredItem(int index) {
        KeyValue[] newKV = new KeyValue[unstructured.length - 1];
        int j = 0;
        for (int i = 0; i < unstructured.length; i++) {
            if (i != index) {
                newKV[j] = unstructured[i];
                j++;
            }
        }
        unstructured = newKV;
    }

    public void replaceUnstructuredItemValue(int index, String value) {
        KeyValue item = unstructured[index];
        item.setValue(value);
    }

    public void replaceUnstructuredItemValue(String key, String value) {
        KeyValue item = getUnstructuredItem(key);
        item.setValue(value);
    }

}
