package com.adins.mss.base.checkout.activity;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.formatter.Formatter;


public class CheckOutResultDialog extends DialogFragment {

    private TextView title;
    private TextView time;
    private TextView date;

    public CheckOutResultDialog() {
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_MinWidth);
    }

    public static CheckOutResultDialog newInstance(String title, String message, LocationInfo locationInfo) {
        CheckOutResultDialog frag = new CheckOutResultDialog();
        String time = Formatter.formatDate(locationInfo.getHandset_time(), Global.TIME_STR_FORMAT);
        String date = Formatter.formatDate(locationInfo.getHandset_time(), Global.DATE_STR_FORMAT3);

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("time", time);
        args.putString("date", date);
        frag.setArguments(args);
        return frag;
    }

    public static CheckOutResultDialog newInstance(String title, LocationInfo locationInfo) {
        CheckOutResultDialog frag = new CheckOutResultDialog();
        String time = Formatter.formatDate(locationInfo.getHandset_time(), Global.TIME_STR_FORMAT);
        String date = Formatter.formatDate(locationInfo.getHandset_time(), Global.DATE_STR_FORMAT3);

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("time", time);
        args.putString("date", date);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_absent_layout, container);
        title = (TextView) view.findViewById(R.id.lbl_title);
        time = (TextView) view.findViewById(R.id.txt_time);
        date = (TextView) view.findViewById(R.id.txt_date);
        String mtitle = getArguments().getString("title");
        String mTime = getArguments().getString("time");
        String mDate = getArguments().getString("date");

        title.setText(mtitle);
        time.setText(mTime);
        date.setText(mDate);

        return view;
    }

}
