package com.adins.mss.base.dynamicform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.audio.AudioRecord;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;

import java.util.Locale;

public class VoiceNotePage extends Activity {
    public SurveyHeaderBean header;
    public int SURVEY_MODE;
    public boolean flag_timer = false;
    ToggleButton btnVoiceNotes;
    ImageButton btnPlay;
    ImageButton btnStop;
    Button btnSave;
    LinearLayout playerLayout;
    View view = null;
    private AudioRecord record;
    private Bundle mArguments;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_note_layout);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        mArguments = getIntent().getExtras();
        int mMode = getIntent().getIntExtra(Global.BUND_KEY_MODE_SURVEY, 0);
        this.SURVEY_MODE = mMode;
        try {
            header = (SurveyHeaderBean) CustomerFragment.getHeader().clone();
        } catch (CloneNotSupportedException e) {
            header = CustomerFragment.getHeader();
        }
        if (header != null)
            initialize();
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.customer_header_error), Toast.LENGTH_SHORT).show();
            finish();
        }
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

    private void initialize() {
        playerLayout = (LinearLayout) findViewById(R.id.recorderLayout);
        btnVoiceNotes = (ToggleButton) findViewById(R.id.btnVoiceNotes);
        TextView noVoiceNote = (TextView) findViewById(R.id.txtNoVoiceNote);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setVisibility(View.GONE);

        record = new AudioRecord(this);


        if (SURVEY_MODE == Global.MODE_SURVEY_TASK ||
                SURVEY_MODE == Global.MODE_VIEW_SENT_SURVEY) {

            if (!(TaskHDataAccess.STATUS_SEND_INIT.equals(header.getIs_prepocessed()) &&
                    TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(header.getStatus()))) {
                btnVoiceNotes.setEnabled(false);
                btnVoiceNotes.setClickable(false);
                btnVoiceNotes.setTextOff(getString(R.string.recording_not_allowed));
                if (header.getVoice_note() != null) {
                    btnVoiceNotes.setVisibility(View.GONE);
                    playerLayout.setVisibility(View.VISIBLE);
                } else {
                    btnVoiceNotes.setVisibility(View.GONE);
                    noVoiceNote.setVisibility(View.VISIBLE);
                }
            }

            if (TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(header.getStatus()) ||
                    TaskHDataAccess.STATUS_SEND_DOWNLOAD.equals(header.getStatus()) ||
                    TaskHDataAccess.STATUS_SEND_INIT.equals(header.getStatus())) {
                if (header.getVoice_note() != null) {
                    btnVoiceNotes.setVisibility(View.VISIBLE);
                    playerLayout.setVisibility(View.VISIBLE);
                } else {
                    if (header.getPriority() == null)
                        btnVoiceNotes.setVisibility(View.VISIBLE);
                    playerLayout.setVisibility(View.GONE);
                }
            }
        } else {
            if (header.getVoice_note() != null) {
                btnVoiceNotes.setVisibility(View.VISIBLE);
                playerLayout.setVisibility(View.VISIBLE);
            } else {
                btnVoiceNotes.setVisibility(View.VISIBLE);
                playerLayout.setVisibility(View.GONE);
            }
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!flag_timer) {
                    btnVoiceNotes.setChecked(false);
                    record.stop(view);
                    header.setVoice_note(null);
                    playerLayout.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                    btnVoiceNotes.setClickable(true);

                    Toast.makeText(getApplicationContext(), getString(R.string.recording_done),
                            Toast.LENGTH_SHORT).show();
                    handler.removeCallbacks(this);
                } else {
                    handler.removeCallbacks(this);
                }
            }
        };

        btnVoiceNotes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                view = v;
                stopPlayer(v);
                if (btnVoiceNotes.isChecked()) {
                    //start record
                    flag_timer = false;
                    record.startRecording(v);
                    playerLayout.setVisibility(View.GONE);
                    btnSave.setVisibility(View.GONE);
                    handler = new Handler();
                    handler.postDelayed(runnable, 30000);

                } else {
                    //stop record
                    flag_timer = true;
                    record.stop(view);
                    header.setVoice_note(null);
                    playerLayout.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                    if (handler != null)
                        handler.removeCallbacks(runnable);
                }
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                byte[] voiceNotes = null;
                try {
                    voiceNotes = record.saveAudioToByte();
                    header.setVoice_note(voiceNotes);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                Intent intent = new Intent();
                intent.putExtra(Global.BUND_KEY_DETAIL_DATA, voiceNotes);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playPlayer(v);
                btnSave.setVisibility(View.GONE);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnSave.setVisibility(View.VISIBLE);
                stopPlayer(v);
                try {
                    if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval()) {
                        btnSave.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
        });
    }

    public void playPlayer(View v) {
        if (SURVEY_MODE == Global.MODE_SURVEY_TASK ||
                SURVEY_MODE == Global.MODE_VIEW_SENT_SURVEY) {//
            if (header.getVoice_note() != null) {
                AudioRecord.playAudio(getApplicationContext(), header.getVoice_note());
            } else {
                record.play(v);
            }
        } else {
            if (header.getVoice_note() != null) {
                AudioRecord.playAudio(getApplicationContext(), header.getVoice_note());
            } else {
                record.play(v);
            }
        }

    }

    public void stopPlayer(View v) {
        try {
            AudioRecord.stopPlay(v);
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }
}
