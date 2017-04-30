package org.rowanacm.android;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.us.acm.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.rowanacm.android.utils.ExternalAppUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MeFragment extends Fragment {

    private static final String LOG_TAG = MeFragment.class.getSimpleName();

    private FirebaseAuth.AuthStateListener mAuthListener;

    public MeFragment() {
        // Required empty public constructor
    }

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAuthListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    private void setupAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(LOG_TAG, "onAuthStateChanged() called with: firebaseAuth = [" + firebaseAuth + "]");

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    String uid = user.getUid();
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + uid);

                    String name = user.getDisplayName();

                    ((TextView)getView().findViewById(R.id.name_text_view)).setText(name);

                    FirebaseDatabase.getInstance().getReference("members").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int meeting_count = dataSnapshot.child("meeting_count").getValue(Integer.class);
                            ((TextView)getView().findViewById(R.id.meeting_count_textview)).setText("Number of meetings attended: " + meeting_count);

                            String committee = dataSnapshot.child("committee").getValue(String.class);
                            ((TextView)getView().findViewById(R.id.committee_text_view)).setText("Committee: " + committee);

                            boolean onSlack = dataSnapshot.child("on_slack").getValue(Boolean.class);
                            String text;
                            if (onSlack) {
                                text = "You are on slack";
                            } else {
                                text = "You are not on slack";
                            }
                            ((TextView)getView().findViewById(R.id.on_slack_textview)).setText(text);



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                    //updateGoogleSignInButtons(false);
                }
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    @OnClick(R.id.change_committee_button)
    protected void chooseCommittee() {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radiobutton_dialog);

        String[] stringArray = getResources().getStringArray(R.array.committee_array);
        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        for (int i = 0; i < stringArray.length; i++) {
            String committee = stringArray[i];
            RadioButton rb = new RadioButton(getActivity());

            final int finalI = i;
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRadioButtonClicked(view, finalI);
                }
            });

            rb.setText(committee);
            rg.addView(rb);
        }

        dialog.show();
    }

    public void onRadioButtonClicked(View view,int ndx) {
        if(view instanceof RadioButton) {

            String[] stringArray = getResources().getStringArray(R.array.committee_keys);//grab key names
            String committee=stringArray[ndx];//set string based on index of
            Log.d(LOG_TAG, "onRadioButtonClicked: "+committee);
            //then send to firebase

            //send firebase

        }
    }

    /**
     * Open the slack app
     */
    @OnClick(R.id.slack_button)
    protected void openSlack() {
        if (ExternalAppUtils.isAppInstalled(getActivity(), "com.Slack")) {
            Uri uri = Uri.parse("slack://open");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            ExternalAppUtils.openPlayStore(getActivity(), "com.Slack");
        }
    }



}
