package org.rowanacm.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.rowanacm.android.firebase.RemoteConfig;
import org.rowanacm.android.authentication.TodoItem;
import org.rowanacm.android.authentication.UserInfo;
import org.rowanacm.android.authentication.UserListener;
import org.rowanacm.android.authentication.UserManager;
import org.rowanacm.android.utils.ExternalAppUtils;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InfoFragment extends BaseFragment {

    private final int RESPONSE_NEW = 100;
    private final int RESPONSE_EXISTING = 110;
    private final int RESPONSE_ALREADY = 120;
    private final int RESPONSE_DISABLED = 200;
    private final int RESPONSE_INVALID = 210;
    private final int RESPONSE_UNKNOWN = 220;

    @Inject RemoteConfig remoteConfig;
    @Inject FirebaseAuth firebaseAuth;
    @Inject DatabaseReference database;
    @Inject GoogleApiClient googleApiClient;
    @Inject AcmClient acmClient;
    @Inject UserManager userManager;

    @BindView(R.id.attendance_layout) ViewGroup attendanceLayout;
    @BindView(R.id.attendance_textview) TextView attendanceTextView;
    @BindView(R.id.attendance_button) Button meetingButton;
    @BindView(R.id.header_image_view) ImageView headerImageView;
    @BindView(R.id.new_member_instructions) TextView newMemberInstructions;

    private ProgressDialog progressDialog;

    private FirebaseAuth.AuthStateListener authListener;
    private ValueEventListener attendanceListener;
    private UserListener userListener;

    private enum AttendanceMode {HIDDEN, PROMPT_GOOGLE, PROMPT_MEETING, SIGNED_IN}

    private boolean signedInMeeting;
    private String currentMeeting;

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        App.get().getAcmComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public @LayoutRes int getLayout() {
        return R.layout.fragment_main_screen;
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

                ((MainTabActivity)getActivity()).updateGoogleSignInButtons(user != null);
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main_tab, menu);
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

        userListener = new UserListener() {
            @Override
            public void onUserChanged(UserInfo currentUser) {
                if (currentUser == null) {
                    return;
                }
                List<TodoItem> todoList = currentUser.getTodoList();
                String todoStr = "Instructions for new members:\n";
                todoStr += "• Download this app ✔\n";
                for (TodoItem todoItem : todoList) {
                    todoStr += "• " + todoItem.getText();
                    if (todoItem.isCompleted()) {
                        todoStr += " ✔";
                    }
                    todoStr += "\n";
                 }
                 newMemberInstructions.setText(todoStr);

            }
        };

        userManager.addUserListener(userListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
        userManager.removeUserListener(userListener);
    }

    @OnClick(R.id.attendance_button)
    protected void signInToMeeting() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            showSigningInDialog();

            firebaseAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(getActivity(), new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    String token = task.getResult().getToken();

                    Call<AttendanceResult> result = acmClient.signIn(token);
                    result.enqueue(new Callback<AttendanceResult>() {
                        @Override
                        public void onResponse(Call<AttendanceResult> call, Response<AttendanceResult> response) {
                            progressDialog.dismiss();

                            int resultCode = response.body().getResponseCode();
                            String message = getAttendanceResult(resultCode);
                            boolean showSnackbar = shouldShowSnackbar(resultCode);

                            showAttendanceResult(message, showSnackbar);
                        }

                        @Override
                        public void onFailure(Call<AttendanceResult> call, Throwable t) {
                            progressDialog.dismiss();

                            String message = getAttendanceResult(RESPONSE_UNKNOWN);
                            boolean showSnackbar = shouldShowSnackbar(RESPONSE_UNKNOWN);
                            showAttendanceResult(message, showSnackbar);
                        }
                    });
                }
            });



        }
    }

    private void showSigningInDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.attendance_loading));
        progressDialog.show();
    }

    private void showAttendanceResult(String message, boolean useSnackbar) {
        if (useSnackbar) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(message);
            builder.create().show();
        }
    }

    private ValueEventListener attendanceListener() {
        return database.child("attendance").child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean attendanceEnabled = (boolean) dataSnapshot.child("enabled").getValue();
                if (attendanceEnabled) {
                    signedInMeeting = false;

                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser == null) {
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

                ((MainTabActivity)getActivity()).showGoogleSignInButton();

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

    @OnClick(R.id.calendar_button)
    protected void addToCalendar() {
        final int MEETING_LENGTH_MINUTES = 90;

        Calendar startTime = Calendar.getInstance();

        startTime.set(2017, Calendar.SEPTEMBER, 8, 14, 0, 0);
        startTime.setTimeZone(TimeZone.getDefault());

        CalendarItem calendarItem = new CalendarItem("ACM",
                "Rowan Association for Computing Machinery",
                "Robinson 201 a/b",
                "FREQ=WEEKLY;BYDAY=FR;UNTIL=20171208",
                startTime,
                MEETING_LENGTH_MINUTES);

        ExternalAppUtils.addEventToCalendar(getActivity(), calendarItem);
    }

    private String getAttendanceResult(int resultCode) {
        switch(resultCode) {
            case RESPONSE_NEW:
                // Signed in successfully. New member
                return getString(R.string.first_sign_in);
            case RESPONSE_EXISTING:
                // Signed in successfully. Existing member
                return getString(R.string.signed_in_successfully);
            case RESPONSE_ALREADY:
                // Already signed in
                return getString(R.string.attendance_already_signed_in);
            case RESPONSE_DISABLED:
                // Didn't sign in. Attendance disabled
                return getString(R.string.attendance_error_disabled);
            case RESPONSE_INVALID:
                // Invalid input
                return getString(R.string.attendance_error_invalid_input);
            case RESPONSE_UNKNOWN:
                // Didn't sign in. Unknown error
                return getString(R.string.attendance_unknown_error);
            default:
                return getString(R.string.attendance_unknown_error);
        }
    }

    private boolean shouldShowSnackbar(int resultCode) {
        switch(resultCode) {
            case RESPONSE_EXISTING:
            case RESPONSE_ALREADY:
            case RESPONSE_DISABLED:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getTitle() {
        return App.get().getString(R.string.info_title);
    }

    @OnClick(R.id.facebook_button)
    public void openFacebook() {
        ExternalAppUtils.openUrl(getActivity(), getString(R.string.acm_facebook_url));
    }

    @OnClick(R.id.twitter_button)
    public void openTwitter() {
        ExternalAppUtils.openUrl(getActivity(), getString(R.string.acm_twitter_url));
    }
}
