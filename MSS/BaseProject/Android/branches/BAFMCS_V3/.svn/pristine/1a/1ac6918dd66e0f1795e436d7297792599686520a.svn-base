package com.adins.mss.base.dynamicform.form.questions;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    public DatePickerFragment(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        Date sysdate = Tool.getSystemDateTime();
        String dt = Formatter.formatDate(sysdate, Global.DATE_STR_FORMAT);
        String[] temp1 = dt.split("/");
        int dayOfMonth = Integer.parseInt(temp1[0]);
        int month = Integer.parseInt((temp1[1])) - 1;
        int year = Integer.parseInt(temp1[2]);

        return new DatePickerDialog(getActivity(), listener,
                year, month, dayOfMonth);
    }

}
