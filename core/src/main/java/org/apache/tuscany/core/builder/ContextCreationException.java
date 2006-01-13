package org.apache.tuscany.core.builder;


/**
 * Denotes an exception creating an instance context
 *
 * @version $Rev$ $Date$
 */
public class ContextCreationException extends BuilderException {

    public ContextCreationException() {
        super();
    }

    public ContextCreationException(String message) {
        super(message);
    }

    public ContextCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContextCreationException(Throwable cause) {
        super(cause);
    }

}

