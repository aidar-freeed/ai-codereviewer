package com.adins.mss.foundation.questiongenerator;

import android.app.TimePickerDialog;
import android.widget.TimePicker;

import com.adins.mss.foundation.formatter.Tool;

public class TimeInputListener {
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hour = Tool.appendZeroForDateTime(hourOfDay, false);
            String min = Tool.appendZeroForDateTime(minute, false);
            DynamicQuestion.setTxtInFocusText(hour + ":" + min);
        }
    };

    public TimeInputListener() {
    }

    public TimePickerDialog.OnTimeSetListener getmTimeSetListener() {
        return mTimeSetListener;
    }
}
