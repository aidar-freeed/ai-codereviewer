package com.adins.mss.foundation.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.avatar.AvatarUploadRequestJson;
import com.adins.mss.base.avatar.AvatarUploader;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.timeline.Constants;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.edmodo.cropper.CropImageView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * @author gigin.ginanjar
 */
public class CroppingImageActivity extends Activity implements AvatarUploader.AvatarUploadHandler {

    // Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    private static final int ON_TOUCH = 1;
    public static String BUND_KEY_ABSOLUTEPATH = "ABSOLUTE PATH";
    public ExifInterface exif = null;
    Bitmap croppedImage;
    InputStream is = null;
    Bitmap bitmap = null;
    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

    private AvatarUploader avatarUploader;

    private FirebaseAnalytics screenName;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cropping_image_layout);
        String path = getIntent().getStringExtra(BUND_KEY_ABSOLUTEPATH);
        final CropImageView cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropImageView.setFixedAspectRatio(true);
        Uri sourceUri = Uri.parse(path);
        final Button rotateButton = (Button) findViewById(R.id.Button_rotate);
        final Button cancelButton = (Button) findViewById(R.id.Button_cancel);
        final Button cropButton = (Button) findViewById(R.id.Button_crop);
        avatarUploader = new AvatarUploader(this,this);
        screenName = FirebaseAnalytics.getInstance(this);

        try {
            int sampleSize = calculateBitmapSampleSize(sourceUri);
            is = getContentResolver().openInputStream(sourceUri);
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = sampleSize;
            bitmap = BitmapFactory.decodeStream(is, null, option);
            bitmap = Utils.resizeImageByPath(bitmap);
        } catch (IOException e) {
            Logger.e("Error reading image: " + e.getMessage(), e);
            Toast.makeText(getApplicationContext(), getString(R.string.error_reading), Toast.LENGTH_SHORT).show();
            rotateButton.setEnabled(false);
            rotateButton.setClickable(false);
            cropButton.setEnabled(false);
            cropButton.setClickable(false);
        } catch (OutOfMemoryError e) {
            Logger.e("OOM reading image: " + e.getMessage(), e);
            rotateButton.setEnabled(false);
            rotateButton.setClickable(false);
            cropButton.setEnabled(false);
            cropButton.setClickable(false);
        } catch (NullPointerException e) {
            Logger.e("Null pointer image: " + e.getMessage(), e);
            Toast.makeText(getApplicationContext(), getString(R.string.error_image_invalid), Toast.LENGTH_SHORT).show();
            rotateButton.setEnabled(false);
            rotateButton.setClickable(false);
            cropButton.setEnabled(false);
            cropButton.setClickable(false);
        }


        try {
            cropImageView.setGuidelines(1);
            ExifFromGallery sourceExif = new ExifFromGallery(sourceUri, this);
            exif = sourceExif.getExif();
            cropImageView.setImageBitmap(bitmap, exif);
        } catch (Exception e) {
            FireCrash.log(e);
            // TODO: handle exception
            try {
                exif = new ExifInterface(sourceUri.getPath());
                cropImageView.setImageBitmap(bitmap, exif);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                cropImageView.setImageBitmap(bitmap);
            }
        }

        rotateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
            }
        });
        if (Constants.flag_edit == 0) {
            cropImageView.setAspectRatio(16, 10);
        } else if (Constants.flag_edit == 1) {
            cropImageView.setAspectRatio(1, 1);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }

        });

        cropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                croppedImage = cropImageView.getCroppedImage();
                if(croppedImage == null){
                    finish();
                }
                //resized bitmap
                croppedImage = Utils.getResizedBitmap(croppedImage,160,160);

                if (Constants.flag_edit == 0) {
                    String temp_uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                    byte[] avatarByte = Utils.bitmapToByte(croppedImage);
                    User user = UserDataAccess.getOne(getApplicationContext(), temp_uuid_user);
                    user.setImage_cover(avatarByte);
                    UserDataAccess.addOrReplace(getApplicationContext(), user);
                    GlobalData.getSharedGlobalData().setUser(user);
                    uploadAvatar(avatarByte);
                } else {
                    byte[] avatarByte = Utils.bitmapToByte(croppedImage);
                    uploadAvatar(avatarByte);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this, getString(R.string.screen_name_cropping_image), null);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }

    private void uploadAvatar(byte[] avatarImg){
        AvatarUploadRequestJson avatarUploadRequestJson = new AvatarUploadRequestJson();
        avatarUploadRequestJson.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        avatarUploadRequestJson.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
        avatarUploadRequestJson.setBase64Img(Utils.byteToBase64(avatarImg));
        avatarUploader.uploadAvatar(avatarUploadRequestJson);
    }

    private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
        InputStream is = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            is = getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options); // Just get image size
        } finally {
        }

        int maxSize = 1024;
        int sampleSize = 1;
        while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
            sampleSize = sampleSize << 1;
        }
        return sampleSize;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            is.close();
            if (bitmap != null)
                bitmap.recycle();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onUploadSuccess(String resultmsg, String image) {
        String temp_uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        User user = UserDataAccess.getOne(getApplicationContext(), temp_uuid_user);

        byte[] avatarByte = Utils.base64ToByte(image);

        user.setImage_profile(avatarByte);
        UserDataAccess.addOrReplace(getApplicationContext(), user);
        GlobalData.getSharedGlobalData().setUser(user);

        Toast.makeText(this, resultmsg, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onUploadFail(String errormsg) {
        if(GlobalData.isRequireRelogin()){
            DialogManager.showForceExitAlert(this,getString(com.adins.mss.base.R.string.msgLogout));
            return;
        }

        Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
        finish();
    }
}
