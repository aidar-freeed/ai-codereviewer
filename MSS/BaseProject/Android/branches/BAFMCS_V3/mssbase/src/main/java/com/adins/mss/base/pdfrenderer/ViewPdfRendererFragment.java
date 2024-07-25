package com.adins.mss.base.pdfrenderer;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.R;
import com.adins.mss.constant.Global;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewPdfRendererFragment extends FragmentActivity implements View.OnClickListener {

    private static final int MAX_BITMAP_SIZE = 100 * 1024 * 1024; // 100 MB
    private static final float DEFAULT_ZOOM = 1;
    private float currentZoomLevel = DEFAULT_ZOOM;

    private ParcelFileDescriptor mFileDescriptor;
    private PdfRenderer mPdfRenderer;
    private PdfRenderer.Page mCurrentPage;

    private RecyclerView mRecyclerView;
    private ImageView mButtonZoomOut;

    private String urlFileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_pdf_renderer);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycleViewPdf);
        ImageView mButtonZoomIn = (ImageView) findViewById(R.id.btnZoomIn);
        mButtonZoomOut = (ImageView) findViewById(R.id.btnZoomOut);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        urlFileName = getIntent().getStringExtra("FILE_LOC");

        mButtonZoomIn.setOnClickListener(this);
        mButtonZoomOut.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            openRenderer(urlFileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.not_available), Toast.LENGTH_SHORT).show();
            this.finish();
        } catch (Exception e) {
            if (Global.IS_DEV) {
                e.printStackTrace();
            }
            Toast.makeText(this, getString(R.string.not_available), Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openRenderer(String urlFileName) throws IOException {
        File file = new File(urlFileName);
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        mPdfRenderer = new PdfRenderer(mFileDescriptor);
        showPagePdf();
    }

    private void showPagePdf() {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        for (int i = 0; i < getPageCount(); i++) {
            Bitmap bitmap = getPagePdf(i);
            bitmaps.add(bitmap);
            if (mCurrentPage != null) {
                mCurrentPage.close();
            }
        }

        ViewPdfAdapter adapter = new ViewPdfAdapter(bitmaps);
        mRecyclerView.setAdapter(adapter);
        updateUi();
    }

    private void updateUi() {
        mButtonZoomOut.setActivated(true);
        if (currentZoomLevel == 1) {
            mButtonZoomOut.setActivated(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Bitmap getPagePdf(int i) {
        if (getPageCount() <= i) {
            return null;
        }

        mCurrentPage = mPdfRenderer.openPage(i);
        float nPercentW = ((float) getResources().getDisplayMetrics().widthPixels / (float) mCurrentPage.getWidth() * currentZoomLevel);

        int newWidth = Math.max(Math.round(mCurrentPage.getWidth() * nPercentW), 1);
        int newHeight = Math.max(Math.round(mCurrentPage.getHeight() * nPercentW), 1);
        Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        if (bitmap.getByteCount() <= MAX_BITMAP_SIZE) {
//            Matrix matrix = new Matrix();
//            float dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_HIGH / getResources().getDisplayMetrics().densityDpi;
//            matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel);

            mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            return bitmap;
        } else {
            Toast.makeText(this, "Zoom has reached Max!", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private int getPageCount() {
        return mPdfRenderer.getPageCount();
    }

    @Override
    protected void onStop() {
        try {
            closeRenderer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    private void closeRenderer() throws IOException {
        if (mCurrentPage != null) {
            mCurrentPage.close();
            mCurrentPage = null;
        }
        if (mPdfRenderer != null) {
            mPdfRenderer.close();
        }
        if (mFileDescriptor != null) {
            mFileDescriptor.close();
        }
    }

    @Override
    public void onClick(View v) {
        int button = v.getId();
        if (button == R.id.btnZoomOut) {
            if (currentZoomLevel - 1 > 0) {
                --currentZoomLevel;
                showPagePdf();
            }
        } else if (button == R.id.btnZoomIn) {
            ++currentZoomLevel;
            showPagePdf();
        }
    }

    private static class ViewPdfAdapter extends RecyclerView.Adapter<ViewPdfAdapter.ViewHolder> {
        List<Bitmap> dataPdf;

        public ViewPdfAdapter(List<Bitmap> dataPdf) {
            this.dataPdf = dataPdf;
        }

        @NonNull
        @Override
        public ViewPdfAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_view_pdf_page, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imageView.setImageBitmap(dataPdf.get(position));
        }

        @Override
        public int getItemCount() {
            return dataPdf.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageViewPdf);
            }
        }
    }
}