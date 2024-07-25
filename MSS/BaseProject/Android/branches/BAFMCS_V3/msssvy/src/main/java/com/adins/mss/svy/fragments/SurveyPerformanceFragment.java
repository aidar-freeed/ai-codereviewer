package com.adins.mss.svy.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.base.util.Utility;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.KeyValue;
import com.adins.mss.svy.R;
import com.adins.mss.svy.models.SurveyorSearchRequest;
import com.adins.mss.svy.models.SurveyorSearchResponse;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

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
                mTarget.setText(dayOfMonth + "/" +Tool.appendZeroForDateTime(monthOfYear, true) + "/" +  year);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
}

/**
 * Created by Aditya Purwa on 1/27/2015.
 */
public class SurveyPerformanceFragment extends Fragment {

    ImageButton buttonSelectDate;
    EditText editDate;
    AppCompatSpinner spinnerCategory;
    private EditText editStartDate;
    private EditText editEndDate;
    private EditText editMonthYear;
    private ImageButton buttonSelectStartDate;
    private ImageButton buttonSelectEndDate;
    private Button buttonSearch;
    private int activeSearchMode;
    private ListView listResult;
    private String[] cbSearchBy;
    private CardView layout;
    private SurveyActivityInterface iSurveyActivity;
    private ImageButton butonMonthYear;
    private Calendar calendar;
    private int bulan;
    private int tahun;
    private FirebaseAnalytics screenName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iSurveyActivity = new SurveyActivityImpl(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenName = FirebaseAnalytics.getInstance(getActivity());
        return inflater.inflate(R.layout.new_fragment_survey_performance, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        Utility.freeMemory();
    }

    @Override
    public void onResume(){
        super.onResume();

        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_survey_performance), null);

        // olivia : set toolbar
        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_surveyperformance));
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init
        calendar = Calendar.getInstance();
        bulan = 0; // Antisipasi null
        tahun = 0; // Antisipasi null

        listResult = (ListView) view.findViewById(R.id.resultListView);
        layout = (CardView) view.findViewById(R.id.resultLayout);

        buttonSelectDate = (ImageButton) view.findViewById(R.id.btnDate);
        buttonSelectStartDate = (ImageButton) view.findViewById(R.id.btnStartDate);
        buttonSelectEndDate = (ImageButton) view.findViewById(R.id.btnEndDate);
        buttonSearch = (Button) view.findViewById(R.id.btnSearchOrder);
        butonMonthYear =  view.findViewById(R.id.btnMonthYear);

        editDate = (EditText) view.findViewById(R.id.txtDateDay);
        editStartDate = (EditText) view.findViewById(R.id.txtStartDate);
        editEndDate = (EditText) view.findViewById(R.id.txtEndDate);
        editMonthYear = view.findViewById(R.id.txtDateAndYear);

        spinnerCategory = (AppCompatSpinner) view.findViewById(R.id.cbSearchBy);

        cbSearchBy = this.getResources().getStringArray(R.array.cbSearchBy);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.spinner_search_layout, R.id.text_spin, getResources().getStringArray(R.array.cbSearchBy));
        adapter.setDropDownViewResource(R.layout.spinner_style);

        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                invalidateForm(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing selected
            }
        });

        // Inisiasi awal
        final String[] months = getActivity().getResources().getStringArray(R.array.cbSearchByMonth);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        bulan = month;
                        tahun = year;
                        String data = months[month] + "-" + String.valueOf(year);
                        editMonthYear.setText(data);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        Calendar minMonthYear = Calendar.getInstance();
        minMonthYear.set(calendar.get(Calendar.YEAR)-2, 0, 1);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.getDatePicker().setMinDate(minMonthYear.getTimeInMillis());

        buttonSelectDate.setOnClickListener(new DateClickListener(getActivity(), editDate));
        buttonSelectStartDate.setOnClickListener(new DateClickListener(getActivity(), editStartDate));
        buttonSelectEndDate.setOnClickListener(new DateClickListener(getActivity(), editEndDate));
        butonMonthYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SurveyorSearchRequest request = new SurveyorSearchRequest();

                try {
                    if (Tool.isInternetconnected(getActivity())) {
                        iSurveyActivity.executeSearch(listener, request, editDate.getText().toString(), tahun, bulan, // Ambil bulan dan tahun
                                editStartDate.getText().toString(), editEndDate.getText().toString(), activeSearchMode);
                    } else {
                        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getActivity());
                        builder.withTitle(getString(R.string.info_capital))
                                .withMessage(getString(R.string.no_internet_connection))
                                .show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!UserHelp.isActive) {
                            UserHelp.showAllUserHelp(SurveyPerformanceFragment.this.getActivity(),
                                    SurveyPerformanceFragment.this.getClass().getSimpleName());
                        }
                    }
                }, SHOW_USERHELP_DELAY_DEFAULT);

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.mnGuide){

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UserHelp.showAllUserHelp(SurveyPerformanceFragment.this.getActivity(),
                            SurveyPerformanceFragment.this.getClass().getSimpleName());
                }
            }, SHOW_USERHELP_DELAY_DEFAULT);
            layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(!UserHelp.isActive)
                        UserHelp.showAllUserHelp(SurveyPerformanceFragment.this.getActivity(),
                            SurveyPerformanceFragment.this.getClass().getSimpleName());
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    TaskListener listener = new TaskListener() {
        @Override
        public void onCompleteTask(Object result) {
            SurveyorSearchResponse serverResponse = (SurveyorSearchResponse) result;
            updateResult(serverResponse.getListKeyValue());
        }

        @Override
        public void onCancelTask(boolean value) {
            //on cancel task
        }

        @Override
        public void onLocalData(Object result) {
            //on local data
        }
    };

    private void updateResult(final KeyValue[] result) {
        layout.setVisibility(View.VISIBLE);
        SurveyPerformanceFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listResult.setAdapter(new SearchResultListAdapter(getActivity(), result));
            }
        });
    }


