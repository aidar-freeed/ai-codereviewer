package com.adins.mss.svy.reassignment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.svy.R;
import com.adins.mss.svy.tool.Constants;

import org.acra.ACRA;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CheckOrderActivity extends Fragment implements OnClickListener, OnItemSelectedListener {
	
	
	protected static EditText txtStartDate;
	protected static EditText txtEndDate;
	protected EditText txtNomorOrder;
	protected Spinner spinnerSearch;
    protected View view;
    private String[] isiSearchBy;
    private Button btnSearch;
    private ImageView btnStartDate;
	private ImageView btnEndDate;
	private LinearLayout byDateLayout;
	private LinearLayout byNomorLayout;
	static CalendarView.OnDateChangeListener onDateChangeListener;

	@Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
    }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Utility.freeMemory();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.new_fragment_check_survey_reassign, container, false);
		//onDateChangeListener = this;
		txtStartDate = (EditText)view.findViewById(R.id.txtStartDate);
		txtEndDate = (EditText)view.findViewById(R.id.txtEndDate);
//		txtNomorOrder = (EditText)view.findViewById(R.id.txtNomorOrder);
		btnSearch = (Button)view.findViewById(R.id.btnSearchOrder);
		btnStartDate =(ImageView)view.findViewById(R.id.btnStartDate);
		btnEndDate=(ImageView)view.findViewById(R.id.btnEndDate);
//		spinnerSearch=(Spinner)view.findViewById(R.id.cbSearchBy);
		byDateLayout=(LinearLayout) view.findViewById(R.id.byDate);
//		byNomorLayout=(LinearLayout) view.findViewById(R.id.byNoOrder);
		
		btnStartDate.setOnClickListener(this);
		btnEndDate.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		
//		isiSearchBy = this.getResources().getStringArray(R.array.cbOrderReassign);
//		if(isiSearchBy.length>1){
//			ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(
//		    		getActivity(), R.array.cbOrderReassign,
//		    		R.layout.spinner_style);
//
//			spinnerSearch.setAdapter(adapter);
//			spinnerSearch.setOnItemSelectedListener(this);
//		}else{
//			view.findViewById(R.id.btnRefresh).setVisibility(View.GONE);
//            spinnerSearch.setVisibility(View.GONE);
//        }

        return view;
	}
		
	@Override
	public void onPause() {
		super.onPause();
		Global.haveLogin = 1;
	}
	
	@Override
	public void onResume(){
	    	super.onResume();
//	    	getActivity().getActionBar().removeAllTabs();
//	    	getActivity().getActionBar().setTitle(getString(R.string.title_mn_checkorder));
//	    	getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}
	
	protected Intent getNextActivityIntent(){
//		return new Intent(getActivity().getApplicationContext(), ResultActivity.class);
		return new Intent(getActivity().getApplicationContext(), ResultOrderActivity.class);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id==R.id.btnStartDate){
			DialogFragment datePicker = new DatePickerDialogCustom();
			Bundle bundle = new Bundle();
			bundle.putString(Constants.KEY_DATE, Constants.KEY_START_DATE);
			bundle.putInt(Constants.KEY_START_DATE, Constants.START_DATE_DIALOG_ID);
			datePicker.setArguments(bundle);
			
			datePicker.show(getFragmentManager(), Constants.KEY_START_DATE);
			
		}
		if(id==R.id.btnEndDate){
			DialogFragment datePicker = new DatePickerDialogCustom();
			Bundle bundle = new Bundle();
			bundle.putString(Constants.KEY_DATE, Constants.KEY_END_DATE);
			bundle.putInt(Constants.KEY_END_DATE, Constants.END_DATE_DIALOG_ID);
			datePicker.setArguments(bundle);
			
			datePicker.show(getFragmentManager(), Constants.KEY_END_DATE);
			
		}
		if(id==R.id.btnSearchOrder){
//			String starDate=txtStartDate.getText().toString();
//			String endDate=txtEndDate.getText().toString();
//			String noOrder=txtNomorOrder.getText().toString();
//			
//			if((starDate.length()>0&&endDate.length()>0)||noOrder.length()>0){
//				Intent nextActivity = getNextActivityIntent();
//
//				nextActivity.putExtra("startDate", txtStartDate.getText().toString());
//				nextActivity.putExtra("endDate", txtEndDate.getText().toString());
//				nextActivity.putExtra("nomorOrder", txtNomorOrder.getText().toString());
//
//	            startActivity(nextActivity);
//			}else{
//				Toast.makeText(getActivity(), "input required !",
//						   Toast.LENGTH_LONG).show();
//			}
			StringBuffer errorMessage = new StringBuffer();
//			if (spinnerSearch.getSelectedItemPosition()==0) {
				if (txtStartDate.getText().toString().length()==0){
                    errorMessage.append(getString(R.string.start_date_required));
                }
                if (txtEndDate.getText().toString().length() == 0) {
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
						Calendar sCalendar = Calendar.getInstance();
						sCalendar.setTimeInMillis(sDate.getTime());
						Calendar eCalendar = Calendar.getInstance();
						eCalendar.setTimeInMillis(eDate.getTime());
						eCalendar.set(eCalendar.HOUR_OF_DAY, 23);
						eCalendar.set(eCalendar.MINUTE, 59);
						eCalendar.set(eCalendar.SECOND, 59);
						sLong = sCalendar.getTimeInMillis();
						eLong = eCalendar.getTimeInMillis();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					long milisecond=eLong-sLong;
					if(milisecond>604799000){
                        errorMessage.append(getString(R.string.data_range_not_allowed));
                    } else if (milisecond < 0) {
                        errorMessage.append(getString(R.string.input_not_valid));
                    }
                } catch (Exception e) {
					FireCrash.log(e);
                    // TODO: handle exception
				}				
//			} else {
//				if(txtNomorOrder.getText().toString().length()==0){
//                    errorMessage.append(getString(R.string.nomor_order_rewuired));
//                }
//            }
            if(errorMessage != null && errorMessage.length( )> 0){
				Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();				
			}else{
				Intent nextActivity = getNextActivityIntent();
	
				nextActivity.putExtra("startDate", txtStartDate.getText().toString());
				nextActivity.putExtra("endDate", txtEndDate.getText().toString());
//				nextActivity.putExtra("nomorOrder", txtNomorOrder.getText().toString());

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
        	txtStartDate.setText("");
        	txtEndDate.setText("");
        }
		else if (itemSearchBy == 0){
        	byDateLayout.setVisibility(View.VISIBLE);
        	byNomorLayout.setVisibility(View.GONE);
        	txtNomorOrder.setText("");
        }
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	/*@Override
	public void onStartDateSetListener(String startDate) {
		txtStartDate.setText(startDate);
	}

	@Override
	public void onEndDateSetListener(String endDate) {
		txtEndDate.setText(endDate);
	}

	@Override
	public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

	}*/

	@SuppressLint("ValidFragment")
	public static class DatePickerDialogCustom extends DialogFragment{

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
				//onDateChangeListener.onStartDateSetListener(startDate);
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
				//onDateChangeListener.onEndDateSetListener(endDate);
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
