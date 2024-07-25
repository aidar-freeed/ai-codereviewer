package com.adins.mss.odr.marketingreport;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.KeyValue;
import com.adins.mss.odr.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by olivia.dg on 11/28/2017.
 */

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

public class MarketingReportFragment extends Fragment {

    ImageButton buttonSelectDate;
    EditText editDate;
    AppCompatSpinner spinnerCategory;
    private EditText editStartDate;
    private EditText editEndDate;
    private ImageButton buttonSelectStartDate;
    private ImageButton buttonSelectEndDate;
    private AppCompatSpinner spinnerMonth;
    private Button buttonSearch;
    private int activeSearchMode;
    private ListView listResult;
    private String[] cbSearchBy;
    private String[] cbSearchByMonth;
    private CardView layout;
    private FirebaseAnalytics screenName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenName = FirebaseAnalytics.getInstance(getActivity());
        return inflater.inflate(R.layout.fragment_marketing_report, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public void onResume(){
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_product_page), null);
        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_marketingreport));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listResult = (ListView) view.findViewById(R.id.resultListView);
        layout = (CardView) view.findViewById(R.id.resultLayout);

        buttonSelectDate = (ImageButton) view.findViewById(R.id.btnDate);
        buttonSelectStartDate = (ImageButton) view.findViewById(R.id.btnStartDate);
        buttonSelectEndDate = (ImageButton) view.findViewById(R.id.btnEndDate);
        buttonSearch = (Button) view.findViewById(R.id.btnSearchOrder);

        editDate = (EditText) view.findViewById(R.id.txtDateDay);
        editStartDate = (EditText) view.findViewById(R.id.txtStartDate);
        editEndDate = (EditText) view.findViewById(R.id.txtEndDate);

        spinnerMonth = (AppCompatSpinner) view.findViewById(R.id.cbSearchByMonth);
        spinnerCategory = (AppCompatSpinner) view.findViewById(R.id.cbSearchBy);

        cbSearchBy = this.getResources().getStringArray(R.array.cbSearchBy);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.cbSearchBy,
                R.layout.spinner_search_layout);
        adapter.setDropDownViewResource(R.layout.spinner_style);

        spinnerCategory.setAdapter(adapter);

        cbSearchByMonth = this.getResources().getStringArray(R.array.cbSearchByMonth);

        Calendar monthCalendar = Calendar.getInstance();
        int maximumMonth = monthCalendar.get(Calendar.MONTH);

        String[] months = getActivity().getResources().getStringArray(R.array.cbSearchByMonth);

        ArrayList<String> availableMonths = new ArrayList<String>();
        availableMonths.addAll(Arrays.asList(months).subList(0, maximumMonth + 1));

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_style2, availableMonths);
        monthAdapter.setDropDownViewResource(R.layout.spinner_style);

        spinnerMonth.setAdapter(monthAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                invalidateForm(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        buttonSelectDate.setOnClickListener(new DateClickListener(getActivity(), editDate));
        buttonSelectStartDate.setOnClickListener(new DateClickListener(getActivity(), editStartDate));
        buttonSelectEndDate.setOnClickListener(new DateClickListener(getActivity(), editEndDate));
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketingSearchRequest request = new MarketingSearchRequest();

                try {
                    if (Tool.isInternetconnected(getActivity())) {
                        executeSearch(request,editDate.getText().toString(), spinnerMonth.getSelectedItemPosition(),editStartDate.getText().toString(), editEndDate.getText().toString());
                    } else {
                        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getActivity());
                        builder.withTitle("INFO")
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

    }

    private void executeSearch(MarketingSearchRequest request, final String date, final int month, final String startDate, final String endDate) throws ParseException, IOException {
        new AsyncTask<Void, Void, MarketingSearchResponse>(){
            final ProgressDialog progress = new ProgressDialog(getActivity());
            String errMessage;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setMessage(getActivity().getString(R.string.contact_server));
                progress.show();
            }

            @Override
            protected MarketingSearchResponse doInBackground(Void... params) {
                try {
                    MarketingSearchRequest searchRequest = new MarketingSearchRequest();
                    searchRequest.setUuidUser(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    searchRequest.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                    try {
                        if (activeSearchMode == 0) {
                            SimpleDateFormat f = new SimpleDateFormat(Global.DATE_STR_FORMAT);
                            Date date1 = f.parse(date);
                            searchRequest.setDate1(date1);
                        }
                        if (activeSearchMode == 1) {
                            SimpleDateFormat f = new SimpleDateFormat(Global.DATE_STR_FORMAT);
                            Date sDate, eDate;
                            long sLong = 0;
                            long eLong = 0;
                            try {
                                sDate = f.parse(startDate);
                                searchRequest.setDate1(sDate);
                                eDate = f.parse(endDate);
                                eDate.setHours(23);
                                eDate.setMinutes(59);
                                eDate.setSeconds(59);
                                searchRequest.setDate2(eDate);
                                sLong = sDate.getTime();
                                eLong = eDate.getTime();
                            } catch (ParseException e) {
                                errMessage = getActivity().getString(R.string.enter_valid_date);
                                return null;
                            }
                            long milisecond = eLong - sLong;
                            if (milisecond > 604799000) {
                                errMessage = getActivity().getString(R.string.data_range_not_allowed);
                                return null;
                            } else if (milisecond < 0) {
                                errMessage = getActivity().getString(R.string.input_not_valid);
                                return null;
                            }

                        }
                        if (activeSearchMode == 2) {
                            searchRequest.setMonth(String.valueOf(month + 1));
                        }
                    } catch (ParseException parseEx) {
                        errMessage = getActivity().getString(R.string.enter_valid_date);
                        return null;
                    }

                    String json = GsonHelper.toJson(searchRequest);

                    String url = GlobalData.getSharedGlobalData().getURL_GET_MKTPERFORMANCE();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
                    HttpConnectionResult serverResult = null;
                    try {
                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    MarketingSearchResponse serverResponse = null;
                    if (serverResult != null && serverResult.isOK()) {
                        try {
                            String responseBody = serverResult.getResult();
                            serverResponse = GsonHelper.fromJson(responseBody, MarketingSearchResponse.class);

                        } catch (Exception e) {
                            if(Global.IS_DEV) {
                                e.printStackTrace();
                                errMessage=e.getMessage();
                            }
                        }
                    } else {
                        errMessage = getActivity().getString(R.string.server_down);
                    }

                    return serverResponse;
                } catch (Exception e) {
                    errMessage = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final MarketingSearchResponse serverResponse) {
                super.onPostExecute(serverResponse);
                if(getActivity()!=null) {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                    if (errMessage != null) {
                        Toast.makeText(getActivity(), errMessage, Toast.LENGTH_SHORT).show();
                    } else if (serverResponse != null && serverResponse.getListKeyValue() != null) {
                        layout.setVisibility(View.VISIBLE);
                        MarketingReportFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listResult.setAdapter(new SearchResultListAdapter(getActivity(), serverResponse.getListKeyValue()));
                            }
                        });
                    }
                }
            }
        }.execute();
    }

    private void invalidateForm(int position) {
        switch (position) {
            case 0:
                showSearchForm(R.id.byDay);
                break;
            case 1:
                showSearchForm(R.id.byEstimatedDate);
                break;
            case 2:
                showSearchForm(R.id.byMonth);
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
