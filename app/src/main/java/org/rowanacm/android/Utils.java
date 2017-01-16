package org.rowanacm.android;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

public class Utils {

    /**
     * Open a url in a Chrome Custom Tab
     * @param url the url to open
     */
    public static void openUrl(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    public static String readSharedPreferenceString(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(
                "org.rowanacm.android", Context.MODE_PRIVATE);

        return prefs.getString(key, ""); // the default value is an empty string
    }
}
