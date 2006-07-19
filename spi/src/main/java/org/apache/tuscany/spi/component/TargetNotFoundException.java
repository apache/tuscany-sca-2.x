package org.apache.tuscany.spi.component;

/**
 * Thrown when a target of an operation cannot be found
 *
 * @version $$Rev$$ $$Date$$
 */
public class TargetNotFoundException extends TargetException {
    public TargetNotFoundException() {
    }

    public TargetNotFoundException(String message) {
        super(message);
    }

    public TargetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TargetNotFoundException(Throwable cause) {
        super(cause);
    }
}
