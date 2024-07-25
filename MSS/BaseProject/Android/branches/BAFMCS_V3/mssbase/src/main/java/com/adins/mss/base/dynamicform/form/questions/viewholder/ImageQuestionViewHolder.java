package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.form.questions.ImageViewerActivity;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.base.timeline.MapsViewer;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.camerainapp.CameraActivity;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gigin.ginanjar on 01/09/2016.
 */
public class ImageQuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String JOB_MH = "JOB MH";

    public QuestionView mView;
    public TextView mQuestionLabel;
    public TextView mQuestionAnswer;
    public ImageView mImageAnswer;
    public ImageView mThumbLocation;
    public QuestionBean bean;
    public FragmentActivity mActivity;
    public OnQuestionClickListener mListener;
    private int group;
    private int position;
    private int quality;
    private User user;

    public ImageQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionImageLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionImageLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionImageAnswer);
        mThumbLocation = (ImageView) itemView.findViewById(R.id.imgLocationAnswer);
        mImageAnswer = (ImageView) itemView.findViewById(R.id.imgPhotoAnswer);
    }

    public ImageQuestionViewHolder(View itemView, FragmentActivity activity, OnQuestionClickListener listener) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionImageLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionImageLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionImageAnswer);
        mThumbLocation = (ImageView) itemView.findViewById(R.id.imgLocationAnswer);
        mImageAnswer = (ImageView) itemView.findViewById(R.id.imgPhotoAnswer);
        mActivity = activity;
        mListener = listener;
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir;
        File image = null;
        try {
            storageDir = context.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists()) {
                storageDir.mkdir();
            }
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing ", e);
        }
        if(image == null)
            return image;

        DynamicFormActivity.setmCurrentPhotoPath(image.getAbsolutePath());
        return image;
    }

    public void bind(final QuestionBean item, int group, int number) {
        bean = item;
        this.group = group;
        position = number - 1;
        String qLabel = number + ". " + bean.getQuestion_label();
        user = GlobalData.getSharedGlobalData().getUser();

        mQuestionLabel.setText(qLabel);
        final byte[] img = bean.getImgAnswer();
        mImageAnswer.setOnClickListener(this);
        mThumbLocation.setOnClickListener(this);

        if (img != null && img.length > 0) {
            if (Tool.isHaveLocation(bean.getAnswer_type())) {
                mThumbLocation.setVisibility(View.VISIBLE);
                mThumbLocation.setImageResource(R.drawable.ic_absent);
            } else {
                mThumbLocation.setVisibility(View.GONE);
            }
            new BitmapWorkerTask(mImageAnswer).execute(bean);
            mQuestionAnswer.setVisibility(View.VISIBLE);
            mQuestionAnswer.setText(bean.getAnswer());
        } else {
            if(!JOB_MH.equalsIgnoreCase(user.getFlag_job())){
                mImageAnswer.setImageResource(R.drawable.icon_camera);
            }else{
                mImageAnswer.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            if (Tool.isHaveLocation(bean.getAnswer_type())) {
                mThumbLocation.setVisibility(View.VISIBLE);
                mThumbLocation.setImageResource(R.drawable.ic_absent);
            } else {
                mThumbLocation.setVisibility(View.GONE);
            }
            mQuestionAnswer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgPhotoAnswer) {
            mListener.onCapturePhotoClick(bean, group, position);
            if (bean.getImgAnswer() != null && Utils.byteToBitmap(bean.getImgAnswer()) != null) {
                quality = Utils.picQuality;
                if (bean.getImg_quality() != null && Global.IMAGE_HQ.equalsIgnoreCase(bean.getImg_quality()))
                    quality = Utils.picHQQuality;

                if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval() || JOB_MH.equalsIgnoreCase(user.getFlag_job())) {
                    // if form in survey verif / approval menu, then preview
                    try {
                        Global.getSharedGlobal().setIsViewer(true);
                        Bundle extras = new Bundle();
                        extras.putByteArray(ImageViewerActivity.BUND_KEY_IMAGE, bean.getImgAnswer());
                        extras.putInt(ImageViewerActivity.BUND_KEY_IMAGE_QUALITY, quality);
                        extras.putBoolean(ImageViewerActivity.BUND_KEY_IMAGE_ISVIEWER, Global.getSharedGlobal().getIsViewer());
                        Intent intent = new Intent(mActivity, ImageViewerActivity.class);
                        intent.putExtras(extras);
                        mActivity.startActivityForResult(intent, Global.REQUEST_EDIT_IMAGE);

                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage(mActivity.getString(R.string.picture_option));
                    builder.setCancelable(true);
                    builder.setPositiveButton(mActivity.getString(R.string.btnView), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                Global.getSharedGlobal().setIsViewer(!DynamicFormActivity.isAllowImageEdit());

                                //Since intent .putByteArray cannot pass data more than 500kb, use this
                                Bitmap bitmap = Utils.byteToBitmap(bean.getImgAnswer());
                                String filePath = tempImageLocation(mActivity, bitmap, "imageTemporary");

                                Bundle extras = new Bundle();
                                extras.putString(ImageViewerActivity.BUND_KEY_IMAGE_TEMP_LOCATION, filePath);
                                extras.putInt(ImageViewerActivity.BUND_KEY_IMAGE_QUALITY, quality);
                                extras.putBoolean(ImageViewerActivity.BUND_KEY_IMAGE_ISVIEWER, Global.getSharedGlobal().getIsViewer());
                                Intent intent = new Intent(mActivity, ImageViewerActivity.class);
                                intent.putExtras(extras);
                                mActivity.startActivityForResult(intent, Global.REQUEST_EDIT_IMAGE);

                            } catch (Exception e) {
                                FireCrash.log(e);
                            }
                        }
                    });
                    builder.setNeutralButton(mActivity.getString(R.string.btnRetake), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            openCameraApp(mActivity);
                        }
                    });
                    builder.setNegativeButton(mActivity.getString(R.string.btnDelete), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            bean.setImgAnswer(null);
                            bean.setAnswer(null);
                            bean.setImgTimestamp(null);
                            if (null != bean.getLocationInfo()) {
                                bean.setLocationInfo(null);
                            }
                            bean.setAnswer("");
                            mImageAnswer.setImageResource(R.drawable.icon_camera);
                            mQuestionAnswer.setText("");
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                }
            } else {
                if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval()) {
                    try {
                        Global.getSharedGlobal().setIsViewer(true);
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                } else {
                    if(!JOB_MH.equalsIgnoreCase(user.getFlag_job())) {
                        openCameraApp(mActivity);
                    }
                }
            }
        } else if (id == R.id.imgLocationAnswer) {
            if (bean.getImgAnswer() != null) {
                try {
                    if (bean.getLocationInfo() != null) {
                        String lat = bean.getLocationInfo().getLatitude();
                        String lng = bean.getLocationInfo().getLongitude();
                        int acc = bean.getLocationInfo().getAccuracy();
                        Intent intent = new Intent(mActivity, MapsViewer.class);
                        intent.putExtra("latitude", lat);
                        intent.putExtra("longitude", lng);
                        intent.putExtra("accuracy", acc);
                        mActivity.startActivity(intent);
                    } else {
                        Toast.makeText(mActivity, mActivity.getString(R.string.coordinat_not_available),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    String lat = bean.getLatitude();
                    String lng = bean.getLongitude();
                    if (lat != null && lng != null) {
                        Intent intent = new Intent(mActivity, MapsViewer.class);
                        intent.putExtra("latitude", lat);
                        intent.putExtra("longitude", lng);
                        mActivity.startActivity(intent);
                    } else {
                        Toast.makeText(mActivity, mActivity.getString(R.string.coordinat_not_available),
                                Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                if (!DynamicFormActivity.getIsVerified() && !DynamicFormActivity.getIsApproval()) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.take_foto_first),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void openCameraApp(FragmentActivity mActivity) {
        if (GlobalData.getSharedGlobalData().isUseOwnCamera()) {
            int quality = Utils.picQuality;
            int thumbHeight = Utils.picHeight;
            int thumbWidht = Utils.picWidth;

            if (bean.getImg_quality() != null && Global.IMAGE_HQ.equalsIgnoreCase(bean.getImg_quality())){
                    thumbHeight = Utils.picHQHeight;
                    thumbWidht = Utils.picHQWidth;
                    quality = Utils.picHQQuality;
                }

            Intent intent = new Intent(mActivity, CameraActivity.class);
            intent.putExtra(CameraActivity.PICTURE_WIDTH, thumbWidht);
            intent.putExtra(CameraActivity.PICTURE_HEIGHT, thumbHeight);
            intent.putExtra(CameraActivity.PICTURE_QUALITY, quality);
            if(bean.getAnswer_type().equals(Global.AT_ID_CARD_PHOTO))
                intent.putExtra(CameraActivity.NEED_BORDER, true);

            mActivity.startActivityForResult(intent, Utils.REQUEST_IN_APP_CAMERA);
        } else {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(mActivity.getApplicationContext());
                    } catch (IOException ex) {
                        FireCrash.log(ex);
                    }
                    if (photoFile != null) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        mActivity.startActivityForResult(intent, Utils.REQUEST_CAMERA);
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }

    class BitmapWorkerTask extends AsyncTask<QuestionBean, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private byte[] data;
        private int[] resolusi;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(QuestionBean... params) {
            QuestionBean bean = params[0];
            data = bean.getImgAnswer();
            if(data !=null) {
                Bitmap bm = Utils.byteToBitmap(data);
                if (bm == null) return null;
                resolusi = new int[2];
                resolusi[0] = bm.getWidth();
                resolusi[1] = bm.getHeight();
                int[] res = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
                Bitmap thumbnail = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
                return thumbnail;
            } else{
                return null;
            }
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
            Utility.freeMemory();
        }
    }

    private static String tempImageLocation(Context context, Bitmap bitmap, String name) {

        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        try( OutputStream os = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error while writing file", e);
        }

        return imageFile.getAbsolutePath();
    }
}
