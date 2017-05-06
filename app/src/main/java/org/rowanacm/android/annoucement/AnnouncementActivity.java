package org.rowanacm.android.annoucement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.us.acm.R;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnnouncementActivity extends AppCompatActivity {
    private static final String LOG_TAG = Announcement.class.getSimpleName();

    @BindView(R.id.announcement_text_view) TextView announcementTextView;
    @BindView(R.id.committee_text_view) TextView committeeTextView;
    @BindView(R.id.date_text_view) TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            Announcement announcement = (Announcement) extras.getSerializable("announcement");

            announcementTextView.setText(announcement.getText());
            committeeTextView.setText(announcement.getCommittee());
            dateTextView.setText(getRelativeDate(announcement));
        }
    }

    private String getRelativeDate(Announcement announcement) {
        long timestamp = announcement.getTimestamp() * 1000;
        long now = new Date().getTime();
        return DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.SECOND_IN_MILLIS).toString();
    }
}
