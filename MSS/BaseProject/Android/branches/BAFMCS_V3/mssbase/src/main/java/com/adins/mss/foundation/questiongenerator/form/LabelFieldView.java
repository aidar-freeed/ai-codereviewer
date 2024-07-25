package com.adins.mss.foundation.questiongenerator.form;

import android.content.Context;
import android.widget.EditText;

import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

public class LabelFieldView extends QuestionView {

    private EditText lblValue;
    private boolean separateThousands;

    public LabelFieldView(Context context, QuestionBean bean) {
        super(context, bean);

        setOrientation(VERTICAL);

        lblValue = new EditText(context);
        lblValue.setEnabled(false);
//		lblValue.setTextColor(Color.BLACK);

        addView(lblValue, defLayout);
    }

    public LabelFieldView(Context context, QuestionBean bean,
                          String label, boolean separateThousands) {
        this(context, bean);
        setLabelText(label);
        this.separateThousands = separateThousands;

        String defAnswer = bean.getAnswer();
        if (defAnswer != null && defAnswer.length() > 0) {
            setValue(defAnswer);
        }
    }

    public String getValue() {
        return lblValue.getText().toString();
    }

    public void setValue(String value) {
        if (separateThousands) {
            value = Tool.separateThousand(value);
        }
        lblValue.setText(value);
    }

    public void updateValue() {
        String value = getQuestionBean().getAnswer();
        if (separateThousands) {
            value = Tool.separateThousand(value);
        }
        lblValue.setText(value);
    }
}
