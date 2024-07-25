package com.adins.mss.odr;

import android.content.Context;

import com.adins.mss.base.dialogfragments.NewTaskDialog;
import com.adins.mss.base.todo.form.NewTaskAdapter;
import com.adins.mss.base.todolist.DoList;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;

import java.util.List;

public class MONewTaskActivity extends NewTaskDialog {
    private DoList list;
    private List<Scheme> objects;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    protected NewTaskAdapter getNewTaskAdapter() {
        list = new DoList(getActivity());
        if (Global.isNewlead) {
            objects = list.getMarketingListScheme();
        } else {
            objects = list.getOrderListScheme();
        }

        return new NewTaskAdapter(getActivity(), objects);
    }
}
