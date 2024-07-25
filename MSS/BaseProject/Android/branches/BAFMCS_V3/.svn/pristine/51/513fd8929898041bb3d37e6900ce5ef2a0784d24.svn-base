package com.adins.mss.odr.update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.odr.R;

import org.apache.http.NameValuePair;

import java.util.List;

public class RejectOrderTask extends AsyncTask<Void, Void, String>{
	private ProgressDialog progressDialog;
	private String errMessage = null;
	private Activity activity;
	List<NameValuePair> params;
	public RejectOrderTask (Activity activity, List<NameValuePair> params){
		this.activity = activity;
		this.params = params;
	}
	@Override
	protected void onPreExecute() {
    	progressDialog = ProgressDialog.show(activity,
				"", activity.getString(R.string.progressWait), true);
	}
	@Override
	protected String doInBackground(Void... params) {
		//FIXME : Tambahin buat ngirim datanya and nerima balikannya
		String result="";
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			errMessage = activity.getString(R.string.cancel_order_error);
		}
		return result;
	}
	@Override
	protected void onPostExecute(String result){
		if (progressDialog.isShowing()){
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
			}
		}
//		if(null==result || "".equals(result)){
//			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
//			dialogBuilder.withTitle("Error")
//				.withMessage(errMessage)
//				.show();
//		}else{
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
			dialogBuilder.withTitle(activity.getString(R.string.success))
				.withMessage(activity.getString(R.string.order_rejected))
				.isCancelable(false)
				.withButton1Text(activity.getString(R.string.btnOk))
				.setButton1Click(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						activity.finish();
					}
				})
				.show();
//		}
	}
}
