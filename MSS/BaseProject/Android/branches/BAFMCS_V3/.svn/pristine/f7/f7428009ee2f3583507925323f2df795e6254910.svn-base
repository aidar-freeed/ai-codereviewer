package com.adins.mss.base.timeline.comment.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.timeline.Constants;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.timeline.comment.CommentArrayAdapter;
import com.adins.mss.base.timeline.comment.CommentManager;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Comment;
import com.adins.mss.dao.Timeline;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.androidquery.AQuery;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentActivity extends Activity {
    private String key_timeline;
    private List<Comment> comments;
    private Timeline content_timeline;
    private CommentArrayAdapter adapter;
    private AQuery query;
    private CommentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);
        Intent intent = getIntent();
        Bundle extra = intent.getExtras();
        key_timeline = extra.getString(Constants.KEY_TIMELINE);

        manager = new CommentManager(this);
        comments = manager.getAllComment(key_timeline);
        try {
            if (comments.isEmpty())
                comments = null;
        } catch (Exception e) {
            FireCrash.log(e);
        }
        adapter = new CommentArrayAdapter(this, comments);

        query = new AQuery(this);

        String uuidUser = "";
        try {
            uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        } catch (Exception e) {
            FireCrash.log(e);
            uuidUser = "user01";
        }

        content_timeline = TimelineManager.getTimeline(this, uuidUser, key_timeline);
        Date dtm_crt = content_timeline.getDtm_crt();
        String str_dtm_crt = Formatter.formatDate(dtm_crt, Global.DATE_TIMESEC_TIMELINE_FORMAT);
        byte[] byte_image = content_timeline.getByte_image();
        Bitmap bm = null;
        if (byte_image != null) {
            try {
                bm = BitmapFactory.decodeByteArray(byte_image, 0, byte_image.length);
            } catch (Exception e) {
                FireCrash.log(e);
            }

            if (bm == null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(byte_image, 0, byte_image.length, options);

                // Calculate inSampleSize
                options.inSampleSize = 64;
                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeByteArray(byte_image, 0, byte_image.length, options);
            }
            query.id(R.id.timelineImageC).image(bm);
        } else {
            query.id(R.id.timelineImageC).visibility(View.GONE);
        }
        query.id(R.id.txtTitleC).text(content_timeline.getTimelineType().getTimeline_type());
        query.id(R.id.txtDescC).text(content_timeline.getDescription());
        query.id(R.id.txtTimeC).text(str_dtm_crt);
        query.id(R.id.commentList).adapter(adapter).setSelection(adapter.getCount() - 1);
        query.id(R.id.btnSendComment).clicked(this, "sendComment");
        if (comments != null)
            query.id(R.id.jmlComments).text(adapter.getCount() + " Comments");
        else
            query.id(R.id.jmlComments).text(adapter.getCount() - 1 + " Comments");

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

    public void sendComment(View view) {
        String comment_desc = query.id(R.id.editComment).getText().toString();
        if (comment_desc.equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.comment_cannot_be_empty),
                    Toast.LENGTH_LONG).show();
        } else {
            query.id(R.id.editComment).text("");
            manager.insertComment(comment_desc, Tool.getUUID(), "Gigin", "Gigin", "Gigin", key_timeline);
            comments = manager.getAllComment(key_timeline);
            adapter = new CommentArrayAdapter(this, comments);
            query.id(R.id.commentList).adapter(adapter).setSelection(adapter.getCount() - 1);
            query.id(R.id.jmlComments).text(adapter.getCount() + " Comments");
        }
    }
}
