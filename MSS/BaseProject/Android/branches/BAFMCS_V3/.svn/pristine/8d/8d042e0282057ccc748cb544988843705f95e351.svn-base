package com.adins.mss.coll.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.form.questions.ImageViewerActivity;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ImageQuestionViewHolder;
import com.adins.mss.base.timeline.Constants;
import com.adins.mss.base.todolist.form.CashOnHandResponse;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.R;
import com.adins.mss.coll.commons.Toaster;
import com.adins.mss.coll.models.DepositReportRequest;
import com.adins.mss.coll.models.DepositReportResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.camerainapp.CameraActivity;
import com.adins.mss.foundation.db.dataaccess.DepositReportDDataAccess;
import com.adins.mss.foundation.db.dataaccess.DepositReportHDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintItemDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.foundation.image.Utils;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.soundcloud.android.crop.Crop;

import org.acra.ACRA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DepositReportTransferFragmentNew extends Fragment {

    public static final String uuidLookupDummy = "lookupDummy";
    private static byte[] byteImage = null;
    private static Bitmap image = null;
    private static Bitmap tempImage = null;
    private View view;
    private View asBank;
    private EditText editNamaKasir;
    private ImageView imageBukti;
    private Button buttonSelectPhoto;
    private Button buttonSend;
    private BankAccountAdapter bankAccountAdapter;
    private List<Lookup> bankAccountList;
    private String selectedBank, selectedbankName, selectedBankAccount;
    private String total = "";
    private String batchId = "";
    private String formName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bankAccountList = new ArrayList<>();
        bankAccountList.clear();
        setDummyBankAccountSpinner();
        String schemeFlag;
        if (DepositReportRecapitulateFragmentNew.selectedDepositSchemeName.contains("Konven")) {
            schemeFlag = "KONVEN";
        } else {
            schemeFlag = "SYARIAH";
        }

        bankAccountList.addAll(LookupDataAccess.getAllByLovGroup(getActivity(), "BANK_NAME", GlobalData.getSharedGlobalData().getUser().getBranch_id(), schemeFlag));

        String branchId = DepositReportRecapitulateFragmentNew.selectedDepositUserObject.getBranch_id();
        if (Global.IS_DEV) {
            System.out.print(branchId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_deposit_report_transfer_new, container, false);
        AppCompatSpinner spinnerBankAccount = (AppCompatSpinner) view.findViewById(R.id.spinnerBankAccount);
        bankAccountAdapter = new BankAccountAdapter(getActivity(), R.layout.spinner_style, R.id.text_spin, bankAccountList);
        bankAccountAdapter.setDropDownViewResource(R.layout.spinner_style);

        spinnerBankAccount.setAdapter(bankAccountAdapter);
        spinnerBankAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedBank = bankAccountAdapter.getItem(position).getValue();
                    selectedbankName = "";
                    selectedBankAccount = "";
                } else {
                    selectedBank = bankAccountAdapter.getItem(position).getValue();
                    selectedBank = selectedBank.replace(" ", "");
                    String[] answers = Tool.split(selectedBank, "|");
                    selectedbankName = answers[0];
                    selectedBankAccount = answers[1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    public void setLabel() {
        if (view != null) {
            TextView txtNamaKasir = (TextView) view.findViewById(R.id.txtCashierName);
            TextView txtBuktiTransfer = (TextView) view.findViewById(R.id.txtBuktiTransfer);

            txtNamaKasir.setText(getActivity().getString(R.string.label_cashier_name_2));
            txtBuktiTransfer.setText(getActivity().getString(R.string.label_transfer_evidence_2));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setLabel();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        total = getArguments().getString("TOTAL_DEPOSIT");
        batchId = getArguments().getString("BATCHID");
        formName = getArguments().getString("FORM");
        asBank = view.findViewById(R.id.transferAsBank);
        View asCashier = view.findViewById(R.id.transferAsCashier);

        asCashier.setVisibility(View.GONE);
        asBank.setVisibility(View.VISIBLE);

        editNamaKasir = (EditText) view.findViewById(R.id.editNamaKasir);
        imageBukti = (ImageView) view.findViewById(R.id.imageBukti);
        buttonSelectPhoto = (Button) view.findViewById(R.id.buttonSelectPhoto);
        buttonSend = (Button) view.findViewById(R.id.buttonSend);

        setLabel();
        imageBukti.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View paramView) {
                if (imageBukti.getDrawable() != null && image != null) {
                    if (byteImage == null)
                        byteImage = Utils.bitmapToByte(tempImage);
                    imageBukti.setDrawingCacheEnabled(true);
                    imageBukti.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    imageBukti.buildDrawingCache();
                    image = Bitmap.createBitmap(imageBukti.getDrawingCache(true));
                    Global.isViewer = true;

                    Global.isViewer = true;
                    Bundle extras = new Bundle();
                    extras.putByteArray(ImageViewerActivity.BUND_KEY_IMAGE, byteImage);
                    extras.putInt(ImageViewerActivity.BUND_KEY_IMAGE_QUALITY, Utils.picQuality);
                    extras.putBoolean(ImageViewerActivity.BUND_KEY_IMAGE_ISVIEWER, Global.isViewer);
                    Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
                    intent.putExtras(extras);
                    imageBukti.setDrawingCacheEnabled(false);
                    startActivity(intent);
                }
            }
        });

        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.flag_edit = 2;
                openCameraApp(getActivity());
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSend.setClickable(false);
                buttonSelectPhoto.setClickable(false);

                if (Tool.isInternetconnected(getActivity())) {
                    new SendDepositReportTask(getActivity()).
                            execute(selectedBankAccount,
                                    selectedbankName,
                                    editNamaKasir.getText().toString().trim(),
                                    String.valueOf(asBank.getVisibility()));
                } else {
                    Toaster.warning(getActivity(), getActivity().getString(R.string.failed_send_data));
                    buttonSend.setClickable(true);
                    buttonSelectPhoto.setClickable(true);
                }
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
            menu.findItem(R.id.menuMore).setVisible(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.REQUEST_IN_APP_CAMERA && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                Uri uri = Uri.parse(bundle.getString(CameraActivity.PICTURE_URI));
                File file = new File(uri.getPath());
                image = Utils.pathToBitmapWithRotation(file);
                tempImage = Utils.pathToBitmapWithRotation(file);
                byteImage = Utils.pathBitmapToByteWithRotation(file);
                imageBukti.setImageBitmap(image);
            }
        } else if (requestCode == Utils.REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            Uri uri = Uri.parse(DynamicFormActivity.mCurrentPhotoPath);
            File file = new File(uri.getPath());
            image = Utils.pathToBitmapWithRotation(file);
            byteImage = Utils.pathBitmapToByteWithRotation(file);
            tempImage = Utils.pathToBitmapWithRotation(file);
            imageBukti.setImageBitmap(image);
        } else if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            Uri outputUri = data.getData();
            try {
                image = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(outputUri));
                tempImage = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(outputUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageBukti.setImageBitmap(image);
        }
        Utility.freeMemory();
    }

    private void openCameraApp(FragmentActivity mActivity) {
        if (GlobalData.getSharedGlobalData().isUseOwnCamera()) {
            int quality = Utils.picQuality;
            int thumbHeight = Utils.picHeight;
            int thumbWidht = Utils.picWidth;

            Intent intent = new Intent(mActivity, CameraActivity.class);
            intent.putExtra(CameraActivity.PICTURE_WIDTH, thumbWidht);
            intent.putExtra(CameraActivity.PICTURE_HEIGHT, thumbHeight);
            intent.putExtra(CameraActivity.PICTURE_QUALITY, quality);

            startActivityForResult(intent, Utils.REQUEST_IN_APP_CAMERA);
        } else {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = ImageQuestionViewHolder.createImageFile(getActivity());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (photoFile != null) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(intent, Utils.REQUEST_CAMERA);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void generatePrintResultDepReport(Activity activity, String cashierName, DepositReportH report) {
        List<PrintItem> printItemList = PrintItemDataAccess.getAll(activity, "DUMYUUIDSCHEMEFORDEPOSITREPORT");

        //delete dulu yang ada di database, karena generate printResult dengan jawaban yang baru
        List<PrintResult> printResultByTaskH = PrintResultDataAccess.getAll(activity, report.getBatch_id());
        if (printResultByTaskH.size() > 0) {
            PrintResultDataAccess.delete(activity, report.getBatch_id());
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
                } else if (label.equals("Cashier Name")) {
                    if (PRtransferBy.getValue().equals("Cashier")) {
                        printResult.setLabel(label);
                        printResult.setValue(report.getCashier_name());
                    }
                } else if (label.equals("Account No")) {
                    if (PRtransferBy.getValue().equals("Bank")) {
                        printResult.setLabel(label);
                        printResult.setValue(report.getBank_account());
                    }
                } else if (label.equals("Bank Name")) {
                    if (PRtransferBy.getValue().equals("Bank")) {
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
                        e.printStackTrace();
                    }
                } else if (label.contains("Deposit Amount")) {
                    int no = Integer.valueOf(label
                            .replace("Deposit Amount", ""));
                    printResult.setLabel("Deposit Amt");
                    List<DepositReportD> reportDs = report
                            .getDepositReportDList();
                    try {
                        printResult.setValue(Tool.separateThousand(reportDs
                                .get(no).getDeposit_amt()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (label.equals("Total")) {
                    printResult.setLabel(label);
                    printResult.setValue(String.valueOf(Tool
                            .separateThousand(total)));
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
                    e.printStackTrace();
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
                List<DepositReportD> reportDs = report.getDepositReportDList();
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
            }
            if (printResult.getLabel() != null) {
                PrintResultDataAccess.add(activity, printResult);
            }
        }
    }

    private void setDummyBankAccountSpinner() {
        Lookup bankAccountDummy = new Lookup();
        bankAccountDummy.setUuid_lookup(uuidLookupDummy);
        bankAccountDummy.setValue(getString(R.string.choose_one));

        bankAccountList.add(0, bankAccountDummy);
    }

    public class SendDepositReportTask extends AsyncTask<String, Void, List<String>> {
        String namaKasir;
        private ProgressDialog progressDialog;
        private final FragmentActivity activity;
        private DepositReportH header;
        private ArrayList<DepositReportD> details;
        private String errMsg;
        private StringBuilder sb;

        public SendDepositReportTask(FragmentActivity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.progressSend), true);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            List<String> results = new ArrayList<>();
            String result = "";
            if (Tool.isInternetconnected(getActivity())) {
                sb = new StringBuilder();

                DepositReportRequest request = new DepositReportRequest();
                request.addImeiAndroidIdToUnstructured();
                header = new DepositReportH();
                header.setCashier_name("");
                header.setUuid_deposit_report_h(Tool.getUUID());
                header.setBatch_id(batchId);
                header.setFlag(formName);
                header.setTransfered_date(Tool.getSystemDateTime());
                header.setDtm_crt(Tool.getSystemDateTime());
                header.setUuid_user(DepositReportRecapitulateFragmentNew.selectedDepositUser);
                header.setUser(DepositReportRecapitulateFragmentNew.selectedDepositUserObject);
                if (null != header.getUuid_user() && !"".equals(header.getUuid_user())) {
                    header.setUsr_crt(header.getUuid_user());
                } else {
                    header.setUsr_crt(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                }

                if (params[3].equals(String.valueOf(View.VISIBLE))) {
                    String noRek = params[0];
                    String bankName = params[1];

                    if (noRek != null && noRek.length() > 0) {
                        header.setBank_account(noRek);
                        header.setBank_name(bankName);
                    } else {
                        sb.append(getActivity().getString(R.string.transfer_to_required));
                    }

                    if (image == null) {
                        sb.append(getActivity().getString(R.string.evidence_required));
                    } else {
                        try {
                            byteImage = Utils.bitmapToByte(tempImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (byteImage != null)
                            header.setImage(byteImage);
                        else
                            sb.append(getActivity().getString(R.string.evidence_required));
                    }
                } else {
                    namaKasir = params[2];
                    if (namaKasir != null && namaKasir.length() > 0) {
                        header.setCashier_name(namaKasir);
                    } else {
                        sb.append(getActivity().getString(R.string.cashier_required));
                    }
                }

                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                request.setReportHeader(header);

                if (sb.length() > 0) {
                    result = sb.toString();
                    results.add(0, result);
                } else {
                    details = new ArrayList<DepositReportD>();
                    List<TaskD> tasks = TaskDDataAccess.getTaskDTagTotalbyBatchId(getActivity(), DepositReportRecapitulateFragmentNew.selectedBatchId);
                    List<TaskD> reportsReconcile = new ArrayList<TaskD>();

                    for (TaskD taskD : tasks) {
                        TaskH taskH = TaskHDataAccess.getOneHeader(getActivity(), taskD.getUuid_task_h());
                        if (taskH.getIs_reconciled().equals("0")) {
                            reportsReconcile.add(taskD);
                        }
                    }
                    for (TaskD task : reportsReconcile) {
                        DepositReportD detail = new DepositReportD();
                        detail.setUuid_task_h(task.getTaskH().getUuid_task_h());
                        detail.setDtm_crt(Tool.getSystemDateTime());
                        detail.setUsr_crt(DepositReportRecapitulateFragmentNew.selectedDepositUser);
                        detail.setUuid_deposit_report_d(Tool.getUUID());
                        String value = task.getText_answer();

                        if (value == null || value.equals(""))
                            value = "0";
                        String tempAnswer = Tool.deleteAll(value, ",");
                        String[] intAnswer = Tool.split(tempAnswer, ".");
                        if (intAnswer.length > 1) {
                            if (intAnswer[1].equals("00")) {
                                value = intAnswer[0];
                            } else {
                                value = tempAnswer;
                            }
                        } else {
                            value = tempAnswer;
                        }

                        detail.setDeposit_amt(value);

                        detail.setUuid_deposit_report_h(header.getUuid_deposit_report_h());
                        details.add(detail);
                    }

                    request.setListReportDetail(details);

                    String url = GlobalData.getSharedGlobalData().getURL_SENDDEPOSITREPORT();
                    String json = GsonHelper.toJson(request);
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
                    HttpConnectionResult serverResult = null;
                    // Firebase Performance Trace Network Request
                    HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(
                            url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                    try {
                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetric, serverResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                        errMsg = e.getMessage();
                    }

                    MssRequestType cohRequest = new MssRequestType();
                    cohRequest.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    cohRequest.addImeiAndroidIdToUnstructured();
                    String urlCoh = GlobalData.getSharedGlobalData().getURL_UPDATE_CASH_ON_HAND();
                    String jsonCoh = GsonHelper.toJson(cohRequest);
                    HttpConnectionResult serverResultCoh = null;
                    // Firebase Performance Trace Network Request
                    HttpMetric networkMetricCoh = FirebasePerformance.getInstance().newHttpMetric(
                            url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetricCoh, jsonCoh);

                    try {
                        serverResultCoh = httpConn.requestToServer(urlCoh, jsonCoh, Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetricCoh, serverResultCoh);
                    } catch (Exception e) {
                        e.printStackTrace();
                        errMsg = e.getMessage();
                    }

                    try {
                        if (serverResult != null) {
                            result = serverResult.getResult();
                        }
                        results.add(0, result);
                        String resultCoh = null;
                        if (serverResultCoh != null) {
                            resultCoh = serverResultCoh.getResult();
                        }
                        results.add(1, resultCoh);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                result = getActivity().getString(R.string.no_internet_connection);
                results.add(0, result);
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<String> results) {
            boolean error = false;
            if (progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                imageBukti.setDrawingCacheEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Global.IS_DEV)
                System.out.println(results);
            if (errMsg != null) {
                final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(getActivity());
                dialog.withTitle(getActivity().getString(R.string.error_capital)).withMessage(this.errMsg).
                        withButton1Text("OK").
                        setButton1Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View paramView) {
                                dialog.dismiss();
                                CustomerFragment.doBack(getActivity());
                            }
                        }).
                        isCancelable(false).show();
                error = true;
            } else {
                if (getActivity().getString(R.string.no_internet_connection).equals(results.get(0))) {
                    Toaster.warning(getActivity(), results.get(0));
                    error = true;
                } else {
                    if (sb != null && sb.length() > 0) {
                        Toaster.warning(getActivity(), results.get(0));
                        error = true;
                    } else {
                        try {
                            DepositReportResponse responseType = GsonHelper.fromJson(results.get(0), DepositReportResponse.class);
                            if (responseType.getStatus().getCode() == 0) {
                                header.setBatch_id(responseType.getBatchId());
                                DepositReportHDataAccess.add(getActivity(), header);
                                for (DepositReportD reportD : details) {
                                    reportD.setIs_sent(Global.TRUE_STRING);
                                    reportD.setDepositReportH(header);
                                    DepositReportDDataAccess.add(getActivity(), reportD);
                                }
                                generatePrintResultDepReport(getActivity(), namaKasir, header);

                                if (results.size() == 2) {
                                    try {
                                        CashOnHandResponse cashOnHandResponse = GsonHelper.fromJson(results.get(1), CashOnHandResponse.class);
                                        if (cashOnHandResponse.getStatus().getCode() == 0) {
                                            User user = GlobalData.getSharedGlobalData().getUser();
                                            user.setCash_on_hand(cashOnHandResponse.getCashOnHand());
                                            GlobalData.getSharedGlobalData().getUser().setCash_on_hand(
                                                    cashOnHandResponse.getCashOnHand()
                                            );
                                            UserDataAccess.addOrReplace(getActivity(), user);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        error = true;
                                    }
                                }

                                final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(getActivity());
                                dialog.withTitle(getActivity().getString(R.string.success)).
                                        withMessage(getActivity().getString(R.string.success_deposit)).
                                        withButton1Text("OK").
                                        setButton1Click(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View paramView) {
                                                dialog.dismiss();
                                                CustomerFragment.doBack(getActivity());
                                                CustomerFragment.doBack(getActivity());
                                                DepositReportDetailActivity.report = header;
                                                Intent intent = new Intent(getActivity(), DepositReportDetailActivity.class);
                                                startActivity(intent);
                                            }
                                        }).
                                        isCancelable(false).show();
                            } else {
                                final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(getActivity());
                                dialog.withTitle(getActivity().getString(R.string.error_capital)).withMessage(responseType.getStatus().getMessage()).
                                        withButton1Text("OK").
                                        setButton1Click(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View paramView) {
                                                dialog.dismiss();
                                                CustomerFragment.doBack(getActivity());
                                            }
                                        }).
                                        isCancelable(false).show();
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            Toaster.warning(getActivity(), results.get(0));
                            error = true;
                        }
                    }
                }
            }
            if (error) {
                buttonSend.setClickable(true);
                buttonSelectPhoto.setClickable(true);
            }
        }
    }

    public class BankAccountAdapter extends ArrayAdapter<Lookup> {
        private final Context context;
        private final List<Lookup> values;

        public BankAccountAdapter(Context context, int resource, int textViewResourceId, List<Lookup> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context = context;
            this.values = objects;
        }

        public int getCount() {
            return values.size();
        }

        public Lookup getItem(int position) {
            return values.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getValue());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getValue());
            return label;
        }
    }

}
