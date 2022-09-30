package com.example.photoshare.tool;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.photoshare.R;

public class Tool_SharedPreferencesManager {

    private final Context context;

    private String usernameKey;
    private String passwordKey;
    private String rememberPasswordKey;
    private String whetherLoginKey;

    private SharedPreferences spFile;
    private SharedPreferences.Editor editor ;

    public Tool_SharedPreferencesManager(Context context){
        this.context = context;
        init();
    }

    private void init(){
        String spFileName = context.getResources().getString(R.string.shared_preferences_file_name);
        spFile = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        editor = spFile.edit();

        usernameKey = context.getResources().getString(R.string.login_account_name);
        passwordKey = context.getResources().getString(R.string.login_password);
        rememberPasswordKey = context.getResources().getString(R.string.login_remember_password);
        whetherLoginKey = context.getResources().getString(R.string.login_whether_login);
    }

    public String getUsernameKey(){
        return spFile.getString(usernameKey, null);
    }

    public String getPasswordKey(){
        return spFile.getString(passwordKey, null);
    }

    public boolean getRememberPassword(){
        return spFile.getBoolean(rememberPasswordKey, false);
    }

    public boolean getWhetherLoginKey(){
        return spFile.getBoolean(whetherLoginKey, false);
    }

    public void saveData(String username,String password,boolean rememberPassword,boolean whetherLogin){
        editor.putString(usernameKey, username);
        editor.putString(passwordKey, password);
        editor.putBoolean(rememberPasswordKey, rememberPassword);
        editor.putBoolean(whetherLoginKey,whetherLogin);
        editor.apply();
    }

    public void removeData(){
        editor.remove(usernameKey);
        editor.remove(passwordKey);
        editor.remove(rememberPasswordKey);
        editor.remove(whetherLoginKey);
        editor.apply();
    }

    public void clearData(){
        editor.clear();
        editor.apply();
    }
}
