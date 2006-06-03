package org.apache.tuscany.spi.component;

/**
 * Thrown when a target for an operation is not of the required type
 *
 * @version $$Rev$$ $$Date$$
 */
public class IllegalTargetException extends TargetException {
    public IllegalTargetException() {
    }

    public IllegalTargetException(String message) {
        super(message);
    }

    public IllegalTargetException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalTargetException(Throwable cause) {
        super(cause);
    }
}
