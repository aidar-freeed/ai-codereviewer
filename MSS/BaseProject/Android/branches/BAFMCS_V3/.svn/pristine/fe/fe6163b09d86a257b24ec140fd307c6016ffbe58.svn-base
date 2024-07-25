package com.adins.mss.foundation.questiongenerator;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.QuestionGroup;
import com.adins.mss.base.timeline.MapsViewer;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.camerainapp.CameraActivity;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.questiongenerator.form.DropdownQuestionView;
import com.adins.mss.foundation.questiongenerator.form.FingerDrawingActivity;
import com.adins.mss.foundation.questiongenerator.form.LabelFieldView;
import com.adins.mss.foundation.questiongenerator.form.LocationTagingView;
import com.adins.mss.foundation.questiongenerator.form.MultipleQuestionView;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;
import com.adins.mss.foundation.questiongenerator.form.RadioQuestionView;

import org.acra.ACRA;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class QuestionViewGenerator {
    public static final int TYPE_DATE = 1;
    public static final int TYPE_TIME = 2;
    public static final int TYPE_DATE_TIME = 3;

    public final LayoutParams defLayout = new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir;
        File image = null;
        try {
            storageDir = context.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists()) {
                storageDir.mkdir();
            }
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing ", e);
        }

        // Save a file: path for use with ACTION_VIEW intents
        if(image == null)
            return null;

        DynamicFormActivity.setmCurrentPhotoPath(image.getAbsolutePath());
        return image;
    }

    public QuestionView generateQuestionGroupTitle(Activity activity, QuestionGroup questionGroup) {
        QuestionView mainContainer = new QuestionView(activity);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(5, 10, 5, 10);
        mainContainer.setQuestionGroup(questionGroup);
        QuestionView container2 = new QuestionView(activity);
        container2.setOrientation(LinearLayout.HORIZONTAL);
        container2.setBackgroundResource(R.color.tv_dark);
        container2.setPadding(5, 10, 5, 10);
        container2.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textLayout.weight = 0.8f;


        TextView label = new TextView(activity);
        label.setText(questionGroup.getQuestion_group_name().toUpperCase());
        label.setTextColor(Color.WHITE);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
        container2.addView(label, textLayout);

        RelativeLayout.LayoutParams viewLayout = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        viewLayout.width = Global.TRIANGLE_SIZE;
        viewLayout.height = Global.TRIANGLE_SIZE;
        viewLayout.bottomMargin = 10;
        viewLayout.topMargin = 10;
        viewLayout.leftMargin = 10;
        viewLayout.rightMargin = 10;

        RelativeLayout.LayoutParams relLayout = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        RelativeLayout relativeLayout = new RelativeLayout(activity);
        relativeLayout.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        View view = new View(activity);
        view.setBackgroundResource(R.drawable.triangle);
        view.setRotation(mainContainer.isExpanded() ? 0f : 180f);

        relativeLayout.addView(view, viewLayout);
        container2.addView(relativeLayout, relLayout);

        mainContainer.addView(container2, defLayout);
        mainContainer.setTitleOnly(true);


        return mainContainer;
    }

    public QuestionView generateTextQuestion(final Activity activity,
                                             final QuestionBean bean, int number, int inpType) {

        final QuestionView container = new QuestionView(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(5, 0, 5, 0);

        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {
            final String requiredHint = activity.getString(R.string.requiredField);
            TextView label = new TextView(activity);
            String questionLabel = number + ". " + bean.getQuestion_label();
            label.setText(questionLabel);
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.addView(label, defLayout);

            final EditText text = new EditText(activity);
            if (null != bean.getAnswer() && !"".equalsIgnoreCase(bean.getAnswer())) {
                text.setText(bean.getAnswer());
            }
            if (Global.AT_DECIMAL.equals(bean.getAnswer_type())) {
                text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else {
                text.setInputType(inpType);
            }
            InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                    bean.getMax_length())};
            text.setFilters(inputFilters);
            text.setLayoutParams(defLayout);
            text.addTextChangedListener(new TextWatcher() {
                String TempText = "";

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //EMPTY
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // save answer to bean
                    if (bean.isReadOnly()) {
                        text.setKeyListener(null);
                        text.setCursorVisible(false);
                    }
                    if (Global.AT_CALCULATION.equals(bean.getAnswer_type())) {
                        TempText = Tool.separateThousand(text.getText().toString().trim());
                    } else {
                        TempText = text.getText().toString().trim();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (bean.isRelevanted()) {
                        if (TempText.contains(".")) {
                            String newText = text.getText().toString().trim();
                            if (!newText.contains("."))
                                newText = Tool.separateThousand(newText);
                            if (!TempText.equals(newText)) {
                                if (!newText.contains(","))
                                    newText = Tool.separateThousand(newText);
                                if (!TempText.equals(newText))
                                    container.setChanged(true);
                                else
                                    container.setChanged(false);
                            } else
                                container.setChanged(false);
                        } else {
                            TempText = Tool.separateThousand(TempText);
                            String newText = text.getText().toString().trim();
                            if (!newText.contains("."))
                                newText = Tool.separateThousand(newText);
                            if (!TempText.equals(newText))
                                container.setChanged(true);
                            else
                                container.setChanged(false);
                        }

                    }
                }
            });
            text.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (Global.AT_CURRENCY.equals(bean.getAnswer_type())) {
                        if (!hasFocus) {
                            text.setInputType(InputType.TYPE_CLASS_TEXT);
                            String answer = text.getText().toString().trim();
                            String currencyView = Tool.separateThousand(answer);
                            InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                                    50)};
                            text.setFilters(inputFilters);
                            if (currencyView == null) currencyView = "";
                            text.setText(currencyView);

                        } else {
                            InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                                    bean.getMax_length())};
                            text.setFilters(inputFilters);

                            String answer = text.getText().toString().trim();
                            String finalAnswer = "";
                            if (answer != null && answer.length() > 0) {
                                String tempAnswer = Tool.deleteAll(answer, ",");
                                String[] intAnswer = Tool.split(tempAnswer, ".");
                                if (intAnswer.length > 1) {
                                    if (intAnswer[1].equals("00"))
                                        finalAnswer = intAnswer[0];
                                    else {
                                        finalAnswer = tempAnswer;
                                    }
                                } else {
                                    finalAnswer = tempAnswer;
                                }
                            }
                            text.setInputType(InputType.TYPE_CLASS_NUMBER);
                            text.setText(finalAnswer);
                        }
                    }
                }
            });

            text.setPadding(5, 0, 0, 0);
            if (inpType == InputType.TYPE_TEXT_FLAG_MULTI_LINE) {
                text.setSingleLine(false);
            } else {
                text.setSingleLine(true);
            }

            if (bean.getIs_mandatory().equals(Global.TRUE_STRING))
                text.setHint(requiredHint);

            if (bean.isReadOnly()) {
                text.setSingleLine(false);
                text.setKeyListener(null);
                text.setCursorVisible(false);
                try {
                    text.setEnabled(false);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
            container.addView(text, defLayout);
        }

        return container;
    }

    //Gigin ~ autotext Question
    public QuestionView generateTextWithSuggestionQuestion(Activity activity,
                                                           final QuestionBean bean, int number) {

        final QuestionView container = new QuestionView(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(5, 0, 5, 0);

        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {
            final String requiredHint = activity.getString(R.string.requiredField);
            TextView label = new TextView(activity);
            String questionLabel = number + ". " + bean.getQuestion_label();
            label.setText(questionLabel);
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.addView(label, defLayout);

            final AutoCompleteTextView text = new AutoCompleteTextView(activity);

            if (null != bean.getAnswer() && !"".equalsIgnoreCase(bean.getAnswer())) {
                text.setText(bean.getAnswer());
            }

            InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                    bean.getMax_length())};
            text.setFilters(inputFilters);
            text.setPadding(5, 0, 0, 0);

            SingleArrayAdapter adapter = new SingleArrayAdapter(activity, bean);
            text.setAdapter(adapter);
            text.setThreshold(1);
            text.setSingleLine();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                text.setDropDownBackgroundDrawable(activity.getResources().getDrawable(R.drawable.actionbar_background, activity.getTheme()));
            } else {
                text.setDropDownBackgroundDrawable(activity.getResources().getDrawable(R.drawable.actionbar_background));
            }
            text.addTextChangedListener(new TextWatcher() {
                String TempText = "";

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //EMPTY
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // save answer to bean
                    TempText = text.getText().toString().trim();
                    if (bean.isReadOnly()) {
                        text.setKeyListener(null);
                        text.setCursorVisible(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (bean.isRelevanted()) {
                        String newText = text.getText().toString().trim();
                        if (!TempText.equals(newText)) {
                            container.setChanged(true);
                        } else
                            container.setChanged(false);
                    }
                }
            });

            if (bean.getIs_mandatory().equals(Global.TRUE_STRING))
                text.setHint(requiredHint);

            if (bean.isReadOnly()) {
                text.setKeyListener(null);
                text.setCursorVisible(false);
                try {
                    text.setEnabled(false);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
            container.addView(text);
        }

        return container;
    }

    //--------------------------------------------------------------------------
    //Glen 17 Oct 2014
    private QuestionView generateCalculationField(Activity activity,
                                                  QuestionBean bean, int number) {
        String label = number + ". " + bean.getQuestion_label();
        LabelFieldView field = new LabelFieldView(activity, bean, label, true);

        return field;
    }

    public QuestionView generateDateTimeQuestion(final Activity activity,
                                                 QuestionBean bean, int number, int type) throws Exception {

        QuestionView container = new QuestionView(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(5, 0, 5, 0);

        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {
            TextView label = new TextView(activity);
            String questionLabel = number + ". " + bean.getQuestion_label();
            label.setText(questionLabel);
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.addView(label, defLayout);

            String format = null;
            View.OnClickListener listener = null;
            String btnLabel = null;
            final TextView text = new TextView(activity);
            text.setPadding(0, 0, 5, 0);

            switch (type) {
                case TYPE_DATE:
                    format = Global.DATE_STR_FORMAT;
                    btnLabel = activity.getString(R.string.btnDate);
                    listener = new View.OnClickListener() {
                        public void onClick(View v) {
                            activity.showDialog(QuestionViewGenerator.TYPE_DATE);
                            DynamicQuestion.setTxtInFocus(text);
                        }
                    };
                    break;
                case TYPE_TIME:
                    format = Global.TIME_STR_FORMAT;
                    btnLabel = activity.getString(R.string.btnTime);
                    listener = new View.OnClickListener() {
                        public void onClick(View v) {
                            activity.showDialog(QuestionViewGenerator.TYPE_TIME);
                            DynamicQuestion.setTxtInFocus(text);

                        }
                    };
                    break;
                case TYPE_DATE_TIME:
                    format = Global.DATE_TIME_STR_FORMAT;
                    btnLabel = activity.getString(R.string.btnDate);
                    listener = new View.OnClickListener() {
                        public void onClick(View v) {
                            activity.showDialog(QuestionViewGenerator.TYPE_DATE_TIME);
                            DynamicQuestion.setTxtInFocus(text);
                        }
                    };
                    break;
                default:
                    format = Global.DATE_STR_FORMAT;
                    btnLabel = activity.getString(R.string.btnDate);
                    listener = new View.OnClickListener() {
                        public void onClick(View v) {
                            activity.showDialog(QuestionViewGenerator.TYPE_DATE);
                            DynamicQuestion.setTxtInFocus(text);
                        }
                    };
                    break;
            }

            LinearLayout row = new LinearLayout(activity);
            row.setOrientation(LinearLayout.HORIZONTAL);
            String answer = bean.getAnswer();
            try {
                Date date = null;
                if (type == TYPE_TIME) {
                    date = Formatter.parseDate(answer, Global.TIME_STR_FORMAT2);
                } else {
                    date = Formatter.parseDate(answer, Global.DATE_STR_FORMAT_GSON);
                }

                answer = Formatter.formatDate(date, format);
                text.setText(answer);
            } catch (Exception ex) {
                text.setText(format);
                try {
                    if (answer != null && answer.length() > 0) {
                        try {
                            long dtLong = Formatter.stringToDate(answer);
                            Date date = new Date(dtLong);
                            if (date != null) {
                                answer = Formatter.formatDate(date, format);
                            }
                        } catch (Exception e) {
                            FireCrash.log(e);
                            answer = Formatter.formatDate(Formatter.parseDate(answer, "dd-MMM-yyyy"), format);
                        }
                        if (answer != null && answer.length() > 0) {
                            text.setText(answer);
                        } else {
                            text.setText(format);
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.4f);
            tp.setMargins(0, 0, 5, 0);
            row.addView(text, tp);

            Button btn = new Button(activity);
            btn.setText(btnLabel);
            if (bean.isReadOnly()) {
                btn.setEnabled(false);
                btn.setVisibility(View.GONE);
            } else {
                btn.setOnClickListener(listener);
            }
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.6f);
            row.addView(btn, bp);


            container.addView(row);
        }

        return container;
    }

    public QuestionView generateLocationQuestion(final Activity activity,
                                                 final QuestionBean bean, int number, int type) throws Exception {

        QuestionView container = new QuestionView(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(5, 0, 5, 0);

        LinearLayout imgContainer = new QuestionView(activity);
        imgContainer.setOrientation(LinearLayout.HORIZONTAL);
        imgContainer.setGravity(Gravity.CENTER);
        imgContainer.setPadding(5, 0, 5, 0);

        final QuestionBean bean2 = bean;
        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {
            TextView label = new TextView(activity);
            String questionLabel = number + ". " + bean.getQuestion_label();
            label.setText(questionLabel);
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.addView(label, defLayout);

            String format = null;
            View.OnClickListener listener = null;
            String btnLabel = null;
            final TextView text = new TextView(activity);

            final ImageView thumbLct = new ImageView(activity);
            thumbLct.setPadding(15, 0, 0, 0);

            if (Global.AT_GPS.equals(bean.getAnswer_type())) {

                btnLabel = activity.getString(R.string.btnGPSnLBS);
                listener = new View.OnClickListener() {
                    public void onClick(View v) {
                        LocationInfo info = Global.LTM.getCurrentLocation(Global.FLAG_LOCATION_CAMERA);
                        bean.setLocationInfo(info);
                        bean2.setLocationInfo(info);
                        text.setText(LocationTrackingManager.toAnswerString(info));
                        DynamicQuestion.setQuestionInFocus(bean2);
                        DynamicQuestion.setTxtInFocus(text);
                    }
                };
            } else {
                btnLabel = activity.getString(R.string.btnGPS);
                listener = new View.OnClickListener() {
                    public void onClick(View v) {
                        DynamicQuestion.setQuestionInFocus(bean);
                        DynamicQuestion.setTxtInFocus(text);
                        Intent intent = new Intent(activity, LocationTagingView.class);
                        activity.startActivityForResult(intent, Global.REQUEST_LOCATIONTAGGING);
                    }
                };
            }
            final float scale = 0;
            int w = Tool.dpToPixel(scale, Global.THUMBNAIL_WIDTH);
            int h = Tool.dpToPixel(scale, Global.THUMBNAIL_HEIGHT);
            ViewGroup.LayoutParams imgLayout = new LayoutParams(w, h);
            thumbLct.setLayoutParams(imgLayout);
            thumbLct.setImageResource(R.drawable.ic_absent);
            thumbLct.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (bean.getAnswer() != null && bean.getAnswer().length() > 0) {
                        try {
                            String lat = bean.getLocationInfo().getLatitude();
                            String lng = bean.getLocationInfo().getLongitude();
                            int acc = bean.getLocationInfo().getAccuracy();
                            Intent intent = new Intent(activity, MapsViewer.class);
                            intent.putExtra("latitude", lat);
                            intent.putExtra("longitude", lng);
                            intent.putExtra("accuracy", acc);
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            String lat = bean.getLatitude();
                            String lng = bean.getLongitude();
                            Intent intent = new Intent(activity, MapsViewer.class);
                            intent.putExtra("latitude", lat);
                            intent.putExtra("longitude", lng);
                            activity.startActivity(intent);
                        }
                    } else {
                        Toast.makeText(activity, "Set Location First!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });


            LinearLayout row = new LinearLayout(activity);
            row.setOrientation(LinearLayout.VERTICAL);

            try {
                String answer = bean.getAnswer();

                text.setText(answer);
            } catch (Exception ex) {
                text.setText("-");
            }


            Button btn = new Button(activity);
            btn.setText(btnLabel);
            if (bean.isReadOnly()) {
                btn.setEnabled(false);
            } else {
                btn.setOnClickListener(listener);
            }

            if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval()) {

            } else {
                imgContainer.addView(btn, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            imgContainer.addView(thumbLct, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            row.addView(text, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            container.addView(imgContainer);
            container.addView(row);
        }

        return container;
    }

    public DropdownQuestionView generateDropdownQuestion(Activity activity,
                                                         QuestionBean bean, int number) {

        //glen 27 Aug 2014
        DropdownQuestionView container = new DropdownQuestionView(activity, bean);
        //Glen manually setVisibility
        container.setVisible(false);

        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(5, 0, 5, 0);

        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {
            //Glen setvisible
            container.setVisible(true);

            final String prompt = activity.getString(R.string.promptSelectOne);
            String questionLabel = number + ". " + bean.getQuestion_label();
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.setLabelText(questionLabel);

            //Glen 27 Aug 2014, check if original method yield any result. if not, better lookup from table, which happen on DynamicSurveyActivity
            List<OptionAnswerBean> listOptions = bean.getOptionAnswers();
           if (bean.getOptionRelevances() != null && bean.getOptionRelevances().length > 0) {
                listOptions = new ArrayList<>();
            } else if (listOptions != null && !listOptions.isEmpty()) {    //there is options. either has been looked up, or options exist the old way
                container.setOptions(activity, listOptions);
            } else {                                                        //empty. sure just leave it blank
                listOptions = new ArrayList<>();
            }

            container.getSpinner().setPrompt(prompt);

            if (Global.AT_DROPDOWN_W_DESCRIPTION.equals(bean.getAnswer_type())) {
                container.enableDescription(activity);
            }


            if (bean.isReadOnly()) {
                container.getSpinner().setClickable(false);
                container.getSpinner().setEnabled(false);
            }

        }

        return container;
    }

    //Glen 26 Aug 2014, note: might not be needed anymore if Dropdown With Desc can use method generateDropdownQuestion above correctly
    public LinearLayout generateDropdownDescQuestion(Activity activity,
                                                     QuestionBean bean, int number) {
        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(5, 0, 5, 0);

        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {
            final String prompt = activity.getString(R.string.promptSelectOne);
            TextView label = new TextView(activity);

            String questionLabel = number + ". " + bean.getQuestion_label();
            label.setText(questionLabel);
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.addView(label, defLayout);

            List<OptionAnswerBean> listOptions = bean.getOptionAnswers();

            Spinner spinner = new Spinner(activity);
            ArrayAdapter<OptionAnswerBean> spAdapter = new ArrayAdapter<>(
                    activity, R.layout.spinner_style2, listOptions);
            spAdapter.setDropDownViewResource(R.layout.spinner_style);
            spinner.setAdapter(spAdapter);
            spinner.setPrompt(prompt);

            EditText desc = new EditText(activity);
            InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                    Global.DEFAULT_MAX_LENGTH)};
            desc.setFilters(inputFilters);
            desc.setHeight(40);

            int selected = Tool.getSelectedIndex(listOptions);
            if (selected != -1) {
                spinner.setSelection(selected);

                OptionAnswerBean optBean = listOptions.get(selected);
                desc.setText(optBean.getValue());
            }
            container.addView(spinner, defLayout);
            container.addView(desc, defLayout);
        }

        return container;
    }

    //Glen 10 Oct 2014, convert to new class
    public QuestionView generateMultipleQuestion(Activity activity,
                                                 QuestionBean bean, int number) {


        MultipleQuestionView container = new MultipleQuestionView(activity, bean);
        container.setVisible(false);

        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(5, 0, 5, 0);

        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {
            //manually setVisibility
            container.setVisible(true);

            String questionLabel = number + ". " + bean.getQuestion_label();
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.setLabelText(questionLabel);


            List<OptionAnswerBean> listOptions = bean.getOptionAnswers();


            //Glen 15 Oct 2014, enable description if needed
            if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(bean.getAnswer_type())) {
                container.enableDescription(activity);
            }

            if (Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(bean.getAnswer_type())) {
                container.enableOneDescription(activity);
            }

            if (listOptions != null && !listOptions.isEmpty()) {
                container.setOptions(activity, listOptions);
            }

        }


        return container;
    }

    //Glen 10 Oct 2014, combine this logic on generateMultipleObjects
    public LinearLayout generateMultipleDescQuestion(Activity activity,
                                                     QuestionBean bean, int number) {
        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(5, 0, 5, 0);

        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {
            TextView label = new TextView(activity);
            label.setTextColor(Color.BLACK);
            String questionLabel = number + ". " + bean.getQuestion_label();
            label.setText(questionLabel);
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.addView(label, defLayout);

            List<OptionAnswerBean> listOptions = bean.getOptionAnswers();

            for (OptionAnswerBean optBean : listOptions) {
                CheckBox chk = new CheckBox(activity);
                chk.setText(optBean.getValue());
                chk.setTextColor(Color.BLACK);
                chk.setChecked(optBean.isSelected());
                container.addView(chk, defLayout);

                EditText desc = new EditText(activity);
                InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                        Global.DEFAULT_MAX_LENGTH)};
                desc.setFilters(inputFilters);
                desc.setHeight(40);
                if (optBean.isSelected()) {
                    desc.setText(optBean.getValue());
                }
                container.addView(desc, defLayout);
            }
        }

        return container;
    }

    //Glen 1 Oct 2014, convert to new class
//	public LinearLayout generateRadioQuestion(Activity activity,
    public RadioQuestionView generateRadioQuestion(Activity activity,
                                                   QuestionBean bean, int number) {
        //Glen 1 Oct 2014, merge this method with the description one
        boolean withDescription = bean.getAnswer_type().equals(Global.AT_RADIO_W_DESCRIPTION);

        RadioQuestionView container = new RadioQuestionView(activity, bean);
        //Glen visible is default to false
        container.setVisible(false);

        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(5, 0, 5, 0);

        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {

            //it's visible
            container.setVisible(true);
            String questionLabel = number + ". " + bean.getQuestion_label();
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.setLabelText(number + ". " + bean.getQuestion_label());

            List<OptionAnswerBean> listOptions = bean.getOptionAnswers();

            if (withDescription) {
                container.enableDescription(activity);
            }


            if (bean.getOptionRelevances() != null && bean.getOptionRelevances().length > 0) {
                listOptions = new ArrayList<>();
            } else if (listOptions != null && !listOptions.isEmpty()) {


                container.setOptions(activity, listOptions);


            } else {
                listOptions = new ArrayList<>();
            }
        }

        return container;
    }

    public LinearLayout generateRadioDescQuestion(Activity activity,
                                                  QuestionBean bean, int number) {
        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(5, 0, 5, 0);

        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {
            TextView label = new TextView(activity);
            String questionLabel = number + ". " + bean.getQuestion_label();
            label.setText(questionLabel);
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.addView(label, defLayout);

            List<OptionAnswerBean> listOptions = bean.getOptionAnswers();

            RadioGroup rdGroup = null;
            if (listOptions != null && !listOptions.isEmpty()) {
                rdGroup = new RadioGroup(activity);
                for (OptionAnswerBean optBean : listOptions) {

                    RadioButton rb = new RadioButton(activity);
                    rb.setText(optBean.getValue());
                    rb.setTextColor(Color.BLACK);
                    rdGroup.addView(rb, defLayout);
                }
                container.addView(rdGroup, defLayout);

                int selected = Tool.getSelectedIndex(listOptions);
                if (selected != -1) {
                    RadioButton rb = (RadioButton) rdGroup.getChildAt(selected);
                    rb.setChecked(true);
                }

                EditText desc = new EditText(activity);
                InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                        Global.DEFAULT_MAX_LENGTH)};
                desc.setFilters(inputFilters);
                desc.setHeight(40);

                if (selected != -1) {
                    OptionAnswerBean optBean = listOptions.get(selected);
                    desc.setText(optBean.getValue());
                }
                container.addView(desc, defLayout);
            }
        }

        return container;
    }

    public QuestionView generateImageQuestion(final Activity activity, final QuestionBean bean, int number, final int iconCameraResId, final Class<?> ViewImageActivity, final Class<?> CameraPreviewActivity) {
        Utility.freeMemory();
        QuestionView container = new QuestionView(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        container.setPadding(5, 0, 5, 0);

        LinearLayout imgContainer = new QuestionView(activity);
        imgContainer.setOrientation(LinearLayout.HORIZONTAL);
        imgContainer.setGravity(Gravity.CENTER);
        imgContainer.setPadding(5, 0, 5, 0);

        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {
            TextView label = new TextView(activity);
            String questionLabel = number + ". " + bean.getQuestion_label();
            label.setText(questionLabel);
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.addView(label, defLayout);

            if (true) {
                final byte[] img = bean.getImgAnswer();
                final ImageView thumb = new ImageView(activity);
                final ImageView thumbLct = new ImageView(activity);
                thumbLct.setPadding(15, 0, 0, 0);
                final TextView txtDetail = new TextView(activity);
                final float scale = 0;
                int w = Tool.dpToPixel(scale, Global.THUMBNAIL_WIDTH);
                int h = Tool.dpToPixel(scale, Global.THUMBNAIL_HEIGHT);
                ViewGroup.LayoutParams imgLayout = new LayoutParams(w, h);
                thumb.requestFocusFromTouch();
                thumb.setLayoutParams(imgLayout);

                thumb.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        DynamicQuestion.setThumbInFocus(thumb);
                        DynamicQuestion.setQuestionInFocus(bean);
                        DynamicQuestion.setTxtDetailInFocus(txtDetail);

                        if (DynamicQuestion.getQuestionInFocus().getImgAnswer() != null) {
                            if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval()) {
                                try {
                                    Global.getSharedGlobal().setIsViewer(true);
                                    Bundle extras = new Bundle();
                                    extras.putByteArray(Global.BUND_KEY_IMAGE_BYTE, bean.getImgAnswer());
                                    Intent intent = new Intent(activity, ViewImageActivity);
                                    intent.putExtras(extras);
                                    activity.startActivity(intent);

                                } catch (Exception e) {
                                    FireCrash.log(e);
                                }
                            } else {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                builder.setMessage(activity.getString(R.string.picture_option));
                                builder.setCancelable(true);


                                builder.setPositiveButton(activity.getString(R.string.btnView), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            Global.getSharedGlobal().setIsViewer(!DynamicFormActivity.isAllowImageEdit());
                                            Bundle extras = new Bundle();
                                            extras.putByteArray(Global.BUND_KEY_IMAGE_BYTE, bean.getImgAnswer());
                                            Intent intent = new Intent(activity, ViewImageActivity);
                                            intent.putExtras(extras);
                                            activity.startActivity(intent);

                                        } catch (Exception e) {             FireCrash.log(e);
                                        }


                                    }
                                });
                                builder.setNeutralButton(activity.getString(R.string.btnRetake), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        openCameraApp(activity);
                                    }
                                });
                                builder.setNegativeButton(activity.getString(R.string.btnDelete), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        bean.setImgAnswer(null);
                                        bean.setImgTimestamp(null);
                                        DynamicQuestion.saveImage(null);

                                        thumb.setImageResource(iconCameraResId);

                                        txtDetail.setText("");
                                        dialog.cancel();
                                    }
                                });
                                builder.create().show();
                            }
                        } else {
                            if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval()) {
                                try {
                                    Global.getSharedGlobal().setIsViewer(true);
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                }
                            } else {
                                openCameraApp(activity);
                            }
                        }

                    }
                });

                thumbLct.setLayoutParams(imgLayout);
                thumbLct.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (bean.getImgAnswer() != null) {
                            try {
                                String lat = bean.getLocationInfo().getLatitude();
                                String lng = bean.getLocationInfo().getLongitude();
                                int acc = bean.getLocationInfo().getAccuracy();
                                Intent intent = new Intent(activity, MapsViewer.class);
                                intent.putExtra("latitude", lat);
                                intent.putExtra("longitude", lng);
                                intent.putExtra("accuracy", acc);
                                activity.startActivity(intent);
                            } catch (Exception e) {
                                FireCrash.log(e);
                                String lat = bean.getLatitude();
                                String lng = bean.getLongitude();
                                Intent intent = new Intent(activity, MapsViewer.class);
                                intent.putExtra("latitude", lat);
                                intent.putExtra("longitude", lng);
                                activity.startActivity(intent);
                            }
                        } else {
                            if (!DynamicFormActivity.getIsVerified() && !DynamicFormActivity.getIsApproval()) {
                                Toast.makeText(activity, activity.getString(R.string.take_foto_first),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });


                if (img != null && img.length > 0) {
                    if (Tool.isHaveLocation(bean.getAnswer_type())) {
                        thumbLct.setImageResource(R.drawable.ic_absent);
                    }
                   new BitmapWorkerTask(thumb, txtDetail).execute(bean);
                } else {
                    thumb.setImageResource(iconCameraResId);
                    if (Tool.isHaveLocation(bean.getAnswer_type())) {
                        thumbLct.setImageResource(R.drawable.ic_absent);
                    }
                }
                imgContainer.addView(thumb, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                imgContainer.addView(thumbLct, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                container.addView(imgContainer, defLayout);
                container.addView(txtDetail, defLayout);


            }
        }

        return container;

    }

    public QuestionView generateDrawingQuestion(final Activity activity, final QuestionBean bean, int number) {
        QuestionView container = new QuestionView(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        container.setPadding(5, 0, 5, 0);

        if (bean.getIs_visible().equals(Global.TRUE_STRING)) {
            TextView label = new TextView(activity);
            String questionLabel = number + ". " + bean.getQuestion_label();
            label.setText(questionLabel);
            ACRA.getErrorReporter().putCustomData("LAST_QUESTION", questionLabel);
            container.addView(label, defLayout);

            byte[] img = bean.getImgAnswer();
            final ImageView thumb = new ImageView(activity);
            thumb.setMaxHeight(Global.THUMBNAIL_HEIGHT);
            thumb.setMaxWidth(Global.THUMBNAIL_WIDTH);
            thumb.setClickable(true);
            thumb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DynamicQuestion.setThumbInFocus(thumb);
                    DynamicQuestion.setQuestionInFocus(bean);
                    activity.startActivity(new Intent(activity, FingerDrawingActivity.class));
                }
            });


            if (img != null && img.length > 0) {
                Bitmap bm = BitmapFactory.decodeByteArray(img, 0, img.length);
                int[] res = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
                Bitmap thumbnail = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
                thumb.setImageBitmap(thumbnail);
            } else {
                thumb.setImageResource(android.R.drawable.ic_menu_edit);
            }
            container.addView(thumb, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        return container;
    }

    public QuestionView generateByAnswerType(Activity activity,
                                             QuestionBean bean, int number, final int iconCameraResId, final Class<?> ViewImageActivity, final Class<?> CameraPreviewActivity, final QuestionGroup questionGroup) throws Exception {
        QuestionView linear = new QuestionView(activity);
        linear.setOrientation(LinearLayout.VERTICAL);

        if (questionGroup != null) {
            return this.generateQuestionGroupTitle(activity, questionGroup);
        }

        String answerType = bean.getAnswer_type();
        if (Global.AT_TEXT.equals(answerType)) {
            return this.generateTextQuestion(activity, bean, number, InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        } else if (Global.AT_TEXT_WITH_SUGGESTION.equals(answerType)) {
            return this.generateTextWithSuggestionQuestion(activity, bean, number);
        } else if (Global.AT_TEXT_MULTILINE.equals(answerType)) {
            return this.generateTextQuestion(activity, bean, number, InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else if (Global.AT_CURRENCY.equals(answerType)) {
            return this.generateTextQuestion(activity, bean, number, InputType.TYPE_CLASS_NUMBER);
        } else if (Global.AT_NUMERIC.equals(answerType)) {
            return this.generateTextQuestion(activity, bean, number, InputType.TYPE_CLASS_NUMBER);
        } else if (Global.AT_DECIMAL.equals(answerType)) {
            return this.generateTextQuestion(activity, bean, number, InputType.TYPE_CLASS_NUMBER);
        } else if (Global.AT_MULTIPLE.equals(answerType)) {
            return this.generateMultipleQuestion(activity, bean, number);
        } else if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(answerType)) {
            return this.generateMultipleQuestion(activity, bean, number);
        } else if (Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(answerType)) {
            return this.generateMultipleQuestion(activity, bean, number);
        } else if (Global.AT_RADIO.equals(answerType)) {
            return this.generateRadioQuestion(activity, bean, number);
        } else if (Global.AT_RADIO_W_DESCRIPTION.equals(answerType)) {
            return this.generateRadioQuestion(activity, bean, number);
        } else if (Global.AT_DROPDOWN.equals(answerType)) {
            return this.generateDropdownQuestion(activity, bean, number);
        } else if (Global.AT_DROPDOWN_W_DESCRIPTION.equals(answerType)) {
            return this.generateDropdownQuestion(activity, bean, number);
        } else if (Global.AT_DATE.equals(answerType)) {
            return this.generateDateTimeQuestion(activity, bean, number, TYPE_DATE);
        } else if (Global.AT_TIME.equals(answerType)) {
            return this.generateDateTimeQuestion(activity, bean, number, TYPE_TIME);
        } else if (Global.AT_DATE_TIME.equals(answerType)) {
            return this.generateDateTimeQuestion(activity, bean, number, TYPE_DATE_TIME);
        } else if (Tool.isImage(answerType)) {
            return this.generateImageQuestion(activity, bean, number, iconCameraResId, ViewImageActivity, CameraPreviewActivity);
        } else if (Global.AT_DRAWING.equals(answerType)) {
            return this.generateDrawingQuestion(activity, bean, number);
        } else if (Global.AT_LOCATION.equals(answerType)) {
            return this.generateLocationQuestion(activity, bean, number, TYPE_DATE);
        }
        //Glen 17 Oct 2014, new AT
        else if (Global.AT_CALCULATION.equals(answerType)) {
            return this.generateCalculationField(activity, bean, number);
        } else if (Global.AT_GPS.equals(answerType)) {
            return this.generateLocationQuestion(activity, bean, number, TYPE_DATE);
        } else if (Global.AT_GPS_N_LBS.equals(answerType)) {
            return this.generateLocationQuestion(activity, bean, number, TYPE_DATE);
        } else {
            return null;
        }
    }

    private void openCameraApp(Activity activity) {
        if (GlobalData.getSharedGlobalData().isUseOwnCamera()) {
            int quality = Utils.picQuality;
            int thumbHeight = Utils.picHeight;
            int thumbWidht = Utils.picWidth;
            QuestionBean bean = DynamicFormActivity.getQuestionInFocus();

            if (bean.getImg_quality() != null && bean.getImg_quality().equalsIgnoreCase(Global.IMAGE_HQ)) {
                thumbHeight = Utils.picHQHeight;
                thumbWidht = Utils.picHQWidth;
                quality = Utils.picHQQuality;
            }

            Intent intent = new Intent(activity, CameraActivity.class);
            intent.putExtra(CameraActivity.PICTURE_WIDTH, thumbWidht);
            intent.putExtra(CameraActivity.PICTURE_HEIGHT, thumbHeight);
            intent.putExtra(CameraActivity.PICTURE_QUALITY, quality);

            activity.startActivityForResult(intent, Utils.REQUEST_IN_APP_CAMERA);
        } else {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(activity.getApplicationContext());
                    } catch (IOException ex) {
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        activity.startActivityForResult(intent, Utils.REQUEST_CAMERA);

                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }

    class BitmapWorkerTask extends AsyncTask<QuestionBean, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private final WeakReference<TextView> textViewReference;
        private byte[] data;
        private int[] resolusi;
        private String indicatorGPS;
        private String formattedSize;

        public BitmapWorkerTask(ImageView imageView, TextView textView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
            textViewReference = new WeakReference<>(textView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(QuestionBean... params) {
            QuestionBean bean = params[0];

            if (bean.getAnswer() != null) {
                indicatorGPS = bean.getAnswer();
            } else {
                if (bean.getLocationInfo() != null)
                    indicatorGPS = LocationTrackingManager.toAnswerStringShort(bean.getLocationInfo());
            }
            long size = bean.getImgAnswer().length;
            formattedSize = Formatter.formatByteSize(size);

            data = bean.getImgAnswer();
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            resolusi = new int[2];
            resolusi[0] = bm.getWidth();
            resolusi[1] = bm.getHeight();
            int[] res = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
            Bitmap thumbnail = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
            return thumbnail;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
                if (textViewReference != null && resolusi != null) {
                    final TextView textView = textViewReference.get();
                    if (textView != null) {
                        String text;
                        if (indicatorGPS != null)
                            text = resolusi[0] + " x " + resolusi[1] + ". Size " + formattedSize + "\n" + indicatorGPS;
                        else
                            text = resolusi[0] + " x " + resolusi[1] + ". Size " + formattedSize;
                        textView.setText(text);
                    }
                }
            }
        }
    }

    class SingleArrayAdapter extends BaseAdapter implements Filterable {
        private static final int MAX_RESULTS = 10;
        private Context mContext;
        private QuestionBean bean;
        private List<String> resultList = new ArrayList<>();

        public SingleArrayAdapter(Context context, QuestionBean bean) {
            this.mContext = context;
            this.bean = bean;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.autotext_list, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.textauto)).setText(getItem(position));
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        List<String> books = LookupDataAccess.getAllCodeByFilter(mContext, bean.getLov_group(), constraint.toString(), MAX_RESULTS);

                        // Assign the data to the FilterResults
                        filterResults.values = books;
                        filterResults.count = books.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        resultList = (List<String>) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public class SuggestionArrayAdapter extends BaseAdapter implements Filterable {
        private static final int MAX_RESULTS = 10;
        private Context mContext;
        private QuestionBean bean;
        private List<OptionAnswerBean> resultList = new ArrayList<>();

        public SuggestionArrayAdapter(Context context, QuestionBean bean) {
            this.mContext = context;
            this.bean = bean;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        public List<OptionAnswerBean> getOptions() {
            return resultList;
        }

        @Override
        public OptionAnswerBean getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.autotext_list, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.textauto)).setText(getItem(position).toString());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        List<OptionAnswerBean> books = OptionAnswerBean.getOptionList(LookupDataAccess.getAllByLovGroupWithFilter(mContext, bean.getLov_group(), constraint.toString(), 10));
                        // Assign the data to the FilterResults
                        filterResults.values = books;
                        filterResults.count = books.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        resultList = (List<OptionAnswerBean>) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}
