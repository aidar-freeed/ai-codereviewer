package com.adins.mss.foundation.questiongenerator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;

import java.util.Date;
import java.util.List;

public class AnswerViewGenerator {
    public static final int TYPE_DATE = 1;
    public static final int TYPE_TIME = 2;
    public static final int TYPE_DATE_TIME = 3;

    public final LayoutParams defLayout = new LayoutParams(
            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

    public LinearLayout generateTextAnswer(Activity activity,
                                           QuestionBean bean, int number) {

        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);

        TextView label = new TextView(activity);
        label.setText(number + ". " + bean.getQuestion_label());
        label.setTextColor(Color.BLACK);
        container.addView(label, defLayout);

        TextView text = new TextView(activity);
        text.setText(bean.getAnswer());
        //Glen 16 Sept 2014, if decimal, use thousand separator
        //Glen 21 Oct 2014, if calculation, use thousand separator too
//		if (Global.AT_DECIMAL.equals(bean.getAnswer_type()) ||
//				Global.AT_CALCULATION.equals(bean.getAnswer_type())){	
        //bong 17 march 15 include at_currecnty
        if (Global.AT_DECIMAL.equals(bean.getAnswer_type()) ||
                Global.AT_CALCULATION.equals(bean.getAnswer_type()) ||
                Global.AT_CURRENCY.equals(bean.getAnswer_type())) {
            String answer = bean.getAnswer();
            //Glen 21 Oct 2014, use tool
            String separatedAnswer = Tool.separateThousand(answer);

            text.setText(separatedAnswer);
        }
        text.setTextColor(Color.BLACK);
        container.addView(text);

        return container;
    }

    //add 27 juni 2012~bangkit
    public LinearLayout generateLocationAnswer(Activity activity,
                                               QuestionBean bean, int number) {

        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);

        TextView label = new TextView(activity);
        label.setText(number + ". " + bean.getQuestion_label());
        label.setTextColor(Color.BLACK);
        container.addView(label, defLayout);

        TextView text = new TextView(activity);
        text.setText(bean.getAnswer());
        text.setTextColor(Color.BLACK);
        container.addView(text);

