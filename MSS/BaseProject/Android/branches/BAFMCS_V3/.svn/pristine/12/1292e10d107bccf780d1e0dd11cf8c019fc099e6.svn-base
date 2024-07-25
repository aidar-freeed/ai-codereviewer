package com.adins.mss.odr.accounts;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.adins.mss.base.dynamicform.form.ScrollingLinearLayoutManager;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ExpandableRecyclerView;
import com.adins.mss.base.todo.form.GetSchemeTask;
import com.adins.mss.base.util.Utility;
import com.adins.mss.dao.Account;
import com.adins.mss.foundation.db.dataaccess.AccountDataAccess;
import com.adins.mss.odr.MONewTaskActivity;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.adapter.AccountButtonAdapter;
import com.adins.mss.odr.accounts.adapter.AccountButtonOpportunitiesAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by muhammad.aap on 11/27/2018.
 */

public class AccountDetailFragment extends Fragment {
    private String uuidAccount;
    private Account account;
    private EditText txtAddress;
    private EditText txtPhone1;
    private EditText txtPhone2;
    private EditText txtPhone3;
    private Button btnNewProspect;
    ExpandableRecyclerView qRecyclerView;
    public static AccountButtonAdapter buttonOppAdapter;
    //private ExpandableListView btnRecyclerViewOpportunities;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    AccountButtonOpportunitiesAdapter listAdapter;
 //   public static AdapterBaru questionAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_detail, container, false);
        Bundle bundle = getArguments();

        String uuidAccount = bundle.getString("uuidAccount");
        account = AccountDataAccess.getOne(getActivity(), uuidAccount);

        txtAddress = (EditText) view.findViewById(R.id.txtAddressAccDetail);
        txtPhone1 = (EditText) view.findViewById(R.id.txtNumber1Acc);
        txtPhone2 = (EditText) view.findViewById(R.id.txtNumber2Acc);
        txtPhone3 = (EditText) view.findViewById(R.id.txtNumber3Acc);
        btnNewProspect = (Button) view.findViewById(R.id.btnNewProspectAcc);
       // btnRecyclerViewOpportunities = (ExpandableListView) view.findViewById(R.id.btnRecyclerViewOpportunitiesAcc);

        txtAddress.setText(account.getAccount_address());
        txtPhone1.setText(account.getAccount_phone_1());

        if (account.getAccount_phone_2() != null && !account.getAccount_phone_2().isEmpty())
            txtPhone2.setText(account.getAccount_phone_2());

        //if (account.getAccount_phone_3() != null && !account.getAccount_phone_3().isEmpty())
       //     txtPhone3.setText(account.getAccount_phone_3());

        btnNewProspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetSchemeTask task = new GetSchemeTask(getActivity(), new MONewTaskActivity(), true);
                task.execute();
            }
        });

       // questionAdapter = new QuestionViewAdapter(getActivity(), qRecyclerView, listOfQuestionGroup, listOfQuestionBean, this);
    //    btnRecyclerViewOpportunities.setAdapter(questionAdapter);

        // preparing list data
        prepareListData();

        listAdapter = new AccountButtonOpportunitiesAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
//        btnRecyclerViewOpportunities.setAdapter(listAdapter);
//
//        // Listview Group click listener
//        btnRecyclerViewOpportunities.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v,
//                                        int groupPosition, long id) {
//                // Toast.makeText(getApplicationContext(),
//                // "Group Clicked " + listDataHeader.get(groupPosition),
//                // Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
//
//        // Listview Group expanded listener
//        btnRecyclerViewOpportunities.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getActivity(),
//                        listDataHeader.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Listview Group collasped listener
//        btnRecyclerViewOpportunities.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getActivity(),
//                        listDataHeader.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        // Listview on child click listener
//        btnRecyclerViewOpportunities.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition, int childPosition, long id) {
//                // TODO Auto-generated method stub
//                Toast.makeText(
//                        getActivity(),
//                        listDataHeader.get(groupPosition)
//                                + " : "
//                                + listDataChild.get(
//                                listDataHeader.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT)
//                        .show();
//                return false;
//            }
//        });

        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            qRecyclerView = (ExpandableRecyclerView) view.findViewById(R.id.btnOpportunities);

            int duration = getResources().getInteger(com.adins.mss.base.R.integer.scroll_duration);
            qRecyclerView.setLayoutManager(new ScrollingLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false, duration));


          //  buttonOppAdapter = new AccountButtonAdapter(getActivity(), qRecyclerView, listOfQuestionBean, this);
            qRecyclerView.setAdapter(buttonOppAdapter);
        }

        return view;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataHeader.add("Opportunities");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        listDataChild.put(listDataHeader.get(0), nowShowing);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(com.adins.mss.base.R.string.header_mn_account_detail));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
