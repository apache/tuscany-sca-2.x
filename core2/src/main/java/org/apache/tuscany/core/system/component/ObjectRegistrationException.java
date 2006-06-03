package org.apache.tuscany.core.system.component;

import org.apache.tuscany.spi.component.ComponentRuntimeException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ObjectRegistrationException extends ComponentRuntimeException {
    public ObjectRegistrationException() {
    }

    public ObjectRegistrationException(String message) {
        super(message);
    }

    public ObjectRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectRegistrationException(Throwable cause) {
        super(cause);
    }
}
