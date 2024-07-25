package com.adins.mss.coll.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.R;
import com.adins.mss.coll.fragments.view.DepositReportTransferView;
import com.adins.mss.coll.interfaces.OnCameraInAppListener;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;

import org.acra.ACRA;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

/**
 * Created by Aditya Purwa on 2/4/2015.
 */
public class DepositReportTransferFragment extends Fragment {
//    private static final int REQUEST_FOR_CAMERA_CAPTURE = 4;
//    private Spinner transferBySpinner;
//    private View asBank;
//    private View asCashier;
//	private TextView txtNomorRekening;
//	private TextView txtNamaBank;
//	private TextView txtNamaKasir;
//	private TextView txtBuktiTransfer;
//    private EditText editNomorRekening;
//    private EditText editNamaBank;
//    private EditText editBatchId;
//    private EditText editNamaKasir;
//    private ImageView imageBukti;
//    private Button buttonSelectPhoto;
//    private Button buttonSend;
//	private View view;
//    protected static byte[] byteImage = null;
    String total 	= "";
	String batchId 	= "";
//    protected static Bitmap image = null;
//    protected static Bitmap tempImage = null;
	private DepositReportTransferView view;

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Utility.freeMemory();

		view.onDestroy();
//		if(image!=null && !image.isRecycled()){
//			image.recycle();
//			image=null;
//		}
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new DepositReportTransferView(getActivity(), onCameraInAppListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				UserHelp.showAllUserHelp(DepositReportTransferFragment.this.getActivity(),DepositReportTransferFragment.this.getClass().getSimpleName());
			}
		}, SHOW_USERHELP_DELAY_DEFAULT);
		return view.layoutInflater(inflater, container);
    }

	private OnCameraInAppListener onCameraInAppListener = new OnCameraInAppListener() {
		@Override
		public void onImageCapture(Intent intent, int requestCode) {
			startActivityForResult(intent, requestCode);
		}
	};

//	public void setLabel(){
//		if(view!=null) {
//			txtNomorRekening = (TextView) view.findViewById(R.id.txtNomorRekening);
//			txtNamaBank = (TextView) view.findViewById(R.id.txtBankName);
//			txtNamaKasir = (TextView) view.findViewById(R.id.txtCashierName);
//			txtBuktiTransfer = (TextView) view.findViewById(R.id.txtBuktiTransfer);
//
//			txtNomorRekening.setText(getActivity().getString(R.string.label_account_number_2));
//			txtNamaBank.setText(getActivity().getString(R.string.label_bank_name_2));
//			txtNamaKasir.setText(getActivity().getString(R.string.label_cashier_name_2));
//			txtBuktiTransfer.setText(getActivity().getString(R.string.label_transfer_evidence_2));
//		}
//	}

	@Override
	public void onResume() {
		super.onResume();
//		setLabel();
	}

	@Override
    public void onViewCreated(View mView, Bundle savedInstanceState) {
		super.onViewCreated(mView, savedInstanceState);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());

		total = getArguments().getString("TOTAL_DEPOSIT");
		batchId = getArguments().getString(com.adins.mss.coll.tool.Constants.KEY_BUND_BATCHID);

		view.total = total;
		view.setBatchId(batchId);
		view.onCreate();
	}

	@Override
	public void onAttach(Context context) {
		setHasOptionsMenu(true);
		super.onAttach(context);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.mnGuide){
			if(!Global.BACKPRESS_RESTRICTION) {

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						UserHelp.showAllUserHelp(DepositReportTransferFragment.this.getActivity(), DepositReportTransferFragment.this.getClass().getSimpleName());
					}
				}, SHOW_USERHELP_DELAY_DEFAULT);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public static void onStartActivityResult(Intent intent, int requestCode) {
		new DynamicFormActivity().startActivityForResult(intent, requestCode);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		view.onActivityResult(requestCode, resultCode, data);
	}

    public void onDestroy(){
    	super.onDestroy();
		view.onDestroy();
    }
}