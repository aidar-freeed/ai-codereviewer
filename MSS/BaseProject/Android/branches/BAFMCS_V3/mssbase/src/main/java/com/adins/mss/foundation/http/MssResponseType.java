package com.adins.mss.foundation.http;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

//import javax.xml.bind.annotation.XmlRootElement;

//@XmlRootElement(name="result")
public class MssResponseType implements Serializable {
    @SerializedName("unstructured")
    protected KeyValue[] unstructured;
    @SerializedName("status")
    protected MssResponseType.Status status;

    public KeyValue[] getUnstructured() {
        return unstructured;
    }

    public void setUnstructured(KeyValue[] unstructured) {
        this.unstructured = unstructured;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(MssResponseType.Status status) {
        this.status = status;
    }

    public static class Status implements Serializable {
        @SerializedName("code")
        protected int code;
        @SerializedName("message")
        protected String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
