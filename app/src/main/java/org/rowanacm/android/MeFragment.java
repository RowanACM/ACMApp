package org.rowanacm.android;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.us.acm.R;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.rowanacm.android.utils.ExternalAppUtils;

import butterknife.BindView;
import butterknife.OnClick;


public class MeFragment extends BaseFragment {

    private static final String LOG_TAG = MeFragment.class.getSimpleName();

    private FirebaseAuth.AuthStateListener mAuthListener;

    @BindView(R.id.name_text_view) TextView nameTextView;
    @BindView(R.id.on_slack_textview) TextView onSlackTextView;
    @BindView(R.id.meeting_count_textview) TextView meetingCountTextView;
    @BindView(R.id.committee_text_view) TextView committeeTextView;
    @BindView(R.id.profile_pic_image_view) ImageView profilePicImageView;

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
    public @LayoutRes int getLayout() {
        return R.layout.fragment_me;
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

    @Override
    public String getTitle() {
        return "ME";
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

                    nameTextView.setText(name);

                    FirebaseDatabase.getInstance().getReference("members").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int meeting_count = dataSnapshot.child("meeting_count").getValue(Integer.class);
                            //meetingCountTextView.setText("Number of meetings attended: " + meeting_count);

                            String committee = dataSnapshot.child("committee").getValue(String.class);
                            //committeeTextView.setText("Committee: " + committee);

                            boolean onSlack = dataSnapshot.child("on_slack").getValue(Boolean.class);
                            String text;
                            if (onSlack) {
                                text = getString(R.string.user_on_slack);
                            } else {
                                text = getString(R.string.user_not_on_slack);
                            }
                            onSlackTextView.setText(text);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
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
        final Dialog dialog = new ChooseCommitteeDialog(getActivity()) {
            @Override
            public void onRadioButtonClicked(int index) {
                onCommitteeChanged(index);
            }
        };
        dialog.show();
    }

    public void onCommitteeChanged(int index) {
        String[] stringArray = getResources().getStringArray(R.array.committee_keys); //grab key names
        String committee = stringArray[index]; //set string based on index of
        Log.d(LOG_TAG, "onRadioButtonClicked: "+committee);
        //then send to firebase

        //send firebase
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main_tab, menu);
    }

}
