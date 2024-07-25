package com.adins.mss.base.log;

import com.adins.mss.dao.PrintResult;

import java.util.List;

public abstract class AbstractPrintManager {
    public static String PRINT_ANSWER = "002";
    protected boolean connected = false;
    protected String valueSeparator = ":";
    protected char blank = ' ';
    protected int labelStart = 0;
    protected int separatorStart = 0;
    protected int valueStart = 0;
    protected List<PrintResult> list;

    public AbstractPrintManager(List<PrintResult> list) {
        if (list == null)
            throw new IllegalArgumentException("Print list can not be null!");

        this.list = list;

        int longestLabel = 1;
        for (PrintResult bean : list) {
            if (PRINT_ANSWER.equals(bean.getPrint_type_id())) {
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

    public void setValueSeparator(String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    public boolean isConnected() {
        return connected;
    }

    public abstract boolean connect() throws Exception;

    public abstract boolean disconnect() throws Exception;

    public abstract boolean print() throws Exception;

    // bong Oct 28th, 2014 - adding code from fif
    public abstract boolean printSato() throws Exception;

    public abstract boolean printZebra() throws Exception;
}
