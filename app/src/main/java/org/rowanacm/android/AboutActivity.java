package org.rowanacm.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.us.acm.BuildConfig;
import android.us.acm.R;
import android.widget.Toast;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.model.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.danielstone.materialaboutlibrary.model.MaterialAboutTitleItem;

public class AboutActivity extends MaterialAboutActivity {

    @Override
    protected MaterialAboutList getMaterialAboutList(Context context) {

        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();

        // Add items to card
        String title = BuildConfig.DEBUG ? "Rowan ACM" : "Rowan ACM DEBUG";

        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(title)
                .icon(R.mipmap.ic_launcher)
                .build());

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Version")
                .subText(getVersionName())
                .icon(R.drawable.ic_about_info)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        if(Math.random() < 0.1)
                            Toast.makeText(AboutActivity.this, "Easter \uD83E\uDD5A", Toast.LENGTH_SHORT).show();
                    }
                })
                .build());
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Changelog")
                .icon(R.drawable.ic_settings_black_48dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        openUrl("https://github.com/RowanACM/ACMAppAndroid/commits/master");
                    }
                })
                .build());
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Licenses")
                .icon(R.drawable.ic_settings_black_48dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Toast.makeText(AboutActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                    }
                })
                .build());

        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title("Author");

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Rowan Mobile App Committee")
                .icon(R.drawable.ic_settings_black_48dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        openUrl("https://rowanacm.org/committees.html");
                    }
                })
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("View source on GitHub")
                .icon(R.drawable.ic_settings_black_48dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        openUrl("https://github.com/RowanACM/ACMAppAndroid");
                    }
                })
                .build());

        MaterialAboutCard.Builder supportCardBuilder = new MaterialAboutCard.Builder();
        supportCardBuilder.title("Support Development");
        supportCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Report Bugs")
                .subText("Report bugs or request new features.")
                .icon(R.drawable.ic_settings_black_48dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        openUrl("https://github.com/RowanACM/ACMAppAndroid/issues");
                    }
                })
                .build());

        supportCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Rate on the Play Store")
                .subText("Please rate this app on the Play Store")
                .icon(R.drawable.ic_play_store)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Utils.openUrl(AboutActivity.this, "https://play.google.com/store/apps/details?id=org.rowanacm.android");
                    }
                })
                .build());

        return new MaterialAboutList.Builder()
                .addCard(appCardBuilder.build())
                .addCard(authorCardBuilder.build())
                .addCard(supportCardBuilder.build())
                .build();
    }

    private String getVersionName() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }

    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }

    /**
     * Open a url in a Chrome Custom Tab
     * @param url the url to open
     */
    private void openUrl(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

}