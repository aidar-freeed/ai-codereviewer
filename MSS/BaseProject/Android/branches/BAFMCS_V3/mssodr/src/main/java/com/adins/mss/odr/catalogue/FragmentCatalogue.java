package com.adins.mss.odr.catalogue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.adins.mss.base.util.Utility;
import com.adins.mss.dao.Catalogue;
import com.adins.mss.dao.MobileContentD;
import com.adins.mss.foundation.db.dataaccess.CatalogueDataAccess;
import com.adins.mss.foundation.db.dataaccess.MobileContentDDataAccess;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.odr.R;
import com.adins.mss.odr.catalogue.imageslider.FragmentSlider;
import com.adins.mss.odr.catalogue.imageslider.SliderIndicator;
import com.adins.mss.odr.catalogue.imageslider.SliderPagerAdapter;
import com.adins.mss.odr.catalogue.imageslider.SliderView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivia.dg on 11/28/2017.
 */

public class FragmentCatalogue extends Fragment {
    private List<MobileContentD> promoList;
    private List<Catalogue> catalogueList;

    private SliderView sliderView;
    private SliderPagerAdapter mAdapter;
    private SliderIndicator mIndicator;
    private LinearLayout mLinearLayout;

    private RecyclerView list;
    private RecyclerView.LayoutManager layoutManager;
    private CatalogueListAdapter adapter;
    private FirebaseAnalytics screenName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
        promoList = MobileContentDDataAccess.getAllContent(context);
        catalogueList = CatalogueDataAccess.getAll(context);
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
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);

        list = (RecyclerView) view.findViewById(R.id.catalogueList);
        layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        adapter = new CatalogueListAdapter(getActivity(), catalogueList);
        list.setAdapter(adapter);

        sliderView = (SliderView) view.findViewById(R.id.sliderView);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.pagesContainer);

        setupSlider();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_promo_catalogue), null);
        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_catalogue));
    }

    private void setupSlider() {
        List<Fragment> fragments = new ArrayList<>();
        if (promoList != null && promoList.size() != 0) {
            sliderView.setDurationScroll(800);
            sliderView.isVerticalScrollBarEnabled();
            for (MobileContentD content : promoList) {
                fragments.add(FragmentSlider.newInstance(content.getContent()));
            }
        } else {
            sliderView.setDurationScroll(0);
            Bitmap noPromo = BitmapFactory.decodeResource(getResources(), R.drawable.nopromotion);
            byte[] temp = Utils.bitmapToByte(noPromo);
            fragments.add(FragmentSlider.newInstance(temp));
        }
//        fragments.add(FragmentSlider.newInstance("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTHN2Mp-hEWSIUMgLVdnUwaP0V5x9dvpluFd8zsq0EMhzCQfsek"));
//        fragments.add(FragmentSlider.newInstance("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTcYsl7qSRTZI9zw7DnYxiGfv9foj6s7OpKek121ZFziqWGWjNi"));
//        fragments.add(FragmentSlider.newInstance("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQDEtJebJpj6EPoFzeaeMb1WdqyyawvyMpB59sMkdfW2HH0BXYp"));

        mAdapter = new SliderPagerAdapter(getFragmentManager(), fragments);
        sliderView.setAdapter(mAdapter);
        mIndicator = new SliderIndicator(getContext(), mLinearLayout, sliderView, R.drawable.indicator_circle, true);
        mIndicator.setPageCount(fragments.size());
        try {
            mIndicator.show();
        } catch (Exception e) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        mIndicator.cleanup();
    }
}
