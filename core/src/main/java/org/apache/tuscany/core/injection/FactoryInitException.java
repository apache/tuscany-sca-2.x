package org.apache.tuscany.core.injection;

/**
 * Denotes an exception initializing an object factory
 * 
 * @version $Rev$ $Date$
 */
public class FactoryInitException extends InjectionRuntimeException {

    public FactoryInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public FactoryInitException(String message) {
        super(message);
    }

    public FactoryInitException(Throwable cause) {
        super(cause);
    }

    public FactoryInitException() {
        super();
    }

}

