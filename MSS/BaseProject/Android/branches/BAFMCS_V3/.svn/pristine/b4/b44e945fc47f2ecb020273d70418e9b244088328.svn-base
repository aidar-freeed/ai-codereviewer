package com.adins.mss.foundation.questiongenerator;

import com.adins.mss.constant.Global;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.ReceiptVoucher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//import com.adins.util.Tool;

public class OptionAnswerBean extends Lookup implements Serializable {
    private boolean selected;

    //Glen 3 Oct 2014
    private String lovCode;

    public OptionAnswerBean() {
    }

    public OptionAnswerBean(String code, String value) {
        setCode(code);
        setValue(value);
    }

    public OptionAnswerBean(String code, String value, String lovCode) {
        setCode(code);
        setValue(value);
        this.lovCode = lovCode;
    }

    public OptionAnswerBean(ReceiptVoucher rv, int index){
        setUuid_lookup(rv.getUuid_receipt_voucher());
        setCode(rv.getUuid_receipt_voucher());
        setValue(rv.getRv_number());
        setSequence(index);
        setDtm_upd(rv.getDtm_crt());
        setLov_group(rv.getFlag_sources());
        setIs_active(Global.TRUE_STRING);
        setIs_deleted(Global.FALSE_STRING);
    }

    public OptionAnswerBean(ReceiptVoucher rv){
        setUuid_lookup(rv.getUuid_receipt_voucher());
        setCode(rv.getUuid_receipt_voucher());
        setValue(rv.getRv_number());
        setDtm_upd(rv.getDtm_crt());
        setLov_group(rv.getFlag_sources());
        setIs_active(Global.TRUE_STRING);
        setIs_deleted(Global.FALSE_STRING);
    }

    public OptionAnswerBean(Lookup lookup) {
        setUuid_lookup(lookup.getUuid_lookup());
        setOption_id(lookup.getOption_id());
        setCode(lookup.getCode());
        setValue(lookup.getValue());
        setFilter1(lookup.getFilter1());
        setFilter2(lookup.getFilter2());
        setFilter3(lookup.getFilter3());
        setFilter4(lookup.getFilter4());
        setFilter5(lookup.getFilter5());
        setIs_active(lookup.getIs_active());
        setIs_deleted(lookup.getIs_deleted());
        setSequence(lookup.getSequence());
        //  setLookup_id(lookup.getLookup_id());
        setUsr_crt(lookup.getUsr_crt());
        setDtm_crt(lookup.getDtm_crt());
        setUsr_upd(lookup.getUsr_upd());
        setDtm_upd(lookup.getDtm_upd());
        setUuid_question_set(lookup.getUuid_question_set());
        setLov_group(lookup.getLov_group());
    }

    public static List<OptionAnswerBean> getOptionList(List<Lookup> lookupList) {
        List<OptionAnswerBean> optionAnswers = new ArrayList<OptionAnswerBean>();

        for (Lookup lookup : lookupList) {
            OptionAnswerBean oA = new OptionAnswerBean(lookup);
            optionAnswers.add(oA);
        }
        return optionAnswers;

    }

    public static List<OptionAnswerBean> getOptionListFromRv(List<ReceiptVoucher> vouchers) {
        List<OptionAnswerBean> optionAnswers = new ArrayList<OptionAnswerBean>();

        for (int i=0; i<vouchers.size(); i++) {
            OptionAnswerBean oA = new OptionAnswerBean(vouchers.get(i),i);
            optionAnswers.add(oA);
        }
        return optionAnswers;
    }

    public static String optionAnswerToString(List<OptionAnswerBean> beans) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < beans.size(); i++) {
            OptionAnswerBean bean = beans.get(i);
            sb.append(bean.getValue());
            if (i < beans.size() - 1)
                sb.append(Global.DELIMETER_DATA);
        }
        return sb.toString();
    }

    public static String optionToSelectedString(List<OptionAnswerBean> beans) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < beans.size(); i++) {
            OptionAnswerBean bean = beans.get(i);
            sb.append(bean.getUuid_lookup());
            if (i < beans.size() - 1)
                sb.append(Global.DELIMETER_DATA);
        }
        return sb.toString();
    }

    public static OptionAnswerBean getOptionAnswerByValue(List<OptionAnswerBean> beans, String value) {
        OptionAnswerBean optBean = null;
        for (OptionAnswerBean bean : beans) {
            if (value.equals(bean.getValue())) {
                optBean = bean;
                break;
            }
        }
        return optBean;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String toString() {
        return getValue();
    }

    public String getLovCode() {
        return lovCode;
    }

    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
    }
}
