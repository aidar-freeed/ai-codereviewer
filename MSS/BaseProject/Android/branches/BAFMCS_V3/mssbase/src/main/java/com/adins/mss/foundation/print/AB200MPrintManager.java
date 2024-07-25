package com.adins.mss.foundation.print;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import android.util.Log;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LogoPrint;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.foundation.db.dataaccess.LogoPrintDataAccess;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.adins.mss.logger.Logger;
import com.woosim.printer.WoosimCmd;
import com.woosim.printer.WoosimImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import zj.com.command.sdk.PrinterCommand;
import zj.com.customize.sdk.Other;

/**
 * Created by angga.permadi on 3/3/2016.
 */
public class AB200MPrintManager extends AbstractPrintManager {
    public static final String PRINT_PREFERENCES = "PRINT_PREFERENCES";
    public static final String PRINTER_KEY = "PRINTER_KEY";
    public static final String PRINTER_WOOSIM = "woosim";
    public static final int TEXT_ATTRIBUTE_EMPHASIZED = 1;
    public static final String TAG = "AB200MPrintManager";

    private String deviceName;
    private PrinterConnect printerConnect;
    private String lastPrinterConnected;

    public AB200MPrintManager(Context context, List<PrintResult> list) {
        super(context, list);
        ObscuredSharedPreferences preferences = ObscuredSharedPreferences.getPrefs(context, PRINT_PREFERENCES, Context.MODE_PRIVATE);
        lastPrinterConnected = preferences.getString(PRINTER_KEY, "");
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isPrinterConnected() {
        return BluePrintDriver.isPrinterConnected();
    }

    @Override
    public void connect() throws Exception {
        if (printerConnect != null) {
            printerConnect.cancel(true);
            printerConnect = null;
        }

        printerConnect = new PrinterConnect();
        printerConnect.execute();
    }

    @Override
    public void connect(BluetoothDevice device) throws Exception {
        if (printerConnect != null) {
            printerConnect.cancel(true);
            printerConnect = null;
        }

        printerConnect = new PrinterConnect(device);
        printerConnect.execute();
    }

    @Override
    public void disconnect() throws Exception {
        BluePrintDriver.close();
        deviceName = null;
        connected = false;

        if (listener != null) {
            listener.onDisconnect();
        }
    }

    @Override
    public void printText(String text, int alignment, int attribute, int size, boolean s) {
        BluePrintDriver.InitPrinter();

        if (BluePrintDriver.IsNoConnection()) {
            return;
        }

        switch (attribute) {
            case AbstractPrintManager.TEXT_ATTRIBUTE_FONT_A:
                break;
            case AbstractPrintManager.TEXT_ATTRIBUTE_EMPHASIZED:
                BluePrintDriver.AddBold((byte) TEXT_ATTRIBUTE_EMPHASIZED);
                break;
            default:
                break;
        }

        BluePrintDriver.AddAlignMode((byte) alignment);
        BluePrintDriver.ImportData(text);
        if (!BluePrintDriver.excute()) {
            connected = false;
            Logger.d("", "onError print ");
        }
        BluePrintDriver.ClearData();
    }

    @Override
    public void printBitmap(Bitmap bitmap, int alignment, int span, int level, boolean s) {
        if (BluePrintDriver.IsNoConnection()) {
            return;
        }

        //olivia : utk dapat nama logo sesuai nama tenant
        String tenant = GlobalData.getSharedGlobalData().getTenant();
        int pos = tenant.indexOf("@");
        String tenantName = tenant.substring(pos+1, tenant.length());

        if (deviceName != null && deviceName.toLowerCase().contains(PRINTER_WOOSIM)) {
            Bitmap logo = null;
            LogoPrint logoPrint = LogoPrintDataAccess.getOne(context, tenantName);
            if (logoPrint != null) {
                logo = BitmapFactory.decodeByteArray(logoPrint.getImage_bitmap(), 0, logoPrint.getImage_bitmap().length);
            } else {
                BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.adins_logo);
                if (drawable != null) logo = drawable.getBitmap();
            }
            int nMode = 0;
            int nPaperWidth = 320;
            if (logo != null) {
                int width = ((nPaperWidth + 7) / 8) * 8;
                int height = logo.getHeight() * width / logo.getWidth();
                height = ((height + 7) / 8) * 8;

                Bitmap rszBitmap = logo;
                if (logo.getWidth() != width) {
                    rszBitmap = Other.resizeImage(logo, width, height);
                }

                byte[] data = WoosimImage.printBitmap(0, 0, 256, 200, rszBitmap);
                BluePrintDriver.printByteData(zj.com.command.sdk.Command.ESC_Init);
                BluePrintDriver.printByteData(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_CENTER));
                BluePrintDriver.printByteData(data);
                BluePrintDriver.printByteData(PrinterCommand.POS_Set_PrtAndFeedPaper(20));
            }
        } else {
            LogoPrint logoPrint = LogoPrintDataAccess.getOne(context, tenantName);
            if (logoPrint != null) {
                bitmap = BitmapFactory.decodeByteArray(logoPrint.getImage_bitmap(), 0, logoPrint.getImage_bitmap().length);
            } else {
                BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.adins_logo);
                if (drawable != null) bitmap = drawable.getBitmap();
            }

