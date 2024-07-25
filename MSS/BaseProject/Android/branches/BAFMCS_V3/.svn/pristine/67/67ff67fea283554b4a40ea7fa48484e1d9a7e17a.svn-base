package com.adins.mss.base.dynamicform;

import com.adins.mss.dao.Scheme;

import java.io.Serializable;
import java.util.Date;

public class FormBean extends Scheme implements Serializable {

    public FormBean(Scheme scheme) {
        if (scheme == null)
            throw new IllegalArgumentException("Scheme == null !");


        setUuid_scheme(scheme.getUuid_scheme());
        setScheme_description(scheme.getScheme_description());
        setScheme_last_update(scheme.getScheme_last_update());
        setIs_printable(scheme.getIs_printable());
        setForm_id(scheme.getForm_id());
        setUsr_crt(scheme.getUsr_crt());
        setIs_preview_server(scheme.getIs_preview_server());
        setDtm_crt(scheme.getDtm_crt());
        setUsr_upd(scheme.getUsr_upd());
        setDtm_upd(scheme.getDtm_upd());
        setForm_type(scheme.getForm_type());
        setIs_active(scheme.getIs_active());
        setForm_version(scheme.getForm_version());//new
    }

    public FormBean(String id, Date lastUpdate, String isPrintable) {
        setUuid_scheme(id);
        setScheme_last_update(lastUpdate);
        setIs_printable(isPrintable);

    }

    public void setPreviewServer(String bool) {
        setIs_preview_server(bool);
    }

    public String toString() {
        return getForm_id() + " - " + getScheme_description();
    }
}
