package ru.it4u24.joker;

import android.content.SharedPreferences;
import android.util.Log;

public class KeystoreSharedPreferences implements Keystore {

    private String login;
    private String password;
    private SharedPreferences sharedPreferences;

    public final String KEY_LOG_SERVICE1C = "service1cLog";
    public final String KEY_PAS_SERVICE1C = "service1cPas";

    public KeystoreSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public String getLogin(String key) {
        return sharedPreferences.getString(key, "");
    }

    @Override
    public String getPassword(String key) {
        return sharedPreferences.getString(key, "");
    }

    @Override
    public void setLogin(String key, String login) {
        setString(key, login);
    }

    @Override
    public void setPassword(String key, String password) {
        setString(key, password);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(key, value);
        ed.apply();
        Log.d("myLogs", "Сохранено: " + key + "=" + value);
    }

    public void setLoginPasswordService1c(String login, String password) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(KEY_LOG_SERVICE1C, login);
        ed.putString(KEY_PAS_SERVICE1C, password);
        ed.apply();
        Log.d("myLogs", "Сохранено логин/пароль: " + login  + "/" + password);
    }
}
