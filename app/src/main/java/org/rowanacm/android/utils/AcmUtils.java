package org.rowanacm.android.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class AcmUtils {

    public static String readSharedPreferenceString(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(
                "org.rowanacm.android", Context.MODE_PRIVATE);

        return prefs.getString(key, ""); // the default value is an empty string
    }
}
