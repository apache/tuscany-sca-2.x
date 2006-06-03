package org.apache.tuscany.core.injection;

/**
 * Denotes an error when invoking on an object
 *
 * @version $Rev$ $Date$
 */
public class ObjectCallbackException extends InjectionRuntimeException {

    public ObjectCallbackException() {
        super();
    }

    public ObjectCallbackException(String message) {
        super(message);
    }

    public ObjectCallbackException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectCallbackException(Throwable cause) {
        super(cause);
    }

}
