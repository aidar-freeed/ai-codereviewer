package com.adins.mss.foundation.rvgenerator;

/**
 * Created by intishar.fa on 09/10/2018.
 */
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RvGenerator {
    private BigInteger taskIdInt,noKontrakId;

    public RvGenerator(){

    }

    public String generateRVNum(String taskId, String noKontrak){

        Date submitDate = Calendar.getInstance().getTime();
        //convert taskId to int
        taskId = convertCharToInt(taskId);
        taskIdInt = new BigInteger(taskId);//use it for multiply operation

        //convert no kontrak to int
        String noKontrakConv = convertCharToInt(noKontrak);
        noKontrakId = new BigInteger(noKontrakConv);//use it for multiply operation
        //pad no kontrak to max 12 char if it's length smaller than 10
        if(noKontrakConv.length() < 10)
            noKontrakConv = paddingSequence(noKontrakConv,12);
        noKontrak = noKontrakConv;

        //string builder for concat all fragment
        StringBuilder fullRvNum = new StringBuilder();

        //fragment 1
        String headFragment = getFragment1(submitDate);
        fullRvNum.append(headFragment);
        System.out.println("head fragment: "+headFragment);

        //fragment 2
        String midFragment = getFragment2(noKontrak);
        fullRvNum.append(midFragment);
        System.out.println("mid fragment: "+midFragment);

        //fragment 3
        String tailFragment = getFragment3();
        fullRvNum.append(tailFragment);
        System.out.println("tail fragment: "+tailFragment);

        System.out.println("Full RV Number: "+fullRvNum.toString());
        return fullRvNum.toString();
    }

    private String formatDate(Date date,String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    private String getFragment1(Date submitDate){
        return formatDate(submitDate,"yyyyMMdd");
    }

    private String getFragment2(String noKontrak){
        StringBuilder sb = new StringBuilder();
        //no kontrak at 5-8th digit
        sb.append(noKontrak.substring(4,8));

        //taskId * 7
        long multTaskId7 = taskIdInt.longValue() * 7;
        String multTaskId7padd = paddingSequence(String.valueOf(multTaskId7),7);
        sb.append(multTaskId7padd.substring(0,7));

        //no kontrak at 9-10th digit
        sb.append(noKontrak.substring(8,10));

        //no kontrak at 1-4th digit
        sb.append(noKontrak.substring(0,4));

        return sb.toString();
    }

    private String getFragment3(){
        BigInteger taskIdMultNoKontrak = taskIdInt.multiply(noKontrakId);

        String taskIdMultNoKontrakStr = String.valueOf(taskIdMultNoKontrak);
        taskIdMultNoKontrakStr = paddingSequence(taskIdMultNoKontrakStr,7);
        int diff = taskIdMultNoKontrakStr.length() - 7;
        String result = null;
        if(diff == 0){
            result = taskIdMultNoKontrakStr;
        }
        else if(diff > 0){
            result = taskIdMultNoKontrakStr.substring(diff,taskIdMultNoKontrakStr.length());
        }
        return result;
    }

    private String convertCharToInt(String charseq){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<charseq.length(); i++){
            byte asciiCode = (byte) charseq.charAt(i);
            if(asciiCode < 48 || asciiCode > 57){//test if this char is not number
                stringBuilder.append(String.valueOf(asciiCode));//append its ascii
            }
            else {//if number just append it
                stringBuilder.append(charseq.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

    private String paddingSequence(String seq,int targetPaddingNum){
        int currentLength = seq.length();
        if(currentLength >= targetPaddingNum)
            return seq;
        else {
            StringBuilder sb = new StringBuilder();
            int diff = targetPaddingNum - currentLength;
            for(int i=0; i<diff; i++){
                sb.append('0');
            }
            sb.append(seq);
            return sb.toString();
        }
    }

}

