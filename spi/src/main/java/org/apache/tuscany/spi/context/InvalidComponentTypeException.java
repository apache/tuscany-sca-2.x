package org.apache.tuscany.spi.context;

/**
 * Thrown when an operation is attempted using the wrong context type
 *
 * @version $$Rev$$ $$Date$$
 */
public class InvalidComponentTypeException extends ComponentRuntimeException {
    public InvalidComponentTypeException() {
    }

    public InvalidComponentTypeException(String message) {
        super(message);
    }

    public InvalidComponentTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidComponentTypeException(Throwable cause) {
        super(cause);
    }
}
