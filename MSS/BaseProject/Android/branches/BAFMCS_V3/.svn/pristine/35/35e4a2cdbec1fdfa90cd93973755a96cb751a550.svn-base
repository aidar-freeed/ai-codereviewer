package com.adins.mss.foundation.print.rv;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.dao.ReceiptVoucher;

/**
 * Created by angga.permadi on 5/11/2016.
 */
public class RvNumberItem extends LinearLayout {
    private View view;

    public RvNumberItem(Context context) {
        this(context, R.layout.spinner_style);
    }

    public RvNumberItem(Context context, int resource) {
        super(context);
        view = inflate(context, resource, this);
    }

    public void bind(ReceiptVoucher bean, int position) {
        TextView tv = (TextView) view.findViewById(R.id.text_spin);

        if (position == 0) {
            tv.setVisibility(GONE);
        } else {
            tv.setVisibility(VISIBLE);
            tv.setText(bean.getRv_number());
        }
    }
}
