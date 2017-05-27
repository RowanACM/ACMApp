package org.rowanacm.android;

import android.support.annotation.LayoutRes;


public class CommitteeFragment extends BaseFragment {

    public CommitteeFragment() {

    }

    public static CommitteeFragment newInstance() {
        return new CommitteeFragment();
    }

    @Override
    public @LayoutRes int getLayout() {
        return R.layout.fragment_committee;
    }

    @Override
    public String getTitle() {
        return "COMMITTEES";
    }
}
