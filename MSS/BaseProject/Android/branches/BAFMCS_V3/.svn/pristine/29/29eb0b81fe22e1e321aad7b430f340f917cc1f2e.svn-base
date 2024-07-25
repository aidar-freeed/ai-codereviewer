package com.adins.mss.odr.accounts;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.dao.Account;
import com.adins.mss.dao.Product;
import com.adins.mss.foundation.db.dataaccess.ProductDataAccess;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.adapter.ContactListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivia.dg on 11/17/2017.
 */

public class ContactTabFragment extends Fragment {

    private FragmentActivity activity;
    private Account account;
    private RecyclerView list;
    private RecyclerView.LayoutManager layoutManager;
    public ContactListAdapter adapter;
    private String uuidAccount;
    private List<Product> listProduct = new ArrayList<>();
    private ArrayList<String> uuidProducts = new ArrayList<>();

    public ContactTabFragment(FragmentActivity activity, Account account, ArrayList<String> products) {
        this.activity = activity;
        this.account = account;
        this.uuidProducts = products;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_contact_tab, container, false);

        list = (RecyclerView) view.findViewById(R.id.list);
        layoutManager = new LinearLayoutManager(activity);
        list.setLayoutManager(layoutManager);

        setHasOptionsMenu(true);

//        listContact = ContactDataAccess.getAllByAccount(activity, account.getUuid_account());
        if (uuidProducts != null) {
            for (String uuid : uuidProducts) {
                Product product = ProductDataAccess.getOne(getActivity(), uuid);
                listProduct.add(product);
            }
        }

        adapter = new ContactListAdapter(getActivity(), account, listProduct);
        list.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(getString(R.string.title_mn_account));

        adapter = new ContactListAdapter(activity, account, listProduct);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
