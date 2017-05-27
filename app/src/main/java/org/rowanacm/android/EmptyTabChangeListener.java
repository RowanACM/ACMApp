package org.rowanacm.android;

import android.support.v4.view.ViewPager;


public class EmptyTabChangeListener implements ViewPager.OnPageChangeListener {


    public EmptyTabChangeListener() {
    }

    @Override
    public void onPageSelected(int position) {}

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageScrollStateChanged(int state) {}

}
