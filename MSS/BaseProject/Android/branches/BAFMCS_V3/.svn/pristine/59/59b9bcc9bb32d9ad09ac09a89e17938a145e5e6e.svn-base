package com.adins.mss.foundation.print.rv;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Keep;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.PrintActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SendResultActivity;
import com.adins.mss.base.util.EventBusHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.dao.ReceiptVoucher;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.ReceiptVoucherDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.logger.Logger;
import com.androidquery.AQuery;

import java.util.List;
import java.util.Locale;

/**
 * Created by angga.permadi on 4/20/2016.
 */
public class InputRVNumberActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private AQuery query;
    private RVNumberSender sender;
    private TaskH taskH;
    private ReceiptVoucher rvNumber;
    private String source;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_rv_number);
        setupActionBar();

        this.context = this;

        if (getIntent() != null && getIntent().getExtras() != null &&
                !getIntent().getExtras().getString(PrintActivity.UUID_TASKH, "").isEmpty()) {
            String uuidTask = getIntent().getExtras().getString(PrintActivity.UUID_TASKH);
            taskH = TaskHDataAccess.getOneHeader(this, uuidTask);
            if (taskH == null) taskH = TaskHDataAccess.getOneTaskHeader(this, uuidTask);
            source = getIntent().getExtras().getString(PrintActivity.SOURCE_TASK);
            source = (source == null) ? "" : source;
        }

        if (taskH == null) {
            Toast.makeText(this, "sorry something wrong, try again later", Toast.LENGTH_SHORT).show();
            finish();
        }

        query = new AQuery(this);
        Spinner snReceiptVouchers = (Spinner) query.id(R.id.sn_rv_numbers).getView();

        List<ReceiptVoucher> rvNumbers;
        rvNumbers = ReceiptVoucherDataAccess.getByStatus(this,
                GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                ReceiptVoucherDataAccess.STATUS_NEW);
        ReceiptVoucher hint = new ReceiptVoucher();
        hint.setRv_number(getString(R.string.select_rv_number));
        rvNumbers.add(0, hint);
        RvNumberAdapter spAdapter = new RvNumberAdapter(this, R.layout.spinner_style2, rvNumbers);
        spAdapter.setDropDownViewResource(R.layout.spinner_style);
        snReceiptVouchers.setAdapter(spAdapter);
        snReceiptVouchers.setOnItemSelectedListener(this);

        query.id(R.id.bt_send).clicked(this, "sendRVNumber");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sender = null;
        query = null;
        rvNumber = null;
    }

    private OnSendRVListener listener = new OnSendRVListener() {
        @Override
        public void onSendReceiptVoucher(RVNumberResponse response) {
            if (taskH == null) {
                Toast.makeText(InputRVNumberActivity.this, "TaskH not available", Toast.LENGTH_SHORT).show();
                return;
            }

            if (response.getReqCode() != ApiCodes.RV_NUMBER)
                return;

            Logger.d(this, "onEvent : RVNumberResponse = " + response.toString());

            String title, message;

            if (response.getErrorMessage() != null) {
                title = getString(R.string.failed);
                message = buildErrorMessage(response.getErrorMessage());
                showResultDialog(title, message);
                return;
            }

            if (response.getStatus() != null) {
                if (response.getStatus().getCode() == 0) {
                    title = getString(R.string.success);
                    message = getString(R.string.message_sending_success);
                } else {
                    title = getString(R.string.failed);
                    message = buildErrorMessage(response.getStatus().getMessage());
                }
            } else {
                title = getString(R.string.failed);
                message = buildErrorMessage(getString(R.string.input_rv_number_failed));
            }

            showResultDialog(title, message);
            try {
                String statusRV = title.equals(getString(R.string.failed)) ? TaskHDataAccess.STATUS_RV_PENDING :
                        TaskHDataAccess.STATUS_RV_SENT;
                if (taskH != null) {
                    boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    if (isRVinFront) {
                        taskH.setRv_number(rvNumber.getUuid_receipt_voucher());
                        taskH.setStatus_rv(statusRV);
                        TaskHDataAccess.addOrReplace(context, taskH);
                    } else {
                        if (TaskHDataAccess.STATUS_RV_SENT.equalsIgnoreCase(statusRV)) {
                            taskH.setRv_number(rvNumber.getUuid_receipt_voucher());
                            taskH.setStatus_rv(statusRV);
                            TaskHDataAccess.addOrReplace(context, taskH);
                            if ("log".equalsIgnoreCase(source)) {
                                CustomerFragment.getHeader().setRv_number(rvNumber.getUuid_receipt_voucher());
                            } else if ("submit".equalsIgnoreCase(source)) {
                                SendResultActivity.rvNumber = rvNumber.getUuid_receipt_voucher();
                            }
                        } else {
                            ReceiptVoucherDataAccess.updateToNew(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), rvNumber.getUuid_receipt_voucher());
                        }
                    }

                    response.setReqCode(ApiCodes.RV_NUMBER_AUTO_SEND);
                    // send event to MainService, jalanin autosend rv number
                    EventBusHelper.post(response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) return;
        if (parent.getItemAtPosition(position) instanceof ReceiptVoucher) {
            rvNumber = (ReceiptVoucher) parent.getItemAtPosition(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setupActionBar() {
        if (getActionBar() == null) return;

        getActionBar().setTitle(R.string.input_rv_number_title);
    }

    @Keep // subcribe
    public void sendRVNumber() {
        if (rvNumber == null) {
            Toast.makeText(this, R.string.input_rv_number_error, Toast.LENGTH_SHORT).show();
            return;
        }

        doSend();
    }

    private void doSend() {
        if (sender != null) return; // send harus cuma sekali
        if (GlobalData.getSharedGlobalData().getUser() == null) return;

        ReceiptVoucherDataAccess.updateToUsed(this,
                GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                rvNumber);

        RVEntity rv = new RVEntity();
        rv.setUuid_task_h(taskH.getUuid_task_h());
        rv.setRv_number(rvNumber.getRv_number());
        rv.setDtm_use(rvNumber.getDtm_use());

        RVNumberRequest entity = new RVNumberRequest();
        entity.setRvBlank(rv);

        sender = new RVNumberSender(this, entity, ApiCodes.RV_NUMBER, listener);
        sender.execute();
    }

    private void showResultDialog(String title, String message) {
        final NiftyDialogBuilder dialog = new NiftyDialogBuilder(this, R.style.dialog_untran);
        dialog.withTitle(title)
                .withMessage(message)
                .isCancelable(false)
                .isCancelableOnTouchOutside(false)
                .withButton1Text(getString(R.string.btnClose))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                })
                .show();
    }

    private String buildErrorMessage(String message) {
        return message + ". ";
    }

    public interface OnSendRVListener {
        public void onSendReceiptVoucher(RVNumberResponse response);
    }

}
