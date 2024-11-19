package com.lfsolutions.paymentslibrary.paytm.crypto;

import java.io.PrintStream;

public class SecurityException extends Exception {
    private static final long serialVersionUID = -3956900350777254445L;
    private String errorCode;
    private String errorMessage;
    private Exception exception;

    public SecurityException(String errorCode2, String errorMessage2) {
        super(errorMessage2);
        this.errorCode = errorCode2;
        this.errorMessage = errorMessage2;
    }

    public SecurityException(String errorMessage2) {
        super(errorMessage2);
    }

    public SecurityException(Throwable cause) {
        super(cause);
    }

    public SecurityException(String errorMessage2, Throwable cause) {
        super(errorMessage2, cause);
    }

    public SecurityException(String errorMessage2, Exception exception2) {
        this.errorMessage = errorMessage2;
        this.exception = exception2;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode2) {
        this.errorCode = errorCode2;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage2) {
        this.errorMessage = errorMessage2;
    }

    public void printStackTrace(PrintStream stream) {
        if (this.exception != null) {
            this.exception.printStackTrace(stream);
        }
    }
}
