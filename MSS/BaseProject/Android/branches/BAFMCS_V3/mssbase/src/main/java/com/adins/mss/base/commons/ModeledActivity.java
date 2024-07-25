package com.adins.mss.base.commons;

/**
 * Created by Aditya Purwa on 1/6/2015.
 * Indicate that an activity is implementing a model based activity.
 */
public interface ModeledActivity<T> {
    /**
     * Get the model for the activity.
     *
     * @return The model.
     */
    public T getModel();

    /**
     * Set the model for the activity.
     *
     * @param model The model.
     */
    public void setModel(T model);
}
