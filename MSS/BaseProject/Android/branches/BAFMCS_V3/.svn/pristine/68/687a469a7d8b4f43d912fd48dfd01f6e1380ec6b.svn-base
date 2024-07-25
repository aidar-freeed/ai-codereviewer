package com.adins.mss.foundation.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import com.adins.mss.base.R;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.dynamictheme.ThemeUtility;
import com.adins.mss.foundation.dialog.gitonway.lib.ColorUtils;
import com.adins.mss.foundation.dialog.gitonway.lib.Effectstype;
import com.adins.mss.foundation.dialog.gitonway.lib.effects.BaseEffects;

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

/**
 * @author michael.bw
 *         <p>
 *         <p>
 *         <h1>
 *         <a id="user-content-usage" class="anchor" href="#usage" aria-hidden="true"><span class="octicon octicon-link"></span></a>Usage</h1>
 *         <div class="highlight highlight-java"><pre><span class="pl-stj">NiftyDialogBuilder</span> dialogBuilder<span class="pl-k">=</span><span class="pl-stj">NiftyDialogBuilder</span><span class="pl-k">.</span>getInstance(<span class="pl-v">this</span>);
 *         <p>
 *         dialogBuilder
 *         .withTitle(<span class="pl-s1"><span class="pl-pds">"</span>Modal Dialog<span class="pl-pds">"</span></span>)
 *         .withMessage(<span class="pl-s1"><span class="pl-pds">"</span>This is a modal Dialog.<span class="pl-pds">"</span></span>)
 *         .show();</pre></div>
 *         <br/>
 *         <p>
 *         <h1>
 *         <a id="user-content-usage" class="anchor" href="#usage" aria-hidden="true"><span class="octicon octicon-link"></span></a>Configuration</h1>
 *         <div class="highlight highlight-java"><pre>dialogBuilder
 *         .withTitle(<span class="pl-s1"><span class="pl-pds">"</span>Modal Dialog<span class="pl-pds">"</span></span>)                                  <span class="pl-c">//.withTitle(null)  no title</span>
 *         .withTitleColor(<span class="pl-s1"><span class="pl-pds">"</span>#FFFFFF<span class="pl-pds">"</span></span>)                                  <span class="pl-c">//def</span>
 *         .withDividerColor(<span class="pl-s1"><span class="pl-pds">"</span>#11000000<span class="pl-pds">"</span></span>)                              <span class="pl-c">//def</span>
 *         .withMessage(<span class="pl-s1"><span class="pl-pds">"</span>This is a modal Dialog.<span class="pl-pds">"</span></span>)                     <span class="pl-c">//.withMessage(null)  no Msg</span>
 *         .withMessageColor(<span class="pl-s1"><span class="pl-pds">"</span>#FFFFFFFF<span class="pl-pds">"</span></span>)                              <span class="pl-c">//def  | withMessageColor(int resid)</span>
 *         .withDialogColor(<span class="pl-s1"><span class="pl-pds">"</span>#FFE74C3C<span class="pl-pds">"</span></span>)                               <span class="pl-c">//def  | withDialogColor(int resid)</span>
 *         .withIcon(getResources()<span class="pl-k">.</span>getDrawable(<span class="pl-stj">R</span><span class="pl-k">.</span>drawable<span class="pl-k">.</span>icon))
 *         .withDuration(<span class="pl-c1">700</span>)                                          <span class="pl-c">//def</span>
 *         .withEffect(effect)                                         <span class="pl-c">//def Effectstype.Slidetop</span>
 *         .withButton1Text(<span class="pl-s1"><span class="pl-pds">"</span>OK<span class="pl-pds">"</span></span>)                                      <span class="pl-c">//def gone</span>
 *         .withButton2Text(<span class="pl-s1"><span class="pl-pds">"</span>Cancel<span class="pl-pds">"</span></span>)                                  <span class="pl-c">//def gone</span>
 *         .isCancelableOnTouchOutside(<span class="pl-c1">true</span>)                           <span class="pl-c">//def    | isCancelable(true)</span>
 *         .setCustomView(<span class="pl-stj">R</span><span class="pl-k">.</span>layout<span class="pl-k">.</span>custom_view,v<span class="pl-k">.</span>getContext())         <span class="pl-c">//.setCustomView(View or ResId,context)</span>
 *         .setButton1Click(<span class="pl-k">new</span> <span class="pl-stj">View</span>.<span class="pl-stj">OnClickListener</span>() {
 *         <span class="pl-st">@Override</span>
 *         <span class="pl-s">public</span> <span class="pl-st">void</span> <span class="pl-en">onClick</span>(<span class="pl-stj">View</span> <span class="pl-v">v</span>) {
 *         <span class="pl-stj">Toast</span><span class="pl-k">.</span>makeText(v<span class="pl-k">.</span>getContext(), <span class="pl-s1"><span class="pl-pds">"</span>i'm btn1<span class="pl-pds">"</span></span>, <span class="pl-stj">Toast</span><span class="pl-c1"><span class="pl-k">.</span>LENGTH_SHORT</span>)<span class="pl-k">.</span>show();
 *         }
 *         })
 *         .setButton2Click(<span class="pl-k">new</span> <span class="pl-stj">View</span>.<span class="pl-stj">OnClickListener</span>() {
 *         <span class="pl-st">@Override</span>
 *         <span class="pl-s">public</span> <span class="pl-st">void</span> <span class="pl-en">onClick</span>(<span class="pl-stj">View</span> <span class="pl-v">v</span>) {
 *         <span class="pl-stj">Toast</span><span class="pl-k">.</span>makeText(v<span class="pl-k">.</span>getContext(),<span class="pl-s1"><span class="pl-pds">"</span>i'm btn2<span class="pl-pds">"</span></span>,<span class="pl-stj">Toast</span><span class="pl-c1"><span class="pl-k">.</span>LENGTH_SHORT</span>)<span class="pl-k">.</span>show();
 *         }
 *         })
 *         .show();</pre></div>
 */
