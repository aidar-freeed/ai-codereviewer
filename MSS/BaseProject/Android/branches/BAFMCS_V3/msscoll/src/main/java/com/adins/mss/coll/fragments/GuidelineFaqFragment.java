package com.adins.mss.coll.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.coll.R;
import com.adins.mss.coll.models.GuidelineAdapter;
import com.adins.mss.coll.models.GuidelineFaqRequest;
import com.adins.mss.coll.models.loyaltymodels.DocumentListDetail;
import com.adins.mss.coll.models.loyaltymodels.GuidelineFaqResponse;
import com.adins.mss.constant.Global;
//import com.adins.mss.foundation.formatter.Base64;
import com.adins.mss.foundation.formatter.Tool;
import android.util.Base64;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;

public class GuidelineFaqFragment extends Fragment {

    RecyclerView guidelinesList;
    private static final String ARG_DOCUMENTS = "documents";
    private GuidelineAdapter guidelineAdapter;
    private List<DocumentListDetail> documentList;

    public static GuidelineFaqFragment newInstance(List<DocumentListDetail> documentListDetails) {
        GuidelineFaqFragment fragment = new GuidelineFaqFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DOCUMENTS, (Serializable) documentListDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_guideline_faq, container, false);
        guidelinesList = view.findViewById(R.id.guidelineRV);

        getActivity().setTitle(getString(R.string.title_mn_guideline_faq));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null){
            documentList = (List<DocumentListDetail>) getArguments().getSerializable(ARG_DOCUMENTS);
        }

        if(documentList != null){
            guidelineAdapter = new GuidelineAdapter(documentList, getContext());
            guidelinesList.setLayoutManager(new LinearLayoutManager(getContext()));
            guidelinesList.setAdapter(guidelineAdapter);
        }

    }

}
