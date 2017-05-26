package org.rowanacm.android;

import android.support.annotation.LayoutRes;
import android.us.acm.R;
import android.view.Menu;
import android.view.MenuInflater;


public class CommitteeFragment extends BaseFragment {

    public CommitteeFragment() {
        // Required empty public constructor
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main_tab, menu);
    }
}
