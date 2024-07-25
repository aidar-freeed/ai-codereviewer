package com.adins.mss.coll.tool;

import java.lang.ref.WeakReference;
import java.util.List;

import org.apache.http.NameValuePair;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.dao.MobileContentD;
import com.adins.mss.dao.TaskD;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.coll.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class getImageTask extends AsyncTask<Void, Void, byte[]> {
	private ProgressDialog progressDialog;
	private String errMessage = null;
	private WeakReference<Activity> activity;
	static byte[] imagebyte =null;
	List<NameValuePair> params;
	public getImageTask(Activity activity, List<NameValuePair> params){
		this.activity = new WeakReference<Activity>(activity);
		this.params = params;
	}
	@Override
	protected void onPreExecute() {
    	progressDialog = ProgressDialog.show(activity.get(),
				"", activity.get().getString(R.string.progressWait), true);
	}

	@Override
	protected byte[] doInBackground(Void... arg0) {
		byte[] imageResult =null;
		//FIXME : Tambahin buat ngirim datanya and nerima balikannya
		
			
		return imageResult;
	}
	
	@Override
	protected void onPostExecute(byte[] imageResult){
		if (progressDialog.isShowing()){
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				FireCrash.log(e);
			}
		}
		if(null==imageResult){
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity.get());
			dialogBuilder.withTitle("INFO")
				.withMessage(activity.get().getString(R.string.no_image))
				.show();
		}else{
//			Bitmap largeIcon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.dummy);
//	        imagebyte = Utils.bitmapToByte(largeIcon);
	        
//			Gson gson = new GsonBuilder().setDateFormat("ddMMyyyyHHmmss").create();
//			TaskD taskD = gson.fromJson(result, TaskD.class);
//			imagebyte = taskD.getImage();
		}
	}
}
