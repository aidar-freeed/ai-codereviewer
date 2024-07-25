package com.adins.mss.coll.fragments;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import com.adins.mss.coll.R;
import com.adins.mss.coll.models.InstallmentScheduleItem;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.InstallmentSchedule;
import com.adins.mss.foundation.formatter.Formatter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.acra.ACRA;

/**
 * Created by aditya on 07/05/15.
 */
public class InstallmentScheduleDetailFragment extends AppCompatActivity {
    public static final String INSTALLMENT_SCHEDULE_DETAIL = "InstallmentScheduleDetailFragment.INSTALLMENT_SCHEDULE_DETAIL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_fragment_collection_activity_detail);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
		TableLayout table = (TableLayout) findViewById(R.id.tableCollectionDetail);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(getString(R.string.title_mn_installmentscheduledet));
		toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
		setSupportActionBar(toolbar);

		InstallmentSchedule item = InstallmentScheduleFragment.detailInstallmentSchedule;

		int index = 1;
		Class<InstallmentSchedule> type = InstallmentSchedule.class;

		for (Method method : type.getMethods()) {
			if (!method.getName().startsWith("get")) {
				continue;
			}
			// Seriously? No one want to output the class name for non techies.
			if (method.getName().equals("getClass")) {
				continue;
			}

			View row = LayoutInflater.from(this).inflate(
					R.layout.view_no_label_value, table, false);
			TextView no = (TextView) row.findViewById(R.id.no);
			TextView label = (TextView) row.findViewById(R.id.fieldName);
			TextView value = (TextView) row.findViewById(R.id.fieldValue);

			String labelField = method.getName().replaceFirst("get", "");

			if (labelField.equalsIgnoreCase("Dtm_crt")
					|| labelField.equalsIgnoreCase("Usr_crt")
					|| labelField.equalsIgnoreCase("Uuid_installment_schedule")
					|| labelField.equalsIgnoreCase("Uuid_task_h")
					|| labelField.equalsIgnoreCase("branch_code")) {

			} else {
				no.setText(String.valueOf(index++));
				label.setText(labelField.replace("_", " "));
				label.setGravity(Gravity.LEFT);
				try {
					String stringValue = "";
					Object itemValue = method.invoke(item);
					if (itemValue == null) {
						stringValue = "-";
					} else if (method.getReturnType() == Date.class) {
						stringValue = Formatter.formatDate((Date) itemValue,
								Global.DATE_STR_FORMAT);
					} else {
						stringValue = String.valueOf(itemValue);
					}					
//					if(stringValue!=null && stringValue.length()>3){
//						String part1 = stringValue.substring(stringValue.length()-3);
//	           		 	if(part1.substring(0, 1).equals("."))
//	           		 	{
//	           		 		value.setGravity(Gravity.RIGHT);
//	           		 	}
//					}
					value.setText(stringValue);
					value.setGravity(Gravity.RIGHT);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}

//				if (index % 2 == 1) {
//					row.setBackgroundResource(R.color.tv_gray_light);
//				} else if (index % 2 == 0) {
//					row.setBackgroundResource(R.color.tv_gray);
//				}

				table.addView(row);
			}
		}
	}
}
