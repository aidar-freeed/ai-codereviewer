package com.adins.mss.foundation.camerainapp.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.adins.mss.base.AppContext;

import java.util.HashSet;

/**
 * Created by angga.permadi on 8/4/2016.
 */
public class MovementDetector implements SensorEventListener {
    private static MovementDetector mInstance;
    private HashSet<Listener> mListeners = new HashSet<>();
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float gravity[] = new float[3];
    private float linear_acceleration[] = new float[3];

    private MovementDetector() {
    }

    public static MovementDetector getInstance() {
        if (mInstance == null) {
            mInstance = new MovementDetector();
            mInstance.init();
        }
        return mInstance;
    }

    private void init() {
        sensorMan = (SensorManager) AppContext.getInstance().getApplicationContext().
                getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void start() {
        getInstance();
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorMan.unregisterListener(this);
        sensorMan = null;
        mInstance = null;

        gravity = new float[3];
        linear_acceleration = new float[3];
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    /* (non-Javadoc)
     * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float alpha = 0.8f;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            float x = linear_acceleration[0];
            float y = linear_acceleration[1];
            float z = linear_acceleration[2];

            float diff = (float) Math.sqrt(x * x + y * y + z * z);
            if (diff > 1.5) {
                Logger.d(this, "Device motion detected!!!!, acceleration : " + diff);
                for (Listener listener : mListeners) {
                    listener.onMotionDetected(event, diff);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    public interface Listener {
        void onMotionDetected(SensorEvent event, float acceleration);
    }
}

