package com.adins.mss.odr;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.odr.tool.Constants;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;


public class CheckOrderActivity extends Fragment implements OnClickListener, OnItemSelectedListener{

	class DateClickListener implements View.OnClickListener {
		private final EditText mTarget;
		private final Context mContext;

		public DateClickListener(Context context, EditText target) {
			mContext = context;
			mTarget = target;
		}

		@Override
		public void onClick(View v) {
			Calendar cal = Calendar.getInstance();
			DatePickerDialog dialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					mTarget.setText(dayOfMonth + "/" + Tool.appendZeroForDateTime(monthOfYear, true) + "/" +  year);
				}
			}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
			dialog.show();
		}
	}

	public static final String keyCustName = "EXTRA_CUST_NAME_KEY";
	protected EditText txtStartDate;
	protected EditText txtEndDate;
	protected EditText txtNomorOrder;
	protected Spinner spinnerSearch;
	private EditText txtNamaCustomer;
	private String[] isiSearchBy;
	private Button btnSearch;
	private ImageView btnStartDate;
	private ImageView btnEndDate;
	private RelativeLayout byDateLayout;
	private LinearLayout byNomorLayout;
	protected View view;
	private LinearLayout byNamaLayout, container;
	private FirebaseAnalytics screenName;
	private com.google.android.material.bottomnavigation.BottomNavigationView bottomNav;

	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);
		setHasOptionsMenu(true);
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		bottomNav.getMenu().findItem(com.adins.mss.odr.R.id.taskListNav).setEnabled(true);
		Utility.freeMemory();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		screenName = FirebaseAnalytics.getInstance(getActivity());
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
		view = inflater.inflate(R.layout.check_order, container, false);
		getActivity().setTitle(R.string.title_mn_checkorder);
		txtStartDate = (EditText)view.findViewById(R.id.txtStartDate);
		txtEndDate = (EditText)view.findViewById(R.id.txtEndDate);
		txtNomorOrder = (EditText)view.findViewById(R.id.txtNomorOrder);
		txtNamaCustomer = (EditText) view.findViewById(R.id.txtCustomerName);
		btnSearch = (Button)view.findViewById(R.id.btnSearchOrder);
		btnStartDate =(ImageView)view.findViewById(R.id.btnStartDate);
		btnEndDate=(ImageView)view.findViewById(R.id.btnEndDate);
		spinnerSearch=(Spinner)view.findViewById(R.id.cbSearchBy);
		byDateLayout=(RelativeLayout) view.findViewById(R.id.byDate);
		byNomorLayout=(LinearLayout) view.findViewById(R.id.byNoOrder);
		byNamaLayout = (LinearLayout) view.findViewById(R.id.byCustName);
		bottomNav = getActivity().findViewById(com.adins.mss.odr.R.id.bottomNav);

		btnStartDate.setOnClickListener(new DateClickListener(getActivity(), txtStartDate));
		btnEndDate.setOnClickListener(new DateClickListener(getActivity(), txtEndDate));

		btnSearch.setOnClickListener(this);

		this.container = view.findViewById(R.id.container);
		this.container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if(!UserHelp.isActive)
							UserHelp.showAllUserHelp(CheckOrderActivity.this.getActivity(),CheckOrderActivity.this.getClass().getSimpleName());
					}
				}, SHOW_USERHELP_DELAY_DEFAULT);
			}
		});
		isiSearchBy = this.getResources().getStringArray(R.array.cbSearchBy);
//		ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(
//	    		getActivity(), R.array.cbSearchBy,
//	    		R.layout.spinner_style);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.spinner_style, R.id.text_spin, getResources().getStringArray(R.array.cbSearchBy)) {
			@Override
			public View getView(int position,  View convertView, ViewGroup parent) {
				TextView textView = (TextView) super.getView(position, convertView, parent);

				textView.setTextColor(Color.WHITE);

				return textView;
			}

			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				TextView textView = (TextView) super.getView(position, convertView, parent);

				textView.setTextColor(Color.BLACK);

				return textView;
			}
		};

