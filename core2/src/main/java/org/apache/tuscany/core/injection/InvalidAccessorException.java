package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.injection.InjectionRuntimeException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class InvalidAccessorException extends InjectionRuntimeException {
    public InvalidAccessorException() {
    }

    public InvalidAccessorException(String message) {
        super(message);
    }

    public InvalidAccessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAccessorException(Throwable cause) {
        super(cause);
    }
}
