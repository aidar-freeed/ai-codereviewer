package com.adins.mss.foundation.UserHelp.Bean.Dummy;

public class UserHelpPropertiesDummy {
    private String text;
    String jobstatus;
    boolean square;

    public UserHelpPropertiesDummy() {
    }

    public UserHelpPropertiesDummy(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getJobStatus() {
        return jobstatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobstatus = jobStatus;
    }

    public boolean isSquare() {
        return square;
    }

    public void setSquare(boolean square) {
        this.square = square;
    }
}
