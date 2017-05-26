package org.rowanacm.android;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
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

        appCardBuilder.addItem(new MaterialAboutTitleItem(R.string.app_name, R.mipmap.ic_launcher));

        appCardBuilder.addItem(new MaterialAboutActionItem(
                getString(R.string.version),
                ExternalAppUtils.getVersionName(AboutActivity.this),
                ContextCompat.getDrawable(this, R.drawable.ic_about_info),
                new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        if (Math.random() < 0.1) {
                            Toast.makeText(AboutActivity.this, "Easter \uD83E\uDD5A", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));

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
                        Toast.makeText(AboutActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
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
        supportCardBuilder.title(R.string.support_development);
        supportCardBuilder.addItem(buildCard(R.string.report_bugs, R.string.bug_subtext, R.drawable.ic_settings_black_48dp, getString(R.string.bug_report_url)));
        supportCardBuilder.addItem(buildCard(R.string.rate_title, R.string.rate_description, R.drawable.ic_play_store,  getString(R.string.play_store_url)));

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

    private MaterialAboutActionItem buildCard(@StringRes int text, @StringRes int subtext, @DrawableRes int icon, final String url) {
        return new MaterialAboutActionItem.Builder()
                .text(text).subText(subtext)
                .icon(icon)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        ExternalAppUtils.openUrl(AboutActivity.this, url);
                    }
                }).build();
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }


}