        return container;
    }

    public LinearLayout generateDateTimeAnswer(final Activity activity,
                                               QuestionBean bean, int number, int type) throws Exception {

        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);

        TextView label = new TextView(activity);
        label.setText(number + ". " + bean.getQuestion_label());
        label.setTextColor(Color.BLACK);
        container.addView(label, defLayout);

        String format = null;
        TextView text = new TextView(activity);
        text.setTextColor(Color.BLACK);

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

        try {
            String answer = bean.getAnswer();
            long dtLong = Formatter.stringToDate(answer);
            Date date = new Date(dtLong);
            answer = Formatter.formatDate(date, format);
            text.setText(answer);
        } catch (Exception ex) {
            //Glen 6 Aug 2014, add possibility to insert milliseconds
            String answer = bean.getAnswer();
            long dtLong = Long.parseLong(answer);
            Date date = new Date(dtLong);
            answer = Formatter.formatDate(date, format);
            text.setText(answer);
        }
        container.addView(text);

        return container;
    }

    public LinearLayout generateOptionsAnswer(Activity activity,
                                              QuestionBean bean, int number) {
        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);

        TextView label = new TextView(activity);
        label.setText(number + ". " + bean.getQuestion_label());
        label.setTextColor(Color.BLACK);
        container.addView(label, defLayout);

        //Glen 15 Oct 2014, use selected list only
        List<OptionAnswerBean> listOptions = bean.getSelectedOptionAnswers();

        for (OptionAnswerBean optBean : listOptions) {
            if (optBean.isSelected()) {
                TextView txt = new TextView(activity);
                txt.setTextColor(Color.BLACK);
                StringBuilder sb = new StringBuilder();

                //Glen 3 Oct 2014, refactor, code can be from optionId or lovCode
//				sb.append(optBean.getId()).append(" - ").append(optBean.getLabel());
                String lovCode = optBean.getCode();
                if (lovCode != null && lovCode.length() > 0) {
                    sb.append(lovCode);
                } else {
                    sb.append(optBean.getCode());
                }
                sb.append(" - ").append(optBean.getValue());


                String description = optBean.getValue();
                if (!Tool.isEmptyString(description)) {
                    sb.append(" / ").append(description);
                }
                txt.setText(sb.toString());
                container.addView(txt);
            }
        }

        //Glen 15 Oct 2014, if answer is empty, add empty field
        if (listOptions.size() == 0) {
            TextView txt = new TextView(activity);
            txt.setText("-");
            container.addView(txt);
        }

        return container;
    }

    public LinearLayout generateImageQuestion(final Activity activity, final QuestionBean bean, int number) {
        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);

        TextView label = new TextView(activity);
        label.setText(number + ". " + bean.getQuestion_label());
        label.setTextColor(Color.BLACK);
        container.addView(label, defLayout);

        byte[] img = bean.getImgAnswer();
        final ImageView thumb = new ImageView(activity);


        if (img != null && img.length > 0) {
            //TODO bangkit- harus di gerelalisasikan
            final float scale = 0; //TODO bangkit
            int w = Tool.dpToPixel(scale, Global.THUMBNAIL_WIDTH);
            int h = Tool.dpToPixel(scale, Global.THUMBNAIL_HEIGHT);
            ViewGroup.LayoutParams imgLayout = new LayoutParams(w, h);
            thumb.setLayoutParams(imgLayout);
            Bitmap bm = BitmapFactory.decodeByteArray(img, 0, img.length);
            int[] res = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
            Bitmap thumbnail = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
            thumb.setImageBitmap(thumbnail);
        } else {
            thumb.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        container.addView(thumb, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        return container;
    }

    public LinearLayout generateLookupAnswer(final Activity activity, final QuestionBean bean, int number) {
        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);

        TextView label = new TextView(activity);
        label.setText(number + ". " + bean.getQuestion_label());
        label.setTextColor(Color.BLACK);
        container.addView(label, defLayout);

        String key = bean.getLovId();
        if (!Tool.isEmptyString(key)) {
            TextView txt = new TextView(activity);
            txt.setTextColor(Color.BLACK);
            String value = bean.getAnswer();
            if (key == null || "".equals(key.trim())) {
                key = "(code)";
            }
            if (value == null || "".equals(value.trim())) {
                value = "(name)";
            }
            txt.setText(key + " - " + value);
            container.addView(txt, defLayout);
        }

        return container;
    }

    public LinearLayout generateByAnswerType(Activity activity,
                                             QuestionBean bean, int number) throws Exception {
        LinearLayout linear = new LinearLayout(activity);
        linear.setOrientation(LinearLayout.VERTICAL);

        String answerType = bean.getAnswer_type();
        if (Global.AT_TEXT.equals(answerType)) {
            return this.generateTextAnswer(activity, bean, number);
        }
        //Glen 17 Oct 2014, new type Calculation
        else if (Global.AT_CALCULATION.equals(answerType)) {
            return this.generateTextAnswer(activity, bean, number);
        } else if (Global.AT_GPS.equals(answerType)) {
            return this.generateLocationAnswer(activity, bean, number);
        } else if (Global.AT_GPS_N_LBS.equals(answerType)) {
            return this.generateLocationAnswer(activity, bean, number);
        } else if (Global.AT_MULTIPLE.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean, number);
        } else if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean, number);
        } else if (Global.AT_RADIO.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean, number);
        } else if (Global.AT_RADIO_W_DESCRIPTION.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean, number);
        } else if (Global.AT_DROPDOWN.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean, number);
        } else if (Global.AT_DROPDOWN_W_DESCRIPTION.equals(answerType)) {
            return this.generateOptionsAnswer(activity, bean, number);
        } else if (Global.AT_CURRENCY.equals(answerType)) {
            return this.generateTextAnswer(activity, bean, number);
        } else if (Global.AT_NUMERIC.equals(answerType)) {
            return this.generateTextAnswer(activity, bean, number);
        } else if (Global.AT_DECIMAL.equals(answerType)) {
            return this.generateTextAnswer(activity, bean, number);
        } else if (Global.AT_DATE.equals(answerType)) {
            return this.generateDateTimeAnswer(activity, bean, number, TYPE_DATE);
        } else if (Global.AT_TIME.equals(answerType)) {
            return this.generateDateTimeAnswer(activity, bean, number, TYPE_TIME);
        } else if (Global.AT_DATE_TIME.equals(answerType)) {
            return this.generateDateTimeAnswer(activity, bean, number, TYPE_DATE_TIME);
        } else if (Tool.isImage(answerType)) {
            return this.generateImageQuestion(activity, bean, number);
        } else if (Global.AT_LOV.equals(answerType)) {
            return this.generateLookupAnswer(activity, bean, number);
        } else if (Global.AT_LOV_W_FILTER.equals(answerType)) {
            return this.generateLookupAnswer(activity, bean, number);
        } else if (Global.AT_DRAWING.equals(answerType)) {
            return this.generateImageQuestion(activity, bean, number);
        } else {
            return null;
        }
    }
}
