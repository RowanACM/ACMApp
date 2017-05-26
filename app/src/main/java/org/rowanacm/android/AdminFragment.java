package org.rowanacm.android;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.us.acm.R;


public class AdminFragment extends BaseFragment {
    public AdminFragment() {
        // Required empty public constructor
    }

    public static AdminFragment newInstance() {
        AdminFragment fragment = new AdminFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public @LayoutRes int getLayout() {
        return R.layout.fragment_admin;
    }

    @Override
    public String getTitle() {
        return "ADMIN";
    }
}
