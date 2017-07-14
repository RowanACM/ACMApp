package org.rowanacm.android;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.rowanacm.android.utils.ExternalAppUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MeFragment extends BaseFragment {

    private static final String LOG_TAG = MeFragment.class.getSimpleName();

    @Inject FirebaseAuth firebaseAuth;
    @Inject AcmClient acmClient;

    @BindView(R.id.name_text_view) TextView nameTextView;
    @BindView(R.id.on_slack_textview) TextView onSlackTextView;
    @BindView(R.id.meeting_count_textview) TextView meetingCountTextView;
    @BindView(R.id.committee_text_view) TextView committeeTextView;
    @BindView(R.id.email_textview) TextView emailTextView;
    @BindView(R.id.profile_pic_image_view) ImageView profilePicImageView;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private String googleAuthToken;

    private String currentCommittee;

    public MeFragment() {

    }

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.get().getAcmComponent().inject(this);
        super.onCreate(savedInstanceState);

        firebaseAuth.addIdTokenListener(new FirebaseAuth.IdTokenListener() {
            @Override
            public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        googleAuthToken = task.getResult().getToken();
                        Log.d("AUTHTOKEN", googleAuthToken);
                    }
                });
            }
        });
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

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    acmClient.getUserInfo(task.getResult().getToken()).enqueue(new Callback<UserInfo>() {
                        @Override
                        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                            UserInfo userInfo = response.body();
                            if (userInfo == null) {
                                return;
                            }

                            if (userInfo.getProfilePicture() != null) {
                                Picasso.with(getActivity())
                                        .load(userInfo.getProfilePicture())
                                        .placeholder(R.drawable.person)
                                        .into(profilePicImageView);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserInfo> call, Throwable t) {

                        }
                    });
                }
            });
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public String getTitle() {
        return App.get().getString(R.string.me_title);
    }

    private void setupAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    nameTextView.setText(user.getDisplayName());
                    emailTextView.setText(user.getEmail());

                    FirebaseDatabase.getInstance().getReference("members").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (isVisible()) {
                                DataSnapshot meetingCount = dataSnapshot.child("meeting_count");
                                if (meetingCount != null) {
                                    meetingCountTextView.setText(meetingCount.getValue().toString());
                                }


                                DataSnapshot committees = dataSnapshot.child("committees");
                                List<String> selectedCommittees = new ArrayList<String>();
                                for (DataSnapshot child : committees.getChildren()) {
                                    if (child.getValue(Boolean.class)) {

                                        if (!child.getKey().equalsIgnoreCase("eboard")) {
                                            currentCommittee = child.getKey();
                                        }
                                        selectedCommittees.add(child.getKey());
                                    }
                                }

                                String displayMessage = getCommitteeText(selectedCommittees);
                                committeeTextView.setText(displayMessage);

                                DataSnapshot onSlack = dataSnapshot.child("on_slack");
                                if (onSlack != null && onSlack.getValue(Boolean.class)) {
                                    onSlackTextView.setText(R.string.user_on_slack);
                                } else {
                                    onSlackTextView.setText(R.string.not_on_slack);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private String getCommitteeText(List<String> selectedCommittees) {
        if (selectedCommittees.isEmpty()) {
            return "Committee: None";
        }

        if (selectedCommittees.contains("eboard")) {
            return "Eboard";
        }

        if (selectedCommittees.contains("ai")) {
            return "Committee: AI";
        }

        if (selectedCommittees.contains("game")) {
            return "Committee: Animation/Game";
        }

        return "Committee: " + capitalizeFirstLetterOfString(selectedCommittees.get(0));
    }

    private String capitalizeFirstLetterOfString(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @OnClick({R.id.change_committee_button, R.id.committee_text_view})
    protected void chooseCommittee() {
        // custom dialog
        final Dialog dialog = new ChooseCommitteeDialog(getActivity(), currentCommittee) {
            @Override
            public void onRadioButtonClicked(int index) {
                onCommitteeChanged(index);
            }
        };
        dialog.show();
    }

    public void onCommitteeChanged(int index) {
        String[] stringArray = getResources().getStringArray(R.array.committee_keys); //grab key names
        final String committee = stringArray[index]; //set string based on index of

        Call<AttendanceResult> result = acmClient.setCommittees(googleAuthToken, committee);
        result.enqueue(new Callback<AttendanceResult>() {
            @Override
            public void onResponse(Call<AttendanceResult> call, Response<AttendanceResult> response) {

            }

            @Override
            public void onFailure(Call<AttendanceResult> call, Throwable t) {

            }
        });

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

    protected void showProfilePictureToast() {
        Toast.makeText(getActivity(), "Change your slack profile picture to change the picture in the app", Toast.LENGTH_LONG).show();
    }

}
