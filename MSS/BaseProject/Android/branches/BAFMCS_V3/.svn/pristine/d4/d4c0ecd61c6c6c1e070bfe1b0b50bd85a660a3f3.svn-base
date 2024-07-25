package com.adins.mss.base.checkin.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.R;
import com.adins.mss.base.util.Utility;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

public class CheckInActivity extends Fragment {

    public Fragment fragment = this;
    private CheckInView view;
    private FirebaseAnalytics screenName;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        setHasOptionsMenu(true);
        view = new CheckInView(getActivity());
        view.onAttach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        view.onOptionsItemSelected(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(getActivity());
        view.fragment = fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        view.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_attendance_in), null);

        getActivity().findViewById(R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(R.string.title_mn_absentin));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view.layoutInflater(inflater, container);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.freeMemory();
    }
}