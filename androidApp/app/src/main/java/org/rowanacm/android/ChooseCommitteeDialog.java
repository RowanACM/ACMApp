package org.rowanacm.android;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ChooseCommitteeDialog extends Dialog {

    @BindView(R.id.radio_group) RadioGroup radioGroup;

    public ChooseCommitteeDialog(@NonNull Context context, @Nullable String currentCommittee) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radiobutton_dialog);
        ButterKnife.bind(this);

        String[] committeeKeys = context.getResources().getStringArray(R.array.committee_keys);
        String[] stringArray = context.getResources().getStringArray(R.array.committee_array);

        for (int i = 0; i < stringArray.length; i++) {
            String committee = stringArray[i];
            RadioButton radioButton = new RadioButton(context);

            final int finalI = i;
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRadioButtonClicked(finalI);
                }
            });

            radioButton.setText(committee);

            radioGroup.addView(radioButton);

            if (committeeKeys[i].equals(currentCommittee)) {
                radioGroup.check(radioButton.getId());
            }


        }
    }

    public abstract void onRadioButtonClicked(int index);

}
