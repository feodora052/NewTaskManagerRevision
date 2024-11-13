package com.example.newtaskmanager;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String PREF_NAME = "task_manager_pref";
    private static final String KEY_EMAIL = "email";
    private static SharedPrefManager instance;
    private SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public void saveUserEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply(); // Save the changes
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null); // Retrieve the email
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}