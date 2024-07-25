package com.adins.mss.foundation.questiongenerator;


import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.adins.mss.foundation.formatter.Tool;

public class DateInputListener {
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String month = Tool.appendZeroForDateTime(monthOfYear, true);
            String dt = dayOfMonth + "/" + month + "/" + year;
            DynamicQuestion.setTxtInFocusText(dt);
        }
    };

    public DateInputListener() {
    }

    public DatePickerDialog.OnDateSetListener getmDateSetListener() {
        return mDateSetListener;
    }
}
