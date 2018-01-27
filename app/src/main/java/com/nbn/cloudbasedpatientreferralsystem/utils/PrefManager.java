package com.nbn.cloudbasedpatientreferralsystem.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dudupoo on 25/1/18.
 */

public class PrefManager
{
    Context context;
    private SharedPreferences sharedPreferences;
    private String profile;

    public PrefManager(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(Constants.PREF_MANAGER, Context.MODE_PRIVATE);
    }

    public String getProfile() {
        return sharedPreferences.getString(Constants.KEY_LOGIN_INTENT, "");
    }

    public void setProfile(String mProfile) {
        sharedPreferences.edit().putString(Constants.KEY_LOGIN_INTENT, mProfile).apply();
    }


}
