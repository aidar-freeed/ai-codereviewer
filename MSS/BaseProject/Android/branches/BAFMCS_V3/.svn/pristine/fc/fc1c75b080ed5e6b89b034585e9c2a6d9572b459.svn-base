package com.adins.mss.foundation.camerainapp.helper;

import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.Size;

import com.adins.mss.foundation.formatter.Tool;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CamParaUtil {
    private static CamParaUtil myCamPara = null;
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();

    private CamParaUtil() {
    }

    public static CamParaUtil getInstance() {
        if (myCamPara == null) {
            myCamPara = new CamParaUtil();
            return myCamPara;
        } else {
            return myCamPara;
        }
    }

    public boolean equalRate(Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        return Math.abs(r - rate) <= 0.03;
    }

    /**
     * previewSizes
     *
     * @param params
     */
    public void printSupportPreviewSize(Camera.Parameters params) {
        List<Size> previewSizes = params.getSupportedPreviewSizes();
        for (int i = 0; i < previewSizes.size(); i++) {
            Size size = previewSizes.get(i);
            Logger.i(this, "previewSizes:width = " + size.width + " height = " + size.height);
        }

    }

    /**
     * pictureSizes
     *
     * @param params
     */
    public void printSupportPictureSize(Camera.Parameters params) {
        List<Size> pictureSizes = params.getSupportedPictureSizes();
        for (int i = 0; i < pictureSizes.size(); i++) {
            Size size = pictureSizes.get(i);
            Logger.i(this, "pictureSizes:width = " + size.width
                    + " height = " + size.height);
        }
    }

    /**
     * @param params
     */
    public void printSupportFocusMode(Camera.Parameters params) {
        List<String> focusModes = params.getSupportedFocusModes();
        for (String mode : focusModes) {
            Logger.i(this, "focusModes--" + mode);
        }
    }

    public int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                //Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public Size getPropPictureSize(List<Camera.Size> list, int minWidth, int minHeight) {
        Collections.sort(list, sizeComparator);

        int width = minWidth < minHeight ? minHeight : minWidth;
        double aspectRatio = Tool.getAspectRatio(minWidth,minHeight);
        int i = 0;
        for (Size s : list) {
            // TODO : cari berdasarkan aspect ratio
            if (s.width >= width && Tool.getAspectRatio(s.width,s.height) == aspectRatio) {
                Logger.i(this, "PictureSize : w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = i - 1; // ambil yang paling besar
        }

        Size size;
        if(i == list.size() - 1){
            size = list.get(i);
        }else {
            //Nendi: Find best aspect ratio
            size = list.get(i).width == width ? list.get(i) : list.get(i + 1).width == list.get(i + 1).height ? list.get(i) : list.get(i + 1);
        }
        return size;
    }

    public Size getPropPreviewSize(List<Camera.Size> list, int minWidth, int minHeight) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Size s : list) {
            if (s.width == minWidth && s.height == minHeight) {
                Logger.i(this, "PreviewSize : w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            float th = (float) minWidth / (float) minHeight;
            i = 0;
            for (Size s : list) {
                if ((s.width >= minWidth) && equalRate(s, th)) {
                    Logger.i(this, "PreviewSize : w = " + s.width + "h = " + s.height);
                    break;
                }
                i++;
            }

            if (i == list.size()) {
                i = -1;
                for (int a = list.size() - 1; a >= 0; a--) {
                    Size s = list.get(a);
                    if (equalRate(s, th)) {
                        Logger.i(this, "PreviewSize : w = " + s.width + "h = " + s.height);
                        i = list.indexOf(s);
                        break;
                    }
                }

                if (i == -1) {
                    i = list.size() - 1;
                }
            }
        }
        return list.get(i);
    }

    public class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Size lhs, Size rhs) {
            // TODO Auto-generated method stub
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }
}