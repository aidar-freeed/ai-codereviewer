package com.adins.mss.coll.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.dynamicform.form.questions.ImageViewerActivity;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ImageQuestionViewHolder;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.BankAccountOfBranchResponse;
import com.adins.mss.base.todolist.form.CashOnHandResponse;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.NewMCMainActivity;
import com.adins.mss.coll.R;
import com.adins.mss.coll.commons.Toaster;
import com.adins.mss.coll.models.DepositReportRequest;
import com.adins.mss.coll.models.DepositReportResponse;
import com.adins.mss.coll.tool.Constants;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.BankAccountOfBranch;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.camerainapp.CameraActivity;
import com.adins.mss.foundation.db.dataaccess.BankAccountOfBranchDataAccess;
import com.adins.mss.foundation.db.dataaccess.DepositReportDDataAccess;
import com.adins.mss.foundation.db.dataaccess.DepositReportHDataAccess;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintItemDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.logger.Logger;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.util.Log;

import org.acra.ACRA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aditya Purwa on 2/13/2015.
 */
public class DepositReportACRecapitulateFragment extends Fragment {
    private AppCompatSpinner spinnerForm;
    private AppCompatSpinner spinnerUser;
    private AppCompatSpinner spinnerBatch;
    private Button transferButton;
    private Button buttonSelectPhoto;
    private ImageView imageBukti;
    private ImageView imgExpandBankAccount;
    private AutoCompleteTextView inputBranch;
    private FormAdapter formAdapter;
    private UserAdapter userAdapter;
    private BatchAdapter batchAdapter;
    private LookupArrayAdapter lookupAdapter;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    Map<String, TaskD[]> expandableListDetail;
    //    private String newBatchID;
    public static String selectedDepositScheme;
    public static String selectedDepositSchemeName;
    public static String selectedLookupCode;
    public static String selectedDepositUser;
    public static String selectedBatchId;
    public static User selectedDepositUserObject = null;
    String selectedDepositUserName;
    private List<Scheme> formListName;
    private List<User> userListName;
    private List<Lookup> formLookup;
    private String selectedBranch;
    private List<TaskH> batchIdList;
    private Bitmap image = null;
    private Bitmap tempImage = null;
    private byte[] byteImage = null;
    private double total = 0;

    private boolean isCash = true;
    protected AppCompatSpinner spinnerTypeDeposit;
    private TypeDepositAdapter typeAdapter;
    private List<Lookup> typeDepositList;

    protected AppCompatSpinner spinnerBankAccount;
    private BankAccountAdapter bankAccountAdapter;
    private List<BankAccountOfBranch> bankAccountList = new ArrayList<>();
    private String selectedBank, selectbankAccount, selectbankName;

    private View view;
    private TextView formNameBatch;
    private TextView submitDateBatch;
    private TextView totalDeposit;
    private TextView textBank;
    public TextView mLookupEmpty;

    //	String batchId;
    private int totalNeedPrint;
    public List<TaskH> listTaskH;
    public List<TaskH> listTaskRecap;
    public List<String> listTaskBatch;
    public ToDoList toDoList;
    private RefreshBackgroundTask backgroundTask;
    List<TaskD> reportsReconcile = new ArrayList<TaskD>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        toDoList = new ToDoList(getActivity());
        listTaskH = toDoList.getListTaskInStatusForMultiUser(ToDoList.SEARCH_BY_BATCH_ID, "BATCHID");
        view = inflater.inflate(R.layout.fragment_deposit_report_ac_recapitulate, container, false);
        spinnerTypeDeposit = (AppCompatSpinner) view.findViewById(R.id.spinnerTypeDeposit);
        textBank = (TextView) view.findViewById(R.id.txtTransferTo);
        spinnerBankAccount = (AppCompatSpinner) view.findViewById(R.id.spinnerBankAccount);
        mLookupEmpty = (TextView) view.findViewById(com.adins.mss.base.R.id.questionDropdownEmpty);
        imgExpandBankAccount = (ImageView) view.findViewById(R.id.img_expand_bank_account);

        Lookup cashLookup = new Lookup();
        cashLookup.setCode("CASH");
        cashLookup.setValue("Cash");
        Lookup transferLookup = new Lookup();
        transferLookup.setCode("TRANSFER");
        transferLookup.setValue("Transfer");
        typeDepositList = new ArrayList<>();
        typeDepositList.add(cashLookup);
        typeDepositList.add(transferLookup);
        typeAdapter = new TypeDepositAdapter(getActivity(), R.layout.spinner_style, R.id.text_spin, typeDepositList);
        typeAdapter.setDropDownViewResource(R.layout.spinner_style);

        spinnerTypeDeposit.setAdapter(typeAdapter);
        spinnerTypeDeposit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0) {
                    isCash = true;
                    textBank.setVisibility(View.GONE);
                    spinnerBankAccount.setVisibility(View.GONE);
                    imgExpandBankAccount.setVisibility(View.GONE);
                    mLookupEmpty.setVisibility(View.GONE);
                } else {
                    isCash = false;
                    new SyncBankAccountTask(getActivity()).execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bankAccountAdapter = new BankAccountAdapter(getActivity(), R.layout.spinner_style, R.id.text_spin, bankAccountList);
        bankAccountAdapter.setDropDownViewResource(R.layout.spinner_style);

        spinnerBankAccount.setAdapter(bankAccountAdapter);
        spinnerBankAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBank = bankAccountAdapter.getItem(position).getBank_account_no()+"|"+bankAccountAdapter.getItem(position).getBank_account_name()+"|"+
                        bankAccountAdapter.getItem(position).getBank_account_id();
                selectbankAccount = bankAccountAdapter.getItem(position).getBank_account_no();
                selectbankName = bankAccountAdapter.getItem(position).getBank_account_name();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectedLookupCode = null;
        selectedDepositScheme = null;
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());

//        newBatchID = Generator.generateBatchId(getActivity());

        formListName = new ArrayList<>();
        formListName.clear();
