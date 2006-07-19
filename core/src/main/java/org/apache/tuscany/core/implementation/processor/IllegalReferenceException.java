package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.core.implementation.ProcessingException;

/**
 * Denotes an illegal reference definition in a component type
 *
 * @version $Rev$ $Date$
 */
public class IllegalReferenceException extends ProcessingException {
    public IllegalReferenceException() {
    }

    public IllegalReferenceException(String message) {
        super(message);
    }

    public IllegalReferenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalReferenceException(Throwable cause) {
        super(cause);
    }
}
