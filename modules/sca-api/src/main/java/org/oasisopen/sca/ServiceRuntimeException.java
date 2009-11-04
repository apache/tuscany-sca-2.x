/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca;


/**
 * Base for Exceptions that may be raised by an SCA runtime and which typical
 * application code is not expected to be able to handle.
 *
 * @version $Rev$ $Date$
 */
public class ServiceRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -3876058842262557092L;

    /**
     * Override constructor from RuntimeException.
     *
     * @see RuntimeException
     */
    public ServiceRuntimeException() {
    }

    /**
     * Override constructor from RuntimeException.
     *
     * @param message passed to RuntimeException
     * @see RuntimeException
     */
    public ServiceRuntimeException(String message) {
        super(message);
    }

    /**
     * Override constructor from RuntimeException.
     *
     * @param message passed to RuntimeException
     * @param cause   passed to RuntimeException
     * @see RuntimeException
     */
    public ServiceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Override constructor from RuntimeException.
     *
     * @param cause passed to RuntimeException
     * @see RuntimeException
     */
    public ServiceRuntimeException(Throwable cause) {
        super(cause);
    }
}
