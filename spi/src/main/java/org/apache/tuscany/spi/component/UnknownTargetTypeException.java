package org.apache.tuscany.spi.context;

/**
 * @version $$Rev$$ $$Date$$
 */
public class UnknownTargetTypeException extends TargetException {
    public UnknownTargetTypeException() {
    }

    public UnknownTargetTypeException(String message) {
        super(message);
    }

    public UnknownTargetTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownTargetTypeException(Throwable cause) {
        super(cause);
    }
}
