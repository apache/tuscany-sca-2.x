package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.TuscanyRuntimeException;

/**
 * Root unchecked exception for the injection package
 * 
 * @version $Rev$ $Date$
 */
public abstract class InjectionRuntimeException extends TuscanyRuntimeException {

    public InjectionRuntimeException() {
        super();
    }

    public InjectionRuntimeException(String message) {
        super(message);
    }

    public InjectionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InjectionRuntimeException(Throwable cause) {
        super(cause);
    }

}

