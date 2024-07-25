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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.foundation.dialog.gitonway.lib.ColorUtils;
import com.adins.mss.foundation.dialog.gitonway.lib.Effectstype;
import com.adins.mss.foundation.dialog.gitonway.lib.effects.BaseEffects;
import com.adins.mss.foundation.image.ImageViewer;

//
//import de.greenrobot.daoexample.R;
//

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
public class NiftyDialogBuilder_PL extends Dialog implements DialogInterface {

    private static Context tmpContext;
    private static int mOrientation = 1;
    private static NiftyDialogBuilder_PL instance;
    private final String defTextColor = "#FFFFFFFF";
    private final String defDividerColor = "#11000000";
    private final String defMsgColor = "#FFFFFFFF";
    private final String defDialogColor = "#FF5f5f5f";
    private Effectstype type = null;
    private LinearLayout mLinearLayoutView;
    private LinearLayout mTaskListLayoutView;
    private LinearLayout mVerificationLayoutView;
    private LinearLayout mApprovalLayoutView;
    private LinearLayout mVerificationLayoutViewByBranch;
    private LinearLayout mApprovalLayoutViewByBranch;
    private LinearLayout mAssignmentLayoutView;
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
    private TextView mApprovallistChoiceByBranch;
    private TextView mVerificationlistChoiceByBranch;
    private TextView mAssignmentlistChoice;
    private ImageView mIcon;
    private Button mButton1;
    private Button mButton2;
    private int mDuration = -1;
    private boolean isCancelable = true;

    public NiftyDialogBuilder_PL(Context context) {
        super(context);
        init(context);

    }

