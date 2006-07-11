package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.core.implementation.ProcessingException;

/**
 * Denotes an illegal property definition in a component type
 *
 * @version $Rev$ $Date$
 */
public class IllegalPropertyException extends ProcessingException {
    public IllegalPropertyException() {
    }

    public IllegalPropertyException(String message) {
        super(message);
    }

    public IllegalPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalPropertyException(Throwable cause) {
        super(cause);
    }
}
