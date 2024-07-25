package com.adins.mss.base.dialogfragments;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.FormBean;
import com.adins.mss.base.dynamicform.QuestionSetTask;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.todo.form.NewTaskAdapter;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.androidquery.AQuery;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.Calendar;
import java.util.List;

import de.greenrobot.dao.DaoException;

/**
 * Created by olivia.dg on 10/23/2017.
 */

public abstract class NewTaskDialog extends DialogFragment {
    protected View view;
    protected AQuery query;
    protected List<Scheme> objects;
    private User user = GlobalData.getSharedGlobalData().getUser();
    SurveyHeaderBean header;
    Scheme lastUpdateScheme;
    public Boolean isEditable = false;
    private FirebaseAnalytics screenName;
    public NewTaskDialog() {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle);
    }

    protected abstract NewTaskAdapter getNewTaskAdapter();

    protected TaskH setNewTaskH(Scheme scheme) {
        TaskH taskH = null;
        taskH = new TaskH();
        taskH.setUuid_task_h(Tool.getUUID());
        taskH.setUser(user);
        taskH.setScheme(scheme);
        taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
        taskH.setIs_prepocessed(TaskHDataAccess.STATUS_SEND_INIT);
        taskH.setIs_verification("0");
        return taskH;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(getActivity());
        View view = inflater.inflate(R.layout.new_dialog_new_task, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        wmlp.windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().setAttributes(wmlp);

        query = new AQuery(view);
        query.id(android.R.id.list).adapter(getNewTaskAdapter());
        query.id(android.R.id.list).itemClicked(this, "itemClick");

        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (SchemeDataAccess.getAll(getContext()).isEmpty()) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_scheme_found), Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_new_order), null);
    }

    public void itemClick(AdapterView<?> parent, View v, int position, long id) {
        Scheme selectedScheme = getNewTaskAdapter().getItem(position);
        TaskH selectedTask = setNewTaskH(selectedScheme);
        header = new SurveyHeaderBean(selectedTask);

        Bundle bundle = new Bundle();
        bundle.putSerializable(CustomerFragment.SURVEY_HEADER, header);
        bundle.putInt(CustomerFragment.SURVEY_MODE, Global.MODE_NEW_SURVEY);
        if(selectedScheme.getForm_type().equals("KTP")){
            CustomerFragment.setHeader(header);
            int mode = Global.MODE_NEW_SURVEY;
            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            isEditable = false;
            GlobalData.getSharedGlobalData().setDoingTask(true);
            CustomerFragment.CheckScheme checkScheme = new CustomerFragment.CheckScheme(getActivity());
            checkScheme.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Scheme scheme = null;
            try {
                String uuidScheme = header.getUuid_scheme();
                scheme = SchemeDataAccess.getOne(getActivity(), uuidScheme);
            } catch (DaoException e) {
                ACRA.getErrorReporter().putCustomData("errorGetScheme", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorGetSchemeTime", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Get Scheme"));
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorGetScheme", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorGetSchemeTime", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Get Scheme"));
            }

            if (scheme != null) {
                header.setScheme(scheme);
                FormBean formBean = new FormBean(scheme);
                if (lastUpdateScheme != null) {
                    formBean = null;
                    formBean = new FormBean(lastUpdateScheme);
                }
                if (header.getStart_date() == null) {
                    if (header.getPriority() != null && header.getPriority().length() > 0) {
                    } else {
                        header.setStart_date(Tool.getSystemDateTime());
                    }
                }
                header.setForm(formBean);
                header.setIs_preview_server(formBean.getIs_preview_server());

                Bundle extras = new Bundle();
                if (isEditable) {
                    mode = Global.MODE_NEW_SURVEY;
                    header.setStatus(TaskHDataAccess.STATUS_SEND_DOWNLOAD);
                }
                extras.putInt(Global.BUND_KEY_MODE_SURVEY, mode);
                extras.putString(Global.BUND_KEY_UUID_TASKH, header.getUuid_task_h());
                extras.putSerializable(Global.BUND_KEY_SURVEY_BEAN, header);
                extras.putSerializable(Global.BUND_KEY_FORM_BEAN, formBean);

                QuestionSetTask task = new QuestionSetTask(getActivity(), extras);
                task.execute();
                dismiss();
            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.request_error),
                        Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }else {
            Fragment fragment = CustomerFragment.create(bundle);

            dismiss();

            FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
            transaction.replace(R.id.content_frame, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
