package com.adins.mss.foundation.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.core.content.ContextCompat;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.foundation.formatter.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by angga.permadi on 3/3/2016.
 */
public abstract class AbstractPrintManager {
    protected static final int ALIGNMENT_LEFT = 0;
    protected static final int ALIGNMENT_CENTER = 1;
    protected static final int TEXT_ATTRIBUTE_FONT_A = 0;
    protected static final int TEXT_ATTRIBUTE_EMPHASIZED = 16;
    protected static final int TEXT_SIZE_HORIZONTAL1 = 0;
    protected static final int BITMAP_WIDTH_FULL = -1;

    protected boolean connected = false;
    protected String valueSeparator = ":";
    protected char blank = ' ';
    protected int labelStart = 0;
    protected int separatorStart = 0;
    protected int valueStart = 0;
    protected List<PrintResult> list;
    protected PrintManagerListener listener;
    protected Context context;
    protected BluetoothAdapter mBtAdapter;
    protected Set<BluetoothDevice> pairedDevices;
    private Bitmap logo = null;

    public AbstractPrintManager(Context context, List<PrintResult> list) {
        if (list == null)
            throw new IllegalArgumentException("Print list can not be null!");

        try {
            listener = (PrintManagerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " " +
                    "must implement PrintManagerListener");
        }

        BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.print_logo);

        if (drawable != null) this.logo = drawable.getBitmap();

        this.list = list;
        this.context = context;

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBtAdapter.getBondedDevices();

