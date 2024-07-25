package com.adins.mss.base.util;

public class NPWPValidation {
    private String nomor;
    private int factors, digit, kali, round, selisih;
    private int hasil = 0;

    public NPWPValidation() {

    }

    public NPWPValidation(String nomor) {
        this.setNomor(nomor);
    }

    public boolean getValidation() {
        if (nomor.length() != 15) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            factors = i % 2 == 0 ? 1 : 2;
            digit = Integer.parseInt(Character.toString(nomor.charAt(i)));
            kali = factors * digit;
            if (Integer.toString(kali).length() == 2) {
                hasil += Integer.parseInt(Character.toString(Integer.toString(kali).charAt(0))) + Integer.parseInt(Character.toString(Integer.toString(kali).charAt(1)));
            } else {
                hasil += kali;
            }
        }
        round = (int) Math.ceil((double) hasil / 10) * 10;
        selisih = round - hasil;
        return selisih == Integer.parseInt(Character.toString(nomor.charAt(8)));
    }

    public boolean getValidation(String nomor) {
        this.nomor = nomor;
        return this.getValidation();
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

}
