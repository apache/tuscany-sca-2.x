package org.apache.tuscany.spi.context;

/**
 * @version $$Rev$$ $$Date$$
 */
public class InvalidContextTypeException extends ContextRuntimeException{
    public InvalidContextTypeException() {
    }

    public InvalidContextTypeException(String message) {
        super(message);
    }

    public InvalidContextTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidContextTypeException(Throwable cause) {
        super(cause);
    }
}
