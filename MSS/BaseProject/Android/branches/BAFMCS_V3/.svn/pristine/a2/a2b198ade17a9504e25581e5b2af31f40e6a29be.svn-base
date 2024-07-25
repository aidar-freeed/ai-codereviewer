package com.adins.mss.coll.fragments;

import android.os.Bundle;
import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.adins.mss.coll.R;
import com.adins.mss.coll.adapters.ClosingTaskAdapter;
import com.adins.mss.coll.interfaces.ClosingTaskImpl;
import com.adins.mss.coll.interfaces.ClosingTaskInterface;
import com.adins.mss.coll.networks.ClosingTaskListener;
import com.androidquery.AQuery;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by angga.permadi on 6/6/2016.
 */
public class ClosingTaskFragment extends Fragment implements ClosingTaskListener {
    public static final String CLOSING_TASK_LIST = "closing_task_list";

    private AQuery query;
    private ClosingTaskInterface iClosingTask;
    private FirebaseAnalytics screenName;
    //private ClosingTaskAdapter adapter;

    public static ClosingTaskFragment newInstance() {
        return new ClosingTaskFragment();
    }

    public static ClosingTaskFragment newInstance(Bundle args) {
        ClosingTaskFragment myFragment = new ClosingTaskFragment();
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(getActivity());
        iClosingTask = new ClosingTaskImpl(getActivity(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_closing_task, container, false);
        query = new AQuery(getActivity(), view);
        getActivity().setTitle(R.string.title_mn_closing_task);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            ListView listView = (ListView) query.id(R.id.listClosingTask).getView();
            listView.setAdapter(ClosingTaskAdapter.getInstance());
            updateCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_closing_task), null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        query = null;
    }

    @Override
    public void onClosingTaskSuccess() {
        ClosingTaskAdapter.getInstance().clear();
        ClosingTaskAdapter.getInstance().notifyDataSetChanged();
        updateCount();
    }

    private void updateCount() {
        query.id(R.id.btnClosingTask).clicked(this, "closingTask").text(
                getString(R.string.title_mn_closing_task) + " (" +
                ClosingTaskAdapter.getInstance().getCount() + ")");
    }

    @Keep // subcribe
    public void closingTask() {
        iClosingTask.closingTask();
//        if (GlobalData.getSharedGlobalData().getUser() != null) {
//            String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
//
//            boolean isDraft = TaskHDataAccess.getAllTaskByStatus(getActivity(), uuidUser, TaskHDataAccess.STATUS_SEND_SAVEDRAFT).size() != 0;
//            boolean isRvPending = TaskHDataAccess.getOneTaskByStatusRV(getActivity(), uuidUser, TaskHDataAccess.STATUS_RV_PENDING) != null;
//            boolean isPending = TaskHDataAccess.getAllTaskByStatus(getActivity(), uuidUser, TaskHDataAccess.STATUS_SEND_PENDING).size() != 0;
//
//            if (Global.isUploading || isDraft || isRvPending || isPending) {
//                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
//                dialogBuilder.withTitle(getString(R.string.title_mn_closing_task))
//                        .withMessage(getString(R.string.msg_still_uploading_closing_task))
//                        .withButton1Text(getString(R.string.btnCancel))
//                        .setButton1Click(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View arg0) {
//                                dialogBuilder.dismiss();
//                            }
//                        })
//                        .isCancelable(false)
//                        .isCancelableOnTouchOutside(true)
//                        .show();
//            } else {
//                ClosingTaskRequest request = new ClosingTaskRequest();
//                request.setFlag(ClosingTaskRequest.CLOSING_TASK);
//
//                ClosingTaskSender<ClosingTaskResponse> sender = new ClosingTaskSender<>(
//                        getActivity(), request, ClosingTaskResponse.class);
//                sender.setListener(this);
//                sender.execute();
//            }
//        }
    }
}
