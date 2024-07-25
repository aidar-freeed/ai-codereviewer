package com.adins.mss.base.scheme;

import android.content.Context;
import android.os.AsyncTask;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.Scheme;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.scheme.sync.SyncQuestionSet;
import com.adins.mss.foundation.scheme.sync.SyncQuestionSetListener;
import com.adins.mss.foundation.scheme.sync.SyncQuestionSetRequest;
import com.adins.mss.foundation.scheme.sync.SyncQuestionSetResponse;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gigin.ginanjar on 28/12/2016.
 */

public class SyncQuestionSetTask {

    public static void syncQuestionSetInBackground(final Context activity) {
        List<VersionSchemeTaskBean> listVersionScheme = TaskHDataAccess.getAllVersionSchemeTask(activity);
        if(listVersionScheme != null){
            for (VersionSchemeTaskBean requestScehemeVersion : listVersionScheme){
                SyncQuestionSetRequest request = new SyncQuestionSetRequest();
                request.setForm_version(requestScehemeVersion.getFormVersion());
                request.setUuid_scheme(requestScehemeVersion.getUuidScheme());

                SyncQuestionSet task = new SyncQuestionSet(activity, request, new SyncQuestionSetListener() {
                    @Override
                    public void onProgress() {

                    }

                    @Override
                    public void onError(SyncQuestionSetResponse response) {

                    }

                    @Override
                    public void onSuccess(SyncQuestionSetResponse response, String uuidScheme) {
                        List<QuestionSet> questionSets = response.getListQuestionSet();
                        List<QuestionSet> newquestionSets = new ArrayList<QuestionSet>();
                        Scheme scheme = SchemeDataAccess.getOne(activity, uuidScheme);
                        try {
                            for (QuestionSet questionSet : questionSets) {
                                questionSet
                                        .setUuid_question_set(Tool.getUUID());
                                questionSet.setScheme(scheme);
                                newquestionSets.add(questionSet);
                            }
                            List<String> listVersionScheme = TaskHDataAccess.getAllVersionSchemeTaskByUuidScheme(activity, scheme.getUuid_scheme());
                            listVersionScheme.add(scheme.getForm_version());
                            QuestionSetDataAccess.deleteBySchemeVersion(activity,
                                    scheme.getUuid_scheme(), listVersionScheme);
                            QuestionSetDataAccess.addOrReplace(activity,
                                    scheme.getUuid_scheme(), newquestionSets);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            e.printStackTrace();
                            ACRA.getErrorReporter().putCustomData("errorSyncQuestionSetTask", e.getMessage());
                            ACRA.getErrorReporter().handleSilentException(new Exception("Error: Insert Question Set. " + e.getMessage()));
                        }
                    }
                });
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }
}
