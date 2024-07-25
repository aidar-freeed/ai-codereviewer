package com.adins.mss.svy.tool;

import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.svy.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageThumbnail extends LinearLayout{

	ImageView imgView;
	TextView detailText;
	private Bitmap thumbnail = null;
	byte[] resultImg = null;
	

	public ImageThumbnail(Context context, int width, int height) {
		super(context);
		
		//Init container
		this.setOrientation(LinearLayout.VERTICAL);
		this.setGravity(Gravity.LEFT);
		
		//Init image thumbnail
		imgView = new ImageView(context);
		ViewGroup.LayoutParams imgLayout = new LayoutParams(width, height);
		imgView.setLayoutParams(imgLayout);
		imgView.setImageResource(R.drawable.ic_image);
		
		//Init detail text
		detailText = new TextView(context);
		detailText.setTextColor(Color.BLACK);
		
		this.addView(imgView);
//		this.addView(detailText);
	}
	public byte[] getResultImg() {
		return resultImg;
	}

	public void setResultImg(byte[] resultImg) throws Exception{
		this.resultImg = resultImg;
		
		try{
			Bitmap bm = Utils.byteToBitmap(resultImg);			
			int[] res = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
			Bitmap thumbnail = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
			imgView.setImageBitmap(thumbnail);			
			detailText.setText("   "+bm.getWidth()+" x " +bm.getHeight()+". Size "+ resultImg.length +" Bytes");
		}
		catch(Exception e){
			throw e;
		}
	}
	public void setImageResource(int res){
		imgView.setImageResource(res);
	}
}