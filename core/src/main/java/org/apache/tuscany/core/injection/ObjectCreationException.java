package org.apache.tuscany.core.injection;

/**
 * Denotes an error creating a new object instance
 *
 * @version $Rev$ $Date$
 */
public class ObjectCreationException extends InjectionRuntimeException {

    public ObjectCreationException() {
        super();
    }

    public ObjectCreationException(String message) {
        super(message);
    }

    public ObjectCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectCreationException(Throwable cause) {
        super(cause);
    }

}

