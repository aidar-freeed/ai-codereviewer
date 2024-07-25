package com.adins.mss.base.about.activity;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonVersionResponse extends MssResponseType {

    @SerializedName("listValue")
    List<ListValue> listValue;

    public List<ListValue> getListValues() {
        return this.listValue;
    }

    public void setListValues(List<ListValue> listValue) {
        this.listValue = listValue;
    }

    public class ListValue {
        @SerializedName("key")
        String key;
        @SerializedName("value")
        String value;

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
