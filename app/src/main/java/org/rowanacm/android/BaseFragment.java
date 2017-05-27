package org.rowanacm.android;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment {

    protected Unbinder unbinder;

    public abstract @LayoutRes int getLayout();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(getLayout(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main_tab, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * @return The root view of the fragment casted to a ViewGroup
     */
    protected ViewGroup getRootViewGroup() {
        View rootView = getView();
        if (rootView instanceof ViewGroup) {
            return (ViewGroup) getView();
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(isVisible());
    }

    public String getTitle() {
        return "";
    }

}
