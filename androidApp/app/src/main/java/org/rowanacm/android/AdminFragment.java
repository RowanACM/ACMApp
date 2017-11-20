package org.rowanacm.android;

import android.os.Bundle;
import android.support.annotation.LayoutRes;

import org.rowanacm.android.announcement.CreateAnnouncementDialog;

import javax.inject.Inject;

import butterknife.OnClick;


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

    @OnClick(R.id.create_announcement_button)
    public void createAnnouncement() {
        new CreateAnnouncementDialog(getActivity()).show();
    }

}
