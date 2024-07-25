package com.adins.mss.base;

import android.content.Context;
import com.adins.mss.base.dynamicform.TaskManager;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FirebaseCrashlytics.class)
public class SubmitFailedDraftTaskTest{

    @Mock
    Context mockContext;
    @Mock
    ObscuredSharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(FirebaseCrashlytics.class);
        when(mockContext.getApplicationContext()).thenReturn(mockContext);
    }
    @Test
    public void SendTaskOnBackgroundTest() {
        TaskH taskH = new TaskH();
        taskH.setUuid_task_h("5523735");
        taskH.setCustomer_name("test1");
        taskH.setCustomer_phone("089898989898");
        taskH.setCustomer_address("jl. test1");
        try {
            TaskManager.isSubmitFailedDraft(mockContext, taskH, null);
        }catch (Exception e){}
        assertEquals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT, taskH.getStatus());

//        final CountDownLatch signal = new CountDownLatch(1);
//        final TaskManager.SendTaskOnBackground task = new TaskManager.SendTaskOnBackground(mockContext, Global.MODE_SURVEY_TASK,header,
//                listOfQuestions, false);
//        task.execute();
//
//        assertEquals(false,Global.isManualSubmit);
//        TaskManager.isSubmitFailedDraft(mockContext, taskH, );
//        Timeline timeline = TimelineDataAccess.getOneTimelineByTaskH(mockContext, GlobalData.getSharedGlobalData().getUser().getUuid_user(), "5523735", "C38BB627-4100-11EA-B586-45FFA0B13D54");

    }
}
