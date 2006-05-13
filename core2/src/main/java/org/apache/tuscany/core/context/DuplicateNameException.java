package org.apache.tuscany.core.context;

import org.apache.tuscany.spi.context.ContextRuntimeException;

/**
 * Denotes an attempt to add a context with a name equal to an existing context
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class DuplicateNameException extends ContextRuntimeException {

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
