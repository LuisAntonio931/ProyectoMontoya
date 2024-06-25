package com.example.mione_app;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public LocalStorage(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("STORAGE_LOGIN_API", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getToken() {
        return sharedPreferences.getString("TOKEN", "");
    }

    public void setToken(String token) {
        editor.putString("TOKEN", token);
        editor.commit();
    }
}