    public NiftyDialogBuilder_PL(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public static NiftyDialogBuilder_PL getInstance(Context context) {

        if (instance == null || !tmpContext.equals(context)) {
            synchronized (NiftyDialogBuilder_PL.class) {
                if (instance == null || !tmpContext.equals(context)) {
                    instance = new NiftyDialogBuilder_PL(context, R.style.dialog_untran);
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
        getWindow().setAttributes(params);

    }

    private void init(Context context) {

        mDialogView = View.inflate(context, R.layout.dialog_layout_timeline, null);

        mLinearLayoutView = (LinearLayout) mDialogView.findViewById(R.id.parentPanelT);
        mTaskListLayoutView = (LinearLayout) mDialogView.findViewById(R.id.ChoiceTaskList);
        mApprovalLayoutView = (LinearLayout) mDialogView.findViewById(R.id.ChoiceApprovalList);
        mVerificationLayoutView = (LinearLayout) mDialogView.findViewById(R.id.ChoiceVerificationlist);
        mApprovalLayoutViewByBranch = (LinearLayout) mDialogView.findViewById(R.id.ChoiceApprovalListByBranch);
        mVerificationLayoutViewByBranch = (LinearLayout) mDialogView.findViewById(R.id.ChoiceVerificationlistByBranch);
        mAssignmentLayoutView = (LinearLayout) mDialogView.findViewById(R.id.ChoiceAssignmentList);
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
        mVerificationlistChoiceByBranch = (TextView) mDialogView.findViewById(R.id.Choice4);
        mApprovallistChoiceByBranch = (TextView) mDialogView.findViewById(R.id.Choice5);
        mAssignmentlistChoice = (TextView) mDialogView.findViewById(R.id.Choice6);
        mIcon = (ImageView) mDialogView.findViewById(R.id.iconT);
        mDivider = mDialogView.findViewById(R.id.titleDividerT);
        mButton1 = (Button) mDialogView.findViewById(R.id.button1T);
        mButton2 = (Button) mDialogView.findViewById(R.id.button2T);

        setContentView(mDialogView);

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
    }

    public void toDefault() {
        mTitle.setTextColor(Color.parseColor(defTextColor));
        mDivider.setBackgroundColor(Color.parseColor(defDividerColor));
        mMessage.setTextColor(Color.parseColor(defMsgColor));
        mLinearLayoutView.setBackgroundColor(Color.parseColor(defDialogColor));
    }

    public NiftyDialogBuilder_PL withTaskListChoice() {
        mTaskListLayoutView.setVisibility(View.VISIBLE);
        mLinearLayoutMsgView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialogBuilder_PL setTaskListChoiceClick(View.OnClickListener click) {
        mTasklistChoice.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_PL withVerificationListChoice() {
        mLinearLayoutMsgView.setVisibility(View.VISIBLE);
        mVerificationLayoutView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialogBuilder_PL setVerificationListChoiceClick(View.OnClickListener click) {
        mVerificationlistChoice.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_PL withApprovalListChoice() {
        mApprovalLayoutView.setVisibility(View.VISIBLE);
        mLinearLayoutMsgView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialogBuilder_PL setApprovalListChoiceClick(View.OnClickListener click) {
        mApprovallistChoice.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_PL withVerificationListChoiceByBranch() {
        mLinearLayoutMsgView.setVisibility(View.VISIBLE);
        mVerificationLayoutViewByBranch.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialogBuilder_PL setVerificationListChoiceByBranchClick(View.OnClickListener click) {
        mVerificationlistChoiceByBranch.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_PL withApprovalListChoiceByBranch() {
        mApprovalLayoutViewByBranch.setVisibility(View.VISIBLE);
        mLinearLayoutMsgView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialogBuilder_PL withAssignmentListChoice() {
        mAssignmentLayoutView.setVisibility(View.VISIBLE);
        mLinearLayoutMsgView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialogBuilder_PL setAssignmentListChoiceClick(View.OnClickListener click) {
        mAssignmentlistChoice.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_PL setApprovalListChoiceByBranchClick(View.OnClickListener click) {
        mApprovallistChoiceByBranch.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_PL withDividerColor(String colorString) {
        mDivider.setBackgroundColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialogBuilder_PL withDividerColor(int color) {
        mDivider.setBackgroundColor(color);
        return this;
    }

    public NiftyDialogBuilder_PL withTransparentBackground() {
        mLinearLayoutView.setBackgroundColor(Color.TRANSPARENT);
        return this;
    }

    public NiftyDialogBuilder_PL withTitle(CharSequence title) {
        toggleView(mLinearLayoutTopView, title);
        mTitle.setText(title);
        return this;
    }

    public NiftyDialogBuilder_PL withStatusListTitle() {
        toggleView(mTaskListLayoutView, "Status Task");
        mTasklistChoice.setText("Status Task");
        return this;
    }

    public NiftyDialogBuilder_PL withStatusListTitleAndCounter(String count) {
        toggleView(mTaskListLayoutView, "Status Task");
        mTasklistChoice.setText("Status Task - [" + count + "]");
        return this;
    }

    public NiftyDialogBuilder_PL withTaskListTitleAndCounter(String count) {
        toggleView(mTaskListLayoutView, "Task List");
        mTasklistChoice.setText("Task List - [" + count + "]");
        return this;
    }

    public NiftyDialogBuilder_PL withVerificationListTitleAndCounter(String count) {
        toggleView(mVerificationLayoutView, "Verification List");
        mVerificationlistChoice.setText("Verification List - [" + count + "]");
        return this;
    }

    public NiftyDialogBuilder_PL withApprovalListTitleAndCounter(String count) {
        toggleView(mApprovalLayoutView, "Approval List");
        mApprovallistChoice.setText("Approval List - [" + count + "]");
        return this;
    }

    public NiftyDialogBuilder_PL withVerificationListByBranchTitleAndCounter(String count) {
        toggleView(mVerificationLayoutViewByBranch, "Verification By Branch List");
        mVerificationlistChoiceByBranch.setText("Verification By Branch - [" + count + "]");
        return this;
    }

    public NiftyDialogBuilder_PL withApprovalListByBranchTitleAndCounter(String count) {
        toggleView(mApprovalLayoutViewByBranch, "Approval By Branch List");
        mApprovallistChoiceByBranch.setText("Approval By Branch - [" + count + "]");
        return this;
    }

    public NiftyDialogBuilder_PL withAssignmentListTitleAndCounter(String count) {
        toggleView(mAssignmentLayoutView, "Assignment List");
        mAssignmentlistChoice.setText("Assignment List - [" + count + "]");
        return this;
    }

    public NiftyDialogBuilder_PL withNoTitle() {
        mLinearLayoutTopView.setVisibility(View.GONE);
        return this;
    }

    public NiftyDialogBuilder_PL withTitleColor(String colorString) {
        mTitle.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialogBuilder_PL withTitleColor(int color) {
        mTitle.setTextColor(color);
        return this;
    }

    public NiftyDialogBuilder_PL withMessage(int textResId) {
        toggleView(mLinearLayoutMsgView, textResId);
        mMessage.setText(textResId);
        return this;
    }

    public NiftyDialogBuilder_PL withNoMessage() {
        mLinearLayoutMsgView.setVisibility(View.GONE);
        mMessage.setVisibility(View.GONE);
        return this;
    }

    public NiftyDialogBuilder_PL withMessage(CharSequence msg) {
        toggleView(mLinearLayoutMsgView, msg);
        mMessage.setText(msg);
        return this;
    }

    public NiftyDialogBuilder_PL withMessageColor(String colorString) {
        mMessage.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialogBuilder_PL withMessageColor(int color) {
        mMessage.setTextColor(color);
        return this;
    }

    public NiftyDialogBuilder_PL withImageView(Bitmap bitmap) {
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageBitmap(bitmap);
        return this;
    }

    public NiftyDialogBuilder_PL withAutoText(ArrayAdapter<String> adapter) {
        mtxtSearch.setVisibility(View.VISIBLE);
        mtxtSearch.setText("");
        mtxtSearch.setAdapter(adapter);
        return this;
    }

    public String getAutoText() {
        return mtxtSearch.getText().toString();
    }

    public NiftyDialogBuilder_PL withDialogColor(String colorString) {
        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(Color.parseColor(colorString)));
        return this;
    }

    public NiftyDialogBuilder_PL withDialogColor(int color) {
        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(color));
        return this;
    }

    public NiftyDialogBuilder_PL withIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    public NiftyDialogBuilder_PL withIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }

    public NiftyDialogBuilder_PL withDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public NiftyDialogBuilder_PL withEffect(Effectstype type) {
        this.type = type;
        return this;
    }

    public NiftyDialogBuilder_PL withButtonDrawable(int resid) {
        mButton1.setBackgroundResource(resid);
        mButton2.setBackgroundResource(resid);
        return this;
    }

    public NiftyDialogBuilder_PL withButton1Text(CharSequence text) {
        mButton1.setVisibility(View.VISIBLE);
        mButton1.setText(text);

        return this;
    }

    public NiftyDialogBuilder_PL withButton2Text(CharSequence text) {
        mButton2.setVisibility(View.VISIBLE);
        mButton2.setText(text);
        return this;
    }

    public NiftyDialogBuilder_PL setButton1Click(View.OnClickListener click) {
        mButton1.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder_PL setButton2Click(View.OnClickListener click) {
        mButton2.setOnClickListener(click);
        return this;
    }


    public NiftyDialogBuilder_PL setCustomView(int resId, Context context) {
        View customView = View.inflate(context, resId, null);
        if (mFrameLayoutCustomView.getChildCount() > 0) {
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(customView);
        return this;
    }

    public NiftyDialogBuilder_PL setCustomView(View view, Context context) {
        if (mFrameLayoutCustomView.getChildCount() > 0) {
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(view);

        return this;
    }

    public NiftyDialogBuilder_PL isCancelableOnTouchOutside(boolean cancelable) {
        this.isCancelable = cancelable;
        this.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public NiftyDialogBuilder_PL isCancelable(boolean cancelable) {
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
        mVerificationLayoutViewByBranch.setVisibility(View.GONE);
        mApprovalLayoutViewByBranch.setVisibility(View.GONE);
        mAssignmentLayoutView.setVisibility(View.GONE);
    }
}
