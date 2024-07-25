package com.adins.mss.base.dialogfragments;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;

import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.Common;
import com.adins.mss.base.commons.CommonImpl;
import com.adins.mss.base.commons.SettingImpl;
import com.adins.mss.base.commons.SettingInterface;
import com.adins.mss.base.mainmenu.fragment.NewMenuFragment;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;

/**
 * Created by olivia.dg on 9/20/2017.
 */

public class SettingsDialog extends DialogFragment {

    private SettingInterface setting;
    private Common common;
    private Button btnSave;
    private RadioButton rdIndonesia;
    private RadioButton rdEnglish;

    public SettingsDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Global.isMenuMoreClicked = true;

        View view = inflater.inflate(R.layout.new_dialog_settings, container, false);

//        WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
//        wmlp.windowAnimations = R.style.DialogAnimation2;
//        getDialog().getWindow().setAttributes(wmlp);
//
//        getDialog().setCanceledOnTouchOutside(false);
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//
//        Rect displayRectangle = new Rect();
//        Window window = getDialog().getWindow();
//        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//        view.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        btnSave = (Button) view.findViewById(R.id.btnSave);
        rdIndonesia = (RadioButton) view.findViewById(R.id.rdBahasa);
        rdEnglish = (RadioButton) view.findViewById(R.id.rdEnglish);

        common = new CommonImpl();
        setting = new SettingImpl(getContext());
        String language = setting.getLanguage();

        if (language.equals(LocaleHelper.BAHASA_INDONESIA)) {
            rdIndonesia.setChecked(true);
        } else {
            rdEnglish.setChecked(true);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rdEnglish.isChecked()) {
                    setting.setLanguage(LocaleHelper.ENGLSIH);
                } else if (rdIndonesia.isChecked()) {
                    setting.setLanguage(LocaleHelper.BAHASA_INDONESIA);
                }
                common.setAuditData();

                dismiss();
                Global.isMenuMoreClicked = false;
                Fragment fragment = new NewMenuFragment();
                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Global.isMenuMoreClicked = false;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Global.isMenuMoreClicked = false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Global.isMenuMoreClicked = false;
    }
}
