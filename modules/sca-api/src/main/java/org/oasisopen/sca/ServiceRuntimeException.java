/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca;

/**
 * This exception signals problems in the management of SCA component execution.
 */
public class ServiceRuntimeException extends RuntimeException {
    /**
     * Constructs a ServiceRuntimeException with no detail message. 
     */
    public ServiceRuntimeException() {
        super();
    }

    /**
     * Constructs a ServiceRuntimeException with the specified detail
     * message. 
     *
     * @param     message the detail message
     */
    public ServiceRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a ServiceRuntimeException with the specified detail
     * message and cause.
     *
     * The detail message associated with <code>cause</code> is not
     * automatically incorporated in this exception's detail message.
     *
     * @param     message the detail message
     * @param     cause the cause, or null if the cause is nonexistent
     *            or unknown
     */
    public ServiceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
 
    /**
     * Constructs a ServiceRuntimeException with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>.
     *
     * @param     cause the cause, or null if the cause is nonexistent
     *            or unknown
     */
    public ServiceRuntimeException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 6761623124602414622L;
}
