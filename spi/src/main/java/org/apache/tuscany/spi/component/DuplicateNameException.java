package org.apache.tuscany.spi.component;

/**
 * Denotes an attempt to add an child to a composite component with a name equal to an existing child
 *
 * @version $Rev$ $Date$
 */
public class DuplicateNameException extends ComponentRuntimeException {

    public DuplicateNameException() {
        super();
    }

    public DuplicateNameException(String message) {
        super(message);
    }

    public DuplicateNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateNameException(Throwable cause) {
        super(cause);
    }

}
