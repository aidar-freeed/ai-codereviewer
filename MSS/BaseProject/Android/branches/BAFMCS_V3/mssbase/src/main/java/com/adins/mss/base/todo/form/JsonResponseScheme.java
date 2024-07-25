package com.adins.mss.base.todo.form;

import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.Scheme;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author gigin.ginanjar
 */
public class JsonResponseScheme extends MssResponseType {
    /**
     * Property listScheme
     */
    @SerializedName("listScheme")
    List<Scheme> listScheme;
    @SerializedName("listPrintItems")
    List<PrintItem> listPrintItems;

    /**
     * Gets the listScheme
     */
    public List<Scheme> getListScheme() {
        return this.listScheme;
    }

    /**
     * Sets the listScheme
     */
    public void setListScheme(List<Scheme> value) {
        this.listScheme = value;
    }

    /**
     * Gets the listPrintItems
     */
    public List<PrintItem> getListPrintItems() {
        return this.listPrintItems;
    }

    /**
     * Sets the listPrintItems
     */
    public void setListPrintItems(List<PrintItem> value) {
        this.listPrintItems = value;
    }
}
