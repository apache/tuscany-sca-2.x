package org.apache.tuscany.core.system.context;

import org.apache.tuscany.spi.context.ContextRuntimeException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ObjectRegistrationException extends ContextRuntimeException {
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
