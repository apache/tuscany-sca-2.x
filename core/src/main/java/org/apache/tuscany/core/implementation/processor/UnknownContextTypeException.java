package org.apache.tuscany.core.implementation.processor;

/**
 * Thrown when a method or field marked with {@link org.osoa.sca.annotations.Context} takes an unknown type
 *
 * @version $Rev$ $Date$
 */
public class UnknownContextTypeException extends IllegalContextException {
    public UnknownContextTypeException() {
    }

    public UnknownContextTypeException(String message) {
        super(message);
    }

    public UnknownContextTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownContextTypeException(Throwable cause) {
        super(cause);
    }
}
