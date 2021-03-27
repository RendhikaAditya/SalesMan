package com.example.salesman.util;


import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "data_app";

    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIdUser(String idUser){
        editor.putString("idUser", idUser);
        editor.apply();
    }
    public String getIdUser(){
        return pref.getString("idUser", "");
    }

    public void setNamaUser(String namaUser){
        editor.putString("namaUser", namaUser);
        editor.apply();
    }
    public String getNamaUser(){
        return pref.getString("namaUser", "");
    }


    public void setLvl(String lvl){
        editor.putString("lvl", lvl);
        editor.apply();
    }
    public String getLvl(){
        return pref.getString("lvl", "");
    }


    public boolean getLoginStatus(){
        return pref.getBoolean("login", false);
    }

    public void setLoginStatus(boolean islogin){
        editor.putBoolean("login", islogin);
        editor.apply();
    }




}
