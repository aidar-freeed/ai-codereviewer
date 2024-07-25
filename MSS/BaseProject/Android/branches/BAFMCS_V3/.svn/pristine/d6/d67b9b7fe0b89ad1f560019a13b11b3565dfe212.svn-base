package com.adins.mss.svy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.adins.mss.base.GlobalData;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.config.ConfigFileReader;

import org.acra.ACRA;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Properties;

import static org.acra.ACRA.PREF_DISABLE_ACRA;
import static org.acra.ACRA.PREF_ENABLE_ACRA;

@RunWith(AndroidJUnit4.class)
public class DisableAcraTest {

    Context context;

    @Before
    public void setUpContext(){
        context = InstrumentationRegistry.getTargetContext().getApplicationContext();
    }

    @Test
    public void testDisableAcra(){
        //used for acra log
        User a = null;
        try {
            String id = a.getBranch_id();
        }
        catch (NullPointerException e){
            ACRA.getErrorReporter().putCustomData("User Null Exception",e.getMessage());
            ACRA.getErrorReporter().handleSilentException(new Exception("Null Pointer User"));
        }

        //this code is taken from ACRA code to check acra disable
        //check disable from acra preference
        SharedPreferences acraPref = ACRA.getACRASharedPreferences();
        final boolean enableAcra = acraPref.getBoolean(PREF_ENABLE_ACRA, true);
        boolean disableAcra = acraPref.getBoolean(PREF_DISABLE_ACRA, !enableAcra);

        //test if pref disable acra is true
        Assert.assertTrue(disableAcra);
    }

}
