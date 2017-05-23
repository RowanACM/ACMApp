package org.rowanacm.android;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Unbinder;


public class BaseFragment extends Fragment {

    protected Unbinder unbinder;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
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
