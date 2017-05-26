package org.rowanacm.android.announcement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.rowanacm.android.R;
import org.rowanacm.android.utils.ExternalAppUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnnouncementActivity extends AppCompatActivity {
    private static final String LOG_TAG = Announcement.class.getSimpleName();

    private Announcement announcement;

    @BindView(R.id.announcement_imageview) ImageView announcementImageView;
    @BindView(R.id.announcement_text_view) TextView announcementTextView;
    @BindView(R.id.committee_text_view) TextView committeeTextView;
    @BindView(R.id.date_text_view) TextView dateTextView;
    @BindView(R.id.announcement_url_button) Button urlButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            announcement = (Announcement) savedInstanceState.getSerializable("announcement");
        }
        if (announcement == null) {
            Bundle extras = getIntent().getExtras();
            announcement = (Announcement) extras.getSerializable("announcement");
        }

        announcementTextView.setText(announcement.getText());
        committeeTextView.setText(announcement.getCommittee());
        dateTextView.setText(getRelativeDate(announcement));
        if (announcement.getUrl() == null) {
            urlButton.setVisibility(View.GONE);
        }

        if (announcement.getImageUrl() != null) {
            Picasso.with(this)
                    .load(announcement.getImageUrl())
                    .fit()
                    .into(announcementImageView);
        } else {
            announcementImageView.setVisibility(View.GONE);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("announcement", announcement);
        super.onSaveInstanceState(outState);
    }

    private String getRelativeDate(Announcement announcement) {
        long timestamp = announcement.getTimestamp() * 1000;
        long now = new Date().getTime();
        return DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.SECOND_IN_MILLIS).toString();
    }

    @OnClick(R.id.announcement_url_button)
    public void openAnnoucementUrl() {
        ExternalAppUtils.openUrl(this, announcement.getUrl());
    }
}
