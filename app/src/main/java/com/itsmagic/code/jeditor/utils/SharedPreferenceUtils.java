package com.itsmagic.code.jeditor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedPreferenceUtils {
	// private static final Logger LOG = new Logger("ESM/SharedPreference");

    private static volatile SharedPreferenceUtils INSTANCE;

    public static synchronized SharedPreferenceUtils getInstance() {
        if (INSTANCE == null) { throw new RuntimeException("Initialize first, use 'SharedPreferenceUtils#initializeInstance(AppCompatActivity)'"); }

        return INSTANCE;
    }

    public static synchronized SharedPreferenceUtils initializeInstance(@NonNull AppCompatActivity activity) {
        if (INSTANCE == null) {
			INSTANCE = new SharedPreferenceUtils(activity);
		}
		
        return INSTANCE;
    }

    private final AppCompatActivity activity;
    private final SharedPreferences SP_INSTANCE;
	
    private SharedPreferenceUtils(AppCompatActivity activity) {
        if (INSTANCE != null) { throw new RuntimeException("Please use 'SharedPreferenceUtils#getInstance()'!"); }

        this.activity = activity;
		this.SP_INSTANCE = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
	}
	
	public final void putBool(String key, boolean state) {
		SP_INSTANCE.edit().putBoolean(key, state).apply();
	}
	
	public final boolean getBoolFallback(String key, boolean fallback) {
		return SP_INSTANCE.getBoolean(key, fallback);
	}
	
	public final boolean getBool(String key) {
		return getBoolFallback(key, false);
	}
	
	public final void putString(String key, String value) {
		SP_INSTANCE.edit().putString(key, value).apply();
	}
	
	public final String getStringFallback(String key, String fallback) {
		return SP_INSTANCE.getString(key, fallback);
	}
	
	public final String getString(String key) {
		return getStringFallback(key, null);
	}
	
	public static void dispose() {
		INSTANCE = null;
	}
}
