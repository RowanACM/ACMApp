package org.rowanacm.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

/**
 * A wrapper for SharedPreferences
 */
public class Preferences {

    private static final String STRING_DEFAULT = "";
    private static final boolean BOOLEAN_DEFAULT = false;
    private static final int INT_DEFAULT = 0;
    private static final long LONG_DEFAULT = 0L;

    private SharedPreferences preferences;
    private Context context;

    public Preferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public String getString(@StringRes int keyRes, String defaultValue) {
        return getString(context.getString(keyRes), defaultValue);
    }

    public String getString(String key) {
        return preferences.getString(key, STRING_DEFAULT);
    }

    public String getString(@StringRes int keyRes) {
        return getString(context.getString(keyRes), STRING_DEFAULT);
    }

    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public int getInt(@StringRes int keyRes, int defaultValue) {
        return getInt(context.getString(keyRes), defaultValue);
    }

    public int getInt(String key) {
        return preferences.getInt(key, INT_DEFAULT);
    }

    public int getInt(@StringRes int keyRes) {
        return getInt(context.getString(keyRes), INT_DEFAULT);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public boolean getBoolean(@StringRes int keyRes, boolean defaultValue) {
        return getBoolean(context.getString(keyRes), defaultValue);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, BOOLEAN_DEFAULT);
    }

    public boolean getBoolean(@StringRes int keyRes) {
        return getBoolean(context.getString(keyRes), BOOLEAN_DEFAULT);
    }

    public long getLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    public long getLong(@StringRes int keyRes, long defaultValue) {
        return getLong(context.getString(keyRes), defaultValue);
    }

    public long getLong(String key) {
        return preferences.getLong(key, LONG_DEFAULT);
    }

    public long getLong(@StringRes int keyRes) {
        return getLong(context.getString(keyRes), LONG_DEFAULT);
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void set(@StringRes int keyRes, String value) {
        set(context.getString(keyRes), value);
    }

    public void set(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void set(@StringRes int keyRes, boolean value) {
        set(context.getString(keyRes), value);
    }

    public void set(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void set(@StringRes int keyRes, int value) {
        set(context.getString(keyRes), value);
    }

    public void set(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void set(@StringRes int keyRes, long value) {
        set(context.getString(keyRes), value);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void remove(@StringRes int key) {
        remove(context.getString(key));
    }
}