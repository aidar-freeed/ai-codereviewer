package zj.com.cn.bluetooth.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import androidx.core.view.MotionEventCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.base.dynamicform.TaskManager;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.receipt.ReceiptBuilder;
import com.adins.mss.base.util.EventBusHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.PrintDate;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.ReceiptVoucher;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintDateDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.ReceiptVoucherDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.print.AB200MPrintManager;
import com.adins.mss.foundation.print.AbstractPrintManager;
import com.adins.mss.foundation.print.BixolonPrintManager;
import com.adins.mss.foundation.print.PrintManagerListener;
import com.adins.mss.foundation.print.rv.InputRVNumberActivity;
import com.adins.mss.foundation.print.rv.syncs.SyncRVRequest;
import com.adins.mss.foundation.print.rv.syncs.SyncRVResponse;
import com.adins.mss.foundation.print.rv.syncs.SyncRVTask;
import com.adins.mss.foundation.print.rv.syncs.SyncRvListener;
import com.androidquery.AQuery;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import org.acra.ACRA;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import zj.com.command.sdk.Command;
import zj.com.command.sdk.PrinterCommand;

public class Main_Activity1 extends AppCompatActivity implements OnClickListener, PrintManagerListener {
    public static final String DIRECTORY_RECEIPT = "receipt";
    /******************************************************************************************************/
    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;
    /*******************************************************************************************************/
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    //Printer type identifier
    public static final int PRINTER_TYPE_SATO = 1;
    public static final int PRINTER_TYPE_ENIBIT = 2;
    public static final int PRINTER_TYPE_WINSON = 3;
    public static final int PRINTER_TYPE_EPSON = 4;
    public static final int PRINTER_TYPE_WOOSIM = 5;
    public static final int PRINTER_TYPE_ZEBRA = 6;
    public static final int PRINTER_TYPE_JANZ = 7;
    public static final int PRINTER_TYPE_BELLA_ZCS_103 = 8;
    public static final int PRINTER_TYPE_DEFAULT = 9;
    public static final int PRINTER_TYPE_NOT_SUPPORTED = 99;
    public static final String TASKS = "PrintActivitiy.TASKS";
    public static final String UUID_TASKH = "uuid_taskh";
    public static final String SOURCE_TASK = "source";
    public static final int FROM_ACTIVITY_RV_NUMBER = 1;
    // Debugging
    private static final String TAG = "Main_Activity";
    private static final boolean DEBUG = true;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    final String[] itemsen = {"Print Init", "Print and Paper", "Standard ASCII font", "Compressed ASCII font", "Normal size",
            "Double high power wide", "Twice as high power wide", "Three times the high-powered wide", "Off emphasized mode", "Choose bold mode", "Cancel inverted Print", "Invert selection Print", "Cancel black and white reverse display", "Choose black and white reverse display",
            "Cancel rotated clockwise 90 °", "Select the clockwise rotation of 90 °", "Feed paper Cut", "Beep", "Standard CashBox",
            "Open CashBox", "Char Mode", "Chinese Mode", "Print SelfTest", "DisEnable Button", "Enable Button",
            "Set Underline", "Cancel Underline", "Hex Mode"};
    final byte[][] byteCommands = {
            {0x1b, 0x40, 0x0a},// �?�?打�?�机
            {0x0a}, //打�?�并走纸
            {0x1b, 0x4d, 0x00},// 标准ASCII字体
            {0x1b, 0x4d, 0x01},// 压缩ASCII字体
            {0x1d, 0x21, 0x00},// 字体�?放大
            {0x1d, 0x21, 0x11},// 宽高加�?
            {0x1d, 0x21, 0x22},// 宽高加�?
            {0x1d, 0x21, 0x33},// 宽高加�?
            {0x1b, 0x45, 0x00},// �?�消加粗模�?
            {0x1b, 0x45, 0x01},// 选择加粗模�?
            {0x1b, 0x7b, 0x00},// �?�消倒置打�?�
            {0x1b, 0x7b, 0x01},// 选择倒置打�?�
            {0x1d, 0x42, 0x00},// �?�消黑白�??显
            {0x1d, 0x42, 0x01},// 选择黑白�??显
            {0x1b, 0x56, 0x00},// �?�消顺时针旋转90°
            {0x1b, 0x56, 0x01},// 选择顺时针旋转90°
            {0x0a, 0x1d, 0x56, 0x42, 0x01, 0x0a},//切刀指令
            {0x1b, 0x42, 0x03, 0x03},//蜂鸣指令
            {0x1b, 0x70, 0x00, 0x50, 0x50},//钱箱指令
            {0x10, 0x14, 0x00, 0x05, 0x05},//实时弹钱箱指令
            {0x1c, 0x2e},// 进入字符模�?
            {0x1c, 0x26}, //进入中文模�?
            {0x1f, 0x11, 0x04}, //打�?�自检页
            {0x1b, 0x63, 0x35, 0x01}, //�?止按键
            {0x1b, 0x63, 0x35, 0x00}, //�?�消�?止按键
            {0x1b, 0x2d, 0x02, 0x1c, 0x2d, 0x02}, //设置下划线
            {0x1b, 0x2d, 0x00, 0x1c, 0x2d, 0x00}, //�?�消下划线
            {0x1f, 0x11, 0x03}, //打�?�机进入16进制模�?
    };
    /***************************�?�                          �?***************************************************************/
    final String[] codebar = {"UPC_A", "UPC_E", "JAN13(EAN13)", "JAN8(EAN8)",
            "CODE39", "ITF", "CODABAR", "CODE93", "CODE128", "QR Code"};
    final byte[][] byteCodebar = {
            {0x1b, 0x40},// �?�?打�?�机
            {0x1b, 0x40},// �?�?打�?�机
            {0x1b, 0x40},// �?�?打�?�机
            {0x1b, 0x40},// �?�?打�?�机
            {0x1b, 0x40},// �?�?打�?�机
            {0x1b, 0x40},// �?�?打�?�机
            {0x1b, 0x40},// �?�?打�?�机
            {0x1b, 0x40},// �?�?打�?�机
            {0x1b, 0x40},// �?�?打�?�机
            {0x1b, 0x40},// �?�?打�?�机
    };
    protected String valueSeparator = ":";
    protected char blank = ' ';
    protected int separatorStart = 12;
    int currentPrinterType = 99;
    String bluetoothAddress;
    ZebraPrinter zebraPrinter;
    Connection zebraConnection = null;
    private BixolonPrintManager printManager;
    private AbstractPrintManager printManagerSato;
    /*********************************************************************************/
    private Button btnConnect = null;
    private Button btnClose = null;
    private Button btn_BMP = null;
    private TextView tvDeviceName = null;
    private TextView tvStatus = null;
    private Bitmap bm = null;
    /******************************************************************************************************/
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    private BluetoothService mService = null;
    /******************************************************************************************************/

