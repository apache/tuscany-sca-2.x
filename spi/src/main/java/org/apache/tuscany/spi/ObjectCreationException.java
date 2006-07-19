package org.apache.tuscany.spi;

/**
 * Denotes an error creating a new object instance
 *
 * @version $Rev$ $Date$
 */
public class ObjectCreationException extends TuscanyRuntimeException {
    private static final long serialVersionUID = -6423113430265944499L;

    public ObjectCreationException() {
        super();
    }

    public ObjectCreationException(String message) {
        super(message);
    }

    public ObjectCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectCreationException(Throwable cause) {
        super(cause);
    }

}

