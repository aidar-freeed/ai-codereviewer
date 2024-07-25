package com.adins.mss.odr.accounts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.dao.Account;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.odr.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by olivia.dg on 11/17/2017.
 */

public class AccountDetailTabFragment extends Fragment {
    private FragmentActivity activity;
    private Account account;
    private TextView txtName;
    private TextView txtAddress;
    private TextView txtPhone1;
    private TextView txtPhone2;
    private ImageView image;
    private byte[] bitmapArray;

    public AccountDetailTabFragment(FragmentActivity activity, Account account) {
        this.activity = activity;
        this.account = account;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_detail_tab, container, false);

        setHasOptionsMenu(true);

        txtName = (TextView) view.findViewById(R.id.txtName);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtPhone1 = (TextView) view.findViewById(R.id.txtPhone1);
        txtPhone2 = (TextView) view.findViewById(R.id.txtPhone2);
        image = (ImageView) view.findViewById(R.id.accImage);

        txtName.setText(account.getAccount_name());
        txtAddress.setText(account.getAccount_address());
        txtPhone1.setText(account.getAccount_phone_1());

        if (account.getAccount_phone_2() == null && account.getAccount_phone_2().isEmpty())
            txtPhone2.setText("-");
        else
            txtPhone2.setText(account.getAccount_phone_2());

        final String lat = account.getAccount_latitude();
        final String lng = account.getAccount_longitude();

        try {
            String image_url = "https://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lng+"&zoom=15&size=720x300&maptype=roadmap&markers=color:green%7Clabel:I%7C"+lat+","+lng;

//            ImageLoader imgLoader = new ImageLoader(activity);
            Bitmap bitmap = getBitmapFromURL(image_url);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

            bitmapArray = stream.toByteArray();

            Bitmap bm = null;
            if(bitmapArray!=null){
                try {
                    bm = Utils.byteToBitmap(bitmapArray);
                } catch (Exception e) { }
            }

            if (bm != null)
                image.setImageBitmap(bm);

        } catch (Exception e) {
            e.getMessage();
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri url = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+lat+","+lng);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, url);
//                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        return view;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        } catch (RuntimeException ex) {
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.title_mn_account));
    }

}
