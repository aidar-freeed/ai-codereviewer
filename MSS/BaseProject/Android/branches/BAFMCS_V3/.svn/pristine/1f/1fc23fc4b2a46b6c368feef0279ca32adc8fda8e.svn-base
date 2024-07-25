package com.adins.mss.coll.dashboardcollection.presenter;

import com.adins.mss.coll.dashboardcollection.DashboardCollectionContract;
import com.adins.mss.coll.dashboardcollection.IDashboardCollDataSource;
import com.adins.mss.coll.dashboardcollection.model.CollResultDetail;
import com.adins.mss.coll.dashboardcollection.model.DashboardData;
import com.adins.mss.coll.models.ReportSummaryResponse;

import java.util.ArrayList;
import java.util.List;

public class DashboardCollPresenter implements DashboardCollectionContract.Presenter {

    private DashboardCollectionContract.View view;
    private IDashboardCollDataSource dataSource;
    private DashboardData dashboardData;

    public DashboardCollPresenter(DashboardCollectionContract.View view, IDashboardCollDataSource dataSource) {
        this.view = view;
        this.dataSource = dataSource;
    }

    private void getOutstandingTask() {
        dataSource.getTotalOutstandingTaskAsync(new IDashboardCollDataSource.DashboardResultListener<Integer>() {
            @Override
            public void onResult(Integer result) {
                if(dashboardData == null)
                    dashboardData = new DashboardData();
                dashboardData.setOutstandingNum(result);
                getOutstandingAmount();
            }

            @Override
            public void onError(Exception e) {
                //view error
                view.showErrorInfo(e.getMessage());
            }
        });
    }

    private void getOutstandingAmount() {
        dataSource.getOutstandingAmountAsync(new IDashboardCollDataSource.DashboardResultListener<Double>() {
            @Override
            public void onResult(Double result) {
                dashboardData.setOutstandingAmount(result);
                view.onDashboardDataReceived(dashboardData);//call back to view
            }

            @Override
            public void onError(Exception e) {
                view.showErrorInfo(e.getMessage());
            }
        });
    }

    @Override
    public void requestDashboardData() {
        dataSource.requestDashboardData(new IDashboardCollDataSource.DashboardResultListener<ReportSummaryResponse>() {
            @Override
            public void onResult(ReportSummaryResponse result) {

                if(result == null){
                    getOutstandingTask();
                    return;
                }

                List<ReportSummaryResponse> collResults = result.getList_task();
                dashboardData = new DashboardData();
                dashboardData.setCollectedAmount(result.getTotal_received());
                dashboardData.setTargetAmount(result.getTotal_to_be_paid());
                List<CollResultDetail> collectedDetails = new ArrayList<>();
                List<CollResultDetail> failedDetails = new ArrayList<>();
                List<CollResultDetail> ptpDetails = new ArrayList<>();

                if(collResults == null || collResults.size() == 0){
                    dashboardData.setCollectDetails(collectedDetails);
                    dashboardData.setPtpDetails(ptpDetails);
                    dashboardData.setFailedDetails(failedDetails);
                    getOutstandingTask();
                    return;
                }

                for (ReportSummaryResponse collResult: collResults){
                    if(collResult == null)
                        continue;

                    switch (collResult.getStatusTask()){
                        case "Success":
                            CollResultDetail collectedDetail = new CollResultDetail(CollResultDetail.COLLECTED_TYPE
                                    ,collResult.getAgreementNo()
                                    ,collResult.getCustomerName(),collResult.getAmountPaid());
                            collectedDetails.add(collectedDetail);
                            break;
                        case "Promise to Pay":
                            CollResultDetail ptpDetail = new CollResultDetail(CollResultDetail.PTP_TYPE
                                    ,collResult.getAgreementNo()
                                    ,collResult.getCustomerName(),collResult.getPtp());
                            ptpDetails.add(ptpDetail);
                            break;
                        case "Failed":
                            CollResultDetail failedDetail = new CollResultDetail(CollResultDetail.FAILED_TYPE
                                    ,collResult.getAgreementNo()
                                    ,collResult.getCustomerName(),collResult.getNote());
                            failedDetails.add(failedDetail);
                            break;
                        default:
                            break;
                    }
                }

                dashboardData.setCollectDetails(collectedDetails);
                dashboardData.setPtpDetails(ptpDetails);
                dashboardData.setFailedDetails(failedDetails);

                getOutstandingTask();
            }

            @Override
            public void onError(Exception e) {
                //view->show dialog error
                view.showErrorInfo(e.getMessage());
            }
        });
    }

}
