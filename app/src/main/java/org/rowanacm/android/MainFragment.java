package org.rowanacm.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.us.acm.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;


public class MainFragment extends Fragment {
    private final static String ACM_ATTENDANCE_URL = "https://acm-attendance.firebaseapp.com/";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private ValueEventListener attendanceListener;

    private enum AttendanceMode {HIDDEN, PROMPT_GOOGLE, PROMPT_MEETING, SIGNED_IN}

    boolean haveISignedInAlready;
    String currentMeeting;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);

        rootView.findViewById(R.id.google_sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccess();
            }
        });

        return rootView;
    }

    private void revokeAccess() {
        GoogleApiClient googleApiClient = ((MainTabActivity) getActivity()).getGoogleApiClient();
        if(!googleApiClient.isConnected()) {
            Toast.makeText(getActivity(), "You are not signed in. Unable to sign out", Toast.LENGTH_LONG).show();
        }
        else {

            Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            // ...
                            //updateGoogleSignInButtons(false);
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(getActivity(), "Signed out", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        getView().findViewById(R.id.attendance_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(haveISignedInAlready) {
                    Snackbar.make(getView(), "You already signed in to the meeting ✓", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    DatabaseReference attendance = FirebaseDatabase.getInstance().getReference("attendance").child(currentMeeting);
                    attendance.child(currentUser.getUid()).setValue(currentUser.getDisplayName());
                    Snackbar.make(getView(), "Signing in...", Snackbar.LENGTH_SHORT).show();

                }
            }
        });


        ButterKnife.bind(getActivity());

        attendanceListener = attendanceListener();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    updateGoogleSignInButtons(true);

                    String email = user.getEmail();
                    Log.d(TAG, "onAuthStateChanged: " +  email);

                    if(email != null)
                        slackListener(email);

                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    database.removeEventListener(attendanceListener);
                    attendanceListener = attendanceListener();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    updateGoogleSignInButtons(false);
                }
            }
        };
    }

    private ValueEventListener attendanceListener() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child("attendance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean attendanceEnabled = (boolean) dataSnapshot.child("enabled").getValue();
                if(attendanceEnabled) {

                    haveISignedInAlready = false;

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(currentUser == null) {
                        haveISignedInAlready = false;
                        updateAttendanceViews(AttendanceMode.PROMPT_GOOGLE);
                    }
                    else {
                        String uid = currentUser.getUid();

                        currentMeeting = (String) dataSnapshot.child("current").getValue();
                        Iterable<DataSnapshot> children = dataSnapshot.child(currentMeeting).getChildren();
                        Iterator<DataSnapshot> iter = children.iterator();
                        while (iter.hasNext()) {
                            DataSnapshot snapshot = iter.next();
                            Log.d(TAG, "onDataChange: " + snapshot.getKey());
                            if(snapshot.getKey().equals(uid)) {
                                haveISignedInAlready = true;
                                // tell the user that they already signed in
                                updateAttendanceViews(AttendanceMode.SIGNED_IN);
                                return;
                            }
                        }
                        haveISignedInAlready = false;
                        // The user is signed into their google account but not to the meeting
                        updateAttendanceViews(AttendanceMode.PROMPT_MEETING);
                    }

                }
                else {
                    updateAttendanceViews(AttendanceMode.HIDDEN);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void slackListener(final String email) {
        Log.d(TAG, "slackListener() called with: email = [" + email + "]");
        FirebaseDatabase.getInstance().getReference("slack").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (((String) snapshot.getValue()).equalsIgnoreCase(email)) {
                        updateSlackViews(true);
                        return;
                    }
                }
                updateSlackViews(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void updateSlackViews(boolean onSlack) {
        Log.d(TAG, "updateSlackViews() called with: onSlack = [" + onSlack + "]");

        TextView slackTextView = (TextView) getView().findViewById(R.id.slack_textview);
        Button slackSignUpButton = (Button) getView().findViewById(R.id.slack_sign_up_button);
        slackSignUpButton.setVisibility(View.VISIBLE);
        slackTextView.setVisibility(View.VISIBLE);

        if(onSlack) {
            slackTextView.setText("You are on slack ✓");
            slackSignUpButton.setText("Open Slack");

            slackSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse("slack://open");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }
        else {
            slackTextView.setText("You are not on slack");
            slackSignUpButton.setText("Sign Up");
            slackSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.openUrl(getActivity(), "https://rowanacm.slack.com/signup");
                }
            });
        }
    }

    private void updateAttendanceViews(AttendanceMode attendanceMode) {
        ViewGroup attendanceLayout = (ViewGroup) getView().findViewById(R.id.attendance_layout);
        TextView attendanceTextView = (TextView) getView().findViewById(R.id.attendance_textview);
        Button meetingButton =(Button)getView().findViewById(R.id.attendance_button);
        SignInButton googleSignInButton =(SignInButton) getView().findViewById(R.id.sign_in_google_button);

        switch (attendanceMode) {
            case HIDDEN:
                attendanceLayout.setVisibility(View.GONE);
                attendanceTextView.setVisibility(View.GONE);
                meetingButton.setVisibility(View.GONE);
                break;
            case PROMPT_GOOGLE:
                attendanceLayout.setVisibility(View.VISIBLE);
                attendanceTextView.setVisibility(View.VISIBLE);
                attendanceTextView.setText("Sign in to your google account before you can sign in to the meeting");
                googleSignInButton.setVisibility(View.VISIBLE);
                meetingButton.setVisibility(View.GONE);
                break;
            case SIGNED_IN:
                attendanceLayout.setVisibility(View.VISIBLE);
                attendanceTextView.setVisibility(View.VISIBLE);
                attendanceTextView.setText("You are signed in to the meeting ✓");
                meetingButton.setAnimation(null);
                meetingButton.setVisibility(View.GONE);
                break;
            case PROMPT_MEETING:
                attendanceLayout.setVisibility(View.VISIBLE);
                attendanceTextView.setVisibility(View.GONE);
                googleSignInButton.setVisibility(View.VISIBLE);
                meetingButton.setVisibility(View.VISIBLE);
                Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
                meetingButton.startAnimation(pulse);
                break;
        }
    }

    private void updateGoogleSignInButtons(boolean currentlySignedIn) {
        SignInButton googleSignInButton =(SignInButton) getView().findViewById(R.id.sign_in_google_button);
        TextView signOutTextView = (TextView) getView().findViewById(R.id.google_sign_out_textview);
        Button signOutButton = (Button) getView().findViewById(R.id.google_sign_out_button);

        if(currentlySignedIn) {
            googleSignInButton.setVisibility(View.GONE);
            signOutTextView.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.VISIBLE);
        }
        else {
            googleSignInButton.setVisibility(View.VISIBLE);
            signOutTextView.setVisibility(View.GONE);
            signOutButton.setVisibility(View.GONE);
        }
    }
}
