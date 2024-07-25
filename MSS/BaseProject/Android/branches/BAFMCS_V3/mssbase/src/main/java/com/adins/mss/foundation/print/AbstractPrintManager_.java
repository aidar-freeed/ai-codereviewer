package com.adins.mss.foundation.print;

import com.adins.mss.constant.Global;
import com.adins.mss.dao.PrintResult;

import java.util.List;


public abstract class AbstractPrintManager_ {
    protected boolean connected = false;
    protected String valueSeparator = ":";
    protected char blank = ' ';
    protected int labelStart = 0;
    protected int separatorStart = 0;
    protected int valueStart = 0;
    protected List<PrintResult> list;

    public AbstractPrintManager_(List<PrintResult> list) {
        if (list == null)
            throw new IllegalArgumentException("Print list can not be null!");

        this.list = list;

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

    public void setValueSeparator(String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    public boolean isConnected() {
        return connected;
    }

    public abstract boolean connect() throws Exception;

    public abstract boolean disconnect() throws Exception;

    public abstract boolean print() throws Exception;

    public abstract boolean printSato() throws Exception;

    public abstract boolean printZebra() throws Exception;
    //public abstract boolean printSato(byte[] file) throws Exception;
}

