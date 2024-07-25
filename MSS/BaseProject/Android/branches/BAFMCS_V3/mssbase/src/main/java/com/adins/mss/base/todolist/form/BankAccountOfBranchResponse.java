package com.adins.mss.base.todolist.form;

import com.adins.mss.dao.BankAccountOfBranch;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gigin.ginanjar on 29/09/2016.
 */

public class BankAccountOfBranchResponse extends MssResponseType {
    @SerializedName("listBankAccountOfBranch")
    private List<BankAccountOfBranch> listBankAccountOfBranch;

    public List<BankAccountOfBranch> getListBankAccountOfBranch() {
        return listBankAccountOfBranch;
    }

    public void setListBankAccountOfBranch(List<BankAccountOfBranch> listBankAccountOfBranch) {
        this.listBankAccountOfBranch = listBankAccountOfBranch;
    }
}
