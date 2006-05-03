package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.builder.BuilderException;

/**
 * Denotes an unknown configuration parameter type
 * 
 * @version $Rev$ $Date$
 */
public class UnknownTypeException extends BuilderException {

    public UnknownTypeException() {
        super();
    }

    public UnknownTypeException(String message) {
        super(message);
    }

    public UnknownTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownTypeException(Throwable cause) {
        super(cause);
    }

}
