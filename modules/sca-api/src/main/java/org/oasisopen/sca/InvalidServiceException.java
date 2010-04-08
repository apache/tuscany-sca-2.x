/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca;

/**
 * This exception signals that the ServiceReference is no longer valid.
 * This can happen when the target of the reference is undeployed.
 *
 * This exception is not transient and therefore is unlikely to be
 * resolved by retrying the operation and will most likely require
 * human intervention.
 */
public class InvalidServiceException extends ServiceRuntimeException {
    /**
     * Constructs a InvalidServiceException with no detail message. 
     */
    public InvalidServiceException() {
        super();
    }

    /**
     * Constructs a InvalidServiceException with the specified detail
     * message. 
     *
     * @param     message the detail message
     */
    public InvalidServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a InvalidServiceException with the specified detail
     * message and cause.
     *
     * The detail message associated with <code>cause</code> is not
     * automatically incorporated in this exception's detail message.
     *
     * @param     message the detail message
     * @param     cause the cause, or null if the cause is nonexistent
     *            or unknown
     */
    public InvalidServiceException(String message, Throwable cause) {
        super(message, cause);
    }
 
    /**
     * Constructs a InvalidServiceException with the specified cause and
     * a detail message of <tt>(cause==null ? null : cause.toString())</tt>.
     *
     * @param     cause the cause, or null if the cause is nonexistent
     *            or unknown
     */
    public InvalidServiceException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 7520492728695222145L;
}
