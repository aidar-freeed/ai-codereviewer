package com.adins.mss.foundation.print;

import com.adins.mss.dao.PrintDate;

import java.text.SimpleDateFormat;

/**
 * Created by angga.permadi on 3/10/2016.
 */
public class SubmitPrintEntity {
    private String dtm_print;
    private String uuid_task_h;

    public SubmitPrintEntity() {

    }

    public SubmitPrintEntity(PrintDate task) {
        this(new SimpleDateFormat("ddMMyyyyHHmmss").format(task.getDtm_print()), task.getUuid_task_h());
    }

    public SubmitPrintEntity(String dtm_print, String uuid_task_h) {
        this.dtm_print = dtm_print;
        this.uuid_task_h = uuid_task_h;
    }

    public String getDtm_print() {
        return dtm_print;
    }

    public void setDtm_print(String dtm_print) {
        this.dtm_print = dtm_print;
    }

    public String getUuid_task_h() {
        return uuid_task_h;
    }

    public void setUuid_task_h(String uuid_task_h) {
        this.uuid_task_h = uuid_task_h;
    }
}
