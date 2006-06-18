package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.injection.InjectionRuntimeException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class NoAccessorException extends InjectionRuntimeException {
    public NoAccessorException() {
    }

    public NoAccessorException(String message) {
        super(message);
    }

    public NoAccessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAccessorException(Throwable cause) {
        super(cause);
    }
}
