package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.base.timeline.MapsViewer;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.LocationTagingView;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

/**
 * Created by gigin.ginanjar on 31/08/2016.
 */
public class LocationQuestionViewHolder extends RecyclerView.ViewHolder {
    public QuestionView mView;
    public TextView mQuestionLabel;
    public TextView mQuestionAnswer;
    public Button mButtonSetLocation;
    public ImageView mImageAnswer;
    public QuestionBean bean;
    public FragmentActivity mActivity;
    public OnQuestionClickListener mListener;
    private int group;
    private int position;

    public LocationQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionLocationLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionLocationLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionLocationAnswer);
        mButtonSetLocation = (Button) itemView.findViewById(R.id.btnSetLocation);
        mImageAnswer = (ImageView) itemView.findViewById(R.id.imgLocationAnswer);
    }

    public LocationQuestionViewHolder(View itemView, FragmentActivity activity, OnQuestionClickListener listener) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionLocationLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionLocationLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionLocationAnswer);
        mButtonSetLocation = (Button) itemView.findViewById(R.id.btnSetLocation);
        mImageAnswer = (ImageView) itemView.findViewById(R.id.imgLocationAnswer);
        mActivity = activity;
        mListener = listener;
    }

    public void bind(final QuestionBean item, final int group, int number) {
        bean = item;
        this.group = group;
        position = number - 1;
        String qLabel = number + ". " + bean.getQuestion_label();

        mQuestionLabel.setText(qLabel);
        View.OnClickListener listener = null;

        if (Global.AT_GPS.equals(bean.getAnswer_type())) {
            listener = new View.OnClickListener() {
                public void onClick(View v) {
                    LocationInfo info = Global.LTM.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
                    bean.setLocationInfo(info);
                    bean.setLocationInfo(info);
                    mQuestionAnswer.setVisibility(View.VISIBLE);
                    mQuestionAnswer.setText(LocationTrackingManager.toAnswerString(info));
                }
            };
        } else {
            listener = new View.OnClickListener() {
                public void onClick(View v) {
                    mListener.onSetLocationClick(bean, group, position);
                    Intent intent = new Intent(mActivity, LocationTagingView.class);
                    mActivity.startActivityForResult(intent, Global.REQUEST_LOCATIONTAGGING);
                }
            };
        }
        mImageAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.getAnswer() != null && bean.getAnswer().length() > 0) {
                    try {
                        String lat = bean.getLocationInfo().getLatitude();
                        String lng = bean.getLocationInfo().getLongitude();
                        int acc = bean.getLocationInfo().getAccuracy();
                        Intent intent = new Intent(mActivity, MapsViewer.class);
                        intent.putExtra("latitude", lat);
                        intent.putExtra("longitude", lng);
                        intent.putExtra("accuracy", acc);
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
                    Toast.makeText(mActivity, mActivity.getString(R.string.set_location),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        if (bean.getAnswer() != null && !bean.getAnswer().isEmpty()) {
            mQuestionAnswer.setVisibility(View.VISIBLE);
            mQuestionAnswer.setText(bean.getAnswer());
        }

        mButtonSetLocation.setEnabled(true);
        if (bean.isReadOnly())
            mButtonSetLocation.setEnabled(false);

        mButtonSetLocation.setOnClickListener(listener);
        if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval())
            mButtonSetLocation.setVisibility(View.GONE);
        else
            mButtonSetLocation.setVisibility(View.VISIBLE);

        try {
             DialogManager.showGPSAlert(mActivity);
        }catch (Exception e){
            FireCrash.log(e);
            e.printStackTrace();
        }
    }
}
