package com.adins.mss.coll.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.R;
import com.adins.mss.coll.commons.Generator;
import com.adins.mss.coll.commons.Toaster;
import com.adins.mss.coll.dummy.UserHelpCOLDummy;
import com.adins.mss.coll.fragments.view.DepositReportRecapitulateView;
import com.adins.mss.coll.tool.Constants;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.db.dataaccess.DepositReportDDataAccess;
import com.adins.mss.foundation.db.dataaccess.DepositReportHDataAccess;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintItemDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;

import org.acra.ACRA;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

/**
 * Created by Aditya Purwa on 2/13/2015.
 */
public class DepositReportRecapitulateFragment extends Fragment {
    protected double total = 0;
    String batchId;
    private DepositReportRecapitulateView view;
    List<TaskD> reportsReconcile = new ArrayList<TaskD>();
    private int totalNeedPrint;
    private DepositReportH header;
    ListView list;

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        view = new DepositReportRecapitulateView(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view.layoutInflater(inflater, container);
//        return inflater.inflate(R.layout.new_fragment_deposit_report_recapitulate, container, false);
    }

    @Override
    public void onViewCreated(View mView, Bundle savedInstanceState) {
        super.onViewCreated(mView, savedInstanceState);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        initialize();
        view.onCreate();
        view.publish();
//        Button transferButton = (Button) mView.findViewById(R.id.btnTransfer);
//        transferButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (total != 0)
//                    transfer();
//                else
//                    Toast.makeText(getActivity(), getString(R.string.transfer_failed), Toast.LENGTH_SHORT).show();
//            }
//        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserHelpCOLDummy.showDummyDepositReport(
                        DepositReportRecapitulateFragment.this.getActivity(),
                        DepositReportRecapitulateFragment.this.getClass().getSimpleName(),
                        view);
            }
        }, SHOW_USERHELP_DELAY_DEFAULT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.mnGuide){
            if(!Global.BACKPRESS_RESTRICTION) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UserHelpCOLDummy.showDummyDepositReport(
                                DepositReportRecapitulateFragment.this.getActivity(),
                                DepositReportRecapitulateFragment.this.getClass().getSimpleName(),
                                view);
                    }
                }, SHOW_USERHELP_DELAY_DEFAULT);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void generatePrintResultDepReport(Context activity, String cashierName, DepositReportH report) {
        List<PrintItem> printItemList = PrintItemDataAccess.getAll(activity, "DUMYUUIDSCHEMEFORDEPOSITREPORT");
        List<PrintResult> printResultList = new ArrayList<PrintResult>();

        //delete dulu yang ada di database, karena generate printResult dengan jawaban yang baru
        try {
            List<PrintResult> printResultByTaskH = PrintResultDataAccess.getAll(activity, report.getBatch_id());
            if (printResultByTaskH != null && printResultByTaskH.size() > 0) {
                PrintResultDataAccess.delete(activity, report.getBatch_id());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        PrintResult PRtransferBy = new PrintResult(Tool.getUUID());
        PRtransferBy.setPrint_type_id(Global.PRINT_ANSWER);
        PRtransferBy.setUser(GlobalData.getSharedGlobalData().getUser());
        PRtransferBy.setUuid_task_h(report.getBatch_id());
        for (PrintItem bean : printItemList) {
            PrintResult printResult = new PrintResult(Tool.getUUID());
            printResult.setPrint_type_id(bean.getPrint_type_id());
            printResult.setUser(GlobalData.getSharedGlobalData().getUser());
            printResult.setUuid_task_h(report.getBatch_id());

            if (bean.getPrint_type_id().equals(Global.PRINT_ANSWER)) {
                String label = bean.getPrint_item_label();
                if (label.equals("Batch ID")) {
                    printResult.setLabel(label);
                    printResult.setValue(report.getBatch_id());
                } else if (label.equals("Transfer By")) {
                    if (!report.getBatch_id().equalsIgnoreCase("-")) {
                        printResult.setLabel(label);
                        if (cashierName != null && cashierName.length() > 0) {
                            printResult.setValue("Cashier");
                            PRtransferBy.setLabel(label);
                            PRtransferBy.setValue("Cashier");
                        } else {
                            printResult.setValue("Bank");
                            PRtransferBy.setLabel(label);
                            PRtransferBy.setValue("Bank");
                        }
                    }
                } else if (label.equals("Cashier Name")) {
                    if (PRtransferBy.getValue() != null && PRtransferBy.getValue().equals("Cashier")) {
                        printResult.setLabel(label);
                        printResult.setValue(report.getCashier_name());
                    }
                } else if (label.equals("Account No")) {
                    if (PRtransferBy.getValue() != null && PRtransferBy.getValue().equals("Bank")) {
                        printResult.setLabel(label);
                        printResult.setValue(report.getBank_account());
                    }
                } else if (label.equals("Bank Name")) {
                    if (PRtransferBy.getValue() != null && PRtransferBy.getValue().equals("Bank")) {
                        printResult.setLabel(label);
                        printResult.setValue(report.getBank_name());
                    }
                } else if (label.contains("Agreement No")) {
                    int no = Integer.valueOf(label.replace("Agreement No", ""));
                    printResult.setLabel("Agreement No");
                    List<DepositReportD> reportDs = report
                            .getDepositReportDList();
                    try {
                        TaskH taskHs = TaskHDataAccess.getOneHeader(activity,
                                reportDs.get(no).getUuid_task_h());
                        String agreement_no = "";
                        if (taskHs != null)
                            agreement_no = taskHs.getAppl_no();
                        if (agreement_no == null)
                            agreement_no = "-";
                        printResult.setValue(agreement_no);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        // TODO: handle exception
                    }
                } else if (label.contains("Deposit Amount")) {
                    int no = Integer.valueOf(label
                            .replace("Deposit Amount", ""));
                    printResult.setLabel("Deposit Amt");
                    List<DepositReportD> reportDs = report
                            .getDepositReportDList();
                    try {
                        printResult.setValue(Tool.separateThousand(reportDs
                                .get(no).getDeposit_amt(), false));
                    } catch (Exception e) {
                        FireCrash.log(e);
                        // TODO: handle exception
                    }
                } else if (label.equals("Total")) {
                    printResult.setLabel(label);
                    printResult.setValue(String.valueOf(Tool
                            .separateThousand(total, false)));
                }
            } else if (bean.getPrint_type_id().equals(
                    Global.PRINT_BRANCH_ADDRESS)) {
                printResult.setLabel(GlobalData.getSharedGlobalData().getUser()
                        .getBranch_address());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_BRANCH_NAME)) {
                printResult.setLabel(GlobalData.getSharedGlobalData().getUser()
                        .getBranch_name());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_BT_ID)) {
                String btAddr = "?";
                try {
                    btAddr = BluetoothAdapter.getDefaultAdapter().getAddress();
                } catch (Exception e) {
                    FireCrash.log(e);

                }
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue(btAddr);
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LABEL)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LABEL_BOLD)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id()
                    .equals(Global.PRINT_LABEL_CENTER)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(
                    Global.PRINT_LABEL_CENTER_BOLD)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LOGO)) {
                printResult.setLabel("");
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_NEW_LINE)) {
                String label = bean.getPrint_item_label();
                int no = Integer.valueOf(label.replace("New Line", ""));
                List<DepositReportD> reportDs = DepositReportDDataAccess.getAll(getContext(), report.getUuid_deposit_report_h());
                int size = reportDs.size();
                if (no < size) {
                    printResult.setLabel("------------------------------");
                    printResult.setValue("\n");
                }
                if (no == 999) {
                    printResult.setLabel("==============================");
                    printResult.setValue("\n");
                }
                if (no == 998) {
                    printResult.setLabel("\n");
                    printResult.setValue("\n");
                }
            } else if (bean.getPrint_type_id().equals(Global.PRINT_TIMESTAMP)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_USER_NAME)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue(GlobalData.getSharedGlobalData().getUser()
                        .getFullname());
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LOGIN_ID)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue(GlobalData.getSharedGlobalData().getUser().getLogin_id());
            }
            if (printResult.getLabel() != null) {
                PrintResultDataAccess.add(activity, printResult);
            }
        }
    }

    private void initialize() {
        loadData();
    }

    private void loadData() {
        reportsReconcile.clear();
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        List<TaskD> reports = TaskDDataAccess.getTaskDTagTotal(getActivity(), uuidUser);


        for (TaskD taskD : reports) {
            TaskH taskH = TaskHDataAccess.getOneHeader(getActivity(), taskD.getUuid_task_h());
            if (taskH != null && taskH.getIs_reconciled() != null) {
                if (taskH.getIs_reconciled().equals("0")) {
                    reportsReconcile.add(taskD);
                }
            }

            if (taskH != null) {
                int printCount = taskH.getPrint_count() != null ? taskH.getPrint_count() : 0;
                String rvNumber = taskH.getRv_number();
                boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
                if (printCount > 0 || (rvNumber != null && !rvNumber.isEmpty()) || isRVinFront) {
                    // do nothing
                } else {
                    try {
                        String uuidScheme = taskH.getUuid_scheme();
                        Scheme scheme = SchemeDataAccess.getOne(getActivity(), uuidScheme);
                        if (scheme != null) {
                            if (scheme.getIs_printable().equals(Global.TRUE_STRING))
                                totalNeedPrint++;
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        {
                            totalNeedPrint++;
                        }
                    }
                }
            }

            list = (ListView) getView().findViewById(R.id.listRecapitulationDetail);
            list.setAdapter(new RecapitulationListAdapter(
                            getActivity(),
                            R.layout.item_recapitulation_detail,
                            reportsReconcile.toArray(new TaskD[reportsReconcile.size()])
                    )
            );
        }
        total = sumOfItems(reportsReconcile);
//        ListView list = (ListView) getView().findViewById(R.id.listRecapitulationDetail);
//        list.setAdapter(new RecapitulationListAdapter(
//                        getActivity(),
//                        R.layout.item_recapitulation_detail,
//                        reportsReconcile.toArray(new TaskD[reportsReconcile.size()])
//                )
//        );

//    void transfer() {
//        ListView list = (ListView) getView().findViewById(R.id.recapitulationList);
//        if (list.getAdapter().getCount() <= 2) {
//            Toaster.warning(getActivity(), getString(R.string.nothing_to_report));
//            return;
//        }else if (totalNeedPrint > 0) {
//            Toaster.warning(getActivity(), getActivity().getString(R.string.prompt_printRV));
//            return;
//        }
//
//        BigDecimal totalValue = new BigDecimal(total);
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.KEY_BUND_BATCHID, batchId);
//        bundle.putString("TOTAL_DEPOSIT", totalValue.toString());
//
//        DepositReportTransferFragment fragment = new DepositReportTransferFragment();
//        fragment.setArguments(bundle);
//        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
//		transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
//	    transaction.replace(R.id.content_frame, fragment);
//	    transaction.addToBackStack(null);
//        transaction.commit();
//    }

//    private class RecapitulationListAdapter extends ArrayAdapter<TaskD> {
//
//        private final TaskD[] originalItems;
//
//        public RecapitulationListAdapter(Context context, int resource, TaskD[] objects) {
//            super(context, resource, objects);
//            originalItems = objects;
//        }
//
//        @Override
//        public int getCount() {
//            return super.getCount() + 2;
//        }
//
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = null;
//            if (position == 0) {
//                view = LayoutInflater.from(getContext()).inflate(R.layout.item_recapitulation_detail_black, parent, false);
//
//                TextView label = (TextView) view.findViewById(R.id.itemLabel);
//                TextView value = (TextView) view.findViewById(R.id.itemValue);
//                label.setText("Batch ID");
//                value.setText(Generator.generateBatchId(getContext()));
//                batchId=value.getText().toString().trim();
//            } else {
//                view = LayoutInflater.from(getContext()).inflate(R.layout.item_recapitulation_detail, parent, false);
//
//
//                TextView label = (TextView) view.findViewById(R.id.itemLabel);
//                TextView value = (TextView) view.findViewById(R.id.itemValue);
//
//                if (position == getCount() - 1) {
//                    label.setText("Total");
//                    value.setText(Tool.separateThousand(String.valueOf(sumOfItems(new ArrayList<TaskD>(Arrays.asList(originalItems))))));
//                } else {
//                    TaskD item = getItem(position-1);
////                    label.setText(item.getTaskH().getTask_id());
//                    label.setText(item.getTaskH().getAppl_no());
//                    value.setText(Tool.separateThousand(item.getText_answer()));
//                }
//            }
//
//            return view;
//        }
//    }
//
//    private double sumOfItems(List<TaskD> items) {
//        double sum = 0;
//        try {
//            for (TaskD item : items) {
//                String value = item.getText_answer();
//                if(value==null || value.equals("")) value = "0";
//                String tempAnswer = Tool.deleteAll(value, ",");
//                String[] intAnswer = Tool.split(tempAnswer, ".");
//                if(intAnswer.length>1){
//                    if(intAnswer[1].equals("00"))
//                        value = intAnswer[0];
//                    else {
//                        value=tempAnswer;
//                    }
//                }else{
//                    value=tempAnswer;
//                }
//                double finalValue = Double.parseDouble(value);
//                sum += finalValue;
//            }
//        } catch (Exception e) {             FireCrash.log(e);
//            // TODO: handle exception
//        }
//        return sum;
//    }
    }

    void transfer() {
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        //NEW GS_PRINT_LOCK_MC
        GeneralParameter GSPrintLock = GeneralParameterDataAccess.getOne(getContext(), uuidUser, Global.GS_PRINT_LOCK_MC);

//        ListView list = (ListView) getView().findViewById(R.id.listRecapitulationDetail);
        if (list.getAdapter().getCount() <= 0) {
            Toaster.warning(getActivity(), getString(R.string.nothing_to_report));
            return;
        } else if (totalNeedPrint > 0) {
            //2018-10-30 | Add Bypass Print Lock
            if (GSPrintLock != null && GSPrintLock.getGs_value().equals("1")) {
                Toaster.warning(getActivity(), getActivity().getString(R.string.prompt_printRV));
                return;
            }
        }

        BigDecimal totalValue = new BigDecimal(total);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_BUND_BATCHID, batchId);
        bundle.putString("TOTAL_DEPOSIT", totalValue.toString());
        DepositReportTransferFragment fragment = new DepositReportTransferFragment();
        fragment.setArguments(bundle);
