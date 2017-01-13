package org.rowanacm.android.annoucement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.us.acm.R;
import android.widget.TextView;

import java.util.Date;

public class AnnouncementActivity extends AppCompatActivity {
    Announcement announcement;
    private static final String TAG = "AnnouncementActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            announcement = (Announcement) extras.getSerializable("announcement");

            TextView announcementTextView = (TextView) findViewById(R.id.announcement_text_view);
            TextView committeeTextView = (TextView) findViewById(R.id.committee_text_view);
            TextView dateTextView = (TextView) findViewById(R.id.date_text_view);
            announcementTextView.setText(announcement.getText());
            committeeTextView.setText(announcement.getCommittee());

            long timestamp = announcement.getTimestamp() * 1000;
            long now = new Date().getTime();
            String relativeTime = DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.SECOND_IN_MILLIS).toString();
            dateTextView.setText(relativeTime);
        }
    }
}
