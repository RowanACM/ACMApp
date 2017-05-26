package org.rowanacm.android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.rowanacm.android.announcement.AnnouncementListFragment;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragments = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(InfoFragment.newInstance());
        fragments.add(AnnouncementListFragment.newInstance());
        fragments.add(CommitteeFragment.newInstance());
        fragments.add(MeFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }

    public void addFragment(BaseFragment fragment) {
        fragments.add(fragment);
        //notifyDataSetChanged();
    }

}