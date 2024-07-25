package com.adins.mss.base.todolist.form.todaysplan.dummytaskplan;

public class DummyPlan{
    private boolean showDeleteBtn;
    private String customer;
    private String address;
    private String phone;

    public DummyPlan(boolean showDeleteBtn, String customer, String address, String phone) {
        this.showDeleteBtn = showDeleteBtn;
        this.customer = customer;
        this.address = address;
        this.phone = phone;
    }

    public boolean isShowDeleteBtn() {
        return showDeleteBtn;
    }

    public void setShowDeleteBtn(boolean showDeleteBtn) {
        this.showDeleteBtn = showDeleteBtn;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
