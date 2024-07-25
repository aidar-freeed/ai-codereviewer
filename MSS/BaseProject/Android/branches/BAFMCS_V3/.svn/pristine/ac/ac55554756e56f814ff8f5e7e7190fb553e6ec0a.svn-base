package com.adins.mss.odr.reassignment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.constant.Global;
import com.adins.mss.odr.CheckOrderActivity;

import org.acra.ACRA;

public class OrderReassignmentActivity extends CheckOrderActivity{
	private int taskType;
	private Bundle mArguments;
	@Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        setHasOptionsMenu(false);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        mArguments = getArguments();        
        taskType = mArguments.getInt(Global.BUND_KEY_TASK_TYPE, 0);
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}
	
	@Override
	protected Intent getNextActivityIntent() {
		// TODO Auto-generated method stub
		super.getNextActivityIntent();
		Intent fragment = new Intent(getActivity(), OrderReassignmentResult.class);
		Bundle args = new Bundle();
        args.putString("startDate", txtStartDate.getText().toString());
        args.putString("endDate", txtEndDate.getText().toString());
        args.putString("nomorOrder", txtNomorOrder.getText().toString());
        args.putString("status", "1");
        fragment.putExtras(args);
        return fragment;
	}
	
	
}
