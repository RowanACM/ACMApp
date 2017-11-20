package org.rowanacm.android;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment {

    @Inject FirebaseAnalytics firebaseAnalytics;

    protected Unbinder unbinder;

    public abstract @LayoutRes int getLayout();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        App.get().getAcmComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

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
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(isVisible());

        firebaseAnalytics.setCurrentScreen(getActivity(), getTitle().toLowerCase(), null);
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

    public String getTitle() {
        return App.get().getString(R.string.app_name);
    }

}