//        setAllschemeSpinner();
        formListName.addAll(SchemeDataAccess.getAllActivePriorityScheme(getActivity()));

        batchIdList = new ArrayList<>();
        batchIdList.clear();
        List<TaskH> listTask = new ArrayList<>();
        listTaskBatch = getListBatchId();
        if(listTaskBatch != null && listTaskBatch.size() > 0){
            for(String batch : listTaskBatch){
                if(batch != null){
                    TaskH taskHBatch = TaskHDataAccess.getAllHeader(getActivity(), batch);
                    listTask.add(taskHBatch);
                }
            }
        }
        batchIdList.addAll(listTask);

        userListName = new ArrayList<>();
        userListName.clear();
        userListName.addAll(UserDataAccess.getAllUserActive(getActivity()));

        spinnerForm = (AppCompatSpinner) view.findViewById(R.id.priorityViewByForm);
        formAdapter = new FormAdapter(getActivity(), R.layout.spinner_style, R.id.text_spin, formListName);
        formAdapter.setDropDownViewResource(R.layout.spinner_style);

        spinnerUser = (AppCompatSpinner) view.findViewById(R.id.priorityViewByUser);
        userAdapter = new UserAdapter(getActivity(), R.layout.spinner_style, R.id.text_spin, userListName);
        userAdapter.setDropDownViewResource(R.layout.spinner_style);

        spinnerForm.setVisibility(View.GONE);
        spinnerUser.setVisibility(View.GONE);

        ImageView imgExpandForm = (ImageView) view.findViewById(R.id.img_expand_by_form_ac);
        imgExpandForm.setVisibility(View.GONE);
        ImageView imgExpandUser = (ImageView) view.findViewById(R.id.img_expand_by_user_ac);
        imgExpandUser.setVisibility(View.GONE);

        //saya buat 2 konstruc, bisa pakek yang ini dari punya spinner dropdown
//        lookupAdapter = new LookupAdapter(getActivity(), R.layout.spinner_style, R.id.text_spin, formLookup);
        //atau menggunakan yang ini buat list biasa
//        lookupAdapter = new LookupAdapter(getActivity(), R.layout.autotext_list, formLookup);
        //ini list default dari androidnya
        lookupAdapter = new LookupArrayAdapter(getActivity());
        inputBranch = (AutoCompleteTextView)view.findViewById(R.id.textWithSuggestionBranch);
        inputBranch.setThreshold(1);
        inputBranch.setSingleLine();
        inputBranch.setAdapter(lookupAdapter);
        inputBranch.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));

        inputBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lookup lookup = lookupAdapter.getLookupItem(position);
                inputBranch.setText(lookup.getValue());
                selectedBranch = lookup.getValue().toString();
                selectedLookupCode = lookup.getCode();
                bankAccountList.clear();
                if (!isCash) {
                    bankAccountList.addAll(BankAccountOfBranchDataAccess.getOneByBranchCode(getActivity(), selectedLookupCode));
                    if (0 == bankAccountList.size()) {
                        selectedBank = null;
                        mLookupEmpty.setVisibility(View.VISIBLE);
                        textBank.setVisibility(View.VISIBLE);
                    } else {
                        mLookupEmpty.setVisibility(View.GONE);
                        spinnerBankAccount.setVisibility(View.VISIBLE);
                        imgExpandBankAccount.setVisibility(View.VISIBLE);
                        bankAccountAdapter.notifyDataSetChanged();
                        BankAccountOfBranch getOption = bankAccountList.get(0);
                        selectedBank = getOption.getBank_account_no()+"|"+getOption.getBank_account_name()+"|"+
                                getOption.getBank_account_id();
                        selectbankAccount = getOption.getBank_account_no();
                        selectbankName = getOption.getBank_account_name();
                    }
                }
                hideKeyboardFrom(getActivity(), view);
            }

        });

        spinnerBatch = (AppCompatSpinner) view.findViewById(R.id.priorityViewByBatch);
        batchAdapter = new BatchAdapter(getActivity(), R.layout.spinner_style, R.id.text_spin, batchIdList);
        batchAdapter.setDropDownViewResource(R.layout.spinner_style);

        spinnerForm.setAdapter(formAdapter);
        spinnerForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepositScheme = formAdapter.getItem(position).getUuid_scheme();
                selectedDepositSchemeName = formAdapter.getItem(position).getScheme_description();
                loadData();
                hideKeyboardFrom(getActivity(), view);
//                viewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerForm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideKeyboardFrom(getActivity(), view);
            }
        });

        spinnerUser.setAdapter(userAdapter);
        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepositUser = userAdapter.getItem(position).getUuid_user();
                selectedDepositUserName = userAdapter.getItem(position).getLogin_id();
                selectedDepositUserObject = userAdapter.getItem(position);
                loadData();
//                viewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerBatch.setAdapter(batchAdapter);
        spinnerBatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBatchId = batchAdapter.getItem(position).getBatch_id();
                loadData();
//                viewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imageBukti = (ImageView) view.findViewById(R.id.imageBukti);
        imageBukti.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View paramView) {
                if (null != imageBukti.getDrawable() && null != image) {
                    if (null == byteImage) {
                        byteImage = Utils.bitmapToByte(tempImage);
                    }
                    Global.isViewer=true;
                    Bundle extras = new Bundle();
                    extras.putByteArray(ImageViewerActivity.BUND_KEY_IMAGE, byteImage);
                    extras.putInt(ImageViewerActivity.BUND_KEY_IMAGE_QUALITY, Utils.picQuality);
                    extras.putBoolean(ImageViewerActivity.BUND_KEY_IMAGE_ISVIEWER, Global.isViewer);
                    Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });

        buttonSelectPhoto = (Button) view.findViewById(R.id.buttonSelectPhoto);
        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraApp(getActivity());
            }
        });

        transferButton = (Button) view.findViewById(R.id.transferButton);
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelectPhoto.setClickable(false);

                if(Tool.isInternetconnected(getActivity())) {

//                    if(listTaskH.size()==0) {
                    total = sumOfItems(reportsReconcile);
                    if (total != 0){
                        transferButton.setClickable(false);
                        new SendDepositReportTask(getActivity()).
                                execute(selectedBranch, selectedLookupCode);
                    }
                    else{
                        Toast.makeText(getActivity(), getString(R.string.transfer_failed), Toast.LENGTH_SHORT).show();
                    }
//                    } else {
//                        Toast.makeText(getActivity(), "There are task(s) that still pending", Toast.LENGTH_SHORT).show();
//                    }


                }else{
                    Toaster.warning(getActivity(), getActivity().getString(R.string.failed_send_data));
                    transferButton.setClickable(true);
                    buttonSelectPhoto.setClickable(true);
                }

                //default ke menu transfer
