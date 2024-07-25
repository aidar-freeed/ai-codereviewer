package com.adins.mss.odr.catalogue.imageslider;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adins.mss.odr.R;
import com.bumptech.glide.Glide;

/**
 * Created by olivia.dg on 11/28/2017.
 */

public class FragmentSlider extends Fragment {
    private static final String ARG_PARAM1 = "params";

    private String imageUrls;

    public FragmentSlider() {
    }

    public static FragmentSlider newInstance(byte[] params) {
        FragmentSlider fragment = new FragmentSlider();
        Bundle args = new Bundle();
        args.putByteArray(ARG_PARAM1, params);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        imageUrls = getArguments().getString(ARG_PARAM1);
        View view = inflater.inflate(R.layout.fragment_slider_item, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.img);

        byte[] tempImage = getArguments().getByteArray(ARG_PARAM1);
//        Bitmap bm = null;
//        if(tempImage != null){
//            try {
//                bm = Utils.byteToBitmap(tempImage);
//            } catch (Exception e) { }
//        }
        if (tempImage != null)
            Glide.with(getActivity()).load(tempImage).into(image);

//            image.setImageBitmap(bm);

        return view;
    }
}
