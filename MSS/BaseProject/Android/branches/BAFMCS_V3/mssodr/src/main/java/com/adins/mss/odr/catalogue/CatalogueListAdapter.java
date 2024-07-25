package com.adins.mss.odr.catalogue;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.dao.Catalogue;
import com.adins.mss.odr.R;
import com.adins.mss.odr.catalogue.api.LoadPdf;

import java.util.List;

/**
 * Created by olivia.dg on 11/28/2017.
 */

public class CatalogueListAdapter extends RecyclerView.Adapter<CatalogueListAdapter.CatalogueViewHolder>{
    private FragmentActivity activity;
    private List<Catalogue> objects;

    public CatalogueListAdapter(FragmentActivity activity, List<Catalogue> objects) {
        this.activity = activity;
        this.objects = objects;
    }

    public class CatalogueViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private TextView txtName;
        private TextView txtDesc;

        public CatalogueViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
        }
    }

    @Override
    public CatalogueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalogue_list_item, parent, false);
        CatalogueViewHolder viewHolder = new CatalogueViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CatalogueViewHolder holder, final int position) {
        holder.txtName.setText(objects.get(position).getCatalogue_name());
        holder.txtDesc.setText(objects.get(position).getCatalogue_desc());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadPdf task = new LoadPdf(activity, objects.get(position).getUuid_mkt_catalogue());
                task.execute();
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
}
