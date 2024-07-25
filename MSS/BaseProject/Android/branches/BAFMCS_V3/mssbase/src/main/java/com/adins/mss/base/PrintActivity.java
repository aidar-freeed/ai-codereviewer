package com.adins.mss.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.checkin.CheckInManager;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.base.util.EventBusHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PrintDate;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.ReceiptVoucher;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.db.dataaccess.PrintDateDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.ReceiptVoucherDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.adins.mss.foundation.print.AB200MPrintManager;
import com.adins.mss.foundation.print.AbstractPrintManager;
import com.adins.mss.foundation.print.PrintManagerListener;
import com.adins.mss.foundation.print.rv.InputRVNumberActivity;
import com.adins.mss.foundation.print.rv.syncs.SyncRVRequest;
import com.adins.mss.foundation.print.rv.syncs.SyncRVResponse;
import com.adins.mss.foundation.print.rv.syncs.SyncRVTask;
import com.adins.mss.foundation.print.rv.syncs.SyncRvListener;
import com.androidquery.AQuery;

import org.acra.ACRA;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import zj.com.cn.bluetooth.sdk.DeviceListActivity;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

/**
 * Created by winy.firdasari on 30/01/2015.
 */

public class PrintActivity extends AppCompatActivity implements View.OnClickListener, PrintManagerListener {
    public static final String TASKS = "PrintActivitiy.TASKS";
    public static final String UUID_TASKH = "uuid_taskh";
    public static final String COUNT_RV_NUMBER = "count_rv_number";
    public static final String SOURCE_TASK = "source";
    public static final int FROM_ACTIVITY_RV_NUMBER = 1;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    protected static List<PrintResult> listOfPrint;
    private AbstractPrintManager printManager;
    private TextView lblPrinterStatus;
    private Button btnConnect;
    private Button btnPrint;
    private String uuidTaskH;
    private boolean isPrintDeposit;
    private AQuery query;
    private TaskH taskH;
    private String printerName = "-";
    private String source;
    private BluetoothAdapter mBluetoothAdapter;

    public static void setListOfPrint(List<PrintResult> listOfPrint) {
        PrintActivity.listOfPrint = listOfPrint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_activity);

