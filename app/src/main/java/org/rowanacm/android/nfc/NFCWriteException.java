package org.rowanacm.android.nfc;

/**
 * Exception thrown when writing NFC tags
 */
public class NFCWriteException extends Exception {
    private static final long serialVersionUID = 4647185067874734143L;
    NFCErrorType type;

    public enum NFCErrorType {
        ReadOnly, NoEnoughSpace, tagLost, formattingError, noNdefError, unknownError
    }

    public NFCWriteException(NFCErrorType type) {
        super();
        this.type = type;
    }

    public NFCWriteException(NFCErrorType type, Exception e) {
        super(e);
        this.type = type;
    }

    /**
     * Get the type of the exception
     * @return The cause of the exception
     */
    public NFCErrorType getType() {
        return type;
    }
}