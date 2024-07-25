package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.form.DynamicQuestionActivity;
import com.adins.mss.base.dynamicform.form.questions.DatePickerFragment;
import com.adins.mss.base.dynamicform.form.questions.TimePickerFragment;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gigin.ginanjar on 31/08/2016.
 */
public class DateTimeQuestionViewHolder extends RecyclerView.ViewHolder
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final int TYPE_DATE = 1;
    public static final int TYPE_TIME = 2;
    public static final int TYPE_DATE_TIME = 3;

    Context mContext;
    public QuestionView mView;
    public TextView mQuestionLabel;
    public EditText mQuestionAnswer;
    public Button mButtonSetDate;
    public QuestionBean bean;
    private SlideDateTimeListener dateTimeListener;

    public DateTimeQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionDtmLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionDtmLabel);
        mQuestionAnswer = (EditText) itemView.findViewById(R.id.questionDtmAnswer);
        mButtonSetDate = (Button) itemView.findViewById(R.id.btnSetDtm);
        setDateTimeListener();
    }

    public DateTimeQuestionViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        mView = (QuestionView) itemView.findViewById(R.id.questionDtmLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionDtmLabel);
        mQuestionAnswer = (EditText) itemView.findViewById(R.id.questionDtmAnswer);
        mButtonSetDate = (Button) itemView.findViewById(R.id.btnSetDtm);
        setDateTimeListener();
    }

    private void setDateTimeListener() {
        dateTimeListener = new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Date date) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(date.getTime());
                cal.set(cal.SECOND, 0);
                SimpleDateFormat mFormatter = new SimpleDateFormat(Global.DATE_TIME_STR_FORMAT);
                String dt = mFormatter.format(cal.getTime());
                mQuestionAnswer.setText(dt);
                String format = Global.DATE_TIME_STR_FORMAT;
                Date date2 = null;
                try {
                    date2 = Formatter.parseDate(dt, format);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String finalAnswer = Formatter.formatDate(date2, Global.DATE_STR_FORMAT_GSON);
                if (bean.getAnswer() != null && !bean.getAnswer().equals(finalAnswer)) {
                    bean.setIsCanChange(true);
                    bean.setChange(true);
                } else {
                    bean.setChange(false);
                }
                bean.setAnswer(finalAnswer);
            }
        };
    }

    public void bind(final QuestionBean item, int number) {
        bean = item;
        String answerType = bean.getAnswer_type();
        int type = TYPE_DATE;
        String questionLabel = number + ". " + bean.getQuestion_label();
        String format = null;
        String btnLabel = null;
        String answer = bean.getAnswer();

        mQuestionLabel.setText(questionLabel);

        View.OnClickListener listener = null;

        if (Global.AT_DATE.equals(answerType)) {
            type = TYPE_DATE;
            format = Global.DATE_STR_FORMAT;
            btnLabel = mContext.getString(R.string.btnDate);
            listener = new View.OnClickListener() {
                public void onClick(View v) {
                    DatePickerFragment datePickerFragment = new DatePickerFragment(DateTimeQuestionViewHolder.this);
                    datePickerFragment.show(DynamicQuestionActivity.fragmentManager, "TYPE_DATE");
                }
            };
        } else if (Global.AT_TIME.equals(answerType)) {
            type = TYPE_TIME;
            format = Global.TIME_STR_FORMAT;
            btnLabel = mContext.getString(R.string.btnTime);
            listener = new View.OnClickListener() {
                public void onClick(View v) {
                    TimePickerFragment timePickerFragment = new TimePickerFragment((DateTimeQuestionViewHolder.this));
                    timePickerFragment.show(DynamicQuestionActivity.fragmentManager, "TYPE_TIME");
                }
            };
        } else if (Global.AT_DATE_TIME.equals(answerType)) {
            type = TYPE_DATE_TIME;
//            format = Global.DATE_TIMESEC_STR_FORMAT;
            format = Global.DATE_TIME_STR_FORMAT;
            btnLabel = mContext.getString(R.string.btnDate);
            listener = new View.OnClickListener() {
                public void onClick(View v) {
                    new SlideDateTimePicker.Builder(DynamicQuestionActivity.fragmentManager)
                            .setListener(dateTimeListener)
                            .setInitialDate(new Date())
                            .setIs24HourTime(true)
                            .build()
                            .show();
                }
            };
        } else {
            type = TYPE_DATE;
            format = Global.DATE_STR_FORMAT;
            btnLabel = mContext.getString(R.string.btnDate);
            listener = new View.OnClickListener() {
                public void onClick(View v) {
                    DatePickerFragment datePickerFragment = new DatePickerFragment(DateTimeQuestionViewHolder.this);
                    datePickerFragment.show(DynamicQuestionActivity.fragmentManager, "TYPE_DATE");
                }
            };
        }

        if (answer != null && !answer.isEmpty()) {
            try {
                Date date = null;
                if (type == TYPE_TIME) {
                    date = Formatter.parseDate(answer, Global.TIME_STR_FORMAT2);
                } else {
                    String newFormat = Global.DATE_STR_FORMAT;
                    //cek jawaban untuk penjagaan kondisi kondisi yang tidak diinginkan
                    if (answer.length() == 8) {
                        if (answer.contains("-")) {
                            newFormat = Global.DATE_STR_FORMAT1;
                        } else {
                            newFormat = Global.DATE_STR_FORMAT2;
                        }
                    } else if (answer.length() == 9 || answer.length() == 10 || answer.length() == 11) {
                        if ((answer.length() == 9 || answer.length() == 10) && answer.contains("-")) {
                            newFormat = Global.DATE_STR_FORMAT1;
                        } else if ((answer.length() == 11) && answer.contains("-")) {
                            newFormat = Global.DATE_STR_FORMAT5;
                        } else if (answer.contains(" ")) {
                            newFormat = Global.DATE_STR_FORMAT3;
                        } else if (answer.contains("/")) {
                            newFormat = Global.DATE_STR_FORMAT;
                        }
                    } else if (answer.length() == 12) {
                        newFormat = Global.DATE_STR_FORMAT4;
                    } else if (answer.length() == 14) {
                        newFormat = Global.DATE_STR_FORMAT_GSON;
                    }
                    else {
                        newFormat = Global.DATE_TIMESEC_STR_FORMAT;
                    }
                    date = Formatter.parseDate(answer, newFormat);
                }
                answer = Formatter.formatDate(date, format);
                mQuestionAnswer.setText(answer);
            } catch (Exception ex) {
                FireCrash.log(ex);
                try {
                    if (answer != null && answer.length() > 0) {
                        try {
                            long dtLong = Formatter.stringToDate(answer);
                            Date date = new Date(dtLong);
                            if (date != null) {
                                answer = Formatter.formatDate(date, format);
                            }
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                        if (answer != null && answer.length() > 0) {
                            mQuestionAnswer.setText(answer);
                        } else {
                            mQuestionAnswer.setText(format);
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }

        } else {
            mQuestionAnswer.setText(format);
        }
        mButtonSetDate.setText(btnLabel);
//        mButtonSetDate.setOnClickListener(listener);
        if (bean.isReadOnly()) {
            mButtonSetDate.setEnabled(false);
            mButtonSetDate.setVisibility(View.GONE);
            mQuestionAnswer.setEnabled(false);
        } else {
            mButtonSetDate.setEnabled(true);
            mButtonSetDate.setVisibility(View.VISIBLE);
        }
        mButtonSetDate.setOnClickListener(listener);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String month = Tool.appendZeroForDateTime(monthOfYear, true);
        String dt = Tool.appendZeroForDateTime(dayOfMonth, false) + "/" + month + "/" + year;
        mQuestionAnswer.setText(dt);
        String format = Global.DATE_STR_FORMAT;
        Date date = null;
        try {
            date = Formatter.parseDate(dt, format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String finalAnswer = Formatter.formatDate(date, Global.DATE_STR_FORMAT_GSON);
        if (bean.getAnswer() != null && !bean.getAnswer().equals(finalAnswer)) {
            bean.setIsCanChange(true);
            bean.setChange(true);
        } else {
            if (view.isShown())
                bean.setChange(false);
        }
        bean.setAnswer(finalAnswer);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String hour = Tool.appendZeroForDateTime(hourOfDay, false);
        String min = Tool.appendZeroForDateTime(minute, false);
        String dt = hour + ":" + min;
        mQuestionAnswer.setText(dt);
        String format = Global.TIME_STR_FORMAT;
        Date date = null;
        try {
            date = Formatter.parseDate(dt, format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String finalAnswer = Formatter.formatDate(date, Global.TIME_STR_FORMAT2);
        if (bean.getAnswer() != null && !bean.getAnswer().equals(finalAnswer)) {
            bean.setIsCanChange(true);
            bean.setChange(true);
        } else {
            bean.setChange(false);
        }
        bean.setAnswer(finalAnswer);
    }

}
