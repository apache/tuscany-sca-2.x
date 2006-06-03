package org.apache.tuscany.core.context;

import org.apache.tuscany.spi.context.ComponentRuntimeException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ComponentInitException extends ComponentRuntimeException {
    public ComponentInitException() {
    }

    public ComponentInitException(String message) {
        super(message);
    }

    public ComponentInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComponentInitException(Throwable cause) {
        super(cause);
    }
}
