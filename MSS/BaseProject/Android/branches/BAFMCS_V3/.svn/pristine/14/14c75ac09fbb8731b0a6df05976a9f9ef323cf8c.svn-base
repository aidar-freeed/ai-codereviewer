package com.adins.mss.foundation.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.view.View;
import android.widget.Toast;

import com.adins.mss.base.crashlytics.FireCrash;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author gigin.ginanjar
 */
public class AudioRecord {
    public static MediaPlayer myPlayer;
    private MediaRecorder myRecorder;
    private String outputFile = null;
    private Context context;

    public AudioRecord(Context context) {
        this.context = context;

    }

    public static void stopPlay(View view) {
        try {
            if (myPlayer != null) {
                myPlayer.stop();
                myPlayer.release();
                myPlayer = null;
            }
        } catch (Exception e) {
            FireCrash.log(e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void playAudio(Context context, byte[] soundByteArray) {
        FileOutputStream fos = null;

        try {
            // create temp file that will hold byte array
            File tempAudio = File.createTempFile("voiceNotes", "m4a", context.getCacheDir());
            tempAudio.deleteOnExit();
            fos = new FileOutputStream(tempAudio);
            fos.write(soundByteArray);

            // Tried reusing instance of media player
            // but that resulted in system crashes...
            myPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            try(FileInputStream fis = new FileInputStream(tempAudio)){
                myPlayer.setDataSource(fis.getFD());
                myPlayer.prepare();
                myPlayer.start();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * //	 * Start Recording. <br/>
     * File Always Save in /data/data/files/audio/voiceNotes.m4a
     *
     * @param view
     */
    public void startRecording(View view) {
        File file = context.getFilesDir();
        File newfile = new File(file.getAbsolutePath() + "/audio"); // audio is the directory 2 create
        newfile.mkdir();

        outputFile = newfile + "/voiceNotes.m4a";
        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        myRecorder.setOutputFile(outputFile);
        myRecorder.setAudioEncodingBitRate(8000);
        start(view);

    }

    public void start(View view) {
        try {
            myRecorder.prepare();
            myRecorder.start();
        } catch (IllegalStateException e) {
            // start:it is called before prepare()
            // prepare: it is called after start() or before setOutputFormat()
            e.printStackTrace();
        } catch (IOException e) {
            // prepare() fails
            e.printStackTrace();
        }
        System.out.print("Recording Point: Recording");
        Toast.makeText(context, "Start recording...",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Stop Recording Audio
     */
    public void stop(View view) {
        try {
            myRecorder.stop();
            myRecorder.release();
            myRecorder = null;
        } catch (IllegalStateException e) {
            // it is called before start()
            e.printStackTrace();
        } catch (RuntimeException e) {
            // no valid audio/video data has been received
            e.printStackTrace();
        }
    }

    public void play(View view) {
        try {
            myPlayer = new MediaPlayer();
            myPlayer.setDataSource(outputFile);
            myPlayer.prepare();
            myPlayer.start();
        } catch (Exception e) {
            FireCrash.log(e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Gets Bytes of audioNotes
     *
     * @return byte[]
     */
    public byte[] saveAudioToByte() {
        byte[] files = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(outputFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            File file = new File(outputFile);
            long lengthFile = file.length();
            byte[] buff = new byte[(int) lengthFile];
            int i = Integer.MAX_VALUE;
            try {
                while ((i = fis.read(buff, 0, buff.length)) > 0) {
                    baos.write(buff, 0, i);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            files = baos.toByteArray();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return files;
    }
}
