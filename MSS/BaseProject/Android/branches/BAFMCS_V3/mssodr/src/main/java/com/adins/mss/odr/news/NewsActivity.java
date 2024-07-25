package com.adins.mss.odr.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.MobileContentH;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.odr.R;
import com.adins.mss.odr.tool.Constants;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.List;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class NewsActivity extends Fragment implements OnItemClickListener{
	private NewsListAdapter adapter;
	List<MobileContentH> objects;
	ListView listView;
	boolean isError;
	String errorMessage;
	private FirebaseAnalytics screenName;
	@Override
    public void onAttach(Context activity) {

        super.onAttach(activity);
        setHasOptionsMenu(true);      
        getActivity().setTitle(getString(R.string.title_mn_promo));
        News news= new News(getActivity());
        Bundle bundle = getArguments();
        isError=bundle.getBoolean(Constants.BUND_KEY_NEWS_ERROR);
        errorMessage=bundle.getString(Constants.BUND_KEY_NEWS_ERROR_MESSAGE);
        objects = news.getlistNewsParent();
        adapter = new NewsListAdapter(getActivity(), objects);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
    }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Utility.freeMemory();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_listparent_layout, container, false);
        listView = (ListView)view.findViewById(android.R.id.list);
		screenName = FirebaseAnalytics.getInstance(getActivity());
//        View notFoundView = inflater.inflate(R.layout.news_without_content_layout, null);
        if(objects.size()==0) {
//			listView.addHeaderView(notFoundView);
			view.findViewById(R.id.newTaskLayout).setBackgroundResource(R.drawable.bg_notfound);
			view.findViewById(R.id.txv_data_not_found).setVisibility(View.VISIBLE);
		} else{
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					UserHelp.showAllUserHelpWithListView(NewsActivity.this.getActivity(),NewsActivity.this.getClass().getSimpleName(),listView,0);
				}
			}, SHOW_USERHELP_DELAY_DEFAULT);
		}
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        NiftyDialogBuilder dialogBuilder;
        if(isError){
			isError=false;
			if(GlobalData.isRequireRelogin()){
				DialogManager.showForceExitAlert(getActivity(),getString(R.string.msgLogout));
			}
        	else if (errorMessage.contains(getString(R.string.no_address_associated))) {
        		dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
    			dialogBuilder.withTitle(getString(R.string.error_capital))
                        .withMessage(getString(R.string.use_offline_mode)).show();
			}else{
				dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
				dialogBuilder.withTitle(getString(R.string.error_capital)).withMessage(errorMessage).show();
			}
        }
        return view;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {	
		try {
			Intent intent = new Intent(getContext(), NewsChildActivity.class);
			intent.putExtra("uuid_parent", objects.get(position).getUuid_mobile_content_h());
			listView.setEnabled(false);
			startActivity(intent);
		} catch (Exception e) {
			FireCrash.log(e);
			// TODO: handle exception
		}		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.mnGuide){
			if(!Global.BACKPRESS_RESTRICTION) {

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						UserHelp.showAllUserHelpWithListView(NewsActivity.this.getActivity(), NewsActivity.this.getClass().getSimpleName(), listView, 0);
					}
				}, SHOW_USERHELP_DELAY_DEFAULT);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume(){
	    super.onResume();
	    getActivity().setTitle(getString(R.string.title_mn_promo));
		listView.setEnabled(true);
		//Set Firebase screen name
		screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_promo), null);
//	    getActivity().getActionBar().removeAllTabs();
//	    getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}
}
