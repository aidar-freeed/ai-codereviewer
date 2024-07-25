package com.adins.mss.odr.news;

import android.app.Activity;
import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.dao.MobileContentD;
import com.adins.mss.dao.MobileContentH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.MobileContentDDataAccess;
import com.adins.mss.foundation.db.dataaccess.MobileContentHDataAccess;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.KeyValue;
import com.adins.mss.foundation.http.MssRequestType;

import java.util.List;

public class News {
	private static Context context;
	private static Activity activity;
	private static List<MobileContentH> listHeaderSVR;
	private static List<MobileContentH> listNewsChildSVR;
	private static List<MobileContentD> listContentSVR;
	
	private static List<MobileContentH> listNewsParent;
	private static List<MobileContentH> listNewsChild;
	private static MobileContentH contentH;
	private static List<MobileContentD> listContent;
//    private static String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
    int i = 1;
	
//	public void getNewsHeaderFromServer() {
//		try {
//			listHeaderSVR = getlistNewsParentFromServer();
//			if(listHeaderSVR!=null || listHeaderSVR.size()>0){
//				
//				for(MobileContentH contentP : listHeaderSVR){
//					contentP.setUser(user);
//					if(contentP.getUuid_parent_content()==null||
//							contentP.getUuid_parent_content().equals("")){
//						
//					}
//					MobileContentHDataAccess.add(context, contentP);
//				}
//			}
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//	}

    public News(Activity activity) {
        News.context = activity;
		News.activity=activity;
    }

    public static long getCounterNews(Context context) {
        long counter = 0;
        try {
            counter = MobileContentHDataAccess.getParentCounter(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
//			MOMainMenuActivity.mnNews.setCounter(String.valueOf(listNewsParent.size()));
        } catch (Exception e) {
        }
        return counter;
    }

    public void getNewsContentFromServer(MobileContentH contentH) {
		try {
			String uuid_mobile_content_h = contentH.getUuid_mobile_content_h();
			listContent = getListContentFromServer(uuid_mobile_content_h);
			if(listContent!=null || listContent.size()>0){
				for(MobileContentD contentD : listContent){
					contentD.setMobileContentH(contentH);
					MobileContentDDataAccess.add(context, contentD);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<MobileContentH> getlistNewsParent() {
		listNewsParent = MobileContentHDataAccess.getAllParent(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
		return listNewsParent;
	}
	
	public List<MobileContentH> getAllNews() {
		List<MobileContentH>  listNewsParent = MobileContentHDataAccess.getAll(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
		return listNewsParent;
	}

	public List<MobileContentH> getlistNewsChild(String Uuid_mobile_content_h_Parent) {
		listNewsChild = MobileContentHDataAccess.getAll(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Uuid_mobile_content_h_Parent);
		return listNewsChild;
	}

	public List<MobileContentH> getlistNewsChildWithoutDate(String Uuid_mobile_content_h_Parent) {
		listNewsChild = MobileContentHDataAccess.getAllWithoutDate(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Uuid_mobile_content_h_Parent);
		return listNewsChild;
	}
	
	public MobileContentH getContent(String Uuid_mobile_content_h_Parent) {
		contentH = MobileContentHDataAccess.getOne(context, Uuid_mobile_content_h_Parent);
		return contentH;
	}

    public List<MobileContentD> getlistContent(String Uuid_mobile_content_h_Child) {
		listContent = MobileContentDDataAccess.getAll(context, Uuid_mobile_content_h_Child);
		return listContent;
	}

    public List<MobileContentD> getlistContentOnDate(String Uuid_mobile_content_h_Child) {
		listContent = MobileContentDDataAccess.getAllOnDate(context, Uuid_mobile_content_h_Child);
		return listContent;
	}
    
    public List<MobileContentD> getlistContentWithoutDate(String Uuid_mobile_content_h_Child) {
		listContent = MobileContentDDataAccess.getAll(context, Uuid_mobile_content_h_Child);
		return listContent;
	}
    
	public List<MobileContentD> getListContentFromServer(
            String uuid_mobile_content_h) {
        JsonRequestNews requestType = new JsonRequestNews();
				requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
				requestType.addImeiAndroidIdToUnstructured();
				requestType.setuuid_mobile_content_h(uuid_mobile_content_h);

        String data = GsonHelper.toJson(requestType);

		boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
		boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
		HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
//				HttpConnectionResult result = null;
				String result = null;
        JsonResponseNewsContent content = GsonHelper.fromJson(result, JsonResponseNewsContent.class);
        		listContent = content.getListMobileContentD();

        return listContent;
	}

//	private List<MobileContentH> getlistNewsParentFromServer() {
//		JsonRequestNews requestType = new JsonRequestNews();
//		requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
//		requestType.addItemToUnstructured(imeIvalue, false);
////		requestType.setListContentHeader(getlistNewsParent());
//		Gson gson = new GsonBuilder().setDateFormat("ddMMyyyyHHmmss").create();
//
//		String json = gson.toJson(requestType);
//		String url = "Global.URL_GET_NEWS";
//		
//		HttpCryptedConnection httpConn = new HttpCryptedConnection(Global.ENCRYPT_COMM, Global.DECRYPT_COMM);
//		
//		HttpConnectionResult result = null;
//		try {
//			result = httpConn.requestHTTPPost(url, json);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		String data = result.getResult();
//		data ="{\"listHeader\":[{\"content_description\":\"Lorem ipsum dolor sit amet.\",\"content_name\":\"YAMAHA\",\"effective_date\":\"25022015101314\",\"uuid_mobile_content_h\":\"c0db330c-47eb-4f1b-bfd8-139d27749789\"},{\"content_description\":\"Lorem ipsum dolor sit amet.\",\"content_name\":\"TOYOTA\",\"effective_date\":\"25022015101314\",\"uuid_mobile_content_h\":\"0bd0210a-91f1-40a3-b2f4-347229fb702a\"},{\"content_description\":\"Lorem ipsum dolor sit amet.\",\"content_name\":\"YAMAHA MX\",\"effective_date\":\"25022015101314\",\"uuid_mobile_content_h\":\"2e635f95-2f8e-4681-8393-bd086426c92a\",\"uuid_parent_content\":\"c0db330c-47eb-4f1b-bfd8-139d27749789\"},{\"content_description\":\"Lorem ipsum dolor sit amet.\",\"content_name\":\"YAMAHA R1000\",\"effective_date\":\"25022015101314\",\"uuid_mobile_content_h\":\"6777a562-6efe-4ebf-b16c-0efd7e280e03\",\"uuid_parent_content\":\"c0db330c-47eb-4f1b-bfd8-139d27749789\"}],\"status\":{\"code\":\"1\"}}";
//		try {
////			result = httpConn.requestHTTPPost(Global.URL_GET_CONTENTNEWS, data);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		JsonResponseNewsHeader news = gson.fromJson(data, JsonResponseNewsHeader.class);
//		listNewsParent = news.getListHeader();
////		MobileContentHDataAccess.add(context, mobileContentHList);
//		return listNewsParent;
//	}

    private List<MobileContentH> getlistNewsChildFromServer(
            String uuid_mobile_content_h) {
        // TODO Bikin cara buat dapetin dari server
//		MobileContentHDataAccess.add(context, mobileContentHList);

        return listNewsChild;
    }
}
