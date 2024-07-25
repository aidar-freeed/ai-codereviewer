package com.adins.mss.odr.products.adapter;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.NewMainActivity;
import com.adins.mss.dao.Product;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.odr.R;
import com.adins.mss.odr.products.ProductDetailFragment;

import java.util.List;

/**
 * Created by muhammad.aap on 11/15/2018.
 */

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {
    private FragmentActivity activity;
    private List<Product> objects;
    private Fragment fragment;

    public ProductListAdapter(FragmentActivity activity, List<Product> objects) {
        this.activity = activity;
        this.objects = objects;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private TextView txtName;
        private TextView txtPrice;
        private ImageView imgProduct;

        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            txtName = (TextView) itemView.findViewById(R.id.productNameText); //punya list item
            txtPrice = (TextView) itemView.findViewById(R.id.productPriceText);
            imgProduct = (ImageView) itemView.findViewById(R.id.productPicture);
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
        ProductViewHolder viewHolder = new ProductViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        if (objects.get(position).getLob_image() != null) {
            Bitmap bitmapImage = Utils.byteToBitmap(objects.get(position).getLob_image());
            holder.imgProduct.setImageBitmap(bitmapImage);
        } else {
            holder.imgProduct.setImageResource(R.drawable.img_notavailable);
            holder.imgProduct.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        holder.txtName.setText(objects.get(position).getProduct_name());
        String currency = Tool.separateThousand(objects.get(position).getProduct_value().toString());
        holder.txtPrice.setText("Rp " + currency);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uuidProduct = objects.get(position).getUuid_product();
                gotoProductsDetail(uuidProduct);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (objects == null || objects.size() == 0)
            return 0;
        else
            return objects.size();
    }

    private void gotoProductsDetail(String productId) {
        fragment = new ProductDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uuidProduct", productId);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale, com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
        transaction.replace(com.adins.mss.base.R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
