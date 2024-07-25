package com.adins.mss.foundation.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.UserClone;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CroppingImage {

    private CroppingImage() {
    }

    /**
     * Method for begin Crop from Activity
     *
     * @param source   - Uri Source
     * @param activity - Activity
     */
    public static void beginCrop(Uri source, Activity activity) {
        Uri outputUri = Uri.fromFile(new File(activity.getFilesDir(), "cropped"));
        new Crop(source).output(outputUri).asSquare().start(activity);
    }

    /**
     * Method for begin Crop from Activity
     *
     * @param source       - Uri Source
     * @param outputSource - Uri Output Source
     * @param activity     - Activity
     */
    public static void beginCropInActivity(Uri source, Uri outputSource, Activity activity) {
        new Crop(source).output(outputSource).asSquare().start(activity);
    }

    /**
     * Method for begin Crop from Activity (for Sample)
     *
     * @param activity
     */
    public static void beginCrop(Activity activity) {
        Uri outputUri = Uri.fromFile(new File(activity.getFilesDir(), "cropped"));
        new Crop(outputUri).output(outputUri).asSquare().start(activity);
    }

    public static void beginCrop(Activity activity, byte[] byte_image) {
        FileOutputStream out = null;
        File filename = new File(activity.getFilesDir(), "cropped");
        Uri outputUri = Uri.fromFile(filename);
        try {
            out = new FileOutputStream(filename);
            Bitmap bm = BitmapFactory.decodeByteArray(byte_image, 0, byte_image.length);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new Crop(outputUri).output(outputUri).asSquare().start(activity);
    }

    /**
     * Method for begin Crop from Activity from byte[] data
     *
     * @param source     - Uri source
     * @param activity   - Activity
     * @param byte_image - byte[] byte image
     */
    public static void beginCrop(Uri source, Activity activity, byte[] byte_image) {
        FileOutputStream out = null;
        File filename = new File(activity.getFilesDir(), "cropped");
        Uri outputUri = Uri.fromFile(filename);
        try {
            out = new FileOutputStream(filename);
            Bitmap bm = BitmapFactory.decodeByteArray(byte_image, 0, byte_image.length);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new Crop(source).output(outputUri).asSquare().start(activity);
    }

    /**
     * Method for begin Crop from Fragment
     *
     * @param source       - Uri source
     * @param outputSource - Uri output source
     * @param context      - context
     * @param fragment     - Fragment
     */
    public static void beginCropInFragment(Uri source, Uri outputSource, Context context, Fragment fragment) {
        new Crop(source).output(outputSource).asSquare().start(context, fragment);
    }

    /**
     * Method for begin Crop from Fragment for change Profile Picture
     *
     * @param source
     * @param context
     * @param fragment
     */
    public static void beginCropImgProfile(Uri source, Context context, Fragment fragment) {
        new Crop(source).output(source).asSquare().start(context, fragment);
    }

    /**
     * Method for begin Crop from Fragment for change Profile Picture by byte[]
     *
     * @param context
     * @param fragment
     * @param byte_image
     */
    public static void beginCropImgProfile(Context context, Fragment fragment, byte[] byte_image) {
        File filename = new File(context.getFilesDir(), "imgProfile");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            Bitmap bm = BitmapFactory.decodeByteArray(byte_image, 0, byte_image.length);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri outputUri = Uri.fromFile(filename);
        new Crop(outputUri).output(outputUri).asSquare().start(context, fragment);
    }


    /**
     * Method for begin Crop from Fragment for change Header Picture
     *
     * @param source
     * @param context
     * @param fragment
     */
    public static void beginCropImgHeader(Uri source, Context context, Fragment fragment) {
        new Crop(source).output(source).withAspect(16, 10).start(context, fragment);
    }

    /**
     * Method for begin Crop from Fragment for change Header Picture
     *
     * @param context
     * @param fragment
     * @param byte_image
     */
    public static void beginCropImgHeader(Context context, Fragment fragment, byte[] byte_image) {
        File filename = new File(context.getFilesDir(), "imgHeader");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            Bitmap bm = BitmapFactory.decodeByteArray(byte_image, 0, byte_image.length);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri outputUri = Uri.fromFile(filename);
        new Crop(outputUri).output(outputUri).asSquare().start(context, fragment);
    }

    /**
     * Method for handle Croping
     *
     * @param resultCode
     * @param result
     * @param activity
     * @return
     */
    public static Uri handleCrop(int resultCode, Intent result, Activity activity) {
        Uri outputUri = null;
        if (resultCode == activity.RESULT_OK) {
            outputUri = Crop.getOutput(result);
        } else if (resultCode == Crop.RESULT_ERROR && Global.IS_DEV) {
            Log.i("CropingImage",Crop.getError(result).getMessage());
        }
        return outputUri;
    }

    public static void handleCropHeader(int resultCode, Intent result, Activity activity) {
        Uri outputUri = null;
        if (resultCode == activity.RESULT_OK) {
            outputUri = Crop.getOutput(result);
            File imgFile = new File(outputUri.getPath());
            byte[] byteImage = Utils.pathBitmapToByte(imgFile);
            UserClone user = new UserClone(GlobalData.getSharedGlobalData().getUser(), false);
            user.setImage_cover(byteImage);
            UserDataAccess.addOrReplace(activity.getApplicationContext(), user);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(activity, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void handleCropProfile(int resultCode, Intent result, Activity activity) {
        if (resultCode == activity.RESULT_OK) {
            File imgFile = new File(activity.getFilesDir() + "/imgHeader");
            byte[] byteImage = Utils.pathBitmapToByte(imgFile);
            UserClone user = new UserClone(GlobalData.getSharedGlobalData().getUser(), false);

            user.setImage_profile(byteImage);
            UserDataAccess.addOrReplace(activity.getApplicationContext(), user);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Log.i("CropingImage",Crop.getError(result).getMessage());
            Toast.makeText(activity, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
