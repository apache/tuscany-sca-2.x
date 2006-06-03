package org.apache.tuscany.spi.component;

/**
 * Throw when a scope context cannot be found for a given scope
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScopeNotFoundException extends ScopeRuntimeException {
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
