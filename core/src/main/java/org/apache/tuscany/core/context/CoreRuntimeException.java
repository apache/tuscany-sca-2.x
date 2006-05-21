package org.apache.tuscany.core.context;

import org.apache.tuscany.spi.TuscanyRuntimeException;

/**
 * The root exception for the runtime package. Exceptions occurring in the runtime are generally non-recoverable
 * 
 * @version $Rev$ $Date$
 */
public abstract class CoreRuntimeException extends TuscanyRuntimeException {

    public CoreRuntimeException() {
        super();
    }

    public CoreRuntimeException(String message) {
        super(message);
    }

    public CoreRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreRuntimeException(Throwable cause) {
        super(cause);
    }
}
