package com.adins.mss.base.review;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.QuestionGroup;
import com.adins.mss.base.timeline.MapsViewer;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Reader;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

import java.util.Date;
import java.util.List;


/**
 * @author gigin.ginanjar
 */
@SuppressLint("ResourceAsColor")
public class QuestionReviewGenerator {
    public static final int TYPE_DATE = 1;
    public static final int TYPE_TIME = 2;
    public static final int TYPE_DATE_TIME = 3;


    public final LayoutParams defLayout = new LayoutParams(
            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

    //------------------------------------------------
    //Gigin 5/10/014
    //method untuk ngambil data buat di review
    public QuestionView generateQuestionGroupTitle(Activity activity, QuestionGroup questionGroup) {
        QuestionView mainContainer = new QuestionView(activity);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(5, 5, 5, 5);

        QuestionView container = new QuestionView(activity);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setBackgroundResource(R.color.tv_dark);
        container.setPadding(8, 10, 8, 10);
        mainContainer.setQuestionGroup(questionGroup);
        container.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textLayout.weight = 0.8f;

        TextView label = new TextView(activity);
        label.setText(questionGroup.getQuestion_group_name().toUpperCase());
        label.setTextColor(Color.WHITE);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
        container.addView(label, textLayout);

        RelativeLayout.LayoutParams viewLayout = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        viewLayout.width = Global.TRIANGLE_SIZE;
        viewLayout.height = Global.TRIANGLE_SIZE;
        viewLayout.bottomMargin = 10;
        viewLayout.topMargin = 10;
        viewLayout.leftMargin = 10;
        viewLayout.rightMargin = 10;

        RelativeLayout.LayoutParams relLayout = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        RelativeLayout relativeLayout = new RelativeLayout(activity);
        relativeLayout.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        View view = new View(activity);
        view.setBackgroundResource(R.drawable.triangle);
        view.setRotation(mainContainer.isExpanded() ? 0f : 180f);

        relativeLayout.addView(view, viewLayout);
        container.addView(relativeLayout, relLayout);

        mainContainer.addView(container);
        mainContainer.setTitleOnly(true);
        return mainContainer;
    }

    public QuestionView generateTextAnswer(Activity activity,
                                           QuestionBean bean) {
        QuestionView mainContainer = new QuestionView(activity);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(5, 5, 5, 5);

        QuestionView container = new QuestionView(activity);
        mainContainer.setQuestionBean(bean);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(8, 8, 8, 8);
        container.setBackgroundColor(Color.parseColor("#cccccc"));
        TextView label = new TextView(activity);
        label.setTextSize(18);
        label.setTypeface(null, Typeface.BOLD);
        label.setText(bean.getQuestion_label() + " :");

        String question = bean.getQuestion_label();
        String answer = bean.getAnswer();


        String answerType = bean.getAnswer_type();
        String regex = "(dalam ribuan)";

        if (question.indexOf(regex) != -1) {

            int firstIdx = regex.length();
            int lastIdx = question.length();
            int indexAkhir = lastIdx - firstIdx;

            String newQuestion = question.substring(0, indexAkhir);
            question = newQuestion;
            String answerTemp = Reader.getThousandDigit(answer);
            answer = answerTemp;

            label.setText(question + " :");
        }

        if (Global.AT_DECIMAL.equals(answerType)) {
            String answerTemp = Reader.getCurrencyDigit(answer);
            answer = answerTemp;
        }

        if (Global.AT_CURRENCY.equals(bean.getAnswer_type())) {
            String answerTemp = Tool.separateThousand(answer);
            if (answerTemp == null) answerTemp = "";
            answer = answerTemp;
        }
        TextView text = new TextView(activity);
        text.setText(answer);
        text.setTextSize(16);
        text.setTypeface(null, Typeface.NORMAL);
        container.addView(label, defLayout);
        container.addView(text, defLayout);
        mainContainer.addView(container);
        return mainContainer;
    }

    public QuestionView generateLocationAnswer(Activity activity,
                                               QuestionBean bean) {
        QuestionView mainContainer = new QuestionView(activity);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(5, 5, 5, 5);

        QuestionView container = new QuestionView(activity);
        mainContainer.setQuestionBean(bean);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(8, 8, 8, 8);
        container.setBackgroundColor(Color.parseColor("#cccccc"));
        TextView label = new TextView(activity);
        label.setTextSize(18);
        label.setTypeface(null, Typeface.BOLD);
        label.setText(bean.getQuestion_label() + " :");

        TextView text = new TextView(activity);
        text.setText(bean.getAnswer());
        text.setTextSize(16);
        text.setTypeface(null, Typeface.NORMAL);
        text.setTextColor(Color.BLACK);

        container.addView(label, defLayout);
        container.addView(text, defLayout);

        mainContainer.addView(container);
        return mainContainer;
    }

    public QuestionView generateDateTimeAnswer(final Activity activity,
                                               QuestionBean bean, int type) throws Exception {
        QuestionView mainContainer = new QuestionView(activity);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(5, 5, 5, 5);

        QuestionView container = new QuestionView(activity);
        mainContainer.setQuestionBean(bean);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(8, 8, 8, 8);
        container.setBackgroundColor(Color.parseColor("#cccccc"));
        TextView label = new TextView(activity);
        label.setTextSize(18);
        label.setTypeface(null, Typeface.BOLD);
        label.setText(bean.getQuestion_label() + " :");

        String format = null;
        TextView text = new TextView(activity);
        text.setTextSize(16);
        text.setTypeface(null, Typeface.NORMAL);
        switch (type) {
            case TYPE_DATE:
                format = Global.DATE_STR_FORMAT;
                break;
            case TYPE_TIME:
                format = Global.TIME_STR_FORMAT;
                break;
            case TYPE_DATE_TIME:
                format = Global.DATE_TIME_STR_FORMAT;
                break;
            default:
                format = Global.DATE_STR_FORMAT;
                break;
        }

        String answer = bean.getAnswer();
        try {
            Date date = null;
            if (type == TYPE_TIME) {
                date = Formatter.parseDate(answer, Global.TIME_STR_FORMAT2);
            } else {
                date = Formatter.parseDate(answer, Global.DATE_STR_FORMAT_GSON);
            }
            answer = Formatter.formatDate(date, format);
            text.setText(answer);
        } catch (Exception ex) {
            text.setText(format);
            if (answer != null && answer.length() > 0) {
                long dtLong = Formatter.stringToDate(answer);
                Date date = new Date(dtLong);
                answer = Formatter.formatDate(date, format);

                if (answer.length() > 0) {
                    text.setText(answer);
                } else {
                    text.setText(format);
                }
            }
        }
        container.addView(label, defLayout);
        container.addView(text);
        mainContainer.addView(container);
        return mainContainer;
    }

    public QuestionView generateOptionsAnswer(Activity activity,
                                              QuestionBean bean) {
        QuestionView mainContainer = new QuestionView(activity);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(5, 5, 5, 5);

        QuestionView container = new QuestionView(activity);
        mainContainer.setQuestionBean(bean);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(8, 8, 8, 8);
        container.setBackgroundColor(Color.parseColor("#cccccc"));
        List<OptionAnswerBean> listOptions = bean.getSelectedOptionAnswers();
        TextView label = new TextView(activity);
        label.setTextSize(18);
        label.setTypeface(null, Typeface.BOLD);
        label.setText(bean.getQuestion_label() + " :");
        container.addView(label, defLayout);
        int i = 0;
        String[] arrSelectedAnswer = null;
        try {
            arrSelectedAnswer = Tool.split(bean.getAnswer(), Global.DELIMETER_DATA);
        } catch (Exception e) {
            FireCrash.log(e);
            arrSelectedAnswer = new String[0];
        }
        for (OptionAnswerBean optBean : listOptions) {
            if (optBean.isSelected()) {

                TextView txt = new TextView(activity);
                txt.setTextSize(16);
                txt.setTypeface(null, Typeface.NORMAL);
                StringBuilder sb = new StringBuilder();
                if (Tool.isOptionsWithDescription(bean.getAnswer_type())) {

                    sb.append(optBean.getCode() + " - " + arrSelectedAnswer[i]);
                } else {
                    sb.append(optBean.getValue());
                }

                if ((Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(bean.getAnswer_type())
                        || Global.AT_DROPDOWN_W_DESCRIPTION.equals(bean.getAnswer_type())) && i == listOptions.size() - 1) {
                    sb.append("\nDesc : " + bean.getAnswer());
                }
                txt.setText(sb.toString());

                container.addView(txt);
            } else {
                TextView txt = new TextView(activity);
                txt.setTextSize(16);
                txt.setTypeface(null, Typeface.NORMAL);
                StringBuilder sb = new StringBuilder();
                sb.append(activity.getString(R.string.no_selected_field));
                txt.setText(sb.toString());

                container.addView(txt);
            }
            i++;
        }
        if (listOptions == null || listOptions.isEmpty()) {
            TextView txt = new TextView(activity);
            txt.setTextSize(16);
            txt.setTypeface(null, Typeface.NORMAL);
            StringBuilder sb = new StringBuilder();
            sb.append(activity.getString(R.string.no_selected_field));
            txt.setText(sb.toString());
            container.addView(txt);
        }
        mainContainer.addView(container);
        return mainContainer;
    }

    public QuestionView generateImageAnswer(final Activity activity, final QuestionBean bean,
                                            final Class<?> ViewImageActivity) {
        Utility.freeMemory();
        QuestionView mainContainer = new QuestionView(activity);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(5, 5, 5, 5);

        QuestionView container = new QuestionView(activity);
        mainContainer.setQuestionBean(bean);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(8, 8, 8, 8);
        container.setBackgroundColor(Color.parseColor("#cccccc"));
        TextView label = new TextView(activity);
        label.setText(bean.getQuestion_label() + " :");
        label.setTextSize(18);
        label.setTypeface(null, Typeface.BOLD);
        byte[] img = bean.getImgAnswer();

        LinearLayout imgContainer = new QuestionView(activity);
        imgContainer.setOrientation(LinearLayout.HORIZONTAL);
        imgContainer.setGravity(Gravity.LEFT);
        imgContainer.setPadding(5, 0, 5, 0);

        final ImageView thumb = new ImageView(activity);
        final ImageView thumbLct = new ImageView(activity);
        thumbLct.setPadding(15, 0, 0, 0);


        if (img != null && img.length > 0) {
            final float scale = 0;

            int w = Tool.dpToPixel(scale, Global.THUMBNAIL_WIDTH);
            int h = Tool.dpToPixel(scale, Global.THUMBNAIL_HEIGHT);
            ViewGroup.LayoutParams imgLayout = new LayoutParams(w, h);
            thumb.setLayoutParams(imgLayout);
            Bitmap bm = BitmapFactory.decodeByteArray(img, 0, img.length);
            int[] res = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
            Bitmap thumbnail = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
            thumb.setImageBitmap(thumbnail);
            thumb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        Global.getSharedGlobal().setIsViewer(true);
                        Bundle extras = new Bundle();
                        extras.putByteArray(Global.BUND_KEY_IMAGE_BYTE, bean.getImgAnswer());
                        Intent intent = new Intent(activity, ViewImageActivity);
                        intent.putExtras(extras);
                        activity.startActivity(intent);

                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
            });

            bm.recycle();
            thumbLct.setLayoutParams(imgLayout);
            thumbLct.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (bean.getLocationInfo() != null) {
                        try {
                            String lat = bean.getLocationInfo().getLatitude();
                            String lng = bean.getLocationInfo().getLongitude();
                            int accuracy = bean.getLocationInfo().getAccuracy();
                            Intent intent = new Intent(activity, MapsViewer.class);
                            intent.putExtra("latitude", lat);
                            intent.putExtra("longitude", lng);
                            intent.putExtra("accuracy", accuracy);
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            String lat = bean.getLatitude();
                            String lng = bean.getLongitude();
                            Intent intent = new Intent(activity, MapsViewer.class);
                            intent.putExtra("latitude", lat);
                            intent.putExtra("longitude", lng);
                            activity.startActivity(intent);
                        }
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.msgUnavaibleLocation),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            if (Tool.isHaveLocation(bean.getAnswer_type())) {
                thumbLct.setImageResource(R.drawable.ic_absent);
            }
        } else {
            thumb.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        container.addView(label, defLayout);

        imgContainer.addView(thumb, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imgContainer.addView(thumbLct, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        container.addView(imgContainer, defLayout);
        mainContainer.addView(container);
        return mainContainer;
    }

    public QuestionView generateLookupAnswer(final Activity activity, final QuestionBean bean) {
        QuestionView mainContainer = new QuestionView(activity);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(5, 5, 5, 5);

        QuestionView container = new QuestionView(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setQuestionBean(bean);
        container.setPadding(8, 8, 8, 8);
        container.setBackgroundColor(Color.parseColor("#cccccc"));
        TextView label = new TextView(activity);
        label.setTextSize(18);
        label.setTypeface(null, Typeface.BOLD);
        label.setText(bean.getQuestion_label() + " :");

        String key = bean.getLovId();
        if (!Tool.isEmptyString(key)) {
            TextView txt = new TextView(activity);
            txt.setTextSize(16);
            txt.setTypeface(null, Typeface.NORMAL);
            String value = bean.getAnswer();

            if (value == null || "".equals(value.trim())) {
                value = "(name)";
            }
            txt.setText(value);

            container.addView(label, defLayout);
            container.addView(txt, defLayout);
        }
        mainContainer.addView(container);
        return mainContainer;
    }

    /**
     * Get Layout for Review by Answer type
     *
     * @param activity          - Activity
     * @param bean              - QuestionBean
     * @param ViewImageActivity - Class of ViewImageActivity
     * @return Layout Container
     * @throws Exception
     */
    public QuestionView generateReviewQuestion(Activity activity,
                                               QuestionBean bean, final Class<?> ViewImageActivity, QuestionGroup group) throws Exception {
        QuestionView linear = new QuestionView(activity);
        linear.setOrientation(LinearLayout.VERTICAL);

        if (group != null) {
            return this.generateQuestionGroupTitle(activity, group);
        }
        String answerType = bean.getAnswer_type();
        if (Global.AT_TEXT.equals(answerType)) {
            return this.generateTextAnswer(activity, bean);
        } else if (Global.AT_TEXT_MULTILINE.equals(answerType)) {
            return this.generateTextAnswer(activity, bean);
        } else if (Global.AT_TEXT_WITH_SUGGESTION.equals(answerType)) {
            return this.generateTextAnswer(activity, bean);
        } else if (Global.AT_CURRENCY.equals(answerType)) {
            return this.generateTextAnswer(activity, bean);
        } else if (Global.AT_NUMERIC.equals(answerType)) {
            return this.generateTextAnswer(activity, bean);
        } else if (Global.AT_DECIMAL.equals(answerType)) {
            return this.generateTextAnswer(activity, bean);
        } else if (Global.AT_GPS.equals(answerType)) {
            return this.generateLocationAnswer(activity, bean);
        } else if (Global.AT_GPS_N_LBS.equals(answerType)) {
            return this.generateLocationAnswer(activity, bean);
        } else if (Global.AT_MULTIPLE.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean);
        } else if (Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean);
        } else if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean);
        } else if (Global.AT_RADIO.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean);
        } else if (Global.AT_RADIO_W_DESCRIPTION.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean);
        } else if (Global.AT_DROPDOWN.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean);
        } else if (Global.AT_DROPDOWN_W_DESCRIPTION.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean);
        } else if (Global.AT_DATE.equals(answerType)) {
            return this.generateDateTimeAnswer(activity, bean, TYPE_DATE);
        } else if (Global.AT_TIME.equals(answerType)) {
            return this.generateDateTimeAnswer(activity, bean, TYPE_TIME);
        } else if (Global.AT_DATE_TIME.equals(answerType)) {
            return this.generateDateTimeAnswer(activity, bean, TYPE_DATE_TIME);
        } else if (Tool.isImage(answerType)) {
            return this.generateImageAnswer(activity, bean, ViewImageActivity);
        }
        else if (Global.AT_LOCATION.equals(answerType)) {
            return this.generateLocationAnswer(activity, bean);
        } else if (Global.AT_LOV.equals(answerType)) {
            return this.generateLookupAnswer(activity, bean);
        } else if (Global.AT_LOV_W_FILTER.equals(answerType)) {
            return this.generateLookupAnswer(activity, bean);
        } else if (Global.AT_DRAWING.equals(answerType)) {
            return this.generateImageAnswer(activity, bean, ViewImageActivity);
        } else {
            return null;
        }
    }
}

