package com.adins.mss.foundation.UserHelp.Bean;

public class UserHelpProperties {
    private String text;
    private int sequence;
    private boolean square;
    private boolean recycled;
    private int viewHolderPos;

    public UserHelpProperties(){ }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSquare() {
        return square;
    }

    public void setSquare(boolean square) {
        this.square = square;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public boolean isRecycled() {
        return recycled;
    }

    public void setRecycled(boolean recycled) {
        this.recycled = recycled;
    }

    public int getViewHolderPos() {
        return viewHolderPos;
    }

    public void setViewHolderPos(int viewHolderPos) {
        this.viewHolderPos = viewHolderPos;
    }
}
