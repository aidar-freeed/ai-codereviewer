package com.adins.mss.base.ktpValidation;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;

import java.io.IOException;

/**
 * Created by riska.yessivirna on 8/12/2019.
 */

public class DukcapilApi {
    private final Context context;

    public DukcapilApi(Context context) {
        this.context = context;
    }

    public String request(String nomorKtp, String refId) throws IOException {
        JsonRequestKtpValidation request = new JsonRequestKtpValidation();
        request.setFilter(nomorKtp);
        request.setRefId(refId);
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

        String requestJson = GsonHelper.toJson(request);
        String url = GlobalData.getSharedGlobalData().getURL_DUKCAPIL_VALIDATION();

        if(null==url){
            MainMenuActivity.InitializeGlobalDataIfError(context);
            url = GlobalData.getSharedGlobalData().getURL_DUKCAPIL_VALIDATION();
        }

        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult serverResult = null;
        try {
            serverResult = httpConn.requestToServer(url, requestJson, Global.SORTCONNECTIONTIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String responseJson ="" ;
//        responseJson ="{\"result\":\"1\",\"message\":\"Data is valid!\",\"custName\":\"KELVIN WINGKIE HANDRIANTO\",\"custAddr\":\"JL TANAH TINGGI IV 53\",\"mapValue\":{\"PDDK_AKH\":\"TG\",\"KAB_NAME\":\"WIL KOTA JAKARTA PUSAT\",\"PROP_NAME\":\"Prop DKI Jakarta\",\"NAMA_LGKP\":\"KELVIN WINGKIE HANDRIANTO\",\"KEC_NAME\":\"Kec Johar Baru\",\"AGAMA\":\"03\",\"JENIS_PKRJN\":\"PELAJAR/MAHASISWA\",\"STATUS_KAWIN\":\"S\",\"TGL_LHR\":\"21041995000000\",\"TMPT_LHR_NAME\":\"JAKARTA\",\"NAMA_LGKP_IBU\":\"SUTIJANA\",\"NO_KEL\":\"3171040003\",\"KEL_NAME\":\"Kel Tanah Tinggi\",\"NO_KK\":\"3171081301091574\",\"NO_RT\":\"18\",\"NIK\":\"3171082104950002\",\"NO_KAB\":\"3171\",\"ALAMAT\":\"JL TANAH TINGGI IV 53\",\"JENIS_KLMIN\":\"M\",\"NO_RW\":\"5\",\"AGAMA_DESC\":\"KATHOLIK\",\"NO_PROP\":\"31\",\"NO_KEC\":\"3171040\",\"NO_TMPT_LHR\":\"3171\",\"IS_SUCCESS\":\"1\"},\"status\":{\"code\":0}}" ;
        if(serverResult!=null && serverResult.isOK()){
            try {
                responseJson = serverResult.getResult();
            } catch (Exception e) {
                return null;
            }
        }
        return responseJson;
    }
}