public class NiftyDialogBuilder extends Dialog implements DialogInterface, ThemeLoader.ColorSetLoaderCallback {

    private static Context tmpContext;
    private static NiftyDialogBuilder instance;
    private final String defTextColor = "#FFFFFFFF";
    private final String defDividerColor = "#11000000";
    private final String defMsgColor = "#FFFFFFFF";
    private final String defDialogColor = "#FF5f5f5f";
    private Effectstype type = null;
    private LinearLayout mLinearLayoutView;
    private RelativeLayout mRelativeLayoutView;
    private LinearLayout mLinearLayoutMsgView;
    private LinearLayout mLinearLayoutTopView;
    private FrameLayout mFrameLayoutCustomView;
    private View mDialogView;
    private View mDivider;
    private TextView mTitle;
    private TextView mMessage;
    private ImageView mIcon;
    private Button mButton1;
    private Button mButton2;
    private int mDuration = -1;
    private boolean isCancelable = true;

    public NiftyDialogBuilder(Context context) {
        super(context);
        init(context);
    }

    public NiftyDialogBuilder(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public static NiftyDialogBuilder getInstance(Context context) {

        if (instance == null || !context.equals(tmpContext)) {
            synchronized (NiftyDialogBuilder.class) {
                if (instance == null || !context.equals(tmpContext)) {
                    instance = new NiftyDialogBuilder(context, R.style.dialog_untran);
                }
            }
        }
        tmpContext = context;
        return instance;

    }

    private void applyColorTheme(DynamicTheme dynamicTheme){
        if(dynamicTheme != null && !dynamicTheme.getThemeItemList().isEmpty()){
            int btnColorNormal = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"btn_bg_normal"));
            int btnColorPress = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"btn_bg_pressed"));
            //create color state list for button states
            int[][] states = new int[][] {
                    new int[] { android.R.attr.state_pressed},  // pressed
                    new int[] {}  // normal
            };

            int[] colorlist = new int[]{
                    btnColorPress,
                    btnColorNormal
            };
            ColorStateList colorStateList = new ColorStateList(states,colorlist);
            ThemeUtility.setViewBackground(mButton1,colorStateList);
            ThemeUtility.setViewBackground(mButton2,colorStateList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }

    @Override
    protected void onStop() {
        super.onStop();
        tmpContext = null;
    }

    private void init(Context context) {

        mDialogView = View.inflate(context, R.layout.dialog_layout, null);

        mLinearLayoutView = (LinearLayout) mDialogView.findViewById(R.id.parentPanel);
        mRelativeLayoutView = (RelativeLayout) mDialogView.findViewById(R.id.main);
        mLinearLayoutTopView = (LinearLayout) mDialogView.findViewById(R.id.topPanel);
        mLinearLayoutMsgView = (LinearLayout) mDialogView.findViewById(R.id.contentPanel);
        mFrameLayoutCustomView = (FrameLayout) mDialogView.findViewById(R.id.customPanel);

        mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);
        mMessage = (TextView) mDialogView.findViewById(R.id.message);
        mIcon = (ImageView) mDialogView.findViewById(R.id.icon);
        mButton1 = (Button) mDialogView.findViewById(R.id.button1);
        mButton2 = (Button) mDialogView.findViewById(R.id.button2);

        mMessage.setMovementMethod(LinkMovementMethod.getInstance());

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
        loadSavedTheme(context);
    }

    private void loadSavedTheme(Context context){
        ThemeLoader themeLoader = new ThemeLoader(context);
        themeLoader.loadSavedColorSet(this);
    }

    public void toDefault() {
        mTitle.setTextColor(Color.parseColor(defTextColor));
        mDivider.setBackgroundColor(Color.parseColor(defDividerColor));
        mMessage.setTextColor(Color.parseColor(defMsgColor));
        mLinearLayoutView.setBackgroundColor(Color.parseColor(defDialogColor));
    }

    public NiftyDialogBuilder withDividerColor(String colorString) {
        mDivider.setBackgroundColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialogBuilder withDividerColor(int color) {
        mDivider.setBackgroundColor(color);
        return this;
    }


    public NiftyDialogBuilder withTitle(CharSequence title) {
        toggleView(mLinearLayoutTopView, title);
        mTitle.setText(title);
        return this;
    }

    public NiftyDialogBuilder withTitleColor(String colorString) {
        mTitle.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialogBuilder withTitleColor(int color) {
        mTitle.setTextColor(color);
        return this;
    }

    public NiftyDialogBuilder withMessage(int textResId) {
        toggleView(mLinearLayoutMsgView, textResId);
        mMessage.setText(textResId);
        return this;
    }

    public NiftyDialogBuilder withMessage(CharSequence msg) {
        toggleView(mLinearLayoutMsgView, msg);
        mMessage.setText(msg);
        return this;
    }

    public NiftyDialogBuilder withMessageColor(String colorString) {
        mMessage.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialogBuilder withMessageColor(int color) {
        mMessage.setTextColor(color);
        return this;
    }

    public NiftyDialogBuilder withDialogColor(String colorString) {
        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(Color.parseColor(colorString)));
        return this;
    }

    public NiftyDialogBuilder withDialogColor(int color) {
        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(color));
        return this;
    }

    public NiftyDialogBuilder withIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    public NiftyDialogBuilder withIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }

    public NiftyDialogBuilder withDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public NiftyDialogBuilder withEffect(Effectstype type) {
        this.type = type;
        return this;
    }

    public NiftyDialogBuilder withButtonDrawable(int resid) {
        mButton1.setBackgroundResource(resid);
        mButton2.setBackgroundResource(resid);
        return this;
    }

    public NiftyDialogBuilder withButton1Text(CharSequence text) {
        mButton1.setVisibility(View.VISIBLE);
        mButton1.setText(text);

        return this;
    }

    public NiftyDialogBuilder withButton2Text(CharSequence text) {
        mButton2.setVisibility(View.VISIBLE);
        mButton2.setText(text);
        return this;
    }

    public NiftyDialogBuilder setButton1Click(View.OnClickListener click) {
        mButton1.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder setButton2Click(View.OnClickListener click) {
        mButton2.setOnClickListener(click);
        return this;
    }


    public NiftyDialogBuilder setCustomView(int resId, Context context) {
        View customView = View.inflate(context, resId, null);
        if (mFrameLayoutCustomView.getChildCount() > 0) {
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(customView);
        return this;
    }

    public NiftyDialogBuilder setCustomView(View view, Context context) {
        if (mFrameLayoutCustomView.getChildCount() > 0) {
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(view);

        return this;
    }

    public NiftyDialogBuilder isCancelableOnTouchOutside(boolean cancelable) {
        this.isCancelable = cancelable;
        this.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public NiftyDialogBuilder isCancelable(boolean cancelable) {
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
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme) {
        if(dynamicTheme != null && !dynamicTheme.getThemeItemList().isEmpty()){
            applyColorTheme(dynamicTheme);
        }
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {
        //EMPTY
    }
}
