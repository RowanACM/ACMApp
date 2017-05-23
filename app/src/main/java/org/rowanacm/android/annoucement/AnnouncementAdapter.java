package org.rowanacm.android.annoucement;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.us.acm.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.rowanacm.android.SearchableAdapter;

import java.util.List;

import static butterknife.ButterKnife.findById;

public class AnnouncementAdapter extends SearchableAdapter<AnnouncementAdapter.AnnouncementViewHolder, Announcement> {

    private static final String LOG_TAG = AnnouncementAdapter.class.getSimpleName();

    private Fragment fragment;

    public class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, bodyTextView;
        CardView cardView;

        public AnnouncementViewHolder(View view) {
            super(view);

            cardView = findById(view, R.id.card_view);
            titleTextView = findById(view, R.id.announcement_title_view);
            bodyTextView = findById(view, R.id.announcement_desc_view);
        }

    }

    public AnnouncementAdapter(List<Announcement> announcementList, Fragment fragment) {
        super(announcementList);
        this.fragment = fragment;
    }

    public AnnouncementAdapter(List<Announcement> announcementList, Fragment fragment, int numToDisplay) {
        super(announcementList, numToDisplay);
        this.fragment = fragment;
    }

    @Override
    public AnnouncementViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_view, parent, false);

        return new AnnouncementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AnnouncementViewHolder holder, int position) {
        final Announcement announcement = list.get(position);

        // If the announcement does not have all of its information, do not show it
        if (announcement != null) {
            TextView nameTextView = holder.titleTextView;
            nameTextView.setText(announcement.getTitle());

            TextView descriptionTextView = holder.bodyTextView;
            descriptionTextView.setText(announcement.getText());

            CardView cardView = holder.cardView;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AnnouncementActivity.class);
                    intent.putExtra("announcement", announcement);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

}