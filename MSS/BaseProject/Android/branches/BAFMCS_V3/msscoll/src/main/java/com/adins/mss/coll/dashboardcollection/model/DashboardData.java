package com.adins.mss.coll.dashboardcollection.model;

import java.util.List;

public class DashboardData {
    private double targetAmount,collectedAmount;
    private double outstandingAmount;
    private int outstandingNum;
    private List<CollResultDetail> collectDetails;
    private List<CollResultDetail> ptpDetails;
    private List<CollResultDetail> failedDetails;

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(double collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public int getOutstandingNum() {
        return outstandingNum;
    }

    public void setOutstandingNum(int outstandingNum) {
        this.outstandingNum = outstandingNum;
    }

    public List<CollResultDetail> getCollectDetails() {
        return collectDetails;
    }

    public void setCollectDetails(List<CollResultDetail> collectDetails) {
        this.collectDetails = collectDetails;
    }

    public List<CollResultDetail> getPtpDetails() {
        return ptpDetails;
    }

    public void setPtpDetails(List<CollResultDetail> ptpDetails) {
        this.ptpDetails = ptpDetails;
    }

    public List<CollResultDetail> getFailedDetails() {
        return failedDetails;
    }

    public void setFailedDetails(List<CollResultDetail> failedDetails) {
        this.failedDetails = failedDetails;
    }
}
