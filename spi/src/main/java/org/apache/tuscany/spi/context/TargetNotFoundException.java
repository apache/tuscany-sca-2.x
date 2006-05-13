package org.apache.tuscany.spi.context;

/**
 * @version $$Rev$$ $$Date$$
 */
public class TargetNotFoundException extends TargetException{
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
