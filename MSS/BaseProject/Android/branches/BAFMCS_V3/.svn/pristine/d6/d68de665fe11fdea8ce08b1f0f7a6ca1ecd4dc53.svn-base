package com.adins.mss.odr.news;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.dao.MobileContentD;
import com.adins.mss.dao.MobileContentH;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.odr.R;
import com.androidquery.AQuery;

import org.acra.ACRA;

import java.util.List;
import java.util.Locale;

public class NewsContentActivity extends Activity{
	MobileContentH objects;
	List<MobileContentD> objectDetail = null;
	News news;
	String uuidH;
	ViewPager viewPager;
	AQuery query;
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mobile_content_layout);
	    ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
	    Bundle bundle =  getIntent().getExtras();
        uuidH =bundle.getString("uuid_taskH");
        
	    viewPager = (ViewPager) findViewById(R.id.newsImage);
		news= new News(this);
		
		objects = news.getContent(uuidH);
//		news.getNewsContentFromServer(objects);
		
		query = new AQuery(this);
		query.id(R.id.newsDesc).text(objects.getContent_description());
    	objectDetail = news.getlistContentOnDate(uuidH);
    	
    	List<MobileContentD> allObjectDetail = news.getlistContentWithoutDate(uuidH);
    	
	    NewsContentAdapter adapter = new NewsContentAdapter(this, objectDetail);
	    viewPager.setAdapter(adapter);
	    if(objectDetail==null || objectDetail.size()==0)
	    if(allObjectDetail!=null&&allObjectDetail.size()>0){
	    	NiftyDialogBuilder dialogBuilder;
			dialogBuilder = NiftyDialogBuilder.getInstance(this);
			dialogBuilder.withTitle("INFO").
			withIcon(android.R.drawable.ic_dialog_info).
			withMessage(getString(R.string.data_expired)).show();
	    }
	  }

	@Override
	protected void attachBaseContext(Context newBase) {
		Context context = newBase;
		Locale locale;
		try {
			locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
			if (null == locale) {
				locale = new Locale(LocaleHelper.ENGLSIH);
			}
			context = LocaleHelper.wrap(newBase, locale);
		} catch (Exception e) {
			locale = new Locale(LocaleHelper.ENGLSIH);
			context = LocaleHelper.wrap(newBase, locale);
		} finally {
			super.attachBaseContext(context);
		}
	}
}
