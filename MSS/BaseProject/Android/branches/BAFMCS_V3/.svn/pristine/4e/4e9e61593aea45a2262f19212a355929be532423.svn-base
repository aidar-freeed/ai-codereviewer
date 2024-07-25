package com.adins.mss.coll.items;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.adins.mss.coll.R;
import com.adins.mss.coll.networks.entities.ClosingTaskEntity;
import com.androidquery.AQuery;

/**
 * Created by angga.permadi on 6/6/2016.
 */
public class ClosingTaskItem extends LinearLayout {

    protected AQuery query;

    public ClosingTaskItem(Context context) {
        super(context);
        inflate(context);

        setId((int) (System.currentTimeMillis() / 1000));
    }

    private void inflate(Context context) {
        View view = inflate(context, R.layout.item_closing_task, this);

        if(query == null) {
            query = new AQuery(view);
        } else {
            query = query.recycle(view);
        }
    }

    public void bind(ClosingTaskEntity entity) {
        if (entity == null) return;

        query.id(R.id.tv_no_kontrak).text(entity.getNoKontrak());
        query.id(R.id.tv_customer_name).text(entity.getCustomerName());
        query.id(R.id.tv_no_lkp).text(entity.getNoLkp());
    }
}
