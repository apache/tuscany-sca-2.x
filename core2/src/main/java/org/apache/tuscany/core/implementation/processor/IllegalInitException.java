package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.core.implementation.ProcessingException;

/**
 * Denotes an illegal signature for a method decorated with {@link @org.osoa.sca.annotations.Init}
 *
 * @version $Rev$ $Date$
 */
public class IllegalInitException extends ProcessingException {
    public IllegalInitException() {
    }

    public IllegalInitException(String message) {
        super(message);
    }

    public IllegalInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalInitException(Throwable cause) {
        super(cause);
    }
}
