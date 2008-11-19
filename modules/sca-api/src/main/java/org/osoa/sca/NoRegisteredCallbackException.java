/*
 * (c) Copyright BEA Systems, Inc., Cape Clear Software, International Business Machines Corp, Interface21, IONA Technologies,
 * Oracle, Primeton Technologies, Progress Software, Red Hat, Rogue Wave Software, SAP AG., Siemens AG., Software AG., Sybase
 * Inc., TIBCO Software Inc., 2005, 2007. All rights reserved.
 * 
 * see http://www.osoa.org/display/Main/Service+Component+Architecture+Specifications
 */
package org.osoa.sca;

/**
 * Exception thrown to indicate that no callback has been registered
 * when interacting with a service.
 *
 * @version $Rev$ $Date$
 */
public class NoRegisteredCallbackException extends ServiceRuntimeException {
    private static final long serialVersionUID = 3734864942222558406L;

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @see ServiceRuntimeException
     */
    public NoRegisteredCallbackException() {
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param message passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public NoRegisteredCallbackException(String message) {
        super(message);
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param message passed to ServiceRuntimeException
     * @param cause   passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public NoRegisteredCallbackException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param cause passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public NoRegisteredCallbackException(Throwable cause) {
        super(cause);
    }
}