//        AuthCashierFragment fragment    = AuthCashierFragment.newInstance(bundle);
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    private double sumOfItems(List<TaskD> items) {
        double sum = 0;
        try {
            for (TaskD item : items) {
                String value = item.getText_answer();
                if (value == null || value.equals("")) value = "0";
                String tempAnswer = Tool.deleteAll(value, ",");
                String[] intAnswer = Tool.split(tempAnswer, ".");
                if (intAnswer.length > 1) {
                    if (intAnswer[1].equals("00"))
                        value = intAnswer[0];
                    else {
                        value = tempAnswer;
                    }
                } else {
                    value = tempAnswer;
                }
                double finalValue = Double.parseDouble(value);
                sum += finalValue;
            }
        } catch (Exception e) {
            FireCrash.log(e);
            // TODO: handle exception
        }
        return sum;
    }

    private DepositReportH prepareDummyDepositRHeader() {
        final DepositReportH header = new DepositReportH();
        header.setCashier_name("-");
//        header.setUser_id("");
//        header.setUser_pass("");
        header.setUuid_deposit_report_h(Tool.getUUID());
        header.setBatch_id("-");
        header.setTransfered_date(Tool.getSystemDateTime());
        header.setDtm_crt(Tool.getSystemDateTime());
        header.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
        header.setUser(GlobalData.getSharedGlobalData().getUser());

        ArrayList<DepositReportD> details = new ArrayList<DepositReportD>();
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        for (TaskD task : reportsReconcile) {
            DepositReportD detail = new DepositReportD();
            detail.setUuid_task_h(task.getUuid_task_h());
            detail.setDtm_crt(Tool.getSystemDateTime());
            detail.setUsr_crt(uuidUser);
            detail.setUuid_deposit_report_d(Tool.getUUID());
            String value = task.getText_answer();

            if (value == null || value.equals(""))
                value = "0";
            String tempAnswer = Tool.deleteAll(value, ",");
            String[] intAnswer = Tool.split(tempAnswer, ".");
            if (intAnswer.length > 1) {
                if (intAnswer[1].equals("00"))
                    value = intAnswer[0];
                else {
                    value = tempAnswer;
                }
            } else {
                value = tempAnswer;
            }

            detail.setDeposit_amt(value);

            detail.setUuid_deposit_report_h(header.getUuid_deposit_report_h());
            details.add(detail);
        }

        DepositReportHDataAccess.add(getActivity(), header);
        for (DepositReportD reportD : details) {
            reportD.setIs_sent("10");
            reportD.setDepositReportH(header);
            DepositReportDDataAccess.add(getActivity(), reportD);
        }

        generatePrintResultDepReport(getContext(), "-", header);
        return header;
    }

    private class RecapitulationListAdapter extends ArrayAdapter<TaskD> {

        private final TaskD[] originalItems;

        public RecapitulationListAdapter(Context context, int resource, TaskD[] objects) {
            super(context, resource, objects);
            originalItems = objects;
        }

        @Override
        public int getCount() {
            return super.getCount() + 2;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (position == 0) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_recapitulation_detail_black, parent, false);

                TextView label = (TextView) view.findViewById(R.id.itemLabel);
                TextView value = (TextView) view.findViewById(R.id.itemValue);
                label.setText("Batch ID");
                value.setText(Generator.generateBatchId(getContext()));
                batchId = value.getText().toString().trim();
            } else {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_recapitulation_detail, parent, false);



                TextView label = (TextView) view.findViewById(R.id.itemLabel);
                TextView value = (TextView) view.findViewById(R.id.itemValue);

                if (position == getCount() - 1) {
                    label.setText("Total");
                    value.setText(Tool.separateThousand(String.valueOf(sumOfItems(new ArrayList<TaskD>(Arrays.asList(originalItems))))));
                } else {
                    TaskD item = getItem(position - 1);
//                    label.setText(item.getTaskH().getTask_id());
                    label.setText(item.getTaskH().getAppl_no());
                    value.setText(Tool.separateThousand(item.getText_answer()));
                }
            }

            return view;
        }
    }
}