        Toolbar toolbar = (Toolbar) findViewById(com.adins.mss.base.R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_print));
        toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
        setSupportActionBar(toolbar);

        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());

        query = new AQuery(this);

        lblPrinterStatus = (TextView) findViewById(R.id.lblPrinterStatus);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnPrint = (Button) findViewById(R.id.btnPrint);

        if (btnConnect != null) {
            btnConnect.setOnClickListener(this);
            btnPrint.setOnClickListener(this);
        }

        //bong 8 may 15 - get print result from db
        String taskId = this.getIntent().getStringExtra("taskId");
        isPrintDeposit = getIntent().getBooleanExtra("isPrintDeposit", false);
        source = this.getIntent().getStringExtra("source");

        taskH = TaskHDataAccess.getOneTaskHeader(getApplicationContext(), taskId);
        List<PrintResult> results;

        uuidTaskH = taskH != null ? taskH.getUuid_task_h() : taskId;
        results = PrintResultDataAccess.getAll(getApplicationContext(), uuidTaskH);

        if (results == null || results.isEmpty()) {
            Toast.makeText(this, getString(R.string.printResultNull), Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        printManager = new AB200MPrintManager(this, results);
        Constant.setListOfPrintItem(results);

        updateUI(printManager.isConnected());

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.bt_off), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!UserHelp.isActive)
                            UserHelp.showAllUserHelp(PrintActivity.this,
                                PrintActivity.this.getClass().getSimpleName());
                    }
                }, SHOW_USERHELP_DELAY_DEFAULT);
            }
        });
        return super.onCreateView(parent, name, context, attrs);
    }

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
    protected void onDestroy() {
        super.onDestroy();

        if (printManager != null) {
            if (printManager.isConnected()) {
                try {
                    printManager.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            printManager.releaseResources();
        }

        query = null;
        printManager = null;
        setListOfPrint(null);
    }

    @Override
    public void onBackPressed() {
        if (!query.id(R.id.btnConnect).getButton().isEnabled() ||
                !query.id(R.id.btnPrint).getButton().isEnabled()) {
            Toast.makeText(this, "cannot close, please wait...", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    protected void updateUI(boolean isConnected) {
        if (btnConnect != null) {
            if (isConnected) {
                lblPrinterStatus.setText(getString(R.string.connected));
                btnConnect.setText(getString(R.string.mnDisconnect));
                btnPrint.setVisibility(View.VISIBLE);
            } else {
                lblPrinterStatus.setText(getString(R.string.not_connected));
                btnConnect.setText(getString(R.string.mnConnect));
                btnPrint.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(com.adins.mss.base.R.menu.main_menu, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenuIcon(Global.isGPS);

        if(Global.ENABLE_USER_HELP &&
                (Global.userHelpGuide.get(PrintActivity.this.getClass().getSimpleName())!=null) ||
                Global.userHelpDummyGuide.get(PrintActivity.this.getClass().getSimpleName()) != null){
            menu.findItem(com.adins.mss.base.R.id.mnGuide).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private static Menu mainMenu;

    public static void updateMenuIcon(boolean isGPS) {
        UpdateMenuIcon uItem = new UpdateMenuIcon();
        uItem.updateGPSIcon(mainMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == com.adins.mss.base.R.id.mnGPS && Global.LTM != null) {
            if (Global.LTM.getIsConnected()) {
                Global.LTM.removeLocationListener();
                Global.LTM.connectLocationClient();
            } else {
                CheckInManager.startGPSTracking(getApplicationContext());
            }
            Animation a = AnimationUtils.loadAnimation(this, com.adins.mss.base.R.anim.gps_rotate);
            findViewById(com.adins.mss.base.R.id.mnGPS).startAnimation(a);
        }

        if(id==R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UserHelp.showAllUserHelp(PrintActivity.this, PrintActivity.this.getClass().getSimpleName());
                }
            }, SHOW_USERHELP_DELAY_DEFAULT);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void connect() {
        try {
            printManager.connect();
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.failed_to_connect), Toast.LENGTH_SHORT).show();
        }
    }

    protected void connect(BluetoothDevice device) {
        try {
            printManager.connect();
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.failed_to_connect), Toast.LENGTH_SHORT).show();
        }
    }

    protected void disconnect() {
        try {
            printManager.disconnect();
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnConnect) {
            if (!btnConnect.getText().equals(getResources().getString(R.string.connecting))) {
                if (printManager.isConnected()) {
                    disconnect();
                } else {
                    Intent serverIntent = new Intent(PrintActivity.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
            }
        } else {
            new PrintAsync().execute();
        }
    }

    @Override
    public void onConnected(String deviceName) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(400);

        query.id(R.id.tv_device_name).text(deviceName);
        query.id(R.id.btnConnect).enabled(true);
        updateUI(printManager.isConnected());
        Toast.makeText(this, getString(R.string.connected), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectFailed() {
        onDisconnect();
        showErrorDialog(ErrorType.PRINT_FAILED);
    }

    @Override
    public void onConnecting() {
        query.id(R.id.btnConnect).enabled(false).text(R.string.connecting);
    }

    @Override
    public void onDisconnect() {
        query.id(R.id.tv_device_name).text("-");
        printerName = "-";
        query.id(R.id.btnConnect).enabled(true);
        updateUI(printManager.isConnected());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            // Get the device MAC address
            String address = data.getExtras().getString(
                    DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            // Get the BLuetoothDevice object
            if (BluetoothAdapter.checkBluetoothAddress(address)) {
                BluetoothDevice device = mBluetoothAdapter
                        .getRemoteDevice(address);
                // Attempt to connect to the device
                connect(device);
            } else {
                showErrorDialog(ErrorType.PRINT_FAILED);
            }
        } else {
            finish();
        }
    }

    private void syncRvNumber() {
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        SyncRVRequest request = new SyncRVRequest();
        request.setLastDtmCrt(ReceiptVoucherDataAccess.getLastDate(this, uuidUser));

        SyncRVTask task = new SyncRVTask(this, request, new SyncRvListener() {
            ProgressDialog dialog;

            @Override
            public void onProgress() {
                dialog = ProgressDialog.show(PrintActivity.this, "Sync RV Number", getString(R.string.please_wait), true, false);
            }

            @Override
            public void onError(SyncRVResponse response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (response.getErrorMessage() != null) {
                    showErrorDialog(ErrorType.SYNC_RV_FAILED);
                }
            }

            @Override
            public void onSuccess(SyncRVResponse response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                List<ReceiptVoucher> rvNumbers = response.getListReceiptVoucher();

                if (rvNumbers != null && !rvNumbers.isEmpty()) {
                    try {
                        ReceiptVoucherDataAccess.addNewReceiptVoucher(PrintActivity.this,
                                GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                                rvNumbers);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                        ACRA.getErrorReporter().putCustomData("errorRV", e.getMessage());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Error: Insert RV Error. " + e.getMessage()));
                        Toast.makeText(PrintActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                openInputRVActivity();

            }
        });
        task.execute();
    }

    private void openInputRVActivity() {
        Intent intent = new Intent(PrintActivity.this, InputRVNumberActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(UUID_TASKH, uuidTaskH);
        intent.putExtra(SOURCE_TASK, source);
        startActivityForResult(intent, FROM_ACTIVITY_RV_NUMBER);
    }

    private void showErrorDialog(ErrorType errorType) {
        if (isPrintDeposit) return;
        if (taskH != null && taskH.getPrint_count() != null && taskH.getPrint_count() > 0) {
            return;
        }

        final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(this)
                .isCancelable(true)
                .isCancelableOnTouchOutside(false);

        switch (errorType) {
            case PRINT_FAILED:
                dialog.withTitle(getString(R.string.connect_failed))
                        .withMessage(R.string.reconnect_message)
                        .withButton1Text(getString(R.string.try_again))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent serverIntent = new Intent(PrintActivity.this, DeviceListActivity.class);
                                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                            }
                        })
                        .withButton2Text(getString(R.string.input_rv))
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                syncRvNumber();
                            }
                        });
                break;
            case SYNC_RV_FAILED:
                boolean isRvNumberEmpty = ReceiptVoucherDataAccess.getByStatus(this,
                        GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                        ReceiptVoucherDataAccess.STATUS_NEW).isEmpty();
                dialog.withTitle(getString(R.string.error_capital))
                        .withMessage(R.string.sync_rv_failed)
                        .withButton1Text(getString(R.string.try_again))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                syncRvNumber();
                            }
                        });
                if (!isRvNumberEmpty) {
                    dialog.withButton2Text(getString(R.string.btnNext))
                            .setButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    openInputRVActivity();
                                }
                            });
                } else {
                    dialog.withButton2Text(getString(R.string.btnClose))
                            .setButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
                break;
        }

        dialog.show();
    }

    public enum ErrorType {
        PRINT_FAILED,
        SYNC_RV_FAILED
    }

    public class PrintAsync extends AsyncTask<Void, Void, Boolean> {

        public PrintAsync() {
            //EMPTY
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            query.id(R.id.btnPrint).enabled(false).text("Printing...");
            query.id(R.id.btnConnect).enabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (printManager.isConnected()) {
                    if (printerName.toLowerCase().contains("sato")) {
                        return printManager.printSato();
                    } else {
                        return printManager.print();
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (aBoolean && printManager.isConnected() && printManager.isPrinterConnected()) {
                        if (GlobalData.getSharedGlobalData().getApplication() == null) {
                            NewMainActivity.InitializeGlobalDataIfError(getApplicationContext());
                        }

                        if (GlobalData.getSharedGlobalData().getApplication().equals("MC") && !isPrintDeposit) {
                            if (taskH != null) {
                                int count = taskH.getPrint_count() == null ? 0 : taskH.getPrint_count();
                                taskH.setPrint_count(count + 1);
                                TaskHDataAccess.addOrReplace(PrintActivity.this, taskH);
                            }

                            PrintDate taskSubmit = new PrintDate();
                            taskSubmit.setDtm_print(new Date());
                            taskSubmit.setUuid_task_h(uuidTaskH);
                            PrintDateDataAccess.addOrReplace(PrintActivity.this, taskSubmit);

                            EventBusHelper.post(taskSubmit);
                        }

                        query.id(R.id.btnPrint).enabled(true).text(R.string.print);
                        query.id(R.id.btnConnect).enabled(true);
                        return;
                    }

                    query.id(R.id.btnPrint).enabled(true).text(R.string.print);
                    query.id(R.id.btnConnect).enabled(true);

                    Toast.makeText(PrintActivity.this, "Device is not connected to the printer", Toast.LENGTH_SHORT).show();
                    try {
                        showErrorDialog(ErrorType.PRINT_FAILED);
                        printManager.disconnect();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                }
            }, 2000);
        }
    }

}
