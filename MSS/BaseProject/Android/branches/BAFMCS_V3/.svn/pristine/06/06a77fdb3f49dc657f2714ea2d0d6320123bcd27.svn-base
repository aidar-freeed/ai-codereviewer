package com.adins.mss.coll.closingtask;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.coll.R;
import com.adins.mss.coll.closingtask.models.ClosingTaskRequest;
import com.adins.mss.coll.closingtask.models.ClosingTaskResponse;
import com.adins.mss.coll.closingtask.senders.ClosingTaskListener;
import com.adins.mss.coll.closingtask.senders.ClosingTaskSender;
import com.adins.mss.coll.dummy.ClosingTaskDummyAdapter;
import com.adins.mss.coll.dummy.UserHelpCOLDummy;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.androidquery.AQuery;

/**
 * Created by angga.permadi on 6/6/2016.
 */
public class ClosingTaskFragment extends Fragment implements ClosingTaskListener {
    private AQuery query;
    private static boolean showDummy = true;
    public static ClosingTaskFragment newInstance() {
        return new ClosingTaskFragment();
    }
    private ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_closing_task, container, false);
        query = new AQuery(getActivity(), view);

        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(R.string.title_mn_closing_task);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        setHasOptionsMenu(true);
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) query.id(R.id.listClosingTask).getView();
        if(Global.ENABLE_USER_HELP &&
                showDummy &&
                Global.userHelpDummyGuide.get(ClosingTaskFragment.this.getClass().getSimpleName()) != null &&
                Global.userHelpDummyGuide.get(ClosingTaskFragment.this.getClass().getSimpleName()).size()>0){
            listView.setAdapter(ClosingTaskDummyAdapter.getInstance());
            showDummy=false;
            UserHelpCOLDummy userHelpCOLDummy = new UserHelpCOLDummy();
            userHelpCOLDummy.showDummyClosing(ClosingTaskFragment.this.getActivity(),ClosingTaskFragment.this.getClass().getSimpleName(),listView,ClosingTaskAdapter.getInstance());
        } else {
            listView.setAdapter(ClosingTaskAdapter.getInstance());
        }
        updateCount();

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(R.string.title_mn_closing_task);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.mnGuide){
            if(!Global.BACKPRESS_RESTRICTION) {
                listView.setAdapter(ClosingTaskDummyAdapter.getInstance());
                showDummy = false;
                UserHelpCOLDummy userHelpCOLDummy = new UserHelpCOLDummy();
                userHelpCOLDummy.showDummyClosing(ClosingTaskFragment.this.getActivity(), ClosingTaskFragment.this.getClass().getSimpleName(), listView, ClosingTaskAdapter.getInstance());
            }
        }
        return super.onOptionsItemSelected(item);
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
        if (GlobalData.getSharedGlobalData().getUser() != null) {
            String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            boolean isDraft = TaskHDataAccess.getAllTaskByStatus(getActivity(), uuidUser, TaskHDataAccess.STATUS_SEND_SAVEDRAFT).size() != 0;
            boolean isPending = TaskHDataAccess.getAllTaskByStatus(getActivity(), uuidUser, TaskHDataAccess.STATUS_SEND_PENDING).size() != 0;

            /*boolean isRvPending = TaskHDataAccess.getOneTaskByStatusRV(getActivity(), uuidUser, TaskHDataAccess.STATUS_RV_PENDING) != null;
*/
            if (Global.isIsUploading() || isDraft || isPending) { /*|| isRvPending) {*/
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                dialogBuilder.withTitle(getString(R.string.title_mn_closing_task))
                        .withMessage(getString(R.string.msg_still_uploading_closing_task))
                        .withButton1Text(getString(R.string.btnCancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                dialogBuilder.dismiss();
                            }
                        })
                        .isCancelable(false)
                        .isCancelableOnTouchOutside(true)
                        .show();
            } else {
                ClosingTaskRequest request = new ClosingTaskRequest();
                request.setFlag(ClosingTaskRequest.CLOSING_TASK);

                ClosingTaskSender<ClosingTaskResponse> sender = new ClosingTaskSender<>(
                        getActivity(), request, ClosingTaskResponse.class);
                sender.setListener(this);
                sender.execute();
            }
        }
    }
}
