package com.adins.mss.base.todolist.form.followup;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssResponseType;

import static com.pax.ippi.impl.NeptuneUser.getApplicationContext;

public class FollowUpFormFragment extends Fragment {

    private EditText namaKonsumen;
    private EditText nomorPerjanjian;
    private EditText tanggalJanjiBayar;
    private EditText catatan;
    private Button submitFormBtn;
    private Button scrollUpBtn;
    private Button scrollDownBtn;

    private String uuidTaskH;
    private String customerName;
    private String agreementNo;
    private String flagTask;
    private String tglJanjiBayar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow_up_form, container, false);
        namaKonsumen = view.findViewById(R.id.namaKonsumen);
        nomorPerjanjian = view.findViewById(R.id.nomorPerjanjian);
        tanggalJanjiBayar = view.findViewById(R.id.tanggalJanjiBayar);
        catatan = view.findViewById(R.id.catatan);
        submitFormBtn = view.findViewById(R.id.submitFormBtn);

        scrollDownBtn = view.findViewById(R.id.button_scroll_down);
        scrollUpBtn = view.findViewById(R.id.button_scroll_up);


        if(getArguments() != null){
            uuidTaskH = getArguments().getString("uuidTaskH");
            customerName = getArguments().getString("customerName");
            agreementNo = getArguments().getString("agreementNo");
            flagTask = getArguments().getString("flagTask");
            tglJanjiBayar = getArguments().getString("tglJanjiBayar");

            namaKonsumen.setText(customerName);
            nomorPerjanjian.setText(agreementNo);
            tanggalJanjiBayar.setText(tglJanjiBayar);
        }

        scrollUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int totalLines = catatan.getLineCount();
                int visibleLines = catatan.getMaxLines();
                int lineHeight = catatan.getLineHeight();
                int scrollAmount = lineHeight * visibleLines; // Scroll one page up
                int currentScrollY = catatan.getScrollY();

                int maxScroll = Math.max(0, currentScrollY - scrollAmount);

                catatan.scrollTo(0, maxScroll);
            }
        });

        scrollDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int totalLines = catatan.getLineCount();
                int visibleLines = catatan.getMaxLines();
                int lineHeight = catatan.getLineHeight();
                int scrollAmount = lineHeight * visibleLines; // Scroll one page down
                int currentScrollY = catatan.getScrollY();
                int maxScroll = Math.min(lineHeight * (totalLines - visibleLines), currentScrollY + scrollAmount);

                catatan.scrollTo(0, maxScroll);
            }
        });


        submitFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new submitFormFollowUp().execute();
            }
        });
        return view;
    }



    private class submitFormFollowUp extends AsyncTask<Void, Void, String>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.progressWait), true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                if(Tool.isInternetconnected(getActivity())){
                    String result = "";
                    SubmitFormRequest request = new SubmitFormRequest();
                    request.setUuidTaskH(uuidTaskH);
                    request.setFlagTask(flagTask);

                    request.setFollowUpNotes(catatan.getText().toString());
                    request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                    String json = GsonHelper.toJson(request);
                    String url = GlobalData.getSharedGlobalData().getURL_SUBMIT_FOLLOW_UP();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new  HttpCryptedConnection(getApplicationContext(), encrypt, decrypt);
                    HttpConnectionResult serverResult = null;
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);

                    if(serverResult != null){
                        if(serverResult .isOK()){
                            try {
                                result = serverResult.getResult();
                                return result;

                            }catch (Exception e){
                                FireCrash.log(e);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }catch (Exception e){
                FireCrash.log(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            if(result != null){
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                dialogBuilder.withTitle(getActivity().getString(R.string.success_label))
                        .isCancelableOnTouchOutside(false)
                        .withMessage(getActivity().getString(R.string.submit_follow_up_success))
                        .withButton1Text(getActivity().getString(R.string.btnOk))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBuilder.dismiss();
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                            }
                        })
                        .show();
            }
        }
    }
}
