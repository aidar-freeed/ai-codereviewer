package com.adins.mss.base.todolist.form;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.todaysplan.TodayPlanHandler;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.PlanTask;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.PlanTaskDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapsViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsViewerFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static View view;
    public List<TaskH> listTaskH;
    public LatLngBounds lngBounds;
    SupportMapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private LatLng locationPoint;
    private HashMap<Marker, TaskH> taskHHashMap;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TodayPlanHandler todayPlanHandler;//reference to view or fragment that handles today plan

    public MapsViewerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsViewerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsViewerFragment newInstance(String param1, String param2) {
        MapsViewerFragment fragment = new MapsViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setTodayPlanHandler(TodayPlanHandler todayPlanHandler) {
        this.todayPlanHandler = todayPlanHandler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationPoint = new LatLng(0d, 0d);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        taskHHashMap = new LinkedHashMap<>();
        try {
            view = inflater.inflate(R.layout.maps_layout, container, false);
            getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
            getActivity().findViewById(R.id.search).setVisibility(View.GONE);
            getActivity().setTitle(getString(R.string.title_mn_map));
            initialize();
        } catch (Exception e) {
            FireCrash.log(e);
            initialize();
        }
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().findViewById(R.id.search).setVisibility(View.GONE);
        getActivity().setTitle(getString(R.string.title_mn_map));
        if (mGoogleMap != null)
            mGoogleMap.setOnInfoWindowClickListener(this);
    }

    private void initialize() {
        if (mGoogleMap == null) {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
            mapFragment.getMapAsync(this);
            if (mGoogleMap != null) {
                setupMaps();
            }
        }
        if (GlobalData.getSharedGlobalData().getUser() == null) {
            NewMainActivity.InitializeGlobalDataIfError(getActivity().getApplicationContext());
        }
    }

    private void setupMaps() {
        try {
            LocationInfo info = Global.LTM.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
            double mLatitude = Double.parseDouble(info.getLatitude());
            double mLongitude = Double.parseDouble(info.getLongitude());
            locationPoint = new LatLng(mLatitude, mLongitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(locationPoint);
            markerOptions.title("Your Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mGoogleMap.addMarker(markerOptions);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    private void showAddTaskPlanDialog(final TaskH taskH, final Marker marker){
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        dialogBuilder.withTitle(getActivity().getString(R.string.info_capital))
                .withMessage(getString(R.string.add_customer_to_todays_plan, taskH.getCustomer_name()))
                .withButton1Text(getActivity().getString(R.string.btnYes))
                .withButton2Text(getActivity().getString(R.string.btnCancel))
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if(todayPlanHandler != null){
                            List<TaskH> planTaskHs = new ArrayList<>();
                            planTaskHs.add(taskH);
                            try {
                                todayPlanHandler.addToPlan(planTaskHs);
                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            }catch (Exception e){

                                dialogBuilder.dismiss();
                            }

                        }
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        TaskH task = taskHHashMap.get(marker);
        if (task != null) {
            if(Global.PLAN_TASK_ENABLED){
                List<PlanTask> planTask = PlanTaskDataAccess.findPlanByTaskH(getActivity(),task.getUuid_task_h());
                if(planTask.size() == 0){
                    showAddTaskPlanDialog(task, marker);
                    return;
                }
                if(!Global.isPlanStarted()){
                    Toast.makeText(getActivity(), getString(R.string.pls_start_visit), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            NewMainActivity.fragmentManager.popBackStack();
            SurveyHeaderBean header = new SurveyHeaderBean(task);
            CustomerFragment fragment = CustomerFragment.create(header);
            FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
            transaction.replace(R.id.content_frame, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        menu.findItem(R.id.mnGuide).setVisible(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        setupMaps();
        List<TaskH> taskHList = ToDoList.getListTaskInPriority(getActivity(), ToDoList.SEARCH_BY_ALL, null);
        if (taskHList != null && taskHList.size() > 0) {

            LatLngBounds.Builder bounds = new LatLngBounds.Builder();
            for (final TaskH task : taskHList) {
                try {
                    double latitude = 0;
                    double longitude = 0;
                    boolean isTaskPlan = false;
                    String sLatitude = task.getLatitude();
                    String sLongitude = task.getLongitude();
                    if (sLatitude != null && !sLatitude.equals("0.0") && !sLatitude.equals("null")) {
                        latitude = Double.parseDouble(task.getLatitude());
                    }
                    if (sLongitude != null && !sLongitude.equals("0.0") && !sLongitude.equals("null")) {
                        longitude = Double.parseDouble(task.getLongitude());
                    }
                    if (latitude != 0 || longitude != 0) {

                        //check if task is task plan
                        if(Global.PLAN_TASK_ENABLED){
                            TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                            if(todayPlanRepo != null){
                                PlanTask planTask = todayPlanRepo.getPlanTaskByTaskH(task.getTask_id());
                                if(planTask != null)
                                    isTaskPlan = true;
                            }
                        }

                        LatLng latLng = new LatLng(latitude, longitude);//new LatLng(-6.195274, 106.778008);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(task.getCustomer_name());
                        markerOptions.snippet(task.getCustomer_phone() + "\r\n" + task.getCustomer_address());
                        if(isTaskPlan){
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }
                        else {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        }

                        bounds.include(latLng);
                        Marker marker = mGoogleMap.addMarker(markerOptions);
                        taskHHashMap.put(marker, task);
                        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {

                                View v = getActivity().getLayoutInflater().inflate(R.layout.marker, null);
                                TextView infoTitle = (TextView) v.findViewById(R.id.infoTitle);
                                infoTitle.setText(marker.getTitle());
                                TextView infoSnippet = (TextView) v.findViewById(R.id.infoSnippet);
                                infoSnippet.setText(marker.getSnippet());

                                return v;
                            }
                        });
                    }
                } catch (Exception e) {
                    FireCrash.log(e);

                }
            }
            bounds.include(locationPoint);
            lngBounds = bounds.build();
        }
        final View view = mapFragment.getView();
        if (view.getViewTreeObserver().isAlive()) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation") // We use the new method when supported
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    if (mGoogleMap != null) {
                        try {
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(lngBounds, 50));
                        } catch (Exception e) {
                            FireCrash.log(e);
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationPoint, 12));
                        }
                    }
                }
            });
        }
        mGoogleMap.setOnInfoWindowClickListener(this);
    }
}
