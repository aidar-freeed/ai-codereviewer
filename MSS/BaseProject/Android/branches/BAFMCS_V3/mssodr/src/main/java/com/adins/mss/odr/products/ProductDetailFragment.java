package com.adins.mss.odr.products;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.util.Utility;
import com.adins.mss.dao.Product;
import com.adins.mss.foundation.db.dataaccess.ProductDataAccess;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.odr.R;
import com.adins.mss.odr.products.api.ProductDetailViewPdf;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailFragment extends Fragment implements View.OnClickListener {

    private ImageView imgProduct;
    private TextView txtProductName, txtProductDesc;
    private Button btnViewBrosure;
    private String uuidProduct;
    private Product product;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgProduct = (ImageView) view.findViewById(R.id.imgProdDetail);
        txtProductName = (TextView) view.findViewById(R.id.txtProdName);
        txtProductDesc = (TextView) view.findViewById(R.id.txtProdDescription);
        btnViewBrosure = (Button) view.findViewById(R.id.btnViewBrosure);

        initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utility.freeMemory();
        getActivity().setTitle(getString(com.adins.mss.base.R.string.header_mn_product_detail));
    }

    private void initialize() {
        Bundle bundle = getArguments();
        uuidProduct = bundle.getString("uuidProduct");
        product = ProductDataAccess.getOne(getContext(), uuidProduct);

        if (product.getLob_image() != null) {
            Bitmap bitmapImage = Utils.byteToBitmap(product.getLob_image());
            imgProduct.setImageBitmap(bitmapImage);
        } else {
            imgProduct.setImageResource(R.drawable.img_notavailable);
            imgProduct.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        txtProductName.setText(product.getProduct_name());
        txtProductDesc.setText(product.getProduct_desc());
        btnViewBrosure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int button = v.getId();

        if (button == R.id.btnViewBrosure) {
            ProductDetailViewPdf task = new ProductDetailViewPdf(getActivity(), product);
            task.execute();
        }
    }

}