            if (bitmap != null) {

                /**
                 * 2019.06.20
                 * Bugfix: PRDAITMSS-722
                 * Print Logo Bitmap not properly printed
                 */
                BluePrintDriver.printByteData(zj.com.command.sdk.Command.ESC_Init);
                BluePrintDriver.printByteData(zj.com.command.sdk.Command.LF);
                BluePrintDriver.printByteData(WoosimCmd.setTextAlign(1));
                printBitmapSzzcs(bitmap);
                BluePrintDriver.printByteData(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
            }
        }
    }

    /**
     * 2019.06.20
     * Print using Bitmap data
     */
    @Override
    public void print(Bitmap bitmap) {
        byte[] data = WoosimImage.bmp2PrintableImage(0, 0, bitmap.getWidth(), bitmap.getHeight(), bitmap);
        BluePrintDriver.printByteData(WoosimCmd.initPrinter());
        BluePrintDriver.printByteData(WoosimCmd.printLineFeed(3));
        BluePrintDriver.printByteData(WoosimCmd.setTextAlign(1));
        BluePrintDriver.printByteData(data);
        BluePrintDriver.printByteData(WoosimCmd.cutPaper(1));
    }

    @Override
    public void lineFeed(int lines, boolean s) {
        for (int i = 0; i < lines; i++) {
            BluePrintDriver.printString("");
        }
    }

    @Override
    public void releaseResources() {
        super.releaseResources();
        deviceName = null;

        if (printerConnect != null) {
            printerConnect.cancel(true);
            printerConnect = null;
        }
    }

    /*PRINT SATO*/
    @Override
    public boolean printSato() throws Exception {
        if (connected) {
            byte[] logo = getByteFromImage("adira_new.prn");
            BluePrintDriver.ImportData(this.getDataToPrint(), logo);
            if (!BluePrintDriver.excute()) {
                connected = false;
                Logger.d("", "onError print ");
            }
            BluePrintDriver.ClearData();
        } else {
            throw new Exception("Device is not connected to the Sato printer");
        }
        return true;
    }

    private byte[] getByteFromImage(String image) throws IOException {

        byte[] imgBytes = null;
        ByteArrayOutputStream baos = null;
        AssetManager am = context.getResources().getAssets();
        InputStream in = null;
        try {
            baos = new ByteArrayOutputStream();
            // this image is inside my mobile application
            in = am.open(image); // <<--- image = nama file .prn yang taruh di asset
            byte[] buffer = new byte[4096];
            int n = 0;
            while ((n = in.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }

            imgBytes = baos.toByteArray();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // whatever happends close the streams
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception ex) {
                    FireCrash.log(ex);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                    FireCrash.log(ex);
                }
            }
        }
        byte[] a = new byte[]{2, 27, 65, 27, 73, 71, 49, 27, 80, 83};
        byte[] b = new byte[]{27, 81, 48, 48, 48, 49, 27, 90, 3};

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(a);
        outputStream.write(imgBytes);
        outputStream.write(b);

        byte[] c = outputStream.toByteArray();
        return c;
    }

    private String getDataToPrint() throws Exception {
        int verticalPosition = 96 - 33; // 96, sebagai posisi awal
        char start = (char) 27;
        char begin = (char) 2;
        char end = (char) 3;

        StringBuilder sbToPrint = new StringBuilder();
        sbToPrint.append(begin).append(start);
        for (PrintResult bean : Constant.getListOfPrintItem()) {
            String type = bean.getPrint_type_id();
            String vPosition = "";

            if ((verticalPosition + 33) < 100) {
                verticalPosition = verticalPosition + 33;
                vPosition = "00" + verticalPosition;
            } else if ((verticalPosition + 33) < 1000) {
                verticalPosition = verticalPosition + 33;
                vPosition = "0" + verticalPosition;
            } else {
                vPosition = "" + verticalPosition;
                verticalPosition = verticalPosition + 33;
            }
            if (Global.PRINT_NEW_LINE.equals(type)) {
                String label = bean.getLabel();
                sbToPrint.append(start).append(
                        "L" + vPosition + "000101111000");
                sbToPrint.append(start).append("D" + label);
            } else if (Global.PRINT_LABEL_CENTER.equals(type)) {
                String label = bean.getLabel();
                // nilai sebenarnya 384, dikurangi aj bt jaga2 kalo slah perhtungan
                Vector vt = FontSato.wrap(374, label.trim());

                for (int j = 0; j < vt.size(); j++) {
                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG,"eato " + e);
                    }

                    // ------------- agar posisi bisa ditengah,
                    if(setn == null)
                        continue;

                    int iHposition = (384 - setn.getLenghtSentemce()) / 2;
                    String sHposition = "0";

                    if (iHposition < 10) {
                        sHposition = "000" + iHposition;
                    } else if (iHposition < 100) {
                        sHposition = "00" + iHposition;
                    } else if (iHposition < 1000) {
                        sHposition = "0" + iHposition;
                    }
                    // -------------

                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + sHposition
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());

                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + sHposition
                                + "01111000");
                        verticalPosition = verticalPosition + 33;

                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());

                    }

                }
            } else if (Global.PRINT_LABEL_CENTER_BOLD.equals(type)) {
                String label = bean.getLabel();
                // nilai sebenarnya 384, dikurangi aj bt jaga2 kalo slah perhtungan
                Vector vt = FontSato.wrap(374, label.trim());

                for (int j = 0; j < vt.size(); j++) {
                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG,"eato " + e);
                    }

                    // ------------- agar posisi bisa ditengah,
                    int iHposition = (384 - setn.getLenghtSentemce()) / 2;
                    String sHposition = "0";

                    if (iHposition < 10) {
                        sHposition = "000" + iHposition;
                    } else if (iHposition < 100) {
                        sHposition = "00" + iHposition;
                    } else if (iHposition < 1000) {
                        sHposition = "0" + iHposition;
                    }
                    // -------------

                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + sHposition
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());

                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + sHposition
                                + "01111000");

                        verticalPosition = verticalPosition + 33;

                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());

                    }

                }
            } else if (Global.PRINT_LABEL.equals(type) || Global.PRINT_LABEL_BOLD.equals(type)) {
                String label = bean.getLabel();

                // nilai sebenarnya 384, dikurangi aj bt jaga2 kalo slah perhtungan
                Vector vt = FontSato.wrap(374, label.trim());

                for (int j = 0; j < vt.size(); j++) {
                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG,"sato " + e);
                    }

                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "000101111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());

                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + "000101111000");

                        verticalPosition = verticalPosition + 33;

                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    }
                }
            } else if (Global.PRINT_ANSWER.equals(type)) {
                String label = bean.getLabel();
                String answer = bean.getValue();
                if (answer != null && !answer.equals("")) {
                    if (label.equals("")
                            || label == null
                            || label.equals("null")) { // jika pertanyaan tidak ada labelnya maka posisi ditengahkan
                        // nilai sebenarnya 384, dikurangi aj bt jaga2 kalo slah perhtungan
                        Vector vt = FontSato.wrap(374, answer.trim());

                        for (int j = 0; j < vt.size(); j++) {

                            SentencesSato setn = null;

                            try {
                                setn = (SentencesSato) vt.elementAt(j);
                            } catch (Exception e) {
                                FireCrash.log(e);
                                if (Global.IS_DEV) Log.i(TAG, "sato " + e);
                            }

                            // ------------- agar posisi bisa ditengah,
                            int iHposition = (384 - setn.getLenghtSentemce()) / 2;
                            String sHposition = "0";

                            if (iHposition < 10) {
                                sHposition = "000" + iHposition;
                            } else if (iHposition < 100) {
                                sHposition = "00" + iHposition;
                            } else if (iHposition < 1000) {
                                sHposition = "0" + iHposition;
                            }
                            // -------------

                            if (j == 0) {
                                sbToPrint.append(start).append("L"
                                        + vPosition
                                        + sHposition
                                        + "01111000");
                                sbToPrint.append(start).append("D"
                                        + setn.getSentence().trim());
                            } else {
                                sbToPrint.append(start).append("L"
                                        + vPrintPosition(verticalPosition)
                                        + sHposition
                                        + "01111000");
                                sbToPrint.append(start).append("D"
                                        + setn.getSentence().trim());
                                verticalPosition = verticalPosition + 33;
                            }
                        }

                    } else {

                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "000101111000");
                        sbToPrint.append(start).append("D" + label);

                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "020001111000");
                        sbToPrint.append(start).append("D:");

                        Vector vt = FontSato.wrap(166, answer.trim()); // panjang area yang akan diprint

                        for (int j = 0; j < vt.size(); j++) {

                            SentencesSato setn = null;

                            try {
                                setn = (SentencesSato) vt.elementAt(j);
                            } catch (Exception e) {
                                FireCrash.log(e);
                                if (Global.IS_DEV) Log.i(TAG, "sato " + e);
                            }
                            if (j == 0) {
                                sbToPrint.append(start).append("L"
                                        + vPosition
                                        + "0208"
                                        + "01111000");
                                sbToPrint.append(start).append("D"
                                        + setn.getSentence().trim());
                            } else {
                                sbToPrint.append(start).append("L"
                                        + vPrintPosition(verticalPosition)
                                        + "0208"
                                        + "01111000");
                                verticalPosition = verticalPosition + 33;
                                sbToPrint.append(start).append("D"
                                        + setn.getSentence().trim());
                            }

                        }
                    }
                } else {
                    verticalPosition = verticalPosition - 33;
                }
            } else if (Global.PRINT_USER_NAME.equals(type)) {
                String label = bean.getLabel();
                String answer = GlobalData.getSharedGlobalData().getUser().getFullname();

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "000101111000");
                sbToPrint.append(start).append("D" + label);

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "020001111000");
                sbToPrint.append(start).append("D:");

                Vector vt = FontSato.wrap(166, answer.trim()); // panjang area yang akan diprint

                for (int j = 0; j < vt.size(); j++) {

                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG, "sato " + e);
                    }
                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "0208"
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + "0208"
                                + "01111000");
                        verticalPosition = verticalPosition + 33;
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    }

                }
            } else if (Global.PRINT_LOGIN_ID.equals(type)) {
                String label = bean.getLabel();
                String answer = GlobalData.getSharedGlobalData().getUser().getLogin_id();

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "000101111000");
                sbToPrint.append(start).append("D" + label);

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "020001111000");
                sbToPrint.append(start).append("D:");

                Vector vt = FontSato.wrap(166, answer.trim()); // panjang area yang akan diprint

                for (int j = 0; j < vt.size(); j++) {

                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG, "sato " + e);
                    }
                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "0208"
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + "0208"
                                + "01111000");
                        verticalPosition = verticalPosition + 33;
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    }

                }
            } else if (Global.PRINT_BRANCH_NAME.equals(type) || Global.PRINT_BRANCH_ADDRESS.equals(type)) {
                String label = bean.getLabel();
                // nilai sebenarnya 384, dikurangi aj bt jaga2 kalo slah perhtungan
                Vector vt = FontSato.wrap(374, label.trim());

                for (int j = 0; j < vt.size(); j++) {
                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG,"eato " + e);
                    }

                    // ------------- agar posisi bisa ditengah,
                    int iHposition = (384 - setn.getLenghtSentemce()) / 2;
                    String sHposition = "0";

                    if (iHposition < 10) {
                        sHposition = "000" + iHposition;
                    } else if (iHposition < 100) {
                        sHposition = "00" + iHposition;
                    } else if (iHposition < 1000) {
                        sHposition = "0" + iHposition;
                    }
                    // -------------

                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + sHposition
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());

                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + sHposition
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());

                        verticalPosition = verticalPosition + 33;
                    }
                }
            } else if (Global.PRINT_BT_ID.equals(type)) {
                String label = bean.getLabel();
                String answer = bean.getValue();

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "000101111000");
                sbToPrint.append(start).append("D" + label);

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "020001111000");
                sbToPrint.append(start).append("D:");

                Vector vt;

                vt = FontSato.wrap(166, answer.trim()); // panjang area yang akan diprint

                for (int j = 0; j < vt.size(); j++) {

                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG, "sato " + e);
                    }
                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "0208"
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + "0208"
                                + "01111000");
                        verticalPosition = verticalPosition + 33;
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    }

                }
            } else if (Global.PRINT_TIMESTAMP.equals(type)) {
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
                Date date = new Date();
                String answer = df.format(date);
                String label = bean.getLabel();

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "000101111000");
                sbToPrint.append(start).append("D" + label);

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "020001111000");
                sbToPrint.append(start).append("D:");

                Vector vt;

                vt = FontSato.wrap(166, answer.trim()); // panjang area yang akan diprint

                for (int j = 0; j < vt.size(); j++) {

                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG, "sato " + e);
                    }
                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "0208"
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + "0208"
                                + "01111000");
                        verticalPosition = verticalPosition + 33;
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    }

                }
            }
        }
        // ENDING LINE
        verticalPosition = verticalPosition + 33;
        sbToPrint.append(start).append(
                "L" + vPrintPosition(verticalPosition) + "000101222000");
        verticalPosition = verticalPosition + 33;
        sbToPrint.append(start).append("D----------------");

        sbToPrint.append(start).append("Q0001").append(start).append("Z");
        sbToPrint.append(end);

        return sbToPrint.toString();
    }

    public String vPrintPosition(int verticalPosition) {
        String vPosition = "";
        if ((verticalPosition + 33) < 100) {
            verticalPosition = verticalPosition + 33;
            vPosition = "00" + verticalPosition;
        } else if ((verticalPosition + 33) < 1000) {
            verticalPosition = verticalPosition + 33;
            vPosition = "0" + verticalPosition;
        } else {
            vPosition = "" + verticalPosition;
            verticalPosition = verticalPosition + 33;
        }

        return vPosition;
    }

    public class PrinterConnect extends AsyncTask<Void, Void, Boolean> {
        String selectedPrinter;
        BluetoothDevice inDevice;

        public PrinterConnect() {
        }

        public PrinterConnect(BluetoothDevice inDevice) {
            this.inDevice = inDevice;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (listener != null) {
                listener.onConnecting();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (inDevice == null) {
                if (pairedDevices != null && !pairedDevices.isEmpty()) {
                    List<BluetoothDevice> pairedPrinters = new ArrayList<>();
                    if (lastPrinterConnected != null && !lastPrinterConnected.isEmpty()) {
                        for (BluetoothDevice device : pairedDevices) {
                            if (device.getAddress().equals(lastPrinterConnected))
                                pairedPrinters.add(0, device);
                            else
                                pairedPrinters.add(device);
                        }
                    } else {
                        pairedPrinters.addAll(pairedDevices);
                    }
                    for (BluetoothDevice device : pairedPrinters) {
                        if (device != null) {
                            Logger.d("Device Address : ", device.getAddress() + ", Device Name : " + device.getName());

                            BluePrintDriver.close();
                            if (!BluePrintDriver.OpenPrinter(device.getAddress())) {
                                BluePrintDriver.close();
                            } else {
                                deviceName = device.getName() == null ? device.getAddress() : device.getName();
                                selectedPrinter = device.getAddress();
                                return true;
                            }
                        }
                    }
                }
                return false;
            } else {
                Logger.d("Device Address : ", inDevice.getAddress() + ", Device Name : " + inDevice.getName());

                BluePrintDriver.close();
                if (!BluePrintDriver.OpenPrinter(inDevice.getAddress())) {
                    BluePrintDriver.close();
                } else {
                    deviceName = inDevice.getName() == null ? inDevice.getAddress() : inDevice.getName();
                    selectedPrinter = inDevice.getAddress();
                    return true;
                }
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (Boolean.TRUE.equals(aBoolean)) {
                connected = true;
                if (listener != null) {
                    listener.onConnected(deviceName);
                }
                if (selectedPrinter != null && !selectedPrinter.isEmpty()) {
                    ObscuredSharedPreferences preferences = ObscuredSharedPreferences.getPrefs(context, PRINT_PREFERENCES, Context.MODE_PRIVATE);
                    ObscuredSharedPreferences.Editor sharedPrefEditor = preferences.edit();
                    sharedPrefEditor.putString(PRINTER_KEY, selectedPrinter);
                    sharedPrefEditor.commit();
                    lastPrinterConnected = selectedPrinter;
                }
            } else {
                Toast.makeText(context, context.getString(R.string.no_paired_device), Toast.LENGTH_SHORT).show();
                connected = false;
                if (listener != null) {
                    listener.onConnectFailed();
                }
            }
        }
    }

    // Nendi: 2019.06.20
    // Resize bitmap for printing
    private void printBitmapSzzcs(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        ArrayList arrayList = new ArrayList();
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

        BluePrintDriver.printByteData(printBitmapSzzcs(arrayList));
    }

    // Nendi: 2019.06.20
    // Get bytes for image printing
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
}

