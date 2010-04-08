/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca;

/**
 * This exception signals that the given SCA service does not exist. 
 */

public class NoSuchServiceException extends Exception {
    /**
     * Constructs a NoSuchServiceException with no detail message. 
     */
    public NoSuchServiceException() {
        super();
    }

    /**
     * Constructs a NoSuchServiceException with the specified detail
     * message. 
     *
     * @param     message the detail message
     */
    public NoSuchServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a NoSuchServiceException with the specified detail
     * message and cause.
     *
     * The detail message associated with <code>cause</code> is not
     * automatically incorporated in this exception's detail message.
     *
     * @param     message the detail message
     * @param     cause the cause, or null if the cause is nonexistent
     *            or unknown
     */
    public NoSuchServiceException(String message, Throwable cause) {
        super(message, cause);
    }
 
    /**
     * Constructs a NoSuchServiceException with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>.
     *
     * @param     cause the cause, or null if the cause is nonexistent
     *            or unknown
     */
    public NoSuchServiceException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 6761623124602414622L;
}
