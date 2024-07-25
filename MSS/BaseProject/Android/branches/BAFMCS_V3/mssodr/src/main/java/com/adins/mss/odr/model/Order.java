package com.adins.mss.odr.model;

import java.util.List;

/**
 * Created by winy.firdasari on 20/01/2015.
 */
public class Order {
    public String orderNo;
    public String customerName;
    public List<StatusOrder> statusOrder;


    public Order(){

    }

    public String getOrderNo() {
        return orderNo;
    }
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public List<StatusOrder> getStatusOrder() {
        return statusOrder;
    }
    public void setStatusOrder(List<StatusOrder> statusOrder) {
        this.statusOrder = statusOrder;
    }
}
