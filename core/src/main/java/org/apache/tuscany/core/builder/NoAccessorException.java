package org.apache.tuscany.core.builder;

/**
 * Denotes an attempt to access a non-existent field or method
 * 
 * @version $Rev$ $Date$
 */
public class NoAccessorException extends BuilderException {

    public NoAccessorException() {
        super();
    }

    public NoAccessorException(String message) {
        super(message);
    }

    public NoAccessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAccessorException(Throwable cause) {
        super(cause);
    }

}
