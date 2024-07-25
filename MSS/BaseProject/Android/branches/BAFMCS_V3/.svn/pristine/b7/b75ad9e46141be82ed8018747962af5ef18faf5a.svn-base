package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.depositreport.GetImageTask;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.form.questions.ImageViewerActivity;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.base.timeline.MapsViewer;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gigin.ginanjar on 06/09/2016.
 */
public class ReviewImageViewHolder extends RecyclerView.ViewHolder implements GetImageTask.GetImageData {
    public RelativeLayout layout;
    public TextView mLabelNo;
    public TextView mQuestionLabel;
    public TextView mQuestionAnswer;
    public ImageView mImageAnswer;
    public ImageView mThumbLocation;
    public QuestionBean bean;
    public FragmentActivity mActivity;
    public OnQuestionClickListener listener;

    public ReviewImageViewHolder(View itemView) {
        super(itemView);
        layout = (RelativeLayout) itemView.findViewById(R.id.imageReviewLayout);
        mLabelNo = (TextView) itemView.findViewById(R.id.questionNoLabel);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionImageLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionImageAnswer);
        mThumbLocation = (ImageView) itemView.findViewById(R.id.imgLocationAnswer);
        mImageAnswer = (ImageView) itemView.findViewById(R.id.imgPhotoAnswer);
    }

    public ReviewImageViewHolder(View itemView, FragmentActivity activity, OnQuestionClickListener listener) {
        super(itemView);
        layout = (RelativeLayout) itemView.findViewById(R.id.imageReviewLayout);
        mLabelNo = (TextView) itemView.findViewById(R.id.questionNoLabel);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionImageLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionImageAnswer);
        mThumbLocation = (ImageView) itemView.findViewById(R.id.imgLocationAnswer);
        mImageAnswer = (ImageView) itemView.findViewById(R.id.imgPhotoAnswer);
        mActivity = activity;
        this.listener = listener;
    }

    public void bind(final QuestionBean item, final int group, final int number) {
        bean = item;
        mLabelNo.setText(number + ".");
        String qLabel = bean.getQuestion_label();
        mQuestionLabel.setText(qLabel);
        String answerType = bean.getAnswer_type();
        setImageAnswer(answerType);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReviewClickListener(bean, group, number - 1);
            }
        });
        if (Tool.isHaveLocation(bean.getAnswer_type())) {
            mThumbLocation.setVisibility(View.VISIBLE);
            mThumbLocation.setImageResource(R.drawable.ic_absent);
        } else {
            mThumbLocation.setVisibility(View.GONE);
        }
        String qAnswer = bean.getAnswer();
        if (qAnswer != null && !qAnswer.isEmpty()) {
            mQuestionAnswer.setText(qAnswer);
        } else {
            mQuestionAnswer.setText("");
        }
    }

    private void setImageAnswer(String answerType) {
        bindImageAnswer();
    }

    private void bindImageAnswer() {
        byte[] img = bean.getImgAnswer();
        if (img != null && img.length > 0) {
            new BitmapWorkerTask(mImageAnswer).execute(bean);
            mImageAnswer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        Global.getSharedGlobal().setIsViewer(true);
                        Bundle extras = new Bundle();
                        extras.putByteArray(ImageViewerActivity.BUND_KEY_IMAGE, bean.getImgAnswer());
                        extras.putInt(ImageViewerActivity.BUND_KEY_IMAGE_QUALITY, Utils.picQuality);
                        extras.putBoolean(ImageViewerActivity.BUND_KEY_IMAGE_ISVIEWER, Global.getSharedGlobal().getIsViewer());
                        Intent intent = new Intent(mActivity, ImageViewerActivity.class);
                        intent.putExtras(extras);
                        mActivity.startActivity(intent);
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
            });
            mThumbLocation.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (bean.getLocationInfo() != null) {
                        try {
                            String lat = bean.getLocationInfo().getLatitude();
                            String lng = bean.getLocationInfo().getLongitude();
                            int accuracy = bean.getLocationInfo().getAccuracy();
                            Intent intent = new Intent(mActivity, MapsViewer.class);
                            intent.putExtra("latitude", lat);
                            intent.putExtra("longitude", lng);
                            intent.putExtra("accuracy", accuracy);
                            mActivity.startActivity(intent);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            String lat = bean.getLatitude();
                            String lng = bean.getLongitude();
                            Intent intent = new Intent(mActivity, MapsViewer.class);
                            intent.putExtra("latitude", lat);
                            intent.putExtra("longitude", lng);
                            mActivity.startActivity(intent);
                        }
                    } else {
                        Toast.makeText(mActivity, mActivity.getString(R.string.msgUnavaibleLocation),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            if (Tool.isHaveLocation(bean.getAnswer_type())) {
                mThumbLocation.setImageResource(R.drawable.ic_absent);
                mThumbLocation.setVisibility(View.VISIBLE);
            } else {
                mThumbLocation.setVisibility(View.GONE);
            }
        } else {
            if (CustomerFragment.isViewTask()) {
                mImageAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<NameValuePair> params = new ArrayList<>();
                        params.add(new BasicNameValuePair(Global.BUND_KEY_UUID_TASKH,
                                CustomerFragment.getHeader().getTaskH().getUuid_task_h()));
                        params.add(new BasicNameValuePair(Global.BUND_KEY_QUESTIONID, bean.getQuestion_id()));
                        if (null == bean.getImgAnswer()) {
                            new GetImageTask(mActivity, params, mImageAnswer, mQuestionAnswer, bean, ReviewImageViewHolder.this).execute();
                        } else {
                            bindImageAnswer();
                        }
                    }
                });
            }

            mImageAnswer.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public void getImageData(boolean data) {
        //EMPTY
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
            Bitmap bm = Utils.byteToBitmap(data);
            resolusi = new int[2];
            resolusi[0] = bm.getWidth();
            resolusi[1] = bm.getHeight();
            int[] res = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
            Bitmap thumbnail = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
            return thumbnail;
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
}
