package com.adins.mss.odr.news;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.dao.MobileContentH;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.odr.R;

import org.acra.ACRA;

import java.util.List;
import java.util.Locale;

public class NewsChildActivity extends Activity implements OnItemClickListener{

	private NewsListAdapter adapter;
	List<MobileContentH> objects;
	ListView listView;
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.default_listview);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        Bundle bundle =  getIntent().getExtras();
        String uuidP =bundle.getString("uuid_parent");
        
        listView = (ListView)findViewById(android.R.id.list);
        News news= new News(this);
        objects = news.getlistNewsChild(uuidP);
       
        adapter = new NewsListAdapter(this, objects);
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE ); 
//        View notFoundView = layoutInflater.inflate(R.layout.news_without_content_layout, null, false);
        
        if(objects!=null&&objects.size()>0){        	
            listView.setOnItemClickListener(this);
        }else{
        	List<MobileContentH> tempObjects = news.getlistNewsChildWithoutDate(uuidP);
        	if(tempObjects!=null&&tempObjects.size()>0){
        		NiftyDialogBuilder dialogBuilder;
        			dialogBuilder = NiftyDialogBuilder.getInstance(this);
        			dialogBuilder.withTitle("INFO").withIcon(android.R.drawable.ic_dialog_info).withMessage("Data has Expired").show();
        	}else{
	        	NewsContentTask task = new NewsContentTask(this, getString(R.string.progressWait), 
	        			getString(R.string.msgNoDetail), uuidP, true);
	    		task.execute();
        	}
        }
        listView.setAdapter(adapter);
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
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		NewsContentTask task = new NewsContentTask(this, getString(R.string.progressWait), getString(R.string.msgNoDetail), 
//				objects.get(position).getUuid_mobile_content_h());
//		task.execute();
		List<MobileContentH> objects2;
		News news= new News(this);
		objects2 = news.getlistNewsChild(objects.get(position).getUuid_mobile_content_h());
		if(objects2!=null&&objects2.size()>0){        	
			Intent intent = new Intent(this,NewsChildActivity.class);
			intent.putExtra("uuid_parent", objects.get(position).getUuid_mobile_content_h());
			listView.setEnabled(false);
			startActivity(intent);
        }else{
        	List<MobileContentH> tempObjects = news.getlistNewsChildWithoutDate(objects.get(position).getUuid_mobile_content_h());
        	if(tempObjects!=null&&tempObjects.size()>0){
        		Intent intent = new Intent(this,NewsChildActivity.class);
    			intent.putExtra("uuid_parent", objects.get(position).getUuid_mobile_content_h());
				listView.setEnabled(false);
    			startActivity(intent);
        	}else{
        		NewsContentTask task = new NewsContentTask(this, getString(R.string.progressWait), getString(R.string.msgNoDetail), 
        				objects.get(position).getUuid_mobile_content_h(),false);
        		task.execute();
        	}        	
        }		
	}

	@Override
	protected void onResume() {
		listView.setEnabled(true);
		super.onResume();
	}
}
