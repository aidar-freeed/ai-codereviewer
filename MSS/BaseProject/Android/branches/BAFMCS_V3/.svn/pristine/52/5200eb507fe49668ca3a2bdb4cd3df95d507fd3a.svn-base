package com.adins.mss.odr;

import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.NotEqualSymbol;
import com.gadberry.utility.expression.Expression;
import com.gadberry.utility.expression.OperatorSet;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Before
    public void initializeUser() throws Exception{
        User user = new User("e5131499-fd95-480d-821a-07508e79b911");
        user.setLogin_id("gigin@demo");
        user.setBranch_id("HO");
        user.setBranch_name("Head Office");
        user.setDealer_name("DLR NM");
        user.setFlag_job("JOB MH");
        GlobalData.getSharedGlobalData().setUser(user);
    }

    @Test
    public void testingRelevant() throws Exception {

        String relevantExpression = "({$LOGIN_ID}==gigin) && ({$FLAG_JOB}==JOBMH)";
        String convertedExpression = new String(relevantExpression);        //make a copy of
        if (convertedExpression == null || convertedExpression.length() == 0) {
            Log.i("info", "expresssion null");
        } else {

            //TODO, use extractIdentifierFromString next time to simplify
            boolean needReplacing = true;
            while (needReplacing) {

                int idxOfOpenBrace = convertedExpression.indexOf('{');
                if (idxOfOpenBrace != -1) {                    //there's {, prepare to replace what inside the {}
                    int idxOfCloseBrace = convertedExpression.indexOf('}');
                    String identifier = convertedExpression.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                    int idxOfOpenAbs = identifier.indexOf("$");
                    if(idxOfOpenAbs != -1){
                        String finalIdentifier = identifier.substring(idxOfOpenAbs+1);
                        String flatAnswer ="";

                        if(finalIdentifier.equals(Global.IDF_LOGIN_ID)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                            int idxOfOpenAt = flatAnswer.indexOf('@');
                            if (idxOfOpenAt != -1) {
                                flatAnswer = flatAnswer.substring(0, idxOfOpenAt);
                            }
                        }else if(finalIdentifier.equals(Global.IDF_BRANCH_ID)){
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getBranch_id();
                        }else if(finalIdentifier.equals(Global.IDF_BRANCH_NAME)){
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getBranch_name();
                        }else if(finalIdentifier.equals(Global.IDF_UUID_USER)){
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                        }else if(finalIdentifier.equals(Global.IDF_JOB)){
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getFlag_job();
                        }else if(finalIdentifier.equals(Global.IDF_DEALER_NAME)){
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getDealer_name();
                        }


                        if (flatAnswer != null && flatAnswer.length() > 0) {
                            convertedExpression = convertedExpression.replace("{"+identifier+"}", flatAnswer);
                        } else {            //if there's no answer, just hide the question
                            Logger.e("error",flatAnswer);
                        }

                    }else {
                        String flatAnswer = "Bcd+";
                        if (flatAnswer != null && flatAnswer.length() > 0) {
                            //Glen 22 Oct 2014, enable multi-depth checking for 'multiple' question
                            //NOTE: though it's possible to just iterate on flatAnswer substrings, we prefer to stay on method if size is 1
                            String answers[] = Tool.split(flatAnswer, Global.DELIMETER_DATA);
                            if (answers.length == 1) {
//									convertedExpression = convertedExpression.replace("{"+identifier+"}", flatAnswer);
                                convertedExpression = convertedExpression.replace("{" + identifier + "}", answers[0]);
                            }
                        }
                    }
                }
                else {
                    needReplacing = false;
                }

            }
            try {
                OperatorSet opSet = OperatorSet.getStandardOperatorSet();
                opSet.addOperator("!=", NotEqualSymbol.class);
                Expression exp = new Expression(convertedExpression);
                exp.setOperatorSet(opSet);
                boolean result =  exp.evaluate().toBoolean();
                assertEquals(true,result);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}