package com.adins.mss.base.dynamicform.form.questions;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;

import java.util.Date;

/**
 * Created by gigin.ginanjar on 31/08/2016.
 */
@SuppressLint("ValidFragment")
public class TimePickerFragment extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener listener;

    public TimePickerFragment(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        Date sysdate = Tool.getSystemDateTime();
        String tm = Formatter.formatDate(sysdate, Global.TIME_STR_FORMAT);
        String[] temp2 = tm.split(":");
        int hourOfDay = Integer.parseInt(temp2[0]);
        int minute = Integer.parseInt(temp2[1]);
        return new TimePickerDialog(getActivity(), listener, hourOfDay, minute, true);
    }
}
