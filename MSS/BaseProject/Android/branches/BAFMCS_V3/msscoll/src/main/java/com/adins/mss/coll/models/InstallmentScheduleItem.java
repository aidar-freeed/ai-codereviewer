package com.adins.mss.coll.models;

import java.io.Serializable;
import java.util.Date;

import com.adins.mss.dao.InstallmentSchedule;

/**
 * Created by adityapurwa on 06/05/15.
 */
public class InstallmentScheduleItem extends InstallmentSchedule implements Serializable {
   public InstallmentScheduleItem(InstallmentSchedule schedule){
	   setUuid_installment_schedule(schedule.getUuid_installment_schedule());
	   setUuid_task_h(schedule.getUuid_task_h());
       setAgreement_no(schedule.getAgreement_no());
       setBranch_code(schedule.getBranch_code());
       setInstallment_no(schedule.getInstallment_no());
       setInstallment_amount(schedule.getInstallment_amount());
       setInstallment_paid_amount(schedule.getInstallment_paid_amount());
       setLc_instl_amount(schedule.getLc_instl_amount());
       setLc_instl_paid(schedule.getLc_instl_paid());
       setLc_instl_waived(schedule.getLc_instl_waived());
       setPrincipal_amount(schedule.getPrincipal_amount());
       setInterest_amount(schedule.getInterest_amount());
       setOs_principal_amount(schedule.getOs_principal_amount());
       setOs_interest_amount(schedule.getOs_interest_amount());
       setLc_days(schedule.getLc_days());
       setLc_admin_fee(schedule.getLc_admin_fee());
       setLc_admin_fee_paid(schedule.getLc_admin_fee_paid());
       setLc_admin_fee_waive(schedule.getLc_admin_fee_waive());
       setUsr_crt(schedule.getUsr_crt());
       setDtm_crt(schedule.getDtm_crt());
       setDue_date(schedule.getDue_date());
   }
}
