package com.adins.mss.base.dialogfragments;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.api.ChangePasswordApi;
import com.adins.mss.base.api.ChangePasswordApiCallback;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.models.ChangePasswordRequestModel;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;

/**
 * Created by olivia.dg on 9/20/2017.
 */

public class ChangePasswordDialog extends DialogFragment {

    private TextView title;
    private EditText currentPassword;
    private EditText newPassword;
    private EditText retypePassword;
    private Button changePassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.new_dialog_change_password, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
        wmlp.windowAnimations = R.style.DialogAnimation2;
        getDialog().getWindow().setAttributes(wmlp);

        Rect displayRectangle = new Rect();
        Window window = getDialog().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        view.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        title = (TextView) view.findViewById(R.id.settings);
        currentPassword = (EditText) view.findViewById(R.id.currentPassword);
        newPassword = (EditText) view.findViewById(R.id.newPassword);
        retypePassword = (EditText) view.findViewById(R.id.retypePassword);
        changePassword = (Button) view.findViewById(R.id.btnSave);

        title.setText(getString(R.string.title_mn_resetpassword));

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPassword.getText() == null || currentPassword.getText().toString().equals("")) {
                    currentPassword.setText("");
                }
                if (newPassword.getText() == null ||
                        newPassword.getText().toString().equals("")) {
                    newPassword.setError(getActivity().getString(R.string.new_password_mandatory));
                    return;
                }
                if (!newPassword.getText().toString().equals(retypePassword.getText().toString())) {
                    Toast.makeText(
                            getActivity(),
                            getActivity().getString(R.string.new_password_equal),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ChangePasswordRequestModel request = new ChangePasswordRequestModel();
                request.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                request.setOld_password(currentPassword.getText().toString());
                request.setNew_password(newPassword.getText().toString());
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                ChangePasswordApi api = new ChangePasswordApi(getActivity());
                api.setCallback(new ChangePasswordApiCallback() {
                    @Override
                    public void onFailed(String message) {
                        try {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            FireCrash.log(e);
                            Toast.makeText(getActivity(), getActivity().getString(R.string.change_password_required), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.password_changed), Toast.LENGTH_LONG).show();

                        User user = GlobalData.getSharedGlobalData().getUser();
                        user.setPassword(newPassword.getText().toString());
                        UserDataAccess.addOrReplace(getActivity(), user);
                        clear();
                        dismiss();
                        Global.isMenuMoreClicked = false;
                    }
                });
                api.execute(request);
            }
        });
        return view;
    }

    private void clear() {
        currentPassword.setText("");
        newPassword.setText("");
        retypePassword.setText("");
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
