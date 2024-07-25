package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.form.questions.ImageViewerActivity;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.ArrayList;

public class ReviewLuOnlineQuestionViewHolder extends RecyclerView.ViewHolder {
    private final TextView mQuestionLabel;
    private final ImageView imgPhotos;
    private final Activity mActivity;
    private QuestionBean bean;
    public OnQuestionClickListener listener;

    public ReviewLuOnlineQuestionViewHolder(View itemView, FragmentActivity activity, OnQuestionClickListener listener) {
        super(itemView);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        imgPhotos = (ImageView) itemView.findViewById(R.id.imgPhotoLuOnlineAnswer);
        mActivity = activity;
        this.listener = listener;
    }

    public void bind(final QuestionBean item, final int group, final int number) {
        bean = item;
        String qLabel = number + ". " + bean.getQuestion_label();
        mQuestionLabel.setText(qLabel);

        // Keep Data for not happen moving to Other Question because do Scrolling
        final LinearLayout layoutListDocument = (LinearLayout) itemView.findViewById(R.id.layoutListDocument);
        if (bean.getListImgByteArray() != null) {
            // To avoid trigger many times add photos on 'layoutListDocument'
            layoutListDocument.removeAllViews();

            // Show all list document have been saved on QuestionBean
            if (bean.getListImgByteArray().size() > 0) {
                imgPhotos.setVisibility(View.GONE);
                ArrayList<byte[]> beanListLuDocKeep = bean.getListImgByteArray();
                for (int index = 0; index < beanListLuDocKeep.size(); index++) {
                    ImageView imgView = new ImageView(mActivity);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);

                    final byte[] imageByteArray = beanListLuDocKeep.get(index);
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);

                    layoutParams.setMargins(2, 8, 2, 8);
                    layoutParams.gravity = Gravity.CENTER;
                    imgView.setLayoutParams(layoutParams);
                    imgView.setImageBitmap(bitmap);
                    layoutListDocument.addView(imgView);

                    imgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Global.isViewer = true;
                            Bundle extras = new Bundle();
                            extras.putByteArray(ImageViewerActivity.BUND_KEY_IMAGE, imageByteArray);
                            extras.putInt(ImageViewerActivity.BUND_KEY_IMAGE_QUALITY, Utils.picQuality);
                            extras.putBoolean(ImageViewerActivity.BUND_KEY_IMAGE_ISVIEWER, Global.isViewer);
                            Intent intent = new Intent(mActivity, ImageViewerActivity.class);
                            intent.putExtras(extras);
                            mActivity.startActivity(intent);
                        }
                    });
                }
                layoutListDocument.setVisibility(View.VISIBLE);
            }
        } else {
            layoutListDocument.setVisibility(View.GONE);
            imgPhotos.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        imgPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onReviewClickListener(bean, group, number - 1);
            }
        });
    }

}
