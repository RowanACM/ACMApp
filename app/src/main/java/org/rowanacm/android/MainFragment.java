package org.rowanacm.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private ValueEventListener attendanceListener;

    private enum AttendanceMode {HIDDEN, PROMPT_GOOGLE, PROMPT_MEETING, SIGNED_IN}

    private boolean signedInMeeting;
    private String currentMeeting;


    /**
     * Returns a new instance of this fragment
     */
    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        getView().findViewById(R.id.attendance_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signedInMeeting) {
                    Snackbar.make(getView(), "You already signed in to the meeting ✓", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    signInToMeeting();

                }
            }
        });


        ButterKnife.bind(getActivity());

        attendanceListener = attendanceListener();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // Refresh the attendance status
                FirebaseDatabase.getInstance().getReference().removeEventListener(attendanceListener);
                attendanceListener = attendanceListener();

                if (user != null) {
                    // User is signed in
                    String uid = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + uid);
                    slackListener(uid);

                    updateGoogleSignInButtons(true);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    updateGoogleSignInButtons(false);
                }
            }
        };
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

    /**
     * Revoke access to the user's Google Account and sign out
     */
    @OnClick(R.id.google_sign_out_button)
    private void signOutGoogle() {
        GoogleApiClient googleApiClient = ((MainTabActivity) getActivity()).getGoogleApiClient();
        if(!googleApiClient.isConnected()) {
            // The user is not signed in
            Toast.makeText(getActivity(), R.string.error_sign_out_not_signed_in, Toast.LENGTH_LONG).show();
        }
        else {
            Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(getActivity(), R.string.signed_out, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void signInToMeeting() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("2dvdaw7sq1.execute-api.us-east-1.amazonaws.com")
                    .appendPath("prod")
                    .appendPath("attendance")
                    .appendQueryParameter("uid", currentUser.getUid())
                    .appendQueryParameter("name", currentUser.getDisplayName())
                    .appendQueryParameter("email", currentUser.getEmail())
                    .appendQueryParameter("method", "android");

            String myUrl = builder.build().toString();
            new AttendanceAsyncTask().execute(myUrl);
        }
    }

    private ValueEventListener attendanceListener() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child("attendance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean attendanceEnabled = (boolean) dataSnapshot.child("enabled").getValue();
                if(attendanceEnabled) {

                    signedInMeeting = false;

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(currentUser == null) {
                        signedInMeeting = false;
                        updateAttendanceViews(AttendanceMode.PROMPT_GOOGLE);
                    }
                    else {
                        String uid = currentUser.getUid();

                        currentMeeting = (String) dataSnapshot.child("current").getValue();
                        Iterator<DataSnapshot> childrenIter = dataSnapshot.child(currentMeeting).getChildren().iterator();
                        while (childrenIter.hasNext()) {
                            DataSnapshot snapshot = childrenIter.next();
                            if(snapshot.getKey().equals(uid)) {
                                signedInMeeting = true;
                                // tell the user that they already signed in
                                updateAttendanceViews(AttendanceMode.SIGNED_IN);
                                return;
                            }
                        }
                        signedInMeeting = false;
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

    private void slackListener(final String uid) {
        FirebaseDatabase.getInstance().getReference("members").child(uid).child("on_slack").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean onSlack = dataSnapshot.getValue() != null && (boolean) dataSnapshot.getValue();
                updateSlackViews(onSlack);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void updateSlackViews(boolean onSlack) {
        TextView slackTextView = (TextView) getView().findViewById(R.id.slack_textview);
        Button slackSignUpButton = (Button) getView().findViewById(R.id.slack_sign_up_button);
        slackSignUpButton.setVisibility(View.VISIBLE);
        slackTextView.setVisibility(View.VISIBLE);

        if(onSlack) {
            slackTextView.setText(R.string.on_slack);
            slackSignUpButton.setText(R.string.open_slack);

            slackSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openSlack();
                }
            });
        }
        else {
            slackTextView.setText(R.string.not_on_slack);
            slackSignUpButton.setText(R.string.sign_up_slack);
            slackSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.openUrl(getActivity(), "https://rowanacm.slack.com/signup");
                }
            });
        }
    }

    /**
     * Open the slack app
     */
    private void openSlack() {
        Uri uri = Uri.parse("slack://open");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void updateAttendanceViews(AttendanceMode attendanceMode) {
        View rootView = getView();
        if(rootView == null)
            return;
        ViewGroup attendanceLayout = (ViewGroup) rootView.findViewById(R.id.attendance_layout);
        TextView attendanceTextView = (TextView) rootView.findViewById(R.id.attendance_textview);
        Button meetingButton = (Button) rootView.findViewById(R.id.attendance_button);
        SignInButton googleSignInButton =(SignInButton) rootView.findViewById(R.id.sign_in_google_button);

        switch (attendanceMode) {
            case HIDDEN:
                attendanceLayout.setVisibility(View.GONE);
                attendanceTextView.setVisibility(View.GONE);
                meetingButton.setAnimation(null);
                meetingButton.setVisibility(View.GONE);
                break;
            case PROMPT_GOOGLE:
                attendanceLayout.setVisibility(View.VISIBLE);
                attendanceTextView.setVisibility(View.VISIBLE);
                attendanceTextView.setText(R.string.sign_in_google_before_meeting);
                googleSignInButton.setVisibility(View.VISIBLE);
                meetingButton.setAnimation(null);
                meetingButton.setVisibility(View.GONE);
                break;
            case SIGNED_IN:
                attendanceLayout.setVisibility(View.VISIBLE);
                attendanceTextView.setVisibility(View.VISIBLE);
                attendanceTextView.setText(R.string.signed_into_meeting);
                meetingButton.setAnimation(null);
                meetingButton.setVisibility(View.GONE);
                break;
            case PROMPT_MEETING:
                attendanceLayout.setVisibility(View.VISIBLE);
                attendanceTextView.setVisibility(View.GONE);
                meetingButton.setVisibility(View.VISIBLE);
                Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
                meetingButton.startAnimation(pulse);
                break;
        }
    }

    /**
     * Update the views related to Google sign in
     * @param currentlySignedIn Whether the user is currently signed in
     */
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


    private class AttendanceAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);

                BufferedReader in = null;
                in = new BufferedReader(new InputStreamReader(url.openStream()));

                String response = "";
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    response += inputLine;

                in.close();

                return response;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String resultStr) {
            progressDialog.dismiss();

            int result = Integer.parseInt(resultStr);
            String message;

            // If true, show a snackbar. If false, show an AlertDialog
            boolean snackbar = true;

            switch(result) {
                case 100:
                    // Signed in successfully. New member
                    message = "Signed in successfully. Welcome to your first ACM Meeting. You should have received an email with more information about the club.";
                    snackbar = false;
                    break;
                case 110:
                    // Signed in successfully. Existing member
                    message = "Signed in successfully";
                    snackbar = true;
                    break;
                case 120:
                    // Already signed in
                    message = "You already signed in to the meeting";
                    snackbar = true;
                    break;
                case 200:
                    // Didn't sign in. Attendance disabled
                    message = "Attendance is disabled";
                    snackbar = true;
                    break;
                case 210:
                    // Invalid input
                    message = "Error: Invalid input. Try to sign in again. If you still can't, tell someone on eboard or @TylerCarberry on slack";
                    snackbar = false;
                    break;
                case 220:
                    // Didn't sign in. Unknown error
                    message = "Error: Unknown error. Try to sign in again. If you still can't, tell someone on eboard or @TylerCarberry on slack";
                    snackbar = false;
                    break;
                default:
                    // Didn't sign in. Unknown error
                    message = "Error: Unknown error. Try to sign in again. If you still can't, tell someone on eboard or @TylerCarberry on slack";
                    snackbar = false;
                    break;
            }

            if(snackbar) {
                Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(message);
                builder.create().show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Signing in to meeting");
            progressDialog.show();
        }
    }
}
