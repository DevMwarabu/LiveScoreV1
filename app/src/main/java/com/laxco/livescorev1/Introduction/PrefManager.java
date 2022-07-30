package com.laxco.livescorev1.Introduction;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Timiiza was created by DevMwarabu johmwarabuchone@gmail.com on 1/7/21.
 * Copyright (c) 2021 Laxco Inc-Kericho All rights reserved.
 **/
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String IS_LOGGED_IN = "IsLoggedIn";

    private static final String USER_ID = "user_id";

    private static final String MAP_KEY = "mapkey";


    private static final String ITEMS = "items";
    private static final String LANG = "lang";

    private static final String UNI_ID = "uniID";

    private static final String FACULTY_ID = "facultyID";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setEvaluationType(String type,boolean value) {
        editor.putBoolean(type, value);
        editor.commit();
    }



    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.commit();

    }

    public void setLocalLang(String lang) {
        editor.putString(LANG, lang);
        editor.commit();

    }

    public void setUniID(String uniID) {
        editor.putString(UNI_ID, uniID);
        editor.commit();
    }

    public void setFacultyId(String facultyId) {
        editor.putString(FACULTY_ID, facultyId);
        editor.commit();
    }

    public void setMapKey(String mapKey) {
        editor.putString(MAP_KEY, mapKey);
        editor.commit();
    }

    public void setUserId(String user_id) {
        editor.putString(USER_ID, user_id);
        editor.commit();
    }

    public String getMapKey() {
        return pref.getString(MAP_KEY, null);
    }

    public String getLang() {
        return pref.getString(LANG, "fr");
    }

    public boolean isEvaluationType(String type){
        return pref.getBoolean(type, false);
    }

    public String getFacultyId() {
        return pref.getString(FACULTY_ID, null);
    }

    public String getUniId() {
        return pref.getString(UNI_ID, null);
    }

    public void setLocal(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        _context.getResources().updateConfiguration(configuration, _context.getResources().getDisplayMetrics());
        setLocalLang(language);
    }

    public void clearItems() {
        pref.edit().remove(ITEMS).commit();
    }

    public String getUserId() {
        return pref.getString(USER_ID, null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}
