package com.services;

import androidx.annotation.Keep;

import com.adins.mss.base.util.GsonHelper;

/**
 * Created by angga.permadi on 5/10/2016.
 */
@Keep
public class MainServiceEvent {

    @Keep
    private State mState;

    public MainServiceEvent() {
    }

    public MainServiceEvent(State mState) {
        this.mState = mState;
    }

    public State getmState() {
        return mState;
    }

    public void setmState(State mState) {
        this.mState = mState;
    }

    @Override
    public String toString() {
        return GsonHelper.toJson(this);
    }

    public enum State {
        TASK_DATA_ACCESS_STOP_WAIT,
        TASK_DATA_ACCESS_WAIT
    }
}