    private boolean isDummy;
    /*Tambahan yessi 2017-04-05*/
    private boolean isPrintDeposit;
    private String source;
    private String uuidTaskH;
    /*end*/
    private TaskH taskH;

    private String tenantName;

    /**
     * 2019.06.21
     * Nendi | Update Receipt Builder
     */
    private File receiptDir;
    private int fontSize = 23;
    private ReceiptBuilder receiptBuilder;

    private FirebaseAnalytics screenName;

    //Font Path Identifiers
    public static final String TYPEFACE_CONSOLAS = "fonts/Consolas.ttf";
    public static final String TYPEFACE_MPLUS_LIGHT = "fonts/mplus-1m-light.ttf";
    public static final String TYPEFACE_MPLUS_REGULAR = "fonts/mplus-1m-regular.ttf";
    public static final String TYPEFACE_MPLUS_MEDIUM = "fonts/nk57-monospace-cd-sb.ttf";
    public static final String TYPEFACE_MPLUS_BOLD = "fonts/nk57-monospace-cd-eb.ttf";
    public static final String TYPEFACE_MPLUS_ITALIC = "fonts/mplus-1m-italic.ttf";

    /****************************************************************************************************/
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (DEBUG)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            btnConnect.setEnabled(false);
                            btnClose.setEnabled(true);
                            btn_BMP.setEnabled(true);
                            btnConnect.setVisibility(View.GONE);
                            btnClose.setVisibility(View.VISIBLE);
                            btn_BMP.setVisibility(View.VISIBLE);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                            break;
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_WRITE:

                    break;
                case MESSAGE_READ:

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    tvStatus.setText("Connected");

