package org.rowanacm.android;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.rowanacm.android.user.UserInfo;
import org.rowanacm.android.user.UserListener;
import org.rowanacm.android.user.UserManager;
import org.rowanacm.android.utils.ExternalAppUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MeFragment extends BaseFragment {

    private static final String LOG_TAG = MeFragment.class.getSimpleName();

    @Inject
    UserManager userManager;
    @Inject FirebaseAuth firebaseAuth;
    @Inject AcmClient acmClient;

    @BindView(R.id.name_text_view) TextView nameTextView;
    @BindView(R.id.on_slack_textview) TextView onSlackTextView;
    @BindView(R.id.meeting_count_textview) TextView meetingCountTextView;
    @BindView(R.id.committee_text_view) TextView committeeTextView;
    @BindView(R.id.email_textview) TextView emailTextView;
    @BindView(R.id.profile_pic_image_view) ImageView profilePicImageView;

    UserListener listener;
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
    }

    @Override
    public @LayoutRes int getLayout() {
        return R.layout.fragment_me;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listener = new UserListener() {
            @Override
            public void onUserChanged(UserInfo currentUser) {
                if (currentUser != null) {
                    updateUserUi(currentUser);
                }
            }
        };

        userManager.addUserListener(listener);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        userManager.removeUserListener(listener);
    }

    @Override
    public String getTitle() {
        return App.get().getString(R.string.me_title);
    }

    private void updateUserUi(UserInfo currentUser) {
        if (nameTextView == null) {
            return;
        }

        nameTextView.setText(currentUser.getName());
        emailTextView.setText(currentUser.getRowanEmail());


        if (currentUser.getProfilePicture() != null) {
            Picasso.with(getActivity())
                    .load(currentUser.getProfilePicture())
                    .placeholder(R.drawable.person)
                    .into(profilePicImageView);
        }

        if (currentUser.getOnSlack()) {
            onSlackTextView.setText(R.string.user_on_slack);
        } else {
            onSlackTextView.setText(R.string.not_on_slack);
        }

        committeeTextView.setText(currentUser.getCommitteeText());

        meetingCountTextView.setText(""+currentUser.getMeetingCount());

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

        Call<AttendanceResult> result = acmClient.setCommittees(userManager.getGoogleLoginToken(), committee);
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

    @OnClick(R.id.profile_pic_image_view)
    protected void showProfilePictureToast() {
        Toast.makeText(getActivity(), "Change your slack profile picture to change the picture in the app", Toast.LENGTH_LONG).show();
    }

}
