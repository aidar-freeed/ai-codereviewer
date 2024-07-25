package com.adins.mss.coll.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.commons.CommonImpl;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.R;
import com.adins.mss.coll.fragments.view.DepositReportSummaryView;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.DepositReportHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;

import org.acra.ACRA;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aditya Purwa on 2/13/2015.
 */
public class DepositReportSummaryFragment extends Fragment {
    private DepositReportSummaryView view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        view = new DepositReportSummaryView(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_deposit_report_summary, container, false);
        return view.layoutInflater(inflater, container);
    }

    @Override
    public void onViewCreated(View mView, Bundle savedInstanceState) {
        super.onViewCreated(mView, savedInstanceState);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        view.onCreate();
        view.publish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

//    private void initialize() {
//        populateFields();
//        cleanup();
//        fillHeader();
//        fillDetail();
//    }

//    private void fillHeader() {
//        List<TaskH> h = TaskHDataAccess.getTaskCollToday(getActivity());
//        List<TaskH> allSentTask = TaskHDataAccess.getAllSentTask(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
//        List<TaskH> allSentTaskToday = new ArrayList<TaskH>();
//        for(TaskH taskH : allSentTask){
//            if(CommonImpl.dateIsToday(taskH.getSubmit_date())){
//                allSentTaskToday.add(taskH);
//            }
//        }
//
//        List<TaskH> allCollTask = TaskHDataAccess.getAllTaskCollection(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
//        List<TaskH> collTaskToday = new ArrayList<TaskH>();
//        for(TaskH taskH : allCollTask){
//            if(CommonImpl.dateIsToday(taskH.getAssignment_date())){
//                collTaskToday.add(taskH);
//            }
//        }
//
////    	int totalTask = TaskHDataAccess.getTotalTaskCollToday(getActivity());
//        int totalTask = collTaskToday.size();
////        totalTaskLabel.setText(totalTask + " Tasks");
//       /* List<TaskD> totalTaskCollToday = TaskDDataAccess.getTotalTaskCollToday(getActivity());
//        int totalTaskValue = 0;
//        for (TaskD task : totalTaskCollToday) {
//        	try {
//        		totalTaskValue = Integer.parseInt(task.getText_answer());
//			} catch (Exception e) {             FireCrash.log(e);
//				totalTaskValue = 0;
//			}
//        }*/
//        this.totalTaskValue.setText(totalTask + " Tasks");
//        int paidTask = TaskDDataAccess.getPaid(getActivity()).size();
//        int failTask = TaskDDataAccess.getFail(getActivity()).size();
//
//        List<TaskD> paidAllTask = TaskDDataAccess.getAllPaid(getActivity());
//        paidTask = 0;
//        for(TaskD taskD:paidAllTask){
//            for(TaskH taskH :collTaskToday){
//                if(taskD.getUuid_task_h().equals(taskH.getUuid_task_h())){
//                    paidTask++;
//                }
//            }
//        }
//
////        List<TaskD> failAllTask = TaskDDataAccess.getAllFail(getActivity());
//        failTask = 0;
//        for(TaskH taskH : allSentTaskToday){
//        	/*List<TaskD> ds = TaskDDataAccess.isTaskPaid(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), taskH.getUuid_task_h());
//        	for(TaskD d : ds){
//        		String a = d.getUuid_task_d();
//        	}*/
//            if(!TaskDDataAccess.isTaskPaid(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), taskH.getUuid_task_h())){
//                failTask++;
//            }
////        	List<TaskD> alltaskd = taskH.getTaskDList();
////        	for(TaskD d : alltaskd){
////        		String questionId = d.getQuestion_id();
////        		String questionGroupId = d.getQuestion_group_id();
////        		QuestionSet questionSet = QuestionSetDataAccess.getOne(getActivity(), taskH.getUuid_scheme(), questionId, questionGroupId);
////        		if(questionSet.getTag()!=null && Global.TAG_TOTAL.equals(questionSet.getTag())){
////        			tempPaid ++;
////        			break;
////        		}
////        	}
//        }
//
////        failTask = allSentTaskToday.size() - paidTask;
//
//
////        for(TaskD taskD:failAllTask){
////        	for(TaskH taskH :collTaskToday){
////        		if(taskD.getUuid_task_h().equals(taskH.getUuid_task_h())){
////        			failTask++;
////        		}
////        	}
////        }
//
//        int visitTask = paidTask + failTask;
//
//        totalPaidLabel.setText(paidTask + " Tasks");
//        totalFailLabel.setText(failTask + " Tasks");
//        totalVisitLabel.setText(visitTask + " Tasks");
//    }
//
//    private void fillDetail() {
//    	List<DepositReportH> tempBatches = DepositReportHDataAccess.listOfBacth(getActivity(),GlobalData.getSharedGlobalData().getUser().getUuid_user());
//    	List<DepositReportH> batches = new ArrayList<DepositReportH>();
//    	for(DepositReportH reportH : tempBatches){
//    		if(CommonImpl.dateIsToday(reportH.getDtm_crt())){
//    			batches.add(reportH);
//    		}
//    	}
//        HashMap<DepositReportH, List<DepositReportD>> packedListOfBatch = packListOfBatch(batches);
////        listOfBatch.setAdapter(new PackedBatchListAdapter(getActivity(), packedListOfBatch));
//        listOfBatch.setAdapter(new PackedBatchListAdapterNew(getActivity(), packedListOfBatch));
//    }
//
//    private HashMap<DepositReportH, List<DepositReportD>> packListOfBatch(List<DepositReportH> batches) {
//        HashMap<DepositReportH, List<DepositReportD>> map = new HashMap<DepositReportH, List<DepositReportD>>();
//        for (DepositReportH reportH : batches) {
//        	map.put(reportH, reportH.getDepositReportDList());
//        }
//        return map;
//    }
//
//    private void populateFields() {
//        totalTaskLabel  = (TextView) getView().findViewById(R.id.totalTask);
//        totalTaskValue  = (TextView) getView().findViewById(R.id.totalTaskValue);
//        totalPaidLabel  = (TextView) getView().findViewById(R.id.paidValue);
//        totalVisitLabel = (TextView) getView().findViewById(R.id.visitValue);
//        totalFailLabel  = (TextView) getView().findViewById(R.id.failValue);
//        listOfBatch     = (ListView) getView().findViewById(R.id.listOfBatch);
//    }
//
//    private void cleanup() {
//        Date today = CommonImpl.resetDate();
//        DepositReportHDataAccess.deleteDepositReport(getActivity(), today);
//    }

//    public static boolean dateIsToday(Date date){
//    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date tempDate = null;
//		try {
//			tempDate = sdf.parse(sdf.format(date));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        Date today = Tool.getSystemDate();
//
//        return today.equals(tempDate);
//    }

//    private class PackedBatchListAdapter extends BaseAdapter {
//
//        private final HashMap<TaskH, List<TaskD>> packedBatches;
//        private final List<TaskH> batchesHeaders;
//        private final Context context;
//
//        public PackedBatchListAdapter(Context context, HashMap<TaskH, List<TaskD>> packedBatches) {
//            this.context = context;
//            this.packedBatches = packedBatches;
//            this.batchesHeaders =
//                    Arrays.asList(packedBatches.keySet().toArray(new TaskH[packedBatches.keySet().size()]));
//        }
//
//        @Override
//        public int getCount() {
//            return packedBatches.size() + 1;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return packedBatches.get(batchesHeaders.get(position));
//        }
//
//        public int getTaskCount(int position) {
//            List<TaskD> tasks = (List<TaskD>) getItem(position);
//            return tasks.size();
//        }
//
//        public int getTaskSum(int position) {
//            List<TaskD> tasks = (List<TaskD>) getItem(position);
//            int sum = 0;
//            for (TaskD task : tasks) {
//            	try {
//            		sum += Integer.parseInt(task.getText_answer());
//				} catch (Exception e) {             FireCrash.log(e);
//					sum += 0;
//				}
//            }
//            return sum;
//        }
//
//        public TaskH getHeader(int position) {
//            return batchesHeaders.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                if (position % 2 == 0) {
//                    convertView = LayoutInflater.from(context).inflate(R.layout.item_summary_batch_colored, parent, false);
//                } else {
//                    convertView = LayoutInflater.from(context).inflate(R.layout.item_summary_batch_light, parent, false);
//                }
//            }
//            TextView label = (TextView) convertView.findViewById(R.id.itemLabel);
//            TextView value = (TextView) convertView.findViewById(R.id.itemValue);
//
//            if (position == getCount() - 1) {
//                label.setText("Total");
//                value.setText(String.valueOf(getSumAll()));
//                return convertView;
//            }
//
//            TaskH header = getHeader(position);
//            int taskCount = getTaskCount(position);
//            int taskSum = getTaskSum(position);
//
//            label.setText(header.getAppl_no());
//            value.setText(taskCount + " Tasks, " + taskSum);
//
//            return convertView;
//        }
//
//        private int getSumAll() {
//            int sum = 0;
//            for (int i = 0; i < getCount() - 1; i++) {
//                sum += getTaskSum(i);
//            }
//            return sum;
//        }
//    }
//
//    private class PackedBatchListAdapterNew extends BaseAdapter {
//
//        private final HashMap<DepositReportH, List<DepositReportD>> packedBatches;
//        private final List<DepositReportH> batchesHeaders;
//        private final Context context;
//
//        public PackedBatchListAdapterNew(Context context, HashMap<DepositReportH, List<DepositReportD>> packedBatches) {
//            this.context = context;
//            this.packedBatches = packedBatches;
//            this.batchesHeaders =
//                    Arrays.asList(packedBatches.keySet().toArray(new DepositReportH[packedBatches.keySet().size()]));
//        }
//
//        @Override
//        public int getCount() {
//            return packedBatches.size() + 1;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return packedBatches.get(batchesHeaders.get(position));
//        }
//
//        public int getTaskCount(int position) {
//            List<DepositReportD> tasks = (List<DepositReportD>) getItem(position);
//            return tasks.size();
//        }
//
//        public double getTaskSum(int position) {
//            List<DepositReportD> tasks = (List<DepositReportD>) getItem(position);
//            double sum = 0;
//            for (DepositReportD task : tasks) {
//            	try {
//            		sum += Double.parseDouble(task.getDeposit_amt());
//				} catch (Exception e) {             FireCrash.log(e);
//					sum += 0;
//				}
//            }
//            return sum;
//        }
//
//        public DepositReportH getHeader(int position) {
//            return batchesHeaders.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                if (position % 2 == 0) {
//                    convertView = LayoutInflater.from(context).inflate(R.layout.item_summary_batch_colored, parent, false);
//                } else {
//                    convertView = LayoutInflater.from(context).inflate(R.layout.item_summary_batch_light, parent, false);
//                }
//            }
//            TextView label = (TextView) convertView.findViewById(R.id.itemLabel);
//            TextView value = (TextView) convertView.findViewById(R.id.itemValue);
//
//            if (position == getCount() - 1) {
//                label.setText("Total");
//                value.setText(Tool.separateThousand(String.valueOf(getSumAll())));
//                return convertView;
//            }
//
//            DepositReportH header = getHeader(position);
//            int taskCount = getTaskCount(position);
//            double taskSum = getTaskSum(position);
//
//            label.setText(header.getBatch_id());
//            value.setText(taskCount + " Tasks, " + Tool.separateThousand(taskSum));
//
//            return convertView;
//        }
//
//        private int getSumAll() {
//            int sum = 0;
//            for (int i = 0; i < getCount() - 1; i++) {
//                sum += getTaskSum(i);
//            }
//            return sum;
//        }
//    }
}
