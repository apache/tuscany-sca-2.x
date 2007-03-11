package org.apache.tuscany.hessian;

/**
 * @version $Rev$ $Date$
 */
public class InvalidDestinationException extends HessianException {
    public InvalidDestinationException(String message, String identifier) {
        super(message, identifier);
    }

    public InvalidDestinationException(String message, Throwable cause) {
        super(message, cause);
    }
}
