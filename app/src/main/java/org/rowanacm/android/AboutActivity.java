package org.rowanacm.android;

import android.content.Context;
import android.us.acm.R;
import android.widget.Toast;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.model.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.danielstone.materialaboutlibrary.model.MaterialAboutTitleItem;

import org.rowanacm.android.utils.ExternalAppUtils;

public class AboutActivity extends MaterialAboutActivity {

    @Override
    protected MaterialAboutList getMaterialAboutList(Context context) {

        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();

        // Add items to card
        String title = getString(R.string.app_name);

        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(title)
                .icon(R.mipmap.ic_launcher)
                .build());

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.version)
                .subText(ExternalAppUtils.getVersionName(AboutActivity.this))
                .icon(R.drawable.ic_about_info)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        if (Math.random() < 0.1) {
                            Toast.makeText(AboutActivity.this, "Easter \uD83E\uDD5A", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build());
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.changelog)
                .icon(R.drawable.ic_settings_black_48dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        ExternalAppUtils.openUrl(AboutActivity.this, getString(R.string.changelog_url));
                    }
                })
                .build());
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.licenses)
                .icon(R.drawable.ic_settings_black_48dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Toast.makeText(AboutActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                    }
                })
                .build());

        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title(R.string.author);

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Rowan Mobile App Committee")
                .icon(R.drawable.ic_settings_black_48dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        ExternalAppUtils.openUrl(AboutActivity.this, getString(R.string.committees_url));
                    }
                })
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.view_source)
                .icon(R.drawable.ic_settings_black_48dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        ExternalAppUtils.openUrl(AboutActivity.this, getString(R.string.github_app_url));
                    }
                })
                .build());

        MaterialAboutCard.Builder supportCardBuilder = new MaterialAboutCard.Builder();
        supportCardBuilder.title("Support Development");
        supportCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.report_bugs)
                .subText("Report bugs or request new features.")
                .icon(R.drawable.ic_settings_black_48dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        ExternalAppUtils.openUrl(AboutActivity.this, getString(R.string.bug_report_url));
                    }
                })
                .build());

        supportCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.rate_title)
                .subText(R.string.rate_description)
                .icon(R.drawable.ic_play_store)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        ExternalAppUtils.openUrl(AboutActivity.this, getString(R.string.play_store_url));
                    }
                })
                .build());

        supportCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.privacy_policy)
                .icon(R.drawable.ic_settings_black_48dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        ExternalAppUtils.openUrl(AboutActivity.this, getString(R.string.privacy_policy_url));
                    }
                })
                .build());

        return new MaterialAboutList.Builder()
                .addCard(appCardBuilder.build())
                .addCard(authorCardBuilder.build())
                .addCard(supportCardBuilder.build())
                .build();
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }


}