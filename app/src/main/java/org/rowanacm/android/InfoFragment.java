package org.rowanacm.android;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.us.acm.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.rowanacm.android.firebase.RemoteConfig;
import org.rowanacm.android.utils.ExternalAppUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class InfoFragment extends BaseFragment {
    private static final String LOG_TAG = InfoFragment.class.getSimpleName();

    @Inject RemoteConfig remoteConfig;
    @Inject FirebaseAuth firebaseAuth;
    @Inject DatabaseReference database;
    @Inject GoogleApiClient googleApiClient;

    @BindView(R.id.attendance_layout) ViewGroup attendanceLayout;
    @BindView(R.id.attendance_textview) TextView attendanceTextView;
    @BindView(R.id.attendance_button) Button meetingButton;
    @BindView(R.id.sign_in_google_button) SignInButton googleSignInButton;
    @BindView(R.id.header_image_view) ImageView headerImageView;
    @BindView(R.id.google_sign_out_textview) TextView signOutTextView;
    @BindView(R.id.google_sign_out_button) Button signOutButton;


    private FirebaseAuth.AuthStateListener authListener;
    private ValueEventListener attendanceListener;

    private enum AttendanceMode {HIDDEN, PROMPT_GOOGLE, PROMPT_MEETING, SIGNED_IN}

    private boolean signedInMeeting;
    private String currentMeeting;

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((AcmApplication)getActivity().getApplication()).getAcmComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attendanceListener = attendanceListener();

        loadHeaderImage(view);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // Refresh the attendance status
                database.removeEventListener(attendanceListener);
                attendanceListener = attendanceListener();

                updateGoogleSignInButtons(user != null);
            }
        };
    }

    private void loadHeaderImage(View view) {
        String headerUrl = remoteConfig.getString(R.string.rc_header_image);
        if (headerUrl != null && headerUrl.length() > 5 && view != null) {
            Picasso.with(getActivity()).load(headerUrl).into(headerImageView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }

    /**
     * Revoke access to the user's Google Account and sign out
     */
    @OnClick(R.id.google_sign_out_button)
    public void signOutGoogle() {
        if (!googleApiClient.isConnected()) {
            // The user is not signed in
            Toast.makeText(getActivity(), R.string.error_sign_out_not_signed_in, Toast.LENGTH_LONG).show();
        } else {
            Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            firebaseAuth.signOut();
                            Toast.makeText(getActivity(), R.string.signed_out, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @OnClick(R.id.sign_in_google_button)
    public void signInGoogle() {
        ((MainTabActivity)getActivity()).signInGoogle();
    }

    @OnClick(R.id.attendance_button)
    protected void signInToMeeting() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
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
        return database.child("attendance").child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean attendanceEnabled = (boolean) dataSnapshot.child("enabled").getValue();
                if(attendanceEnabled) {
                    signedInMeeting = false;

                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if(currentUser == null) {
                        signedInMeeting = false;
                        updateAttendanceViews(AttendanceMode.PROMPT_GOOGLE);
                    }
                    else {
                        String uid = currentUser.getUid();
                        currentMeeting = (String) dataSnapshot.child("current").getValue();
                        createSignedInListener(currentMeeting, uid);
                        //updateAttendanceViews(AttendanceMode.PROMPT_MEETING);
                    }

                } else {
                    updateAttendanceViews(AttendanceMode.HIDDEN);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void createSignedInListener(String currentMeeting, String uid) {
        database.child("attendance").child(currentMeeting).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                signedInMeeting = (dataSnapshot.getValue() != null);
                updateAttendanceViews(signedInMeeting ? AttendanceMode.SIGNED_IN : AttendanceMode.PROMPT_MEETING);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * Create an email intent to contact acm@rowan.edu
     */
    @OnClick(R.id.contact_eboard_button)
    protected void contact_eboard() {
        ExternalAppUtils.sendEmail(getActivity(),
                getString(R.string.acm_email_address),
                getString(R.string.contact_eboard_subject),
                getString(R.string.contact_eboard_title));
    }

    private void updateAttendanceViews(AttendanceMode attendanceMode) {
        View rootView = getView();
        if (rootView == null)
            return;
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

                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                String response = "";
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response += inputLine;
                }

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
            boolean showSnackbar = true;

            switch(result) {
                case 100:
                    // Signed in successfully. New member
                    message = getString(R.string.first_sign_in);
                    showSnackbar = false;
                    break;
                case 110:
                    // Signed in successfully. Existing member
                    message = getString(R.string.signed_in_successfully);
                    showSnackbar = true;
                    break;
                case 120:
                    // Already signed in
                    message = getString(R.string.attendance_already_signed_in);
                    showSnackbar = true;
                    break;
                case 200:
                    // Didn't sign in. Attendance disabled
                    message = getString(R.string.attendance_error_disabled);
                    showSnackbar = true;
                    break;
                case 210:
                    // Invalid input
                    message = getString(R.string.attendance_error_invalid_input);
                    showSnackbar = false;
                    break;
                case 220:
                    // Didn't sign in. Unknown error
                    message = getString(R.string.attendance_unknown_error);
                    showSnackbar = false;
                    break;
                default:
                    // Didn't sign in. Unknown error
                    message = getString(R.string.attendance_unknown_error);
                    showSnackbar = false;
                    break;
            }

            if (showSnackbar) {
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
            progressDialog.setMessage(getString(R.string.attendance_loading));
            progressDialog.show();
        }
    }
}
