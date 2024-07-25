package com.adins.mss.base.todolist;

import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.Scheme;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonGetScheme extends MssResponseType {
    @SerializedName("listScheme")
    private List<Scheme> listScheme;

    @SerializedName("listPrintItem")
    private List<PrintItem> listPrintItem;

    public List<PrintItem> getListPrintItem() {
        return listPrintItem;
    }

    public void setListPrintItem(List<PrintItem> listPrintItem) {
        this.listPrintItem = listPrintItem;
    }

    public List<Scheme> getListScheme() {
        return listScheme;
    }

    public void setListSceme(List<Scheme> listScheme) {
        this.listScheme = listScheme;
    }


}
