package com.adins.mss.odr.catalogue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.dao.MobileContentD;
import com.adins.mss.dao.MobileContentH;
import com.adins.mss.foundation.db.dataaccess.MobileContentDDataAccess;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.odr.R;
import com.adins.mss.odr.catalogue.imageslider.SliderIndicator;
import com.adins.mss.odr.news.NewsContentAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by olivia.dg on 11/28/2017.
 */

public class PromoListAdapter extends RecyclerView.Adapter<PromoListAdapter.PromoViewHolder>{
    private FragmentActivity activity;
    private List<MobileContentH> objectsH;

    public PromoListAdapter(FragmentActivity activity, List<MobileContentH> objects) {
        this.activity = activity;
        this.objectsH = objects;
    }

    public class PromoViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private ImageView imageShare;
        private ImageView image;
        private TextView txtDesc;
        private TextView txtName;
        private ViewPager viewPager;
        private NewsContentAdapter adapter;
        private SliderIndicator mIndicator;
        private LinearLayout mLinearLayout;

        public PromoViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
//            image = (ImageView) itemView.findViewById(R.id.imgPromo);
            imageShare = (ImageView) itemView.findViewById(R.id.imgShare);
            txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            viewPager = (ViewPager) itemView.findViewById(R.id.newsImage);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.pagesContainer);

        }


    }

    @Override
    public PromoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_list_item, parent, false);
        PromoViewHolder viewHolder = new PromoViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PromoViewHolder holder, final int position) {
//        final Bitmap imagePromo = Utils.byteToBitmap(objectsH.get(position).getContent());
//        holder.image.setImageBitmap(imagePromo);
        String desc = objectsH.get(position).getContent_description();
        String name = objectsH.get(position).getContent_name();
        if(objectsH.get(position).getUuid_mobile_content_h()!=null){
            List<MobileContentD> detailList = MobileContentDDataAccess.getAll(activity, objectsH.get(position).getUuid_mobile_content_h());
            setupContent(holder, detailList);
        }

        holder.txtName.setText(name);
        holder.txtDesc.setText(desc);
        holder.imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 26) {
                    ((Vibrator) activity.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
                } else {
                    ((Vibrator) activity.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
                }

                int position = holder.viewPager.getCurrentItem();
                MobileContentD content = holder.adapter.objects.get(position);

                // Get access to the URI for the bitmap
                Bitmap imagePromo = Utils.byteToBitmap(content.getContent());
                Uri bmpUri = getLocalBitmapUri(imagePromo);
                if (bmpUri != null) {
                    // Construct a ShareIntent with link to image
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setType("image/*");
                    // Launch sharing dialog for image
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    activity.startActivity(Intent.createChooser(shareIntent, "Share Image"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (objectsH == null || objectsH.size() == 0)
            return 0;
        else
            return objectsH.size();
    }

    public Uri getLocalBitmapUri(Bitmap bitmapImage) {
        // Extract Bitmap from ImageView drawable
        Bitmap bmp = bitmapImage;
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(this.activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void setupContent(PromoViewHolder holder, List<MobileContentD> detailList) {

        holder.adapter = new NewsContentAdapter(activity, detailList);
        holder.viewPager.setAdapter(holder.adapter);

        holder.mIndicator = new SliderIndicator(activity, holder.mLinearLayout, holder.viewPager, R.drawable.indicator_circle, false);
        holder.mIndicator.setPageCount(detailList.size());
        try {
            holder.mIndicator.show();
        } catch (Exception e) {
        }
    }


}
