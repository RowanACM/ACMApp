package org.rowanacm.android.nfc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.us.acm.R;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Helper for writing tags.
 * Creates a dialog while waiting for the tag, and displays error messages with a toast
 */
public class WriteTagHelper implements NfcManager.TagWriteErrorListener, NfcManager.TagWriteListener {
    AlertDialog dialog;
    NfcManager nfcManager;
    Context context;

    /**
     * Create a new WriteTagHelper
     * @param context The context of the helper
     * @param nfcManager The NfcManager to connect with
     */
    public WriteTagHelper(Context context, NfcManager nfcManager) {
        this.context = context;
        this.nfcManager = nfcManager;
    }

    /**
     * Write text to a tag
     * @param text The text to write
     */
    public void writeText(String text){
        dialog = createWaitingDialog();
        dialog.show();
        nfcManager.writeText(text);
    }

    @Override
    public void onTagWritten() {
        dialog.dismiss();
        Toast.makeText(context, R.string.tag_written_toast, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTagWriteError(NFCWriteException exception) {
        dialog.dismiss();
        String errorMessage = exception.getType().toString();
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Creates a dialog while waiting for the tag
     * @return The AlertDialog to show the user
     */
    public AlertDialog createWaitingDialog(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.write_nfc_dialog_view, null, false);

        ImageView image = new ImageView(context);
        image.setImageResource(R.drawable.ic_nfc_black);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.wait_write_dialog_title)
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        nfcManager.undoWriteText();
                    }
                });
        return builder.create();
    }

}