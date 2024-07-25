package com.adins.mss.base.todolist.form;

import android.app.ActionBar;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.helper.OnStartDragListener;
import com.adins.mss.base.todolist.form.helper.SimpleItemTouchHelperCallback;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskHSequence;
import com.adins.mss.foundation.db.dataaccess.TaskHSequenceDataAccess;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ACER 471 on 3/22/2017.
 */

public class SurveyPlanFragment extends Fragment implements OnStartDragListener {

    public static final String REQ_PRIORITY_LIST = "REQ_PRIORITY_LIST";
    public static final String REQ_LOG_LIST = "REQ_LOG_LIST";
    public static final String REQ_STATUS_LIST = "REQ_STATUS_LIST";
    public static final String BUND_KEY_REQ = "BUND_KEY_REQ";

    RecyclerView recyclerView;
    private SurveyListAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private List<TaskH> listTaskH;
    private LinearLayout layoutView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.survey_plan_layout, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerList);
        Button btnSavePlan = (Button) view.findViewById(R.id.btnSavePlan);

        btnSavePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskHSequenceDataAccess.clean(getActivity());
                TaskHSequenceDataAccess.insertAllNewTaskHSeq(getActivity(), adapter.getListTaskH());
                Toast.makeText(getActivity(), "Plan sudah di simpan", Toast.LENGTH_SHORT).show();
                FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                getFragmentManager().popBackStack();
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadListView();

        adapter = new SurveyListAdapter(getActivity(), this, listTaskH);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        layoutView = (LinearLayout) view.findViewById(R.id.layoutView);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


    }

    public void loadListView() {
        listTaskH = null;
        try {
            listTaskH = ToDoList.getListTaskInPriority(getActivity(), 0, null);
            List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(getContext());
            List<TaskH> taskHList = new ArrayList<>();
            if (taskHSequences.isEmpty()) {
                TaskHSequenceDataAccess.insertAllNewTaskHSeq(getContext(), listTaskH);
                taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(getContext());

            }
            for (int i = 0; i < taskHSequences.size(); i++) {
                taskHList.add(taskHSequences.get(i).getTaskH());
            }
            listTaskH = taskHList;
            if ( listTaskH.isEmpty()) {
                layoutView.setBackgroundResource(R.drawable.bg_notfound);
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorLoadListView", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorLoadListView", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat check TaskH"));
            e.printStackTrace();
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().removeAllTabs();
        getActivity().getActionBar().setTitle(getString(R.string.title_mn_tasklist));
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }
}
