package com.adins.mss.base.dynamicform.form.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PhotoDocumentBean implements Serializable {

    @SerializedName("Name")
    private String name;
    @SerializedName("Content")
    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
