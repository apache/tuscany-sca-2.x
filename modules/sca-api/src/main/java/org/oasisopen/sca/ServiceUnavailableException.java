/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca;

/**
 * This exception signals problems in the interaction with remote
 * services.
 *
 * These are exceptions that can be transient, so retrying is
 * appropriate.  Any exception that is a ServiceRuntimeException
 * that is not a ServiceUnavailableException is unlikely to be
 * resolved by retrying the operation, since it most likely
 * requires human intervention.
 */
public class ServiceUnavailableException extends ServiceRuntimeException {
    /**
     * Constructs a ServiceUnavailableException with no detail message. 
     */
    public ServiceUnavailableException() {
        super();
    }

    /**
     * Constructs a ServiceUnavailableException with the specified detail
     * message. 
     *
     * @param     message the detail message
     */
    public ServiceUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs a ServiceUnavailableException with the specified detail
     * message and cause.
     *
     * The detail message associated with <code>cause</code> is not
     * automatically incorporated in this exception's detail message.
     *
     * @param     message the detail message
     * @param     cause the cause, or null if the cause is nonexistent
     *            or unknown
     */
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
 
    /**
     * Constructs a ServiceUnavailableException with the specified cause and
     * a detail message of <tt>(cause==null ? null : cause.toString())</tt>.
     *
     * @param     cause the cause, or null if the cause is nonexistent
     *            or unknown
     */
    public ServiceUnavailableException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 5750303470949048271L;
}
