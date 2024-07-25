package com.adins.mss.base.timeline.comment;

import android.content.Context;

import com.adins.mss.dao.Comment;
import com.adins.mss.foundation.db.dataaccess.CommentDataAccess;
import com.adins.mss.foundation.formatter.Tool;

import java.util.Date;
import java.util.List;

public class CommentManager {
    private Context mContext;

    public CommentManager(Context context) {
        this.mContext = context;
    }

    public List<Comment> getAllComment(String uuid_timeline) {
        return CommentDataAccess.getAll(mContext, uuid_timeline);
    }

    public void insertComment(List<Comment> commentList) {
        CommentDataAccess.add(mContext, commentList);
    }

    public void insertComment(Comment comment) {
        CommentDataAccess.add(mContext, comment);
    }

    public void insertComment(String content_comment, String sender_id, String sender_name, String usr_crt, String usr_upd, String uuid_timeline) {
        String uuid_comment = Tool.getUUID();
        Date dtm_crt_server = null;
        Date dtm_crt = new Date(System.currentTimeMillis());
        java.util.Date dtm_upd = new Date(System.currentTimeMillis());
        Comment comment = new Comment(uuid_comment, content_comment, dtm_crt_server, sender_id, sender_name, usr_crt, dtm_crt, usr_upd, dtm_upd, uuid_timeline);
        CommentDataAccess.add(mContext, comment);
    }

    public void deleteComment(Comment comment) {
        CommentDataAccess.delete(mContext, comment);
    }

    public void deleteComment(String uuid_timeline) {
        CommentDataAccess.delete(mContext, uuid_timeline);
    }

    public void deleteAllComment() {
        CommentDataAccess.clean(mContext);
    }

    public void updateComment(Comment comment) {
        CommentDataAccess.update(mContext, comment);
    }
}