//		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.spinner_style, R.id.text_spin, R.array.cbSearchBy){
//
//			@Override
//			public View getView(int position,  View convertView, ViewGroup parent) {
//				TextView textView = (TextView) super.getView(position, convertView, parent);
//
//				textView.setTextColor(Color.WHITE);
//
//				return textView;
//			}
//
//			@Override
//			public View getDropDownView(int position, View convertView, ViewGroup parent){
//				// Cast the drop down items (popup items) as text view
//				TextView tv = (TextView) super.getDropDownView(position,convertView,parent);
//
//				// Set the text color of drop down items
//				tv.setTextColor(Color.RED);
//
//				// If this item is selected item
////				if(position == mSelectedIndex){
////					// Set spinner selected popup item's text color
////					tv.setTextColor(Color.BLUE);
////				}
//
//				// Return the modified view
//				return tv;
//			}
//		};

		spinnerSearch.setAdapter(adapter);
		spinnerSearch.setOnItemSelectedListener(this);



		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		//Set Firebase screen name
		screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_check_order), null);
		bottomNav.getMenu().findItem(com.adins.mss.odr.R.id.taskListNav).setEnabled(false);
	}

	@Override
	public void onPause() {
		super.onPause();
		Global.haveLogin = 1;
	}

	protected void disableCheckOrderBtn(){
		bottomNav.getMenu().findItem(com.adins.mss.odr.R.id.taskListNav).setEnabled(true);
	}

	protected Intent getNextActivityIntent(){
//		return new Intent(getActivity().getApplicationContext(), ResultActivity.class);
		bottomNav.getMenu().findItem(com.adins.mss.odr.R.id.taskListNav).setEnabled(true);
		return new Intent(getActivity().getApplicationContext(), ResultOrderActivity.class);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id==R.id.btnSearchOrder){
			StringBuffer errorMessage = new StringBuffer();
			if (spinnerSearch.getSelectedItemPosition()==0) {
				if (txtStartDate.getText().toString().length()==0){
					errorMessage.append(getString(R.string.start_date_required));
				}
				if(txtEndDate.getText().toString().length()==0){
					errorMessage.append(getString(R.string.end_date_required));
				}
				try {
					SimpleDateFormat f = new SimpleDateFormat(Global.DATE_STR_FORMAT);
					Date sDate,eDate;
					long sLong = 0;
					long eLong=0;
					try {
						sDate = f.parse(txtStartDate.getText().toString());
						eDate = f.parse(txtEndDate.getText().toString());
						eDate.setHours(23);
						eDate.setMinutes(59);
						eDate.setSeconds(59);
						sLong = sDate.getTime();
						eLong = eDate.getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					long milisecond=eLong-sLong;
					if(milisecond>604799000){
						errorMessage.append(getString(R.string.data_range_not_allowed));
					}
					else if(milisecond<0){
						errorMessage.append(getString(R.string.input_not_valid));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else  if (spinnerSearch.getSelectedItemPosition() == 1) {
				if(txtNomorOrder.getText().toString().length()==0){
					errorMessage.append(getString(R.string.nomor_order_rewuired));
				}
			} else if (spinnerSearch.getSelectedItemPosition() == 2) {
				if(txtNamaCustomer.getText().toString().length() == 0) {
					errorMessage.append(getString(R.string.cust_name_required));
				} else if (txtNamaCustomer.getText().toString().length() < 3){
					errorMessage.append(getString(R.string.cust_name_minimun_required));
				}
			}
			if(errorMessage!=null&&errorMessage.length()>0){
				Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
			}else{
				Intent nextActivity = getNextActivityIntent();

				nextActivity.putExtra("startDate", txtStartDate.getText().toString());
				nextActivity.putExtra("endDate", txtEndDate.getText().toString());
				nextActivity.putExtra("nomorOrder", txtNomorOrder.getText().toString());
				nextActivity.putExtra(keyCustName, txtNamaCustomer.getText().toString());

				startActivity(nextActivity);
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
							   long id) {
		long itemSearchBy = spinnerSearch.getItemIdAtPosition(position);
		if (itemSearchBy == 1){
			byDateLayout.setVisibility(View.GONE);
			byNomorLayout.setVisibility(View.VISIBLE);
			byNamaLayout.setVisibility(View.GONE);
		} else if (itemSearchBy == 0){
			byDateLayout.setVisibility(View.VISIBLE);
			byNomorLayout.setVisibility(View.GONE);
			byNamaLayout.setVisibility(View.GONE);
		} else if (itemSearchBy == 2) {
			byDateLayout.setVisibility(View.GONE);
			byNomorLayout.setVisibility(View.GONE);
			byNamaLayout.setVisibility(View.VISIBLE);
		}

		txtStartDate.setText("");
		txtEndDate.setText("");
		txtNamaCustomer.setText("");
		txtNomorOrder.setText("");
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.mnGuide){
			if(!Global.BACKPRESS_RESTRICTION) {

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (!UserHelp.isActive)
							UserHelp.showAllUserHelp(CheckOrderActivity.this.getActivity(), CheckOrderActivity.this.getClass().getSimpleName());
					}
				}, SHOW_USERHELP_DELAY_DEFAULT);
				container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (!UserHelp.isActive)
							UserHelp.showAllUserHelp(CheckOrderActivity.this.getActivity(), CheckOrderActivity.this.getClass().getSimpleName());
					}
				});
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("ValidFragment")
	public class DatePickerDialogCustom extends DialogFragment{

		int id;
		String startDate ="";
		String endDate ="";
		private DatePickerDialog.OnDateSetListener mStartDateSetListener =
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
										  int dayOfMonth) {
						String month = Tool.appendZeroForDateTime(monthOfYear, true);
						startDate = dayOfMonth + "/" + month + "/" + year;
						txtStartDate.setText(startDate);

					}
				};
		private DatePickerDialog.OnDateSetListener mEndDateSetListener =
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
										  int dayOfMonth) {
						String month = Tool.appendZeroForDateTime(monthOfYear, true);
						endDate = dayOfMonth + "/" + month + "/" + year;
						txtEndDate.setText(endDate);
					}
				};

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			String key = getArguments().getString(Constants.KEY_DATE);
			int id=0;
			if(key.equals(Constants.KEY_START_DATE))
				id = getArguments().getInt(Constants.KEY_START_DATE);
			else if(key.equals(Constants.KEY_END_DATE))
				id = getArguments().getInt(Constants.KEY_END_DATE);

			return DatePickerDialog(id);
		}

		public Dialog DatePickerDialog(int id){
			Date sysdate =new Date(System.currentTimeMillis());
			DatePickerDialog datePicker = null;
			String sdt = Formatter.formatDate(sysdate, "dd/MM/yyyy");
			String[] temp1 = sdt.split("/");
			final int DayOfMonth = Integer.parseInt(temp1[0]);
			final int Month = Integer.parseInt((temp1[1])) - 1;
			final int Year = Integer.parseInt(temp1[2]);
			if (id==Constants.START_DATE_DIALOG_ID) {
				datePicker =  new DatePickerDialog(getActivity(),  mStartDateSetListener, Year, Month, DayOfMonth)
				{
					@Override
					public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
					{
						if (year > Year)
							view.updateDate(Year, Month, DayOfMonth);
						if (monthOfYear > Month && year == Year)
							view.updateDate(Year, Month, DayOfMonth);
						if (dayOfMonth > DayOfMonth && year == Year && monthOfYear == Month)
							view.updateDate(Year, Month, DayOfMonth);
					}
				};
			}
			else if (id==Constants.END_DATE_DIALOG_ID) {

				datePicker =  new DatePickerDialog(getActivity(),  mEndDateSetListener, Year, Month, DayOfMonth)
				{
					@Override
					public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
					{

						if (year > Year)
							view.updateDate(Year, Month, DayOfMonth);
						if (monthOfYear > Month && year == Year)
							view.updateDate(Year, Month, DayOfMonth);
						if (dayOfMonth > DayOfMonth && year == Year && monthOfYear == Month)
							view.updateDate(Year, Month, DayOfMonth);
					}
				};
			}
			return datePicker;
		}
	}
}
