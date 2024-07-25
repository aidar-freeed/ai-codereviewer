package com.adins.mss.base.dynamicform;

import android.content.Context;
import androidx.annotation.NonNull;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.ExcludeFromGson;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SurveyHeaderBean extends TaskH implements Serializable, Cloneable {
    @ExcludeFromGson
    private FormBean form;
    @ExcludeFromGson
    private int imageLeft;

    public SurveyHeaderBean() {
    }

    public SurveyHeaderBean(TaskH taskH) {
        if (taskH == null)
            throw new IllegalArgumentException("taskH is null!");
        setUuid_task_h(taskH.getUuid_task_h());
        setTask_id(taskH.getTask_id());
        setStatus(taskH.getStatus());
        setIs_printable(taskH.getIs_printable());
        setCustomer_name(taskH.getCustomer_name());
        setCustomer_phone(taskH.getCustomer_phone());
        setCustomer_address(taskH.getCustomer_address());
        setNotes(taskH.getNotes());
        setSubmit_date(taskH.getSubmit_date());
        setSubmit_duration(taskH.getSubmit_duration());
        setSubmit_size(taskH.getSubmit_size());
        setSubmit_result(taskH.getSubmit_result());
        setAssignment_date(taskH.getAssignment_date());
        setPrint_count(taskH.getPrint_count());
        setDraft_date(taskH.getDraft_date());
        setUsr_crt(taskH.getUsr_crt());
        setDtm_crt(taskH.getDtm_crt());
        setPriority(taskH.getPriority());
        setLatitude(taskH.getLatitude());
        setLongitude(taskH.getLongitude());
        setScheme_last_update(taskH.getScheme_last_update());
        setIs_verification(taskH.getIs_verification());
        setIs_preview_server(taskH.getIs_preview_server());
        setUuid_user(taskH.getUuid_user());
        setVoice_note(taskH.getVoice_note());
        setUuid_scheme(taskH.getUuid_scheme());
        setZip_code(taskH.getZip_code());
        setScheme(taskH.getScheme());
        setStart_date(taskH.getStart_date());
        setOpen_date(taskH.getOpen_date());
        setLast_saved_question(taskH.getLast_saved_question());
        setAppl_no(taskH.getAppl_no());
        setIs_prepocessed(taskH.getIs_prepocessed());
        setIs_reconciled(taskH.getIs_reconciled());
        setPts_date(taskH.getPts_date());
        setAccess_mode(taskH.getAccess_mode());
        setRv_number(taskH.getRv_number());
        setStatus_rv(taskH.getStatus_rv());
        setFlag(taskH.getFlag());
        setForm_version(taskH.getForm_version());
        setAmt_due(taskH.getAmt_due());
        setOd(taskH.getOd());
        setInst_no(taskH.getInst_no());
        setSurvey_location(taskH.getSurvey_location());
        String schemeIsPrintable = Global.FALSE_STRING;
        try {
            schemeIsPrintable = taskH.getIs_printable();
        } catch (Exception e) {
            FireCrash.log(e);
        }

        String schemeId = taskH.getUuid_scheme();
        this.form = new FormBean(schemeId, getScheme_last_update(), schemeIsPrintable);

        try {
            String previewServer = taskH.getIs_preview_server();
            if (getForm() != null) {
                getForm().setPreviewServer(previewServer);
            }
        } catch (Exception e) {
            FireCrash.log(e);
            String previewServer = Global.FALSE_STRING;
            if (getForm() != null) {
                getForm().setPreviewServer(previewServer);
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public TaskH getTaskH() {
        return this;
    }

    public FormBean getForm() {
        return form;
    }

    public void setForm(FormBean form) {
        this.form = form;
    }

    public void setImageLeft(int imageLeft) {
        this.imageLeft = imageLeft;
    }
}
