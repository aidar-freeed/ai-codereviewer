package com.adins.mss.base;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adins.mss.base.api.ChangePasswordApi;
import com.adins.mss.base.api.ChangePasswordApiCallback;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.form.questions.QuestionsValidator;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.login.DefaultLoginModel;
import com.adins.mss.base.mainmenu.MainMenuHelper;
import com.adins.mss.base.models.ChangePasswordRequestModel;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;

import org.acra.ACRA;

/**
 * Created by adityapurwa on 30/03/15.
 */
public class ChangePasswordFragment extends Fragment implements IShowError {
    public static final String AS_ACTIVITY = "ChangePasswordActivity.AS_ACTIVITY";
    private EditText currentPassword;
    private EditText newPassword;
    private EditText retypePassword;
    private Button changePassword;
    ErrorMessageHandler errorMessageHandler;

    //bong 7 apr 15 not override
    public static void onBackPressed() {
        //EMPTY
    }

    @SuppressLint("NewApi")
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        getActivity().getActionBar().setTitle(getString(R.string.title_mn_changepassword));
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        errorMessageHandler = new ErrorMessageHandler(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setTitle(getString(R.string.title_mn_changepassword));
        getActivity().getActionBar().removeAllTabs();
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        View actionBar = view.findViewById(R.id.actionbar);
        final boolean asActivity = getArguments().getBoolean(AS_ACTIVITY);
        if (actionBar != null && asActivity) {
            actionBar.setVisibility(View.GONE);
        }

        String pwdExp = getActivity().getIntent().getStringExtra(DefaultLoginModel.PWD_EXP);
        try {
            if (pwdExp.equals("1")) {
                errorMessageHandler.processError("",getActivity().getString(R.string.password_expired), ErrorMessageHandler.TOAST_TYPE);
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }


        currentPassword = (EditText) view.findViewById(R.id.currentPassword);
        newPassword = (EditText) view.findViewById(R.id.newPassword);
        retypePassword = (EditText) view.findViewById(R.id.retypePassword);
        changePassword = (Button) view.findViewById(R.id.changePassword);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPassword.getText() == null || currentPassword.getText().toString().equals("")) {
                    currentPassword.setError(getActivity().getString(R.string.current_password_mandatory));
                }
                if (newPassword.getText() == null ||
                        newPassword.getText().toString().equals("")) {
                    newPassword.setError(getActivity().getString(R.string.new_password_mandatory));
                    return;
                }
                if (!newPassword.getText().toString().equals(retypePassword.getText().toString())) {
                    errorMessageHandler.processError(""
                            ,getActivity().getString(R.string.new_password_equal),
                            ErrorMessageHandler.TOAST_TYPE);
                    return;
                }
                if (newPassword.getText().toString().equals(currentPassword.getText().toString())) {
                    errorMessageHandler.processError(""
                            ,getActivity().getString(R.string.new_password_different)
                            , ErrorMessageHandler.TOAST_TYPE);
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
                            errorMessageHandler.processError(getActivity().getString(R.string.warning_capital)
                                    ,message
                                    , ErrorMessageHandler.TOAST_TYPE);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            errorMessageHandler.processError("","Change Password Error", ErrorMessageHandler.TOAST_TYPE);
                        }
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.password_changed), Toast.LENGTH_LONG).show();
                        if (!asActivity) {
                            User user = GlobalData.getSharedGlobalData().getUser();
                            user.setPassword(newPassword.getText().toString());
                            UserDataAccess.addOrReplace(getActivity(), user);
                            MainMenuHelper.doBackFragment(getActivity());
                        } else {
                            User user = GlobalData.getSharedGlobalData().getUser();
                            user.setPassword(newPassword.getText().toString());
                            UserDataAccess.addOrReplace(getActivity(), user);
                            goToSynchronize();
                            Global.syncIntent = null;
                            getActivity().finish();

                        }
                        clear();
                    }
                });

                api.execute(request);
            }
        });
    }

    private void clear() {
        currentPassword.setText("");
        newPassword.setText("");
        retypePassword.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    public boolean checkRegex(EditText editText) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        String answer = editText.getText().toString().trim();
        if ( !answer.isEmpty()) {
            if (QuestionsValidator.regexIsMatch(answer, regex)) {
                return true;
            } else {
                editText.setError(getActivity().getString(R.string.alpha_numeric_warning));
                Toast.makeText(getActivity(), getActivity().getString(R.string.input_not_valid), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void goToSynchronize() {
        if (Global.syncIntent != null) {
            Intent syncIntent = Global.syncIntent;
            startActivity(syncIntent);
        }
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        if(notifType == ErrorMessageHandler.TOAST_TYPE){
            if(errorSubject == null || errorSubject.equals(""))
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getActivity(), errorSubject+": "+errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}