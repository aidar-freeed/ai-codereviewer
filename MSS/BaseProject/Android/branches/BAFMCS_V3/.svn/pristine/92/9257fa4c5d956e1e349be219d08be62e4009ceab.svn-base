package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.form.questions.DrawingCanvasActivity;
import com.adins.mss.base.dynamicform.form.questions.ImageViewerActivity;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

import java.lang.ref.WeakReference;

/**
 * Created by gigin.ginanjar on 01/09/2016.
 */
public class DrawingQuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public QuestionView mView;
    public TextView mQuestionLabel;
    public ImageView mImageEdit;
    public QuestionBean bean;
    public FragmentActivity mActivity;
    public OnQuestionClickListener mListener;
    private int group;
    private int position;
    private User user;
    
    public DrawingQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionDrawingLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionDrawingLabel);
        mImageEdit = (ImageView) itemView.findViewById(R.id.imgDrawing);
    }

    public DrawingQuestionViewHolder(View itemView, FragmentActivity activity, OnQuestionClickListener listener) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionDrawingLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionDrawingLabel);
        mImageEdit = (ImageView) itemView.findViewById(R.id.imgDrawing);
        mActivity = activity;
        mListener = listener;
    }

    public void bind(final QuestionBean item, int group, int number) {
        bean = item;
        this.group = group;
        position = number - 1;
        String qLabel = number + ". " + bean.getQuestion_label();
        user = GlobalData.getSharedGlobalData().getUser();

        mQuestionLabel.setText(qLabel);

        final byte[] img = bean.getImgAnswer();
        mImageEdit.setOnClickListener(this);
        if (img != null && img.length > 0) {
            new BitmapWorkerTask(mImageEdit).execute(bean);
        } else {
            if (!user.getFlag_job().equalsIgnoreCase("JOB MH")){
                mImageEdit.setImageResource(android.R.drawable.ic_menu_edit);
            }else{
                mImageEdit.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval() || user.getFlag_job().equalsIgnoreCase("JOB MH")) {
            if(bean.getImgAnswer() != null){
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
        }else{
            mListener.onEditDrawingClick(bean, group, position);
            Intent intent = new Intent(mActivity, DrawingCanvasActivity.class);
            DrawingCanvasActivity.bean = new QuestionBean(bean);
            if (bean.getImgAnswer() != null)
                DrawingCanvasActivity.bean.setImgAnswer(bean.getImgAnswer());
            mActivity.startActivityForResult(intent, Global.REQUEST_DRAWING_QUESTION);
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
