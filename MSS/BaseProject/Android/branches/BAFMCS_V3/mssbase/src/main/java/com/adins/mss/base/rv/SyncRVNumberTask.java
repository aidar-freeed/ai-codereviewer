package com.adins.mss.base.rv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.ReceiptVoucher;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.ReceiptVoucherDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.print.rv.syncs.SyncRVRequest;
import com.adins.mss.foundation.print.rv.syncs.SyncRVResponse;
import com.adins.mss.foundation.print.rv.syncs.SyncRVTask;
import com.adins.mss.foundation.print.rv.syncs.SyncRvListener;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gigin.ginanjar on 28/12/2016.
 */

public class SyncRVNumberTask {

    public static void syncRvNumber(final Activity activity) {
        final String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        SyncRVRequest request = new SyncRVRequest();
        request.setLastDtmCrt(ReceiptVoucherDataAccess.getLastDate(activity, uuidUser));

        SyncRVTask task = new SyncRVTask(activity, request, new SyncRvListener() {
            ProgressDialog dialog;

            @Override
            public void onProgress() {
                dialog = ProgressDialog.show(activity, "Sync RV Number", activity.getString(R.string.please_wait), true, false);
            }

            @Override
            public void onError(SyncRVResponse response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (response.getErrorMessage() != null) {
                    showErrorDialog();
                }
            }

            @Override
            public void onSuccess(SyncRVResponse response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                List<ReceiptVoucher> rvNumbers = response.getListReceiptVoucher();

                if (rvNumbers != null && rvNumbers.size() > 0) {
                    try {
                        ReceiptVoucherDataAccess.addNewReceiptVoucher(activity,
                                GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                                rvNumbers);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                        ACRA.getErrorReporter().putCustomData("errorRV", e.getMessage());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Error: Insert RV Error. " + e.getMessage()));
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    try {
                        boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(activity, uuidUser);
                        if (isRVinFront) {
                            List<Lookup> lookupRVList = new ArrayList<>();
                            for (int i = 0; i < rvNumbers.size(); i++) {
                                ReceiptVoucher rv = rvNumbers.get(i);
                                Lookup lookup = new Lookup();

                                lookup.setUuid_lookup(rv.getUuid_receipt_voucher());
                                lookup.setCode(rv.getUuid_receipt_voucher());
                                lookup.setValue(rv.getRv_number());
                                lookup.setSequence(i);
                                lookup.setDtm_upd(rv.getDtm_crt());
                                lookup.setLov_group(Global.TAG_RV_NUMBER);

                                lookup.setIs_active(Global.TRUE_STRING);
                                lookup.setIs_deleted(Global.FALSE_STRING);
                                lookupRVList.add(lookup);
                            }
                            LookupDataAccess.addOrUpdateAll(activity, lookupRVList);
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                }
                openInputRVActivity();
            }

            private void openInputRVActivity() {
            }

            private void showErrorDialog() {
                final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(activity)
                        .isCancelable(true)
                        .isCancelableOnTouchOutside(false);
                boolean isRvNumberEmpty = ReceiptVoucherDataAccess.getByStatus(activity,
                        GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                        ReceiptVoucherDataAccess.STATUS_NEW).size() == 0;
                dialog.withTitle(activity.getString(R.string.error_capital))
                        .withMessage(R.string.sync_rv_failed)
                        .withButton1Text(activity.getString(R.string.try_again))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                syncRvNumber(activity);
                            }
                        });
                if (!isRvNumberEmpty) {
                    dialog.withButton2Text(activity.getString(R.string.btnNext))
                            .setButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    openInputRVActivity();
                                }
                            });
                } else {
                    dialog.withButton2Text(activity.getString(R.string.btnClose))
                            .setButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
            }
        });
        task.execute();
    }

    public static void syncRvNumber(Activity activity, SyncRvListener listener) {
        final String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        SyncRVRequest request = new SyncRVRequest();
        request.setLastDtmCrt(ReceiptVoucherDataAccess.getLastDate(activity, uuidUser));

        new SyncRVTask(activity, request, listener).execute();
    }

    public static void syncRvNumberInBackground(final Context activity) {
        final String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        SyncRVRequest request = new SyncRVRequest();
        request.setLastDtmCrt(ReceiptVoucherDataAccess.getLastDate(activity, uuidUser));

        SyncRVTask task = new SyncRVTask(activity, request, new SyncRvListener() {

            @Override
            public void onProgress() {

            }

            @Override
            public void onError(SyncRVResponse response) {

            }

            @Override
            public void onSuccess(SyncRVResponse response) {

                List<ReceiptVoucher> rvNumbers = response.getListReceiptVoucher();

                if (rvNumbers != null && rvNumbers.size() > 0) {
                    try {
                        ReceiptVoucherDataAccess.addNewReceiptVoucher(activity,
                                GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                                rvNumbers);
                        System.out.println();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                        ACRA.getErrorReporter().putCustomData("errorRV", e.getMessage());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Error: Insert RV Error. " + e.getMessage()));
                    }
                    /*try {
                        boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(activity, uuidUser);
                        if (isRVinFront) {
                            LookupDataAccess.deleteByLovGroup(activity, Global.TAG_RV_NUMBER);

                            List<Lookup> lookupRVList = new ArrayList<>();
                            for (int i = 0; i < rvNumbers.size(); i++) {
                                ReceiptVoucher rv = rvNumbers.get(i);
                                Lookup lookup = new Lookup();

                                lookup.setUuid_lookup(rv.getUuid_receipt_voucher());
                                lookup.setCode(rv.getUuid_receipt_voucher());
                                lookup.setValue(rv.getRv_number());
                                lookup.setSequence(i);
                                lookup.setDtm_upd(rv.getDtm_crt());
                                lookup.setLov_group(Global.TAG_RV_NUMBER);

                                lookup.setIs_active(Global.TRUE_STRING);
                                lookup.setIs_deleted(Global.FALSE_STRING);
                                lookupRVList.add(lookup);
                            }
                            LookupDataAccess.addOrUpdateAll(activity, lookupRVList);
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }*/
                }
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
