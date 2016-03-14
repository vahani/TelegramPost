package org.telegram.messenger.postgram.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ShareData {

    public static void saveData(Context con, String variable, String data) {
        SharedPreferences prefs = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(con);
        prefs.edit().putString(variable, data).commit();
    }

    public static String getData(Context con, String variable, String defaultValue) {
        SharedPreferences prefs = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(con);

        String data = prefs.getString(variable, defaultValue);
        return data;
    }

}