package com.adins.mss.base;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adins.mss.base.api.ChangePasswordApi;
import com.adins.mss.base.api.ChangePasswordApiCallback;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.login.DefaultLoginModel;
import com.adins.mss.base.models.ChangePasswordRequestModel;
import com.adins.mss.base.syncfile.FileSyncHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.google.firebase.analytics.FirebaseAnalytics;

public class NewChangePasswordActivity extends AppCompatActivity {

    private EditText currentPassword;
    private EditText newPassword;
    private EditText retypePassword;
    private Button changePassword;
    private FirebaseAnalytics screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenName = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.new_change_password_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.fontColorWhite));
        toolbar.setTitle(getResources().getString(R.string.title_mn_resetpassword));
        setSupportActionBar(toolbar);

        String pwdExp = this.getIntent().getStringExtra(DefaultLoginModel.PWD_EXP);
        try {
            if (pwdExp.equals("1")) {
                Toast.makeText(NewChangePasswordActivity.this, getString(R.string.password_expired),
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            // TODO: handle exception
        }


        currentPassword = (EditText) findViewById(R.id.currentPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        retypePassword = (EditText) findViewById(R.id.retypePassword);
        changePassword = (Button) findViewById(R.id.changePassword);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPassword.getText() == null ||
                        newPassword.getText().toString().equals("")) {
                    newPassword.setError(getString(R.string.new_password_mandatory));
                    return;
                }
                if (!newPassword.getText().toString().equals(retypePassword.getText().toString())) {
                    Toast.makeText(NewChangePasswordActivity.this, getString(R.string.new_password_equal), Toast.LENGTH_SHORT).show();
                    return;
                }

                ChangePasswordRequestModel request = new ChangePasswordRequestModel();
                request.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                request.setOld_password(currentPassword.getText().toString());
                request.setNew_password(newPassword.getText().toString());
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                ChangePasswordApi api = new ChangePasswordApi(NewChangePasswordActivity.this);
                api.setCallback(new ChangePasswordApiCallback() {
                    @Override
                    public void onFailed(String message) {
                        try {
                            Toast.makeText(NewChangePasswordActivity.this, getString(R.string.warning_capital) + ": " + message, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            FireCrash.log(e);
                            Toast.makeText(NewChangePasswordActivity.this, "Change Password Error", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(NewChangePasswordActivity.this, getString(R.string.password_changed), Toast.LENGTH_LONG).show();
                        User user = GlobalData.getSharedGlobalData().getUser();
                        user.setPassword(newPassword.getText().toString());
                        UserDataAccess.addOrReplace(NewChangePasswordActivity.this, user);
//                        goToSynchronize();
                        Global.syncIntent = null;
                        finish();
                        clear();
                    }
                });

                api.execute(request);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        screenName.setCurrentScreen(this, getString(R.string.screen_name_change_password), null);
    }

    private void clear() {
        currentPassword.setText("");
        newPassword.setText("");
        retypePassword.setText("");
    }

    @Override
    public void onBackPressed() {
        //
    }

    private void goToSynchronize() {
        if (Global.syncIntent != null) {
            Intent syncIntent = Global.syncIntent;
            startActivity(syncIntent);
        }
    }
}
