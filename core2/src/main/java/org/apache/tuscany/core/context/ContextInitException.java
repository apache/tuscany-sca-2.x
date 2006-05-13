package org.apache.tuscany.core.context;

import org.apache.tuscany.spi.context.ContextRuntimeException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ContextInitException extends ContextRuntimeException {
    public ContextInitException() {
    }

    public ContextInitException(String message) {
        super(message);
    }

    public ContextInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContextInitException(Throwable cause) {
        super(cause);
    }
}
