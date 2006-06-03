package org.apache.tuscany.spi.component;

/**
 * Thrown when a component cannot be found
 *
 * @version $$Rev$$ $$Date$$
 */
public class ComponentNotFoundException extends ComponentRuntimeException {
    public ComponentNotFoundException() {
    }

    public ComponentNotFoundException(String message) {
        super(message);
    }

    public ComponentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComponentNotFoundException(Throwable cause) {
        super(cause);
    }
}
