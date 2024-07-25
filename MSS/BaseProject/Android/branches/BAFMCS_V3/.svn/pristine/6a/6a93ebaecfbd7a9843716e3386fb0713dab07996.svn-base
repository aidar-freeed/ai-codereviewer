package com.adins.mss.odr.news;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adins.mss.dao.MobileContentD;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.odr.R;

import java.util.List;

public class NewsContentAdapter extends PagerAdapter {
	public List<MobileContentD> objects;
	Activity activity;
    public NewsContentAdapter(Activity activity, List<MobileContentD> objects){
    	this.activity=activity;
    	this.objects=objects;
    }
    @Override
    public int getCount() {
    	int count = 1;
    	if(0!=objects.size())
    		count=objects.size();
      return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
    	((ImageView) object).setBackgroundColor(Color.WHITE);
      return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
	      ImageView imageView = new ImageView(activity);
	      try {
	    	  MobileContentD contentD = objects.get(position);
	          final byte[] imagebyte = contentD.getContent();
	          final Bitmap image = Utils.byteToBitmap(imagebyte);
	          
	          int padding = activity.getResources().getDimensionPixelSize(R.dimen.padding_medium);
	          imageView.setPadding(padding, padding, padding, padding);
	          imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	
	          imageView.setImageBitmap(image);
		} catch (Exception e) {
	      	imageView.setImageResource(R.drawable.img_notavailable);
		}

      container.addView(imageView, 0);
      return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((ImageView) object);
    }
}
