package com.adins.mss.coll.interfaces;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.coll.R;
import com.adins.mss.coll.api.ReportSummaryApi;
import com.adins.mss.coll.models.ReportSummaryResponse;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;

import java.util.Locale;

/**
 * Created by kusnendi.muhamad on 28/07/2017.
 */

public class ReportSummaryImpl implements ReportSummaryInterface {
    private Context context;
    private ReportSummaryResponse reportSummary;
    private TaskListener listener;

    public ReportSummaryImpl(Context context) {
        this.context = context;
    }

    @Override
    public void getReportSummary(TaskListener taskListener) {
        GetReportSummaryTask task = new GetReportSummaryTask(taskListener);
        task.execute();
    }

    private class GetReportSummaryTask extends AsyncTask<Void, Void, ReportSummaryResponse> {
        ProgressDialog dialog;

        public GetReportSummaryTask(TaskListener taskListener) {
            listener = taskListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.progressWait));
            dialog.show();
        }

        @Override
        protected ReportSummaryResponse doInBackground(Void... args) {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                FireCrash.log(e);
                //
            }

            ReportSummaryApi api = new ReportSummaryApi(context);
            return api.request();
        }

        @Override
        protected void onPostExecute(ReportSummaryResponse reportSummaryResponse) {
            super.onPostExecute(reportSummaryResponse);
            if(dialog.isShowing()) dialog.dismiss();

            if (GlobalData.isRequireRelogin()) {

            } else if(reportSummaryResponse != null && reportSummaryResponse.getTotal_received() != null) {
                // perbaikan baru set text pada asyntask karena server mengirimkan total paid dan total to be collected,
                // namun jumlah task.size kosong. jadi tidak ke onComplete
                if (null != reportSummaryResponse.getTotal_to_be_paid() && reportSummaryResponse.getTotal_to_be_paid() >= 0){
                    TextView totalToBeCollected = (TextView)((Activity)context).findViewById(R.id.txtCollected);
                    totalToBeCollected.setText(Tool.separateThousand(String.format(Locale.US, "%.0f",reportSummaryResponse.getTotal_to_be_paid())));
                }
                if (null != reportSummaryResponse.getTotal_received() && reportSummaryResponse.getTotal_received() >= 0){
                    TextView totalPaid = (TextView)((Activity)context).findViewById(R.id.txtPaid);
                    totalPaid.setText(Tool.separateThousand(String.format(Locale.US, "%.0f",reportSummaryResponse.getTotal_received())));
                }
                if(reportSummaryResponse.getList_task().size() == 0) {
                    NiftyDialogBuilder.getInstance(context)
                        .withTitle("INFO")
                        .withMessage(context.getString(R.string.data_not_found))
                        .show();
                } else {
                    listener.onCompleteTask(reportSummaryResponse);
                }
            } else {
                NiftyDialogBuilder.getInstance(context)
                        .withMessage(context.getString(R.string.empty_data))
                        .withTitle(context.getString(R.string.data_not_found))
                        .isCancelable(true)
                        .show();
                return;
            }
        }
    }
}
