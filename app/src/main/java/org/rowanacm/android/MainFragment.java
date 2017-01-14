package org.rowanacm.android;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.rowanacm.android.attendance.AttendanceActivity;

import java.util.Iterator;

import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;


public class MainFragment extends Fragment {
    private final static String ACM_ATTENDANCE_URL = "https://acm-attendance.firebaseapp.com/";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);

        Button signInButton = (Button) rootView.findViewById(R.id.attendance_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivity(AttendanceActivity.class);
            }
        });


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.attendance_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(haveISignedInAlready) {
                    Snackbar.make(getView(), "You already signed in to the meeting", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    DatabaseReference attendance = FirebaseDatabase.getInstance().getReference("attendance").child(currentMeeting);
                    attendance.child(currentUser.getUid()).setValue(currentUser.getDisplayName());
                    Snackbar.make(getView(), "Signing in...", Snackbar.LENGTH_SHORT).show();

                }
            }
        });

        getView().findViewById(R.id.account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivity(AttendanceActivity.class);
            }
        });

        String section = getActivity().getIntent().getStringExtra("section");
        if(section != null && section.equals("attendance")) {
            switchActivity(AttendanceActivity.class);
        }
        ButterKnife.bind(getActivity());

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("attendance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean attendanceEnabled = (boolean) dataSnapshot.child("enabled").getValue();
                if(attendanceEnabled) {
                    getView().findViewById(R.id.attendance_layout).setVisibility(View.VISIBLE);

                    TextView attendanceTextView = (TextView) getView().findViewById(R.id.attendance_textview);
                    Button meetingButton =(Button)getView().findViewById(R.id.attendance_button);
                    Button accountButton =(Button)getView().findViewById(R.id.account_button);

                    haveISignedInAlready = false;

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(currentUser == null) {
                        haveISignedInAlready = false;
                        // prompt user to sign in to their google account

                        attendanceTextView.setText("Sign in to your google account");
                        accountButton.setVisibility(View.VISIBLE);
                        meetingButton.setVisibility(View.GONE);
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
                                attendanceTextView.setText("You already signed in to the meeting");
                                meetingButton.setVisibility(View.GONE);
                                return;
                            }
                        }
                        haveISignedInAlready = false;
                        // The user is signed into their google account but not to the meeting
                        attendanceTextView.setText("Sign in to the meeting");

                        accountButton.setVisibility(View.VISIBLE);
                        meetingButton.setVisibility(View.VISIBLE);
                        Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
                        meetingButton.startAnimation(pulse);
                    }

                }
                else {
                    // Don't show anything related to the attendance
                    getView().findViewById(R.id.attendance_layout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    private void switchActivity(Class newActivity) {
        Intent intent = new Intent(getContext(), newActivity);
        startActivity(intent);
    }
}
