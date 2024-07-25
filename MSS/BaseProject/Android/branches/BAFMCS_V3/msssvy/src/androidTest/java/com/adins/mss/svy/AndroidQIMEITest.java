package com.adins.mss.svy;

import android.content.Context;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.adins.mss.foundation.http.AuditDataType;
import com.adins.mss.foundation.http.AuditDataTypeGenerator;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.foundation.phone.TelephonyInfo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.junit.Assert.assertEquals;

import java.util.Map;


@RunWith(AndroidJUnit4.class)
public class AndroidQIMEITest {
    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule(MSLoginActivity.class);
    @Mock
    Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext().getApplicationContext();

    }

    @Test
    public void LoginActivityTest(){
        Map<String, String> imeiMap =  AuditDataTypeGenerator.getListImeiFromDevice(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            assertEquals(AuditDataTypeGenerator.getAndroidId(), imeiMap.get(MssRequestType.UN_KEY_IMEI));
        } else{
            TelephonyInfo tm = TelephonyInfo.getInstance(context);
            assertEquals(tm.getImeiSIM1(), imeiMap.get(MssRequestType.UN_KEY_IMEI));
        }
    }
}
