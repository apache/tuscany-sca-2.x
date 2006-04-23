package org.apache.tuscany.core.wire;

import org.apache.tuscany.common.TuscanyException;

/**
 * Denotes an application-level exception raised during an invocation over a wire
 * 
 * @version $Rev$ $Date$
 */
public abstract class InvocationException extends TuscanyException {

    public InvocationException() {
        super();
    }

    public InvocationException(String message) {
        super(message);
    }

    public InvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvocationException(Throwable cause) {
        super(cause);
    }

}

