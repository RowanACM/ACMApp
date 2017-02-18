package org.rowanacm.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.us.acm.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CommitteeFragment extends Fragment {

    public CommitteeFragment() {
        // Required empty public constructor
    }

    public static CommitteeFragment newInstance() {
        return new CommitteeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_committee, container, false);
    }


}
