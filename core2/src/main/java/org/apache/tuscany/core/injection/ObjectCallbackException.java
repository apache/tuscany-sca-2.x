package org.apache.tuscany.core.injection;

/**
 * Denotes an error when invoking on an object
 *
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
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
