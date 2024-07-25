package com.adins.mss.coll.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.adins.mss.coll.R;
import com.adins.mss.constant.Global;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;

public class PdfRendererFragment extends Fragment implements View.OnClickListener {

    private ParcelFileDescriptor mFileDescriptor;
    private PdfRenderer mPdfRenderer;
    private LinearLayout pdfLayout;
    private int mPageIndex;
    private PdfRenderer.Page mCurrentPage;

    private String urlFileName;
    private String documentNameTitle;


    private TextView documentName;
    private PhotoView mImageView;
    private Button mButtonPrevious;
    private Button mButtonNext;

    int displayWidth = 0;
    int displayHeight = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_renderer, container, false);

        documentName = (TextView) view.findViewById(R.id.documentNameTitle);
        mImageView = (PhotoView) view.findViewById(R.id.pdfImage);
        mButtonNext = view.findViewById(R.id.next);
        mButtonPrevious = view.findViewById(R.id.previous);
        pdfLayout = view.findViewById(R.id.pdfLayout);
        mButtonNext.setOnClickListener(this);
        mButtonPrevious.setOnClickListener(this);
        mPageIndex = 0;

        if(getArguments() != null){
            urlFileName = getArguments().getString("URL_FILE");
            documentNameTitle = getArguments().getString("documentName");
            documentName.setText(documentNameTitle);
        }

        pdfLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                pdfLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    displayWidth = pdfLayout.getWidth();
                    displayHeight = pdfLayout.getHeight();
                try {
                    openRenderer(getActivity(), urlFileName);
                    showPage(mPageIndex);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), getString(com.adins.mss.base.R.string.not_available), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } catch (Exception ex) {
                    if (Global.IS_DEV) {
                        ex.printStackTrace();
                    }
                    Toast.makeText(getActivity(), getString(com.adins.mss.base.R.string.not_available), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
//        try {
//            openRenderer(getActivity(), urlFileName);
//            showPage(mPageIndex);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(getActivity(), getString(com.adins.mss.base.R.string.not_available), Toast.LENGTH_SHORT).show();
//            getActivity().finish();
//        } catch (Exception ex) {
//            if (Global.IS_DEV) {
//                ex.printStackTrace();
//            }
//            Toast.makeText(getActivity(), getString(com.adins.mss.base.R.string.not_available), Toast.LENGTH_SHORT).show();
//            getActivity().finish();
//        }
    }

    private void openRenderer(Context context, String urlFileName) throws IOException {

        File file = new File(urlFileName);
        if (!file.exists()) {
            throw new IOException("File not found: " + urlFileName);
        }
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        if (mFileDescriptor != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mPdfRenderer = new PdfRenderer(mFileDescriptor);
            }
        }
    }

    @SuppressLint("NewApi")
    private void showPage(int index) {
        if (mPdfRenderer.getPageCount() <= index) {
            return;
        }

        if (null != mCurrentPage) {
            mCurrentPage.close();
        }

        mCurrentPage = mPdfRenderer.openPage(index);

        int pageWidth = mCurrentPage.getWidth();
        int pageHeight = mCurrentPage.getHeight();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        int displayWidth = displayMetrics.widthPixels;
//        int displayHeight = displayMetrics.heightPixels;

//        int displayWidth = pdfLayout.getWidth();
//        int displayHeight = pdfLayout.getHeight();

        float scaleX = (float) displayWidth / pageWidth;
        float scaleY = (float) displayHeight / pageHeight;
        float scale = Math.min(scaleX, scaleY);

        int newWidth = Math.round(pageWidth * scale);
        int newHeight = Math.round(pageHeight * scale);

        Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);

        mCurrentPage.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        mImageView.setImageBitmap(bitmap);
        updateUi();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateUi() {
        int index = mCurrentPage.getIndex();
        int pageCount = mPdfRenderer.getPageCount();
        if (pageCount == 1) {
            mButtonPrevious.setVisibility(View.GONE);
            mButtonNext.setVisibility(View.GONE);
        } else {
            mButtonPrevious.setEnabled(0 != index);
            mButtonNext.setEnabled(index + 1 < pageCount);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        int button = view.getId();
        if (button == R.id.previous) {
            showPage(mCurrentPage.getIndex() - 1);
        } else if (button == R.id.next) {
            showPage(mCurrentPage.getIndex() + 1);
        }
    }
}
