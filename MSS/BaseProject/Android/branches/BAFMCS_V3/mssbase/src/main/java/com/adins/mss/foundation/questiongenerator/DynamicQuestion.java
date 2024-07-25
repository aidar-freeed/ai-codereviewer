package com.adins.mss.foundation.questiongenerator;


import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.crashlytics.FireCrash;

import java.util.List;


public abstract class DynamicQuestion extends AppCompatActivity {

    /* STATIC REF */
    private static TextView txtDetailInFocus;
    private static TextView txtInFocus;
    private static ImageView thumbInFocus;
    private static ImageView thumbLocationInfo;
    private static QuestionBean questionInFocus;
    private static List<QuestionBean> listOfQuestion;


    public static List<QuestionBean> getListOfQuestion() {
        return listOfQuestion;
    }

    public static void setTxtInFocus(TextView v) {
        txtInFocus = v;
    }

    public static void setTxtInFocusText(String text) {
        if (txtInFocus != null) txtInFocus.setText(text);
        txtInFocus = null;
        questionInFocus = null;
    }

    public static TextView getTxtDetailInFocus() {
        return txtDetailInFocus;
    }

    public static void setTxtDetailInFocus(String text) {
        txtDetailInFocus.setText(text);
        txtDetailInFocus = null;
        txtDetailInFocus = null;
    }

    public static void setTxtDetailInFocus(TextView v) {
        txtDetailInFocus = v;
    }

    public static QuestionBean getQuestionInFocus() {
        return questionInFocus;
    }

    public static void setQuestionInFocus(QuestionBean questionInFocus) {
        DynamicQuestion.questionInFocus = questionInFocus;
    }

    public static void saveImage(byte[] bytes) {
        try {
            questionInFocus.setImgAnswer(bytes);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    public static void saveImageLocation(byte[] bytes) {
        questionInFocus.setImgLocation(bytes);
    }

    public static ImageView getThumbInFocus() {
        return thumbInFocus;
    }

    public static void setThumbInFocus(ImageView v) {
        thumbInFocus = v;
    }

    public static void setThumbInFocusImage(Bitmap bm) {
        thumbInFocus.setImageBitmap(bm);
        thumbInFocus = null;
        questionInFocus = null;
    }

    public static ImageView getThumbLocationInfo() {
        return thumbLocationInfo;
    }

    public static void setThumbLocationInfo(ImageView value) {
        thumbLocationInfo = value;
    }

    public static void setThumbLocationInfoImage(Bitmap bm) {
        thumbLocationInfo.setImageBitmap(bm);
        thumbLocationInfo = null;
        questionInFocus = null;
    }


}