//    private void executeSearch(SurveyorSearchRequest request, final String date, final int month, final String startDate, final String endDate) throws ParseException, IOException {
//        new AsyncTask<Void, Void, SurveyorSearchResponse>(){
//            final ProgressDialog progress = new ProgressDialog(getActivity());
//            String errMessage;
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                progress.setMessage(getActivity().getString(R.string.contact_server));
//                progress.show();
//            }
//
//            @Override
//            protected SurveyorSearchResponse doInBackground(Void... params) {
//                try {
//                    SurveyorSearchRequest searchRequest = new SurveyorSearchRequest();
//                    searchRequest.setAudit(GlobalData.getSharedGlobalData().getAuditData());
//
//                    try {
//                        if (activeSearchMode == 0) {
//                            SimpleDateFormat f = new SimpleDateFormat(Global.DATE_STR_FORMAT);
//                            Date date1 = f.parse(date);
//                            searchRequest.setDate1(date1);
//                        }
//                        if (activeSearchMode == 1) {
//                            SimpleDateFormat f = new SimpleDateFormat(Global.DATE_STR_FORMAT);
//                            Date sDate, eDate;
//                            long sLong = 0;
//                            long eLong = 0;
//                            try {
//                                sDate = f.parse(startDate);
//                                searchRequest.setDate1(sDate);
//                                eDate = f.parse(endDate);
//                                eDate.setHours(23);
//                                eDate.setMinutes(59);
//                                eDate.setSeconds(59);
//                                searchRequest.setDate2(eDate);
//                                sLong = sDate.getTime();
//                                eLong = eDate.getTime();
//                            } catch (ParseException e) {
//                                errMessage = getActivity().getString(R.string.enter_valid_date);
//                                return null;
//                            }
//                            long milisecond = eLong - sLong;
//                            if (milisecond > 604799000) {
//                                errMessage = getActivity().getString(R.string.data_range_not_allowed);
//                                return null;
//                            } else if (milisecond < 0) {
//                                errMessage = getActivity().getString(R.string.input_not_valid);
//                                return null;
//                            }
//
//                        }
//                        if (activeSearchMode == 2) {
//                            searchRequest.setMonth(String.valueOf(month + 1));
//                        }
//                    } catch (ParseException parseEx) {
//                        errMessage = getActivity().getString(R.string.enter_valid_date);
//                        return null;
//                    }
//
//                    String json = GsonHelper.toJson(searchRequest);
//
//                    String url = GlobalData.getSharedGlobalData().getURL_GET_SVYPERFORMANCE();
//                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
//                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
//                    HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
//                    HttpConnectionResult serverResult = null;
//                    try {
//                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
//                    } catch (Exception e) {             FireCrash.log(e);
//                        e.printStackTrace();
//                    }
//                    SurveyorSearchResponse serverResponse = null;
//                    if (serverResult != null && serverResult.isOK()) {
//                        try {
//                            String responseBody = serverResult.getResult();
//                            serverResponse = GsonHelper.fromJson(responseBody, SurveyorSearchResponse.class);
//
//                        } catch (Exception e) {             FireCrash.log(e);
//                            if(Global.IS_DEV) {
//                                e.printStackTrace();
//                                errMessage=e.getMessage();
//                            }
//                        }
//                    } else {
//                        errMessage = getActivity().getString(R.string.server_down);
//                    }
//
//                    return serverResponse;
//                } catch (Exception e) {             FireCrash.log(e);
//                    errMessage = e.getMessage();
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(final SurveyorSearchResponse serverResponse) {
//                super.onPostExecute(serverResponse);
//                if(getActivity()!=null) {
//                    if (progress != null && progress.isShowing()) {
//                        progress.dismiss();
//                    }
//                    if (errMessage != null) {
//                        Toaster.error(getActivity(), errMessage);
//                    } else if (serverResponse != null && serverResponse.getListKeyValue() != null) {
//                        layout.setVisibility(View.VISIBLE);
//                        SurveyPerformanceFragment.this.getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                listResult.setAdapter(new SearchResultListAdapter(getActivity(), serverResponse.getListKeyValue()));
//                            }
//                        });
//                    }
//                }
//            }
//        }.execute();
//    }

    private void invalidateForm(int position) {
        switch (position) {
            case 0:
                showSearchForm(R.id.byDay);
                break;
            case 1:
                showSearchForm(R.id.byMonth);
                break;
            case 2:
                showSearchForm(R.id.byEstimatedDate);
                break;
            default:
                break;
        }
        this.activeSearchMode = position;
    }

    private void showSearchForm(int id) {
        View byDay = getView().findViewById(R.id.byDay);
        View byRange = getView().findViewById(R.id.byEstimatedDate);
        View byMonth = getView().findViewById(R.id.byMonth);

        byDay.setVisibility(View.GONE);
        byRange.setVisibility(View.GONE);
        byMonth.setVisibility(View.GONE);

        View active = getView().findViewById(id);
        active.setVisibility(View.VISIBLE);
    }
}

class SearchResultListAdapter extends ArrayAdapter<KeyValue> {

    public SearchResultListAdapter(Context context) {
        super(context, R.layout.view_surveyor_search_result);
    }

    public SearchResultListAdapter(Context context, KeyValue[] values) {
        super(context, R.layout.view_surveyor_search_result, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_surveyor_search_result, parent, false);
        }
        TextView label = (TextView) convertView.findViewById(R.id.taskLabel);
        TextView value = (TextView) convertView.findViewById(R.id.taskValue);

        KeyValue item = getItem(position);
        label.setText(item.getKey());
        value.setText(item.getValue());
        return convertView;
    }
}