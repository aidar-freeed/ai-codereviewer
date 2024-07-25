package com.adins.mss.odr.followup;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.todolist.form.TaskListTask;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GroupTask;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.followup.api.DoFollowUpRequest;
import com.adins.mss.odr.followup.api.DoFollowUpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivia.dg on 11/24/2017.
 */

public class FragmentFollowUpResult extends Fragment implements OnCheckedListener {
    private Context context;
    private List<GroupTask> groupTasks;
    private RecyclerView list;
    private RecyclerView.LayoutManager layoutManager;
    private FollowUpAdapter adapter;
    private Button btnRequest;
    private List<String> groupTaskId;

    public List<String> getGroupTaskId() {
        return groupTaskId;
    }

    public void setGroupTaskId(List<String> groupTaskId) {
        this.groupTaskId = groupTaskId;
    }

    public FragmentFollowUpResult(Context context, List<GroupTask> groupTasks) {
        this.context = context;
        this.groupTasks = groupTasks;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_followup_result, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = (RecyclerView) view.findViewById(R.id.list);
        layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        adapter = new FollowUpAdapter(context, FragmentFollowUpResult.this, groupTasks);
        list.setAdapter(adapter);

        btnRequest = (Button) view.findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getGroupTaskId() != null && getGroupTaskId().size() != 0)
                    doFollowUp(getGroupTaskId());
                else {
                    NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getActivity());
                    builder.withTitle("INFO")
                            .withMessage(getString(R.string.msgNoneSelected))
                            .show();
                }

            }
        });

        groupTaskId = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_followup));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.freeMemory();
    }

    public void doFollowUp(final List<String> groupTaskId) {
        new AsyncTask<Void, Void, String>() {
            final ProgressDialog progress = new ProgressDialog(getActivity());
            String errMessage;
            String result;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setMessage(getActivity().getString(R.string.contact_server));
                progress.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                DoFollowUpRequest request = new DoFollowUpRequest();
                request.setGroupTaskId(groupTaskId);
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                String json = GsonHelper.toJson(request);

                String url = GlobalData.getSharedGlobalData().getURL_DO_FOLLOWUP();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
                HttpConnectionResult serverResult = null;
                try {
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DoFollowUpResponse response = null;
                if (serverResult != null && serverResult.isOK()) {
                    try {
                        String responseBody = serverResult.getResult();
                        response = GsonHelper.fromJson(responseBody, DoFollowUpResponse.class);
                    } catch (Exception e) {
                        if(Global.IS_DEV) {
                            e.printStackTrace();
                            errMessage=e.getMessage();
                        }
                    }

                    result = response.getResult();
                    if (result == null) {
                        errMessage = getActivity().getString(R.string.no_result_from_server);
                    } else  {
                        if (result.equalsIgnoreCase("success")) {
                            result = "success";
                        } else {
                            errMessage = getActivity().getString(R.string.msgFollowupFailed);
                        }
                    }
                } else {
                    errMessage = getActivity().getString(R.string.server_down);
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (getActivity() != null) {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                    if (errMessage != null) {
                        if (errMessage.equals(getActivity().getString(R.string.no_data_from_server))) {
                            NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getActivity());
                            builder.withTitle("INFO")
                                    .withMessage(errMessage)
                                    .show();
                        } else {
                            Toast.makeText(getActivity(), errMessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (result.equalsIgnoreCase("success")) {
                            try {
                                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                                dialogBuilder.withTitle(getActivity().getString(R.string.title_mn_followup))
                                        .withMessage(getActivity().getString(R.string.requestSent))
                                        .withButton1Text(getActivity().getString(R.string.btnOk))
                                        .setButton1Click(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View arg0) {
                                                dialogBuilder.dismiss();
//                                                Fragment fragment1 = new PriorityTabFragment();
//                                                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
//                                                transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale, com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
//                                                transaction.replace(com.adins.mss.base.R.id.content_frame, fragment1);
//                                                transaction.addToBackStack(null);
//                                                transaction.commitAllowingStateLoss();
                                                TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                                        getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                                                task.execute();
                                            }
                                        })
                                        .show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            errMessage = getActivity().getString(R.string.msgFollowupFailed);
                        }
                    }
                }
            }
        }.execute();
    }

    @Override
    public void onChecked(String groupTask) {
        groupTaskId = getGroupTaskId();
        groupTaskId.add(groupTask);
        setGroupTaskId(groupTaskId);
    }

    @Override
    public void onUnchecked(String groupTask) {
        groupTaskId = getGroupTaskId();
        groupTaskId.remove(groupTask);
        setGroupTaskId(groupTaskId);
    }
}
