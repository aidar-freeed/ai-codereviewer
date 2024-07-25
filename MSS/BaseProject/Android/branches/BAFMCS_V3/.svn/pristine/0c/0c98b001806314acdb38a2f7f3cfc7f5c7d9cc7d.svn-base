package com.adins.mss.base.avatar;

import com.google.gson.annotations.SerializedName;

/**
 * Created by intishar.fa on 02/10/2018.
 */

public class AvatarUploadResponseJson {
    @SerializedName("status")
    private UploadAvatarStatus status;
    @SerializedName("result")
    private String result;

    public UploadAvatarStatus getStatus() {
        return status;
    }

    public void setStatus(UploadAvatarStatus status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}

class UploadAvatarStatus{
    @SerializedName("code")
    protected int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
