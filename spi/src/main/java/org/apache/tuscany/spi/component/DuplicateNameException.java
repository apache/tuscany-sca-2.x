package org.apache.tuscany.spi.component;

/**
 * Denotes an attempt to add an child to a composite component with a name equal to an existing child
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
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