                    if (mConnectedDeviceName.toLowerCase().contains("sato")) {
                        currentPrinterType = PRINTER_TYPE_SATO;
                        tvDeviceName.setText(mConnectedDeviceName + " (Sato)");
                    } else if (mConnectedDeviceName.toLowerCase().contains("qsprinter")) {
                        currentPrinterType = PRINTER_TYPE_ENIBIT;
                        tvDeviceName.setText(mConnectedDeviceName + " (Enibit)");
                    } else if (mConnectedDeviceName.contains("BlueTooth")) {
                        currentPrinterType = PRINTER_TYPE_WINSON;
                        tvDeviceName.setText(mConnectedDeviceName + " (Winson)");
                    } else if (mConnectedDeviceName.contains("TM-P20")) {
                        currentPrinterType = PRINTER_TYPE_WINSON;
                        tvDeviceName.setText(mConnectedDeviceName + " (Epson)");
                    } else if (mConnectedDeviceName.contains("zebra")) {
                        currentPrinterType = PRINTER_TYPE_ZEBRA;
                        tvDeviceName.setText(mConnectedDeviceName + " (Zebra)");
                    } else if (mConnectedDeviceName.contains("zcs103")) {
                        currentPrinterType = PRINTER_TYPE_BELLA_ZCS_103;
                        tvDeviceName.setText(mConnectedDeviceName + " (Bella V)");
                    }
                    else if (mConnectedDeviceName.contains("JZ-MP250") || mConnectedDeviceName.contains("JZ-") || mConnectedDeviceName.contains("MPT-")) {
                        currentPrinterType = PRINTER_TYPE_JANZ;
                        tvDeviceName.setText(mConnectedDeviceName + " (Janz)");
                    } else if (mConnectedDeviceName.contains("WOOSIM")) {
                        currentPrinterType = PRINTER_TYPE_WOOSIM;
                        tvDeviceName.setText(mConnectedDeviceName + " (Woosim)");
                    } else {
                        currentPrinterType = PRINTER_TYPE_DEFAULT;
                        tvDeviceName.setText(mConnectedDeviceName);
                    }
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    try {
                        if (!getIntent().hasExtra("PRINT_TEST"))
                            showErrorDialog(Main_Activity1.ErrorType.PRINT_FAILED);
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                    break;
                case MESSAGE_CONNECTION_LOST:
                    Toast.makeText(getApplicationContext(), "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                    updateUI(false);
                    break;
                case MESSAGE_UNABLE_CONNECT:
                    showErrorDialog(Main_Activity1.ErrorType.PRINT_FAILED);

                    break;
            }
        }
    };
    private AQuery query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenName = FirebaseAnalytics.getInstance(this);

        /*Tambahan yessi 2017-04-05*/
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        query = new AQuery(this);
        setContentView(R.layout.main_winson);
        /*end*/

        Toolbar toolbar = (Toolbar) findViewById(com.adins.mss.base.R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_print));
        toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
        setSupportActionBar(toolbar);

        btnConnect = (Button) findViewById(R.id.button_scan_connect);
        btnClose = (Button) findViewById(R.id.btn_close);
        btn_BMP = (Button) findViewById(R.id.btn_prtbmp);
        tvDeviceName = (TextView) findViewById(R.id.tvConnectedDeviceName);
        tvStatus = (TextView) findViewById(R.id.tvConnectionStatus);

        btnConnect.setOnClickListener(this);

        btnClose.setOnClickListener(this);

        btn_BMP.setOnClickListener(this);

        if (!getIntent().hasExtra("PRINT_TEST")) {
            String taskId = this.getIntent().getStringExtra("taskId");
            try {
                taskH = TaskHDataAccess.getOneTaskHeader(getApplicationContext(), taskId);
            } catch (Exception e) {
                FireCrash.log(e);
            }
            List<PrintResult> results;

            uuidTaskH = taskH != null ? taskH.getUuid_task_h() : taskId;
            results = PrintResultDataAccess.getAll(getApplicationContext(), uuidTaskH);

            if (results == null || results.isEmpty()) {
                Toast.makeText(this, getString(R.string.printResultNull), Toast.LENGTH_SHORT).show();
                this.finish();
                return;
            }
            printManagerSato = new AB200MPrintManager(this, results);
            Constant.setListOfPrintItem(results);
        } else {
            printManagerSato = new AB200MPrintManager(this, new ArrayList<PrintResult>());
        }

        /* Nendi: 2018-12 */
        receiptDir = getDir(DIRECTORY_RECEIPT, Context.MODE_PRIVATE);
        isDummy    = getIntent().getBooleanExtra("isDummy", false);
        GeneralParameter fontSizeGS = GeneralParameterDataAccess.getOne(this,
                GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_PRINT_SIZE);
        fontSize = (fontSizeGS != null) ? Integer.parseInt(fontSizeGS.getGs_value()) : 23;
        /* End Nendi */

        /*Tambahan yessi 2017-04-05*/
        isPrintDeposit = getIntent().getBooleanExtra("isPrintDeposit", false);
        source = this.getIntent().getStringExtra("source");
        /*end*/

        // Get local Bluetooth adapter and turn on if adapter turn off
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        if (getIntent().hasExtra(BluetoothDevice.EXTRA_DEVICE)) {
            if (mService == null) KeyListenerInit();
            bluetoothAddress = getIntent().getStringExtra(BluetoothDevice.EXTRA_DEVICE);
            connect(bluetoothAddress);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try {
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            if (null == locale) {
                locale = new Locale(LocaleHelper.ENGLSIH);
            }
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable(); //Nendi: 2018-12-28 | Auto turn on bluetooth device
            }
        } else {
            if (mService == null)
                KeyListenerInit();
        }

    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this, getString(R.string.screen_name_print), null);
        if (!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }

        if (mService != null && mService.getState() == BluetoothService.STATE_NONE) {
            mService.start();
        }

        //Nendi: 2019-01-08 | Add listener for success pairing device
        registerReceiver(bluetoothBondDeviceStateChange,
                new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        // Stop the Bluetooth services
        unregisterReceiver(bluetoothBondDeviceStateChange);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
    }

    /*****************************************************************************************************/
    private void KeyListenerInit() {
        bm = getImageFromAssetsFile(tenantName + ".bmp");
        btnClose.setEnabled(false);
        btn_BMP.setEnabled(false);

        mService = new BluetoothService(this, mHandler);
    }

    protected void disconnect() {
        if (currentPrinterType == PRINTER_TYPE_WOOSIM) {
            try {
                printManagerSato.disconnect();
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
            }
        } else {
            mService.stop();
        }
    }

    @Override
    public void onClick(final View v) {
        int i = v.getId();
        if (i == R.id.button_scan_connect) {
            if (getIntent().hasExtra(BluetoothDevice.EXTRA_DEVICE)) {
                connect(getIntent().getStringExtra(BluetoothDevice.EXTRA_DEVICE));
            } else {
                Intent serverIntent = new Intent(Main_Activity1.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        } else if (i == R.id.btn_close) {
            disconnect();
            btnConnect.setText(getText(R.string.connect));
            btnConnect.setEnabled(true);
            btnClose.setEnabled(false);
            btn_BMP.setEnabled(false);
            btnConnect.setVisibility(View.VISIBLE);
            btnClose.setVisibility(View.GONE);
            btn_BMP.setVisibility(View.GONE);
            tvDeviceName.setText("-");
            tvStatus.setText("Not connected");
        } else if (i == R.id.btn_prtbmp) {
            if (currentPrinterType == PRINTER_TYPE_ZEBRA) {
                try {
                    doConnectionZebra();
                } catch (Exception e) {
                    FireCrash.log(e);
                    Toast.makeText(Main_Activity1.this, "Cannot Print " + e, Toast.LENGTH_SHORT).show();
                }

                if (GlobalData.getSharedGlobalData().getApplication() == null) {
                    NewMainActivity.InitializeGlobalDataIfError(getApplicationContext());
                }
                if (GlobalData.getSharedGlobalData().getApplication().equals("MC") && !isPrintDeposit) {
                    if (taskH != null) {
                        int count = taskH.getPrint_count() == null ? 0 : taskH.getPrint_count();
                        taskH.setPrint_count(count + 1);
                        TaskHDataAccess.addOrReplace(Main_Activity1.this, taskH);
                    }

                    PrintDate taskSubmit = new PrintDate();
                    taskSubmit.setDtm_print(new Date());
                    taskSubmit.setUuid_task_h(uuidTaskH);
                    PrintDateDataAccess.addOrReplace(Main_Activity1.this, taskSubmit);

                    EventBusHelper.post(taskSubmit);
                }
            } else if (currentPrinterType == PRINTER_TYPE_ENIBIT || currentPrinterType == PRINTER_TYPE_WINSON
                    || currentPrinterType == PRINTER_TYPE_EPSON || currentPrinterType == PRINTER_TYPE_JANZ
                    || currentPrinterType == PRINTER_TYPE_BELLA_ZCS_103 || currentPrinterType == PRINTER_TYPE_WOOSIM
                    || currentPrinterType == PRINTER_TYPE_DEFAULT) {

                printTask printTask = new printTask(this);
                printTask.execute();
            }
            else {
                Toast.makeText(this, "Connected printer is not supported", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void finishPrint() {
        receiptBuilder.addBlankSpace(20);
        Bitmap bitmap = receiptBuilder.build(this);

        try {
            print(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printAlignLeft(String text) {
        addText(text, fontSize, Paint.Align.LEFT, false, true);
    }

    private void printAlignCenterLongString(String text, Boolean isBold) throws UnsupportedEncodingException {
        int charCount = 32;
        int i;
        StringBuilder sb = new StringBuilder();
        StringBuilder sbTemp = new StringBuilder();
        String[] words = text.split(" ");

        for (i = 0; i <= words.length - 1; i++) {

            if (charCount - words[i].length() >= 2) {
                charCount = charCount - words[i].length();
                charCount--;
                sbTemp.append(words[i]);
                sbTemp.append(" ");
            } else {
                sbTemp.setLength(Math.max(sbTemp.length() - 1, 0));
                int spaceLength = (32 - sbTemp.length()) / 2;
                while (spaceLength > 0) {
                    sb.append(" ");
                    spaceLength--;
                }
                sb.append(sbTemp.toString());
                addText(sb.toString().trim(), fontSize-1, Paint.Align.CENTER, false, true);
                sb.setLength(0);
                charCount = 32;
                sbTemp.setLength(0);
                sbTemp.append(words[i]);
                sbTemp.append(" ");
                charCount = charCount - words[i].length();
                charCount--;
            }
        }
        sbTemp.setLength(Math.max(sbTemp.length() - 1, 0));
        int spaceLength = (32 - sbTemp.length()) / 2;
        while (spaceLength > 0) {
            sb.append(" ");
            spaceLength--;
        }
        sb.append(sbTemp.toString());
        String print = sb.toString();
        addText(print.trim(), fontSize-1, Paint.Align.CENTER, false, true);
    }

    private void printAlignCenter(String text, boolean isBold) throws UnsupportedEncodingException  {
        int textLength = text.length();
        //1 line = 48 char
        //winson , enibit 32 char
        if (textLength > 32)
            printAlignCenterLongString(text, isBold);
        else {
            addText(text, fontSize-1, Paint.Align.CENTER, isBold, true);
        }
    }

    private void addText(String text, int textSize, Paint.Align alignment, boolean isBold, boolean newLine) {
        if (isBold) receiptBuilder.setTypeface(this, TYPEFACE_MPLUS_BOLD);
        else receiptBuilder.setTypeface(this, TYPEFACE_MPLUS_MEDIUM);
        receiptBuilder.setAlign(alignment)
                .setColor(Color.BLACK)
                .setTextSize(textSize)
                .addText(text);

        if (newLine) receiptBuilder.addBlankSpace(8);
    }

    private void printBitmapSzzcs(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        List<byte[]> arrayList = new ArrayList();
        byte[] obj = new byte[4];
        obj[0] = (byte) 29;
        obj[1] = (byte) 118;
        obj[2] = (byte) 48;
        arrayList.add(obj);
        int i = (width % 8 == 0 ? width / 8 : (width / 8) + 1) % 256;
        int i2 = (width % 8 == 0 ? width / 8 : (width / 8) + 1) / 256;
        int i3 = height % 256;
        int i4 = height / 256;
        arrayList.add(new byte[]{(byte) i, (byte) i2, (byte) i3, (byte) i4});
        int i5 = width % 8;
        StringBuilder stringBuffer = new StringBuilder();
        if (i5 > 0) {
            for (i2 = 0; i2 < 8 - i5; i2++) {
                stringBuffer.append("0");
            }
        }
        StringBuilder stringBuffer2 = new StringBuilder();
        for (i3 = 0; i3 < height; i3++) {
            i = 0;
            stringBuffer2.setLength(0);
            byte[] obj2 = new byte[(i5 == 0 ? width / 8 : (width / 8) + 1)];
            i4 = 0;
            while (i4 < width) {
                i2 = bitmap.getPixel(i4, i3);
                int i6 = (i2 >> 16) & MotionEventCompat.ACTION_MASK;
                int i7 = (i2 >> 8) & MotionEventCompat.ACTION_MASK;
                i2 &= MotionEventCompat.ACTION_MASK;
                if (i6 > 200 || i7 > 200 || i2 > 200) {
                    stringBuffer2.append("0");
                } else {
                    stringBuffer2.append("1");
                }
                if (stringBuffer2.length() == 8) {
                    i2 = i + 1;
                    obj2[i] = (byte) Integer.parseInt(stringBuffer2.toString(), 2);
                    stringBuffer2.setLength(0);
                } else {
                    i2 = i;
                }
                i4++;
                i = i2;
            }
            if (i5 > 0) {
                stringBuffer2.append(stringBuffer);
                obj2[i] = (byte) Integer.parseInt(stringBuffer2.toString(), 2);
            }
            arrayList.add(obj2);
        }

        SendDataByte(printBitmapSzzcs(arrayList));
    }

    /*
     *SendDataByte
     */
    private void SendDataByte(byte[] data) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mService.write(data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: {
                // When DeviceListActivityy returns with a device to connect
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Get the BLuetoothDevice object
                    if (data.hasExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS)) {
                        // Get the device MAC address
                        String address = data.getExtras().getString(
                                DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                        bluetoothAddress = address;
                        connect(address); //Nendi: 2019-01-09
                    } else if (data.hasExtra(DeviceListActivity.EXTRA_ACTION_DELETE)) { //Nendi - 2019-01-08 | Add request unPairing bluetooth device
                        if (data.getExtras().getInt(DeviceListActivity.EXTRA_ACTION_DELETE) == 1) {
                            BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            unpairDevice(device);

                            SharedPreferences printPrefs = getSharedPreferences("printPrefs", Context.MODE_PRIVATE);
                            if (printPrefs.contains("printerName") && (printPrefs.getString("printerName", "").equals(device.getName()) &&
                                    printPrefs.getString("printerAddress", "").equals(device.getAddress()))) {
                                printPrefs.edit().clear().apply();
                            }
                        }
                    } else { //Nendi - 2019-01-08 | Add request pairing bluetooth device
                        BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        pairDevice(device);
                    }
                } else {
                    finish();
                }
                break;
            }
            case REQUEST_ENABLE_BT: {
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
                    KeyListenerInit();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
        }
    }

    //Method for connecting to bluetooth device
    private void connect(String bluetoothAddress) {
        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(bluetoothAddress);

        // Connect to bluetooth printer type woosim || sato
        if (device.getName().toLowerCase().contains("sato") || device.getName().toLowerCase().contains("woosim")) {
            currentPrinterType = PRINTER_TYPE_WOOSIM;

            try {
                printManagerSato.connect(device);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, getResources().getString(R.string.failed_to_connect), Toast.LENGTH_SHORT).show();
            }

            return;
        }

        // Attempt to connect to other device
        if (mService != null){
            mService.connect(device);
        } else {
            //Nendi: 2018-12-28 | Fix error on request bluetooth devices
            Toast.makeText(this, getString(R.string.failed_to_connect_bluetooth), Toast.LENGTH_LONG).show();
            mService = new BluetoothService(this, mHandler);
        }
    }

    //Method for request pairing bluetooth device
    public static void pairDevice(BluetoothDevice device) {
        try {
            Log.d("pairDevice()", "Start Pairing...");
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("pairDevice()", "Pairing finished.");
        } catch (Exception e) {
            Log.e("pairDevice()", e.getMessage());
        }
    }

    public static void unpairDevice(BluetoothDevice device) {
        try {
            Log.d("unpairDevice()", "Start Un-Pairing...");
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("unpairDevice()", "Un-Pairing finished.");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //Nendi: 2019-01-08 | Bond device state change listener
    private BroadcastReceiver bluetoothBondDeviceStateChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    // Attempt to connect to the device
                    connect(mDevice.getAddress());
                }
            }
        }
    };

    /****************************************************************************************************/

    public boolean isValidDate(String value) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            df.setLenient(false);
            df.parse(value.trim());
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private void print_data() throws UnsupportedEncodingException {
        for (PrintResult bean : Constant.getListOfPrintItem()) {

            String type = bean.getPrint_type_id();
            if (Global.PRINT_NEW_LINE.equals(type)) {
                receiptBuilder.addParagraph();
            } else if (Global.PRINT_BRANCH_ADDRESS.equals(type)) {
                printAlignCenter(bean.getLabel(), false);
            } else if (Global.PRINT_BRANCH_NAME.equals(type)) {
                printAlignCenter(bean.getLabel(), false);
            } else if (Global.PRINT_LABEL_CENTER.equals(type)) {
                printAlignCenter(bean.getLabel(), false);
            } else if (Global.PRINT_LABEL.equals(type)) {
                if (bean.getLabel().equals("")) {
                    receiptBuilder.addBlankSpace(6);
                } else {
                    printAlignLeft(bean.getLabel());
                }
            } else if (Global.PRINT_LABEL_BOLD.equals(type)) {
                printAlignLeft(bean.getLabel());
            } else if (Global.PRINT_LABEL_CENTER_BOLD.equals(type)) {
                printAlignCenter(bean.getLabel(), true);
                receiptBuilder.addBlankSpace(2);

                String value = bean.getValue();
                value = (value == null) ? "" : value;

                if ("".equalsIgnoreCase(value)) {
                    printAlignCenter(value, true);
                }
            } else if (Global.PRINT_ANSWER.equals(type)) {
                String label = bean.getLabel();

                String value = bean.getValue();
                value = (value == null) ? "" : value;

                if (!"".equalsIgnoreCase(value)) {
                    if (isValidDate(value)) {
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = null;
                        try {
                            date = Formatter.parseDate(value.trim(), "yyyy/MM/dd");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        value = df.format(date);
                    }

                    if ("".equalsIgnoreCase(label)) {
                        printAlignCenter(value, false);
                    } else {
                        printLeftRight(label, value);
                    }
                }
            } else if (Global.PRINT_ANSWER_NO.equals(type)) {
                String label = bean.getLabel();
                String value = bean.getValue();
                value = (value == null) ? "" : value;

                if ("".equalsIgnoreCase(label)) {
                    printAlignCenter(value, false);
                } else {
                    printLeftRight(label, value);
                }
            } else if (Global.PRINT_USER_NAME.equals(type)) {
                String label = bean.getLabel();
                String value = GlobalData.getSharedGlobalData().getUser().getFullname();
                value = (value == null) ? "" : value;
                printLeftRight(label, value);
            } else if (Global.PRINT_LOGIN_ID.equals(type)) {
                String label = bean.getLabel();

                String value = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                value = (value == null) ? "" : value;
                printLeftRight(label, value);
            } else if (Global.PRINT_BT_ID.equals(type)) {
                String label = bean.getLabel();
                String value = "?";
                try {
                    value = bean.getValue();
                } catch (Exception e) {
                    FireCrash.log(e);
                    // empty
                }
                value = (value == null) ? "" : value;
                printLeftRight(label, value);
            } else if (Global.PRINT_TIMESTAMP.equals(type)) {
                String label = bean.getLabel();

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = new Date();
                String value = df.format(date);

                value = (value == null) ? "" : value;
                printLeftRight(label, value);
            }
            else if(Global.PRINT_UNIQUE_RV.equals(type)){
                String label = bean.getLabel();

                String value = bean.getValue();
                value = (value == null) ? "" : value;
                if ("".equalsIgnoreCase(label)) {
                    printAlignCenter(value, false);
                } else {
                    printAlignCenter(label.replace(":", ""), true);
                    printAlignCenter(value, false);
                }
            }
        }
    }

    private void printLeftRight(String left, String right) {
        receiptBuilder.setTextSize(fontSize);
        receiptBuilder.setTypeface(this, TYPEFACE_MPLUS_MEDIUM);
        receiptBuilder.setAlign(Paint.Align.LEFT);
        receiptBuilder.addText(left, false);
        receiptBuilder.setAlign(Paint.Align.RIGHT);
        receiptBuilder.addText(right);
        receiptBuilder.addBlankSpace(5);
    }

    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }

    public String rataKanan(int digit) {
        String hasil = "";
        if (digit == 4) {
            hasil = "    ";
        } else if (digit == 5) {
            hasil = "   ";
        } else if (digit == 6) {
            hasil = "  ";
        } else if (digit == 7) {
            hasil = " ";
        }
        return hasil;
    }

    private void doConnectionZebra() {
        sendZplOverBluetooth();
    }

    private void sendZplOverBluetooth() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    try {
                        try {
                            if (mService.getState() == BluetoothService.STATE_CONNECTED) {
                                mService.stop();
                            }
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                    if (null == zebraConnection) {
                        zebraConnection = new BluetoothConnection(bluetoothAddress);
                    }

                    if (isPrinterReady(zebraConnection)) {

                        try {
                            zebraPrinter = ZebraPrinterFactory.getInstance(zebraConnection);
                            zebraPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
                        } catch (Exception e) {
                            FireCrash.log(e);

                        }
                        Thread.sleep(500);

                        try {
                            if (!zebraConnection.isConnected()) {
                                zebraConnection.open();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Main_Activity1.this, "Status : Zebra Printer is Open", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            // Make sure got the printer before writing to the connection
                            Thread.sleep(500);

                            getDataToPrintZebra(zebraConnection);

                            // Make sure the data got to the printer before closing the connection
                            Thread.sleep(500);

                            // Close the connection to release resources.
                            zebraConnection.close();

                        } catch (Exception ee) {
                            FireCrash.log(ee);
                        }
                    } else {
                        Connection zebraConnection = new BluetoothConnection(bluetoothAddress);
                        zebraConnection.open();
                        try {
                            zebraPrinter = ZebraPrinterFactory.getInstance(zebraConnection);
                            zebraPrinter.sendCommand("! U1 setvar \"device.languages\" \"line_print\"\r\n");
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                        if (zebraConnection.isConnected()) {
                            getDataToPrintZebra(zebraConnection);
                            Thread.sleep(500);
                            zebraConnection.close();
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    // Handle communications error here.
                    e.printStackTrace();
                } finally {
                    try {
                        zebraConnection.close();
                    } catch (ConnectionException e) {
                        e.printStackTrace();
                    }
                }

            }

        }).start();
    }


    private boolean isPrinterReady(Connection thePrinterConn) {
        boolean isOK = false;
        try {
            thePrinterConn.open();
            // Creates a ZebraPrinter object to use Zebra specific functionality like getCurrentStatus()
            ZebraPrinter printer = ZebraPrinterFactory.getInstance(thePrinterConn);
            PrinterStatus printerStatus = printer.getCurrentStatus();
            if (printerStatus.isReadyToPrint) {
                isOK = true;
            } else if (printerStatus.isPaused) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Main_Activity1.this, "Cannot Print because the printer is paused.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (printerStatus.isHeadOpen) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Main_Activity1.this, "Cannot Print because the printer media door is open.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (printerStatus.isPaperOut) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Main_Activity1.this, "Cannot Print because the paper is out.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Main_Activity1.this, "Cannot Print.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (ZebraPrinterLanguageUnknownException e) {
            e.printStackTrace();
        }
        return isOK;
    }

    void setPrintZebra(String print, Connection zebraConnection) {
        if (zebraConnection.isConnected()) {
            try {

//                "^XA" +/*Start*/
//                        "^FO20,20" + /*Field Origin*/
//                        "^FD" +
//                        print;//+ //+ /*Field Data*/
//                "^FS" + /*Field Separator*/
//                        "^XZ";/*End*/

                String zplData =
                        "^XA^FO20,20^FD" +print+"^FS ^XZ";
//                        "^A0N,25,25" +  size print
                zebraConnection.write(zplData.getBytes());
            } catch (ConnectionException e) {
                e.printStackTrace();
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Main_Activity1.this, "Gagal Mengeprint", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getDataToPrintZebra(Connection zebraConnection) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Toast.makeText(Main_Activity1.this, "Status : Ready To Print", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                }
            });
            for (final PrintResult bean : Constant.getListOfPrintItem()) {
                String type = bean.getPrint_type_id();
                if (Global.PRINT_LABEL_CENTER.equals(type) || Global.PRINT_LABEL_CENTER_BOLD.equals(type)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    sb.append(bean.getLabel());
                    sb.append(blank);
                    sb.append(valueSeparator);
                    sb.append(blank);
                    sb.append(bean.getValue());
                    String print = sb.toString();
                    setPrintZebra(print, zebraConnection);
                } else if (Global.PRINT_LABEL_BOLD.equals(type)) {
                    //tidak menemukan kode untuk bold
                } else if (Global.PRINT_NEW_LINE.equalsIgnoreCase(type)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("\n");
                    String print = sb.toString();
                    setPrintZebra(print, zebraConnection);
                } else if (Global.PRINT_BRANCH_ADDRESS.equals(type)) {
                    StringBuilder sb = new StringBuilder();
                    String label = bean.getLabel();
                    sb.append(label);
                    sb.append(blank);
                    String print = sb.toString();
                    setPrintZebra(print, zebraConnection);
                } else if (Global.PRINT_BRANCH_NAME.equals(type) || Global.PRINT_BT_ID.equals(type) || Global.PRINT_ANSWER_NO.equals(type)) {
                    StringBuilder sb = new StringBuilder();
                    String label = bean.getLabel();
                    sb.append(label);
                    sb.append(blank);
                    sb.append(valueSeparator);
                    sb.append(blank);
                    sb.append(bean.getValue());
                    String print = sb.toString();
                    setPrintZebra(print, zebraConnection);
                } else if (Global.PRINT_USER_NAME.equals(type)) {
                    StringBuilder sb = new StringBuilder();
                    String label = bean.getLabel();
                    int labelLen = label.length();
                    sb.append(label);
                    int spaceLength = separatorStart - labelLen;
                    while (spaceLength > 0) {
                        sb.append(blank);
                        spaceLength--;
                    }
                    sb.append(valueSeparator);
                    sb.append(blank);

                    String value = GlobalData.getSharedGlobalData().getUser().getFullname();
                    value = (value == null) ? "" : value;
                    sb.append(value);
                    String print = sb.toString();
                    setPrintZebra(print, zebraConnection);
                } else if (Global.PRINT_LOGIN_ID.equals(type)) {
                    StringBuilder sb = new StringBuilder();
                    String label = bean.getLabel();
                    int labelLen = label.length();
                    sb.append(label);
                    int spaceLength = separatorStart - labelLen;
                    while (spaceLength > 0) {
                        sb.append(blank);
                        spaceLength--;
                    }
                    sb.append(valueSeparator);
                    sb.append(blank);

                    String value = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                    value = (value == null) ? "" : value;
                    sb.append(value);
                    String print = sb.toString();
                    setPrintZebra(print, zebraConnection);
                } else if (Global.PRINT_ANSWER.equals(type)) {
                    StringBuilder sb = new StringBuilder();
                    String label = bean.getLabel();
                    int labelLen = label.length();
                    if (label == null || label.equals("")) {
                        //question no label = center
                        String value = bean.getValue();
                        int valueLen = value.length();
                        int spaceLength = (30 - valueLen) / 2;
                        while (spaceLength > 0) {
                            sb.append(blank);
                            spaceLength--;
                        }
                        sb.append(value);
                    } else {
                        sb.append(label);
                        int spaceLength = separatorStart - labelLen;
                        while (spaceLength > 0) {
                            sb.append(blank);
                            spaceLength--;
                        }
                        sb.append(valueSeparator);
                        sb.append(blank);

                        String value = bean.getValue();
                        value = (value == null) ? "" : value;
                        sb.append(value);
                    }

                    String print = sb.toString();
                    setPrintZebra(print, zebraConnection);
                }
                else if (Global.PRINT_TIMESTAMP.equals(type)) {
                    StringBuilder sb = new StringBuilder();
                    String label = bean.getLabel();
                    int labelLen = label.length();
                    sb.append(label);
                    int spaceLength = separatorStart - labelLen;
                    while (spaceLength > 0) {
                        sb.append(blank);
                        spaceLength--;
                    }
                    sb.append(valueSeparator);
                    sb.append(blank);

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
                    Date date = new Date();
                    String value = df.format(date);
                    value = (value == null) ? "" : value;
                    sb.append(value);

                    String print = sb.toString();
                    setPrintZebra(print, zebraConnection);
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            FireCrash.log(e);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Main_Activity1.this, "Print Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Tambahan Yessi 2017-04-05 copy from PrintActivity*/
    protected void showErrorDialog(Main_Activity1.ErrorType errorType) {
        if (isPrintDeposit) return;
        if (taskH != null) {
            if (taskH.getPrint_count() != null && taskH.getPrint_count() > 0) return;
        }
        final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(this)
                .isCancelable(true)
                .isCancelableOnTouchOutside(false);

        switch (errorType) {
            case PRINT_FAILED:
                dialog.withTitle(getString(R.string.connect_failed))
                        .withMessage(R.string.reconnect_message)
                        .withButton1Text(getString(R.string.try_again))
                        .setButton1Click(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent serverIntent = new Intent(Main_Activity1.this, DeviceListActivity.class);
                                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                            }
                        })
                        .withButton2Text(getString(R.string.input_rv))
                        .setButton2Click(new OnClickListener() {
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
                        .setButton1Click(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                syncRvNumber();
                            }
                        });
                if (!isRvNumberEmpty) {
                    dialog.withButton2Text(getString(R.string.btnNext))
                            .setButton2Click(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    openInputRVActivity();
                                }
                            });
                } else {
                    dialog.withButton2Text(getString(R.string.btnClose))
                            .setButton2Click(new OnClickListener() {
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

    protected void connect() {
        try {
            printManager.connect();
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                dialog = ProgressDialog.show(Main_Activity1.this, "Sync RV Number", getString(R.string.please_wait), true, false);
            }

            @Override
            public void onError(SyncRVResponse response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (response.getErrorMessage() != null) {
                    showErrorDialog(Main_Activity1.ErrorType.SYNC_RV_FAILED);
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
                        ReceiptVoucherDataAccess.addNewReceiptVoucher(Main_Activity1.this,
                                GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                                rvNumbers);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                        ACRA.getErrorReporter().putCustomData("errorRV", e.getMessage());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Error: Insert RV Error. " + e.getMessage()));
                        Toast.makeText(Main_Activity1.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                openInputRVActivity();
            }
        });
        task.execute();
    }

    private void openInputRVActivity() {
        Intent intent = new Intent(Main_Activity1.this, InputRVNumberActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(UUID_TASKH, uuidTaskH);
        intent.putExtra(SOURCE_TASK, source);
        startActivityForResult(intent, FROM_ACTIVITY_RV_NUMBER);
    }

    @Override
    public void onConnected(String deviceName) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(400);

        btnConnect.setEnabled(false);
        btnClose.setEnabled(true);
        btn_BMP.setEnabled(true);
        btnConnect.setVisibility(View.GONE);
        btnClose.setVisibility(View.VISIBLE);
        btn_BMP.setVisibility(View.VISIBLE);

        tvDeviceName.setText(deviceName);
        updateUI(true);
        Toast.makeText(this, getString(R.string.connected), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectFailed() {
        onDisconnect();
        showErrorDialog(Main_Activity1.ErrorType.PRINT_FAILED);
    }

    @Override
    public void onConnecting() {
        query.id(R.id.button_scan).enabled(false).text(R.string.connecting);
    }

    @Override
    public void onDisconnect() {
        query.id(R.id.tv_device_name).text("-");
        query.id(R.id.button_scan).enabled(true);
        updateUI(false);
    }

    protected void updateUI(boolean isConnected) {
        if (btnConnect != null) {
            if (isConnected) {
                tvStatus.setText(getString(R.string.connected));
                btnConnect.setText(getString(R.string.mnDisconnect));
                btn_BMP.setVisibility(View.VISIBLE);
            } else {
                tvStatus.setText(getString(R.string.not_connected));
                btnConnect.setText(getString(R.string.mnConnect));
                btn_BMP.setVisibility(View.INVISIBLE);
            }
        }
    }

    public enum ErrorType {
        PRINT_FAILED,
        SYNC_RV_FAILED
    }

    private static byte[] printBitmapSzzcs(List<byte[]> list) {
        int i = 0;
        for (byte[] length : list) {
            i = length.length + i;
        }
        byte[] obj = new byte[i];
        i = 0;
        for (byte[] length2 : list) {
            System.arraycopy(length2, 0, obj, i, length2.length);
            i = length2.length + i;
        }
        String str = "BTPrinter";
        StringBuilder stringBuilder = new StringBuilder("");
        for (byte b : obj) {
            String toHexString = Integer.toHexString(b & MotionEventCompat.ACTION_MASK);
            if (toHexString.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(toHexString);
        }
        Log.e(str, stringBuilder.toString());
        return obj;
    }

    protected void print(Bitmap bitmap) throws Exception {
        SendDataByte(Command.ESC_Init);
        printBitmapSzzcs(bitmap);
        SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
        SendDataByte(PrinterCommand.POS_Set_Cut(1));
        SendDataByte(PrinterCommand.POS_Set_PrtInit());

        if (bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private void initPrint() {
        SendDataByte(Command.ESC_Init);
        SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
        SendDataByte(PrinterCommand.POS_Set_Cut(1));
        SendDataByte(PrinterCommand.POS_Set_PrtInit());
    }

    private class printTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog dialog;
        private Context context;

        public printTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean isSuccess = true;

            try {
                if (currentPrinterType == PRINTER_TYPE_WOOSIM) {
                    if (getIntent().hasExtra("PRINT_TEST")){
                        generatePrintTest();
                        printManagerSato.print(receiptBuilder.build(this.context));
                        return false;
                    }

                    GeneralParameter gs = GeneralParameterDataAccess.getOne(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_MAX_PRINT_COUNT_COPY);
                    int count = Integer.valueOf(gs.getGs_value());
                    //tika 2019-12-12, penjagaan !is_print_deposit
                    if (!isPrintDeposit){
                        if (taskH.getPrint_count() == 0) {
                            //Enhance dari last commit Ardhanz
                            printManagerSato.print(taskH.getPrint_count() , count);
                        } else if (taskH.getPrint_count() > 0 && taskH.getPrint_count() <= count) {
                            printManagerSato.print(taskH.getPrint_count() , count);
                        } else if (taskH.getPrint_count() > count) {
                            return false;
                        }
                    } else {
                        printManagerSato.print();}
                } else {
                    //check if has receipt file
                    File receipt;
                    if (getIntent().hasExtra("PRINT_TEST")) {
                        receipt = new File(receiptDir, "printTest.png");
                    } else {
                        receipt = new File(receiptDir, isPrintDeposit ?
                                getIntent().getStringExtra("taskId").concat(".png") : taskH.getAppl_no().concat(".png"));
                    }

                    if (receipt.exists()) {
                        FileInputStream inputStream = new FileInputStream(receipt);
                        try {
                            BitmapFactory.Options opt = new BitmapFactory.Options();
                            opt.inPreferredConfig = Bitmap.Config.RGB_565;
                            opt.inPurgeable = true;
                            opt.inInputShareable = true;
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, opt);
                            print(bitmap);
                        } catch(Exception excepetion) {
                            isSuccess = false;
                            excepetion.printStackTrace();
                        } finally {
                            inputStream.close();
                        }
                    } else { //Initial Print Receipt
                        if (getIntent().hasExtra("PRINT_TEST")) {
                            generatePrintTest();
                        } else {
                            BitmapFactory.Options opt = new BitmapFactory.Options();
                            opt.inPreferredConfig = Bitmap.Config.RGB_565;
                            opt.inPurgeable = true;
                            opt.inInputShareable = true;
                            Bitmap bitmap = BitmapFactory.decodeStream(getResources().openRawResource(R.raw.adins_logo), null, opt);

                            receiptBuilder = new ReceiptBuilder(384);
                            receiptBuilder.setMargin(10, 0).setTextSize(23);
                            receiptBuilder.setAlign(Paint.Align.CENTER).addImage(bitmap).addParagraph().addBlankSpace(20);
                            /* End Initial Print Receipt */
                            print_data();
                        }

                        //GET MAX PRINT COUNT FROM GS
                        GeneralParameter gs = GeneralParameterDataAccess.getOne(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_MAX_PRINT_COUNT_COPY);
                        int count = Integer.valueOf(gs.getGs_value());
                        //tika 2019-12-12, penjagaan !is_print_deposit
                        if (!isPrintDeposit){
                            if (taskH.getPrint_count() == 0) {
                                //Enhance dari last commit Ardhanz
                                initPrint();
                                finishPrint();
                            } else if (taskH.getPrint_count() > 0 && taskH.getPrint_count() <= count) {
                                //Add this if print copy
                                receiptBuilder.addLine();
                                receiptBuilder.addBlankSpace(5);
                                receiptBuilder.setAlign(Paint.Align.CENTER);
                                receiptBuilder.addText("COPY" + " " + taskH.getPrint_count() + " OF " + gs.getGs_value());
                                //Enhance dari last commit Ardhanz
                                initPrint();
                                finishPrint();
                            } else if (taskH.getPrint_count() > count) {
                                return false;
                            }
                        } else {
                            initPrint();
                            finishPrint();
                        }
                        //COMMENT AMPE SINI

                    }
                }

                /*Tambahan yessi 2017-04-05*/
                if (GlobalData.getSharedGlobalData().getApplication() == null) {
                    MainMenuActivity.InitializeGlobalDataIfError(getApplicationContext());
                }
                /*end*/
            } catch (Exception e) {
                //PENJAAGAAN PRINT
                if (getIntent().hasExtra("PRINT_TEST")) {
                    //Enhance dari last commit Ardhanz
                    initPrint();
                    finishPrint();
                } else {
                    isSuccess = false;
                    FireCrash.log(e);
                    e.printStackTrace();
                }
                //COMMENT AMPE SINI

            }

            return isSuccess;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.setMessage(context.getString(R.string.please_wait));
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();

            // UNTUK LIMIT PRINT COUNT
            try {
            if (Boolean.FALSE.equals(result)){
              if (taskH.getPrint_count() > 1){
               showMessagePrint();
              return;
              }
             return;
            }
            //COMMENT AMPE SINI

            if (GlobalData.getSharedGlobalData().getApplication().equals("MC") && !isPrintDeposit) {
                if (taskH != null) {
                    int count = taskH.getPrint_count() == null ? 0 : taskH.getPrint_count();
                    taskH.setPrint_count(count + 1);
                    TaskHDataAccess.addOrReplace(Main_Activity1.this, taskH);

                    //update print count in save task failed
                    TaskManager.getTaskHAndTaskD(Main_Activity1.this);
                    //comment sampai sini
                }

                PrintDate taskSubmit = new PrintDate();
                taskSubmit.setDtm_print(new Date());
                taskSubmit.setUuid_task_h(uuidTaskH);
                PrintDateDataAccess.addOrReplace(Main_Activity1.this, taskSubmit);

                EventBusHelper.post(taskSubmit);
            }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void generatePrintTest() {
        User user = GlobalData.getSharedGlobalData().getUser();

        receiptBuilder = new ReceiptBuilder(384);
        receiptBuilder.setMargin(10, 0).setTextSize(23);
        receiptBuilder.setAlign(Paint.Align.CENTER);
        receiptBuilder.addText("        PRINT TEST        ");
        receiptBuilder.addLine();
        receiptBuilder.addBlankSpace(5);
        receiptBuilder.setAlign(Paint.Align.LEFT).addText("Timestamp", false);
        receiptBuilder.setAlign(Paint.Align.RIGHT).addText(Formatter.formatDate(new Date(), Global.DATE_TIME_STR_FORMAT));
        receiptBuilder.setAlign(Paint.Align.LEFT).addText("Login ID", false);
        receiptBuilder.setAlign(Paint.Align.RIGHT).addText(user.getLogin_id());
        receiptBuilder.setAlign(Paint.Align.LEFT).addText("Fullname", false);
        receiptBuilder.setAlign(Paint.Align.RIGHT).addText(user.getFullname());
        receiptBuilder.addLine();
        receiptBuilder.addBlankSpace(5);
        receiptBuilder.setAlign(Paint.Align.CENTER);
        receiptBuilder.addText("        PRINT TEST        ");
        receiptBuilder.addLine();
        receiptBuilder.addBlankSpace(25);
    }

    //UNTUK LIMIT PRINT COUNT
    private void showMessagePrint(){
        try {
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
            dialogBuilder.withTitle(getString(R.string.info_capital)).withMessage(getString(R.string.print_message))
                    .withButton1Text(getString(R.string.btnOk))
                    .setButton1Click(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            dialogBuilder.dismiss();
                        }
                    }).show();
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }
    // COMMENT SAMPAI SINI
}