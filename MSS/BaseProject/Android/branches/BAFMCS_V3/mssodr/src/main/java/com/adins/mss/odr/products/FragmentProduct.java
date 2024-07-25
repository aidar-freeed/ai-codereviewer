package com.adins.mss.odr.products;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.util.Utility;
import com.adins.mss.dao.Product;
import com.adins.mss.foundation.db.dataaccess.ProductDataAccess;
import com.adins.mss.odr.R;
import com.adins.mss.odr.products.adapter.ProductListAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.List;

/**
 * Created by muhammad.aap on 11/15/2018.
 */

public class FragmentProduct extends Fragment {
    private List<Product> productList;
    private RecyclerView list;
    private ProductListAdapter adapter;
    private FirebaseAnalytics screenName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenName = FirebaseAnalytics.getInstance(getActivity());
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        list = (RecyclerView) view.findViewById(R.id.productList);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        list.setHasFixedSize(true);
        list.setLayoutManager(layoutManager);

        productList = ProductDataAccess.getAllByIsActive(getActivity());
        adapter = new ProductListAdapter(getActivity(), productList);
        list.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_marketing_report), null);
        getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_products));
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
