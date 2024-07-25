package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.form.ScrollingLinearLayoutManager;
import com.adins.mss.base.dynamicform.form.models.CriteriaParameter;
import com.adins.mss.base.dynamicform.form.models.LookupAnswerBean;
import com.adins.mss.base.dynamicform.form.models.Parameter;
import com.adins.mss.base.dynamicform.form.questions.LookupAnswerTask;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.adins.mss.base.dynamicform.form.questions.viewholder.LookupCriteriaOnlineActivity.KEY_SELECTED_CRITERIA;

public class LookupFilterActivity extends FragmentActivity implements View.OnClickListener, OnQuestionClickListener {
    public static QuestionBean selectedBean;
    private List<LookupAnswerBean> beanList;
    private RecyclerView qRecyclerView;
    private LookupCriteriaViewAdapter viewAdapter;
    private RelativeLayout filterLayout;
    private EditText txtFilter;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_filter);
        filterLayout = (RelativeLayout) findViewById(R.id.filterLayout);
        txtFilter = (EditText) findViewById(R.id.edtFilter);
        qRecyclerView = (RecyclerView) findViewById(R.id.criteriaRecycleView);
        qRecyclerView.setLayoutManager(new ScrollingLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false, 1000));
        if (selectedBean != null)
            beanList = selectedBean.getLookupsAnswerBean();
        if (beanList == null) {
            beanList = new ArrayList<>();
            qRecyclerView.setVisibility(View.GONE);
            filterLayout.setVisibility(View.VISIBLE);
        } else {
            qRecyclerView.setVisibility(View.VISIBLE);
            filterLayout.setVisibility(View.GONE);
        }
        viewAdapter = new LookupCriteriaViewAdapter(this, beanList, this);
        qRecyclerView.setAdapter(viewAdapter);
        mode = getIntent().getIntExtra(Global.BUND_KEY_MODE_SURVEY, 0);
        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSearch) {
            List<Parameter> parameters = new ArrayList<>();
            if (beanList != null && beanList.size() > 0) {
                for (LookupAnswerBean answerBean : beanList) {
                    String answer = QuestionBean.getAnswer(answerBean);
                    Parameter parameter = new Parameter();
                    parameter.setRefId(answerBean.getIdentifier_name());
                    parameter.setAnswer(answer);
                    parameters.add(parameter);
                }
            } else {
                Parameter parameter = new Parameter();
                parameter.setAnswer(txtFilter.getText().toString().trim());
                parameters.add(parameter);
            }
            CriteriaParameter criteriaParameter = new CriteriaParameter();
            criteriaParameter.setParameters(parameters);

            new LookupAnswerTask(this, selectedBean, criteriaParameter, mode).execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Global.REQUEST_LOOKUP_ANSWER) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_SELECTED_CRITERIA, bundle.getSerializable(KEY_SELECTED_CRITERIA));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            onBackPressed();
        }
    }

    @Override
    public void onSetLocationClick(QuestionBean bean, int group, int position) {

    }

    @Override
    public void onEditDrawingClick(QuestionBean bean, int group, int position) {

    }

    @Override
    public void onCapturePhotoClick(QuestionBean bean, int group, int position) {

    }

    @Override
    public void onLookupSelectedListener(QuestionBean bean, int group, int position) {

    }

    @Override
    public void onReviewClickListener(QuestionBean bean, int group, int position) {

    }

    @Override
    public void onValidasiDukcapilListener(QuestionBean bean, int group, int position) {

    }
}
