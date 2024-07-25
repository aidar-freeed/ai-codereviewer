package com.adins.mss.coll.commons;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;

/**
 * Created by Aditya Purwa on 3/5/2015.
 */
public class Generator {
    public static String generateBatchId(Context context) {

        java.util.Date now = Tool.getSystemDateTime();
        String loginId= GlobalData.getSharedGlobalData().getUser().getLogin_id();
        int idxOfOpenBrace = loginId.indexOf('@');
		if(idxOfOpenBrace!=-1){
			loginId = loginId.substring(0,idxOfOpenBrace);			
		}
        return
        		loginId +
                        Formatter.formatDate(now, Global.DATE_STR_FORMAT4)+
                        PersistentCounter.getAndIncrement(context, "batchId");

    }
}
