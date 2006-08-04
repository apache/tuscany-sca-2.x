package org.apache.tuscany.core.implementation.processor;

/**
 * Denotes an illegal callback definition
 *
 * @version $Rev$ $Date$
 */

public class IllegalCallbackException extends IllegalServiceDefinitionException {
    public IllegalCallbackException() {
    }

    public IllegalCallbackException(String message) {
        super(message);
    }

    public IllegalCallbackException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalCallbackException(Throwable cause) {
        super(cause);
    }
}
