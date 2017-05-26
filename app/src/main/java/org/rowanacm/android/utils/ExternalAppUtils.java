package org.rowanacm.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.us.acm.R;


public class ExternalAppUtils {

    public static void openPlayStore(Context context, String packageName) {
        String url = "https://play.google.com/store/apps/details?id=" + packageName;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void openUrl(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.primary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }

    }

    public static void sendEmail(Activity activity, String to, String subject, String chooserTitle) {
        ShareCompat.IntentBuilder.from(activity)
                .setType("message/rfc822")
                .addEmailTo(to)
                .setSubject(subject)
                //.setText(body)
                //.setHtmlText(body) //If you are using HTML in your body text
                .setChooserTitle(chooserTitle)
                .startChooser();
    }
}
