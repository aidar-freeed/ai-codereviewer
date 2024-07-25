package com.adins.mss.base.todolist.form;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AllHeaderViewerActivity extends Activity {
    public static final String BUND_KEY_REQ = "BUND_KEY_REQ";
    public static final String REQ_PRIORITY_LIST = "REQ_PRIORITY_LIST";
    public static final String REQ_LOG_LIST = "REQ_LOG_LIST";
    public static final String REQ_STATUS_LIST = "REQ_STATUS_LIST";
    private ListView listHeader;
    private TaskHeaderAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_header_viewer_layout);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        initialize();
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
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void initialize() {
        listHeader = (ListView) findViewById(android.R.id.list);
        List<TaskH> listTaskH = null;
        String key_req = getIntent().getExtras().getString(BUND_KEY_REQ);
        if (REQ_PRIORITY_LIST.equals(key_req))
            listTaskH = ToDoList.getListTaskInPriority(getApplicationContext(), 0, null);
        else if (REQ_STATUS_LIST.equals(key_req))
            listTaskH = TaskHDataAccess.getAllTaskInStatus(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
        else if (REQ_LOG_LIST.equals(key_req))
            listTaskH = TaskHDataAccess.getAllSentTask(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
        else
            listTaskH = new ArrayList<>();

        listAdapter = new TaskHeaderAdapter(getApplicationContext(), listTaskH);

        listHeader.setAdapter(listAdapter);
    }


    public class TaskHeaderAdapter extends ArrayAdapter<TaskH> {
        private Context mContext;
        private List<TaskH> listTaskH;

        public TaskHeaderAdapter(Context c, List<TaskH> listTaskH) {
            super(c, R.layout.all_header_viewer_item, listTaskH);
            mContext = c;
            this.listTaskH = listTaskH;
        }

        @Override
        public int getCount() {
            return listTaskH.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v;
            v = inflater.inflate(R.layout.all_header_viewer_item, null);
            TextView txtPriority = (TextView) v.findViewById(R.id.txtPriority);
            TextView txtCustName = (TextView) v.findViewById(R.id.txtCustName);
            TextView txtCustAddress = (TextView) v.findViewById(R.id.txtCustAddress);
            TextView txtCustPhone = (TextView) v.findViewById(R.id.txtCustPhone);
            TextView txtNotes = (TextView) v.findViewById(R.id.txtNotes);
            TaskH taskH = listTaskH.get(position);

            txtPriority.setText(taskH.getPriority());
            txtCustName.setText(taskH.getCustomer_name());
            txtCustAddress.setText(taskH.getCustomer_address());
            txtCustPhone.setText(taskH.getCustomer_phone());
            txtNotes.setText(taskH.getNotes());

            return v;
        }
    }
}
