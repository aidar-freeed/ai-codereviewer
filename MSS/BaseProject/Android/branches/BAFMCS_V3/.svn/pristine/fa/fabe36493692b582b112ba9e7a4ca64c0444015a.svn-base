package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;
import android.database.Cursor;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.foundation.db.DaoOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TableDataAccess {

    public static JSONArray getDataByQuery(Context context, String query) {
        DaoSession daoSession = DaoOpenHelper.getDaoSession(context);
        String querySelect = null;
        Cursor cursor;

        String[] queryStatement = query.split(";");
        if (queryStatement.length > 0) {
            for (String statement : queryStatement) {
                if (statement.contains("INSERT") || statement.contains("UPDATE") || statement.contains("DELETE")) {
                    daoSession.getDatabase().execSQL(statement);
                    JSONObject data = new JSONObject();
                    try {
                        data.put("Message", "SUCCESS EXECUTE QUERY");
                        JSONArray dataAll = new JSONArray();
                        dataAll.put(data);
                        return dataAll;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (statement.contains("SELECT")) {
                    querySelect = statement;
                }
            }
        } else {
            if (query.contains("INSERT") || query.contains("UPDATE") || query.contains("DELETE")) {
                daoSession.getDatabase().execSQL(query);
            } else if (query.contains("SELECT")) {
                querySelect = query;
            }
        }

        if (null != querySelect) {
            cursor = daoSession.getDatabase().rawQuery(querySelect, null);

            JSONArray dataAll = new JSONArray();
            try {
                if (cursor.moveToFirst()) {
                    int totalColumn = cursor.getColumnCount();
                    do {
                        JSONObject data = new JSONObject();
                        for (int i = 0; i < totalColumn; i++) {
                            if (null != cursor.getColumnName(i)) {
                                if (1 == cursor.getType(i)) {
                                    data.put(cursor.getColumnName(i), cursor.getInt(i));
                                } else if (2 == cursor.getType(i)) {
                                    data.put(cursor.getColumnName(i), cursor.getFloat(i));
                                } else if (3 == cursor.getType(i)) {
                                    data.put(cursor.getColumnName(i), cursor.getString(i));
                                } else if (4 == cursor.getType(i)) {
                                    data.put(cursor.getColumnName(i), cursor.getBlob(i));
                                } else {
                                    data.put(cursor.getColumnName(i), "");
                                }
                            }
                        }
                        dataAll.put(data);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
            return dataAll;
        }
        return null;
    }

}
