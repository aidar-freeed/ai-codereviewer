package com.adins.mss.base.todolist.form.helper;

import com.adins.mss.dao.Scheme;

public class TaskFilterParam {
    private Scheme scheme;
    private int searchType;
    private int ptp;
    private String customerName;
    private String osFrom;
    private String osTo;
    private String tenorFrom;
    private String tenorTo;

    public TaskFilterParam(Scheme scheme, int searchType) {
        this.scheme = scheme;
        this.searchType = searchType;
    }

    public TaskFilterParam(Scheme scheme, int searchType, int ptp, String customerName, String osFrom, String osTo, String tenorFrom, String tenorTo) {
        this.scheme = scheme;
        this.searchType = searchType;
        this.ptp = ptp;
        this.customerName = customerName;
        this.osFrom = osFrom;
        this.osTo = osTo;
        this.tenorFrom = tenorFrom;
        this.tenorTo = tenorTo;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public int getSearchType() {
        return searchType;
    }

    public int getPtp() {
        return ptp;
    }

    public void setPtp(int ptp) {
        this.ptp = ptp;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOsFrom() {
        return osFrom;
    }

    public void setOsFrom(String osFrom) {
        this.osFrom = osFrom;
    }

    public String getOsTo() {
        return osTo;
    }

    public void setOsTo(String osTo) {
        this.osTo = osTo;
    }

    public String getTenorFrom() {
        return tenorFrom;
    }

    public void setTenorFrom(String tenorFrom) {
        this.tenorFrom = tenorFrom;
    }

    public String getTenorTo() {
        return tenorTo;
    }

    public void setTenorTo(String tenorTo) {
        this.tenorTo = tenorTo;
    }
}