//                if(listTaskH.size()==0) {
//                    total = sumOfItems(reportsReconcile);
//                    if (total != 0)
//                        transfer();
//                    else
//                        Toast.makeText(getActivity(), getString(R.string.transfer_failed), Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getActivity(), "There are task(s) that still pending", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        //make sure that first selected user is current logged user
        User defaultUser = GlobalData.getSharedGlobalData().getUser();
        int position = -1;
        for(User user : userListName){
            position++;
            if(user.getUuid_user().equalsIgnoreCase(defaultUser.getUuid_user())){
                if(selectedDepositUserObject == null){
                    selectedDepositUserName = userListName.get(position).getLogin_id();
                    selectedDepositUser = userListName.get(position).getUuid_user();
                    selectedDepositUserObject = userListName.get(position);
                }
                spinnerUser.setSelection(position);
                break;
            }
        }

        formNameBatch = (TextView) getView().findViewById(R.id.formBatchValue);
        submitDateBatch = (TextView) getView().findViewById(R.id.dateBatchValue);
        totalDeposit = (TextView) getView().findViewById(R.id.totalDepValue);

        initialize();
    }

    private void openCameraApp(Activity mActivity) {
        if (GlobalData.getSharedGlobalData().isUseOwnCamera()) {
            int quality = Utils.picQuality;
            int thumbHeight = Utils.picHeight;
            int thumbWidht = Utils.picWidth;

            Intent intent = new Intent(mActivity, CameraActivity.class);
            intent.putExtra(CameraActivity.PICTURE_WIDTH, thumbWidht);
            intent.putExtra(CameraActivity.PICTURE_HEIGHT, thumbHeight);
            intent.putExtra(CameraActivity.PICTURE_QUALITY, quality);

            startActivityForResult(intent, Utils.REQUEST_IN_APP_CAMERA);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (null != intent.resolveActivity(mActivity.getPackageManager())) {
                File photoFile = null;
                try {
                    photoFile = ImageQuestionViewHolder.createImageFile(getActivity());
                } catch (IOException ex) {
                    if (Global.IS_DEV) {
                        Logger.e(this, "Error open camera: " + ex.getMessage());
                    }
                }
                if (null != photoFile) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(intent, Utils.REQUEST_CAMERA);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Utils.REQUEST_IN_APP_CAMERA == requestCode && Activity.RESULT_OK == resultCode) {
            Bundle bundle = data.getExtras();
            if (null != bundle) {
                Uri uri = Uri.parse(bundle.getString(CameraActivity.PICTURE_URI));
                File file = new File(uri.getPath());
                image = Utils.pathToBitmapWithRotation(file);
                tempImage =  Utils.pathToBitmapWithRotation(file);
                byteImage = Utils.pathBitmapToByteWithRotation(file);
                imageBukti.setImageBitmap(image);
            }
        } else if (Utils.REQUEST_CAMERA == requestCode && Activity.RESULT_OK == resultCode) {
            Uri uri= Uri.parse(DynamicFormActivity.mCurrentPhotoPath);
            File file = new File(uri.getPath());
            image = Utils.pathToBitmapWithRotation(file);
            byteImage = Utils.pathBitmapToByteWithRotation(file);
            tempImage =  Utils.pathToBitmapWithRotation(file);
            imageBukti.setImageBitmap(image);
        } else if (Crop.REQUEST_PICK == requestCode && Activity.RESULT_OK == resultCode) {
            Uri outputUri = data.getData();
            try {
                image = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(outputUri));
                tempImage = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(outputUri));
            } catch (FileNotFoundException e) {
                if (Global.IS_DEV) {
                    Log.e("Error reading image: " + e.getMessage(), e);
                }
            }
            imageBukti.setImageBitmap(image);
        }
        Utility.freeMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    private void initialize() {
        loadData();
    }

    private void loadData() {
        reportsReconcile.clear();
//        List<TaskD> reports = TaskDDataAccess.getTaskDTagTotalbyForm(getActivity(), selectedDepositScheme);
//        List<TaskD> reports = TaskDDataAccess.getTaskDTagTotalbyFormAndUser(getActivity(), selectedDepositScheme, selectedDepositUser);
        List<TaskD> reports = TaskDDataAccess.getTaskDTagTotalbyBatchId(getActivity(), selectedBatchId);


        for (TaskD taskD : reports) {
            TaskH taskH = TaskHDataAccess.getOneHeader(getActivity(), taskD.getUuid_task_h());
            selectedDepositScheme = taskH.getUuid_scheme();
            selectedDepositSchemeName = SchemeDataAccess.getOneSchemeName(getActivity(), selectedDepositScheme);
            if (taskH != null && taskH.getIs_reconciled() != null) {
                if (taskH.getIs_reconciled().equals("0")) {
                    reportsReconcile.add(taskD);
                }
            }

            if (taskH != null) {
                int printCount = taskH.getPrint_count() != null ? taskH.getPrint_count():0;
                String rvNumber = taskH.getRv_number();
                boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
                if ( printCount >0 || (rvNumber != null && !rvNumber.isEmpty()) || isRVinFront) {
                    // do nothing
                } else {
                    try {
                        String uuidScheme = taskH.getUuid_scheme();
                        Scheme scheme = SchemeDataAccess.getOne(getActivity(), uuidScheme);
                        if (scheme != null) {
                            if(scheme.getIs_printable().equals(Global.TRUE_STRING))
                                totalNeedPrint++;
                        }
                    }catch (Exception e){
                        totalNeedPrint++;
                    }
                }
                formNameBatch.setText(selectedDepositSchemeName);
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                submitDateBatch.setText(df.format(taskH.getSubmit_date()));
            }
        }
        totalDeposit.setText(Tool.separateThousand(String.valueOf(sumOfItems(new ArrayList<>(Arrays.asList(reportsReconcile.toArray(new TaskD[reportsReconcile.size()])))))));

        expandableListView = (ExpandableListView) getView().findViewById(R.id.expandableListView);
        Map<String, TaskD[]> expandableDetail = new HashMap<>();
        expandableDetail.put("Agreement List", reportsReconcile.toArray(new TaskD[reportsReconcile.size()]));
        expandableListDetail = expandableDetail;
        List<String> listTitle = new ArrayList<>();
        listTitle.add("Agreement List");
        expandableListTitle = listTitle;
        expandableListAdapter = new CustomExpandableListAdapter(getActivity(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
    }

    private List<String> getListBatchId (){
        List<String> depositedBatchList = new ArrayList<>();
        List<DepositReportH> depositedBatch = DepositReportHDataAccess.listOfBacth(getActivity());
        if(depositedBatch != null){
            for(DepositReportH depositHeader : depositedBatch){
                depositedBatchList.add(depositHeader.getBatch_id());
            }
        }
        List<String> undeposited = TaskHDataAccess.getAllBatchIdList(getActivity(), depositedBatchList);
        return undeposited;
    }


    void transfer() {
        ListView list = (ListView) getView().findViewById(R.id.recapitulationList);
        if (list.getAdapter().getCount() <= 1) {
            Toaster.warning(getActivity(), getString(R.string.nothing_to_report));
            return;
        }else if (totalNeedPrint > 0) {
            Toaster.warning(getActivity(), getActivity().getString(R.string.prompt_printRV));
            return;
        }
        BigDecimal totalValue = BigDecimal.valueOf(total);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_BUND_BATCHID, selectedBatchId);
        bundle.putString("TOTAL_DEPOSIT", totalValue.toString());
        bundle.putString("FORM", selectedDepositSchemeName);
        DepositReportTransferFragment fragment = new DepositReportTransferFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = NewMCMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public class TypeDepositAdapter extends ArrayAdapter<Lookup> {
        private Context context;
        private List<Lookup> values;

        public TypeDepositAdapter(Context context, int resource, int textViewResourceId, List<Lookup> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context=context;
            this.values=objects;
        }

        public int getCount(){
            return values.size();
        }

        public Lookup getItem(int position){
            return values.get(position);
        }

        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView)view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getValue());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView)view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getValue());
            return label;
        }
    }

    public class BankAccountAdapter extends ArrayAdapter<BankAccountOfBranch> {
        private Context context;
        private List<BankAccountOfBranch> values;

        public BankAccountAdapter(Context context, int resource, int textViewResourceId, List<BankAccountOfBranch> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context=context;
            this.values=objects;
        }

        public int getCount(){
            return values.size();
        }

        public BankAccountOfBranch getItem(int position){
            return values.get(position);
        }

        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView)view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getBank_account_no()+"-"+values.get(position).getBank_account_name());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView)view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getBank_account_no()+"-"+values.get(position).getBank_account_name());
            return label;
        }
    }

    private class RecapitulationListAdapter extends ArrayAdapter<TaskD> {

        private final TaskD[] originalItems;

        public RecapitulationListAdapter(Context context, int resource, TaskD[] objects) {
            super(context, resource, objects);
            originalItems = objects;
        }

        @Override
        public int getCount() {
            return super.getCount() + 1;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
//            if (position == 0) {
//                view = LayoutInflater.from(getActivity()).inflate(R.layout.item_recapitulation_detail_black, parent, false);
//
//                TextView label = (TextView) view.findViewById(R.id.itemLabel);
//                TextView value = (TextView) view.findViewById(R.id.itemValue);
//                label.setText(getString(R.string.label_agreement_no));
//                value.setText("Amount");
////                batchId=value.getText().toString().trim();
//            } else {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.item_recapitulation_detail, parent, false);

            TextView label = (TextView) view.findViewById(R.id.itemLabel);
            TextView value = (TextView) view.findViewById(R.id.itemValue);

            if (position == getCount() - 1) {
                label.setText("Total");
                value.setText(Tool.separateThousand(String.valueOf(sumOfItems(new ArrayList<TaskD>(Arrays.asList(originalItems))))));
                value.setText(Tool.separateThousand(String.valueOf(sumOfItems(new ArrayList<TaskD>(Arrays.asList(originalItems))))));
            } else {
                TaskD item = getItem(position);
//                    label.setText(item.getTaskH().getTask_id());
                label.setText(item.getTaskH().getAppl_no());
                value.setText(Tool.separateThousand(item.getText_answer()));
            }
//            }

            return view;
        }
    }

    public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> expandableListTitle;
        private Map<String, TaskD[]> expandableListDetail;

        public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                           Map<String, TaskD[]> expandableListDetail) {
            this.context = context;
            this.expandableListTitle = expandableListTitle;
            this.expandableListDetail = expandableListDetail;
        }

        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
            String[] listDetail = new String[2];
            TaskD[] taskDArray = expandableListDetail.get(this.expandableListTitle.get(listPosition));
            TaskD taskD = taskDArray[expandedListPosition];
            listDetail[0] = taskD.getTaskH().getAppl_no();
            listDetail[1] = Tool.separateThousand(taskD.getText_answer());
            return listDetail;
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final String[] expandedListText = (String[]) getChild(listPosition, expandedListPosition);
            if (null == convertView) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_recapitulation_detail, null);
            }

            TextView expandedListLabelView = (TextView) convertView.findViewById(R.id.itemLabel);
            TextView expandedListValueView = (TextView) convertView.findViewById(R.id.itemValue);

            expandedListLabelView.setText(expandedListText[0]);
            expandedListValueView.setText(expandedListText[1]);
            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .length;
        }

        @Override
        public Object getGroup(int listPosition) {
            return this.expandableListTitle.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return this.expandableListTitle.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(int listPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(listPosition);
            if (null == convertView) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group, null);
            }
            TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }
    }

    private double sumOfItems(List<TaskD> items) {
        double sum = 0;
        try {
            for (TaskD item : items) {
                String value = item.getText_answer();
                if(value==null || value.equals("")) value = "0";
                String tempAnswer = Tool.deleteAll(value, ",");
                String[] intAnswer = Tool.split(tempAnswer, ".");
                if(intAnswer.length>1){
                    if(intAnswer[1].equals("00"))
                        value = intAnswer[0];
                    else {
                        value=tempAnswer;
                    }
                }else{
                    value=tempAnswer;
                }
                double finalValue = Double.parseDouble(value);
                sum += finalValue;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return sum;
    }

    public class FormAdapter extends ArrayAdapter<Scheme> {
        private Context context;
        private List<Scheme> values;

        public FormAdapter(Context context, int resource, int textViewResourceId, List<Scheme> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context=context;
            this.values=objects;
        }

        public int getCount(){
            return values.size();
        }

        public Scheme getItem(int position){
            return values.get(position);
        }

        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView)view.findViewById(R.id.text_spin);
            label.setText("Form : "+values.get(position).getScheme_description());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView)view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getScheme_description());
            return label;
        }
    }

    public class BatchAdapter extends ArrayAdapter<TaskH> {
        private Context context;
        private List<TaskH> values;

        public BatchAdapter(Context context, int resource, int textViewResourceId, List<TaskH> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context=context;
            this.values=objects;
        }

        public int getCount(){
            return values.size();
        }

        public TaskH getItem(int position){
            return values.get(position);
        }

        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView)view.findViewById(R.id.text_spin);
            label.setText("Batch : "+values.get(position).getBatch_id());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView)view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getBatch_id());
            return label;
        }
    }

    public class UserAdapter extends ArrayAdapter<User> {
        private Context context;
        private List<User> values;

        public UserAdapter(Context context, int resource, int textViewResourceId, List<User> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context=context;
            this.values=objects;
        }

        public int getCount(){
            return values.size();
        }

        public User getItem(int position){
            return values.get(position);
        }

        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView)view.findViewById(R.id.text_spin);
            label.setText("User : "+values.get(position).getLogin_id());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= getActivity().getLayoutInflater();
            View view=inflater.inflate(R.layout.spinner_style, parent, false);
            TextView label = (TextView)view.findViewById(R.id.text_spin);
            label.setText(values.get(position).getLogin_id());
            return label;
        }
    }

    public class LookupAdapter extends ArrayAdapter<Lookup> {
        private Context context;
        private List<Lookup> values;

        public LookupAdapter(Context context, int textViewResourceId, List<Lookup> objects) {
            super(context, textViewResourceId, objects);
            this.context=context;
            this.values=objects;
        }

        public LookupAdapter(Context context, int resource, int textViewResourceId, List<Lookup> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context=context;
            this.values=objects;
        }

        public int getCount(){
            return values.size();
        }

        public Lookup getItem(int position){
            return values.get(position);
        }

        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            AutoTextViewHolder holder;
            LayoutInflater inflater= getActivity().getLayoutInflater();
            View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new AutoTextViewHolder(convertView);
            holder.bind(getItem(position));
            return convertView;
        }

    }
    class LookupArrayAdapter extends BaseAdapter implements Filterable {
        private static final int MAX_RESULTS = 10;
        private Context mContext;
        private List<Lookup> resultList = new ArrayList<>();

        public LookupArrayAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int position) {
            return resultList.get(position).toString();
        }

        public Lookup getLookupItem(int position) {
            return resultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AutoTextViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(com.adins.mss.base.R.layout.autotext_list, parent, false);
            }
            holder = new AutoTextViewHolder(convertView);
            holder.bind(getLookupItem(position));
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        List<Lookup> beanList = LookupDataAccess.getAllByLovGroupWithFilter(mContext, "BRANCH", constraint.toString(), MAX_RESULTS);
                        // Assign the data to the FilterResults
                        filterResults.values = beanList;
                        filterResults.count = beanList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        resultList = (List<Lookup>) results.values;
                        formLookup = resultList;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public class AutoTextViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public Lookup bean;
        public AutoTextViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(com.adins.mss.base.R.id.textauto);
        }
        public void bind(Lookup optBean){
            bean = optBean;
            textView.setText(bean.getValue());
        }
    }

    private void saveSelectedOptionToBean(OptionAnswerBean option) {

        List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<OptionAnswerBean>();
        if (option != null) {
            option.setSelected(true);
            selectedOptionAnswers.add(option);
            inputBranch.setText(option.toString());
//            bean.setLovCode(option.getCode());
//            bean.setLookupId(option.getUuid_lookup());
        }
//        else{
//            bean.setLovCode(null);
//            bean.setLookupId(null);
//        }
//        bean.setSelectedOptionAnswers(selectedOptionAnswers);
//        bean.setAnswer(option.toString());

    }

    @Override
    public void onResume() {
        super.onResume();
        if (listTaskH != null) {
            initiateRefresh();
        }
    }

    private void initiateRefresh() {
        cancelRefreshTask();
        backgroundTask = new RefreshBackgroundTask();
        backgroundTask.execute();
    }
    private void cancelRefreshTask(){
        if(backgroundTask!=null){
            backgroundTask.cancel(true);
            backgroundTask=null;
        }
    }

    private class RefreshBackgroundTask extends AsyncTask<Void, Void, List<TaskH>> {

        static final int TASK_DURATION = 2 * 1000; // 2 seconds

        @Override
        protected List<TaskH> doInBackground(Void... params) {
            // Sleep for a small amount of time to simulate a background-task
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            listTaskH.clear();
            listTaskH.addAll(toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_BATCH_ID, "BATCHID"));
            ToDoList.listOfSurveyStatus=null;
            List<SurveyHeaderBean> list = new ArrayList<SurveyHeaderBean>();
            for(TaskH h:listTaskH){
                list.add(new SurveyHeaderBean(h));
            }
            ToDoList.listOfSurveyStatus = list;

            // Return a new random list of cheeses
            return listTaskH;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(List<TaskH> result) {
            super.onPostExecute(result);

        }

    }

    public class SyncBankAccountTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        private Activity activity;
        public SyncBankAccountTask(Activity activity){
            this.activity=activity;
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.progressWait), true);
        }

        @Override
        protected String doInBackground(String... params) {
            String results = "";
            if(Tool.isInternetconnected(getActivity())){
                MssRequestType request = new MssRequestType();
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                request.addImeiAndroidIdToUnstructured();

                String url = GlobalData.getSharedGlobalData().getURL_GET_BANK_ACCOUNT_BRANCH();
                String json = GsonHelper.toJson(request);
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
                HttpConnectionResult serverResult = null;
                // Firebase Performance Trace Network Request
                HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(
                        url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, json);

                try {
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverResult);
                } catch (Exception e) {
                    if (Global.IS_DEV) {
                        e.printStackTrace();
                    }
                }

                try {
                    if (serverResult != null) {
                        results = serverResult.getResult();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            } else {
                results = getActivity().getString(R.string.no_internet_connection);
            }

            return results;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()){
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                }
            }
            if(Global.IS_DEV) {
                System.out.println(result);
            }
            if (getActivity().getString(R.string.no_internet_connection).equals(result)) {
                Toaster.warning(getActivity(), result);
            } else {
                try {
                    BankAccountOfBranchResponse bankAccountResponse = GsonHelper.fromJson(result, BankAccountOfBranchResponse.class);
                    if (bankAccountResponse.getListBankAccountOfBranch() != null && bankAccountResponse.getListBankAccountOfBranch().size() > 0) {
                        BankAccountOfBranchDataAccess.clean(getActivity());
                        List<BankAccountOfBranch> bankAccountList = new ArrayList<>();
                        for (int i = 0; i < bankAccountResponse.getListBankAccountOfBranch().size(); i++) {
                            BankAccountOfBranch bankAccount = bankAccountResponse.getListBankAccountOfBranch().get(i);
                            bankAccountList.add(bankAccount);
                        }
                        BankAccountOfBranchDataAccess.add(getActivity(), bankAccountList);
                    } else {
                        BankAccountOfBranchDataAccess.clean(getActivity());
                    }

                } catch (Exception e) {
                    if (Global.IS_DEV) {
                        e.printStackTrace();
                    }
                }
            }
            bankAccountList.clear();
            List<BankAccountOfBranch> resultList = BankAccountOfBranchDataAccess.getAll(getActivity());
            if (resultList != null && resultList.size() > 0) {
                mLookupEmpty.setVisibility(View.GONE);
                textBank.setVisibility(View.VISIBLE);
                spinnerBankAccount.setVisibility(View.VISIBLE);
                imgExpandBankAccount.setVisibility(View.VISIBLE);
                if (selectedLookupCode != null) {
                    bankAccountList.addAll(BankAccountOfBranchDataAccess.getOneByBranchCode(getActivity(), selectedLookupCode));
                    if (0 == bankAccountList.size()) {
                        selectedBank = null;
                        mLookupEmpty.setVisibility(View.VISIBLE);
                    } else {
                        mLookupEmpty.setVisibility(View.GONE);
                        spinnerBankAccount.setVisibility(View.VISIBLE);
                        imgExpandBankAccount.setVisibility(View.VISIBLE);
                        bankAccountAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                selectedBank = null;
                mLookupEmpty.setVisibility(View.VISIBLE);
                textBank.setVisibility(View.VISIBLE);
                spinnerBankAccount.setVisibility(View.GONE);
                imgExpandBankAccount.setVisibility(View.GONE);
            }
        }
    }

    public class SendDepositReportTask extends AsyncTask<String, Void, List<String>> {
        private ProgressDialog progressDialog;
        private Activity activity;
        private DepositReportH header;
        private ArrayList<DepositReportD> details;
        String namaKasir;
        private String errMsg;
        private StringBuilder sb;
        public SendDepositReportTask(Activity activity){
            this.activity=activity;
        }
        @Override
        protected void onPreExecute() {
            this.progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.progressSend), true);
        }
        @Override
        protected List<String> doInBackground(String... params) {
            List<String> results = new ArrayList<>();
            String result = "";
            if(Tool.isInternetconnected(getActivity())){
                sb = new StringBuilder();

                DepositReportRequest request = new DepositReportRequest();
                request.addImeiAndroidIdToUnstructured();
                header = new DepositReportH();
                header.setUuid_deposit_report_h(Tool.getUUID());
                header.setTransfered_date(Tool.getSystemDateTime());
                header.setDtm_crt(Tool.getSystemDateTime());
                header.setUuid_user(DepositReportACRecapitulateFragment.selectedDepositUser);
                header.setFlag(selectedDepositSchemeName);
                if (null != header.getUuid_user() && !"".equals(header.getUuid_user())) {
                    header.setUsr_crt(header.getUuid_user());
                } else {
                    header.setUsr_crt(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                }

                if (!isCash) {
                    if (null == selectedBank) {
                        sb.append(getActivity().getString(R.string.bankaccount_required));
                    } else {
                        header.setCashier_name(selectedBank);
                    }
                }
                if (params[0] != null) {
                    header.setBranch_payment(selectedLookupCode);
                } else {
                    sb.append(getActivity().getString(R.string.branchpayment_required));
                }
                if (null == image) {
                    sb.append(getActivity().getString(R.string.evidence_pc_required));
                } else {
                    try {
                        byteImage = Utils.bitmapToByte(tempImage);
                    } catch (Exception e) {
                        if (Global.IS_DEV) {
                            Logger.e(this, "Error get photo: " + e.getMessage());
                        }
                    }
                    if (null != byteImage) {
                        header.setImage(byteImage);
                    } else {
                        sb.append(getActivity().getString(R.string.evidence_pc_required));
                    }
                }
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                request.setReportHeader(header);

                if(sb.length()>0){
                    result = sb.toString();
                    results.add(0, result);
                }else{
                    details = new ArrayList<DepositReportD>();
//                    List<TaskD> tasks = TaskDDataAccess.getTaskDTagTotalbyForm(getActivity(), DepositReportRecapitulateFragment.selectedDepositScheme);
                    List<TaskD> tasks = TaskDDataAccess.getTaskDTagTotalbyBatchId(getActivity(),selectedBatchId);
                    List<TaskD> reportsReconcile = new ArrayList<TaskD>();

                    for (TaskD taskD : tasks) {
                        TaskH taskH = TaskHDataAccess.getOneHeader(getActivity(), taskD.getUuid_task_h());
                        if (taskH.getIs_reconciled().equals("0")) {
                            reportsReconcile.add(taskD);
                        }
                    }
                    for (TaskD task : reportsReconcile) {
                        DepositReportD detail = new DepositReportD();
                        detail.setUuid_task_h(task.getTaskH().getUuid_task_h());
                        detail.setDtm_crt(Tool.getSystemDateTime());
//                        detail.setUsr_crt(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                        detail.setUsr_crt(DepositReportACRecapitulateFragment.selectedDepositUser);
                        detail.setUuid_deposit_report_d(Tool.getUUID());
                        String value = task.getText_answer();

                        if (value == null || value.equals(""))
                            value = "0";
                        String tempAnswer = Tool.deleteAll(value, ",");
                        String[] intAnswer = Tool.split(tempAnswer, ".");
                        if (intAnswer.length > 1) {
                            if (intAnswer[1].equals("00"))
                                value = intAnswer[0];
                            else {
                                value = tempAnswer;
                            }
                        } else {
                            value = tempAnswer;
                        }

                        detail.setDeposit_amt(value);

                        detail.setUuid_deposit_report_h(header.getUuid_deposit_report_h());
                        details.add(detail);
                    }

                    request.setListReportDetail(details);

                    String url = GlobalData.getSharedGlobalData().getURL_SENDDEPOSITREPORTAC();
                    String json = GsonHelper.toJson(request);
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
                    HttpConnectionResult serverResult = null;
                    // Firebase Performance Trace Network Request
                    HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(
                            url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                    try {
                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetric, serverResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                        errMsg = e.getMessage();
                    }

                    MssRequestType cohRequest = new MssRequestType();
                    cohRequest.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    cohRequest.addImeiAndroidIdToUnstructured();
                    String urlCoh = GlobalData.getSharedGlobalData().getURL_UPDATE_CASH_ON_HAND();
                    String jsonCoh = GsonHelper.toJson(cohRequest);
                    HttpConnectionResult serverResultCoh = null;
                    // Firebase Performance Trace Network Request
                    HttpMetric networkMetricCoh = FirebasePerformance.getInstance().newHttpMetric(
                            url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetricCoh, jsonCoh);

                    try {
                        serverResultCoh = httpConn.requestToServer(urlCoh, jsonCoh, Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetricCoh, serverResultCoh);
                    } catch (Exception e) {
                        e.printStackTrace();
                        errMsg = e.getMessage();
                    }

                    try {
                        if (serverResult != null) {
                            result=serverResult.getResult();
                        }
                        results.add(0, result);
                        String resultCoh = null;
                        if (serverResultCoh != null) {
                            resultCoh = serverResultCoh.getResult();
                        }
                        results.add(1, resultCoh);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }else{
                result = getActivity().getString(R.string.no_internet_connection);
                results.add(0, result);
            }


            return results;
        }
        @Override
        protected void onPostExecute(List<String> results) {
            boolean error = false;
            if (progressDialog.isShowing()){
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                }
            }
            if(Global.IS_DEV)
                System.out.println(results);
            if(errMsg!=null){
                final NiftyDialogBuilder dialog= NiftyDialogBuilder.getInstance(getActivity());
                dialog.withTitle(getActivity().getString(R.string.error_capital)).withMessage(this.errMsg).
                        withButton1Text("OK").
                        setButton1Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View paramView) {
                                dialog.dismiss();
                                CustomerFragment.doBack(getActivity());
                            }
                        }).
                        isCancelable(false).show();
                error = true;
            }else{
                if(getActivity().getString(R.string.no_internet_connection).equals(results.get(0))){
                    Toaster.warning(getActivity(), results.get(0));
                    error = true;
                }else{
                    if(sb!=null && sb.length()>0){
                        Toaster.warning(getActivity(), results.get(0));
                        error = true;
                    }else{
                        try{
                            DepositReportResponse responseType = GsonHelper.fromJson(results.get(0), DepositReportResponse.class);
                            if(responseType.getStatus().getCode()==0){
                                header.setBatch_id(responseType.getBatchId());
                                header.setFlag(selectedDepositSchemeName);
                                header.setBranch_payment(selectedLookupCode);
                                header.setBank_account(selectbankAccount);
                                header.setBank_name(selectbankName);
                                DepositReportHDataAccess.add(getActivity(), header);
                                for(DepositReportD reportD : details){
                                    reportD.setIs_sent(Global.TRUE_STRING);
                                    reportD.setDepositReportH(header);
                                    DepositReportDDataAccess.add(getActivity(), reportD);
                                }
                                generatePrintResultDepReport(getActivity(), namaKasir, header);

                                if(results.size()==2){
                                    try {
                                        CashOnHandResponse cashOnHandResponse = GsonHelper.fromJson(results.get(1), CashOnHandResponse.class);
                                        if (cashOnHandResponse.getStatus().getCode() == 0) {
                                            User user = GlobalData.getSharedGlobalData().getUser();
                                            user.setCash_on_hand(cashOnHandResponse.getCashOnHand());
                                            GlobalData.getSharedGlobalData().getUser().setCash_on_hand(
                                                    cashOnHandResponse.getCashOnHand()
                                            );
                                            UserDataAccess.addOrReplace(getActivity(), user);
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        error = true;
                                    }
                                }

                                final NiftyDialogBuilder dialog= NiftyDialogBuilder.getInstance(getActivity());
                                dialog.withTitle(getActivity().getString(R.string.success)).
                                        withMessage(getActivity().getString(R.string.success_deposit)).
                                        withButton1Text("OK").
                                        setButton1Click(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View paramView) {
                                                dialog.dismiss();
                                                CustomerFragment.doBack(getActivity());
                                                CustomerFragment.doBack(getActivity());
                                                DepositReportACDetailActivity.report = header;
                                                Intent intent = new Intent(getActivity(), DepositReportACDetailActivity.class);
                                                startActivity(intent);
                                            }
                                        }).
                                        isCancelable(false).show();
                            }else{
                                final NiftyDialogBuilder dialog= NiftyDialogBuilder.getInstance(getActivity());
                                dialog.withTitle(getActivity().getString(R.string.error_capital)).withMessage(responseType.getStatus().getMessage()).
                                        withButton1Text("OK").
                                        setButton1Click(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View paramView) {
                                                dialog.dismiss();
                                                CustomerFragment.doBack(getActivity());
                                            }
                                        }).
                                        isCancelable(false).show();
                            }
                        }
                        catch (Exception e2){
                            Toaster.warning(getActivity(), results.get(0));
                            error = true;
                        }
                    }
                }
            }
            if(error) {
                transferButton.setClickable(true);
                buttonSelectPhoto.setClickable(true);
            }
        }
    }

    public void generatePrintResultDepReport(Activity activity, String cashierName, DepositReportH report){
        List<PrintItem> printItemList = PrintItemDataAccess.getAll(activity, "DUMYUUIDSCHEMEFORDEPOSITREPORT");
        List<PrintResult> printResultList = new ArrayList<PrintResult>();

        //delete dulu yang ada di database, karena generate printResult dengan jawaban yang baru
        List<PrintResult> printResultByTaskH = PrintResultDataAccess.getAll(activity, report.getBatch_id());
        if(printResultByTaskH.size()>0){
            PrintResultDataAccess.delete(activity, report.getBatch_id());
        }
        PrintResult PRtransferBy = new PrintResult(Tool.getUUID());
        PRtransferBy.setPrint_type_id(Global.PRINT_ANSWER);
        PRtransferBy.setUser(GlobalData.getSharedGlobalData().getUser());
        PRtransferBy.setUuid_task_h(report.getBatch_id());
        for (PrintItem bean : printItemList) {
            PrintResult printResult = new PrintResult(Tool.getUUID());
            printResult.setPrint_type_id(bean.getPrint_type_id());
            printResult.setUser(GlobalData.getSharedGlobalData().getUser());
            printResult.setUuid_task_h(report.getBatch_id());

            if (bean.getPrint_type_id().equals(Global.PRINT_ANSWER)) {
                String label = bean.getPrint_item_label();
                if (label.equals("Batch ID")) {
                    printResult.setLabel(label);
                    printResult.setValue(report.getBatch_id());
                } else if (label.equals("Transfer By")) {
                    printResult.setLabel(label);
                    if (cashierName != null && cashierName.length() > 0) {
                        printResult.setValue("Cashier");
                        PRtransferBy.setLabel(label);
                        PRtransferBy.setValue("Cashier");
                    } else {
                        printResult.setValue("Bank");
                        PRtransferBy.setLabel(label);
                        PRtransferBy.setValue("Bank");
                    }
                } else if (label.equals("Cashier Name")) {
                    if (PRtransferBy.getValue().equals("Cashier")) {
                        printResult.setLabel(label);
                        printResult.setValue(report.getCashier_name());
                    }
                } else if (label.equals("Account No")) {
                    if (PRtransferBy.getValue().equals("Bank")) {
                        printResult.setLabel(label);
                        printResult.setValue(report.getBank_account());
                    }
                } else if (label.equals("Bank Name")) {
                    if (PRtransferBy.getValue().equals("Bank")) {
                        printResult.setLabel(label);
                        printResult.setValue(report.getBank_name());
                    }
                } else if (label.contains("Agreement No")) {
                    int no = Integer.valueOf(label.replace("Agreement No", ""));
                    printResult.setLabel("Agreement No");
                    List<DepositReportD> reportDs = report
                            .getDepositReportDList();
                    try {
                        TaskH taskHs = TaskHDataAccess.getOneHeader(activity,
                                reportDs.get(no).getUuid_task_h());
                        String agreement_no = "";
                        if (taskHs != null)
                            agreement_no = taskHs.getAppl_no();
                        if (agreement_no == null)
                            agreement_no = "-";
                        printResult.setValue(agreement_no);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                } else if (label.contains("Deposit Amount")) {
                    int no = Integer.valueOf(label
                            .replace("Deposit Amount", ""));
                    printResult.setLabel("Deposit Amt");
                    List<DepositReportD> reportDs = report
                            .getDepositReportDList();
                    try {
                        printResult.setValue(Tool.separateThousand(reportDs
                                .get(no).getDeposit_amt()));
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                } else if (label.equals("Total")) {
                    printResult.setLabel(label);
                    printResult.setValue(String.valueOf(Tool
                            .separateThousand(total)));
                }
            } else if (bean.getPrint_type_id().equals(
                    Global.PRINT_BRANCH_ADDRESS)) {
                printResult.setLabel(GlobalData.getSharedGlobalData().getUser()
                        .getBranch_address());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_BRANCH_NAME)) {
                printResult.setLabel(GlobalData.getSharedGlobalData().getUser()
                        .getBranch_name());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_BT_ID)) {
                String btAddr = "?";
                try {
                    btAddr = BluetoothAdapter.getDefaultAdapter().getAddress();
                } catch (Exception e) {

                }
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue(btAddr);
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LABEL)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LABEL_BOLD)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id()
                    .equals(Global.PRINT_LABEL_CENTER)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(
                    Global.PRINT_LABEL_CENTER_BOLD)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LOGO)) {
                printResult.setLabel("");
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_NEW_LINE)) {
                String label = bean.getPrint_item_label();
                int no = Integer.valueOf(label.replace("New Line", ""));
                List<DepositReportD> reportDs = report.getDepositReportDList();
                int size = reportDs.size();
                if (no < size) {
                    printResult.setLabel("------------------------------");
                    printResult.setValue("\n");
                }
                if (no == 999) {
                    printResult.setLabel("==============================");
                    printResult.setValue("\n");
                }
                if (no == 998) {
                    printResult.setLabel("\n");
                    printResult.setValue("\n");
                }
            } else if (bean.getPrint_type_id().equals(Global.PRINT_TIMESTAMP)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_USER_NAME)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue(GlobalData.getSharedGlobalData().getUser()
                        .getFullname());
            }
            if (printResult.getLabel() != null) {
                PrintResultDataAccess.add(activity, printResult);
            }
        }
    }
    public static void hideKeyboardFrom(Context context, View view) {
        if (null!=view){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }
}