package org.rowanacm.android;

import android.os.Bundle;
import android.us.acm.R;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;


public class CommitteeFragment extends BaseFragment {

    public CommitteeFragment() {
        // Required empty public constructor
    }

    public static CommitteeFragment newInstance() {
        return new CommitteeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_committee, container, false);
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
