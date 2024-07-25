package com.github.jjobes.slidedatetimepicker;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.adins.mss.base.R;

import java.util.Date;

/**
 * The fragment for the first page in the ViewPager that holds
 * the {@link CustomDatePicker}.
 *
 * @author jjobes
 */
public class DateFragment extends Fragment {
    private DateChangedListener mCallback;
    private CustomDatePicker mDatePicker;
    public DateFragment() {
        // Required empty public constructor for fragment.
    }

    /**
     * Return an instance of DateFragment with its bundle filled with the
     * constructor arguments. The values in the bundle are retrieved in
     * {@link #onCreateView()} below to properly initialize the DatePicker.
     *
     * @param theme
     * @param year
     * @param month
     * @param day
     * @param minDate
     * @param maxDate
     * @return an instance of DateFragment
     */
    public static final DateFragment newInstance(int theme, int year, int month,
                                                 int day, Date minDate, Date maxDate) {
        DateFragment f = new DateFragment();

        Bundle b = new Bundle();
        b.putInt("theme", theme);
        b.putInt("year", year);
        b.putInt("month", month);
        b.putInt("day", day);
        b.putSerializable("minDate", minDate);
        b.putSerializable("maxDate", maxDate);
        f.setArguments(b);

        return f;
    }

    /**
     * Cast the reference to {@link SlideDateTimeDialogFragment}
     * to a {@link DateChangedListener}.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
//            mCallback = (DateChangedListener) getTargetFragment();
            mCallback = (DateChangedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement " +
                    "DateFragment.DateChangedListener interface");
        }
    }

    /**
     * Create and return the user interface view for this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int theme = getArguments().getInt("theme");
        int initialYear = getArguments().getInt("year");
        int initialMonth = getArguments().getInt("month");
        int initialDay = getArguments().getInt("day");
        Date minDate = (Date) getArguments().getSerializable("minDate");
        Date maxDate = (Date) getArguments().getSerializable("maxDate");

        // Unless we inflate using a cloned inflater with a Holo theme,
        // on Lollipop devices the DatePicker will be the new-style
        // DatePicker, which is not what we want. So we will
        // clone the inflater that we're given but with our specified
        // theme, then inflate the layout with this new inflater.

        Context contextThemeWrapper = new ContextThemeWrapper(
                getActivity(),
                theme == SlideDateTimePicker.HOLO_DARK ?
                        android.R.style.Theme_Holo :
                        android.R.style.Theme_Holo_Light);

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View v = localInflater.inflate(R.layout.fragment_date, container, false);

        mDatePicker = (CustomDatePicker) v.findViewById(R.id.datePicker);
        // block keyboard popping up on touch
        mDatePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        mDatePicker.init(
                initialYear,
                initialMonth,
                initialDay,
                new OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                        mCallback.onDateChanged(year, monthOfYear, dayOfMonth);
                    }
                });

        if (minDate != null)
            mDatePicker.setMinDate(minDate.getTime());

        if (maxDate != null)
            mDatePicker.setMaxDate(maxDate.getTime());

        return v;
    }

    /**
     * Used to communicate back to the parent fragment as the user
     * is changing the date spinners so we can dynamically update
     * the tab text.
     */
    public interface DateChangedListener {
        void onDateChanged(int year, int month, int day);
    }
}
