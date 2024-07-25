package com.adins.mss.foundation.camera;

import android.hardware.Camera;
import android.hardware.Camera.Face;

import com.adins.mss.foundation.camerainapp.helper.Logger;

public class FaceDetectionListener implements android.hardware.Camera.FaceDetectionListener {

    @Override
    public void onFaceDetection(Face[] faces, Camera camera) {
        // TODO Auto-generated method stub
        if (faces.length > 0) {
            Logger.d("FaceDetection", "face detected: " + faces.length +
                    " Face 1 Location X: " + faces[0].rect.centerX() +
                    "Y: " + faces[0].rect.centerY());
        }
    }
}
