package org.rowanacm.android;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.us.acm.R;

import org.rowanacm.android.annoucement.AnnouncementListFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Context context;

        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return InfoFragment.newInstance();
                case 1:
                    return AnnouncementListFragment.newInstance();
                case 2:
                    return CommitteeFragment.newInstance();
                case 3:
                    return MeFragment.newInstance();

            }
            return null;
        }

        @Override
        public int getCount() {
            // Number of pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return context.getString(R.string.info_title);
                case 1:
                    return context.getString(R.string.announcements_title);
                case 2:
                    return context.getString(R.string.committee_title);
                case 3:
                    return context.getString(R.string.me_title);

            }
            return null;
        }
    }