package org.apache.tuscany.spi.wire;

import org.apache.tuscany.spi.TuscanyRuntimeException;

/**
 * Denotes an error creating a proxy
 *
 * @version $$Rev$$ $$Date$$
 */
public class ProxyCreationException extends TuscanyRuntimeException {
    public ProxyCreationException() {
    }

    public ProxyCreationException(String message) {
        super(message);
    }

    public ProxyCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyCreationException(Throwable cause) {
        super(cause);
    }
}
