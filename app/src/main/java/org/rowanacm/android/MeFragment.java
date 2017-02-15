package org.rowanacm.android;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.us.acm.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class MeFragment extends Fragment {
    public MeFragment() {
        // Required empty public constructor
    }

    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false);
    }


    //TODO Call this method
    protected void chooseCommittee() {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radiobutton_dialog);

        String[] stringArray = getResources().getStringArray(R.array.committee_array);
        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        for (String committee : stringArray) {
            RadioButton rb = new RadioButton(getActivity()); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(committee);
            rg.addView(rb);
        }

        dialog.show();
    }


}
