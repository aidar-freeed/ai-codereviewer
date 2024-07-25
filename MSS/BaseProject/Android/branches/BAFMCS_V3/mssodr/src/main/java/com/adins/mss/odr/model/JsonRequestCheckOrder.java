package com.adins.mss.odr.model;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * @author gigin.ginanjar
 */
public class JsonRequestCheckOrder extends MssRequestType {
    @SerializedName("order_number")
    String order_number;
    @SerializedName("start_date")
    String start_date;
    @SerializedName("end_date")
    String end_date;
    @SerializedName("status")
    String status;
    @SerializedName("flag")
    String flag;
    @SerializedName("customer_name")
    String customer_name;

    /**
     * Gets the flag
     */
    public String getFlag() {
        return this.flag;
    }

    /**
     * Sets the flag
     */
    public void setFlag(String value) {
        this.flag = value;
    }

    /**
     * Gets the orderNumber
     */
    public String getOrderNumber() {
        return this.order_number;
    }

    /**
     * Sets the orderNumber
     */
    public void setOrderNumber(String value) {
        this.order_number = value;
    }

    /**
     * Gets the startDate
     */
    public String getStartDate() {
        return this.start_date;
    }

    /**
     * Sets the startDate
     */
    public void setStartDate(String value) {
        this.start_date = value;
    }

    /**
     * Gets the endDate
     */
    public String getEndDate() {
        return this.end_date;
    }

    /**
     * Sets the endDate
     */
    public void setEndDate(String value) {
        this.end_date = value;
    }

    /**
     * Gets the status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets the status
     */
    public void setStatus(String value) {
        this.status = value;
    }

    public String getCustomerName() {
        return customer_name;
    }

    public void setCustomerName(String customer_name) {
        this.customer_name = customer_name;
    }
}
