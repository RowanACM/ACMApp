package org.rowanacm.android.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.us.acm.R;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

/**
 * Handle reading and writing from NFC
 */
public class NfcManager {
    private static final String LOG_TAG = NfcManager.class.getSimpleName();

    NfcAdapter nfcAdapter;
    Activity activity;
    PendingIntent pendingIntent;

    TagReadListener onTagReadListener;
    TagWriteListener onTagWriteListener;
    TagWriteErrorListener onTagWriteErrorListener;

    // The text to write to the NFC tag
    String textToWrite = null;

    public NfcManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * Sets the listener to read events
     */
    public void setOnTagReadListener(TagReadListener onTagReadListener) {
        this.onTagReadListener = onTagReadListener;
    }

    /**
     * Sets the listener to write events
     */
    public void setOnTagWriteListener(TagWriteListener onTagWriteListener) {
        this.onTagWriteListener = onTagWriteListener;
    }

    /**
     * Sets the listener to write error events
     */
    public void setOnTagWriteErrorListener(TagWriteErrorListener onTagWriteErrorListener) {
        this.onTagWriteErrorListener = onTagWriteErrorListener;
    }

    /**
     * Indicates that we want to write the given text to the next tag detected
     */
    public void writeText(String writeText) {
        this.textToWrite = writeText;
    }

    /**
     * Stops a textToWrite operation
     */
    public void undoWriteText() {
        this.textToWrite = null;
    }

    /**
     * To be executed on OnCreate of the activity
     * @return true if the device has nfc capabilities
     */
    public boolean onActivityCreate() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        pendingIntent = PendingIntent.getActivity(activity, 0,
                new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        return nfcAdapter!=null;
    }

    /**
     * To be executed on onResume of the activity
     */
    public void onActivityResume() {
        Log.d(LOG_TAG, "onActivityResume() called");
        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(activity, R.string.turn_on_nfc, Toast.LENGTH_LONG).show();
            }
            nfcAdapter.enableForegroundDispatch(activity, pendingIntent, null, null);
            Log.d(LOG_TAG, "onActivityResume: enabled dispatch");
        }
    }

    /**
     * When the activity is paused, disable the NFC adapter
     */
    public void onActivityPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(activity);
        }
    }

    /**
     * To be executed on onNewIntent of activity
     * @param intent
     */
    public void onActivityNewIntent(Intent intent) {
        // TODO Check if the following line has any use
        // activity.setIntent(intent);
        if (textToWrite == null)
            readTagFromIntent(intent);
        else {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            try {
                writeTag(activity, tag, textToWrite);
                onTagWriteListener.onTagWritten();
            } catch (NFCWriteException exception) {
                onTagWriteErrorListener.onTagWriteError(exception);
            } finally {
                textToWrite = null;
            }
        }
    }

    /**
     * Reads a tag for a given intent and notifies listeners
     * @param intent
     */
    private void readTagFromIntent(Intent intent) {
        Log.d(LOG_TAG, "readTagFromIntent: ");

        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Log.d(LOG_TAG, "Tag read");

            Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] tagByteId = myTag.getId();
            String tagId = byteArrayToHexString(tagByteId);

            Log.d(LOG_TAG, "readTagFromIntent: " + Arrays.toString(myTag.getTechList()));

            int size = -1;
            boolean writable = false;
            String type = "unknown";

            // get NDEF tag details
            Ndef ndefTag = Ndef.get(myTag);
            if(ndefTag != null) {

                size = ndefTag.getMaxSize();         // tag size
                writable = ndefTag.isWritable(); // is tag writable?
                type = ndefTag.getType();         // tag type


                // get NDEF message details
                NdefMessage ndefMesg = ndefTag.getCachedNdefMessage();
                NdefRecord[] ndefRecords = ndefMesg.getRecords();
                int len = ndefRecords.length;
                String[] recTypes = new String[len];     // will contain the NDEF record types
                for (int i = 0; i < len; i++) {
                    recTypes[i] = new String(ndefRecords[i].getType());
                    byte[] mesg = ndefRecords[i].getPayload();

                    Log.d(LOG_TAG, "readTagFromIntent: MESSAGE " + Arrays.toString(mesg));
                }
            }



            String output = "NFC SCANNED | "
                    + "tagId="+tagId
                    + " toString="+myTag.toString()
                    + " describeContents="+myTag.describeContents()
                    + " size="+size
                    + " writable="+writable
                    + " type="+type;

            onTagReadListener.onTagRead(output);
        }
    }

    /**
     * Convert a byte array to a hex string
     * @param inarray The byte array to convert
     * @return A hex string representation of the arry
     */
    private String byteArrayToHexString(byte[] inarray) {
        // I honestly have no idea what this code is doing but it works
        int i, j, in;
        String[] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; j++) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    /**
     * Convert a NdefRecord to a string
     * @param record The NdefRecord to convert
     * @return The string representation of the record
     */
    public String ndefRecordToString(NdefRecord record) {
        byte[] payload = record.getPayload();
        return new String(payload);
    }

    /**
     * Writes a text to a tag
     * @param context The context of the app
     * @param tag The tag to write to
     * @param data The string to write to the tag
     * @throws NFCWriteException
     */
    protected void writeTag(Context context, Tag tag, String data) throws NFCWriteException {
        // Record with actual data we care about
        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, null, data.getBytes());

        // Complete NDEF message with both records
        NdefMessage message = new NdefMessage(new NdefRecord[] { relayRecord });

        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            // If the tag is already formatted, just write the message to it
            try {
                ndef.connect();
            } catch (IOException e) {
                throw new NFCWriteException(NFCWriteException.NFCErrorType.unknownError);
            }
            // Make sure the tag is writable
            if (!ndef.isWritable()) {
                throw new NFCWriteException(NFCWriteException.NFCErrorType.ReadOnly);
            }

            // Check if there's enough space on the tag for the message
            int size = message.toByteArray().length;
            if (ndef.getMaxSize() < size) {
                throw new NFCWriteException(NFCWriteException.NFCErrorType.NoEnoughSpace);
            }

            try {
                // Write the data to the tag
                ndef.writeNdefMessage(message);
            } catch (TagLostException tle) {
                throw new NFCWriteException(NFCWriteException.NFCErrorType.tagLost, tle);
            } catch (IOException ioe) {
                throw new NFCWriteException(NFCWriteException.NFCErrorType.formattingError, ioe);// nfcFormattingErrorTitle
            } catch (FormatException fe) {
                throw new NFCWriteException(NFCWriteException.NFCErrorType.formattingError, fe);
            }
        } else {
            // If the tag is not formatted, format it with the message
            NdefFormatable format = NdefFormatable.get(tag);
            if (format != null) {
                try {
                    format.connect();
                    format.format(message);
                } catch (TagLostException tle) {
                    throw new NFCWriteException(NFCWriteException.NFCErrorType.tagLost, tle);
                } catch (IOException ioe) {
                    throw new NFCWriteException(NFCWriteException.NFCErrorType.formattingError, ioe);
                } catch (FormatException fe) {
                    throw new NFCWriteException(NFCWriteException.NFCErrorType.formattingError, fe);
                }
            } else {
                throw new NFCWriteException(NFCWriteException.NFCErrorType.noNdefError);
            }
        }

    }

    public interface TagReadListener {
        public void onTagRead(String tagRead);
    }

    public interface TagWriteListener {
        public void onTagWritten();
    }

    public interface TagWriteErrorListener {
        public void onTagWriteError(NFCWriteException exception);
    }
}