package com.adins.mss.base.timeline.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Comment;
import com.adins.mss.foundation.formatter.Formatter;
import com.androidquery.AQuery;

import java.util.Date;
import java.util.List;

public class CommentArrayAdapter extends ArrayAdapter<Comment> {
    private Context mContext;
    private List<Comment> comments;
    private AQuery query;

    /**
     * Inisialisasi Comment Array Adapter
     *
     * @param context  - Context
     * @param comments - List of Comment
     */
    public CommentArrayAdapter(Context context, List<Comment> comments) {
        super(context, R.layout.comment_item_layout, comments);
        this.mContext = context;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        int count = 0;
        try {
            count = comments.size();
        } catch (Exception e) {
            FireCrash.log(e);
            count = 1;
        }
        return count;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item_layout, parent, false);
        } else {

        }
        query = new AQuery(convertView);
        if (comments == null) {
            getLayoutIfDataNotFound();
        } else {
            Comment comment = comments.get(position);
            String usr_crt = comment.getUsr_crt();
            String content = comment.getComment();
            Date dtm_crt = comment.getDtm_crt();
            String time = Formatter.formatDate(dtm_crt, Global.DATE_TIMESEC_TIMELINE_FORMAT);

            query.id(R.id.txt_usrCrt).text(usr_crt).visible();
            query.id(R.id.contentComment).text(content);
            query.id(R.id.txt_timeCrt).text(time).visible();
        }

        return convertView;
    }

    public void getLayoutIfDataNotFound() {
        query.id(R.id.txt_usrCrt).visibility(View.GONE);
        query.id(R.id.txt_timeCrt).visibility(View.GONE);
        query.id(R.id.contentComment).text("No Comment");
    }
}
