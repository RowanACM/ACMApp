package org.rowanacm.android.announcement;


import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.rowanacm.android.AcmClient;
import org.rowanacm.android.App;
import org.rowanacm.android.R;
import org.rowanacm.android.ServerResponse;
import org.rowanacm.android.authentication.UserManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAnnouncementDialog extends AlertDialog.Builder {

    @Inject UserManager userManager;
    @Inject AcmClient acmClient;

    @BindView(R.id.title_edit_text) EditText titleEditText;
    @BindView(R.id.message_edit_text) EditText messageEditText;
    @BindView(R.id.committee_spinner) Spinner committeeSpinner;
    @BindView(R.id.slack_check_box) CheckBox slackCheckBox;

    public CreateAnnouncementDialog(final Activity activity) {
        super(activity);
        App.get().getAcmComponent().inject(this);

        setTitle(R.string.create_announcement);
        final View dialogView = activity.getLayoutInflater().inflate(R.layout.create_announcement_view, null);
        setView(dialogView);
        ButterKnife.bind(this, dialogView);

        setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            // When Cancel is pressed, close the dialog and do nothing
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = titleEditText.getText().toString();
                String message = messageEditText.getText().toString();

                int committeePosition = committeeSpinner.getSelectedItemPosition();
                String committeeId = activity.getResources().getStringArray(R.array.committee_keys)[committeePosition];

                Call<ServerResponse> serverResponseCall = acmClient.postAnnouncement(userManager.getGoogleLoginToken(), title, message, committeeId, slackCheckBox.isChecked());
                serverResponseCall.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(activity, "Unknown error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.committee_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        committeeSpinner.setAdapter(adapter);
    }

}
