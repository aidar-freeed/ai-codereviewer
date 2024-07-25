package com.adins.mss.odr.followup;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Account;
import com.adins.mss.dao.GroupTask;
import com.adins.mss.dao.Product;
import com.adins.mss.foundation.db.dataaccess.AccountDataAccess;
import com.adins.mss.foundation.db.dataaccess.ProductDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.api.AccountsSearchRequest;
import com.adins.mss.odr.followup.api.GetFollowUpResponse;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivia.dg on 11/24/2017.
 */

public class
FragmentFollowUpSearch extends Fragment {

    private AppCompatSpinner spinnerAccount;
    private AppCompatSpinner spinnerProduct;
    private AppCompatSpinner spinnerStatus;
    private Button btnSearch;
    private List<Account> accountList;
    private List<Product> productList;
    private String[] statusList;
    private String tempAccount;
    private String tempProduct;
    private String tempStatus;
    private FirebaseAnalytics screenName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenName = FirebaseAnalytics.getInstance(getActivity());
        return inflater.inflate(R.layout.new_fragment_account_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerAccount = (AppCompatSpinner) view.findViewById(R.id.spinnerAccount);
        spinnerProduct = (AppCompatSpinner) view.findViewById(R.id.spinnerProduct);
        spinnerStatus = (AppCompatSpinner) view.findViewById(R.id.spinnerStatus);
        btnSearch = (Button) view.findViewById(R.id.btnSearch);

        btnSearch.setText(getString(R.string.reqOppor));

        accountList = new ArrayList<>();
        accountList.clear();
        setAllAccountSpinner();
        List<Account> listAcc = AccountDataAccess.getAll(getActivity());
        if (listAcc != null)
            accountList.addAll(listAcc);
        AccountAdapter accountSpinner = new AccountAdapter(getContext(), R.layout.spinner_style2, accountList);
        spinnerAccount.setAdapter(accountSpinner);

        productList = new ArrayList<>();
        productList.clear();
        setAllProductSpinner();
        List<Product> listProd = ProductDataAccess.getAll(getContext());
        if (listProd != null)
            productList.addAll(listProd);
        ProductAdapter productSpinner = new ProductAdapter(getContext(), R.layout.spinner_style2, productList);
        spinnerProduct.setAdapter(productSpinner);

        statusList = getActivity().getResources().getStringArray(R.array.dropdownAccountStatus);
        StatusAdapter statusSpinner = new StatusAdapter(getContext(), R.layout.spinner_style2, statusList);
        spinnerStatus.setAdapter(statusSpinner);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account account = (Account) spinnerAccount.getSelectedItem();
                Product product = (Product) spinnerProduct.getSelectedItem();

                tempStatus = spinnerStatus.getSelectedItem().toString();
                if (tempStatus.equalsIgnoreCase(getString(R.string.allStatus)))
                    tempStatus = "";
                tempAccount = account.getUuid_account();
                tempProduct = product.getUuid_product();

                try {
                    if (Tool.isInternetconnected(getActivity())) {
                        doSearch(tempAccount, tempProduct, tempStatus);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setAllAccountSpinner() {
        Account accountDummy = new Account();
        accountDummy.setUuid_account("");
        accountDummy.setAccount_name("All Account");

        accountList.add(0, accountDummy);
    }

    private void setAllProductSpinner() {
        Product productDummy = new Product();
        productDummy.setUuid_product("");
        productDummy.setProduct_name("All Product");

        productList.add(0, productDummy);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_follow_up), null);
        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_followup));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    private void doSearch(final String account, final String product, final String status) throws ParseException, IOException {
        new AsyncTask<Void, Void, List<GroupTask>>() {
            final ProgressDialog progress = new ProgressDialog(getActivity());
            String errMessage;
            List<GroupTask> result = new ArrayList<GroupTask>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setMessage(getActivity().getString(R.string.contact_server));
                progress.show();
            }

            @Override
            protected List<GroupTask> doInBackground(Void... params) {
                AccountsSearchRequest request = new AccountsSearchRequest();
                request.setUuid_account(account);
                request.setUuid_product(product);
                request.setUuid_status(status);
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                String json = GsonHelper.toJson(request);

                String url = GlobalData.getSharedGlobalData().getURL_GET_FOLLOWUP();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
                HttpConnectionResult serverResult = null;
                try {
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                GetFollowUpResponse response = null;
                if (serverResult != null && serverResult.isOK()) {
                    try {
                        String responseBody = serverResult.getResult();
                        response = GsonHelper.fromJson(responseBody, GetFollowUpResponse.class);
                    } catch (Exception e) {
                        if(Global.IS_DEV) {
                            e.printStackTrace();
                            errMessage=e.getMessage();
                        }
                    }

                    List<GroupTask> groupTaskList = response.getListFollowUp();
                    if (groupTaskList != null && groupTaskList.size() != 0) {
                        result = groupTaskList;
                    } else {
                        errMessage = getActivity().getString(R.string.no_data_from_server);
                    }
                } else {
                    errMessage = getActivity().getString(R.string.server_down);
                }
                return result;
            }


            @Override
            protected void onPostExecute(List<GroupTask> result) {
                if(getActivity()!=null) {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                    if (errMessage != null) {
                        if (errMessage.equals(getActivity().getString(R.string.no_data_from_server))) {
                            NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getActivity());
                            builder.withTitle("INFO")
                                    .withMessage(errMessage)
                                    .show();
                        } else {
                            Toast.makeText(getActivity(), errMessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Fragment fragment = new FragmentFollowUpResult(getContext(), result);
                        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale, com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
                        transaction.replace(com.adins.mss.base.R.id.content_frame, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            }
        }.execute();

    }

    public class StatusAdapter extends ArrayAdapter<String> {
        private Context context;
        private String[] values;

        public StatusAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            this.context = context;
            this.values = objects;
        }

        public int getCount() {
            return values.length;
        }

        public String getItem(int position) {
            return values[position];
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style2, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values[position]);
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values[position]);
            return label;
        }
    }

    public class AccountAdapter extends ArrayAdapter<Account> {
        private Context context;
        private List<Account> values;

        public AccountAdapter(Context context, int resource, List<Account> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values = objects;
        }

        public int getCount() {
            return values.size();
        }

        public Account getItem(int position) {
            return values.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style2, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getAccount_name());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getAccount_name());
            return label;
        }
    }

    public class ProductAdapter extends ArrayAdapter<Product> {
        private Context context;
        private List<Product> values;

        public ProductAdapter(Context context, int resource, List<Product> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values = objects;
        }

        public int getCount() {
            return values.size();
        }

        public Product getItem(int position) {
            return values.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style2, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getProduct_name());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView) view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getProduct_name());
            return label;
        }
    }
}
