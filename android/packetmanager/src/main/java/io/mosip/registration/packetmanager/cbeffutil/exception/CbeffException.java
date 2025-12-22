package io.mosip.registration.packetmanager.cbeffutil.exception;

public class CbeffException extends Exception {

    private static final long serialVersionUID = 1L;

    public CbeffException(String message) {
        super(message);
    }

    public CbeffException(String message, Throwable cause) {
        super(message, cause);
    }
}

