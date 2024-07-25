package com.adins.mss.coll.fragments.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.adins.mss.base.commons.ViewImpl;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.coll.R;
import com.adins.mss.coll.commons.ViewManager;
import com.adins.mss.coll.interfaces.callback.DepositReportCallback;
import com.adins.mss.coll.interfaces.DepositReportImpl;
import com.adins.mss.coll.interfaces.DepositReportInterface;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.TaskD;
import com.adins.mss.foundation.formatter.Tool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 28/07/2017.
 */

public class DepositReportSummaryView extends ViewManager {
    public View view;
    private Activity activity;
    private ListView listOfBatch;
    private TextView totalTaskLabel;
    private TextView totalPaidLabel;
    private TextView totalVisitLabel;
    private TextView totalFailLabel;
    private TextView totalTaskValue;
    private TextView total;
    private DepositReportInterface DepositReport;
    private int sum = 0;
    private int totalSum = 0;

    public DepositReportSummaryView(Activity activity) {
        super(activity);
        this.activity = activity;
        DepositReport = new DepositReportImpl(activity);
    }

    @Override
    public void publish() {
        DepositReport.cleanDepositReportH();
        DepositReport.fillHeader(this);
        DepositReport.fillDetail(this);
    }

    public View layoutInflater(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.new_fragment_deposit_report_summary, container, false);
        return view;
    }

    @Override
    public void onCreate() {
        totalTaskLabel  = (TextView) view.findViewById(R.id.totalTask);
        totalTaskValue  = (TextView) view.findViewById(R.id.totalTaskValue);
        totalPaidLabel  = (TextView) view.findViewById(R.id.paidValue);
        totalVisitLabel = (TextView) view.findViewById(R.id.visitValue);
        totalFailLabel  = (TextView) view.findViewById(R.id.failValue);
        listOfBatch     = (ListView) view.findViewById(R.id.summaryDetail);
        total = (TextView) view.findViewById(R.id.total);
        total.setText(Tool.separateThousand(String.valueOf(totalSum)));
    }

    @Override
    public void OnFillHeader(int totalTask, int paidTask, int failTask, int visitTask) {
        this.totalTaskValue.setText(totalTask + " Tasks");
        totalPaidLabel.setText(paidTask + " Tasks");
        totalFailLabel.setText(failTask + " Tasks");
        totalVisitLabel.setText(visitTask + " Tasks");
    }

    @Override
    public void OnFillDetail(HashMap<DepositReportH, List<DepositReportD>> packedListOfBatch) {
        listOfBatch.setAdapter(new PackedBatchListAdapterNew(activity, packedListOfBatch));
    }

    private void getTotal() {
        total.setText(Tool.separateThousand(String.valueOf(totalSum)));
    }

    private class PackedBatchListAdapterNew extends BaseAdapter {
        private final HashMap<DepositReportH, List<DepositReportD>> packedBatches;
        private final List<DepositReportH> batchesHeaders;
        private final Context context;
        private int curr = 0;

        public PackedBatchListAdapterNew(Context context, HashMap<DepositReportH, List<DepositReportD>> packedBatches) {
            this.context = context;
            this.packedBatches = packedBatches;
            this.batchesHeaders =
                    Arrays.asList(packedBatches.keySet().toArray(new DepositReportH[packedBatches.keySet().size()]));
        }

        @Override
        public int getCount() {
            return packedBatches.size();
        }

        @Override
        public Object getItem(int position) {
            return packedBatches.get(batchesHeaders.get(position));
        }

        public int getTaskCount(int position) {
            List<DepositReportD> tasks = (List<DepositReportD>) getItem(position);
            return tasks.size();
        }

        public double getTaskSum(int position) {
            List<DepositReportD> tasks = (List<DepositReportD>) getItem(position);
            double sum = 0;
            for (DepositReportD task : tasks) {
                try {
                    sum += Double.parseDouble(task.getDeposit_amt());
                } catch (Exception e) {
                    FireCrash.log(e);
                    sum += 0;
                }
            }
            return sum;
        }

        public DepositReportH getHeader(int position) {
            return batchesHeaders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
//                if (position % 2 == 0) {
//                    convertView = LayoutInflater.from(context).inflate(R.layout.item_summary_batch_colored, parent, false);
//                } else {
                    convertView = LayoutInflater.from(context).inflate(R.layout.new_summary_detail_item, parent, false);
//                }
            }
            TextView label = (TextView) convertView.findViewById(R.id.itemLabel);
            TextView value = (TextView) convertView.findViewById(R.id.itemValue);

//            if (position == getCount() - 1) {
//                label.setText("Total");
//                value.setText(Tool.separateThousand(String.valueOf(getSumAll())));
//                return convertView;
//            }

            DepositReportH header = getHeader(position);
            int taskCount = getTaskCount(position);
            double taskSum = getTaskSum(position);

            label.setText(header.getBatch_id());
            value.setText(taskCount + " Tasks, " + Tool.separateThousand(taskSum));

            int pos = getCount();
            curr += 1;

            if (curr == pos) {
                totalSum = getSumAll();
                getTotal();
            }

            return convertView;
        }

        private int getSumAll() {
            for (int i = 0; i < getCount(); i++) {
                sum += getTaskSum(i);
            }
            return sum;
        }
    }
}