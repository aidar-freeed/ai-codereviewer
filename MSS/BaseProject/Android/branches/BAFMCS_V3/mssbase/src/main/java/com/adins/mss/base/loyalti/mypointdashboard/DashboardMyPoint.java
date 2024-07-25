package com.adins.mss.base.loyalti.mypointdashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.loyalti.monthlypointacquisition.MonthlyPointsChartView;
import com.adins.mss.base.loyalti.userhelpdummy.DashboardMyPointItemDummyAdapter;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpViewDummy;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DashboardMyPoint extends Fragment {

    private OnListFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    DashboardMyPointItemRecyclerViewAdapter adapter;
    private FirebaseAnalytics screenName;

    public DashboardMyPoint() {
        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_point_item_list, container, false);
        recyclerView = view.findViewById(R.id.itemList);
        getActivity().setTitle(getString(R.string.dashboard_competition_page_title));
        Global.positionStack.push(1);

        setupDashboardListener();

        adapter = new DashboardMyPointItemRecyclerViewAdapter(mListener, getActivity());

        GetDetailListKompetisi getDetailListKompetisi = new GetDetailListKompetisi(recyclerView, adapter, this.getActivity());
        getDetailListKompetisi.setClassName(this.getClass().getSimpleName());
        getDetailListKompetisi.execute();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_my_points), null);
    }

    private void setupDashboardListener(){
        mListener = new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Bundle item) {
                //dummy redirect without data: soon to be changed
                goToMonthlyPointView(item);
            }
        };
    }

    public void goToMonthlyPointView(Bundle bundle){
        MonthlyPointsChartView monthlyPointsChart = new MonthlyPointsChartView();
        monthlyPointsChart.setArguments(bundle);
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, monthlyPointsChart);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(needShowUserHelp()){
            menu.findItem(R.id.mnGuide).setVisible(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
            UserHelp.reloadUserHelp(getActivity(), DashboardMyPoint.class.getSimpleName());
            if (needShowUserHelp())
                recyclerView.setAdapter(new DashboardMyPointItemDummyAdapter(this.getActivity(),recyclerView, adapter));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Bundle item);
    }

    private boolean needShowUserHelp() {
        List<UserHelpViewDummy> userHelpViews = Global.userHelpDummyGuide.get(DashboardMyPoint.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpViews != null;
    }

}
