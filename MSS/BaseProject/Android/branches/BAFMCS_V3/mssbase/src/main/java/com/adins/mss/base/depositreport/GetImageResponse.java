package com.adins.mss.base.depositreport;

import com.adins.mss.dao.TaskD;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by angga.permadi on 8/30/2016.
 */
public class GetImageResponse extends MssResponseType {

    @SerializedName("img")
    private List<TaskD> image;

    public List<TaskD> getImage() {
        return image;
    }

    public void setImage(List<TaskD> image) {
        this.image = image;
    }
}
