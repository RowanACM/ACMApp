package org.rowanacm.android.announcement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.rowanacm.android.R;
import org.rowanacm.android.utils.ExternalAppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnnouncementActivity extends AppCompatActivity {
    private static final String LOG_TAG = Announcement.class.getSimpleName();

    public static final String ANNOUNCEMENT_EXTRA_KEY = "announcement";
    private static final String ANNOUNCEMENT_INSTANT_STATE_KEY = "announcement";

    private Announcement announcement;

    @BindView(R.id.announcement_imageview) ImageView announcementImageView;
    @BindView(R.id.title_text_view) TextView titleTextView;
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
            announcement = (Announcement) savedInstanceState.getSerializable(ANNOUNCEMENT_INSTANT_STATE_KEY);
        }
        if (announcement == null) {
            Bundle extras = getIntent().getExtras();
            announcement = (Announcement) extras.getSerializable(ANNOUNCEMENT_EXTRA_KEY);
        }

        titleTextView.setText(announcement.getTitle());
        announcementTextView.setText(announcement.getText());
        committeeTextView.setText(announcement.getCommittee());
        dateTextView.setText(announcement.getRelativeDate());
        if (announcement.getUrl() == null) {
            urlButton.setVisibility(View.GONE);
        }

        setTitle(announcement.getCommittee());

        /*
        if (announcement.getImageUrl() != null) {
            loadHeaderImage();
        } else {
            announcementImageView.setVisibility(View.GONE);
        }
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ANNOUNCEMENT_INSTANT_STATE_KEY, announcement);
        super.onSaveInstanceState(outState);
    }

    private void loadHeaderImage() {
        Picasso.with(this)
                .load(announcement.getImageUrl())
                .fit()
                .into(announcementImageView);
    }

    @OnClick(R.id.announcement_url_button)
    public void openAnnouncementUrl() {
        ExternalAppUtils.openUrl(this, announcement.getUrl());
    }
}
