package org.rowanacm.android;

import android.os.Bundle;
import android.support.annotation.LayoutRes;

import javax.inject.Inject;


public class AdminFragment extends BaseFragment {

    @Inject AdminManager adminManager;

    public AdminFragment() {

    }

    public static AdminFragment newInstance() {
        AdminFragment fragment = new AdminFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.get().getAcmComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public @LayoutRes int getLayout() {
        return R.layout.fragment_admin;
    }

    @Override
    public String getTitle() {
        return App.get().getString(R.string.admin_title);
    }

}
