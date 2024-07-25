package com.adins.mss.odr.other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Keep;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.timeline.Constants;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.image.CroppingImage;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.odr.R;
import com.androidquery.AQuery;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class SettingActivity extends Fragment{
	AQuery query;
	@Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.setting_layout, container, false);
		query = new AQuery(view);
        
		query.id(R.id.btnChangeHeader).clicked(this, "changeHeader");
        query.id(R.id.btnChangeProfile).clicked(this, "changeProfile");
        
        if(query.id(R.id.profilePicture).getCachedImage(getActivity().getFilesDir()+"/imgProfile")!=null)
        	query.id(R.id.profilePicture).image(getActivity().getFilesDir()+"/imgProfile", true, true);            
        else
			query.id(R.id.profilePicture).image(R.drawable.profile_image);
		
        if(query.id(R.id.headerPicture).getCachedImage(getActivity().getFilesDir()+"/imgHeader")!=null)
        	query.id(R.id.headerPicture).image(getActivity().getFilesDir()+"/imgHeader", true, true).height(250);
        else
			query.id(R.id.headerPicture).image(R.drawable.dummy);

	//	query.id(R.id.edit_header).clicked(this, "changeHeader");
	//	query.id(R.id.editProfile).longClicked(this, "changeProfile");
        
		return view;
	}

	@Keep
	 public void changeHeader(View view){
		 Constants.flag_edit=0;
		 final NiftyDialogBuilder builder = new NiftyDialogBuilder(SettingActivity.this.getActivity());
		 builder.withButton1Text("Camera");
		 builder.withButton2Text("Gallery");
		 builder.setButton1Click(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 Intent cameraIntent = new Intent(v.getContext(), CameraPreviewActivity.class);
				 cameraIntent.putExtra("EDIT_MODE_IMAGE",0);
				 startActivityForResult(cameraIntent, 1);
				 builder.dismiss();
			 }
		 });

		 builder.setButton2Click(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 Crop.pickImage(getActivity(), SettingActivity.this);
				 builder.dismiss();
			 }
		 });
		 builder.show();
	 }

	@Keep
	 public void changeProfile(View view){
		 Constants.flag_edit=1;
		 final NiftyDialogBuilder builder = new NiftyDialogBuilder(SettingActivity.this.getActivity());
		 builder.withButton1Text("Camera");
		 builder.withButton2Text("Gallery");
		 builder.setButton1Click(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 Intent cameraIntent = new Intent(v.getContext(), CameraPreviewActivity.class);
				 cameraIntent.putExtra("EDIT_MODE_IMAGE", 1);
				 startActivityForResult(cameraIntent, 1);
				 builder.dismiss();
			 }
		 });

		 builder.setButton2Click(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 Crop.pickImage(getActivity(), SettingActivity.this);
				 builder.dismiss();
			 }
		 });
		 builder.show();
	 }
	
	 @Override
	 public void onResume(){
		 super.onResume();
		 if(query.id(R.id.profilePicture).getCachedImage(getActivity().getFilesDir()+"/imgProfile")!=null)
	        	query.id(R.id.profilePicture).image(getActivity().getFilesDir()+"/imgProfile", true, true);            
	        else
				query.id(R.id.profilePicture).image(R.drawable.profile_image);
			
	        if(query.id(R.id.headerPicture).getCachedImage(getActivity().getFilesDir()+"/imgHeader")!=null)
	        	query.id(R.id.headerPicture).image(getActivity().getFilesDir()+"/imgHeader", true, true).height(250);
	        else
				query.id(R.id.headerPicture).image(R.drawable.dummy);
	 }
	 
	 @Override
		public void onActivityResult(int requestCode, int resultCode, Intent result) {
	    	super.onActivityResult(requestCode, resultCode, result); 
			if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
				if(Constants.flag_edit==0){
					CroppingImage.beginCropImgHeader(result.getData(),getActivity(),SettingActivity.this);
				}else{
					CroppingImage.beginCropImgProfile(result.getData(),getActivity(),SettingActivity.this);
				}

	        } else if(requestCode == Utils.REQUEST_CAMERA) {
				if(Constants.flag_edit==0) {
					Uri outputUri = Uri.fromFile(new File(getActivity().getFilesDir(), "imgHeader"));
					CroppingImage.beginCrop(outputUri, getActivity());
				}else{
					Uri outputUri = Uri.fromFile(new File(getActivity().getFilesDir(), "imgProfile"));
					CroppingImage.beginCrop(outputUri, getActivity());
				}
			} else if (requestCode == Crop.REQUEST_CROP) {
//	        	Uri imgUri = CroppingImage.handleCrop(resultCode, result, getActivity());
			}
	    }
}
