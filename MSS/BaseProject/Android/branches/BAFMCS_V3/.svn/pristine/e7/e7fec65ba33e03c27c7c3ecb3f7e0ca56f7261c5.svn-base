package com.adins.mss.coll.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Keep;

import com.adins.mss.base.PrintActivity;
import com.adins.mss.coll.R;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.PaymentChannel;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.PaymentChannelDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.image.ViewImageActivity;
import com.androidquery.AQuery;

import org.acra.ACRA;

import java.util.List;

public class DepositReportPCDetailActivity extends Activity {
	protected static DepositReportH report;
	protected int total = 0;
	List<DepositReportD> reportDs;
	String cashierName;
	String bankAccount;
	String channel;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deposit_report_detail_layout_pc);
		AQuery query = new AQuery(this);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
		cashierName = report.getCashier_name();
		bankAccount = report.getBank_account();
		PaymentChannel channelDetail = PaymentChannelDataAccess.getOnePaymentChannel(getApplicationContext(), report.getCode_channel());
		channel = channelDetail.getDescription()+" | "+channelDetail.getCode();


		reportDs = report.getDepositReportDList();

		query.id(R.id.txtBatchId).text(report.getBatch_id());
		query.id(R.id.txtBankName).text(channel);
		query.id(R.id.txtNoTransaction).text(report.getNo_transaction());
		query.id(R.id.imgEvidenceTransfer).image(Utils.byteToBitmap(report.getImage())).clicked(this, "ViewImage");

		ImageButton btnPrintDepReport = (ImageButton)findViewById(R.id.btnPrintDepReport);
		btnPrintDepReport.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), PrintActivity.class);
	        	intent.putExtra("taskId", report.getBatch_id());
				intent.putExtra("isPrintDeposit", true);
	        	startActivity(intent);
			}
		});
        
        int no =1;
		TableLayout detailTable = (TableLayout)findViewById(R.id.tableLayout1);
		LayoutParams lp =new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,0.25f);
		
		LinearLayout detail = new LinearLayout(this);
		detail.setOrientation(LinearLayout.HORIZONTAL);
		detail.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
		detail.setBackgroundResource(R.color.tv_gray_light);
		
		TextView lblDetail = new TextView(this);
		lblDetail.setText("Detail");
		lblDetail.setTextColor(Color.BLACK);
		lblDetail.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,0.25f));
		detail.addView(lblDetail);
		detailTable.addView(detail);
		
		
		for (int i = 0; i < reportDs.size(); i++) {
			try {
				DepositReportD reportD = reportDs.get(i);
					LinearLayout row = new LinearLayout(this);
					row.setOrientation(LinearLayout.HORIZONTAL);
					row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
					if (no%2 == 0){		//if even number, set to blue background
						row.setBackgroundResource(R.color.tv_gray_light);
					}
					else {
						row.setBackgroundResource(R.color.tv_gray);
					}
					
					TextView lblNo = new TextView(this);
					lblNo.setText(no+ ". ");
					lblNo.setGravity(Gravity.CENTER_HORIZONTAL);
					lblNo.setTextColor(Color.BLACK);
					lblNo.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,0.4f));
					row.addView(lblNo);
					
					TaskH taskHs= TaskHDataAccess.getOneHeader(getApplicationContext(), reportD.getUuid_task_h());
                	String agreement_no = "";
                	if(taskHs!=null)
                		agreement_no = taskHs.getAppl_no();
					
					TextView lblLabel = new TextView(this);
					lblLabel.setText(agreement_no);
					lblLabel.setTextColor(Color.BLACK);
					lblLabel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,0.25f));
					row.addView(lblLabel);
					
					TextView lblSpace = new TextView(this);
					lblSpace.setText(" : ");
					lblSpace.setTextColor(Color.BLACK);
					lblSpace.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
					row.addView(lblSpace);

					try {
						TextView lblAnswer = new TextView(this);
						lblAnswer.setText(Tool.separateThousand(reportD.getDeposit_amt()));
						lblAnswer.setTextColor(Color.BLACK);
						lblAnswer.setGravity(Gravity.RIGHT);
						lblAnswer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,0.25f));
						row.addView(lblAnswer, lp);
					} catch (Exception e) {
					}
					
					detailTable.addView(row);
					no++;
							
			}
			finally {
			}
		}
        
        
	}

	@Keep
	 public void ViewImage(View view){
		 	Global.isViewer =true;
			Bundle extras = new Bundle();
			extras.putByteArray(Global.BUND_KEY_IMAGE_BYTE, report.getImage());
			Intent intent = new Intent(this, ViewImageActivity.class);
			intent.putExtras(extras);
			startActivity(intent);
	 }
	
	 @Keep
	 private class RecapitulationListAdapter extends ArrayAdapter<DepositReportD> {

	        private final DepositReportD[] originalItems;

	        public RecapitulationListAdapter(Context context, DepositReportD[] objects) {
	            super(context, R.layout.deposit_report_list_recap, objects);
	            originalItems = objects;
	        }

	        @Override
	        public int getCount() {
	            return super.getCount() + 1;
	        }


	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            View view = null;
	                view = LayoutInflater.from(getContext()).inflate(R.layout.deposit_report_list_recap, parent, false);
	                

	                TextView label = (TextView) view.findViewById(R.id.itemLabel);
	                TextView value = (TextView) view.findViewById(R.id.itemValue);
	                TextView agreement= (TextView) view.findViewById(R.id.itemValueAgreement);
	                RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.agreementNumber_layout);
	                LinearLayout backLayout = (LinearLayout)view.findViewById(R.id.itemBase);
	                if(position%2==1)
	                	backLayout.setBackgroundResource(R.color.tv_gray);
	                if (position == getCount() - 1) {
	                    label.setText("Total");
	                    value.setText(Tool.separateThousand(String.valueOf(sumOfItems())));
	                    layout.setVisibility(View.GONE);
	                } else {
	                	DepositReportD item = getItem(position);
	                	TaskH taskHs= TaskHDataAccess.getOneHeader(getApplicationContext(), item.getUuid_task_h());
	                	String agreement_no = "";
	                	if(taskHs!=null)
	                		agreement_no = taskHs.getAppl_no();
	                	agreement.setText(agreement_no);
	                    value.setText(Tool.separateThousand(item.getDeposit_amt()));
	                }	            

	            return view;
	        }

	        private int sumOfItems() {
	            int sum = 0;
	            try {
	            	for (DepositReportD item : originalItems) {
	            		String value = item.getDeposit_amt();
	            		if(value==null || value.equals("")) value = "0";
	            		int finalValue = Integer.parseInt(value);
	                    sum += finalValue;
	                }
				} catch (Exception e) {
					// TODO: handle exception
				}
	            total = sum;
//	            generatePrintResultDepReport(DepositReportDetailActivity.this, report);
	            return sum;
	        }
	    }
}
