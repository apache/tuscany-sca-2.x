package org.apache.tuscany.spi.context;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ScopeNotFoundException extends ScopeRuntimeException{
    public ScopeNotFoundException() {
    }

    public ScopeNotFoundException(String message) {
        super(message);
    }

    public ScopeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScopeNotFoundException(Throwable cause) {
        super(cause);
    }
}
