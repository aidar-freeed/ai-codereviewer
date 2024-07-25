package com.adins.mss.odr.accounts.adapter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.NewMainActivity;
import com.adins.mss.dao.Account;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.AccountDetailFragment;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by olivia.dg on 11/17/2017.
 */

public class    AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountListViewHolder> {

    private static FragmentActivity activity;
    private List<Account> accounts;
    protected Fragment fragment;

    public AccountListAdapter(FragmentActivity activity, List<Account> accounts) {
        this.activity = activity;
        this.accounts = accounts;
    }

    public class AccountListViewHolder extends RecyclerView.ViewHolder {
        public String phoneNumber;
        public final View mView;
        public Account mItem;
        public final TextView txtName;
        public final TextView txtInitial;
        public final ImageButton btnCall;

        public AccountListViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtInitial = (TextView) itemView.findViewById(R.id.txtThumbnailCircle);
            btnCall = (ImageButton) itemView.findViewById(R.id.callButtonAccount);
        }

        public void bind(Account account) {
            mItem = account;
            txtName.setText(account.getAccount_name());
            txtInitial.setText(initial(account.getAccount_name()));

            if(account.getAccount_phone_1()!=null){
                phoneNumber=account.getAccount_phone_1();
                btnCall.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (ActivityCompat.checkSelfPermission(btnCall.getContext(),
                                    android.Manifest.permission.CALL_PHONE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+phoneNumber));
                                activity.startActivity(callIntent);
                            } else {
                                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                            }
                        }
                    }
                });
            }else{
                btnCall.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(activity,"This customer does not have any registered phone number",Toast.LENGTH_SHORT);
                    }
                });
            }

        }
    }

    @Override
    public AccountListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_account_list_item, parent, false);
        AccountListViewHolder viewHolder = new AccountListViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AccountListViewHolder holder, final int position) {
        holder.mItem = accounts.get(position);
        holder.bind(accounts.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetail(accounts.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (accounts == null || accounts.size() == 0)
            return 0;
        else
            return accounts.size();
    }

    public void openDetail(Account account) {
        fragment = new AccountDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uuidAccount", account.getUuid_account());
        fragment.setArguments(bundle);
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale, com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
        transaction.replace(com.adins.mss.base.R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private String initial(String initial) {
        Pattern p = Pattern.compile("((^| )[A-Za-z])");
        Matcher m = p.matcher(initial);
        StringBuilder init = new StringBuilder();
        int counter =0;
        while (m.find() && counter<2) {
            init.append(m.group().trim());
            counter++;
        }

        return init.toString().toUpperCase();
    }
}
