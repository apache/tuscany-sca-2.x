/*
 * (c) Copyright BEA Systems, Inc., Cape Clear Software, International Business Machines Corp, Interface21, IONA Technologies,
 * Oracle, Primeton Technologies, Progress Software, Red Hat, Rogue Wave Software, SAP AG., Siemens AG., Software AG., Sybase
 * Inc., TIBCO Software Inc., 2005, 2007. All rights reserved.
 * 
 * see http://www.osoa.org/display/Main/Service+Component+Architecture+Specifications
 */
package org.osoa.sca;

/**
 * Exception used to indicate that a runtime exception occurred during the invocation of and external service.
 *
 * @version $Rev$ $Date$
 */
public class ServiceUnavailableException extends ServiceRuntimeException {

    private static final long serialVersionUID = -5869397223249401047L;

    /**
     * Constructs a new ServiceUnavailableException.
     */
    public ServiceUnavailableException() {
        super((Throwable) null);
    }

    /**
     * Constructs a new ServiceUnavailableException with the specified detail message.
     *
     * @param message The detail message (which is saved to later retrieval by the getMessage() method).
     */
    public ServiceUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs a new ServiceUnavailableException with the specified cause.
     *
     * @param cause The cause (which is saved to later retrieval by the getCause() method).
     */
    public ServiceUnavailableException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ServiceUnavailableException with the specified detail message and cause.
     *
     * @param message The message (which is saved to later retrieval by the getMessage() method).
     * @param cause   The cause (which is saved to later retrieval by the getCause() method).
     */
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

}
