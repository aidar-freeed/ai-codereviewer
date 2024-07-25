package com.github.jjobes.slidedatetimepicker;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.format.DateFormat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TimePicker;

import com.adins.mss.base.R;


/**
 * The fragment for the second page in the ViewPager that holds
 * the TimePicker.
 *
 * @author jjobes
 */
public class TimeFragment extends Fragment {
    private TimeChangedListener mCallback;
    private TimePicker mTimePicker;
    public TimeFragment() {
        // Required empty public constructor for fragment.
    }

    /**
     * Return an instance of TimeFragment with its bundle filled with the
     * constructor arguments. The values in the bundle are retrieved in
     * below to properly initialize the TimePicker.
     *
     * @param theme
     * @param hour
     * @param minute
     * @param isClientSpecified24HourTime
     * @param is24HourTime
     * @return
     */
    public static final TimeFragment newInstance(int theme, int hour, int minute,
                                                 boolean isClientSpecified24HourTime, boolean is24HourTime) {
        TimeFragment f = new TimeFragment();

        Bundle b = new Bundle();
        b.putInt("theme", theme);
        b.putInt("hour", hour);
        b.putInt("minute", minute);
        b.putBoolean("isClientSpecified24HourTime", isClientSpecified24HourTime);
        b.putBoolean("is24HourTime", is24HourTime);
        f.setArguments(b);

        return f;
    }

    /**
     * Cast the reference to {@link SlideDateTimeDialogFragment} to a
     * {@link TimeChangedListener}.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
//            mCallback = (TimeChangedListener) getTargetFragment();
            mCallback = (TimeChangedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement " +
                    "TimeFragment.TimeChangedListener interface");
        }
    }

    /**
     * Create and return the user interface view for this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int theme = getArguments().getInt("theme");
        int initialHour = getArguments().getInt("hour");
        int initialMinute = getArguments().getInt("minute");
        boolean isClientSpecified24HourTime = getArguments().getBoolean("isClientSpecified24HourTime");
        boolean is24HourTime = getArguments().getBoolean("is24HourTime");

        // Unless we inflate using a cloned inflater with a Holo theme,
        // on Lollipop devices the TimePicker will be the new-style
        // radial TimePicker, which is not what we want. So we will
        // clone the inflater that we're given but with our specified
        // theme, then inflate the layout with this new inflater.

        Context contextThemeWrapper = new ContextThemeWrapper(
                getActivity(),
                theme == SlideDateTimePicker.HOLO_DARK ?
                        android.R.style.Theme_Holo :
                        android.R.style.Theme_Holo_Light);

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View v = localInflater.inflate(R.layout.fragment_time, container, false);

        mTimePicker = (TimePicker) v.findViewById(R.id.timePicker);
        // block keyboard popping up on touch
        mTimePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mCallback.onTimeChanged(hourOfDay, minute);
            }
        });

        // If the client specifies a 24-hour time format, set it on
        // the TimePicker.
        if (isClientSpecified24HourTime) {
            mTimePicker.setIs24HourView(is24HourTime);
        } else {
            // If the client does not specify a 24-hour time format, use the
            // device default.
            mTimePicker.setIs24HourView(DateFormat.is24HourFormat(
                    getTargetFragment().getActivity()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(initialHour);
        } else {
            mTimePicker.setCurrentHour(initialHour);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setMinute(initialMinute);
        } else {
            mTimePicker.setCurrentMinute(initialMinute);
        }

        // Fix for the bug where a TimePicker's onTimeChanged() is not called when
        // the user toggles the AM/PM button. Only applies to 4.0.0 and 4.0.3.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            fixTimePickerBug18982();
        }

        return v;
    }

    /**
     * Workaround for bug in Android TimePicker where the onTimeChanged() callback
     * is not invoked when the user toggles between AM/PM. But we need to be able
     * to detect this in order to dynamically update the tab title properly when
     * the user toggles between AM/PM.
     * <p>
     * Registered as Issue 18982:
     * <p>
     * https://code.google.com/p/android/issues/detail?id=18982
     */
    private void fixTimePickerBug18982() {
        View amPmView = ((ViewGroup) mTimePicker.getChildAt(0)).getChildAt(3);

        if (amPmView instanceof NumberPicker) {
            ((NumberPicker) amPmView).setOnValueChangedListener(new OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    if (picker.getValue() == 1)  // PM
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (mTimePicker.getHour() < 12)
                                mTimePicker.setHour(mTimePicker.getHour() + 12);
                        } else {
                            if (mTimePicker.getCurrentHour() < 12) {
                                mTimePicker.setCurrentHour(mTimePicker.getCurrentHour() + 12);
                            }
                        }
                    } else  // AM
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (mTimePicker.getHour() >= 12)
                                mTimePicker.setHour(mTimePicker.getHour() - 12);
                        } else {
                            if (mTimePicker.getCurrentHour() >= 12)
                                mTimePicker.setCurrentHour(mTimePicker.getCurrentHour() - 12);
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mCallback.onTimeChanged(
                                mTimePicker.getHour(),
                                mTimePicker.getMinute());
                    } else {
                        mCallback.onTimeChanged(
                                mTimePicker.getCurrentHour(),
                                mTimePicker.getCurrentMinute()
                        );
                    }
                }
            });
        }
    }

    /**
     * Used to communicate back to the parent fragment as the user
     * is changing the time spinners so we can dynamically update
     * the tab text.
     */
    public interface TimeChangedListener {
        void onTimeChanged(int hour, int minute);
    }
}
