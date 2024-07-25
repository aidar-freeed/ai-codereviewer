package com.adins.mss.odr.other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.adins.mss.odr.R;
import com.adins.mss.odr.R.id;
import com.adins.mss.odr.R.layout;
import com.adins.mss.odr.other.AllTaskLocation;

/**
 * Created by winy.firdasari on 27/01/2015.
 */
public class ViewMapActivity extends FragmentActivity {
    Fragment fragment;
    Button btnAllTask;
    Button btnTaskCol;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_map_layout);

        btnAllTask = (Button) findViewById(R.id.btnSubmit2);
        btnAllTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new AllTaskLocation();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();

            }
        });

        /*btnTaskCol = (Button)findViewById(R.id.btnSubmit);
        btnTaskCol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent taskcolIntent = new Intent(ViewMapActivity.this,TaskCollLocation.class);
                ViewMapActivity.this.startActivity(taskcolIntent);
            }
        });*/

    }


}