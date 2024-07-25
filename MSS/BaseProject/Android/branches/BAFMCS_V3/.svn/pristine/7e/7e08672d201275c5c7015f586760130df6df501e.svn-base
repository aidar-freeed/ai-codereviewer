package com.adins.mss.foundation.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.audio.AudioRecord;
import com.adins.mss.foundation.dialog.gitonway.lib.ColorUtils;
import com.adins.mss.foundation.dialog.gitonway.lib.Effectstype;
import com.adins.mss.foundation.dialog.gitonway.lib.effects.BaseEffects;
import com.adins.mss.foundation.image.ImageViewer;

/*
 * Copyright 2014 litao
 * https://github.com/sd6352051/NiftyDialogEffects
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class NiftyDialogBuilder_Voice extends Dialog implements DialogInterface {

    private static Context tmpContext;
    private static int mOrientation = 1;
    private static NiftyDialogBuilder_Voice instance;
    private final String defTextColor = "#FFFFFFFF";
    private final String defDividerColor = "#11000000";
    private final String defMsgColor = "#FFFFFFFF";
    private final String defDialogColor = "#FF5f5f5f";
    private Effectstype type = null;
    private LinearLayout mLinearLayoutView;
    private LinearLayout mTaskListLayoutView;
    private LinearLayout mVerificationLayoutView;
    private LinearLayout mApprovalLayoutView;
    private RelativeLayout mRelativeLayoutView;
    private LinearLayout mLinearLayoutMsgView;
    private LinearLayout mLinearLayoutTopView;
    private FrameLayout mFrameLayoutCustomView;
    private ImageView mImageView;
    private AutoCompleteTextView mtxtSearch;
    private View mDialogView;
    private View mDivider;
    private TextView mTitle;
    private TextView mMessage;
    private TextView mTasklistChoice;
    private TextView mApprovallistChoice;
    private TextView mVerificationlistChoice;
    private ImageView mIcon;
    private Button mButton1;
    private Button mButton2;
    private int mDuration = -1;
    private boolean isCancelable = true;
    private ToggleButton btnVoiceNotes;
    private ImageButton btnPlay;
    private ImageButton btnStop;
    private LinearLayout playerLayout;
    private AudioRecord record;
    private TextView noVoiceNote;
    private SurveyHeaderBean header;
    private int MODE;

    public NiftyDialogBuilder_Voice(Context context) {
        super(context);
        init(context);

    }

    public NiftyDialogBuilder_Voice(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public static NiftyDialogBuilder_Voice getInstance(Context context) {

        if (instance == null || !tmpContext.equals(context)) {
            synchronized (NiftyDialogBuilder_Voice.class) {
                if (instance == null || !tmpContext.equals(context)) {
                    instance = new NiftyDialogBuilder_Voice(context, R.style.dialog_untran);
                }
            }
        }
        tmpContext = context;
        return instance;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }

    private void init(final Context context) {
        record = new AudioRecord(context);
        mDialogView = View.inflate(context, R.layout.dialog_layout_voice, null);

        mLinearLayoutView = (LinearLayout) mDialogView.findViewById(R.id.parentPanelT);
        mTaskListLayoutView = (LinearLayout) mDialogView.findViewById(R.id.ChoiceTaskList);
        mApprovalLayoutView = (LinearLayout) mDialogView.findViewById(R.id.ChoiceApprovalList);
        mVerificationLayoutView = (LinearLayout) mDialogView.findViewById(R.id.ChoiceVerificationlist);
        mRelativeLayoutView = (RelativeLayout) mDialogView.findViewById(R.id.mainlayout);
        mLinearLayoutTopView = (LinearLayout) mDialogView.findViewById(R.id.topPanelT);
        mLinearLayoutMsgView = (LinearLayout) mDialogView.findViewById(R.id.contentPanelT);
        mFrameLayoutCustomView = (FrameLayout) mDialogView.findViewById(R.id.customPanelT);

        mImageView = (ImageView) mDialogView.findViewById(R.id.imageViewT);
        mImageView.setOnTouchListener(new ImageViewer());

        mtxtSearch = (AutoCompleteTextView) mDialogView.findViewById(R.id.txtSearch);


        mTitle = (TextView) mDialogView.findViewById(R.id.alertTitleT);
        mMessage = (TextView) mDialogView.findViewById(R.id.messageT);
        mTasklistChoice = (TextView) mDialogView.findViewById(R.id.Choice1);
        mVerificationlistChoice = (TextView) mDialogView.findViewById(R.id.Choice2);
        mApprovallistChoice = (TextView) mDialogView.findViewById(R.id.Choice3);
        mIcon = (ImageView) mDialogView.findViewById(R.id.iconT);
        mDivider = mDialogView.findViewById(R.id.titleDividerT);
        mButton1 = (Button) mDialogView.findViewById(R.id.button1T);
        mButton2 = (Button) mDialogView.findViewById(R.id.button2T);
        playerLayout = (LinearLayout) mDialogView.findViewById(R.id.recorderLayout);
        btnVoiceNotes = (ToggleButton) mDialogView.findViewById(R.id.btnVoiceNotes);
        noVoiceNote = (TextView) mDialogView.findViewById(R.id.txtNoVoiceNote);
        btnPlay = (ImageButton) mDialogView.findViewById(R.id.btnPlay);
        btnStop = (ImageButton) mDialogView.findViewById(R.id.btnStop);
        setContentView(mDialogView);

        if (header.getVoice_note() != null) {
            btnVoiceNotes.setVisibility(View.GONE);
            playerLayout.setVisibility(View.VISIBLE);
        } else {
            btnVoiceNotes.setVisibility(View.GONE);
            noVoiceNote.setVisibility(View.VISIBLE);
        }

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                mLinearLayoutView.setVisibility(View.VISIBLE);
                if (type == null) {
                    type = Effectstype.Slidetop;
                }
                start(type);


            }
        });
        mRelativeLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCancelable) dismiss();
            }
        });


        btnVoiceNotes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (btnVoiceNotes.isChecked()) {
                    //start record
                    record.startRecording(v);
                    playerLayout.setVisibility(View.GONE);
                } else {
                    //stop record
                    record.stop(v);
                    playerLayout.setVisibility(View.VISIBLE);
                    try {
                        header.setVoice_note(record.saveAudioToByte());
                    } catch (Exception e) {
                        FireCrash.log(e);
                        header.setVoice_note(header.getVoice_note());
                    }
                }
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MODE == Global.MODE_SURVEY_TASK ||
                        MODE == Global.MODE_VIEW_SENT_SURVEY) {//
                    AudioRecord.playAudio(context, header.getVoice_note());
                } else {
                    record.play(v);
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                record.stopPlay(v);
            }
        });


    }

    public void toDefault() {
        mTitle.setTextColor(Color.parseColor(defTextColor));
        mDivider.setBackgroundColor(Color.parseColor(defDividerColor));
        mMessage.setTextColor(Color.parseColor(defMsgColor));
        mLinearLayoutView.setBackgroundColor(Color.parseColor(defDialogColor));
    }

    public NiftyDialogBuilder_Voice withTaskListChoice() {
        mTaskListLayoutView.setVisibility(View.VISIBLE);
        mLinearLayoutMsgView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialogBuilder_Voice setTaskListChoiceClick(View.OnClickListener click) {
        mTasklistChoice.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_Voice withVerificationListChoice() {
        mLinearLayoutMsgView.setVisibility(View.VISIBLE);
        mVerificationLayoutView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialogBuilder_Voice setVerificationListChoiceClick(View.OnClickListener click) {
        mVerificationlistChoice.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_Voice withApprovalListChoice() {
        mApprovalLayoutView.setVisibility(View.VISIBLE);
        mLinearLayoutMsgView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialogBuilder_Voice setApprovalListChoiceClick(View.OnClickListener click) {
        mApprovallistChoice.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_Voice withDividerColor(String colorString) {
        mDivider.setBackgroundColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialogBuilder_Voice withDividerColor(int color) {
        mDivider.setBackgroundColor(color);
        return this;
    }

    public NiftyDialogBuilder_Voice withTransparentBackground() {
        mLinearLayoutView.setBackgroundColor(Color.TRANSPARENT);
        return this;
    }

    public NiftyDialogBuilder_Voice withTitle(CharSequence title) {
        toggleView(mLinearLayoutTopView, title);
        mTitle.setText(title);
        return this;
    }

    public NiftyDialogBuilder_Voice withStatusListTitle() {
        toggleView(mTaskListLayoutView, "Status Task");
        mTasklistChoice.setText("Status Task");
        return this;
    }

    public NiftyDialogBuilder_Voice withStatusListTitleAndCounter(String count) {
        toggleView(mTaskListLayoutView, "Status Task");
        mTasklistChoice.setText("Status Task - [" + count + "]");
        return this;
    }

    public NiftyDialogBuilder_Voice withTaskListTitleAndCounter(String count) {
        toggleView(mTaskListLayoutView, "Task List");
        mTasklistChoice.setText("Task List - [" + count + "]");
        return this;
    }

    public NiftyDialogBuilder_Voice withVerificationListTitleAndCounter(String count) {
        toggleView(mVerificationLayoutView, "Verification List");
        mVerificationlistChoice.setText("Verification List - [" + count + "]");
        return this;
    }

    public NiftyDialogBuilder_Voice withApprovalListTitleAndCounter(String count) {
        toggleView(mApprovalLayoutView, "Approval List");
        mApprovallistChoice.setText("Approval List - [" + count + "]");
        return this;
    }

    public NiftyDialogBuilder_Voice withNoTitle() {
        mLinearLayoutTopView.setVisibility(View.GONE);
        return this;
    }

    public NiftyDialogBuilder_Voice withTitleColor(String colorString) {
        mTitle.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialogBuilder_Voice withTitleColor(int color) {
        mTitle.setTextColor(color);
        return this;
    }

    public NiftyDialogBuilder_Voice withMessage(int textResId) {
        toggleView(mLinearLayoutMsgView, textResId);
        mMessage.setText(textResId);
        return this;
    }

    public NiftyDialogBuilder_Voice setHeader(SurveyHeaderBean bean) {
        this.header = bean;
        return this;
    }

    public NiftyDialogBuilder_Voice setSurveyMode(int mode) {
        this.MODE = mode;
        return this;
    }

    public NiftyDialogBuilder_Voice withNoMessage() {
        mLinearLayoutMsgView.setVisibility(View.GONE);
        mMessage.setVisibility(View.GONE);
        return this;
    }

    public NiftyDialogBuilder_Voice withMessage(CharSequence msg) {
        toggleView(mLinearLayoutMsgView, msg);
        mMessage.setText(msg);
        return this;
    }

    public NiftyDialogBuilder_Voice withMessageColor(String colorString) {
        mMessage.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialogBuilder_Voice withMessageColor(int color) {
        mMessage.setTextColor(color);
        return this;
    }

    public NiftyDialogBuilder_Voice withImageView(Bitmap bitmap) {
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageBitmap(bitmap);
        return this;
    }

    public NiftyDialogBuilder_Voice withAutoText(ArrayAdapter<String> adapter) {
        mtxtSearch.setVisibility(View.VISIBLE);
        mtxtSearch.setText("");
        mtxtSearch.setAdapter(adapter);
        return this;
    }

    public String getAutoText() {
        return mtxtSearch.getText().toString();
    }

    public NiftyDialogBuilder_Voice withDialogColor(String colorString) {
        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(Color.parseColor(colorString)));
        return this;
    }

    public NiftyDialogBuilder_Voice withDialogColor(int color) {
        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(color));
        return this;
    }

    public NiftyDialogBuilder_Voice withIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    public NiftyDialogBuilder_Voice withIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }

    public NiftyDialogBuilder_Voice withDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public NiftyDialogBuilder_Voice withEffect(Effectstype type) {
        this.type = type;
        return this;
    }

    public NiftyDialogBuilder_Voice withButtonDrawable(int resid) {
        mButton1.setBackgroundResource(resid);
        mButton2.setBackgroundResource(resid);
        return this;
    }

    public NiftyDialogBuilder_Voice withButton1Text(CharSequence text) {
        mButton1.setVisibility(View.VISIBLE);
        mButton1.setText(text);

        return this;
    }

    public NiftyDialogBuilder_Voice withButton2Text(CharSequence text) {
        mButton2.setVisibility(View.VISIBLE);
        mButton2.setText(text);
        return this;
    }

    public NiftyDialogBuilder_Voice setButton1Click(View.OnClickListener click) {
        mButton1.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_Voice setButton2Click(View.OnClickListener click) {
        mButton2.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_Voice setCustomView(int resId, Context context) {
        View customView = View.inflate(context, resId, null);
        if (mFrameLayoutCustomView.getChildCount() > 0) {
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(customView);
        return this;
    }

    public NiftyDialogBuilder_Voice setCustomView(View view, Context context) {
        if (mFrameLayoutCustomView.getChildCount() > 0) {
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(view);

        return this;
    }

    public NiftyDialogBuilder_Voice isCancelableOnTouchOutside(boolean cancelable) {
        this.isCancelable = cancelable;
        this.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public NiftyDialogBuilder_Voice isCancelable(boolean cancelable) {
        this.isCancelable = cancelable;
        this.setCancelable(cancelable);
        return this;
    }

    private void toggleView(View view, Object obj) {
        if (obj == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void show() {
        super.show();
    }

    private void start(Effectstype type) {
        BaseEffects animator = type.getAnimator();
        if (mDuration != -1) {
            animator.setDuration(Math.abs(mDuration));
        }
        animator.start(mRelativeLayoutView);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mButton1.setVisibility(View.GONE);
        mButton2.setVisibility(View.GONE);
        mTaskListLayoutView.setVisibility(View.GONE);
        mVerificationLayoutView.setVisibility(View.GONE);
        mApprovalLayoutView.setVisibility(View.GONE);
    }
}
