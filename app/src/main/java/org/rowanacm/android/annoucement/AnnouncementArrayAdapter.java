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

import java.util.ArrayList;
import java.util.List;

public class AnnouncementArrayAdapter extends RecyclerView.Adapter<AnnouncementArrayAdapter.MyViewHolder> {

    private static final String TAG = "AnnouncementArrayAdapter";

    private Fragment fragment;
    private List<Announcement> announcementList;
    private List<Announcement> announcementListAll;

    private int numToDisplay;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, bodyTextView;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view);
            titleTextView = (TextView) view.findViewById(R.id.announcement_title_view);
            bodyTextView = (TextView) view.findViewById(R.id.announcement_desc_view);
        }

    }

    public AnnouncementArrayAdapter(List<Announcement> announcementList, Fragment fragment) {
        // Display 10 announcements by default
        this(announcementList, fragment, 10);
    }

    public AnnouncementArrayAdapter(List<Announcement> announcementList, Fragment fragment, int numToDisplay) {
        this.fragment = fragment;
        this.numToDisplay = numToDisplay;

        this.announcementList = new ArrayList<>();
        this.announcementListAll = announcementList;

        for(int i = 0; i < numToDisplay && i < announcementList.size(); i++) {
            Announcement announcement = announcementList.get(i);
            this.announcementList.add(announcement);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Announcement announcement = announcementList.get(position);

        // If the announcement does not have all of its information, do not show it
        if(announcement != null) {
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

            /*
            View.OnClickListener callOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.openDialer(fragment, Utilities.getFirstPhoneNumber(announcement.getPhoneNumber()));
                }
            };

            TextView phoneTextView = holder.phoneTextView;
            phoneTextView.setText(announcement.getPhoneNumber());
            phoneTextView.setOnClickListener(callOnClick);

            ImageView phoneIcon = holder.callIcon;
            phoneIcon.setOnClickListener(callOnClick);

            View.OnClickListener mapOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utilities.openMapIntent(fragment, Utilities.getMapUri(announcement.getName(), announcement.getCity(), announcement.getState()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            };

            TextView addressTextView =  holder.addressTextView;
            addressTextView.setText(announcement.getFullAddress());
            addressTextView.setOnClickListener(mapOnClick);

            ImageView addressIcon =  holder.addressIcon;
            addressIcon.setOnClickListener(mapOnClick);

            ImageView facilityImageView = holder.facilityImageView;
            Bitmap facilityImage = announcement.getFacilityImage();
            if(facilityImage != null)
                facilityImageView.setImageBitmap(announcement.getFacilityImage());
            else
                facilityImageView.setImageBitmap(BitmapFactory.decodeResource(fragment.getResources(), R.drawable.default_facility_image));

            facilityImageView.setOnClickListener(mapOnClick);

            // Tapping the more info button opens the website
            Button moreInfoButton = holder.moreInfoButton;
            moreInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = Utilities.getFirstPhoneNumber(announcement.getUrl());
                    Utilities.openBrowserIntent(fragment, url);
                }
            });
            */

        }
    }

    public void addItem(Announcement announcement) {
        announcementListAll.add(announcement);
        if(announcementList.size() < numToDisplay)
            announcementList.add(announcement);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public void filter(String text) {

        if(text.isEmpty()){
            announcementList.clear();

            for(int i = 0; i < numToDisplay && i < announcementListAll.size(); i++) {
                announcementList.add(announcementListAll.get(i));
            }
        } else {
            ArrayList<Announcement> result = new ArrayList<>();
            text = text.toLowerCase().trim();
            for(Announcement item: announcementListAll) {

                if(item.getTitle().toLowerCase().contains(text) ||
                        item.getCommittee().toLowerCase().contains(text) ||
                        item.getText().toLowerCase().contains(text)) {
                    result.add(item);
                }
            }
            announcementList.clear();

            for(int i = 0; i < numToDisplay && i < result.size(); i++) {
                announcementList.add(result.get(i));
            }
        }
        notifyDataSetChanged();
    }


}