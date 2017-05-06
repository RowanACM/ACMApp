package org.rowanacm.android;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.us.acm.R;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ChooseCommitteeDialog extends Dialog {

    @BindView(R.id.radio_group) RadioGroup radioGroup;

    public ChooseCommitteeDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radiobutton_dialog);
        ButterKnife.bind(this);

        String[] stringArray = context.getResources().getStringArray(R.array.committee_array);

        for (int i = 0; i < stringArray.length; i++) {
            String committee = stringArray[i];
            RadioButton button = new RadioButton(context);

            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRadioButtonClicked(finalI);
                }
            });

            button.setText(committee);
            radioGroup.addView(button);
        }
    }

    public abstract void onRadioButtonClicked(int index);

}
