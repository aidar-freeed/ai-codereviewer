package com.adins.mss.foundation.print;

import com.adins.mss.foundation.formatter.Tool;

import java.util.Vector;

public class FontSato {
    public FontSato() {

    }

    //untuk mendapatkan ukuran 1 character setiap font
    public static double getFontSize(String character) {

        if ("I".equals(character) || "i".equals(character) || "l".equals(character) || ".".equals(character) || ",".equals(character) || ";".equals(character) || ":".equals(character)) {
            return 4;
        } else if ("j".equals(character)) {
            return 5.01;
        } else if ("f".equals(character) || "t".equals(character)) {
            return 6;
        } else if ("r".equals(character) || "z".equals(character)) {
            return 6.98;
        } else if ("F".equals(character) || "L".equals(character)) {
            return 8;
        } else if ("b".equals(character) || "h".equals(character) || "k".equals(character) || "n".equals(character) || "p".equals(character)) {
            return 8.93;
        } else if ("a".equals(character) || "c".equals(character) || "d".equals(character) || "e".equals(character) || "g".equals(character) || "o".equals(character) || "q".equals(character) || "s".equals(character) || "u".equals(character) || "v".equals(character) || "y".equals(character)) {
            return 9.03;
        } else if ("E".equals(character) || "J".equals(character) || "0".equals(character) || "1".equals(character) || "2".equals(character) || "3".equals(character) || "4".equals(character) || "5".equals(character) || "6".equals(character) || "7".equals(character) || "8".equals(character) || "9".equals(character)) {
            return 9.08;
        } else if (" ".equals(character)) {
            return 9.71;
        } else if ("B".equals(character) || "C".equals(character) || "D".equals(character) || "G".equals(character) || "H".equals(character) || "K".equals(character) || "O".equals(character) || "P".equals(character) || "R".equals(character) || "S".equals(character) || "T".equals(character) || "U".equals(character) || "Z".equals(character) || "/".equals(character)) {
            return 10.01;
        } else if ("x".equals(character)) {
            return 10.03;
        } else if ("N".equals(character) || "Q".equals(character) || "X".equals(character)) {
            return 10.97;
        } else if ("V".equals(character)) {
            return 11.1;
        } else if ("A".equals(character) || "Y".equals(character)) {
            return 12;
        } else if ("M".equals(character) || "m".equals(character)) {
            return 13.98;
        } else if ("w".equals(character)) {
            return 14.7;
        } else if ("W".equals(character)) {
            return 17.86;
        } else {
            return 12;
        }


    }

    //untuk mendapatkan ukuran 1 baris setiap font
    public static int getLength(String sentence) {
        double lenght = 0;

        for (int i = 0; i < sentence.length(); i++) {
            lenght = lenght + getFontSize(sentence.charAt(i) + "");

        }
        return (int) lenght;
    }

    //memotong setiap kalimat jika kepanjangan, dan menjadikan baris berikutnya
    public static Vector wrap(int lenghtPixel, String text) {

        Vector result = new Vector();
        String[] arrayToPrint = Tool.split(text, " ");
        StringBuffer totalChar = new StringBuffer();
        int lenghtChar = 384;

        if (lenghtPixel > 0) {
            lenghtChar = lenghtPixel;
        }

        for (int i = 0; i < arrayToPrint.length; i++) {

            SentencesSato sentencesSato = new SentencesSato();
            int lenghtSentemce = 0;
            int lenghtSentemce2 = 0;
            if (i == 0) { //untuk kata pertama
                lenghtSentemce = getLength(arrayToPrint[i].trim());
                if (lenghtSentemce < lenghtChar) {
                    totalChar.append(arrayToPrint[i]);
                } else {// jika setelah  pertama ternyata lebih dari 1 baris

                    sentencesSato.setSentence(arrayToPrint[i].trim());
                    sentencesSato.setLenghtSentemce(lenghtChar);
                    result.addElement(sentencesSato);

                    totalChar = new StringBuffer();
                }
            } else {
                //10 adalah nilai untuk spasi, karena akan ditambahkan spasi
                lenghtSentemce = getLength(totalChar.toString()) + 10;
                lenghtSentemce2 = getLength(arrayToPrint[i].trim());

                if (lenghtSentemce2 >= lenghtChar && totalChar.length() <= 1) {
                    // jika setelah huruf pertama ternyata lebih dari 1 baris
                    sentencesSato.setSentence(arrayToPrint[i].trim());
                    sentencesSato.setLenghtSentemce(lenghtChar);
                    result.addElement(sentencesSato);

                    totalChar = new StringBuffer();

                } else {

                    if ((lenghtSentemce + lenghtSentemce2) < lenghtChar) {
                        totalChar.append(" " + arrayToPrint[i].trim());

                    } else {
                        i--;
                        lenghtSentemce = getLength(totalChar.toString().trim());
                        sentencesSato.setSentence(totalChar.toString().trim());
                        sentencesSato.setLenghtSentemce(lenghtSentemce);
                        result.addElement(sentencesSato);


                        totalChar = new StringBuffer();
                    }
                }

            }

        }
        int lenghtFinish = getLength(totalChar.toString());
        if (lenghtFinish > 0 && lenghtFinish < lenghtChar) {
            SentencesSato sentencesSato = new SentencesSato();
            sentencesSato.setSentence(totalChar.toString().trim());
            sentencesSato.setLenghtSentemce(getLength(totalChar.toString().trim()));
            result.addElement(sentencesSato);

        }

        return result;
    }

}