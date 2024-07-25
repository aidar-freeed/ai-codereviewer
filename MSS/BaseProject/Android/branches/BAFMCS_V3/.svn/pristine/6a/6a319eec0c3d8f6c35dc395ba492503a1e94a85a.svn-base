package com.adins.mss.odr;

import java.io.File;

import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.sync.DefaultSynchronizeScheme;
import com.adins.mss.foundation.sync.Synchronize;
import com.adins.mss.foundation.sync.Synchronize.SynchronizeListener;
import com.adins.mss.foundation.sync.SynchronizeItem;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SynchronizeActivity extends Activity implements SynchronizeListener, Runnable, OnClickListener{

	ProgressBar progressBar;
	TextView progressLabel;
	
	File file = this.getDatabasePath("msm").getParentFile();
	String DB_PATH = file +"/";
	
	Synchronize synchronize;
	public SynchronizeActivity(){
		
	}
	
	public void startSync(){
		synchronize = new Synchronize();
		synchronize.setSynchronizeScheme(new DefaultSynchronizeScheme());
		synchronize.setListener(this);
		
		//TODO Bikin Task Buat Gabungin ke UI
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.synchronize_layout);
		
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		progressLabel = (TextView) findViewById(R.id.progressLabel);
		
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronize.startSynchronize();
	}

	@Override
	public void progressUpdated(float values) {
		// TODO Auto-generated method stub
		float progress = values;
		int progressInt = (int) progress;
		updateProgressBar(progressInt);
	}

	protected void updateProgressBar(int progress){	
		progressBar.setProgress(progress);
		progressLabel.setText("Progress : " + progress + "%");
	}
	@Override
	public void synchronizeFailed(SynchronizeItem arg0,
			HttpConnectionResult arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

}