        int longestLabel = 1;
        for (PrintResult bean : list) {
            if (Global.PRINT_ANSWER.equals(bean.getPrint_type_id())) {
                String label = bean.getLabel();
                if (label != null) {
                    int len = label.length();
                    longestLabel = (len > longestLabel) ? len : longestLabel;
                }
            }
        }
        this.separatorStart = longestLabel + 1;
        this.valueStart = longestLabel + 3;
    }

    public boolean print(Integer printCountData , Integer limitPrintData) throws Exception {
        boolean isNeedFeedLine = true;
        if (connected) {
            for (PrintResult bean : list) {
                String type = bean.getPrint_type_id();
                if (Global.PRINT_NEW_LINE.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_BRANCH_ADDRESS.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_BRANCH_NAME.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_LABEL_CENTER.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_LABEL.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_LABEL_BOLD.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_EMPHASIZED, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_LABEL_CENTER_BOLD.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_CENTER, TEXT_ATTRIBUTE_EMPHASIZED, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_LABEL_COPY.equals(type)) {
                    if (printCountData > 0 && printCountData <= limitPrintData) {
                        printText("COPY" + " " + printCountData + " OF " + limitPrintData, ALIGNMENT_CENTER, TEXT_ATTRIBUTE_EMPHASIZED, TEXT_SIZE_HORIZONTAL1, false);
                    }
                } else if (Global.PRINT_LOGO.equals(type)) {
                    printBitmap(logo, ALIGNMENT_CENTER, BITMAP_WIDTH_FULL, 20, false);
                } else if (Global.PRINT_ANSWER.equals(type)) {
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

                    String value = bean.getValue();
                    value = (value == null) ? "" : value;

                    if ("".equalsIgnoreCase(value)) {
                        isNeedFeedLine = false;
                    } else {
                        if (isValidDate(value)) {
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = Formatter.parseDate(value.trim(), "yyyy/MM/dd");
                            value = df.format(date);
                        }

                        if ("".equalsIgnoreCase(label)) {
                            printText(value, ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                        } else {
                            sb.append(value);
                            printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                        }
                    }
                } else if (Global.PRINT_ANSWER_NO.equals(type)) {
                    StringBuilder sb = new StringBuilder();

                    String label = bean.getLabel();
                    sb.append(label);
                    sb.append(blank);
                    sb.append(valueSeparator);
                    sb.append(blank);

                    String value = bean.getValue();
                    value = (value == null) ? "" : value;

                    if ("".equalsIgnoreCase(label)) {
                        printText(value, ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                    } else {
                        sb.append(value);
                        printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                    }
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

                    printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
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

                    printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_BT_ID.equals(type)) {
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
                    String value = "?";
                    try {
                        value = bean.getValue();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        // empty
                    }
                    value = (value == null) ? "" : value;
                    sb.append(value);

                    printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_TIMESTAMP.equals(type)) {
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

                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    Date date = new Date();
                    String value = df.format(date);

                    value = (value == null) ? "" : value;
                    sb.append(value);

                    printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                }
                else if(Global.PRINT_UNIQUE_RV.equals(type)){
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

                    String value = bean.getValue();
                    value = (value == null) ? "" : value;
                    if ("".equalsIgnoreCase(value)) {
                        isNeedFeedLine = false;
                    } else {
                        if ("".equalsIgnoreCase(label)) {
                            printText(value, ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                        } else {
                            sb.append(value);
                            printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                        }
                    }
                }
                if (isNeedFeedLine) {
                    lineFeed(1, false);
                } else {
                    isNeedFeedLine = true;
                }

            }
            lineFeed(2, false);
        } else {
            return false;
        }

        return true;
    }

    public boolean print() throws Exception {
        boolean isNeedFeedLine = true;
        if (connected) {
            for (PrintResult bean : list) {
                String type = bean.getPrint_type_id();
                if (Global.PRINT_NEW_LINE.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_BRANCH_ADDRESS.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_BRANCH_NAME.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_LABEL_CENTER.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_LABEL.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_LABEL_BOLD.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_EMPHASIZED, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_LABEL_CENTER_BOLD.equals(type)) {
                    printText(bean.getLabel(), ALIGNMENT_CENTER, TEXT_ATTRIBUTE_EMPHASIZED, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_LOGO.equals(type)) {
                    printBitmap(logo, ALIGNMENT_CENTER, BITMAP_WIDTH_FULL, 20, false);
                } else if (Global.PRINT_ANSWER.equals(type)) {
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

                    String value = bean.getValue();
                    value = (value == null) ? "" : value;

                    if ("".equalsIgnoreCase(value)) {
                        isNeedFeedLine = false;
                    } else {
                        if (isValidDate(value)) {
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = Formatter.parseDate(value.trim(), "yyyy/MM/dd");
                            value = df.format(date);
                        }

                        if ("".equalsIgnoreCase(label)) {
                            printText(value, ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                        } else {
                            sb.append(value);
                            printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                        }
                    }
                } else if (Global.PRINT_ANSWER_NO.equals(type)) {
                    StringBuilder sb = new StringBuilder();

                    String label = bean.getLabel();
                    sb.append(label);
                    sb.append(blank);
                    sb.append(valueSeparator);
                    sb.append(blank);

                    String value = bean.getValue();
                    value = (value == null) ? "" : value;

                    if ("".equalsIgnoreCase(label)) {
                        printText(value, ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                    } else {
                        sb.append(value);
                        printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                    }
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

                    printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
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

                    printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_BT_ID.equals(type)) {
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
                    String value = "?";
                    try {
                        value = bean.getValue();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        // empty
                    }
                    value = (value == null) ? "" : value;
                    sb.append(value);

                    printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                } else if (Global.PRINT_TIMESTAMP.equals(type)) {
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

                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    Date date = new Date();
                    String value = df.format(date);

                    value = (value == null) ? "" : value;
                    sb.append(value);

                    printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                }
                else if(Global.PRINT_UNIQUE_RV.equals(type)){
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

                    String value = bean.getValue();
                    value = (value == null) ? "" : value;
                    if ("".equalsIgnoreCase(value)) {
                        isNeedFeedLine = false;
                    } else {
                        if ("".equalsIgnoreCase(label)) {
                            printText(value, ALIGNMENT_CENTER, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                        } else {
                            sb.append(value);
                            printText(sb.toString(), ALIGNMENT_LEFT, TEXT_ATTRIBUTE_FONT_A, TEXT_SIZE_HORIZONTAL1, false);
                        }
                    }
                }
                if (isNeedFeedLine) {
                    lineFeed(1, false);
                } else {
                    isNeedFeedLine = true;
                }

            }
            lineFeed(2, false);
        } else {
            return false;
        }

        return true;
    }

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

    public void setValueSeparator(String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    public void releaseResources() {
        if (logo != null) {
            logo.recycle();
        }

        logo = null;
    }

    public abstract void print(Bitmap bitmap);

    public abstract boolean isConnected();

    public abstract boolean isPrinterConnected();

    public abstract void connect() throws Exception;

    public abstract void disconnect() throws Exception;

    public abstract void printText(String text, int alignment, int attribute, int size, boolean s);

    public abstract void printBitmap(Bitmap bitmap, int alignment, int width, int level, boolean s);

    public abstract void lineFeed(int lines, boolean s);

    public abstract void connect(BluetoothDevice device) throws Exception;

    public abstract boolean printSato() throws Exception;
}
