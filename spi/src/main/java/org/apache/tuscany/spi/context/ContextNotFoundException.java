package org.apache.tuscany.spi.context;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ContextNotFoundException extends ContextRuntimeException {
    public ContextNotFoundException() {
    }

    public ContextNotFoundException(String message) {
        super(message);
    }

    public ContextNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContextNotFoundException(Throwable cause) {
        super(cause);
    }
}
