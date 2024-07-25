package com.adins.mss.coll.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.loyalti.mypointdashboard.DashboardMyPoint;
import com.adins.mss.coll.NewMCMainActivity;
import com.adins.mss.coll.R;
import com.adins.mss.coll.dummy.MyDashboardItemDummyAdapter;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpViewDummy;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.adins.mss.coll.loyalti.pointacquisitionmonthly.MonthlyPointsChartView;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DashBoardFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    MyDashBoardItemRecyclerViewAdapter adapter;
    private FirebaseAnalytics screenName;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DashBoardFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DashBoardFragment newInstance(int columnCount) {
        DashBoardFragment fragment = new DashBoardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenName = FirebaseAnalytics.getInstance(getActivity());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_item_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        getActivity().setTitle(getString(R.string.dashboard_competition_page_title));
        Global.positionStack.push(1);
        String listKompetisi = GlobalData.getSharedGlobalData().getUser().getListKompetisi();
        String url_header = GlobalData.getSharedGlobalData().getURL_GET_DETAILKOMPETISI();

        setupDashboardListener();

            adapter = new MyDashBoardItemRecyclerViewAdapter(mListener, getActivity());

            GetDetailListKompetisi getDetailListKompetisi = new GetDetailListKompetisi(recyclerView, adapter, this.getActivity());
            getDetailListKompetisi.setClassName(this.getClass().getSimpleName());
            getDetailListKompetisi.execute();
//        }
//        else {
//            try {
//                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
//                dialogBuilder.withTitle(getString(com.adins.mss.base.R.string.info_capital)).withMessage(getString(com.adins.mss.base.R.string.info_failgetdata))
//                        .withButton1Text(getString(com.adins.mss.base.R.string.btnOk))
//                        .setButton1Click(new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View arg0) {
//                                dialogBuilder.dismiss();
//                            }
//                        }).show();
//            } catch (Exception e) {
//                FireCrash.log(e);
//                e.printStackTrace();
//            }
////            Toast.makeText(getActivity(), "No competition found. Please re-sync to open your details achievement" , Toast.LENGTH_SHORT).show();
//        }



        // Set the adapter
//        if (view instanceof RecyclerView) {
//            Context context = view.getContext();
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
//        }
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
        FragmentTransaction transaction = NewMCMainActivity.fragmentManager.beginTransaction();
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
        if(checkUserHelpAvailability()){
            menu.findItem(R.id.mnGuide).setVisible(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.mnGuide){
            if(!Global.BACKPRESS_RESTRICTION) {
                recyclerView.setAdapter(new MyDashboardItemDummyAdapter(this.getActivity(),recyclerView, adapter));
            }
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
    public interface    OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Bundle item);

    }


    private boolean checkUserHelpAvailability() {
        List<UserHelpViewDummy> userHelpViews = Global.userHelpDummyGuide.get(DashBoardFragment.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpViews != null;
    }
//    private void getDetailKompetisi(final Context activity ) {
//        new AsyncTask<Void, Void, String>() {
//            String errMsg = "";
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                progressDialog = ProgressDialog.show(activity, "", "Please Wait", true);
//
//            }
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                if (Tool.isInternetconnected(activity)) {
//
//                    Gson gson = new GsonBuilder().setDateFormat("ddMMyyyyHHmmss").registerTypeHierarchyAdapter(byte[].class,
//                            new GsonHelper.ByteArrayToBase64TypeAdapter()).create();
//                    String result;
//                    DetailKompetisiRequest requestType = new DetailKompetisiRequest();
//                    ArrayList<String> data = null;
//                    data = new ArrayList<>();
//                    for(int i = 0 ; i < beanResps.getBeanResp().size() ; i++){
//                        data.add(beanResps.getBeanResp().get(i).getMembershipProgramCode());
//                     }
//                    requestType.setMEMBERSHIP_PROGRAM_CODE(data);
//                    requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
////                    requestType.addItemToUnstructured(new KeyValue("imei", GlobalData.getSharedGlobalData().getImei()), false);
//
//                    String json = GsonHelper.toJson(requestType);
//                    String url = GlobalData.getSharedGlobalData().getURL_GET_DETAILKOMPETISI();
//                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
//                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
//                    HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
//                    HttpConnectionResult serverResult = null;
//                    try {
//                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        errMsg = e.getMessage();
//                        return errMsg;
//                    }
//
//                    if(serverResult.getStatusCode() == 200){
//                        try {
//                            DetailKompetisiResponse response = gson.fromJson(serverResult.getResult(), DetailKompetisiResponse.class);
//                            if (serverResult != null) {
//
//                                dataDetail = new DetailKompetisiResponse();
//                                dataDetail = response;
//                            }
//                        } catch (Exception e) {
//                            return e.getMessage();
//                        }
//                    }else {
//
//                    }
//
//                    return errMsg;
//                } else {
//                    return errMsg;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                progressDialog.dismiss();
//                setupDashboardListener();
//                recyclerView.setAdapter(new MyDashBoardItemRecyclerViewAdapter(beanResps.getBeanResp(), dataDetail, mListener,getActivity()));
//                if (errMsg.length() != 0) {
//                    Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show();
//                }
//            }
//        }.execute();
//    }
    }
