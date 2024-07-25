package com.adins.mss.base.dynamicform.form.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TableAnswerResponse {
    @SerializedName("tableAnswer")
    private List<TableAnswer> tableAnswer = null;

    public List<TableAnswer> getTableAnswer() {
        return tableAnswer;
    }

    public void setTableAnswer(List<TableAnswer> tableAnswer) {
        this.tableAnswer = tableAnswer;
    }

    public class TableAnswer {

        @SerializedName("owner")
        private String owner;

        @SerializedName("data")
        private SummaryAnswerBean summaryAnswer;

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public SummaryAnswerBean getSummaryAnswer() {
            return summaryAnswer;
        }

        public void setSummaryAnswer(SummaryAnswerBean summaryAnswer) {
            this.summaryAnswer = summaryAnswer;
        }
    }
}
