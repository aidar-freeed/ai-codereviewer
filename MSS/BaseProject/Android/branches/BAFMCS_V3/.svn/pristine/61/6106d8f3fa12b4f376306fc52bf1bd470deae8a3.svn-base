package com.adins.mss.coll.fragments.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.coll.Navigator;
import com.adins.mss.coll.R;
import com.adins.mss.coll.commons.Generator;
import com.adins.mss.coll.commons.Toaster;
import com.adins.mss.coll.commons.ViewManager;
import com.adins.mss.coll.interfaces.callback.DepositReportCallback;
import com.adins.mss.coll.interfaces.DepositReportImpl;
import com.adins.mss.coll.interfaces.DepositReportInterface;
import com.adins.mss.coll.interfaces.NavigatorInterface;
import com.adins.mss.coll.tool.Constants;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.TaskD;
import com.adins.mss.foundation.formatter.Tool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 28/07/2017.
 */

public class DepositReportRecapitulateView extends ViewManager {

    protected double total = 0;

    public String batchId;
    public Activity activity;

    private View view;
    private int totalNeedPrint;
    private DepositReportInterface depositReport;
    List<TaskD> reportsReconcile = new ArrayList<TaskD>();

    private TextView txtBatchId;
    private TextView txtTotal;

    public DepositReportRecapitulateView(Activity activity) {
        super(activity);
        this.activity = activity;
        depositReport = new DepositReportImpl(activity);
    }

    @Override
    public void publish() {
        depositReport.getReportsReconcile(this);
    }

    public View layoutInflater(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.new_fragment_deposit_report_recapitulate, container, false);
        return view;
    }

    @Override
    public void onCreate() {
        txtBatchId = (TextView) view.findViewById(R.id.batchId);
        txtTotal = (TextView) view.findViewById(R.id.total);

        txtBatchId.setText(Generator.generateBatchId(activity));
        batchId = txtBatchId.getText().toString().trim();
        txtTotal.setText(Tool.separateThousand(String.valueOf(total)));

        Button transferButton = (Button) view.findViewById(R.id.btnTransfer);

        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total = depositReport.sumOfItems(reportsReconcile);
                if(total!=0) doTransfer();
                else Toast.makeText(activity, activity.getString(R.string.transfer_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getTotal() {
        txtTotal.setText(Tool.separateThousand(String.valueOf(total)));
    }

    private void doTransfer() {
        ListView list = (ListView) view.findViewById(R.id.listRecapitulationDetail);
        if (list.getAdapter().getCount() == 0) {
            Toaster.warning(activity, activity.getString(R.string.nothing_to_report));
            return;
        }else if (totalNeedPrint > 0) {
            Toaster.warning(activity, activity.getString(R.string.prompt_printRV));
            return;
        }

        BigDecimal totalValue = new BigDecimal(total);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_BUND_BATCHID, batchId);
        bundle.putString("TOTAL_DEPOSIT", totalValue.toString());


        NavigatorInterface navigator = new Navigator(activity, bundle);
        navigator.route(Navigator.DEPOSIT_REPORT_TRANSFER);
    }

    @Override
    public void OnLoadReconcileData(List<TaskD> reconcileReport, int totalNeedPrint) {
        this.reportsReconcile = reconcileReport;
        this.totalNeedPrint   = totalNeedPrint;
        containerInit();
    }

    private class RecapitulationListAdapter extends ArrayAdapter<TaskD> {
        private final TaskD[] originalItems;

        public RecapitulationListAdapter(Context context, int resource, TaskD[] objects) {
            super(context, resource, objects);
            originalItems = objects;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

                view = LayoutInflater.from(getContext()).inflate(R.layout.new_recapitulation_detail_item, parent, false);

                TextView label = (TextView) view.findViewById(R.id.itemLabel);
                TextView value = (TextView) view.findViewById(R.id.itemValue);

                if (position == getCount()-1) {
                    total = depositReport.sumOfItems(new ArrayList<TaskD>(Arrays.asList(originalItems)));
                    getTotal();
                }

                TaskD item = getItem(position);
                if (!item.getText_answer().equals("0") && !item.getText_answer().equals("")) {
                    label.setText(item.getTaskH().getAppl_no());
                    value.setText(Tool.separateThousand(item.getText_answer()));
                }

            return view;
        }
    }

    private class RecapitulationListAdapterDummy extends ArrayAdapter<TaskD> {

        public RecapitulationListAdapterDummy(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            view = LayoutInflater.from(getContext()).inflate(R.layout.new_recapitulation_detail_item, parent, false);

            TextView label = (TextView) view.findViewById(R.id.itemLabel);
            TextView value = (TextView) view.findViewById(R.id.itemValue);

            label.setText("Sample Batch1");
            value.setText(Tool.separateThousand(10000));

            return view;
        }
    }
    public void containerDummy(){
        ListView list = (ListView) view.findViewById(R.id.listRecapitulationDetail);
        list.setAdapter(new RecapitulationListAdapterDummy(activity, R.layout.new_recapitulation_detail_item));
    }
    public void containerInit(){
        ListView list = (ListView) view.findViewById(R.id.listRecapitulationDetail);
        list.setAdapter(new RecapitulationListAdapter(activity, R.layout.new_recapitulation_detail_item,
                reportsReconcile.toArray(new TaskD[reportsReconcile.size()])));
    }
}
