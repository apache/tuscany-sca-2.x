package org.apache.tuscany.spi.context;

/**
 * @version $$Rev$$ $$Date$$
 */
public class IllegalTargetException extends TargetException{
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
