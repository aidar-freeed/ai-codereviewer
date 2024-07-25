package com.adins.mss.base.personalization;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;

/**
 * Helper class which help to pick picture from gallery or camera to apply as profile photo or cover photo. This class
 * pick a picture from user chosen application for camera or gallery, and save the picture to user database
 *
 * @author glen.iglesias
 */
public class Personalization {

//	public static final int TYPE_PROF_PICT = 1;
//	public static final int TYPE_COVER_PICT = 2;

    public static final String FAILED_SIZE_LIMIT = "Image size exceeded maximum allowed limit";
    public static final String FAILED_CONNECTION = "Connection to server failed";
    public Personalization() {
    }

    /**
     * Method to update photo, including cropping image, store to database, and, on upcoming version, send to server.
     * <br>Method will call PersonalizationHandler on success or fail
     *
     * @param context
     * @param image
     * @param imageType type of image to be updated, either profile picture or cover picture
     * @param handler   a PersonalizationHandler to whom updatePhoto will call on success or failure
     */
    public static void updatePhoto(Context context, byte[] image, ImageType imageType, PersonalizationHandler handler) {
        //TODO use cropping function
        byte[] croppedImage = image;
        int sizeLimit = GlobalData.getSharedGlobalData().getMaxPhotoSize();
        if (croppedImage.length > sizeLimit) {
            handler.onExceedSizeLimit(croppedImage, imageType, sizeLimit);
        } else {
            //process picture
            savePicture(context, croppedImage, imageType);
            //no need to send picture to server yet
            handler.onUpdateSuccess(croppedImage, imageType);
        }
    }

    /**
     * Save image to database and update current user data. Use updatePhoto() for more complete implementation like cropping and sending to server
     *
     * @param context
     * @param image
     * @param imageType type of image to be updated, either profile picture or cover picture
     */
    public static void savePicture(Context context, byte[] image, ImageType imageType) {
        User currentActiveUser = GlobalData.getSharedGlobalData().getUser();
        if (imageType == ImageType.TYPE_PROF_PICT) {
            currentActiveUser.setImage_profile(image);
        } else {
            currentActiveUser.setImage_cover(image);
        }
        UserDataAccess.update(context, currentActiveUser);
    }

    /**
     * A method to update profile picture by uploading it to server
     *
     * @param context
     * @param profPicture
     * @throws Exception
     * @deprecated use updatePhoto() instead
     */
    @SuppressWarnings("unused")
    private static Object changeProfilePicture(Context context, byte[] profPicture) throws Exception {
        // TODO
        //crop to fit
        byte[] croppedImage = profPicture;
//		//save to GlobalData and DB
//		User currentActiveUser = GlobalData.getSharedGlobalData().getUser();
//		currentActiveUser.setImage_profile(croppedImage);
//		UserDataAccess.update(context, currentActiveUser);

        //send to server
        sendPicture(context, croppedImage, ImageType.TYPE_PROF_PICT);
        return false;
    }

    /**
     * A method to update cover picture by uploading it to server
     *
     * @param context
     * @param coverPicture
     * @return
     * @deprecated use updatePhoto() instead, hide by changing access to private only
     */
    @SuppressWarnings("unused")
    private static Object changeCoverPicture(Context context, byte[] coverPicture) {
        // TODO
        //crop to fit
        byte[] croppedImage = coverPicture;
//		//save to GlobalData and DB
//		User currentActiveUser = GlobalData.getSharedGlobalData().getUser();
//		currentActiveUser.setImage_profile(croppedImage);
//		UserDataAccess.update(context, currentActiveUser);

        //send to server
        sendPicture(context, croppedImage, ImageType.TYPE_PROF_PICT);
        return false;
    }

    //=== Deprecated or Unpublished ===//

    /**
     * Method upload picture to server
     *
     * @param image
     * @param imageType
     * @return
     * @deprecated MSS save profile picture only on local device
     */
    private static Object sendPicture(Context context, byte[] image, ImageType imageType) {
        String url = "";

        // TODO
        switch (imageType) {
            case TYPE_COVER_PICT:
                url = GlobalData.getSharedGlobalData().getUrlPersonalization();
                ;
                break;
            case TYPE_PROF_PICT:
                url = GlobalData.getSharedGlobalData().getUrlPersonalization();
                ;
                break;
            default:
                url = "";
                break;
        }

        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);

        HttpConnectionResult result = null;
        try {
            result = httpConn.requestToServer(url, "");
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }

        if (result != null) {
            //TODO
            if (result.isOK()) {
                //save to DB

                if (imageType == ImageType.TYPE_PROF_PICT) {

                }
            } else {

            }
        }
        return null;
    }

    public enum ImageType {
        TYPE_PROF_PICT,
        TYPE_COVER_PICT
    }

    public interface PersonalizationHandler {
        /**
         * Called when update is successful
         *
         * @param image     image which was successfully updated
         * @param imageType image type of updated image
         */
        public void onUpdateSuccess(byte[] image, ImageType imageType);

        /**
         * Called when update failed because of exceeded size limit
         *
         * @param image     image which update is requested and failed
         * @param sizeLimit size limit which was exceeded
         * @param imageType image type of updated image
         */
        public void onExceedSizeLimit(byte[] image, ImageType imageType, int sizeLimit);

        //TODO check on login or other place, if HttpConnectionResult is the correct type to return
//		/**
//		 * Called when update failed because of connection failure
//		 * @param image image which update is requested and failed
//		 * @param failedResult HttpConnectionResult of failed connection attempt
//		 * @param imageType image type of updated image
//		 */
//		public void onConnetionFailed(byte[] image, ImageType imageType, HttpConnectionResult failedResult);
    }

}
