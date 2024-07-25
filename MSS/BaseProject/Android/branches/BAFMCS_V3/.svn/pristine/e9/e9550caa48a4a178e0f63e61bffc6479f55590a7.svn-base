package com.adins.mss.odr.accounts.adapter;

import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.adins.mss.dao.Account;
import com.adins.mss.dao.Contact;
import com.adins.mss.dao.Product;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.LoadContact;

import java.util.List;

/**
 * Created by olivia.dg on 11/17/2017.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder> {

    private static FragmentActivity activity;
    private List<Product> productList;
    private Account account;

    public ContactListAdapter(FragmentActivity activity, Account account, List<Product> productList) {
        this.activity = activity;
        this.account = account;
        this.productList = productList;
    }

    public class ContactListViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView txtProduct;

        public ContactListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            txtProduct = (TextView) itemView.findViewById(R.id.txtProduct);
        }
    }

    @Override
    public ContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.productc_contact_list_item, parent, false);
        ContactListViewHolder viewHolder = new ContactListViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactListViewHolder holder, final int position) {
        holder.txtProduct.setText(productList.get(position).getProduct_name());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDetail(activity, productList.get(position));
                LoadContact task = new LoadContact(activity, account, productList.get(position).getUuid_product());
                task.execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (productList == null || productList.size() == 0)
            return 0;
        else
            return productList.size();
    }

    private void showDetail(FragmentActivity activity, Contact contact) {
        final AlertDialog dialog = new AlertDialog.Builder(activity, R.style.Dialog_NoTitle)
                .setView(R.layout.dialog_contact_detail)
                .create();
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        wmlp.windowAnimations = R.style.DialogAnimation;
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView contactName = (TextView) dialog.findViewById(R.id.txtName);
        TextView contactDept = (TextView) dialog.findViewById(R.id.txtDepartment);
        TextView contactPhone = (TextView) dialog.findViewById(R.id.txtPhone);
        TextView contactEmail = (TextView) dialog.findViewById(R.id.txtEmail);

        contactName.setText(contact.getContact_name());
        contactDept.setText(contact.getContact_dept());
        contactPhone.setText(contact.getContact_phone());
        contactEmail.setText(contact.getContact_email());
    }
}
