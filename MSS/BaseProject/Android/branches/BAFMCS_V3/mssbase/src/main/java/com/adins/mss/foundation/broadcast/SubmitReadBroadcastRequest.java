package com.adins.mss.foundation.broadcast;

import com.google.gson.annotations.SerializedName;

public class SubmitReadBroadcastRequest {
    @SerializedName("uuid_user")
    String uuidUser;
    @SerializedName("uuid_notification")
    String uuidNotification;

    public String getUuidUser() {
        return uuidUser;
    }

    public void setUuidUser(String uuidUser) {
        this.uuidUser = uuidUser;
    }

    public String getUuidNotification() {
        return uuidNotification;
    }

    public void setUuidNotification(String uuidNotification) {
        this.uuidNotification = uuidNotification;
    }
}
